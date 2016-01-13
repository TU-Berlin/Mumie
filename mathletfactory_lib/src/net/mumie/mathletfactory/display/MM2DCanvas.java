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

package net.mumie.mathletfactory.display;

import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.action.handler.global.GlobalCanvas2DResizeHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalKeyboard2DRotateHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalKeyboard2DScaleHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalKeyboard2DTranslateHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouse2DScaleHandler;
import net.mumie.mathletfactory.action.handler.global.GlobalMouse2DTranslateHandler;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.util.Affine2DDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This class acts as a base class for all 2 dimensional rendering.
 *
 * @author Amsel, vossbeck
 * @mm.docstatus finished
 */
public abstract class MM2DCanvas extends MMCanvas {

	private static String m_default2DScreenType;
	
	private Affine2DDouble m_world2Screen;
	private Affine2DDouble m_screen2World;

	private GlobalCanvas2DResizeHandler m_transformationHandler;

  private Rectangle2D m_autoscaleRect = null;
  private boolean m_keepAR = false;

	public MM2DCanvas() {
		super();
		// initialize the worldToScreen Transformation (they are initialized to the
		// identity)
		m_world2Screen = new Affine2DDouble();
		m_screen2World = new Affine2DDouble();
		m_transformationHandler = new GlobalCanvas2DResizeHandler(this);
		new GlobalMouse2DScaleHandler(this);
		new GlobalMouse2DTranslateHandler(this);
		new GlobalKeyboard2DTranslateHandler(this);
		new GlobalKeyboard2DRotateHandler(this);
		new GlobalKeyboard2DScaleHandler(this);
    addComponentListener(this);
	}

	public Affine2DDouble getWorld2Screen() {
		//System.out.println("w2s = "+m_world2Screen);
		return m_world2Screen;
	}

	public void setWorld2Screen(Affine2DDouble aFastTransformation) {
		m_world2Screen = aFastTransformation;
		adjustTransformations();
	}

	public Affine2DDouble getScreen2World() {
		return m_screen2World;
	}

	public void adjustTransformations() {
		m_world2Screen.getInverse(m_screen2World);
	}

	public void getWorldFromScreen(
		double[] javaScreenCoords,
		double[] worldDrawCoords) {
		getScreen2World().applyTo(javaScreenCoords, worldDrawCoords);
	}

	public GlobalCanvas2DResizeHandler getW2STransformationHandler() {
		return m_transformationHandler;
	}

	public double[] getWorldLowerLeft() {
		double[] screenLowerLeft = new double[] { 0, getHeight()};
		double[] result = new double[2];
		getScreen2World().applyTo(screenLowerLeft, result);
		return result;
	}

	public double[] getWorldUpperRight() {
		double[] screenUpperRight = new double[] { getWidth(), 0 };
		double[] result = new double[2];
		getScreen2World().applyTo(screenUpperRight, result);
		return result;
	}

	public Rectangle2D getWorldBounds() {
		double[] screenLowerLeft = new double[] { 0, getHeight()};
		double[] screenUpperRight = new double[] { getWidth(), 0 };
		double[] worldLowerLeft = new double[2];
		double[] worldUpperRight = new double[2];

		throw new TodoException();
	}

    /**
     * Sets the world2screen mapping so that all objects displayed within the canvas are completely
     * visible on the drawing board (provided their {@link net.mumie.mathletfactory.mmobject.VisualizeInCanvasIF#getWorldBoundingBox getWorldBoundingBox}
     * method is implemented properly).
     */
  public void autoScale() {
  	autoScale(false);
  }

  /**
   * Sets the world2screen mapping so that all objects displayed within the canvas are completely
   * visible on the drawing board (provided their {@link net.mumie.mathletfactory.mmobject.VisualizeInCanvasIF#getWorldBoundingBox getWorldBoundingBox}
   * method is implemented properly).
   * @param keepAR  autoScale but keep aspect ratio
   */
	public void autoScale(boolean keepAR) {
    m_keepAR = keepAR;
		m_autoscaleRect = new Rectangle2D.Double(); //origin always visible
		for (int i = 0; i < getObjectCount(); i++) {
			MMCanvasObjectIF object = getObject(i);
			if (object.isVisible()) {
				Rectangle2D objectRect = object.getWorldBoundingBox();
				if (objectRect != null) {
					m_autoscaleRect = m_autoscaleRect == null ? objectRect : m_autoscaleRect.createUnion(objectRect);
				}
			}
		}
		if (m_autoscaleRect.getWidth() == 0) {
			m_autoscaleRect =
				new Rectangle2D.Double(
					m_autoscaleRect.getX() - .5,
					m_autoscaleRect.getY(),
					1,
					m_autoscaleRect.getHeight());
		}
		if (m_autoscaleRect.getHeight() == 0) {
			m_autoscaleRect =
				new Rectangle2D.Double(
					m_autoscaleRect.getX(),
					m_autoscaleRect.getY() - .5,
					m_autoscaleRect.getWidth(),
					1);
		}
		m_autoscaleRect =
			new Rectangle2D.Double(
				m_autoscaleRect.getCenterX() - m_autoscaleRect.getWidth() * 0.55,
				m_autoscaleRect.getCenterY() - m_autoscaleRect.getHeight() * 0.55,
				m_autoscaleRect.getWidth() * 1.1,
				m_autoscaleRect.getHeight() * 1.1);
    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
	}


  public void componentResized(ComponentEvent e) {
		if (getDrawingBoard() != null) {
	    getW2STransformationHandler().doAction();
	    rescale();
		}
  }

    /**
     * Rescales the world area displayed by the canvas according to the prior {@link #autoScale()} call.
     * (This method will have no effect, until autoscaling has been invoked).
     */
    public void rescale() {
		if (m_autoscaleRect != null) {
			if (m_keepAR) {
				getWorld2Screen().setRectToScreenKeepAR(m_autoscaleRect,
						getDrawingBoard().getWidth(), getDrawingBoard().getHeight());
			} else {
				getWorld2Screen().setRectToScreen(m_autoscaleRect,
						getDrawingBoard().getWidth(), getDrawingBoard().getHeight());
			}
			adjustTransformations();
			renderScene();
			repaint();
		}
	}

  public static MM2DCanvas createCanvas2D() {
  	if(m_default2DScreenType == null)
  		throw new RuntimeException("Default 2D screen type was not defined !");
  	return (MM2DCanvas) MMCanvas.createCanvas(m_default2DScreenType);
  }
  
  public static void setDefault2DScreenType(String screenTypeName) {
  	m_default2DScreenType = screenTypeName;
  }
  
  public static String getDefault2DScreenType() {
  	return m_default2DScreenType;
  }
}
