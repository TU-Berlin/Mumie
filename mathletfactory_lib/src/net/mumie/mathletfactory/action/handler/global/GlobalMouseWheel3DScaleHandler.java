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

import java.awt.event.MouseWheelEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;

/**
 * This handler allows the user to translate himself in the world 
 * (thus virtually translating the scene in the other direction) by using
 * the mousewheel. 
 * 
 * @author Tan
 * @mm.docstatus finished
 */
public class GlobalMouseWheel3DScaleHandler extends Global3DHandler {
	
	private double    m_canvasWidth;
	private int       m_xMouseMoveInPixel;
	private double    m_adjustScale = 1.2;
	private double[]  m_direction = new double[3];
	private double[]  m_transformedDirection = new double[3];
	private int m_xPixOld;
	private int m_xPixNew;
	
	public GlobalMouseWheel3DScaleHandler(MM3DCanvas aCanvas){
		super(aCanvas);
	}

	public boolean doAction(MMEvent event) {
		if(event.getEventType()!=507) return false;
		Affine3DDouble afg = getCanvas3D().getWorld2WorldView();
		int zoom = event.getMouseWheelRotation();
		  if(zoom<0){
			  //zoom in
			  m_xPixNew = m_xPixOld+2;
		  }
		  if(zoom>0){
			  //zoom out
			  m_xPixNew = m_xPixOld-2;
		  }
	    m_xMouseMoveInPixel = Math.abs(m_xPixNew-m_xPixOld);
	    m_canvasWidth = m_canvas.getWidth();
	    if(m_xPixNew >= m_xPixOld)
	      m_direction[2] = 1 - (m_adjustScale*(m_canvasWidth + m_xMouseMoveInPixel)/m_canvasWidth);
	    else
	      m_direction[2] = -1 + (m_adjustScale*(m_canvasWidth + m_xMouseMoveInPixel)/m_canvasWidth);
	    afg.applyDeformationPartTo(m_direction, m_transformedDirection);
	    afg.leftTranslate(m_transformedDirection);
	    getCanvas3D().setWorld2WorldView(afg);
	    
	    m_canvas.renderFromWorldDraw();
	    m_canvas.repaint();
	    
	    m_xPixOld = m_xPixNew;
	    return false;
	}
	
	 public void finish(){
		    super.finish();
		    getCanvas3D().getWorld2WorldView().addSnapshotToHistory();
//		    m_firstValuesUnset = true;
     }
	 
	protected boolean userDefinedDealsWith(MMEvent event) {
		if(event.getEventType()==MouseWheelEvent.MOUSE_WHEEL){
			return true;
		}
		return false;
	}

}
