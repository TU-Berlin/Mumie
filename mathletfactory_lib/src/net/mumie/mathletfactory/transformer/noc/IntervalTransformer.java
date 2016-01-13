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

import net.mumie.mathletfactory.display.noc.set.MMIntervalPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * @author gronau
 * @mm.docstatus finished
 */
public class IntervalTransformer extends ContainerObjectTransformer {
	
	private MMIntervalPanel m_renderer;
		
	public void initialize(MMObjectIF master) {
		super.initialize(master);
		m_mmPanel = new MMIntervalPanel(master, this);
		m_renderer = (MMIntervalPanel)m_mmPanel;
		render();
	}
	
	public void render() {
		super.render();
		m_renderer.setBoundary(
				getRealMaster().getLowerBoundaryVal(), getRealMaster().getLowerBoundaryType(),
				getRealMaster().getUpperBoundaryVal(), getRealMaster().getUpperBoundaryType(), 
				getRealMaster().getForm());
		m_renderer.setEditable(getRealMaster().isEditable());
		m_renderer.setLowerBoundaryEditable(getRealMaster().isLowerBoundaryEditable());
		m_renderer.setUpperBoundaryEditable(getRealMaster().isUpperBoundaryEditable());
		m_renderer.render();
	}
	
	private MMInterval getRealMaster() {
		return (MMInterval)m_masterMMObject;
	}
}
