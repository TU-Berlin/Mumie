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

package net.mumie.coursecreator.graph.cells;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Map;
import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.graph.NavGraph;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jgraph.graph.DefaultEdge;
import com.jgraph.graph.GraphConstants;



/**
 * Representation of an EdgeCell
 *
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: CCEdgeCell.java,v 1.22 2009/03/30 12:40:28 vrichter Exp $
 */
public class CCEdgeCell extends DefaultEdge{
	
	static final long serialVersionUID=0;

	private CCGraphCell startCell;
	private CCGraphCell endCell;
	private Point startCellPoint; // x,y Koord of startVertex (left-top point)
	private Point endCellPoint;
	protected NavGraph graph;
	
	/**
	 * List with points (without start/end)
	 */
	private LinkedList<Point> drawEdgeList; // List with points (without start/end)
	private boolean firstEdgeMove;
	private boolean exists;// well.. only red edges don't exists..
	private boolean red;// an edge can be on redLine, but not in the net..
	private boolean reverse; // rueckrichtung..
	public boolean drawArrows;
	static int WIDTH=CommandConstants.EDGE_ARROW_WIDTH;
	static int HEIGHT=CommandConstants.EDGE_ARROW_HEIGHT;
	/**
     * Creates a new EdgeCell instance using the given Object
     * as userObject.
     */
    public CCEdgeCell(NavGraph g) {
    	super();
    	this.startCell = null;
		this.endCell = null;
		this.drawEdgeList=new LinkedList<Point>();
		this.setStartCellPoint(new Point(0,0));
		this.setEndCellPoint(new Point(0,0));
		this.firstEdgeMove = true;
		this.exists = true;
		this.red = true;
		this.reverse = false;
		this.drawArrows = true;
		this.graph = g;
		Map attrMap = this.getAttributes();
		GraphConstants.setLineWidth(attrMap, Math.max(WIDTH,HEIGHT));
		GraphConstants.setLineColor(attrMap, new Color(255,255,255,0));
    }

    
    /**
     * edges are equal iff they have the same start- and endcell and the same points
     */
    public boolean equals(Object obj) {
    	if (obj == null) return false;
    	if (!(obj instanceof CCEdgeCell)) return false;

    	CCEdgeCell obp = (CCEdgeCell) obj; 
    	if ((this.getStartCell().equals(obp.getStartCell()))&&
    			(this.getEndCell().equals(obp.getEndCell()))){
    		
    		LinkedList<Point> listm = this.getDrawEdgeList();
    		LinkedList<Point> listo = obp.getDrawEdgeList();
    		for (int i= 0; i<Math.min(listm.size(),listo.size());i++){
    			if (!listm.get(i).equals(listo.get(i))) return false; 
    		}
    		
    	}else return false;
    		
		return true;
	}

    /**
	 * add orthogonal point to EdgeList
	 */
	public void addPoint(Point p){
		this.drawEdgeList.addLast(this.getOrthPoint(p));
	}

	/**
	 * sets the koordinates of the Startvertex
	 * @param p
	 */
	public void setStartCellPoint(Point p){
		this.startCellPoint = p;
	}
	
	public Point getStartCellPoint(){
		return this.startCellPoint;
	}
	
	
	public void toggleDirection(){
		this.setReverse(!this.getReverse());
		babble("dir "+this.getReverse());
	}
	
	/**
	 * returns the orthogonal Point concerning last attached point p
	 * @param p
	 * @return
	 */
	public Point getOrthPoint(Point p){
		Point orthP = new Point();
		
		if (this.drawEdgeList.size()==0) {
			orthP.setLocation(this.getStartCellPoint().x,p.y);
			
		}else{
		
			Point prev= (Point) this.drawEdgeList.getLast();
			
			if ((this.drawEdgeList.size() % 2) ==1)
				orthP.setLocation(p.getLocation().x,prev.getLocation().y);
			else 
				orthP.setLocation(prev.getLocation().x,p.getLocation().y);
		}
		return orthP;
	}
	
	/**
	 * attaches last cornerpoint if necessary
	 * computes centerpoints
	 */
	public void concludeAdding(){

		LinkedList<Point> newList = new LinkedList<Point>();
		
		if (this.drawEdgeList.size()==0){// no points
			return;
		}
		
		if ((this.drawEdgeList.size() % 2) ==1){
			
			Point p = new Point((Point)this.drawEdgeList.getLast());
			this.drawEdgeList.addLast(p);
			
		}
		//add centerpoints
		for (int i = 0;i<(this.drawEdgeList.size()-1);i++){

			newList.addLast(this.drawEdgeList.get(i));
			newList.addLast(new Point(
					((this.drawEdgeList.get(i)).x+((Point)this.drawEdgeList.get(i+1)).x)/2,
			        ((this.drawEdgeList.get(i)).y+((Point)this.drawEdgeList.get(i+1)).y)/2));

		}
		newList.addLast(this.drawEdgeList.getLast());
		this.drawEdgeList.clear();
		this.drawEdgeList = newList;

	}
	
	/**
	 * removes last point from actual pointlist
	 */
	public void removeLastPoint(){
		if (this.drawEdgeList.size()>0) this.drawEdgeList.removeLast();
	}

	
	public CCGraphCell getEndCell() {
		return endCell;
	}

	public void setEndCell(CCGraphCell endCell) {
		this.endCell = endCell;
	}

	public CCGraphCell getStartCell() {
		return startCell;
	}

	public void setStartCell(CCGraphCell startCell) {
		this.startCell = startCell;
	}

	/**
	 * get pointlist (without start/end)
	 */
	public LinkedList<Point> getDrawEdgeList() {
		return drawEdgeList;
	}
	
	public void setDrawEdgeList(LinkedList<Point> list){
		this.drawEdgeList = list;
	}
	
	/**
	 * 
	 * @return number of points in this.drawEdgeList
	 */
	public int getDrawEdgeListSize(){
		return this.drawEdgeList.size();
	}

	
	public Point getEndCellPoint() {
		return endCellPoint;
	}

	public void setEndCellPoint(Point endCellPoint) {
		this.endCellPoint = endCellPoint;
	}

	public boolean isFirstEdgeMove() {
		return firstEdgeMove;
	}

	public void setFirstEdgeMove(boolean firstEdgeMove) {
		this.firstEdgeMove = firstEdgeMove;
	}

	public boolean getRed(){
		return this.red;
	}
	public String toString() {
		return "";
	}
	
	public boolean getExists() {
		return exists;
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}

	public boolean setRed(boolean r) {
		boolean status = false;
		if (this.red!= r) status = true;
		this.red = r;
		return status;
	}

	/**
	 * red Line is possible for only a cell
	 * if both vertices are MainComponentCell
	 * @return true if both vertices are MainComponentCell<br>
	 * false otherwise
	 */
	public boolean isRedPossible(){
		return (this.getStartCell() instanceof MainComponentCell &&
				this.getEndCell() instanceof MainComponentCell);
	}
    /**
     * XML representation of an EdgeCell in an Graph.
     * respects reverse in following way:<br>
     * if reverse is true, then from is endcell and to is startcell<br>
     * if reverse is false, then from is startcell, to is endcell
     * @return {@link org.w3c.dom.Element} representing this cell
     */
    public Element getContentXML(Document doc, String prefix, boolean withNids) {

    	Element xmlElem = doc.createElement(prefix+"arc");
    	
    	//startCell
    	String sNid = String.valueOf(this.startCell.getNodeID());
    	
    	//endCell    	
    	String eNid = String.valueOf(this.endCell.getNodeID());

    	
    	if (!this.getReverse()){
    		if (withNids){
    			xmlElem.setAttribute("from",sNid);
    			xmlElem.setAttribute("to",eNid);
    		}
   		
			//points
	    	for (int i=0; i<this.getDrawEdgeListSize();i++ ,i++){
	    		Element xmlPoint = doc.createElement( prefix+"point");
	    		xmlPoint.setAttribute("posx",Integer.toString(((Point)this.getDrawEdgeList().get(i)).x));
	    		xmlPoint.setAttribute("posy",Integer.toString(((Point)this.getDrawEdgeList().get(i)).y));
	    		xmlElem.appendChild(xmlPoint);
	    	}
    	}
    	else {// arc is reverse
    		if (withNids){
	    		xmlElem.setAttribute("from",eNid);
	    		xmlElem.setAttribute("to",sNid);
    		}
   		
			//points
	    	for (int i=this.getDrawEdgeListSize()-1; i>=0;i--,i--){
	    		Element xmlPoint = doc.createElement(prefix+"point");
	    		xmlPoint.setAttribute("posx",Integer.toString(((Point)this.getDrawEdgeList().get(i)).x));
	    		xmlPoint.setAttribute("posy",Integer.toString(((Point)this.getDrawEdgeList().get(i)).y));
	    		xmlElem.appendChild(xmlPoint);
	    	}
    	}
    	return xmlElem;
    }

	public boolean getReverse() {
		return this.reverse;
	}

	public void setReverse(boolean rev) {
		this.reverse = rev;
	}
	
	void babble(String bab){
		if (this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_CCEDGE()&&this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG())
			System.out.println("CCEdgeCell: "+bab);
	}
}