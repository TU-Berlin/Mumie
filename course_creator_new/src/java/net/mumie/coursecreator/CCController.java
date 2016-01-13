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

import java.awt.Cursor;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.LinkedList;


import javax.swing.*;
import javax.swing.filechooser.FileFilter;


import com.jgraph.event.*;

import net.mumie.coursecreator.events.*;
import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.graph.NavGraph;

import net.mumie.coursecreator.graph.cells.ComponentCell;
import net.mumie.coursecreator.graph.cells.MainComponentCell;
import net.mumie.coursecreator.graph.cells.MainComponentConstants;
import net.mumie.coursecreator.graph.cells.SubComponentCell;
import net.mumie.coursecreator.graph.cells.SubComponentConstants;
import net.mumie.coursecreator.gui.CCLoginDialog;
import net.mumie.coursecreator.gui.DebugDialog;
import net.mumie.coursecreator.gui.ElementTreeSearchDialog;
import net.mumie.coursecreator.gui.KeySetDialog;
import net.mumie.coursecreator.gui.PresentationDialog;
import net.mumie.coursecreator.threads.HelpCompiler;
import net.mumie.coursecreator.threads.ShowSourceInEditor;
import net.mumie.coursecreator.treeview.ContentTreeNavigationFrame;

import net.mumie.japs.client.JapsClient;


/**
 * <p>
 * The CCController has three major functions.
 * </p><ul><li>
 * It is the ActionListener for the buttons of the CourseCreator.
 * After determining the command it calls the appropriate method in the CCModel.
 * </li><li>
 * It activates or deactivates the buttons of the CourseCreator according
 * to the main graph and the selected cells.
 * </li><li>
 * It shows appropriate dialogs and messages depending on the command called 
 * or to call.
 * </li>
 * </ul>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:sinha@math.tu-berlin.de">Uwe Sinha</a>
 * @version $Id: CCController.java,v 1.121 2009/03/31 12:11:11 vrichter Exp $
 */
public class CCController implements ActionListener,GraphSelectionListener,
CCModelChangeListener,MouseListener,DropTargetListener,
ComponentListener{

    private CCModel model;
    private CCConfig config;
    
    public static String dummyfile;
    
    /**
     * @TODO as soon as the CC is connected to MMCDK - this Frame 
     * is useful to choose the Graph in the database
     */
//    private CourseLoaderTreeNavigationFrame courseLoader;
    
    private InputMap imap;
    private ActionMap amap;

    public static CourseCreator frame;
    
    private CCLoginDialog cclogin;
    private DebugDialog debugDialog;
    private KeySetDialog keySetDialog;
    private PresentationDialog presentDialog;
    private ElementTreeSearchDialog elementTreeSearchDialog;
    private String currStoreDir = null;
    private String currLoadDir = null;
    
    private boolean offline=true;
    
    private ContentTreeNavigationFrame elementSelector;
    
    /**
     * the constructor
     * @param cc - the view of the CC -holds all the style-informations 
     */
    public CCController(CourseCreator cc) {
    	super();
    	CCController.frame = cc;
    	
    	this.config = new CCConfig(this);
    	frame.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			exitDialog();
    		}
    	});
    	
    	// get the Login-Server from User
    	this.cclogin = new CCLoginDialog(this); 
    	this.debugDialog = new DebugDialog(this); 
    	this.keySetDialog = new KeySetDialog(this); 
    	this.presentDialog = new PresentationDialog(this); 
    	this.elementTreeSearchDialog = new ElementTreeSearchDialog(this);

    	// set initial background options
//    	UIManager.put("OptionPane.background",this.background);
//    	UIManager.put("Panel.background",this.background);
//    	UIManager.put("Button.background",this.background);
    	currStoreDir = new File(this.config.getSTORE_DIR()).getPath();
    	currLoadDir = new File(this.config.getLOAD_DIR()).getPath();
    }
    
    private void initTreeView(boolean offline){

    	this.elementSelector = new ContentTreeNavigationFrame(offline);

    	elementSelector.init(this);
    }
    
    /**
     * shows again the Login-Dialog for choosing a new Japs-Server
     * @param flag
     */
    public void showLoginDialog(boolean flag)
    { 
    	this.cclogin.setVisible(flag); 
    }
    
    /**
     * Set model to given model.
     * @param model
     */
    public void setModel(CCModel model) {
    	this.model = model;
    }

    /**
     * For getting the actual prefixURL which is used by the JapsClient
     * @return the prefix URL
     */
    public String prefixURL(){
    	return cclogin.getURLPrefix();
    }
    
    /**
     * returns the JapsClient obtained by the {@link CCLoginDialog}
     * @return the actual instance of the <code>JapsClient</code>
     */
    public JapsClient getJapsClient()
    {
    	return cclogin.getJapsClient();
    }
    
    /**
     * Implementation of ActionListener Interface. 
     */
    public void actionPerformed(ActionEvent ae) {
    	String cmd = ae.getActionCommand();
    	babble("cmd "+cmd);
    	
//    	add Cell cmd is addCell_doctype_category
    	if ((cmd.length() > 7)&&cmd.startsWith(CommandConstants.ADD_CELL)){

    		int docType = Integer.parseInt(cmd.substring(cmd.indexOf("_")+1,cmd.lastIndexOf("_")));
    		int cat = Integer.parseInt(cmd.substring(cmd.lastIndexOf("_")+1,cmd.length()));
    		
    		if (!this.model.getFirstGraph().isSectionGraph() && MainComponentConstants.isSectionCategory(cat)){
    			babble("new sectionGraph built");
    			this.updateELClasses();
    		}
    		this.getModel().insert(docType, cat);
    		
    		
    	} else if (cmd.equals(CommandConstants.MOVE_UP)){
    		this.model.getNavModel().move(CommandConstants.UP);
    	} else if (cmd.equals(CommandConstants.MOVE_DOWN)){
    		this.model.getNavModel().move(CommandConstants.DOWN);
    	} else if (cmd.equals(CommandConstants.MOVE_RIGHT)){
    		this.model.getNavModel().move(CommandConstants.RIGHT);
    	} else if (cmd.equals(CommandConstants.MOVE_LEFT)){
    		this.model.getNavModel().move(CommandConstants.LEFT);
    		
    	}else if (cmd.startsWith(CommandConstants.GRAPH)) {
    		// set buffered graph
    		this.model.setBufferedGraph(Integer.parseInt(cmd.substring(CommandConstants.GRAPH.length())));

    	} else if (cmd.equals(CommandConstants.CONNECT)) {
    		// connect two elements
    		this.model.connect();
    	} else if (cmd.equals(CommandConstants.SWAP)) {
    		// swap two elements
    		this.model.getNavModel().swapGraphCells();

    	} else if (cmd.equals(CommandConstants.DELETE)) {
    		// delete an/a element/node/section/subelement
    		this.model.delete();

    	} else if (cmd.equals(CommandConstants.TOGGLERED)) {
    		// toggle edge from black to red or vice versa

    		this.model.getNavModel().toggleRed();

    	} else if (cmd.equals(CommandConstants.TOGGLEEXISTS)) {
    		this.model.getNavModel().toggleExists();    	
    		
    	} else if (cmd.equals(CommandConstants.ZOOM_IN)) {
    		// zoom in/out
    		this.model.getNavModel().zoomIn();
    		frame.buttonsEnabling();
    		
    	} else if (cmd.equals(CommandConstants.ZOOM_OUT)) {
    		// zoom in/out
    		this.model.getNavModel().zoomOut();
    		frame.buttonsEnabling();
    		
    	} else if (cmd.equals(CommandConstants.CHANGE_EDGE_DIRECTION)) {
    		// change edge direction
    		this.model.getNavModel().toggleEdgeDirection();

    	} else if (cmd.equals(CommandConstants.SAVE)) {
    		this.model.save(null,false);
    	} else if (cmd.equals(CommandConstants.SAVEAS)) {
    		this.model.save(null,true);
    		
        } else if (cmd.equals(CommandConstants.LOAD)) {
    		// open choose dialog
        	this.dialogChooseLoad();
//        	this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=1);
        	this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=0);

        } else if (cmd.equals(CommandConstants.REMOVE_GRAPH)) {
        	if (this.dialogReallyRemoveGraph()){// ask user if really ..
    		this.model.removeGraph(0);// remove maingraph
    		this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=0);
        	}

        } else if (cmd.equals(CommandConstants.UNDO)) {
        	this.getModel().undo();
    	} else if (cmd.equals(CommandConstants.CHECK_REDLINE)) {
    		this.getModel().checkRedLine();

    	} else if (cmd.equals(CommandConstants.DEL_REDLINE)) {
    		this.model.getNavModel().getMainGraph().clearRed();

    	} else if (cmd.equals(CommandConstants.SHOW_SOURCES)) {
    		this.model.getNavModel().showSourcesOrDrains(true);

    	} else if (cmd.equals(CommandConstants.SHOW_DRAINS)) {
    		this.model.getNavModel().showSourcesOrDrains(false);

    	} else if (cmd.equals(CommandConstants.TEST_FOR_CIRCLES)) {
    		this.model.getNavModel().showCircles();

    	} else if (cmd.equals(CommandConstants.ALIGN)) {
    		this.model.getNavModel().getMainGraph().alignCells();

    	}else if (cmd.equals(CommandConstants.NEW_GRAPH)){
    		switch (this.newDialog("welchen Typ wollen Sie neu erstellen?", "Neu")) {
    		case 0:{ this.actionPerformed(new ActionEvent(this,0,CommandConstants.NEW_NAVGRAPH));
			break;}
    		case 1:{ this.actionPerformed(new ActionEvent(this,0,CommandConstants.NEW_FLAT));
			break;}

			default:// abbruch
				break;
			} 
    	}else if(cmd.equals(CommandConstants.NEW_COURSE)){
    		this.model.getFirstGraph().setGraphType(MetaInfos.GRAPHTYPE_SECTION);
    		this.model.courseCreator.toolBarsEnabling();
    	}else if(cmd.equals(CommandConstants.NEW_COURSESECTION)){
    		this.model.getFirstGraph().setGraphType(MetaInfos.GRAPHTYPE_ELEMENT);
    		this.model.courseCreator.toolBarsEnabling();
    	}else if(cmd.equals(CommandConstants.NEW_PROBLEM)){
    		this.model.getFirstGraph().setGraphType(MetaInfos.GRAPHTYPE_HOMEWORK);
    		this.model.courseCreator.toolBarsEnabling();
    		
    	
    	} else if (cmd.equals(CommandConstants.NEW_NAVGRAPH)) {
//    		this.newDialog(); // the annoying Dialog...
    		this.model.newCourse();
//    		this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=1);
    		this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=0);
   
    	} else if (cmd.equals(CommandConstants.NEW_FLAT)) {
    		this.model.newFlatCourse();
//    		this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=1);
    		this.model.courseCreator.enableCloseGraph(this.model.getBufferedGraphs().size()!=0);
    	
    	} else if (cmd.equals(CommandConstants.QUIT)) {
    		this.exitDialog();
    		
    	} else if (cmd.equals(CommandConstants.SHOW_ELEMENTSELECTOR)) {
    		if (this.getOffline()) {
    			this.model.courseCreator.setElementSelState(false);
    			if (this.dialogNoTreeNavFrameBecauseOffline()){
    				this.actionPerformed(new ActionEvent(this,0,CommandConstants.LOGIN));
    			}
    		}else{
    			this.elementSelector.setVisible(!this.elementSelector.isVisible());
    			this.model.courseCreator.setElementSelState(this.elementSelector.isVisible());
    		}
    		
    	} else if (cmd.equals(CommandConstants.SWITCH_PAINT_ORDER)) {
    		this.model.getNavModel().getMainGraph().switchPaintOrder();  

    	} else if (cmd.equals(CommandConstants.LOGIN)) {
    		if (this.elementSelector!=null){
    			this.elementSelector.setVisible(false);
    			this.model.courseCreator.setElementSelState(false);
    		}
    		if (this.getModel().courseCreator.metaInfoPanel.getClassChooser()!=null)
    		this.getModel().courseCreator.metaInfoPanel.getClassChooser().setVisible(false);
    		this.cclogin.setVisible(true);
    	} else if (cmd.equals(CommandConstants.ASSIGN_CERTS)) {
    		this.dialogAssignCert(false);
    		
    	} else if (cmd.equals(CommandConstants.SHOW_DEBUG_DIALOG)) {
    		this.debugDialog.setVisible(true);
    		
    	} else if (cmd.equals(CommandConstants.SHOW_KEYSET_DIALOG)) {
    		this.keySetDialog.setVisible(true);
    	} else if (cmd.equals(CommandConstants.SHOW_PRESENTATION_DIALOG)) {
    		this.presentDialog.setVisible(true);
    	
    	} else if (cmd.equals(CommandConstants.SHOW_ADMIN_DIALOG)) {
//    		this.adminDialog.setVisible(true);//is not used, but importent
    	}else  	if (cmd.equals(CommandConstants.TREEVIEW_SEARCH)) {
    		this.elementTreeSearchDialog.setVisible(true);
    	} else if (cmd.equals(CommandConstants.SHOW_ARROWS)) {
    		this.model.getNavModel().getMainGraph().toggleShowArrows();
    	} else if (cmd.equals(CommandConstants.SET_GRIDSIZE)) {
    		babble("grid");
    		int p=this.dialogAssignGridSize();
    		babble("p"+p);
    		if (p>0){
    			this.model.setGridsize(p);
    			this.getConfig().setGRIDSIZE(p);
    		}
    	} else if (cmd.equals(CommandConstants.SET_MOVESTEP)) {
    		babble("move");
    		int p=this.dialogAssignMoveStep();
    		babble("p"+p);
    		if (p>0){
    			this.model.setMoveStep(p);
    			this.getConfig().setMOVE_STEP(p);
    		}

    	} else if (cmd.equals(CommandConstants.SHOW_GRID)) {
    		if (this.getModel().getKursTyp()==CCModel.KURS_NAVGRAPH){
    			this.getModel().setShowGrid(!this.getModel().getShowGrid());
    			this.getConfig().setSHOWGRID(this.getModel().getShowGrid());
    		}
    	} else if (cmd.equals(CommandConstants.SHOW_HELP)){
    		this.showHelp();
    	} else if (cmd.equals(CommandConstants.SHOW_ABOUT)){
    		this.showAbout();
    	} else if (cmd.equals(CommandConstants.SHOW_ALIAS)) {
    		this.dialogDisplayAlias();
    		
    	}else 
    		
    		if (cmd.equals(CommandConstants.SHOW_FILESOURCE)){
    	
    		String s = this.getModel().getSourceFileNameXML();
    		babble("name "+s);
    		if (s!= null) this.showCode(s);
    		else {
    			JOptionPane.showMessageDialog(frame,"es existiert kein Graph", "kein Graph",JOptionPane.INFORMATION_MESSAGE);
    		}
    		
    	} else if (cmd.equals(CCLoginDialog.CMD_OK)) {
    		// should only started when japs_client is set by CCLoginDialog 
    		
    		if (cclogin.getURLPrefix().startsWith("https")){
    			if (System.getProperty("javax.net.ssl.trustStore")==null){
    				this.dialogCertNotExists("Es ist keine Datei f\u00fc die Zertifikate gesetzt",false);
    			}
    		}
    		
    		cclogin.stop(CCLoginDialog.OK);
    		if (cclogin.getConnected()){
    			this.initTreeView(false);
    			this.setOffline(false);
    			frame.metaInfoPanel.setJapsClient(this.getJapsClient());
    		}else this.initTreeView(true);
    		
    	} else if (cmd.equals(CCLoginDialog.CMD_DELETE)){
    		Object[] options = {"Ja", "Nein"};
    		int n = JOptionPane.showOptionDialog(frame, 
				     "Server aus der Liste entfernen?",
				     "Server entfernen", 
				     JOptionPane.YES_NO_OPTION,
				     JOptionPane.INFORMATION_MESSAGE,
				     null,
				     options,
				     options[1]);
        	if (n==JOptionPane.YES_OPTION){
				if (cclogin.getURLPrefix()==null) dialogErrorOccured("", "bitte zuerst einen zu l\u00f6schenden Server anklicken", JOptionPane.INFORMATION_MESSAGE);
				else {
					
					this.getConfig().removeServer(cclogin.getURLPrefix());
					this.cclogin.updateCombobox();
				}
    		}
        	
    	} else if (cmd.equals(CCLoginDialog.CMD_CANCEL)){
    		cclogin.stop(CCLoginDialog.CANCELED);
    	}
    	else if (cmd.equals(CCLoginDialog.CMD_NO_SERVER)){
    		cclogin.stop(CCLoginDialog.OFFLINE);
    		this.initTreeView(true);
    		this.setOffline(true);
    	}
    }

    
    
    public void notifyClosingWindow(String name) {    	
    	frame.notifyClosingWindow(name);
    }

  
	
	
	/** @Override */
    public void componentHidden(ComponentEvent e) {}
    /** @Override */
	public void componentMoved(ComponentEvent e) {
		this.config.setPOSITION_COURSE_CREATOR(e.getComponent().getLocation());
		
	}

	/** @Override 
	 * if the mgpanel is resized, maybe the statustext is to long or to short,
	 * so easily reprint it..
	 */
	public void componentResized(ComponentEvent e) {
		if (e.getSource().equals(frame)){
		this.config.setSIZE_COURSE_CREATOR(e.getComponent().getSize()); 
		}
		else{
			Object[] status = this.model.getStatus();
			CourseCreator.printStatus(String.valueOf(status[0]),String.valueOf(status[1]), String.valueOf(status[2]));
		}
	}

	/** @Override */
	public void componentShown(ComponentEvent e) {}
	
    /**
     * Message is displayed if there are no changes to save and changes should be saved.
     */
    public boolean dialogNoChanges() {
    	Object[] options = {"Ja", "Nein"};
    	int n = JOptionPane.showOptionDialog(frame, 
    					     "Es wurden keine \u00c4nderungen vorgenommen!\n trotzdem Speichern?",
    					     "keine \u00c4nderungen", 
    					     JOptionPane.YES_NO_OPTION,
    					     JOptionPane.INFORMATION_MESSAGE,
    					     null,
    					     options,
    					     options[1]);
    		return (n==JOptionPane.YES_OPTION);
    }

    
     
    public void dialogOpenFailed(String name) {
    	String error = "file "+name+ " existiert nicht";
    	
    	JOptionPane.showMessageDialog(frame,error,
    			"Datei \u00f6ffnen fehlgeschlagen",
    			JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void dialogErrorOccured(String title,String msg, int type){
    	if (CCConfig.getSHOW_EXCEPTION())
    	JOptionPane.showMessageDialog(
    			frame,msg, title,type);
    }


    /**
     * Choose whether to merge, load new or abort.
     */ 
    private void dialogChooseLoad() {
    	
	    String fileName = getFileNameByDialog(false);
	    
	    if(!fileName.equals("")) 
	    	if (-2==this.model.load(currLoadDir, fileName))
	    		this.dialogOpenFailed(currLoadDir + "/"+ fileName);
	    babble("LOadDir "+this.currLoadDir);
    }
    
    /**
     * 
     * @param quest
     * @param title
     * @return 0 if NavGraph<br>
     * 1 if FlatGraph<br>
     * 
     */
    private int newDialog(String quest, String title){
    	Object[] options = {"NavGraph", "FlatGraph", "Abbrechen"};
    	return JOptionPane.showOptionDialog(frame, 
    					     quest,title, 
    					     JOptionPane.YES_NO_CANCEL_OPTION,
    					     JOptionPane.INFORMATION_MESSAGE,
    					     null,
    					     options,
    					     options[0]);
    		
    }
    
    /**
     * Dialog is displayed before an existing File is written
     * @return 
     * 0 if file is to be overwritten <br>
     * 1 if nothing<br>
     * 2 if save again under new name
     */
    public int dialogOverwrite(String dir, String name) {
	Object[] options = {"Ja", "Nein", "Speichern unter"};
	int n = JOptionPane.showOptionDialog(frame, 
					     "Wollen Sie die bestehenden Dateien\n"
						 +dir+name+".meta.xml und \n" 
					     +dir+name+".content.xml\n \u00fcberschreiben?",
					     "Ueberschreiben einer bestehenden Datei", 
					     JOptionPane.YES_NO_CANCEL_OPTION,
					     JOptionPane.INFORMATION_MESSAGE,
					     null,
					     options,
					     options[0]);
		return n;
    }
    
    /**
     * Dialog is displayed when is tried to save a File to a FileName which exists and is opened
     * 
     * @return 0 if user want to try to save again<br>
     * 	1 if abort<br>
     * 2 if save and delete save name of the other graph
     */
    public int dialogFileToSaveExistAndIsOpen() {
    	Object[] options = {"Speichern wiederholen", "Abbrechen", 
    			"<html>Speichern und Dateinamen <br> des anderen Kurses verwerfen" };
    	int n = JOptionPane.showOptionDialog(frame, 
    			"Die Datei existiert bereits und ist ge\u00f6ffnet.",
    			"Fehler beim Speichern", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.ERROR_MESSAGE,
    			null,
    			options,
    			options[1]);
    	return n;
    }
    
    /**
     * called if a graph was saved successfully.
     * generates an Dialog with 'Save success'-Message
     * if the graph would not be able to be checked in an warnig is also shown
     * 
     * to add a criterion for errors do this:
     * 1. add errorString = ""
     * 2. add  "!errorString.equals("")" in summary
     * 3. concat errorString if not equal "" to msg
     * @return true if user want to see the code 
     */
    public boolean dialogSaveSucceed(){
    	String msg = "<html><h1>Der Graph wurde gespeichert!!</h1>";
    	String errorDoc="";
    	String errorRedLine="";
    	
    	int msgType = JOptionPane.INFORMATION_MESSAGE;
    	
    	
    	LinkedList<String> vals = this.model.getNavModel().getMainGraph().checkDocumentsAssigned();
    	babble("VAL"+vals.size());
    	if (vals.size()!=0){

    		errorDoc = "<br>- mindestens einer Komponente, zB \"";
    			for (int i=0;i<vals.size();i++){
    				errorDoc=errorDoc.concat("<br>"+((ComponentCell)this.model.getNavModel().getMainGraph().getComponentCellByLid(vals.get(i))).getCellDescription());
    			}errorDoc=errorDoc.concat("<br><br> wurde kein Document zugewiesen");
    	}
    	//check red line
    	errorRedLine=this.model.getNavModel().getMainGraph().redLineValid();
    	
    	// summerize errors
    	if (!errorDoc.equals("")||!errorRedLine.equals("")){
    		msg = msg.concat("<br> wegen folgender Probleme w\u00e4re das Document nicht eincheckbar:<br>");
    		msgType = JOptionPane.WARNING_MESSAGE;
    		if (!errorDoc.equals("")) 	 msg = msg.concat(errorDoc);
			if (!errorRedLine.equals(""))msg = msg.concat("<br>- "+errorRedLine);
		
    	}
    	msg=msg.concat("</html>");
    	
//    	JOptionPane.showMessageDialog(frame, msg, "graph gespeichert", msgType);
    	String editor = System.getenv("EDITOR");
    	if (editor.contains("/"))
    		editor = System.getenv("EDITOR").substring(System.getenv("EDITOR").lastIndexOf("/")+1);
    	Object[] options = {"Ok", "Dateien anzeigen ("+editor+")"};
    	return (1== JOptionPane.showOptionDialog(frame, msg, "graph gespeichert",
    			JOptionPane.YES_NO_OPTION, msgType,null,options,
    			options[0]));
    	
    }
    
    /**
     * Dialog is displayed when is tried to open an File which doesnt exists
     */
    private void dialogFileToLoadDoesntExist(String dir,String name) {	
	JOptionPane.showMessageDialog(frame, "Die Datei " +	dir + "/" +name + " existiert nicht",  
		      "Fehler beim Laden", 
		      JOptionPane.ERROR_MESSAGE);	
	}
    
    /**
     * displayed before remove a Graph
     * @return true if user wants to delete the mainGraph 
     */ 
    private boolean dialogReallyRemoveGraph() {
    	Object[] options = {"Ja", "Nein"};
    	String msg = "Wollen Sie diesen Graph schliessen?";
    	if (this.getModel().getNavModel().getMainGraph().getChanged()){
    		msg =  "Dieser Graph ist nicht gespeichert.\n " +
    				"Wollen Sie den Graph schliessen?";
    	}
    	int n = JOptionPane.showOptionDialog(frame, 
    			msg,
    			"Graph schliessen?", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	return (n == JOptionPane.YES_OPTION); 
    }
    
    
    protected boolean dialogShowFile() {
    	Object[] options = {"aus Datei", "wie angezeigt"};
    	String msg = "Der aktuelle Graph ist nicht gespeichert. Welche XML-Source soll angezeigt werden?";
    	int n = JOptionPane.showOptionDialog(frame, 
    			msg,
    			"gespeicherter Graph nicht aktuell", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	return (n == JOptionPane.YES_OPTION); 
    }
    
    /**
     * starts a new thread and that one starts 2 EDITOR (from System)
     * and these displays the sourcecode
     * @param file
     */
    public void showCode(String file){
    	dummyfile = file;

    	
    	ShowSourceInEditor ssie = new ShowSourceInEditor(this);
    	Thread sourceEditorThread = new Thread(ssie);
    	sourceEditorThread.start();
		    	
    }
    
    /** shows a dialog with  some informations about the cc */
    private void showAbout(){
    	String message = new String("<html>This is the CourseCreator <b>Version 2-1-1</b><p>" +
    			"here are the most essential differences to the last Version:<br>" +
    			"<ul>" +
    			"<li> choose the coursetype (at the moment just NavGraph)" +
    			"<li> get a help" +
    			"<li> search for Documents in the Elementzuweisungsfenster" +
    			"<li> Undo" +
    			"<li> Points and labels are in the Metainfofield" +
    			"<li> signaling Components which are not complete" +
    			"<li> better Information about saved Graphs" +
    			"<li> displaying current Graph in System editor" +
    			"<li> show a grid"+
    			"</ul>");
    	JOptionPane.showMessageDialog(frame, message, "About Coursecreator", JOptionPane.INFORMATION_MESSAGE);
    	
    }
    
    /** shows the helpfiles (as xhtml in firefox)<br>
     * the files are created by mmtex and mmxalan */
    private void showHelp(){
    	
    	String helpdir = new String(this.config.getDOC_DIR());
    	String helpname = new String("howto");
    	String helpextension = new String(".xhtml");
    	String helpSourceExtension = new String(".tex");
    	
    	File help = new File(helpdir.concat(helpname.concat(helpextension)));  
    	File helpsource = new File(helpdir.concat(helpname.concat(helpSourceExtension)));  
    	while ((!(help.exists()))&&(!(helpsource.exists()))){
    		
    		Object[] options = {"Ja", "Nein", "?"};
        	int n = JOptionPane.showOptionDialog(frame,
    				"<html>die Datei "+help.getAbsolutePath()+"<br>bzw<br>"
    				+helpsource.getAbsolutePath()+"<br>existieren nicht<br>jetzt suchen?</html>",
    				"Datei existiert nicht", JOptionPane.YES_NO_OPTION,
        			JOptionPane.WARNING_MESSAGE,
        			null, options,options[1]);
    		
        	switch (n) {
        	case 0:
        		dialogAssignDoc();
				break;
        	case 2:
        		JOptionPane.showMessageDialog(frame,
        			"<html>die Dokumentation befindet sich im Verzeichnis<br>course_creator_new/docs</html>",
        			"Verzeichnis der Dokumentation", JOptionPane.INFORMATION_MESSAGE);
        		break;	
        	case 1:
        		return;
        	
			default:
				break;
			}
        	helpdir = new String(this.config.getDOC_DIR());
        	help = new File(helpdir.concat(helpname.concat(helpextension)));
        	helpsource = new File(helpdir.concat(helpname.concat(helpSourceExtension)));
    	}
    	
    	if (!helpIsUpToDate(helpdir, help))		{
    		//helpfile neu erstellen
        	Thread helpCompileThread = new Thread(new HelpCompiler(helpdir.concat(helpname)));
        	helpCompileThread.start();

        	frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        	while (!helpCompileThread.getState().equals(Thread.State.TERMINATED)){ 
        		
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					babble("fehler");
					e.printStackTrace();
				}

        	}
        	frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        	
    	}
    	
    	//anzeigen
    	String cmdString = "firefox "+help.getAbsolutePath();
    	try {
			Runtime.getRuntime().exec(cmdString);
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(frame,
					"<html>IOException<p>"+e+"</html>",
					"TreeNavigationFrame IOException",
					JOptionPane.ERROR_MESSAGE);
		    e.printStackTrace();
		}
    	
    }

	/**
	 * Tests if the helpfile is the newest file
	 * @param helpdir
	 * @param help
	 * @return false there is an newer tex-file -or does not exists<br>
	 * true the helpfile is newer then all tex-files
	 */
	private boolean helpIsUpToDate(String helpdir, File help) {
		if (!help.exists()) return false;
		long helpdate = help.lastModified();
    	
    	File dir = new File(helpdir);
		File[] items = dir.listFiles();
		for (int i=0;i<items.length;i++){
			File item =items[i];
			if (item.getName().endsWith(".tex"))
				if (item.lastModified()>helpdate) return false;
		}
		return true;
	}
    
    /**
     * displayed before quitting CourseCreator 
     */ 
    public void exitDialog() {
    	Object[] options = {"Ja", "Nein"};
    	int n = JOptionPane.showOptionDialog(frame, 
    			"Wollen Sie das Programm wirklich beenden?",
    			"Beenden des CourseCreators?", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	if (n == JOptionPane.YES_OPTION) {
    		if (checkIfUnsavedGraphsOpen()){// proof if unsaved graphs are open
    					
    			// noch undo dateien loeschen
				File dir = new File("./");
				File[] items = dir.listFiles();
				
				for (int i=0;i<items.length;i++)				{
					File file = items[i];
					if (file.isFile())
						if (file.getName().startsWith(this.getModel().UNDONAME)) file.delete();
				} 
				//Programm beenden
				System.exit(0);
    		}
    			
    	} 
    }
    

    /**
     * is called, when a Graph ng isn't saved on exit
     * 
     * @param graph
     * @return 0 if exit is to go on, <br>
     * 1 if exit stopped.
     * 2 if immediately exit
     * 3 if beenden abbrechen
     */
    private int dialogSaveOnExit(Graph graph){
    	Object[] options = {"Ja", "Nein","Nein, sofort beenden","Beenden abbrechen"};
    	String name;
    	if (graph.getMetaInfos().getSaveName()!=null)name = graph.getMetaInfos().getSaveName()+".meta.xml";
    	
    	else name = "- bisher ohne Dateinamen -";
    	
    	name = name.concat(" "+graph.getMetaInfos().getName());
    	
    	int n = JOptionPane.showOptionDialog(frame, 
    			"der Graph "+name+" ist nicht gespeichert.\nVor Beenden speichern?",
    			"Ungespeicherter Graph", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	
    	if (n == 0) {// save and go on
    		this.model.save(graph,false);
    		
    	}return n;
    	
    }

    /**
     * checks iff unsaved Graphs open when exiting
     * @return true if exit is to go on, false, if exit has to be stopped
     */
    private boolean checkIfUnsavedGraphsOpen(){
    	
    	for (int i=0; i<this.model.getBufferedGraphs().size();i++){
    		NavGraph ng = (NavGraph)this.model.getBufferedGraphs().get(i);
    		
    		if (ng.getChanged()&&!ng.isEmpty()) {
    			
    			int dec = dialogSaveOnExit(ng);
    			if (dec>=2) return (dec==2); 
    		}
    	}
    	return true;
    }
    
    

    /**
     * Dialog to decide what to do if a Graph is allready open
     * @return
     * 0 Datei \u00f6ffnen und dateiname l\u00f6schen<br>
     * 1 Datei \u00f6ffnen alte verwerfen<br>
     * 2 do not open
     */
    protected int dialogFileIsAllreadyOpen(){
    	Object[] options = {"<html>Datei \u00f6ffnen,<br>Dateiname l\u00f6schen",
    			"<html>Datei \u00f6ffnen,<br>alte verwerfen",
    			"Abbrechen"};
    	int n = JOptionPane.showOptionDialog(frame, 
    			"Die gew\u00e4hlte Datei existiert schon",
    			"Beenden des CourseCreators?", 
    			JOptionPane.YES_NO_CANCEL_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	return n;
    }

    /**
     * showes a save/load dialog
     * showes only .meta.xml-Files
     * get the filename to which you want to save or which you want to load.
     * @param saveOrLoad  true: show save dialog; false: show load dialog
     */
    public String getFileNameByDialog(boolean saveOrLoad) {

		String fileName = "";
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		
		FileFilter metaxmlFilter = new FileFilter(){// show files ending with .meta.xml
	        public boolean accept(File f) {
	            return f.getName().toLowerCase().endsWith(".meta.xml") || f.isDirectory();
	        }
	        public String getDescription() {
	            return "*.meta.xml";
	        }
	    };
		fileChooser.setFileFilter(metaxmlFilter); 
		fileChooser.addChoosableFileFilter(metaxmlFilter);
		File selectedFile;
		int returnValue;
	
		if (saveOrLoad) {
			if (this.currStoreDir != null) 
				fileChooser.setCurrentDirectory(new File(this.currStoreDir));
			fileChooser.setDialogTitle("Lokal speichern");
		    returnValue = fileChooser.showSaveDialog(frame);
		    
		} else {
			if (this.currLoadDir != null) 
				fileChooser.setCurrentDirectory(new File(this.currLoadDir));
			fileChooser.setDialogTitle("Lokal laden");
		    returnValue = fileChooser.showOpenDialog(frame);
		}
		
		if (returnValue != JFileChooser.APPROVE_OPTION) return fileName;
	    
		// user selected OK
	    selectedFile = fileChooser.getSelectedFile();
	    String path = selectedFile.getParentFile().getPath();

	    fileName = selectedFile.getName();
	   
	    if (!saveOrLoad){ // load
		    if (!(selectedFile.getName().endsWith(".meta.xml"))){
		    	selectedFile=new File(path+"/"+fileName+".meta.xml");
		    }
		    if (!selectedFile.exists()) {
				this.dialogFileToLoadDoesntExist(path,selectedFile.getName());
				return "";
			}
		    this.currLoadDir = path;
		    this.config.setLOAD_DIR(this.currLoadDir);
	  
	    }else { //save
	       	fileName = path+ "/"+fileName;
	    	if (fileName.endsWith(".meta.xml")) 
	    		fileName = fileName.substring(0,fileName.length()-".meta.xml".length());

			this.currStoreDir = path;
	    	this.config.setSTORE_DIR(this.currStoreDir);	
	    }
	
	    return fileName;
    }

    /**
     * dialog um Punkte zuzuweisen
     * @param c
     */
    public void dialogAssignPoints(Object c) { 
    	   
    	MainComponentCell cell = (MainComponentCell)c; //TODO! anpassen an weitere Graphen
    	int p = cell.getPoints();
    	String in =""; 
    	if (cell.getHasPoints()) in = Integer.toString(p);
    	
    	String inputValue = JOptionPane.showInputDialog
    	    (frame, "Punkte f\u00fcr diese Aufgabe", in);
    	
    	if (inputValue == null) {
    	    // cancelled by user
    	    return ;
    	} // end of if (inputValue == null)
    	
    	if (inputValue.equals("")){
    		if (cell.getHasPoints()) this.model.getFirstGraph().setChanged(true);
    		cell.setHasPoints(false);
    		
    		return;
    	}
    	
    	try {
    	    p = Integer.parseInt(inputValue); 
    	} catch (NumberFormatException e) {
    	    badPointsDialog(); 
    	    babble(e.toString()); 
    	} // end of try-catch

    	if (p >= 0) {
    		if (p!=cell.getPoints()){ 
	    		cell.setPoints(p);
	    		if (!cell.getHasPoints()) cell.setHasPoints(true);
	    		this.model.getFirstGraph().setChanged(true);
	    		babble(p + " Punkte zugewiesen.");
    		}
    	} // end of if (p >= 0)
    	else {
    	    badPointsDialog(); 
    	    babble("p = " + p); 
    	} // end of if (p >= 0) else
    }
    
    /** assign Gridsize for navgraph
     * @return new gridsize
     */
    public int dialogAssignGridSize() { 
 	   
    	if (this.getModel().getKursTyp()!=CCModel.KURS_NAVGRAPH) return -1;
    	
    	String in = Integer.toString(this.getModel().getGridsize()); 
    	
    	
    	String inputValue = JOptionPane.showInputDialog
    	    (frame, "Gittergr\u00f6\u00DFe", in);
    	
    	if (inputValue == null) {
    	    // cancelled by user
    	    return -1;
    	} // end of if (inputValue == null)
    	
    	if (inputValue.equals("")){
    		
    		return -1;
    	}
    	int p=-1;
    	try {
    	    p = Integer.parseInt(inputValue); 
    	} catch (NumberFormatException e) {
    	    badNumberDialog(); 
    	    babble(e.toString()); 
    	} // end of try-catch

    	if (p > 0) {
    		 
	    return p;
    		
    	} // end of if (p >= 0)
    	else {
    	    badNumberDialog(); 
    	    babble("p = " + p); 
    	    return -1;
    	} // end of if (p >= 0) else
    }
    
    /** assign Movestep for navgraph
     * @return new movestep
     */
    public int dialogAssignMoveStep() { 
 	   
    	if (this.getModel().getKursTyp()!=CCModel.KURS_NAVGRAPH) return -1;
    	
    	String in = Integer.toString(this.getModel().getMoveStep()); 
    	
    	
    	String inputValue = JOptionPane.showInputDialog
    	    (frame, "Schrittweite", in);
    	
    	if (inputValue == null) {
    	    // cancelled by user
    	    return -1;
    	} // end of if (inputValue == null)
    	
    	if (inputValue.equals("")){
    		
    		return -1;
    	}
    	int p=-1;
    	try {
    	    p = Integer.parseInt(inputValue); 
    	} catch (NumberFormatException e) {
    		badNumberDialog(); 
    	    babble(e.toString()); 
    	} // end of try-catch

    	if (p >= 0) {
    		 
	    return p;
    		
    	} // end of if (p >= 0)
    	else {
    		badNumberDialog(); 
    	    babble("p = " + p); 
    	    return -1;
    	} // end of if (p >= 0) else
    }
    
 
    
    /**
     * showes the assignLabelDialog
     * sets the label for cell iff inputValue of the Dialog is
     * 		not empty, not null (Input was cancelled) or did not changed
     * @param cell
     * @param allCells
     */
    public void assignLabelDialog(Object cell) {
    	
    	if (cell==null) return;
    	String p=null;
    	String inputValue;
    	
    	p = ((ComponentCell)cell).getLabel();//TODO anpassen an weitere Graphen
    	
    	inputValue = JOptionPane.showInputDialog(frame, "Label ", p); 
    	
    	if (inputValue == null) {    	    // cancelled by user
    	    return ;
    	} // end of if (inputValue == null)
    	
    	if (inputValue.equals("")) {//empty input not allowed
    		JOptionPane.showMessageDialog
    	    (frame, "Leeres Label ist nicht erlaubt",
    	     "B\u00f6ser Fehler",JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
    	if (inputValue.equals(p)){ return;}// label did not changed

      	((ComponentCell)cell).setLabel(inputValue);
      	this.model.getFirstGraph().setChanged(true);
      	this.notifyModel();
    }//assignLabelDialog();
    

    /** is used when number should be non-negativ (\in N +00) */
    private static void badPointsDialog(){
	JOptionPane.showMessageDialog
	    (frame,
	     "Bitte nur nicht-negative, ganze Zahlen eingeben!",
	     "Eingabefehler",JOptionPane.ERROR_MESSAGE);
	
    }

    /** is used when number should be positiv (\in N) */
    private static void badNumberDialog(){
    	JOptionPane.showMessageDialog
    	    (frame,
    	     "Bitte nur positive, ganze Zahlen eingeben!",
    	     "Eingabefehler",JOptionPane.ERROR_MESSAGE);
    	
        }
    /**
     * is called when japslogin failed
     * @param s errormessage form japs
     */
    public void loginFailed(String s){
    	
    	if (s.equals("javax.net.ssl.SSLException: java.lang.RuntimeException: Unexpected error: java.security.InvalidAlgorithmParameterException: the trustAnchors parameter must be non-empty"))
    		// certificate is not set 
    		this.dialogCertNotExists("Zertifikat ist nicht gesetzt",false);
    	
    	else if (s.equals("javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target")){
    		// certificate is not correct
    		this.dialogCertNotExists("Die als Zertifikat angegebene Datei ist kein Zertifikat f\u00fcr diesen Server",true);
    		
    	}	
    	else if (s.equals("java.net.SocketException: java.security.NoSuchAlgorithmException: Error constructing implementation (algorithm: Default, provider: SunJSSE, class: com.sun.net.ssl.internal.ssl.DefaultSSLContextImpl)")){
    		//File is not a trustStore file
    		this.dialogCertNotExists("Die als Zertifikat angegebene Datei ist kein Zertifikat",false);
    	}
    	else if (s.startsWith("java.lang.IllegalArgumentException: Improper url:")){
    		// Server is maintained
    		if (this.retryLoginWhenFailed(s,"Server wird gewartet"))
        		this.actionPerformed(new ActionEvent(this,0,CommandConstants.LOGIN));
    	}
    		
    	else if (s.equals("java.net.ConnectException: Connection refused")){
    		// Server existiert nicht
    		if (this.retryLoginWhenFailed(s,"Server existiert nicht"))
        		this.actionPerformed(new ActionEvent(this,0,CommandConstants.LOGIN));
    	}
    	else{// anderer Fehler
	    	this.actionPerformed(new ActionEvent(this,0,CCLoginDialog.CMD_NO_SERVER));
	    	if (this.retryLoginWhenFailed(s,"Unbekannter Fehler"))
	    		this.actionPerformed(new ActionEvent(this,0,CommandConstants.LOGIN));
    	}
    	
    	
    }
    
    /**
     * when the certificate is not correct, this dialog is shown<br>
     * it asks for setting a new certificat<br>
     * if user answers 'yes' dialogAssignCert() -a Filechooser for certificates- is called
     * 
     * @param s describes the certificate is not correct problem
     */
    public void dialogCertNotExists(String s, boolean enableShowAliasButton) {
//    	enableShowAliasButton = true;
    	Object[] options2 = {"Ja", "Nein",};
    	Object[] options3 = {"Ja", "Nein","bekannte Aliase"};
    	
    	int n = 0;
    	if (enableShowAliasButton)
    		n = JOptionPane.showOptionDialog(frame, 
    			s+"\nSoll das Zertifikat jetzt gesetzt und neu eingeloggt werden?",
    			"Zertifikat nicht gesetzt", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.WARNING_MESSAGE,
    			null,
    			options3,
    			options3[1]);
    	else 
    		n = JOptionPane.showOptionDialog(frame, 
    			s+"\nSoll das Zertifikat jetzt gesetzt und neu eingeloggt werden?",
    			"Zertifikat nicht gesetzt", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.WARNING_MESSAGE,
    			null,
    			options2,
    			options2[1]);
    	
    	if (n == JOptionPane.YES_OPTION) {
    		this.dialogAssignCert(true);
    	} 
    	if (n == 2){this.dialogDisplayAlias();}
    	
    }
    
    public boolean dialogCloseCauseNotLogin() {
    	Object[] options2 = {"Ja", "<html><p align =\"center\">Nein<br>(Offline arbeiten)<p></html>",};
    	
    	int n = 0;
    		n = JOptionPane.showOptionDialog(frame, 
    			"es wurde drei mal falsch eingeloggt.\nSoll der CourseCreator beendet werden?",
    			"Falscher Login", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.WARNING_MESSAGE,
    			null,
    			options2,
    			options2[1]);
    	
    return n==JOptionPane.YES_OPTION;	
    }
    
    private void dialogDisplayAlias(){
    	String msg=new String("<html>folgende Server-Aliase sind bekannt:<br>");
    	
    	try {
			KeyStore ks = KeyStore.getInstance("jks");
			String location = this.config.getCERT_DIR();
			ks.load(new FileInputStream(location), null);
			Enumeration<String> ali = ks.aliases();
			while (ali.hasMoreElements()){
				String al = ali.nextElement();
				
				msg = msg.concat("<br>"+al);
			}
		} catch (Exception e) {return;}
		msg = msg.concat("</html>");
    	JOptionPane.showMessageDialog(frame, msg, "Aliase", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * shows a Filechooser to select the certifiacte<br>
     * if a file was choosen, then it tests if the file is correct
     * @return 
     *  0 everything is ok <br>
     * -1 incorrect keystore format<br>
     * 
     * 
     */
    public void dialogAssignCert(boolean relogin){
    	boolean status = false;
    	boolean certSetable = false;
    	JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fileChooser.setFileHidingEnabled(false);
		fileChooser.setCurrentDirectory(new File(this.config.getCERT_DIR()));
		fileChooser.setDialogTitle("Zertifikat");
		File selectedFile=null;
		int returnValue;
		while (!status){
			
		    String msg =null;
		    String title = null;
		    String err = null;

			returnValue = fileChooser.showDialog(frame, "OK");
			if (returnValue == JFileChooser.APPROVE_OPTION) {
			    // user selected OK
			    selectedFile = fileChooser.getSelectedFile();
			    String location = selectedFile.getAbsolutePath();
				try {
					KeyStore ks = KeyStore.getInstance("jks");
					ks.load(new FileInputStream(location), null);
					
					status = true;
					certSetable = true;
					
				} catch (KeyStoreException e) {
					title = "KeyStoreException";
					msg = "KeyStoreException";
					err = e.toString();
				
				} catch (NoSuchAlgorithmException e) {
					title = "NoSuchAlgorithmException";
					msg = "NoSuchAlgorithmException";
					err = e.toString();

				} catch (CertificateException e) {
					title = "CertificateException";
					msg = "CertificateException";
					err = e.toString();

				} catch (IOException e) {
					title = "Inkorrektes Dateiformat";
					msg = "Diese Datei hat kein korrektes Keystoreformat";
					err = e.toString();
				}
			}else status = true;
			
			if (!status){
				Object[] options = {"Ja", "Nein"};
				int n=JOptionPane.showOptionDialog(frame,
					msg +"\n\nFehlermeldung: " +err+
					"\n\nNochmal versuchen?", 
					title,
					JOptionPane.YES_NO_OPTION,
					JOptionPane.ERROR_MESSAGE,
					 null,
					 options, options[0]);
				if (n==1) status=true;
			}
				
		}//while (!status)
		if (certSetable){
			this.config.setCERT_DIR(selectedFile.getAbsolutePath());
		    frame.setCertPath();
		    if (relogin) this.actionPerformed(new ActionEvent(this,0,CommandConstants.LOGIN));// relogin
		}
		
    } 

    /**
     * shows a Filechooser to select the documentationdirectory <br>
     */
    private void dialogAssignDoc(){
    	
    	JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	fileChooser.setFileHidingEnabled(false);
		fileChooser.setCurrentDirectory(new File(this.config.getDOC_DIR()));
		fileChooser.setDialogTitle("Documentation");
		File selectedFile=null;
		int returnValue;
		

		returnValue = fileChooser.showDialog(frame, "OK");
		if (returnValue == JFileChooser.APPROVE_OPTION) {
		    // user selected OK
		    selectedFile = fileChooser.getSelectedFile();
		    String location = selectedFile.getAbsolutePath();
		    if (!location.endsWith("/"))location= location.concat("/");
		    config.setDOC_DIR(location);
		}
			

		
    } 
    
    /**
     * replaces an long string by a string with newlines
     * @param s long string
     * @param length max linelength afterwards
     * @return
     */
    private String stringCut(String s, int length){
    	String newS = new String("");
    	
    	while (s.length()>length){
    		String sub = s.substring(0, length-1);
    		int last = sub.lastIndexOf(" ");
    		if (last ==-1)last = sub.length();
    		newS = newS.concat(sub.substring(0,last)+"\n");
    		s=s.substring(last+1);
    	}
    	newS = newS.concat(s);
    	return newS;
    }
    
    /**
     * when japsLogin failed, this showes an dialog<br>
     * with an errormessage an the original error<br>
     * user can choose if he wants to retry login
     * @param error the original Erromessage
     * @param describingMsg an error describing message
     * @return
     */
    private boolean retryLoginWhenFailed(String error, String describingMsg){
		Object[] options = {"Ja", "Nein"};
		String s = describingMsg+"\nFehlermeldung:\n"+this.stringCut(error, 80);
		
    	int n = JOptionPane.showOptionDialog(frame, 
    			s+"\n Nochmal versuchen?",
    			"Login fehlgeschlagen", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	if (n == JOptionPane.YES_OPTION) {return true;}
    	return false;
	}
    
    
    
    private boolean dialogNoTreeNavFrameBecauseOffline(){
		Object[] options = {"Ja", "Nein"};
		
    	int n = JOptionPane.showOptionDialog(frame, 
    			"<html> \u00d6ffnen des Elementzuweisungsfensters nicht möglich," +
    			"<br>da auf keinem Server eingeloggt ist." +
    			"<p> Jetzt einloggen? </html>",
    			"Kein Server", 
    			JOptionPane.YES_NO_OPTION,
    			JOptionPane.INFORMATION_MESSAGE,
    			null,
    			options,
    			options[1]);
    	if (n == JOptionPane.YES_OPTION) {return true;}
    	return false;
	}
     /**
     * wrapped around actionPerformed method.
     * needed because you can't call <code>actionPerformed</code> of the CourseCreator
     * class within <code>actionPerformed</code> method of anonymous AbstractAction class.
     * @see #createActionInputMap(javax.swing.JPanel)
     */
    public void wrapper(ActionEvent ae) {
    	this.actionPerformed(ae);
    }

    /**
     * Create action input map.
     */ 
   public void createActionInputMap() {
	   
	   
	   	amap = new ActionMap();

		amap.put(CommandConstants.KEYPRESSED_CHECKRED, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.CHECK_REDLINE));
			}
		});
		amap.put(CommandConstants.KEYPRESSED_CONNECT, new AbstractAction() {
			static final long serialVersionUID=0;
			public void actionPerformed(ActionEvent e) {
				
				model.connect();
			}
		});
		amap.put(CommandConstants.KEYPRESSED_DELETE, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.DELETE));
			}
		});


		amap.put(CommandConstants.KEYPRESSED_DOWN, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.move(CommandConstants.DOWN);
			}
		});

		amap.put(CommandConstants.KEYPRESSED_EDGEDIR, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.toggleEdgeDirection();
			}
		});
	 
		amap.put(CommandConstants.KEYPRESSED_EXISTS, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.TOGGLEEXISTS));

			}
		});
		amap.put(CommandConstants.KEYPRESSED_GRID, new AbstractAction() {
			static final long serialVersionUID = 0;

			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.ALIGN));
			}
		});

		amap.put(CommandConstants.KEYPRESSED_LEFT, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.move(CommandConstants.LEFT);
			}
		});
	
		amap.put(CommandConstants.KEYPRESSED_MOVEIN, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.moveSubCell(CommandConstants.MOVE_SUBCELL_IN);
			}
		});

		amap.put(CommandConstants.KEYPRESSED_MOVEOUT, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.moveSubCell(CommandConstants.MOVE_SUBCELL_OUT);
			}
		});
	
		amap.put(CommandConstants.KEYPRESSED_NEW, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.NEW_GRAPH));
			}
		});
	
		amap.put(CommandConstants.KEYPRESSED_OPEN, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.LOAD));
			}
		});
		
		amap.put(CommandConstants.KEYPRESSED_RED, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.TOGGLERED));
			}
		});
		amap.put(CommandConstants.KEYPRESSED_RIGHT, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.move(CommandConstants.RIGHT);
			}
		});
		amap.put(CommandConstants.KEYPRESSED_SAVE, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
							
				wrapper(new ActionEvent(this, 0, CommandConstants.SAVE));
			}
		});
		amap.put(CommandConstants.KEYPRESSED_SWAP, new AbstractAction() {
			static final long serialVersionUID=0;
			public void actionPerformed(ActionEvent e) {
				model.swapGraphCells();
			}
		});
		amap.put(CommandConstants.KEYPRESSED_UP, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				model.move(CommandConstants.UP);
			}
		});

		amap.put(CommandConstants.KEYPRESSED_ZOOMIN, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.ZOOM_IN));
			}
		});

		amap.put(CommandConstants.KEYPRESSED_ZOOMOUT, new AbstractAction() {
			static final long serialVersionUID = 0;
			public void actionPerformed(ActionEvent e) {
				wrapper(new ActionEvent(this, 0, CommandConstants.ZOOM_OUT));
			}
		});

		
		this.setKeymap();
        frame.mainpane.setActionMap(amap);
        frame.mainpane.setInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, imap);
        
   } // createActionInputMap(Panel m)

   public void setKeymap(){
	
	   imap = new InputMap();
	   this.setKeymap(this.config.getKey_checkRed(), CommandConstants.KEYPRESSED_CHECKRED);
       this.setKeymap(this.config.getKey_connect(), CommandConstants.KEYPRESSED_CONNECT);
       this.setKeymap(this.config.getKey_delete(), CommandConstants.KEYPRESSED_DELETE);
       this.setKeymap(this.config.getKey_down(), CommandConstants.KEYPRESSED_DOWN);
       this.setKeymap(this.config.getKey_edgeDir(), CommandConstants.KEYPRESSED_EDGEDIR);
       this.setKeymap(this.config.getKey_exists(), CommandConstants.KEYPRESSED_EXISTS);
       this.setKeymap(this.config.getKey_grid(), CommandConstants.KEYPRESSED_GRID);
       this.setKeymap(this.config.getKey_left(), CommandConstants.KEYPRESSED_LEFT);
       this.setKeymap(this.config.getKey_movein(), CommandConstants.KEYPRESSED_MOVEIN);
       this.setKeymap(this.config.getKey_moveout(), CommandConstants.KEYPRESSED_MOVEOUT);
       this.setKeymap(this.config.getKey_new(), CommandConstants.KEYPRESSED_NEW);
       this.setKeymap(this.config.getKey_open(), CommandConstants.KEYPRESSED_OPEN);
       this.setKeymap(this.config.getKey_red(), CommandConstants.KEYPRESSED_RED);
       this.setKeymap(this.config.getKey_right(), CommandConstants.KEYPRESSED_RIGHT);
       this.setKeymap(this.config.getKey_save(), CommandConstants.KEYPRESSED_SAVE);
       this.setKeymap(this.config.getKey_swap(), CommandConstants.KEYPRESSED_SWAP);
       this.setKeymap(this.config.getKey_up(), CommandConstants.KEYPRESSED_UP);
       this.setKeymap(this.config.getKey_zoomin(), CommandConstants.KEYPRESSED_ZOOMIN);
       this.setKeymap(this.config.getKey_zoomout(), CommandConstants.KEYPRESSED_ZOOMOUT);
   }
   
   
   /**
    * joines new key and command (KEYPRESSED_)
    * @param s
    * @param cmd
    */
   private void setKeymap(String s, String cmd){
	   char c = this.getKey(s);
	   if (c==KeyEvent.CHAR_UNDEFINED){
		   JOptionPane.showMessageDialog(frame,"Fehler: Taste "+s+" nicht moeglich",
	    			"Taste nicht moeglich",
	    			JOptionPane.INFORMATION_MESSAGE);
		   return;
	   }
	   
	   imap.put(KeyStroke.getKeyStroke(c, this.getModifier(s)),cmd);
   }
   
   private int getModifier(String s){
	   int value=0;
	   if (s.contains("CTRL")) value +=KeyEvent.CTRL_DOWN_MASK;
	   if (s.contains("ALT")) value +=KeyEvent.ALT_DOWN_MASK;
	   if (s.contains("SHIFT")) value +=KeyEvent.SHIFT_DOWN_MASK;
	   if (s.contains("META")) value +=KeyEvent.META_DOWN_MASK;
	   
	   return value;
   }
   private char getKey(String s){


		if (s.endsWith("F10")) return KeyEvent.VK_F10;
		else if (s.endsWith("F11")) return KeyEvent.VK_F11;
		else if (s.endsWith("F12")) return KeyEvent.VK_F12;
		else if (s.endsWith("F1")) return KeyEvent.VK_F1;
		else if (s.endsWith("F2")) return KeyEvent.VK_F2;
		else if (s.endsWith("F3")) return KeyEvent.VK_F3;
		else if (s.endsWith("F4")) return KeyEvent.VK_F4;
		else if (s.endsWith("F5")) return KeyEvent.VK_F5;
		else if (s.endsWith("F6")) return KeyEvent.VK_F6;
		else if (s.endsWith("F7")) return KeyEvent.VK_F7;
		else if (s.endsWith("F8")) return KeyEvent.VK_F8;
		else if (s.endsWith("F9")) return KeyEvent.VK_F9;
		
		else if (s.endsWith(CommandConstants.stringBACKSPACE)) return KeyEvent.VK_BACK_SPACE;
		else if (s.endsWith(CommandConstants.stringDelete)) return KeyEvent.VK_DELETE;
		
		else if (s.endsWith(CommandConstants.stringDOWN))		return KeyEvent.VK_DOWN;
		else if (s.endsWith(CommandConstants.stringEND))		return KeyEvent.VK_END;
		else if (s.endsWith(CommandConstants.stringENTER))		return KeyEvent.VK_ENTER;
		else if (s.endsWith(CommandConstants.stringESC))		return KeyEvent.VK_ESCAPE;
		else if (s.endsWith(CommandConstants.stringHOME))		return KeyEvent.VK_HOME;
		else if (s.endsWith(CommandConstants.stringINSERT))	return KeyEvent.VK_INSERT;
		else if (s.endsWith(CommandConstants.stringLEFT))		return KeyEvent.VK_KP_LEFT;
		else if (s.endsWith(CommandConstants.stringPAGEDOWN))	return KeyEvent.VK_PAGE_DOWN;
		else if (s.endsWith(CommandConstants.stringPAGEUP))	return KeyEvent.VK_PAGE_UP;
		else if (s.endsWith(CommandConstants.stringPAUSE))		return KeyEvent.VK_PAUSE;
		else if (s.endsWith(CommandConstants.stringRIGHT))		return KeyEvent.VK_RIGHT;
		else if (s.endsWith(CommandConstants.stringSPACE))		return KeyEvent.VK_SPACE;
		else if (s.endsWith(CommandConstants.stringUP))		return KeyEvent.VK_UP;
		else if (s.endsWith(CommandConstants.stringPLUS))		return KeyEvent.VK_PLUS;
		else if (s.endsWith(CommandConstants.stringMINUS))		return KeyEvent.VK_MINUS;
		
		
		else if (s.endsWith("a")) return KeyEvent.VK_A;
		else if (s.endsWith("b")) return KeyEvent.VK_B;
		else if (s.endsWith("c")) return KeyEvent.VK_C;
		else if (s.endsWith("d")) return KeyEvent.VK_D;
		else if (s.endsWith("e")) return KeyEvent.VK_E;
		else if (s.endsWith("f")) return KeyEvent.VK_F;
		else if (s.endsWith("g")) return KeyEvent.VK_G;
		else if (s.endsWith("h")) return KeyEvent.VK_H;
		else if (s.endsWith("i")) return KeyEvent.VK_I;
		else if (s.endsWith("j")) return KeyEvent.VK_J;
		else if (s.endsWith("k")) return KeyEvent.VK_K;
		else if (s.endsWith("l")) return KeyEvent.VK_L;
		else if (s.endsWith("m")) return KeyEvent.VK_M;
		else if (s.endsWith("n")) return KeyEvent.VK_N;
		else if (s.endsWith("o")) return KeyEvent.VK_O;
		else if (s.endsWith("p")) return KeyEvent.VK_P;
		else if (s.endsWith("q")) return KeyEvent.VK_Q;
		else if (s.endsWith("r")) return KeyEvent.VK_R;
		else if (s.endsWith("s")) return KeyEvent.VK_S;
		else if (s.endsWith("t")) return KeyEvent.VK_T;
		else if (s.endsWith("u")) return KeyEvent.VK_U;
		else if (s.endsWith("v")) return KeyEvent.VK_V;
		else if (s.endsWith("w")) return KeyEvent.VK_W;
		else if (s.endsWith("x")) return KeyEvent.VK_X;
		else if (s.endsWith("y")) return KeyEvent.VK_Y;
		else if (s.endsWith("z")) return KeyEvent.VK_Z;
		else if (s.endsWith("1")) return KeyEvent.VK_1;
		else if (s.endsWith("2")) return KeyEvent.VK_2;
		else if (s.endsWith("3")) return KeyEvent.VK_3;
		else if (s.endsWith("4")) return KeyEvent.VK_4;
		else if (s.endsWith("5")) return KeyEvent.VK_5;
		else if (s.endsWith("6")) return KeyEvent.VK_6;
		else if (s.endsWith("7")) return KeyEvent.VK_7;
		else if (s.endsWith("8")) return KeyEvent.VK_8;
		else if (s.endsWith("9")) return KeyEvent.VK_9;
		else if (s.endsWith("0")) return KeyEvent.VK_0;
		
		else return KeyEvent.CHAR_UNDEFINED;
	
}
   
   
   /**
     * Implementation of CCModelChangeListener Interface.
     */
    public void modelChanged(CCModelChangedEvent ce) {
	    
	    Object[] status = this.model.getStatus();
	    CourseCreator.printStatus(String.valueOf(status[0]),String.valueOf(status[1]), String.valueOf(status[2]));
	   if (this.getModel().getFirstGraph()==null) this.elementSelector.setVisible(false); 
    }
    
    /**
     * notifies the Model that a changement has been done ... 
     * is practical to keep the model hidden for some 
     * classes who already got access to the controller
     *
     */
    public void notifyModel()
    {
    	this.model.notify(this.model.getFirstGraph(), CCModelChangedEvent.SET_PATH, true);
    }

    private boolean docTypeAssigned = false;

    /**
     * Implementation of GraphSelectionListener Interface.
     * Receives selection events from parent panel and from main panel.
     */
    public void valueChanged(GraphSelectionEvent selectionEvent){//TODO anpassen an weitere graphen
      if (selectionEvent.getSource().equals(this.model.getFirstGraph())) {
    	  frame.buttonsEnabling();
    	  frame.toolBarsEnabling();
      }
      Object[] cells = this.model.getFirstGraph().getSelectionCells();
      if(cells.length == 1)	{
    	
    	  Object cell = cells[0];
    	  if( this.model.getFirstGraph().isElementGraph()){
    		  if ( cell instanceof MainComponentCell ){

				  this.elementSelector.setAllowedDocumentType(
						  MainComponentConstants.getComponentType(MetaInfos.GRAPHTYPE_ELEMENT));
				  babble("setAll.DocType = element");
				  docTypeAssigned = true;
    		  }
    		  else if ( cell instanceof SubComponentCell ){

    			  this.elementSelector.setAllowedDocumentType(
    					  SubComponentConstants.getComponentType(SubComponentConstants.DOCTYPE_SUBELEMENT));
    			  babble("setAll.DocType = subelement");
    			  docTypeAssigned = true;				
    		  }
    	  }
    	  else if( this.model.getFirstGraph().isProblemGraph() ){
    		  if( cell instanceof MainComponentCell ){

    			  this.elementSelector.setAllowedDocumentType(
    					  MainComponentConstants.getComponentType(MetaInfos.GRAPHTYPE_HOMEWORK));
    			  babble("setAll.DocType = problem");
    			  docTypeAssigned = true;
    		  }
    	  }
    	  else if ( this.model.getFirstGraph().isSectionGraph() ){
    		  if(cell instanceof MainComponentCell ){
    			  this.elementSelector.setAllowedDocumentType(
    					  MainComponentConstants.getComponentType(
    							  MainComponentConstants.DOCTYPE_SECTION));
    			  babble("setAll.DocType = course_section");
    			  docTypeAssigned = true;
    		  }
    		  else if ( cell instanceof SubComponentCell ){
    			  this.elementSelector.setAllowedDocumentType(
    					  SubComponentConstants.getComponentType(SubComponentConstants.DOCTYPE_SUBSECTION));
    			  babble("setAll.DocType = worksheet");
    			  docTypeAssigned = true;				
    		  }
    	  }
    	  else if(docTypeAssigned){
    		  this.elementSelector.setAllowedDocumentType("none");
    		  babble("setAll. no DocType allowed");
    		  docTypeAssigned = false;
    	  }
      }
      else { // no cell or multiple cells selected
    	  if (this.elementSelector!=null)
    	  this.elementSelector.setAllowedDocumentType("none");
      }
    }

    public void updateELClasses() {
    	frame.metaInfoPanel.enableELClass(); }
    
    
    /**
     * Sets the path to the summary into the Meta-Infos
     * @param path a <code>String</code> with the path
     */
    public void setSummary(String path){
    	this.model.setSummary(path);
    
    }

    /**
     * The <code>setPath</code> method has the funtion to set the path of the actual
     * graph
     *
     * @param path a <code>String</code> value
     * @param name a <code>String</code> value
     * @param category The category of the document to be assigned. May be
     * -1 iff the document to be assigned has no categories.
     * @see CCModel#setPath(int,String,int)
     */
    public void setPath(String path, String name, int category) {
    	babble("called setPath with path: " + path +" name: "+name+" category: "+category);
    	this.model.setPath(path, name, category);
    }
    
    public void setChangedFlag(boolean f)
    {
    	this.model.getFirstGraph().setChanged(f);
    }    

  
    
    /**
     * FIXME - method including the whole load-procedure has to be programmed - for using mmcdk
     * @param path
     */
    public void loadFromJAPS(String path)
    {
    	
    }

    /**
     * File filter class for xml files.
     * shows only directories and xml files in FileChooser dialog
     */
    protected static class XMLFileFilter extends javax.swing.filechooser.FileFilter {
    	public boolean accept(File file) {
    		return file.isDirectory() || file.getName().endsWith(".xml");
    	}

    	public String getDescription() {
    		return "XML Datei (*.xml)";
    	}
    }
    
    /**
     * Method for debugging-outputs
     * @param bab
     */
    void babble(String bab){
		if (this.config.getDEBUG_CCCONTROLLER() && this.config.getDEBUG()) System.out.println("CCController:"+bab);
	}

	public CCModel getModel() {return model;}
	
	public CCConfig getConfig(){return this.config;}
	public ContentTreeNavigationFrame getElementSelector(){return this.elementSelector;}
	
	/**
	 * in case the whole CC works offline this flag should be set
	 * it sets also the classBotton on/off
	 * @param ofl - offline flag
	 */
	public void setOffline(boolean ofl){
		this.offline = ofl;
		this.model.courseCreator.metaInfoPanel.enableELClass();
	}
	
	public boolean getOffline() {
		return offline;
	}
	 public void mousePressed(MouseEvent me) {
         JComponent c = (JComponent)me.getSource();
         TransferHandler th = c.getTransferHandler();
         th.exportAsDrag(c, me, TransferHandler.COPY);
      }

	
	public void mouseClicked(MouseEvent e) {
		// TODO brauch man mouseevents??
	}

	public void mouseEntered(MouseEvent e) {
	}

	
	public void mouseExited(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {} 
	public void dragEnter(DropTargetDragEvent dtde) {
		// 
		babble("dragenter");
	}

		public void dragExit(DropTargetEvent dte) {
		babble("dragexit");
	}

	public void dragOver(DropTargetDragEvent dtde) {
		// TODO drag and drop includen
		babble("dragover");
	}
	public void drop(DropTargetDropEvent dtde) {
		babble("dorp "+dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		babble("dropactionchanged ");
	}
}

