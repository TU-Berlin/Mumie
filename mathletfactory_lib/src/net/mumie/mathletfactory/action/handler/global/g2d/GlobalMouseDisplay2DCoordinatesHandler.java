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

package net.mumie.mathletfactory.action.handler.global.g2d;

import java.awt.event.MouseEvent;

import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.global.Global2DHandler;
import net.mumie.mathletfactory.display.MM2DCanvas;
import net.mumie.mathletfactory.util.CanvasMessage;

/**
 * This handler draws the world coordinates of the screen point on which the mouse pointer rests.   
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public class GlobalMouseDisplay2DCoordinatesHandler extends Global2DHandler {
  
  private final double[] m_javaScreen = new double[2];
  private final double[] m_worldDraw = new double[2];
  private CanvasMessage m_coordsMessage;
  
  /** Constructs the handler to work in the given canvas. */
  public GlobalMouseDisplay2DCoordinatesHandler(MM2DCanvas aCanvas) {
    super(aCanvas);
    m_coordsMessage = new CanvasMessage(aCanvas);
    aCanvas.addMessage(m_coordsMessage);
  }
  
  
  public boolean doAction(MMEvent anMMEvent) {
    if(!getCanvas().getShowMouseCoordinates()) {
      m_coordsMessage.setMessage(null);
      return false;
    }
    int xPix = anMMEvent.getX();
    int yPix = anMMEvent.getY();
    m_javaScreen[0] = xPix;
    m_javaScreen[1] = yPix;
    
    getCanvas2D().getWorldFromScreen(m_javaScreen,m_worldDraw);
    
    double xWorld = (double)Math.round(100*m_worldDraw[0])/100.;
    double yWorld = (double)Math.round(100*m_worldDraw[1])/100.;
    
//    Graphics2D gr2d = ((MMG2DCanvas)getCanvas()).getGraphics2D();
//    Graphics gr = getCanvas().getGraphics();
//    gr.setColor(Color.red);
    String coordsString = "("+xWorld+ ", "+ yWorld+ ")";
    m_coordsMessage.setMessage(coordsString);
    getCanvas().repaint();
    
//    
//    FontRenderContext frc = gr2d.getFontRenderContext();
//    Rectangle2D bounds = gr2d.getFont().getStringBounds(coordsString,frc);
//    float width = (float)bounds.getWidth();
//    float height = (float)bounds.getHeight();
//
//    int dx=(int)width,dy=(int)height;
//    int drawX = 0, drawY = 0;
//    
//    if (getCanvas().isInCanvasQuarter(xPix,yPix) == getCanvas().CANVAS_UPPER_RIGHT_QUARTER){
//      drawX = xPix-dx;
//      drawY = yPix+dy;
//    }
//    else if(getCanvas().isInCanvasQuarter(xPix,yPix) == getCanvas().CANVAS_UPPER_LEFT_QUARTER) {
//      drawX = xPix;
//      drawY = yPix+dy;
//    }
//    else if(getCanvas().isInCanvasQuarter(xPix,yPix) == getCanvas().CANVAS_LOWER_LEFT_QUARTER) {
//      drawX = xPix;
//      drawY = yPix;
//    }
//    else {
//      drawX = xPix-dx;
//      drawY = yPix;
//    }
//
//    gr.drawString(coordsString, drawX,drawY);
    return false;
  }
  
  public void finish() {}
    
  public boolean userDefinedDealsWith(MMEvent anEvent) {
    boolean resp = anEvent.getEventType() == MouseEvent.MOUSE_MOVED;
    
    if( resp )
      return true;
    else
      return false;
  }
  
}

