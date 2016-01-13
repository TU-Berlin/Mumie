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

package net.mumie.coursecreator.treeview;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.AWTEvent;

import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import net.mumie.japs.client.JapsClient;
import net.mumie.japs.client.JapsClientException;
import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.gui.ElementMetaPanel;
import net.mumie.coursecreator.tools.StringCutter;

/**
 * Frame fuer das Elementzuweisungsfenster
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: TreeNavigationFrame.java,v 1.35 2009/03/31 19:41:12 vrichter Exp $
 */

public abstract class TreeNavigationFrame extends JFrame implements ActionListener,ComponentListener
{
	
	protected CCController controller;
    private TreePopupMenu treePopup;
    
	protected String selectedDocumentPath = "";
	protected String selectedDocumentTitle = "";
	protected String selectedDocumentPure = "";
	
    protected int selectedDocumentCategory = 0;
    protected String selectedDocumentType = "";
    protected int selectedDocumentId=0;
    protected String selectedDocumentDesc="";
    protected String selectedDocumentCreated="";
    protected String selectedDocumentModified="";
    protected int selectedDocumentSection=0;
        
    private String allowedDocumentType = "none"; 
    
    private IconMap iconMap ; 
    private ColorMap colMap = new ColorMap();
    
    private JButton setButton;
    private JButton previewButton;
    private JButton metainfoButton;
    private JCheckBox showGenericCheck;
    private JCheckBox showNonGenericCheck;
    private JCheckBox showMetaInfoPane;
    
    private JTree ntree;
 
    private ElementMetaPanel metaInfoPane;
    private JSplitPane splitPane;
    private Dimension windowDim;
    private JPanel rootPane;
    
    String setButtonText = "Set";
    String previewButtonText = "Preview";
    String cancelButtonText = "Cancel";
    String reloadButtonText = "Reload";
    String metainfoButtonText = "MetaInfo";
    String showGenericText = "show Generic";
    String showNonGenericText = "show Non-Generic";
    String showMetaInfoPaneText = "show MetaInfos";
    
    Dimension dim = new Dimension(460,650);
    String ICON_PATH;
    static final String ICON_INDEX = "IconIndex.xml";
    
    private boolean preview=false;
    static boolean offline = true;
	
    /**
     * Constructor
     * @param is it offline?
     */
    public TreeNavigationFrame(boolean ofl){
    	this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    	
    	this.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
 			
    			setVisible(false);
    		}
    		
    	});
    	this.addComponentListener(this);
    	offline=ofl;
    }    
    
    /**
     * initialize the Frame and builds the layout of this frame
     * This function has to be called after the JapsClient is created
     * @param ccc - the responsible CCController
     */
    public void init(CCController ccc){
    	this.controller = ccc;
    	this.setSize(this.controller.getConfig().getSIZE_TREE_NAVIGATION());
    	this.setLocation(this.controller.getConfig().getPOSITION_TREE_NAVIGATION());
    	this.ICON_PATH = ccc.getConfig().getICON_PATH();
    	this.iconMap = new IconMap(this.controller);
    	this.treePopup = new TreePopupMenu(this);
    	this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    	
    	this.buildLayout();
    }
    
    /**
     * sets the responsible Controller
     * @param ccc
     */
    public void setController(CCController ccc){
    	this.controller = ccc;
    }
    
    


	/**
     * Action on "set" button press - defined by the herited classes
     */
    abstract protected void setButtonAction();
   
    abstract protected ContentTreeModel MakeTreeTopModel(JapsClient japsClient, String urlPrefix)
    throws IOException, NullPointerException, JapsClientException;
    
    
   /**
    * builds the treepane and the tree 
    * @return
    */
    private JScrollPane getTreePane()
    {
    	ContentTreeModel top_model = null;
    	try{
    		top_model = MakeTreeTopModel(this.controller.getJapsClient(), this.controller.prefixURL());
    	}
    	catch(Exception ex)
    	{
    		babble("getTreePane(): "+ ex + "\n");
    		Throwable t = ex.getCause(); 
    		if (t != null) {
    			babble("\nRoot cause:\n" + t); 
    		} // end of if ()	    
    	}

//  	simply Icon-Action
    	iconMap.setIconBase(ICON_PATH);
    	iconMap.init(iconMap.getIconBase().toString()+ICON_INDEX);

    	colMap = iconMap.getColorMap();

    	ntree = new JTree(top_model);
    	ntree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    	ntree.addTreeWillExpandListener(top_model);
    	
    	MouseListener ml = new MouseAdapter(){
    		public void mousePressed(MouseEvent e) {
    			if (SwingUtilities.isRightMouseButton(e)){
	    		    treePopup.update();
	    		    if (treePopup.hasItems()) {
	    		    	treePopup.show(e.getComponent(), e.getX(), e.getY());
	    		    	
	    		    }
    			}
    		}
    	};
		ntree.addMouseListener(ml);

		if (ntree==null) 
			CCController.dialogErrorOccured(
					"ntree ERROR",
					"TreeNavigationFrame: ERROR: ntree is null",
					JOptionPane.ERROR_MESSAGE);
		if (ntree.getModel()==null) 
			CCController.dialogErrorOccured(
					"ntree ERROR",
					"TreeNavigationFrame: ERROR: ntree.getModel is null",
					JOptionPane.ERROR_MESSAGE);
		

    	ToolTipManager.sharedInstance().registerComponent(ntree);

    	// Listen for events signaling that another tree node has
    	// been selected
    	ntree.addTreeSelectionListener(new TreeSelectionListener() {
    		public void valueChanged(TreeSelectionEvent e) {
    			//getAppletContext().showStatus("Selected some node.");
    			DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
    							ntree.getLastSelectedPathComponent();

    			if (node == null) return;

    			Object nodeInfo = node.getUserObject();

    			DocumentAdapter wpa = (DocumentAdapter)nodeInfo;
    			changedSelectionAction(wpa);

    			babble("node: "+nodeInfo.toString());
    			
    		}
    	});

    	// Now do the complete layout:

    	if (ntree != null) {
    		//Create the scroll pane and add the tree to it. 
    		JScrollPane treeView = layoutTreeAndEmbedInJScrollPane(ntree);

    		Dimension minimumSize = new Dimension(300, 300);
    		treeView.setMinimumSize(minimumSize);
    		treeView.setPreferredSize(new Dimension(300, 600));

    		return treeView;
    	} // end of if (tree != null)
    	else {
    		return null;
    	} // end of if (tree != null) else
    }
    
    
    
    private JScrollPane layoutTreeAndEmbedInJScrollPane (JTree ntree) {

    	UIManager.put("ScrollBar.thumb", colMap.get("scrollBarFront"));
    	UIManager.put("ScrollBar.thumbDarkShadow", Color.black);
    	UIManager.put("ScrollBar.thumbHighlight", Color.white);
    	UIManager.put("ScrollBar.thumbShadow", Color.darkGray);

    	// here, the global 'icons' field -- originally obtained from the
    	// NavigationTreeNursery -- is fed to the MumieTreeCellRenderer
    	TreeCellRenderer rend = new TreeCellRenderer(this.iconMap);

    	rend.setBackground(colMap.getColor("bgcol")); 
    	rend.setBackgroundSelectionColor(colMap.getColor("selcol")); 
    	rend.setTextSelectionColor(colMap.getColor("seltextcol")); 
    	rend.setBackgroundNonSelectionColor(colMap.getColor("bgcol")); 

    	ntree.setCellRenderer(rend);	
    	ntree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION); 
    	Color bg = colMap.getColor("bgcol"); 
    	if (bg != null) {
    		ntree.setBackground(bg);  
    	} // end of if ()
    	ntree.setBorder(new EmptyBorder(getContentPane().getInsets())); 
    	ntree.setRootVisible(true);
    	
    	ntree.add(this.treePopup);
    	
    	JScrollPane jsp = new JScrollPane(ntree);

    	return jsp;
    }
    
    /**
     * Call the actions required on selection change
     */
    protected void changedSelectionAction(DocumentAdapter wpa){
    	selectedDocumentTitle = wpa.getName();
    	selectedDocumentPure = wpa.getPureName();
    	selectedDocumentPath = wpa.getPath();
    	selectedDocumentType = wpa.getDocumentType();
    	selectedDocumentCategory = wpa.getCategory();
    	selectedDocumentId = wpa.getID();
    	selectedDocumentDesc = wpa.getDescription();
    	selectedDocumentCreated = wpa.getCreated();
    	selectedDocumentModified = wpa.getModified();
    	selectedDocumentSection = wpa.getSection();
    	
    	this.enableSetButton();
    	if (this.isPreview()) {
    		this.enableMetainfoButton();
    		this.enablePreviewButton();
    	}

    	updateMetaInfos();
    }
    
    /**
     * @Override
     * to get size and position.
     * necessary for writing size and position to configfile 
     */
    public void componentHidden(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}

	/**
	 * called, when window moved
	 * necesssary for writing position to configfile
	 */
	public void componentMoved(ComponentEvent e) {
		this.controller.getConfig().setPOSITION_TREE_NAVIGATION(e.getComponent().getLocation());
	}

	/**
	 * called, when window resized
	 * necesssary for writing size to configfile
	 */
	public void componentResized(ComponentEvent e) {
		this.windowDim=e.getComponent().getSize();
		this.controller.getConfig().setSIZE_TREE_NAVIGATION(this.windowDim);
		this.setFileValue();
	}

	/**
     * Builds the layout.
     */
    protected void buildLayout(){
    
    	//rootpane
    	rootPane = new JPanel();
    	rootPane.setLayout(new BoxLayout(rootPane,BoxLayout.Y_AXIS));
    	rootPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));   	
    	
    	metaInfoPane = new ElementMetaPanel(this.controller);
    	metaInfoPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    	metaInfoPane.setEditable(false);
    	JScrollPane treeView = new JScrollPane();
    	
    	if (!offline) {
    		treeView = this.getTreePane();
    	}
    	
    	rootPane.add(treeView);// tree
    	rootPane.add(Box.createRigidArea(new Dimension(0, 10)));
    	rootPane.add(createButtonPane());//buttonleiste
    	
    	splitPane= new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    	splitPane.setLeftComponent(metaInfoPane);
    	splitPane.setRightComponent(rootPane);
    	
    	splitPane.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI());
    	
    	this.getContentPane().add(splitPane, BorderLayout.CENTER);
    }

	/**
	 * creates the Buttonpane
	 * @return
	 */
	private JPanel createButtonPane() {
		//setbutton
    	setButton = new JButton(setButtonText);
    	setButton.setEnabled(false);  
    	setButton.setActionCommand(CommandConstants.TREEVIEW_SET);
    	setButton.addActionListener(this);
    	
//    	previewButton
    	previewButton = null;
    	if (this.isPreview()) {
    		previewButton = new JButton(previewButtonText);
    		previewButton.setEnabled(false);  
    		previewButton.setActionCommand(CommandConstants.TREEVIEW_PREVIEW);
    		previewButton.addActionListener(this);
    	}
    	
    	//metainfoButton
    	metainfoButton = new JButton(metainfoButtonText);
		metainfoButton.setEnabled(false);  
		metainfoButton.setActionCommand(CommandConstants.TREEVIEW_METAINFOS);
		metainfoButton.addActionListener(this);
	
    	
    	//cancelbutton
    	JButton cancelButton = new JButton(cancelButtonText);
    	cancelButton.setActionCommand(CommandConstants.TREEVIEW_CANCEL);
    	cancelButton.addActionListener(this);
    	
    	// reload
    	JButton reloadButton = new JButton(reloadButtonText);
    	reloadButton.setActionCommand(CommandConstants.TREEVIEW_RELOAD);
    	reloadButton.addActionListener(this);
    	
//    	showNonGeneric
    	showNonGenericCheck = new JCheckBox(showNonGenericText);
    	showNonGenericCheck.setSelected(this.controller.getConfig().getSHOW_NON_GENERIC());  
    	showNonGenericCheck.setActionCommand(CommandConstants.TREEVIEW_SHOW_NON_GENERIC);
    	showNonGenericCheck.addActionListener(this);
    	
//    	showGeneric
    	showGenericCheck = new JCheckBox(showGenericText);
    	showGenericCheck.setSelected(this.controller.getConfig().getSHOW_GENERIC());  
    	showGenericCheck.setActionCommand(CommandConstants.TREEVIEW_SHOW_GENERIC);
    	showGenericCheck.addActionListener(this);

//    	showGeneric
    	showMetaInfoPane = new JCheckBox(showMetaInfoPaneText);
    	showMetaInfoPane.setSelected(this.controller.getConfig().getSHOW_METAINFOPANE());  
    	showMetaInfoPane.setActionCommand(CommandConstants.TREEVIEW_SHOW_METAINFOPANE);
    	showMetaInfoPane.addActionListener(this);
    	
    	//buttonPane
    	JPanel buttonPane = new JPanel();
    	this.setFixedSize(buttonPane, new Dimension(this.dim.width,40));
    	buttonPane = new JPanel();
    	buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
    	buttonPane.add(Box.createHorizontalGlue());
    	buttonPane.add(setButton);
    	buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    	
    	if (this.isPreview()) {
    		buttonPane.add(previewButton);
    		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    	}
    	buttonPane.add(metainfoButton);
    	buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    	buttonPane.add(cancelButton);
    	buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
    	
    	JPanel checks = new JPanel(new GridLayout(3,1));
    	checks.add(showNonGenericCheck);
    	checks.add(showGenericCheck);
    	checks.add(showMetaInfoPane);
    	
    	buttonPane.add(checks);
    	
    	JButton btSearch = new JButton("Suche");
    	btSearch.addActionListener(this.controller);
    	btSearch.setActionCommand(CommandConstants.TREEVIEW_SEARCH);
    	buttonPane.add(btSearch);
    	return buttonPane;
	}
    
	/** sets for a Component Minimum-, Preferred-, and MaximumSize */
	private void setFixedSize(JComponent comp, Dimension d){
		comp.setSize(d);
		comp.setMinimumSize(d);
		comp.setPreferredSize(d);
		comp.setMaximumSize(d);
	}
	
	
	
	/**
	 * sets the informations of the selected Element  
	 *
	 */
	private void updateMetaInfos(){
		setTitleValue();
		setDescValue();
		setCategoryValue();
		setFileValue();
		setTypValue();
		setIDValue();
		setSectionValue();
		setCreatedValue();
		setModifiedValue();
		setPureValue();
	}
	
	/**
	 * sets title to metainfofield from selectedDocumentTitle
	 * sets selectedDocumentTitle as tooltip
	 */
	private void setTitleValue(){
		this.metaInfoPane.txtfield_title.setToolTipText(
				StringCutter.cutEquidistant(this.selectedDocumentTitle,
						" ", 60, "<br>","<html>", "<html>"));
		this.metaInfoPane.txtfield_title.setText(this.selectedDocumentTitle);
	}
	
	/**
	 * sets description to metainfofield from selectedDocumentDesc
	 * sets selectedDocumentDesc as tooltip
	 */
	private void setDescValue(){
		this.metaInfoPane.txtfield_desc.setToolTipText(
				StringCutter.cutEquidistant(this.selectedDocumentDesc,
						" ", 60, "<br>","<html>", "<html>"));
		this.metaInfoPane.txtfield_desc.setText(selectedDocumentDesc);
	}
	
	/**
	 * sets File to metainfofield from selectedDocumentFile
	 * truncates if nessesary
	 * sets selectedDocumentFile as tooltip
	 */
	private void setFileValue(){
		
		this.metaInfoPane.txtfield_file.setToolTipText(
				StringCutter.cutEquidistant(this.selectedDocumentPath,
						"/", 60,"<br>","<html>","<html>"));
		if (this.getFont()==null) {
			this.metaInfoPane.txtfield_file.setText(this.selectedDocumentPath);
			return;
		}
		
		this.metaInfoPane.txtfield_file.setText(
				StringCutter.cut(this.selectedDocumentPath,
						"/", this.windowDim.width-160
						, this.getFontMetrics(this.getFont()), "..", true));		
	}
	
	/**
	 * sets pureName to metainfofield from selectedDocumentPure
	 * sets selectedDocumentPure as tooltip
	 */
	private void setPureValue(){
		this.metaInfoPane.txtfield_pure.setToolTipText(this.selectedDocumentPure);
		this.metaInfoPane.txtfield_pure.setText(this.selectedDocumentPure);
	}
	
	/**
	 * sets Typ to metainfofield from selectedDocumentTyp
	 * sets selectedDocumentTyp as tooltip
	 */
	private void setTypValue(){
		this.metaInfoPane.txtfield_typ.setToolTipText(this.selectedDocumentType);
		this.metaInfoPane.txtfield_typ.setText(this.selectedDocumentType);
	}
	
////////////////////////////////////////////////////
	// set methodes //
	////////////////////////////////////////////////////
	/**
	 * sets category to metainfofield from selectedDocumentCategory
	 * sets selectedDocumentCategory as tooltip
	 */
	private void setCategoryValue(){
		this.metaInfoPane.txtfield_cat.setToolTipText(String.valueOf(this.selectedDocumentCategory));
		this.metaInfoPane.txtfield_cat.setText(String.valueOf(this.selectedDocumentCategory));
	}
	
	/**
	 * sets ID to metainfofield from selectedDocumentId
	 * sets selectedDocumentId as tooltip
	 */
	private void setIDValue(){
		this.metaInfoPane.txtfield_id.setToolTipText(String.valueOf(this.selectedDocumentId));
		this.metaInfoPane.txtfield_id.setText(String.valueOf(this.selectedDocumentId));
	}
	
	/**
	 * sets Section to metainfofield from selectedDocumentSection
	 * sets selectedDocumentSection as tooltip
	 */
	private void setSectionValue(){
		this.metaInfoPane.txtfield_section.setToolTipText(String.valueOf(this.selectedDocumentSection));
		this.metaInfoPane.txtfield_section.setText(String.valueOf(this.selectedDocumentSection));
	}
	/**
	 * sets Created to metainfofield from selectedDocumentCreated
	 * sets selectedDocumentCreated as tooltip
	 */
	private void setCreatedValue(){
		this.metaInfoPane.txtfield_created.setToolTipText(this.selectedDocumentCreated);
		this.metaInfoPane.txtfield_created.setText(selectedDocumentCreated);
	}
	/**
	 * sets Modified to metainfofield from selectedDocumentModified
	 * sets selectedDocumentModified as tooltip
	 */
	private void setModifiedValue(){
		this.metaInfoPane.txtfield_modified.setToolTipText(this.selectedDocumentModified);
		this.metaInfoPane.txtfield_modified.setText(selectedDocumentModified);
	}

	
	
    public void actionPerformed (ActionEvent e) {
		babble("Actionevent "+e);
		if (e.getActionCommand().equals(CommandConstants.TREEVIEW_CANCEL)) {    			
			setVisible(false);
			    			
		}else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_SET)) {    			
			setButtonAction();
			    			
		} else if (e.getActionCommand().equals("open")) {    			
			setVisible(true);
			
		} else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_PREVIEW)) {
			buttonActionPreview();
			    			
		} else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_METAINFOS)) {
			buttonActionMetainfo();
			    			
		} else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_RELOAD)) {
			buttonActionReload();
			    			
		} else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_SHOW_GENERIC)) {
			this.controller.getConfig().setSHOW_GENERIC(!this.controller.getConfig().getSHOW_GENERIC());
			this.buttonActionReloadTree();
			this.validate();
			this.repaint();
		} else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_SHOW_NON_GENERIC)) {
			this.controller.getConfig().setSHOW_NON_GENERIC(!this.controller.getConfig().getSHOW_NON_GENERIC());
			this.buttonActionReloadTree();
			this.validate();
			this.repaint();
		} else if (e.getActionCommand().equals(CommandConstants.TREEVIEW_SHOW_METAINFOPANE)) {
			this.controller.getConfig().setSHOW_METAINFOPANE(!this.controller.getConfig().getSHOW_METAINFOPANE());
			babble("this.controller.getConfig().getSHOW_METAINFOPANE()"+this.controller.getConfig().getSHOW_METAINFOPANE());
//			new ComponentEvent(this,ComponentEvent.COMPONENT_RESIZED);//FIXME irgendeinen event erzeugen!
//			this.repaint();
			
			this.metaInfoPane.setVisible(this.controller.getConfig().getSHOW_METAINFOPANE());
			this.metaInfoPane.validate();
			this.metaInfoPane.repaint();//FIXME show metainfofield it doesnt work at all..
			this.splitPane.repaint();
			this.splitPane.validate();
			this.rootPane.repaint();
			this.rootPane.validate();
			this.getContentPane().validate();
			this.getContentPane().repaint();
			
		}
	}
    
    /**
     * Method to enable the CourseCreator to (un)hide this frame
     */
    public void setVisible (boolean visible) {    	
    		super.setVisible(visible);
    		if (visible == false) {
    			this.controller.notifyClosingWindow(this.getTitle());
    		}
    	
    }
    
    private void buttonActionPreview()
    {
    	String cmdString = "firefox "+this.controller.prefixURL()+
    		"/protected/view/document/type-name/"+
    		this.selectedDocumentType+"/id/"+this.selectedDocumentId;
    	babble("try to start command:"+cmdString);
    	try {
    		Runtime.getRuntime().exec(cmdString);
    	}
    	catch (IOException e) {
    		CCController.dialogErrorOccured(
					"TreeNavigationFrame IOException",
					"<html>IOException<p>"+e+"</html>",
					JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
         // System.exit(-1);
        }
    }
    
    private void buttonActionMetainfo(){
    	String cmdString = "firefox "+this.controller.prefixURL()+"/protected/info/document/type-name/"+this.selectedDocumentType+"/id/"+this.selectedDocumentId;
    	babble("try to start command:"+cmdString);
    	try {
    		Runtime.getRuntime().exec(cmdString);
    	}
    	catch (IOException e) {
    		CCController.dialogErrorOccured(
					"TreeNavigationFrame IOException",
					"<html>IOException<p>"+e+"</html>",
					JOptionPane.ERROR_MESSAGE);
    		
            e.printStackTrace();
         // System.exit(-1);
        }
    }
    
    private void buttonActionReload(){// TODO db-Reload programmieren
    	babble("reload");
    	
    }
    private void buttonActionReloadTree(){// TODO db-Reload programmieren
//    	JTree newTree = new JTree();
    	Object troot = ntree.getModel().getRoot();
    	if (troot instanceof DefaultMutableTreeNode) {
    		DefaultMutableTreeNode root = (DefaultMutableTreeNode)troot;
    		
    		babble("Title: '" + 
    				((AbstractDocumentAdapter)root.getUserObject()).toString()
    				+ "'\n    Children: " + root.getChildCount() 
    				+ "\n    Depth: " + root.getDepth());
    	
    		this.reload(root);
    	
    	}else System.out.println("not Instance of Defaultbla");
    }
  
    
    private void reload(TreeNode node){// TODO db-Reload programmieren
    	System.out.println("reload "+node);
  
    	if (node.getChildCount()==0){
    		System.out.println("Leaf "+node.toString() );
    		return;
    	}else{ System.out.println("kein leaf"+node.toString() );
    	System.out.println("hat also Blaeter "+node.getChildCount());
    	}
    	for (int i = 0; i< node.getChildCount(); i++){
    		if (node.getChildAt(i).toString().equals(ntree.getModel().getRoot().toString())){
    			System.out.println("dis is rootkind");
    		}else
    		{
    		System.out.println(i+" new kid "+(node.getChildAt(i)).toString() );
    		this.reload(node.getChildAt(i));
    		System.out.println("reload done");
    		}
    	}
    	
    }
    
    /**
     * Let the CourseCreator tell us which DocType to allow 
     */
    public void setAllowedDocumentType (String docType) { 
    	
    	allowedDocumentType = docType;
    	
    	this.enableSetButton();
    }
    
    /**
     * enables setButton if possible <br>
     * depends on <code>isEnabledSetButton</code>
     *
     */
    public void enableSetButton(){
    	babble("this.isEnabledSetButton() "+this.isEnabledSetButton());
    	setButton.setEnabled(this.isEnabledSetButton());
    }
    
    /**
     * enables previewButton if possible <br>
     * depends on <code>isEnabledPreviewButton</code>
     *
     */
    public void enablePreviewButton(){
    	previewButton.setEnabled(this.isEnabledPreviewButton());
    }
    /**
     * enables metainfoButton if possible <br>
     * depends on <code>isEnabledMetainfoButton</code>
     *
     */
    public void enableMetainfoButton(){
    	metainfoButton.setEnabled(this.isEnabledMetainfoButton());
    }
    /**
     * this function returns weather the Setbutton
     * should be anabled or disabled.
     * @return true if an Element is selected which needs an enabled SetButton <br>
     * false otherwise
     *   
     */
    public boolean isEnabledSetButton(){
    	return (selectedDocumentType.equals(allowedDocumentType) 
    			|| selectedDocumentType.equals("generic_summary"));
    }
    
    
    /**
     * this function returns weather the PreviewButton
     * should be anabled or disabled.
     * @return true if an Element is selected which needs an enabled PreviewButton <br>
     * false otherwise
     *   
     */
    public boolean isEnabledPreviewButton(){
    	return (selectedDocumentType.equals("generic_element") || 
				selectedDocumentType.equals("generic_subelement") || 
				selectedDocumentType.equals("generic_problem") ||
				selectedDocumentType.equals("generic_summary"));
    }
    
    /**
     * this function returns weather the MetaInfoButton
     * should be anabled or disabled.
     * @return true if an Element is selected which needs an enabled MetaInfoButton <br>
     * false otherwise
     *   
     */
    public boolean isEnabledMetainfoButton(){
    	return (selectedDocumentType.equals("generic_element") || 
				selectedDocumentType.equals("generic_subelement") || 
				selectedDocumentType.equals("generic_problem") ||
				selectedDocumentType.equals("generic_summary"));    }
    
    
	protected boolean isPreview() {return preview;}
	protected void setPreview(boolean preview) {this.preview = preview;}
    /**
     * Display an error message in the Java Console (or not anotifyClosingWindowt all, if
     * {@link #DEBUG} is false) together with the "name" parameter
     * @param msg what to display
     */
    void babble (String msg){
    	if (this.controller.getConfig().getDEBUG()) System.out.println("TreeNavigationFrame: " + msg); 
    }
}
