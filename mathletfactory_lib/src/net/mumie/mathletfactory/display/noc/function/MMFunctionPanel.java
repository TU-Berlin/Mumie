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

package net.mumie.mathletfactory.display.noc.function;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.noc.op.OperationPanel;
import net.mumie.mathletfactory.display.noc.util.ExpressionPopupMenu;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 *  This class displays the symbolic representation of a function, using a
 *  {@link net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel}
 *  for the function name and an
 *  {@link net.mumie.mathletfactory.display.noc.op.OperationPanel}
 *  for the defining expression.
 *  The master {@link net.mumie.mathletfactory.mmobject.MMObjectIF} for this
 *  panel must implement the {@link net.mumie.mathletfactory.algebra.op.UsesOperationIF}
 *  interface.
 *
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class MMFunctionPanel extends OperationPanel {

  private String m_functionString;

  private JPanel m_warnPanel = new JPanel();

  private JButton m_warnButton;

  private boolean m_showWarnButton = true;

  private JFrame m_defFrame;

  /**
   * Constructs the function panel with the given master, which must implement
   * the {@link net.mumie.mathletfactory.algebra.op.UsesOperationIF}.
   *
   * @see net.mumie.mathletfactory.display.noc.ContainerDrawable#ContainerDrawable(MMObjectIF)
   */
  public MMFunctionPanel(MMObjectIF master, ContainerObjectTransformer transformer) {
    super(master, ((UsesOperationIF) master).getOperation(), transformer, null);
    addPropertyChangeListener((PropertyChangeListener) getMaster());
    createWarnPanel();
    new ExpressionPopupMenu(this);
    add(m_warnPanel, -1);
  }

  private void createWarnPanel() {
    m_warnButton = new JButton(){
      TextLayout excl;
      public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setBackground(getBackground());
        g2.clearRect(0,0, getWidth(), getHeight());
        g2.setColor(MMFunctionPanel.this.getForeground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                            RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        excl = new TextLayout("!", g2.getFont(), g2.getFontRenderContext());
        excl.draw(g2, (float)(getWidth()/2 - excl.getBounds().getWidth()/2), (float)(getHeight()/2 + excl.getBounds().getHeight()/2));
        g2.draw(new Ellipse2D.Double(getWidth()/2 - excl.getBounds().getHeight()*0.8, getHeight()/2 - excl.getBounds().getHeight()*0.8 - excl.getBounds().getWidth()/2,
        1.6*excl.getBounds().getHeight()+excl.getBounds().getWidth()/2, 1.6*excl.getBounds().getHeight()+excl.getBounds().getWidth()/2));
      }

      public Dimension getMinimumSize(){
        if(excl == null){
          revalidate();
          return super.getMinimumSize();
        }
        return new Dimension((int)(3*excl.getBounds().getHeight()),(int)(3*excl.getBounds().getHeight()));
      }
    };
    m_warnButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        setUpDefFrame();
        m_defFrame.repaint();
      }
    });
    m_warnButton.setBorderPainted(false);
    m_warnButton.setFocusPainted(false);
    m_warnPanel.setLayout(new GridLayout(1,1));
    m_warnPanel.add(m_warnButton, BorderLayout.CENTER);
    m_warnButton.setToolTipText(ResourceManager.getMessage("gaps_in_definition"));
  }
  
  public void setLabel(String label) {
  	// only write label via transformer if not set explicitly
  	if(m_functionString == null)
  		super.setLabel(createFunctionStringFromLabel(getMaster()));
  }

  /**
   * If this method is called, the function panel generates a function
   * string (e.g "f(x,y) = ") for the function panel depending
   * on the masters label (in the above example the label has the value "f").
   * @see net.mumie.mathletfactory.mmobject.MMObjectIF#getLabel
   */
  public String createFunctionStringFromLabel(MMObjectIF master) {
  	String result = "";
    String[] vars = ((UsesOperationIF)master).getOperation().getUsedVariables();
    if(vars == null || vars.length == 0){ // operation contains no variables
      if(getMaster() instanceof MMFunctionDefByOp){
        vars = new String[]{ ((MMFunctionDefByOp)getMaster()).getVariableId()};
      }
      else
        vars = new String[] {"x"};
    }
    String extraSpaces = " ";
    String functionLabel = master.getLabel();
    if(functionLabel.length() > 0 && functionLabel.toCharArray()[0] > 255) // non ascii variable, may have buggy metrics
      extraSpaces += "  ";
    result += functionLabel+" (";

    for(int i=0;i<vars.length;i++){
    	result += vars[i];
      if(vars[i].toCharArray()[0] > 255) // non ascii variable, may have buggy metrics
        extraSpaces += "";
      if(i + 1 < vars.length)
      	result += ",";
    }
  	result += ") =" + extraSpaces;
  	return result;
  }

  private void setUpDefFrame() {
    MMRelation notDefRel = new MMRelation(((UsesOperationIF)getMaster()).getOperation().getDefinedRelation());
    notDefRel.negate();
    if(m_defFrame == null)
      m_defFrame = new JFrame();
    else
      m_defFrame.getContentPane().removeAll();
    m_defFrame.setTitle(ResourceManager.getMessage("def_gaps"));
    Box frameBox = Box.createVerticalBox();

    frameBox.add(Box.createVerticalGlue());

    JLabel label = new JLabel(ResourceManager.getMessage("expression_defined"));
    //label.setBorder(new BevelBorder(BevelBorder.LOWERED));
    label.setAlignmentX(CENTER_ALIGNMENT);
    frameBox.add(label);

    frameBox.add(Box.createVerticalGlue());
    //System.out.println("setting up for "+notDefRel);
    JComponent panel = notDefRel.getAsContainerContent();
    //panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    panel.setAlignmentX(CENTER_ALIGNMENT);
    frameBox.add(panel);

    frameBox.add(Box.createVerticalGlue());

    m_defFrame.getContentPane().add(new JScrollPane(frameBox), BorderLayout.CENTER);
    int width = (int)Math.max(panel.getPreferredSize().getWidth()*1.5, label.getPreferredSize().getWidth()*1.05);
    int height = (int)(panel.getPreferredSize().getHeight() + label.getPreferredSize().getHeight()+50);
    m_defFrame.setSize(width, height);
    m_defFrame.setLocation(100,100);
    m_defFrame.validate();
    m_defFrame.setVisible(true);
  }

  /** Sets the given operation as the function expression to be rendered. */
  public void setFunctionExpression(Operation operation) {
    setOperation(operation);
    //System.out.println(((UsesOperationIF)getMaster()).getOperation()+" is defined for "+((UsesOperationIF)getMaster()).getOperation().getDefinedRelation().toString());
    if(!((UsesOperationIF)getMaster()).getOperation().getDefinedRelation().toString().equals("ALL") && m_showWarnButton)
      m_warnPanel.setVisible(true);
    else{
      m_warnPanel.setVisible(false);
      if(m_defFrame != null && m_defFrame.isVisible())
        m_defFrame.setVisible(false);
    }
    revalidate();
    if(m_defFrame != null && m_defFrame.isVisible()){
      setUpDefFrame();
    }
  }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    super.setForeground(color);
    if(m_warnPanel != null)
      m_warnPanel.setForeground(color);
  }

  /**
   * Sets the color of the background.
   */
  public void setBackground(Color color){
    super.setBackground(color);
    if(m_warnPanel != null)
      m_warnPanel.setBackground(color);
    if(m_warnButton != null)
      m_warnButton.setBackground(color);

  }

  /**
   * Sets the string that is displayed in front of the rendered function
   * expression. The default value is <code>master.getLabel()+"(x) ="</code>.
   * Note that any given value is rendered in non-italic fashion.
   */
  public void setFunctionString(String functionString) {
  	super.setLabel(functionString);
    m_functionString = functionString;
  }

  /**
   * Returns the string that is displayed before the rendered function expression.
   */
  public String getFunctionString() {
    return m_functionString;
  }

  /**
   * Returns whether the warning button should be shown on partially defined expression
   */
  public boolean isShowWarnButton() {
    return m_showWarnButton;
  }

  /**
   * Sets whether the warning button should be shown on partially defined expression
   */
  public void setShowWarnButton(boolean b) {
    if(m_showWarnButton == b)
      return;
    m_showWarnButton = b;
    setFunctionExpression(((UsesOperationIF)getMaster()).getOperation());
  }

  /** 
   * Sets the function label visible.
   * @deprecated replaced by <code>setLabelVisible(boolean)</code>
   * @see net.mumie.mathletfactory.display.noc.MMPanel#setLabelVisible(boolean)
   */
  public void setFunctionLabelVisible(boolean visible){
  	setLabelVisible(visible);// backward compatibility
  }
  
  /**
   * Additionally sets the theme property "MMFunctionPanel.font".
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    setFont((Font)MumieTheme.getThemeProperty("MMFunctionPanel.font"));
  }
}
