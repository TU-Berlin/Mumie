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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

public class GUITestControlPanel extends JPanel {

	private Vector m_testPanels = new Vector();
	private Vector m_settingPanels = new Vector();
//	private SettingPanel m_editablePanel, m_editedPanel, m_prefWidthRespected, m_prefHeightRespected;
	private GUITestPanel m_currentTestPanel;
	
	private JButton m_renderButton, m_repaintButton, m_revalidateButton, m_dolayoutButton, m_getMathMLNodeButton, m_getViewerButton, m_toStringButton;
	private JPanel m_buttonPanel;
	
//	private JPanel m_allSettingsPanel;
	private TestSetting m_isEditableSetting, m_isEditedSetting, m_labelSetting, 
											 m_selectableSetting, m_prefWidthSetting, m_prefHeightSetting, m_respectPrefWidthSetting, m_respectPrefHeightSetting,
											 m_fontRenderedWithObjectColor, m_labelDisplayedSetting, m_objectColorSetting, m_fontSetting;
	
	private JPanel m_infoPanel;
	private JLabel m_classLabel, m_drawableLabel, m_trafoLabel;
	
	GUITestControlPanel() {
		super(new BorderLayout());
		
		m_infoPanel = new JPanel();//new GridLayout(2, 1));
		m_classLabel = new JLabel();
		m_drawableLabel = new JLabel();
		m_trafoLabel = new JLabel();
		m_infoPanel.add(m_classLabel);
		m_infoPanel.add(Box.createHorizontalStrut(15));
		m_infoPanel.add(m_trafoLabel);
		m_infoPanel.add(Box.createHorizontalStrut(15));
		m_infoPanel.add(m_drawableLabel);
//		JScrollPane infoScroller = new JScrollPane(m_infoPanel);
		
		m_buttonPanel = new JPanel();
		m_renderButton = new JButton("Render");
		m_renderButton.addActionListener(new RenderAction());
		m_repaintButton = new JButton("Repaint");
		m_repaintButton.addActionListener(new RepaintAction());
		m_revalidateButton = new JButton("Revalidate");
		m_revalidateButton.addActionListener(new RevalidateAction());
		m_dolayoutButton = new JButton("DoLayout");
		m_dolayoutButton.addActionListener(new DoLayoutAction());
		m_getMathMLNodeButton = new JButton("GetMathMLNode");
		m_getMathMLNodeButton.addActionListener(new GetMathMLNodeAction());
		m_getViewerButton = new JButton("Get Viewer");
		m_getViewerButton.addActionListener(new GetViewerAction());
		m_toStringButton = new JButton("toString");
		m_toStringButton.addActionListener(new ToStringAction());
		
		m_buttonPanel.add(m_renderButton);
		m_buttonPanel.add(m_repaintButton);
		m_buttonPanel.add(m_revalidateButton);
		m_buttonPanel.add(m_dolayoutButton);
		m_buttonPanel.add(m_toStringButton);
		m_buttonPanel.add(m_getMathMLNodeButton);
		m_buttonPanel.add(m_getViewerButton);
		enableControls(false);
		
		createSettings();
		
		JTabbedPane tabPane = new JTabbedPane();

//		m_allSettingsPanel = new JPanel(new GridLayout(4, 3));
//		m_allSettingsPanel.setBorder(BorderFactory.createLoweredBevelBorder());
//		m_allSettingsPanel.add(new JLabel("<html><u>MM-Object properties"));
//		m_allSettingsPanel.add(new JLabel("<html><u>Drawable properties"));
//		m_allSettingsPanel.add(new JLabel("<html><u>Display properties"));
		
		
		JPanel mmoCard = new JPanel(new GridLayout(3, 1));
		mmoCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabPane.addTab("MM-Object properties", mmoCard);
		
		JPanel drawableCard = new JPanel(new GridLayout(3, 2));
		drawableCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabPane.addTab("Drawable properties", drawableCard);
		
		JPanel displayPropsCard = new JPanel(new GridLayout(3, 1));
		displayPropsCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabPane.addTab("Display properties", displayPropsCard);
		
		mmoCard.add(addSettingPanel("Editable", m_isEditableSetting, SettingPanel.BOOLEAN_MODE));
		mmoCard.add(addSettingPanel("Edited", m_isEditedSetting,  SettingPanel.BOOLEAN_MODE));
		mmoCard.add(addSettingPanel("Label", m_labelSetting,  SettingPanel.STRING_MODE));

		displayPropsCard.add(addSettingPanel("Font color == object color", m_fontRenderedWithObjectColor,  SettingPanel.BOOLEAN_MODE));
		displayPropsCard.add(addSettingPanel("Label displayed", m_labelDisplayedSetting,  SettingPanel.BOOLEAN_MODE));
		displayPropsCard.add(addSettingPanel("Object color", m_objectColorSetting,  SettingPanel.COLOR_MODE));
		displayPropsCard.add(addSettingPanel("Font", m_fontSetting,  SettingPanel.FONT_MODE));
		
		drawableCard.add(addSettingPanel("Selectable", m_selectableSetting,  SettingPanel.BOOLEAN_MODE));
		drawableCard.add(new JLabel("<empty>"));
		drawableCard.add(addSettingPanel("Preferred width", m_prefWidthSetting,  SettingPanel.INTEGER_MODE));
		drawableCard.add(addSettingPanel("Parent respects preferred width", m_respectPrefWidthSetting,  SettingPanel.BOOLEAN_MODE));
		drawableCard.add(addSettingPanel("Preferred height", m_prefHeightSetting,  SettingPanel.INTEGER_MODE));
		drawableCard.add(addSettingPanel("Parent respects preferred height", m_respectPrefHeightSetting,  SettingPanel.BOOLEAN_MODE));

		add(m_infoPanel, BorderLayout.NORTH);
		add(tabPane, BorderLayout.CENTER);
		add(m_buttonPanel, BorderLayout.SOUTH);
	}
	
	SettingPanel addSettingPanel(String name, TestSetting setting, int mode) {
		SettingPanel result = new SettingPanel(name, setting, mode);
		result.setController(this);
		result.setEnabled(false);
		m_settingPanels.add(result);
//		m_allSettingsPanel.add(result);
		return result;
	}
	
	private void createSettings() {
		// is editable
		m_isEditableSetting = new TestSetting(new Boolean(true)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null) {
					testPanel.getMMObject().setEditable(getBooleanValue());
					System.out.println("setEditable="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null)
					setValue(testPanel.getMMObject().isEditable());
			}
		};
		// is edited
		m_isEditedSetting = new TestSetting(new Boolean(true)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getExerciseObject() != null) {
					testPanel.getExerciseObject().setEdited(getBooleanValue());
					System.out.println("setEdited="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				setValue(testPanel.getExerciseObject().isEdited());
			}
			
			boolean isActive(GUITestPanel testPanel) {
				return testPanel != null && testPanel.getExerciseObject() != null;
			}
		};
		// label text
		m_labelSetting = new TestSetting(new String()) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null) {
					testPanel.getMMObject().setLabel(getStringValue());
					System.out.println("setLabel="+getStringValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null)
					setValue(testPanel.getMMObject().getLabel());
			}
		};

		// selectable
		m_selectableSetting = new TestSetting(new Boolean(true)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getDrawable() != null) {
					testPanel.getDrawable().setSelectable(getBooleanValue());
					System.out.println("setSelectable="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getDrawable() != null)
					setValue(testPanel.getDrawable().isSelectable());
			}
		};
		// pref width
		m_prefWidthSetting = new TestSetting(new Integer(-1)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getDrawable() != null) {						
					testPanel.getDrawable().setWidth(getIntegerValue());
					System.out.println("setWidth="+getIntegerValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getDrawable() != null) {
					if(testPanel.getDrawable().isFixedWidth() && testPanel.getDrawable().getPreferredSize() != null)						
						setValue(testPanel.getDrawable().getPreferredSize().width);
					else
						setValue(-1);
				}
			}
		};
		// pref height
		m_prefHeightSetting = new TestSetting(new Integer(-1)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getDrawable() != null) {						
					testPanel.getDrawable().setHeight(getIntegerValue());
					System.out.println("setHeight="+getIntegerValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getDrawable() != null) {
					if(testPanel.getDrawable().isFixedHeight() && testPanel.getDrawable().getPreferredSize() != null)						
						setValue(testPanel.getDrawable().getPreferredSize().height);
					else
						setValue(-1);
				}
			}
		};
		// preferred width respected
		m_respectPrefWidthSetting = new TestSetting(new Boolean(true)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null) {
					testPanel.setPreferredWidth(getBooleanValue());
					System.out.println("setPreferredWidth="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null)
					setValue(testPanel.isPreferredWidth());
			}
		};
		// preferred height respected
		m_respectPrefHeightSetting = new TestSetting(new Boolean(true)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null) {
					testPanel.setPreferredHeight(getBooleanValue());
					System.out.println("setPreferredHeight="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null)
					setValue(testPanel.isPreferredHeight());
			}
		};
		// font
		m_fontSetting = new TestSetting((Font) null) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null) {
					testPanel.getMMObject().getDisplayProperties().setFont(getFontValue());
					System.out.println("setFont="+getFontValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null)
					setValue(testPanel.getMMObject().getDisplayProperties().getFont());
			}
		};
		// label displayed
		m_labelDisplayedSetting = new TestSetting(new Boolean(true)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null) {
					testPanel.getMMObject().getDisplayProperties().setLabelDisplayed(getBooleanValue());
					System.out.println("setLabelDisplayed="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null)
					setValue(testPanel.getMMObject().getDisplayProperties().isLabelDisplayed());
			}
		};
		// font color
		m_fontRenderedWithObjectColor = new TestSetting(new Boolean(false)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null) {
					testPanel.getMMObject().getDisplayProperties().setFontRenderedWithObjectColor(getBooleanValue());
					System.out.println("setFontRenderedWithObjectColor="+getBooleanValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null)
					setValue(testPanel.getMMObject().getDisplayProperties().isFontRenderedWithObjectColor());
			}
		};
		// foreground color
		m_objectColorSetting = new TestSetting(new Color(0, 0, 0)) {
			void applyValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null) {
					testPanel.getMMObject().getDisplayProperties().setObjectColor(getColorValue());
					System.out.println("setObjectColor="+getColorValue());
				}
			}
			void loadValue(GUITestPanel testPanel) {
				if(testPanel != null && testPanel.getMMObject() != null && testPanel.getMMObject().getDisplayProperties() != null)
					setValue(testPanel.getMMObject().getDisplayProperties().getObjectColor());
			}
		};
	}
	
	private void enableControls(boolean flag) {
		if(flag == false) {
			m_classLabel.setText("");
			m_drawableLabel.setText("");
			m_trafoLabel.setText("");
		}
		m_classLabel.setEnabled(flag);
		m_drawableLabel.setEnabled(flag);
		m_trafoLabel.setEnabled(flag);
		m_renderButton.setEnabled(flag);
		m_repaintButton.setEnabled(flag);
		m_revalidateButton.setEnabled(flag);
		if(flag && getCurrentTestPanel().getMMObject() instanceof MathMLSerializable)
			m_getMathMLNodeButton.setEnabled(true);
		else
			m_getMathMLNodeButton.setEnabled(false);
		if(flag && getCurrentTestPanel().getMMObject() instanceof ExerciseObjectIF)
			m_getViewerButton.setEnabled(true);
		else
			m_getViewerButton.setEnabled(false);
		m_toStringButton.setEnabled(flag);
	}
	
	private void setLabels() {
		Class objectClass = getCurrentTestPanel().getMMObject().getClass();
		m_classLabel.setText("<html>MM-Object class: <font color=\"blue\">" + getShortClassName(objectClass));
		m_classLabel.setToolTipText("MM-Object package: " + objectClass.getPackage().getName());
		Class drawableClass = getCurrentTestPanel().getDrawable().getClass();
		m_drawableLabel.setText("<html>Drawable class:   <font color=\"blue\">" + getShortClassName(drawableClass));
		m_drawableLabel.setToolTipText("Drawable package: " + drawableClass.getPackage().getName());
		Class trafoClass = getCurrentTestPanel().getMMObject().getDisplayTransformerMap().get(getCurrentTestPanel().getDrawable()).getClass();
		m_trafoLabel.setText("<html>Transformer class: <font color=\"blue\">" + getShortClassName(trafoClass));
		m_trafoLabel.setToolTipText("Transformer package: " + trafoClass.getPackage().getName());
	}
	
	private String getShortClassName(Class aClass) {
    try {
      String className = aClass.getName();
      return className.substring(className.lastIndexOf('.') + 1);    	
    } catch(IndexOutOfBoundsException e) {// class is in default package
    	return aClass.getName();
    }
	}
	
	GUITestPanel addTestPanel(GUITestPanel testPanel) {
		m_testPanels.add(testPanel);
		testPanel.setController(this);
		testPanel.addMouseListener(new MyMouseListener());
		return testPanel;
	}
	
	void select(GUITestPanel testPanel) {
		if(m_currentTestPanel != null) {
			m_currentTestPanel.setSelected(false);
		}
		m_currentTestPanel = testPanel;
		if(m_currentTestPanel.hasErrors()) {
			deselect();
			return;
		}
		m_currentTestPanel.setSelected(true);
		for(int i = 0; i < m_settingPanels.size(); i++) {
			SettingPanel sp = (SettingPanel) m_settingPanels.get(i);
			if(sp.isActive(testPanel)) {
				sp.setEnabled(true);
				sp.loadSetting(testPanel);
			} else {
				sp.setEnabled(false);
			}
		}
		setLabels();
		enableControls(true);
	}
	
	void deselect() {
		if(m_currentTestPanel != null)
			m_currentTestPanel.setSelected(false);
		m_currentTestPanel = null;
		for(int i = 0; i < m_settingPanels.size(); i++) {
			((SettingPanel) m_settingPanels.get(i)).setEnabled(false);
		}
		enableControls(false);
	}
	
	GUITestPanel getCurrentTestPanel() {
		return m_currentTestPanel;
	}
	
	class MyMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			e.getComponent().requestFocusInWindow();
			select((GUITestPanel) e.getComponent());
		}
	}
	
	class RenderAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null) {
				System.out.println("Render!");
				getCurrentTestPanel().getMMObject().render();
			}
		}
	}
	
	class RepaintAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null) {
				System.out.println("Repaint!");
				getCurrentTestPanel().repaint();
			}
		}
	}
	
	class RevalidateAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null) {
				System.out.println("Revalidate!");
				getCurrentTestPanel().revalidate();
			}
		}
	}
	
	class DoLayoutAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null) {
				System.out.println("DoLayout!");
				getCurrentTestPanel().doLayout();
			}
		}
	}
	
	class GetMathMLNodeAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null && getCurrentTestPanel().getMMObject() != null && getCurrentTestPanel().getMMObject() instanceof MathMLSerializable) {
				System.out.println("GetMathMLNode!");
				System.out.println(XMLUtils.nodeToString(
						((MathMLSerializable) getCurrentTestPanel().getMMObject()).getMathMLNode(XMLUtils.getDefaultDocument()) ));
			}
		}
	}
	
	class GetViewerAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null && getCurrentTestPanel().getMMObject() != null && getCurrentTestPanel().getMMObject() instanceof ExerciseObjectIF) {
				System.out.println("GetViewer!");
				JComponent drawable = null;
				try {
					drawable = ExerciseObjectFactory.createViewer(
							((ExerciseObjectIF) getCurrentTestPanel().getMMObject()).getMathMLNode(XMLUtils.getDefaultDocument()));
				} catch(Exception e) {
					System.err.println("Error while creating viewer !");
					e.printStackTrace();
					return;
				}
				JFrame frame = new JFrame(getShortClassName(drawable.getClass()));
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JPanel resizer = new JPanel();
				resizer.setBorder(BorderFactory.createTitledBorder(getShortClassName(drawable.getClass())));
				resizer.add(drawable);
				frame.getContentPane().add(resizer);
				frame.pack();
				frame.setLocationRelativeTo(getCurrentTestPanel());
				frame.setVisible(true);
			}
		}
	}
	
	class ToStringAction extends AbstractAction {
		public void actionPerformed(ActionEvent ae) {
			if(getCurrentTestPanel() != null && getCurrentTestPanel().getMMObject() != null) {
				System.out.println("ToString!");
				System.out.println(getCurrentTestPanel().getMMObject().toString());
			}
		}
	}
}
