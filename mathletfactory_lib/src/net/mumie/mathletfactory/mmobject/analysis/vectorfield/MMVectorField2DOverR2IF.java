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

package net.mumie.mathletfactory.mmobject.analysis.vectorfield;

import java.awt.Color;

import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.analysis.vectorfield.VectorField2DOverR2IF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 * This interface is implemented by all mmobjects that represent vector fields over R�. It offers
 * some methods concerning the visual properties of the vector field.
 *  
 * @author ahlborg, Paehler, heimann
 * @mm.docstatus finished
 */
public interface MMVectorField2DOverR2IF
	extends VectorField2DOverR2IF, MMCanvasObjectIF {
    
	/**
	 * If set to true the vectors length will be correctly displayed. Otherwise only a direction field
	 * (vector length = const) will be displayed.
	 */
	public void setVisualizeByLength(boolean aFlag);

	/**
	 * Returns whether the field will be displayed as direction field or real vector field. 
	 */
	public boolean isVisualizeByLength();

	/**
	 * If set to true the vectors width displayed will be proportional to the (mathematical) length of the 
	 * vector. Otherwise a constant vector width will be used. 
	 */
	public void setVisualizeByWidth(boolean aFlag);

	/**
	 * Returns whether the vectors width displayed will be proportional to the (mathematical) length of the 
	 * vector. 
	 */
	public boolean isVisualizeByWidth();
	  
	/**
	 * If set to true the length of the vectors are automatically scaled to fit proper into the canvas.
	 */
	public void setAutoscaleLength(boolean aFlag);
		
	/**
	 * Returns whether the length of the vectors are automatically scaled or not. 
	 */
	public boolean isAutoscaleLength();

	/**
	 * If set to true the displayed color of the vectors  will be proportional to the (mathematical) 
	 * length of the vectors. 
	 */
	public void setVisualizeByColor(boolean aFlag);

	/**
	 * If set to true the display color of the vectors  will be proportional to the (mathematical) 
	 * length of the vectors. color1 and color2 are the colors used for the visualization for the 
	 * extrema (min and max).
	 */
	public void setVisualizeByColor(boolean aFlag, Color color1, Color color2);
	
	/**
	 * Returns the two colors used for visualize the vectors length proportional to the (mathematical) 
	 * length of the vectors if setVisualizeByColor is set to true. 
	 */
	public Color[] getColorsForVisualization();

	/**
	 * Sets the two colors used for visualize the vectors length proportional to the (mathematical) 
	 * length of the vectors if setVisualizeByColor is set to true. 
	 */
	public void setColorsForVisualization(Color color1, Color color2);

	/**
	 * Returns whether the diplayed color of the vectors will be proportional to the (mathematical) 
	 * length of the vectors. 
	 */
	public boolean isVisualizeByColor();
	
	/** 
	 * Sets the constant arrow length used by the system when displaying the field as direction field. 
	 */
	public void setArrowLength(double newLength);

	/**
	 * Returns the constant arrow length used by the system when displaying the field as direction field. 
	 */
	public double getArrowLength();
	
	/** 
	 * If set to true an alternative grid for vector display is used.
	 */
	public void setAlternativeVectorGrid(boolean aFlag);
	
	/** 
	 * Returns true if an alternative grid for vector display is used.
	 */
	public boolean isAlternativeVectorGrid();

	/** 
	 * If set to true vectors are displayed.
	 * Note: if isVisible()==false this setting will be ignored
	 */	
	public void setVectorsVisible(boolean aFlag);
	
	/** 
	 * Returns true if vectors are displayed.
	 */
	public boolean isVectorsVisible();
	
	/** 
	 * If set to true vector field lines are displayed.
	 * If aFlag is true and no points (determining the vector field lines) are explicit 
	 * defined then some vector field lines are computed automatically.
	 * Note: if isVisible()==false this setting will be ignored
	 */
	public void setFieldLinesVisible(boolean aFlag);

	/** 
	 * If set to true vector field lines are displayed.
	 * The displayed vector field lines are determined by the given points.
	 * If points==null and aFlag is true then some vector field lines are automatically computed.
	 * Note: if isVisible()==false this setting will be ignored
	 */
	public void setFieldLinesVisible(boolean aFlag, Affine2DPoint[] points);

	/**
	 * Sets the points which determine the displayed vector field lines. 
	 */
	public void setFieldLinesPoints(Affine2DPoint[] points);
	
	/** 
	 * Returns the points which determine the displayed vector field lines. 
	 */
	public Affine2DPoint[] getFieldLinesPoints();

	/**	 
	 * Returns true if vector field lines are displayed. 
	 */	
	public boolean isFieldLinesVisible();
	
	/** 
	 * Returns the DisplayProperties of the vector field lines. 
	 */	
	public PolygonDisplayProperties getFieldLinesDisplayProperties();
	
	/** 
	 * Sets the exactitude used for the calculation of the vector field lines.
	 * Values near zero increase the exactitude. 
	 */	
	public void setFieldLinesExactitude(double value);
	
	/** 
	 * Returns the exactitude used for the calculation of the vector field lines. 
	 */	
	public double getFieldLinesExactitude();
	
	/** 
	 * Sets the number of points calculated for each vector field line from an initial point in each direction.
	 * (each fieldline is represented by 2*value+1 points) 
	 */	
	public void setFieldLinesLength(int value);

	/** 
	 * Returns the number of points calculated for each vector field line from an initial point in each direction.
	 * (each fieldline is represented by 2*value+1 points) 
	 */			
	public int getFieldLinesLength();
	
	/** Sets the number of vectors in x direction. */
	public void setXCount(int x);

	/** Sets the number of vectors in y direction. */
	public void setYCount(int y);

	/** Returns the number of vectors in x direction. */
	public int getXCount();

	/** Returns the number of vectors in y direction. */
	public int getYCount();
}
