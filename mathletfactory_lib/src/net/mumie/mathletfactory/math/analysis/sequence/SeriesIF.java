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

/**
 * This interface defines the must have for a series representation. At the
 * present time it should suffice to implement {@link #getSum}. 
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public interface SeriesIF {
  /**
   * Returns the sum value for a natural number. The method should not 
   * create any side effects. <strong>Attention</strong> repeated call to 
   * getEntry should result in two independent instances of MNumber.
   * @param index a natural number
   * @return a new instance of number representing sum value for index 
   */
  public MNumber getSum(MNatural index);

}
