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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.HashMap;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.node.NumberOp;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.number.numeric.Determinant;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;

/**
 * This class represents an arbitrary matrix with operations of the same number
 * class as entries.
 *
 * @author Paehler
 * @mm.docstatus finished
 * @deprecated use {@link net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix} with {@link net.mumie.mathletfactory.math.number.MOpNumber} instead
 */
public class OpMatrix extends Matrix implements NumberTypeDependentIF, MathMLSerializable {

  private boolean m_normalize = true;

  /**
   * This is not a java-like construction: To reduce "new" operations during
   * code execution in method applyTo(), we use a global temporary object,
   * that will be initialized only once. Observe: this construction is not
   * multi-thread secure!
   */
//  private OpTuple m_tempTupel;

  public static OpMatrix getIdentity(Class entryClass, int dimension) {
    OpMatrix m = new OpMatrix(entryClass, dimension, true);
    m.setToIdentity();
    return m;
  }

  /**
   * constructs a <code>OpMatrix</code> that is a deep copy of <code>
   * matrix</code>.
   */
  public OpMatrix(OpMatrix matrix) {
    super(
      Operation.class,
      matrix.getColumnCount(),
      new Object[matrix.getColumnCount() * matrix.getRowCount()]);
    for (int i = 1; i <= getRowCount(); i++) {
      for (int j = 1; j <= getColumnCount(); j++) {
        setEntry(i, j, matrix.getEntryAsOpRef(i, j));
      }
    }
  }

  /**
   * constructs a <code>OpMatrix</code> that is a copy of the given <code>NumberMatrix</code>.
   */
  public OpMatrix(NumberMatrix matrix) {
    super(
      Operation.class,
      matrix.getColumnCount(),
      Operation.getNewOpArray(matrix.getNumberClass(), matrix.getColumnCount() * matrix.getRowCount(), true));
    for (int i = 1; i <= getRowCount(); i++) {
      for (int j = 1; j <= getColumnCount(); j++) {
        setEntry(i, j, matrix.getEntryAsNumberRef(i, j));
      }
    }
  }

  /**
   * constructs a <code>OpMatrix</code> with <code>numOfCols</code> number
   * of columns and with the number entries of type <code>numberClass</code>. The
   * numbers are the elements in <code>entries</code> and the user has to care
   * for consistency of their number type. <code>numOfCols</code> will (of course)
   * equal the length of any row in the <code>OpMatrix</code> and by that the
   * number of rows will equal entries.length / <code>numOfCols</code>. Again the
   * user has to care for the fact that entries.length must be a multiple of
   * <code>numOfCols</code>. The object array itsself is treated &quot;rowwise&quot;
   */
  public OpMatrix(Class numberClass, int numOfCols, Object[] entries) {
    //TODO: check that the entries do not contain null references!
    super(Operation.class, numOfCols, entries);
  }

  /**
   * constructs a zero matrix with entries of number type
   * <code>numberClass</code> and with <code>numOfCols</code> columns and
   * <code>numOfRows</code> rows.
   */
  public OpMatrix(Class numberClass, int numOfCols, int numOfRows, boolean normalize) {
    super(
      Operation.class,
      numOfCols,
      Operation.getNewOpArray(numberClass, numOfCols * numOfRows, normalize));
  }

  /**
   * constructs a zero square matrix with entries of number type
   * <code>numberClass</code> and of desired dimension.
   */
  public OpMatrix(Class numberClass, int dimension, boolean normalize) {
    this(numberClass, dimension, dimension, normalize);
  }

  /**
   * constructs a number matrix of number type <code>numberClass</code> and with
   * the {@link OpTuple}-array treated as rows or columns for this <code>
   * OpMatrix</code> in dependence of <code>rowWise</code>.
   *
   */
  public OpMatrix(Class numberClass, boolean rowWise, OpTuple[] rowcol) {
    super(
      Operation.class,
      rowcol.length,
      MNumber.getNewNumberArray(
           numberClass,
           rowcol[0].getDimension() * rowcol.length));
    // reshaping is not nice, but the super()-call must be the first statement
    // in the constructor and there we treat rowcol as ColumnTupel:
    if (rowWise) {
      reshape(rowcol.length, rowcol[0].getDimension());
      setRowVectors(rowcol);
    }
    else
      setColumnVectors(rowcol);
  }

  /** For convenience: Produces a mxn double matrix with the given (row-wise) entries as values. */
  public OpMatrix(int numOfCols, int numOfRows, double[] values){
    this(MDouble.class, numOfCols, numOfRows, true);
    for (int i = 1; i <= getRowCount(); i++) {
      for (int j = 1; j <= getColumnCount(); j++) {
        setEntry(i, j, values[(i-1)*numOfCols + j-1]);
      }
    }
  }

  protected void init(int numOfCols, Object[] entries) {
    setFormater(new MMNumberFormat("0.00"));
    super.init(numOfCols, entries);
  }

  public Class getNumberClass() {
    return ((Operation)getEntry(1,1)).getNumberClass();
  }

  public void setEntry(int row, int col, Operation v) {
    setEntryRef(row, col, v.deepCopy());
  }

  public void setEntry(int row, int col, String expression){
    setEntryRef(row, col, new Operation(getNumberClass(), expression, m_normalize));
  }

  public void setEntry(int row, int col, MNumber aValue) {
    setEntryRef(row, col, aValue);
  }

  public void setEntry(int row, int col, double aValue) {
    setEntryRef(row, col, aValue);
  }

  public Operation getEntry(int row, int col) {
    return ((Operation) getEntryRef(row, col)).deepCopy();
  }

  public void setEntryRef(int row, int col, MNumber aValue) {
    Operation entry = getEntryAsOpRef(row, col);
    if(entry == null)
      entry = new Operation(aValue.getClass(),"0",m_normalize);
    entry.setOpRoot(new NumberOp(aValue));
  }

  public void setEntryRef(int row, int col, double aValue) {
    getEntryAsOpRef(row, col).setOpRoot(new NumberOp(getNumberClass(), aValue));
  }

  /**
   * Returns the reference to the {@link net.mumie.mathletfactory.math.number.MNumber}
   * at (<code>row</code>, <code>column</code>) in this matrix.
   *
   * @param    row                 an int
   * @param    column              an int
   *
   * @return   a MNumber
   *
   * @version  10/14/2002
   */
  public Operation getEntryAsOpRef(int row, int column) {
    return (Operation) getEntryRef(row, column);
  }

  /**
   * Returns all entries of this <code>OpMatrix</code> rowwise as a one
   * dimensional array of {@link net.mumie.mathletfactory.math.algebra.op.Operation}s.
   *
   * @version  10/14/2002
   */
  public Operation[] getEntriesAsOpRef() {
    Operation[] tmp = new Operation[m_entries.length];
    for (int i = 0; i < m_entries.length; i++)
      tmp[i] = (Operation) m_entries[i];
    return tmp;
  }

  public Object[] getEntriesDataAsObjectRefIHopeYouKnowWhatYouDo() {
    return m_entries;
  }

  public OpMatrix setRowVector(int row, OpTuple row_vector) {
    int calcLength = Math.min(row_vector.getDimension(), getColumnCount());
    for (int i = 1; i <= calcLength; i++) {
      getEntryAsOpRef(row, i).set(row_vector.getEntryRef(i));
    }
    return this;
  }

  /**
   * Makes the entries of the <code>column</code>th column of this <code>
   * OpMatrix</code> change their values to those given in <code>
   * col_vector</code>. This method has no sideeffects, i.e. this <code>
   * OpMatrix</code>
   *
   * @version  10/14/2002
   */
  public OpMatrix setColumnVector(int column, OpTuple col_vector) {
    int calcLength = Math.min(col_vector.getDimension(), getRowCount());
    for (int i = 1; i <= calcLength; i++) {
      getEntryAsOpRef(i, column).set(col_vector.getEntryRef(i));
    }
    return this;
  }

  /**
   * This method treats the traversed <code>OpTuple</code>s as row vectors
   * for this <code>OpMatrix</code>. No check about the entry classes or
   * any dimension will be done but the method works without side effects.
   * Especially this method works without creating new objects.
   *
   * @version  10/14/2002
   */
  public OpMatrix setRowVectors(OpTuple[] rowVectors) {
    for (int i = 1; i <= getRowCount(); i++)
      setRowVector(i, rowVectors[i - 1]);
    return this;
  }

  /**
   * This method treats the traversed <code>OpTuple</code>s as column vectors
   * for this <code>OpMatrix</code>. No check about the entry classes or
   * any dimension will be done but the method works without side effects.
   * Especially this method works without creating new objects.
   *
   * @version  10/14/2002
   */
  public OpMatrix setColumnVectors(OpTuple[] colVectors) {
    for (int j = 1; j <= getColumnCount(); j++)
      setColumnVector(j, colVectors[j - 1]);
    return this;
  }

  public OpTuple getColumnVector(int columnNumber) {
    OpTuple columnVector = new OpTuple(getNumberClass(), getRowCount(), m_normalize);
    for (int i = 1; i <= getRowCount(); i++) {
      columnVector.getEntryRef(i).set((Operation)getEntryRef(i, columnNumber));
    }
    return columnVector;
  }

  public OpTuple getRowVector(int rowNumber) {
    OpTuple rowVector = new OpTuple(getNumberClass(), getColumnCount(), m_normalize);
    for (int i = 1; i <= getColumnCount(); i++) {
      rowVector.getEntryRef(i).set((Operation) getEntryRef(rowNumber, i));
    }
    return rowVector;
  }

  public OpTuple[] getRowVectors() {
    OpTuple[] rows = new OpTuple[getRowCount()];
    for (int i = 0; i < rows.length; i++)
      rows[i] = getRowVector(i + 1);
    return rows;
  }

  public OpTuple[] getColumnVectors() {
    OpTuple[] columns = new OpTuple[getColumnCount()];
    for (int i = 0; i < columns.length; i++)
      columns[i] = getColumnVector(i + 1);
    return columns;
  }

  public void exportRow(int row, OpTuple rowTupel) {
    for (int i = 1; i <= getColumnCount(); i++)
      rowTupel.getEntryRef(i).set(getEntryAsOpRef(row, i));
  }

  /**
   * Fills up the given <code>columnTupel</code> with the corresponding entries
   * in this matrix, i.e. <code>rowTupel[i-1]</code> will get the entries
   * of the <it>i</it>th row in this matrix.  No &quot;new&quot; will be made.
   *
   * @version  10/16/2002
   */
  public void exportToRows(OpTuple[] rowTupel) {
    for (int i = 1; i <= getRowCount(); i++)
      exportRow(i, rowTupel[i - 1]);
  }

  public void exportColumn(int col, OpTuple colTupel) {
    for (int i = 1; i <= getRowCount(); i++)
      colTupel.getEntryRef(i).set(getEntryAsOpRef(i, col));
  }

  /**
   * Fills up the given <code>columnTupel</code> with the corresponding entries
   * in this matrix, i.e. <code>columnTupel[i-1]</code> will get the entries
   * of the <it>i</it>th column in this matrix. No &quot;new&quot; will be made.
   *
   * @version  10/16/2002
   */
  public void exportToColumns(OpTuple[] columnTupel) {
    for (int i = 1; i <= getColumnCount(); i++)
      exportColumn(i, columnTupel[i - 1]);
  }

  /**
   * Sets this matrix to the identity if this matrix is square and throws
   * an {@link IllegalUsageException} if it is not.
   *
   * @version  10/14/2002
   */
  public OpMatrix setToIdentity() {
    if (isSquare()) {
      for (int i = 1; i <= getDimension(); i++) {
        for (int j = 1; j <= getDimension(); j++) {
          setEntryRef(
            i,
            j,
            i == j
              ? NumberFactory.newInstance(getNumberClass(), 1)
              : NumberFactory.newInstance(getNumberClass()));
        }
      }
    }
    else
      throw new IllegalUsageException("can only be applied to square matrices");
    return this;
  }

  /**
   * Tests whether this matrix is the identity or not.
   *
   * @version  10/14/2002
   */
  public boolean isIdentity() {
    if (isSquare()) {
      for (int i = 1; i <= getDimension(); i++) {
        for (int j = 1; j <= getDimension(); j++) {
          if (!(i == j
                  ? getEntryAsOpRef(i, j).getDouble() == 1
                  : getEntryAsOpRef(i, j).isZero()))
            return false;
        }
      }
      return true;
    }
    else
      return false;
  }

  public OpMatrix adjoint() {
    throw new TodoException();
  }

  /**
   * Method applyTo applies a matrix to a column vector by standard matrix
   * multiplication. The matrix may have <emph>any</emph>
   * size. If the vector is smaller than the matrix width than only the upper
   * left vectorSize x vectorSize matrix is used. If the matrix height is smaller
   * than the length of the vector only the first matrix heigth elements from
   * the vector are changed. The other vector elements are left unchanged. If the
   * vector is larger than the matrix width only the first matrix width elements
   * of the vector are changed.
   * Observe: This is a method with side effect, i.e. the argument <code>aVector
   * </code> will be changed.
   *
   * @version  9/12/2002
   */
  public OpTuple applyTo(OpTuple aColumnTupel) {
    OpTuple m_tempTupel = new OpTuple(aColumnTupel);

    if (aColumnTupel.isColumnVector()) {
      int calcLength = Math.min(getColumnCount(), m_tempTupel.getDimension());
      for (int i = 1; i <= calcLength; i++) {
        //getRowVector(i) will be modified by the dot product, but this doesnt
        // matter here.
        //aColumnTupel.setEntryRef(i, getRowVector(i).dotProductFast(m_tempTupel));
        getRowVector(i).dotProductFast(
          m_tempTupel,
          aColumnTupel.getEntryRef(i));
      }
      return aColumnTupel;
    }
    else {
      throw new IllegalArgumentException("column vector excpected");
    }
  }

  /**
   *  Up to 4 times faster than the method above.
   */
  public void applyTo(OpTuple aColumnTupel, OpTuple result) {
    int n = aColumnTupel.getRowCount();
    Operation tmp = new Operation(getNumberClass(),"0",true);
    for (int i = 1; i <= n; i++) {
      Operation resultEntry = new Operation(getNumberClass(),"0",true);
      for (int j = 1; j <= n; j++) {
        tmp.setOpRoot(new NumberOp(getNumberClass(), 0));
        tmp.addTo(aColumnTupel.getEntryRef(j));
        tmp.mult((Operation) getEntryRef(i, j));
        resultEntry.addTo(tmp);
      }
      result.setEntryRef(i, resultEntry);
    }
    // do the same side effect as applyTo(aColumnTupel);
    aColumnTupel.m_entries = result.m_entries;
  }

  public OpMatrix multWithNumber(MNumber aNumber) {
    for (int r = 1; r <= getRowCount(); r++) {
      for (int c = 1; c <= getColumnCount(); c++) {
        getEntryAsOpRef(r, c).mult(new Operation(aNumber));
      }
    }
    return this;
  }

  /**
   * This methods performs the matrix multiplication between this <code>
   * OpMatrix</code> and <code>aMatrix</code> and changes this instance, i.e.
   * resizes this matrix so that it fits to the product matrix' size.
   */
  public OpMatrix mult(OpMatrix aMatrix) {
    int rowOut = getRowCount();
    int colOut = aMatrix.getColumnCount();

    if (getColumnCount() == aMatrix.getRowCount()) {
      OpMatrix tmp = (OpMatrix) deepCopy();
      if (getColumnCount() != colOut) {
        //this.reshape(rowOut, colOut);
        this.resize(rowOut, colOut);
      }
      for (int r = 1; r <= getRowCount(); r++) {
        for (int c = 1; c <= getColumnCount(); c++) {
          setEntryRef(
            r,
            c,
            tmp.getRowVector(r).dotProduct(aMatrix.getColumnVector(c)));
        }
      }
    }

    else
      throw new IllegalArgumentException("shape of matrix not compatible for matrix multiplication");
    return this;
  }

  /** Returns a new matrix with the values of m1.mult(m2). */
  public static OpMatrix mult(OpMatrix m1, OpMatrix m2){
    OpMatrix retVal = new OpMatrix(m1.getNumberClass(), m1.getColumnCount(), m1.getRowCount(), m1.m_normalize);
    retVal.copyFrom(m1);
    return retVal.mult(m2);
  }

  public boolean isZero() {
    for (int r = 1; r <= getRowCount(); r++) {
      for (int c = 1; c <= getColumnCount(); c++) {
        if (!getEntryAsOpRef(r, c).isZero())
          return false;
      }
    }
    return true;
  }

  public MNumber getMaxAbsEntryRef() {
    MNumber max = (MNumber) getEntryRef(1, 1);
    for (int r = 1; r <= getRowCount(); r++)
      for (int c = 1; c <= getColumnCount(); c++)

          if (max.absed().getDouble()
                    <((MNumber) getEntryRef(r, c)).absed().getDouble())
         max = (MNumber) getEntryRef(r, c);

    return max;
  }

  /**
   * checks whether the non zero entries of this matrix only appear on the
   * (virtual) diagonal. Because this matrix needs not to be a square,
   * for a matrix like [[1,0,0],[0,1,0]] would also returned true.
   *
   * @version  10/16/2002
   */
  public boolean isDiagonalMatrix() {
    for (int i = 1; i <= getRowCount(); i++)
      for (int j = 1; j <= getColumnCount(); j++) {
        if (i == j || getEntryAsOpRef(i, j).isZero())
          continue;
        else
          return false;
      }
    return true;
  }

  /**
   * Sets all entries to zero (respecting the number entry class of the
   * matrix).
   *
   * @version  10/14/2002
   */
  public void setToZero() {
    for (int i = 1; i <= getRowCount(); i++) {
      for (int j = 1; j <= getColumnCount(); j++) {
        Operation entry = getEntryAsOpRef(i, j);
        if (entry == null) {
          setEntry(i, j, 0);
        } else {
          entry.setDouble(0);
        }
      }
    }
  }

  public OpMatrix addTo(OpMatrix aMatrix) {
    int rowMin = Math.min(getRowCount(), aMatrix.getRowCount());
    int colMin = Math.min(getColumnCount(), aMatrix.getColumnCount());
    for (int r = 1; r <= rowMin; r++) {
      for (int c = 1; c <= colMin; c++) {
        getEntryAsOpRef(r, c).addTo(aMatrix.getEntryAsOpRef(r, c));
      }
    }
    return this;
  }

  public OpMatrix subFrom(OpMatrix aMatrix) {
    int rowMin = Math.min(getRowCount(), aMatrix.getRowCount());
    int colMin = Math.min(getColumnCount(), aMatrix.getColumnCount());
    for (int r = 1; r <= rowMin; r++) {
      for (int c = 1; c <= colMin; c++) {
        getEntryAsOpRef(r, c).subFrom(aMatrix.getEntryAsOpRef(r, c));
      }
    }
    return this;
  }

  /**
   * Every entry m(i,j) of this instance of <code>OpMatrix</code> will be
   * replaced by -1*m(i,j) and this instance will be returned.
   *
   * @version  10/16/2002
   */
  public OpMatrix negate() {
    for (int r = 1; r <= getRowCount(); r++) {
      for (int c = 1; c <= getColumnCount(); c++) {
        getEntryAsOpRef(r, c).negate();
      }
    }
    return this;
  }

  public OpMatrix shallowCopy() {
    try {
      return (OpMatrix) this.clone();
    }
    catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null; //should never happen
  }

  /**
   * This method returns a new instance of <code>OpMatrix</code> which has
   * the same dimension as this matrix and identical entries, i.e.
   * {@link net.mumie.mathletfactory.math.number.MNumber} instances that have the
   * same value as in this matrix.
   *
   * @version  10/16/2002
   */
  public OpMatrix deepCopy() {
    try {
      Constructor constructor =
        getClass().getConstructor(new Class[] { getClass()});
      return (OpMatrix) constructor.newInstance(new Object[] { this });

    }
    catch (NoSuchMethodException e) {
      e.printStackTrace();
      throw new IllegalArgumentException(
        getClass() + " needs a constructor that takes a " + getClass());
    }
    catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    catch (InstantiationException e) {
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * All {@link net.mumie.mathletfactory.math.number.MNumber} entries in this matrix
   * will be set to the value of the corresponding entry in <code>aMatrix</code>.
   *
   * This method works without creating new <code>MNumber</code>s, but observe
   * that <code>aMatrix</code> may not hold any null entries - this would leed
   * to a {@link java.lang.NullPointerException}.
   *
   * @version  10/16/2002
   */
  public OpMatrix copyFrom(OpMatrix aMatrix) {
    for (int i = 0; i < m_entries.length; i++) {
      ((Operation) m_entries[i]).set((Operation) aMatrix.m_entries[i]);
    }
    return this;
  }

  private class MMNumberFormat extends Format {
    private DecimalFormat format;
    public MMNumberFormat() {
      format = new DecimalFormat();
    }
    public MMNumberFormat(String pattern) {
      format = new DecimalFormat(pattern);
    }
    public MMNumberFormat(String pattern, DecimalFormatSymbols sym) {
      format = new DecimalFormat(pattern, sym);
    }

    public StringBuffer format(
      Object obj,
      StringBuffer toAppendTo,
      FieldPosition pos) {
      return new StringBuffer(obj.toString());
    }

    public Object parseObject(String source, ParsePosition status) {
      Object result = format.parseObject(source, status);
      return new MDouble(((Number) result).doubleValue());
    }
  }


  // determinant has to be implemented on this level already
  // since otherwise A^t A which is a square matrix cannot be worked with

  /**
   * Computes the determinant of this matrix if it is square and throws an
   * {@link IllegalUsageException} otherwise.
   *
   * @version  10/16/2002
   */
  public Operation determinant() {
	/*schneller workaround, wegen seltsamen Verhalten der Methode -> scheinbar in Endlosschleife bei folgender Matrix:
 
 /                   \                                                          
| -x+1    0    0    0 |                                                         
|                     |                                                         
|    0 -x+1    0    4 |                                                         
|                     |                                                         
|    4    0 -x+2    4 |                                                         
|                     |                                                         
|    0    1    0 -x-1 |                                                         
 \                   /
 Ursache bislang unbekannt, vermutlich Normalisierungsproblem!?!
*/
    /*
     * Fehler nicht mehr reproduzierbar --> workaround wieder herausgenommen
     */
    if (isSquare())
      return Determinant.det(getEntriesAsOpRef());
//    	return new Operation(getNumberClass(),Determinant.det(getEntriesAsOpRef()).toString(),false);
    else
      throw new IllegalUsageException("can only be applied to square matrices");
  }

  /**
   *
   *
   *
   * @version  10/16/2002
   */
  public OpMatrix transposed() {
    return (OpMatrix) deepCopy().transpose();
  }

  /**
   * Checks whether this matrix is invertible or not.
   *
   * @version  10/16/2002
   */
  public boolean isInvertible() {
    if (isSquare())
      return (!determinant().isZero());
    else
      return false;
  }

  /**
   * checks whether this matrix is idempotent or not, i.e. whether
   * M*M = M.
   *
   * @version  10/16/2002
   */
  public boolean isProjector() {
    OpMatrix product = deepCopy();
    product.mult(this);
    return product.equals(this);
  };


  public double[] toDoubleArray() {
    double[] retVal = new double[m_entries.length];
    for (int i = 0; i < m_entries.length; i++)
      retVal[i] = ((MNumber) m_entries[i]).getDouble();
    return retVal;
  }

  public void resize(int numOfRows, int numOfCols) {
    setNewSize(numOfRows, numOfCols);
    for (int i = 0; i < m_entries.length; i++) {
      if (m_entries[i] == null) {
        m_entries[i] = NumberFactory.newInstance(getNumberClass());
      }
    }
    fireDimensionChanged();
  }

  public boolean equals(Object shouldBeAMatrix) {
  	if(shouldBeAMatrix instanceof OpMatrix) {
  		OpMatrix m = (OpMatrix)shouldBeAMatrix;
  		if(hasSameShape(m))
  		{
  			for(int  i=0; i < this.m_entries.length; i++) {
  				int row = getRowFromIndex(i);
  				int col = getColumnFromIndex(i);
  				if(!getEntry(row, col).equals(m.getEntry(row, col)))
  					return false;
  			}
  			return true;
  		}
  		else
  			return false;
  	}
  	else
  		return super.equals(shouldBeAMatrix);
  }

  /**
   * Returns a <code>NumberMatrix</code> that contains the evaluated result of this <code>OpMatrix</code>.
   */
  public NumberMatrix getEvaluated(HashMap variableValues){
    NumberMatrix retVal = new NumberMatrix(getNumberClass(), getColumnCount(), getRowCount());
    for(int i=1;i<=getRowCount();i++)
      for(int j=1;j<=getColumnCount();j++){
        retVal.setEntry(i, j, getEntryAsOpRef(i, j).evaluate(variableValues));
      }
    return retVal;
  }

  /**
   * Sets the given identifier as constant in all entries.
   */
  public void setConstant(String identifier) {
    for(int i=1;i<=getRowCount();i++)
      for(int j=1;j<=getColumnCount();j++){
        getEntryAsOpRef(i, j).setConstant(identifier);
      }
  }

  /**
   * Sets the given identifier as parameter in all entries.
   */
  public void setParameter(String identifier) {
    for(int i=1;i<=getRowCount();i++)
      for(int j=1;j<=getColumnCount();j++){
        getEntryAsOpRef(i, j).setParameter(identifier);
      }
  }

  /**
   * Sets the given identifier as variable in all entries.
   */
  public void setVariable(String identifier) {
    for(int i=1;i<=getRowCount();i++)
      for(int j=1;j<=getColumnCount();j++){
        getEntryAsOpRef(i, j).setVariable(identifier);
      }
  }

  /**
   *  Assigns the given value to all <code>VariableOp</code>s with the given
   *  identifier.
   */
  public void assignValue(String identifier, MNumber value) {
    for(int i=1;i<=getRowCount();i++)
      for(int j=1;j<=getColumnCount();j++){
        getEntryAsOpRef(i, j).assignValue(identifier, value);
      }
  }

//  public Node getMathMLNode() {
//    return getMathMLNode(XMLUtils.getDefaultDocument());
//  }

//  public Node getMathMLNode(Document doc) {
//		Element mtable = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mtable");
//		mtable.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
//		mtable.setAttribute("class", "bmatrix");
//
//		for(int r = 1; r <= getRowCount(); r++) {
//			mtable.appendChild(doc.createTextNode("\n  "));
//			Node mtr = mtable.appendChild(doc.createElement("mtr"));
//			for(int c = 1; c <= getColumnCount(); c++) {
//				mtr.appendChild(doc.createTextNode("\n    "));
//				Node mtd = mtr.appendChild(doc.createElement("mtd"));
//				mtd.appendChild(doc.createTextNode("\n      "));
//
//				mtd.appendChild(getEntryAsOpRef(r, c).getMathMLNode(doc));
////				} else if(getNumberClass().equals(MInteger.class)) {
////					mtd.appendChild(((MInteger)getEntry(r, c)).getMathMLNode());
////				} else {
////				  Node mn = mtd.appendChild(doc.createElement("mn"));
////				  mn.appendChild(doc.createTextNode(getEntry(r, c).toString()));
////				}
//				mtd.appendChild(doc.createTextNode("\n    "));
//			}
//			mtr.appendChild(doc.createTextNode("\n  "));
//		}
//		mtable.appendChild(doc.createTextNode("\n"));
//
//    return mtable;
//  }

//  public void setMathMLNode(Node node) {
//    if(!node.getNodeName().equalsIgnoreCase("mtable"))
//      throw new NumberFormatException("Node name must be \"<mtable>\"!");
//
//    OpMatrix m = new OpMatrix(getNumberClass(), 0, false);
//    NodeList rowsList = node.getChildNodes();
//    for (int k = 0; k < rowsList.getLength(); k++) {
//      Node row = rowsList.item(k);
//      if(row.getNodeName().equalsIgnoreCase("mtr")) {
//        NodeList colsList = row.getChildNodes();
//        OpTuple tuple = new OpTuple(getNumberClass(), 0, false);
//        for (int c = 0; c < colsList.getLength(); c++) {
//          Node column = colsList.item(c);
//          if(column.getNodeName().equalsIgnoreCase("mtd")) {
//            NodeList mnList = column.getChildNodes();
//            for (int entry = 0; entry < mnList.getLength(); entry++) {
//              Node numberEntry = mnList.item(entry);
//			  if(numberEntry.getNodeType() == Node.TEXT_NODE)
//			   continue;
//           Operation op = new Operation(getNumberClass(), "0", false);
//           op.setMathMLNode(numberEntry);
////			NumberFactory.setMathMLNode(numberEntry, op);
////            if(number == null)
////              continue;
//              tuple.resize(tuple.getRowCount()+1, 1);
////              if(number.getClass().isAssignableFrom(tuple.getNumberClass()))
////                tuple.setEntry(tuple.getRowCount(), number);
////              else
////                tuple.setEntry(tuple.getRowCount(), number.getDouble());
//            }
//          }
//        }
//        m.resize(m.getRowCount()+1, tuple.getRowCount());
//        m.setRowVector(m.getRowCount(), tuple);
//      }
//    }
//    resize(m.getRowCount(), m.getColumnCount());
//    copyFrom(m);
//  }
}
