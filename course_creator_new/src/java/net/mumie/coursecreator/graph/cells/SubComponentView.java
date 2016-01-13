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

import com.jgraph.JGraph;
import com.jgraph.graph.*;

import java.awt.*;


/**
 * The View of an SubElement.
 */
public class SubComponentView extends VertexView {

	static final long serialVersionUID=0;
    public static SubComponentRenderer renderer = new SubComponentRenderer();

    /**
     * Creates a new SubElementView instance.
     */
    public SubComponentView(Object cell, JGraph graph, CellMapper cm) {
      super(cell, graph, cm);
    }

    /**
     * Returns the cell renderer
     */
    public CellViewRenderer getRenderer() {
      return SubComponentView.renderer;
    }

    public static class SubComponentRenderer extends VertexRenderer {
    	
    	static final long serialVersionUID=0;
		public void paint(Graphics g) {
			
		    Graphics2D g2 = (Graphics2D) g;
		    Dimension d = getSize();
		    int b = borderWidth;
		    
		    SubComponentCell cell = (SubComponentCell) this.view.getCell();
		    
		    Color col = Color.LIGHT_GRAY;

		    if(cell.getElementPath() != ComponentCellConstants.PATH_UNDEFINED) {
				col = SubComponentConstants.getSubColor(cell.getCategory());
		    }
		    
		    g.setColor(col);
		    g.fillRect(b-1, b-1, d.width-b, d.height-b);
	
		    setOpaque(false);
		   
		    super.paint(g);
	
		    if (selected) {
				g2.setStroke(new BasicStroke(2));
				g.setColor(Color.red);
				g.drawRect(1,1, d.width-2, d.height-2);
		    }
		    
		    //text
		    g.setColor(Color.BLACK);
		    g.setFont(
		    		new Font(g.getFont().getName(),
		    				 g.getFont().getStyle(),
		    				 10));
		    FontMetrics metrics=g.getFontMetrics();
	        g.drawString(
	        		 cell.getString(),
	        		 (d.width-metrics.stringWidth(cell.getString()))/2,
	        		 (d.height-metrics.getHeight())/2+metrics.getAscent());
	        g2.setStroke(new BasicStroke(1));
			if (cell.getLabel().equals(ComponentCellConstants.DEFAULT_LABEL)){
				Color c = Color.gray;
				g.setColor(c);
				g.drawRect(1,1, d.width-3, d.height-3);
				g.drawRect(2,2, d.width-5, d.height-5);
			}
		}
    }

    void babble(String bab){
		if (((SubComponentCell)this.cell).graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_SUBCOMPONENT() &&
				((SubComponentCell)this.cell).graph.getNavModel().getCCModel().getController().getConfig().getDEBUG())
			System.out.println("SubComponentView: "+bab);
	}
}


