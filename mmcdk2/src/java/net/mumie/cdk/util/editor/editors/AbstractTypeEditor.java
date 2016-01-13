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

package net.mumie.cdk.util.editor.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import net.mumie.cdk.util.editor.tables.ComponentsTable;
import net.mumie.cdk.util.editor.tables.DefaultEditorTable;
import net.mumie.cdk.util.editor.tables.GDImEntriesTable;
import net.mumie.cocoon.checkin.EditableMaster;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.notions.DocType;

/**
 * This class handles the common meta information and acts as a base for all
 * specific master type editors.
 * 
 * @author Markus Gronau <a href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public abstract class AbstractTypeEditor {

  public final static String TYPE_INFO = "info.type";

  public final static String NAME_INFO = "info.name";

  public final static String DESCRIPTION_INFO = "info.description";

  public final static String CHANGELOG_INFO = "info.changelog";

  public final static String COPYRIGHT_INFO = "info.copyright";

  public final static String WIDTH_INFO = "info.width";

  public final static String HEIGHT_INFO = "info.height";

  public final static String QUALIFIED_NAME_INFO = "info.qualified-name";

  public final static String INFO_PAGE_INFO = "info.info-page";

  public final static String THUMBNAIL_INFO = "info.thumbnail";

  public final static String CONTENT_TYPE_INFO = "info.content-type";

  public final static String SOURCE_TYPE_INFO = "info.source-type";

  private final static int COMMON_ATTRIBUTES_TAB = 0;

  private final static int TYPE_ATTRIBUTES_TAB = 1;

  private final static Dimension FIELD_DIM = new Dimension(450, 20);

  private final static Dimension AREA_DIM = new Dimension(447, 40);

  public static AbstractTypeEditor createTypeEditor(Master master)
      throws MasterException {
    switch (master.getType()) {
    case DocType.IMAGE:
      return new ImageTypeEditor(master);
    case DocType.APPLET:
      return new AppletTypeEditor(master);
    case DocType.JAR:
      return new JarTypeEditor(master);
    case DocType.PAGE:
      return new PageTypeEditor(master);
    case DocType.CSS_STYLESHEET:
      return new CSSTypeEditor(master);
    case DocType.XSL_STYLESHEET:
      return new XSLTypeEditor(master);
    }
    throw new MasterException("Unknown document type of master: "
        + master.getTypeName());
  }

  private final EditableMaster m_master;

  private final Map<String, TextInputComponent> m_inputComponents = new LinkedHashMap<String, TextInputComponent>();

  private final Properties m_messages = new Properties();

  private JPanel m_contentPane;

  private JPanel[] m_propPanels = new JPanel[4];

  public AbstractTypeEditor(EditableMaster master) throws MasterException {
    m_master = master;

    loadMessages();
    createLayout();
    initCommonAttributes();
    initTypeAttributes();
  }

  private void loadMessages() throws MasterException {
    try {
      m_messages.load(getClass().getResourceAsStream(
          "/net/mumie/cdk/util/editor/Messages_de.properties"));
    } catch (IOException e) {
      throw new MasterException("Cannot load editor's messages", e);
    }
  }

  private void createLayout() throws MasterException {
    m_propPanels[0] = new JPanel();
    BoxLayout bl = new BoxLayout(m_propPanels[0], BoxLayout.Y_AXIS);
    m_propPanels[0].setLayout(bl);
    m_propPanels[0].setBorder(new TitledBorder(
        getMessage("editor.common.tooltip")));

    Box firstTab = Box.createVerticalBox();
    firstTab.add(m_propPanels[0]);
    m_propPanels[1] = new JPanel();
    bl = new BoxLayout(m_propPanels[1], BoxLayout.Y_AXIS);
    m_propPanels[1].setLayout(bl);
    m_propPanels[1].setBorder(new TitledBorder(getMessage("editor."
        + getTypeName() + ".tooltip")));
    firstTab.add(m_propPanels[1]);
    JPanel resizer = new JPanel();
    resizer.add(firstTab);
    m_propPanels[2] = new JPanel();
    m_propPanels[2].setBorder(new TitledBorder(
        getMessage("editor.components.tooltip")));
    DefaultEditorTable componentsTable = new ComponentsTable(getMaster());
    componentsTable.setPreferredScrollableViewportSize(new Dimension(550, 50));
    JPanel tableResizer = new JPanel(new BorderLayout());
    tableResizer.add(componentsTable.getTableHeader(), BorderLayout.PAGE_START);
    tableResizer.add(new JScrollPane(componentsTable), BorderLayout.CENTER);
    m_propPanels[2].add(tableResizer);
    firstTab.add(m_propPanels[2]);
    m_propPanels[3] = new JPanel();
    m_propPanels[3].setBorder(new TitledBorder(
        getMessage("editor.gdim-entries.tooltip")));
    DefaultEditorTable gdimEntriesTable = new GDImEntriesTable(getMaster());
    gdimEntriesTable.setPreferredScrollableViewportSize(new Dimension(550, 50));
    tableResizer = new JPanel(new BorderLayout());
    tableResizer
        .add(gdimEntriesTable.getTableHeader(), BorderLayout.PAGE_START);
    tableResizer.add(new JScrollPane(gdimEntriesTable), BorderLayout.CENTER);
    m_propPanels[3].add(tableResizer);
    firstTab.add(m_propPanels[3]);

    m_contentPane = new JPanel(new BorderLayout());
    m_contentPane.add(new JScrollPane(resizer), BorderLayout.CENTER);
  }

  public JComponent getContentPane() {
    return m_contentPane;
  }

  protected void initCommonAttributes() throws MasterException {
    addCommonAttribute(TYPE_INFO);
    addCommonAttribute(NAME_INFO);
    addCommonAttribute(DESCRIPTION_INFO);
    addCommonAttribute(CHANGELOG_INFO);
    addCommonAttribute(COPYRIGHT_INFO);
  }

  protected abstract void initTypeAttributes() throws MasterException;

  protected String getData(String name) throws MasterException {
    if (name.equals(TYPE_INFO))
      return getMaster().getTypeName();
    else if (name.equals(NAME_INFO))
      return getMaster().getName();
    else if (name.equals(DESCRIPTION_INFO))
      return getMaster().getDescription();
    else if (name.equals(CHANGELOG_INFO))
      return getMaster().getChangelog();
    else if (name.equals(COPYRIGHT_INFO))
      return getMaster().getCopyright();
    else
      throw new MasterException("Illegal \"" + name
          + "\" parameter for type editor " + getClass().getName());
  }

  protected void setData(String name, String data) throws MasterException {
    if (name.equals(TYPE_INFO))
      throw new MasterException("Cannot change type after creation!");
    else if (name.equals(NAME_INFO))
      getMaster().setName(data);
    else if (name.equals(DESCRIPTION_INFO))
      getMaster().setDescription(data);
    else if (name.equals(CHANGELOG_INFO))
      getMaster().setChangelog(data);
    else if (name.equals(COPYRIGHT_INFO))
      getMaster().setCopyright(data);
    else
      throw new MasterException("Illegal \"" + name
          + "\" parameter for type editor " + getClass().getName());
  }

  protected String getDefaultData(String name) throws MasterException {
    return null;
  }

  public EditableMaster getMaster() {
    return m_master;
  }

  /**
   * Returns the document type name for this editor.
   */
  protected abstract String getTypeName();

  protected TextInputComponent createInputComponent(String name)
      throws MasterException {
    if (name.equals(TYPE_INFO))
      return createLabel();
    else if (name.equals(NAME_INFO))
      return createTextField();
    else if (name.equals(DESCRIPTION_INFO))
      return createTextArea();
    else if (name.equals(CHANGELOG_INFO))
      return createTextArea();
    else if (name.equals(COPYRIGHT_INFO))
      return createTextArea();
    else
      throw new MasterException("Illegal \"" + name
          + "\" parameter for type editor " + getClass().getName());
  }

  protected TextInputComponent createLabel() {
    InputLabel result = new InputLabel();
    return result;
  }

  protected TextInputComponent createTextField() {
    InputField result = new InputField();
    return result;
  }

  protected TextInputComponent createTextArea() {
    InputArea result = new InputArea();
    return result;
  }

  protected TextInputComponent createComboBox(String[] defaultValues) {
    InputBox result = new InputBox(defaultValues);
    result.setEditable(true);
    return result;
  }

  protected TextInputComponent createSpinner() {
    InputSpinner result = new InputSpinner();
    return result;
  }

  protected final void addCommonAttribute(String name) throws MasterException {
    addEditorComponent(name, COMMON_ATTRIBUTES_TAB);
  }

  protected final void addTypeAttribute(String name) throws MasterException {
    addEditorComponent(name, TYPE_ATTRIBUTES_TAB);
  }

  private void addEditorComponent(String name, int location)
      throws MasterException {
    TextInputComponent inputComponent = createInputComponent(name);
    if (inputComponent.isEditable()) // only add changeable metainfos
      m_inputComponents.put(name, inputComponent);
    String data = getData(name);
    if (data == null)
      data = getDefaultData(name);
    inputComponent.setText(data);
    inputComponent.setToolTipText(getMessage(name + ".tooltip"));
    JLabel label = new JLabel(getMessage(name) + ":");
    label.setToolTipText(inputComponent.getToolTipText());
    JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    row.add(label);
    if (inputComponent.useScrollbars())
      row.add(new JScrollPane(inputComponent.getComponent()));
    else
      row.add(inputComponent.getComponent());
    m_propPanels[location].add(row);
  }

  public String getMessage(String key) {
    return m_messages.getProperty(key);
  }

  public final void save() throws MasterException {
    Set<String> keys = m_inputComponents.keySet();
    for (Iterator<String> i = keys.iterator(); i.hasNext();) {
      String key = i.next();
      TextInputComponent comp = m_inputComponents.get(key);
      setData(key, comp.getText());
    }
  }

  class InputLabel extends JLabel implements TextInputComponent {

    InputLabel() {
      super();
      setPreferredSize(FIELD_DIM);
      setFont(getFont().deriveFont(Font.PLAIN));
    }

    public Component getComponent() {
      return this;
    }

    public boolean useScrollbars() {
      return false;
    }

    public boolean isEditable() {
      return false;
    }
  }

  class InputField extends JTextField implements TextInputComponent {

    InputField() {
      super();
      setPreferredSize(FIELD_DIM);
    }

    public Component getComponent() {
      return this;
    }

    public boolean useScrollbars() {
      return false;
    }
  }

  class InputBox extends JComboBox implements TextInputComponent {

    InputBox(String[] defaultValues) {
      super(defaultValues);
      setPreferredSize(FIELD_DIM);
      setFont(getFont().deriveFont(Font.PLAIN));
    }

    public String getText() {
      return (String) getSelectedItem();
    }

    public void setText(String text) {
      setSelectedItem(text);
    }

    public Component getComponent() {
      return this;
    }

    public boolean useScrollbars() {
      return false;
    }
  }

  class InputArea extends JTextArea implements TextInputComponent {

    InputArea() {
      super();
      setPreferredSize(AREA_DIM);
    }

    public Component getComponent() {
      return this;
    }

    public boolean useScrollbars() {
      return true;
    }
  }

  class InputSpinner extends JSpinner implements TextInputComponent {

    InputSpinner() {
      super(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 10));
      if (getEditor() instanceof JSpinner.DefaultEditor)
        ((JSpinner.DefaultEditor) getEditor()).getTextField()
            .setHorizontalAlignment(JTextField.LEFT);
      setPreferredSize(FIELD_DIM);
    }

    public String getText() {
      return getValue().toString();
    }

    public void setText(String text) {
      setValue(new Integer(text));
    }

    public Component getComponent() {
      return this;
    }

    public boolean useScrollbars() {
      return false;
    }

    public boolean isEditable() {
      return true;
    }
  }
}
