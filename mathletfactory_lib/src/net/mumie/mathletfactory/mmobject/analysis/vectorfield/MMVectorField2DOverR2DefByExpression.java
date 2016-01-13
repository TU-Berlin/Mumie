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
import java.util.ArrayList;

import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.analysis.vectorfield.VectorField2DOverR2IF;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class implements a vector field that is defined by an adapter of an
 * {@link net.mumie.mathletfactory.math.analysis.vectorfield.VectorField2DOverR2IF}.
 * @author ahlborg
 * @mm.docstatus finished
 */
public class MMVectorField2DOverR2DefByExpression extends MMDefaultCanvasObject implements MMVectorField2DOverR2IF {

	private Class m_numberClass;
  
	private VectorField2DOverR2IF m_fieldEvaluator;
  
	private ArrayList m_addedFields; // to store the added vector fields in
  
	private int m_xCount, m_yCount, m_polygonPoints;
  
	private double m_arrowLength, m_fieldLinesExactitude;
  
	private boolean m_vLength, m_vWidth, m_vAutoscale, m_vColor, m_altVGrid, m_fieldLines, m_arrows;
	
	private Color m_vColor1, m_vColor2;
	
	private Affine2DPoint[] m_fieldLinesPoints;
	
	private PolygonDisplayProperties m_fieldLinesDisplayProperties;

	public MMVectorField2DOverR2DefByExpression(Class aClass) {
		m_numberClass = aClass;
		setYCount(4);
		setXCount(4);
		setArrowLength(1);
		setVisualizeByLength(false);
		setVisualizeByWidth(false);
		setDisplayProperties(LineDisplayProperties.VECTOR_DEFAULT);
		m_fieldLinesDisplayProperties=new PolygonDisplayProperties();
		m_fieldLinesDisplayProperties.setTransparency(0.2);
		m_fieldLinesDisplayProperties.setLineWidth(2);
		setFieldLinesExactitude(0.01);
		setFieldLinesLength(100);
		setVectorsVisible(true);
		setFieldLinesVisible(false);
		m_addedFields = new ArrayList();
		m_fieldEvaluator = new VectorField2DOverR2IF(){
//			public void evaluate(Affine2DPoint in, NumberVector out){}
			public void evaluate(double[] in, double[] out){out[0]=0;out[1]=0;return;}
			public Class getNumberClass(){return null;}
		};
	}
	
	/**
	 * Adds <code>field</code> to this vector field, i.e. superposes the two fields
	 * and stores this dependency in this <code>MMVectorField2DOverR2</code>.
	 */
	public void add(MMVectorField2DOverR2DefByExpression field) {
		if(field != this)
			m_addedFields.add(field);
		else
			throw new IllegalArgumentException("Cannot add a vector field to itself!");
	}
	
	/**
	 * Removes <code>field</code> from the list of vector fields to superpose.
	 */
	public void remove(MMVectorField2DOverR2DefByExpression field) {
		m_addedFields.remove(field);
	}
	
	/**
	 * Removes all vector fields from the list of fields to superpose.
	 */
	public void removeAll() {
		m_addedFields.clear();
	}

	public void setVisualizeByLength(boolean aFlag) {
		m_vLength = aFlag;
	}

	public boolean isVisualizeByLength() {
		return m_vLength;
	}

	public void setVisualizeByWidth(boolean aFlag) {
		m_vWidth = aFlag;
	}

	public boolean isVisualizeByWidth() {
		return m_vWidth;
	}

	public void setAutoscaleLength(boolean aFlag) {
		m_vAutoscale = aFlag;
	}
	
	public boolean isAutoscaleLength() {
		return m_vAutoscale;
	}
	
	public void setVisualizeByColor(boolean aFlag) {
		if(aFlag&&(m_vColor1==null||m_vColor2==null)){
			Color c;
			if(getDisplayProperties().getObjectColor().getRed()+getDisplayProperties().getObjectColor().getGreen()+
					getDisplayProperties().getObjectColor().getBlue()<255*1.5)c=Color.white;
			else c=Color.black;
			setVisualizeByColor(aFlag, getDisplayProperties().getObjectColor(), c);
		}else m_vColor = aFlag;
	}

	public boolean isVisualizeByColor() {
		return m_vColor;
	}
	
	public void setVisualizeByColor(boolean aFlag, Color color1, Color color2) {
		m_vColor = aFlag;
		m_vColor1 = color1;
		m_vColor2 = color2;
	}
	
	public void setColorsForVisualization(Color color1, Color color2) {
		m_vColor1 = color1;
		m_vColor2 = color2;
	}
	
	public Color[] getColorsForVisualization() {
		return new Color[]{m_vColor1,m_vColor2};
	}
	
	public void setAlternativeVectorGrid(boolean aFlag) {
		m_altVGrid = aFlag;
	}

	public boolean isAlternativeVectorGrid() {
		return m_altVGrid;
	}
	
	public void setArrowLength(double newLength) {
		m_arrowLength = newLength;
	}

	public double getArrowLength() {
		return m_arrowLength;
	}

	public void setVectorsVisible(boolean aFlag){
		m_arrows=aFlag;
	}
	
	public boolean isVectorsVisible(){
		return m_arrows;
	}
	
	public void setFieldLinesVisible(boolean aFlag) {
		m_fieldLines=aFlag;
	}
	
	public boolean isFieldLinesVisible() {
		return m_fieldLines;
	}
	
	public PolygonDisplayProperties getFieldLinesDisplayProperties() {
		return m_fieldLinesDisplayProperties;
	}
	
	public void setFieldLinesExactitude(double value) {
		m_fieldLinesExactitude=value;
	}
	
	public double getFieldLinesExactitude() {
		return m_fieldLinesExactitude;
	}
	
	public void setFieldLinesLength(int value) {
		m_polygonPoints=value;
	}
	
	public int getFieldLinesLength() {
		return m_polygonPoints;
	}

	public void setFieldLinesVisible(boolean aFlag, Affine2DPoint[] points) {
		m_fieldLines=aFlag;
		m_fieldLinesPoints=points;
	}
	
	public void setFieldLinesPoints(Affine2DPoint[] points) {	
		m_fieldLinesPoints=points;
	}
	
	public Affine2DPoint[] getFieldLinesPoints() {
		return m_fieldLinesPoints;
	}
	
	public void setXCount(int x) {
		m_xCount = x;
	}

	public void setYCount(int y) {
		m_yCount = y;
	}

	public int getXCount() {
		return m_xCount;
	}

	public int getYCount() {
		return m_yCount;
	}

	public void evaluate(double[] inData, double[] outData) {
		m_fieldEvaluator.evaluate(inData, outData);
		for(int i = 0; i < m_addedFields.size(); i++) {
			double[] outTmpData = new double[2]; // all is two dimensional!
			// note: the user can overwrite "outData"'s value so create a new one and add them later
			((MMVectorField2DOverR2DefByExpression)m_addedFields.get(i)).evaluate(inData, outTmpData);
			outData[0] += outTmpData[0];
			outData[1] += outTmpData[1];
		}
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.VECTOR_FIELD2D_ARROW_TRANSFORM;
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.NO_TRANSFORM_TYPE;
	}

  /** Sets the evaluation expressions for this vector field. */
	public void setEvaluateExpression(VectorField2DOverR2IF vectorField) {
		m_fieldEvaluator = vectorField;
	}
  
  public Class getNumberClass() {
    return m_numberClass;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.VECTOR_FIELD2D_ARROW_TRANSFORM;
  }
  
}
