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

package net.mumie.mathletfactory.transformer.g2d;

import java.awt.Color;

import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPolygonDrawable;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.mmobject.analysis.vectorfield.MMVectorField2DOverR2IF;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for classes implementing
 * {@link net.mumie.mathletfactory.mmobject.analysis.vectorfield.MMVectorField2DOverR2IF}.
 * 
 * @author ahlborg, heimann
 * @mm.docstatus finished
 */
public class VectorField2DTransformer extends Affine2DDefaultTransformer {

	private int m_nXArrows = 0,
		m_nYArrows = 0,
		m_nAllArrows = 0,
		currentArrow = 0;
	private double[] m_initInWorld = new double[2];
	private double[] m_initOnScreen = new double[2];
	private double[] m_endInWorld = new double[2];
	private double[] m_endOnScreen = new double[2];
	private double[] currentArrowWidth;
	private Color[] currentArrowColor;
	private double m_arrowLength, currentArrowLength;
	private double[] initInMath = new double[2];
	private double[] endInMath = new double[2];
	private int currentLine = 0;
	private final int m_nXYFieldlines = 5;//vector field lines grid parameter
	private int m_nXFieldLines;
	private int m_nYFieldLines;
	private int m_nAllFieldLines;
	
	private double screenXMin;
	private double screenYMin;
	private double screenXMax;
	private double screenYMax;
	private double screenWidth;
	private double screenHight;

	public void synchronizeMath2Screen() {
		if (m_nXArrows != getRealMasterObject().getXCount()
			|| m_nYArrows != getRealMasterObject().getYCount()
			|| m_arrowLength != getRealMasterObject().getArrowLength()||
			(getRealMasterObject().getFieldLinesPoints()!=null&&m_nAllFieldLines!=getRealMasterObject().getFieldLinesPoints().length)||
			m_nXFieldLines!=m_nXYFieldlines||m_nYFieldLines!=m_nXYFieldlines||m_nAllFieldLines!=Math.pow(m_nXYFieldlines, 2))
			initAllDataFields();
		synchronizeWorld2Screen();
	}

	private void initAllDataFields() {
		m_arrowLength = getRealMasterObject().getArrowLength();
		m_nXArrows = getRealMasterObject().getXCount();
		m_nYArrows = getRealMasterObject().getYCount();
		m_nAllArrows = m_nXArrows * m_nYArrows;
		m_allDrawables = new G2DLineDrawable[m_nAllArrows];
		for (int i = 0; i < m_nAllArrows; i++)
			m_allDrawables[i] = new G2DLineDrawable();
		currentArrowWidth = new double[m_nAllArrows];
		currentArrowColor = new Color[m_nAllArrows];
		//vector field lines
		if(getRealMasterObject().getFieldLinesPoints()!=null){
			m_nAllFieldLines = getRealMasterObject().getFieldLinesPoints().length;
		}else{
			m_nXFieldLines = m_nXYFieldlines;
			m_nYFieldLines = m_nXYFieldlines;
			m_nAllFieldLines = m_nXFieldLines*m_nYFieldLines;
		}
		m_additionalDrawables = new G2DPolygonDrawable[m_nAllFieldLines];
		for (int i = 0; i < m_nAllFieldLines; i++){
			m_additionalDrawables[i] = new G2DPolygonDrawable(1);
			((G2DPolygonDrawable) m_additionalDrawables[i]).setOpened();
		}
	}

	public void synchronizeWorld2Screen() {
		Affine2DPoint initialPoint =
			new Affine2DPoint(getRealMasterObject().getNumberClass());
		Affine2DPoint endPoint =
			new Affine2DPoint(getRealMasterObject().getNumberClass());
		adjustWorldBounds();
		screenXMin = m_llInWorldDraw[0];
		screenYMin = m_llInWorldDraw[1];
		screenXMax = m_urInWorldDraw[0];
		screenYMax = m_urInWorldDraw[1];
		screenWidth = m_urInWorldDraw[0] - screenXMin;
		screenHight = m_urInWorldDraw[1] - screenYMin;
		double xVertice = screenWidth / (m_nXArrows + 1);
		double yVertice = screenHight / (m_nYArrows + 1);
		double originalLineWidth = getProperties().getLineWidth();
		double maxArrowLength = 0;
		double x = 0;
		if(getRealMasterObject().isVectorsVisible()){
		//alternative arrow grid
		if(getRealMasterObject().isAlternativeVectorGrid())x = xVertice/4;
		if((getRealMasterObject().isAutoscaleLength()&&getRealMasterObject().isVisualizeByLength())||getRealMasterObject().isVisualizeByColor())
			maxArrowLength=getMaxLength(screenXMin, screenYMin, xVertice, yVertice);
		for (int xVerticeCount = 0; xVerticeCount < m_nXArrows; xVerticeCount++) {
			for (int yVerticeCount = 0;yVerticeCount < m_nYArrows;yVerticeCount++) {
				currentArrow = xVerticeCount + yVerticeCount * m_nXArrows;
				if(yVerticeCount%2==0)
					initInMath[0] = screenXMin + (xVerticeCount + 1) * xVertice-x;
				else initInMath[0] = screenXMin + (xVerticeCount + 1) * xVertice+x;
				initInMath[1] = screenYMin + (yVerticeCount + 1) * yVertice;
				getRealMasterObject().evaluate(initInMath, endInMath);
				if (getRealMasterObject().isVisualizeByLength()) currentArrowLength = 1;
				else currentArrowLength = Math.sqrt(endInMath[0] * endInMath[0] + endInMath[1] * endInMath[1]);
				currentArrowLength *= getWorld2Screen().getXScale()/100;
				//visualize field intensity by arrow width
				if (getRealMasterObject().isVisualizeByWidth()) currentArrowWidth[currentArrow] =
						originalLineWidth * Math.sqrt(endInMath[0] * endInMath[0] + endInMath[1] * endInMath[1]);
				double xdif = (endInMath[0] / currentArrowLength) * m_arrowLength;
				double ydif = (endInMath[1] / currentArrowLength) * m_arrowLength;
				//visualize field intensity by color
				if(getRealMasterObject().isVisualizeByColor()){
					double length;
					double temp;
					if(!getRealMasterObject().isVisualizeByLength())temp = Math.sqrt(endInMath[0] * endInMath[0] + endInMath[1] * endInMath[1]);
					else temp=1;
					double xdif0 = (Math.abs(xdif/m_arrowLength*temp));
					double ydif0 = (Math.abs(ydif/m_arrowLength*temp));
					length=Math.sqrt(xdif0*xdif0+ydif0*ydif0);
					currentArrowColor[currentArrow]=getColor(maxArrowLength, length);
				}
				//visualize field intensity by autoscaled arrow length
				if(getRealMasterObject().isVisualizeByLength()&&getRealMasterObject().isAutoscaleLength()){
					double scale = 1/maxArrowLength;
					double verticeMin = Math.min(xVertice, yVertice);
					double xdif0 = verticeMin*xdif/m_arrowLength*scale;
					double ydif0 = verticeMin*ydif/m_arrowLength*scale;
					initInMath[0] = initInMath[0] - xdif0 / 2;
					initInMath[1] = initInMath[1] - ydif0 / 2;
					endInMath[0] = initInMath[0] + xdif0;
					endInMath[1] = initInMath[1] + ydif0;
				}else{
					initInMath[0] = initInMath[0] - xdif / 2;
					initInMath[1] = initInMath[1] - ydif / 2;
					endInMath[0] = initInMath[0] + xdif;
					endInMath[1] = initInMath[1] + ydif;
				}
				initialPoint.setXY(initInMath[0], initInMath[1]);
				endPoint.setXY(endInMath[0], endInMath[1]);
				math2World(initialPoint, m_initInWorld);
				math2World(endPoint, m_endInWorld);
				world2Screen(m_initInWorld, m_initOnScreen);
				world2Screen(m_endInWorld, m_endOnScreen);
				getDrawable().setPoints(m_initOnScreen[0],m_initOnScreen[1],m_endOnScreen[0],m_endOnScreen[1]);
			}
		}
		getProperties().setLineWidth(originalLineWidth);
		}
		//display vector field lines
		if(getRealMasterObject().isFieldLinesVisible()){
			xVertice = screenWidth / (m_nXFieldLines+0.5);
			yVertice = screenHight / (m_nYFieldLines-1);
			x = xVertice/2;
			//field lines 'grid'
			if(getRealMasterObject().getFieldLinesPoints()==null)
			for (int xVerticeCount = 0; xVerticeCount < m_nXFieldLines; xVerticeCount++) 
				for (int yVerticeCount = 0;yVerticeCount < m_nYFieldLines;yVerticeCount++) {
					if(yVerticeCount%2==0)
						initInMath[0] = screenXMin + (xVerticeCount) * xVertice;
					else initInMath[0] = screenXMin + (xVerticeCount+1) * xVertice+x;
					initInMath[1] = screenYMin + (yVerticeCount) * yVertice;
					currentLine= xVerticeCount + yVerticeCount * m_nXFieldLines;
					getFieldLine(new double[]{initInMath[0],initInMath[1]});
				}
			//uses user defined points
			else for (int pointCount = 0; pointCount < getRealMasterObject().getFieldLinesPoints().length; pointCount++) {
				currentLine = pointCount;
				getFieldLine(new double[]{getRealMasterObject().getFieldLinesPoints()[pointCount].getXAsDouble(),
						getRealMasterObject().getFieldLinesPoints()[pointCount].getYAsDouble()});
			}
		}
	}
	
	//calculates (the part of) a field line to a given point in0
	private void getFieldLine(double[] in0){
		int max =getRealMasterObject().getFieldLinesLength();
		double exactitude = getRealMasterObject().getFieldLinesExactitude();
		double[][] cache1 = new double[max+1][2];
		double[][] cache2 = new double[max+1][2];
		int count1=1;
		int count2=1;
		cache1[0][0] = in0[0];
		cache1[0][1] = in0[1];
		cache2[0][0] = in0[0];
		cache2[0][1] = in0[1];
		double[] out = new double[in0.length];
		for (int i = 1; i <= max; i++) {
			getRealMasterObject().evaluate(cache1[i-1], out);
			currentArrowLength = Math.sqrt(out[0] * out[0] + out[1] * out[1]);
			currentArrowLength *= getWorld2Screen().getXScale()/100;
			double xdif = (out[0]/currentArrowLength);
			double ydif = (out[1]/currentArrowLength);
				out[0] = cache1[i-1][0] + xdif*exactitude;
				out[1] = cache1[i-1][1] + ydif*exactitude;
			cache1[i][0]=out[0];
			cache1[i][1]=out[1];
			count1++;
			if(out[0]<screenXMin-screenWidth||out[0]>screenXMax+screenWidth||
					out[1]<screenYMin-screenHight||out[1]>screenYMax+screenHight) break;
		}
		for (int i = 1; i <= max; i++) {
			getRealMasterObject().evaluate(cache2[i-1], out);
			currentArrowLength = Math.sqrt(out[0] * out[0] + out[1] * out[1]);
			currentArrowLength *= getWorld2Screen().getXScale()/100;
			double xdif = (out[0]/currentArrowLength);
			double ydif = (out[1]/currentArrowLength);
				out[0] = cache2[i-1][0] - xdif*exactitude;
				out[1] = cache2[i-1][1] - ydif*exactitude;
			cache2[i][0]=out[0];
			cache2[i][1]=out[1];
			count2++;
			if(out[0]<screenXMin-screenWidth||out[0]>screenXMax+screenWidth||
					out[1]<screenYMin-screenHight||out[1]>screenYMax+screenHight) break;
		}
		getAdditionalDrawable().initLength(count1+count2-1);
		int vertexCounter = 0;
		Affine2DPoint endPoint = new Affine2DPoint(getRealMasterObject().getNumberClass());
		for (int i = 0; i < count1+count2; i++) {
			if(i<count2){
				endPoint.setXY(cache2[count2-i-1][0], cache2[count2-i-1][1]);
				vertexCounter++;
			}else if(i!=count2){
				endPoint.setXY(cache1[i-count2][0], cache1[i-count2][1]);
				vertexCounter++;
			}
			math2World(endPoint, m_endInWorld);
			world2Screen(m_endInWorld, m_endOnScreen);
			if(i!=count2)getAdditionalDrawable().setPoint(vertexCounter-1, m_endOnScreen[0],m_endOnScreen[1]);
		}
	}

	//returns the current polygon
	private G2DPolygonDrawable getAdditionalDrawable() {
		return (G2DPolygonDrawable) m_additionalDrawables[currentLine];
	}
	
	//returns the maximum arrow length (used for autoscaling and color visualization)
	private double getMaxLength(double screenXMin,double screenYMin, double xVertice, double yVertice){
		double maxArrowLength=0;
		double x = 0;
		if(getRealMasterObject().isAlternativeVectorGrid())x = xVertice/4;
		for (int xVerticeCount = 0; xVerticeCount < m_nXArrows; xVerticeCount++) {
			for (int yVerticeCount = 0;yVerticeCount < m_nYArrows;yVerticeCount++) {
				currentArrow = xVerticeCount + yVerticeCount * m_nXArrows;
				if(yVerticeCount%2==0)
					initInMath[0] = screenXMin + (xVerticeCount + 1) * xVertice -x;
				else initInMath[0] = screenXMin + (xVerticeCount + 1) * xVertice +x;
				initInMath[1] = screenYMin + (yVerticeCount + 1) * yVertice;
				getRealMasterObject().evaluate(initInMath, endInMath);
			    currentArrowLength = getWorld2Screen().getXScale()/100;
				double xdif = (endInMath[0] / currentArrowLength);
				double ydif = (endInMath[1] / currentArrowLength);
				double length = Math.sqrt(xdif*xdif + ydif*ydif); 
				if(length>maxArrowLength)maxArrowLength=length;
			}
		}
		return maxArrowLength;
	}

	//returns the resulting arrow color for color visualization
	private Color getColor(double max_arrow_length, double length){
		Color c2 = getRealMasterObject().getColorsForVisualization()[0];
		Color c1 = getRealMasterObject().getColorsForVisualization()[1];
		double weight = length/max_arrow_length;
		int r0 = (int)Math.abs(Math.round(c1.getRed()+(c2.getRed()-c1.getRed())*weight));
		int g0 = (int)Math.abs(Math.round(c1.getGreen()+(c2.getGreen()-c1.getGreen())*weight));
		int b0 = (int)Math.abs(Math.round(c1.getBlue()+(c2.getBlue()-c1.getBlue())*weight));
		Color c = new Color(r0,g0,b0);
		return c;
	}
	
	private MMVectorField2DOverR2IF getRealMasterObject() {
		return (MMVectorField2DOverR2IF) m_masterMMObject;
	}

	//returns the current arrow
	private G2DLineDrawable getDrawable() {
		return (G2DLineDrawable) m_allDrawables[currentArrow];
	}

	public void draw() {
		if(getRealMasterObject().isVisible()&&getRealMasterObject().isVectorsVisible()){
			Color originalObjectColor = getRealMasterObject().getDisplayProperties().getObjectColor();
			Color originalBorderColor = getRealMasterObject().getDisplayProperties().getBorderColor();
			for (int i = 0; i < m_nAllArrows; i++)
				//color visualization
				if(getRealMasterObject().isVisualizeByColor()&&getRealMasterObject().isVisible()){
					LineDisplayProperties prop = (LineDisplayProperties) getRealMasterObject().getDisplayProperties();
					prop.setObjectColor(currentArrowColor[i]);
//					prop.setBorderColor(currentArrowColor[i]);
					((G2DLineDrawable) m_allDrawables[i]).draw(getRealMasterObject().getCanvas(),prop);
				} else ((G2DLineDrawable) m_allDrawables[i]).draw(getRealMasterObject());
			getRealMasterObject().getDisplayProperties().setObjectColor(originalObjectColor);
			getRealMasterObject().getDisplayProperties().setBorderColor(originalBorderColor);
		}
		//vector field lines
		if(getRealMasterObject().isVisible()&&getRealMasterObject().isFieldLinesVisible()){
			for(int i=0;i<m_nAllFieldLines;i++){
				((G2DPolygonDrawable) m_additionalDrawables[i]).draw(getRealMasterObject().getCanvas(),getFieldLinesProperties());
			}
		}
	}

	
	public void render() {
		if(getRealMasterObject().isVisible())synchronizeMath2Screen();
		if(getRealMasterObject().isVisible()&&getRealMasterObject().isVectorsVisible()){
			if (getRealMasterObject().isVisualizeByWidth()) {
				LineDisplayProperties prop =
					(LineDisplayProperties) getRealMasterObject().getDisplayProperties();
				double originalWidth = prop.getLineWidth();
				for (int i = 0; i < m_nAllArrows; i++) {
					prop.setLineWidth(currentArrowWidth[i]);
					((G2DLineDrawable) m_allDrawables[i]).render(prop);
				}
				prop.setLineWidth(originalWidth);
			}else for (int i = 0; i < m_nAllArrows; i++) {
				((G2DLineDrawable) m_allDrawables[i]).render(getProperties());
			}
		}
		//vector field lines
		if(getRealMasterObject().isVisible()&&getRealMasterObject().isFieldLinesVisible()){
			for(int i=0;i<m_nAllFieldLines;i++){
				((G2DPolygonDrawable) m_additionalDrawables[i]).render(getFieldLinesProperties());
			}
		}
	}

	public void renderFromWorld() {
		render();
	}

	public LineDisplayProperties getProperties() {
		return (LineDisplayProperties) getRealMasterObject().getDisplayProperties();
	}
	
	public PolygonDisplayProperties getFieldLinesProperties() {
		return (PolygonDisplayProperties) getRealMasterObject().getFieldLinesDisplayProperties();
	}
}
