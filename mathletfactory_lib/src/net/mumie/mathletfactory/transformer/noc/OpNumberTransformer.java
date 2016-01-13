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
 * Created on Apr 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.mumie.mathletfactory.transformer.noc;

import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;


/**
 * @author gronau
 */
public class OpNumberTransformer extends ContainerObjectTransformer {

	public void initialize(MMObjectIF master) {
		super.initialize(master);
		m_mmPanel = new MMNumberPanel(master, null);
		render();
	}

	public void render() {
		super.render();
		((MMNumberPanel)m_mmPanel).setOperation(getRealMaster().getOperation());
		((MMNumberPanel)m_mmPanel).setEditable(m_masterMMObject.isEditable());
		((MMNumberPanel)m_mmPanel).render();
	}

	public MOpNumber getRealMaster() {
		return (MOpNumber)getMaster();
	}

}
