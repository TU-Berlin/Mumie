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

package net.mumie.mathletfactory.display.noc.function;

import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * A panel that renders the symbolic representation of a series.
 * 
 * @author Paehler, Gronau
 * @mm.docstatus finished
 */
public class MMSeriesPanel extends MMFunctionPanel {

  public MMSeriesPanel(MMObjectIF master, ContainerObjectTransformer transformer) {
    super(master, transformer);
  }

  /**
   * If this method is called, the function panel generates a function
   * string (e.g "s_n = ") for the function panel depending
   * on the masters label (in the above example the label has the value "s").
   * @see net.mumie.mathletfactory.mmobject.MMObjectIF#getLabel
   */
  public String createFunctionStringFromLabel(MMObjectIF master) {
  	String result = " ";
    String[] vars = ((UsesOperationIF)master).getOperation().getUsedVariables();
    if(vars == null || vars.length == 0) // operation contains no variables
      vars = new String[] {"n"};
    Map attributes = new HashMap(2);
    attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
    attributes.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
    result += master.getLabel();
    for(int i=0;i<vars.length;i++){
      result += "_{" + vars[i] + "}";
    }
    result += " = \u2211 ";
    return result;
  }


}
