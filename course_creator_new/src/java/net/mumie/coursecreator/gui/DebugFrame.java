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

package net.mumie.coursecreator.gui;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.CourseCreator;
import net.mumie.coursecreator.CCConfig;

/**
 * This Class is the Frame for the Debugging
 * 
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: DebugFrame.java,v 1.4 2009/03/30 12:40:19 vrichter Exp $
 * TODO this is not useful for the CC-users otherwise it would be  
 */
public class DebugFrame extends JPanel implements ActionListener{
	
	static final long serialVersionUID=0;
	
	private CourseCreator cc;
	private CCConfig config;

	private boolean local;
	
	private JCheckBox exception;
	
	private JCheckBox debug;
	private JCheckBox debugCCController;
	private JCheckBox debugCourseCreator;
	private JCheckBox debugCCConfig;
	private JCheckBox debugCCModel;
	
	private JCheckBox debugNavGraph;
	private JCheckBox debugNavGraphController;
	private JCheckBox debugNavMarqueeHandler;
	
	private JCheckBox debugMainComponent;
	private JCheckBox debugSubComponent;
	private JCheckBox debugCCEdge;
	private JCheckBox debugBranch;
	
	private JCheckBox debugMetaInfos;
	private JCheckBox debugMetaInfoField;
	private JCheckBox debugMetaInfoFieldController;
	
	private JCheckBox debugContentTreeNavigation;
	private JCheckBox debugContentTreeNode;
	private JCheckBox debugContentTreeModel;
	private JCheckBox debugDocumentAdapter;
	
	private JCheckBox debugGraphLoader;
	

	
	/**
	 * Constructor for a blank LoginDialog
	 * The User has to type in the Server including logindata in order to log into the
	 * JAPS-Server
	 * @param controller - the owner of this dialo gets activated after the OK-Button is pressed (when not already activated)
	 * @throws HeadlessException
	 */
	public DebugFrame(CCConfig c,CourseCreator cc, boolean l) {
		
		this.cc = cc;
		this.config = c;	
		this.local = l;
		
		this.buildLayout();
		this.setSettings();
	}
	

	private void buildLayout(){
		
		// Dimensions of the Dialog		
		int width = 400;
		int heightField = 70;
		int smallheightField = 35;
		int numFields = 17;

		int height = heightField *numFields+smallheightField; 
		 Dimension d = new Dimension(width,heightField);
		 Dimension dsmall = new Dimension(width,smallheightField);
	    
	    // sets the Layout
	    JPanel mainPane = new JPanel(new GridLayout(numFields,1));
	    JPanel firstPane = createPane(dsmall,"Debug und Exceptions",1);
	    
	    this.debug = this.createCheckBox("Debug",
	    		"sollen generell Debugmeldungen angezeigt werden",
	    		CommandConstants.D_DEBUG,firstPane);
	    this.exception = this.createCheckBox("Exceptions",
	    		"sollen generell Fenster mit Exceptions angezeigt werden",
	    		CommandConstants.D_EXCEPTION,firstPane);
	    
	    mainPane.add(firstPane);
	    // panel for general options
	    JPanel genPane = createPane(d,"generelles Debug",2);
	    	    
	    this.debugCCController = this.createCheckBox("CCController",
	    		"Debugmeldungen für die CCController Klasse",
	    		CommandConstants.D_CCCONTROLLER,genPane);
	    this.debugCourseCreator = this.createCheckBox("CourseCreator",
	    		"Debugmeldungen für die CourseCreator Klasse",
	    		CommandConstants.D_COURSE_CREATOR,genPane);
	    this.debugCCModel = this.createCheckBox("CCModel",
	    		"Debugmeldungen für die CCModel Klasse",
	    		CommandConstants.D_CCMODEL,genPane);
	    this.debugCCConfig = this.createCheckBox("CCConfig",
	    		"Debugmeldungen für die CCConfig Klasse",
	    		CommandConstants.D_CCCONFIG,genPane);

	    mainPane.add(genPane);
	    
	    // panel for NavGraph options
	    JPanel navPane = createPane(d,"NavGraph",2);
	    
	    this.debugNavGraph = this.createCheckBox("NavGraph",
	    		"Debugmeldungen für die NavGraph Klasse",
	    		CommandConstants.D_NAVGRAPH,navPane);
	    this.debugNavGraphController = this.createCheckBox("NavGraphController",
	    		"Debugmeldungen für die NavGraphController Klasse",
	    		CommandConstants.D_NAVGRAPH_CONTROLLER,navPane);
	    this.debugNavMarqueeHandler = this.createCheckBox("NavMarqueeHandler",
	    		"Debugmeldungen für die NavMarqueeHandler Klasse",
	    		CommandConstants.D_NAV_MARQUE_HANDLER,navPane);
	    mainPane.add(navPane);
	    
	    // panel for Komponenten options
	    JPanel compPane = createPane(d,"Komponenten",2);
	    
	    this.debugMainComponent = this.createCheckBox("MainComponent",
	    		"Debugmeldungen für die MainComponent Klasse",
	    		CommandConstants.D_MAINCOMPONENT,compPane);
	    this.debugSubComponent = this.createCheckBox("SubComponent",
	    		"Debugmeldungen für die SubComponent Klasse",
	    		CommandConstants.D_SUBCOMPONENT,compPane);
	    this.debugBranch = this.createCheckBox("Branch",
	    		"Debugmeldungen für die Branch Klassen",
	    		CommandConstants.D_BRANCH,compPane);
	    this.debugCCEdge = this.createCheckBox("CCEdge",
	    		"Debugmeldungen für die CCEdge Klassen",
	    		CommandConstants.D_CCEDGE,compPane);
	    mainPane.add(compPane);
	    
	    
	    // panel for MetaInfo options
	    JPanel metaPane = createPane(d,"MetaInfo",2);
	    this.debugMetaInfos = this.createCheckBox("MetaInfos",
	    		"Debugmeldungen für die MetaInfos Klasse",
	    		CommandConstants.D_META_INFOS,metaPane);
	    this.debugMetaInfoField = this.createCheckBox("MetaInfoField",
	    		"Debugmeldungen für die MetaInfoField Klasse",
	    		CommandConstants.D_META_INFO_FIELD,metaPane);
	    this.debugMetaInfoFieldController = this.createCheckBox("MetaInfoFieldController",
	    		"Debugmeldungen für die MetaInfoFieldController Klasse",
	    		CommandConstants.D_META_INFO_FIELD_CONTROLLER,metaPane);
	    mainPane.add(metaPane);
	    
//	  panel for ComponentTree options
	    JPanel treePane = createPane(d,"TreeView",2);
	    
	    this.debugContentTreeModel = this.createCheckBox("ContentTreeModel",
	    		"Debugmeldungen für die ContentTreeModel Klasse",
	    		CommandConstants.D_CONTENT_TREE_MODEL,treePane);
	    this.debugContentTreeNavigation = this.createCheckBox("ContentTreeNavigation",
	    		"Debugmeldungen für die ContentTreeNavigation Klasse",
	    		CommandConstants.D_CONTENT_TREE_NAVIGATION,treePane);
	    this.debugContentTreeNode = this.createCheckBox("ContentTreeNode",
	    		"Debugmeldungen für die ContentTreeNode Klassen",
	    		CommandConstants.D_CONTENT_TREE_NODE,treePane);
	    this.debugDocumentAdapter = this.createCheckBox("DocumentAdapter",
	    		"Debugmeldungen für die DocumentAdapter Klassen",
	    		CommandConstants.D_DOCUMENT_ADAPTER,treePane);
	    mainPane.add(treePane);
	    
//		  panel for miscellaneous options
	    JPanel sonPane = createPane(dsmall,"sonstiges",1);
	    
	    this.debugGraphLoader = this.createCheckBox("GraphLoader",
	    		"Debugmeldungen für die GraphLoader Klasse",
	    		CommandConstants.D_GRAPH_LOADER,sonPane);
	    
	    mainPane.add(sonPane);
	    
	    
	    GridBagConstraints mainPaneStyle = new GridBagConstraints();
	    mainPaneStyle.anchor = GridBagConstraints.CENTER;
	    mainPaneStyle.insets.top = 4;
	    mainPaneStyle.insets.right = 4;
	    mainPaneStyle.insets.bottom = 4;
	    mainPaneStyle.insets.left = 4;
	    mainPaneStyle.gridx = 0;
	    mainPaneStyle.gridy = 0;
	    
	    this.add(mainPane,mainPaneStyle);
	    
	    this.setSize(width, height);	 
	}


	/**
	 * creates an Panel and returns it
	 * ATTENTION: the panel has gridlayout (rows,2)
	 * @param d dimension of the panel (Minimum/Prefered/Maximum Size)
	 * @param name the titel
	 * @param rows number of rows
	 * @return
	 */
	private JPanel createPane(Dimension d,String name,int rows) {
		JPanel p = new JPanel();
	    p.setBackground(this.cc.getBackground());
	    p.setLayout(new GridLayout(rows,2));
	    
	    p.setMinimumSize(d);
	    p.setPreferredSize(d);
	    p.setMaximumSize(d);
	    
	    p.setBorder(new TitledBorder(new EtchedBorder(),name));

		return p;
	} 
	
	/**
	 * creats an Checkbox and returns it
	 * @param name name of the checkbox
	 * @param toolTipText the Tooltiptext
	 * @param cmd the ActionComand
	 * @param pane the panel which this checkbox is added to
	 * @return
	 */
	private JCheckBox createCheckBox(String name, String toolTipText,String cmd,JPanel pane){
		JCheckBox jcb = new JCheckBox(name);
		jcb.setBackground(cc.getBackground());
		jcb.setToolTipText(toolTipText);
		jcb.addActionListener(this);
		jcb.setActionCommand(cmd);
	    pane.add(jcb);
		return jcb;
		
	}
	
	/** sets values for checkboxes from CCConfigClass */
	private void setSettings(){
		this.debug.setSelected(config.getDEBUG());
		this.exception.setSelected(CCConfig.getSHOW_EXCEPTION());
		
		this.debugCCConfig.setSelected(config.getDEBUG_CCCONFIG());
		this.debugCCController.setSelected(config.getDEBUG_CCCONTROLLER());
		this.debugCourseCreator.setSelected(config.getDEBUG_COURSE_CREATOR());
		this.debugCCModel.setSelected(config.getDEBUG_CCMODEL());
		
		this.debugNavGraph.setSelected(config.getDEBUG_NAVGRAPH());
		this.debugNavGraphController.setSelected(config.getDEBUG_NAVGRAPH_CONTROLLER());
		this.debugNavMarqueeHandler.setSelected(config.getDEBUG_NAVGRAPH_MARQUEE_HANDLER());
		
		this.debugMainComponent.setSelected(config.getDEBUG_MAINCOMPONENT());
		this.debugSubComponent.setSelected(config.getDEBUG_SUBCOMPONENT());
		this.debugBranch.setSelected(config.getDEBUG_BRANCH());
		this.debugCCEdge.setSelected(config.getDEBUG_CCEDGE());
		
		this.debugMetaInfoField.setSelected(config.getDEBUG_META_INFO_FIELD());
		this.debugMetaInfoFieldController.setSelected(config.getDEBUG_META_INFO_FIELD_CONTROLLER());
		this.debugMetaInfos.setSelected(config.getDEBUG_META_INFOS());
		
		this.debugContentTreeModel.setSelected(config.getDEBUG_CONTENT_TREE_MODEL());
		this.debugContentTreeNavigation.setSelected(config.getDEBUG_CONTENT_TREE_NAVIGATION());
		this.debugContentTreeNode.setSelected(config.getDEBUG_CONTENT_TREE_NODE());
		this.debugDocumentAdapter.setSelected(config.getDEBUG_DOCUMENT_ADAPTER());
		
		this.debugGraphLoader.setSelected(config.getDEBUG_GRAPH_LOADER());
	}
	
	 /** Implementation of ActionListener Interface. */
    public void actionPerformed(ActionEvent ae) {
    	String cmd = ae.getActionCommand();

    	if (!local) return;
    	
    	if (cmd.equals(CommandConstants.D_EXCEPTION)){
    		this.config.toggleShow_EXCEPTION();
    	}
    	
    	if (cmd.equals(CommandConstants.D_DEBUG)){
    		this.config.toggleDEBUG();
    	}
    	if (cmd.equals(CommandConstants.D_CCCONTROLLER)){
    		this.config.toggleDEBUG_CCCONTROLLER();
    	}
    	else if (cmd.equals(CommandConstants.D_CCCONFIG)){
    		this.config.toggleDEBUG_CCCONFIG();
    	}
    	else if (cmd.equals(CommandConstants.D_CCMODEL)){
    		this.config.toggleDEBUG_CCMODEL();
    	}
    	else if (cmd.equals(CommandConstants.D_COURSE_CREATOR)){
    		this.config.toggleDEBUG_COURSE_CREATOR();
    	}
    	else if (cmd.equals(CommandConstants.D_NAVGRAPH)){
    		this.config.toggleDEBUG_NAVGRAPH();
    	}
    	else if (cmd.equals(CommandConstants.D_NAVGRAPH_CONTROLLER)){
    		this.config.toggleDEBUG_NAVGRAPH_CONTROLLER();
    	}
    	else if (cmd.equals(CommandConstants.D_NAV_MARQUE_HANDLER)){
    		this.config.toggleDEBUG_NAVGRAPH_MARQUEE_HANDLER();
    	}
    	else if (cmd.equals(CommandConstants.D_MAINCOMPONENT)){
    		this.config.toggleDEBUG_MAINCOMPONENT();
    	}
    	else if (cmd.equals(CommandConstants.D_SUBCOMPONENT)){
    		this.config.toggleDEBUG_SUBCOMPONENT();
    	}
    	else if (cmd.equals(CommandConstants.D_BRANCH)){
    		this.config.toggleDEBUG_BRANCH();
    	}
    	else if (cmd.equals(CommandConstants.D_CCEDGE)){
    		this.config.toggleDEBUG_CCEDGE();
    	}
    	else if (cmd.equals(CommandConstants.D_META_INFOS)){
    		this.config.toggleDEBUG_META_INFOS();
    	}
    	else if (cmd.equals(CommandConstants.D_META_INFO_FIELD)){
    		this.config.toggleDEBUG_META_INFO_FIELD();
    	}
    	else if (cmd.equals(CommandConstants.D_META_INFO_FIELD_CONTROLLER)){
    		this.config.toggleDEBUG_META_INFO_FIELD_CONTROLLER();
    	}
    	else if (cmd.equals(CommandConstants.D_CONTENT_TREE_MODEL)){
    		this.config.toggleDEBUG_CONTENT_TREE_MODEL();
    	}
    	else if (cmd.equals(CommandConstants.D_CONTENT_TREE_NAVIGATION)){
    		this.config.toggleDEBUG_CONTENT_TREE_NAVIGATION();
    	}
    	else if (cmd.equals(CommandConstants.D_CONTENT_TREE_NODE)){
    		this.config.toggleDEBUG_CONTENT_TREE_NODE();
    	}
    	else if (cmd.equals(CommandConstants.D_DOCUMENT_ADAPTER)){
    		this.config.toggleDEBUG_DOCUMENT_ADAPTER();
    	}
    	else if (cmd.equals(CommandConstants.D_GRAPH_LOADER)){
    		this.config.toggleDEBUG_GRAPH_LOADER();
    	}
    	
    	this.config.writeConfig(local);
    }
	
    /** set this non-visible */
	public void stop(){
		this.setVisible(false);
	}
	
	void babble(String bab){
		if (this.config.getDEBUG()) System.out.println("DebugFrame: "+bab);
	}
	
}
