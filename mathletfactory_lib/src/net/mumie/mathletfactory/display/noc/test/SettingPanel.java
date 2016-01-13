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

package net.mumie.mathletfactory.display.noc.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SettingPanel extends JPanel implements ActionListener {

	final static int BOOLEAN_MODE = 0;
	final static int STRING_MODE = 1;
	final static int COLOR_MODE = 2;
	final static int INTEGER_MODE = 3;
	final static int FONT_MODE = 4;
	
	private EditorIF m_editor;
	private TestSetting m_setting;
	private GUITestControlPanel m_controller;
	private int m_mode = -1;
	
	SettingPanel(String name, TestSetting setting, int mode) {
		super(new BorderLayout());
		m_mode = mode;
		m_setting = setting;
		switch(m_mode) {
		case BOOLEAN_MODE:
			m_editor = new BooleanEditor(name);
			((BooleanEditor) m_editor).addActionListener(this);
			break;
		case COLOR_MODE:
			m_editor = new ColorEditor(name);
			break;
		case STRING_MODE:
			m_editor = new StringEditor(name);
			break;
		case INTEGER_MODE:
			m_editor = new IntegerEditor(name);
			break;
		case FONT_MODE:
			m_editor = new FontEditor(name);
			break;
		}
		add((JComponent)m_editor, BorderLayout.CENTER);
	}
	
	public void setEnabled(boolean flag) {
		super.setEnabled(flag);
		m_editor.setEnabled(flag);
	}
	
	void setController(GUITestControlPanel controlPanel) {
		m_controller = controlPanel;
	}
	
	void applySetting(GUITestPanel testPanel) {
		m_setting.applyValue(testPanel);
		m_editor.setValue(m_setting);
	}
	void loadSetting(GUITestPanel testPanel) {
		m_setting.loadValue(testPanel);
		m_editor.setValue(m_setting);
	}
	
	boolean isActive(GUITestPanel testPanel) {
		return m_setting.isActive(testPanel);
	}
//	TestSetting readSetting();
	
	public void actionPerformed(ActionEvent ae) {
		applyToController();
	}
	
	private void applyToController() {
		if(m_controller != null) {
			m_setting.setValue(m_editor.getValue());
			m_setting.applyValue(m_controller.getCurrentTestPanel());
		}
	}
	
	class BooleanEditor extends JCheckBox implements EditorIF {
		BooleanEditor(String name) {
			super(name);
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					m_setting.setValue(isSelected());
				}
			});
		}

		public void setValue(TestSetting setting) {
			setSelected(setting.getBooleanValue());
		}
		
		public Object getValue() {
			return new Boolean(isSelected());
		}
	}
	
	class ColorEditor extends JPanel implements EditorIF {
		private JButton m_colorButton;
		private JLabel m_nameLabel;
		ColorEditor(String name) {
			super(new FlowLayout(FlowLayout.LEFT));
			
			m_colorButton = new JButton("");
			m_colorButton.setBorder(null);
			m_colorButton.setPreferredSize(new Dimension(15, 15));
			m_nameLabel = new JLabel(name);
			add(m_colorButton);
			add(m_nameLabel);
			m_colorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Color c = JColorChooser.showDialog(m_controller, "Choose a color...", m_colorButton.getBackground());
					if(c != null) {
						m_setting.setValue(c);
						m_colorButton.setBackground(c);
						applyToController();
					}
				}
			});
		}
		
		public void setEnabled(boolean flag) {
			super.setEnabled(flag);
			m_colorButton.setEnabled(flag);
			m_nameLabel.setEnabled(flag);
		}

		public void setValue(TestSetting setting) {
			m_colorButton.setBackground(setting.getColorValue());
		}
		
		public Object getValue() {
			return m_colorButton.getBackground();
		}
	}

	class IntegerEditor extends JPanel implements EditorIF {
		private JTextField m_textfield;
		private JLabel m_nameLabel;
		private String m_oldText;
		IntegerEditor(String name) {
			super(new FlowLayout(FlowLayout.LEFT));
			
			m_textfield = new JTextField(3);
			m_nameLabel = new JLabel(name);
			add(m_textfield);
			add(m_nameLabel);
			m_textfield.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					try {
						int i = Integer.parseInt(m_textfield.getText());
						m_setting.setValue(i);
						applyToController();
						m_oldText = m_textfield.getText();
					} catch(NumberFormatException nfe) {
						m_textfield.setText(m_oldText);
					}
				}
			});
		}
		
		public void setEnabled(boolean flag) {
			super.setEnabled(flag);
			m_textfield.setEnabled(flag);
			m_nameLabel.setEnabled(flag);
		}

		public void setValue(TestSetting setting) {
			m_textfield.setText(setting.getValue().toString());
		}
		
		public Object getValue() {
			return new Integer(m_textfield.getText());
		}
	}

	class FontEditor extends JPanel implements EditorIF {
		private JButton m_fontButton;
		private JLabel m_nameLabel;
		FontEditor(String name) {
			super(new FlowLayout(FlowLayout.LEFT));
			
			m_fontButton = new JButton("Text");
			m_fontButton.setPreferredSize(new Dimension(70, 30));
			m_fontButton.setBorder(BorderFactory.createEtchedBorder());
			m_nameLabel = new JLabel(name);
			add(m_fontButton);
			add(m_nameLabel);
			m_fontButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					FontChooser fc = new FontChooser(SettingPanel.this, m_fontButton.getFont());
					Font f = fc.showDialog(m_setting.getFontValue());
					if(f != null) {
						m_setting.setValue(f);
						m_fontButton.setFont(f);
						applyToController();
					}
				}
			});
		}
		
		public void setEnabled(boolean flag) {
			super.setEnabled(flag);
			m_fontButton.setEnabled(flag);
			m_nameLabel.setEnabled(flag);
		}

		public void setValue(TestSetting setting) {
			m_fontButton.setFont(setting.getFontValue());
		}
		
		public Object getValue() {
			return m_fontButton.getFont();
		}
	}
	
	class FontChooser extends JDialog {
		private JButton m_cancelButton = new JButton("Cancel"), m_okButton = new JButton("  OK  ");
		private JComboBox m_nameBox = new JComboBox(new String[] {"Serif", "SansSerif", "Monospaced", "Dialog", "DialogInput"});
		private JComboBox m_sizeBox = new JComboBox(new String[] {"8", "9", "10", "12", "14", "16", "18", "20", "22", "24", "26"});
		private JComboBox m_styleBox = new JComboBox(new String[] {"PLAIN", "BOLD", "ITALIC", "BOLD-ITALIC"});
		private Font m_font;
		FontChooser(Component c, Font font) {
			super((Frame) SwingUtilities.windowForComponent(c), "Choose a font...", true);
			m_sizeBox.setEditable(true);
			
			JPanel centerPane = new JPanel(new GridLayout(3, 2));
			centerPane.add(new JLabel("Name:"));
			centerPane.add(m_nameBox);
			centerPane.add(new JLabel("Style:"));
			centerPane.add(m_styleBox);
			centerPane.add(new JLabel("Size:"));
			centerPane.add(m_sizeBox);
			
			
			JPanel bottomPane = new JPanel();
			bottomPane.add(m_okButton);
			bottomPane.add(m_cancelButton);
			
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(new JLabel("Choose a font..."), BorderLayout.NORTH);
			getContentPane().add(centerPane, BorderLayout.CENTER);
			getContentPane().add(bottomPane, BorderLayout.SOUTH);
			
			m_okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					m_font = new Font((String) m_nameBox.getSelectedItem(), parseStyle((String) m_styleBox.getSelectedItem()), Integer.parseInt((String) m_sizeBox.getSelectedItem()));
					dispose();
				}
			});
			m_cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					dispose();
				}
			});
			
			pack();
			setSize(300, 200);
		}
		
	 int parseStyle(String style) {
			if(style.equals("PLAIN"))
				return Font.PLAIN;
			if(style.equals("BOLD"))
				return Font.BOLD;
			if(style.equals("ITALIC"))
				return Font.ITALIC;
			if(style.equals("BOLD-ITALIC"))
				return Font.ITALIC | Font.BOLD;
			return -1;
		}
		
		Font showDialog(Font f) {
			selectEntry(f.getName(), m_nameBox);
			selectStyle(f.getStyle(), m_styleBox);
			selectEntry(f.getSize()+"", m_sizeBox);
			setVisible(true);
			return m_font;
		}
		
		void selectEntry(String value, JComboBox box) {
			for(int i = 0; i < box.getItemCount(); i++) {
				if(value.equals(box.getItemAt(i))) {
					box.setSelectedIndex(i);
					break;
				}
			}
		}
		
		void selectStyle(int style, JComboBox box) {
			if(style == 0)
				box.setSelectedIndex(0);
			else if((style & Font.BOLD) != 0 && (style & Font.ITALIC) == 0)
				box.setSelectedIndex(1);
			else if((style & Font.ITALIC) != 0 && (style & Font.BOLD) == 0)
				box.setSelectedIndex(2);
			else
				box.setSelectedIndex(3);
		}
	}

	class StringEditor extends JPanel implements EditorIF {
		private JTextField m_textfield;
		private JLabel m_nameLabel;
		StringEditor(String name) {
			super(new FlowLayout(FlowLayout.LEFT));
			
			m_textfield = new JTextField(5);
			m_nameLabel = new JLabel(name);
			add(m_textfield);
			add(m_nameLabel);
			m_textfield.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					m_setting.setValue(m_textfield.getText());
					applyToController();
				}
			});
		}
		
		public void setEnabled(boolean flag) {
			super.setEnabled(flag);
			m_textfield.setEnabled(flag);
			m_nameLabel.setEnabled(flag);
		}

		public void setValue(TestSetting setting) {
			m_textfield.setText(setting.getStringValue());
		}
		
		public Object getValue() {
			return m_textfield.getText();
		}
	}

	
	interface EditorIF {
		void setValue(TestSetting setting);
		Object getValue();
		void setEnabled(boolean flag);
	}
}
