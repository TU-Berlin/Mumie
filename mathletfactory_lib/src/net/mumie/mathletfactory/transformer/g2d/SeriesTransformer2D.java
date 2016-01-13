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
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMSeriesDefByOp;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This transformer displays a Series as a collection of points on a 2D plane.
 * Lines can be drawn parallel to x und y axis. Whether lines and point are drawn
 * or not, depends on the display properties for them. If the property for the
 * point (line) is <code>null</code> the point (line) is not drawn.
 * By default point and lines from point to x axis are drawn.
 *   
 * @author Paehler
 * @mm.docstatus finished
 */
public class SeriesTransformer2D extends Canvas2DObjectTransformer {
  private LineDisplayProperties m_xLineProperties;
  private boolean m_autoBounds;
  private int m_manualBoundsFrom;
  private int m_manualBoundsTo;
  private int m_boundsFrom;
  private int m_boundsTo;

  public SeriesTransformer2D() {
    m_autoBounds = false;
    m_manualBoundsFrom = 1;
    m_manualBoundsTo = 30;

    m_xLineProperties = new LineDisplayProperties();
    m_xLineProperties.setLineWidth(5);
    m_xLineProperties.setBorderWidth(0);
    m_xLineProperties.setObjectColor(Color.RED);
    m_xLineProperties.setFilled(true);
    m_xLineProperties.setArrowAtStart(LineDisplayProperties.LINE_END);
    m_xLineProperties.setArrowAtEnd(LineDisplayProperties.LINE_END);    
  }
  
  protected int drawablesPerValue() {
    int result = 0; 
    if (m_xLineProperties != null) result++; 
    return result; 
  }
  
  private int fillArrays(int offset, int length, Class drawableClass, DisplayProperties properties) {
    if (properties == null)
      return offset;

    try {      
      for (int i = offset; i < length + offset; i++) {
        m_additionalDrawables[i] = (CanvasDrawable)drawableClass.newInstance();
        m_additionalProperties[i] = properties;
      }
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return offset + length; 
  }
  
  protected void setPropertiesFromObject() {
    DisplayProperties objectProperties = getMaster().getDisplayProperties();
    if (objectProperties == null)
      return;
    if (m_xLineProperties != null) {
      objectProperties.copyPropertiesInto(m_xLineProperties); 
    }
    getMaster().setDisplayProperties(objectProperties);
  }

  protected void initDrawables(int length) {
    int drawablesPerValue = drawablesPerValue();
    m_additionalDrawables = new CanvasDrawable[drawablesPerValue*length]; 
    m_additionalProperties = new DisplayProperties[m_additionalDrawables.length];
    int offset = 0;
    setPropertiesFromObject();    
    offset = fillArrays(offset, length, G2DLineDrawable.class, m_xLineProperties);
  }
  
  public void synchronizeMath2Screen() {
    synchronizeWorld2Screen();
  }
  
  private class EdgeDescriptor {
    public EdgeDescriptor(int coordinateIndex) {
      this.coordinateIndex = coordinateIndex;
    }
    private static final int X_COORDINATE = 0;
    private static final int Y_COORDINATE = 1;
    double min;
    double max;
    double worldDistance;
    double screenDistance;
    int coordinateIndex;
  }
  private void getWorldEdgesOfAxis(EdgeDescriptor descriptor) {
    
    Rectangle rect = getMasterAsCanvasObject().getCanvas().getClientRect();
    
    double[] axisFrom = new double[2]; 
    double[] axisTo = new double[2];
    double[] intersection = new double[4];
    double[] center = new double[2];
    
    screen2World(rect.getWidth()/2, rect.getHeight()/2, center);
    
    world2Screen(center[0], center[1], axisFrom);
    if (descriptor.coordinateIndex == EdgeDescriptor.X_COORDINATE)
      world2Screen(center[0] + 1, center[1], axisTo);
    else
      world2Screen(center[0], center[1] + 1, axisTo);

    if (Graphics2DHelper.lineRectIntersection2D(
        new double[] {axisFrom[0], axisFrom[1], axisTo[0], axisTo[1]}, 
        new double[] {rect.x, rect.y, rect.x + rect.width, rect.y + rect.height},
        intersection) == 2) {
      double[] intersect1 = new double[] {intersection[0], intersection[1]};
      double[] intersect2 = new double[] {intersection[2], intersection[3]};
      descriptor.screenDistance = Graphics2DHelper.pointDistance(
          intersect1[0], intersect1[1],
          intersect2[0], intersect2[1]);
      screen2World(new double[] {intersection[0], intersection[1]} , intersect1); 
      screen2World(new double[] {intersection[2], intersection[3]} , intersect2);
      descriptor.worldDistance = Graphics2DHelper.pointDistance(
          intersect1[0], intersect1[1],
          intersect2[0], intersect2[1]);
      descriptor.min = intersect1[descriptor.coordinateIndex];      
      descriptor.max = intersect2[descriptor.coordinateIndex];      
      if (axisTo[descriptor.coordinateIndex] < axisFrom[descriptor.coordinateIndex]) {
        double tmp = descriptor.min; descriptor.min = descriptor.max; descriptor.max = tmp;
      }
    }
  }

  public void synchronizeWorld2Screen() {
    EdgeDescriptor xDescriptor = new EdgeDescriptor(EdgeDescriptor.X_COORDINATE);
    EdgeDescriptor yDescriptor = new EdgeDescriptor(EdgeDescriptor.Y_COORDINATE);
    
    getWorldEdgesOfAxis(xDescriptor);
    xDescriptor.min = Math.max(xDescriptor.min, 0);
    xDescriptor.max = Math.max(xDescriptor.max, 0);
    getWorldEdgesOfAxis(yDescriptor);
    int[] bounds = new int[2];
    
    if (isAutoBounds()) {
      bounds = null;
      if (bounds != null) {
        if (bounds[0] > xDescriptor.min)
          bounds[0] = (int)xDescriptor.min;
        if (bounds[1] < xDescriptor.max)
          bounds[1] = (int)xDescriptor.max;
      }
    } else {
      bounds[0] = getBoundsFrom();
      bounds[1] = getBoundsTo();
    }
    if (bounds != null) {
      initDrawables(bounds[1]-bounds[0] + 1);
      
      int pointsToDraw = m_additionalDrawables.length/drawablesPerValue();
      int yOffset = m_xLineProperties == null ? 0 : pointsToDraw;
      //int yOffset = pointProperties == null ? 0 : pointsToDraw;
      //int xOffset = yOffset + (yLineProperties == null ? 0 : pointsToDraw);
      Rectangle rect = getMasterAsCanvasObject().getCanvas().getClientRect(); 
      
      for (int i = 0; i < pointsToDraw; i++) {
        double[] coord = new double[2];
        double[] screenCoordPoint = new double[2];
        double[] screenCoordAxis = new double[2];
        MMSeriesDefByOp s = getMasterObject();
        MNatural x_n = new MNatural();
        coord[0] = bounds[0] + i;
        x_n.setDouble(coord[0]);
        coord[1] = s.getSum(x_n).getDouble();
        if (Double.isInfinite(coord[1]) || Double.isNaN(coord[1]))
          continue;
        world2Screen(coord, screenCoordPoint);
        if (m_xLineProperties != null) {
          coord[0] = bounds[0] + i;
          coord[1] = 0;
          world2Screen(coord, screenCoordAxis);
          //G2DLineDrawable line = (G2DLineDrawable)m_additionalDrawables[i+xOffset];
          G2DLineDrawable line = (G2DLineDrawable)m_additionalDrawables[i];
          line.setPoints(screenCoordAxis[0], screenCoordAxis[1], screenCoordPoint[0], screenCoordPoint[1]);
        }
      }
    } else {
      initDrawables(0);
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
  
  public MMSeriesDefByOp getMasterObject() {
    return (MMSeriesDefByOp)m_masterMMObject;
  }
  /**
    * @return
    */
   public boolean isAutoBounds() {
     return m_autoBounds;
   }

   /**
    * @return
    */
   public int getBoundsFrom() {
     return isAutoBounds()? m_boundsFrom : m_manualBoundsFrom;
   }

   /**
    * @return
    */
   public int getBoundsTo() {
     return isAutoBounds()? m_boundsTo : m_manualBoundsTo;
   }

   /**
    * @param b
    */
   public void setAutoBounds(boolean b) {
     m_autoBounds = b;
   }

   /**
    * @param i
    */
   public void setBoundsFrom(int i) {
     if(i > m_boundsTo)
       i = m_boundsTo;
     m_boundsFrom = i;
     if (!isAutoBounds())
       m_manualBoundsFrom = i;
   }

   /**
    * @param i
    */
   public void setBoundsTo(int i) {
     if(i < m_boundsFrom)
       i = m_boundsFrom;  
     m_boundsTo = i;
     if (!isAutoBounds())
       m_manualBoundsTo = i;
   }

}
