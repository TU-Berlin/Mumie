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

package net.mumie.mathletfactory.action.updater;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR2Vector;

/**
 * This updater ensures the updating of a 2d vector that is the sum or difference of two other vectors.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class VectorDefByTwoVectorsUpdater extends MMUpdater {
  
  
  private NumberVector m_vector1;
  private NumberVector m_vector2;
  private NumberVector m_dependantVector;
  private VectorDependant m_dependantType;

  
  public VectorDefByTwoVectorsUpdater(MMDefaultR2Vector dependantVector,
                                      MMDefaultR2Vector firstVector,
                                      MMDefaultR2Vector secondVector,
                                      VectorDependant dependantType) {
    super(dependantVector,new MMObjectIF[]{firstVector,secondVector});
    m_dependantVector = dependantVector;
    m_vector1 = firstVector;
    m_vector2 = secondVector;
    m_dependantType = dependantType;
		ActionManager.performActionCycleFromObjects(m_parents);
  }

  
  public void userDefinedUpdate() {
    m_dependantType.setDependant(m_vector1,m_vector2,m_dependantVector);
  }

  
  // dependency types:

  public static final VectorDependant SUM = new VectorDependant() {
    public void setDependant(NumberVector v1,NumberVector v2, NumberVector dependantVector){
      NumberTuple v1coords = (NumberTuple)v1.getDefaultCoordinates().deepCopy();
      dependantVector.setDefaultCoordinates(v1coords.addTo(v2.getDefaultCoordinates()));
    }
  };
  
  public static final VectorDependant DIFFERENCE = new VectorDependant() {
    public void setDependant(NumberVector v1,NumberVector v2, NumberVector dependantVector){
      NumberTuple v1coords = (NumberTuple)v1.getDefaultCoordinates().deepCopy();
      dependantVector.setDefaultCoordinates(v1coords.subFrom(v2.getDefaultCoordinates()));
    }
  };
  
  interface VectorDependant {
    public void setDependant(NumberVector vector1,
                             NumberVector vector2,
                             NumberVector dependant);
  }
}

