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

package net.mumie.coursecreator.graph;

import com.jgraph.JGraph;
import com.jgraph.graph.CellMapper;
import com.jgraph.graph.CellView;
import com.jgraph.graph.ConnectionSet;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.Edge;
import com.jgraph.graph.EdgeView;
import com.jgraph.graph.GraphCell;
import com.jgraph.graph.GraphConstants;
import com.jgraph.graph.PortView;
import com.jgraph.graph.VertexView;
import com.jgraph.plaf.basic.BasicGraphUI;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;


import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CommandConstants; 
import net.mumie.coursecreator.Graph;


import net.mumie.coursecreator.events.CCModelChangedEvent;
import net.mumie.coursecreator.graph.cells.BranchCell;
import net.mumie.coursecreator.graph.cells.BranchCellConstants;
import net.mumie.coursecreator.graph.cells.BranchView;
import net.mumie.coursecreator.graph.cells.CCEdgeCell;
import net.mumie.coursecreator.graph.cells.CCEdgeView;
import net.mumie.coursecreator.graph.cells.CCGraphCell;
import net.mumie.coursecreator.graph.cells.CellConstants;
import net.mumie.coursecreator.graph.cells.ComponentCell;
import net.mumie.coursecreator.graph.cells.ComponentCellConstants;
import net.mumie.coursecreator.graph.cells.MainComponentCell;
import net.mumie.coursecreator.graph.cells.MainComponentConstants;
import net.mumie.coursecreator.graph.cells.MainComponentView;
import net.mumie.coursecreator.graph.cells.SubComponentCell;
import net.mumie.coursecreator.graph.cells.SubComponentConstants;
import net.mumie.coursecreator.graph.cells.SubComponentView;
import net.mumie.coursecreator.xml.GraphHolder;
import net.mumie.coursecreator.xml.GraphXMLizer;
import net.mumie.coursecreator.xml.XMLConstants;


/**
 * <p>
 * The main class for managing a Graph. 
 * </p>
 * It provides methods for saving and editing a graph
 * 
 * 
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @version $Id: NavGraph.java,v 1.85 2009/03/31 12:20:14 vrichter Exp $
 */

public class NavGraph extends JGraph implements Graph{
	
	public boolean pre = true; // erst wenn Graph fertig geladen ist, sind modelchanged events sinnvoll
	static final long serialVersionUID=0;
	
    private int lid_counter = 0; //
    private LinkedList<String> lids;
    private int undoNumber = -1;
    private String undoName = "iamannavgraphandihopenobodyguessesmyname";
    private int nid_counter = 0; //
    
    private boolean changed;
    private NavGraphController controller;
    private NavModel navModel;
    
    private boolean showArrows;
    
    private boolean setStatusDefault = false;
    private boolean setHomeCatDefault = false;
        
//    private LinkedList redCellList; // containes all red cells (should be only MainComponentCells)
    private LinkedList<CCEdgeCell> redEdgeList; // containes all red edges

    public boolean insertEdgeMode = false;
    public CCEdgeCell eCell;
    
    private MetaInfos metas;
    private boolean redIsUp = false; 
//  toXML
    private Document document = null;
    private DocumentBuilder builder;
    private String metaXML;
    private String contentXML;
    
    private Object[] lastMarkedCells;
    
    
    /**
     * Create a new NavGraph instance.
     */
    public NavGraph(NavModel c) {
    	super(new NavGraphModel());
    	
    	this.navModel = c;setConstraints();
    	
    	
    	setMarqueeHandler(new NavMarqueeHandler(this));
    	
    	this.metas = new MetaInfos(this.getNavModel().getCCModel().getController());
    	this.showArrows = true;
    	setGraphController(this);
    	this.redEdgeList = new LinkedList<CCEdgeCell>();
    	this.lids = new LinkedList<String>();
    	this.changed = true; // well .. setChange methode also notifys model
    	
    	((BasicGraphUI) getUI()).MAXCELLS = 1000;
    	((BasicGraphUI) getUI()).MAXHANDLES = 1000;
    	
    	//needed for saveXML
    	try {
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		builder = factory.newDocumentBuilder();
    	} catch (ParserConfigurationException pce) {
    		CCController.dialogErrorOccured(
    				"NavGraph ParserConfigurationException",
    				"<html>ParserConfigurationException while getting document builder<p>"+pce+"</html>",
    				JOptionPane.ERROR_MESSAGE);
    	}	
    	
    	this.lastMarkedCells = new Object[2];
    }
    
    /**
     * The contraints depending on the state.
     */
    private void setConstraints() {
    	// Tell the Graph to Select new Cells upon Insertion
    	setSelectNewCells(false);
    	// Make Ports Visible by Default
    	setPortsVisible(false);
    	// Use the Grid (but don't make it Visible)
    	setGridEnabled(true);
    	// Set the Grid size
    	this.setGridSize(this.getNavModel().getCCModel().getController().getConfig().getGRIDSIZE());
    	//set grid visiblwe
    	this.setGridVisible(this.getNavModel().getCCModel().getController().getConfig().getSHOWGRID());
    	this.setGridColor(Color.gray);
    	// Set the Snap Size to 2 Pixel
    	setSnapSize(CommandConstants.SNAP_SIZE);
    	// disallow resizing of cells
    	setSizeable(false);
    	// disallow editing of cells
    	setEditable(false);
    	// disallow disconnecting edges
    	setDisconnectable(false);
    }
    
    
    /**
     * Sets the controller for the given graph.
     * The controller which are set are the GraphSelectionListener,
     * the observer for the view and the GraphModelListener.
     */
    public void setGraphController(NavGraph graph) {
		controller = new NavGraphController(graph);
		graph.addGraphSelectionListener(controller);
		graph.addGraphSelectionListener(this.getNavModel().getCCModel().getCourseCreator().getMetaInfoField().getController());
		graph.getView().addObserver(controller);
		graph.getModel().addGraphModelListener(controller);
    }
    
    /**
     * determines if the current Graph is a SectionGraph
     * @return true graph is a Section
     *         false Graph is no Section
     */
    public boolean isSectionGraph(){
    	if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_SECTION) return true;
    	return false;
    } 
    
   
    /** set graphtype */
	public void setGraphType(int type){
    	this.metas.setGraphType(type);
    	this.setChanged(true);
    }
    
    /**
     * @return true graph is a Problem
     *         false Graph is no Problem
     */
    public boolean isProblemGraph(){
    	if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_HOMEWORK || 
    			this.metas.getGraphType()==MetaInfos.GRAPHTYPE_PRELEARN ||
    			this.metas.getGraphType()==MetaInfos.GRAPHTYPE_SELFTEST) return true;
    	return false;
    } 
    
    /**
     * @return true graph is a Element
     *         false Graph is no Element
     */
    public boolean isElementGraph(){
    	if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_ELEMENT) return true;
    	return false;
    } 
    
    /**
     * Returns true if the graph contains no cells.
     */
    public boolean isEmpty() {
    	return (getRoots().length == 0);
    }
    
    /**
     * 
     * @param category
     */
    private void insertFirstMainComponentCell(int category){
    	if (this.metas.getStatus()==MetaInfos.ST_UNDEFINED)this.setSetStatus(true);
    	if (MainComponentConstants.isElementCategory(category))  // first MainComponent defines Doctype
			this.metas.setGraphType(MetaInfos.GRAPHTYPE_ELEMENT);
			
		else if (MainComponentConstants.isProblemCategory(category)){
			this.metas.setGraphType(MetaInfos.GRAPHTYPE_HOMEWORK); // by default a new ProblemGraph is a HomeWork
    		this.setsetHomeCatDefault(true);
		}
		else if (MainComponentConstants.isSectionCategory(category))
			this.metas.setGraphType(MetaInfos.GRAPHTYPE_SECTION);
    }
    /**
     * creates new cell and generates nid and lid for it
     * @param cellType
     * @param category
     * @return
     */
    public Object createCell(int cellType, int category){
    	Object vertex=null;
    	
    	switch (cellType){
	    	case CellConstants.CELLTYPE_BRANCH:
	    		this.nid_counter++;
	    		vertex = new BranchCell(category, this.nid_counter);

	    		break;
	    	case CellConstants.CELLTYPE_MAINCOMPONENT:
	    		this.nid_counter++;
	    		this.lid_counter++;
	    		
	    		vertex = new MainComponentCell(this, category, this.nid_counter,Integer.toString(this.lid_counter));
	    	 	this.lids.addLast(String.valueOf(this.lid_counter));
	    		if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_UNDEFINED){// first Cell is inserted ..
	    			this.insertFirstMainComponentCell(category);
	        		        			
	        	}
	    		
	    		break;
	    	case CellConstants.CELLTYPE_SUBCOMPONENT: 
	    		Object selectedCell = getSelectionCell();
	    		if (selectedCell==null) return null;
	        	if ((selectedCell instanceof MainComponentCell)&&
	        			(!(MainComponentConstants.isProblemCategory(((MainComponentCell)selectedCell).getCategory())))) {
	        		this.lid_counter++;
	        		vertex = new SubComponentCell(this, category,(MainComponentCell)selectedCell,Integer.toString(this.lid_counter));
	        		this.lids.addLast(String.valueOf(this.lid_counter));
	        	} else return  null;//error
	    		break;
	    	default :
	    		return null;
	    		
    	}//switch
    	return vertex; 
    }

    /**
     * creates new cell and gets all informations
     * @param cellType branch main or Sub 
     * @param id 
     * @param cat category
     * @param nid nid for CCGraphCells, -1 for subComponents 
     * @param lid lid for ComponentCells, null for Branchcells
     * @param parent only for subComponents, null for Branch- and MainComponentCells 
     * @param x position for CCGraphCells, -1 for SubCells
     * @param y position for CCGraphCells, -1 for SubCells
     * @param label Label for ComponentCells, null for Branchcells
     * 
     * @return the new Cell
     */
    public Object createCell(int cellType, String path, int cat, int nid, String lid, Object parent, int x, int y,String label){
    	Object vertex=null;
    	
    	if (nid>-1)this.setNid_counter(Math.max(nid,this.nid_counter));
    	
    	//test if lid exists..
    	if (lid!=null){// FIXME what if lids contains letters??
    		
    		if (this.lids.contains(lid)){// lid ist allready existing // so set new one!
    			CCController.dialogErrorOccured(
    					"NavGraph: Error", 
    					"NavGraph: ERROR: LID " +lid+" allready existing!",
    					JOptionPane.ERROR_MESSAGE);
    			this.lid_counter++;
    			lid = String.valueOf(this.lid_counter);
    			
    		}else     		
    		this.lid_counter=Math.max(Integer.parseInt(lid),this.lid_counter);
    		
    		this.lids.addLast(lid);
    	}

    	switch (cellType){
	    	case CellConstants.CELLTYPE_BRANCH:
	    		
	    		vertex = new BranchCell(cat, nid);
	    		((CCGraphCell)vertex).setPosX(x);
	    		((CCGraphCell)vertex).setPosY(y);

	    		break;
	    	case CellConstants.CELLTYPE_MAINCOMPONENT:
	    		
	    		vertex = new MainComponentCell(this, cat, nid,lid);
	    	 	
	    		if (this.metas.getGraphType()==MetaInfos.GRAPHTYPE_UNDEFINED){// first Cell is inserted ..
	        		if (MainComponentConstants.isElementCategory(cat))  // first MainComponent defines Doctype
	        			this.metas.setGraphType(MetaInfos.GRAPHTYPE_ELEMENT);
	        		else if (MainComponentConstants.isProblemCategory(cat))
	        			this.metas.setGraphType(MetaInfos.GRAPHTYPE_HOMEWORK); // by default a new ProblemGraph is a HomeWork
	        		else if (MainComponentConstants.isSectionCategory(cat))
	        			this.metas.setGraphType(MetaInfos.GRAPHTYPE_SECTION);
	        	}
	    		
	    		((CCGraphCell)vertex).setPosX(x);
	    		((CCGraphCell)vertex).setPosY(y);
	    		((MainComponentCell)vertex).setLabel(label);
	    		((MainComponentCell)vertex).setElementPath(path);
	    		break;
	    	case CellConstants.CELLTYPE_SUBCOMPONENT: 
	    		
	    		if (!(parent instanceof MainComponentCell)) 
	    		vertex = new SubComponentCell(this, cat,null,lid);
	    		else
	        	vertex = new SubComponentCell(this, cat,(MainComponentCell)parent,lid);
	
	        	((SubComponentCell)vertex).setLabel(label);
	        	((SubComponentCell)vertex).setElementPath(path);
	    		break;
	    	default :
	    		return null;
	    		
    	}//switch
    	
    	return vertex; 
    }

    /**
     * creates a new cell (without document)
     * @param vertex - an {@link Object} with the vertex of this cell
     * @return the cell itself
     */
    public Object insertCell(Object vertex){    	
    	
    	if (vertex==null) return null;
    	
    	Point p = null;
    	Dimension size = null;
    	
	    if (vertex instanceof BranchCell) 
	    	size = BranchCellConstants.NODEDIM;
	    else if (vertex instanceof MainComponentCell)
	    	size = MainComponentConstants.ELDIM;
	    else if (vertex instanceof SubComponentCell)
	        size = SubComponentConstants.SUBELDIM;
	    else return null;
    	
    	if (vertex instanceof CCGraphCell){
    		((CCGraphCell)vertex).add(new DefaultPort());
    		
    		
    		if (((CCGraphCell)vertex).getPosX()==-1){
    		
	    		p = snap(getInsertPoint());
	    		((CCGraphCell)vertex).setPosX(p.x);
	    		((CCGraphCell)vertex).setPosY(p.y);
	    		
	    	}else {
	    		p = new Point(((CCGraphCell)vertex).getPosX(),((CCGraphCell)vertex).getPosY());
	    	}
    		
    	}
    	else if (vertex instanceof SubComponentCell){
    	   	// calculate position
        	Map atts = getView().getMapping(((SubComponentCell)vertex).getParent(),false).getAttributes();
        	
        	Rectangle rec = (Rectangle) atts.get("bounds");
        	p = rec.getLocation();
        	p.translate(20,20);
        	
    	}
    	
    	Map map = GraphConstants.createMap();

    	GraphConstants.setBounds(map, new Rectangle(p,size));
    	GraphConstants.setBorderColor(map, Color.black);
    	GraphConstants.setOpaque(map, true);
    	
    	Hashtable<Object, Map> attributes = new Hashtable<Object, Map>();
    	attributes.put(vertex, map);
    	getModel().insert(new Object[] {vertex}, null, null, attributes);
    	
   	 	if (vertex instanceof SubComponentCell){
	   	 	MainComponentCell parent = ((SubComponentCell)vertex).getParent();
		 		
			int pc = 0;
			for (int i=0; i<parent.getSubCells().size();  i++){
				
				SubComponentCell suc = (SubComponentCell) parent.getSubCells().get(i);
				if (SubComponentConstants.getAlignAsInt(((SubComponentCell)vertex).getCategory())
		    		>= SubComponentConstants.getAlignAsInt(suc.getCategory())){
					pc++;
				}else break;
			}
			
			parent.addSubCell(pc,(SubComponentCell)vertex);
			this.getView().toFront(getView().getMapping(parent.getSubCells().toArray()));

   	 	}
    	this.setChanged(true);
    	this.setSelectionCell(vertex);
    	
    	return vertex;
    }
    
    
    
	/**
	 * deletes all cells and edges which are selected,
	 * deletes a subComponent if its parent is selected
	 * deletes an edge if its start or endcell is selected
	 *
	 */
    public void delete(){
    	
    	Object[] selectedCells =getSelectionCells();
    	Object[] descends = this.getDescendants((selectedCells));
    	
    	//first add all cells/edges to these lists and remove them at the end
    	LinkedList<CCEdgeCell> edges = this.getEdgeCells();
    	LinkedList<CCEdgeCell> edgesDel = new LinkedList<CCEdgeCell>();
    	LinkedList<SubComponentCell> subcells = this.getSubComponentCells(); 
    	LinkedList<SubComponentCell> subcellsDel = new LinkedList<SubComponentCell>();
    	LinkedList<DefaultPort> ports = new LinkedList<DefaultPort>();
    	
    	// get depending ports
    	for (int i=0;i<descends.length;i++){
    		if (descends[i] instanceof DefaultPort) ports.addLast((DefaultPort)descends[i]);
    	} 
    	
    	// get depending edges 
    	for (int i=0;i<edges.size();i++){
    		CCEdgeCell e = edges.get(i);
    		CCGraphCell nidS = e.getStartCell();
    		CCGraphCell nidE = e.getEndCell();
    		
    		boolean depending = false;
    		boolean selected = false;
    		
    		for (int k=0;k<selectedCells.length;k++){
    			if (selectedCells[k] instanceof CCGraphCell){
    				if (((CCGraphCell)selectedCells[k]).equals(nidE)||
    					((CCGraphCell)selectedCells[k]).equals(nidS))
    					depending = true;
    			}else if (selectedCells[k] instanceof CCEdgeCell){
    				if (selectedCells[k].equals(e)) selected =true;
    			}
    		}
    		if (! selected)if (depending == true){
    			edgesDel.addLast(e);
    			// remove from red line..
    			if (this.getRedEdgeList().contains(e)) this.getRedEdgeList().remove(e);
    		}
    	}
    	
    	// get dependig subcells
    	for (int i=0;i<subcells.size();i++){// teste alle subcells, ob sie geloescht werden soolen
    		SubComponentCell sub = subcells.get(i);
    		MainComponentCell main = sub.getParent();
    		
    		boolean depending = false;
    		boolean selected = false;
    		
    		
    		for (int k=0;k<selectedCells.length;k++){
    			//vergleiche alle selectedCells, ob sie Vater der sub sind  
    			if (selectedCells[k] instanceof MainComponentCell){
    				if (((MainComponentCell)selectedCells[k]).equals(main)){
    					depending = true;
    				}
    			}else if (selectedCells[k] instanceof SubComponentCell){
    				if (selectedCells[k].equals(sub)) selected = true;
//    				 remove subcell from parentList
    				((SubComponentCell)selectedCells[k]).getParent().getSubCells().remove(selectedCells[k]);
    			}
    		}
    		if (! selected)
    		if (depending == true) subcellsDel.addLast(sub);
    	}

    	//remove depending edges, subs and ports
    	this.getModel().remove(edgesDel.toArray());
    	this.getModel().remove(subcellsDel.toArray());
    	this.getModel().remove(ports.toArray());
    	
    	// finally remove selected cells
    	this.getModel().remove(selectedCells);
    	
    	if (this.getMainComponentCells().size()==0)
    		this.metas.setGraphType(MetaInfos.GRAPHTYPE_UNDEFINED);
    	
    	this.setChanged(true);
    }

    /**
     * toggles the direction of one selected edge
     *
     */
    public void toggleEdgeDirection(){
    	if (this.getSelectionCells().length==1)
  		if (this.getSelectionCell() instanceof CCEdgeCell){
	    	((CCEdgeCell)this.getSelectionCell()).toggleDirection();
	    	this.clearSelection();
	    	this.setChanged(true);
  		}
    	
    }

    /**
     * gets an NodeID and returns the corresponding MainComponentcell
     * or null, if there is no MainComponentCell with this NID
     * @param nid the Nid we looking for an MainComponentCell
     * @return a MainComponetCell with the given NID or null, if no MainComponentCell with this Nid exists
     */
  	public MainComponentCell getMainComponentCellByNid(int nid){
  		
  		LinkedList<MainComponentCell> suspected = this.getMainComponentCells();
  		for (int i= 0;i<suspected.size();i++){
  			if ((suspected.get(i)).getNodeID()==nid)
  				return suspected.get(i);
  		}
  		return null;
  	}  
  	/**
     * gets an LocaleID and returns the corresponding Componentcell
     * or null, if there is no ComponentCell with this LID
     * @param lid the Lid we looking for an ComponentCell
     * @return a ComponentCell with the given LID or null, if no ComponentCell with this Lid exists
     */
  	public ComponentCell getComponentCellByLid(String lid){
  		
  		LinkedList<ComponentCell> suspected = this.getComponentCells();
  		for (int i= 0;i<suspected.size();i++){
  			if ((suspected.get(i)).getLid().equals(lid))
  				return suspected.get(i);
  		}
  		return null;
  	}
	  /**
	   * Zoom out while scale is greater then 0.2.
	   */
	public void zoomOut() {
	  	if (this.getScale() >= 0.2)
	  	this.setScale(this.getScale()-0.1);
	}

	/**
	 * Zoom in while scale is less then 0.9.
	 */
	public void zoomIn() {

	  	if (this.getScale() <= 0.9)
	  	this.setScale(this.getScale()+0.1);
  	}

	/**
	 * proofs if a SubCell is moveable
	 * @param cell the tested cell
	 * @param type in or out ..
	 * @return the cell wich to switch or null if not switchable
	 */
	public SubComponentCell isMoveable(SubComponentCell cell, String type){
		LinkedList<SubComponentCell> subcellList =cell.getParent().getSubCells();
		int cellPosition = subcellList.indexOf(cell);
		SubComponentCell switcher;
		
		if (type == CommandConstants.MOVE_SUBCELL_OUT){
			if (cellPosition==subcellList.size()-1)return null;
			switcher = subcellList.get(cellPosition+1);
		}
		else{
			if (cellPosition==0)return null;
			switcher = subcellList.get(cellPosition-1);
		}
		if (SubComponentConstants.getAlignAsInt(switcher.getCategory())!=SubComponentConstants.getAlignAsInt(cell.getCategory()))
			return null;// not same alignment
		return switcher;
	}
	
	/**
	 * change the position (the count in xml) of subcell
	 * @param type
	 */
	public void moveSubCell(String type){
		SubComponentCell cell = (SubComponentCell)this.getSelectionCell();
		LinkedList<SubComponentCell> subcellList =cell.getParent().getSubCells();
		
		SubComponentCell switcher=this.isMoveable(cell, type);
		
		if (switcher==null) return;
		
		//switch
		subcellList.add(subcellList.indexOf(cell),
				subcellList.remove(subcellList.indexOf(switcher)));

		this.setChanged(true);
		this.getController().arrangeSubCells(cell.getParent());
		this.clearSelection();
	}
	
	/**
	 * tries to toggle the exists-state
	 * for the selected 
	 */
	public void toggleExists(){
		
		if (this.getSelectionCount()==1)//not possible to toggle more then one edge
			// toggle edge from black to red or vice versa
		if (this.getSelectionCell() instanceof CCEdgeCell){
			if (!this.toggleEdgeExists((CCEdgeCell)this.getSelectionCell()))
				if (controller.dialogdeleteEdge((CCEdgeCell)this.getSelectionCell()))
					this.delete();
		}
	}
	
	/**
	 * toggles the exists-state if possible or returns false 
	 * an edge e exists iff e is in the net  
	 * @param e
	 * @return false if after toogleing the edge would be not existing and not on red line
	 */
    public boolean toggleEdgeExists(CCEdgeCell e){
       	if (!e.getRed() && e.getExists()) return false;
    	e.setExists(!e.getExists());
		this.setChanged(true);
		this.clearSelection();
		return true;
    }
    
	/**
	 * tries to toggle the red-state
	 * for the selected 
	 */
    public void toggleRed(){
		
		if (this.getSelectionCount()==1){//not possible to toggle more then one edge
			
			if (this.getSelectionCell() instanceof CCEdgeCell)

	    		if (!this.toggleRed((CCEdgeCell)this.getSelectionCell()))
					if (this.controller.dialogdeleteEdge((CCEdgeCell)this.getSelectionCell()))
						this.delete();
	    			
		}else{
			if (this.redLineIsPossible(this.getSelectionCells())){
				this.generateRedLine(this.getSelectionCells());
			}
		}		
	}
    
	/**
	 * toggles the redLine-state if possible or returns false 
	 * @param e
	 * @return false if after toogleing the edge would be not existing and not on red line
	 * otherwise it returns true
	 */
    public boolean toggleRed(CCEdgeCell e){
    	if (e.getRed() && !e.getExists()) return false;
    	
    	e.setRed(!e.getRed());
    	
    	if (e.getRed())this.getRedEdgeList().addLast(e);
    	else this.getRedEdgeList().remove(e);
		
    	this.setChanged(true);
		this.clearSelection();
		return true;
    }

    ////////////////////////////////////////////////////////
    // checkGraphFunctions
    ////////////////////////////////////////////////////////
    
    /**
     * checks if all Problems assigned valid pointvalues
     * pointvalue is valid iff it is greater than 0
     * @return 0 if every Problem has valid pointvalue<br>
     * -1 if this graph is no Problemgraph
     * s.th. > 0 if there is at least one Problem with invalid value and this returnvalue is the nid  
     */
    public int checkProblemsAssignedPoints(){
    	if (!this.isProblemGraph()) return -1;
    	LinkedList<MainComponentCell> cells = this.getMainComponentCells();
    	for (int i = 0 ; i < cells.size(); i++){
    		if (cells.get(i).getPoints()<= 0) return cells.get(i).getNodeID();
    	}
    	return 0;
    }
    
    /**
     * checks if to all ComponentCells is a Document assigned
     * @return an empty String if every ComponentCell has a Document assigned<br>
     * A String if at least one ComponentCell has no path the string represents to lid 
     */
    public LinkedList<String> checkDocumentsAssigned(){
    	LinkedList<ComponentCell> cells = this.getComponentCells();
    	LinkedList<String> evil = new LinkedList<String>();
    	for (int i = 0 ; i < cells.size(); i++){
    		if (cells.get(i).getElementPath().equals(ComponentCellConstants.PATH_UNDEFINED))
    			 evil.addLast(cells.get(i).getLid());
    	}
    	return evil;
    }
    
    /**
     * checks if a summary exists
     * @return true if an summary for this graph exists otherwise false
     */
    public boolean checkSummaryExists(){
    	if (this.getMetaInfos().getSummaryPath()==null) return false;
    	return true;
    }
    
    /**
     * sets sources or Drains -remaining on isSource- to Selectioncells to higlight them
     * @param isSource
     * @return
     */
    public boolean setSourcesOrDrains2SelectionCells(boolean isSource){
    	LinkedList<CCGraphCell> cells = this.getSourcesAndDrainsAndIsolated(isSource, this.getGraphCells(), this.getEdgeCells());
    	LinkedList<CCGraphCell> sourceAndDrains = new LinkedList<CCGraphCell>();
    	
    	for (int i=0;i<cells.size();i++){
    		Object child = cells.get(i).children().nextElement();
    		if (child instanceof DefaultPort) 
    			if (((DefaultPort) child).edges().hasNext()) 
    				sourceAndDrains.addLast(cells.get(i));
    	}
    	if (sourceAndDrains.size()==0) return false;

    	this.setSelectionCells(sourceAndDrains.toArray());
    	return true;
    }
    /**
     * returns a List of all graphcells which have:<br>
     * only outgoing edges<br>
     * only ingoing edges<br>
     * no edges
     */
    private LinkedList<CCGraphCell> getSourcesAndDrainsAndIsolated
    (boolean isSource, LinkedList<CCGraphCell> cells, LinkedList<CCEdgeCell> edges){
    	
    	LinkedList<CCGraphCell> graphCells =new LinkedList<CCGraphCell>();
    	for (int i=0;i<cells.size();i++) graphCells.addLast(cells.get(i));
    	
    	for (int i=0; i< edges.size();i++){
    		CCEdgeCell cell = (CCEdgeCell) edges.get(i);
    		if (isSource == cell.getReverse()) 
    			graphCells.remove(cell.getStartCell());
    		else graphCells.remove(cell.getEndCell());
    	}
    	
    	return graphCells;
    	
    }
    
    /** removes (recursivly) all sources or all drains (depending on isSource) from a list of cells and edges
     * so it returns all cells and edges which belong to at least one directed circle.
     * 
     * @param circleGraph is an Object[2] first entry is LinkedList cells second is LinkedList edges 
     * @param isSource true if remove Sources, falls if remove drains
     * @return
     */ 
    private Object[] removeSourcesOrDrains(Object[] circleGraph, boolean isSource){
    	LinkedList<CCGraphCell> cells = (LinkedList<CCGraphCell>)circleGraph[0];
    	LinkedList<CCEdgeCell> edges = (LinkedList<CCEdgeCell>)circleGraph[1];
    	LinkedList<CCGraphCell> sources;

    	while ((sources = this.getSourcesAndDrainsAndIsolated(isSource,(LinkedList<CCGraphCell>)cells,edges)).size()!=0){
    		for (int i=0;i<sources.size();i++){
    			CCGraphCell s = (CCGraphCell)sources.get(i);
    			
    			// remove all edges which are outedges for the source
    			// not nessesary to remove inEdges because it are sources! 
    			LinkedList<CCEdgeCell> removeEdgeList;
    			if (isSource) removeEdgeList=this.getOutEdges(s,false,true);
    			else removeEdgeList=this.getInEdges(s,false,true);
    			for (int k=0;k<removeEdgeList.size();k++)
    				edges.remove(removeEdgeList.get(k));
    			 
    		}
    		
    		// now remove all sources
    		for (int k=0;k<sources.size();k++) cells.remove(sources.get(k));

    	}
    	
    	circleGraph[0]= cells;
    	circleGraph[1]= edges;
    	return circleGraph;
    }

    /**
     * returns all cells for this graph which are part of at least one circle and the associated edges
     * @return
     */
    public Object[] testForCircles(){
    	LinkedList<CCGraphCell> cells = this.getGraphCells();
    	LinkedList<CCEdgeCell> edges = this.getEdgeCells();
    	if (cells.size()==0||edges.size()==0) return null;

    	Object[] circleGraph = new Object[2]; 
    	circleGraph[0]=cells;
    	circleGraph[1]=edges;
    	
    	circleGraph = this.removeSourcesOrDrains(circleGraph,true);
//    	 well, now we know if the graph contains circles,
//    	but we also need a drainless graph for the showCirclefunction!
    	circleGraph = this.removeSourcesOrDrains(circleGraph,false);
   	
    	return circleGraph;
    }

    
    /**
     * gets a list of circles and edges with no sources or drains 
     * selects one simple circle and sets thea to setSelectedCells  
     * 
     * @param cells
     * @param edges
     */
    public void showCircle(LinkedList cells, LinkedList edges){

    	if (cells.size()==0||edges.size()==0)return;
    	
    	CCGraphCell startCell = (CCGraphCell)cells.getFirst();
    	CCGraphCell actCell = startCell;
    	LinkedList<CCGraphCell> circleCells = new LinkedList<CCGraphCell>();
    	LinkedList<CCEdgeCell> circleEdges = new LinkedList<CCEdgeCell>();
    	
    	do {// you just can walk any way, because there are no drains!
    		cells.remove(actCell);
    		
    		circleCells.add(actCell);
			LinkedList<CCEdgeCell> outEdges = this.getOutEdges(actCell,false,true);
			CCEdgeCell actEdge= (CCEdgeCell)outEdges.removeFirst();
			while (!edges.contains(actEdge)){
				actEdge = (CCEdgeCell)outEdges.removeFirst();
			}
			circleEdges.add(actEdge);
			actCell = actEdge.getEndCell();
    		
		} while (!actCell.equals(startCell));// but stop if you reach one you alraedy had..
    	
    	
    	LinkedList circle = circleCells;
    	for (int i = 0;i<circleEdges.size();i++){
    		circle.addLast(circleEdges.get(i));
    	}
    	
    	this.setSelectionCells(circle.toArray());
    	
    }
    
    /**
     * @param isSource show source
     */
    public void showSourcesOrDrains(boolean isSource){
    	
    	if (this.getEdgeCells().size()==0) this.controller.dialogGraphContainsNoEdges();
    	else if (this.setSourcesOrDrains2SelectionCells(isSource)==false)
    		this.controller.dialogGraphContainsNoSourcesOrDrains(isSource);
    } 
    
    /** showcircles */
    public void showCircles(){
    	Object[] circleGraph  = this.testForCircles();
    	if (circleGraph==null){
    		if (this.getGraphCells().size()==0)
    		this.controller.dialogGraphContainsNoVertices();
    		else this.controller.dialogGraphContainsNoEdges();
    		return;
    	}
    	if (circleGraph[0]==null||((LinkedList)circleGraph[0]).size()==0){
    		this.controller.dialogGraphContainsNoCircles();
    		return;
    	}
    	
    	this.showCircle((LinkedList)circleGraph[0],(LinkedList)circleGraph[1]);
    	
    } 

    
    /**
     * all errormessages for red Line 
     * 
     * @param cell an cell where an error occured
     * @param errorCode: <br>
     * 0 everything ok<br>
     * 1 cell has more then one incomming red edges<br>
     * 2 graph has more the one startcell<br>
     * 3 graph has more the no startcell means graph is a circle<br>
     * <br>
     * 5 Graph has no red edges<br>
     * 6 cell has more then one outgoing red edges<br>
     * @return
     */
    public static String generateCheckRedLineError(Object cell, int errorCode){
    	String message = null;
    	switch (errorCode){
    		case 0: return "Der rote Faden ist korrekt";
	    	case 1: 
	    		
	    		if (cell  instanceof BranchCell) {
					message = BranchCellConstants.getCategoryAsString(((BranchCell)cell).getCategory());
			
				} else if (cell  instanceof MainComponentCell) {
					message = ((MainComponentCell)cell).getCellDescription();
				}
	    		
	    		return "("+message + ") hat zu viele eingehende roten Kanten";
	    	
	    	case 2: return "es gibt mehr als einen Anfang des roten Fadens";
	    	case 3: return "Der rote Faden ist ein Kreis";
	    	case 5: return "Es sind keine roten Kanten vorhanden";
	    	case 6:
	    		if (cell  instanceof BranchCell) {
					message = BranchCellConstants.getCategoryAsString(((BranchCell)cell).getCategory());
			
				} else if (cell  instanceof MainComponentCell) {
					message = ((MainComponentCell)cell).getCellDescription();
				}
	    		return "("+message + ") hat zu viele ausgehende roten Kanten";
	    	case 7: return BranchCellConstants.getCategoryAsString(((BranchCell)cell).getCategory())+" ist auf dem roten Faden";
	    	
	    	default:return "unbekannter Fehler" +errorCode; 
	    }
    	
    }

    /**
     * 
     * @param selectedCells
     * @return if no edges are selected or if at least one edge is "open"
     */
    public boolean redLineIsPossible(Object[] selectedCells){ 
    	LinkedList<CCEdgeCell> allEdges = new LinkedList<CCEdgeCell>(); //edges in selection
    	for (int i = 0; i<selectedCells.length; i++){
    		if (selectedCells[i] instanceof CCEdgeCell){
    			if (((CCEdgeCell)selectedCells[i]).isRedPossible()) return true;
    			allEdges.addLast((CCEdgeCell)selectedCells[i]);
    		}
    	}
    	
    	//no edges in selection
    	if (allEdges.size()==0) return false;
    	
    	LinkedList<CCEdgeCell> ed = new LinkedList<CCEdgeCell>();//selected edges which doesn't have 2 MainKomps
    	for (int i=0;i<allEdges.size();i++){
    	if (!((CCEdgeCell)selectedCells[i]).isRedPossible())
    		ed.addLast(allEdges.get(i));
    	}
    	
    	while (!ed.isEmpty()){// 
    		CCEdgeCell firstEdge = null;
    		CCEdgeCell lastEdge = null;
    		CCEdgeCell middleEdge = null;
    		for (int i=0;i<ed.size();i++){// search fo starts
    			if (ed.get(i).getStartCell() instanceof MainComponentCell&& !ed.get(i).getReverse()||
    					ed.get(i).getEndCell() instanceof MainComponentCell&& ed.get(i).getReverse()) {
    				firstEdge = ed.remove(i);
    				break;
    			}
    		}
    		if (firstEdge== null) return false;
    		
    		
    		//kanten zusammensetzen
    		LinkedList<CCEdgeCell> detectedEdge = new LinkedList<CCEdgeCell>();
    		while (lastEdge == null){
	    		detectedEdge.addLast(firstEdge);
	    		
	    		CCGraphCell start = null;
	    		if (!firstEdge.getReverse()) start = firstEdge.getEndCell();
	    		else start = firstEdge.getStartCell();
    			LinkedList<CCEdgeCell> outEdges = this.getOutEdges(start);
	    		
	    		for (int i = 0; i<outEdges.size();i++){
	    			if (ed.contains(outEdges.get(i))){ 
	    				middleEdge = ed.remove(ed.indexOf(outEdges.get(i)));
	    				break;
	    			}
	    		}
	    		if (middleEdge == null) return false;
	    		if ((!middleEdge.getReverse()&& middleEdge.getEndCell() instanceof MainComponentCell)
	    				||(middleEdge.getReverse()&& middleEdge.getStartCell() instanceof MainComponentCell)){
	    			lastEdge = middleEdge;
	    			detectedEdge.addLast(lastEdge);
	    			return true;
	    		}
	    		else {
	    			firstEdge = middleEdge;
	    			middleEdge = null;
	    		}
    		}
    	}
    	
    	
    	return true;
    	
    }
    
    /**
     * generates from a set of selected edges red lines if possible
     * @param cells from which red lines are generated
     * @return true if at least one line could be generated
     */
    
    public boolean generateRedLine(Object[] cells){
    	
    	if (!this.redLineIsPossible(cells)) return false;
    	
    	boolean status = false;
    	
    	LinkedList<CCEdgeCell> edges = new LinkedList<CCEdgeCell>();// all cells
    	LinkedList<CCEdgeCell> ed = new LinkedList<CCEdgeCell>();// only cells where red is possible
    	
    	// get all edges form all cells 
    	for (int i = 0; i<cells.length; i++){
    		if (cells[i] instanceof CCEdgeCell)
    			edges.addLast((CCEdgeCell)cells[i]);
    	}
    	
    	// add all "open" edges to ed and set all "normal" edges red  
    	for (int i=0; i<edges.size();i++)
    		if (((CCEdgeCell)edges.get(i)).isRedPossible())
    			status = ((CCEdgeCell)edges.get(i)).setRed(true)||status;
    		else {
    			ed.addLast(edges.get(i));
    		}
    	
    	while (!ed.isEmpty()){// 
    		CCEdgeCell firstEdge = null;
    		CCEdgeCell lastEdge = null;
    		CCEdgeCell middleEdge = null;
    		for (int i=0;i<ed.size();i++){// search fo starts
    			if ((!ed.get(i).getReverse())&&(ed.get(i).getStartCell() instanceof MainComponentCell)||
    					(ed.get(i).getReverse())&&(ed.get(i).getEndCell() instanceof MainComponentCell)) {
    				firstEdge = ed.remove(i);
    				break;
    			}
    		}
    		if (firstEdge== null) return status;
    		
    		
    		//kanten zusammensetzen
    		LinkedList<CCEdgeCell> detectedEdge = new LinkedList<CCEdgeCell>();
    		while (lastEdge == null){
    			CCGraphCell end;
    			if (!firstEdge.getReverse()) end = firstEdge.getEndCell();
    			else end = firstEdge.getStartCell();
	    		LinkedList<CCEdgeCell> outEdges = this.getOutEdges(end);
	    		
	    		detectedEdge.addLast(firstEdge);
	    		for (int i = 0; i<outEdges.size();i++){
	    			if (ed.contains(outEdges.get(i))){ 
	    				middleEdge = ed.remove(ed.indexOf(outEdges.get(i)));
	    				break;
	    			}
	    		}
	    		if (middleEdge == null) return false;
	    		if ((!middleEdge.getReverse())&&(middleEdge.getEndCell() instanceof MainComponentCell)
	    				||((middleEdge.getReverse())&&(middleEdge.getStartCell() instanceof MainComponentCell))){
	    			lastEdge = middleEdge;
	    			detectedEdge.addLast(lastEdge);
	    		}
	    		else {firstEdge = middleEdge;middleEdge = null;}
    		}

    		//now all edges are ordered by the path
    		
    		//generate CornerPoints
    		LinkedList<Point> cornerPoints = new LinkedList<Point>();
        	for (int i=0;i<detectedEdge.size();i++){
        		LinkedList<Point> points = detectedEdge.get(i).getDrawEdgeList();
        		if (!detectedEdge.get(i).getReverse()){
	        		for (int k=0;k<points.size();k+=2){
	        			cornerPoints.addLast(points.get(k));
	        		}
        		}
        		else {// edge is reverse, so take points the other way around
        			for (int k=points.size()-1;k>=0;k-=2){
            			cornerPoints.addLast(points.get(k));
            		}
        		}
        	}
        	
        	CCGraphCell start;
        	if (!detectedEdge.getFirst().getReverse()) start = detectedEdge.getFirst().getStartCell();
        	else start = detectedEdge.getFirst().getEndCell();
        	CCGraphCell end;
        	if (!detectedEdge.getLast().getReverse()) end = detectedEdge.getLast().getEndCell();
        	else end = detectedEdge.getLast().getStartCell();
        	
        	//add new edge
        	this.insertEdge(start, end, cornerPoints, true, false);
        	
    	}
    	
    	this.clearSelection();
    	this.setChanged(true);
    	return true;
    }
    
    /**
     * 
     * @return
     */
    public String checkRedLine(){
    	    	
    	LinkedList<CCEdgeCell> edges =this.getRedEdgeList();
    	if (edges.size()==0) return generateCheckRedLineError(null, 5);
    	
    	LinkedList<CCGraphCell> cells = new LinkedList<CCGraphCell>();//a list of all cells belonging to red line
    
    	for (int i=0;i<edges.size();i++){
    		CCGraphCell cell = ((CCEdgeCell)edges.get(i)).getStartCell();
    		if (!cells.contains(cell))
    		cells.addLast(cell);
    		
    		cell = ((CCEdgeCell)edges.get(i)).getEndCell();
    		if (!cells.contains(cell))
    		cells.addLast(cell);
    	}
    	
    	CCGraphCell startCell = null;
    	for (int i=0;i<cells.size();i++){
    		
    		if (cells.get(i) instanceof BranchCell) return generateCheckRedLineError(cells.get(i), 7);
    		
    		LinkedList<CCEdgeCell> redInEdges = this.getInEdges((CCGraphCell)cells.get(i),true,false);
    		LinkedList<CCEdgeCell> redOutEdges = this.getOutEdges((CCGraphCell)cells.get(i),true,false);
    		
    		// check if exactly one edge is ingoing and one is outgoing
    	 	if (redInEdges.size()>1) return generateCheckRedLineError(cells.get(i),1); //graph with deg() > 2 has branches!
    		if (redOutEdges.size()>1) return generateCheckRedLineError(cells.get(i),6); //graph with deg() > 2 has branches!
    	
    		// found one which can be first cell
    		if ((redOutEdges.size()==1)&&(redInEdges.size()==0)) 
    			if (startCell!=null) return generateCheckRedLineError(null,2); 
    			else startCell = (CCGraphCell)cells.get(i);
    	}
    	
    	if (startCell==null) return generateCheckRedLineError(null,3); // kein pfad sondern Kreis
    	return generateCheckRedLineError(null,0);
    }
    
    /**
     * checks if red Line is valid. if its valid returns an empty String, else it returns the errorating
     * @return
     */
    public String redLineValid(){
    	String status = this.checkRedLine();
    	if ((status.equals(generateCheckRedLineError(null, 0)))||
    	(status.equals(generateCheckRedLineError(null,5 ))))
    	return "";
    	return status;
    }
   
    /**
     * List of all out edges connected to cell (depending on red an exist)
     * so cell is Endvertex and edge is not reverse or cell is Startvertex if edge is reverse
     * @param cell
     * @return
     */
    private LinkedList<CCEdgeCell> getInEdges(CCGraphCell cell, boolean red, boolean exist){
    	LinkedList<CCEdgeCell> edges = new LinkedList<CCEdgeCell>();
    	Enumeration children =cell.children();
		while(children.hasMoreElements()) {
    		Object child = children.nextElement();
    		if (child instanceof DefaultPort) {
    			Iterator i = ((DefaultPort) child).edges();
    			while(i.hasNext()) {
    				CCEdgeCell e = (CCEdgeCell) i.next();
//    				if (e.getRed()){			
    				if ((red && e.getRed())||(exist && e.getExists())){
    					if ((e.getStartCell().equals(cell)&&e.getReverse())||(e.getEndCell().equals(cell)&&!e.getReverse()))
    					edges.addLast(e);
    				}
    			}
    		}
		}
		return edges;
    }
    
    /**
     * List of all red out edges connected to cell
     * so cell is Startvertex and edge is reverse or Endvertex if edge is reverse
     * @param cell
     * @return
     */
    private LinkedList<CCEdgeCell> getOutEdges(CCGraphCell cell, boolean red, boolean exist){
    	LinkedList<CCEdgeCell> redEdges = new LinkedList<CCEdgeCell>();
    	Enumeration children =cell.children();
		while(children.hasMoreElements()) {
    		Object child = children.nextElement();
    		if (child instanceof DefaultPort) {
    			Iterator i = ((DefaultPort) child).edges();
    			while(i.hasNext()) {
    				CCEdgeCell e = (CCEdgeCell) i.next();
//    				if (e.getRed()){
    				if ((red && e.getRed())||(exist && e.getExists())){
    					if ((e.getStartCell().equals(cell)&&!e.getReverse())||(e.getEndCell().equals(cell)&&e.getReverse()))
    					redEdges.addLast(e);
    				}
    			}
    		}
		}
		return redEdges;
    }
    
    /**
     * returns all outgoing edges for a cell
     * @param cell
     * @return
     */
    private LinkedList<CCEdgeCell> getOutEdges(CCGraphCell cell){
    	LinkedList<CCEdgeCell> edges = new LinkedList<CCEdgeCell>();
    	Enumeration children =cell.children();
		while(children.hasMoreElements()) {
    		Object child = children.nextElement();
    		if (child instanceof DefaultPort) {
    			Iterator i = ((DefaultPort) child).edges();
    			while(i.hasNext()) {
    				CCEdgeCell e = (CCEdgeCell) i.next();
    					if ((e.getStartCell().equals(cell)&&!e.getReverse())||(e.getEndCell().equals(cell)&&e.getReverse()))
    					edges.addLast(e);
    			}
    		}
		}
		return edges;
    }
    
    
    /////////////////////////////////////////////
    // methodes for edges
    /////////////////////////////////////////////
    /** connect start and end */ 
    public void connect(Object start,Object end){
    	Object[] objs = new Object[2];
    	objs[0] = start;
    	objs[1] = end;
    	this.setSelectionCells(objs);
    	this.connect();
    	
    }
    /**
     * Connect two CCGraphCells with black line iff
     * two Cells are selected and both are CCGraphCells
     * sets that one with smaller y-koordinate as startcell
     */
    public void connect() {
    	
    	Object[] objs = getSelectionCells();
    	
    	if((objs.length == 2) && (objs[0] instanceof CCGraphCell) && (objs[1] instanceof CCGraphCell)) {
    		CCGraphCell startCell = (CCGraphCell)objs[0];
    		CCGraphCell endCell = (CCGraphCell)objs[1];
    		
        	Point pStart= ((Rectangle)getView().getMapping(startCell,false).getAttributes().get("bounds")).getLocation();
        	Point pEnd= ((Rectangle)getView().getMapping(endCell,false).getAttributes().get("bounds")).getLocation();
        	
        	if (this.getLastMarkedCells()[0]!=null&&this.getLastMarkedCells()[1]!=null){
        		if (this.getLastMarkedCells()[0].equals(startCell)&&this.getLastMarkedCells()[1].equals(endCell)){
        		
        		this.prepareInsertEdge(startCell,new Point(pStart.x+CommandConstants.ZWOELF,pStart.y+CommandConstants.ZWOELF),false,true);
        		this.insertEdge(endCell,new Point(pEnd.x+CommandConstants.ZWOELF,pEnd.y+CommandConstants.ZWOELF));
        	}
        	else{
        		
        			this.prepareInsertEdge(endCell,new Point(pEnd.x+CommandConstants.ZWOELF,pEnd.y+CommandConstants.ZWOELF),false,true);
	        		this.insertEdge(startCell,new Point(pStart.x+CommandConstants.ZWOELF,pStart.y+CommandConstants.ZWOELF));
        	
        		}
        	}else{
	        	if ((pStart.y<pEnd.y)||((pStart.y==pEnd.y)&&(pStart.x<pEnd.x))){// startCell is thatone with smaller y-koordinate
	            	this.prepareInsertEdge(startCell,new Point(pStart.x+CommandConstants.ZWOELF,pStart.y+CommandConstants.ZWOELF),false,true);
	        		this.insertEdge(endCell,new Point(pEnd.x+CommandConstants.ZWOELF,pEnd.y+CommandConstants.ZWOELF));
	        		
	        	}else{
	            	this.prepareInsertEdge(endCell,new Point(pEnd.x+CommandConstants.ZWOELF,pEnd.y+CommandConstants.ZWOELF),false,true);
	        		this.insertEdge(startCell,new Point(pStart.x+CommandConstants.ZWOELF,pStart.y+CommandConstants.ZWOELF));
	        		
	        	}
        	}
    	}
    }
    

    /**
     * interchange two selected graphcells
     */
    public void swapGraphCells(){
    	
    	if (this.getSelectionCount()!= 2)return;
    	Object cell1 = this.getSelectionCells()[0];
    	Object cell2 = this.getSelectionCells()[1];
    	
    	if (!(cell1 instanceof CCGraphCell))
   		if (!(cell2 instanceof CCGraphCell)) return;

    	//both of type CCGraphCell
		CCGraphCell gc1 = (CCGraphCell)cell1;
		CCGraphCell gc2 = (CCGraphCell)cell2;
		
		LinkedList<CCEdgeCell> edges = new LinkedList<CCEdgeCell>();
		LinkedList<CCEdgeCell> allEdges = this.getEdgeCells();
		
		//get all edges in list edges
		for (int i=0;i<allEdges.size();i++){
			CCEdgeCell edge = allEdges.get(i);
			if (!edges.contains(edge)){
    			if ((edge.getStartCell().equals(gc1))
    					||(edge.getStartCell().equals(gc2))
    					||(edge.getEndCell().equals(gc1))
    					||(edge.getEndCell().equals(gc2)))
    				edges.addLast(edge);
			}
		}
		
		// new document
		this.document = builder.newDocument();
		if (this.document == null) {
    		CCController.dialogErrorOccured("ERROR", "ERROR: document is null", JOptionPane.ERROR_MESSAGE);
    		return;
		}

		// save edges
		GraphHolder gl = new GraphHolder(this.document);
		Element csection = gl.makeSwapCellXML(edges, String.valueOf(gc1.getNodeID()), String.valueOf(gc2.getNodeID()));
		
		//delete edges
		this.setSelectionCells(edges.toArray());
    	this.delete();
    	
    	//now position change
		int x = gc1.getPosX();
		int y = gc1.getPosY();
		gc1.setPosX(gc2.getPosX());
		gc1.setPosY(gc2.getPosY());
		gc2.setPosX(x);
		gc2.setPosY(y);
		
		// and update cells -otherwise the do not move
		this.cellMoved(this.getCellView(gc1));
		this.cellMoved(this.getCellView(gc2));
		
		//last reload edges
    	try {
    		gl.setEdges(csection, this);
			
		} catch (JaxenException e) {
			CCController.dialogErrorOccured("ERROR", "ERROR: Graph konnte nicht geladen werden", JOptionPane.ERROR_MESSAGE);
		}
    	
    	this.setChanged(true);
    }

	

	/**
	 * babbles all edges!
	 */
    public void babbleAllEdges() {
		for (int k=0;k<this.getEdgeCells().size();k++){
			CCEdgeCell e = this.getEdgeCells().get(k);
			CCEdgeView ev = (CCEdgeView)getView().getMapping(e,false);
			babble("\n");
			for (int i=0;i<ev.getPointCount();i++){
				
				int x=0;
				int y=0;
				if (i==0){ 
					x = e.getStartCellPoint().x; 
					y = e.getStartCellPoint().y;
					System.out.println("source: "+e.getSource());
				
				}else if (i==ev.getPointCount()-1){
					x = e.getEndCellPoint().x; 
					y = e.getEndCellPoint().y;
				}else {
					x = e.getDrawEdgeList().get(i-1).x;
					y = e.getDrawEdgeList().get(i-1).y;
					
				}
				
				
				System.out.print("Edge: ("+ x+ ","+y
					  +") Ev ("+ev.getPoint(i).x+","+ev.getPoint(i).y+")");
				if (x!=ev.getPoint(i).x) System.out.print("X");
				if (y!=ev.getPoint(i).y) System.out.print("Y");
				
				System.out.println("");
				if (i==ev.getPointCount()-1) System.out.println("target: "+e.getTarget());
			}
		
		}
	}

    /** babbles all cells <br>
     * for gebug useful */
    public void babbleAllCells() {
		for (int k=0;k<this.getMainComponentCells().size();k++){
			MainComponentCell e = this.getMainComponentCells().get(k);
			
			System.out.println(e);
			for (int i=0;i<e.getSubCells().size();i++){
				System.out.println("sub "+e.getSubCells().get(i));
			}
		
		}
	}

    /** babbles all branches <br>
     * for gebug useful */
    public void babbleAllBranches() {
		for (int k=0;k<this.getBranchCells().size();k++){
			BranchCell e = this.getBranchCells().get(k);
			
			System.out.println(e);
		
		}
	}
    
    /**
     * sets startvertex, location of Startvertex  and edgeDrawmode to this.eProp
     * @param cell
     */
    public void prepareInsertEdge(CCGraphCell cell,Point p,boolean red,boolean exists){
    	this.eCell = new CCEdgeCell(this);
    	this.eCell.setStartCell(cell);
    	this.eCell.setRed(red);
    	this.eCell.setExists(exists);
		this.insertEdgeMode = true;  
		this.eCell.setStartCellPoint(p);
    }

    /**
     * generates Edge between this.eProp and cell
     * @param cell Endvertex
     * @param p Point of EndVertex
     */
    public void insertEdge(CCGraphCell cell,Point p){
    	
    	this.eCell.setEndCell(cell);
    	this.eCell.setEndCellPoint(p);
    	
    	this.eCell.concludeAdding();
    	this.insertEdgeMode = false;
    	
    	//connect and insert
    	this.connectOrthByHand(this.eCell,this.eCell.getRed(),this.eCell.getExists());
    	
    }

    /** inserts an edge into this graph. 
     * 
     * @param startCell
     * @param endCell
     * @param cornerPoints holding only the cornerPoints. the middlepoints are calculated by this function
     * @param isRed
     * @param exists
     */
    public void insertEdge(CCGraphCell startCell, CCGraphCell endCell, LinkedList<Point> cornerPoints, boolean isRed, boolean exists){

    	LinkedList<Point> points= new LinkedList<Point>();
    	for (int i=0;i<cornerPoints.size()-1;i++){
    		points.addLast(cornerPoints.get(i));
    		points.addLast(new Point(((int)(((Point)cornerPoints.get(i)).x+((Point)cornerPoints.get(i+1)).x)/2)
    				,((int)(((Point)cornerPoints.get(i)).y+((Point)cornerPoints.get(i+1)).y)/2)));
    		
    	}
    	points.addLast(cornerPoints.getLast());
    	
    	this.prepareInsertEdge(startCell,new Point(startCell.getPosX()+CommandConstants.ZWOELF,startCell.getPosY()),isRed,exists);
    	
    	this.eCell.setDrawEdgeList(points);
    	this.eCell.setEndCell(endCell);
    	this.eCell.setEndCellPoint(new Point(endCell.getPosX()+CommandConstants.ZWOELF,endCell.getPosY()));
    	
    	this.insertEdgeMode = false;
    	
    	this.connectOrthByHand(this.eCell,this.eCell.getRed(),this.eCell.getExists());
    	
    }
    
    /**
     * connects start and end using the Pointlist on they are not connected
     * ruft auch setCheanged auf -- undo
     * @param edge edgeCell
     * @param red is on red line?
     * @param exists is it black?
     */
    public void connectOrthByHand(CCEdgeCell edge, boolean red, boolean exists){
    	LinkedList<Point> list=edge.getDrawEdgeList();
    	CCGraphCell start=edge.getStartCell();
    	CCGraphCell end=edge.getEndCell();
    	
    	if (start.equals(end)) return;
    	
    	edge.setRed(red);
    	if (edge.getRed()) this.getRedEdgeList().addLast(edge);
    	
    	edge.setExists(exists);
    	
    	ConnectionSet cs = new ConnectionSet();
		cs.connect(edge, start.getChildren().get(0), end.getChildren().get(0));
		
		// create Attributes of Edge
		Map map = GraphConstants.createMap();
		Hashtable attributes = new Hashtable();
		
		GraphConstants.setLineStyle(map,GraphConstants.ORTHOGONAL);// Kanten orthogonal
		
		attributes.put(edge, map);
		    		
		// Insert edge
		getModel().insert(new Object[] {edge}, cs, null, attributes);
		// Move edge to back
		getView().toBack(getView().getMapping(new Object[] {edge}));
		// Insert Point
		CCEdgeView ev = (CCEdgeView)getView().getMapping(edge,false);
		    		
		// left points of elements
		Point pStart = ((Rectangle)(getView().getMapping(start,false).getAttributes()).get("bounds")).getLocation();
		Point pEnd = ((Rectangle)(getView().getMapping(end,false).getAttributes()).get("bounds")).getLocation();
		
		//maybe there exists no points in the Pointlist
		if (list.size()==0){
			
			list.addLast(new Point(pStart.x+CommandConstants.ZWOELF,(pStart.y+pEnd.y)/2+CommandConstants.ZWOELF));
			list.addLast(new Point((pStart.x+pEnd.x)/2+CommandConstants.ZWOELF,(pStart.y+pEnd.y)/2+CommandConstants.ZWOELF));
			list.addLast(new Point(pEnd.x+CommandConstants.ZWOELF,(pStart.y+pEnd.y)/2+CommandConstants.ZWOELF));
		}
		
		// orthogonal first n last Points 
		Point firstPoint = new Point(pStart.x+CommandConstants.ZWOELF,((Point)list.getFirst()).y); // +12 cause not center but left points
		Point lastPoint = new Point(pEnd.x+CommandConstants.ZWOELF,((Point)list.getLast()).y);
		
		//change EdgeDraw list
		list.set(0,firstPoint);
		list.set(list.size()-1,lastPoint);
		
		//but now centerpoints also moved!
		Point move = (Point) list.get(1);
		Point pre = firstPoint;
		Point next = (Point)list.get(2);
		move.setLocation((pre.x+next.x)/2,(pre.y+next.y)/2);
		list.set(1,move);
		
		move = (Point) list.get(list.size()-2);
		pre = (Point) list.get(list.size()-3);
		next = lastPoint;
		move.setLocation((pre.x+next.x)/2,(pre.y+next.y)/2);
		list.set(list.size()-2,move);
		
		// add all points to edgePointlist
		for (int i=1;i<=list.size();i++) ev.addPoint(i,(Point)list.get(i-1));//verschoben um 1, da erster/letzter Punkt Port
	  
		this.setChanged(true);
       	
    	this.clearSelection();
   
    }
    /**
     * this Function moves all components
     * of this graph by x and y pixel
     * @param x
     * @param y
     */
    public void moveWholeGraph(int x, int y){
    	
    	LinkedList<CCGraphCell> graphCells =  this.getGraphCells();
    	for (int i = 0 ; i< graphCells.size(); i++){//setCells
    		
    		CCGraphCell cell = (CCGraphCell)graphCells.get(i);
    		
    		cell.setPosX(cell.getPosX()+x);
    		cell.setPosY(cell.getPosY()+y);
    		
    		this.cellMoved(this.getCellView(cell));
    	}
    	LinkedList<CCEdgeCell> edges = this.getEdgeCells();//set edges
    	for (int i = 0 ; i< edges.size(); i++){
    		CCEdgeCell edge = (CCEdgeCell)edges.get(i);
    		LinkedList<Point> points = edge.getDrawEdgeList();
    		LinkedList<Point> newPointList = new LinkedList<Point>();
    		
    		CCEdgeView ev = (CCEdgeView)getView().getMapping(edge,false);
    		
    		for (int k = 0; k < points.size();k++){
    			Point p = (Point) points.get(k);
    			Point newP = new Point(p.x , p.y + y); 
    			newPointList.addLast(newP);
    			
    			ev.setPoint(k+1, newP);
    		}
    		edge.setDrawEdgeList(newPointList);// update der Punkte
    		this.edgeMoved(ev);
    	}
    	
    }
    
    /** this shall switch the paintorder<br>
     * unfortunately, it doesn't work FIXME
     */
    public void switchPaintOrder(){
    	Object[] cells = this.getRoots();
    	for (int i=0; i<cells.length;i++){
    		if (cells[i]  instanceof CCEdgeCell) {
    			babble("getRoots edge red:" +((CCEdgeCell)cells[i]).getRed());
    			babble("getRoots edge exists: "+ ((CCEdgeCell)cells[i]).getExists());
			}
    	}
    	LinkedList<CCEdgeCell> edges = new LinkedList<CCEdgeCell>();
    	for (int i=0; i<cells.length;i++){
    		if (cells[i]  instanceof CCEdgeCell) {
    			CCEdgeCell edge = (CCEdgeCell) cells[i];
    			if (this.redIsUp){ // move red edges to start 
    				if (!edge.getRed()) edges.addLast(edge);
    			}
    			else{
    				if (!edge.getExists()) edges.addLast(edge);
    			}
			}
    	}
    	this.redIsUp =!this.redIsUp ; 
    	babble("removeSize: "+ edges.size());
    	babble("redIsUp "+redIsUp);

		if (this.redIsUp) this.getModel().toBack(edges.toArray());
		else  this.getModel().toFront(edges.toArray());
		babble("this.getModel().toString(): "+this.getRoots());
		cells = this.getRoots();
		for (int i=0; i<edges.size();i++){

    			babble("edges:edge red:" +((CCEdgeCell)edges.get(i)).getRed());
    			babble("edges:edge exists: "+ ((CCEdgeCell)edges.get(i)).getExists());
			
    	}
		this.repaint();
		
    }
   
    /** align cells after justify (grid) */
    public void alignCells(){
    	babble("aling");
    	int abw = 2;
    	Object[] cells = this.getSelectionCells();
    	for (int i=0;i<cells.length;i++){
    	
    		if (cells[i] instanceof CCGraphCell) {
				CCGraphCell c = (CCGraphCell)cells[i];
				babble("vorher "+c.getPosX());
				c.setPosX( Math.round((c.getPosX()-abw)/this.getGridSize())*this.getGridSize()+2);
				c.setPosY( Math.round((c.getPosY()-abw)/this.getGridSize())*this.getGridSize()+2);
				babble("nachher "+c.getPosX());
				this.cellMoved(this.getCellView(c));
	    	}
    	}
    	this.setChanged(true);
       	this.clearSelection();
    }
    /**
     * searches the smallst x-Position and the smallest y position and returns them as Point
     * @return the Point with the smalleszt x and y Position
     */
    public Point getSmallestPositionValue(){
    	int x = Integer.MAX_VALUE;
    	int y = Integer.MAX_VALUE;
    	
    	LinkedList<CCGraphCell> graphCells =  this.getGraphCells();
    	for (int i = 0 ; i< graphCells.size(); i++){
    		CCGraphCell cell = (CCGraphCell) graphCells.get(i);
    		if (cell.getPosX()<x) x =cell.getPosX();
    		if (cell.getPosY()<y) y =cell.getPosY();
    	}
    	
    	LinkedList<CCEdgeCell> edges = this.getEdgeCells();
    	for (int i = 0 ; i< edges.size(); i++){
    		CCEdgeCell edge = (CCEdgeCell)edges.get(i);
    		LinkedList<Point> points = edge.getDrawEdgeList();
    		for (int k = 0; k<points.size();k++){
    			Point point = (Point) points.get(k);
    			if (point.x < x) x= point.x;
    			if (point.y < y) y= point.y;
    		}
    	}
    	
    	return new Point(x,y);
    }
    
    /**
     * sets for a Graphcell new bounds, and rearrange SubCells and Edges.
     * 
     * @param cv
     */
    public void cellMoved(CellView cv){
    	if ((cv instanceof MainComponentView)||(cv instanceof BranchView)){
    		
    		Object m = cv.getAttributes().get("bounds");
    		
    		CCGraphCell cell;
    		if (cv instanceof MainComponentView) cell =((MainComponentCell)cv.getCell());
    		else cell = ((BranchCell)cv.getCell());
    		
    		Point neu = new Point(cell.getPosX(),cell.getPosY());
    		Map map = cv.getAttributes();
    		Dimension size = ((Rectangle) m).getSize();

        	GraphConstants.setBounds(map, new Rectangle(neu,size));
        	
    		cv.update();//without cells do not move
    		this.controller.arrangeSubCells(cv);
    		this.controller.arrangeEdges(cell);
    	}
    	

    } 

    /**
     * arranged an edge if the related cell moved..
     * moves the first point (the first angle) and the second (the first horizontal straight one)
     * @param ev the edgeview whose edgecell is to move
     * @param cell the cell thet moved
     */
    
    protected void arrangeEdge(CCEdgeView ev,CCGraphCell cell){
    	CCEdgeCell edge = (CCEdgeCell)ev.getCell();
    	LinkedList<Point> list =edge.getDrawEdgeList();
    	
    	if (edge.getStartCell().equals(cell)){
    		
    		Point pStart = new Point(((PortView)ev.getSource()).getBounds().getLocation().x+3,
                    ((PortView)ev.getSource()).getBounds().getLocation().y+3);
    		
    		Point p=(Point)list.get(2);
    		Point p1 = new Point(pStart.x,p.y);
    		ev.setPoint(1,p1);
    		list.set(0,p1);

    		Point p2 = new Point((pStart.x+p.x)/2,p.y);
    		ev.setPoint(2,p2);
    		list.set(1,p2);
    		edge.setDrawEdgeList(list);
    		edge.setStartCellPoint(pStart);
    		
    	}else{
    		Point pEnd = new Point(((PortView)ev.getTarget()).getBounds().getLocation().x+3,
                    ((PortView)ev.getTarget()).getBounds().getLocation().y+3);
    		
    		Point p=(Point)list.get(list.size()-3);
    		Point p1 = new Point(pEnd.x,p.y);
    		ev.setPoint(list.size(),p1);
    		list.set(list.size()-1,p1);

    		Point p2 = new Point((pEnd.x+p.x)/2,p.y);
    		ev.setPoint(list.size()-1,p2);
    		list.set(list.size()-2,p2);
    		edge.setDrawEdgeList(list);
    		edge.setEndCellPoint(pEnd);
    	}
		
    }
    /**
     * called by update
     * proofs if an edgepoint changed
     * calculates the orthogonal points
     * @param ev
     */
    protected void edgeMoved(CCEdgeView ev){
    	
    	int point =0;
    	boolean movedEdgeFound = false;
    	CCEdgeCell changedME = null;
    	for (int i=0 ; i<this.getRoots().length;i++){
    		if (movedEdgeFound) break;
    		
    		if (this.getRoots()[i] instanceof CCEdgeCell){
    		
    			changedME =(CCEdgeCell)ev.getCell();
	    		
	    		if (changedME.isFirstEdgeMove()){//first update() is before edge is complete
	    			changedME.setFirstEdgeMove(false); 
	    			return;
	        	}
	    		
	    		CCEdgeCell orgME=(CCEdgeCell)this.getRoots()[i];
	    		List list = (List)ev.getAttributes().get("points");// first and last are PortViews!

	    		if (changedME.equals(orgME)){// moved edge found
	    			movedEdgeFound = true;

	    			for (int j=0;j<changedME.getDrawEdgeList().size();j++){//proof if point moved
	    				
	    				if (!((Point)changedME.getDrawEdgeList().get(j)).equals((Point)list.get(j+1))){
	    			    	point = j+1;
	    					break;
	    				}
	    			}

	    			if (point == 0){
		    			//test if arrange
		    	    	Point pStart = orgME.getStartCellPoint();
		    	    	Point pEnd = orgME.getEndCellPoint();
	
		    	    	Point movedStartPort = new Point(((Point)((PortView)list.get(0)).getBounds().getLocation()).x+3,
	    				          ((Point)((PortView)list.get(0)).getBounds().getLocation()).y+3);
		    	    	Point movedEndPort = new Point(((Point)((PortView)list.get(list.size()-1)).getBounds().getLocation()).x+3,
	    				          ((Point)((PortView)list.get(list.size()-1)).getBounds().getLocation()).y+3);
		    	    	
		    	    	//test if first/last point changed
		    	    	if (!pStart.equals(movedStartPort)){ 
		    	    		point = 1;
		    	    		changedME.setStartCellPoint(movedStartPort);
		    	    	}
		    	    	else if (!pEnd.equals(movedEndPort)){
		    	    		point = list.size()-2;
		    	    		changedME.setEndCellPoint(movedEndPort);
		    	    	}
	    			}
	    		}
    		}
    	}
    	
    	if (point == 0) return;// is orthogonal
    	// not orthogonal!
    	List<Point> list = (List<Point>)ev.getAttributes().get("points");// first and last are PortViews!
    	if (point==1){//first Point moved

    		Point pStart = new Point(((PortView)ev.getSource()).getBounds().getLocation().x+3,
                    ((PortView)ev.getSource()).getBounds().getLocation().y+3);

    		Point p=(Point)list.get(1);
    		p.setLocation(pStart.x,p.y);
    		ev.setPoint(1,p);
    		list.set(1,p);

    		Point p3=(Point)list.get(3);//next corner
    		p3.setLocation(p3.x,p.y);
    		ev.setPoint(3,p3);
    		list.set(3,p3);
    		
    		Point p2=(Point)list.get(2);// centerpoint
    		p2.setLocation((p.x+p3.x)/2,p.y);
    		ev.setPoint(2,p2);
    		list.set(2,p2);

    		if (list.size()>5){// otherwise there is no vertical centerpoint
	    		Point p5=(Point)list.get(5);
	    		
	    		Point p4=(Point)list.get(4);// horizontal centerpoint
	    		p4.setLocation((p4.x),(p.y+p5.y)/2);
	    		ev.setPoint(4,p4);
	    		list.set(4,p4);
    		}
    		
    		//test if also lastpoint moved the the whole edge was moved
        	Point pEnd = new Point(((PortView)ev.getTarget()).getBounds().getLocation().x+3,
                    ((PortView)ev.getTarget()).getBounds().getLocation().y+3);

        	if (((Point)list.get(list.size()-2)).x!=pEnd.x) point = list.size()-2; 
    	
    		
    	}//first point moved
    	
    	if (point == list.size()-2){//last Point moved
    	
    		int size = list.size(); 
        	Point pEnd = new Point(((PortView)ev.getTarget()).getBounds().getLocation().x+3,
                    ((PortView)ev.getTarget()).getBounds().getLocation().y+3);

    		Point p=(Point)list.get(size-2);
    		p.setLocation(pEnd.x,p.y);
    		ev.setPoint(size-2,p);
    		list.set(size-2,p);
    
    		Point p3=(Point)list.get(size-4);//next corner
    		p3.setLocation(p3.x,p.y);
    		ev.setPoint(size-4,p3);
    		list.set(size-4,p3);
    		
    		Point p2=(Point)list.get(size-3);// centerpoint
    		p2.setLocation((p.x+p3.x)/2,p.y);
    		ev.setPoint(size-3,p2);
    		list.set(size-3,p2);
    		
    		if (list.size()>5){// otherwise there is no vertical centerpoint
	    		Point p5=(Point)list.get(size-6);
	    	
	    		Point p4=(Point)list.get(size-5);// horizontal centerpoint
	    		p4.setLocation((p4.x),(p.y+p5.y)/2);
	    		ev.setPoint(size-5,p4);
	    		list.set(size-5,p4);
	    	
    		}
    		
    		
    	}//last Point moved
    	
    	//one of the egdePoints moved..
    	
    	if ((point!= 1)&&(point != (list.size()-2))){
    	
	    	int size = list.size(); 
	    	Point prevP = (Point)list.get(point-1);
	    	Point moveP = (Point)list.get(point);
	    	Point nextP = (Point)list.get(point+1);
			
	    	switch (point%4){
				case 0:{//centerpoint vertical
					
					Point prevCornerP = (Point)list.get(point-3);
			    	Point prevCenterP = (Point)list.get(point-2);// this one needs new centerpoint
	
			    	Point nextCenterP = (Point)list.get(point+2);// this one needs new centerpoint
			    	Point nextCornerP = (Point)list.get(point+3);
			    	
					prevP.setLocation(moveP.x,	prevP.y);
					moveP.setLocation(moveP.x,	(prevP.y+nextP.y)/2);
					nextP.setLocation(moveP.x,	nextP.y);
					
					prevCenterP.setLocation((prevCornerP.x+prevP.x)/2,prevCenterP.y);
					nextCenterP.setLocation((prevP.x+nextCornerP.x)/2,nextCenterP.y);
					
					list.set(point-2,prevCenterP);
					list.set(point+2,nextCenterP);

					break;
					}	
				case 1:{//point from vertical to horizontal
					Point ppCornerP   = (Point)list.get(point-4);
					Point prevCenterP = (Point)list.get(point-3);
					Point prevCornerP = (Point)list.get(point-2);
					Point nextCornerP = (Point)list.get(point+2);
					
					prevCenterP.setLocation((ppCornerP.x+moveP.x)/2,prevCenterP.y);
					prevCornerP.setLocation(moveP.x,prevCornerP.y);
					
					prevP.setLocation(moveP.x,(prevCornerP.y+moveP.y)/2);

					nextP.setLocation((moveP.x+nextCornerP.x)/2,moveP.y);
					nextCornerP.setLocation(nextCornerP.x,moveP.y);
	
					list.set(point-4,ppCornerP);
					list.set(point-3,prevCenterP);
					list.set(point-2,prevCornerP);
					list.set(point+2,nextCornerP);
					
	
					if (point<size-4){
						
						Point nextCenterP = (Point)list.get(point+3);
						Point nnCornerP   = (Point)list.get(point+4);

						nextCenterP.setLocation(nextCenterP.x,(moveP.y+nnCornerP.y)/2);
						
						list.set(point+3,nextCenterP);
					}
		
					break;
					}	
				case 2:{// centerpoint horizontal
					

					prevP.setLocation(prevP.x,				moveP.y);
					moveP.setLocation((prevP.x+nextP.x)/2,	moveP.y);
					nextP.setLocation(nextP.x,				moveP.y);
					
					if (point>2){// sixth point is first s.th. the prior horizontal centerpoint has to be calculated
				    	
						Point prevCornerP = (Point)list.get(point-3);
			    		Point prevCenterP = (Point)list.get(point-2);// this one needs new centerpoint
					
						prevCenterP.setLocation(prevCenterP.x,(prevCornerP.y+prevP.y)/2);
						
						list.set(point-2,prevCenterP);
					}
					
					
					if (point<size-3){
				    	
						Point nextCenterP = (Point)list.get(point+2);// this one needs new centerpoint
						Point nextCornerP = (Point)list.get(point+3);
	
						nextCenterP.setLocation(nextCenterP.x,(prevP.y+nextCornerP.y)/2);
						
						list.set(point+2,nextCenterP);
					}
					break;
					}	
				case 3:{// point from horizontal to vertical
					Point prevCornerP = (Point)list.get(point-2);
					Point nextCornerP = (Point)list.get(point+2);
					Point nextCenterP = (Point)list.get(point+3);
					Point nnCornerP   = (Point)list.get(point+4);
				
					prevCornerP.setLocation(prevCornerP.x,moveP.y);
					prevP.setLocation((prevCornerP.x+moveP.x)/2, moveP.y);
					nextP.setLocation(moveP.x,(moveP.y+nextCornerP.y)/2);
					nextCornerP.setLocation(moveP.x,nextCornerP.y);
					nextCenterP.setLocation((moveP.x+nnCornerP.x)/2,nextCenterP.y);
					
					if (point>3){
						Point ppCornerP   = (Point)list.get(point-4);
						Point prevCenterP = (Point)list.get(point-3);
						
						prevCenterP.setLocation(prevCenterP.x,(ppCornerP.y+prevCornerP.y)/2);
						
						list.set(point-3,prevCenterP);
						
					}
					
					list.set(point-2,prevCornerP);
					list.set(point+2,nextCornerP);
					list.set(point+3,nextCenterP);
					list.set(point+4,nnCornerP);
					
					
					break;
				}	
			}//switch    	

	    	list.set(point-1,prevP);
	    	list.set(point,moveP);
	    	list.set(point+1,nextP);

    	}//default  first < point < last
    	
    	for (int i=1; i<list.size()-1;i++){
    		changedME.getDrawEdgeList().set(i-1,list.get(i));
    	}
    	
//    	repaint
	    this.setChanged(true);
    	
    }

	protected Point getInsertPoint() {
	   	return new Point(20+this.getVisibleRect().x,20+this.getVisibleRect().y);
	}

	////////////////////////////////////////////
	// needful methodes for CellLists
	////////////////////////////////////////////
	/**
	 * 
	 * @return all componentCells
	 */
	public LinkedList<ComponentCell> getComponentCells(){
		Object cells[] = this.getRoots();
		LinkedList<ComponentCell> list = new LinkedList<ComponentCell>();
		for (int i=0; i<cells.length;i++)
			if (cells[i] instanceof ComponentCell) list.addLast((ComponentCell)cells[i]);
		return list;
	}
	/**
	 * 
	 * @return all MainComponentCells
	 */
	public LinkedList<MainComponentCell> getMainComponentCells(){
		Object cells[] = this.getRoots();
		LinkedList<MainComponentCell> list = new LinkedList<MainComponentCell>();
		for (int i=0; i<cells.length;i++)
			if (cells[i] instanceof MainComponentCell) list.addLast((MainComponentCell)cells[i]);
		return list;
	}
	/** @return all BranchCells
	 */
	public LinkedList<BranchCell> getBranchCells(){
		Object cells[] = this.getRoots();
		LinkedList<BranchCell> list = new LinkedList<BranchCell>();
		for (int i=0; i<cells.length;i++)
			if (cells[i] instanceof BranchCell) list.addLast((BranchCell)cells[i]);
		return list;
	}
	/**
	 * 
	 * @return all CCEdgeCells
	 */
	public LinkedList<CCEdgeCell> getEdgeCells(){
		Object cells[] = this.getRoots();
		LinkedList<CCEdgeCell> list = new LinkedList<CCEdgeCell>();
		for (int i=0; i<cells.length;i++)
			if (cells[i] instanceof CCEdgeCell) list.addLast((CCEdgeCell)cells[i]);
		return list;
	}
	/**
	 * 
	 * @return all SubComponentCells
	 */
	public LinkedList<SubComponentCell> getSubComponentCells(){
		Object cells[] = this.getRoots();
		LinkedList<SubComponentCell> list = new LinkedList<SubComponentCell>();
		for (int i=0; i<cells.length;i++)
			if (cells[i] instanceof SubComponentCell) list.addLast((SubComponentCell)cells[i]);
		return list;
	}
	/**
	 * 
	 * @return all GraphCells
	 */
	public LinkedList<CCGraphCell> getGraphCells(){
		Object cells[] = this.getRoots();
		LinkedList<CCGraphCell> list = new LinkedList<CCGraphCell>();
		for (int i=0; i<cells.length;i++)
			if (cells[i] instanceof CCGraphCell) list.addLast((CCGraphCell)cells[i]);
		return list;
	}
	/**
	 * 
	 * @return all Cells (as LinkedList)
	 */
	public LinkedList<Object> getCells(){
		Object cells[] = this.getRoots();
		LinkedList<Object> list = new LinkedList<Object>();
		for (int i=0; i<cells.length;i++) list.addLast(cells[i]);
		return list;
	}
	
	/**
	 * returns the GraphCell with the specified nid
	 * @param nid
	 * @return
	 */
	public CCGraphCell getCellByNid(int nid){
		LinkedList<CCGraphCell> graphCells = this.getGraphCells();
		for (int i= 0; i<graphCells.size();i++){
			if ( ((CCGraphCell)graphCells.get(i)).getNodeID()==nid) 
				return ((CCGraphCell)graphCells.get(i));
		}
		return null;
	}
	
	/**
	 * clear red edge list 
	 * sets the red-Varibale false
	 */
	public void clearRedEdgeList(){
		
		LinkedList<CCEdgeCell> list = this.getRedEdgeList();
		for (int i= 0;i<list.size();i++){
			((CCEdgeCell) list.get(i)).setRed(false);
		}
		list.clear();
		
	}
	
	/**
	 * clears red line
	 */
	public void clearRed(){
		this.clearRedEdgeList();
		this.setChanged(true);
	}
	
	/////////////////////////////////////////
	// create views
	/////////////////////////////////////////
	protected VertexView createVertexView(Object v, CellMapper cm) {

		if (v instanceof MainComponentCell)
			return new MainComponentView(v,this,cm);
		else if (v instanceof SubComponentCell)
			return new SubComponentView(v,this,cm);
		else if (v instanceof BranchCell)
			return new BranchView(v,this,cm);
		
    	return super.createVertexView(v, cm);
    }
	
    protected EdgeView createEdgeView(Edge e, CellMapper cm) {
    	
	  	if (e instanceof CCEdgeCell)
	  		return new CCEdgeView(e,this,cm);
	  	
		return new EdgeView(e,this,cm);
	}
    
    /////////////////////////////////////////////////
    // get/set-methodes
    /////////////////////////////////////////////////
    /**
     * Get the MetaInfos for this Graph
     * @return <code>MetaInfos</code>
     */
	public MetaInfos getMetaInfos() {
		return metas;
	}
	
	/**
     * Set the meta informations of the graph.
     * @param metas MetaInfos, the new meta infos of the graph
     */
    public void setMetaInfos(MetaInfos metas) {
    	this.metas = metas;
    }

	public NavGraphController getController() {
		return controller;
	}
	
	public LinkedList<CCEdgeCell> getRedEdgeList() {
		return redEdgeList;
	}

	public void setRedEdgeList(LinkedList<CCEdgeCell> redEdgeList) {
		this.redEdgeList = redEdgeList;
	}
    
	public int getNid_counter() {return nid_counter;}
	private void setNid_counter(int nc){this.nid_counter=nc;}
	
	public int getLid_counter() {return lid_counter;}
	public void setLid_counter(int lid_counter) { this.lid_counter = lid_counter;}
	
	public boolean getChanged() {return changed;}


	/**
	 * sets changedvariable
	 * returns if the changedvariable changed.. is importent for rebuild the menu..
	 * calls notify model!
	 * @param changed
	 * @return
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
		if (!pre){
			this.getNavModel().getCCModel().notify(this, CCModelChangedEvent.SET_PATH, true);
			babble("update");
			this.updateUndo();
		}
		
	}


    /**
     * sets the summary
     * @param url
     */
    public void setSummary(String url){
    	if (!this.getSummary().equals(url)){
    		this.setChanged(true);
    		this.metas.setSummary(url);
    	}
    }
    
    /**
     * returns the Summary
     * @return a {@link String} with the path to the summary of this graph
     */
    public String getSummary(){
    	return this.metas.getSummaryPath();
    	}
	
 

	/*******************************************
	// toXML-Methods
	*******************************************/
	
	////////////////////////////////////////////
	// MetaMethodes
	////////////////////////////////////////////
	
 
	/**
     * provides the XML-Syntax for the meta-Information and thus 
     * for the Meta-XML-file
     */
    public String toXMLMeta(){
    	this.metaXML = "";
    	Document doc  = this.buildMetaDocument();

    	try {
    		this.metaXML = GraphXMLizer.toXMLString(doc);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}

    	return this.metaXML;
    }
	
	/**
	 * @return the contentXML
	 */
	public String getContentXML() {
		return contentXML;
	}

	/**
	 * @return the metaXML
	 */
	public String getMetaXML() {
		return metaXML;
	}

	/**
     *  creates the MetaDocument 
     * @return
     */
    private Document buildMetaDocument(){

    	this.document = builder.newDocument();
    	
    	if (this.document != null) {
    		
    		//rootElement des XMLFiles
    		Element root = document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + XMLConstants.getRootTag(this));
        	
    		//UseMode
    		root.setAttribute("use-mode","checkin");
    		
    		//NameSpace
    		root.setAttribute("xmlns:mumie",XMLConstants.NS_META);
    		 
    		document.appendChild(root);
    		
    		//MetaInfos	
    		this.getXMLMetaInfos(root);

    		root.appendChild(this.getXMLMetaComponents());
    		
    		//contentType
    		Element conType = document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "content_type");
    		conType.setAttribute("type","text");
    		conType.setAttribute("subtype","xml");
    		root.appendChild(conType);

    	}
    	return document;

    }
    

    /**
     * create MetaInfo part of the XML file
     * 
     */
    private void getXMLMetaInfos(Element xmlParent)   {
    	MetaInfos meta = this.getMetaInfos();
    	meta.setDefaults();
    	
    	//status
    	
    	Element status = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "status");
    	status.setAttribute("name",MetaInfos.getStatusAsString(meta.getStatus()));
    	xmlParent.appendChild(status);
    	
    	//name
    	Element name = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "name");
    	Node nameData = this.document.createTextNode(meta.getName());
    	name.appendChild(nameData);
    	xmlParent.appendChild(name);
    	
    	// description
    	Element description = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META  + "description");
    	Node descriptionData = this.document.createTextNode(meta.getDescription());
    	description.appendChild(descriptionData);
    	xmlParent.appendChild(description);
    	
    	//copyright
    	Element copyright = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "copyright");
    	Node copyrightData = this.document.createTextNode(meta.getCopyright());
    	copyright.appendChild(copyrightData);
    	xmlParent.appendChild(copyright);
    	
    	//summary
    	if (meta.getSummaryPath()!=null){
	    	Element summary = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "generic_summary");
			summary.setAttribute("path",String.valueOf(meta.getSummaryPath()));
			xmlParent.appendChild(summary);
    	}
    	
    	//authors
    	Element authors = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "authors");
    	int[] aus = meta.getAuthors();
    	if (aus != null) {
    		for (int i = 0; i < aus.length; i++) {
    			Element author = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "author");
    			author.setAttribute("id",String.valueOf(aus[i]));
    			authors.appendChild(author);
    		}
    	}
    	xmlParent.appendChild(authors);
    	
    	//starttime
    	if (this.isProblemGraph()){
    	
    		this.getNavModel().getCCModel().getController().getModel().setUpdateDateEvent();
    		
    		Element sDate = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "timeframe_start");
    		sDate.setAttribute("value", meta.getFormattedDate(meta.getDateStartCal()));
    		sDate.setAttribute("raw", meta.getRawDate(meta.getDateStartCal()));
    		xmlParent.appendChild(sDate); 

    		Element eDate = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "timeframe_end");
    		eDate.setAttribute("value", meta.getFormattedDate(meta.getDateEndCal()));
    		eDate.setAttribute("raw", meta.getRawDate(meta.getDateEndCal()));
    		xmlParent.appendChild(eDate);
    	}

    	// category
   
    	if (this.isProblemGraph())
    	if (meta.getGraphType() != XMLConstants.UNDEFINED_ID) {
    		Element category = this.document.createElementNS(XMLConstants.NS_META,XMLConstants.PREFIX_META + "category");
    		category.setAttribute("name",MetaInfos.getGraphtypeAsString(meta.getGraphType()));
    		xmlParent.appendChild(category);
    		
    	}
    	

    	if ((this.isSectionGraph())) {    		
    		if((meta.getClassPath() != XMLConstants.PATH_UNDEFINED))
    		{
    			Element elClass = this.document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "class");
    			elClass.setAttribute("path",String.valueOf(meta.getClassPath())+".meta.xml");
    			elClass.setAttribute("name",String.valueOf(meta.getClassName()));
    			xmlParent.appendChild(elClass);
    		}
    			    
    	}
    }
    
    
    
    /**
     * create the components part of the XML file
     */
    private Element getXMLMetaComponents() {
    	
    	Element components = document.createElementNS(XMLConstants.NS_META, XMLConstants.PREFIX_META + "components");
    	
    	LinkedList<MainComponentCell> cellList = this.getMainComponentCells();
   
		for (int i=0;i<cellList.size();i++) {
			MainComponentCell cell = (MainComponentCell)cellList.get(i);
		
			// insert Elements
			Element xmlElem = cell.getMetaXML(document);
			components.appendChild(xmlElem);
			// insert SubElements - not for Problems
			if (!(this.isProblemGraph())) {
			 	
				// create xml tags for sub elements
				LinkedList<SubComponentCell> list = cell.getSubCells();
		
				for (int k=0; k<list.size();k++) {
				    SubComponentCell sub = (SubComponentCell) list.get(k);
					Element subXmlElem = sub.getMetaXML(document);
					components.appendChild(subXmlElem);
				}
			}    				
		}
    	return components;
	}
    
    ///////////////////////////////////////////////
    // ContentXML Methodes
    ///////////////////////////////////////////////
    
    /**
     * provides the XML-Syntax for the content-File
     */
    public String toXMLContent(){
    	this.contentXML = "";
    	Document doc = this.buildContentDocument();

    	try {
    		contentXML = GraphXMLizer.toXMLString(doc);
			} catch (UnsupportedEncodingException e) {
			
				e.printStackTrace();
			} catch (TransformerException e) {
			
				e.printStackTrace();
			}
    	
    	return contentXML;
    }
    
    /**
     * creates the ContentDocument 
     * @return
     */
    private Document buildContentDocument(){

    	
    	this.document = builder.newDocument();
    	
    	if (this.document != null) {

    		// root
    		Element csection = document.createElementNS(
    				XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
    				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + 
    						XMLConstants.getRootTag(this));
    		
    		// namespace
    		csection.setAttribute("xmlns:" + XMLConstants.getPrefixName(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
    				XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())));
    		
    		
    		document.appendChild(csection);
    		
    		//net
    		csection.appendChild(this.getXMLContentNet());
    		
    		//thread
    		csection.appendChild(this.getXMLContentRedNet());
    	}
    	
    	return document;
	}
    
    /**
     * creates the netXMLElement for Content
     * @return
     */
    private Element getXMLContentNet(){
		Element net = this.document.createElementNS(XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + "net");
		
//		 NetNodes
		net.appendChild(this.getXMLContentNodes());
//		 NetArcs
		net.appendChild(this.getXMLContentArcs(false));
		
		return net;
    }

    /**
     * creates the nodesXMLElement for Content
     * @return
     */
    private Element getXMLContentNodes(){
		Element nodes = this.document.createElementNS(XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + "nodes");

		LinkedList<CCGraphCell> objs = this.getGraphCells();
		for(int i = 0; i < objs.size(); i++) {

			if (objs.get(i) instanceof MainComponentCell){
			
				MainComponentCell cell = (MainComponentCell) objs.get(i); 
				Element elem = (Element)cell.getContentXML(this.document, 
						XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),false);
			 	
			
				// position count (top left, top right, bottom right, bottom left)
				int[] posCount = new int[] { 1,1,1,1 };
	
				// create xml tags for sub elements
				LinkedList<SubComponentCell> subElems = cell.getSubCells();
				
				for (int j=0;j<subElems.size();j++){
				    			    
					SubComponentCell subCell = (SubComponentCell)subElems.get(j);
					Element subElem = subCell.getContentXML(this.document,XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())));
	
					// calc position
					int pos = SubComponentConstants.getAlignAsInt(subCell.getCategory());
					String posString = SubComponentConstants.getAlignAsString(subCell.getCategory());
					
					subElem.setAttribute("align", posString); 
					subElem.setAttribute("count", Integer.toString(posCount[pos])); 
					posCount[pos]++;
				    
					elem.appendChild(subElem);
				}
				nodes.appendChild(elem);

			}else if (objs.get(i) instanceof BranchCell){ 
			
				Element elem = (Element)((BranchCell)objs.get(i)).getContentXML(this.document, XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())));
				nodes.appendChild(elem);
			}

    	}
    	return nodes;
    }

    /**
     * creates the arcsXMLElement for Content
     * @return
     */
    private Element getXMLContentArcs(boolean isRed){
		Element arcs = this.document.createElementNS(
				XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(metas.getGraphType())),
				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + "arcs");

		Object[] objs = this.getRoots();
		for(int i = 0; i < objs.length; i++) {
			if(objs[i] instanceof CCEdgeCell)
				if (isRed){
					
					if (((CCEdgeCell)objs[i]).getRed())
						arcs.appendChild(((CCEdgeCell)objs[i]).
								getContentXML(this.document,XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),true));
					
				}else {
					if (((CCEdgeCell)objs[i]).getExists())
						arcs.appendChild(((CCEdgeCell)objs[i]).
								getContentXML(this.document,XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),true));
				}
			}
		
		return arcs;
    }

    /**
     * creates the threadXMLElement for Content
     * @param d
     * @return
     */
    private Element getXMLContentRedNet(){
		Element thread = this.document.createElementNS(
				XMLConstants.getNS_STRUCT(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())),
				XMLConstants.getPrefix(MetaInfos.getGraphTypeForXML(this.metas.getGraphType())) + "thread");
		thread.appendChild(this.getXMLContentArcs(true));
		return thread;
    }

    public CellView getCellView(Object edge){return this.getView().getMapping(edge,true);}
    
    /** gibt Graphtype + name + cells zurueck */
    public String toString(){
    	return MetaInfos.getGraphtypeAsString(this.metas.getGraphType())+
    	":\n   name: "+this.getName()+"\n   Cells: "+this.getCells().toString();}

    /**
     * toggles if arrows are to be drawn
     * edges can draw arrows individual, but that is for all edges in this graph
     * repaints
     */
	public void toggleShowArrows() { 
		this.showArrows=!this.showArrows;
		for (int i=0;i<this.getEdgeCells().size();i++){
			CCEdgeCell edge = (CCEdgeCell)this.getEdgeCells().get(i);
			edge.drawArrows=this.showArrows;
		}
		this.repaint();
	}
	
	
	protected boolean getShowArrows(){
		return this.showArrows;
	}
	
	void babble(String bab){
		if 
		(this.getNavModel().getCCModel().getController().getConfig().getDEBUG_NAVGRAPH() && this.getNavModel().getCCModel().getController().getConfig().getDEBUG()) System.out.println("NavGraph: "+bab);
	}	
	void bab(String bab){
		if 
		(this.getNavModel().getCCModel().getController().getConfig().getDEBUG_NAVGRAPH() && this.getNavModel().getCCModel().getController().getConfig().getDEBUG()) System.out.print("NavGraph: "+bab);
	}

	/** CCModel asks for when an cell is inserted and the inserted Cell was first MainComponendcell.
	 * if this function will return true the metaInfoField defaultCatrgory is set to homework. 
	 * 
	 */
	public boolean getSetStatus() {
		return setStatusDefault;
	}
	/** is need to set MetaInfoPanel-Status-radioButton to default 
	 * 
	 * @param HomeCatDefault true if defaultcategory is to be set
	 */
	public void setSetStatus(boolean setStatus) {
		this.setStatusDefault = setStatus;
	}
	
	/** CCModel asks for when an cell is inserted and the inserted Cell
	 * was first MainComponendcell and it was an Problemcell.
	 * if this function will return true the metaInfoField defaultCatrgory is set to homework. 
	 */
	public boolean getsetHomeCatDefault() {
		return setHomeCatDefault;
	}

	/** is need to set MetaInfoPanel-Category for Problems 
	 * @param HomeCatDefault true if defaultcategory is to be set
	 */
	public void setsetHomeCatDefault(boolean HomeCatDefault) {
		this.setHomeCatDefault= HomeCatDefault;
	}

	public Object[] getLastMarkedCells() {
		return this.lastMarkedCells;
	}

	public void setLastMarkedCell(Object[] cell) {
		this.lastMarkedCells = cell;
	}

	/**
	 * @return the navModel
	 */
	public NavModel getNavModel() {
		return navModel;
	}

	/**
	 * for each button the enable status 
	 * @param button 
	 * @return true if the button is to be enabled
	 */public boolean getButtonEnable(String button){
		
		if (button.equals(CommandConstants.BUTTON_CONNECT)){
			//connect
			if (this.getSelectionCount()==2)
				if ((this.getSelectionCells()[0] instanceof GraphCell)&&
						this.getSelectionCells()[1] instanceof GraphCell) return true;

		} else if (button.equals(CommandConstants.BUTTON_DELETE)){
			// delete selected
			return this.getSelectionCount()>0; 

		} else if (button.equals(CommandConstants.BUTTON_EDGEDIR)){
			// change edgeDirection
			if (this.getSelectionCount()==1)
				return this.getSelectionCell() instanceof CCEdgeCell;
		} else if (button.equals(CommandConstants.BUTTON_EXISTS)){
			if (this.getSelectionCount()==1)
				return this.getSelectionCell() instanceof CCEdgeCell;
		} else if (button.equals(CommandConstants.BUTTON_RED)){
			return this.redLineIsPossible(this.getSelectionCells());

		} else if (button.equals(CommandConstants.BUTTON_SWAP)){
			if (this.getSelectionCount()==2)
				if ((this.getSelectionCells()[0] instanceof GraphCell)&&
						this.getSelectionCells()[1] instanceof GraphCell) return true;
		} else if (button.equals(CommandConstants.BUTTON_ZOOMIN)){
			return (this.getScale()<= 0.9);
		} else if (button.equals(CommandConstants.BUTTON_ZOOMOUT)){
			return (this.getScale()>=0.2);
			
		}else if (button.equals(CommandConstants.BUTTON_SHOWSOURCE)){
			return (this.getCells().size()!=0);
		}else if (button.equals(CommandConstants.BUTTON_UNDO)){
			return (this.undoNumber>0);
		
		}return false;
	}	
	

	public int getUndoNumber() {
		return this.undoNumber;
	}

	public void setUndoNumber(int n) {
		this.undoNumber= n;
	}

	private void updateUndo(){
		this.undoNumber++;
		this.getNavModel().getCCModel().updateUndo(this);
		 
	}

	public String getUndoName() {
		return this.undoName;
	}
	public void setUndoName(String s){
		this.undoName = s;
	
	}
	
}
