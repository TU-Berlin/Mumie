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

import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;

/**
 * This interface defines the must have for a sequence representation. At the
 * present time it should suffice to implement {@link #getEntry} and
 * {@link #bounds} to use display features. The other methods are declared for
 * future use.
 *
 * A sequence is defined as a map from |N to some other number space.
 *
 *  @author Amsel
 *  @mm.docstatus finished
 */
public interface SequenceIF extends NumberTypeDependentIF {

  /**
   * returns the sequence value for a natural number. The method should not
   * create any side effects. <strong>Attention</strong> repeated call to
   * getEntry should result in two independent instances of MNumber.
   * @param index a natural number
   * @return a new instance of number representing sequence value for index
   */
  public MNumber getEntry(MNatural index);

  /**
   * bounds should return the reasonable values to calculate for this sequence.
   * The return parameter is broken down to <code>int</code> instead of
   * <code>MNatural</code>. The method gets an interval of range (fMin, fMax)
   * and an epsilon values that represents the smallest displayable gap between
   * two point on screen in world coordinates.
   * the bounds parameter may be null. bounds shound then create an appropriate
   * array. otherwise it should use the given array for return value.
   * @param fMin lower bound of displayable range
   * @param fMax upper bound of displayable range
   * @param epsilon point distance in world coordinates
   * @param bounds preallocated result array, may be null, must have length of
   * at least 2.
   * @return returns the lower and upper bounds of resonable domain for sequence
   * display.
   */
  public int[] bounds(double fMin, double fMax, double epsilon, int[] bounds);

  /**
   * @return limes inferior of sequence or null of sequence does'nt have one.
   */
  public MNumber getLimesInferior();

  /**
   * @return limes superior of sequence or null if sequence does'nt have one.
   */
  public MNumber getLimesSuperior();

  /**
   * @return alle accumulation points of sequence or null of sequence
   * does'nt have one.
   */
  public MNumber[] getAccumulationPoints();

  /**
   * returns a subsequence of sequence.
   * @param indexSequence should be a strong monoton sequence that maps
   * MNatural to MNatural. the range of indexSequence is the domain for the
   * subsequence.
   * @return x_n may be the sequence x_i the indexSeqence. The returned
   * sequence x_r is:<br>
   * x_r: |N -> |K, x |-> x_n(x_i(x))
   */
  public SequenceIF getSubSebquence(SequenceIF indexSequence);
}
