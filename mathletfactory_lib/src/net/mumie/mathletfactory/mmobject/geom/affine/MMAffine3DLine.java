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

package net.mumie.mathletfactory.mmobject.geom.affine;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 *  This class represents lines in the affine 3d space.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MMAffine3DLine extends MMAffine3DSubspace implements MMCanvasObjectIF {

  /**
   * Constructs a line going through <code>initialPoint</code> and <code>endPoint</code>.
   */
  public MMAffine3DLine(MMAffine3DPoint initialPoint, MMAffine3DPoint endPoint) {
    super(initialPoint.getNumberClass(),new Affine3DPoint[]{initialPoint,endPoint});
    setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.DEFAULT));
    setLabel("L");
  }
  
  /**
   * Constructs a line going through <code>initialCoord</code> and <code>endCoord</code>.
   */
  public MMAffine3DLine(NumberTuple initialCoord, NumberTuple endCoord) {
    super(initialCoord.getNumberClass(),new Affine3DPoint[]{new Affine3DPoint(initialCoord), new Affine3DPoint(endCoord)});
    setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.DEFAULT));
    setLabel("l");
  }

  /**
   * Constructs a line going through <code>(x1, y1, z1)</code> and <code>(x2, y2, z2)</code>.
   */
  public MMAffine3DLine(Class numberClass, double x1, double y1, double z1, double x2, double y2, double z2) {
    super(numberClass ,new Affine3DPoint[]{new Affine3DPoint(numberClass, x1,y1,z1), 
                                                            new Affine3DPoint(numberClass, x2, y2, z2)});
    setDisplayProperties(new LineDisplayProperties(LineDisplayProperties.DEFAULT));
    setLabel("l");
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

