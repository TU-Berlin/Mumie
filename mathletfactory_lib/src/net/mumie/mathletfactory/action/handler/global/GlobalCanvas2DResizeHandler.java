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

package net.mumie.mathletfactory.action.handler.global;

import net.mumie.mathletfactory.display.MM2DCanvas;

/**
 * A handler that deals with the resizing of a 2D canvas. This handler differs from other
 * global handlers in that it does not process <code>MMEvents</code> but is directly called
 * from the <code>MMCanvas2D</code>, when it is resized (i.e. a resize event occurs).
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class GlobalCanvas2DResizeHandler {
  public static final int FIRST_QUADRANT = 1;
  public static final int SECOND_QUADRANT = 2;
  public static final int THIRD_QUADRANT = 4;
  public static final int FOURTH_QUADRANT = 8;
  

	/** the MM2DCanvas for which this GlobalCanvas2DResizeHandler works */
	private MM2DCanvas m_canvas;

	/* the handler stores the latest dimensions of the drawing board of the client canvas */
	private double m_canvasWidth, m_canvasHeight;
	
	
	private double m_xToCentre = 0, m_yToCentre = 0, m_worldWidth = 1, m_worldHeight = 1;
	private boolean m_transformationInitialised = false;
	private int m_callCount = 0;
	private int m_adjustTransformationCount = 0;
  private boolean m_adjustQuadrants = false;
  private int m_quandrantsToShow = FIRST_QUADRANT | SECOND_QUADRANT | THIRD_QUADRANT | FOURTH_QUADRANT;
  
	/**
	 * Initialises a <code>GlobalCanvas2DResizeHandler</code> working for <code>aCanvas</code>
	 * as it's client. 
	 */
	public GlobalCanvas2DResizeHandler(MM2DCanvas aCanvas) {
		m_canvas = aCanvas;
	}

  private void adjustTransformation(){
    m_canvasWidth = m_canvas.getDrawingBoard().getWidth();
    m_canvasHeight = m_canvas.getDrawingBoard().getHeight();
    m_canvas.getWorld2Screen().setRectToScreen(new double[]{m_xToCentre - m_worldWidth/2, m_yToCentre - m_worldHeight/2}, 
      new double[]{m_xToCentre + m_worldWidth/2, m_yToCentre + m_worldHeight/2}, m_canvasWidth, m_canvasHeight);
    m_canvas.adjustTransformations();
    m_canvas.getWorld2Screen().getHistory().clear();
    m_canvas.getWorld2Screen().addSnapshotToHistory();
  }
  
  public boolean isTransformationInitialized() {
  	return m_transformationInitialised;
  }

	private void adjustUFTransformation() {
		m_adjustTransformationCount++;

		/*
		 * this is: make the shorter side of the canvas rectangle correspond to
		 * the dimension of m_worldDimX. Consequently you will at least see a world
		 * square of length m_worldDimX in the canvas!
		 */
		double ufScale =
			Math.min(m_canvas.getDrawingBoard().getWidth() , 
               m_canvas.getDrawingBoard().getHeight()) / m_worldWidth;
		m_canvas.getWorld2Screen().setPointToCanvasCentreUF(
			m_xToCentre,
			m_yToCentre,
			ufScale,
			m_canvas.getDrawingBoard().getWidth(),
			m_canvas.getDrawingBoard().getHeight());
		m_canvas.adjustTransformations();
		m_canvasWidth = m_canvas.getDrawingBoard().getWidth();
		m_canvasHeight = m_canvas.getDrawingBoard().getHeight();
    if (m_adjustQuadrants)
      showQuadrant(m_quandrantsToShow);
    m_canvas.getWorld2Screen().getHistory().clear();
    m_canvas.getWorld2Screen().addSnapshotToHistory();
	}

	/**
	 * Will be called when an componentEvent occured...
	 */
	public void doAction() {
		m_callCount++;
		if (m_canvas.getHeight() == 0 || m_canvas.getWidth() == 0)
			return;
		else {
      if(m_worldWidth == m_worldHeight)
        adjustUFTransformation();
      else
        adjustTransformation();
		}
		m_canvas.renderFromWorldDraw();
		m_canvas.repaint();
	}

  /**
   * Determines, which coordinates of the world should be shown as center in the associated canvas.
   */
	public GlobalCanvas2DResizeHandler setWorldCentreCoordinates(
		double x,
		double y) {
		m_xToCentre = x;
		m_yToCentre = y;
		m_transformationInitialised = false;
    if(m_worldWidth == m_worldHeight)
		  adjustUFTransformation();
    else
      adjustTransformation();
		m_transformationInitialised = true;
		return this;
	}
  
  /**
   * Moves the origin such that the quadrant(s) given in quandrantsToShow is(are) visible.
   * <ul> 
   * <li>1: first quadrant
   * <li>2: second quadrant
   * <li>4: third quadrant
   * <li>8: fourth quadrant
   * </ul>
   * Any combination of the above numbers is allowed. I.e. 3 show the upper 
   * halfplane. The methode assumes, that the canvas is not rotated on screen. 
   */
  public GlobalCanvas2DResizeHandler showQuadrant(int quandrantsToShow) {
    if ((m_canvas.getWidth() == 0) || (m_canvas.getHeight() == 0)) {
      m_quandrantsToShow = quandrantsToShow;
      m_adjustQuadrants = true;
      return this;
    }
    boolean showLeft = ((quandrantsToShow & 2) != 0) || ((quandrantsToShow & 4) != 0);
    boolean showRight = ((quandrantsToShow & 1) != 0) || ((quandrantsToShow & 8) != 0);
    boolean showTop = ((quandrantsToShow & 1) != 0) || ((quandrantsToShow & 2) != 0);
    boolean showBottom = ((quandrantsToShow & 4) != 0) || ((quandrantsToShow & 8) != 0);
    double[] worldLowerLeft = m_canvas.getWorldLowerLeft();
    double[] worldUpperRight = m_canvas.getWorldUpperRight();
    double width = (worldUpperRight[0] - worldLowerLeft[0]);
    double height = (worldUpperRight[1] - worldLowerLeft[1]);
    m_xToCentre = worldLowerLeft[0] + width/2;
    m_yToCentre = worldLowerLeft[1] + height/2;
    if (showLeft) {
      if (showRight) {
        m_xToCentre = 0;
      } else {
        m_xToCentre = -width/2;
      }
    } else {
      if (showRight) {
        m_xToCentre = width/2;
      }
    }
    if (showTop) {
      if (showBottom) {
        m_yToCentre = 0;
      } else {
        m_yToCentre = height/2;
      }
    } else {
      if (showBottom) {
        m_yToCentre = -height/2;
      }
    }
    m_adjustQuadrants = false;
    setWorldCentreCoordinates(m_xToCentre, m_yToCentre);
    return this;
  }

  /**
   * Sets the dimensions of the viewport (i.e. the part of the world seen in the canvas) so that an
   * aspect ratio of 1:1 is achieved (i.e. a square is rendered as a square). The width or height 
   * (whichever is lesser) of the canvas is set to <code>dim</code>. 
   */
	public GlobalCanvas2DResizeHandler setUniformWorldDim(double dim) {
		m_worldWidth = dim;
    m_worldHeight = dim;
		m_transformationInitialised = false;
		adjustUFTransformation();
		m_transformationInitialised = true;
		return this;
	}

  /**
   * Sets the width and height of the world to be viewed in the canvas. 
   */
  public GlobalCanvas2DResizeHandler setWorldDim(double worldWidth, double worldHeight) {
    m_worldWidth = worldWidth;
    m_worldHeight = worldHeight;
    m_transformationInitialised = false;
    adjustTransformation();
    m_transformationInitialised = true;
    return this;
  }
}
