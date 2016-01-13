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

package net.mumie.mathletfactory.appletskeleton.g2d;

import java.awt.BorderLayout;

import net.mumie.mathletfactory.appletskeleton.UpperLowerCanvasApplet;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;

/**
 * Abstract skeleton class for a 2D-Applet with 2
 * {@link net.mumie.mathletfactory.display.MM2DCanvas MM2DCanvas} aligned in a <i>column</i>
 * and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * below.
 * 
 * @author Gronau 
 * @mm.docstatus finished
 */

public abstract class UpperLowerG2DCanvasApplet
  extends UpperLowerCanvasApplet {
  
  /** The world coordinates that are at the center of the Upper canvas. */
  private double m_worldCenterXUpper = 0, m_worldCenterYUpper = 0;
  
  /** The world coordinates that are at the center of the Lower canvas. */
  private double m_worldCenterXLower = 0, m_worldCenterYLower = 0;
  
  /** The width and height in world coordinates that are displayed by the Upper canvas. */
  private double  m_worldWidthUpper = 1, m_worldHeightUpper = 1;
  
  /** The width and height in world coordinates that are displayed by the Upper canvas. */
  private double  m_worldWidthLower = 1, m_worldHeightLower = 1;
    
  public void init() {
    super.init();
    m_upperCanvas = new MMG2DCanvas(MMG2DCanvas.VERTICAL_TOOLBAR);
    m_lowerCanvas = new MMG2DCanvas(MMG2DCanvas.VERTICAL_TOOLBAR);
    m_upperCanvasPane.add(m_upperCanvas, BorderLayout.CENTER);
    m_lowerCanvasPane.add(m_lowerCanvas, BorderLayout.CENTER);
  }

  public MMG2DCanvas getUpperCanvas2D() {
    return (MMG2DCanvas) getUpperCanvas();
  }

  public MMG2DCanvas getLowerCanvas2D() {
    return (MMG2DCanvas) getLowerCanvas();
  }
  
  
  /** parse parameters that set the world dimensions. */
  public void getCanvasParameters(){
    try {
      if(getParameter("worldWidthUpper") != null)
         m_worldWidthUpper = 1.2 * Double.parseDouble(getParameter("worldWidthUpper"));
      if(getParameter("worldHeightUpper") != null)
         m_worldHeightUpper = 1.2 * Double.parseDouble(getParameter("worldHeightUpper"));
      if(getParameter("worldCenterXUpper") != null)
         m_worldCenterXUpper = Double.parseDouble(getParameter("worldCenterXUpper"));
      if(getParameter("worldCenterYUpper") != null)
         m_worldCenterYUpper = Double.parseDouble(getParameter("worldCenterYUpper"));
      if(getParameter("worldWidthLower") != null)
         m_worldWidthLower = 1.2 * Double.parseDouble(getParameter("worldWidthLower"));
      if(getParameter("worldHeightLower") != null)
         m_worldHeightLower = 1.2 * Double.parseDouble(getParameter("worldHeightLower"));
      if(getParameter("worldCenterXLower") != null)
         m_worldCenterXLower = Double.parseDouble(getParameter("worldCenterXLower"));
      if(getParameter("worldCenterYLower") != null)
         m_worldCenterYLower = Double.parseDouble(getParameter("worldCenterYLower"));
      }catch(Exception e){}
    adjustWorld2Screen();
  }
 
  /** Sets the world to screen maps according to the given dimensions. */
  public void adjustWorld2Screen(){
    getUpperCanvas2D().getW2STransformationHandler().setWorldCentreCoordinates(m_worldCenterXUpper, m_worldCenterYUpper);
    if(m_worldWidthUpper == m_worldHeightUpper){
      //System.out.println("width is "+getWorldWidth()+", height is "+getWorldHeight());
      getUpperCanvas2D().getW2STransformationHandler().setUniformWorldDim(m_worldWidthUpper);
    } else
      getUpperCanvas2D().getW2STransformationHandler().setWorldDim(m_worldWidthUpper, m_worldHeightUpper);
    getLowerCanvas2D().getW2STransformationHandler().setWorldCentreCoordinates(m_worldCenterXLower, m_worldCenterYLower);
    if(m_worldWidthLower == m_worldHeightLower){
      //System.out.println("width is "+getWorldWidth()+", height is "+getWorldHeight());
      getLowerCanvas2D().getW2STransformationHandler().setUniformWorldDim(m_worldWidthLower);
    } else
      getLowerCanvas2D().getW2STransformationHandler().setWorldDim(m_worldWidthLower, m_worldHeightLower);
  }

  /** Calls super method and resets the history of the canvases. */
  public void reset() {
    super.reset();
    getCanvasParameters();
    getUpperCanvas2D().getWorld2Screen().getHistory().clear();
    getUpperCanvas2D().getWorld2Screen().addSnapshotToHistory();
    getLowerCanvas2D().getWorld2Screen().getHistory().clear();
    getLowerCanvas2D().getWorld2Screen().addSnapshotToHistory();
    getUpperCanvas2D().renderScene();
    getLowerCanvas2D().renderScene();
    getUpperCanvas2D().repaint();
    getLowerCanvas2D().repaint();
  }
  
  /**
   * Returns the x world coordinate that is at the center of the upper canvas.
   */    
  public double getWorldCenterXUpper() {
    return m_worldCenterXUpper;
  }

  /**
   * Returns the x world coordinate that is at the center of the lower canvas.
   */    
  public double getWorldCenterXLower() {
    return m_worldCenterXLower;
  }

  /**
   * Returns the y world coordinate that is at the center of the lower canvas.
   */    
  public double getWorldCenterYUpper() {
    return m_worldCenterYUpper;
  }

  /**
   * Returns the y world coordinate that is at the center of the lower canvas.
   */    
  public double getWorldCenterYLower() {
    return m_worldCenterYLower;
  }

  /**
   * Returns the height in world coordinates that is displayed by the upper canvas.
   */
  public double getWorldHeightUpper() {
    return m_worldHeightUpper;
  }

  /**
   * Returns the height in world coordinates that is displayed by the lower canvas.
   */
  public double getWorldHeightLower() {
    return m_worldHeightLower;
  }

  /**
   * Returns the width in world coordinates that is displayed by the upper canvas.
   */
  public double getWorldWidthUpper() {
    return m_worldWidthUpper;
  }

  /**
   * Returns the width in world coordinates that is displayed by the lower canvas.
   */
  public double getWorldWidthLower() {
    return m_worldWidthLower;
  }

  /**
   * Sets the x world coordinate that is at the center of the upper canvas.
   */
  public void setWorldCenterXUpper(double d) {
    m_worldCenterXUpper = d;
  }

  /**
   * Sets the x world coordinate that is at the center of the lower canvas.
   */
  public void setWorldCenterXLower(double d) {
    m_worldCenterXLower = d;
  }

  /**
   * Sets the y world coordinate that is at the center of the upper canvas.
   */
  public void setWorldCenterYUpper(double d) {
    m_worldCenterYUpper = d;
  }

  /**
   * Sets the y world coordinate that is at the center of the lower canvas.
   */
  public void setWorldCenterYLower(double d) {
    m_worldCenterYLower = d;
  }

  /**
   * Sets the height in world coordinates that is displayed by the upper canvas.
   */
  public void setWorldHeightUpper(double d) {
    m_worldHeightUpper = d;
  }

  /**
   * Sets the height in world coordinates that is displayed by the lower canvas.
   */
  public void setWorldHeightLower(double d) {
    m_worldHeightLower = d;
  }

  /**
   * Sets the width in world coordinates that is displayed by the upper canvas.
   */
  public void setWorldWidthUpper(double d) {
    m_worldWidthUpper = d;
  }

  /**
   * Sets the width in world coordinates that is displayed by the lower canvas.
   */
  public void setWorldWidthLower(double d) {
    m_worldWidthLower = d;
  }
}
