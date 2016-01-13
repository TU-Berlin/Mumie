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

import net.mumie.coursecreator.CommandConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

/**
 * Class for Branchcelldata
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: BranchCell.java,v 1.10 2009/03/30 12:40:30 vrichter Exp $
 *
 */
public class BranchCell extends CCGraphCell{
	
	static final long serialVersionUID=0;
	/**
	 * the category is
	 * 0 for ''And'' or
	 * 1 for ''Or''
	 */
	private int category;
	
	/**
	 * creates a new BranchCell with category and NodeID
	 * @param cat
	 * @param nid
	 */
	public BranchCell(int cat,int nid){
		
		this.category = cat;
		this.setNodeID(nid);
		
	}
	
	/**
     * Return the ContentXML representation of an BranchCell when saving the graph
     * @return {@link org.w3c.dom.Element} representing this cell
     */
    public Element getContentXML(Document doc, String prefix) {
		
    	Element xmlElem = doc.createElement(prefix 
    	                  + BranchCellConstants.getCategoryAsString(this.getCategory()));
    	
    	xmlElem.setAttribute("nid",String.valueOf(this.getNodeID()));
    	
    	xmlElem.setAttribute("posx",Integer.toString(this.getPosX()+CommandConstants.ZWOELF));
    	xmlElem.setAttribute("posy",Integer.toString(this.getPosY()+CommandConstants.ZWOELF));
    	
		return xmlElem;
    }

    
    
	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * "" because the view Label uses this
	 */
	public String toString() {
		return ""; 
	}
	
}