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
import java.net.URL;

import javax.help.CSH;
import javax.help.DefaultHelpBroker;
import javax.help.HelpBroker;
import javax.help.TOCItem;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.system.MathletRuntime;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public class DefaultMumieHelp extends MumieHelp {

	private final static MumieLogger LOGGER = MumieLogger.getLogger(DefaultMumieHelp.class);
	private final static LogCategory HELP_TOC_CATEGORY = LOGGER.getCategory("help.toc");

	/** Main HelpSet */
	private MumieHelpSet m_mainHelpset = new MumieHelpSet();

	/** Main Help Broker */
	private HelpBroker m_mainHelpBroker;
	
	private ContextSensitiveHelp m_csHelp;

	private JButton m_contextHelpButton;
	
	DefaultMumieHelp(MathletRuntime mathlet) {
		super(mathlet);
		// Load applet help file (if existant)
		String appletHelpFile = m_mathlet.getBasePath() + "/" + "Help_" + getLocale().getLanguage() + ".html";
		if(m_mathlet.getContext() != null && m_mathlet.getContext().getAppletClass().getResource(appletHelpFile) != null)
			m_mainHelpset.setAppletURL(appletHelpFile);
		// create presentation
		m_mainHelpBroker = m_mainHelpset.createHelpBroker();
		// create context sensitive help
		m_csHelp = new ContextSensitiveHelp(m_mainHelpset);
		// set up CSH manager
		CSH.addManager(m_csHelp);
		// create context sensitive help button
		m_contextHelpButton = new JButton(new ImageIcon(getClass().getResource("/resource/icon/contextual_help_16.gif")));
		// add context help action
		m_contextHelpButton.addActionListener(new CSH.DisplayHelpAfterTracking(m_mainHelpBroker));
	}
	
	public void installHelp(JRootPane rootPane, String paneHelpID) {
		if(paneHelpID == null)
			paneHelpID = m_mainHelpset.getHomeID().getIDString();
		m_mainHelpBroker.enableHelpKey(rootPane, paneHelpID, m_mainHelpset);
	}
	
	public void installHelp(final AbstractButton button, String buttonHelpID) {
		if(buttonHelpID == null)
			buttonHelpID = m_mainHelpset.getHomeID().getIDString();
		final String helpID = buttonHelpID;
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				openHelp(helpID, SwingUtilities.windowForComponent(button));
			}
		});
	}
	
	public JButton getContextHelpButton() {
		return m_contextHelpButton;
	}
	
	public void showHelp() {
		openHelp(m_mainHelpset.getHomeID().getIDString());
	}

	protected void openHelp(String helpID) {
		m_mainHelpBroker.setCurrentID(helpID);
		m_mainHelpBroker.setDisplayed(true);
	}

	private void openHelp(String helpID, Window frame) {
		m_mainHelpBroker.setCurrentID(helpID);
		((DefaultHelpBroker)m_mainHelpBroker).setActivationWindow(frame);
		m_mainHelpBroker.setDisplayed(true);
	}
	
	protected DefaultMutableTreeNode createTOCItem(DefaultMutableTreeNode parentNode, String target, String text) {
		if(parentNode != null) {
			for(int i = 0; i < parentNode.getChildCount(); i++) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) parentNode.getChildAt(i);
				TOCItem item = (TOCItem) n.getUserObject();
				if(item.getID() != null && item.getID().getIDString().equals(target)) {
					if(item.getName() == null || item.getName().equals(""))
						item.setName(text);
					LOGGER.log(HELP_TOC_CATEGORY, "Returning existing toc item \"" + text + "\" for target \"" + target + "\"");
					return n;
				}
			}
		}
		return m_mainHelpset.getTOCView().createTOCItem(target, text);
	}
	
	protected void addHelpSet(URL base, PropertyMapIF id2urlMap, PropertyMapIF id2classMap, DefaultMutableTreeNode treeData) {
		// update CS help
		m_csHelp.copyClassMap(id2classMap);
		// add extension's help information as a new help set
		MumieHelpSet hs = new MumieHelpSet(this, base, id2urlMap, treeData);
		// adding to main help set
		m_mainHelpset.add(hs);
	}
	
	public void addHelpSupport(MathletContext mathlet) {
		super.addHelpSupport(mathlet);
		mathlet.addControl(getContextHelpButton(), MathletContext.BOTTOM_LEFT_PANE);
	}
}
