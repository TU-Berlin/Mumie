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
import net.mumie.mathletfactory.display.MM2DCanvas;

/** A template for writing a handler that allows the creation of points. 
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class CreateHandler2DTemplate extends Global2DHandler {
  
  
  protected MMEvent m_event;

  /** Constructs the handler to work in the given canvas. */
  public CreateHandler2DTemplate(MM2DCanvas aCanvas) {
    super(aCanvas);
    m_event = new MMEvent(MouseEvent.MOUSE_CLICKED,
                          MouseEvent.BUTTON1_MASK,
                          1,
                          KeyEvent.VK_C,
                          MMEvent.NO_MODIFIER_SET);
  }
  
  public boolean userDefinedDealsWith(MMEvent event) {
    return m_event.equals(event);
  }
  
  public boolean doAction(MMEvent event) {
    int xPos = event.getX();
    int yPos = event.getY();
    double[] worldCoords = new double[2];
    getCanvas2D().getScreen2World().applyTo(xPos,yPos,worldCoords);
    pointCreated(worldCoords);
    return true;
  }
  
  protected void pointCreated(double[] worldCoords){
      }
  
  public void finish() {
    getCanvas().renderScene();
    getCanvas().repaint();
  }
  
}


