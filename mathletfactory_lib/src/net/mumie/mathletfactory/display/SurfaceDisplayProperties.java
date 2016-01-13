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


import java.awt.Color;


/**
 * Display properties for surface rendering.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class SurfaceDisplayProperties extends DisplayProperties {

	public static final SurfaceDisplayProperties DEFAULT = new SurfaceDisplayProperties();

	public final static String DP_BASE_PATH = "display.surface.";

	/** 
	 * Name of the <i>grid color</i> property.
	 * @see #getGridColor()
	 */
	public final static String GRID_COLOR_PROPERTY = DP_BASE_PATH + "gridColor";

	/** 
	 * Name of the <i>grid visible</i> property.
	 * @see #isGridVisible()
	 */
	public final static String GRID_VISIBLE_PROPERTY = DP_BASE_PATH + "gridVisible";

	// Standardwerte (Farben) fuer Gitter und die Flaechen
	public static final Color GRID_COLOR_DEFAULT = Color.BLACK;

	private static final String BORDER_VISIBLE_OLD = "borderVisibleOld";

  //
  // load default properties into static map
  //
  
  static {
  	DEFAULT.setDefaultProperty(GRID_COLOR_PROPERTY, GRID_COLOR_DEFAULT);
  	DEFAULT.setDefaultProperty(GRID_VISIBLE_PROPERTY, true);
  }
  
	/** Empty constructor. */
	public SurfaceDisplayProperties() {
		this(null);
	}

//	public SurfaceDisplayProperties( String name ) {
//		super( name );
//		this.getShaderDisplayProperties().setVertexVisible( false );
//		this.getShaderDisplayProperties().setEdgeVisible( false );
//		this.getShaderDisplayProperties().setDiffuseColor( SurfaceDisplayProperties.SURFACE_COLOR_DEFAULT );
//	}

	/** Copy constructor. */
	public SurfaceDisplayProperties( DisplayProperties p ) {
  	super(p);
//		if ( !( p instanceof SurfaceDisplayProperties ) ) {
////			this.getShaderDisplayProperties().setVertexVisible( false );
//
//			this.setBorderDisplayed( p.isBorderDisplayed() );
//			this.setFilled( p.isFilled() );
//		}
	}

	public Color getGridColor() {
		return getColorProperty(GRID_COLOR_PROPERTY, GRID_COLOR_DEFAULT);
	}

	public void setGridColor( Color c ) {
		setProperty(GRID_COLOR_PROPERTY, c);
	}

	public boolean isGridVisible() {
		return getBooleanProperty(GRID_VISIBLE_PROPERTY, true);
	}

	public void setGridVisible( boolean v ) {
		setProperty(GRID_VISIBLE_PROPERTY, v);
	}

	/**
	 * Sets the "filled" property.
	 * Note that the "isBorderDisplayed" property will be changed to <code>true</code> 
	 * if the argument <code>filled</code> is set to <code>false</code>.
	 */
	public void setFilled( boolean filled ) {
		if ( !filled ) {
			this.setProperty( DP_BASE_PATH + BORDER_VISIBLE_OLD, new Boolean( this.isBorderDisplayed() ) );
			this.setBorderDisplayed( true );
		} else {
			Object value = this.getProperty( DP_BASE_PATH + BORDER_VISIBLE_OLD );
			if ( value != null ) {
//				this.setDrawBorder( ( Boolean ) value );
				this.setBorderDisplayed( ((Boolean) value).booleanValue() );
			}
		}
		super.setFilled( filled );
	}

	protected DisplayProperties createInstance() {
		return new SurfaceDisplayProperties();
	}

	public Object clone() {
		DisplayProperties props = (DisplayProperties) super.clone();
		return new SurfaceDisplayProperties( props );
	}
}
