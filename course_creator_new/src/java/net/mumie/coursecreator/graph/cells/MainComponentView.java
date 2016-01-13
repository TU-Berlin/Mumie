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

/** draw MainComponent */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.jgraph.JGraph;
import com.jgraph.graph.CellMapper;
import com.jgraph.graph.CellViewRenderer;
import com.jgraph.graph.VertexRenderer;
import com.jgraph.graph.VertexView;


/**
 * The View of an ElementCell.
 */
public class MainComponentView extends VertexView {
	
	static final long serialVersionUID=0;
	public static MainComponentRenderer renderer = new MainComponentRenderer();
	
	/**
	 * Creates a new ElementView instance.
	 */
	public MainComponentView(Object cell, JGraph graph, CellMapper cm) {
		super(cell, graph, cm);
	}
	
	/**
	 * Returns the cell renderer.
	 */
	public CellViewRenderer getRenderer() {
		return MainComponentView.renderer;
	}
	
	/**
	 * the renderer of this view
	 *
	 */
	public static class MainComponentRenderer extends VertexRenderer {
		
		
		static final long serialVersionUID=0;
		public void paint(Graphics g) {
			
			Graphics2D g2 = (Graphics2D) g;
			
			Dimension d = getSize();
			int b = borderWidth;
			boolean tmp = selected;
			
			MainComponentCell cell = (MainComponentCell) this.view.getCell();
			
			// Determine background color of cell (depends on type)
			// if there's nothing assigned, background is gray
			// if Section, background is white
			Color col = Color.LIGHT_GRAY;
			if(!(cell.getElementPath().equals(ComponentCellConstants.PATH_UNDEFINED))){ 
				col = MainComponentConstants.getMainColor(cell.getCategory());
			}
			 
		
			g.setColor(col);
			g.fillRect(b-1, b-1, d.width-b, d.height-b);
			
			setOpaque(false);
			if (tmp) setBorder(null);
			selected = false;
			super.paint(g);
			
			
			if(tmp) {
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.red);
				g2.drawRect(1,1, d.width-2, d.height-2);
			}
			g2.setStroke(new BasicStroke(1));
			if (cell.getLabel().equals(ComponentCellConstants.DEFAULT_LABEL)){
				Color c = Color.gray;
				g.setColor(c);
				g.drawRect(1,1, d.width-3, d.height-3);
				g.drawRect(2,2, d.width-5, d.height-5);
			}
			if (cell.getDocType()== MainComponentConstants.DOCTYPE_PROBLEM)
			if (cell.getPoints()==0 && cell.getHasPoints()){
				Color c = Color.gray;
				g.setColor(c);
				g.drawRect(1,1, d.width-3, d.height-3);
				g.drawRect(2,2, d.width-5, d.height-5);
			} 
		}
	}
	
   void babble(String bab) {
		if (((MainComponentCell)this.cell).graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_MAINCOMPONENT()
				&& ((MainComponentCell)this.cell).graph.getNavModel().getCCModel().getController().getConfig().getDEBUG())
			System.out.println("MainComponentView: " + bab);
	}
}


