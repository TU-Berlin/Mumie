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

import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMFunctionOverR2;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class implements a vector field that is defined by two string expressions.
 * 
 * @author ahlborg, Paehler
 * @mm.docstatus finished
 */
public class MMVectorField2DOverR2DefByComponents extends MMDefaultCanvasObject
	implements MMVectorField2DOverR2IF {
    
	private Class m_numberClass;
  
	private int m_xCount, m_yCount, m_polygonPoints;
  
	private double m_arrowLength, m_fieldLinesExactitude;
  
	private boolean m_vLength, m_vWidth, m_vAutoscale, m_vColor, m_altVGrid, m_fieldLines, m_arrows;
  
	private MMFunctionOverR2 evaluateFunction1;
  
	private MMFunctionOverR2 evaluateFunction2;

	private MNumber m_tmpX, m_tmpY;
	
	private Color m_vColor1, m_vColor2;
	
	private Affine2DPoint[] m_fieldLinesPoints;
	
	private PolygonDisplayProperties m_fieldLinesDisplayProperties;

	public MMVectorField2DOverR2DefByComponents(Class aClass) {
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
		m_tmpX = NumberFactory.newInstance(m_numberClass);
		m_tmpY = NumberFactory.newInstance(m_numberClass);
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

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.VECTOR_FIELD2D_ARROW_TRANSFORM;
	}

	public void evaluate(double[] inData, double[] outData) {
		m_tmpX.setDouble(inData[0]);
		m_tmpY.setDouble(inData[1]);
		outData[0] = evaluateFunction1.evaluate(m_tmpX, m_tmpY).getDouble();
		outData[1] = evaluateFunction2.evaluate(m_tmpX, m_tmpY).getDouble();
	}

	public Class getNumberClass() {
		return m_numberClass;
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.VECTOR_FIELD2D_ARROW_TRANSFORM;
	}

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.NO_TRANSFORM_TYPE;
	}

  /** Sets the expressions used for calculation by this vector field. */
	public void setEvaluateStrings(
		String expressionString1,
		String expressionString2) {
		evaluateFunction1 = new MMFunctionOverR2(m_numberClass, expressionString1);
		evaluateFunction2 = new MMFunctionOverR2(m_numberClass, expressionString2);
	}
}
