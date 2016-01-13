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

package net.mumie.mathletfactory.display.noc.number;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.mumie.mathletfactory.action.updater.DependencyAdapter;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.transformer.noc.DoubleSliderTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This panel allows the representation of doubles as an animatable slider. The value can
 * be changed by dragging the slider with the mouse. Additionally an animation of a continuously
 * changing value can be started and stopped. The boundaries of the slider can also be adjusted
 * by the user.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMDoubleSliderPanel extends MMCompoundPanel implements ChangeListener, ActionListener {

  /** Unicode Constant for "play" sign. */
  public final static String PLAY = "\u25ba";
  /** Unicode Constant for "stop" sign. */
  public final static String STOP = "\u25a0";
  
  private JSlider m_slider = new JSlider();
  
  private Box m_verticalBox = new Box(BoxLayout.Y_AXIS);
  
  private MMDouble m_number = new MMDouble();
  
  private MMDouble m_leftBound, m_rightBound;   
  
  /** A flag for preventing update cycles in {@link #render} and {@link #stateChanged}. */
  private boolean m_masterIsUpToDate = false;
  
  private boolean m_allowOnlyIntegers = false;
  
  private JButton m_playButton = new JButton(PLAY);
  
  private Timer m_timer = new Timer(20, this);
  
  private MMNumberPanel m_numberPanel;
  
  public MMDoubleSliderPanel(MMObjectIF master, double leftBound, double rightBound, DoubleSliderTransformer transformer) {
    super(master, transformer);
    m_number = (MMDouble)master;
    m_numberPanel = (MMNumberPanel)m_number.getAsContainerContent();
    addMMPanel(m_numberPanel);
    // this drawable only makes sense if the master is editable
    m_number.setEditable(true);
    addPropertyChangeListener(m_number);
    m_slider.addChangeListener(this);
    getViewerComponent().setLayout(new BorderLayout());
    m_leftBound = new MMDouble(leftBound);
    m_rightBound = new MMDouble(rightBound);
    
    DependencyAdapter depAdapter = new DependencyAdapter();
    m_number.dependsOn(m_leftBound, depAdapter);
    m_number.dependsOn(m_rightBound, depAdapter);
    JPanel m_labelBox = new JPanel();
    JPanel m_sliderBox = new JPanel();

    m_slider.setPreferredSize(new Dimension(100, m_slider.getPreferredSize().height));
    m_slider.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
			  if(m_number.isEditable())
			    setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
    });
    MMNumberPanel leftBoundPanel = (MMNumberPanel) m_leftBound.getAsContainerContent();
    addMMPanel(leftBoundPanel);
    m_sliderBox.add(leftBoundPanel);
    m_sliderBox.add(m_slider);
    MMNumberPanel rightBoundPanel = (MMNumberPanel) m_rightBound.getAsContainerContent();
    addMMPanel(rightBoundPanel);
    m_sliderBox.add(rightBoundPanel);
    m_labelBox.add(Box.createHorizontalGlue());
    m_labelBox.add(m_numberPanel);
    m_labelBox.add(Box.createHorizontalGlue());
    m_playButton.setFont(new Font("Sans",Font.PLAIN, 8));
    m_playButton.setBorder(new EmptyBorder(2,2,2,2));
    m_playButton.setToolTipText(ResourceManager.getMessage("animation.start"));
    m_playButton.setVisible(false);
    m_playButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        if(m_playButton.getText().equals(PLAY))
          startSlidingAnimation();
        else
          stopSlidingAnimation();
        }
    });
    m_labelBox.add(m_playButton);
    m_labelBox.add(Box.createHorizontalGlue());
    m_verticalBox.add(m_labelBox);
    m_verticalBox.add(m_sliderBox);
    add(m_verticalBox, BorderLayout.CENTER);
  }
  
  public void setLabel(String label) {
    // forward compliance property to master panel
    m_numberPanel.getPanelRenderingHints().setAppendEqualSign(getPanelRenderingHints().isAppendEqualSign());
    // adjust label
    m_numberPanel.setLabel(label);
  	// empty implementation --> label is set in m_numberPanel from same master
  }
  
  /**
   * Add an additional change listener to the slider component.
   */
  public void addChangeListener(ChangeListener l) {
    m_slider.addChangeListener(l);
  }

  /**
   * Remove an additional change listener to the slider component.
   */
  public void removeChangeListener(ChangeListener l) {
    m_slider.removeChangeListener(l);
  }

  /**
   * Updates the double value of the master according to the slider position.
   */
  public void stateChanged(ChangeEvent e){
    int percent = m_slider.getValue();
    double newValue = m_leftBound.getDouble()+0.01*percent*(m_rightBound.getDouble() - m_leftBound.getDouble());
    // set the value to exactly, what is shown by the number panel
    String newValueString = m_numberPanel.getDecimalFormat().format(newValue);
    newValueString = newValueString.replace(',','.');
    //System.out.println("setting value to "+newValueString);
    newValue = Double.parseDouble(newValueString);
    if(m_allowOnlyIntegers)
      newValue = Math.floor(newValue);  
    setToolTipText(""+newValue);
    //System.out.println("new value: "+newValue+", old value: "+m_number.getDouble());
    if(!m_masterIsUpToDate)
      firePropertyChange(PropertyHandlerIF.NUMBER, m_number, new MMDouble(newValue)); 
  }
  
  public void setDecimalFormat(DecimalFormat format) {
  	m_numberPanel.setDecimalFormat(format);
  }
  
  public DecimalFormat getDecimalFormat() {
  	return m_numberPanel.getDecimalFormat();
  }
  
  /**
   * Sets whether the value and boundaries may be edited.
   */
  public void setEditable(boolean editable){
    super.setEditable(editable);
    m_number.setEditable(editable);
    m_leftBound.setEditable(editable);
    m_rightBound.setEditable(editable);
    m_slider.setEnabled(editable);
    render();
  }

  /**
   * Sets whether the left boundary may be edited.
   */
  public void setLeftBoundEditable(boolean editable){
    m_leftBound.setEditable(editable);
    render();
  }

  /**
   * Sets whether the right boundary may be edited.
   */
  public void setRightBoundEditable(boolean editable){
    m_rightBound.setEditable(editable);
    render();
  }

  /**
   * Sets whether the number field may be edited.
   */
  public void setNumberEditable(boolean editable){
    m_number.setEditable(editable);
    render();
  }

  /**
   * Specifies whether the slider should display only integer values.
   */
  public void setAllowOnlyIntegers(boolean allowOnlyIntegers){
    m_allowOnlyIntegers = allowOnlyIntegers;
  }
  
  /**
   * Specifies whether the slider should be in horizontal or vertical orientation.
   */  
  public void setVerticalOrientation() {
    m_slider.setOrientation(1);
  }

  /**
   * Specifies whether the slider should be inverted in direction.
   */  
  public void setInverted(boolean inverted) { 
    m_slider.setInverted(inverted);
  }

  /**
   * Specifies whether the slider should be displayed without any other panels.
   */  
  public void displayOnlySlider() {
    m_verticalBox.removeAll();
    add(m_slider);
  }

  /**
   * Returns the slider value from 0 to 99.
   */  
  public int getSliderValue() {
    return m_slider.getValue();
  }

  /**
   * Returns whether the slider should display only integer values.
   */
  public boolean getAllowOnlyIntegers(){
    return m_allowOnlyIntegers;
  }
  
  public void render(){
    m_leftBound.render();
    m_rightBound.render();
    //System.out.println("unrounded new value is "+m_number.getDouble()/(m_rightBound.getDouble() - m_leftBound.getDouble())*100);
    int newValue = (int)Math.round((m_number.getDouble() - m_leftBound.getDouble())/(m_rightBound.getDouble() - m_leftBound.getDouble())*100);
    //System.out.println("Setting slider to "+newValue+"%");
    m_masterIsUpToDate = true;
    m_slider.setValue(newValue); // setValue only sets the slider's model
    m_masterIsUpToDate = false;
  }

  /**
   * Sets the left and right boundaries of the values between the user can slide.
   */
  public void setBounds(double leftBound, double rightBound){
    if(m_allowOnlyIntegers){
      leftBound = Math.floor(leftBound);
      rightBound = Math.ceil(rightBound);
    }
    m_leftBound.setDouble(leftBound);
    m_rightBound.setDouble(rightBound);
    render();    
  }

  /**
   * Returns the left boundary of the values between the user can slide.
   */ 
  public double getLeftBound() {
    return m_leftBound.getDouble();
  }

  /**
   * Returns the right boundary of the values between the user can slide.
   */ 
  public double getRightBound() {
    return m_rightBound.getDouble();
  }

  /**
   * Sets the left boundary of the values between the user can slide.
   */ 
  public void setLeftBound(double leftBound) {
    m_leftBound.setDouble(leftBound);
    m_number = (MMDouble)getMaster();
    if(m_number.getDouble() < leftBound)
      firePropertyChange(PropertyHandlerIF.NUMBER, m_number, m_leftBound);
    else 
      render();
  }

  /**
   * Sets the right boundary of the values between the user can slide.
   */ 
  public void setRightBound(double rightBound) {
    m_rightBound.setDouble(rightBound);
    m_number = (MMDouble)getMaster();
    if(m_number.getDouble() > rightBound)
      firePropertyChange(PropertyHandlerIF.NUMBER, m_number, m_rightBound);
    else 
      render();
  }

  /** Sets the visibility status of the play button. Default is invisible. */
  public void setAnimationButtonVisible(boolean b){
    m_playButton.setVisible(b);
    stopSlidingAnimation();
    repaint();
  }
  
  /** Returns the visibility status of the play button. Default is invisible. */
  public boolean isAnimationButtonVisible(){
    return m_playButton.isVisible();
  }
  
  /**
   * Performs an animation by sliding through the whole range.
   */  
  private void startSlidingAnimation(){    
    m_playButton.setText(STOP);
    m_playButton.setToolTipText(ResourceManager.getMessage("animation.stop"));
    m_timer.start();
  }

  /**
   * Stops the animation. 
   */
  private void stopSlidingAnimation(){    
    m_playButton.setText(PLAY);
    m_playButton.setToolTipText(ResourceManager.getMessage("animation.start"));
    m_timer.stop();
  }
  
  /**
   * Called by the animation timer. Increases the slider by one percent, starting from zero again
   * when 100 is reached.
   */
  public void actionPerformed(ActionEvent e) {
    if(m_slider.getValue()>99)
      m_slider.setValue(0);
    else
      m_slider.setValue(m_slider.getValue()+1);
  }

  /**
   * Sets the background of the slider to the given color.
   */
  public void setSliderBGColor(Color color){
    m_slider.setBackground(color);
  }
  
}
