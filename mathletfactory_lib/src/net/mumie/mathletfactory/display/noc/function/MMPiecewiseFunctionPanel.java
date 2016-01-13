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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.HashSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.appletskeleton.util.theme.PlainButton;
import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.op.OperationDisplay;
import net.mumie.mathletfactory.display.noc.op.OperationPanel;
import net.mumie.mathletfactory.display.noc.set.MMIntervalVariablePanel;
import net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.display.noc.util.ExpressionPopupMenu;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.analysis.function.MMPiecewiseFunction;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.mmobject.util.IndexedEntry;
import net.mumie.mathletfactory.transformer.GeneralTransformer;
import net.mumie.mathletfactory.transformer.noc.PiecewiseFunctionTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 *  This class displays the symbolic representation of a 
 *  {@link net.mumie.mathletfactory.mmobject.analysis.function.MMPiecewiseFunction} using a
 *  {@link net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel} 
 *  for the function name and for each expression an 
 *  {@link net.mumie.mathletfactory.display.noc.op.OperationPanel}.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class MMPiecewiseFunctionPanel extends MMCompoundPanel implements PropertyChangeListener {

  /**
   *  The graphical view component of the right hand side of the function equation.
   */
  private OperationPanel[] m_functionExpressions;

  private String m_functionLabel = null;
  
  private MMIntervalVariablePanel[] m_intervalPanels;

  private ParenSymbolLabel m_paren;

  private String m_functionString;
  
  private Box m_box = new Box(BoxLayout.X_AXIS);

  private JButton m_plusButton, m_minusButton;
  
  private Box m_plusMinusBox;

  private JLabel m_warningLabel = new JLabel("");
  
  /**
   * Constructs the function panel with the given master.
   */
  public MMPiecewiseFunctionPanel(MMObjectIF master, PiecewiseFunctionTransformer transformer) {
    super(master, transformer);
    getViewerComponent().setLayout(new BorderLayout());
    ImageIcon icon = new ImageIcon(getClass().getResource("/resource/icon/plus.gif"));
		addPropertyChangeListener((PropertyHandlerIF) getMaster());
    m_plusButton = new PlainButton(icon); 
    m_minusButton = new PlainButton(new ImageIcon(getClass().getResource("/resource/icon/minus.gif")));
    m_plusButton.setBorderPainted(false);
    m_plusButton.setFocusPainted(false);
    m_minusButton.setBorderPainted(false);
    m_minusButton.setFocusPainted(false);
    m_plusButton.setPreferredSize(new Dimension(icon.getIconWidth()+1, icon.getIconHeight()+1));
    m_minusButton.setPreferredSize(new Dimension(icon.getIconWidth()+1, icon.getIconHeight()+1));
    m_plusButton.setToolTipText(ResourceManager.getMessage("Add_expression"));
    m_minusButton.setToolTipText(ResourceManager.getMessage("Remove_expression"));
    m_plusButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        Class nClass = NumberFactory.newInstance(getMaster().getNumberClass()).getMMClass();
        getRealMaster().addExpression(new Operation(getMaster().getNumberClass(),"0",true), 
                                      new MMInterval(nClass, 0.0, MMInterval.CLOSED,
                                                      0.0, MMInterval.CLOSED, MMInterval.SQUARE));
        formatFunctionBox();
      }
    } );
    m_minusButton.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        Class nClass = NumberFactory.newInstance(getMaster().getNumberClass()).getMMClass();
        if(m_functionExpressions.length > 1){
          getRealMaster().removeLastExpression();
          formatFunctionBox();
        } else
          Toolkit.getDefaultToolkit().beep();
      }
    } );
    Font warningFont = m_warningLabel.getFont().deriveFont(Font.PLAIN, 10.0f);   
    m_warningLabel.setForeground(Color.RED);
    m_warningLabel.setFont(warningFont);
    m_functionExpressions = new OperationPanel[getRealMaster().getOperations().length];
    m_intervalPanels = new MMIntervalVariablePanel[m_functionExpressions.length];
    setFunctionExpressions(getRealMaster().getOperations(), transformer);
    formatFunctionBox();
    new ExpressionPopupMenu(this);
    getViewerComponent().add(m_box, BorderLayout.CENTER);
    setFont((Font)MumieTheme.getThemeProperty("MMFunctionPanel.font"));
  }

  /**
   * If this method is called, the function panel generates a function
   * string (e.g "f(x,y) = ") for the function panel depending
   * on the masters label (in the above example the label has the value "f").
   * @see net.mumie.mathletfactory.mmobject.MMObjectIF#getLabel
   */
  public String createFunctionStringFromLabel(String label) {
  	String result = "";
    HashSet varSet = new HashSet();
    for(int i=0;i<getRealMaster().getOperations().length;i++)
      for(int j=0;j<getRealMaster().getOperation(i).getUsedVariables().length;j++)
        varSet.add(getRealMaster().getOperation(i).getUsedVariables()[j]);
    String[] vars = new String[varSet.size()];
    vars = (String[])varSet.toArray(vars);
    if(vars == null || vars.length == 0) // operations contains no variables
      vars = new String[] {"x"};
    result += label+" (";
    for(int i=0;i<vars.length;i++){
    	result += vars[i];
      if(i +1 < vars.length)
      	result += ",";
    }
    result += ") =  ";
    return result;
  }

  private void formatFunctionBox(){
  	removeAllMMPanels();
    m_box.removeAll();
    m_box.add(Box.createGlue());
    m_paren = new ParenSymbolLabel("{", getFont());
    m_box.add(m_paren);
    Box functionsBox = Box.createVerticalBox();    
    functionsBox.add(Box.createVerticalStrut(10));
    for(int i=0;i<getRealMaster().getOperations().length;i++){
      m_functionExpressions[i].setAlignment(OperationDisplay.ALIGN_LEFT);
      m_functionExpressions[i].setFont((Font)MumieTheme.getThemeProperty("MMFunctionPanel.font"));
      m_intervalPanels[i].setFont((Font)MumieTheme.getThemeProperty("MMFunctionPanel.font"));
      AlignablePanel functionAndIntervalBox = new AlignablePanel();
      functionAndIntervalBox.add(m_functionExpressions[i]);
      functionAndIntervalBox.add(Box.createHorizontalStrut(10));
      AttributedStringLabel forPanel = new AttributedStringLabel(ResourceManager.getMessage("for"));
      forPanel.setFont(getFont());
      forPanel.setForeground(getForeground());
      functionAndIntervalBox.add(forPanel);
      functionAndIntervalBox.add(Box.createHorizontalStrut(10));
      functionAndIntervalBox.add(m_intervalPanels[i]);
      functionsBox.add(functionAndIntervalBox);
      functionsBox.add(Box.createVerticalStrut(10));
    }
    m_plusMinusBox = Box.createHorizontalBox();
    m_plusMinusBox.add(m_warningLabel);
    m_plusMinusBox.add(Box.createHorizontalGlue());
    m_plusMinusBox.add(m_plusButton); 
    m_plusMinusBox.add(Box.createHorizontalStrut(3));   
    m_plusMinusBox.add(m_minusButton);
    if(isEditable())
      functionsBox.add(m_plusMinusBox);
    m_box.add(functionsBox);              
    m_plusButton.revalidate();
    revalidate();
  }
   
  /**
   *  Sets the given operations and intervals to be rendered. Intervals must be instances of 
   *  {@link net.mumie.mathletfactory.mmobject.set.MMInterval}. 
   */
  public void setFunctionExpressions(Operation[] operations, PiecewiseFunctionTransformer transformer) {
    boolean equal = operations.length == m_functionExpressions.length;
    int i=0;
    if(m_functionExpressions != null && m_functionExpressions.length == operations.length){ // the same as before?
      while(equal && i < operations.length){
        equal = m_functionExpressions[i] != null && operations[i].equals(m_functionExpressions[i].getOperation()) &&
                m_intervalPanels[i].getMaster().equals(getRealMaster().getIntervals()[i]);
        i++;
      }
      if(equal)
        return;
    }
    m_functionExpressions = new OperationPanel[operations.length];
    m_intervalPanels = new MMIntervalVariablePanel[operations.length];
    for(i=0;i<m_functionExpressions.length;i++){
      m_functionExpressions[i] = new OperationPanel(getMaster(), operations[i], transformer, getFont());
      addMMPanel(m_functionExpressions[i]);
      m_functionExpressions[i].setEditable(getMaster().isEditable());
      m_functionExpressions[i].addPropertyChangeListener(this);
// TODO kein MM-Objekt des Masters benutzen, sonst kein herkoemliches rendering moeglich
      m_intervalPanels[i] = (MMIntervalVariablePanel)((MMInterval)getRealMaster().getIntervals()[i]).getAsContainerContent(GeneralTransformer.INTERVAL_VARIABLE_TRANSFORM);
// TODO mmpanel hinzufuegen wenn compound panels selbst ein root-panel haben duerfen
//     addMMPanel(m_intervalPanels[i]);
      m_intervalPanels[i].setEditable(getMaster().isEditable());
      m_intervalPanels[i].addPropertyChangeListener(this);
    }
    formatFunctionBox();
  }
  
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyHandlerIF.OPERATION)) {
			IndexedEntry oldValue = null, newValue = null;
			for(int i = 0; i < m_functionExpressions.length; i++) {
				if(evt.getSource() == m_functionExpressions[i]) {
					oldValue = new IndexedEntry(evt.getOldValue(), i, PropertyHandlerIF.OPERATION);
					newValue = new IndexedEntry(evt.getNewValue(), i, PropertyHandlerIF.OPERATION);
					break;
				}
			}
			firePropertyChange(PropertyHandlerIF.INDEXED_ENTRY, oldValue, newValue);
		}
// TODO Umstellen auf IndexedEntry wenn kein MM-Objekt des Masters mehr benutzt wird
//		if(evt.getPropertyName().equals(PropertyHandlerIF.INTERVAL)
//				|| evt.getPropertyName().equals(PropertyHandlerIF.INTERVAL_LOW_BOUND_TYPE)
//				|| evt.getPropertyName().equals(PropertyHandlerIF.INTERVAL_UP_BOUND_TYPE)) {
//			
//		}
	}

  /**
   * Sets the font for the function expression to be rendered. This method
   * is invoked by the transformer of an MMObject and should not be called
   * by an application programmer, since the font will be overwritten by the
   * font property of the master.
   */
  public void setFont(Font font) {
    if(font == null)
      return;
    super.setFont(font);
    if( m_box == null)
      return;
    m_plusButton.revalidate();
    for(int i=0;i<m_functionExpressions.length;i++){      
      m_functionExpressions[i].setFont(font);
      m_intervalPanels[i].setFont(font);
      m_functionExpressions[i].revalidate();
    }
    formatFunctionBox();
    repaint();
  }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    super.setForeground(color);    
    if(m_functionExpressions == null)
      return;
    for(int i=0;i<m_functionExpressions.length;i++){
      m_functionExpressions[i].setForeground(color);
      m_intervalPanels[i].setForeground(color);
    }
    formatFunctionBox();
  }
  
  /**
   * This method should not be called by an application. Instead
   * the <code>setEditable()</code> method in the
   * {@link net.mumie.mathletfactory.mmobject.MMObjectIF master}
   * should be called, after which this method is called internally by the
   * transformer.
   */
  public void setEditable(boolean editable) {
    if(editable == isEditable())
      return; 
    if(m_functionExpressions != null)
      for(int i=0;i<m_functionExpressions.length;i++){
        m_functionExpressions[i].setEditable(editable);
        m_intervalPanels[i].setEditable(editable);
      }
    // add or remove plusMinusBox and BevelBorder
    formatFunctionBox();
  }
  
  /** Returns, whether this panel is editable. */
  public boolean isEditable(){
    if(m_functionExpressions == null || m_functionExpressions.length == 0)
      return false;
    return m_functionExpressions[0].isEditable();
  }

  /**
   * Sets the string that is displayed in front of the rendered function
   * expression. The default value is <code>master.getLabel()+"(x) ="</code>.
   * Note that any given value is rendered in non-italic fashion. If you wish
   * to display italic identifiers, use
   * {@link #setFunctionString(AttributedStringLabel)}.
   */
  public void setFunctionString(String functionString) {
    m_functionString = functionString;
    super.setLabel(functionString);
  }
  
  public void setLabel(String label) {
  	// only write label via transformer if not set explicitly
  	if(m_functionString == null)
  		super.setLabel(createFunctionStringFromLabel(getMaster().getLabel()));
  }

  /**
   *  This method is called by
   *  {@link net.mumie.mathletfactory.transformer.FunctionTransformer#render}. If the
   *  label of the master has changed, the change is propagated. Do not call this
   *  method, since the value will be overwritten in the next rendering cycle.
   */
  public void setFunctionLabel(String label){
    if(!label.equals(m_functionLabel)){
      createFunctionStringFromLabel(label);
      formatFunctionBox();
    }
  }
  
  /** 
   * Sets the given display format for decimal (i.e. MDouble) numbers. 
   * Otherwise the format using {@link net.mumie.mathletfactory.display.noc.op.node.NumberView#DEFAULT_PATTERN} 
   * is used.
   */
  public void setDecimalFormat(DecimalFormat format){
    if(m_functionExpressions != null)
      for(int i=0;i<m_functionExpressions.length;i++)
        m_functionExpressions[i].setDecimalFormat(format);
  }

  /**
   * Returns the string that is displayed before the rendered function expression.
   */
  public String getFunctionString() {
    return m_functionString;
  }
  
  /**
   * Ensures, that the given listener is passed to all descending containers.
   * @see java.awt.Component#addMouseListener(MouseListener)
   */
  public void addMouseListener(MouseListener l){
    super.addMouseListener(l);
    if(m_functionExpressions != null)
      for(int i=0;i<m_functionExpressions.length;i++)
        m_functionExpressions[i].addMouseListener(l);
  }
  
  public void setOverlappingWarning(boolean warn){
    if(warn)
      m_warningLabel.setText(ResourceManager.getMessage("overlapping_intervals"));
    else
      m_warningLabel.setText("");
  }
  
  private MMPiecewiseFunction getRealMaster(){
    return (MMPiecewiseFunction) getMaster();
  }
  
  /**
   * Additionally sets the theme property "MMFunctionPanel.font".
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    setFont((Font)MumieTheme.getThemeProperty("MMFunctionPanel.font"));
  }

}
