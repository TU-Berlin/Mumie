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

import org.w3c.dom.Node;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.poly.Polynomial;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.number.numeric.Determinant;
import net.mumie.mathletfactory.math.number.numeric.Invert;
import net.mumie.mathletfactory.math.util.MatrixUtils;
import net.mumie.mathletfactory.util.xml.ExerciseCompoundObjectIF;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;

/**
 * This class represents an arbitrary matrix with numbers of the same number
 * class as entries.
 *
 * @author gronau, amsel, holschneider, vossbeck
 * @mm.docstatus finished
 */
public class NumberMatrix extends Matrix implements NumberTypeDependentIF, ExerciseCompoundObjectIF {

  private boolean m_isRealType = false;

	/**	This Field describes the current edited status. */
//	private boolean m_isEdited = true;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;

	public static NumberMatrix getIdentity(Class entryClass, int dimension) {
    NumberMatrix m = new NumberMatrix(entryClass, dimension);
    m.setToIdentity();
    return m;
  }
	
  /**
   * constructs a <code>NumberMatrix</code> that is a deep copy of <code>
   * matrix</code>.
   */
  public NumberMatrix(NumberMatrix matrix) {
    super(
      matrix.getNumberClass(),
      matrix.getColumnCount(),
      new Object[matrix.getColumnCount() * matrix.getRowCount()]);
    for (int i = 1; i <= getRowCount(); i++) {
      for (int j = 1; j <= getColumnCount(); j++) {
        setEntry(i, j, matrix.getEntryAsNumberRef(i, j));
      }
    }
  }

  /**
   * constructs a <code>NumberMatrix</code> with <code>numOfCols</code> number
   * of columns and with the number entries of type <code>numberClass</code>. The
   * numbers are the elements in <code>entries</code> and the user has to care
   * for consistency of their number type. <code>numOfCols</code> will (of course)
   * equal the length of any row in the <code>NumberMatrix</code> and by that the
   * number of rows will equal entries.length / <code>numOfCols</code>. Again the
   * user has to care for the fact that entries.length must be a multiple of
   * <code>numOfCols</code>. The object array itsself is treated &quot;rowwise&quot;
   */
  public NumberMatrix(Class numberClass, int numOfCols, Object[] entries) {
    //TODO: check that the entries do not contain null references!
    super(numberClass, numOfCols, entries);
  }

  /**
   * constructs a zero matrix with entries of number type
   * <code>numberClass</code> and with <code>numOfCols</code> columns and
   * <code>numOfRows</code> rows.
   */
  public NumberMatrix(Class numberClass, int numOfCols, int numOfRows) {
    super(
      numberClass,
      numOfCols,
      MNumber.getNewNumberArray(numberClass, numOfCols * numOfRows));
  }

  /**
   * constructs a zero square matrix with entries of number type
   * <code>numberClass</code> and of desired dimension.
   */
  public NumberMatrix(Class numberClass, int dimension) {
    this(numberClass, dimension, dimension);
  }

  /**
   * Constructs a number matrix of number type <code>numberClass</code> and with
   * the {@link NumberTuple}-array treated as rows or columns for this <code>
   * NumberMatrix</code> in dependence of <code>rowWise</code>.
   */
  public NumberMatrix(Class numberClass, boolean rowWise, NumberTuple[] rowcol) {
    super(
      numberClass,
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

  /**
   * constructs an instance of <code>NumberMatrix</code> <b>M</b> that complies
   * to <b>M</b>&lowast;domain[i] = range[i] (where &lowast; means the standard
   * matrix multiplication and domain[i] is treated as columnvector. This method
   * expects that all elements in domain and  range are based on the same
   * number type and that the domain elements  are linearly independent.
   */
  public NumberMatrix(NumberTuple[] domain, NumberTuple[] range) {
    // todo check linear indepence of domain
    // check good dimensions of vectors
    this(
      domain[0].getNumberClass(),
      range[0].getDimension(),
      domain[0].getDimension());
    //new Object[domain[0].getDimension() * range[0].getDimension()]);
    //use setAsLinearMapping
    setAsLinearMapping(domain, range);
  }

  
	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param mathMLnode a MathML/XML node
   * @throws Exception when an error while instantiation occurrs
	 */
  public NumberMatrix(Node content) throws Exception {
    super(ExerciseObjectFactory.getNumberClass(content),
      0,
      new Object[0]);
    setMathMLNode(content);
  }
    
  /** For convenience: Produces a mxn double matrix with the given (row-wise) entries as values. */
  public NumberMatrix(int numOfCols, int numOfRows, double[] values){
    this(MDouble.class, numOfCols, numOfRows);
    for(int i=0;i<values.length;i++)
      getEntriesAsNumberRef()[i].setDouble(values[i]);
  }

  protected void init(int numOfCols, Object[] entries) {
   	setFormater(new MMNumberFormat("0.00"));
    super.init(numOfCols, entries);
  }

  protected void checkEntry(Object entry) {
    super.checkEntry(entry);
    if (isRealType() && (entry != null))
      if (!MRealNumber.class.isAssignableFrom(entry.getClass()))
        throw new IllegalArgumentException("A RealMatrix may contain only subclasses of MRealNumber");
  }

  /**
   * Setting to <code>true</code> will cause this matrix to accept only such
   * {@link net.mumie.mathletfactory.math.number.MNumber}s that inherit from
   * {@link net.mumie.mathletfactory.math.number.MRealNumber}.
   *
   * @version  10/16/2002
   */
  public void setRealType(boolean setToReal) {
    if (setToReal == m_isRealType)
      return;

    if (!setToReal) {
      m_isRealType = false;
      return;
    }

    if (MRealNumber.class.isAssignableFrom(getNumberClass())) {
      m_isRealType = true;
      return;
    }

    if (getNumberClass() == MNumber.class) {
      try {
        m_isRealType = true;
        check();
      }
      catch (RuntimeException e) {
        m_isRealType = false;
        throw e;
      }
    }
  }

  public boolean isRealType() {
    //the matrix has realtype entries iff entryType is MRealNumber or subType
    //or m_isRealType is set m_isRealType may be set by setRealType(true). This
    //leads to an error on subclasses on MNumber that aren't subclass of
    //MRealNumber
    return MRealNumber.class.isAssignableFrom(getNumberClass()) || m_isRealType;
  }

  protected void setEntryClass(Class entryClass) {
    if (m_isRealType) {
      if ((MRealNumber.class.isAssignableFrom(entryClass))
          || (entryClass == MNumber.class)) {
        super.setEntryClass(entryClass);
        return;
      }
      else
        throw new IllegalArgumentException("entryClass must be a MNumber or a (sub)class of MRealNumber (the isReal flag is set)");
    }
    else {
      if (MNumber.class.isAssignableFrom(entryClass)) {
        super.setEntryClass(entryClass);
        return;
      }
    }
    throw new IllegalArgumentException("entryClass must be a (sub)class of MNumber");
  }

  /**
   * Copies the given number value to the entry with the given indices.
   */
  public void setEntry(int row, int col, MNumber v) {
    setEntryRef(row, col, v.copy());
  }

  /**
   * Sets the given number value to the entry with the given indices.
   */
  public void setEntry(int row, int col, double aValue) {
    getEntryAsNumberRef(row, col).setDouble(aValue);
  }

  /**
   * Returns a copy of the entry for the given indices.
   */
  public MNumber getEntry(int row, int col) {
    return ((MNumber) getEntryRef(row, col)).copy();
  }

  /**
   * Returns the reference to the {@link MNumber}
   * at (<code>row</code>, <code>column</code>) in this matrix.
   *
   * @param    row                 an int
   * @param    column              an int
   *
   * @return   a MNumber
   *
   * @version  10/14/2002
   */
  public MNumber getEntryAsNumberRef(int row, int column) {
    return (MNumber) getEntryRef(row, column);
  }

  /**
   * Returns all entries of this <code>NumberMatrix</code> rowwise as a one
   * dimensional array of {@link net.mumie.mathletfactory.math.number.MNumber}s.
   *
   * @version  10/14/2002
   */
  public MNumber[] getEntriesAsNumberRef() {
    MNumber[] tmp = new MNumber[m_entries.length];
    for (int i = 0; i < m_entries.length; i++)
      tmp[i] = (MNumber) m_entries[i];
    return tmp;
  }

  /**
   * Makes the entries of the <code>row</code>th row of this <code>
   * NumberMatrix</code> change their values to those given in <code>
   * row_vector</code>.
   */
  public NumberMatrix setRowVector(int row, NumberTuple row_vector) {
    int calcLength = Math.min(row_vector.getDimension(), getColumnCount());
    for (int i = 1; i <= calcLength; i++) {
      getEntryAsNumberRef(row, i).set(row_vector.getEntryRef(i));
    }
    return this;
  }

  /**
   * Makes the entries of the <code>column</code>th column of this <code>
   * NumberMatrix</code> change their values to those given in <code>
   * col_vector</code>.
   */
  public NumberMatrix setColumnVector(int column, NumberTuple col_vector) {
    int calcLength = Math.min(col_vector.getDimension(), getRowCount());
    for (int i = 1; i <= calcLength; i++) {
      getEntryAsNumberRef(i, column).set(col_vector.getEntryRef(i));
    }
    return this;
  }

  /**
   * This method treats the traversed <code>NumberTuple</code>s as row vectors
   * for this <code>NumberMatrix</code>. No check about the entry classes or
   * any dimension will be done but the method works without side effects.
   * Especially this method works without creating new objects.
   *
   * @version  10/14/2002
   */
  public NumberMatrix setRowVectors(NumberTuple[] rowVectors) {
    for (int i = 1; i <= getRowCount(); i++)
      setRowVector(i, rowVectors[i - 1]);
    return this;
  }

  /**
   * This method treats the traversed <code>NumberTuple</code>s as column vectors
   * for this <code>NumberMatrix</code>. No check about the entry classes or
   * any dimension will be done but the method works without side effects.
   * Especially this method works without creating new objects.
   *
   * @version  10/14/2002
   */
  public NumberMatrix setColumnVectors(NumberTuple[] colVectors) {
    for (int j = 1; j <= getColumnCount(); j++)
      setColumnVector(j, colVectors[j - 1]);
    return this;
  }

  public NumberTuple getColumnVector(int columnNumber) {
    NumberTuple columnVector = new NumberTuple(getNumberClass(), getRowCount());
    for (int i = 1; i <= getRowCount(); i++) {
      columnVector.getEntryRef(i).set(
                                            (MNumber) getEntryRef(i,
                                                                   columnNumber));
    }
    return columnVector;
  }

  public NumberTuple getRowVector(int rowNumber) {
    NumberTuple rowVector = new NumberTuple(getNumberClass(), getColumnCount());
    for (int i = 1; i <= getColumnCount(); i++) {
      rowVector.getEntryRef(i).set((MNumber) getEntryRef(rowNumber, i));
    }
    return rowVector;
  }

  public NumberTuple[] getRowVectors() {
    NumberTuple[] rows = new NumberTuple[getRowCount()];
    for (int i = 0; i < rows.length; i++)
      rows[i] = getRowVector(i + 1);
    return rows;
  }

  public NumberTuple[] getColumnVectors() {
    NumberTuple[] columns = new NumberTuple[getColumnCount()];
    for (int i = 0; i < columns.length; i++)
      columns[i] = getColumnVector(i + 1);
    return columns;
  }

  public void exportRow(int row, NumberTuple rowTupel) {
    for (int i = 1; i <= getColumnCount(); i++)
      rowTupel.getEntryRef(i).set(getEntryAsNumberRef(row, i));
  }

  /**
   * Fills up the given <code>columnTupel</code> with the corresponding entries
   * in this matrix, i.e. <code>rowTupel[i-1]</code> will get the entries
   * of the <it>i</it>th row in this matrix.  No &quot;new&quot; will be made.
   *
   * @version  10/16/2002
   */
  public void exportToRows(NumberTuple[] rowTupel) {
    for (int i = 1; i <= getRowCount(); i++)
      exportRow(i, rowTupel[i - 1]);
  }

  public void exportColumn(int col, NumberTuple colTupel) {
    for (int i = 1; i <= getRowCount(); i++)
      colTupel.getEntryRef(i).set(getEntryAsNumberRef(i, col));
  }

  /**
   * Fills up the given <code>columnTupel</code> with the corresponding entries
   * in this matrix, i.e. <code>columnTupel[i-1]</code> will get the entries
   * of the <it>i</it>th column in this matrix. No &quot;new&quot; will be made.
   *
   * @version  10/16/2002
   */
  public void exportToColumns(NumberTuple[] columnTupel) {
    for (int i = 1; i <= getColumnCount(); i++)
      exportColumn(i, columnTupel[i - 1]);
  }

  /**
   * Author Matthias Holschneider<br>
   * generates a matrix that if interpretd as linear
   * operator maps domain[i] into range[i]
   */
  public NumberMatrix setAsLinearMapping(
    NumberTuple[] domain,
    NumberTuple[] range) {
    // to do check validity of dimensions
    //reshape(range[0].getDimension(),range.length); // not final shape
    setColumnVectors(range);

    NumberMatrix m =
      new NumberMatrix(getNumberClass(), false, domain);
    NumberMatrix mT = (NumberMatrix) m.transposed();

    // M m = b <=> M = b mT (m mT)^{-1}
    mult(mT).mult((m.mult(mT)).inverse());
    return this;
  }

  /**
   * Sets this matrix to the identity if this matrix is square and throws
   * an {@link net.mumie.mathletfactory.action.message.IllegalUsageException} if it is not.
   *
   * @version  10/14/2002
   */
  public NumberMatrix setToIdentity() {
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
                  ? getEntryAsNumberRef(i, j).getDouble() == 1
                  : getEntryAsNumberRef(i, j).isZero()))
            return false;
        }
      }
      return true;
    }
    else
      return false;
  }

  /**
   This method return the adjoint of this matrix.
   */
  public NumberMatrix adjoint() {
  	
    for (int r = 1; r <= getRowCount(); r++) {
        for (int c = 1; c <= getColumnCount(); c++) {
          getEntryAsNumberRef(r, c).conjugate();
        }
      }
    transpose();
    return this;
  	
   // throw new TodoException();
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
  public NumberTuple applyTo(NumberTuple aColumnTupel) {
    NumberTuple m_tempTupel = new NumberTuple(aColumnTupel);

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
  public void applyTo(NumberTuple aColumnTupel, NumberTuple result) {
    int n = aColumnTupel.getRowCount();
    MNumber tmp = NumberFactory.newInstance(getNumberClass());
    for (int i = 1; i <= n; i++) {
      MNumber resultEntry = NumberFactory.newInstance(getNumberClass());
      for (int j = 1; j <= n; j++) {
        tmp.setDouble(0);
        tmp.add((MNumber) aColumnTupel.getEntryRef(j));
        tmp.mult((MNumber) getEntryRef(i, j));
        resultEntry.add(tmp);
      }
      result.setEntryRef(i, resultEntry);
    }
    // do the same side effect as applyTo(aColumnTupel);
    aColumnTupel.m_entries = result.m_entries;
  }

  public NumberMatrix multWithNumber(MNumber aNumber) {
    for (int r = 1; r <= getRowCount(); r++) {
      for (int c = 1; c <= getColumnCount(); c++) {
        getEntryAsNumberRef(r, c).mult(aNumber);
      }
    }
    return this;
  }

  /**
   * This methods performs the matrix multiplication between this <code>
   * NumberMatrix</code> and <code>aMatrix</code> and changes this instance, i.e.
   * resizes this matrix so that it fits to the product matrix' size.
   */
  public NumberMatrix mult(NumberMatrix aMatrix) {
    int rowOut = getRowCount();
    int colOut = aMatrix.getColumnCount();

    if (getColumnCount() == aMatrix.getRowCount()) {
      NumberMatrix tmp = (NumberMatrix) deepCopy();
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
  public static NumberMatrix mult(NumberMatrix m1, NumberMatrix m2){
    NumberMatrix retVal = new NumberMatrix(m1.getNumberClass(), m1.getColumnCount(), m1.getRowCount());
    retVal.copyFrom(m1);
    return retVal.mult(m2);
  }

  /**
   * Returns true if all entries are equal to zero.
   */
  public boolean isZero() {
    for (int r = 1; r <= getRowCount(); r++) {
      for (int c = 1; c <= getColumnCount(); c++) {
        if (!getEntryAsNumberRef(r, c).isZero())
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
        if (i == j || getEntryAsNumberRef(i, j).isZero())
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
        MNumber entry = getEntryAsNumberRef(i, j);
        if (entry == null) {
          setEntry(i, j, NumberFactory.newInstance(getNumberClass()));
        } else {
          entry.setDouble(0);
        }
      }
    }
  }

  /**
   * Adds <code>aMatrix</code> to this matrix and returns <code>this</code>.
   */
  public NumberMatrix addTo(NumberMatrix aMatrix) {
    int rowMin = Math.min(getRowCount(), aMatrix.getRowCount());
    int colMin = Math.min(getColumnCount(), aMatrix.getColumnCount());
    for (int r = 1; r <= rowMin; r++) {
      for (int c = 1; c <= colMin; c++) {
        getEntryAsNumberRef(r, c).add(aMatrix.getEntryAsNumberRef(r, c));
      }
    }
    return this;
  }

  /**
   * Substracts <code>aMatrix</code> from this matrix and returns <code>this</code>.
   */
  public NumberMatrix subFrom(NumberMatrix aMatrix) {
    int rowMin = Math.min(getRowCount(), aMatrix.getRowCount());
    int colMin = Math.min(getColumnCount(), aMatrix.getColumnCount());
    for (int r = 1; r <= rowMin; r++) {
      for (int c = 1; c <= colMin; c++) {
        getEntryAsNumberRef(r, c).sub(aMatrix.getEntryAsNumberRef(r, c));
      }
    }
    return this;
  }

  /**
   * Every entry m(i,j) of this instance of <code>NumberMatrix</code> will be
   * replaced by -1*m(i,j) and this instance will be returned.
   *
   * @version  10/16/2002
   */
  public NumberMatrix negate() {
    for (int r = 1; r <= getRowCount(); r++) {
      for (int c = 1; c <= getColumnCount(); c++) {
        getEntryAsNumberRef(r, c).negate();
      }
    }
    return this;
  }

  public NumberMatrix shallowCopy() {
    try {
      return (NumberMatrix) this.clone();
    }
    catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null; //should never happen
  }

  /**
   * This method returns a new instance of <code>NumberMatrix</code> which has
   * the same dimension as this matrix and identical entries, i.e.
   * {@link net.mumie.mathletfactory.math.number.MNumber} instances that have the
   * same value as in this matrix.
   *
   * @version  10/16/2002
   */
  public NumberMatrix deepCopy() {
    try {
      Constructor constructor =
        getClass().getConstructor(new Class[] { getClass()});
      return (NumberMatrix) constructor.newInstance(new Object[] { this });

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
   * The "edited"-flags will not be changed in this number matrix. 
   *
   * This method works without creating new <code>MNumber</code>s, but observe
   * that <code>aMatrix</code> may not hold any null entries - this would leed
   * to a {@link java.lang.NullPointerException}.
   *
   * @version  10/16/2002
   */
  public NumberMatrix copyFrom(NumberMatrix aMatrix) {
    return copyFrom(aMatrix, false);
  }
  
  /**
   * All {@link net.mumie.mathletfactory.math.number.MNumber} entries in this matrix
   * will be set to the value of the corresponding entry in <code>aMatrix</code>.
   * The "edited"-flags will be copied to this number matrix if the flag 
   * <code>copyEditedFlags</code> is set to <code>true</code>. 
   *
   * This method works without creating new <code>MNumber</code>s, but observe
   * that <code>aMatrix</code> may not hold any null entries - this would leed
   * to a {@link java.lang.NullPointerException}.
   */
  public NumberMatrix copyFrom(NumberMatrix aMatrix, boolean copyEditedFlags) {
    for (int i = 0; i < m_entries.length; i++) {
      ((MNumber) m_entries[i]).set((MNumber) aMatrix.m_entries[i]);
      if(copyEditedFlags)
      	((MNumber) m_entries[i]).setEdited(((MNumber) aMatrix.m_entries[i]).isEdited());
    }
    return this;
  }

  /**
   * this method tests if this <code>NumberMatrix</code> is collinear to
   * <code>aMatrix</code>, i.e. if it is a multiple of it.
   *
   * @version  9/26/2002
   */
//  public boolean isCollinear(NumberMatrix aMatrix) {
//    if (hasSameShape(aMatrix)) {
//      if(aMatrix.isZero() || isZero())
//        return true;
//      MNumber ratio = null;
//      MNumber entry, aMatrixEntry;
//      for (int i = 1; i <= getRowCount(); i++) {
//        for (int j = 1; j <= getColumnCount(); j++) {
//          entry = (MNumber) getEntryRef(i, j);
//          aMatrixEntry = (MNumber) aMatrix.getEntryRef(i, j);
//          if (ratio == null) {
//            if (aMatrixEntry.isZero()) {
//              if (!entry.isZero())
//                return false;
//              else
//                ratio = NumberFactory.newInstance(getNumberClass());
//            }
//            else {
//              if (entry.isZero()) {
//                return false;
//              }
//              else {
//                ratio = MNumber.divide(entry, aMatrixEntry);
//              }
//            }
//          }
//          else {
//            //ratio is a Number != +/- infty and != 0
//            if (aMatrixEntry.isZero()) {
//              if (!entry.isZero()) {
//                return false;
//              }
//            }
//            else {
//              if (!ratio.equals(MNumber.divide(entry, aMatrixEntry))) {
//                return false;
//              }
//            }
//          }
//        }
//      }
//      return true;
//    }
//    else
//      return false;
//  }
  
  public boolean isCollinear(NumberMatrix aMatrix) {
	    if (hasSameShape(aMatrix)) {
	      if(aMatrix.isZero() || isZero())
	        return true;
	      
	      MNumber ratio = null;
	      MNumber entry, aMatrixEntry;
	      for (int i = 1; i <= getRowCount(); i++) {
	        for (int j = 1; j <= getColumnCount(); j++) {
	          entry = (MNumber) getEntryRef(i, j);
	          aMatrixEntry = (MNumber) aMatrix.getEntryRef(i, j);
	          if (entry.isZero() && !aMatrixEntry.isZero())
	        	  return false;
	          if (!entry.isZero() && aMatrixEntry.isZero())
	        	  return false;
	          
	          if (!entry.isZero() && !aMatrixEntry.isZero()) { 
	        	  ratio = MNumber.divide(aMatrixEntry, entry);
	        	  return this.deepCopy().multWithNumber(ratio).equals(aMatrix);
	          }
	        }
	      }
	      return false;
	    }
	    else
	      return false;
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
    	
      if (obj instanceof MOpNumber || obj instanceof MComplex || obj instanceof MComplexRational) {
    	return new StringBuffer(obj.toString());
      }
    	
      if (obj instanceof MNumber) 
        obj = new Double(((MNumber) obj).getDouble());
      return format.format(obj, toAppendTo, pos);
    }

    public Object parseObject(String source, ParsePosition status) {
      Object result = format.parseObject(source, status);
      return new MDouble(((Number) result).doubleValue());
    }
  }

  /**
   * This instance of <code>NumberMatrix</code> will become the (mathematical)
   * inverse of itsself. An {@link IllegalUsageException}
   * will be thrown if this matrix is not square or not invertible.
   *
   * @version  10/16/2002
   */
  public NumberMatrix inverse() {
    if (isSquare()) {
      MNumber det = determinant();
      if (det.isZero())
        throw new IllegalUsageException("Matrix is not invertible (determinant is zero)");
      NumberMatrix copy = (NumberMatrix) deepCopy();
      switch (getColumnCount()) {
        case 1 :
          {
            MNumber unit = getEntryAsNumberRef(1, 1).create().setDouble(1.0);
            setEntryRef(1, 1, unit.div(getEntryAsNumberRef(1, 1)));
            return this;
          }
        case 2 :
          {
            setEntryRef(
              1,
              1,
              MNumber.divide(copy.getEntryAsNumberRef(2, 2), det));
            setEntryRef(
              2,
              2,
              MNumber.divide(copy.getEntryAsNumberRef(1, 1), det));
            setEntryRef(
              1,
              2,
              copy.getEntryAsNumberRef(1, 2).negated().div(det));
            setEntryRef(
              2,
              1,
              copy.getEntryAsNumberRef(2, 1).negated().div(det));
            return this;
          }
        case 3 :
          {
            setEntryRef(
              1,
              1,
              copy
                .getEntry(2, 2)
                .mult(copy.getEntry(3, 3))
                .sub(copy.getEntry(2, 3).mult(copy.getEntry(3, 2)))
                .div(det));
            setEntryRef(
              1,
              2,
              copy
                .getEntry(1, 3)
                .mult(copy.getEntry(3, 2))
                .sub(copy.getEntry(1, 2).mult(copy.getEntry(3, 3)))
                .div(det));
            setEntryRef(
              1,
              3,
              copy
                .getEntry(1, 2)
                .mult(copy.getEntry(2, 3))
                .sub(copy.getEntry(1, 3).mult(copy.getEntry(2, 2)))
                .div(det));
            setEntryRef(
              2,
              1,
              copy
                .getEntry(2, 3)
                .mult(copy.getEntry(3, 1))
                .sub(copy.getEntry(2, 1).mult(copy.getEntry(3, 3)))
                .div(det));
            setEntryRef(
              2,
              2,
              copy
                .getEntry(1, 1)
                .mult(copy.getEntry(3, 3))
                .sub(copy.getEntry(1, 3).mult(copy.getEntry(3, 1)))
                .div(det));
            setEntryRef(
              2,
              3,
              copy
                .getEntry(1, 3)
                .mult(copy.getEntry(2, 1))
                .sub(copy.getEntry(1, 1).mult(copy.getEntry(2, 3)))
                .div(det));
            setEntryRef(
              3,
              1,
              copy
                .getEntry(2, 1)
                .mult(copy.getEntry(3, 2))
                .sub(copy.getEntry(2, 2).mult(copy.getEntry(3, 1)))
                .div(det));
            setEntryRef(
              3,
              2,
              copy
                .getEntry(1, 2)
                .mult(copy.getEntry(3, 1))
                .sub(copy.getEntry(1, 1).mult(copy.getEntry(3, 2)))
                .div(det));
            setEntryRef(
              3,
              3,
              copy
                .getEntry(1, 1)
                .mult(copy.getEntry(2, 2))
                .sub(copy.getEntry(1, 2).mult(copy.getEntry(2, 1)))
                .div(det));
            return this;
          }
      }
      MNumber[] tmp = new MNumber[m_entries.length];
      for (int i = 0; i < m_entries.length; i++)
        tmp[i] = (MNumber) m_entries[i];
      m_entries = Invert.invertByGauss(tmp);
      return this;
      // throw new TodoException("inverse implementieren");

    }
    else {
      throw new IllegalUsageException("makes only sense for square matrices");
    }
  } // to be done (check dimension!)

  // determinant has to be implemented on this level already
  // since otherwise A^t A which is a square matrix cannot be worked with

  /**
   * Computes the determinant of this matrix if it is square and throws an
   * {@link IllegalUsageException} otherwise.
   *
   * @version  10/16/2002
   */
  public MNumber determinant() {
    if (isSquare())
      return Determinant.det(getEntriesAsNumberRef());
    else
      throw new IllegalUsageException("can only be applied to square matrices");
  }

  /**
   * Returns the inverse this matrix and throws an {@link IllegalUsageException}
   * if it is not possible. This matrix remains unchanged.
   *
   * @version  10/16/2002
   */
  public NumberMatrix inverted() {
    return deepCopy().inverse();
  }

  /**
   * Returns a transposed copy of this NumberMatrix.
   */
  public NumberMatrix transposed() {
    return (NumberMatrix) deepCopy().transpose();
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
   * Computes the rank, i.e. the maximum number of linearly independant row
   * vectors or column vectors.
   */
  public int rank() {
  	NumberMatrix inputMatrix = null;
  	if(getNumberClass().isAssignableFrom(MInteger.class)) {
  		inputMatrix = new NumberMatrix(MDouble.class, getColumnCount(), getRowCount());
  		inputMatrix.copyFrom(this);
  	} else {
  		inputMatrix = this;
  	}
    NumberMatrix echelonMatrix = MatrixUtils.getEchelonForm(inputMatrix);
    int rank = echelonMatrix.getRowCount();// max rank at first
		for (int r = 1; r <= echelonMatrix.getRowCount(); r++) {
			if (echelonMatrix.getRowVector(r).isZero()) // substract 1 for every empty row 
				rank--;
		}
		return rank;
  }

  public NumberMatrix echelonForm() {
    return MatrixUtils.getEchelonForm(this);
  }

  /**
   * checks whether this matrix is idempotent or not, i.e. whether
   * M*M = M.
   *
   * @version  10/16/2002
   */
  public boolean isProjector() {
    NumberMatrix product = deepCopy();
    product.mult(this);
    return product.equals(this);
  };

  /**
   * Checks whether this matrix represents a rotation, i.e. if it is orthogonal
   * and its determinant equals 1.
   *
   * @version  10/16/2002
   */
  public boolean isRotation() {
    return determinant().equals(
      NumberFactory.newInstance(getNumberClass(), 1.0))
      && isOrthogonal();
  }

  /**
   * Checks whether this matrix is orthogonal, i.e. if M^-1 = M^T.
   *
   * @version  10/16/2002
   */
  public boolean isOrthogonal() {
    if (!isInvertible())
      return false;
    NumberMatrix inverse = inverted();
    NumberMatrix transpose = (NumberMatrix) deepCopy().transpose();
    return inverse.equals(transpose);
  }

  public boolean isReflection() {
    throw new TodoException();
  }

  public double[] toDoubleArray() {
    double[] retVal = new double[m_entries.length];
    for (int i = 0; i < m_entries.length; i++)
      retVal[i] = ((MNumber) m_entries[i]).getDouble();
    return retVal;
  }

  protected void setNewSize(int numOfRows, int numOfCols) {
    super.setNewSize(numOfRows, numOfCols);
//  	System.out.println("new size");
    for (int i = 0; i < m_entries.length; i++) {
      if (m_entries[i] == null) {
        m_entries[i] = NumberFactory.newInstance(getNumberClass());
      }
    }
//    fireDimensionChanged();
  }

  public boolean equals(Object shouldBeAMatrix) {
  	if(shouldBeAMatrix instanceof NumberMatrix) {
  		NumberMatrix m = (NumberMatrix)shouldBeAMatrix;
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
   * compares this matrix with another one and returns the number
   * of different matrix entries
   * returns Integer.MAX_VALUE if both matrices differ in shape
   * or 'shouldBeAMatrix' isn't an instance of NumberMatrix
   * 
   * @version  5/12/2006
   */
  public int equalsWithErrorCounter(Object shouldBeAMatrix) {
	  	int counter=0;
	  	if(shouldBeAMatrix instanceof NumberMatrix) {
	  		NumberMatrix m = (NumberMatrix)shouldBeAMatrix;
	  		if(hasSameShape(m))
	  		{
	  			for(int  i=0; i < this.m_entries.length; i++) {
	  				int row = getRowFromIndex(i);
	  				int col = getColumnFromIndex(i);
	  				if(!((MNumber)getEntryRef(row,col)).isEdited() ^ !((MNumber)m.getEntryRef(row, col)).isEdited())counter++;
	  				else if(!getEntry(row, col).equals(m.getEntry(row, col)))
	  					counter++;
	  			}
	  			return counter;
	  		}
	  		else
	  			return Integer.MAX_VALUE;
	  	}
	  	else
	  		return Integer.MAX_VALUE;
	  }

  public Polynomial getCharPoly(){
    if(!isSquare())
      throw new IllegalStateException("Matrix must be square!");
    OpMatrix opMatrix = new OpMatrix(this);
    for(int i=1;i<=getColumnCount();i++)
      opMatrix.setEntry(i, i, new Operation(getNumberClass(), ""+getEntryRef(i, i)+"-x", true));
    Operation poly = opMatrix.determinant();
    poly.expand();
    return poly.getPolynomialHolder().getAsPolynomial("x");
  }

  /**
   * Returns true if at least one entry was edited by the user.
   */
  public boolean isEdited() {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				if(isEdited(row, col))
					return true;
				}
    }
    return false;
  }
  
  /**
   * Returns if the cell at the specified position was edited.
   * Returns <code>true</code> if the cell entry is not a {@link MNumber}.
   */
  public boolean isEdited(int row, int col) {
		if(getEntryRef(row, col) instanceof MNumber)
			return ((MNumber)getEntryRef(row, col)).isEdited();
		else
			return true;
  }
  
  public boolean isCompletelyEdited() {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				if( !isEdited(row, col))
					return false;
			}
    }
    return true;
  }
  
  /**
   * Copies the "edited"-flag of every entry from <code>aMatrix</code> to this matrix.
   * Notice: No values will be changed.
   */
  public void setEdited(NumberMatrix aMatrix) {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				setEdited(row, col, ((MNumber)aMatrix.getEntryRef(row, col)).isEdited());
			}
		}
  }
  
	/**
	 * Sets the edited-flag for one cell.
	 */
	public void setEdited(int row, int col, boolean edited) {
		if(m_entries[getIndexFromEntry(row, col)] instanceof MNumber) {
			((MNumber)m_entries[getIndexFromEntry(row, col)]).setEdited(edited);
		}
	}

	/**
	 * Sets the edited-flag for all entries.
	 */
	public void setEdited(boolean edited) {
		// No global field "edited" is needed as for setEditable(boolean) in MMNumberMatrix
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				setEdited(row, col, edited);
			}
		}
	}

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

  public Class getNumberClass() {
    return getEntryClass();
  }

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
	
	 public boolean isNormalForm() {
			for(int row = 1; row <= getRowCount(); row++) {
				for(int col = 1; col <= getColumnCount(); col++) {
					if(!isNormalForm(row, col))
						return false;
				}
			}
			return true;
	  }
	  
	  /**
	   * Returns if the cell at the specified position was edited.
	   * Returns <code>true</code> if the cell entry is not a {@link MNumber}.
	   */
	  public boolean isNormalForm(int row, int col) {
			if(getEntryRef(row, col) instanceof MOpNumber)
				return ((MOpNumber)getEntryRef(row, col)).isNormalForm();
			else
				return true;
	  }
	
	
	public void setNormalForm(int row, int col, boolean normalize) {
		if(m_entries[getIndexFromEntry(row, col)] instanceof MOpNumber) {
			((MOpNumber)m_entries[getIndexFromEntry(row, col)]).setNormalForm(normalize);
		}
	}
	
	public void setNormalForm(boolean normalize) {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				setNormalForm(row, col, normalize);
			}
		}
	}
	
	
	public boolean getUsedVariables() {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				if(!getUsedVariables(row, col))
					return false;
			}
		}
		return true;
  }
  
  /**
   * Returns if the cell at the specified position was edited.
   * Returns <code>true</code> if the cell entry is not a {@link MNumber}.
   */
	public boolean getUsedVariables(int row, int col) {
		if(getEntryRef(row, col) instanceof MOpNumber)
			return ((MOpNumber)getEntryRef(row, col)).getUsedVariables();
		else
			return true;
	}


	public void setUsedVariables(int row, int col, boolean b) {
		if(m_entries[getIndexFromEntry(row, col)] instanceof MOpNumber) {
			((MOpNumber)m_entries[getIndexFromEntry(row, col)]).setUsedVariables(b);
		}
	}

	public void setUsedVariables(boolean b) {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				setUsedVariables(row, col, b);
			}
		}
	}
	
	
	
	
	/**
	 * Returns the trace of this matrix.
	 * This matrix must be square to calculate the trace.
	 */
	public MNumber trace() {
		if(!isSquare())
			throw new IllegalArgumentException("Matrix must be square to calculate the trace!");
    MNumber sum = NumberFactory.newInstance(getNumberClass());
    for(int i = 1; i <= getRowCount(); i++)
        sum.add(getEntry(i,i));
    return sum;
	}

	/**
	 * Returns the trace of an arbitrary matrix.
	 * The matrix must be square to calculate the trace.
	 */
	public static MNumber trace(NumberMatrix m) {
		if(!m.isSquare())
			throw new IllegalArgumentException("Matrix must be square to calculate the trace!");
    MNumber sum = NumberFactory.newInstance(m.getNumberClass());
    for(int i = 1; i <= m.getRowCount(); i++)
        sum.add(m.getEntry(i,i));
    return sum;
	}
	
  /**
   * Returns the scalar product of this and <code>aMatrix</code> as a MNumber.
   */
	public MNumber scalarProduct(NumberMatrix aMatrix) {
		return scalarProduct(this, aMatrix);
	}
	
  /**
   * Returns the scalar product of <code>m1</code> and <code>m2</code> as a MNumber.
   */
	public static MNumber scalarProduct(NumberMatrix m1, NumberMatrix m2) {
		return trace(m1.deepCopy().adjoint().mult(m2));
	}
	
	/**
	 * Returns the standard norm of this matrix.
	 */
	public MNumber standardNorm() {
		return standardNorm(this);
	}
	
	/**
	 * Returns the standard norm of an arbitrary matrix.
	 */
	public static MNumber standardNorm(NumberMatrix aMatrix) {
		return aMatrix.scalarProduct(aMatrix).squareRoot();
	}
	
	/**
	 * Returns the distance between this and <code>aMatrix</code>.
	 * Both matrices must have the same shape.
	 */
  public MNumber distance(NumberMatrix aMatrix) {
  	if(!this.hasSameShape(aMatrix))
  		throw new IllegalArgumentException("Both matrices must have the same shape!");
  	return standardNorm(this.deepCopy().subFrom(aMatrix));
  }

  /**
	 * Returns the distance between 2 matrices.
	 * Both matrices must have the same shape.
	 */
  public static MNumber distance(NumberMatrix matrix1, NumberMatrix matrix2) {
  	if(!matrix1.hasSameShape(matrix2))
  		throw new IllegalArgumentException("Both matrices must have the same shape!");
  	return standardNorm(matrix1.deepCopy().subFrom(matrix2));
  }
}
