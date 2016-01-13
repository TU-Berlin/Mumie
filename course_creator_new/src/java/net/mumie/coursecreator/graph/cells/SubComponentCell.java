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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.xml.XMLConstants;

/**
 * Responsible for managing SubComponentCells like remarks, motivations ...
 * 
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: SubComponentCell.java,v 1.21 2009/03/30 12:40:23 vrichter Exp $
 *
 */
public class SubComponentCell implements ComponentCell{
	/**
	 * the Doctype, possible:
	 * Section, Element, Problem, SubSection and SubElement as int
	 */
	int docType =-1; 
	
	/**
	 * the local ID
	 */
	String lid ="";
	
	/**
	 * the category is defined by japs depending on docType
	 */
	private int category = -1; 
	
	private String path=ComponentCellConstants.PATH_UNDEFINED;
	
	/**
	 * the label
	 */
	String label=ComponentCellConstants.DEFAULT_LABEL;

	/**
	 * the ComponentCell this subComponent belongs to
	 */
	private MainComponentCell parent;

	
	/**
	 * iff navGraph represents a SectionGraph, it can have a SubGraph
	 */
	private NavGraph subGraph; 
	public NavGraph graph;
	/**
	 * the constructor of this object which sets the most important things 
	 * @param cat - a Category
	 * @param parent - the corresponding {@link MainComponentCell}
	 * @param lid - a local ID for identifying the Cell
	 */
	public SubComponentCell(NavGraph g,int cat, MainComponentCell parent,String lid){
		this.graph = g;
		this.category = cat; 
		this.parent = parent;
		this.setLid(lid);
		if (SubComponentConstants.isSubSectionCategory(cat)) this.docType =SubComponentConstants.DOCTYPE_SUBSECTION;
		else this.docType =SubComponentConstants.DOCTYPE_SUBELEMENT;
	}

	
	/**
     * Return the ContentXML representation of an BranchCell when saving the graph
     * @return {@link org.w3c.dom.Element} representing this cell
     */
    public Element getContentXML(Document doc, String prefix) {
    	Element xmlElem = doc.createElement( 
	    		prefix + SubComponentConstants.getComponentType(getDocType()));
	    
    	xmlElem.setAttribute("lid",lid);
       	
		return xmlElem;
    }

    /**
     * the Meta-Infos for this Cell coded in XML
     */        
	public Element getMetaXML(Document doc) {

	    Element xmlElem = doc.createElement(
	    		XMLConstants.PREFIX_META + 
	    		SubComponentConstants.getComponentType(getDocType()));
	    
	    xmlElem.setAttribute("lid",this.getLid());
	    xmlElem.setAttribute("path",this.getElementPath());

	    //label
	    Element labelElem = doc.createElement(XMLConstants.PREFIX_META + "ref_attribute");
	    labelElem.setAttribute("name", "label");
	    labelElem.setAttribute("value", this.getLabel());
	    xmlElem.appendChild(labelElem);

	    //category
	    Element catElem = doc.createElement(XMLConstants.PREFIX_META + "category");
	    catElem.setAttribute("name",SubComponentConstants.getCategoryAsString(this.getCategory()));
	    xmlElem.appendChild(catElem);
	    
	    return xmlElem;
	}
	
	/** 
	 * generates the string inherent this cell
	 * @return
	 */
	public String getString(){
		  int category = this.category;
		  if ( category >= 0 )
		    if ( category < SubComponentConstants.shorts.length )
			return SubComponentConstants.shorts[category];
		  return "unknown-category";
		}

	public String toString(){		 
//		return this.category+this.label;
		return "";
	}


	///////////////////////////////////////////////
	// ComponentCellMethodes
	///////////////////////////////////////////////
	public String getElementPath(){return this.path;}
	public void setElementPath(String path)
	{
		this.path = path;
	}
	
	public int getCategory() {return this.category;}
	public void setCategory(int category) {this.category = category;}

	public int getDocType() {return docType;}
	public void setDocType(int docType) {this.docType = docType;}

	public String getLabel() {return label;}
	public void setLabel(String label) {this.label = label;}

	public String getLid() {return lid;}
	public void setLid(String lid) {this.lid = lid;}


	public MainComponentCell getParent() {
		return parent;
	}


	public void setParent(MainComponentCell parent) {
		this.parent = parent;
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
		this.subGraph = subGraph;
	}

	/**
	 * gives the information if it is possible to navigate down this Cell into another Graph
	 */
	public boolean navDownPossible(){
		return (SubComponentConstants.isSubSectionCategory(this.category));
	}
	
	/**
	 * returns an String, that Describes this Cell 
	 */ 
	public String getCellDescription(){
		String s = this.toString().concat(" "+this.getLabel());
		if (this.getElementPath().equals(ComponentCellConstants.PATH_UNDEFINED))
			s = s.concat(" kein Document zugewiesen");
		else  
			s = s.concat(" "+this.getElementPath());
		return s;
	}
}