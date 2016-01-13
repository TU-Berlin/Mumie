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

package net.mumie.cdk.util.editor.tables;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * This class handles as a base for every master type editor table.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public abstract class EditorTableModel extends AbstractTableModel {

  public static Vector createList(Object[] anArray) {
    Vector result = new Vector(anArray.length);
    for (int i = 0; i < anArray.length; i++)
      result.add(anArray[i]);
    return result;
  }

  private final Vector m_rows;

  private String[] m_columnNames;

  public abstract Object getValue(Object o, int column);

  public abstract void setValue(Object o, int column, Object value);

  public abstract int getColumnCount();

  protected abstract Object createRow();

  protected EditorTableModel(Vector v) {
    m_rows = (v == null ? new Vector() : v);
    addRow(createRow());
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return true;
  }

  public int getRowCount() {
    return m_rows.size();
  }

  protected void addRow(Object o) {
    m_rows.add(o);
    fireTableRowsInserted(getRowCount() - 1, getRowCount());
  }

  protected void removeRow(int rowIndex) {
    m_rows.remove(rowIndex);
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
    return getValue(m_rows.get(rowIndex), columnIndex);
  }

  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    Object oldValue = getValue(m_rows.get(rowIndex), columnIndex);
    // only store changed values in model
    if ((oldValue == null && aValue != null)
        || (oldValue != null && aValue == null)
        || (oldValue != null && aValue != null && !oldValue.equals(aValue))) {
      setValue(m_rows.get(rowIndex), columnIndex, aValue);
      // append empty row
      if (rowIndex == getRowCount() - 1) {
        if (!aValue.equals(""))
          addRow(createRow());
        // else {
        // // remove last row if empty
        // boolean empty = true;
        // for(int i = 0; i < getColumnCount(); i++) {
        // Object value = getValueAt(rowIndex, i);
        // if(value != null && !value.equals("")) {
        // empty = false;
        // break;
        // }
        // }
        // System.out.println("empty: " + empty);
        // // if(empty)
        // // removeRow(rowIndex);
        // }
      }
    }
  }

  public void setColumnNames(String[] names) {
    m_columnNames = names;
  }

  public String getColumnName(int column) {
    if (m_columnNames == null || column >= m_columnNames.length)
      return super.getColumnName(column);
    return m_columnNames[column];
  }
}
