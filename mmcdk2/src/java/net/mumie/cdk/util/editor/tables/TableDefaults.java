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

import java.util.Iterator;
import java.util.Vector;

import javax.swing.table.TableModel;

/**
 * This class handles static default values for table columns.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class TableDefaults {

  private final Vector<Vector> m_columns = new Vector<Vector>();

  private TableModel m_model;

  public TableDefaults() {
    this(null);
  }

  public TableDefaults(TableModel model) {
    setTableModel(model);
  }

  public void setTableModel(TableModel model) {
    m_model = model;
  }

  public void addAll(TableDefaults other) {
    for (int i = 0; i < other.m_columns.size(); i++) {
      if (i >= this.m_columns.size()) // enlarge list if necessary
        this.m_columns.setSize(i + 1);
      // retrieve column list
      Vector v = this.m_columns.get(i);
      if (v == null) { // column list may be empty (i.e. null)
        v = new Vector();
        this.m_columns.set(i, v);
      }
      // add all other column list' entries to this column list
      Vector list = other.m_columns.get(i);
      if (list != null)
        v.addAll(list);
    }
  }

  public void addDefaultValue(int column, Object defaultValue) {
    if (column >= m_columns.size()) // enlarge list if necessary
      m_columns.setSize(column + 1);
    // retrieve column list
    Vector v = m_columns.get(column);
    if (v == null) { // column list may be empty (i.e. null)
      v = new Vector();
      m_columns.set(column, v);
    }
    // add default value to this column list
    v.add(defaultValue);
  }

  public Object[] getDefaultValues(int column) {
    return getDefaultValues(-1, column);
  }

  public Object[] getDefaultValues(int row, int column) {
    if (column >= m_columns.size()) // nothing added for this column
      return new Object[0];
    // retrieve column list
    Vector v = m_columns.get(column);
    // column list may be empty (i.e. null)
    if (v == null)
      return new Object[0];
    Vector result = new Vector();
    for (Iterator i = v.iterator(); i.hasNext();) {
      Object value = i.next();
      if (value instanceof TableCellDefaults) {
        Object modelValue = (m_model == null || row == -1 ? null : m_model
            .getValueAt(row, column));
        Object[] defaults = ((TableCellDefaults) value).getDefaultValues(
            modelValue, column);
        for (int j = 0; j < defaults.length; j++)
          result.add(defaults[j]);
      } else
        result.add(value);
    }
    return result.toArray();
  }
}
