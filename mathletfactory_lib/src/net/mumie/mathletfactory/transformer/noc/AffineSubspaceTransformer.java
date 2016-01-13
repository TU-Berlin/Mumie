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

import java.util.logging.Logger;

import net.mumie.mathletfactory.display.noc.geom.MMAffineSubspacePanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffineSubspaceIF;

/**
 * Transformer for 
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffineSubspace}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class AffineSubspaceTransformer extends TransformerUsingMatrixPanel {

	private static Logger logger =
		Logger.getLogger(AffineSubspaceTransformer.class.getName());

	public void initializeForMatrixPanel(MMObjectIF masterObject) {
		if (!m_isInitialized) {
			m_masterMMObject = (MMAffineSubspaceIF)masterObject;
			m_mmPanel = new MMAffineSubspacePanel(masterObject, this);
			m_isInitialized = true;
		}
		else
			logger.warning("initialize:: call happened at least a second time");
	}

	public void render() {
		super.render();
    //System.out.println("master dimension is "+getRealMaster().getDimension()+", panel dimension is "+getSubspacePanel().getDimension());
    if(getRealMaster().getDimension() != getSubspacePanel().getDimension())
      getSubspacePanel().createPanel();
    if(getRealMaster().getDimension() == -1)
      return;
		getSubspacePanel().getPointPanel().setValuesFromNumberMatrix(
			getRealMaster().getAffineCoordinates()[0]);
		getSubspacePanel().getPointPanel().setEditable(
			m_masterMMObject.isEditable());
    if(getRealMaster().getDimension() == 0)
      return;  
		getSubspacePanel().getFirstDirectionPanel().setValuesFromNumberMatrix(
			(getRealMaster().getAffineCoordinates()[1]).subFrom(
				getRealMaster().getAffineCoordinates()[0]));
		getSubspacePanel().getFirstDirectionPanel().setEditable(
			m_masterMMObject.isEditable());

		if (getRealMaster().getDimension() == 1 ) 
      return;
	  getSubspacePanel().getSecondDirectionPanel().setValuesFromNumberMatrix(
		  (getRealMaster().getAffineCoordinates()[2]).subFrom(
			 getRealMaster().getAffineCoordinates()[0]));
			getSubspacePanel().getSecondDirectionPanel().setEditable(
				m_masterMMObject.isEditable());
	}

	private MMAffineSubspacePanel getSubspacePanel() {
		return (MMAffineSubspacePanel)m_mmPanel;
	}

	private MMAffineSubspaceIF getRealMaster() {
		return (MMAffineSubspaceIF)m_masterMMObject;
	}
}
