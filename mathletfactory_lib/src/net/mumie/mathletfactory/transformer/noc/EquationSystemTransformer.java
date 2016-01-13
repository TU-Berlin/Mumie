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

/*
 * Created on 07.10.2003
 *
 */
package net.mumie.mathletfactory.transformer.noc;

import net.mumie.mathletfactory.display.noc.MMEquationSystemPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.MMEquationSystem;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * Transformer for {@link net.mumie.mathletfactory.mmobject.algebra.MMEquationSystem}.
 * 
 * @author Gronau
 * @mm.docstatus finished
 */
public class EquationSystemTransformer extends ContainerObjectTransformer {

	public void initialize(MMObjectIF master) {
		super.initialize(master);
		m_mmPanel = new MMEquationSystemPanel(master, this);
		render();
	}

	public void render() {
		((MMEquationSystemPanel) m_mmPanel).setValues(
			getRealMaster().getCoeffMatrix(),
			getRealMaster().getInhomogeneityVector());
		((MMEquationSystemPanel) m_mmPanel).render();
	}

	private MMEquationSystem getRealMaster() {
		return (MMEquationSystem) m_masterMMObject;
	}
}
