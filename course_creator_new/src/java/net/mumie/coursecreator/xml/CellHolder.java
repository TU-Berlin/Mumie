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

package net.mumie.coursecreator.xml;

import java.util.LinkedList;

/** the class holds a cell for loading a Graph
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 */

public class CellHolder {

	private int cellType =-1;
	private String path =null;
	private int cat=-1;
	private int nid=-1;
	private String lid = null;
	private Object parent = null;
	private int x = -1;
	private int y = -1;
	private String label = null;
	
	private boolean hasPoints;
	private int points;
	
	private LinkedList<CellHolder> subCells;
	
	CellHolder(int cellType, String path, int cat, int nid, String lid, Object parent, int x, int y,String label){
		this.cat 		= cat;
		this.cellType 	= cellType;
		this.label 	= label;
		this.lid 		= lid;
		this.nid 		= nid;
		this.parent 	= parent;
		this.path		= path;
		this.x			= x;
		this.y 		= y;
	}

	/** @return the cat */
	public int getCat() {return cat;}

	/** @return the cellType */
	public int getCellType() {return cellType;}

	/** @return the label */
	public String getLabel() {return label;}

	/** @return the lid */
	public String getLid() {return lid;}

	/** @return the nid */
	public int getNid() {return nid;}

	/** @return the parent */
	public Object getParent() {return parent;}

	/** @return the path */
	public String getPath() {return path;}

	/** @return the x */
	public int getX() {return x;}

	/** @return the y */
	public int getY() {return y;}

	/** @return the hasPoints */
	public boolean isHasPoints() {return hasPoints;}

	/** @param hasPoints the hasPoints to set */
	public void setHasPoints(boolean hasPoints) {this.hasPoints = hasPoints;}

	/** @return the points */
	public int getPoints() {return points;}

	/** @param points the points to set */
	public void setPoints(int points) {this.points = points;}

	/** @return the subCells */
	public LinkedList<CellHolder> getSubCells() {return subCells;}

	/** @param subCells the subCells to set */
	public void setSubCells(LinkedList<CellHolder> subCells) {this.subCells = subCells;}


	public String toString() {
		return "Type "+this.cellType+" Cat "+this.getCat()+" Label "+this.getLabel();
	}
	

	
}