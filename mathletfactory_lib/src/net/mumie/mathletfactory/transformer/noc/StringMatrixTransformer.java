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

import net.mumie.mathletfactory.display.noc.matrix.MMStringMatrixPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.util.MMStringMatrix;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.util.MMStringMatrix}.
 *
 * @author gronau
 * @mm.docstatus finished
 */
public class StringMatrixTransformer extends ContainerObjectTransformer {

  public void initialize(MMObjectIF masterObject) {
    super.initialize(masterObject);
    m_mmPanel = new MMStringMatrixPanel(getRealMaster(), this);
    getRealMMPanel().setEditable(getRealMaster().isEditable());
    getMMPanel().addPropertyChangeListener(getRealMaster());
  }

  public void render() {
		super.render();
    getRealMMPanel().setValues(getRealMaster());
    getRealMMPanel().setEditable(getRealMaster().isEditable());
    getMMPanel().render();
  }

  private MMStringMatrix getRealMaster() {
    return (MMStringMatrix) getMaster();
  }

  private MMStringMatrixPanel getRealMMPanel() {
    return (MMStringMatrixPanel) getMMPanel();
  }
}
