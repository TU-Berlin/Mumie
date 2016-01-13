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
import net.mumie.mathletfactory.display.PointDisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMSequenceIF;

/**
 * This transformer displays a Sequence as a collection of points on a 2D plane.
 * Lines can be drawn parallel to x und y axis. Whether lines and point are drawn
 * or not, depends on the display properties for them. If the property for the
 * point (line) is <code>null</code> the point (line) is not drawn.
 * By default point and lines from point to x axis are drawn.
 *   
 * @author Amsel
 * @mm.docstatus finished
 */
public class SequenceTransformer2D extends AbstractSequenceTransformer {
  private PointDisplayProperties pointProperties;
  private LineDisplayProperties xLineProperties;
  private LineDisplayProperties yLineProperties;
  
  public SequenceTransformer2D() {
    pointProperties = null; /*new PointDisplayProperties();
    pointProperties.setPointRadius(2);
    pointProperties.setBorderWidth(1);
    pointProperties.setBorderColor(Color.RED);
    pointProperties.setObjectColor(Color.WHITE);
*/
    xLineProperties = new LineDisplayProperties();
    xLineProperties.setLineWidth(4);
    xLineProperties.setBorderWidth(0);
    xLineProperties.setObjectColor(Color.RED);
    xLineProperties.setArrowAtStart(LineDisplayProperties.LINE_END);
    xLineProperties.setArrowAtEnd(LineDisplayProperties.LINE_END);
  }
  
  protected int drawablesPerValue() {
    int result = 0;
    if (pointProperties != null) result++; 
    if (xLineProperties != null) result++; 
    if (yLineProperties != null) result++;
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
    if (pointProperties != null) {
      objectProperties.copyPropertiesInto(pointProperties);
      if (objectProperties instanceof LineDisplayProperties) {
        pointProperties.setPointRadius((int)((LineDisplayProperties)objectProperties).getLineWidth()/2); 
      }
      pointProperties.setObjectColor(Color.WHITE);
    }
    if (xLineProperties != null) {
      objectProperties.copyPropertiesInto(xLineProperties); 
      if (objectProperties instanceof PointDisplayProperties) {
        xLineProperties.setLineWidth(((PointDisplayProperties)objectProperties).getPointRadius()*2); 
      }
    }
    if (yLineProperties != null) {
      objectProperties.copyPropertiesInto(yLineProperties); 
      if (objectProperties instanceof PointDisplayProperties) {
        yLineProperties.setLineWidth(((PointDisplayProperties)objectProperties).getPointRadius()*2); 
      }
    }
  }

  protected void initDrawables(int length) {
    int drawablesPerValue = drawablesPerValue();
    m_additionalDrawables = new CanvasDrawable[drawablesPerValue*length]; 
    m_additionalProperties = new DisplayProperties[m_additionalDrawables.length];
    int offset = 0;
    setPropertiesFromObject();    
    offset = fillArrays(offset, length, G2DLineDrawable.class, xLineProperties);
    offset = fillArrays(offset, length, G2DLineDrawable.class, yLineProperties);
    fillArrays(offset, length, G2DPointDrawable.class, pointProperties);
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
      bounds = getMasterObject().getBounds(yDescriptor.min, yDescriptor.max, 3*yDescriptor.worldDistance/yDescriptor.screenDistance, null);
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
      int yOffset = xLineProperties == null ? 0 : pointsToDraw;
      int pointOffset = yOffset + (yLineProperties == null ? 0 : pointsToDraw);
      //int yOffset = pointProperties == null ? 0 : pointsToDraw;
      //int xOffset = yOffset + (yLineProperties == null ? 0 : pointsToDraw);
      Rectangle rect = getMasterAsCanvasObject().getCanvas().getClientRect(); 
      
      for (int i = 0; i < pointsToDraw; i++) {
        double[] coord = new double[2];
        double[] screenCoordPoint = new double[2];
        double[] screenCoordAxis = new double[2];
        MMSequenceIF s = getMasterObject();
        MNatural x_n = new MNatural();
        coord[0] = bounds[0] + i;
        x_n.setDouble(coord[0]);
        coord[1] = s.getSequenceValue(x_n).getDouble();
        if (Double.isInfinite(coord[1]) || Double.isNaN(coord[1]))
          continue;
        world2Screen(coord, screenCoordPoint);
        if (pointProperties != null) {  
          //G2DPointDrawable point = (G2DPointDrawable)m_additionalDrawables[i];
          G2DPointDrawable point = (G2DPointDrawable)m_additionalDrawables[i+pointOffset];
          point.setPoint(screenCoordPoint);
        }
        if (yLineProperties != null) {
          coord[0] = 0;
          world2Screen(coord, screenCoordAxis);
          G2DLineDrawable line = (G2DLineDrawable)m_additionalDrawables[i+yOffset];
          line.setPoints(screenCoordAxis[0], screenCoordAxis[1], screenCoordPoint[0], screenCoordPoint[1]);
          if (!rect.intersectsLine((int)screenCoordAxis[0], (int)screenCoordAxis[1], (int)screenCoordPoint[0], (int)screenCoordPoint[1])) {
            m_additionalDrawables[i+yOffset] = null;
            System.out.println("nulled");
          }
        }
        if (xLineProperties != null) {
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
  
  public MMSequenceIF getMasterObject() {
    return (MMSequenceIF)m_masterMMObject;
  }

}
