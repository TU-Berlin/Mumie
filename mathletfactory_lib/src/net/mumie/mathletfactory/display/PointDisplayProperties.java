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
 * This class handles the specific display properties for all types of points.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class PointDisplayProperties extends DisplayProperties {

	public final static String DP_BASE_PATH = "display.point.";
	
	/** 
	 * Name of the <i>point radius</i> property.
	 * @see #getPointRadius()
	 */
	public final static String POINT_RADIUS_PROPERTY = DP_BASE_PATH + "radius";

	public final static PointDisplayProperties DEFAULT = new PointDisplayProperties();

	// Standardwerte fuer die Groesse der Punkte
	public final static int POINT_RADIUS_DEFAULT = 5;

  //
  // load default properties into static map
  //
  
  static {
  	DEFAULT.setDefaultProperty(POINT_RADIUS_PROPERTY, POINT_RADIUS_DEFAULT);
  }
  
	public PointDisplayProperties() {
		this(null);
	}

//	public PointDisplayProperties( String name ) {
//		super( name );
//		this.getShaderDisplayProperties().setEdgeVisible( false );
//		this.getShaderDisplayProperties().setFaceVisible( false );
//	}
//
	/** Copy constructor */
	public PointDisplayProperties( DisplayProperties p ) {
		super( p );
		// DP: moved to JR3DShaderDisplayProperties's copy constructor
//		if ( !( p instanceof PointDisplayProperties ) ) {
//			this.getShaderDisplayProperties().setVertexVisible( true );
//			this.getShaderDisplayProperties().setEdgeVisible( false );
//			this.getShaderDisplayProperties().setFaceVisible( false );
//		}
	}

	/**
	 * Returns the radius of the point.
	 */
	public int getPointRadius() {
		Object value = this.getProperty(POINT_RADIUS_PROPERTY);
		if ( value == null ) {
			value = new Double( POINT_RADIUS_DEFAULT );
		}
		return (( Double ) value).intValue();
	}

	/**
	 * Sets the radius of the point to <code>radius</code>.
	 */
	public void setPointRadius( int radius ) {
		this.setProperty( POINT_RADIUS_PROPERTY, new Double( Math.abs( radius ) ) );
	}

	protected DisplayProperties createInstance() {
		return new PointDisplayProperties();
	}

	public Object clone() {
		DisplayProperties props = (DisplayProperties) super.clone();
		return new PointDisplayProperties( props );
	}
}
