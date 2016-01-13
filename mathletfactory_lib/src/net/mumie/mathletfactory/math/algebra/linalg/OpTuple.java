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

package net.mumie.mathletfactory.math.algebra.linalg;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.xml.XMLUtils;

/**
 * This class represents a one column matrix, i.e. a column vector.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class OpTuple extends OpMatrix {

  /**
   * creates a vector as "column vector", i.e. the underlying OpMatrix
   * has rowlength=1 after first creation.
   */
  public OpTuple(Class numberClass, Object[] entries) {
    super(numberClass, 1, entries);
  }

  public OpTuple(Class numberClass, int dimension, boolean normalize) {
    super(numberClass, 1, dimension, normalize);
  }

  public OpTuple(Class numberClass, double x, double y) {
    this(
      numberClass,
      new Object[] {
        new Operation(numberClass, ""+x, true),
        new Operation(numberClass, ""+y, true)});
  }

  public OpTuple(Class numberClass, double x, double y, double z) {
    this(
      numberClass,
      new Object[] {
        new Operation(numberClass, ""+x, true),
        new Operation(numberClass, ""+y, true),
        new Operation(numberClass, ""+z, true)});
  }

  public OpTuple[] asOneDArray() {
    OpTuple[] tmp = new OpTuple[1];
    tmp[0] = this;
    return tmp;
  }

  public OpTuple(OpTuple aVector) {
    super(aVector);
  }

  public OpTuple(OpMatrix aOneColumnMatrix) {
    super(aOneColumnMatrix);
    if (aOneColumnMatrix.getColumnCount() != 1)
      throw new IllegalArgumentException(
        "only matrices with one column can "
          + "be converted to a OpTuple!");
  }

  public void setEntryRef(int index, MNumber value) {
    if (isColumnVector()) {
      setEntryRef(1, index, value);
    }
    else {
      setEntryRef(index, 1, value);
    }
  }

  public void setEntryRef(int index, Operation value) {
    if (isColumnVector()) {
      setEntryRef(1, index, value);
    }
    else {
      setEntryRef(index, 1, value);
    }
  }

  public void setEntry(int index, Operation value) {
    if (isColumnVector()) {
      setEntry(1, index, value);
    }
    else {
      setEntry(index, 1, value);
    }
  }

  public void setEntry(int index, MNumber value) {
    if (isColumnVector()) {
      setEntry(1, index, value);
    }
    else {
      setEntry(index, 1, value);
    }
  }

  public void setEntry(int index, double aValue) {
    if (isColumnVector())
      setEntry(1, index, aValue);
    else
      setEntry(1, index, aValue);
  }

  public Operation getEntryRef(int index) {
    if (isColumnVector()) {
      return (Operation) getEntryRef(1, index);
    }
    else {
      return (Operation) getEntryRef(index, 1);
    }
  }

  public Operation getEntry(int index) {
    if (isColumnVector()) {
      return (Operation) getEntry(1, index);
    }
    else {
      return (Operation) getEntry(index, 1);
    }
  }

  public boolean isColumnVector() {
    return getColumnCount() == 1;
  }

  protected void checkDimension(OpTuple aVector) {
    if (getDimension() != aVector.getDimension())
      throw new IllegalArgumentException("vector must have same dimension");
  }

  public Operation scalarProduct(OpTuple aVector) {
    OpTuple tmp = (OpTuple) aVector.deepCopy();

    for (int i = 1; i <= getDimension(); i++) //fortran indexing!
      tmp.getEntryRef(i).conjugate();

    return dotProduct(tmp);
  }

  public Operation standardNorm() {
    return scalarProduct(this).root(2);
  }

  /*
   * Computes the sum of this(i)*aVector(i) for i between 1 and the maximum
   * length of both <code>OpTuple</code>s. Both must be based on the same
   * number type ({@link net.mumie.mathletfactory.number.MDouble},
   * {@link net.mumie.mathletfactory.number.MRational}, ...). Both Op
   * tuples remain unchanged.
   */
  public Operation dotProduct(OpTuple aVector) {
    if (getNumberClass().equals(aVector.getNumberClass())) {
      int calcLength = Math.min(getDimension(), aVector.getDimension());
      Operation sum = new Operation(getNumberClass(), "0", true); // fortran-indexing!!
      for (int i = 1; i <= calcLength; i++)
        sum.addTo(Operation.mult(getEntryRef(i), aVector.getEntryRef(i)));
      return sum;
    }
    else
      throw new IllegalArgumentException("both tupels must be based on the same number class");
  }

  /*
   * Computes the sum of this(i)*aVector(i) for i between 1 and the maximum
   * length of both <code>OpTuple</code>s. Both must be based on the same
   * number type ({@link net.mumie.mathletfactory.number.MDouble},
   * {@link net.mumie.mathletfactory.number.MRational}, ...). This <code>
   * OpTuple</code> will be changed during the computation and will hold
   * the product entries.
   */
  public Operation dotProductFast(OpTuple aVector) {
    Operation sum = new Operation(getNumberClass(), "0", true);
    dotProductFast(aVector, sum);
    return sum;
  }

  /*
   * Computes the sum of this(i)*aVector(i) for i between 1 and the maximum
   * length of both <code>OpTuple</code>s. Both must be based on the same
   * number type ({@link net.mumie.mathletfactory.number.MDouble},
   * {@link net.mumie.mathletfactory.number.MRational}, ...). This <code>
   * OpTuple</code> will be changed during the computation and will hold
   * the product entries.
   */
  public void dotProductFast(OpTuple aVector, Operation result) {
    if (getNumberClass().equals(aVector.getNumberClass())) {
      int calcLength = Math.min(getDimension(), aVector.getDimension());
      result.set(getEntryRef(1).mult(aVector.getEntryRef(1)));
      for (int i = 2; i <= calcLength; i++)
        result.addTo(getEntryRef(i).mult(aVector.getEntryRef(i)));
    }
    else
      throw new IllegalArgumentException("both tupels must be based on the same number class");
  }

  /**
   *  Calculates this * aVector^T
   */
  public OpMatrix outerProduct(OpTuple aVector) {
    int dim = getDimension();
    OpMatrix result = new OpMatrix(getNumberClass(), dim, dim, true);
    for (int i = 1; i <= dim; i++)
      for (int j = 1; j <= dim; j++)
        result.setEntryRef(i, j, getEntry(i).mult(aVector.getEntry(j)));
    return result;
  }

  /**
   * Method multiplyWithScalar
   */
  public OpTuple multiplyWithScalar(MNumber aNumber) {
    for (int i = 1; i <= this.getDimension(); i++) {
      getEntryRef(i).mult(new Operation(new NumberOp(aNumber)));
    }
    return this;
  }

  /**
   * Method multiplyWithScalar
   */
  public OpTuple multiplyWithScalar(double aNumber) {
    return multiplyWithScalar(NumberFactory.newInstance(getNumberClass(), aNumber));
  }

  public int getDimension() {
    return getRowCount() > getColumnCount() ? getRowCount() : getColumnCount();
  }

  /*public Object clone() {
   try {
   return super.clone();
   }catch (CloneNotSupportedException e) {
   e.printStackTrace();
   }
   return null;
   }*/

  public OpTuple addTo(OpTuple addend) {
    if (getDimension() >= addend.getDimension()) {
      for (int i = 1; i <= addend.getDimension(); ++i) {
        getEntryRef(i).addTo(addend.getEntryRef(i));
      }
    }
    else {
      Object[] tmp = new Object[getDimension()];
      for (int i = 1; i <= getDimension(); ++i) {
        tmp[i - 1] = getEntryRef(i);
        ((Operation) tmp[i - 1]).addTo(addend.getEntryRef(i));
      }
      init(1, tmp);
    }
    return this;
  }

  public OpTuple subFrom(OpTuple minuend) {
    if (getDimension() >= minuend.getDimension()) {
      for (int i = 1; i <= minuend.getDimension(); ++i) {
        getEntryRef(i).subFrom(minuend.getEntryRef(i));
      }
    }
    else {
      Object[] tmp = new Object[getDimension()];
      for (int i = 1; i <= getDimension(); ++i) {
        tmp[i - 1] = getEntryRef(i);
        ((Operation) tmp[i - 1]).subFrom(minuend.getEntryRef(i));
      }
      init(1, tmp);
    }
    return this;
  }

  public OpTuple getSubVectorView(Class vectorClass, int i, int j) {
    if (!OpTuple.class.isAssignableFrom(vectorClass))
      throw new IllegalArgumentException("vectorClass has to be a Vector or subclass of it");
    if (isColumnVector())
      return (OpTuple) getSubMatrixView(vectorClass, 1, i, 1, j);
    else
      return (OpTuple) getSubMatrixView(vectorClass, i, 1, j, 1);
  }

  /**
   * returns the index of the first entry, for which
   * {@link net.mumie.mathletfactory.math.number.MNumber#isZero isZero()} is true,
   *  it returns -1 if no entry is zero.
   */
  public int getIndexOfFirstNonZeroEntry() {
    for (int i = 1; i <= m_entries.length; i++)
      if (!((Operation) m_entries[i - 1]).isZero())
        return i;
    return -1;
  }

  public OpTuple copyFrom(OpTuple aTupel) {
    int calcLength = Math.min(getDimension(), aTupel.getDimension());
    for (int i = 0; i < calcLength; i++) {
      ((Operation) m_entries[i]).set((Operation) aTupel.m_entries[i]);
    }
    return this;
  }

  public String toString() {
    String tupleString = "";
    for (int i = 0; i < this.getDimension(); i++) {
      tupleString = tupleString + getEntry(i + 1) + ", ";
    }
    return "[" + tupleString.substring(0, tupleString.length() - 2) + "]";
  }

  public int hashCode(){
    int retVal = 0;
    for (int i=0;i<m_entries.length;i++)
      retVal += m_entries[i].hashCode() + m_entries[i].hashCode() >>> i;
    return retVal;
  }
  
  public Node getMathMLNode(Document doc) {
    Element mrow = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mrow");
    mrow.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
    mrow.setAttribute("class", "column-vector");

    for(int r = 1; r <= getRowCount(); r++) {
      mrow.appendChild(doc.createTextNode("\n  "));
      mrow.appendChild(getEntryAsOpRef(r, 1).getMathMLNode(doc));
    }
    mrow.appendChild(doc.createTextNode("\n"));
    return mrow;
  }


  /*
   public static void main(String[] args){
   NumberTuple aV = new NumberTuple(MDouble.class, 3);
   aV.setEntry(1, new MDouble(1));
   aV.setEntry(2, new MDouble(2));
   aV.setEntry(3, new MDouble(3));
   System.out.println(aV.outerProduct(aV));
   } */
}
