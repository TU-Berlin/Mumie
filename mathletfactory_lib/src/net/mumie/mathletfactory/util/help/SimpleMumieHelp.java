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

package net.mumie.mathletfactory.util.help;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public class SimpleMumieHelp extends MumieHelp {

	private final static MumieLogger LOGGER = MumieLogger.getLogger(SimpleMumieHelp.class);
	
	private final static LogCategory HELP_TOC_CATEGORY = LOGGER.getCategory("help.toc");
	
	private final static LogCategory HELP_ID_MAP_CATEGORY = LOGGER.getCategory("help.id-map");
	
	public final static String HOME_ID = "index";

	private SimpleHelpFrame m_helpFrame;
	
	SimpleMumieHelp(MathletRuntime mathlet) {
		super(mathlet);
	}
	
	private void createHelpFrame() {
		if(m_helpFrame == null) {
			m_helpFrame = new SimpleHelpFrame(this);
		}
	}
	
	public void installHelp(final JRootPane rootPane, String paneHelpID) {
		if(paneHelpID == null)
			paneHelpID = getHomeID();
		final String helpID = paneHelpID;
		String actionName = "Help";
		Action action = new AbstractAction(actionName) {
			public void actionPerformed(ActionEvent e) {
				openHelp(helpID, SwingUtilities.windowForComponent(rootPane));
			}
		};
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), actionName);
		rootPane.getActionMap().put(actionName, action);
	}
	
	public void installHelp(final AbstractButton button, String buttonHelpID) {
		if(buttonHelpID == null)
			buttonHelpID = getHomeID();
		final String helpID = buttonHelpID;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				openHelp(helpID, SwingUtilities.windowForComponent(button));
			}
		});
	}
	
	public void showHelp() {
		openHelp(getHomeID());
	}
	
	protected void openHelp(String helpID) {
		createHelpFrame();
		m_helpFrame.setCurrentID(helpID);
		m_helpFrame.setVisible(true);
	}
	
	private void openHelp(String helpID, Window frame) {
		createHelpFrame();
		m_helpFrame.setCurrentID(helpID);
//		((DefaultHelpBroker)m_mainHelpBroker).setActivationWindow(frame);
		m_helpFrame.setVisible(true);
	}
	
	protected DefaultMutableTreeNode createTOCItem(DefaultMutableTreeNode parentNode, String target, String text) {
		if(parentNode != null) {
			for(int i = 0; i < parentNode.getChildCount(); i++) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) parentNode.getChildAt(i);
				TocItem item = (TocItem) n.getUserObject();
				if(item.getTarget() != null && item.getTarget().equals(target)) {
					if(item.getText() == null || item.getText().equals(""))
						item.setText(text);
					LOGGER.log(HELP_TOC_CATEGORY, "Returning existing toc item \"" + text + "\" for target \"" + target + "\"");
					return n;
				}
			}
		}
		return new DefaultMutableTreeNode(new TocItem(target, text));
	}
	
	protected void addHelpSet(URL base, PropertyMapIF id2urlMap, PropertyMapIF id2classMap, DefaultMutableTreeNode treeData) {
		SimpleHelpSet helpSet = new SimpleHelpSet(this, base, id2urlMap, treeData);
		createHelpFrame();
		m_helpFrame.addHelpSet(helpSet);
	}
	
	public String getHomeID() {
		return HOME_ID;
	}
	
	class TocItem {
		
		private String m_target;
		
		private String m_text;
		
		TocItem(String target, String text) {
			m_target = target;
			m_text = text;
		}
		
		public String getTarget() {
			return (String) m_target;
		}
		
		public String getText() {
			return m_text;
		}
		
		void setText(String text) {
			m_text = text;
		}
		
		public boolean equals(Object o) {
			if(o instanceof TocItem) {
				TocItem i = (TocItem) o;
				return this.getTarget().equals(i.getTarget()) && this.getText().equals(i.getText());
			} else
				return false;
		}
		
		public String toString() {
			return getText();
		}
	}
	
	class SimpleHelpSet {
		
		public final static String APPLET_INFO_ID = "applet_info";
		
		private URL m_base;
		
		private PropertyMapIF m_map = new DefaultPropertyMap();
		
		private DefaultMutableTreeNode m_treeData;
		
		SimpleHelpSet(MumieHelp help, URL base, PropertyMapIF id2urlMap, DefaultMutableTreeNode treeData) {
			m_base = base;
			addAllMappings(id2urlMap);
			m_treeData = treeData;
		}
		
		void addAllMappings(PropertyMapIF map) {
			Property[] props = map.getProperties();
			for(int i = 0; i < props.length; i++) {
				addMapping(props[i].getName(), (String) props[i].getValue());
			}
		}
		
		public void addMapping(String id, String url) {
			m_map.setProperty(id, url);
			LOGGER.log(HELP_ID_MAP_CATEGORY, "Adding id \"" + id + "\" for URL \"" + url + "\"");
		}
		
		public String getURLForID(String id) {
			return (String) m_map.getProperty(id);
		}
		
		public String getIdForURL(URL helpFile) {
			Property[] props = m_map.getProperties();
			for(int i = 0; i < props.length; i++) {
				if(isSame((String) props[i].getValue(), helpFile))
					return props[i].getName();
			}
			return null;
		}
		
		private boolean isSame(String path, URL refURL) {
			try {
				URL u = getClass().getResource(path);
				if(u != null && u.equals(refURL))
					return true;
				return new URL(getBase(), path).equals(refURL);
			} catch (MalformedURLException e) {} // return false on exception
			return false;
		}
		
		public URL getBase() {
			return m_base;
		}
		
		public DefaultMutableTreeNode getTreeData() {
			return m_treeData;
		}
		
		public void setAppletURL(String url) {
			addMapping(APPLET_INFO_ID, url);
		}
		
	}
}
