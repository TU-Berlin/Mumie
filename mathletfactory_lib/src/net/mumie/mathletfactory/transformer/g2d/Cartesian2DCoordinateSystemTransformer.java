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

package net.mumie.mathletfactory.transformer.g2d;

import java.awt.Color;
import java.awt.Rectangle;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.g2d.G2DLegendDrawable;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.math.util.MathUtilLib;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;
import net.mumie.mathletfactory.util.TickMark;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMCoordinateSystem}.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class Cartesian2DCoordinateSystemTransformer
	extends Affine2DDefaultTransformer {
	private double[] m_initInWorld = new double[2]; // zero
	private double[] m_endInWorldX = new double[2]; // X
	private double[] m_endInWorldY = new double[2]; // Y

	private double[] m_initOnScreen = new double[2];
	private double[] m_endOnScreenX = new double[2];
	private double[] m_endOnScreenY = new double[2];

	private Affine2DPoint m_zero = new Affine2DPoint(MDouble.class, 0, 0);
	private Affine2DPoint m_x = new Affine2DPoint(MDouble.class, 1, 0);
	private Affine2DPoint m_y =	new Affine2DPoint(MDouble.class, 0, 1);

  private boolean m_hasBeenSynchronized = false;
	private Rectangle m_oldVisibleArea;

  public Cartesian2DCoordinateSystemTransformer() {
		m_additionalDrawables =
			new CanvasDrawable[] {
				new G2DLineDrawable(),
				new G2DLineDrawable(),
				new G2DLegendDrawable(),
				new G2DLegendDrawable()};

		m_additionalProperties =
			new DisplayProperties[] {
				new LineDisplayProperties(), // x-axis
				new LineDisplayProperties(), // y-axis
				new DisplayProperties(),
				new DisplayProperties()};
		((LineDisplayProperties) m_additionalProperties[0]).setLineWidth(2);
		((LineDisplayProperties) m_additionalProperties[0]).setBorderWidth(0);
		((LineDisplayProperties) m_additionalProperties[0]).setArrowAtEnd(
			LineDisplayProperties.PLAIN_ARROW_END);
		((LineDisplayProperties) m_additionalProperties[0]).setObjectColor(
			Color.gray);
		((LineDisplayProperties) m_additionalProperties[0]).setBorderColor(
			Color.gray);
		((LineDisplayProperties) m_additionalProperties[1]).setLineWidth(2);
		((LineDisplayProperties) m_additionalProperties[1]).setBorderWidth(0);
		((LineDisplayProperties) m_additionalProperties[1]).setArrowAtEnd(
			LineDisplayProperties.PLAIN_ARROW_END);
		((LineDisplayProperties) m_additionalProperties[1]).setObjectColor(
			Color.gray);
		((LineDisplayProperties) m_additionalProperties[1]).setBorderColor(
			Color.gray);
	}

  public void initialize(MMObjectIF master) {
    super.initialize(master);
    setDisplayStyle();
  }
	/**
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#getMathObjectFromScreen(double, net.mumie.mathletfactory.MathEntityIF)
	 */
	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF mathObject) {
		throw new TodoException();
	}

	/**
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#getScreenPointFromMath(net.mumie.mathletfactory.MathEntityIF, double)
	 */
	public void getScreenPointFromMath(
		NumberTypeDependentIF entity,
		double[] javaScreenCoordinates) {
		throw new TodoException();
	}

	/**
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#synchronizeMath2Screen()
	 */
	public void synchronizeMath2Screen() {
		math2World(m_zero, m_initInWorld);
		math2World(m_x, m_endInWorldX);
		math2World(m_y, m_endInWorldY);
		synchronizeWorld2Screen();
	}

//	private double calcFactor(double[] minMax) { // input: (x1, x2)
//		double absMax = Math.abs(minMax[1] - minMax[0]);
//		double result = Math.log(absMax) / Math.log(10);
//
//		return Math.pow(10, Math.floor(result)); // 10^floor(ln(|x2-x1|)/ln(10))
//	}

	private double worldUnitLength;

	private void adjustAxis(
		CanvasDrawable line,
		G2DLegendDrawable legend,
    TickMark[] log10table,
    double minTickDistance,
		double[] endOnScreen) {
		MMCanvas canvas = getMasterAsCanvasObject().getCanvas();
		Rectangle canvasRect = canvas.getClientRect();
		double[] coord = new double[4];

		int res =
			Graphics2DHelper.lineRectIntersection2D(
				new double[] {
					m_initOnScreen[0],
					m_initOnScreen[1],
					endOnScreen[0],
					endOnScreen[1] },
				new double[] {
					canvasRect.getX(),
					canvasRect.getY(),
					canvasRect.getX() + canvasRect.getWidth(),
					canvasRect.getY() + canvasRect.getHeight()},
				coord);
    m_hasBeenSynchronized = true;
    for(int i=0;i<coord.length;i++)    
      if(Double.isNaN(coord[i]) || Double.isInfinite(coord[i]))
        m_hasBeenSynchronized = false;
		((G2DLineDrawable) line).setPoints(
			coord[0],
			coord[1],
			coord[2],
			coord[3]);

		double[] worldCoord = new double[coord.length];
		screen2World(coord, worldCoord);

		legend.setStartX(coord[0]);
		legend.setStartY(coord[1]);
		legend.setStopX(coord[2]);
		legend.setStopY(coord[3]);

		double unitLengthOnScreen =
			Graphics2DHelper.pointDistance(
				endOnScreen[0],
				endOnScreen[1],
				m_initOnScreen[0],
				m_initOnScreen[1]);
		double minTickDistanceInWorld = minTickDistance / unitLengthOnScreen;
		double log10 = MathUtilLib.log10(minTickDistanceInWorld);

		//select magnitude
		double magnitude = (int) log10 == 0 ? 1 : Math.pow(10, (int) log10);
		if (log10 < 0) {
			magnitude /= 10;
		}
		//select distance
    TickMark distance = null;

		log10 = log10 < 0 ? 1 + (log10 - (int) log10) : (log10 - (int) log10);
		for (int i = 0; i < log10table.length; i++) {
			distance = log10table[i];
			if (distance.getValue() > log10) 
				break;
		}

		double unitLength = Math.pow(10, distance.getValue());
		legend.setTickMark(distance);
    legend.setMagnitude(magnitude);
    unitLength *= magnitude; 
		legend.setTickDistance(unitLengthOnScreen * unitLength);
		worldUnitLength = unitLength;
		double screenUnitLength = unitLengthOnScreen * unitLength;

		legend.setScaleX(
			(endOnScreen[0] - m_initOnScreen[0]) / unitLengthOnScreen);
		legend.setScaleY(
			(endOnScreen[1] - m_initOnScreen[1]) / unitLengthOnScreen);

		double zeroStartDistance =
			Graphics2DHelper.pointDistance(
				coord[0],
				coord[1],
				m_initOnScreen[0],
				m_initOnScreen[1]);
		if (zeroStartDistance == 0) {
			legend.setStartOffsetX(coord[0]);
			legend.setStartOffsetY(coord[1]);
			legend.setStartValue(0);
		}
		else {
			double offset = zeroStartDistance % legend.getTickDistance();
			/*boolean ptInRect = Graphics2DHelper.pointInRect(m_initOnScreen,
			new double[] {canvasRect.getX(), canvasRect.getY(),
			  canvasRect.getX() + canvasRect.getWidth(), canvasRect.getY() + canvas.getHeight()});*/
			double scaleX = (m_initOnScreen[0] - coord[0]) / zeroStartDistance;
			double scaleY = (m_initOnScreen[1] - coord[1]) / zeroStartDistance;
			legend.setStartOffsetX(coord[0] + scaleX * offset);
			legend.setStartOffsetY(coord[1] + scaleY * offset);
			legend.setStartValue(
				((zeroStartDistance - offset) / unitLengthOnScreen));

			if (Graphics2DHelper
				.dotProduct(
					new double[] {
						endOnScreen[0] - m_initOnScreen[0],
						endOnScreen[1] - m_initOnScreen[1] },
					new double[] {
						legend.getStartOffsetX() - m_initOnScreen[0],
						legend.getStartOffsetY() - m_initOnScreen[1] })
				< 0) {
				legend.setStartValue(-legend.getStartValue());
			}
		}
	}

	/**
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#synchronizeWorld2Screen()
	 */
	public void synchronizeWorld2Screen() {
		world2Screen(m_initInWorld, m_initOnScreen);
		world2Screen(m_endInWorldX, m_endOnScreenX);
		world2Screen(m_endInWorldY, m_endOnScreenY);
    MMCoordinateSystem cs = (MMCoordinateSystem)getMaster();

		G2DLegendDrawable legendDrawableX =
			(G2DLegendDrawable) m_additionalDrawables[2];
		G2DLegendDrawable legendDrawableY =
			(G2DLegendDrawable) m_additionalDrawables[3];

		double[] centerScreen = new double[2];
		double[] centerWorld = new double[2];
		centerScreen[0] = getCanvas().getDrawingBoard().getWidth() / 2.0d;
		centerScreen[1] = getCanvas().getDrawingBoard().getHeight() / 2.0d;
		screen2World(centerScreen, centerWorld);

		adjustAxis(m_additionalDrawables[0], legendDrawableX, cs.getLog10tableX(), cs.getMinTickDistance(), m_endOnScreenX);

		centerWorld[0] -= centerWorld[0] % worldUnitLength;

		adjustAxis(m_additionalDrawables[1], legendDrawableY, cs.getLog10tableY(), cs.getMinTickDistance(), m_endOnScreenY);

		centerWorld[1] -= centerWorld[1] % worldUnitLength;

		world2Screen(centerWorld, centerScreen);
		legendDrawableX.setCenterX(centerScreen[0]);
		legendDrawableX.setCenterY(centerScreen[1]);

		legendDrawableY.setCenterX(centerScreen[0]);
		legendDrawableY.setCenterY(centerScreen[1]);
    ((G2DLegendDrawable)m_additionalDrawables[2]).setAxisLabel(getRealMaster().getXAxisLabel());
    ((G2DLegendDrawable)m_additionalDrawables[3]).setAxisLabel(getRealMaster().getYAxisLabel());
	}

	/**
	 * @see net.mumie.mathletfactory.transformer.GeneralTransformer#render()
	 */
	public void render() {
		super.render();
		(
			(
				G2DLegendDrawable) m_additionalDrawables[2])
					.setGridLineDesciptors(
			((MMCoordinateSystem) getMaster()).getXGridLineDescriptors());
		(
			(
				G2DLegendDrawable) m_additionalDrawables[3])
					.setGridLineDesciptors(
			((MMCoordinateSystem) getMaster()).getYGridLineDescriptors());
	}

  public void draw() {
  	Rectangle newClientRect = getCanvas().getClientRect();
    if(!m_hasBeenSynchronized)
      render();
    // adjust axes to new visible area of canvas
    else if(!newClientRect.equals(m_oldVisibleArea)) // check if area has changed
      render();
    // update internal field
    m_oldVisibleArea = newClientRect;
    super.draw();
  }

  public void setDisplayStyle(){
    m_invisibleDrawables.clear();
    if((getRealMaster().getDisplayStyle() & MMCoordinateSystem.DISPLAY_X) == 0){    
      m_invisibleDrawables.add(m_additionalDrawables[0]);
      m_invisibleDrawables.add(m_additionalDrawables[2]);
      ((G2DLegendDrawable)m_additionalDrawables[3]).setDrawZero(true);
    }
    if((getRealMaster().getDisplayStyle() & MMCoordinateSystem.DISPLAY_Y) == 0){    
      m_invisibleDrawables.add(m_additionalDrawables[1]);
      m_invisibleDrawables.add(m_additionalDrawables[3]);
      ((G2DLegendDrawable)m_additionalDrawables[2]).setDrawZero(true);
    }
    ((G2DLegendDrawable)m_additionalDrawables[2]).setDrawGridLines((getRealMaster().getDisplayStyle() & MMCoordinateSystem.DISPLAY_GRIDLINES_X) != 0);    
    ((G2DLegendDrawable)m_additionalDrawables[3]).setDrawGridLines((getRealMaster().getDisplayStyle() & MMCoordinateSystem.DISPLAY_GRIDLINES_Y) != 0);
    m_zero.setXY(getRealMaster().getCenterX(), getRealMaster().getCenterY());
    m_x.setXY(getRealMaster().getCenterX()+1, getRealMaster().getCenterY());    
    m_y.setXY(getRealMaster().getCenterX(), getRealMaster().getCenterY()+1);
  }
  
  private MMCoordinateSystem getRealMaster(){
    return (MMCoordinateSystem)getMaster();
  }
}
