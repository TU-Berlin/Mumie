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

package net.mumie.mathletfactory.action;

import java.awt.AWTEvent;

import javax.swing.event.ChangeEvent;
 
/** 
 *  This class represents the event in the whole MM-System and acts as a
 *  wrapper for AWT-events: Whenever an AWT-event is fired, this will be noticed
 *  by the {@link net.mumie.mathletfactory.action.DefaultCanvasController}, where the
 *  AWT-event is wrapped in an MMEvent and passed over to the central
 *  dispatching method {@link DefaultCanvasController#controlAction
 *  DefaultCanvasController.controlAction()},
 *  which in turn calls the {@link net.mumie.mathletfactory.action.handler.MMHandler#doAction
 *  MMHandler.doAction()}-method.
 * 
 *  @author vossbeck
 *  @mm.docstatus finished 
 */
public class MMEvent{
  
  
  private int     m_eventType;   // type of moving: dragged, clicked, ...
  private int     m_x;           // position.x
  private int     m_y;           // position.y
  private int     m_mouseButton;
  private int     m_clickCount;
  private int			m_mouseWheelRotation;
  private int     m_keyCode;
  private int     m_modifier;
  
  public static int NO_MODIFIER_SET = AWTEvent.RESERVED_ID_MAX+1;
  public static int NO_KEY = AWTEvent.RESERVED_ID_MAX+2;

  
 
  private ChangeEvent m_changeEvent;
  private Object m_source = null;
  
  public MMEvent(){
    m_modifier = MMEvent.NO_MODIFIER_SET;
  }
  
  public MMEvent(ChangeEvent e, Object source){
    m_changeEvent = e;
    m_source = source;
  }
    
  /**
   * eventType: use fields from MouseEvent as MOUSE_PRESSED or KeyEvent as
   *            KeyTyped. (the field eventType is originally inherited from
   *            AWTEvent)<br>
   * mouseButton: use fields from MouseEvent as BUTTON1_MASK<br>
   * clickCount: use fields from MouseEvent<br>
   * keyCode: use fields from KeyEvent as VK_A<br>
   * modifier: use fields from InputEvent as SHIFT_MASK<br>
   */
  public MMEvent(int eventType, int mouseButton, int clickCount, int keyCode, int modifier){
    m_eventType = eventType;
    m_mouseButton = mouseButton;
    m_clickCount = clickCount;
    m_keyCode = keyCode;
    m_modifier = modifier;
  }
  
  /**
   *  Two Events are considered to be equal, when their types (Mouse-, Key-,
   *  etc.) and their characteristics for generation (double-click, key-code,
   *  etc.) match.
   */
  public boolean equals(MMEvent event){
    return
      m_mouseButton == event.m_mouseButton &&
      m_keyCode == event.m_keyCode &&
      m_modifier == event.m_modifier&&
      m_clickCount == event.m_clickCount &&
      m_eventType == event.m_eventType;
  }
  
  public Object getSource(){
    return m_source;
  }
 
  public void setSource(Object source){
    m_source = source;
  }
  
  public void setMouseButton(int mouseButton)
  {
    m_mouseButton = mouseButton;
  }
  
  public int getMouseButton()
  {
    return m_mouseButton;
  }
  
  public void setKeyCode(int keyCode){
    m_keyCode = keyCode;
  }
  
  public int getKeyCode(){
    return m_keyCode;
  }
  
  public void setModifier(int modifier){
    m_modifier = modifier;
  }
  
  public int getModifier(){
    return m_modifier;
  }
  
  public void setX(int x){
    m_x = x;
  }
  
  public int getX(){
    return m_x;
  }
  
  public void setY(int y){
    m_y = y;
  }
  
  public int getY(){
    return m_y;
  }
  
  public void setEventType(int type){
    m_eventType = type;
  }
  
  public int getEventType(){
    return m_eventType;
  }
  
  public void setClickCount(int clicks){
    m_clickCount = clicks;
  }
  
  public int getClickCount(){
    return m_clickCount;
  }
  
  public void setMouseWheelRotation(int nr) {
    m_mouseWheelRotation = nr;
  }
  
  public int getMouseWheelRotation() {
    return m_mouseWheelRotation;
  }
  
  
  public ChangeEvent getChangeEvent() {
    return m_changeEvent;
  }
  
  public void setChangeEvent(ChangeEvent e) {
    m_changeEvent = e;
  }
  
  
  public String toString(){
    return new String("eventType: " + m_eventType + "\n" +
                        "m_mouseButton: " + m_mouseButton + "\n" +
                        "m_clickCount: " + m_clickCount + "\n" +
                        "m_keyCode: " + m_keyCode+ "\n" +
                        "m_modifier: " + m_modifier+ "\n" );
  }
  
}
