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

package net.mumie.mathletfactory.mmobject.analysis.sequence;

import net.mumie.mathletfactory.math.analysis.sequence.SequenceAdapter;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class offers a body for special sequences that are constructed using a subclass 
 * {@link net.mumie.mathletfactory.math.analysis.sequence.SequenceAdapter}.
 * 
 * @author amsel
 * @mm.docstatus finished
 */
public class MMSequence extends MMDefaultCanvasObject implements MMSequenceIF{
  private final SequenceAdapter m_sequence;
      
  public MMSequence(SequenceAdapter sequence) {
    m_sequence = sequence;
  }
  
  public MNumber getSequenceValue(MNatural index){
    return m_sequence.getEntry(index);
  }
  
  public int[] getBounds(double fMin, double fMax, double epsilon, int[] bounds) {
    return m_sequence.bounds(fMin, fMax, epsilon, bounds);
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.SEQUENCE_TRANSFORM_1D;
  }

  public int getDefaultTransformType(){
    return getDefaultTransformTypeInCanvas();
  }

  public Class getNumberClass() {
     return m_sequence.getNumberClass();
  }
}
