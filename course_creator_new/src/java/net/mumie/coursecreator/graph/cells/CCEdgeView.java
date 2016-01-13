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
import java.awt.Graphics;
import java.awt.Point;
import java.util.LinkedList;

import com.jgraph.JGraph;
import com.jgraph.graph.CellMapper;
import com.jgraph.graph.CellViewRenderer;
import com.jgraph.graph.EdgeRenderer;
import com.jgraph.graph.EdgeView;

import net.mumie.coursecreator.CommandConstants;
/**
 * The View-Class for the Edges
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: CCEdgeView.java,v 1.19 2009/03/30 12:40:27 vrichter Exp $
 *
 */
public class CCEdgeView extends EdgeView{
	
	
	static final long serialVersionUID=0;
	public static MyEdgeRenderer renderer = new MyEdgeRenderer();
	

	/** Creates a new MyEdgeView instance.	 */
	public CCEdgeView(Object cell, JGraph graph, CellMapper cm) {
		super(cell, graph, cm);
		
	}
	
	/** Returns the edge renderer.     */
	public CellViewRenderer getRenderer() {
		return CCEdgeView.renderer;
    }

	
    public static class MyEdgeRenderer extends EdgeRenderer {

    	static final long serialVersionUID=0;
    	static final int FILLED=1;
    	static final int OPEN=2;
    	static final int NORMAL=3;
    	static final int WHITE_FILLED=4;
    	static final int WHITE_OPEN=5;
    	static final int LIGHT=6;
    	static boolean normalStyle = false;
    
    	static int STYLE=LIGHT;
    	static int WIDTH=CommandConstants.EDGE_ARROW_WIDTH;
    	static int HEIGHT=CommandConstants.EDGE_ARROW_HEIGHT;
    	static int MOVE_FIRST_N_LAST=MainComponentConstants.ELDIM.height/4;
    	static final int MIN_PIXEL_PAINT =WIDTH;
    	
    	public void paint(Graphics g) {
    		
    		super.paint(g);
    		
    		CCEdgeCell edge = (CCEdgeCell) this.view.getCell();
		    int size = edge.getDrawEdgeListSize()+2;
			
			int[] xPoints = new int[size];
			int[] yPoints = new int[size];
			
			int[] xUpPoints = new int[size];
			int[] yUpPoints = new int[size];
			int[] xDwPoints = new int[size];
			int[] yDwPoints = new int[size];
			
			//start/endPoints
			xPoints[0]=edge.getStartCellPoint().x;
			yPoints[0]=edge.getStartCellPoint().y;
			
			xPoints[size-1]=edge.getEndCellPoint().x;
			yPoints[size-1]=edge.getEndCellPoint().y;
			
			LinkedList<Point> list = edge.getDrawEdgeList();
			
			for (int i=1;i<size-1;i++){// well we need these points early
				Point p = (Point)list.get(i-1);
				xPoints[i] = p.x;
				yPoints[i] = p.y;
			}
			
			//firstpoints
			if (yPoints[0] <yPoints[1]){
				xUpPoints[0]=edge.getStartCellPoint().x+1;
				yUpPoints[0]=edge.getStartCellPoint().y;
				xDwPoints[0]=edge.getStartCellPoint().x-1;
				yDwPoints[0]=edge.getStartCellPoint().y;
			} else{
				xUpPoints[0]=edge.getStartCellPoint().x-1;
				yUpPoints[0]=edge.getStartCellPoint().y;
				xDwPoints[0]=edge.getStartCellPoint().x+1;
				yDwPoints[0]=edge.getStartCellPoint().y;
			}
			
			//linePoints
			for (int i=1;i<size-1;i++){
				Point p = (Point)list.get(i-1);
				if (i%4==1){
					if (yPoints[i-1]>yPoints[i]){// kante kommt von unten
						if (xPoints[i]>xPoints[i+1]){// nach links
							xUpPoints[i] = p.x-1;
							yUpPoints[i] = p.y+1;
							xDwPoints[i] = p.x+1;
							yDwPoints[i] = p.y-1;
						}else {// von unten nach rechts
							xUpPoints[i] = p.x-1;
							yUpPoints[i] = p.y-1;
							xDwPoints[i] = p.x+1;
							yDwPoints[i] = p.y+1;
						}
					}else{ // kante von oben 
						if ( xPoints[i]>xPoints[i+1]){// nach links
							xUpPoints[i] = p.x+1;
							yUpPoints[i] = p.y+1;
							xDwPoints[i] = p.x-1;
							yDwPoints[i] = p.y-1;
						}else {
							xUpPoints[i] = p.x+1;
							yUpPoints[i] = p.y-1;
							xDwPoints[i] = p.x-1;
							yDwPoints[i] = p.y+1;
						}
					}
				}
				 else if (i%4==3){
					if (xPoints[i-1]>xPoints[i]){//von links
						if (yPoints[i]>yPoints[i+1]){// nach oben
							
							xUpPoints[i] = p.x-1;
							yUpPoints[i] = p.y+1;
							xDwPoints[i] = p.x+1;
							yDwPoints[i] = p.y-1;
						
						}else {
							xUpPoints[i] = p.x+1;
							yUpPoints[i] = p.y+1;
							xDwPoints[i] = p.x-1;
							yDwPoints[i] = p.y-1;
						}
					}else {
						if ( yPoints[i]<=yPoints[i+1]){
							xUpPoints[i] = p.x+1;
							yUpPoints[i] = p.y-1;
							xDwPoints[i] = p.x-1;
							yDwPoints[i] = p.y+1;
						
						}else{
							xUpPoints[i] = p.x-1;
							yUpPoints[i] = p.y-1;
							xDwPoints[i] = p.x+1;
							yDwPoints[i] = p.y+1;
						}
					}

				}else if (i%4==2){//waagerecht
					xUpPoints[i] = xPoints[i];
					yUpPoints[i] = yUpPoints[i-1];
					xDwPoints[i] = xPoints[i];
					yDwPoints[i] = yDwPoints[i-1];
			
				}else if (i%4==0){//senkrecht
					xUpPoints[i] = xUpPoints[i-1];
					yUpPoints[i] = yPoints[i];
					xDwPoints[i] = xDwPoints[i-1];
					yDwPoints[i] = yPoints[i];
			
				}

			}

			//last points
			xUpPoints[size-1]=xUpPoints[size-2];
			yUpPoints[size-1]=yPoints[size-1];
			xDwPoints[size-1]=xDwPoints[size-2];
			yDwPoints[size-1]=yPoints[size-1];
			
			if (edge.getExists() && edge.getRed()){// doubleline
				g.setColor(Color.red); // red
				g.drawPolyline(xUpPoints, yUpPoints, size);
				g.drawPolyline(xDwPoints, yDwPoints, size);
				g.setColor(Color.black);
				g.drawPolyline(xPoints, yPoints, size);
				
			}else if (edge.getExists()){//black line
				g.setColor(Color.black);	// black
				g.drawPolyline(xUpPoints, yUpPoints, size);
				g.drawPolyline(xPoints, yPoints, size);
			
			}else if (edge.getRed()){//red line
				g.setColor(Color.red); // red
				g.drawPolyline(xDwPoints, yDwPoints, size);
				g.drawPolyline(xPoints, yPoints, size);
			}
			
			
			// arrows
			if (edge.drawArrows){//Style normal
				if (CommandConstants.EDGE_ARROW_DRAW_VERTICAL)
					this.paintVertical(g,edge,xPoints,yPoints,size);
				this.paintHorizontal(g,edge,xPoints,yPoints,size,list);
			}
    	}
    	
    	/**
    	 * paints all horizontal arrows if possible
    	 * @param g
    	 * @param edge
    	 * @param xPoints
    	 * @param yPoints
    	 * @param size
    	 * @param list
    	 */
    	private void paintHorizontal(Graphics g,CCEdgeCell edge, int[] xPoints, int[] yPoints,int size,LinkedList list){
			for (int i=0;i<size/4;i++){
				Point p1 = (Point)list.get(i*4+1);
				Point p2 = (Point)list.get(i*4+2);
				
				int[] x = new int[3];
				int[] y = new int[3];
	
				if (Math.abs(p1.x - p2.x)>MIN_PIXEL_PAINT ){
					y[0]= p1.y-CCEdgeView.MyEdgeRenderer.HEIGHT;
					y[1]= p1.y;
					y[2]= p1.y+CCEdgeView.MyEdgeRenderer.HEIGHT;
	
	
					if (((p1.x > p2.x)&&(edge.getReverse()))||(!(p1.x > p2.x)&&(!edge.getReverse()))){
						x[0]= p1.x-CCEdgeView.MyEdgeRenderer.WIDTH;
						x[1]= p1.x+CCEdgeView.MyEdgeRenderer.WIDTH;
						x[2]= p1.x-CCEdgeView.MyEdgeRenderer.WIDTH;
						
					}else{
						x[0]= p1.x+CCEdgeView.MyEdgeRenderer.WIDTH;
						x[1]= p1.x-CCEdgeView.MyEdgeRenderer.WIDTH;
						x[2]= p1.x+CCEdgeView.MyEdgeRenderer.WIDTH;
					}
				}
					else if (p1.x==p2.x){
						
//						boolean down = //FIXME Pfeile richtigrum zeichnen!
						x[0]= p1.x-CCEdgeView.MyEdgeRenderer.HEIGHT;
						x[1]= p1.x;
						x[2]= p1.x+CCEdgeView.MyEdgeRenderer.HEIGHT;
						
						if (((p1.y > p2.y)&&(edge.getReverse()))||(!(p1.y > p2.y)&&(!edge.getReverse()))){
							
							y[0]= p1.y-CCEdgeView.MyEdgeRenderer.WIDTH;
							y[1]= p1.y+CCEdgeView.MyEdgeRenderer.WIDTH;
							y[2]= p1.y-CCEdgeView.MyEdgeRenderer.WIDTH;
							
						}else{
							y[0]= p1.y+CCEdgeView.MyEdgeRenderer.WIDTH;
							y[1]= p1.y-CCEdgeView.MyEdgeRenderer.WIDTH;
							y[2]= p1.y+CCEdgeView.MyEdgeRenderer.WIDTH;		
						}
						
					}
				
				this.paintarrow(g,x,y);
			}
    	}
    	
    	private void paintVertical(Graphics g,CCEdgeCell edge, int[] xPoints, int[] yPoints,int size){
			
			int[] x = new int[3];
			int[] y = new int[3];
			
			//paint last and first
			// first arrow
			Point p1;
			Point p2;
			if (CommandConstants.PAINT_FIRSTNLAST){
				if (yPoints[0]<yPoints[1]){//kante geht von oben nach unten
					p1 = new Point(xPoints[0],yPoints[0]+MOVE_FIRST_N_LAST);
					p2 = new Point(xPoints[1],yPoints[1]+MOVE_FIRST_N_LAST);
				}else{
					p1 = new Point(xPoints[0],yPoints[0]-MOVE_FIRST_N_LAST);
					p2 = new Point(xPoints[1],yPoints[1]-MOVE_FIRST_N_LAST);
				}
				
	
				x[0]= p1.x-CCEdgeView.MyEdgeRenderer.HEIGHT;
				x[1]= p1.x;
				x[2]= p1.x+CCEdgeView.MyEdgeRenderer.HEIGHT;
				if (Math.abs(p1.y - p2.y)>MIN_PIXEL_PAINT  +2*MOVE_FIRST_N_LAST){
					if (((p1.y > p2.y)&&(edge.getReverse()))||(!(p1.y > p2.y)&&(!edge.getReverse()))){
						y[0]= (p1.y+p2.y)/2-CCEdgeView.MyEdgeRenderer.WIDTH;
						y[1]= (p1.y+p2.y)/2+CCEdgeView.MyEdgeRenderer.WIDTH;
						y[2]= (p1.y+p2.y)/2-CCEdgeView.MyEdgeRenderer.WIDTH;
						
					}else{
						y[0]= (p1.y+p2.y)/2+CCEdgeView.MyEdgeRenderer.WIDTH;
						y[1]= (p1.y+p2.y)/2-CCEdgeView.MyEdgeRenderer.WIDTH;
						y[2]= (p1.y+p2.y)/2+CCEdgeView.MyEdgeRenderer.WIDTH;
					}
		
					this.paintarrow(g,x,y);
				}
	
				// last one
				p1 = new Point(xPoints[xPoints.length-2],yPoints[xPoints.length-2]);
				p2 = new Point(xPoints[xPoints.length-1],yPoints[xPoints.length-1]);
	
				if (yPoints[yPoints.length-2]<yPoints[yPoints.length-1]){//kante geht von oben nach unten
					p1 = new Point(xPoints[xPoints.length-2],yPoints[xPoints.length-2]-MOVE_FIRST_N_LAST);
					p2 = new Point(xPoints[xPoints.length-1],yPoints[xPoints.length-1]-MOVE_FIRST_N_LAST);
				}else{
					p1 = new Point(xPoints[xPoints.length-2],yPoints[xPoints.length-2]+MOVE_FIRST_N_LAST);
					p2 = new Point(xPoints[xPoints.length-1],yPoints[xPoints.length-1]+MOVE_FIRST_N_LAST);
				}
				
				if (Math.abs(p1.y - p2.y)>MIN_PIXEL_PAINT +2*MOVE_FIRST_N_LAST){
					x[0]= p1.x-CCEdgeView.MyEdgeRenderer.HEIGHT;
					x[1]= p1.x;
					x[2]= p1.x+CCEdgeView.MyEdgeRenderer.HEIGHT;
					
					if (((p1.y > p2.y)&&(edge.getReverse()))||(!(p1.y > p2.y)&&(!edge.getReverse()))){
						y[0]= (p1.y+p2.y)/2-CCEdgeView.MyEdgeRenderer.WIDTH;
						y[1]= (p1.y+p2.y)/2+CCEdgeView.MyEdgeRenderer.WIDTH;
						y[2]= (p1.y+p2.y)/2-CCEdgeView.MyEdgeRenderer.WIDTH;
						
					}else{
						y[0]= (p1.y+p2.y)/2+CCEdgeView.MyEdgeRenderer.WIDTH;
						y[1]= (p1.y+p2.y)/2-CCEdgeView.MyEdgeRenderer.WIDTH;
						y[2]= (p1.y+p2.y)/2+CCEdgeView.MyEdgeRenderer.WIDTH;
					}
		
					this.paintarrow(g,x,y);
				}
				
			}
			// paint arrows on line
			for (int i=1;i<size/4;i++){
				
				p1 = new Point(xPoints[i*4],yPoints[i*4]);
				p2 = new Point(xPoints[i*4+1],yPoints[i*4+1]);
				
				if (Math.abs(p1.y - p2.y)>MIN_PIXEL_PAINT ){
				
					x = new int[3];
					y = new int[3];
	
					x[0]= p1.x-CCEdgeView.MyEdgeRenderer.HEIGHT;
					x[1]= p1.x;
					x[2]= p1.x+CCEdgeView.MyEdgeRenderer.HEIGHT;
	
					if (((p1.y > p2.y)&&(edge.getReverse()))||(!(p1.y > p2.y)&&(!edge.getReverse()))){
						y[0]= p1.y-CCEdgeView.MyEdgeRenderer.WIDTH;
						y[1]= p1.y+CCEdgeView.MyEdgeRenderer.WIDTH;
						y[2]= p1.y-CCEdgeView.MyEdgeRenderer.WIDTH;
						
					}else{
						y[0]= p1.y+CCEdgeView.MyEdgeRenderer.WIDTH;
						y[1]= p1.y-CCEdgeView.MyEdgeRenderer.WIDTH;
						y[2]= p1.y+CCEdgeView.MyEdgeRenderer.WIDTH;
					}
					this.paintarrow(g,x,y);
				}
    		}
		}
		

		private void paintarrow(Graphics g,int[] x, int[] y){
		
			switch (CCEdgeView.MyEdgeRenderer.STYLE){
		
				case WHITE_OPEN:{
					Color color= g.getColor();
					g.setColor(Color.WHITE);
					
					g.fillPolygon(x,y,x.length);
					g.setColor(color);
					g.drawPolyline(x,y,x.length);
				
				}
				
				case WHITE_FILLED:{
					Color color= g.getColor();
					g.setColor(Color.WHITE);
					
					g.fillPolygon(x,y,x.length);
					g.setColor(color);
					g.drawPolyline(x,y,x.length);
					g.drawLine(x[0],y[0],x[2],y[2]);
				}
				case OPEN:{
					g.drawPolyline(x,y,x.length);
				}
				case NORMAL:{
					g.fillPolygon(x,y,x.length);
				}
				case LIGHT:{
					Color color= g.getColor();
					if (color.equals(Color.red)) 
						g.setColor(new Color(255,200,200));
					else 
						g.setColor(Color.gray);
					
					g.fillPolygon(x,y,x.length);
					g.setColor(color);

					g.drawPolyline(x,y,x.length);
					g.drawLine(x[0],y[0],x[2],y[2]);
				}
			
			}//switch
		}  

		void babble(String bab){
		if (((CCEdgeCell)this.view.getCell()).graph.getNavModel().getCCModel().getController().getConfig().getDEBUG() &&
				((CCEdgeCell)this.view.getCell()).graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_CCEDGE())
			System.out.println("CCEdgeView: "+bab);
		}
    }

}// MyEdgeView
