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

package net.mumie.mathletfactory.mmobject.algebra.linalg;

import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.LinearMapDefByVectorsUpdater;
import net.mumie.mathletfactory.display.noc.matrix.MMNumberMatrixPanel;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents an endomorphismus in R<sup>2</sup>, i.e. a 
 * {@link net.mumie.mathletfactory.math.algebra.linalg.LinearMap} from R<sup>2</sup>
 * into itsself.
 * 
 *  @author vossbeck
 *  @mm.docstatus finished
 */
public class MMDefaultR2Endomorphism extends MMDefaultRNEndomorphism {

  /**
   * constructs an <code>MMDefaultR2Endomorphism</code> that is a
   * {@link net.mumie.mathletfactory.algebra.LinearMap} from <code>aR2Space</code>
   * into itsself. It is initialized as identity. Use method
   * {@link #setDefaultMatrixRepresentation} to change this <code>
   * MMDefaultR2Endomorphism</code>.
   */
  public MMDefaultR2Endomorphism(MMDefaultR2 aR2Space) {
    super(aR2Space);
  }

  /**
   * constructs an <code>MMDefaultR2Endomorphism</code> that is a
   * {@link net.mumie.mathletfactory.algebra.LinearMap} from an instance of
   * {@link net.mumie.mathletfactory.mmobject.MMDefaultR2} to itsself and that
   * maps domain[i] to range[i] (i=1,2)
   */
  public MMDefaultR2Endomorphism(
    MMDefaultR2Vector[] domain,
    MMDefaultR2Vector[] range) {
    super(domain, range);
    //TODO: set appropiate display properties for linear map:
  }

  /**
   * Class constructor which creates an <code>MMDefaultR2Endomorphism</code> that is a
   * {@link net.mumie.mathletfactory.algebra.LinearMap} from an instance of
   * {@link net.mumie.mathletfactory.mmobject.MMDefaultR2} to itsself and that
   * maps domain[i] to range[i] (i=1,2). If <code>isRelation</code> is 
   * <code>true</code> the <code>MMDefaultR2Endomorphism</code> will be updated
   * depending on a change of domain and range. Furthermore it is possible to add a 
   * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}. 
   */
  public MMDefaultR2Endomorphism(
    MMDefaultR2Vector[] domain,
    MMDefaultR2Vector[] range,
    boolean isRelation, SpecialCaseListener listener) {
    super(domain, range);
    if(isRelation)
      new LinearMapDefByVectorsUpdater(this, domain, range, listener);
    //TODO: set appropiate display properties for linear map:
  }
  
  /**
   * Class constructor which creates an <code>MMDefaultR2Endomorphism</code> that is a
   * {@link net.mumie.mathletfactory.algebra.LinearMap} from an instance of
   * {@link net.mumie.mathletfactory.mmobject.MMDefaultR2} to itsself and that
   * maps domain[i] to range[i] (i=1,2). The symbolic representation of the
   * <code>MMDefaultR2Endomorphism</code> as a matrix is editable.
   */
   public MMDefaultR2Endomorphism(
    MMDefaultR2Vector[] domain,
    MMDefaultR2Vector[] range,
    int transformType) {
    this(domain, range);
		MMNumberMatrixPanel display = (MMNumberMatrixPanel) getAsContainerContent();
    display.setEditable(true);
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.LINEAR_MAP_TO_MATRIX_TRANSFORM;
  }

}
