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

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.xml.XMLConstants;

/**
 * This is the implementation of the ComponentCell for MainElements. 
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 * @author <a href="mailto:binder@math.tu-berlin.de">Jens Binder</a>
 * @version $Id: MainComponentCell.java,v 1.33 2009/03/19 15:53:25 vrichter Exp $
 */
public class MainComponentCell extends CCGraphCell implements ComponentCell{

	static final long serialVersionUID=0;
	/**
	 * the Doctype, possible:
	 * Section, Element, Problem, SubSection and SubElement as int
	 */
	int docType =-1; 
	
	/**
	 * the local ID
	 */
	private String lid ="";
	
	/**
	 * the category is defined by japs depending on docType
	 */
	private int category; 
	
	/**
	 * the path of this element inside the checkin-folder
	 */
	private String path=ComponentCellConstants.PATH_UNDEFINED;
	
	/**
	 * the label
	 */
	String label=ComponentCellConstants.DEFAULT_LABEL;
	
	/**
	 * list of SubComponentCells which belong to this cell
	 */
	private LinkedList<SubComponentCell> subCells;

	private boolean hasPoints = true;
	private int points = 0; //only if problem!
	public NavGraph graph;
	/**
	 * iff navGraph represents a SectionGraph, it can have a SubGraph
	 */
	private NavGraph subGraph; 
	
	/**
	 * the constructor of this object which sets the most important things 
	 * @param cat - a Category
	 * @param nid - the NodeID
	 * @param lid - a local ID for identifying the Cell
	 */
	public MainComponentCell(NavGraph g, int cat, int nid, String lid){
		this.graph = g;
		this.subCells=new LinkedList<SubComponentCell>();
		category = cat;
		this.setNodeID(nid);
		this.setLid(lid);
		if (MainComponentConstants.isElementCategory(cat)) this.docType = MainComponentConstants.DOCTYPE_ELEMENT;
		else if (MainComponentConstants.isSectionCategory(cat)) this.docType =MainComponentConstants.DOCTYPE_SECTION;
		else this.docType =MainComponentConstants.DOCTYPE_PROBLEM;
	}
	
	/**
	 * this method is responsible to XML-code the data in this Cell for saving the whole graph
	 * @param doc - a DOM-Document which shall produce all the Nodes
	 * @param prefix - "csec: csub:" and so on ...
	 * @param isRed - is this cell on the red line?!?
	 * @return a DOM-Element ready to insert into a DOM-Tree(XML-Document)
	 */
	public Element getContentXML(Document doc,String prefix, boolean isRed) {
	
		Element xmlElem = doc.createElement(
				prefix + MainComponentConstants.getComponentType(this.getDocType()));
		
		xmlElem.setAttribute("lid",this.getLid());
		xmlElem.setAttribute("nid",Integer.toString(this.getNodeID()));
		
		if (!isRed) {// needed for all nodes, but not for red line
		    xmlElem.setAttribute("posx",Integer.toString(this.getPosX()+CommandConstants.ZWOELF));
		    xmlElem.setAttribute("posy",Integer.toString(this.getPosY()+CommandConstants.ZWOELF));
		}
		return xmlElem;
	
	}

    /**
     * Returns the XML representation of an MainComponentCell when saving the graph.
     * @param doc - a DOM-Document responsible for creating the Element
     * @return an {@link org.w3c.dom.Element} representing this cell (meta)
     */	
	public Element getMetaXML(Document doc) {
	    Element xmlElem = doc.createElement(
	    		XMLConstants.PREFIX_META +
	    		MainComponentConstants.getComponentType(this.getDocType()));
	    xmlElem.setAttribute("lid",this.getLid());
	    xmlElem.setAttribute("path",this.getElementPath());
	    
	    //label
	    Element labelElem = doc.createElement(XMLConstants.PREFIX_META + "ref_attribute");
	    labelElem.setAttribute("name", "label");
	    labelElem.setAttribute("value", this.getLabel());
	    xmlElem.appendChild(labelElem);

	    //points for Problems
	    if (this.getDocType()==MainComponentConstants.DOCTYPE_PROBLEM && this.getHasPoints()){
	    	babble("Set Points for Problem");
		    Element probElem = doc.createElement(XMLConstants.PREFIX_META + "ref_attribute");
		    probElem.setAttribute("name", "points");
		    probElem.setAttribute("value", Integer.toString(this.getPoints()));
		    
		    xmlElem.appendChild(probElem);
	    }
	    
	    //category but only if isnt a section
	    if (this.getDocType()!=MainComponentConstants.DOCTYPE_SECTION){
		    Element catElem = doc.createElement(XMLConstants.PREFIX_META + "category");
		    catElem.setAttribute("name",MainComponentConstants.getCategoryAsString(this.getCategory()));
		    xmlElem.appendChild(catElem);
	    }
	    
		return xmlElem;
	}
	

	/**
	 * the String-representation of this Cell
	 */
	public String toString(){
		return MainComponentConstants.shorts[this.getCategory()];
	}
	
	////////////////////////////////////////
	// from componentCell Interface
	////////////////////////////////////////	
	
	//category
	public int getCategory() {
		return category;
	}

    public void setCategory(int cat) {
		this.category= cat;
	}

    //lid
    public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	//label
	public void setLabel(String label) {
		this.label = label;
	}
    public String getLabel() {
		return label;
	}
 	    	
	//docType
    
	public void setDocType(int docType) {
		this.docType = docType;
	}
	public int getDocType() {
		return docType;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * iff navGraph represents a SectionGraph, it can have a SubGraph
	 */
	public NavGraph getSubGraph() {
		return subGraph;
	}

	/**
	 * iff navGraph represents a SectionGraph, it can have a SubGraph
	 * @param subGraph
	 */
	public void setSubGraph(NavGraph subGraph) {
		if (this.navDownPossible()){
			this.subGraph = subGraph;
		}else{
			babble("tried to set a subgraph to a non-section-element!!");
		}
	}

	/**
	 * gives the information if it is possible to navigate down this Cell into another Graph
	 */
	public boolean navDownPossible(){
		return (MainComponentConstants.isSectionCategory(this.category));
	}
	
	/**
	 * returns an String, that Describes this Cell 
	 */ 
	public String getCellDescription(){
		String s = this.toString().concat(" "+this.getLabel());
		if (this.getElementPath().equals(ComponentCellConstants.PATH_UNDEFINED)) s = s.concat(" kein Document zugewiesen");
		else  s = s.concat(" "+this.getElementPath());
		return s;
	}
	
	/**
	 * 
	 * @param index
	 * @param child
	 */
	public void addSubCell(int index, SubComponentCell child) {
		this.getSubCells().add(index,child);
	}
	

	public LinkedList<SubComponentCell> getSubCells() {
		return subCells;
	}

	public void setSubCells(LinkedList<SubComponentCell> subCells) {
		this.subCells = subCells;
	}

	/**
	 * returns the path of this cell
	 * default is ComponentCellConstants.PATH_UNDEFINED
	 */
	public String getElementPath(){
		return this.path;
	}
	
	public void setElementPath(String path){
		this.path = path;
	}
	
	/** 
	 * for Problems .. there are Problems without points..
	 * 
	 * @return
	 */ 
	public boolean getHasPoints() {return hasPoints;}
	/** 
	 * for Problems .. there are Problems without points..
	 */ 
	public void setHasPoints(boolean hasPoints) {this.hasPoints = hasPoints;}
	
	void babble(String bab){
		if (this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_MAINCOMPONENT() && this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG())
			System.out.println("MainComponentCell: "+bab);
	}
}