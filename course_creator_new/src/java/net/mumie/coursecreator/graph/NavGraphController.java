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

import net.mumie.coursecreator.CCController;
import net.mumie.coursecreator.CourseCreator;
import net.mumie.coursecreator.graph.cells.*;
import net.mumie.coursecreator.CommandConstants;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Point;

import javax.swing.JOptionPane;


import com.jgraph.event.GraphModelEvent;
import com.jgraph.event.GraphModelListener;
import com.jgraph.event.GraphSelectionEvent;
import com.jgraph.event.GraphSelectionListener;
import com.jgraph.graph.DefaultPort;
import com.jgraph.graph.VertexView;

/**
 * <p>
 * Implements the Interface for the GraphSelectionListener and the GraphModelListener
 * and it functions as Observer for the GraphView.
 * </p>
 * @author <a href="mailto:doehrint@in.tum.de">Thomas D&ouml;hring</a>
 * @author <a href="mailto:sinha@math.tu-berlin.de">Uwe Sinha</a>
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @version $Id: NavGraphController.java,v 1.28 2009/03/19 15:53:15 vrichter Exp $
 */

public class NavGraphController implements GraphSelectionListener, GraphModelListener,Observer {

    private NavGraph graph;    

    /**
     * Creates a new SectionGraphController instance.
     */
    public NavGraphController (NavGraph sg) {
	this.graph = sg;
    }

    /**
     * returns status of an selected cell<br>
     * <br>
     * the status is tripartite. <br>
     * status[0] is label<br>
     * status[1] is path<br>
     * status[2] is true if marked cell is an ComponentCell ATTENTION: its the String ''true''!<br>
     * <br>
     * if more or less one cell is marked, status[0] and status[1] are empty, status[2] is ''false''  
     * @return status[0] is label<br>
     * status[1] is path<br>
     * status[2] is true if marked cell is an ComponentCell ATTENTION: its the String ''true''!
     */
    public Object[] getStatus(){

        Object[] status = new Object[3];
        status[0] = "";
        status[1] = "";
        status[2] = "false";// is cell with label and path??
        
        int nr = graph.getSelectionCount();
        if (nr!=1) return status;

        Object[] cells = this.graph.getSelectionCells();

        if (cells[0] instanceof ComponentCell){
        	status[2]="true";
        	ComponentCell cell = (ComponentCell)cells[0];
            status[0] = cell.getLabel();
             if(cell.getElementPath() ==ComponentCellConstants.PATH_UNDEFINED)
                            status[1] = "";
                        else
                            status[1] = cell.getElementPath();

        }else if (cells[0] instanceof CCEdgeCell){
                    CCEdgeCell cell = (CCEdgeCell)cells[0];
                    
                    if (cell.getExists()){
                    if (cell.getRed()) status[1] = "Linie rot im Netzwerk";
                    else status[1] = "Linie schwarz";
            }else status[1] = "Linie rot";

        }else if (cells[0] instanceof BranchCell){
                    BranchCell cell = (BranchCell)cells[0];
                    status[1] = BranchCellConstants.getCategoryAsString(cell.getCategory());
            }
        return status;
    }

    
    
    /**
     * Implementation of GraphSelectionListener Interface.
     */
    public void valueChanged(GraphSelectionEvent se) {
		// count selected Cells
    	
		int nr = graph.getSelectionCount();
		babble("anz sel: "+nr);
		if (nr == 1) {
			
		    Object o = graph.getSelectionCell();
		    
		    if ((graph.insertEdgeMode==true)){
			    if (((o instanceof MainComponentCell))|| ((!this.graph.eCell.getRed())&&(o instanceof CCGraphCell))) {// draw edges
			    
			    	graph.insertEdge((CCGraphCell)o,new Point(((Rectangle)(graph.getView().getMapping((CCGraphCell)graph.getSelectionCell(),false).getAttributes()).get("bounds")).getLocation().x+CommandConstants.ZWOELF,
							((Rectangle)(graph.getView().getMapping((CCGraphCell)graph.getSelectionCell(),false).getAttributes()).get("bounds")).getLocation().y+CommandConstants.ZWOELF));
			    } else{

			    } 
		    }else{//no edge drawing so keep cell for draw edge in order
		    	if (o instanceof CCGraphCell){
		    		Object[] obj= this.graph.getLastMarkedCells();
		    		obj[0] = o;
					this.graph.setLastMarkedCell(obj);
		    	}
		    	
		    }
	
		}else if (nr==2 ){
			Object[] obj= this.graph.getLastMarkedCells();
			if (obj[0]!=null){
				Object[] selected = graph.getSelectionCells();
				if (obj[0].equals(selected[0]))
					obj[1]=selected[1];
				else if (obj[0].equals(selected[1]))
					obj[1]=selected[0];
				else {obj[0]=null;obj[1]=null;}
				this.graph.setLastMarkedCell(obj);
			}
	    }
		else {Object[] obj= this.graph.getLastMarkedCells();obj[0]=null;obj[1]=null;	
	    this.graph.setLastMarkedCell(obj);}
		
		Object[] status = this.getStatus();
		
		CourseCreator.printStatus(String.valueOf(status[0]),String.valueOf(status[1]), String.valueOf(status[2]));

    }

    /**
     * Implementation of GraphModelListener
     */
    public void graphChanged(GraphModelEvent me) {    
	   babble("graph changed!!!!");
    	arrange();
    }

    /**
     * Call arrangeSubSections on each SectionCell.
     */
    public void arrange() {
    	
    	LinkedList<MainComponentCell> cells = graph.getMainComponentCells();
    	for (int i = 0; i < cells.size(); i++) {
			arrangeSubCells(cells.get(i));
		}
    }

    /**
     * Observer of GraphView.
     */
    public void update(Observable o, Object arg) {
  	 	 babble("graph UPDATE");
		graph.getView().deleteObserver(this);
		
		if (arg instanceof GraphModelEvent.GraphViewChange) {
		    GraphModelEvent.GraphViewChange changes = (GraphModelEvent.GraphViewChange) arg;
		    Object[] cells = changes.getChanged();
		    Object elem = null;
		    
		    // first move all edges
		    for (int i = 0; i < cells.length; i++) {
		    	if (cells[i] instanceof CCEdgeView) {
					graph.edgeMoved((CCEdgeView) cells[i]);
		    	}
						
		    }
		    
		    // check if a Section has changed
		    for (int i = 0; i < cells.length; i++) {
		    	
		    	if (cells[i] instanceof MainComponentView) {
		    		
				    elem = ((MainComponentView)cells[i]).getCell();
				    
				    arrangeSubCells(elem);
				   this.arrangeCell(elem);
				    // arrange Edges
				    arrangeEdges(elem);
				    
				    graph.setChanged(true);
				} else if (cells[i] instanceof SubComponentView) {
				    SubComponentCell sub = (SubComponentCell)((SubComponentView)cells[i]).getCell();
				    
				    elem = sub.getParent();
				    arrangeSubCells(elem);
				    graph.setChanged(true);
				} 
				else if (cells[i] instanceof BranchView) {
				    elem = ((VertexView)cells[i]).getCell();
				    arrangeEdges(elem);
				    this.arrangeCell(elem);
				    graph.setChanged(true);
				}
		    }  
		    
		}
		//proof if some elements have negative x,y-Components..
		Point p = this.graph.getSmallestPositionValue();
		if (p.x <0 || p.y < 0){
			int moveX = 0;
			int moveY = 0;
			if (p.x<0) moveX = -p.x;
			if (p.y<0) moveY = -p.y;
			
			this.graph.moveWholeGraph(moveX,moveY);
		}
		graph.getView().addObserver(this);
    }
    
      
    /**
     * sets new position to cell..
     */
    
    private void arrangeCell(Object o){
//    	babble("arrange cell");
    	CCGraphCell cell;
    	if (o instanceof MainComponentCell)
    		cell = (MainComponentCell)o;
    	else if (o instanceof BranchCell)
    		cell = (BranchCell)o;
    	else if (o instanceof MainComponentView) 
    		cell = (MainComponentCell)((MainComponentView)o).getCell();
    	else return;
    	
 	    Point p = ((Rectangle)(graph.getView().getMapping(cell,false).getAttributes()).get("bounds")).getLocation();
 	    cell.setPosX(p.x);
 	    cell.setPosY(p.y);
    }

    /**
     * Arrange edges.
     *
     * @param elem the SectionCell or node
     */
    public void arrangeEdges(Object elem) {
    	if (elem instanceof CCGraphCell) {
	    	
			 Enumeration children = ((CCGraphCell)elem).children();
			 
			 while(children.hasMoreElements()){// well there is only one port..
			     Object child = children.nextElement();
			     Iterator edges = ((DefaultPort)child).edges();
		
			     while(edges.hasNext()){
			    	 CCEdgeCell e = (CCEdgeCell)edges.next(); 
			    	 graph.arrangeEdge((CCEdgeView)graph.getView().getMapping(e,false),(CCGraphCell)elem);
			     }
		     }
    	}
     }

    /**
     * Arrange the SubComponentCells around the MainComponentCell
     *
     * @param  main the MainComponentCell
     */
    protected void arrangeSubCells(Object main) {
    	
   
    	MainComponentCell mainCell;
    	if (main instanceof MainComponentCell)
    		mainCell = (MainComponentCell)main;
    	else if (main instanceof MainComponentView) 
    		mainCell = (MainComponentCell)((MainComponentView)main).getCell();
    	else return;
    	
    	if (mainCell.getSubCells().size() == 0)return; 
		
		// get position of main section
	    Map atts = graph.getView().getMapping(mainCell,false).getAttributes();
	    Rectangle rec = (Rectangle) atts.get("bounds");
	    Point p = rec.getLocation();

	    int tl = 0;
	    int tr = 0;
	    int bl = 0;
	    int br = 0;

	    LinkedList<SubComponentCell> subs = mainCell.getSubCells();

	    for (int i=0;i<subs.size();i++) {
	    	
			Point q = new Point(p);
			SubComponentCell sub = (SubComponentCell) subs.get(i);
			
	 		switch (SubComponentConstants.getAlignAsInt(sub.getCategory())) {
		 		
	 		    case 0 : q.translate(-20-tl*10,-10-tl*10); tl++; break;
	 		    case 1 : q.translate(20+tr*10,-10-tr*10); tr++; break;
	 		    case 2 : q.translate(-20-bl*10,20+bl*10); bl++; break;
	 		    case 3 : q.translate(20+br*10,20+br*10); br++; break;
	 		}
		
	       	Dimension size = SubComponentConstants.SUBELDIM;
			((VertexView)graph.getView().getMapping(sub,false)).setBounds(new Rectangle(q, size));
		    }
    }
    
    /** dialog asks iff edge shall be deleted 
     * @return true force delete false otherwise	 
     */
    public boolean dialogdeleteEdge(CCEdgeCell e){
    	Object[] options = {"Ja", "Nein"};
    	int n = JOptionPane.showOptionDialog(CCController.frame, 
    					     " Diese Kante loeschen?",
    					     " Diese Kante loeschen?", 
    					     JOptionPane.YES_NO_OPTION,
    					     JOptionPane.INFORMATION_MESSAGE,
    					     null,
    					     options,
    					     options[1]);
    	if (n == JOptionPane.YES_OPTION) {
    		return true;
    	}return false;
    		
    }
    
    /**
     * Message is displayed if the red line is incorrect.
     */
    public void dialogRedNotCorrect(String error) {
    	int errorTyp = JOptionPane.ERROR_MESSAGE;
    		if (error.equals(NavGraph.generateCheckRedLineError(null,0))) 
    			errorTyp= JOptionPane.INFORMATION_MESSAGE;
    	JOptionPane.showMessageDialog(CCController.frame,error, "Rote Linie getestet",errorTyp);
    }

    /**
     * Message is displayed if the grrapf contains no Drains or sources is incorrect.
     */
    public void dialogGraphContainsNoSourcesOrDrains(boolean isSource) {
    	String typeName;
    	if (isSource) typeName = "Quellen";
    	else typeName = "Senken";
    	
    	JOptionPane.showMessageDialog
    		(CCController.frame,"Graph enth\u00e4lt keine "+
    				typeName, "auf "+typeName + " getestet",JOptionPane.ERROR_MESSAGE);
    }

    /**
     * test for circles Message
     * Message is displayed if the graph contains no circles.
     */
    protected void dialogGraphContainsNoCircles() {
    	String error = "Graph ist kreisfrei";
    	
    	JOptionPane.showMessageDialog(CCController.frame,error,
    			"auf Kreise getestet",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * test for circles Message
     * Message is displayed if the graph contains no edges.
     */
    protected void dialogGraphContainsNoEdges() {
    	String error = "Graph enth\u00e4lt keine Kanten";
    	
    	JOptionPane.showMessageDialog(CCController.frame,error,
    			"auf Kreise getestet",JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * test for circles Message
     * Message is displayed if the graph contains no vertices.
     */
    protected void dialogGraphContainsNoVertices() {
    	String error = "Graph enth\u00e4lt keine Knoten";
    	
    	JOptionPane.showMessageDialog(CCController.frame,error,
    			"auf Kreise getestet",JOptionPane.INFORMATION_MESSAGE);
    }
    
	void babble(String bab){
		if (this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_NAVGRAPH_CONTROLLER() && this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG()) System.out.println("NavGraphController: "+bab);
	}
}