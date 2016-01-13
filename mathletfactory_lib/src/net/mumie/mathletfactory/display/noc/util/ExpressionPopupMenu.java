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

package net.mumie.mathletfactory.display.noc.util;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *  This class offers a popup menu that allows to change the
 *  properties of a registered {@link javax.swing.JComponent}
 *  with a popup (mostly right-button) mouse click on the component. 
 *  At the moment these are only font sizing capabilities, but
 *  further capabilities are planned.
 *   
 *  @author Paehler
 *  @mm.docstatus finished
 *  @mm.todo add further property change capability
 */
public class ExpressionPopupMenu extends JPopupMenu{
  
  /**
   * The component to which this context menu is attached. 
   */
  protected JComponent m_display;
  
  /**
   * Creates a popup menu for the given component.
   */
  public ExpressionPopupMenu(JComponent display){
    m_display = display;
    m_display.addMouseListener(new PopupListener());
    JMenuItem menuItem = new JMenuItem("Increase font size");
    menuItem.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
            alterFontSize(1.2);
          }});
    add(menuItem);
    menuItem = new JMenuItem("Decrease font size");
    menuItem.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
            alterFontSize(0.8);
          }});
    add(menuItem);
  }
    
  /**
   * Alters the font size of the registered component by multiplying it with
   * <code>factor</code>.
   */
  protected void alterFontSize(double factor){
    Font usedFont = m_display.getFont();
    Font newFont = usedFont.deriveFont((float)(usedFont.getSize() * factor));
    m_display.setFont(newFont);
    m_display.getParent().setFont(newFont);
    m_display.revalidate();
  }
  
  /**
   * The mouse popup listener for this object. It is called
   * when the user does a popup (mostly right-button) click onto
   * the registered component.  
   */
  public class PopupListener extends MouseAdapter {
    
    /**
     * Checks, whether popup menu should be shown.
     */
    public void mousePressed(MouseEvent e) {
      maybeShowPopup(e);
    }
    
    /**
     * Checks, whether popup menu should be shown.
     */
    public void mouseReleased(MouseEvent e) {
      maybeShowPopup(e);
    }
    /**
     * If mouse event is a popup trigger, the menu is shown.
     */
    private void maybeShowPopup(MouseEvent e) {
      if (e.isPopupTrigger()) {
        show(e.getComponent(), e.getX(), e.getY());
        e.consume();
      }
    }};
}
  
