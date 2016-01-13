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

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This interface is implemented by all classes that are an affine subspace and have a parametric
 * symbolic representation. Used by {@link net.mumie.mathletfactory.transformer.noc.AffineSubspaceTransformer}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public interface MMAffineSubspaceIF extends MMCanvasObjectIF{
  
  /** Returns the dimension of this affine subspace. */
  public int getDimension();
  
  /** Returns the dimension of the affine space that contains this subspace. */
  public int getEnvDimension();
  
  /**
   * Returns points of this subspace that can be used to produce a basis. To be more precise, they
   * are the projective basis of this affine subspace, in affine coordinates.
   */
  public NumberTuple[] getAffineCoordinates();
}