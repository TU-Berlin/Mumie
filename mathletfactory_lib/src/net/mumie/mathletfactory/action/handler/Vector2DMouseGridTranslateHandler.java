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

package net.mumie.mathletfactory.action.handler;

import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;
import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This handler allows a user to translate the end point of a selected 
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector}
 * by dragging it with the mouse on a grid of user defined width.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Vector2DMouseGridTranslateHandler extends MMHandler {

	private MMEvent m_event;
  private double unitInMath;
	private final double[] m_newScreenCoords = new double[2];
	private double[] m_newWorldCoords = new double[2];
  
  public Vector2DMouseGridTranslateHandler(JComponent display){
    this(display, 0);
  }
  
	public Vector2DMouseGridTranslateHandler(JComponent display, double unit) {
		super(display);
		m_event =
			new MMEvent(
				MouseEvent.MOUSE_DRAGGED,
				MouseEvent.BUTTON1_MASK,
				0,
				MMEvent.NO_KEY,
				MMEvent.NO_MODIFIER_SET);
    setUnitInMath(unit);
	}
	
	public boolean userDefinedDealsWith(MMEvent event) {
		return m_event.equals(event);
	}

	public void userDefinedAction(MMEvent event) {
    // case unitInMath=0 is not good implemented!
    if(unitInMath == 0){
      if(getMaster() instanceof MMDefaultR2Vector)             
        setUnitInMath(((MMDefaultR2)((MMDefaultR2Vector)getMaster()).getVectorSpace()).getGridInMath());        
      else        
        setUnitInMath(0.1);
    } 
    //System.out.println("setting unit to "+getUnitInMath());
		m_newScreenCoords[0] = event.getX();
		m_newScreenCoords[1] = event.getY();

		((Canvas2DObjectTransformer) ((MMCanvasObjectIF) getMaster()).getCanvasTransformer())
				.getScreen2World().applyTo(m_newScreenCoords, m_newWorldCoords);

    for (int i = 0; i < 2;  i++) {                    
      m_newWorldCoords[i] = rightOnGrid(m_newWorldCoords[i], unitInMath);               
    }
		
    if(getMaster() instanceof MMDefaultR2Vector){
      ((MMDefaultR2Vector)getMaster()).setDefaultCoordinates(m_newWorldCoords[0], m_newWorldCoords[1]);
    } else 
    ((MMComplex)getMaster()).setComplex(m_newWorldCoords[0], m_newWorldCoords[1]);
	}
	
	public void userDefinedFinish() {
	}

	private double rightOnGrid(double originalValue, double unit) {
		return Math.rint((originalValue / unit)) * unit;
	}

  /** Checks, whether master is MMComplex or MMDefaultR2Vector. */
  public void setMaster(MMObjectIF master) {
    if(!(master instanceof MMDefaultR2Vector) && !(master instanceof MMComplex))
      throw new IllegalArgumentException("Master must be MMComplex or MMDefaultR2Vector!");
    super.setMaster(master);
  }

  /** Returns the width of a unit in the grid (in mathematical coordinates).*/
  public double getUnitInMath() {
    return unitInMath;
  }

  /** Sets the width of a unit in the grid (in mathematical coordinates).*/
  public void setUnitInMath(double number) {
    unitInMath = number;
  }
}
