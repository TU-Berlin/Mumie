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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.jgraph.JGraph;
import com.jgraph.graph.CellMapper;
import com.jgraph.graph.CellViewRenderer;
import com.jgraph.graph.VertexRenderer;
import com.jgraph.graph.VertexView;

import javax.swing.ImageIcon;

import net.mumie.coursecreator.graph.NavGraph;
/**
 * AND- or OR-Element
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: BranchView.java,v 1.5 2009/03/19 15:53:22 vrichter Exp $
 */
public class BranchView extends VertexView {

	
	static final long serialVersionUID=0;
	
    public static BranchRenderer renderer = new BranchRenderer();

    /**
     * Create a new NodeView instance.
     */
    public BranchView(Object cell, JGraph graph, CellMapper cm) {
      super(cell, graph, cm);
      
    }

    /**
     * Returns the intersection of the bounding rectangle and the
     * straight line between the source and the specified point p.
     * The specified point is expected not to intersect the bounds.
     */
    public Point getPerimeterPoint(Point source, Point p) {
      // Compute relative bounds
      Rectangle r = getBounds();
      int x = r.x+5;
      int y = r.y+5;
      int a = 8;
      int b = 8;

      // Get center
      int xCenter = (int) (x + a);
      int yCenter = (int) (y + b);

      // Compute angle
      int dx = p.x - xCenter;
      int dy = p.y - yCenter;
      double t = Math.atan2(dy, dx);

      // Compute Perimeter Point
      int xout = xCenter+(int) (a*Math.cos(t))-1;
      int yout = yCenter+(int) (b*Math.sin(t))-1;

      // Return perimeter point
      return new Point (xout, yout);
    }

    public CellViewRenderer getRenderer() {
      return renderer;
    }

    public static class BranchRenderer extends VertexRenderer {

    	static final long serialVersionUID=0;
		static ImageIcon picAnd;
		static ImageIcon picOr;
		
		public void paint(Graphics g) {
	
		    if(picAnd == null) {
		    	picAnd = new ImageIcon(((NavGraph)graph).getNavModel().getCCModel().getController().getConfig().getICON_PATH()+"and.png");
		    }
		    if(picOr == null) {
		    	picOr =  new ImageIcon(((NavGraph)graph).getNavModel().getCCModel().getController().getConfig().getICON_PATH()+"or.png");
		    }
	
		    BranchCell cell = (BranchCell) this.view.getCell();
	
		    ImageIcon pic = new ImageIcon();
		    if (cell.getCategory()==BranchCellConstants.getCategoryAsInt(BranchCellConstants.ANDSYM)) 
		    	pic = picAnd;
		    if (cell.getCategory()==BranchCellConstants.getCategoryAsInt(BranchCellConstants.ORSYM)) 
		    	pic = picOr;

		    Graphics2D g2 = (Graphics2D) g;
		    Dimension d = getSize();
		    boolean tmp = selected;
	
		    try {
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
		
				g.drawImage(pic.getImage(),4,4,null);
				
		    } finally {
		    	selected = tmp;
		    }
		    if (selected) {
				g2.setStroke(new BasicStroke(2));
				g.setColor(Color.red);
				g.drawOval(5, 5, d.width-9, d.height-9);
		    }
		}
    }

}







