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

package net.mumie.mathletfactory.display.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.SwingConstants;


/**
 * This class is used layout out GUI components in a container
 * in a matrix/table-like manner. It can be used as a LayoutManger for every SWING container.
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class MatrixLayout implements LayoutManager {

	public final static int LEFT = SwingConstants.LEFT;
	public final static int RIGHT = SwingConstants.RIGHT;
	public final static int CENTER = SwingConstants.CENTER;
	public final static int TOP = SwingConstants.TOP;
	public final static int BOTTOM = SwingConstants.BOTTOM;
	
	/** Fields containing the dimension of the matrix. */
	private int m_rows, m_columns;

	/** Fields containing dimension informations for each row and column. */
	private int[] m_rowHeights, m_columnWidths;
	
	/** Field containing the horizontal alignment. */
	private int m_horizontalAlignment;
	
	/** Field containing the vertical alignment. */
	private int m_verticalAlignment;
	
	/** Field containing the horizontal gap between components. */
	private int m_hGap = 2;
	
	/** Field containing the vertical gap between components. */
	private int m_vGap = 2;

  /**
   * If the fixed value is set != 0, this overrides the specific rowHeights or columnWidths (needed for
   * controlling the exact graphical dimensions of the panel).
   */
  private int m_fixedRowHeight = 0, m_fixedColumnWidth = 0;

	/**
	 * Constructs a new MatrixLayout with given dimension and center alignment.
	 */
	public MatrixLayout(int rows, int columns) {
		this(rows, columns, CENTER, CENTER);
	}
	/**
	 * Constructs a new MatrixLayout with given dimension and alignment.
	 */
	public MatrixLayout(int rows, int columns, int horizontalAlignment, int verticalAlignment) {
		m_horizontalAlignment = horizontalAlignment;
		m_verticalAlignment = verticalAlignment;
		setDimension(rows, columns);
	}

	/**
	 * Sets the dimension of this matrix layout.
	 * 
	 * @param rows number of rows; must be >= 0
	 * @param columns number of columns; must be >= 0
	 */
	public void setDimension(int rows, int columns) {
		if(rows < 0)
			throw new IllegalArgumentException("Number of rows must be >= 0 !");
		m_rows = rows;
		if(columns < 0)
			throw new IllegalArgumentException("Number of columns must be >= 0 !");
		m_columns = columns;
		// reset internal fields for row heights and column widths
		m_rowHeights = new int[m_rows];
		m_columnWidths = new int[m_columns];
	}

	/**
	 * Adjust the internal fields for row heights and column widths
	 * according to fixed row heights and column widths.
	 */
	private void updateSizes(Container parent) {
    if (parent.getComponentCount() == 0)
      return;

		for (int i = 0; i < m_rowHeights.length; i++)
			m_rowHeights[i] = m_fixedRowHeight;
		for (int i = 0; i < m_columnWidths.length; i++)
			m_columnWidths[i] = m_fixedColumnWidth;

		for (int i = 0; i < m_columns; i++) {
			for (int j = 0; j < m_rows; j++) {
				if(getIndexFromPosition(j, i) >= parent.getComponentCount())
					continue;
				int rowHeight = 0, colWidth = 0;
				int newRowHeight =
					parent
						.getComponent(getIndexFromPosition(j, i))
						.getPreferredSize()
						.height;
        if(m_fixedRowHeight != 0)
          m_rowHeights[j] = m_fixedRowHeight;
        else if (newRowHeight > m_rowHeights[j])
					  m_rowHeights[j] = newRowHeight;

				int newColWidth =
					parent
						.getComponent(getIndexFromPosition(j, i))
						.getPreferredSize()
						.width;
        if(m_fixedColumnWidth != 0)
          m_columnWidths[i] = m_fixedColumnWidth;
        else if (newColWidth > m_columnWidths[i] )
					m_columnWidths[i] = newColWidth;
			}
		}
	}

	public void layoutContainer(Container parent) {
		updateSizes(parent);
		if (parent.getComponentCount() == 0)
			return;

		int leftMargin = 0, topMargin = 0;
		int width = 0, height = 0;
		for (int c = 0; c < m_columns; c++)
			width += m_columnWidths[c] + (c < m_columns-1 ? m_hGap : 0);
		for (int r = 0; r < m_rows; r++)
			height += m_rowHeights[r] + (r < m_rows-1 ? m_vGap : 0);
		
		if(m_horizontalAlignment == LEFT) {
			leftMargin = parent.getInsets().left;
		} else if(m_horizontalAlignment == RIGHT) {
			leftMargin = parent.getWidth() - parent.getInsets().right - width;
		} else { // center
			leftMargin = (int) (parent.getWidth() - width) / 2;
		}
		
		if(m_verticalAlignment == TOP) {
			topMargin = parent.getInsets().top;
		} else if(m_verticalAlignment == BOTTOM) {
			topMargin = parent.getHeight() - parent.getInsets().bottom - height;
		} else { // center
			topMargin = (int) (parent.getHeight() - height) / 2;
		}
		
		int lastX = leftMargin, lastY = topMargin;
		for (int row = 0; row < m_rows; row++) {
			for (int col = 0; col < m_columns; col++) {
				if(getIndexFromPosition(row, col) >= parent.getComponentCount())
					continue;
				Component c =
					parent.getComponent(getIndexFromPosition(row, col));
				c.setBounds(
					lastX,
					lastY,
					m_columnWidths[col],
					m_rowHeights[row]);
				lastX += m_columnWidths[col] + (col < m_columns-1 ? m_hGap : 0);
			}
			lastY += m_rowHeights[row] + m_vGap;
			lastX = leftMargin;
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		updateSizes(parent);
		Insets insets = parent.getInsets();
		int width = 0, height = 0;
		for (int c = 0; c < m_columns; c++)
			width += m_columnWidths[c] + (c < m_columns-1 ? m_hGap : 0);
		for (int r = 0; r < m_rows; r++)
			height += m_rowHeights[r] + (r < m_rows-1 ? m_vGap : 0);
		return new Dimension(
			insets.left + width + insets.right,
			insets.top + height + insets.bottom);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return minimumLayoutSize(parent);
	}

	/**
	 * Used to translate grid coordinates to an index integer for arrays.
	 */
	private int getIndexFromPosition(int row, int col) {
		return ((row > 0) ? m_columns * row : 0) + col;
	}

  /**
   * Returns the fixed column width. If the fixed value is set != 0, this overrides the specific
   * rowHeights or columnWidths (needed for controlling the exact graphical dimensions of the panel).
   */
  public int getFixedColumnWidth() {
    return m_fixedColumnWidth;
  }

  /**
   * Returns the fixed row height. If the fixed value is set != 0, this overrides the specific
   * rowHeights or columnWidths (needed for controlling the exact graphical dimensions of the panel).
   */
  public int getFixedRowHeight() {
    return m_fixedRowHeight;
  }

  /**
   * Sets the fixed column width. If the fixed value is set != 0, this overrides the specific
   * rowHeights or columnWidths (needed for controlling the exact graphical dimensions of the panel).
   */
  public void setFixedColumnWidth(int i) {
    m_fixedColumnWidth = i;
  }

  /**
   * Sets the fixed row height. If the fixed value is set != 0, this overrides the specific
   * rowHeights or columnWidths (needed for controlling the exact graphical dimensions of the panel).
   */
  public void setFixedRowHeight(int i) {
    m_fixedRowHeight = i;
  }
  
  /**
   * Returns the horizontal gap between components.
   */
  public int getHorizontalGap() {
  	return m_hGap;
  }

  /**
   * Sets the horizontal gap between components. Must be >= 0.
   */
  public void setHorizontalGap(int hGap) {
  	if(hGap < 0)
  		throw new IllegalArgumentException("Horizontal gap must be >= 0 !");
  	m_hGap = hGap;
  }
  
  /**
   * Returns the vertical gap between components.
   */
  public int getVerticalGap() {
  	return m_vGap;
  }

  /**
   * Sets the vertical gap between components. Must be >= 0.
   */
  public void setVerticalGap(int vGap) {
  	if(vGap < 0)
  		throw new IllegalArgumentException("Vertical gap must be >= 0 !");
  	m_vGap = vGap;
  }

	public void addLayoutComponent(String name, Component comp) {}

	public void removeLayoutComponent(Component comp) {}
}
