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

package net.mumie.mathletfactory.display;


/**
 * This class handles the specific display properties for all types of polygons.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class PolygonDisplayProperties extends DisplayProperties {

	public static final PolygonDisplayProperties DEFAULT = new PolygonDisplayProperties();

  //
  // load default properties into static map
  //
  
  static {
  	DEFAULT.setDefaultProperty(FILLED_PROPERTY, false);
  }
  
	/**
	 * Constructs a new instance of this class.
	 * 
	 */
	public PolygonDisplayProperties() {
		this(null);
	}

	/**
	 * Constructs a new instance of this class with the given properties.
	 * 
	 * @param props
	 *            the properties to be copyied
	 */
	public PolygonDisplayProperties( DisplayProperties p ) {
		super( p );
		setFilled(false);
	}

	/**
	 * Returns the line width of the polygon.
	 */
	public double getLineWidth() {
		return getBorderWidth();
	}

	/**
	 * sets the line width of the polygon.
	 */
	public void setLineWidth( double width ) {
		setBorderWidth( ( int ) width );
	}

	public Object clone() {
		DisplayProperties props = (DisplayProperties) super.clone();
		return new PolygonDisplayProperties( props );
	}
}
