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

package net.mumie.coursecreator.flatgraph;


import net.mumie.coursecreator.graph.cells.ComponentCellConstants;
import net.mumie.coursecreator.graph.cells.MainComponentConstants;
import net.mumie.coursecreator.xml.XMLConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * this class describes the cell of an FlatGraph  
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 *
 */
public class FlatCell {//TODO implement this
	static final long serialVersionUID=0;
	/**
	 * the Doctype, possible:
	 * Section, Element, Problem, SubSection and SubElement as int
	 */
	int docType =-1; 
	/**
	 * the category is defined by japs depending on docType
	 */
	private int category; 
	/**
	 * the local ID
	 */
	private String lid ="";
	
	/**
	 * the path of this element inside the checkin-folder
	 */
	private String path=ComponentCellConstants.PATH_UNDEFINED;
	
	/**
	 * the label
	 */
	String label=ComponentCellConstants.DEFAULT_LABEL;
	
	private int NodeID;
	

	private boolean hasPoints = true;
	private int points = 0; //only if problem!
	
	
	/**
	 * creates a new FlatCell with category and NodeID
	 * @param cat
	 * @param nid
	 */
	public FlatCell(int cat,int nid){
		
		this.category = cat;
		this.setNodeID(nid);
		
	}
	
	/**
     * Return the ContentXML representation of an BranchCell when saving the graph
     * @return {@link org.w3c.dom.Element} representing this cell
     */
    public Element getContentXML(Document doc, String prefix) {
		
    	Element xmlElem = doc.createElement(prefix + (this.getCategory()));
    	
    	xmlElem.setAttribute("nid",String.valueOf(this.getNodeID()));
    	
		return xmlElem;
    }


    /**
     * Returns the XML representation of an MainComponentCell when saving the graph.
     * @param doc - a DOM-Document responsible for creating the Element
     * @return an {@link org.w3c.dom.Element} representing this cell (meta)
     */	
	public Element getMetaXML(Document doc) {
	    Element xmlElem = doc.createElement(
	    		XMLConstants.PREFIX_META );
	    
	    xmlElem.setAttribute("path",this.getElementPath());
	    
	    //label
	    Element labelElem = doc.createElement(XMLConstants.PREFIX_META + "ref_attribute");
	    labelElem.setAttribute("name", "label");
	    labelElem.setAttribute("value", this.getLabel());
	    xmlElem.appendChild(labelElem);

	    //points for Problems
	    if (this.getDocType()==MainComponentConstants.DOCTYPE_PROBLEM && this.getHasPoints()){
	    	
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
	 * returns the path of this cell
	 * default is ComponentCellConstants.PATH_UNDEFINED
	 */
	public String getElementPath(){
		return this.path;
	}
	
	public void setElementPath(String path){
		this.path = path;
	}
    public int getNodeID() {return NodeID; }
    public void setNodeID(int nid) {NodeID = nid;}
    
	public int getCategory() {return category;}
	public void setCategory(int category) {this.category = category;}

	//docType
    public void setDocType(int docType) {this.docType = docType;}
	public int getDocType() {return docType;}
	
	public int getPoints() {return points;}
	public void setPoints(int points) {this.points = points;}
    
	//lid
    public String getLid() {return lid;}
	public void setLid(String lid) {this.lid = lid;}

	//label
	public void setLabel(String label) {this.label = label;}
    public String getLabel() {return label;}
    
	/**for Problems .. there are Problems without points..*/ 
	public boolean getHasPoints() {return hasPoints;}
	
	/**
	 * "" because the view Label uses this
	 */
	public String toString() {
		return ""; 
	}
}
