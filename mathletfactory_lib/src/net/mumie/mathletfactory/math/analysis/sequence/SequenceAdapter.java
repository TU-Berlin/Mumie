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

package net.mumie.mathletfactory.math.analysis.sequence;

import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.number.MNumber;

/**
 * A poor implementation of SequenceIF that implements TodoExcetion for methods
 * don't needed at the moment.
 * 
 * The method getWorldBoundingBox is added to make autosize in canvas available.
 * 
 *  @author Amsel
 *  @mm.docstatus finished
 */
public abstract class SequenceAdapter implements SequenceIF {
  
  private Class m_numberClass;
  
  public SequenceAdapter(Class numberClass){
    m_numberClass = numberClass;
  }
  
  public MNumber getLimesInferior() {
    throw new TodoException();
  }
  
    public MNumber getLimesSuperior() {
    throw new TodoException();
  }
  
  public MNumber[] getAccumulationPoints() {
    throw new TodoException();
  }
  
  public SequenceIF getSubSebquence(SequenceIF indexSequence) {
    throw new TodoException();
  }
  
  public Rectangle2D getWorldBoundingBox() {
    return null;
  }
  
  public Class getNumberClass(){
    return m_numberClass;
  }
  
}
