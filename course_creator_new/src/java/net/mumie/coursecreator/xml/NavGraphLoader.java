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

import net.mumie.coursecreator.CCModel;
import net.mumie.coursecreator.graph.MetaInfos;
import net.mumie.coursecreator.graph.NavGraph;
import net.mumie.coursecreator.graph.cells.CCGraphCell;
import net.mumie.coursecreator.graph.cells.MainComponentCell;
import net.mumie.coursecreator.graph.cells.SubComponentCell;


/** the class loads a NavGraph
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 */
public class NavGraphLoader {

	private GraphLoader graphLoader;
	private NavGraph graph;
	private LinkedList<CCGraphCell> graphCells;
	public NavGraphLoader(GraphLoader gl) {
		this.graphLoader = gl;
		this.createGraph();
	
	}
	
	private void createGraph(){
	
		this.graph = new NavGraph(((CCModel)this.graphLoader.getModel()).getNavModel());
		
		this.addCells();
		this.addEdges();
		
		this.graph.setMetaInfos(this.graphLoader.getMetaInfos());
	}

	private void addCells(){
		LinkedList<CellHolder> cells = this.graphLoader.getCells();
		this.graphCells = new LinkedList<CCGraphCell>();
		for (int i = 0 ; i< cells.size(); i++){
			CellHolder c = cells.get(i);
		
			Object vertex = this.graph.createCell(c.getCellType(),c.getPath(), c.getCat(),
					c.getNid(),c.getLid(), c.getParent(), c.getX(), c.getY(), c.getLabel());
			this.graph.insertCell(vertex);
			if (vertex instanceof CCGraphCell) this.graphCells.addLast((CCGraphCell)vertex);
			if (vertex instanceof MainComponentCell){
				this.addSubcells((MainComponentCell)vertex, c.getSubCells());
			
				if (MetaInfos.isProblemGraphType(this.graph.getMetaInfos().getGraphType()))
					((MainComponentCell)vertex).setPoints(c.getPoints());
				
			}
		}
	}

	private void addSubcells(MainComponentCell main, LinkedList<CellHolder> subs){
		LinkedList<SubComponentCell> subComponentList = new LinkedList<SubComponentCell>();
		for (int i = 0; i< subs.size();i++){
			
			CellHolder c = subs.get(i);
			
			Object vertex = this.graph.createCell(c.getCellType(), c.getPath(), c.getCat(),
					c.getNid(), c.getLid(), main, c.getX(), c.getY(), c.getLabel());
			this.graph.insertCell(vertex);
			subComponentList.addLast((SubComponentCell)vertex);
			
		}
		((MainComponentCell)main).setSubCells(subComponentList);
		
	}
	
	
	private void addEdges(){
		LinkedList<EdgeHolder> edges = this.graphLoader.getEdges();
		
		for (int i = 0 ; i< edges.size(); i++){
			EdgeHolder c = edges.get(i);
			this.graph.insertEdge(this.getCellByNid(c.getStartCell()), this.getCellByNid(c.getEndCell()), c.getPoints(), c.isRed(), c.isExists());
		}
	}
	
	/** @return the graph */
	public NavGraph getGraph() {return graph;}

	/**
	 * returns the GraphCell with the specified nid
	 * @param nid
	 * @return
	 */
	public CCGraphCell getCellByNid(int nid){
		LinkedList<CCGraphCell> graphCells = this.getGraphCells();
		for (int i= 0; i<graphCells.size();i++){
			if ( graphCells.get(i).getNodeID()==nid) 
				return graphCells.get(i);
		}
		return null;
	}
	
	private LinkedList<CCGraphCell> getGraphCells(){
		return graphCells;
	}
}
