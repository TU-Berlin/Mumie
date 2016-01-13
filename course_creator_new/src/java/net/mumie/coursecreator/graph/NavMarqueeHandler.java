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

package net.mumie.coursecreator.graph;

import net.mumie.coursecreator.CommandConstants;
import net.mumie.coursecreator.graph.cells.CCEdgeCell;
import net.mumie.coursecreator.graph.cells.CCGraphCell;
import net.mumie.coursecreator.graph.cells.ComponentCell;
import net.mumie.coursecreator.graph.cells.MainComponentCell;
import net.mumie.coursecreator.graph.cells.SubComponentCell;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.jgraph.graph.BasicMarqueeHandler;


/**
 * ElementMarqueeHandler
 * A Marquee handler acts as a "high-level" mouse handler
 *
 * @author <a href="mailto:doehrint@in.tum.de">Thomas D&ouml;hring</a>
 * @author <a href="mailto:sinha@math.tu-berlin.de">Uwe Sinha</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: NavMarqueeHandler.java,v 1.28 2009/03/19 15:53:20 vrichter Exp $
 */

public class NavMarqueeHandler extends BasicMarqueeHandler {

    // Holds the current Point
    protected Point current;
    
    // holds the graph
    protected NavGraph graph;
    
    /**
     * Create a  new ElementMarqueeHandler instance.
     */
    public NavMarqueeHandler() {
    	super();
    	
    }

    /**
     * Create a  new ElementMarqueeHandler instance.
     * @param g the ElementGraph the hanlder belongs to
     */
    public NavMarqueeHandler(NavGraph g) {
    	super();
    	
    	this.graph = g;
    }

    // Gain control for PopUpMenu
    public boolean isForceMarqueeEvent(MouseEvent e) {
    	// display popup menu?
    	if (SwingUtilities.isRightMouseButton(e)) return true;
		return super.isForceMarqueeEvent(e);
    }
    
    // Display PopupMenu
    public void mousePressed(final MouseEvent e) {
    	// if right mouse button
    	if (SwingUtilities.isRightMouseButton(e)) {
    		if (graph.insertEdgeMode==true){
    			graph.eCell.removeLastPoint();
    			this.graph.repaint(0);
    		}else{
    			
        		// Scale from Screen to Model
	    		Point l = graph.fromScreen(e.getPoint());
	    		// Find cell in Model Coordinates
	    		Object cell = graph.getFirstCellForLocation(l.x,l.y);
	    		// Create PopUpMenu for the Cell
	    		JPopupMenu menu = createPopupMenu(e.getPoint(),cell);
	    		// Display PopupMenu
	    		menu.show(graph, e.getX(), e.getY());
    		}
    	} else {
    		if (graph.insertEdgeMode==true){
    			
    			Point p = graph.fromScreen(e.getPoint());

//    			p.move((int)(p.x*graph.getScale()),(int)(p.y*graph.getScale()));
    			
    			graph.eCell.addPoint(p);
    			this.graph.repaint(0);
    			
    		} else super.mousePressed(e);
    	}
    }

    /**
     * creates and returnes a KontextMenu at the specified Point for the selected Cells
     * @param pt the Position of the KontextMenu 
     * @param cell if this is null, rightMouseClick was at a specified Cell<br> 
     * if this is null, the mouseEvent was 
     * at an empty location and the menu is for all selected Cells 
     * @return the menu
     */
    public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
    	
    	// set Cell as selected cell. 
    	// Seien mehrere Komponenten markiert, nach rechtsklick auf EINE Komponente 
    	// ist nur diese markiert und daher gilt auch das Menu nur f\u00fcr diese Komponente 
		if (cell!=null) {graph.clearSelection(); graph.setSelectionCell(cell);}
		    
    	JPopupMenu menu = new JPopupMenu();
    
    	if (graph.isSelectionEmpty()) return menu;
			
		// delete for every cell
		menu.add(new AbstractAction("L\u00f6schen") {
			static final long serialVersionUID=0;
			public void actionPerformed(ActionEvent e) {
				graph.delete();
			}
		});
		
		if (graph.getSelectionCount() != 1){
			if (graph.getSelectionCount()==2){
				if ((graph.getSelectionCells()[0] instanceof CCGraphCell)
				  &&(graph.getSelectionCells()[1] instanceof CCGraphCell))
					
				menu.add(new AbstractAction("Verbinden") {
					static final long serialVersionUID=0;
					Object start = graph.getSelectionCells()[0];
					Object end = graph.getSelectionCells()[1];
						
					public void actionPerformed(ActionEvent e) {
						graph.connect(start,end);
					}
				});
			
			if (((graph.getSelectionCells()[0] instanceof CCGraphCell)
					  &&(graph.getSelectionCells()[1] instanceof CCGraphCell))
					  ||((graph.getSelectionCells()[0] instanceof SubComponentCell)
							  &&(graph.getSelectionCells()[1] instanceof SubComponentCell)))
				
				menu.add(new AbstractAction("Vertauschen") {
					static final long serialVersionUID=0;
					
					public void actionPerformed(ActionEvent e) {
						graph.swapGraphCells();
					}
				});

			}	
				
			if (this.graph.redLineIsPossible(graph.getSelectionCells())){
				menu.add(new AbstractAction("erzeuge roten Faden") {
					static final long serialVersionUID=0;
					public void actionPerformed(ActionEvent e) {
						if (!graph.generateRedLine(graph.getSelectionCells())) 
							NavController.redLineErrorDialog();
					}
				});
			}
			return menu;
			
		} 

		if (this.graph.getSelectionCell() instanceof CCEdgeCell) {
			return this.createEdgeMenu(menu,(CCEdgeCell) this.graph.getSelectionCell());
			
		}
		
		if (this.graph.getSelectionCell() instanceof MainComponentCell) {
			if (graph.isProblemGraph()){
				menu.add(new AbstractAction("Punkte vergeben") {
					static final long serialVersionUID=0;
					public void actionPerformed(ActionEvent e) {
						graph.getNavModel().getCCModel().getController().dialogAssignPoints((MainComponentCell)graph.getSelectionCell()); 
					}	
				});	
			}
		}
		

		if (this.graph.getSelectionCell() instanceof CCGraphCell&&(graph.getGraphCells().size()>1)){//edges only iff at least 2 cells..
			
			menu.add(new AbstractAction("schwarze Kante ziehen") {
				static final long serialVersionUID=0;
				public void actionPerformed(ActionEvent e) {
					graph.prepareInsertEdge((CCGraphCell)graph.getSelectionCell(),
							new Point(((Rectangle)(graph.getView().getMapping((CCGraphCell)graph.getSelectionCell(),false).getAttributes()).get("bounds")).getLocation().x+CommandConstants.ZWOELF,
									((Rectangle)(graph.getView().getMapping((CCGraphCell)graph.getSelectionCell(),false).getAttributes()).get("bounds")).getLocation().y+CommandConstants.ZWOELF),
									false,true);
				}	
			});
			
			if (graph.getSelectionCell() instanceof MainComponentCell){
	
				menu.add(new AbstractAction("rote Kante ziehen") {
					static final long serialVersionUID=0;
					public void actionPerformed(ActionEvent e) {
						graph.prepareInsertEdge((CCGraphCell)graph.getSelectionCell(),
								new Point(((Rectangle)(graph.getView().getMapping((CCGraphCell)graph.getSelectionCell(),false).getAttributes()).get("bounds")).getLocation().x+CommandConstants.ZWOELF,
										((Rectangle)(graph.getView().getMapping((CCGraphCell)graph.getSelectionCell(),false).getAttributes()).get("bounds")).getLocation().y+CommandConstants.ZWOELF),
										true,false);
					}	
				});
			}
		}
			
		
		if (this.graph.getSelectionCell() instanceof SubComponentCell){// only if moveable
			if ((SubComponentCell)graph.isMoveable((SubComponentCell)graph.getSelectionCell(), CommandConstants.MOVE_SUBCELL_IN)!=null){
				menu.add(new AbstractAction("move In") {
					static final long serialVersionUID=0;
					public void actionPerformed(ActionEvent e) {
						graph.moveSubCell(CommandConstants.MOVE_SUBCELL_IN);
					}
				});
			}
			if ((SubComponentCell)graph.isMoveable((SubComponentCell)graph.getSelectionCell(), CommandConstants.MOVE_SUBCELL_OUT)!=null){
				menu.add(new AbstractAction("move Out") {
					static final long serialVersionUID=0;
					public void actionPerformed(ActionEvent e) {
						graph.moveSubCell(CommandConstants.MOVE_SUBCELL_OUT);
					}
				});
			}
		}
		
		//editable Labels:
		if (((this.graph.getSelectionCell() instanceof ComponentCell))
				&& (graph.getSelectionCount() == 1)) {
			menu.add(new AbstractAction("Label setzen") {
				static final long serialVersionUID=0;
				public void actionPerformed(ActionEvent e) {
					graph.getNavModel().getCCModel().getController().assignLabelDialog(graph.getSelectionCell());
				}
			});
		}
		
		return menu;
    }
    
/**
 * creates menu items for edges add attaches them to an existing menu
 * @param menu the existing menu
 * @param ed the egde
 * @return the menu with entries for the given edge
 */
    private JPopupMenu createEdgeMenu(JPopupMenu menu, final CCEdgeCell ed){
    	
		if (ed.getStartCell() instanceof MainComponentCell && ed.getEndCell() instanceof MainComponentCell){
		
			if (((CCEdgeCell)ed).getRed())
				menu.add(new AbstractAction("rote Linie entfernen") {
					static final long serialVersionUID=0;
					public void actionPerformed(ActionEvent e) {

						if (!graph.toggleRed(ed))
							if (graph.getController().dialogdeleteEdge(ed))
								graph.delete();
					}	
				});
				
			else menu.add(new AbstractAction("rote Linie setzen") {
				static final long serialVersionUID=0;
				public void actionPerformed(ActionEvent e) {

					if (!graph.toggleRed(ed))
						if (graph.getController().dialogdeleteEdge(ed))
							graph.delete();
				}	
			});
		}
		if (ed.getExists())
			menu.add(new AbstractAction("aus Netzwerk entfernen") {
				static final long serialVersionUID=0;
				public void actionPerformed(ActionEvent e) {
				  
					if (!graph.toggleEdgeExists(ed))
						if (graph.getController().dialogdeleteEdge(ed))
							graph.delete();

				}	
			});
		else menu.add(new AbstractAction("ins Netzwerk nehmen") {
			static final long serialVersionUID=0;
			public void actionPerformed(ActionEvent e) {
				
				if (!graph.toggleEdgeExists(ed))
					if (graph.getController().dialogdeleteEdge(ed))
						graph.delete();

			}	
		});

		
		menu.add(new AbstractAction("Richtung \u00e4ndern") {
			static final long serialVersionUID=0;
			public void actionPerformed(ActionEvent e) {
					graph.toggleEdgeDirection();
			}	
		});
		return menu;

    }
	
    /**
     * this is a listener for the MouseEvent
     */
	public void mouseMoved(MouseEvent arg0) {
		this.setCurrent(arg0.getPoint());
		
		if (this.getCurrent()!=null) this.graph.repaint(0);
		
		super.mouseMoved(arg0);
		
	}

	/**
	 * the paint-method which draws the whole graph
	 */
	public void paint(Graphics g) {
		
		if ((graph.eCell!=null) && (graph.insertEdgeMode==true)) {//paints helplines while drawing a line 
			
			g.setColor(Color.BLUE);
			
			int size = this.graph.eCell.getDrawEdgeList().size();
			
			Point pPrev = new Point(((Rectangle)(this.graph.getView().getMapping(this.graph.eCell.getStartCell(),false).getAttributes()).get("bounds")).getLocation().x+CommandConstants.ZWOELF,
					((Rectangle)(this.graph.getView().getMapping(this.graph.eCell.getStartCell(),false).getAttributes()).get("bounds")).getLocation().y+CommandConstants.ZWOELF);
			
			pPrev.move((int)(pPrev.x*graph.getScale()),(int)(pPrev.y* graph.getScale()));

			// helpLines
			if (size>0){
				
				LinkedList<Point> list = this.graph.eCell.getDrawEdgeList();
				LinkedList<Point> modifiedList = new LinkedList<Point>();
				
				for (int i=0 ;i<list.size();i++){
					modifiedList.addLast(
						new Point((int)(((Point)list.get(i)).x*this.graph.getScale()),
							(int)(((Point)list.get(i)).y*this.graph.getScale())));
				
				}
				
				Point pStart;
				if (pPrev.y<((Point)modifiedList.getFirst()).y)
					pStart = new Point(pPrev.x,(int)((pPrev.y+(int)(CommandConstants.ZWOELF*this.graph.getScale()))));
				else 
					pStart = new Point(pPrev.x,(int)((pPrev.y-(int)(CommandConstants.ZWOELF*this.graph.getScale()))));

				// this would be the first Edge..
				g.drawLine(
						pStart.x,
						pStart.y,
						((Point)modifiedList.getFirst()).x,
						((Point)modifiedList.getFirst()).y);

				//other points
				int[] xPoints = new int[size];
				int[] yPoints = new int[size];
				
				for (int i=0;i<size;i++){
					Point p = (Point)modifiedList.get(i);
					xPoints[i] = p.x;
					yPoints[i] = p.y;
				}
				
				g.drawPolyline(xPoints,yPoints,size);
				pPrev = ((Point)modifiedList.getLast()); 
			}
			
			// this is the 'non-fixed' helpline
			g.setColor(Color.GRAY);
			if (size == 0)
				if (pPrev.y>this.getCurrent().y) pPrev.y=pPrev.y-(int)(CommandConstants.ZWOELF*this.graph.getScale());
				else pPrev.y=pPrev.y+(int)(CommandConstants.ZWOELF*this.graph.getScale());
			
			Point orth2=this.graph.eCell.getOrthPoint(this.getCurrent());
			Point orth = null;
			
			if (this.graph.eCell.getDrawEdgeListSize()%2!=0)
				orth = new Point((int)(orth2.x),(int)(orth2.y*this.graph.getScale()));
			else 
				orth  = new Point((int)(orth2.x*this.graph.getScale()),(int)(orth2.y));
			g.drawLine(
				pPrev.x,
				pPrev.y,
				orth.x,
				orth.y);
		}
		
		super.paint(g);
	}

	/**
	 * 
	 * @return the current Point
	 */
	public Point getCurrent() {
		return current;
	}

	public void setCurrent(Point current) {
		this.current = current;
	}
	
	void babble(String bab){
			if (this.graph.getNavModel().getCCModel().getController().getConfig().getDEBUG_NAVGRAPH_MARQUEE_HANDLER()
					&& graph.getNavModel().getCCModel().getController().getConfig().getDEBUG())
				System.out.println("NavMarqueeHandler: "+bab);
	}
}
