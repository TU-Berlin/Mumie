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

import java.util.Enumeration;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Color3f;

/**
 *  This class implements the flashing behavior for selected
 *  {@link J3DDrawable}s.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class SelectionBehavior extends Behavior
{
  public static final int SELECTION_POSTID = 123;
  private ColoringAttributes m_coloringAttributes;
  private Appearance m_appearance = new Appearance();
  private Color3f m_color = new Color3f(), m_newColor = new Color3f();
  private double m_time = 0;

 
  /**
   *  Creates the selection behavior with the specified appearance.
   *  Whenever a drawable is selected, the appearance will be 
   *  transferred onto this drawable.
   * 
   *  @param appearance
   */
  public SelectionBehavior(Appearance appearance){
    m_appearance = appearance;
    m_coloringAttributes = new ColoringAttributes();
    m_coloringAttributes.setColor(0.8f,0,0);
    m_appearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_READ);
    m_coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
  }
  
  /**
   * state the Java3D wakeup criterion,
   * @see javax.media.j3d.Behavior#initialize()
   */
  public void initialize(){
    wakeupOn(new WakeupOnBehaviorPost(null, SELECTION_POSTID));
  }
  
  public void setColor(ColoringAttributes colorAtts){
    colorAtts.getColor(m_color);
  }
  
  /**
   * Sets the color flashing of the appearance specified in the constructor. 
   * @see javax.media.j3d.Behavior#processStimulus(Enumeration)
   */
  public void processStimulus(Enumeration criteria){
    //m_appearance.getColoringAttributes().getColor(m_color);
    //m_coloringAttributes.setColor(m_color);
    m_time = System.currentTimeMillis() / 600;
    m_newColor.set(m_color);
    //System.out.println("before: "+m_color);
    float arg = 0.8f * (float)Math.sin(m_time);
    m_newColor.x += arg*arg;
    m_newColor.y += arg*arg;
    m_newColor.z += arg*arg;
    //System.out.println("after: "+m_newColor);
    m_coloringAttributes.setColor(m_newColor);
    m_appearance.setColoringAttributes(m_coloringAttributes);
    wakeupOn(new WakeupOnBehaviorPost(null, SELECTION_POSTID));
    postId(SELECTION_POSTID);
  }
}

