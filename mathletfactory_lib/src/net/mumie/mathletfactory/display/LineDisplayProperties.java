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


import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;


/**
 * This class handles the specific display properties for all types of lines.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class LineDisplayProperties extends DisplayProperties {

	public final static String DP_BASE_PATH = "display.line.";
	
	/** 
	 * Name of the <i>line width</i> property.
	 * @see #getLineWidth()
	 */
	public final static String LINE_WIDTH_PROPERTY = DP_BASE_PATH + "width";
	
	public final static LineDisplayProperties DEFAULT = new LineDisplayProperties();
	
	public final static LineDisplayProperties VECTOR_DEFAULT = new LineDisplayProperties();

	/** 
	 * Name of the <i>arrow at start</i> property.
	 * @see #getArrowAtStart()
	 */
	public final static String ARROW_AT_START_PROPERTY = DP_BASE_PATH + "arrowAtStart";
	
	/** 
	 * Name of the <i>arrow at end</i> property.
	 * @see #getArrowAtEnd()
	 */
	public final static String ARROW_AT_END_PROPERTY = DP_BASE_PATH + "arrowAtEnd";

	// Standardmaessig ist eine Linie 3 Pixel dick (wenn TUBES_DRAW = false)
	public final static double DEFAULT_LINE_WIDTH = 3;

	/** Constant for a straight line ending. */
	public final static GeneralPath LINE_END = new GeneralPath();

	/** Constant for a curved line ending. */
	public final static GeneralPath ARC_END = new GeneralPath();

	/** Constant for a simple arrow line ending. */
	public final static GeneralPath SIMPLE_ARROW_END = new GeneralPath();

	/** Constant for a plain arrow line ending. */
	public final static GeneralPath PLAIN_ARROW_END = new GeneralPath();
	
	/** Constant for a complex arrow line ending. */
	public final static GeneralPath COMPLEX_ARROW_END = new GeneralPath();
	
	/** Constant for an arrow line ending. */
	public final static GeneralPath ARROW_END = new GeneralPath();

  //
  // load default properties into static map
  //
  
	static {
		LINE_END.moveTo( 0, 0 );
		LINE_END.lineTo( 10, 0 );

		ARC_END.append( new Arc2D.Double( 5, -5, 10, 10, 0, 180, Arc2D.OPEN ), false );

		SIMPLE_ARROW_END.moveTo( 20, 25 );
		SIMPLE_ARROW_END.lineTo( 20, 14 );
		SIMPLE_ARROW_END.lineTo( 27, 25 );
		SIMPLE_ARROW_END.lineTo( 30, 22 );
		SIMPLE_ARROW_END.lineTo( 15, 0 );
		SIMPLE_ARROW_END.lineTo( 0, 22 );
		SIMPLE_ARROW_END.lineTo( 3, 25 );
		SIMPLE_ARROW_END.lineTo( 10, 14 );
		SIMPLE_ARROW_END.lineTo( 10, 25 );

		PLAIN_ARROW_END.moveTo( 20, 25 );
		PLAIN_ARROW_END.lineTo( 30, 25 );
		PLAIN_ARROW_END.lineTo( 15, 0 );
		PLAIN_ARROW_END.lineTo( 0, 25 );
		PLAIN_ARROW_END.lineTo( 10, 25 );

		COMPLEX_ARROW_END.moveTo( 20, 25 );
		COMPLEX_ARROW_END.lineTo( 20, 20 );
		COMPLEX_ARROW_END.lineTo( 30, 25 );
		COMPLEX_ARROW_END.lineTo( 15, 0 );
		COMPLEX_ARROW_END.lineTo( 0, 25 );
		COMPLEX_ARROW_END.lineTo( 10, 20 );
		COMPLEX_ARROW_END.lineTo( 10, 25 );

		ARROW_END.moveTo( 30, 38 );
		ARROW_END.lineTo( 45, 38 );
		ARROW_END.lineTo( 22, 0 );
		ARROW_END.lineTo( 0, 38 );
		ARROW_END.lineTo( 15, 38 );

		VECTOR_DEFAULT.setDefaultProperty(LINE_WIDTH_PROPERTY, DEFAULT_LINE_WIDTH);
		VECTOR_DEFAULT.setArrowAtStart( ARC_END );
		VECTOR_DEFAULT.setArrowAtEnd( COMPLEX_ARROW_END );
		
		DEFAULT.setDefaultProperty(LINE_WIDTH_PROPERTY, DEFAULT_LINE_WIDTH);
		DEFAULT.setDefaultProperty(ARROW_AT_START_PROPERTY, ARC_END);
		DEFAULT.setDefaultProperty(ARROW_AT_END_PROPERTY, ARC_END);
	}

	public LineDisplayProperties() {
		this(null);
	}

//	public LineDisplayProperties( String name ) {
//		super( name );
//		this.getShaderDisplayProperties().setVertexVisible( false );
//		this.getShaderDisplayProperties().setFaceVisible( false );
//	}

	public LineDisplayProperties( DisplayProperties p ) {
		super( p );
//		if(p != null)
//			copyPropertiesFrom(p);
		
//	 DP: moved to JR3DShaderDisplayProperties's copy constructor
//		if ( !( p instanceof LineDisplayProperties ) ) {
//			this.getShaderDisplayProperties().setEdgeVisible( true );
//			this.getShaderDisplayProperties().setVertexVisible( false );
//			this.getShaderDisplayProperties().setFaceVisible( false );
//		}
	}

	public GeneralPath getArrowAtStart() {
		Object value = this.getProperty( ARROW_AT_START_PROPERTY );
		if ( value == null ) {
			value = ARC_END;
		}
		return ( GeneralPath ) value;
	}

	public void setArrowAtStart( GeneralPath value ) {
		this.setProperty( ARROW_AT_START_PROPERTY, value );
	}

	public GeneralPath getArrowAtEnd() {
		Object value = this.getProperty( ARROW_AT_END_PROPERTY );
		if ( value == null ) {
			value = ARC_END;
		}
		return ( GeneralPath ) value;
	}

	public void setArrowAtEnd( GeneralPath value ) {
		this.setProperty( ARROW_AT_END_PROPERTY, value );
	}

	public double getLineWidth() {
		Object value = this.getProperty( LINE_WIDTH_PROPERTY );
		if ( value == null ) {
			value = new Double( DEFAULT_LINE_WIDTH );
		}
		return (( Double ) value).doubleValue();
	}

	public void setLineWidth( double width ) {
		this.setProperty( LINE_WIDTH_PROPERTY, new Double( width ) );
	}

	protected DisplayProperties createInstance() {
		return new LineDisplayProperties();
	}

	public Object clone() {
		DisplayProperties props = (DisplayProperties) super.clone();
		return new LineDisplayProperties( props );
	}
}
