/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.mumie.mathletfactory.display.g2d;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.math.util.MathUtilLib;
import net.mumie.mathletfactory.util.TickMark;

/**
 * Used for drawing labels of a coordinate system in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 *  
 * @author amsel
 * @mm.docstatus finished
 */
public class G2DLegendDrawable extends CanvasDrawable {
  
  private boolean m_drawGridLines = true;
  private boolean m_drawZero = false;
	private boolean useExponential = true;
	private double m_startX = 0;
	private double m_startY = 0;
	private double m_stopX = 0;
	private double m_stopY = 0;
	private double m_startOffsetX = 0;
	private double m_startOffsetY = 0;
	private double m_centerX = 0;
	private double m_centerY = 0;
	private double m_scaleX = 1; //needed because start stop may be 0
	private double m_scaleY = 1; //needed because start stop may be 0
  private double m_magnitude = 1;
	private TickMark m_tickMark = null;
	private double m_startValue = 0;
	private double m_TickDistance = 1000; //in pixel
	private double m_tickLength = 3;
	private ArrayList m_gridLineDesciptors;
  private String m_axisLabel = "";
  

	public G2DLegendDrawable() {
	}

	public double lineLength() {
		return Graphics2DHelper.pointDistance(m_startX, m_startY, m_stopX, m_stopY);
	}

	private DecimalFormat format = new DecimalFormat("0.####;-0.####");

	protected String formatValue(double value, double formatFactor) {
		if (value == 0) {
			return "0";
		}
		return format.format(value / formatFactor);
	}

  /** Draws the label at the given position. */
	protected void drawLabel(Graphics2D g2d, String label, double x, double y,	/*double scaleX, double scaleY, */
	int direction, boolean adjust, Rectangle boundary) {
		Rectangle2D rect =
			new TextLayout(label, g2d.getFont(), g2d.getFontRenderContext())
				.getBounds();
		//center Text on tick
		double textX = x - rect.getWidth() /  2.0; //fuer Beschriftung des Koordinatensystems
		double textY = y - rect.getHeight() / 2.0;
		//move text to end of tick mark (depends on direction the ticks goes. text
		//is aligned at the right  most (if equal bottom most) end
		textX -= direction
			* (rect.getWidth() / 2.0 + getTickLength() + 2)
			* m_scaleY;
		textY += direction
			* (rect.getHeight() / 2.0 + getTickLength() + 2)
			* m_scaleX;
		double xPos = textX - rect.getX();
		double yPos = textY - rect.getY();
		if (adjust) {
			int minDist = 5;
			if (xPos < (minDist + boundary.getX()))
				xPos = minDist;
			if ((xPos + rect.getWidth() + minDist)
				> (boundary.getX() + boundary.getWidth()))
				xPos =
					(boundary.getX() + boundary.getWidth()) - rect.getWidth() - minDist;
			if ((yPos - rect.getHeight() - minDist) < boundary.getY())
				yPos = rect.getHeight() + minDist + boundary.getY();
			if ((yPos + minDist) > (boundary.getY() + boundary.getHeight()))
				yPos = boundary.getY() + boundary.getHeight() - minDist;
		}
		g2d.drawString(label, (int) (xPos), (int) (yPos));
	}
  
	protected void valueToScreenPosition(
		double value,
		double tickDistanceX,
		double tickDistanceY,
		double[] pos) {
		double mathDistance = value - m_startValue;
		pos[0] = m_startOffsetX + mathDistance * (tickDistanceX / (m_tickMark.getPower10()*m_magnitude));
		pos[1] = m_startOffsetY + mathDistance * (tickDistanceY / (m_tickMark.getPower10()*m_magnitude));
	}
  
	protected void drawObject(MMCanvas destination, DisplayProperties properties) {
		MMG2DCanvas canvas = (MMG2DCanvas)destination;
		Graphics2D g2d = canvas.getGraphics2D();
		Rectangle canvasBounds = canvas.getClientRect();
		double tickDistanceX = m_TickDistance * m_scaleX;
		double tickDistanceY = m_TickDistance * m_scaleY;
		double value = m_startValue;
		double formatFactor = 1.0d;
		double drawLength =
			Math.max(canvasBounds.getWidth(), canvasBounds.getHeight());
		if (useExponential) {
			double maxValue =
				Math.max(
					Math.abs(m_startValue),
					Math.abs((m_tickMark.getPower10()*m_magnitude)* (1 + lineLength() / (m_TickDistance))));
			double magnitude = MathUtilLib.log10(maxValue);
			if (Math.abs(magnitude) > 3) {
				formatFactor = Math.pow(10, (int)magnitude);
			}
		}
		double saveLength =
			Math.sqrt(
				Math.pow(canvasBounds.getWidth(), 2)
					+ Math.pow(canvasBounds.getHeight(), 2));
		double gridLinesToDraw = saveLength / m_TickDistance;
		g2d.setColor(Color.lightGray);
		double x = m_centerX;
    double y = m_centerY;
    if (m_drawGridLines){
      g2d.drawLine(
        (int) (x - saveLength * m_scaleY),
        (int) (y + saveLength * m_scaleX),
        (int) (x + saveLength * m_scaleY),
        (int) (y - saveLength * m_scaleX));
      for (int i = 1; i < gridLinesToDraw; i++) {
        x = m_centerX + i * tickDistanceX;
        y = m_centerY + i * tickDistanceY;
        g2d.drawLine(
          (int) (x - saveLength * m_scaleY),
          (int) (y + saveLength * m_scaleX),
          (int) (x + saveLength * m_scaleY),
          (int) (y - saveLength * m_scaleX));
        x = m_centerX - i * tickDistanceX;
        y = m_centerY - i * tickDistanceY;
        g2d.drawLine(
          (int) (x - saveLength * m_scaleY),
          (int) (y + saveLength * m_scaleX),
          (int) (x + saveLength * m_scaleY),
          (int) (y - saveLength * m_scaleX));
      }
    }
		if (lineLength() != 0) {
			x = m_startOffsetX;
			y = m_startOffsetY;
			g2d.setColor(Color.black);
			while (Graphics2DHelper
				.pointDistance(m_startOffsetX, m_startOffsetY, x, y)
				< Graphics2DHelper.pointDistance(
					m_startOffsetX,
					m_startOffsetY,
					m_stopX,
					m_stopY)) {
				if (Math.abs(value) > 1.0E-12 || m_drawZero) { //skip zero
					g2d.drawLine(
						(int) (x - getTickLength() * m_scaleY),
						(int) (y + getTickLength() * m_scaleX),
						(int) (x + getTickLength() * m_scaleY),
						(int) (y - getTickLength() * m_scaleX));
          String label = m_tickMark.getLabel(); 
          if (label == null) {
            label = formatValue(value, formatFactor);
          } else {
            double multiple = value / m_tickMark.getPower10();
            String faktor = formatValue(multiple, formatFactor);
            if (faktor.equals("1")) {
              faktor = ""; 
            }
            if (faktor.equals("-1")) {
              faktor = "-";
            }
            label = faktor + m_tickMark.getLabel(); 
          }
					drawLabel(
						g2d,
						label,
						x,
						y,
					/*scaleX, scaleY, */
					1, false, canvasBounds);
				}
				x += tickDistanceX;
				y += tickDistanceY;
				value += (m_tickMark.getPower10()*m_magnitude);
			}
		}
    
		if (formatFactor != 1) {
			String label = new DecimalFormat("0E0").format(formatFactor)+getAxisLabel() ;
			drawLabel(g2d, label, m_stopX, m_stopY, /*scaleX, scaleY, */
			-1, true, canvasBounds);
		} else {
      drawLabel(g2d, getAxisLabel(), m_stopX, m_stopY, -1, true, canvasBounds);
		}
    
		double[] pos = new double[2];
		for (int i = 0; i < getGridLineDesciptors().size(); i++) {
			GridLineDescriptor line =
				(GridLineDescriptor)getGridLineDesciptors().get(i);
			valueToScreenPosition(
				line.getPosition(),
				tickDistanceX,
				tickDistanceY,
				pos);
			double[] intersection = new double[4];
			double res =
				Graphics2DHelper.lineRectIntersection2D(
					new double[] {
						pos[0],
						pos[1],
						pos[0] + tickDistanceY,
						pos[1] - tickDistanceX },
					new double[] {
						canvasBounds.getX(),
						canvasBounds.getY(),
						canvasBounds.getX() + canvasBounds.getWidth(),
						canvasBounds.getY() + canvasBounds.getHeight()},
					intersection);
			g2d.setColor(line.getColor());
			g2d.drawLine(
				(int) (intersection[0]),
				(int) (intersection[1]),
				(int) (intersection[2]),
				(int) (intersection[3]));
			drawLabel(
				g2d,
				line.getDescription(),
				pos[0],
				pos[1],
			/*scaleX, scaleY, */
			1, false, canvasBounds);
			/*g2d.drawLine(
				(int)getStartX(),
				(int)getStartY(),
				(int)getStopX(),
				(int)getStopY());*/
		}
	}
  
	protected void drawSelection(
		Object destination,
		DisplayProperties properties) {
		//legend is not selectable so nothing todo
	}

	public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
		return false;
	}

	public void render(DisplayProperties properties) {
	}

	/** Returns the x coordinate from which the labeling starts. */
	public double getStartX() {
		return m_startX;
	}
  
  /** Returns the y coordinate from which the labeling starts. */
	public double getStartY() {
		return m_startY;
	}
  
  /** Returns the x coordinate where the labeling stops. */
	public double getStopX() {
		return m_stopX;
	}
	
  /** Returns the y coordinate where the labeling stops. */
	public double getStopY() {
		return m_stopY;
	}

	/** Returns the length between two tick marks. */
	public TickMark getTickMark() {
		return m_tickMark;
	}

	/** Returns whether exponential notation is used. */
	public boolean isUseExponential() {
		return useExponential;
	}

  /** Sets the x coordinate from which the labeling starts. */
	public void setStartX(double startX) {
		m_startX = startX;
	}

  /** Sets the y coordinate from which the labeling starts. */
	public void setStartY(double startY) {
		m_startY = startY;
	}

  /** Sets the x coordinate where the labeling stops. */
  public void setStopX(double stopX) {
		m_stopX = stopX;
	}
  
  /** Sets the y coordinate where the labeling stops. */
	public void setStopY(double stopY) {
		m_stopY = stopY;
	}
  
	/** Sets the length between two tick marks. */
	public void setTickMark(TickMark unitLength) {
		m_tickMark = unitLength;
	}
  
  /** Sets whether exponential notation is used. */
	public void setUseExponential(boolean useExponential) {
		this.useExponential = useExponential;
	}

  /** Returns the starting offset in x direction. */ 
	public double getStartOffsetX() {
		return m_startOffsetX;
	}
  
  /** Returns the starting offset in y direction. */
	public double getStartOffsetY() {
		return m_startOffsetY;
	}
	
  /** Sets the starting offset in x direction. */
	public void setStartOffsetX(double zeroX) {
		m_startOffsetX = zeroX;
	}

  /** Sets the starting offset in y direction. */
  public void setStartOffsetY(double zeroY) {
		m_startOffsetY = zeroY;
	}
  
  /** Returns the distance between two ticks. */
	public double getTickDistance() {
		return m_TickDistance;
	}
  
  /** Sets the distance between two ticks. */
	public void setTickDistance(double distance) {
		m_TickDistance = distance;
	}
  
  /** Returns the starting value. */
	public double getStartValue() {
		return m_startValue;
	}
  
  /** Sets the starting value. */
  public void setStartValue(double startValue) {
		m_startValue = startValue;
	}
  
  /** Returns the length of each tick. */
	public double getTickLength() {
		return m_tickLength;
	}
  
  /** Returns the length of each tick. */
  public void setTickLength(double tickLength) {
		m_tickLength = tickLength;
	}

  /** Returns all grid line descriptors of this legend. */  
	public ArrayList getGridLineDesciptors() {
		return m_gridLineDesciptors;
	}
  
  /** Sets the grid line descriptors of this legend. */
	public void setGridLineDesciptors(ArrayList gridLineDesciptors) {
		m_gridLineDesciptors = gridLineDesciptors;
	}

  /** Returns the x coordinate of the center. */   
	public double getCenterX() {
		return m_centerX;
	}
  
  /** Returns the y coordinate of the center. */
  public double getCenterY() {
		return m_centerY;
	}
  
  /** Sets the x coordinate of the center. */
  public void setCenterX(double centerX) {
		m_centerX = centerX;
	}
  

  /** Sets the y coordinate of the center. */
  public void setCenterY(double centerY) {
		m_centerY = centerY;
	}

  /** Returns the x scale. */  
	public double getScaleX() {
		return m_scaleX;
	}
  

  /** Returns the y coordinate of the center. */
  public double getScaleY() {
		return m_scaleY;
	}
  
  /** Sets the x coordinate of the center. */
	public void setScaleX(double scaleX) {
		m_scaleX = scaleX;
	}
  
  /** Sets the y coordinate of the center. */
  public void setScaleY(double scaleY) {
		m_scaleY = scaleY;
	}
  
  /** Returns the magnitude displayed. */
  public double getMagnitude() {
    return m_magnitude;
  }

  /** Sets the magnitude displayed. */
  public void setMagnitude(double d) {
    m_magnitude = d;
  }

  /** Returns whether to draw grid lines. */
  public boolean isDrawGridLines() {
    return m_drawGridLines;
  }

  /** Sets whether to draw grid lines. */
  public void setDrawGridLines(boolean b) {
    m_drawGridLines = b;
  }

  /** Returns whether the zero should also be drawn.  */
  public boolean isDrawZero() {
    return m_drawZero;
  }

  /** Sets whether the zero should also be drawn.  */
  public void setDrawZero(boolean b) {
    m_drawZero = b;
  }
  
  /**
   * Returns the Label of this axis.
   */
  public String getAxisLabel() {
    return m_axisLabel;
  }

  /**
   * Sets the Label of this axis.
   */
  public void setAxisLabel(String string) {
    m_axisLabel = string;
  }

}
