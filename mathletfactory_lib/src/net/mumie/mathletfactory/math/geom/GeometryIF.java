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

package net.mumie.mathletfactory.math.geom;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 * This interface is implemented by all classes that are part of a geometry (i.e. on which
 * elements of the associated geometric group may operate).
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public interface GeometryIF extends NumberTypeDependentIF {
  
  /** Constant indicating no valid geometry type. */
  public static final int NO_GEOMETRY_TYPE = -1;
  
  /** Constant indicating euclidean 2D geometry type. */
  public static final int EUCLIDEAN2D_GEOMETRY =  0;
  /** Constant indicating hyperbolic 2D geometry type. */
  public static final int HYPERBOLIC2D_GEOMETRY = 10;
  /** Constant indicating affine geometry type. */
  public static final int AFFINE_GEOMETRY = 50;
  /** Constant indicating affine 2D geometry type. */
  public static final int AFFINE2D_GEOMETRY = 52;
  /** Constant indicating affine 3D geometry type. */
  public static final int AFFINE3D_GEOMETRY = 53;
  /** Constant indicating euclidean 3D geometry type. */
  public static final int EUCLIDEAN3D_GEOMETRY =  100;
  /** Constant indicating projective nD geometry type. */
  public static final int PROJECTIVEND_GEOMETRY = 1000;
  

  
  /**
   * Returns the geometry type of the current geometric object.
   * Those types are defined as static final variables in <code>GeometryIF</code>.
   */
  public int getGeomType();

  /** Lets the element of the associated geometric group operate on this object. */  
  public GeometryIF groupAction(GroupElementIF aGroup);
  
}

