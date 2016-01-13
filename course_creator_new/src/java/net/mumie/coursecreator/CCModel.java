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

import java.awt.event.ActionEvent;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;
import javax.xml.transform.TransformerException;

import net.mumie.coursecreator.events.*;
import net.mumie.coursecreator.flatgraph.FlatGraph;
import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.graph.NavModel;
import net.mumie.coursecreator.graph.cells.*;
import net.mumie.coursecreator.xml.*;

/**
 * <p>
 * The CCModel is the central instance for managing the graph and the 
 * course structure. It holds a reference to each visible graph, i.e.
 * the main graph, the parent graph and the clipboard graph.
 *</p><p>
 * The CCModel is responsible for calling the methods from the appropriate 
 * graph classes when inserting or deleting a cell or from the I/O classes
 * when saving or loading a graph.
 * It is also responsible for transfering cells to or from the clipboard graph.
 * The navigation through the course structure is also done by the CCModel as well
 * as the distribution of content to the cells and some other minor functions.
 * </p>
 *
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @version $Id: CCModel.java,v 1.74 2009/03/31 19:41:09 vrichter Exp $
 */

public class CCModel {

	public static final int KURS_UNDEFINED = -1;
	public static final int KURS_NAVGRAPH = 0;
	public static final int KURS_FLATGRAPH = 1;
    private CCController controller;
    protected CourseCreator courseCreator; 

    private GraphLoader graphLoader;
    protected Map elClasses;
    
    private int kursTyp;
    private LinkedList<Graph> bufferedGraphs;
    private NavModel navModel;
    
    private int gridsize;
    private boolean showgrid;
    private int movestep;
    
    
	 /** Reihung verwaltet alle interessierten ModelChangeListener */
    private LinkedList<CCModelChangeListener> listeners;
    
    public String UNDONAME = ".thisisanothercomplicatenamefortheundofiles";
    public String DUMMYFILE = ".thisisjustacomplicatenameforadummyfile";
    
    /**
     * Create a new CCModel instance.
     */
    public CCModel (CCController controller, CourseCreator courseCreator) {

    	this.listeners = new LinkedList<CCModelChangeListener>();
		this.courseCreator = courseCreator;
	
		this.controller = controller;
		this.controller.setModel(this);
			
		this.elClasses = null;
		this.graphLoader = new GraphLoader(this);
		this.bufferedGraphs = new LinkedList<Graph>();
		this.navModel = new NavModel(this);
		this.setKursTyp(KURS_UNDEFINED);
		this.movestep = this.controller.getConfig().getMOVE_STEP();
		this.showgrid = this.controller.getConfig().getSHOWGRID();
		this.gridsize = this.controller.getConfig().getGRIDSIZE();
    }
    
    
    
    /**
     * Set content of an Element/SubElement/Problem Cell.
     * @param path      The path of the content
     * @param name      The name of the content
     * @param category  The category of the content. May be
     * -1 iff the document to be assigned has no categories. Currently,
     * this is true only for documents of type <code>problem</code>.
     */
    public void setPath(String path, String name, int category) {
    	Object[] arr = this.getFirstGraph().getSelectionCells();//FIXME anpassen an andere Graphtypen
		if (arr.length == 1) {
			if (arr[0] instanceof MainComponentCell ) {
				MainComponentCell cell = (MainComponentCell) arr[0];
				cell.setElementPath(path);
				if (!this.getFirstGraph().isProblemGraph()) cell.setCategory(category);
			} else if (arr[0] instanceof SubComponentCell) {
				SubComponentCell subcell = (SubComponentCell) arr[0];
				subcell.setElementPath(path);
				subcell.setCategory(category);
			}
			this.getFirstGraph().setChanged(true);
		}
    }
    	
 
    /**
     * Add a ModelChangeListener to the listeners list.
     * The Listener is only added if not already present in the list.
     * @param m CCModelChangeListener, the listener to add to the list
     */
    public void addModelChangeListener (CCModelChangeListener m) {
	if (!this.listeners.contains(m))
	    this.listeners.add(m);
    }
      
    /**
     * Remove a ModelChangeListener from the listeners list.
     * @param m CCModelChangeListener, the listener to remove from the list
     */
    public void removeModelChangeListener (CCModelChangeListener m){
	this.listeners.remove(m);
	      
    }

    /**
     * Notify all CCModelChangeListeners.
     * @param changedGraph changed graph
     * @param type         type of the CCModelChangedEvent
     */
    public void notify (Graph changedGraph, int type, boolean buffersMenuRefresh) {

		CCModelChangedEvent ce = new CCModelChangedEvent(this,changedGraph,type);
		
		// with this event the MetaInfos of the new Graph will be written into the
		// MetaInfoField
		ListIterator it = this.listeners.listIterator();
		while (it.hasNext()){
		    ((CCModelChangeListener)it.next()).modelChanged(ce);
		}
		
		// and buffersMenu is written new only if 
		if (buffersMenuRefresh)
			this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
		
    }
    
    
   
    /**
     * Create a new course.
     * A not saved course will be lost.
     */
    public void newCourse() {
    	this.setKursTyp(KURS_NAVGRAPH);
    	Graph g = this.navModel.newCourse();
    	
    	this.bufferedGraphs.addFirst(g);
    	
    	this.babbleAllBufferedGraphs();
    	this.notify(this.getFirstGraph(), CCModelChangedEvent.CHANGED_GRAPHS,true);
    	
    }
    
    public void newFlatCourse() {
    	JOptionPane.showMessageDialog(CCController.frame, "Sorry, flatgraphs are not implemented yet");
    	if (false)//FIXME implement flatgraph dann if entfernen
    	{
    	this.setKursTyp(KURS_FLATGRAPH);
    	this.courseCreator.createFlatGraphPanel();
//    	this.bufferedNavGraphs.addFirst(g); 
    	}
    }
    

    /**
     * Set local ID counter.
     * @param cnt Value the counter is set to
     */
    public void setLIDcount(int cnt) {
   		this.getFirstGraph().setLid_counter(cnt);
    }
    
   
    /**
     * Set the CCController of this CCModel.
     * @param cc CCController, 
     */
    public void setController(CCController cc) {
	this.controller = cc;
    }


    /**
	 * @return the controller
	 */
	public CCController getController() {
		return controller;
	}

	/**
     * updated MetaInfos of mainGraph
     * @param name
     * @param descr
     */
    public void updateMetaInfosName(String value){   
    	
    	MetaInfos metaInfos = this.getFirstGraph().getMetaInfos();    	
    	if (!metaInfos.getName().equals(value)){
    		babble("set  name ... "+value);
    		metaInfos.setName(value);
    		this.getFirstGraph().setChanged(true);
    		this.courseCreator.setLblNameGraph(); 
        	this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
        	CourseCreator.printStatus("",null);
    	}
    
    	
    }
    
    /**
     * update the description-text of the meta-info-field
     * @param value
     */
    public void updateMetaInfosDescr(String value){    	
    	MetaInfos metaInfos = this.getFirstGraph().getMetaInfos();    	
    	if (!metaInfos.getDescription().equals(value)){

    		metaInfos.setDescr(value);
    		this.getFirstGraph().setChanged(true);
        	this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
        	CourseCreator.printStatus("",null);
    	}
    
    }
    
    public void setUpdateDateEvent(){
    	this.courseCreator.actionPerformed(
    			new ActionEvent(this.courseCreator.metaInfoPanel.getDateStartSpinner(),0,CommandConstants.UPDATE_METAINFODATE));
    	
    }
    
    /**
     * updates the start-time for the meta-info-field for problems
     * @param start - a <code>Calendar</code> Variable
     */
    public void updateMetaInfosStart(Calendar start){
    	
    	MetaInfos metaInfos = this.getFirstGraph().getMetaInfos(); 
    	
    	if (!metaInfos.isSameDate(metaInfos.getDateStartCal(),start.getTime())) {
    		metaInfos.setDateStartCal(start);
    		this.getFirstGraph().setChanged(true);
    	}
    	
    }
    
    /**
     * updates the end-time for the meta-info-field for problems
     * @param end - a <code>Calendar</code> Variable
     */
    public void updateMetaInfosEnd(Calendar end){
    	
    	
    	MetaInfos metaInfos = this.getFirstGraph().getMetaInfos(); 
    	
    	if (!metaInfos.isSameDate(metaInfos.getDateEndCal(),end.getTime())) {
    		metaInfos.setDateEndCal(end);
    		this.getFirstGraph().setChanged(true);
    		this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
    	}
    	
    }
    
    /**
     * updates the "Lehrveranstaltung" for the meta-info-field for problems
     * @param path
     */
    public void updateMetaInfosClass(String path){
    	
    	
    	MetaInfos metaInfos = this.getFirstGraph().getMetaInfos(); 
    	if (!metaInfos.getClassPath().equals(path)){
    		metaInfos.setClassPath(path);
    		this.getFirstGraph().setChanged(true);
    		this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
    	}

    }

    /**
     * changes the type and status of the current graph
     * @param type - the type - one out of <code>CommandConstants.META_INFO_FIELD_ST_</code>
     * and <code>CommandConstants.META_INFO_FIELD_CAT_</code>
     * @param value - an <code>int</code>-value - what shall be the changement
     */
    public void updateMetaInfos(String type, int value){    	
    	MetaInfos metaInfos = this.getFirstGraph().getMetaInfos();    	
    	if (type==CommandConstants.META_INFO_FIELD_ST_){
	    	if (metaInfos.getStatus()!=value){
	    		metaInfos.setStatus(value);
	    		this.getFirstGraph().setChanged(true);
	    		this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
	    	}
    	}else if (type==CommandConstants.META_INFO_FIELD_CAT_){
    		if (metaInfos.getGraphType()!=value){
    			metaInfos.setGraphType(value);
	    		this.getFirstGraph().setChanged(true);
	    		this.courseCreator.actionPerformed(new ActionEvent(this,0,CommandConstants.BUFFERREFRESH));
	    	}
    	}
    }
    
    /** only set <code>setChanged(true)<code> */ 
    public void updateMetaInfos(){this.getFirstGraph().setChanged(true);}
    
    /**
     * Set the abstract text of the active graph.
     * @param path - a <code>String</code> containing the path to the summary
     */
    public void setSummary(String path){
    	this.getFirstGraph().setSummary(path);
    	this.notify(this.navModel.getMainGraph(), CCModelChangedEvent.CHANGED_GRAPHS,false);
    }

    /**
     * Deletes selected Elements/Sections/Nodes/Subelements/Edges/Exercises.
     */
    public void delete() {
    	this.getFirstGraph().delete();
    }

    /**
     * removes the nr-th graph
     * @param nr
     */
    public void removeGraph(int nr){
    	Graph g = this.bufferedGraphs.get(nr);
    	if (g instanceof NavGraph){
    		this.navModel.removeGraph(nr);
    	}
    	this.getBufferedGraphs().remove(nr);
    	if (this.getBufferedGraphs().size()==0){
    		// der letzte Graph wurde geloescht
    		this.setKursTyp(KURS_UNDEFINED);
    		this.notify(null, CCModelChangedEvent.CHANGED_GRAPHS, true);

    		this.courseCreator.setInitStat();
    		return;
    	}
    	Graph graph = this.getBufferedGraphs().get(0);
    	this.setGraphMain(graph);
    	if (nr == 0) {
    		// show second graph
    		// set Kurstyp to second graph
    		
    		if (graph instanceof NavGraph){
    			
    			this.setKursTyp(KURS_NAVGRAPH);
    		}
    		else if (graph instanceof FlatGraph)
    			this.setKursTyp(KURS_FLATGRAPH);
    	}
    	
    	this.notify(graph, CCModelChangedEvent.CHANGED_GRAPHS, true);
    	
    }
    
    /**
     * checks if summary exists.
     * this is necessary for check-in
     * @return
     */
    public boolean checkSummaryExists(){
    	if (this.getKursTyp()==KURS_NAVGRAPH) {
    		return this.navModel.getMainGraph().checkSummaryExists();
    	}
    	return false;
    }
    
    /**
     * checks if red line is correct
     * this is necessary for check-in
     */
    protected void checkRedLine(){
    	if (this.getKursTyp()==KURS_NAVGRAPH) {
    		this.getNavModel().getMainGraph().getController().dialogRedNotCorrect(this.getNavModel().getMainGraph().checkRedLine());
    	}
    }
    
    /**
     * inserts a new component (cell) in the main graph
     *  
     * @param docType
     * @param cat
     */
    public void insert(int docType,int cat){
    	if (this.getKursTyp()==KURS_NAVGRAPH) {
    		NavGraph g = (NavGraph)this.getFirstGraph();
    	
    		g.insertCell(g.createCell(docType,cat));
			if (g.getSetStatus()){//set default-Value for status-Field
				g.setSetStatus(false);
				this.courseCreator.metaInfoPanel.setDefaultStatus();// set to panel
				g.getMetaInfos().setStatus(this.courseCreator.metaInfoPanel.getStatus()); // set to metainfos
			}
			
			if (g.getsetHomeCatDefault()){//set default-Value for Category for problems-Field
				g.setsetHomeCatDefault(false);
				this.courseCreator.metaInfoPanel.setDefaultCat();
				g.getMetaInfos().setGraphType(this.courseCreator.metaInfoPanel.getGraphType());
			}	
		}
    }
    
    /**
     * Load a Navgraph from the fileSystem (out of the mmcdk(checkin)-branch /TODO auf andere Graphtypen umstellen
     * @param dir
     * @param fileName
     * @param merge
     * @return 0 if everything is fine<br>
     * -1 if file is allready open and user canceled open<br>
     * -2 if file does not exists
     * 
     */
    public int load(String dir,String fileName) {
    	int dec= -1;
    	
    	
    	int graphPosition = this.graphIsOpen(fileName,dir);
    	
    	if (graphPosition!=-1){ 
    		dec = controller.dialogFileIsAllreadyOpen();
    		if (dec == 2) return -1;// dont open anything
    	}
    	
    	 
    	NavGraph ng = (NavGraph)graphLoader.load(dir, fileName);
    	ng.pre = false;
    	this.setGraphMain(ng);
    	this.notify(ng, CCModelChangedEvent.CHANGED_GRAPHS,true);
		
		if (dec == 1){//Datei oeffnen alte verwerfen
			this.bufferedGraphs.remove(graphPosition);// remove before setGraphMain, because setGraphMain changes order in bufferedGraphList
		}if (dec==0){// Datei oeffnen und dateiname loeschen
			ng.getMetaInfos().setSaveName(null);
			ng.setChanged(true);// graph has no savename
		}else ng.setChanged(false);
		if (ng.getSelectionCount()!=0)
		ng.clearSelection();
		return 0;
    }
    
    
    
    /**
     * proofs if a Navgraph is open by proofing the filename and the directory
     * @param dir the dir the file is stored
     * @param fileName is the hole name: name.meta.xml or just the name without .meta.xml
     * @return the position of the old graph in the bufferedGraphslist but -1 if Graph is not existing
     */
	public int graphIsOpen(String fileName,String dir){
		
		if(dir==null) return -1;
		if (fileName==null) return -1;
		
		if (dir.endsWith("/"))	dir=dir.substring(0, dir.length()-1);
		
		if (fileName.endsWith(".meta.xml"))// file is now without ".meta.xml"
			fileName=fileName.substring(0,fileName.length()-".meta.xml".length());
		
		for (int i=0;i<this.bufferedGraphs.size();i++){
			
			String buffSaveDir = ((NavGraph)this.bufferedGraphs.get(i)).getMetaInfos().getSaveDir();
			if (buffSaveDir !=null){
				if (buffSaveDir.endsWith("/")){ 
					buffSaveDir=buffSaveDir.substring(0, buffSaveDir.length()-1);
				}
				if (dir.equals(buffSaveDir)){ // equal directory
					String bufferedSaveName = 
						((NavGraph)this.bufferedGraphs.get(i)).getMetaInfos().getSaveName();
					if (bufferedSaveName!=null)
					if (fileName.equals(bufferedSaveName)){
						return i;
					}
				}
			}
			
		}
		return -1;
	}
	
	/**
	 * static method for bugtracking
	 * @param bab
	 */
	void babble(String bab){
		if (this.controller.getConfig().getDEBUG_CCMODEL() && this.controller.getConfig().getDEBUG()) System.out.println("CCModel:"+bab);
	}
	
	/**
	 * returns first buffered graph as Graph.
	 * every function which need the first graph should use this!
	 * @return
	 */
	public Graph getFirstGraph(){
		
		if (this.getKursTyp()== KURS_NAVGRAPH) 
			return this.navModel.getMainGraph();
		else return null;
	}
	
	/**
	 * babbles all graphs..<br>
	 * just useful for debugging 
	 */
	public void babbleAllBufferedGraphs(){
		System.out.println(" Buffered Graphs ("+this.bufferedGraphs.size()+") Stueck");
		for (int i=0; i<this.bufferedGraphs.size();i++){
			System.out.println(i+" "+this.bufferedGraphs.get(i));
		}
	}
	
	/**
	 * sets g as new maingraph
	 * @param g
	 */
	private void setGraphMain(Graph g) {
	
		// buffered Graphs updaten
		if (this.getBufferedGraphs().contains(g)){
    		this.getBufferedGraphs().remove(g);
    	}
    	this.getBufferedGraphs().addFirst(g);
//    	this.babbleAllBufferedGraphs();
    	
    	// aktuellen Graph im entsprechenden Model setzen
    	if (g instanceof NavGraph) {
    		this.navModel.setGraphMain((NavGraph) g);
    		this.setKursTyp(KURS_NAVGRAPH);
    	}
    	
		// Buffermenu update
    	this.notify(g, CCModelChangedEvent.CHANGED_GRAPHS, true);

	
	}

    /** @return the kursTyp */
	public int getKursTyp() {return kursTyp;}

	/** @param t the kursTyp to set */
	public void setKursTyp(int t) {this.kursTyp = t;}

	/** @return a <code>LinkedList</code> with the buffered Graphs which are also presented in the menu */
	public LinkedList<Graph> getBufferedGraphs() {
		return bufferedGraphs;
	}
	
	/**
     * shows the i-th graph of the bufferedGraphList
     * @param i
     */
    public void setBufferedGraph(int i){
    	if (i>this.getBufferedGraphs().size()-1) return;
    	Graph ng = this.getBufferedGraphs().get(i);
    	this.setGraphMain(ng);
		this.notify(ng, CCModelChangedEvent.CHANGED_GRAPHS,true);
    	
    }

    /**
     * for removeGraph we need a structure which hold the index nuber and the graph itself
     * @author vrichter
     */
    public class IndexGraph {
    	int index;
    	Graph graph;
    
    	public IndexGraph(Graph g, int i) {
    		this.index = i;
    		this.graph = g;
		}
    	/** @return the index */
    	public  int getIndex() {return index;}
    	protected void setIndex(int i){this.index =i;}
	
    	/** @return the typ */
    	public  Graph getGraph() {return graph;}
		/** @param typ the typ to set */
    	protected  void setTyp(Graph typ) {this.graph = typ;}
	
    	
    }
    /**
     * get next Graph of given type after given index
     * @param typ typ if Graph to return
     * @param index start value
     * @return next Graph of given typ after index
     */
    public IndexGraph getNextGraph(int typ, int index){
    	if (index>bufferedGraphs.size()) return null;
    	for (int i=index;i<bufferedGraphs.size();i++){
    		Graph g = bufferedGraphs.get(i);
    		if ((typ==KURS_NAVGRAPH)&&(g instanceof NavGraph)){ //TODO an andere Kurstypen anpassen
    			IndexGraph ig =new IndexGraph(g,i);
    			return ig;
    		}
    	} 
    	
    	
    	return null;
    }

	/** @return the navModel */
	public NavModel getNavModel() {return navModel; }
    
	/** returns status of an selected cell<br>
	 * depends one Graphtyp. for NavGraph:<br>	
     * if more or less one cell is marked, status[0] and status[1] are empty, status[2] is ''false''  
     * @return status[0] is label<br>
     * status[1] is path<br>
     * status[2] is true if marked cell is an ComponentCell ATTENTION: its the String ''true''!
     * 
	 * @return
	 */
	protected Object[] getStatus(){
		if (this.getKursTyp()== KURS_NAVGRAPH) {//TODO an andere Graphtypen anpassen
			return getNavModel().getMainGraph().getController().getStatus();
			
		}
		
		 Object[] status = new Object[3];
	        status[0] = "";
	        status[1] = "";
	        status[2] = "false";
		return status;
	}
	
	
	/**
	 * moves a component
	 * @param direction 
	 */
    protected void move(int dir){
    	if (this.getKursTyp()== KURS_NAVGRAPH) {
			getNavModel().move(dir);
			
		}
    	
    }
    
    /**
     * sends the connectCommand
     */
    protected void connect(){
    	if (this.getKursTyp()== KURS_NAVGRAPH) {
    		
    		this.getNavModel().connect();
    	}
    }
    
    /**
     * sets gridsize
     * @param value
     */
    public void setGridsize(int value){
    	this.gridsize = value;
    	for (int i=0;i<this.getBufferedGraphs().size();i++){
    		Graph g =this.getBufferedGraphs().get(i); 
    		if (g instanceof NavGraph){
    			((NavGraph)g).setGridSize(this.gridsize);
    		}
    	}
    }
    
    /**
     * 
     * @return gridsize
     */
    public int getGridsize(){
    	return this.gridsize;
    	
    }
    
    /**
     * movesteps for moving Conponents with keyboard
     * @param value
     */
    public void setMoveStep(int value){
    	this.movestep=value;
    	this.controller.getConfig().setMOVE_STEP(value);
    }
    
    /**
     * @return movestep
     */
    public int getMoveStep(){
    	return this.movestep;
    	
    }
    
    /**
	 * @return the showGrid
	 */
	public boolean getShowGrid() {
		return this.showgrid;
	}

	/**
	 * @param showGrid the showGrid to set
	 */
	public void setShowGrid(boolean showGrid) {
		this.showgrid=showGrid;
		for (int i=0;i<this.getBufferedGraphs().size();i++){
    		Graph g =this.getBufferedGraphs().get(i); 
    		if (g instanceof NavGraph){
    			((NavGraph)g).setGridVisible(showGrid);
    		}
		}
	}
    
    /** toggles the edgedirection    */
    protected void toggleEdgeDirection(){
    	if (this.getKursTyp()== KURS_NAVGRAPH) {
    		this.getNavModel().toggleEdgeDirection();
    	}
    }
    
    /** moves a Susbcell 
     * 
     * @param s CommandConstants.MOVE_SUBCELL_IN or <br>
     * CommandConstants.MOVE_SUBCELL_OUT
     */
    protected void moveSubCell(String s){
    	
    	if (this.getKursTyp()== KURS_NAVGRAPH) {
    		this.getNavModel().moveSubCell(s);
    	}
    }
    
    /** vertauscht die beiden markierten cellen
     */
    protected void swapGraphCells(){
    	if (this.getKursTyp()== KURS_NAVGRAPH) {
    		this.getNavModel().swapGraphCells();
    	}
    }    
    
    /**
     * Save graph g<br>
     * if g is null the maingraph is saved
     * 
     * @param g
     * @param saveAs 
     */
    protected void save(Graph g,boolean saveAs){
    	if (g == null) g=this.getFirstGraph();
    	if (g instanceof NavGraph) this.getNavModel().save((NavGraph)g, saveAs);
    }
    
    /** returns the filename for the sourcecode of a xml file <br>
     *  ccModel.DUMMYFILE
     */
    protected String getSourceFileNameXML(){
    	
    	babble("ccModel showSource");
    	if (this.getFirstGraph()== null)return null;
    	if ((this.getFirstGraph().getMetaInfos().getSaveName()!=null) && (this.getFirstGraph().getChanged())){
    		
    		boolean dec = this.getController().dialogShowFile();
    				
    		if (this.getKursTyp() == KURS_NAVGRAPH){
    			if (dec)
    				return this.getNavModel().getMainGraph().getMetaInfos().getSaveDir()+this.getNavModel().getMainGraph().getMetaInfos().getSaveName();
    			else{
    				//zuerst speichern
    				try {
						this.navModel.saveGraph("./", DUMMYFILE,(NavGraph)this.getFirstGraph(), true);
					} catch (IOException e) {
						return null;
						
					} catch (TransformerException e) {
						return null;
						
					}
					return DUMMYFILE;
    			}//else
    		}//if (kurstyp)
//    		else if //FIXME anderer Kurstyp
    	}
    	else if (this.getFirstGraph().getMetaInfos().getSaveName()==null){
    		if (this.getKursTyp() == KURS_NAVGRAPH){
    			try {
					this.navModel.saveGraph("./", DUMMYFILE,(NavGraph)this.getFirstGraph(), true);
				} catch (IOException e) {
					return null;
					
				} catch (TransformerException e) {
					return null;
					
				}
				return DUMMYFILE;
    		}
//    		else if //FIXME anderer Kurstyp
    	}
    	else if (this.getFirstGraph().getChanged()){
    		if (this.getKursTyp() == KURS_NAVGRAPH){
    			return this.getNavModel().getMainGraph().getMetaInfos().getSaveDir()+this.getNavModel().getMainGraph().getMetaInfos().getSaveName();
    		}
    	}
    	//gleichheit
    	if (this.getKursTyp() == KURS_NAVGRAPH){
			return this.getNavModel().getMainGraph().getMetaInfos().getSaveDir()+this.getNavModel().getMainGraph().getMetaInfos().getSaveName();
		}
    	return null;
    }

	/**
	 * @return the courseCreator
	 */
	public CourseCreator getCourseCreator() {
		return courseCreator;
	}
	
	/** does the undo */
	protected void undo(){
		
		String fileName = this.UNDONAME+this.getFirstGraph().getUndoName() +"_"+ (this.getFirstGraph().getUndoNumber()-1);
		this.getFirstGraph().setUndoNumber(this.getFirstGraph().getUndoNumber()-1);
		this.load("./",fileName);
		
//		now the undograph is the first..
//		copy data from..
		this.getFirstGraph().getMetaInfos().setSaveName(
				this.getBufferedGraphs().get(1).getMetaInfos().getSaveName());
		this.getFirstGraph().getMetaInfos().setSaveDir(
				this.getBufferedGraphs().get(1).getMetaInfos().getSaveDir());
		this.getFirstGraph().setUndoNumber(this.getBufferedGraphs().get(1).getUndoNumber());
		this.getFirstGraph().setUndoName(this.getBufferedGraphs().get(1).getUndoName());
		this.removeGraph(1);
	}
	/** resaves a graph (when something happened) */
	public void updateUndo(Graph g){
		String fileName = this.UNDONAME+this.getFirstGraph().getUndoName()+"_"+ this.getFirstGraph().getUndoNumber();
		if (this.getKursTyp() == KURS_NAVGRAPH){
			try {
				this.navModel.saveGraph("./", fileName,(NavGraph)this.getFirstGraph(), true);
			} catch (IOException e) {
				return;
				
			} catch (TransformerException e) {
				return;
				
			}
		}// TODO anderer Kurstpy 	
	}

}
