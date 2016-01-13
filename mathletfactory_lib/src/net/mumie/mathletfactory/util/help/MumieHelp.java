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

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.tree.DefaultMutableTreeNode;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.appletskeleton.system.SystemDescriptor;
import net.mumie.mathletfactory.appletskeleton.util.MenuButton;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogIDIF;
import net.mumie.mathletfactory.util.extension.Extension;
import net.mumie.mathletfactory.util.extension.HelpExtension;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.property.PropertyTreeNode;

public abstract class MumieHelp {
	
	private final static MumieLogger LOGGER = MumieLogger.getLogger(MumieHelp.class);
	
	private final static LogCategory HELP_SYSTEM_CATEGORY = LOGGER.getCategory("help.system");
	
	public static MumieHelp createHelpSystem(MathletRuntime mathlet) throws Exception {
		MumieHelp help = null;
		if(SystemDescriptor.isJavaHelpAvailable() && mathlet.getParameter("useJavaHelp", "true").equals("true"))
			help = new DefaultMumieHelp(mathlet);
		else
			help = new SimpleMumieHelp(mathlet);
		return help;
	}
	
	protected MathletRuntime m_mathlet;
	
	private Locale m_locale;
	
	public abstract void installHelp(JRootPane rootPane, String paneHelpID);
	
	public abstract void installHelp(final AbstractButton button, String buttonHelpID);
	
	public abstract void showHelp();
	
	protected abstract void openHelp(String helpID);
	
	protected abstract DefaultMutableTreeNode createTOCItem(DefaultMutableTreeNode parentNode, String target, String text);

	protected abstract void addHelpSet(URL base, PropertyMapIF id2urlMap, PropertyMapIF id2classMap, DefaultMutableTreeNode treeData);
	
	public MumieHelp(MathletRuntime mathlet) {
		m_mathlet = mathlet;
		m_locale = mathlet.getLocale();
	}
	
	public Locale getLocale() {
		return m_locale;
	}
	
	public JButton createHelpMenuButton() {
		// define actions for buttons
		Action defaultAction = new AbstractAction(m_mathlet.getMessage("Help")) {
			public void actionPerformed(ActionEvent ae) {
				showHelp();
			}
		};
		Action defaultLongTextAction = new AbstractAction(m_mathlet.getMessage("overview")) {
			public void actionPerformed(ActionEvent ae) {
				showHelp();
			}
		};
		Action showAboutAction = new AbstractAction(m_mathlet.getMessage("About")) {
			public void actionPerformed(ActionEvent ae) {
				if(m_mathlet.getContext() != null)
					m_mathlet.showDialog(DialogIDIF.MATHLET_ABOUT_DIALOG, null);
			}
		};
		// create help button (a menu button)
		MenuButton helpMenuButton = new MenuButton(defaultAction, new Action[] {
				defaultLongTextAction, showAboutAction });
		helpMenuButton.setToolTipText(m_mathlet.getMessage("help_tooltip"));
		return helpMenuButton;
	}
	
	public void addHelpExtension(Extension e, Locale locale) throws FileNotFoundException {
		HelpExtension he = e.getHelpInformation();
		
		// create property tree
		PropertyTreeNode propRootNode = PropertyTreeNode.createTree(he.getMessages().getMessages(locale).getProperties());
		// create root node
		DefaultMutableTreeNode treeData = addNode(null, propRootNode);
		String indexPath = e.getBasePath() + "/help/" + locale.getLanguage() + "/index.html";
		URL baseURL = MumieHelp.class.getResource(indexPath);
		// look for different base path and help file for mathlets
		if(baseURL == null && e.getName().equals(Extension.MATHLET_EXTENSION_NAME)) {
			String appletIndexPath = e.getBasePath() + "/Help_" + locale.getLanguage() + ".html";
			URL appletBaseURL = MumieHelp.class.getResource(appletIndexPath);
			if(appletBaseURL != null)
				baseURL = appletBaseURL;
		}
		if(baseURL == null)
			LOGGER.log(HELP_SYSTEM_CATEGORY, "\"index.html\" file not found: \"" + indexPath + "\" Omitting help from extension " + e.getName());
		else {
			LOGGER.log(HELP_SYSTEM_CATEGORY, "Adding help from extension " + e.getName());
			// add a help system specific new help set
			addHelpSet(baseURL, he.getId2URLMap(locale), he.getId2ClassMap(), treeData);
		}
	}
	
	protected DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, PropertyTreeNode node) {
		String value = node.getProperty() == null ? "" : (String) node.getProperty().getValue();
		DefaultMutableTreeNode tocNode = createTOCItem(parentNode, node.getName(), value);
		if(parentNode != null)
			parentNode.add(tocNode);
		for(Iterator i = node.getChildren().iterator(); i.hasNext(); ) {
			PropertyTreeNode child = (PropertyTreeNode) i.next();
			addNode(tocNode, child);
		}
		return tocNode;
	}
	
	public void addHelpSupport(MathletContext mathlet) {
		// add default help button
		mathlet.addControl(createHelpMenuButton(), MathletContext.BOTTOM_LEFT_PANE);
		// install help for applet root pane (if context exists)
		JRootPane rootPane = null;
		if(mathlet.getAppletFrame() == null)
			rootPane = mathlet.getJApplet().getRootPane();
		else
			rootPane = mathlet.getAppletFrame().getRootPane();
		installHelp(rootPane, null);
	}
}
