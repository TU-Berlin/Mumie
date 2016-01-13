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

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import net.mumie.cdk.util.editor.types.BasicEditableGDIMEntry;
import net.mumie.cdk.util.editor.types.BasicEditableMaster;
import net.mumie.cdk.util.editor.types.ReferenceEditableMaster;
import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;

/**
 * This class handles the viewing and editing of meta information in a table.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class DefaultEditorTable extends JTable {

  public static Vector createEditableMasterList(Object[] anArray)
      throws MasterException {
    Vector result = new Vector(anArray.length);
    for (int i = 0; i < anArray.length; i++)
      result.add(BasicEditableMaster.createEditableMaster((Master) anArray[i]));
    return result;
  }

  public static Vector createMasterReferenceList(Object[] anArray)
      throws MasterException {
    Vector result = new Vector(anArray.length);
    for (int i = 0; i < anArray.length; i++)
      result.add(new ReferenceEditableMaster((Master) anArray[i]));
    return result;
  }

  public static Vector createGDIMEntryList(Object[] anArray)
      throws MasterException {
    Vector result = new Vector(anArray.length);
    for (int i = 0; i < anArray.length; i++)
      result.add(new BasicEditableGDIMEntry((GDIMEntry) anArray[i]));
    return result;
  }

  private TableDefaults m_defaults = new TableDefaults();

  public DefaultEditorTable(TableModel model) {
    super(model);
    m_defaults.setTableModel(model);
    setCellSelectionEnabled(true);
    // create cell renderer
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    // create cell editor with default values in popup menu
    final BoxModel boxModel = new BoxModel();
    final JComboBox box = new JComboBox(boxModel);
    box.setEditable(true);
    TableCellEditor editor = new DefaultCellEditor(box) {
      public Component getTableCellEditorComponent(JTable table, Object value,
          boolean isSelected, int row, int column) {
        // prepare editor
        boxModel.setSelectedItem(value);
        box.getEditor().setItem(value);
        Object[] defaultValues = m_defaults.getDefaultValues(row, column);
        boxModel.setDefaultValues(defaultValues);
        return box;
      }
    };
    box.addItemListener(new ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          box.getEditor().setItem(e.getItem());
        }
      }
    });
    // set editor for all columns
    for (int i = 0; i < getColumnCount(); i++) {
      getColumnModel().getColumn(i).setCellRenderer(renderer);
      getColumnModel().getColumn(i).setCellEditor(editor);
    }
  }

  public void addDefaults(TableDefaults defaults) {
    m_defaults.addAll(defaults);
  }

  public void setModel(TableModel model) {
    super.setModel(model);
    if (m_defaults != null)
      m_defaults.setTableModel(model);
  }

  class BoxModel implements ComboBoxModel {

    private Object m_selectedItem;

    private Object[] m_values = new Object[0];

    public void setSelectedItem(Object anItem) {
      m_selectedItem = anItem;
    }

    public Object getSelectedItem() {
      return m_selectedItem;
    }

    public int getSize() {
      return m_values.length;
    }

    public Object getElementAt(int index) {
      return m_values[index];
    }

    public void setDefaultValues(Object[] list) {
      m_values = list;
    }

    public void addListDataListener(ListDataListener l) {
    }

    public void removeListDataListener(ListDataListener l) {
    }
  }
}
