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

package net.mumie.mathletfactory.transformer.noc;


import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.display.noc.matrix.MMLESPanel;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

public class LESTransformer extends ContainerObjectTransformer implements ChangeListener{
	
	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		m_mmPanel = new MMLESPanel(masterObject, this);
		m_isInitialized = true;
		((NumberMatrix)masterObject).addDimensionChangeListener(this);
		render();
	}
	
	public void render() {
		((MMLESPanel)m_mmPanel).setValuesFromNumberMatrix((NumberMatrix)getMaster());
		((MMLESPanel)m_mmPanel).setEditable(getMaster().isEditable());
		m_mmPanel.render();
	}
	
	public void stateChanged(ChangeEvent event) {
	    ((MMLESPanel)m_mmPanel).createSurface();
	  }

}
