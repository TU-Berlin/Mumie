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

package net.mumie.mathletfactory.appletskeleton.j3d;

import java.awt.BorderLayout;

import net.mumie.mathletfactory.appletskeleton.SideBySideCanvasApplet;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.util.AffineDouble;

/**
 * Abstract skeleton class for a 3D-Applet with 2
 * {@link net.mumie.mathletfactory.display.MM3DCanvas MM3DCanvas}
 * aligned in a <i>row</i> and one
 * {@link net.mumie.mathletfactory.appletskeleton.util.ControlPanel ControlPanel}
 * below.
 * 
 * @author Gronau 
 * @mm.docstatus finished
 */

public abstract class SideBySideJ3DCanvasApplet
  extends SideBySideCanvasApplet {

  private double[] m_worldPositionLeft = new double[]{0,-2,0}, 
                   m_worldDirectionLeft = new double[]{0,1,0}, 
                   m_worldUpwardsLeft = new double[]{0,0,1}, 
                   m_worldPositionRight = new double[]{0,-2,0}, 
                   m_worldDirectionRight = new double[]{0,1,0}, 
                   m_worldUpwardsRight = new double[]{0,0,1};
    
  /** Adds canvas components and parses parameters. */
  public void init() {
    super.init();
    m_leftCanvas = new MMJ3DCanvas();
    m_rightCanvas = new MMJ3DCanvas();
    m_leftCanvasPane.add(m_leftCanvas, BorderLayout.CENTER);
    m_rightCanvasPane.add(m_rightCanvas, BorderLayout.CENTER);
    // to prevent some nasty resize bugs:
    m_leftCanvasPane.addComponentListener(m_leftCanvas);
    m_rightCanvasPane.addComponentListener(m_rightCanvas);
    m_canvasSplitPane.addComponentListener(m_leftCanvas);
    m_canvasSplitPane.addComponentListener(m_rightCanvas);
    addComponentListener(m_leftCanvas);
    addComponentListener(m_rightCanvas);
    getCanvasParameters();
  }

  /** Parses parameters that set the world dimensions. */
  public void getCanvasParameters(){
    try {
      if(getParameter("worldPositionLeft") != null)
         m_worldPositionLeft = AffineDouble.parseArray(getParameter("worldPositionLeft"));
      if(getParameter("worldDirectionLeft") != null)
         m_worldDirectionLeft = AffineDouble.parseArray(getParameter("worldDirectionLeft"));
      if(getParameter("worldUpwardsLeft") != null)
         m_worldUpwardsLeft = AffineDouble.parseArray(getParameter("worldUpwardsLeft"));
      if(getParameter("worldPositionRight") != null)
         m_worldPositionRight = AffineDouble.parseArray(getParameter("worldPositionRight"));
      if(getParameter("worldDirectionRight") != null)
         m_worldDirectionRight = AffineDouble.parseArray(getParameter("worldDirectionRight"));
      if(getParameter("worldUpwardsRight") != null)
         m_worldUpwardsRight = AffineDouble.parseArray(getParameter("worldUpwardsRight"));
      }catch(Exception e){}
    adjustWorld2WorldView();
  }

  /** Updates the viewer position and direction. */
  public void adjustWorld2WorldView(){
    getLeftCanvas3D().setLookAt(m_worldPositionLeft, m_worldDirectionLeft, m_worldUpwardsLeft);    
    getRightCanvas3D().setLookAt(m_worldPositionRight, m_worldDirectionRight, m_worldUpwardsRight);    
  }


  
  public MMJ3DCanvas getLeftCanvas3D() {
    return (MMJ3DCanvas) getLeftCanvas();
  }

  public MMJ3DCanvas getRightCanvas3D() {
    return (MMJ3DCanvas) getRightCanvas();
  }
  
  /** Resets the view positions to initial coordinates. */
  public void reset(){
    super.reset();
    getCanvasParameters();
    getLeftCanvas3D().getWorld2Screen().getHistory().clear();
    getLeftCanvas3D().getWorld2Screen().addSnapshotToHistory();
    getRightCanvas3D().getWorld2Screen().getHistory().clear();
    getRightCanvas3D().getWorld2Screen().addSnapshotToHistory();
  }

}
