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

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents a one column matrix, i.e. a column vector.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class NumberTuple extends NumberMatrix {

	
	public final static String COLUMN_VECTOR_TYPE = "column-vector";
	public final static String ROW_VECTOR_TYPE = "row-vector";
	
  /**
   * Creates a vector as "column vector", i.e. the underlying NumberMatrix
   * has 1 single column after first creation. The number of rows will be determined
   * by the number of entries in the array.
   */
  public NumberTuple(Class numberClass, Object[] entries) {
    super(numberClass, 1, entries);
  }

  /**
   * Creates a vector as "column vector", i.e. the underlying NumberMatrix
   * has 1 single column after first creation. The number of rows will be <code>dimension</code>.
   */
  public NumberTuple(Class numberClass, int dimension) {
    super(numberClass, 1, dimension);
  }

  /**
   * Creates a 2 dimensional vector as "column vector", i.e. the underlying NumberMatrix
   * has 1 single column after first creation. The number of rows will be 2.
   */
  public NumberTuple(Class numberClass, double x, double y) {
    this(
      numberClass,
      new Object[] {
        NumberFactory.newInstance(numberClass, x),
        NumberFactory.newInstance(numberClass, y)});
  }

  
  /**
   * Creates a 3 dimensional vector as "column vector", i.e. the underlying NumberMatrix
   * has 1 single column after first creation. The number of rows will be 3.
   */
  public NumberTuple(Class numberClass, double x, double y, double z) {
    this(
      numberClass,
      new Object[] {
        NumberFactory.newInstance(numberClass).setDouble(x),
        NumberFactory.newInstance(numberClass, y),
        NumberFactory.newInstance(numberClass, z)});
  }

  public NumberTuple[] asOneDArray() {
    NumberTuple[] tmp = new NumberTuple[1];
    tmp[0] = this;
    return tmp;
  }

  /**
   * Copy constructor
   */
  public NumberTuple(NumberTuple aVector) {
    super(aVector);
  }

  public NumberTuple(NumberMatrix aOneColumnMatrix) {
    super(aOneColumnMatrix);
    if (aOneColumnMatrix.getColumnCount() != 1)
      throw new IllegalArgumentException(
        "only matrices with one column can "
          + "be converted to a NumberTuple!");
  }

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param mathMLnode a MathML/XML node
   * @throws Exception when an error while instantiation occurrs
	 */
  public NumberTuple(Node content) throws Exception {
    super(content);
  }

  public void setEntryRef(int index, MNumber value) {
    if (isColumnVector()) {
      setEntryRef(1, index, value);
    }
    else {
      setEntryRef(index, 1, value);
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

  public MNumber getEntryRef(int index) {
    if (isColumnVector()) {
      return (MNumber) getEntryRef(1, index);
    }
    else {
      return (MNumber) getEntryRef(index, 1);
    }
  }

  public MNumber getEntry(int index) {
    if (isColumnVector()) {
      return (MNumber) getEntry(1, index);
    }
    else {
      return (MNumber) getEntry(index, 1);
    }
  }

  /**
   * Returns if this vector is a column vector, i.e. a vector whose 
   * number of columns is 1.
   */
  public boolean isColumnVector() {
    return getColumnCount() == 1;
  }

  protected void checkDimension(NumberTuple aVector) {
    if (getDimension() != aVector.getDimension())
      throw new IllegalArgumentException("vector must have same dimension");
  }

  /**
   * Returns the scalar product of this and <code>aVector</code> as a MNumber.
   */
  public MNumber scalarProduct(NumberTuple aVector) {
    NumberTuple tmp = (NumberTuple) aVector.deepCopy();

    for (int i = 1; i <= getDimension(); i++) //fortan indexing!
      tmp.getEntryRef(i).conjugate();

    return dotProduct(tmp);
  }

  public MNumber standardNorm() {
    return scalarProduct(this).squareRoot();
  }

  /**
   * Computes the sum of this(i)*aVector(i) for i between 1 and the maximum
   * length of both <code>NumberTuple</code>s. Both must be based on the same
   * number type ({@link net.mumie.mathletfactory.math.number.MDouble},
   * {@link net.mumie.mathletfactory.math.number.MRational}, ...). Both number
   * tuples remain unchanged.
   */
  public MNumber dotProduct(NumberTuple aVector) {
    if (getNumberClass().equals(aVector.getNumberClass())) {
      int calcLength = Math.min(getDimension(), aVector.getDimension());
      MNumber sum = getEntryRef(1).create(); // fortran-indexing!!
      for (int i = 1; i <= calcLength; i++)
        sum.add(MNumber.multiply(getEntryRef(i), aVector.getEntryRef(i)));
      return sum;
    }
    else
      throw new IllegalArgumentException("both tupels must be based on the same number class");
  }

  /**
   * Computes the sum of this(i)*aVector(i) for i between 1 and the maximum
   * length of both <code>NumberTuple</code>s. Both must be based on the same
   * number type ({@link net.mumie.mathletfactory.number.MDouble},
   * {@link net.mumie.mathletfactory.number.MRational}, ...). This <code>NumberTuple</code>
   * will be changed during the computation and will hold
   * the product entries.
   */
  public MNumber dotProductFast(NumberTuple aVector) {
    MNumber result = NumberFactory.newInstance(getNumberClass());
    dotProductFast(aVector, result);
    return result;
  }

  /**
   * Computes the sum of this(i)*aVector(i) for i between 1 and the maximum
   * length of both <code>NumberTuple</code>s. Both must be based on the same
   * number type ({@link net.mumie.mathletfactory.number.MDouble},
   * {@link net.mumie.mathletfactory.number.MRational}, ...). This <code>NumberTuple</code>
   * will be changed during the computation and will hold
   * the product entries.
   */
  public void dotProductFast(NumberTuple aVector, MNumber result) {
    if (getNumberClass().equals(aVector.getNumberClass())) {
      int calcLength = Math.min(getDimension(), aVector.getDimension());
      result.set(getEntryRef(1).mult(aVector.getEntryRef(1)));
      for (int i = 2; i <= calcLength; i++)
        result.add(getEntryRef(i).mult(aVector.getEntryRef(i)));
    }
    else
      throw new IllegalArgumentException("both tupels must be based on the same number class");
  }

  /**
   *  Calculates this * aVector^T
   */
  public NumberMatrix outerProduct(NumberTuple aVector) {
    int dim = getDimension();
    NumberMatrix result = new NumberMatrix(getNumberClass(), dim, dim);
    for (int i = 1; i <= dim; i++)
      for (int j = 1; j <= dim; j++)
        result.setEntryRef(i, j, getEntry(i).mult(aVector.getEntry(j)));
    return result;
  }

  /**
   * Method multiplyWithScalar
   */
  public NumberTuple multiplyWithScalar(MNumber aNumber) {
    for (int i = 1; i <= this.getDimension(); i++) {
      getEntryRef(i).mult(aNumber);
    }
    return this;
  }

  /**
   * Method multiplyWithScalar
   */
  public NumberTuple multiplyWithScalar(double aNumber) {
    return multiplyWithScalar(NumberFactory.newInstance(getNumberClass(), aNumber));
  }

  /**
   * Returns the dimension of this number tuple, i.e. max(getRowCount(), getColumnCount()).
   */
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

  /**
   * Adds the tuple <code>addend</code> to this and returns this.
   */
  public NumberTuple addTo(NumberTuple addend) {
    if (getDimension() >= addend.getDimension()) {
      for (int i = 1; i <= addend.getDimension(); ++i) {
        getEntryRef(i).add(addend.getEntryRef(i));
      }
    }
    else {
      Object[] tmp = new Object[getDimension()];
      for (int i = 1; i <= getDimension(); ++i) {
        tmp[i - 1] = getEntryRef(i);
        ((MNumber) tmp[i - 1]).add(addend.getEntryRef(i));
      }
      init(1, tmp);
    }
    return this;
  }

  /**
   * Substracts the tuple <code>minuend</code> from this and returns this.
   */
  public NumberTuple subFrom(NumberTuple minuend) {
    if (getDimension() >= minuend.getDimension()) {
      for (int i = 1; i <= minuend.getDimension(); ++i) {
        getEntryRef(i).sub(minuend.getEntryRef(i));
      }
    }
    else {
      Object[] tmp = new Object[getDimension()];
      for (int i = 1; i <= getDimension(); ++i) {
        tmp[i - 1] = getEntryRef(i);
        ((MNumber) tmp[i - 1]).sub(minuend.getEntryRef(i));
      }
      init(1, tmp);
    }
    return this;
  }

  public NumberTuple getSubVectorView(Class vectorClass, int i, int j) {
    if (!NumberTuple.class.isAssignableFrom(vectorClass))
      throw new IllegalArgumentException("vectorClass has to be a Vector or subclass of it");
    if (isColumnVector())
      return (NumberTuple) getSubMatrixView(vectorClass, 1, i, 1, j);
    else
      return (NumberTuple) getSubMatrixView(vectorClass, i, 1, j, 1);
  }

  /**
   * returns the index of the first entry, for which
   * {@link net.mumie.mathletfactory.math.number.MNumber#isZero isZero()} is true,
   *  it returns -1 if no entry is zero.
   */
  public int getIndexOfFirstNonZeroEntry() {
    for (int i = 1; i <= m_entries.length; i++)
      if (!((MNumber) m_entries[i - 1]).isZero())
        return i;
    return -1;
  }

  public NumberTuple copyFrom(NumberTuple aTupel) {
    int calcLength = Math.min(getDimension(), aTupel.getDimension());
    for (int i = 0; i < calcLength; i++) {
      ((MNumber) m_entries[i]).set((MNumber) aTupel.m_entries[i]);
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

  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
    Element mrow = ExerciseObjectFactory.createNode(this, "mrow", doc);
    mrow.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
    if(isColumnVector())
    	mrow.setAttribute("class", COLUMN_VECTOR_TYPE);
    else
    	mrow.setAttribute("class", ROW_VECTOR_TYPE);

    for(int d = 1; d <= getDimension(); d++) {
      mrow.appendChild(doc.createTextNode("\n  "));
      mrow.appendChild(getEntryRef(d).getMathMLNode(doc));
    }
    mrow.appendChild(doc.createTextNode("\n"));
    return mrow;
  }

  /**
   * Sets the XML content of a single cell.
   * Parameter <code>index</code> must be 1 based.
   */
  public void setMathMLNode(Node xmlNode, int index) {
	  	Object entry = getEntryRef(index);
	  	if(entry instanceof MathMLSerializable) {
	  		if(xmlNode != null) {
	  			((MathMLSerializable) entry).setMathMLNode(xmlNode);
	  		} else {// edited-flag for ex-obj.'s
	  			if(entry instanceof ExerciseObjectIF) {
	  				((ExerciseObjectIF) entry).setEdited(false);
	  			}// do nothing if not ex-obj and node is null
	  		}
	  	} else {
	  		throw new XMLParsingException("Entry does not implement to read from XML content!");
	  	}
	  }

  public void setMathMLNode(Node node) {
  	if(node.getNodeName().equalsIgnoreCase("mtable")) {
  		super.setMathMLNode(node);
  		return;
  	}
    if(!node.getNodeName().equalsIgnoreCase("mrow"))
      throw new XMLParsingException("Node name must be \"<mrow>\" or \"<mtable>\"!");

    setNewSize(0, 0);
    NodeList mnList = node.getChildNodes();
    for (int entry = 0; entry < mnList.getLength(); entry++) {
      Node numberEntry = mnList.item(entry);
      if(numberEntry.getNodeType() == Node.TEXT_NODE)
        continue;
      if(isColumnVector())
    	  setNewSize(getRowCount()+1, 1);
      else // is row vector!
    	  setNewSize(1, getColumnCount()+1);
      setMathMLNode(numberEntry, getDimension());
    }
    ExerciseObjectFactory.importExerciseAttributes(node, this);
    fireDimensionChanged();
  }
  
  /**
   * Returns if the cell at the specified position was edited.
   */
  public boolean isEdited(int index) {
		return getEntryRef(index).isEdited();
  }
  
	/**
	 * Sets the edited-flag for one cell.
	 */
	public void setEdited(int index, boolean edited) {
		getEntryRef(index).setEdited(edited);
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
