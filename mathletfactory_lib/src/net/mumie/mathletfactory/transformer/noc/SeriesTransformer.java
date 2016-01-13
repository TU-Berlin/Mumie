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

import net.mumie.mathletfactory.display.noc.function.MMFunctionPanel;
import net.mumie.mathletfactory.display.noc.function.MMSeriesPanel;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 *  This class transforms 
 *  {@link net.mumie.mathletfactory.mmobject.MMContainerObjectIF}s implementing
 *  {@link net.mumie.mathletfactory.algebra.op.UsesOperationIF} into 
 *  a {@link net.mumie.mathletfactory.display.noc.FunctionPanel}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SeriesTransformer extends ContainerObjectTransformer {
    
  public void initialize(MMObjectIF master) {
    super.initialize(master);
    m_mmPanel = new MMSeriesPanel(master, this);
    render();
  }
  
  /**
   * Refreshes the panel's mathematical content, the font and the 
   * editable property. 
   * @see net.mumie.mathletfactory.transformer.GeneralTransformer#render()
   */
  public final void render(){
  	super.render();
    ((MMSeriesPanel)m_mmPanel).setFunctionExpression(((UsesOperationIF)getMaster()).getOperation());
    ((MMSeriesPanel)m_mmPanel).setEditable(getMaster().isEditable());
    ((MMSeriesPanel)m_mmPanel).setShowWarnButton(false);
    if(getMaster().getDisplayProperties().isFontRenderedWithObjectColor())
      ((MMFunctionPanel)m_mmPanel).setForeground((getMaster().getDisplayProperties().getObjectColor()));
  }
}

