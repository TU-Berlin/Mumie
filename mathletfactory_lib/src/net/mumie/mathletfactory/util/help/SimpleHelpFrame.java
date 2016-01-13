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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.mumie.mathletfactory.util.help.SimpleMumieHelp.SimpleHelpSet;
import net.mumie.mathletfactory.util.help.SimpleMumieHelp.TocItem;

public class SimpleHelpFrame extends JFrame implements TreeSelectionListener, HyperlinkListener {

//	private SimpleMumieHelp m_help;
	
	private JTree m_tocTree;
	
	private DefaultTreeModel m_treeModel;
	
	private JEditorPane m_htmlRenderer;
	
	private JSplitPane m_splitPane;
	
	private DefaultMutableTreeNode m_tocRootNode = new DefaultMutableTreeNode(null);
	
	private Vector m_helpSets = new Vector();
	
	SimpleHelpFrame(SimpleMumieHelp help) {
		super("Mumie Hilfe");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		m_treeModel = new DefaultTreeModel(m_tocRootNode);
		m_tocTree = new JTree(m_treeModel);
		m_tocTree.addTreeSelectionListener(this);
		m_tocTree.setRootVisible(false);
		JScrollPane treeScroller = new JScrollPane(m_tocTree);
		
		m_htmlRenderer = new JEditorPane();
		m_htmlRenderer.setEditable(false);
		m_htmlRenderer.setContentType("text/html");
		m_htmlRenderer.addHyperlinkListener(this);
		JScrollPane htmlScroller = new JScrollPane(m_htmlRenderer);
		m_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroller, htmlScroller);
		
		getContentPane().add(m_splitPane);
		
		pack();
		setSize(800, 600);
		center();
	}
	
  public void hyperlinkUpdate(HyperlinkEvent e) {
		try {
	    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    	m_htmlRenderer.setPage(e.getURL());
	    	String newTarget = getIDFromHelpSets(e.getURL());
	    	if(newTarget != null)
	    		loadTocItem(newTarget);
	    	else
	    		m_tocTree.setSelectionPath(null);
	    }
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
  }

	public void valueChanged(TreeSelectionEvent e) {
		if(e.isAddedPath() && !e.getNewLeadSelectionPath().equals(e.getOldLeadSelectionPath())) {
			DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
			TocItem newItem = (TocItem) newNode.getUserObject();
			setCurrentID(newItem.getTarget());
		}
	}

	public void setCurrentID(String idString) {
		URL targetURL = getURLFromHelpSets(idString);
		if(targetURL == null && !SimpleMumieHelp.HOME_ID.equals(idString))
			targetURL = getURLFromHelpSets(SimpleMumieHelp.HOME_ID);
		if(targetURL == null)
			return;
		loadHTMLFile(targetURL);
		loadTocItem(idString);
	}
	
	private void loadHTMLFile(URL htmlFile) {
		try {
			m_htmlRenderer.setPage(htmlFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadTocItem(String idString) {
		DefaultMutableTreeNode node = getNode(m_tocRootNode, idString);
		if(node == null) {
			m_tocTree.setSelectionPath(null);
			return;
		}
		TreeNode[] path = m_treeModel.getPathToRoot(node);
		m_tocTree.setSelectionPath(new TreePath(path));
	}
	
	private DefaultMutableTreeNode getNode(DefaultMutableTreeNode node, String target) {
		TocItem nodeItem = (TocItem) node.getUserObject();
		if(nodeItem != null && target.equals(nodeItem.getTarget()))
			return node;
		for(int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode n = getNode((DefaultMutableTreeNode) node.getChildAt(i), target);
			if(n != null)
				return n;
		}
		return null;
	}
	
	private URL getURLFromHelpSets(String idString) {
		for(int i = 0; i < m_helpSets.size(); i++) {
			try {
				SimpleHelpSet hs = (SimpleHelpSet) m_helpSets.get(i);
				String relURL = hs.getURLForID(idString);
				if(relURL == null)
					continue;
				URL targetURL = getClass().getResource(relURL);
				if(targetURL != null)
					return targetURL;
				targetURL = new URL(hs.getBase(), relURL);
				if(targetURL != null)
					return targetURL;
			} catch(MalformedURLException e) {
				continue;
			}
		}
		return null;
	}
	
	private String getIDFromHelpSets(URL helpFile) {
		for(int i = 0; i < m_helpSets.size(); i++) {
			SimpleHelpSet hs = (SimpleHelpSet) m_helpSets.get(i);
			String id = hs.getIdForURL(helpFile);
			if(id != null)
				return id;
		}
		return null;
	}
  /** Centers this frame on the screen. */
  public void center() {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screenSize.width - getWidth())/2;
    int y = (screenSize.height - getHeight())/2;
    setLocation(x,y);
  }
  
  private void addNodesToTOC(DefaultMutableTreeNode node) {
  	if(m_tocRootNode == null) {
  		m_tocRootNode = node;
  	} else {
			for(int i = 0; i < node.getChildCount(); i++) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getChildAt(i);
				addNode(m_tocRootNode, n);
			}
  	}
		m_treeModel.setRoot(m_tocRootNode);
		m_treeModel.reload(m_tocRootNode);
  }
  
  private DefaultMutableTreeNode addNode(DefaultMutableTreeNode parentNode, DefaultMutableTreeNode tocNode) {
  	DefaultMutableTreeNode newTocNode = addNodeImpl(parentNode, tocNode);
		for(Enumeration i = tocNode.children(); i.hasMoreElements(); ) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) i.nextElement();
			addNode(newTocNode, child);
		}
		return newTocNode;
  }
  
  private DefaultMutableTreeNode addNodeImpl(DefaultMutableTreeNode parentNode, DefaultMutableTreeNode tocNode) {
		for(int i = 0; i < parentNode.getChildCount(); i++) {
	  	TocItem tocItem = (TocItem) tocNode.getUserObject();
	  	String target = tocItem.getTarget();
	  	String text = tocItem.getText();
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) parentNode.getChildAt(i);
			TocItem item = (TocItem) n.getUserObject();
			if(item.getTarget() != null && item.getTarget().equals(target) && item.getText().equals(text))
				return n;
		}
		DefaultMutableTreeNode newTocNode = (DefaultMutableTreeNode) tocNode.clone();
		parentNode.add(newTocNode);
		return newTocNode;
  }

  void addHelpSet(SimpleHelpSet helpSet) {
  	m_helpSets.add(helpSet);
  	addNodesToTOC(helpSet.getTreeData());
  }
}
