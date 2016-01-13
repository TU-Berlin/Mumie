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
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * This class represents a matrix.
 * The indices for rows and cols are one-based (Fortran-Notation).
 * 
 * @author amsel
 * @mm.docstatus finished
 */
public class Matrix implements Cloneable, MathMLSerializable {
  	
  private static Logger logger = Logger.getLogger(Matrix.class.getName());
  private ArrayList changeListeners = new ArrayList();
  
  /**
   * Method getStringMatrix returns a new instance of <code>Matrix</code>
   * of desired dimension with all entries equal to the overgiven <code>String
   * </code>.
   *
   * @param    numOfColumns        an int
   * @param    numOfRows           an int
   * @param    s                   a  String
   *
   * @return   a Matrix
   *
   */
  public static Matrix getStringMatrix(int numOfColumns, int numOfRows, String s) {
    Object[] entries = new Object[numOfColumns*numOfRows];
    for(int i=0; i<numOfColumns*numOfRows; i++)
      entries[i] = new String(s);
    return new Matrix(String.class, numOfColumns, entries);
  }
  
  
  private class DefaultFormat extends Format {
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
      return toAppendTo.append(obj.toString());
    }
    
    public Object parseObject (String source, ParsePosition status) {
      return new String(source);
    }
  }
  
  protected Format format = new DefaultFormat();
  
  private int m_numOfCols;
  private Class m_entryclass;
  
  protected Object[] m_entries;//protected is necessary because we want to use
  // the array as argument for det-routine in
  // number matrix.
  
  /**
   * Creates a new nxm Matrix. All entries are checked to be a
   * <code>entryClass</code> (or null). The <code>numOfCols</code> specifies the
   * width of the matrix. The <code>entries</code> array must have a length that
   * is a multiple of numOfCols. The number of rows in the matrix is
   * <code>entries.length / numOfCols</code>.
   */
  public Matrix(Class entryclass, int numOfCols, Object[] entries) {
    setEntryClass(entryclass);
    init(numOfCols, entries);
  }
    
  /**
   * reshapes this <code>Matrix</code> to a matrix with number of columns and
   * rows as traversed in the argument if the product <code>numOfRows</code>*
   * <code>numOfColumns</code> equals the total number of entries in this matrix.
   *
   * @param    numOfRows           an int
   * @param    numOfCols           an int
   *
   * @version  10/16/2002
   */
  protected void reshape(int numOfRows, int numOfCols) {
    if( numOfRows*numOfCols == m_entries.length ) {
      m_numOfCols = numOfCols;
      fireDimensionChanged();
    } else
      throw new IllegalArgumentException("matrix has current dimension ncols="+getColumnCount()+
                                         ", nrows="+getRowCount()+" and cannot be reshaped to a matrix"+
                                         " of size ("+numOfCols+", "+numOfRows+")");
  }
  
  protected void setNewSize(int numOfRows, int numOfCols) {
    Object[] newEntries = new Object[numOfCols*numOfRows];   
    for (int row = 1; row <= Math.min(numOfRows, getRowCount()); row++) {
      System.arraycopy(m_entries, (getColumnCount())*(row-1), newEntries, numOfCols*(row-1), Math.min(numOfCols, getColumnCount()));
    }
    m_entries = newEntries; 
    m_numOfCols = numOfCols;
  }
  /**
   * resizes the matrix to <code>numOfRows</code>x <code>numOfCols</code>. Pay
   * attention: some entries may be lost (num of colums or rows decreased) 
   * @param numOfRows
   * @param numOfCols
   */
  public void resize(int numOfRows, int numOfCols) {
    setNewSize(numOfRows, numOfCols);
    fireDimensionChanged();
  }
  
  /**
   * This method should check validity of the entire matrix. In
   * <code>Matrix</code> implementation <code>checkEntry</code> is called for
   * all entries.
   */
  protected void check() {
    for (int i = 1; i <= getRowCount(); i++) {
      for (int j = 1; j <= getColumnCount(); j++) {
         checkEntry(getEntryRef(i, j));
      }
    }
  }
  
  /**
   * Should check validity of a matrix entry m_ij. By default the entry may be
   * <code>null</code> or a subclass of entrieClass (see
   * <code>getEntryClass</code>).
   */
  protected void checkEntry(Object entry) {
    if (entry == null)
      return;
    if(!m_entryclass.isAssignableFrom(entry.getClass())&&
    		!(MOpNumber.class.isAssignableFrom(m_entryclass)&&MOpNumber.class.isAssignableFrom(entry.getClass())))
      throw new IllegalArgumentException("entry has to be a " + m_entryclass +
          " but is a " + entry.getClass());
  }
  
  /**
   * initializes the matrix with numOfCols und entries. Neither the content of
   * entries nor entries itself is copied. entries must bei not <code>null</code>
   *  After setting <code>check</code> is called to ensure that all entries are
   * valid.
   */
  protected void init(int numOfCols, Object[] entries) {
    if (entries == null)
      throw new IllegalArgumentException("entries must not be null");
    m_numOfCols = numOfCols;
    m_entries = entries; //deep copy impossible on objects
    check();
  }
  
  /**
   * changes the entryClass. After that <code>check</code> is called to ensure
   * that all matrix entries are valid according to the new entryClass.
   */
  protected void setEntryClass(Class entryclass) {
    m_entryclass = entryclass;
    check(); //not neccessary when m_entryClass is a subclass of entryclass
  }
  
  /**
   * returns the current <code>entryClass</code>. This class determines what
   * type is allowed for the matrix entries. A typical entryClass would be
   * String. This ensures that alle entries are strings (or a subclass of it,
   * when the entryclass is not final). So you can simply cast the result of
   * getEntry to entryClass without an exception.<p>
   * For matrizes of numbers there are special classes, which are able to handle
   * things specific for matrizes of numbers such as multiplication, addition,
   * inverse and so on.
   */
  public Class getEntryClass() {
    return m_entryclass;
  }
  
  /**
   * Determines the row that corrospond to a index in the matrix entries. All
   * entries are aligned in a flat array, row by row from upper left to lower
   * right.<p>
   * The resulting row index is <strong>one</strong>based.
   */
  protected int getRowFromIndex(int index) {
    return index / m_numOfCols + 1;
  }
  
  
  /**
   * Determines the column that corresponds to an index in the matrix entries. All
   * entries are aligned in a flat array, row by row from upper left to lower
   * right.<p>
   * The resulting column index is <strong>one</strong>based.
   */
  protected int getColumnFromIndex(int index) {
    return index % m_numOfCols + 1;
  }
  
  /**
   * Returns the array index of matrix element (i,j) at row i and column j. row
   * and column are <strong>one</strong>-based. The resulting array index is
   * <strong>zero</strong>-based.
   */
  protected int getIndexFromEntry(int row, int column) {
    return (row-1)*m_numOfCols + (column - 1);
  }
  
  /**
   * Returns the array index of matrix element (i,j) at row i and column j. row
   * and column are <strong>one</strong>-based. The resulting array index is
   * <strong>zero</strong>-based.
   */
  public static int getIndexFromEntry(int row, int column, int numOfCols) {
		return (row-1)*numOfCols + (column - 1);
  }
  
  /**
   * returns the number of rows of the matrix.
   */
  public int getRowCount() {
    return m_numOfCols == 0 ? 0 : m_entries.length / m_numOfCols;
  }
  
  /**
   * returns the number of columns of the matrix.
   */
  public int getColumnCount() {
    return m_numOfCols;
  }
  
  /**
   * Method getDimension
   * If this matrix is a square matrix the method returns the number of
   * columns which coincides with the number of rows.
   *
   * @return   an int
   *
   * @version  8/6/2002
   */
  public int getDimension() {
    if(isSquare())
      return getColumnCount();
    else
      throw new IllegalUsageException("method can only be called for square matrices");
  }
  
  /**
   * return true iff the row <strong>and</strong> column length of
   * <code>aMatrix</code> are equal the <code>this</code> matrix otherwise false,
   */
  public boolean hasSameShape(Matrix aMatrix){
    return getColumnCount()==aMatrix.getColumnCount() && getRowCount()==aMatrix.getRowCount();
  }
  
  /**
   * returns true iff the matrix is square that means the number of rows
   * columns are equal.
   */
  public boolean isSquare() {
    return getRowCount() == getColumnCount();
  }
    
  /**
   * returns a view of the matrix entry at the specified row and column. That
   * means the reference is returned.
   * There is no way to get a copy of the entry because this behaviour is not
   * typical for <em>all</em> objects.
   */
  public Object getEntryRef(int row, int column) {
    return m_entries[getIndexFromEntry(row, column)];
  }
  
  /**
   * sets the value
   */
  public void setEntryRef(int row, int column, Object value) {
    checkEntry(value);
    m_entries[getIndexFromEntry(row, column)] = value;
  }
  
  public boolean equals(Object shouldBeAMatrix) {
    if(!(shouldBeAMatrix instanceof Matrix))
      return false;
    Matrix otherMatrix = (Matrix)shouldBeAMatrix;
    if( hasSameShape(otherMatrix) ) {
      for (int i=0; i<m_entries.length; i++){
        if( ! m_entries[i].equals(otherMatrix.m_entries[i]) )
          return false;
      }
      return true;
    }
    else
      return false;
  }
  
  public Format getFormater() {
    return format;
  }
  
  public void setFormater(Format format) {
    this.format = format;
  }
  
  private int getColumnSize(int column) {
    int result = 0;
    Format formater = getFormater();
    for (int i = 1; i <= getRowCount(); i++) {
      //      result = Math.max(formater.format(new Object[] {getEntryView(i, column)}).toString().length(), result);
      result = Math.max(formater.format(getEntryRef(i, column)).toString().length(), result);
    }
    return result + 1;
  }
  
  private class IntegerContainer {
    private int value;
    public IntegerContainer(int value) { setValue(value); }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
  }
  
  private int[] getColumnSizes(IntegerContainer size) {
    int[] result = new int[getColumnCount()];
    size.setValue(0);
    for (int i = 1; i <= getColumnCount(); i++) {
      result[i-1] = getColumnSize(i);
      size.setValue(size.getValue() + result[i-1]);
    }
    return result;
  }
  
  public String mathematicaRepresentation() {
    if ((getRowCount() == 0) || (getColumnCount() == 0)) {
      return "()";
    }
    StringBuffer result  = new StringBuffer();
    result.append("{");
    for (int r = 1; r <= getRowCount(); r++) {
      result.append("{");
      for (int c = 1; c <= getColumnCount(); c++) {
        result.append(getEntryRef(r, c).toString());
        if (c < getColumnCount())
          result.append(",");
      }
      if (r < getRowCount())
        result.append(",");
    }
    result.append("}");
    return result.toString();
  }
  
  public String toString() {
    if ((getRowCount() == 0) || (getColumnCount() == 0)) {
      return "()";
    }
    
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < 80; i++)
      buffer.append(' ');
    buffer.append("\n");
    char[] row = buffer.toString().toCharArray();
    int numRows = 2 * getRowCount() + 1;
    char[] matrix = new char[row.length * numRows];
    for (int i = 1; i <= numRows; i++) {
      System.arraycopy(row, 0, matrix, (i-1)*row.length, row.length);
    }
    
    IntegerContainer size = new IntegerContainer(0);
    int[] columnSizes = getColumnSizes(size);
    if (size.getValue() > 77) {
      return mathematicaRepresentation();
    }
    
    Format formater = getFormater();
    
    for (int r = 1; r <= numRows; r++) {
      int rowOffset = (r - 1)*row.length;
      if (r % 2 == 0) {
        int columnOffset = 1;
        matrix[rowOffset] = '|';
        for (int c = 1; c <= getColumnCount(); c++) {
          columnOffset += columnSizes[c-1];
          //          char[] val = formater.format(new Object[] {getEntryView(r / 2, c)}).toCharArray();
          char[] val = formater.format(getEntryRef(r / 2, c)).toCharArray();
          System.arraycopy(val, 0, matrix, rowOffset + columnOffset - val.length, val.length);
        }
        matrix[rowOffset + size.getValue() + 2] = '|';
      } else {
        if (r == 1) {
          matrix[rowOffset+1] = '/';
          matrix[rowOffset + size.getValue() + 1] = '\\';
        } else {
          if (r == numRows) {
            matrix[rowOffset+1] = '\\';
            matrix[rowOffset + size.getValue() + 1] = '/';
          } else {
            matrix[rowOffset] = '|';
            matrix[rowOffset + size.getValue() + 2] = '|';
          }
        }
      }
      
    }
    return new String(matrix);
  }
  
  public Matrix transpose() {
    Object[] oldEntries = new Object[m_entries.length];
    for(int i=0;i<m_entries.length;i++)
      oldEntries[i] = m_entries[i];
    Matrix original = new Matrix(getEntryClass(), m_numOfCols, oldEntries);
    m_numOfCols = getRowCount();
    for (int i = 1; i <= getRowCount(); i++) 
      for (int j = 1; j <= getColumnCount(); j++){
        //System.out.println("orig("+j+","+i+") = "+original.getEntryRef(j, i));
        //System.out.println("this("+i+","+j+") = "+getEntryRef(i, j));
        setEntryRef(i, j, original.getEntryRef(j, i));
      }
    check();
    return this;
  }
  
  public Matrix getSubMatrixView(Class matrixClass, int row1, int col1, int row2, int col2){
    return getSubMatrixView(matrixClass, row1, col1, row2, col2, 1, 1);
  }
  
  /**
   *  Side effect:
   *  does create a link connection.
   */
  private Matrix getSubMatrixView(Class matrixClass, int row1, int col1, int row2, int col2,
      int offsetRow, int offsetCol){
    if (!Matrix.class.isAssignableFrom(matrixClass))
      throw new IllegalArgumentException("matrixClass has to be a Matrix or subclass of it");
    // returns a view of the submatrix
    int mr = ((row2-row1)/offsetRow);
    int mc = ((col2-col1)/offsetCol);
    Object[] tmp = new Object[mr*mc];
    try {
      Constructor constructor = matrixClass.getConstructor(new Class[] {Class.class, int.class, Object[].class});
      Matrix res =  (Matrix)constructor.newInstance(new Object[] {getEntryClass(), new Integer(mc), tmp});
      for (int ir=0; ir<mr; ++ir){
        for (int jc=0; jc<mc; ++jc){
          res.setEntryRef(ir+1,jc+1,getEntryRef(
                           row1+ir*offsetRow, col1+jc*offsetCol
                         )
            );
        }
      }
      return res;
    } catch (NoSuchMethodException e) {
      logger.severe("getSubMatrixView():: needs a constructor that takes a number class, the number of columns and a object array");
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public void addDimensionChangeListener(ChangeListener listener) {
    changeListeners.add(listener);
  }
  
  public void removeDimensionChangeListener(ChangeListener listener) {
    changeListeners.remove(listener);
  }
  
  protected void fireDimensionChanged() {
    ChangeEvent event = new ChangeEvent(this);
    for (Iterator iter = changeListeners.iterator(); iter.hasNext();) {
      ChangeListener listener = (ChangeListener) iter.next();
      listener.stateChanged(event);
    }
  }
  
  public void setMathMLNode(Node xmlNode, int row, int column) {
  	Object entry = getEntryRef(row, column);
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
  
  public Node getMathMLNode() {
    return getMathMLNode(XMLUtils.getDefaultDocument());
  }

  public Node getMathMLNode(Document doc) {
		Element mtable = (this instanceof ExerciseObjectIF) ? 
				ExerciseObjectFactory.createNode((ExerciseObjectIF) this, "mtable", doc)
				: doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mtable");
		mtable.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
		mtable.setAttribute("class", "bmatrix");

		for(int r = 1; r <= getRowCount(); r++) {
			mtable.appendChild(doc.createTextNode("\n  "));
			Node mtr = mtable.appendChild(doc.createElement("mtr"));
			for(int c = 1; c <= getColumnCount(); c++) {
				mtr.appendChild(doc.createTextNode("\n    "));
				Node mtd = mtr.appendChild(doc.createElement("mtd"));
				mtd.appendChild(doc.createTextNode("\n      "));
				Object entry = getEntryRef(r, c);
				if(entry instanceof MathMLSerializable)
					mtd.appendChild(((MathMLSerializable)getEntryRef(r, c)).getMathMLNode(doc));
				else
					throw new XMLParsingException("Entry does not implement to write XML content!");
				mtd.appendChild(doc.createTextNode("\n    "));
			}
			mtr.appendChild(doc.createTextNode("\n  "));
		}
		mtable.appendChild(doc.createTextNode("\n"));

    return mtable;
  }

  public void setMathMLNode(Node matrixNode) {
    if(!matrixNode.getNodeName().equalsIgnoreCase("mtable"))
      throw new XMLParsingException("Node name must be \"<mtable>\"!", matrixNode);
    
    setNewSize(1, 1);
    int rowPos = 1;
    int rowIndex = 0;
    while((rowIndex = XMLUtils.getNextNonTextNodeIndex(matrixNode, rowIndex)) > -1) {
      Node rowNode = matrixNode.getChildNodes().item(rowIndex);
     	if(!rowNode.getNodeName().equalsIgnoreCase("mtr"))
     		throw new XMLParsingException("Node name must be \"<mtr>\"!", rowNode);
     	int colIndex = 0;
    	int colPos = 1;
    	while((colIndex = XMLUtils.getNextNonTextNodeIndex(rowNode, colIndex)) > -1) {
     	Node colNode = rowNode.getChildNodes().item(colIndex);
     	if(!colNode.getNodeName().equalsIgnoreCase("mtd"))
     		throw new XMLParsingException("Node name must be \"<mtd>\"!", colNode);
   		if(rowPos > getRowCount())
    			setNewSize(rowPos, getColumnCount());
   		if(colPos > getColumnCount())
  			setNewSize(getRowCount(), colPos);
   		if(colNode != null)
      	setMathMLNode(XMLUtils.getNextNonTextNode(colNode, 1), rowPos, colPos);
   		else
   			setMathMLNode(null, rowPos, colPos);
    	colIndex++;
    	colPos++;
    	}
    	rowIndex++;
    	rowPos++;
    }
    if(this instanceof ExerciseObjectIF)
    	ExerciseObjectFactory.importExerciseAttributes(matrixNode, (ExerciseObjectIF) this);
    fireDimensionChanged();
  }
}

