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

package net.mumie.mathletfactory.transformer.j3d;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.j3d.J3DDrawable;
import net.mumie.mathletfactory.display.j3d.J3DParameterizedSurfaceDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPolyLineDrawable;
import net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable;
import net.mumie.mathletfactory.display.j3d.J3DRenderingHints;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMParametricFunctionInR3;

/**
 *  This class transforms 
 *  {@link net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMParametricFunctionInR3} 
 *  objects into a Java3D 
 *  {@link net.mumie.mathletfactory.display.j3d.J3DParameterizedSurfaceDrawable}.
 *  
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class ParametricFunctionInR3Transformer
	extends Affine3DDefaultTransformer {

	/** Every time the function is recalculated, the version number is incremented */
	private int m_version = 0;
	private LineDisplayProperties ldp;
	/** 
	 * Constructs the transformer for the given 
	 * {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas} display.
	 */
	public ParametricFunctionInR3Transformer() {
		m_allDrawables =
			new CanvasDrawable[] { new J3DParameterizedSurfaceDrawable(), new J3DPolyLineDrawable()};
		m_activeDrawable = m_allDrawables[0];
		m_additionalDrawables = new CanvasDrawable[]{new J3DPolyLineDrawable(),new J3DPolyLineDrawable()};
		ldp = new LineDisplayProperties();
		ldp.setLineWidth(1.5);
    	m_additionalProperties = new DisplayProperties[]{ldp,ldp};
	}

	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		((J3DDrawable) m_additionalDrawables[0]).setCanvas(
				(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		((J3DDrawable) m_additionalDrawables[1]).setCanvas(
				(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		((J3DPolyLineDrawable)m_additionalDrawables[0]).setPoints(new NumberTuple[]{new NumberTuple(getMaster().getNumberClass(),3),new NumberTuple(getMaster().getNumberClass(),3)});
    	((J3DPolyLineDrawable)m_additionalDrawables[1]).setPoints(new NumberTuple[]{new NumberTuple(getMaster().getNumberClass(),3),new NumberTuple(getMaster().getNumberClass(),3)});
		m_version = getRealMaster().getVersion()-1;
		synchronizeMath2Screen();
	}

	public double[] getWorldPickPointFromMaster() {
		return new double[3];
	}

	public void synchronizeMath2Screen() {
		boolean lineDisplayed = ((Boolean) getRealMaster().getDisplayProperties().getProperty(J3DRenderingHints.J3D_LINE_DISPLAYED_PROPERTY, new Boolean(false))).booleanValue();
		lineDisplayed = lineDisplayed && getRealMaster().isVisible();
		if(!lineDisplayed){
			((J3DRemovableDrawable)m_additionalDrawables[0]).removeShape();
			((J3DRemovableDrawable)m_additionalDrawables[1]).removeShape();
			m_invisibleDrawables.add(m_additionalDrawables[0]);
			m_invisibleDrawables.add(m_additionalDrawables[1]);
		}else{
			m_invisibleDrawables.remove(m_additionalDrawables[0]);
			m_invisibleDrawables.remove(m_additionalDrawables[1]);
		}
	if(getRealMaster().getParameters().length == 1){
		((J3DRemovableDrawable)m_additionalDrawables[0]).removeShape();
		((J3DRemovableDrawable)m_additionalDrawables[1]).removeShape();
		m_invisibleDrawables.add(m_additionalDrawables[0]);
		m_invisibleDrawables.add(m_additionalDrawables[1]);
      if(m_activeDrawable != m_allDrawables[1]){
        ((J3DRemovableDrawable)m_activeDrawable).removeShape();      
        m_activeDrawable = m_allDrawables[1];
      }
      if (getRealMaster().getVersion() > m_version) {
        ((J3DPolyLineDrawable) m_activeDrawable).setPoints(getRealMaster().getValues()[0]);
        m_version = getRealMaster().getVersion();
      }      
    } else {      
      if(m_activeDrawable != m_allDrawables[0]){
        ((J3DRemovableDrawable)m_activeDrawable).removeShape();      
        m_activeDrawable = m_allDrawables[0];
      }
      if (getRealMaster().getVersion() > m_version) {
        ((J3DParameterizedSurfaceDrawable) m_activeDrawable).setValues(
          getRealMaster().getValues());
        m_version = getRealMaster().getVersion();
        if(lineDisplayed){
	    	((J3DPolyLineDrawable)m_additionalDrawables[0]).setPoints(createFlatIValues());
	    	((J3DPolyLineDrawable)m_additionalDrawables[1]).setPoints(createFlatJValues());
        }
      }
    }
	}

	private MMParametricFunctionInR3 getRealMaster() {
		return (MMParametricFunctionInR3) m_masterMMObject;
	}



	public void render() {
		super.render();
		renderLabel(getWorldPickPointFromMaster());
	}
	
	public NumberTuple[] createFlatJValues(){
		NumberTuple[][] values = getRealMaster().getValues();
		NumberTuple[] flat = new NumberTuple[values.length*values[0].length];
		int index=0;
		int richtung =0;
		for(int i=0;i<values.length;i++){
			if(richtung ==0){
				//down
				for(int j=0;j<values[0].length;j++){
					flat[index++] = values[i][j];
				}
				richtung=1;
			}else{
				//up
				for(int j=values[0].length-1;j>=0;j--){
					flat[index++] = values[i][j];
				}
				richtung=0;
			}
		}
		return flat;
	}
	public NumberTuple[] createFlatIValues(){
		NumberTuple[][] values = getRealMaster().getValues();
		NumberTuple[] flat = new NumberTuple[values.length*values[0].length];
		int index=0;
		int richtung =0;
		for(int j=0;j<values[0].length;j++){
			if(richtung ==0){
				//right
				for(int i=0;i<values.length;i++){
					flat[index++] = values[i][j];
				}
				richtung=1;
			}else{
				//left
				for(int i=values.length-1;i>=0;i--){
					flat[index++] = values[i][j];
				}
				richtung=0;
			}
		}
		return flat;
	}
	
	public void remove(){
		super.remove();
		((J3DRemovableDrawable)m_additionalDrawables[0]).removeShape();
		((J3DRemovableDrawable)m_additionalDrawables[1]).removeShape();
	}
}
