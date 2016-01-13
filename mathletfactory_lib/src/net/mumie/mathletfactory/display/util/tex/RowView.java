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

package net.mumie.mathletfactory.display.util.tex;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.SwingConstants;

import net.mumie.mathletfactory.display.util.AbstractView;
import net.mumie.mathletfactory.display.util.ViewIF;

public class RowView extends AbstractView {

	private int m_hgap = 1;
	
	private int m_vgap = 1;
	
	private RowList m_rowList = new RowList(true);// create first row
	
	public ViewIF copy() {
		return new RowView();
	}
	
	public double getBaseline(Graphics g) {
		RowList rows = createLayoutedRowList(g, getSize().width);
		return rows.getRow(0).getBaseline();// row 0 always exists
	}
	
	public Dimension getPreferredSize(Graphics context) {
  	Insets insets = getInsets();
		Dimension dim = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
		int width = (getMaximumSize().width == -1) ? -1 : getMaximumSize().width - insets.left - insets.right;
  	RowList rows = createLayoutedRowList(context, width);
  	dim.width += rows.getSize().width;
  	dim.height += rows.getSize().height;
		return dim;
	}
		
	public void paint(Graphics g, int x, int y, int width, int height) {
		super.paint(g, x, y, width, height);
  	Insets insets = getInsets();
  	RowList rows = createLayoutedRowList(g, width - insets.left - insets.right);
  	
  	// horizontal alignment
  	int hAlign = getHorizontalAlignment();
  	int xStart;
  	if(hAlign == SwingConstants.LEFT)
  		xStart = insets.left + x;
  	else if(hAlign == SwingConstants.RIGHT)
  		xStart = (int) ((width - insets.right - rows.getSize().width) + x);
  	else // CENTER
  		xStart = (int) ((width - insets.left - insets.right - rows.getSize().width) / 2 + x);
  		
  	// vertical alignment
  	double yStart = ((height - insets.top - insets.bottom - rows.getSize().height) / 2) + y;
  	
  	// calculate positions and draw children
		int xPos;
		double yPos = yStart;
		for(int r = 0; r < rows.getRowCount(); r++) {
			Row row = rows.getRow(r);
	  	// calculate horizontal position
	  	if(hAlign == SwingConstants.LEFT)
				xPos = xStart;
	  	else if(hAlign == SwingConstants.RIGHT)
				xPos = (int) ((width - insets.right - row.getSize().width) + x);
	  	else // CENTER
				xPos = (int) ((width - insets.left - insets.right - row.getSize().width) / 2 + x);
	  	
	  	// paint children at calculated baseline
			for(int i = 0; i < row.getChildCount(); i++) {
				ViewIF child = row.getChild(i);
				Dimension childDim = child.getPreferredSize(g);
				int cBaseline = insets.top + (int) (yPos + row.getMaxAscent() - child.getBaseline(g) - child.getInsets().top);
				child.paint(g, insets.left + xPos, cBaseline, childDim.width, childDim.height);
				xPos += childDim.width;
	  		if(i < row.getChildCount() - 1)
	  			xPos += m_hgap; // add horizontal gap between 2 components
			}
	  	// calculate vertical position
			yPos += row.getSize().height;
  		if(r < rows.getRowCount() - 1)
  			yPos += m_vgap; // add vertical gap between 2 rows
		}
	}
	
	private RowList createLayoutedRowList(Graphics g, int maxWidth) {
		RowList result = new RowList(false);
		for (int r = 0; r < m_rowList.getRowCount(); r++) {
			Row row = m_rowList.getRow(r);
			Row currentRow = new Row();

			for (int c = 0; c < row.getChildCount(); c++) {
				ViewIF child = row.getChild(c);
				Dimension childDim = child.getPreferredSize(g);
				// check if child should be tiled and/or placed in new row
				if (maxWidth != -1 && currentRow.getSize().width + childDim.width > maxWidth) {
					// tile text views
					if (child instanceof TextView) {
						TextView tv = (TextView) child;
						String[] substrings = tv.getSubStrings(g, maxWidth - currentRow.getSize().width, maxWidth);
						if(substrings[0] != null) { // may be null if nothing fits into current line
							if(substrings.length == 1) { // text could not be tiled 
								result.addRow(currentRow, g); // insert new row
								currentRow = new Row();
							}
							currentRow.addChild(createTextView(tv, substrings[0]), g);
						}
						// add other strings to new rows
						for(int s = 1; s < substrings.length; s++) {
							// add new row
							result.addRow(currentRow, g);
							currentRow = new Row();
							currentRow.addChild(createTextView(tv, substrings[s]), g);
						}
					}
					// place child in new row (not the first)
					else {
						// is it the first child?
						if(currentRow.getChildCount() > 0) {
							result.addRow(currentRow, g);
							currentRow = new Row();
						}
						currentRow.addChild(child, g);
					}
				} else { // child fits completely into current row
					// add current child to resulting row
					currentRow.addChild(child, g);
				}
			}
			// add current row to result
			result.addRow(currentRow, g);
		}
		return result;
	}
	
	private TextView createTextView(TextView master, String newText) {
		TextView result = new TextView(newText);
		result.setAttributes(master.getAttributes());
		return result;
	}
	
	public ViewIF addChild(ViewIF child) {
		getCurrentRow().addChild(child);
		return super.addChild(child);
	}
	
	private Row getCurrentRow() {
		return m_rowList.getLastRow();
	}
	
	public void addNewRow() {
		m_rowList.addRow(new Row());
	}
	
	class RowList {
		private ArrayList m_rows = new ArrayList();
		private Dimension m_size = new Dimension();
		
		RowList(boolean createFirstRow) {
			if(createFirstRow)
				addRow(new Row());
		}
		
		Row addRow(Row row) {
			m_rows.add(row);
			return row;
		}
		
		Row addRow(Row row, Graphics g) {
			m_rows.add(row);
			if(g != null) {
				m_size.width = Math.max(m_size.width, row.getSize().width);
				m_size.height += row.getSize().height;
				// add vertical gap between 2 rows
				if(getChildCount() > 0)
					m_size.height += m_vgap;
			}
			return row;
		}
		
		int getRowCount() {
			return m_rows.size();
		}
		
		Row getRow(int index) {
			return (Row) m_rows.get(index);
		}
		
		Row getLastRow() {
			return getRow(getRowCount() - 1);
		}

		Dimension getSize() {
			return m_size;
		}
	}
	
	class Row {
		private ArrayList m_children = new ArrayList();
		private Dimension m_size = new Dimension();
		private double m_maxAscent, m_maxDescent, m_baseline;
		
		Row() {}
		
		Row(ViewIF child) {
			addChild(child);
		}
		
		int getChildCount() {
			return m_children.size();
		}
		
		void addChild(ViewIF child) {
			m_children.add(child);
		}
		
		void addChild(ViewIF child, Graphics g) {
			m_children.add(child);
			if(g != null) {
				Dimension size = child.getPreferredSize(g);
				// per default, empty views (i.e. also empty rows) have no size
				if(child instanceof EmptyView) {
					m_size.height = Math.max(m_size.height, g.getFontMetrics(getFont()).getHeight());
					return;
				}
				double baseline = child.getBaseline(g);
				double ascent = baseline + child.getInsets().top;
				double descent = size.height - child.getInsets().top - baseline;
				if (ascent > m_maxAscent) {
					m_maxAscent = ascent;
					m_baseline = baseline;
				}
				m_maxDescent = (descent > m_maxDescent) ? descent : m_maxDescent;
				m_size.width += size.width;
				if(getChildCount() > 0)
					m_size.width += m_hgap; // add horizontal gap between 2 components
				m_size.height = (int) (m_maxAscent + m_maxDescent);
			}
		}
		
		ViewIF getChild(int index) {
			return (ViewIF) m_children.get(index);
		}
				
		double getBaseline() {
			return m_baseline;
		}

		double getMaxAscent() {
			return m_maxAscent;
		}

		Dimension getSize() {
			return m_size;
		}
	}
}
