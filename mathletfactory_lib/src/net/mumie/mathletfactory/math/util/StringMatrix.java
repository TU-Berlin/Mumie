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

package net.mumie.mathletfactory.math.util;

import net.mumie.mathletfactory.action.message.MathContentException;
import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.util.xml.ExerciseCompoundObjectIF;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents an arbitrary mxn matrix with Strings as cell content.
 * 
 * @author gronau
 */
public class StringMatrix implements ExerciseCompoundObjectIF {

	public final static String COLUMN_VECTOR_TYPE = "column-vector";
	public final static String ROW_VECTOR_TYPE = "row-vector";
	
	private int m_rows, m_cols;

	private MString[] m_values;

	private MString[] m_rightSideValues;

	private boolean m_hasRightSide;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;
	
	public StringMatrix(int numOfRows, int numOfCols) {
		m_rows = numOfRows;
		m_cols = numOfCols;
		m_values = new MString[numOfRows * numOfCols];
		m_rightSideValues = new MString[numOfRows];
		for (int r = 1; r <= numOfRows; r++) {
			m_rightSideValues[r - 1] = new MString();
			for (int c = 1; c <= numOfCols; c++) {
				m_values[Matrix.getIndexFromEntry(r, c, m_cols)] = new MString();
			}
		}
	}

	public StringMatrix(int numOfCols, String[] entries) {
		if (numOfCols == 0)
			throw new IllegalArgumentException(
					"Number of columns must be greater than 0.");
		int rows = entries.length / numOfCols;
		if (entries.length % numOfCols != 0)
			throw new IllegalArgumentException(
					"Length of array not compatible with number of columns.");
		m_rows = rows;
		m_cols = numOfCols;
		m_values = new MString[entries.length];
		m_rightSideValues = new MString[rows];
		for (int r = 1; r <= rows; r++) {
			m_rightSideValues[r - 1] = new MString();
			for (int c = 1; c <= numOfCols; c++) {
				m_values[Matrix.getIndexFromEntry(r, c, m_cols)] = new MString(
						entries[Matrix.getIndexFromEntry(r, c, numOfCols)]);
			}
		}
	}

	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param content a MathML/XML node
	 */
	public StringMatrix(Node content) {
		m_rows = 0;
		m_cols = 0;
		setMathMLNode(content);
	}
	
	public void setEntry(int row, int col, String entry) {
		m_values[Matrix.getIndexFromEntry(row, col, m_cols)].setValue(entry);
	}

	public void setEntries(String[] values) {
		for (int i = 1; i <= m_values.length; i++) {
			m_values[i - 1].setValue(values[i - 1]);
		}
	}

	public void setEntries(MString[] values) {
		for (int i = 1; i <= m_values.length; i++) {
			m_values[i - 1].setValue(values[i - 1].getValue());
		}
	}

	/**
	 * Returns the entry with the specified coordinates inside the 1-indexed
	 * matrix.
	 */
	public MString getEntry(int row, int col) {
		return m_values[Matrix.getIndexFromEntry(row, col, m_cols)];
	}

	public void setRightSideEntry(int row, String entry) {
		if (!m_hasRightSide)
			throw new MathContentException(
					"Can not set right side entry since this matrix has no right side.");
		m_rightSideValues[row - 1].setValue(entry);
	}

	public MString getRightSideEntry(int row) {
		return m_rightSideValues[row - 1];
	}

	public int getRowCount() {
		return m_rows;
	}

	public int getColumnCount() {
		return m_cols;
	}

	public String toString() {
		if ((getRowCount() == 0) || (getColumnCount() == 0)) {
			return "()";
		}
		String s = "";
		for (int r = 1; r <= getRowCount(); r++) {
			s += "( ";
			for (int c = 1; c <= getColumnCount(); c++) {
				s += getEntry(r, c).getValue();
				if (c <= getColumnCount() - 1)
					s += "  ";
			}
			s += " )\n";
		}
		return s;
	}

	public boolean equals(Object stringMatrix) {
		if (!(stringMatrix instanceof StringMatrix))
			throw new IllegalArgumentException(
					"Object to compare must be a StringMatrix!");
		StringMatrix m = (StringMatrix) stringMatrix;
		for (int i = 0; i < m_values.length; i++) {
			if (!m_values[i].getValue().trim().equals(
					m.m_values[i].getValue().trim()))
				return false;
		}
		if (m_hasRightSide) {
			if (!m.m_hasRightSide)
				return false;
			for (int i = 0; i < m_rightSideValues.length; i++) {
				if (!m_rightSideValues[i].getValue().trim().equals(
						m.m_rightSideValues[i].getValue().trim()))
					return false;
			}
		} else if (m.m_hasRightSide)
			return false;
		return true;
	}

	public Node getMathMLNode() {
		return getMathMLNode(XMLUtils.getDefaultDocument());
	}

	public Node getMathMLNode(Document doc) {
		if(isColumnVector() || isRowVector()) { // matrix is a vector!
	    Element mrow = ExerciseObjectFactory.createNode(this, "mrow", doc);
	    mrow.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
	    if(isColumnVector())
	    	mrow.setAttribute("class", COLUMN_VECTOR_TYPE);
	    else
	    	mrow.setAttribute("class", ROW_VECTOR_TYPE);

	    int dim = isColumnVector() ? getRowCount(): getColumnCount();
	    for(int d = 1; d <= dim; d++) {
	      mrow.appendChild(doc.createTextNode("\n  "));
	      int r = isColumnVector() ? d : 1;
	      int c = isColumnVector() ? 1 : d;
	      mrow.appendChild(getEntry(r, c).getMathMLNode(doc));
	    }
	    mrow.appendChild(doc.createTextNode("\n"));
	    return mrow;
		} else {
			Element mtable = ExerciseObjectFactory.createNode(this, "mtable", doc);
			mtable.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
			mtable.setAttribute("class", "bmatrix");
	
			for (int r = 1; r <= getRowCount(); r++) {
				mtable.appendChild(doc.createTextNode("\n  "));
				Node mtr = mtable.appendChild(doc.createElement("mtr"));
				for (int c = 1; c <= getColumnCount(); c++) {
					mtr.appendChild(doc.createTextNode("\n    "));
					Node mtd = mtr.appendChild(doc.createElement("mtd"));
					mtd.appendChild(getEntry(r, c).getMathMLNode(doc));
				}
				mtr.appendChild(doc.createTextNode("\n  "));
			}
			mtable.appendChild(doc.createTextNode("\n"));
	
			return mtable;
		}
	}

  public void setMathMLNode(Node matrixNode) {
  	if(matrixNode.getNodeName().equalsIgnoreCase("mtable")) {
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
		    	setMathMLNode(colNode.getChildNodes().item(0), rowPos, colPos);
		    	colIndex++;
		    	colPos++;
	    	}
	    	rowIndex++;
	    	rowPos++;
	    }
  	} else if(matrixNode.getNodeName().equalsIgnoreCase("mrow")) {
      setNewSize(0, 0);
      NodeList mnList = matrixNode.getChildNodes();
      for (int entry = 0; entry < mnList.getLength(); entry++) {
        Node numberEntry = mnList.item(entry);
        if(numberEntry.getNodeType() == Node.TEXT_NODE)
          continue;
        if(isColumnVector()) {
      	  setNewSize(getRowCount()+1, 1);
          setMathMLNode(numberEntry, getRowCount(), 1);
        } else { // is row vector!
      	  setNewSize(1, getColumnCount()+1);
          setMathMLNode(numberEntry, 1, getColumnCount());
        }
      }
  	} else
      throw new XMLParsingException("Node name must be \"<mtable>\"!", matrixNode);
    ExerciseObjectFactory.importExerciseAttributes(matrixNode, this);
//    fireDimensionChanged();
  }
  
  public void setMathMLNode(Node xmlNode, int row, int column) {
  	Object entry = getEntry(row, column);
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

	public void resize(int numOfRows, int numOfCols) {
		setNewSize(numOfRows, numOfCols);
	}

	protected void setNewSize(int numOfRows, int numOfCols) {
		MString[] newEntries = new MString[numOfCols * numOfRows];
		for (int r = 1; r <= numOfRows; r++) {
			for (int c = 1; c <= numOfCols; c++) {
				if (r <= m_rows && c <= m_cols)
					newEntries[Matrix.getIndexFromEntry(r, c, numOfCols)] = m_values[Matrix
							.getIndexFromEntry(r, c, m_cols)];
				else
					newEntries[Matrix.getIndexFromEntry(r, c, numOfCols)] = new MString();
			}
		}
		m_values = newEntries;
		m_rows = numOfRows;
		m_cols = numOfCols;
	}

  /**
   * Returns if this vector is a column vector, i.e. a vector whose 
   * number of columns is 1.
   */
  public boolean isColumnVector() {
    return getColumnCount() == 1;
  }

  /**
   * Returns if this vector is a row vector, i.e. a vector whose 
   * number of rows is 1.
   */
  public boolean isRowVector() {
    return getRowCount() == 1;
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
	   */
	  public boolean isEdited(int row, int col) {
		return getEntry(row, col).isEdited();
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

	public void setEdited(boolean edited) {
		for(int row = 1; row <= getRowCount(); row++) {
			for(int col = 1; col <= getColumnCount(); col++) {
				getEntry(row, col).setEdited(edited);
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
		return MString.class;
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}	
}
