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

package net.mumie.mathletfactory.display.noc.rel.node;

import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.math.algebra.rel.node.RelNode;

/**
 * This is the base interface for all views of a  
 * {@link net.mumie.mathletfactory.algebra.rel.node.RelNode}. Additionaly 
 * to implementing this interface, a view  for a 
 * {@link net.mumie.mathletfactory.algebra.rel.node.RelNode} extends a 
 * {@link javax.swing.JComponent}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */

public interface RelationComponent extends Alignable {
  
  /** Sets the font in which the relation is rendered. */
  public void setFont(Font font);
  
  /** Returns the "master" object, the model of this view. */
  public RelNode getMasterRelNode();
  
  /** 
   * Adds the property change listener (usually a
   * {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation} object).
   * If a relation part changes, the responsible component will inform
   * the master {@link net.mumie.mathletfactory.mmobject.algebra.MMRelation}
   * object via the java beans property change mechanism using the property
   * name   
   * {@link net.mumie.mathletfactory.mmobject.PropertyHandlerIF#RELATION}.
   */
  public void addPropertyChangeListener(PropertyChangeListener l);
  
  /** 
   * Sets the given display format for decimal (i.e. MDouble) numbers. 
   * Otherwise the format using 
   * {@link net.mumie.mathletfactory.display.noc.op.node.NumberView#DEFAULT_PATTERN} 
   * is used.
   */
  public void setDecimalFormat(DecimalFormat format);
  
  /** 
   * Returns the display format for decimal (i.e. MDouble) numbers. 
   */
  public abstract DecimalFormat getDecimalFormat();
}
