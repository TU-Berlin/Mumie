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

package net.mumie.mathletfactory.display.j3d;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.TransformGroup;

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 *  This is the base class for all J3DDrawables, which can be removed from a
 *  live scene graph. This is needed, when an object should be manipulated by
 *  the mouse or other user interaction, while being displayed.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
 
public abstract class J3DRemovableDrawable extends J3DDrawable {
  
  protected BranchGroup m_shape = new BranchGroup();
  protected TransformGroup m_transformGroup = null;
  protected DisplayProperties displayProperties;
  
  /** Called by super class. */
  protected J3DRemovableDrawable(){
    m_shape.setCapability(BranchGroup.ALLOW_DETACH);
  }
  
  /**
   * Rebuilds the shape of this drawable, if necessary, and returns it.
   */
  protected Group getShape(DisplayProperties properties) {
    if(m_rebuildNeeded){
      createShape(properties);
      //System.out.println(this+" created");
    }
    return m_shape;
  }
  
  /**
   *  Sets the transform group that is responsible for position and orientation
   *  of complex shapes.
   */
  protected void setTransformGroup(TransformGroup tg){
    m_transformGroup = tg;
  }
  
  /**
   *  This method is used whenever the shape is built (initial setup as well
   *  as after a reset).
   */
  protected abstract void createShape(DisplayProperties properties);
  
  /**
   *  This method calls {@link #removeShape} and sets a flag for recreation 
   *  in the next {@link #getShape} call and issues a 
   *  <code>getCanvas().repaint()</code>.
   */
  public void resetShape() {
    removeShape();
    m_rebuildNeeded = true;
    getCanvas().repaint();
  }
  
  /**
   *  This method removes the branchgroup from the live scene graph and disconnects
   *  the reusable parts from their children and parents.
   */
  public void removeShape(){
    if(m_shape != null){
      m_shape.detach();
      m_shape.removeAllChildren();
    }
    if(m_transformGroup != null)
       m_transformGroup.removeAllChildren();    
    m_rebuildNeeded = true;
  }
  
  /**
   * Sets the display properties for the last render() call. If they differ from the current, 
   * a rebuilt is needed.
   */
  public DisplayProperties getDisplayProperties() {
    return displayProperties;
  }

  /**
   * Returns the display properties for the last render() call. If they differ from the current, 
   * a rebuilt is needed.
   */
  public void setDisplayProperties(DisplayProperties properties) {
    displayProperties = properties;
  }
  
  /** Additionally checks, whether the display properties have changed. */
  public void render(DisplayProperties props){
    if(!props.equals(getDisplayProperties()))    
      resetShape();    
    super.render(props);
    setDisplayProperties(props);
  }

}



