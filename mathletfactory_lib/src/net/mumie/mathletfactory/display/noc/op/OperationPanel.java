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

package net.mumie.mathletfactory.display.noc.op;

import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;

import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.noc.MMEditablePanel;
import net.mumie.mathletfactory.display.noc.number.MMNumberPanel;
import net.mumie.mathletfactory.display.noc.op.node.NumberView;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;

/**
 *  This class implements the interactive presentation of a number based
 *  operation of arbitrary content. Note that this class extends a
 *  container drawable, but if it is used directly (not subclassed), it
 *  is no real container drawable since there is no MMOperation to act as
 *  master for it. In this case the master should be set to <code>null</code>.
 *
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */
public class OperationPanel extends MMEditablePanel {

  /** Actually displays the operation. */
  private OperationDisplay m_display;

  /** The operation that is displayed and edited. */
  private Operation m_operation;

  /** By default, we don't normalize operations. */
  private boolean m_normalForm = false;
  
  private DecimalFormat m_format = new DecimalFormat(NumberView.DEFAULT_PATTERN);

  private int m_precision = 2;

  public OperationPanel(MMObjectIF master, Operation masterOperation, Font font, boolean normalForm) {
    this(master, masterOperation, new ContainerObjectTransformer(){public void render(){}}, font);
  }
  /**
   *
   * Constructs an operation panel for the given master and font. The normalForm
   * flag is used in the case, that the panel is edited.
   */
  public OperationPanel(MMObjectIF master, Operation masterOperation, ContainerObjectTransformer transformer, Font font) {
    super(master, transformer, new OperationDisplay(font == null ? MumieTheme.DEFAULT_THEME.getControlTextFont() : font));
    m_normalForm = masterOperation.isNormalForm();
    m_display = (OperationDisplay) getValueViewer();
    if(this instanceof MMNumberPanel)
      m_display.setFont((Font)MumieTheme.getThemeProperty("MMNumberPanel.font"));
    else
      m_display.setFont((Font)MumieTheme.getThemeProperty("OperationPanel.font"));
    setOperation(masterOperation);
    setDecimalFormat(m_format);
    addComponentListener(new ComponentAdapter() {
    	public void componentResized(ComponentEvent e) {
    		// baseline may change after resizing component (baseline is height dependant)
    		revalidate();
    	}
    });
  }

	protected boolean checkContent(String content) {
		try {
			OpParser.getOperation(m_operation.getNumberClass(), content, m_normalForm);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	protected String getEditingContent() {
		return getOperation().toString(getDecimalFormat());
	}
	
	protected void applyContent(String content) {
    Operation newExpression =
      OpParser.getOperation(m_operation.getNumberClass(), content, m_normalForm);
    Operation oldExpression = isEdited() ? m_operation : null;
    log("old value: " + oldExpression + ", fire new value: " + newExpression);
    firePropertyChange(PropertyHandlerIF.OPERATION, oldExpression, newExpression);
	}
	  
  public void render() {
    m_display.render();
    super.render();
  }

  /**
   *  Sets the operation to be displayed and edited.
   */
  public void setOperation(Operation operation) {
    // update only, if value has changed
    if(operation.equals(m_operation))
      return;
    m_operation = operation.deepCopy();// needed because this op changes if master op is changed
    m_display.setOpViewRoot(OpViewMapper.getViewFor(m_operation.getOpRoot()));
    m_display.setDecimalFormat(m_format);
    m_display.render();
	  revalidate();
  }

  /**
   *  Returns the operation to be displayed and edited.
   */
  public Operation getOperation(){
    return m_operation;
  }
  
  /**
   *  Sets the alignment of the expression within the component. Permitted
   *  Values are the constants defined in {@link OperationDisplay}.
   */
  public void setAlignment(int flag) {
    m_display.setAlignment(flag);
  }

  /**
   * For debugging purposes, outputs the string representation of the master
   * operation and the dimensions.
   */
  public String toString(){
    return (m_operation != null ? m_operation.toString() : "") + ", " + getSize() + " baseline: "+getBaseline();
  }

  /**
   * Sets the given display format for decimal (i.e. MDouble) numbers.
   * Otherwise the format using {@link net.mumie.mathletfactory.display.noc.op.node.NumberView#DEFAULT_PATTERN}
   * is used.
   */
  public void setDecimalFormat(DecimalFormat format){
    m_format = format;
    m_display.setDecimalFormat(format);
  }

  /**
   * Returns the display format for decimal (i.e. MDouble) numbers.
   */
  public DecimalFormat getDecimalFormat(){
    return m_display.getDecimalFormat();
  }

  /**
   * Returns whether the panel generates normalized or non-normalized operations.
   */
  public boolean isNormalForm() {
	  return m_normalForm;
  }

  /**
   * Sets whether the panel generates normalized or non-normalized operations.
   */
  public void setNormalForm(boolean b) {
  	m_normalForm = b;
  }

  /**
   * Additionally sets the theme property "OperationPanel.font" or "MMNumberPanel.font" (if instance of MMNumberPanel).
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    if(m_display != null)
      if(this instanceof MMNumberPanel)
        m_display.setFont((Font)MumieTheme.getThemeProperty("MMNumberPanel.font"));
      else
        m_display.setFont((Font)MumieTheme.getThemeProperty("OperationPanel.font"));
  }
  
  /**
   * Sets if a tool tip is visible containing the current string representation
   * of its master.
   */
  public void setToolTipVisible(boolean visible) {
    m_display.setToolTipVisible(visible);
  }

  /**
   * Returns if a tool tip containing the current string representation
   * of its master is visible.
   */
  public boolean isToolTipVisible() {
    return m_display.isToolTipVisible();
  }
  
  public void setPrecision(int precision) {
	  if (precision >= 0) {
		  m_precision = precision;
		  
		  String pattern = "#.";
		  for (int i = 0; i < precision; i++) 
			  pattern += "#"; 
		  
		  setDecimalFormat(new DecimalFormat(pattern));	  
	  }
  }
  
  public int getPrecision() {
	  return m_precision;
  }
  
  
	private void log(String message) {
//		System.out.println("OperationPanel@" + Integer.toHexString(hashCode()) + ": " + message);
	}
}

