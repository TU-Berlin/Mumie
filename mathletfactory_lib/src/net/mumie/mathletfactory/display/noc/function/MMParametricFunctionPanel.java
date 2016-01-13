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
import java.awt.Dimension;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.op.OperationDisplay;
import net.mumie.mathletfactory.display.noc.op.OperationPanel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOpArrayIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.transformer.noc.ParametricFunctionTransformer;

/**
 *  This class displays the symbolic representation of a function, using a
 *  <code>JLabel</code> for the function name and an 
 *  {@link net.mumie.mathletfactory.display.noc.op.OperationPanel}
 *  for the defining expression.
 *  The master {@link net.mumie.mathletfactory.mmobject.MMObjectIF} for this
 *  panel must implement the {@link net.mumie.mathletfactory.algebra.op.UsesOperationIF}
 *  interface.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class MMParametricFunctionPanel extends MMCompoundPanel implements PropertyChangeListener {

  /**
   *  The graphical view component of the right hand side of the function equation.
   */
  protected OperationPanel[] m_functionExpression;

  private String m_functionString;
  
  private Box m_box = new Box(BoxLayout.X_AXIS);
  
  private Box m_vectorBox = new Box(BoxLayout.Y_AXIS);

  private ParenSymbolLabel[] m_parenLabels = new ParenSymbolLabel[2];

  /**
   * Constructs the function panel with the given master, which must implement 
   * the {@link net.mumie.mathletfactory.algebra.op.UsesOperationIF}.
   * 
   * @see net.mumie.mathletfactory.display.noc.ContainerDrawable#ContainerDrawable(MMObjectIF)
   */
  public MMParametricFunctionPanel(MMObjectIF master, ParametricFunctionTransformer transformer) {
    super(master, transformer);
    addPropertyChangeListener(master);
    getViewerComponent().setLayout(new BorderLayout());
    m_parenLabels[0] = new ParenSymbolLabel("(", getFont());
    m_parenLabels[1] = new ParenSymbolLabel(")", getFont());
    
    m_box.add(Box.createGlue());
    m_box.add(m_parenLabels[0]);
    //m_parenLabels[0].setBorder(new LineBorder(Color.BLACK));
    //m_parenLabels[1].setBorder(new LineBorder(Color.BLACK));
    Operation[] operations = ((UsesOpArrayIF) getMaster()).getOperations();
    m_functionExpression = new OperationPanel[operations.length];
    for(int i=0;i<operations.length;i++){
      m_functionExpression[i] = new OperationPanel(null, operations[i], transformer, getFont());
      addMMPanel(m_functionExpression[i]);
      m_functionExpression[i].addPropertyChangeListener(this);
      m_functionExpression[i].setAlignment(OperationDisplay.ALIGN_CENTER);
      m_vectorBox.add(m_functionExpression[i]);
    }
    
    m_box.add(m_vectorBox);
    m_box.add(m_parenLabels[1]);
    m_box.add(Box.createGlue());
    getViewerComponent().add(m_box, BorderLayout.CENTER);
  }

  /**
   * If this method is called, the function panel generates a function
   * string (e.g "f(x,y) = ") for the function panel depending
   * on the masters label (in the above example the label has the value "f").
   * @see net.mumie.mathletfactory.mmobject.MMObjectIF#getLabel
   */
  public String createFunctionStringFromLabel(MMObjectIF master) {
  	String result = "";
    HashSet variables = new HashSet();
    Operation[] operations = ((UsesOpArrayIF)master).getOperations(); 
    operations[0].getUsedVariables(variables);
    String[] vars = new String[variables.size()];
    vars = (String[])variables.toArray(vars);
 
    result += master.getLabel()+" (";
    for(int i=0;i<vars.length;i++){
    	result += vars[i];   
      if(i +1 < vars.length)
      	result += ",";
      }
    result += ") = ";
    return result;
  }

  public void propertyChange(PropertyChangeEvent e){
    if(!e.getPropertyName().equals(PropertyHandlerIF.OPERATION))
      return;
    Operation[] newOperations = new Operation[m_functionExpression.length];
    for(int i=0;i<m_functionExpression.length;i++)
      if(e.getOldValue() == m_functionExpression[i].getOperation())
        newOperations[i] = (Operation)e.getNewValue();
      else
        newOperations[i] = m_functionExpression[i].getOperation();
    firePropertyChange(PropertyHandlerIF.OPERATION_ARRAY, null, newOperations);
  } 
   
  /**
   * Maximum size is minimum size.
   * @see java.awt.Component#getMaximumSize()
   */
  public Dimension getMaximumSize(){
    return getMinimumSize();
  }
 
  /** Sets the given operation as the function expression to be rendered. */
  public void setFunctionExpressions(Operation[] operations) {
    for(int i=0;i<operations.length;i++)
      m_functionExpression[i].setOperation(operations[i]);
  }

  /** 
   * Sets the font for the function expression to be rendered. This method 
   * is invoked by the transformer of an MMObject and should not be called
   * by an application programmer, since the font will be overwritten by the
   * font property of the master. 
   */
  public void setFont(Font font) {
    super.setFont(font);
    if (m_functionExpression != null){
      for(int i=0;i<m_functionExpression.length; i++)
        m_functionExpression[i].setFont(getFont());
    }
    revalidate();
    repaint();
  }

  /**
   * This method should not be called by an application. Instead
   * the <code>setEditable()</code> method in the
   * {@link net.mumie.mathletfactory.mmobject.MMObjectIF master}
   * should be called, after which this method is called internally by the 
   * transformer.
   */
  public void setEditable(boolean editable) {
    for(int i=0;i<m_functionExpression.length;i++)
      m_functionExpression[i].setEditable(editable);
  }
  
  public void setLabel(String label) {
  	// only write label via transformer if not set explicitly
  	if(m_functionString == null)
  		super.setLabel(createFunctionStringFromLabel(getMaster()));
  }

  /**
   * Sets the string that is displayed before the rendered function expression.
   * The default value is <code>"f(x) ="</code>.
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
   * Additionally sets the theme property "OperationPanel.font".
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    setFont((Font)MumieTheme.getThemeProperty("OperationPanel.font"));
  }
}

