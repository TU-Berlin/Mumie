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

package net.mumie.mathletfactory.math.geom.affine;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;

/**
 * This class acts as a base class for all affine lines.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class AffineLine extends AffineSpace {
  
  public AffineLine(Class entryClass, int externalDimension) {
    super(entryClass,1,externalDimension);
  }  
  
  public AffineLine(AffineLine anAffineLine) {
    this(anAffineLine.getNumberClass(),anAffineLine.getEnvDimension());
    setProjectiveCoordinates(anAffineLine.getProjectiveCoordinates());
  }
  
  public NumberTuple getAffineCoordinatesOfInitialPoint() {
    return super.getAffineCoordinates()[0];
  }
  
  public NumberTuple getAffineCoordinatesOfEndPoint() {
    return super.getAffineCoordinates()[1];
  }
  
  public NumberTuple getCoordinatesOfDirectionVector(){
    return getAffineCoordinatesOfEndPoint().subFrom(getAffineCoordinatesOfInitialPoint());
  }
  
  public boolean isDegenerated() {
    return getAffineCoordinates().length == 1;  
  }
}

