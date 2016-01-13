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
import java.awt.Dimension;
import java.awt.Font;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.display.layout.MatrixLayout;
import net.mumie.mathletfactory.math.algebra.linalg.Matrix;

/**
 * This class implements the symbolic representation of a matrix as a panel.
 *  
 * @author Gronau 
 * @mm.docstatus finished
 */
public class StringMatrixPanel extends JPanel {

	/** Field with the current number of rows and columns. */
	private int m_rows, m_columns;

	/** Flag indicating the presence of a right side. */
	private boolean m_hasRightSide = false;

	/** The place where the internally stored string array is located. */
	private String[] m_entries;
	
	private JLabel[] m_entryLabels;

//	private String[] m_rightSideEntries;

	/** Panels for the left and the right side. */
	private JPanel m_drawablesPanel,	m_inhomogeneityPanel;

	Logger logger = Logger.getLogger(StringMatrixPanel.class.getName());

	/**
	 * Initializes a matrix-panel for a given array of Strings.
	 */
	public StringMatrixPanel(String[] entries, int rows, int columns) {
		super(new BorderLayout());
	  m_entries = entries;
    m_entryLabels = new JLabel[entries.length];
    setEntries(m_entries);
		m_drawablesPanel = new JPanel();
		setColumnsAndRows(columns, rows);
		m_drawablesPanel.setLayout(new MatrixLayout(rows, columns));
    m_drawablesPanel.setBorder(new LineBorder(Color.BLACK));
		m_drawablesPanel.setBorder(new MatrixBorder(m_drawablesPanel, MatrixBorder.PARENTHESES));
    setFont(getFont().deriveFont(Font.PLAIN));
		add(m_drawablesPanel, BorderLayout.CENTER);
		m_inhomogeneityPanel = new JPanel();
	}

  /**
	 * Initializes a matrix-panel for a given array of Strings.
   * @param borderType
   *          the type of matrix border to be used 
   * @see MatrixBorder
   */
  public StringMatrixPanel(String[] entries, int rows, int columns, int borderType) {
    this(entries, rows, columns);
    setBorderType(borderType);    
  }

  /**
	 * Initializes a matrix-panel for a given array of Strings.
   * @param borderType
   *          the type of matrix border to be used 
   * @param colors
   *          the colors to be used as foreground color for the entries 
   * @see MatrixBorder
   */
  public StringMatrixPanel(String[] entries, Color[] colors, int rows, int columns, int borderType) {
    this(entries, rows, columns, borderType);
    setColors(colors);        
  }

	/**
	 * Convenience method for square matrices.
	 * 
   * @param entries
   *          the strings to be displayed
	 * @param dimension
	 *          square dimension of the matrix
	 */
	public StringMatrixPanel(String[] entries,	int dimension) {
		this(entries, dimension, dimension);
	}


	public void setInhomogeneity(String[] rightSide) {
		for (int i = 0; i < rightSide.length; i++) {
		  m_drawablesPanel.add(new JLabel(rightSide[i]), i);
		}
    m_hasRightSide = true;
	}

	public void hasRightSide(boolean show) {
		if (show == m_hasRightSide)
			return;
		m_hasRightSide = show;
		if (show) {
			add(m_inhomogeneityPanel, BorderLayout.EAST);
		} else {
			remove(m_inhomogeneityPanel);
		}
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
		if(c == null)
			c = getBackground();
		((JComponent) m_drawablesPanel.getComponent(getIndexFromPosition(row, col))).setBorder(new LineBorder(c, 2));
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
	
	public String getEntry(int row, int col) {
	  return m_entries[getIndexFromPosition(row, col)];
	}
	
	public void setEntries(String[] entries) {
	  m_entries = entries;
	  m_entryLabels = new JLabel[m_entries.length];
	  for(int i = 0; i < m_entryLabels.length; i++) {
	    m_entryLabels[i] = new JLabel("<html>" + m_entries[i] + "</html>");
	  }
	}

  /** Sets the given preferred height (of each cell) for all cells of the string matrix. */
  public void setCellHeight(int height){
    for(int i=0;i<m_entryLabels.length;i++){
      Dimension dim = m_entryLabels[i].getPreferredSize();
      dim.height = height;
      m_entryLabels[i].setPreferredSize(dim);
      m_entryLabels[i].setSize(dim);
    }
    revalidate();
  }

  /** Sets the given preferred width (of each cell) for all cells of the string matrix. */
  public void setCellWidth(int width){
    for(int i=0;i<m_entryLabels.length;i++){
      Dimension dim = m_entryLabels[i].getPreferredSize();
      dim.width = width;
      m_entryLabels[i].setPreferredSize(dim);
      m_entryLabels[i].setSize(dim);
    }    
    revalidate();
  }

  /** Returns the given preferred height (of each cell) which is equal for all cells of the string matrix. */
  public int getCellHeight(){
    return (int)m_entryLabels[0].getPreferredSize().getHeight();
  }

  /** Returns the given preferred width (of each cell) which is equal for all cells of the string matrix. */
  public int getCellWidth(){
    return (int)m_entryLabels[0].getPreferredSize().getWidth();
  }

  /** Sets the given alignment for all cells of the string matrix. */
  public void setHorizontalCellAlignment(int type){
    for(int i=0;i<m_entryLabels.length;i++){
      m_entryLabels[i].setHorizontalAlignment(type);
    }
    revalidate();        
  }

  /** Sets the given alignment for all cells of the string matrix. */
  public void setVerticalCellAlignment(int type){
    for(int i=0;i<m_entryLabels.length;i++){
      m_entryLabels[i].setVerticalAlignment(type);
    }        
    revalidate();
  }

  /** Sets the given foreground color for the cell at the chosen row and colum index. */
  public void setCellForeground(int row, int col, Color color){
    m_entryLabels[getIndexFromPosition(row, col)].setForeground(color);
    repaint();        
  }

  /** Sets the given foreground color for all cells of the string matrix. */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_entryLabels == null)
      return;
    for(int i=0;i<m_entryLabels.length;i++){
      m_entryLabels[i].setForeground(color);
    }        
    repaint();
  }  

  /** 
   * Sets the given foreground color for this panel (This can be overridden for single cells 
   * by {@link #markEntry}). 
   */
  public void setBackground(Color color){
    super.setBackground(color);
    if(m_drawablesPanel == null)
      return;
//    m_drawablesPanel.setBackground(color);
//    repaint();
  }  

  public void setFont(Font font){
    super.setFont(font);
    if(m_entryLabels != null)
    for(int i=0;i<m_entryLabels.length;i++){
      m_entryLabels[i].setFont(font);
    }        
    repaint();
  }

  /**
   * Sets the given foreground colors for the cells of the string matrix. The length of the given 
   * array must equal the length of the entries array.
   */
  public void setColors(Color[] colors){
    for(int i=0;i<colors.length;i++)
      m_entryLabels[i].setForeground(colors[i]);
    repaint();
  }

  /** Sets the layout of the matrix to the given column and row count. */
	public void setColumnsAndRows(int columns, int rows) {
		if (m_entryLabels != null) {
			m_drawablesPanel.setLayout(new MatrixLayout(rows, columns));
			m_drawablesPanel.removeAll();
			for (int i = 0; i<m_entryLabels.length;i++) {         
			  m_drawablesPanel.add(m_entryLabels[i]);
        //m_entries[i].setBorder(new LineBorder(Color.BLACK));
			}
		}
		m_columns = columns;
		m_rows = rows;
    revalidate();
	}
}
