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

/*
 * Created on 09.09.2003
 *
 */
package net.mumie.mathletfactory.display.noc.matrix;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import net.mumie.mathletfactory.action.SelectionListener;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.display.noc.symbol.MMStringPanel;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;
import net.mumie.mathletfactory.math.util.MString;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.util.MMString;
import net.mumie.mathletfactory.mmobject.util.MMStringMatrix;
import net.mumie.mathletfactory.mmobject.util.MatrixEntry;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 * This class implements the symbolic representation of a matrix as a panel.
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class MMStringMatrixPanel
  extends MMCompoundPanel
  implements PropertyChangeListener {

  /** Field with the current number of rows and columns. */
  private int m_rows, m_columns;

  private boolean m_hasRightSide = false;

  /** The place where the internally stored number array is located. */
  private MMString[] m_entries;

  private MMStringMatrix m_matrix;

//  /** Contains the old matrix values. For property changes.*/
//  private String[] m_oldMatrix;

  private MMString[] m_rightSideNumbers;

  /** Contains the editable-flags for all entries. */
  private Boolean[] m_entriesEditable;

  /** Panels for the left and the right side. */
  private JPanel m_drawablesPanel = new JPanel(),
    m_inhomogeneityPanel = new JPanel();
  private JPanel m_verticalLineHelperPanel = new JPanel(new BorderLayout());

  Logger logger = Logger.getLogger(MMNumberMatrixPanel.class.getName());

  /**
   * This class is used to render an instance of
   * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMNumberMatrix}.
   * It holds internally an array of MM-numbers which are used to display the
   * number values via the MM-number-drawables.
   *
   * @param master
   *          must be a MMNumberMatrix
   * @param rows
   *          number of rows; it must correspond to this of the master
   * @param columns
   *          number of columns; it must correspond to this of the master
   */
  public MMStringMatrixPanel(MMStringMatrix master,
    ContainerObjectTransformer transformer) {
    super(master, transformer);
    m_matrix = master;
    m_drawablesPanel.setLayout(new MatrixLayout(master.getRowCount(), master.getColumnCount()));
    setColumnsAndRows(master.getColumnCount(), master.getRowCount());
    m_drawablesPanel.setBorder(new MatrixBorder(m_drawablesPanel, MatrixBorder.BRACKETS));

    m_inhomogeneityPanel.setLayout(new MatrixLayout(master.getRowCount(), 1));

    initialize();
    setForeground(master.getDisplayProperties().getObjectColor());

    getViewerComponent().setLayout(new BorderLayout());
    getViewerComponent().add(m_drawablesPanel, BorderLayout.CENTER);
    m_verticalLineHelperPanel.add(m_inhomogeneityPanel, BorderLayout.CENTER);
    getViewerComponent().add(m_verticalLineHelperPanel, BorderLayout.EAST);
    m_inhomogeneityPanel.setVisible(false);

    addPropertyChangeListener((PropertyHandlerIF)getMaster());
  }

  /*
   * Used to initialize the internally stored number array and the drawables.
   */
  private void initialize() {
    m_entries = new MMString[getIndexFromPosition(m_rows, m_columns) + 1];
    m_entriesEditable = new Boolean[m_entries.length];

    for (int j = 0; j < m_rows; j++) {
      for (int i = 0; i < m_columns; i++) {
        int index = getIndexFromPosition(j + 1, i + 1);
        m_entries[index] = new MMString(m_matrix.getEntry(j+1, i+1).getValue());
        m_entries[index].setDisplayProperties(new DisplayProperties());
        m_entries[index].setEdited(m_matrix.getEntry(j+1, i+1).isEdited());
        MMStringPanel c2 = (MMStringPanel)m_entries[index].getAsContainerContent();
				addMMPanel(c2);
//        c2.setRememberSize(false);
        m_drawablesPanel.add(c2, index);
        c2.addPropertyChangeListener(this);
      }
    }
  }

  /**
   * Sets the values from <code>aNumberMatrix</code> in the table model and
   * repaints the renderers.
   *
   * @param aNumberMatrix
   *          an instance of
   *          {@link net.mumie.mathletfactory.util.math.NumberMatrix}.
   * @see net.mumie.mathletfactory.util.math.NumberMatrix NumberMatrix
   * @exception IllegalArgumentException
   *              if the dimensions of the NumberMatrix and the table model are
   *              not equal.
   */
//  public void setValuesFromStringMatrix(MMStringMatrix matrix) {
//    if (matrix.getColumnCount() != m_columns
//      || matrix.getRowCount() != m_rows)
//      throw new IllegalArgumentException("Dimensions of the NumberMatrix and the drawable's data model are not equal!");
////    m_oldMatrix = aNumberMatrix;
//    for (int j = 1; j <= m_rows; j++) {
//      for (int i = 1; i <= m_columns; i++) {
//        int index = getIndexFromPosition(j, i);
//        MMString mValue = matrix.getEntry(j, i);
//        ((MNumber)m_entries[index]).set(mValue);
////        m_entries[index].setEditable(m_isEditable);
//        m_entries[index].setLabel(null);
//        m_entries[index].render();
//      }
//    }
//    //    m_drawablesPanel.validate(); // useless?
//  }

  /**
   * Sets the values from <code>anOpMatrix</code> in the table model and
   * repaints the renderers.
   *
   * @param anOpMatrix
   *          an instance of
   *          {@link net.mumie.mathletfactory.util.math.algebra.linalg.OpMatrix}.
   * @exception IllegalArgumentException
   *              if the dimensions of the OpMatrix and the table model are
   *              not equal.
   */
//  public void setValuesFromNumberMatrix(OpMatrix anOpMatrix) {
//    if (anOpMatrix.getColumnCount() != m_columns
//      || anOpMatrix.getRowCount() != m_rows)
//      throw new IllegalArgumentException("Dimensions of the NumberMatrix and the drawable's data model are not equal!");
//    m_oldMatrix = anOpMatrix;
//    for (int j = 1; j <= m_rows; j++) {
//      for (int i = 1; i <= m_columns; i++) {
//        int index = getIndexFromPosition(j, i);
//        getEntryPanel(j, i).setOperation(anOpMatrix.getEntryAsOpRef(j, i).deepCopy());
//        ((MNumber)m_entries[index]).set(anOpMatrix.getEntryAsOpRef(j, i).evaluate(new HashMap()));
////        m_entries[index].setEditable(m_isEditable);
//        m_entries[index].setLabel(null);
//        m_entries[index].render();
//      }
//    }
//    //    m_drawablesPanel.validate(); // useless?
//  }

//  public void setInhomogeneity(NumberTuple rightSide) {
//    if(rightSide == null) {
//      hasRightSide(false);
//      return;
//    }
//    if(rightSide.getRowCount() != m_rows)
//      throw new IllegalArgumentException("Row count of the NumberMatrix and the inhomogeneity vector are not equal!");
//    MNumber m = NumberFactory.newInstance(rightSide.getNumberClass());
//    if(m_rightSideNumbers == null)
//      m_rightSideNumbers = new MMObjectIF[rightSide.getRowCount()];
//    for (int i = 0; i < rightSide.getRowCount(); i++) {
//      if (m_rightSideNumbers[i] == null) {
//        m_rightSideNumbers[i] = NumberFactory.getNewMMInstanceFor(m);
//        m_rightSideNumbers[i].setDisplayProperties(new DisplayProperties());
//        JComponent c = m_rightSideNumbers[i].getAsContainerContent();
//        ((MMNumberPanel)c).setRememberSize(false);
//        m_inhomogeneityPanel.add(c);
//        c.addPropertyChangeListener(this);
//      }
//      ((MNumber)m_rightSideNumbers[i]).set(rightSide.getEntry(i + 1));
//      m_rightSideNumbers[i].render();
//    }
//    hasRightSide(true);
//  }

  public void hasRightSide(boolean show) {
    if (show == m_hasRightSide)
      return;
    m_hasRightSide = show;
    m_inhomogeneityPanel.setVisible(show);
    if(show) {
      m_drawablesPanel.setBorder(new MatrixBorder(m_drawablesPanel, getBorderType(), MatrixBorder.ONLY_LEFT_BORDER));
      m_inhomogeneityPanel.setBorder(new MatrixBorder(m_inhomogeneityPanel, getBorderType(), MatrixBorder.ONLY_RIGHT_BORDER));
      m_verticalLineHelperPanel.setBorder(new MatrixBorder(m_verticalLineHelperPanel, MatrixBorder.DETERMINANT, MatrixBorder.ONLY_LEFT_BORDER));
    } else {
      m_drawablesPanel.setBorder(new MatrixBorder(m_drawablesPanel, getBorderType()));
    }
  }

  /**
   * This panel listens to single changes in the number drawables it holds and
   * forwards a new PropertyChange-Event (type: PropertyHandlerIF.NUMBERMATRIX)
   * with the new matrix entries to the master.
   */
  public void propertyChange(PropertyChangeEvent evt) {
    if (!evt.getPropertyName().equals(PropertyHandlerIF.STRING))
      return;
		log("propertyChanged for prop: " + evt.getPropertyName());
    MatrixEntry newValue = null;
    MatrixEntry oldValue = null;
		for (int r = 1; r <= getRows(); r++) {
			for (int c = 1; c <= getColumns(); c++) {
				if(getEntry(r, c) == ((MMStringPanel)evt.getSource()).getMaster()) {
					newValue = new MatrixEntry(evt.getNewValue(), r, c);
					oldValue = new MatrixEntry(evt.getOldValue(), r, c);
					break;
				}
			}
		}
    log("fire string matrix entry changed: old Value: " + oldValue + ", new value: " + newValue);
		firePropertyChange(
      PropertyHandlerIF.MATRIX_ENTRY,
      oldValue,
      newValue);
    String newEntries[] = new String[m_entries.length];
    for(int i = 0; i < m_entries.length; i++) {
      newEntries[i] = new String(m_entries[i].getValue());
    }
//    firePropertyChange(
//      PropertyHandlerIF.STRING_MATRIX,
//      m_oldMatrix,
//      newEntries);
    
//    m_oldMatrix = newEntries;
  }

  /**
   * Returns the stored data in this drawable as a NumberMatrix.
   */
//  public NumberMatrix getValuesAsNumberMatrix() {
//    NumberMatrix result =
//      new NumberMatrix(getMaster().getNumberClass(), getColumns(), getRows());
//    for (int r = 1; r <= getRows(); r++) {
//      for (int c = 1; c <= getColumns(); c++) {
//        MNumber n = (MNumber)m_entries[getIndexFromPosition(r, c)];
//        result.setEntry(r, c, n);
//        // matrix indices are 1-based!
//      }
//    }
//    return result;
//  }

  /**
   * Renders all entry renderers.
   */
  public void render() {
    for (int r = 1; r <= m_rows; r++) {
      if(m_hasRightSide)
        m_rightSideNumbers[r-1].render();
      for (int c = 1; c <= m_columns; c++) {
        int index = getIndexFromPosition(r, c);
        m_entries[index].render();
      }
    }
  }

  /**
   * Passes the values from <code>aMatrix</code> to the number renderers.
   * Differentiation of data types will be made by the <code>entryClass</code>
   * value in the matrix. Strings are parsed as operations which results will
   * be delivered. The String is also used as label for the MM-number that will
   * be displayed instead of the numeric number.
   * MNumbers are just passed.
   * @param aMatrix
   */
  public void setValues(MMStringMatrix aMatrix) {
    if (aMatrix.getColumnCount() != m_columns
      || aMatrix.getRowCount() != m_rows) {
//      throw new IllegalArgumentException("Dimensions of the Matrix and the drawable's data model are not equal!");
    	setColumnsAndRows(aMatrix.getColumnCount(), aMatrix.getRowCount());
    }
    for (int j = 1; j <= m_rows; j++) {
      for (int i = 1; i <= m_columns; i++) {
        int index = getIndexFromPosition(j, i);
        MString entry = aMatrix.getEntry(j, i);
        m_entries[index].setValue(entry.getValue());
        m_entries[index].setEditable(isEditable());
        m_entries[index].setEdited(entry.isEdited());
        m_entries[index].render();
      }
    }
  }

//  /**
//   * Sets the values from <code>aVector</code> to the column <code>column</code>.
//   *
//   * @param aVector
//   *          an instance of net.mumie.mathletfactory.util.math.NumberTuple.
//   * @param column
//   *          a 1-based integer
//   * @see net.mumie.mathletfactory.util.math.NumberTuple NumberTuple
//   * @exception IllegalArgumentException
//   *              if the NumberTuple <code>aVector</code> has a wrong length
//   *              (i.e. different to the table model).
//   */
//  public void setColumnValues(int column, String[] aVector) {
//    if (aVector.length != m_rows)
//      throw new IllegalArgumentException("Dimensions of the String array and the drawable's data model are not equal!");
//    for (int j = 1; j <= m_rows; j++) {
//      int index = getIndexFromPosition(j, column);
//      m_entries[index].setValue(aVector[j]);
////      m_entries[index].setEditable(m_isEditable);
//      m_entries[index].render();
//    }
//    ((MMStringMatrix)m_oldMatrix).setColumnVector(column, aVector);
//  }

  /**
   * Sets whether the matrix drawable should be editable or not. This method
   * passes the editable-flag to all the renderers.
   */
  public void setEditable(boolean editable) {
    if (editable == isEditable())
      return;
    for (int i = 0; i < m_entries.length; i++) {
      if (m_entries[i] != null && m_entriesEditable[i] == null) {
        m_entries[i].setEditable(editable);
        m_entries[i].render();
      }
    }
    super.setEditable(editable);
  }

  /**
   * Sets the custom editable-flag for one cell, <code>null</code> will restore matrix-defaults.
   */
  public void setEditable(int row, int col, Boolean editable) {
    m_entriesEditable[getIndexFromPosition(row, col)] = editable;
    if(editable != null)
      m_entries[getIndexFromPosition(row, col)].setEditable(editable.booleanValue());
    else
      m_entries[getIndexFromPosition(row, col)].setEditable(isEditable());
  }

  /**
   * Returns the DisplayProperties of the renderer specified by its "location".
   * Entries are 1-based and may be null;
   */
  public DisplayProperties getDisplayPropertiesForEntry(int col, int row) {
    return m_entries[getIndexFromPosition(row, col)].getDisplayProperties();
  }

  /**
   * Marks a row with a defined background color. Setting null will restore the
   * default color.
   *
   * @param row
   *          int-Value row is 1-based
   */
  public void markRow(int row, Color c) {
    for (int i = 1; i <= getColumns(); i++)
    	markEntry(row, i, c);
  }

  /**
   * Marks a column with a defined background color. Setting null will restore
   * the default color.
   *
   * @param col
   *          1-based number
   */
  public void markColumn(int col, Color c) {
    for (int i = 1; i <= getRows(); i++)
    	markEntry(i, col, c);
  }

  /**
   * Marks a matrix entry with a defined background color. Setting null will
   * restore the default color.
   *
   * @param col
   *          1-based number
   */
  public void markEntry(int row, int col, Color c) {
    ((MMPanel) m_drawablesPanel.getComponent(getIndexFromPosition(row, col))).setBorderColor(c);
  }

  /**
   * Returns the position index of an entry with the grid-coordinates row and
   * col in this table.
   *
   * @param row
   *          an integer >= 1 and <= the number of rows
   * @param col
   *          an integer >= 1 and <= the number of columns
   *
   * @return int an integer >= 0 and
   *         <= array.length-1, otherwise a negative number
   */
  protected int getIndexFromPosition(int row, int col) {
    return Matrix.getIndexFromEntry(row, col, m_columns);
  }

  /** Returns the border type of this matrix panel (square, determinant style, etc.). */
  public int getBorderType() {
    return ((MatrixBorder)m_drawablesPanel.getBorder()).getBorderType();
  }

  /** Sets the border type of this matrix panel (square, determinant style, etc.). */
  public void setBorderType(int i) {
    ((MatrixBorder)m_drawablesPanel.getBorder()).setBorderType(i);
  }

  /**
   * @return
   */
  public int getColumns() {
    return m_columns;
  }

  /**
   * @return
   */
  public int getRows() {
    return m_rows;
  }

  /**
   * @param i
   */
  public void setColumns(int i) {
    setColumnsAndRows(i, m_rows);
  }

  /**
   * @param i
   */
  public void setRows(int i) {
    setColumnsAndRows(m_columns, i);
  }

  /** Sets the layout of the matrix to the given column and row count. */
  public void setColumnsAndRows(int columns, int rows) {
    if (m_entries != null) {
      m_drawablesPanel.setLayout(new MatrixLayout(rows, columns));
      MMString[] newMatrix =
        new MMString[columns * rows];
      Component[] comps = m_drawablesPanel.getComponents();
      m_drawablesPanel.removeAll();
      for (int r = 0; r < rows; r++) {
        for (int c = 0; c < columns; c++) {
          int i = this.getIndexFromPosition(r + 1, c + 1);
          int j = Matrix.getIndexFromEntry(r + 1, c + 1, columns);
          if (r >= m_rows || c >= m_columns) {
            newMatrix[j] = new MMString();
            newMatrix[j].setDisplayProperties(new DisplayProperties());
            JComponent comp = newMatrix[j].getAsContainerContent();
            m_drawablesPanel.add(comp, j);
            comp.addPropertyChangeListener(this);
            newMatrix[j].setEditable(isEditable());
          } else {
            newMatrix[j] = m_entries[i];
            JComponent comp = (JComponent)comps[i];
            m_drawablesPanel.add(comp, j);
          }
        }
      }
      m_entries = newMatrix;
      m_entriesEditable = new Boolean[m_entries.length];
    }
    m_columns = columns;
    m_rows = rows;
    ((MatrixLayout)(m_drawablesPanel.getLayout())).setDimension(rows, columns);
  }

  /** Returns the maximal height of all cells. */
  public int getMaximumCellHeight(){
    int height = 0;
    Component[] comps = m_drawablesPanel.getComponents();
    for(int i=0;i<comps.length;i++){
      height = Math.max(height, comps[i].getSize().height);
    }
    return height;
  }

  /** Returns the maximal width of all cells. */
  public int getMaximumCellWidth(){
    int width = 0;
    Component[] comps = m_drawablesPanel.getComponents();
    for(int i=0;i<comps.length;i++){
      width = Math.max(width, comps[i].getSize().width);
    }
    return width;
  }


  /** Sets the given height (of each cell) for all cells of the string matrix. */
  public void setCellHeight(int height){
    MatrixLayout ml = (MatrixLayout)m_drawablesPanel.getLayout();
    ml.setFixedRowHeight(height);
    m_drawablesPanel.doLayout();
    m_drawablesPanel.revalidate();
    ml = (MatrixLayout)m_inhomogeneityPanel.getLayout();
    ml.setFixedRowHeight(height);
    m_inhomogeneityPanel.doLayout();
    m_inhomogeneityPanel.revalidate();
    repaint();
  }

  /** Sets the given width (of each cell) for all cells of the string matrix. */
  public void setCellWidth(int width){
    MatrixLayout ml = (MatrixLayout)m_drawablesPanel.getLayout();
    ml.setFixedColumnWidth(width);
    m_drawablesPanel.doLayout();
    m_drawablesPanel.revalidate();
    repaint();
  }
  
  private MMString getEntry(int index) {
  	return m_entries[index];
  }
  
  /**
   * Returns the string entry with the given indices.
   */
  public MMString getEntry(int row, int col) {
  	return getEntry(getIndexFromPosition(row, col));
  }

  /**
   * Returns the string panel of the entry with the given indices.
   */
  public MMStringPanel getEntryPanel(int row, int col){
    return getEntryPanel(getIndexFromPosition(row, col));
  }

  private MMStringPanel getEntryPanel(int index){
    return (MMStringPanel)m_drawablesPanel.getComponent(index);
  }

  /**
   * Delegates the given color to all number entry panels.
   */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_drawablesPanel == null)
      return;
    m_drawablesPanel.setForeground(color);
    if(m_drawablesPanel.getBorder() != null)
      ((MatrixBorder)m_drawablesPanel.getBorder()).setBracketColor(color);
    if(m_entries == null)
      return;
    for(int i=0;i<m_entries.length;i++){
      m_entries[i].getDisplayProperties().setObjectColor(color);
      m_drawablesPanel.getComponent(i).setForeground(color);
    }
    repaint();

  }

  public void setEdited(boolean isEdited) {
    super.setEdited(isEdited);
    for(int i = 0; i < m_drawablesPanel.getComponentCount(); i++) {
      getEntryPanel(i).setEdited(isEdited);
    }
  }

  /**
   * Returns true if at least one entry was edited by the user.
   */
  public boolean isEdited() {
    for(int i = 0; i < m_drawablesPanel.getComponentCount(); i++) {
      if(getEntryPanel(i).isEdited())
        return true;
    }
    return false;
  }

  /**
   * Returns true if all entries were edited by the user.
   */
  public boolean isCompletelyEdited() {
    for(int i = 0; i < m_drawablesPanel.getComponentCount(); i++) {
      if( !getEntryPanel(i).isEdited())
        return false;
    }
    return true;
  }

  public void setBorder(Border border){
    super.setBorder(border);
    if(border instanceof MatrixBorder)
      ((MatrixBorder)border).setBracketColor(getForeground());
  }

  public void addSelectionListener(int row, int col, SelectionListener l) {
    ((MMStringPanel)m_drawablesPanel.getComponent(getIndexFromPosition(row, col))).addSelectionListener(l);
  }

  public void removeSelectionListener(int row, int col, SelectionListener l) {
    ((MMStringPanel)m_drawablesPanel.getComponent(getIndexFromPosition(row, col))).removeSelectionListener(l);
  }

  public void setSelected(int row, int col, boolean selected) {
  ((MMStringPanel)m_drawablesPanel.getComponent(getIndexFromPosition(row, col))).setSelected(selected);
  }

  public void setSelectable(int row, int col, boolean selectable) {
  ((MMStringPanel)m_drawablesPanel.getComponent(getIndexFromPosition(row, col))).setSelectable(selectable);
  }
  
	private void log(String message) {
//			System.out.println("MMStringMatrixPanel: " + message);
	}
}
