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
import java.awt.Rectangle;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;

/**
 * This transformer displays a Sequence as a collection of points on a line. 
 * Its displayed on a Graphics2DCanvans. The points color moves from white (for
 * the smallest sequence value in domain) to the color given in 
 * <code>DisplayProperties</code> (for the largest sequence value in domain).   
 * 
 *  @author Amsel
 *  @mm.docstatus finished
 */
public class SequenceTransformer1D extends AbstractSequenceTransformer {
  private PointDisplayProperties m_properties;
  
  public SequenceTransformer1D() {
    m_properties = new PointDisplayProperties();
    m_properties.setBorderColor(Color.BLACK);
    m_properties.setBorderWidth(1);
    m_properties.setPointRadius(4);
    m_properties.setObjectColor(Color.CYAN);
  }
  
  protected void initDrawables(int length) {
    m_additionalDrawables = new CanvasDrawable[length]; 
    m_additionalProperties = new DisplayProperties[length];
    for (int i = 0; i < m_additionalDrawables.length; i+=2) {
      m_additionalDrawables[i] = new G2DPointDrawable();
    }
  }
      
  public void synchronizeMath2Screen() {
    synchronizeWorld2Screen();
  }
  
  public void synchronizeWorld2Screen() {
    Color objectColor = getProperties().getObjectColor();
    float[] hsv = Color.RGBtoHSB(
        objectColor.getRed(), 
        objectColor.getGreen(), 
        objectColor.getBlue(), null);
    if (isAutoBounds()) {
      Rectangle rect = getMasterAsCanvasObject().getCanvas().getClientRect();
      double[] origin = new double[2];
      double[] xUnit = new double[2];
      world2Screen(0, 0, origin);
      world2Screen(1, 0, xUnit);
      double[] intersection = new double[4];

      if (Graphics2DHelper.lineRectIntersection2D(
          new double[] {origin[0], origin[1], xUnit[0], xUnit[1]}, 
          new double[] {rect.x, rect.y, rect.x + rect.width, rect.y + rect.height},
          intersection) == 2) {
        double[] intersect1 = new double[] {intersection[0], intersection[1]};
        double[] intersect2 = new double[] {intersection[2], intersection[3]};
        double screenLength = Graphics2DHelper.pointDistance(
            intersect1[0], intersect1[1],
            intersect2[0], intersect2[1]);
        screen2World(new double[] {intersection[0], intersection[1]} , intersect1); 
        screen2World(new double[] {intersection[2], intersection[3]} , intersect2);
        double worldLength = Graphics2DHelper.pointDistance(
            intersect1[0], intersect1[1],
            intersect2[0], intersect2[1]);
        double fMin = intersect1[1];      
        double fMax = intersect2[1];      
        if (xUnit[1] > origin[1]) {
          double tmp = fMin; fMin = fMax; fMax = tmp;
        }
      
        int[] bounds = getMasterObject().getBounds(fMin, fMax, 10*worldLength/screenLength, null);
        setBoundsFrom(bounds[0]);
        setBoundsTo(bounds[1]);
      }    
    }
    m_additionalDrawables = new CanvasDrawable[getBoundsTo() - getBoundsFrom() + 1];
    m_additionalProperties = new DisplayProperties[m_additionalDrawables.length];
    float stepSize = hsv[1] / (m_additionalDrawables.length);
    for (int i = 0; i < m_additionalDrawables.length-1; i++) {
      m_additionalProperties[i] = (DisplayProperties)(getProperties().clone());
      m_additionalProperties[i].setBorderColor(Color.BLACK);
      m_additionalProperties[i].setObjectColor(Color.getHSBColor(hsv[0], (i)*stepSize, hsv[2]));

      MNatural x_n = new MNatural();
      x_n.setDouble(i + getBoundsFrom());
      double[] screenCoord = new double[2];
      double realValue = 0;
      // in case of complex number class
      double imagValue = 0;
      if(getMasterObject() != null && getMasterObject().getNumberClass() != null && getMasterObject().getNumberClass().isAssignableFrom(MComplex.class)){
        imagValue = ((MComplex)getMasterObject().getSequenceValue(x_n)).getIm();
        realValue = ((MComplex)getMasterObject().getSequenceValue(x_n)).getRe();
      } else
        realValue = getMasterObject().getSequenceValue(x_n).getDouble();
      //System.out.println("value for "+i+1+" is "+realValue);
      if (Double.isNaN(realValue) || Double.isInfinite(realValue) 
          || Double.isNaN(imagValue) || Double.isInfinite(imagValue)) {
        m_additionalDrawables[i] = null;
        continue; 
      }
      
      m_additionalDrawables[i] = new G2DPointDrawable();
      world2Screen(realValue, imagValue, screenCoord);
      ((G2DPointDrawable)m_additionalDrawables[i]).setPoint(screenCoord[0], screenCoord[1]);

    }
  }
  
  public void getMathObjectFromScreen(
    double[] javaScreenCoordinates,
    NumberTypeDependentIF mathObject) {
      throw new TodoException();
  }

  public void getScreenPointFromMath(
    NumberTypeDependentIF entity,
    double[] javaScreenCoordinates) {
      throw new TodoException();
  }

  /**
   * @return
   */
  public DisplayProperties getProperties() {
    return getMaster().getDisplayProperties() == null?
        m_properties:
        getMaster().getDisplayProperties();
  }

}
