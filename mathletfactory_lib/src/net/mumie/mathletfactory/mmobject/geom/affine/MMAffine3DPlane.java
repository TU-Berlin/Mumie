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

import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 *  This class represents planes in the affine 3d space.
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMAffine3DPlane extends MMAffine3DSubspace implements MMCanvasObjectIF {
    
  /**
   * Constructs a plane that contains the three given points. Since the super 
   * constructor of {@link MMAffine3DSubspace} is invoked it is not checked, whether 
   * the points really span a plane! 
   */
  public MMAffine3DPlane(MMAffine3DPoint firstPoint, MMAffine3DPoint secondPoint, MMAffine3DPoint thirdPoint) {
    super(firstPoint.getNumberClass(), new Affine3DPoint[]{firstPoint,secondPoint, thirdPoint});
    setDisplayProperties(SurfaceDisplayProperties.DEFAULT);
    setLabel("e");
  }
  
  
  /**
   * Constructs a plane that contains the three given points. Since the super 
   * constructor of {@link MMAffine3DSubspace} is invoked it is not checked, whether 
   * the points really span a plane! 
   */
  public MMAffine3DPlane(NumberTuple firstCoord, NumberTuple secondCoord, NumberTuple thirdCoord) {
    super(firstCoord.getNumberClass(),new Affine3DPoint[]{new Affine3DPoint(firstCoord), 
      new Affine3DPoint(secondCoord), new Affine3DPoint(thirdCoord)});
    setDisplayProperties(new SurfaceDisplayProperties(SurfaceDisplayProperties.DEFAULT));
    setLabel("P");
  }

  /**
   * Constructs a plane that contains the three given points. Since the super 
   * constructor of {@link MMAffine3DSubspace} is invoked it is not checked, whether 
   * the points really span a plane! 
   */
  public MMAffine3DPlane(Class numberClass, double x1, double y1, double z1, double x2, double y2, double z2,
                         double x3, double y3, double z3) {
    super(numberClass ,new Affine3DPoint[]{new Affine3DPoint(numberClass, x1,y1,z1), 
                                           new Affine3DPoint(numberClass, x2, y2, z2),
                                           new Affine3DPoint(numberClass, x3, y3, z3)});
    setDisplayProperties(new SurfaceDisplayProperties(SurfaceDisplayProperties.DEFAULT));
    setLabel("e");
  }

}


