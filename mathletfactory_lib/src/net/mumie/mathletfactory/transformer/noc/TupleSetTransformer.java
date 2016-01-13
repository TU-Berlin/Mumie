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

import net.mumie.mathletfactory.display.noc.MMTupleSetPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMTupleSet;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

public class TupleSetTransformer extends ContainerObjectTransformer{
	  public void initialize(MMObjectIF masterObject) {
		    super.initialize(masterObject);
		    m_mmPanel = new MMTupleSetPanel(getRealMaster(), this);
		    render();
	  }

	  public void render() {
		  if(((MMTupleSetPanel)m_mmPanel).getDimension()!=getRealMaster().getDimension())
			  ((MMTupleSetPanel) m_mmPanel).setDimension(getRealMaster().getDimension());
		  if(((MMTupleSetPanel)m_mmPanel).isRightVariablesEnabled()!=getRealMaster().isRightVariablesEnabled())
			  ((MMTupleSetPanel)m_mmPanel).setRightVariablesEnabled(getRealMaster().isRightVariablesEnabled());
		  if(((MMTupleSetPanel)m_mmPanel).isFactorsEnabled()!=getRealMaster().isFactorsEnabled())
			  ((MMTupleSetPanel)m_mmPanel).setFactorsEnabled(getRealMaster().isFactorsEnabled());
		  if(((MMTupleSetPanel)m_mmPanel).isEditable()!=getRealMaster().isEditable())
			  ((MMTupleSetPanel)m_mmPanel).setEditable(getRealMaster().isEditable());
		  if(((MMTupleSetPanel)m_mmPanel).isButtonsEnabled()!=getRealMaster().isButtonsEnabled())
			  ((MMTupleSetPanel)m_mmPanel).setButtonsEnabled(getRealMaster().isButtonsEnabled());
		  if(((MMTupleSetPanel)m_mmPanel).getLeftComponentCount()!=getRealMaster().getNumberOfComponents())
			  ((MMTupleSetPanel) m_mmPanel).updateLeftMMObjects();
		  else ((MMTupleSetPanel)m_mmPanel).updateLeftValues(getRealMaster().getVectors(),getRealMaster().getFactors());
		  if(((MMTupleSetPanel)m_mmPanel).getRightComponentCount()!=getRealMaster().getNumberOfVariables())
			  ((MMTupleSetPanel) m_mmPanel).updateRightMMObjects();
		  else ((MMTupleSetPanel)m_mmPanel).updateRightValues(getRealMaster().getRightVariables());
		  super.render();
		  m_mmPanel.setSize(m_mmPanel.getPreferredSize());
	  }

	  private MMTupleSet getRealMaster() {
		  return (MMTupleSet)m_masterMMObject;
	  }
}
