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

package net.mumie.coursecreator;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;


import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import net.mumie.coursecreator.events.CCModelChangeListener;
import net.mumie.coursecreator.events.CCModelChangedEvent;
import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.graph.cells.MainComponentCell;
import net.mumie.coursecreator.graph.cells.SubComponentConstants;
import net.mumie.coursecreator.graph.cells.MainComponentConstants;
import net.mumie.coursecreator.graph.cells.CellConstants;
import net.mumie.coursecreator.graph.cells.BranchCellConstants;
import net.mumie.coursecreator.gui.MetaInfoField;
import net.mumie.coursecreator.tools.StringCutter;

/**
 * <p>
 * The CourseCreator class provides the application layout.
 * </p>type filter text
 * http://www.eclipse.org/downloads/
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: CourseCreator.java,v 1.86 2009/03/31 19:41:11 vrichter Exp $
 */

public class CourseCreator extends JFrame implements CCModelChangeListener{

	static final long serialVersionUID=0;
	private CCModel model;
    private CCController controller;
        
    private JPanel centerpane;
    
    private JPanel mgpanel;
    private JComponent maingraphpanel;
    private JComponent parentPanel;
    private JSplitPane split;
    private JSplitPane splitPaneV;
    private JPanel pPanel;
      private static JLabel lbl_status;
    private JLabel lblParentTitle;
    private JLabel lblNameGraph;
    private Color background = new Color(0.95f,0.95f,0.95f);

    public JButton btConnect;
    public JButton btSwap;
    public JButton btRed;
    public JButton btExists;
    public JButton btDelete;
    public JButton btZoomIn,btZoomOut;
    public JButton btEdgeDir;
    
    public JButton btRemoveGraph;
    public JButton btSaveGraph;
    public JButton btNewGraph;
    public JButton btLoadGraph;
    public JButton btUndo;

    private JButton btNewNavGraph;
    private JButton btNewFlatGraph;
    

    private JButton btNewCourse;
    private JButton btNewCourseSection;
    private JButton btNewProblem;
    
    public JToolBar mainElToolbar;
    private JPanel mainELPanel;
    
    public JToolBar secToolbar;
    private JPanel secPanel;
    
    public JToolBar subSecToolbar;
    private JPanel subSecPanel;
    
    public JToolBar subElToolbar;
    private JPanel subElPanel;
    
    public JToolBar problemToolbar;
    private JPanel problemPanel;
    
    public JToolBar branchToolbar;
    private JPanel branchPanel;
    
    JCheckBoxMenuItem miShowGrid;
    JMenuItem miSetGridSize;
    JMenuItem miSetMoveStep;
    
    public MetaInfoField metaInfoPanel; 
    
    private JMenu bufferMenu=null;
    private JMenuBar jmenu;
    protected JPanel mainpane;

    private JMenuItem closeGraph;
    
    protected JCheckBoxMenuItem miElemSel;
    protected JCheckBoxMenuItem miHirarchy;
    protected JCheckBoxMenuItem miOrder;
    protected JCheckBoxMenuItem miShowRed;
    
    public static boolean comment=false;
    public static String MM_BUILD_PREFIX="";
    
    public static FontMetrics metrics;
    private String startWithFile = ""; // the Filename if mmcc is called with Filename
    /**
     * Creates a new CourseCreator instance.
     * Uses a new frame.
     */
    public CourseCreator() {
    	// nothing - the important stuff is done in the init()-Method
    }

    /**
     * Creates a new CourseCreator instance.
     * the programm starts with "mmcc file"
     */
    public CourseCreator(String s) {
    	// nothing - the important stuff is done in the init()-Method
    	if (s.startsWith("./")) s=s.substring(2);
    	this.startWithFile =System.getProperty("user.dir")+"/"+s;
    	
    }

    /**
     * The init-Method intantiates the main components of the CC
     *
     */
    private void init(){
    	this.controller = new CCController(this);
    	
    	setCertPath();
    	
    	this.model = new CCModel(this.controller, this);
    	this.model.addModelChangeListener(this);
	  	this.setTitle("CourseCreator - ");
	  	this.controller.showLoginDialog(true);
    	this.setContentPane(makeContentPane());
    	
	  	this.setSize(this.controller.getConfig().getSIZE_COURSE_CREATOR());
    	this.setLocation(this.controller.getConfig().getPOSITION_COURSE_CREATOR());
	  	this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	  	metrics = this.getGraphics().getFontMetrics();
	  	this.addComponentListener(this.controller);
	 
	  	if (!this.startWithFile.equals("")){
	  		String dir = ""; 
	  		if (this.startWithFile.contains("/")){
	  			dir = this.startWithFile.substring(0, startWithFile.lastIndexOf("/"));
	    		this.startWithFile= startWithFile.substring(startWithFile.lastIndexOf("/")+1);
	    	}
	  		if (-2==this.model.load(dir,this.startWithFile))
	  			this.controller.dialogOpenFailed(this.startWithFile);
	  	} 
    }

	/**
	 * sets certificate<br>
	 * removes old one if one exists
	 */
	public void setCertPath() {
		
		if (System.getProperty("javax.net.ssl.trustStore")!=null){
			System.clearProperty("javax.net.ssl.trustStore");
		}
		
		if (!controller.getConfig().getCERT_DIR().equals("")){
			System.setProperty("javax.net.ssl.trustStore",controller.getConfig().getCERT_DIR());
		}
	}

    /**
     * Create the content pane.
     */
    public JPanel makeContentPane() {

    	this.setBackground(this.background);

    	// main pane containing all other panes
    	mainpane = new JPanel();

    	mainpane.setBackground(this.background);
    	mainpane.setBorder(BorderFactory.createLineBorder(Color.black,2));

    	mainpane.setLayout(new BorderLayout(2,2));

    	pPanel = new JPanel();
    	pPanel.setBackground(this.background);
    	pPanel.setMinimumSize(new Dimension(350,200));
    	pPanel.setPreferredSize(new Dimension(400,250));
    	pPanel.setLayout(new BorderLayout());
    	lblParentTitle = new JLabel();
    	lblParentTitle.setBackground(this.background);
    	pPanel.add(lblParentTitle, BorderLayout.NORTH);


    	MetaInfos metas;
    	if (this.model.getFirstGraph()==null)
    		metas = new MetaInfos(this.controller);
    	else metas=this.model.getFirstGraph().getMetaInfos();
    	metaInfoPanel = new MetaInfoField(this,metas);
    	this.model.addModelChangeListener(metaInfoPanel);
    	
    	metaInfoPanel.setBackground(this.background);
    	
    	Dimension d = new Dimension(400,400);
    	metaInfoPanel.setMinimumSize(d);
    	metaInfoPanel.setPreferredSize(d);
    	metaInfoPanel.setMaximumSize(d);
    	
    	// subpane in the center of the mainpane
    	centerpane = new JPanel();
    	centerpane.setBackground(this.background);
    	centerpane.setLayout(new BorderLayout());
    	centerpane.setMinimumSize(new Dimension(350,450));
    	centerpane.setPreferredSize(new Dimension(450,450));

    	mgpanel = new JPanel();
    	mgpanel.setLayout(new BorderLayout());
    	mgpanel.setBackground(this.background);
    	
    	
    	lblNameGraph = new JLabel(MetaInfos.DEFAULT_NAME);
    	lblNameGraph.setBackground(this.background);
    	mgpanel.add(lblNameGraph, BorderLayout.NORTH);

    	maingraphpanel = new JScrollPane((NavGraph)this.model.getFirstGraph());
    	
    	maingraphpanel.setBackground(this.background);
    	
    	//following stuff is need for drag n Drop//TODO
//    	maingraphpanel.setTransferHandler(new TransferHandler() {
//    		static final long serialVersionUID=0;
//            @Override
//            public int getSourceActions(JComponent c) {
//               return COPY;
//            }
//            
//            @Override
//            protected Transferable createTransferable(JComponent c) {
//               String test = "test";
//               System.out.println("dis ist der maingraph: "+test); 
//               
//                Transferable ss = new StringSelection(c.getName());
//                System.out.println(ss); 
//               return ss;
//            }
//
//         }); 
//    	maingraphpanel.addMouseListener((MouseListener)this.controller);
//    	 DropTarget dT = new DropTarget (maingraphpanel, this.controller);

    	
    	
    	
    	mgpanel.add(maingraphpanel, BorderLayout.CENTER);
    	
    	mgpanel.addComponentListener(this.controller);
    	
    	centerpane.add(mgpanel, BorderLayout.CENTER);

    	lbl_status = new JLabel();
    	lbl_status.setBackground(this.background);
    	lbl_status.setHorizontalAlignment(SwingConstants.RIGHT);
    	lbl_status.setText(" ");
    	centerpane.add(lbl_status, BorderLayout.SOUTH);
    	
    	// toolbar above all SplitPanes
    	JPanel splits = new JPanel();
    	splits.setBackground(this.background);
    	splits.setLayout(new BorderLayout());
    	JToolBar toolbar = createToolBar();
    	toolbar.setBackground(this.background);
    	splits.add(toolbar, BorderLayout.NORTH);
    	
    	splitPaneV= new JSplitPane(JSplitPane.VERTICAL_SPLIT);  	
    	
    	splitPaneV.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI());
    	splitPaneV.setBackground(this.background);
    	splitPaneV.setResizeWeight(1);
    	JSplitPane splitPaneH= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    	splitPaneH.setUI(new javax.swing.plaf.basic.BasicSplitPaneUI());
    	splitPaneH.setBackground(this.background);
    	
    	splitPaneH.setResizeWeight(1);
    	this.split = splitPaneH;
    	splitPaneH.setLeftComponent(centerpane);
    	splitPaneH.setRightComponent(splitPaneV);

    	splitPaneV.setLeftComponent(pPanel);
    	splitPaneV.setRightComponent(metaInfoPanel);

    	splits.add(splitPaneH, BorderLayout.CENTER);

    	// subpane containing toolbars for Section, Elements and Subelements
    	JPanel elementpane = new JPanel();
    	elementpane.setBackground(this.background);
    	elementpane.setLayout(new BoxLayout(elementpane, BoxLayout.Y_AXIS));

     	this.btNewCourse = createToolbarButton("newCourse_norm.png","newCourse_norm.png",
     			"Neuer Kurs",CommandConstants.NEW_COURSE,this.controller, false);
     	this.btNewCourseSection = createToolbarButton("newCourseSection_norm.png","newCourseSection_norm.png",
    			"Neuer Kursabschnitt",CommandConstants.NEW_COURSESECTION,this.controller, false);
     	this.btNewProblem = createToolbarButton("newProblem_norm.png","newProblem_norm.png",
    			"Neuer Kurs",CommandConstants.NEW_PROBLEM,this.controller, false);
    	
     	elementpane.add(this.btNewCourse);
    	elementpane.add(this.btNewCourseSection);
    	elementpane.add(this.btNewProblem);
    	
     	this.btNewNavGraph = createToolbarButton("newNavGraph_norm.png","newNavGraph_norm.png",
    			"Neuer NavGraph",CommandConstants.NEW_NAVGRAPH,this.controller, false);
     	this.btNewFlatGraph = createToolbarButton("newFlatGraph_norm.png","newFlatGraph_gr.png",
    			"Neuer Flacher Kurs",CommandConstants.NEW_FLAT,this.controller, false);
    	
     	elementpane.add(this.btNewNavGraph);
    	elementpane.add(this.btNewFlatGraph);
    	
    	
    	// panel containing sectiontoolbar
    	secPanel = new JPanel();
    	secPanel.setBackground(this.background);
    	secPanel.setBorder(new TitledBorder(new EtchedBorder(),"Kursabschnitt"));
    	secPanel.setLayout(new BoxLayout(secPanel, BoxLayout.X_AXIS));
    	secPanel.setMinimumSize(new Dimension(100,80));
    	secPanel.setPreferredSize(new Dimension(100,80));
    	secPanel.setMaximumSize(new Dimension(100,80));
    	elementpane.add(secPanel);

    	secToolbar = createSecToolBar();
    	secToolbar.setBackground(this.background);
    	JPanel secToolbarPanel = new JPanel();
    	secToolbarPanel.setBackground(this.background);
    	secToolbarPanel.setLayout(new BoxLayout(secToolbarPanel, BoxLayout.X_AXIS));
    	secToolbar.setMinimumSize(new Dimension(60,50));
    	secToolbar.setPreferredSize(new Dimension(60,50));
    	secToolbar.setMaximumSize(new Dimension(60,50));
    	secToolbarPanel.add(secToolbar);

    	secPanel.add(secToolbarPanel);

    	// panel containing subsection toolbar
    	subSecPanel = new JPanel();
    	subSecPanel.setBackground(this.background);
    	subSecPanel.setBorder(new TitledBorder(new EtchedBorder(),"Trainingsblatt"));
    	subSecPanel.setLayout(new BoxLayout(subSecPanel, BoxLayout.X_AXIS));
    	subSecPanel.setMinimumSize(new Dimension(100,60));
    	subSecPanel.setPreferredSize(new Dimension(100,60));
    	subSecPanel.setMaximumSize(new Dimension(100,60));
    	elementpane.add(subSecPanel);

    	subSecToolbar = createSubSecToolBar();
    	subSecToolbar.setBackground(this.background);
    	JPanel subSecToolbarPanel = new JPanel();
    	subSecToolbarPanel.setBackground(this.background);
    	subSecToolbarPanel.setLayout(new BoxLayout(subSecToolbarPanel, BoxLayout.X_AXIS));
    	subSecToolbar.setMinimumSize(new Dimension(76,40));
    	subSecToolbar.setPreferredSize(new Dimension(76,40));
    	subSecToolbar.setMaximumSize(new Dimension(76,40));
    	subSecToolbarPanel.add(subSecToolbar);

    	subSecPanel.add(subSecToolbarPanel);

    	// panel containing mainelementtoolbar
    	mainELPanel = new JPanel();
    	
    	mainELPanel.setBackground(this.background);
    	mainELPanel.setBorder(new TitledBorder(new EtchedBorder(),"Element"));
    	mainELPanel.setLayout(new BoxLayout(mainELPanel, BoxLayout.X_AXIS));
    	mainELPanel.setMinimumSize(new Dimension(100,110));
    	mainELPanel.setPreferredSize(new Dimension(100,110));
    	mainELPanel.setMaximumSize(new Dimension(100,110));
    	elementpane.add(mainELPanel);

    	mainElToolbar = createElementToolBar();
    	mainElToolbar.setBackground(this.background);
    	mainElToolbar.setMinimumSize(new Dimension(74,110));
    	mainElToolbar.setPreferredSize(new Dimension(74,110));
    	mainElToolbar.setMaximumSize(new Dimension(74,110));

    	mainELPanel.add(mainElToolbar);

    	// panel containing subelementtoolbar
    	subElPanel = new JPanel();
    	subElPanel.setBackground(this.background);
    	subElPanel.setBorder(new TitledBorder(new EtchedBorder(),"Subelement"));
    	subElPanel.setLayout(new BoxLayout(subElPanel, BoxLayout.X_AXIS));
    	subElPanel.setMinimumSize(new Dimension(100,100));
    	subElPanel.setPreferredSize(new Dimension(100,100));
    	subElPanel.setMaximumSize(new Dimension(100,100));
    	elementpane.add(subElPanel);

    	subElToolbar = createSubToolBar();
    	subElToolbar.setBackground(this.background);
    	subElToolbar.setMinimumSize(new Dimension(74,100));
    	subElToolbar.setPreferredSize(new Dimension(74,100));
    	subElToolbar.setMaximumSize(new Dimension(74,100));

    	subElPanel.add(subElToolbar);
//    	setEnabled(subElToolbar,false);


    	// panel containing problem toolbar
    	// panel containing mainelementtoolbar
    	problemPanel = new JPanel();
    	problemPanel.setBackground(this.background);
    	problemPanel.setBorder(new TitledBorder(new EtchedBorder(),"Aufgabe"));
    	problemPanel.setLayout(new BoxLayout(problemPanel, BoxLayout.X_AXIS));
    	problemPanel.setMinimumSize(new Dimension(100,55));
    	problemPanel.setPreferredSize(new Dimension(100,55));
    	problemPanel.setMaximumSize(new Dimension(100,55));
    	elementpane.add(problemPanel);

    	problemToolbar = createProblemToolBar();
    	problemToolbar.setBackground(this.background);
    	problemToolbar.setMinimumSize(new Dimension(74,55));
    	problemToolbar.setPreferredSize(new Dimension(74,55));
    	problemToolbar.setMaximumSize(new Dimension(74,55));

    	problemPanel.add(problemToolbar);

    	branchPanel = new JPanel();
    	branchPanel.setBackground(this.background);
    	branchPanel.setBorder(new TitledBorder(new EtchedBorder(),"Verzweigung"));
    	branchPanel.setLayout(new BoxLayout(branchPanel, BoxLayout.X_AXIS));
    	branchPanel.setMinimumSize(new Dimension(100,55));
    	branchPanel.setPreferredSize(new Dimension(100,55));
    	branchPanel.setMaximumSize(new Dimension(100,55));
    	elementpane.add(branchPanel);
    	
    	branchToolbar = createBranchToolBar();
    	branchToolbar.setBackground(this.background);
    	branchToolbar.setMinimumSize(new Dimension(74,55));
    	branchToolbar.setPreferredSize(new Dimension(74,55));
    	branchToolbar.setMaximumSize(new Dimension(74,55));

    	branchPanel.add(branchToolbar);
    	
    	
    	// putting all panes together
    	mainpane.add(elementpane, BorderLayout.WEST);
    	mainpane.add(splits, BorderLayout.CENTER);

    	// adding menubar
    	jmenu = createMenuBar();
    	jmenu.setBackground(this.background);
    	mainpane.add(jmenu, BorderLayout.NORTH);
	
    	controller.createActionInputMap();

    	this.setInitStat();
    	
    	return mainpane;
    }

    /**
     * Creates the MenuBar.
     */
    private JMenuBar createMenuBar() {

    	JMenuBar jmenu = new JMenuBar();

    	/* --- File Menu --- */
    	JMenu m = new JMenu("Datei");
    	m.setBackground(this.background);

    	JMenuItem mi = new JMenuItem("Neu");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.NEW_GRAPH);
    	m.add(mi);
//    	mi = new JMenuItem("Neuer flacher Kurs");
//    	mi.setBackground(this.background);
//    	mi.addActionListener(this.controller);
//    	mi.setActionCommand(CommandConstants.NEW_FLAT);
//    	m.add(mi);
    	m.addSeparator();

    	mi = new JMenuItem("Laden");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.LOAD);
    	m.add(mi);

    	m.addSeparator();

    	mi = new JMenuItem("Speichern");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.SAVE);
    	m.add(mi);

    	mi = new JMenuItem("Speichern (als neues Dokument)");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.SAVEAS);
    	m.add(mi);

//    	mi = new JMenuItem("Speichern + Online Checkin");
//    	mi.setBackground(this.background);
//    	mi.addActionListener(this.controller);
//    	mi.setActionCommand(CommandConstants.SAVECHECKIN);
//    	m.add(mi);
    	
    	m.addSeparator();
    	
    	closeGraph = new JMenuItem("Graph schliessen");
    	closeGraph.setBackground(this.background);
    	closeGraph.addActionListener(this.controller);
    	closeGraph.setActionCommand(CommandConstants.REMOVE_GRAPH);
    	closeGraph.setEnabled(false);
    	m.add(closeGraph);
    	
    	mi = new JMenuItem("Beenden");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.QUIT);
    	m.add(mi);
    	jmenu.add(m);

    	/* --- Edit Menu --- */

    	m = new JMenu("Bearbeiten");
    	m.setBackground(this.background);

    	mi = new JMenuItem("rote Linie \u00fcberpr\u00fcfen");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.CHECK_REDLINE);
    	m.add(mi);
    	
    	mi = new JMenuItem("rote Linie l\u00f6schen");
    	mi.setBackground(this.background);
    	mi.addActionListener(this.controller);
    	mi.setActionCommand(CommandConstants.DEL_REDLINE);
    	m.add(mi);
    	
    	m.addSeparator();

    	JMenu mG = new JMenu("Graph pr\u00fcfen");
    	mG.setBackground(this.background);
    	mG.addActionListener(this.controller);
    	
    	JMenuItem mii = new JMenuItem("Quellen anzeigen");
    	mii.setBackground(this.background);
    	mii.addActionListener(this.controller);
    	mii.setActionCommand(CommandConstants.SHOW_SOURCES);
    	mG.add(mii);
    	
    	mii = new JMenuItem("Senken anzeigen");
    	mii.setBackground(this.background);
    	mii.addActionListener(this.controller);
    	mii.setActionCommand(CommandConstants.SHOW_DRAINS);
    	mG.add(mii);

    	mii = new JMenuItem("auf Kreise \u00fcberpr\u00fcfen");
    	mii.setBackground(this.background);
    	mii.addActionListener(this.controller);
    	mii.setActionCommand(CommandConstants.TEST_FOR_CIRCLES);
    	mG.add(mii);
    	
    	m.add(mG);
    	jmenu.add(m);

    	/* --- View Menu --- */
    	m = new JMenu("Ansicht");
    	m.setBackground(this.background);

    	miElemSel = new JCheckBoxMenuItem("Elementzuweisungsfenster");
    	miElemSel.setBackground(this.background);
    	miElemSel.addActionListener(this.controller);
    	miElemSel.setActionCommand(CommandConstants.SHOW_ELEMENTSELECTOR);
    	m.add(miElemSel);
    	
    	JCheckBoxMenuItem miShowArrows = new JCheckBoxMenuItem("Richtungspfeile");
    	miShowArrows.setBackground(this.background);
    	miShowArrows.addActionListener(this.controller);
    	miShowArrows.setActionCommand(CommandConstants.SHOW_ARROWS);
    	m.add(miShowArrows);
    	
    	miSetMoveStep = new JMenuItem("MoveStep");
    	miSetMoveStep.setBackground(this.background);
    	miSetMoveStep.addActionListener(this.controller);
    	miSetMoveStep.setActionCommand(CommandConstants.SET_MOVESTEP);
    	m.add(miSetMoveStep);
    	
    	miSetGridSize = new JMenuItem("Gitter");
    	miSetGridSize.setBackground(this.background);
    	miSetGridSize.addActionListener(this.controller);
    	miSetGridSize.setActionCommand(CommandConstants.SET_GRIDSIZE);
    	m.add(miSetGridSize);
    	
    	miShowGrid = new JCheckBoxMenuItem("Gitter anzeigen");
    	miShowGrid.setBackground(this.background);
    	miShowGrid.addActionListener(this.controller);
    	miShowGrid.setActionCommand(CommandConstants.SHOW_GRID);
    	miShowGrid.setSelected(this.getModel().getShowGrid());
    	m.add(miShowGrid);
    	
    	JMenuItem miShowAlias = new JMenuItem("HTTPS-Server");
    	miShowAlias.setBackground(this.background);
    	miShowAlias.addActionListener(this.controller);
    	miShowAlias.setActionCommand(CommandConstants.SHOW_ALIAS);
    	m.add(miShowAlias);
    	
    	jmenu.add(m);

    	/* --- Configuration Menu --- */
    	m = new JMenu("Einstellungen");
    	m.setBackground(this.background);

    	JMenuItem miLogin = new JMenuItem("neuer Server-Login");
    	miLogin.setBackground(this.background);
    	miLogin.addActionListener(this.controller);
    	miLogin.setActionCommand(CommandConstants.LOGIN);
    	m.add(miLogin);
    	
    	JMenuItem miAssingCert = new JMenuItem("Zertifikat \u00e4ndern");
    	miAssingCert.setBackground(this.background);
    	miAssingCert.addActionListener(this.controller);
    	miAssingCert.setActionCommand(CommandConstants.ASSIGN_CERTS);
    	m.add(miAssingCert);
    	
    	JMenuItem miDebugDialog = new JMenuItem("Debugeinstellungen");
    	miDebugDialog.setBackground(this.background);
    	miDebugDialog.addActionListener(this.controller);
    	miDebugDialog.setActionCommand(CommandConstants.SHOW_DEBUG_DIALOG);
//    	m.add(miDebugDialog); //TODO add this, when you are programmer 
    	
    	JMenuItem miKeySetDialog = new JMenuItem("Tastenk\u00fcrzel");
    	miKeySetDialog.setBackground(this.background);
    	miKeySetDialog.addActionListener(this.controller);
    	miKeySetDialog.setActionCommand(CommandConstants.SHOW_KEYSET_DIALOG);
    	m.add(miKeySetDialog);
    	
//    	JMenuItem miPresentationDialog = new JMenuItem("Darstellung");//TODO den Darstellungskrempel hier zentralisieren
//    	miPresentationDialog.setBackground(this.background);
//    	miPresentationDialog.addActionListener(this.controller);
//    	miPresentationDialog.setActionCommand(CommandConstants.SHOW_PRESENTATION_DIALOG);
//    	m.add(miPresentationDialog);
    	
    	jmenu.add(m);
    	
    	/* --- Windows Menu --- */
    	this.bufferMenu = new JMenu("Fenster");
    	this.bufferMenu.setBackground(this.background);
    	jmenu.add(createBuffersMenu(this.bufferMenu));
    	
    	
    	m = new JMenu("Help");
    	m.setBackground(this.background);
    	
    	
    	JMenuItem miHelp = new JMenuItem("Hilfe");
    	miHelp.setBackground(this.background);
    	miHelp.addActionListener(this.controller);
    	miHelp.setActionCommand(CommandConstants.SHOW_HELP);
    	m.add(miHelp);
    	
    	JMenuItem miAbout = new JMenuItem("About");
    	miAbout.setBackground(this.background);
    	miAbout.addActionListener(this.controller);
    	miAbout.setActionCommand(CommandConstants.SHOW_ABOUT);
    	m.add(miAbout);
    	
    	jmenu.add(m);
    	
//    	JMenuItem miAbout = new JMenuItem("About"); //TODO version
//    	miAbout.setBackground(this.background);
//    	miAbout.addActionListener(this.controller);
//    	miAbout.setActionCommand(CommandConstants.SHOW_ABOUT);
//    	m.add(miAbout);
    	
    	return jmenu;
    }
    
    /**
     * 
     * creates the buffer menu
     * @param bm
     * @return
     */
    private JMenu createBuffersMenu(JMenu bm){
    	bm.removeAll();

    	if (this.model.getBufferedGraphs()!=null){
    		int  numberOfBufferedGraphs = this.model.getBufferedGraphs().size();
    		for (int i=0;i<numberOfBufferedGraphs;i++){

    			JMenuItem mi = new JMenuItem(this.getBufferMenuEntryText(i));
    			mi.setBackground(this.background);
    			mi.addActionListener(this.controller);
    			mi.setActionCommand(CommandConstants.GRAPH+i);

    			bm.add(mi);
    		}
    	}
    	return bm;
    }

    /**
     * loads an Image from the local FileSystem
     * @param uri
     * @return
     * @throws IOException
     */
    public static byte[] loadImage(URI uri) throws IOException {
		
		InputStream ist = new FileInputStream(uri.getPath());
		
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		byte[] bytes = new byte[1024]; 
		
		int r; 
		
		while ((r = ist.read(bytes,0,1024)) > 0) {
			out.write(bytes,0,r); 
		} // end of while ()
		
		return out.toByteArray(); 
	}
    
    /**
     * Create a ToolbarButton.
     */
    private JButton createToolbarButton(String picName, String disPicName, String sToolTip, String command, Object al, boolean isDragable) {
    	
    	ImageIcon enabled = null;
    	ImageIcon disabled = null;
    	
    	try {
    		enabled = new ImageIcon(loadImage(new URI(this.controller.getConfig().getICON_PATH() + picName)));
    		disabled = new ImageIcon(loadImage(new URI(this.controller.getConfig().getICON_PATH() + disPicName)));
    	} catch (IOException ioe) {
        	babble("CourseCreator:createToolbarButton():"+ioe.toString());
    	} catch (URISyntaxException use){
    		babble("CourseCreator:createToolbarButton():"+use.toString());
    	}
    	    	
    	JButton b = new JButton(enabled);
    	b.setDisabledIcon(disabled);
    	
    	b.setMargin(new Insets(0,0,0,0));
    	
    	if (sToolTip != null) {
    		b.setToolTipText(sToolTip);
    	}
    	int height = enabled.getIconHeight();
    	int width = enabled.getIconWidth();
    	
    	b.setActionCommand(command);
    	b.setMinimumSize(new Dimension(width,height));
    	b.setPreferredSize(new Dimension(width,height));
    	b.setMaximumSize(new Dimension(width,height));
    	
    	b.addActionListener((ActionListener)al);
    	
    	if (isDragable){
//    	if (false){
	    	b.setTransferHandler(new TransferHandler() {
	    		static final long serialVersionUID=0;
	            @Override
	            public int getSourceActions(JComponent c) {
	               return COPY;
	            }
	            
	            @Override //TODO drag n drop
	            protected Transferable createTransferable(JComponent c) {
	          
	               babble("createTransferable mit Inhalt: "+c.getName()); 
	               
//	                Transferable ss = new StringSelection(c.getName());
	                Transferable ss = new Transferable() {
					
						public boolean isDataFlavorSupported(DataFlavor flavor) {
							// TODO Auto-generated method stub
							return false;
						}
					
						public DataFlavor[] getTransferDataFlavors() {
							// TODO Auto-generated method stub
							DataFlavor[] df = new DataFlavor[3];
							df[1].setHumanPresentableName("katze");
							df[0].setHumanPresentableName("hund");
							return df;
						}
					
						public Object getTransferData(DataFlavor flavor)
								throws UnsupportedFlavorException, IOException {
							
							return "test";
						}
					
					};
	               return ss;//TODO drag n drop
	            }

	         }); 
	    	b.addMouseListener((MouseListener)al);
	    	//TODO this is need for drag n drop
//	    	 DropTarget dT = new DropTarget (b, this.controller);

//	    	b.setDropTarget(this.maingraphpanel);
    	}
    	
    	return b;
    	
    }

    /**
     * Creates the SectionCell-toolbar.
     */
    private JToolBar createSecToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);
    	toolbar.setLayout(new BoxLayout(toolbar,BoxLayout.Y_AXIS));

    	toolbar.add(createToolbarButton("section_norm.png","section_gr.png", "Kursabschnitt", 
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_SECTION),
    			this.controller, true));

    	return toolbar;
    }

    /**
     * Creates the Course Subsection (i.e.: training section) toolbar.
     */
    private JToolBar createSubSecToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);
    	toolbar.setLayout(new BoxLayout(toolbar,BoxLayout.X_AXIS));

    	toolbar.add(createToolbarButton("HA_button_norm.png","HA_button_akt.png",
    			"Trainingsblatt_HomeWork",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_HOMEWORK),
    			this.controller, true));
    	toolbar.add(createToolbarButton("PL_button_norm.png","PL_button_akt.png",
    			"Trainingsblatt_Prelearn",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_PRELEARN),
    			this.controller, true));

    	return toolbar;

    }

    
    /**
     * Creates a problem toolbar.
     */
    private JToolBar createProblemToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);
    	toolbar.setLayout(new GridLayout(1,2));

    	toolbar.add
    	(createToolbarButton("a_button_norm.png","a_button_akt.png", 
    			"Applet-Aufgabe",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_APPLET),
    			this.controller, true));

    	toolbar.add
    	(createToolbarButton("m_button_norm.png","m_button_akt.png", 
    			"MC-Aufgabe",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_MCHOICE),
    			this.controller, true));

    	return toolbar;

    }

    
    /**
     * Creates a branchnode toolbar.
     */
    private JToolBar createBranchToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);

    	toolbar.setLayout(new GridLayout(1,2));
    	toolbar.add(
    			createToolbarButton("and_norm.png","and_gr.png", "UND-Knoten",
    					this.createButtonCmd(CellConstants.CELLTYPE_BRANCH,BranchCellConstants.ANDSYM),
    					this.controller, true));

    	toolbar.add(
    			createToolbarButton(
    					"or_norm.png", "or_gr.png", "ODER-Knoten",
    					this.createButtonCmd(CellConstants.CELLTYPE_BRANCH,BranchCellConstants.ORSYM),
    					this.controller, true));



    	return toolbar;

    }

    
    /**
     * Creates the ElementCell-toolbar.
     */
    private JToolBar createElementToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);
    	toolbar.setLayout(new GridLayout(3,2));
    	
    	toolbar.add (createToolbarButton("m_button_norm.png","m_button_akt.png", "Motivation",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_MOTIVATION),
    			this.controller, true));
    	toolbar.add(createToolbarButton("d_button_norm.png","d_button_akt.png","Definition",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_DEFINITION),
    			this.controller, true));
    	toolbar.add(createToolbarButton("t_button_norm.png","t_button_akt.png","Theorem",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_THEOREM),
    			this.controller, true));
    	toolbar.add(createToolbarButton("l_button_norm.png","l_button_akt.png","Lemma",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_LEMMA),
    			this.controller, true));
    	toolbar.add(createToolbarButton("a_button_norm.png","a_button_akt.png","Anwendung",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_APPLICATION),
    			this.controller, true));
    	toolbar.add(createToolbarButton("al_button_norm.png","al_button_akt.png","Algorithmus",
    			createButtonCmd(CellConstants.CELLTYPE_MAINCOMPONENT,MainComponentConstants.CAT_ALGORITHM),
    			this.controller, true));

    	return toolbar;
    }
    
    
    /**
     * Creates the SubElementCell-toolbar.
     *
     */
    private JToolBar createSubToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);
    	toolbar.setLayout(new GridLayout(4,2));

    	toolbar.add(createToolbarButton("her_button_norm.png","her_button_akt.png","Herleitung",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_DEDUCTION),
    			this.controller, true));
    	toolbar.add(createToolbarButton("bew_button_norm.png","bew_button_akt.png","Beweis",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_PROOF),
    			this.controller, true));
    	toolbar.add(createToolbarButton("mot_button_norm.png","mot_button_akt.png","Motivation",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_MOTIVATION),
    			this.controller, true));
    	toolbar.add(createToolbarButton("bem_button_norm.png","bem_button_akt.png","Bemerkung",	
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_REMARK),
    			this.controller, true));
    	toolbar.add(createToolbarButton("his_button_norm.png","his_button_akt.png","Historie",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_HISTORY),
    			this.controller, true));
    	toolbar.add(createToolbarButton("vis_button_norm.png","vis_button_akt.png","Visualisierung",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_VISUALIZATION),
    			this.controller, true));
    	toolbar.add(createToolbarButton("bsp_button_norm.png","bsp_button_akt.png","Beispiel",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_EXAMPLE),
    			this.controller, true));
    	toolbar.add(createToolbarButton("tst_button_norm.png","tst_button_akt.png","Test",
    			createButtonCmd(CellConstants.CELLTYPE_SUBCOMPONENT,SubComponentConstants.CAT_TEST),
    			this.controller, true));

    	return toolbar;
    }

    /**
     * Creates the toolbar for main panel.
     */
    private JToolBar createToolBar() {

    	JToolBar toolbar = new JToolBar();
    	toolbar.setFloatable(false);

    	btConnect = createToolbarButton("connect_norm.png","connect_gr.png",
    			"Verbinden zweier Elemente/Kursabschnitte/\u00dcbungen",
    			CommandConstants.CONNECT,this.controller, false);
    	toolbar.add(btConnect);
    	
    	btRed = createToolbarButton("red_norm.png","red_gr.png",
    			"Kante auf/von rote/r Linie",
    			CommandConstants.TOGGLERED,this.controller, false);
    	toolbar.add(btRed);
    	
    	btExists = createToolbarButton("exist_norm.png","exist_gr.png",
    			"Kante ins/aus Netzwerk",
    			CommandConstants.TOGGLEEXISTS,this.controller, false);
    	toolbar.add(btExists);
    	
    	toolbar.addSeparator();

    	btDelete = createToolbarButton("delete_norm.png","delete_gr.png",
    			"L\u00f6schen der markierten Komponenten",
    			CommandConstants.DELETE,this.controller, false);
    	toolbar.add(btDelete);

    	
    	toolbar.addSeparator();
    	
    	btZoomIn = createToolbarButton("zoom_in.png","zoom_in_gr.png",
    			"hinein Zoomen",
    			CommandConstants.ZOOM_IN,this.controller, false);
    	toolbar.add(btZoomIn);
    	
    	btZoomOut = createToolbarButton("zoom_out.png","zoom_out_gr.png",
    			"heraus Zoomen",
    			CommandConstants.ZOOM_OUT,this.controller, false);
    	toolbar.add(btZoomOut);

    	toolbar.addSeparator();
    	
    	btEdgeDir = createToolbarButton("change_edge_direction_ak.png","change_edge_direction.png",
    			"Kanten Richtung \u00e4ndern",
    			CommandConstants.CHANGE_EDGE_DIRECTION,this.controller, false);
    	toolbar.add(btEdgeDir);
    	
    	btSwap = createToolbarButton("change_norm.png","change_gr.png",
    			"Vertauschen zweier Elemente/Kursabschnitte/\u00dcbungen",
    			CommandConstants.SWAP,this.controller, false);
    	toolbar.add(btSwap);
    	
    	toolbar.addSeparator();
    	toolbar.addSeparator();
    	toolbar.addSeparator();
    	toolbar.addSeparator();
    	
    
    	btNewGraph = createToolbarButton("new_norm.png","new_gr.png",
    			"Neuer Graph",
    			CommandConstants.NEW_GRAPH,this.controller, false);
    	toolbar.add(btNewGraph);
    	
    	btLoadGraph = createToolbarButton("load_norm.png","load_gr.png",
    			"Laden",
    			CommandConstants.LOAD,this.controller, false);
    	toolbar.add(btLoadGraph);
    	
    	btSaveGraph = createToolbarButton("save_norm.png","save_gr.png",
    			"Speichern dieses Graphen",
    			CommandConstants.SAVE,this.controller, false);
    	toolbar.add(btSaveGraph);
    	
    	toolbar.addSeparator();
    	
    	btUndo = createToolbarButton("undo_norm.png","undo_gr.png",
    			"R\u00fcckg\u00e4ngig",
    			CommandConstants.UNDO,this.controller, false);
    	toolbar.add(btUndo);

    	toolbar.addSeparator();
    	
    	btRemoveGraph = createToolbarButton("removeGraph_norm.png","removeGraph_gr.png",
    			"L\u00f6schen dieses Graphen",
    			CommandConstants.REMOVE_GRAPH,this.controller, false);
    	toolbar.add(btRemoveGraph);
    	
    	return toolbar;
    }


    /**
     * creates Commands for ComponentToolBar 
     * Comamnd is addCell_docType_cat e.g. addCell_2_4
     */
    private String createButtonCmd(int docType, String cat){
    	StringBuffer sb = new StringBuffer(CommandConstants.ADD_CELL+"_");
    	int catI;
    	if (docType==CellConstants.CELLTYPE_BRANCH)
	    	catI = BranchCellConstants.getCategoryAsInt(cat);
    	else if (docType==CellConstants.CELLTYPE_MAINCOMPONENT)
    		catI = MainComponentConstants.getCategoryAsInt(cat);
    	else catI = SubComponentConstants.getCategoryAsInt(cat);
    	
    	sb.append(docType+"_"+Integer.toString(catI));
    	
    	return sb.toString(); 
    	
    }   

    /**
     * Implementation of CCModelChangeListener Interface.
     * Called when changes in the model occur.
     */
    public void modelChanged(CCModelChangedEvent le) {
    	
    	this.buttonsEnabling();
    	this.toolBarsEnabling();
    	this.menuEnabling();
    	if(le.getType()==CCModelChangedEvent.CHANGED_GRAPHS)
    	{ 	
    		
			this.mgpanel.remove(maingraphpanel);
			if (this.getModel().getKursTyp()== CCModel.KURS_NAVGRAPH)
				maingraphpanel = new JScrollPane((NavGraph)this.model.getFirstGraph());
			else {
				
				maingraphpanel = new JScrollPane();
				((JScrollPane)maingraphpanel).getViewport().setBackground(Color.white);
				
			}
			this.mgpanel.add(maingraphpanel,BorderLayout.CENTER);
		
			Dimension d = pPanel.getSize();
			pPanel = new JPanel();
			pPanel.setBackground(this.background);
			pPanel.setMinimumSize(new Dimension(250,150));
			pPanel.setPreferredSize(d);
			pPanel.setMaximumSize(new Dimension(350,315));
			pPanel.setLayout(new BorderLayout());
		 
			parentPanel = new JScrollPane();
			parentPanel.setMinimumSize(new Dimension(250,150));
			parentPanel.setPreferredSize(d);
			parentPanel.setMaximumSize(new Dimension(350,315));
			
			pPanel.add(this.lblParentTitle,BorderLayout.NORTH);
			pPanel.add(parentPanel, BorderLayout.CENTER);
		
		  	splitPaneV.setLeftComponent(pPanel);
			// workaround - don't know how to refresh the splitpane
			this.split.setDividerLocation(this.split.getDividerLocation());
		
			setLblNameGraph();
			
			this.createBuffersMenu(this.bufferMenu);
    	}
    	if (le.getType()==CCModelChangedEvent.SET_PATH){
    		
    		this.getModel().getFirstGraph().repaint();
    		this.createBuffersMenu(this.bufferMenu);
    	}
		
    }

    /** enables some menu items */
    public void menuEnabling() {
    	boolean b = this.getModel().getKursTyp()==CCModel.KURS_NAVGRAPH;
    	this.miSetGridSize.setEnabled(b);
    	this.miSetMoveStep.setEnabled(b);
    	this.miShowGrid.setEnabled(b);
    	
    }
    
    /** sets button enabled or disabled */
    public void buttonsEnabling() {
    	
    	if (this.getModel().getFirstGraph()==null){
    		btConnect.setEnabled(false);
    		btDelete.setEnabled(false);
    		btEdgeDir.setEnabled(false);
    		btExists.setEnabled(false);
    		btRed.setEnabled(false);
    		btSwap.setEnabled(false);
    		btZoomIn.setEnabled(false);
    		btZoomOut.setEnabled(false);
    		btUndo.setEnabled(false);
    		return;
    	}
    		
		btConnect.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_CONNECT));
		btDelete.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_DELETE));
		btEdgeDir.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_EDGEDIR));
		btExists.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_EXISTS));
		btRed.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_RED));
		btSwap.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_SWAP));
		btZoomIn.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_ZOOMIN));
		btZoomOut.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_ZOOMOUT));
		btUndo.setEnabled(
				this.getModel().getFirstGraph().getButtonEnable(CommandConstants.BUTTON_UNDO));
    }
    
    /**  enable the buttons according to the selections in the different graphs
     * (main, parent and clipboard)
     */
    protected void toolBarsEnabling() {
    	
    	// metaInfofenster
    	metaInfoPanel.enableNameDescPanel(false);
    	metaInfoPanel.enableStatusPanel(false);
    	metaInfoPanel.enableELClass(false);
    	metaInfoPanel.visibleEnableCLPanel(false);
		metaInfoPanel.visibleEnableCategoryFrame(false);
		metaInfoPanel.visibleEnableDateFrame(false);
    	
		// linker Frame - Komponenten
		setEnabled(branchToolbar, false);
		this.branchPanel.setVisible(false);
		
		setEnabled(secToolbar, false);
		this.secPanel.setVisible(false);
		
		setEnabled(subSecToolbar, false);
		this.subSecPanel.setVisible(false);
		
		setEnabled(mainElToolbar, false);
		this.mainELPanel.setVisible(false);
		
		setEnabled(subElToolbar, false);
		this.subElPanel.setVisible(false);
		
		setEnabled(problemToolbar, false);
		this.problemPanel.setVisible(false);
		
		// linker Frame - neuer Graphtyp 
		this.btNewCourse.setVisible(false);
		this.btNewCourseSection.setVisible(false);
		this.btNewProblem.setVisible(false);
		
		//linker frame - neuer Graph
		this.btNewNavGraph.setVisible(false);
		this.btNewFlatGraph.setVisible(false);
		
		if (this.model.getFirstGraph()==null){
			this.btNewNavGraph.setVisible(true);
			this.btNewFlatGraph.setVisible(true); //TODO set visible, when flatgraph is implemented
//			this.btNewFlatGraph.setEnabled(false);
			return;
		}else {
			metaInfoPanel.enableNameDescPanel(true);
			metaInfoPanel.enableStatusPanel(true);
		}
		
		if (this.model.getFirstGraph().getMetaInfos().getGraphType()
				==MetaInfos.GRAPHTYPE_UNDEFINED) {
			this.btNewCourse.setVisible(true);
			this.btNewCourseSection.setVisible(true);
			this.btNewProblem.setVisible(true);
			return;
		}
		setEnabled(branchToolbar, true);
		this.branchPanel.setVisible(true);
	    
	    if (this.model.getFirstGraph().isSectionGraph()){
	    	setEnabled(secToolbar,true);	
	    	this.secPanel.setVisible(true);
	    	setEnabled(subSecToolbar,true);	
	    	this.subSecPanel.setVisible(true);
	    	
	    	
	    	metaInfoPanel.visibleEnableCLPanel(true);
	    		    	
	    }else if (this.model.getFirstGraph().isProblemGraph()){
			
			setEnabled(problemToolbar, true);
			this.problemPanel.setVisible(true);
			
			metaInfoPanel.visibleEnableCategoryFrame(true);
			metaInfoPanel.visibleEnableDateFrame(true);
			
			
	    } else {//elementgraph

	    	setEnabled(mainElToolbar, true);
			this.mainELPanel.setVisible(true);
			
			setEnabled(subElToolbar, true);
			this.subElPanel.setVisible(true);
	    }
    
	    int nr = this.model.getFirstGraph().getSelectionCount();
	   
	    if (nr==1){
	    	Object o = this.model.getFirstGraph().getSelectionCell();
	    	
	    	 
	    	if (o instanceof MainComponentCell){
	    		setEnabled(subElToolbar, 
	    				MainComponentConstants.isElementCategory(((MainComponentCell)o).getCategory()));
	    		setEnabled(subSecToolbar, MainComponentConstants.isSectionCategory(((MainComponentCell)o).getCategory()));
	    	}else {
	    		setEnabled(subElToolbar, false);
	    		setEnabled(subSecToolbar, false);
	    	}
	    	
	    }else {

	    	setEnabled(subElToolbar, false);
    		setEnabled(subSecToolbar, false);
	    }
    }

    
    /** setzt initialisierungsstatus (ohne Graphen) */
    protected void setInitStat(){
    	babble("this.controller.getModel().getBufferedGraphs().size() "+this.controller.getModel().getBufferedGraphs().size()) ;
    	this.enableCloseGraph(false);
    	this.buttonsEnabling();
    	this.toolBarsEnabling();
    	this.menuEnabling();
    	this.modelChanged(new CCModelChangedEvent(this,null,CCModelChangedEvent.CHANGED_GRAPHS));
    } 
    
	/**
	 * setzt titel des Graphen
	 */
	public void setLblNameGraph() {
		// view name of graph in main window
		if (this.model.getFirstGraph()==null){
			this.lblNameGraph.setText("");
			return;
		}
		String name = this.model.getFirstGraph().getMetaInfos().getName();
		if((name == null) || (name.length() == 0)) name = "-- kein Name --";
		
		this.lblNameGraph.setText(name);
	}

    /**
     * Display String in status label.
     * @param s String, the text to display.
     */
	public static void printStatus(String s, String path) {
		if (lbl_status==null){
			CCController.dialogErrorOccured("CourseCreator: ERROR",
					"CourseCreator: lbl_status is null", 
					
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		if (path!=null) lbl_status.setToolTipText(path);
		lbl_status.setText(" "+s);// sieht ohne " " gequetscht aus
    	lbl_status.setHorizontalAlignment(SwingConstants.LEFT);
    }	
    
    
	/**
	 * trunks the statusstring
	 * the status is the label, a blank charakter and the truncated path 
	 * the path is truncated iff the lbl_status is to short for label and path;
	 * path is truncated ahead a slash and if its truncated the path starts with ../
	 * <br>
	 * if path is empty, 
	 * 
	 * @param label 
	 * @param path
	 */
	public static void printStatus(String label, String path, String isCell) {

		if ((label.equals("")) && (path.equals(""))&& isCell.equals("false")){printStatus(path,null);return;}
    	if (path.equals("")){ printStatus(label +" kein Document zugewiesen",null);return;}
    	if (label.equals("")){
    		printStatus(StringCutter.cut(path, "/", lbl_status.getSize().getWidth()-20, CourseCreator.metrics,"..",true),
    					StringCutter.cutEquidistant(path, "/", 80, "<br>", "<html>", "<html>"));
    		return;
    	}
    	
    	//now both != ""

    	String s = StringCutter.cut(path, "/", lbl_status.getSize().getWidth()-CourseCreator.metrics.stringWidth(label+" - ")-20, CourseCreator.metrics,"..",true);
    	s = label +" - "+ s;
    	printStatus(s,StringCutter.cutEquidistant(path, "/", 80, "<br>", "<html>", "<html>"));
    	
    }
    /**
     * Enable/disable all buttons in given toolbar.
     * @param toolbar toolbar containing the buttons to enable/disable
     * @param enabled boolean, flag to decide wether to enable or disable the buttons
     *                true: enable, false: disable
     */
    public void setEnabled(JToolBar toolbar, boolean enabled) {
    	Component[] content = toolbar.getComponents();
    	for (int i = 0; i < content.length; i++) {
    		content[i].setEnabled(enabled);
    	}
    }

    /**
     * Receives notification of other applets' closing windows.
     * used for communication with ElementSelector- &
     * CourseLoader-Applet
     * @param name  the name of the applet as defined by the
     *              corresponding parameters in the html code
     */
    public void notifyClosingWindow(String name) {
    	if(name.equals("ElementSelector")) {
    		this.miElemSel.setState(false);
    	}
    }


    /**
     * Set the ParentPanel Label to the given text.
     * @param lbl The new text of the label
     */
    public void setParentLbl(String lbl) {
    	this.lblParentTitle.setText(lbl);
    }
    
    /**
     * enables or disables the closegraph
     * @param val
     */
    public void enableCloseGraph(boolean val){
    	closeGraph.setEnabled(val);
    	btRemoveGraph.setEnabled(val);
    } 
    /**
     * sets Entrys for the Buffersmenu
     * @param line 
     */
    private void setBufferMenuEntry(int line){
    	
    	this.bufferMenu.getItem(line).setText(this.getBufferMenuEntryText(line));
    }
    
    
    /**
     * returns the Menutext for the buffered graphs
     * @param line
     * @return
     */
    private String getBufferMenuEntryText(int line){

    	NavGraph g = (NavGraph)this.model.getBufferedGraphs().get(line);
    	
    	String type = MetaInfos.getGraphtypeAsString(g.getMetaInfos().getGraphType());
    	
    	//number and graphName
    	String newName = String.valueOf(line).concat(" ("+type+") ").concat(g.getMetaInfos().getName());
    	
    	//filename
    	if (g.getMetaInfos().getSaveName()==null) newName = newName.concat(" kein Dateiname");
		else newName = newName.concat(" "+g.getMetaInfos().getSaveName()+".meta.xml");
    	
    	//changed..
    	if (g.getChanged()) newName = newName.concat(" *");
    	
    	return newName;
    }
	/**
	 * ActionEventFunction
	*/
     public void actionPerformed(ActionEvent e){
    	
    	String cmd = e.getActionCommand();
    	if (cmd.equals(CommandConstants.BUFFERREFRESH)) {
    		if (this.model.getFirstGraph()!=null)
    		this.setBufferMenuEntry(0);
    	}else if (cmd.equals(CommandConstants.UPDATE_METAINFODATE)){
    		babble("startTime "+ this.metaInfoPanel.getDateStartSpinner().getModel().getValue());
    	}
    }

    public CCModel getModel() {
		return model;
	}

	public FontMetrics getMetrics() {
		return metrics;
	}

	public void setMetrics(FontMetrics met) {
		metrics = met;
	}

	/**
	 * sets check mark in menu to state
	 * @param state
	 */
	public void setElementSelState(boolean state) {
		this.miElemSel.setSelected(state);
	}

	public boolean getElementSelState() {
		return this.miElemSel.isSelected();
	}
	
	/**
	 * //TODO testfunktion fuer flatgraph  
	 *
	 */
	protected void createFlatGraphPanel(){
		
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Wurzel" ); 
		 
		 
		for ( int ast = 0; ast < 4; ast++ ) 
		{ 
		  DefaultMutableTreeNode node = new DefaultMutableTreeNode( "Knoten " + ast ); 
		  root.add( node ); 
		 
		  for ( int blatt = 1; blatt < 4; blatt++ ) 
		    node.add( new DefaultMutableTreeNode("Blatt " + (ast*3+blatt )) ); 
		} 
		 
		JTree tree = new JTree( root ); 
		 
		this.mgpanel.remove(maingraphpanel);

		maingraphpanel=new JScrollPane( tree ) ;
		this.mgpanel.add(maingraphpanel,BorderLayout.CENTER);
		mgpanel.repaint();
		mgpanel.validate();
		
		
	}
	
    /**
     * the main entrance method to the whole CourseCreator
     * @param args - arguments like the server.xml-File
     */
    public static void main(String[] args)
    {
    	CourseCreator cc;
    	// parsing the arguments
    	if(args.length>0){
    		MM_BUILD_PREFIX=args[0];
    	}    	
    	if (args.length>1){//open with file
    		cc = new CourseCreator(args[1]);
    	}else cc = new CourseCreator();
    	
    	cc.init();
    }

	void babble(String bab){
		if (this.controller.getConfig().getDEBUG_COURSE_CREATOR() && this.controller.getConfig().getDEBUG()) System.out.println("CourseCreator:"+bab);
	}

	public CCController getController() {
		return controller;
	}
	
	public MetaInfoField getMetaInfoField(){
		return this.metaInfoPanel;
	}
	
}
