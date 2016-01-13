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

import net.mumie.cdk.util.editor.types.ReferenceEditableMaster;
import net.mumie.cocoon.checkin.EditableMaster;
import net.mumie.cocoon.checkin.MasterException;

/**
 * This class handles the <code>components</code> section of meta info files.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class ComponentsTable extends DefaultEditorTable {

  public ComponentsTable(EditableMaster master) throws MasterException {
    super(new ComponentsTableModel(createMasterReferenceList(master
        .getComponents())));

    getColumnModel().getColumn(0).setMaxWidth(200);
    getColumnModel().getColumn(1).setMaxWidth(200);
    getColumnModel().getColumn(2).setPreferredWidth(400);
    getColumnModel().getColumn(2).setMaxWidth(800);

    TableDefaults appletDefaults = new TableDefaults();
    appletDefaults.addDefaultValue(0, "applet");
    addDefaults(appletDefaults);

    TableDefaults imageDefaults = new TableDefaults();
    imageDefaults.addDefaultValue(0, "image");
    addDefaults(imageDefaults);

    TableDefaults jarDefaults = new TableDefaults();
    jarDefaults.addDefaultValue(0, "jar");
    addDefaults(jarDefaults);
  }
}

class ComponentsTableModel extends EditorTableModel {

  ComponentsTableModel(Vector v) {
    super(v);
    setColumnNames(new String[] { "Type", "Local ID", "Path" });
  }

  protected Object createRow() {
    try {
      return new ReferenceEditableMaster();
    } catch (MasterException e) {
      e.printStackTrace();
    }
    return null;
  }

  public int getColumnCount() {
    return 3;
  }

  public Object getValue(Object o, int column) {
    try {
      EditableMaster component = (EditableMaster) o;
      switch (column) {
      case 0:
        return component.getTypeName();
      case 1:
        return component.getLid();
      case 2:
        return component.getPath();
      }
    } catch (MasterException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void setValue(Object o, int column, Object value) {
    try {
      EditableMaster component = (EditableMaster) o;
      // System.out.println("Change column " + column + " to " + value);
      switch (column) {
      case 0:
        component.setTypeName((String) value);
        break;
      case 1:
        component.setLid((String) value);
        break;
      case 2:
        component.setPath((String) value);
        break;
      }
    } catch (MasterException e) {
      e.printStackTrace();
    }
  }
}