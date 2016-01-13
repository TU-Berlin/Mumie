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

import java.awt.Point;
import java.util.LinkedList;

/** the class holds an edge for loading a Graph
 * @author <a href="mailto:vrichter@math.tu-berlin.de">Verena Richter</a>
 */
public class EdgeHolder {

	private int startCell;
	private int endCell;
	private LinkedList<Point> points;
	private boolean red;
	private boolean exists;
	
	EdgeHolder(int s, int e ,LinkedList<Point> points,boolean red ,boolean exists){
		this.endCell = e;
		this.startCell =s;
		
		this.points =points;
		
		this.exists =exists;
		this.red = red;
		
	}

	/** @return the endCell */
	public int getEndCell() {return endCell;}

	/** @return the exists */
	public boolean isExists() {return exists;}

	/** @return the points */
	public LinkedList<Point> getPoints() {return points;}

	/** @return the red */
	public boolean isRed() {return red;}

	/** @return the startCell*/
	public int getStartCell() {return startCell;}
	
	
}
