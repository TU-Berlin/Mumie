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

package net.mumie.mathletfactory.action.handler.global;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;

/**
 * This handler allows the user to "orbit" the viewer around the origin of
 * the world coordinate system (thus virtually rotating it) by dragging the 
 * mouse with the left button pressed.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class GlobalMouse3DRotateHandler extends Global3DHandler {
  
  
  private final MMEvent[] m_events;
  
  private boolean m_firstValuesUnset = true;
  private boolean m_drawWhileRotate = true;
  
  private int m_xPixOld;
  private int m_yPixOld;
  private int m_xPixNew;
  private int m_yPixNew;
  
  private double[] m_direction = new double[3];
  private double[] m_transformedDirection = new double[3];
  private Affine3DDouble m_rotation = new Affine3DDouble(), m_result = new Affine3DDouble();
  
  private double    m_lambda=0.5; // changes the feeling for the rotation :-)
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalMouse3DRotateHandler(MM3DCanvas aCanvas) {
    super(aCanvas);
    m_events = new MMEvent[2];
    m_events[0] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        MMEvent.NO_KEY,
        //InputEvent.CTRL_DOWN_MASK);
        MMEvent.NO_MODIFIER_SET);
    m_events[1] = new MMEvent(MouseEvent.MOUSE_DRAGGED,
        MouseEvent.BUTTON1_MASK,
        0,
        KeyEvent.VK_R,
        //InputEvent.CTRL_DOWN_MASK);
        MMEvent.NO_MODIFIER_SET);
  }
  
  /**
   * Resets the reference point for measuring the distance dragged
   * and adds a snapshot to the history of the canvas.    
   */
  public void finish() {
    getCanvas3D().getWorld2WorldView().addSnapshotToHistory();
    super.finish();
    m_firstValuesUnset = true;
  }
  
  /**
   * Calculates the distance dragged and uses it as angle for rotating. The 
   * (world draw coordinate) axis for the rotation is the vector that is 
   * orthogonal to the direction dragged and that lies in the view plane.  
   * @see net.mumie.mathletfactory.action.handler.global.MMGlobalHandler#doAction(MMEvent)
   */
  public boolean doAction(MMEvent event) {
    //System.out.println("GlobalMouse3DRotateHandler::doAction called!");
    Affine3DDouble afg = getCanvas3D().getWorld2WorldView();
    
    if( m_firstValuesUnset ) {
      m_xPixOld = getCanvas().getController().getLastPressedX();
      m_yPixOld = getCanvas().getController().getLastPressedY();
      m_firstValuesUnset = false;
    }
    m_xPixNew = event.getX();
    m_yPixNew = event.getY();
    
    m_direction[0] = (double)(m_yPixOld - m_yPixNew) / m_canvas.getWidth();
    m_direction[1] = (double)(m_xPixOld - m_xPixNew) / m_canvas.getHeight();
    m_direction[2] = 0;
    double norm = Affine3DDouble.standardNorm(m_direction);
    
    // simple calculation of the rotation angle:
    double angle = m_lambda*2*Math.PI*norm;
    Affine3DDouble.scale(m_direction, 1./norm);
    if(Double.isNaN(m_direction[0]) || Double.isNaN(m_direction[1])
      || m_direction[0] == 0 || m_direction[1] == 0){
      return true;
    }
    afg.applyDeformationPartTo(m_direction, m_transformedDirection);
    m_rotation.setToRotation(m_transformedDirection, angle);
   
    Affine3DDouble.compose(m_result, m_rotation, afg);
    afg.set(m_result);
    getCanvas3D().setWorld2WorldView(afg);
    if( m_drawWhileRotate ) {
      m_canvas.renderFromWorldDraw();
      m_canvas.repaint();
    }
    
    m_xPixOld = m_xPixNew;
    m_yPixOld = m_yPixNew;
    
    /*
    double[] coords = new double[3];
    m_canvas.getRealScreen2WorldDrawFast().applyTo(new double[]{m_xPixNew, m_yPixNew,0.}, coords);
    System.out.println("m_realScreen2WorldDrawFast says: ("+coords[0]+","+coords[1]+","+coords[2]);
    
                  
    NumberTuple p = new NumberTuple(MDouble.class, m_xPixNew, m_yPixNew, 0);
    //m_canvas.getRealScreen2NormalizedTrafo().applyTo(p);
    //m_canvas.getNormalizedScreen2WorldDrawTrafo().applyTo(p);
    m_canvas.getRealScreen2WorldDrawTrafo().applyTo(p);
    System.out.println("while m_realScreen2WorldDraw says : ("+p.getEntryRef(1).getDouble()+","
                      +p.getEntryRef(2).getDouble()+","+p.getEntryRef(3).getDouble()+")");
     */
    return true;
    /*
    Point3d point = new Point3d();
    ((Canvas3D)m_canvas.getDrawingBoard()).getPixelLocationInImagePlate(m_xPixNew, m_yPixNew, point);
    Transform3D imagePlateToVWorld = new Transform3D();
    ((Canvas3D)m_canvas.getDrawingBoard()).getImagePlateToVworld(imagePlateToVWorld);
    imagePlateToVWorld.transform(point);
    System.out.println("mouse pointer at ("+m_xPixNew+","+m_yPixNew+"), which is "+point);
    
    NumberTuple p = new NumberTuple(MDouble.class, m_xPixNew, m_yPixNew, 0);
    //m_canvas.getRealScreen2NormalizedTrafo().applyTo(p);
    //m_canvas.getNormalizedScreen2WorldDrawTrafo().applyTo(p);
    m_canvas.getRealScreen2WorldDrawTrafo().applyTo(p);
    System.out.println("while m_realScreen2NormalizedTrafo says: "+p);
    //if(true) return true;
     */
    
  }
  
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    for(int i=0; i<m_events.length; i++)
      if(m_events[i].equals(anEvent))
        return true;
    return false;
  }
  
}

