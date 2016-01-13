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

import com.jgraph.graph.DefaultGraphCell;

/**
 * the abstract superclass for a cell in the graph - is implemented by {@link MainComponentCell} 
 * and {@link SubComponentCell}
 * @author <a href="mailto:binder@math.tu-berlin.de>Jens Binder</a>
 * @author <a href="mailto:vrichter@math.tu-berlin.de>Verena Richter</a>
 * @version $Id: CCGraphCell.java,v 1.5 2009/03/30 12:40:26 vrichter Exp $
 *
 */

abstract public class CCGraphCell extends DefaultGraphCell{

	/**
	 * unique NodeID
	 */
  private int NodeID; 
  
  /**
   * x-koordinate for View
   */
  private int posX=-1;  
  
  /**
   * y-koordinate for View
   */
  private int posY=-1;

  public CCGraphCell(){}

  
  public int getNodeID() {
	  return NodeID;
  }
	
  public void setNodeID(int nodeID) {
	NodeID = nodeID;
  }
	
  public int getPosX() {
	return posX;
  }
	
  public void setPosX(int posX) {
	this.posX = posX;
  }
	
  public int getPosY() {
	return posY;
  }
	
  public void setPosY(int posY) {
	this.posY = posY;
  }
	
}

