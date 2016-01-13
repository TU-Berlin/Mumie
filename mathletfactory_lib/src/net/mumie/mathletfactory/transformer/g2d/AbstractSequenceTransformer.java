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

package net.mumie.mathletfactory.transformer.g2d;

import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.analysis.sequence.MMSequenceIF;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * This class offers the basic functionality for rendering sequences. 
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public abstract class AbstractSequenceTransformer extends Canvas2DObjectTransformer {
  private boolean m_autoBounds;
  private int m_manualBoundsFrom;
  private int m_manualBoundsTo;
  private int m_boundsFrom;
  private int m_boundsTo;

  public AbstractSequenceTransformer() {
    m_autoBounds = true;
    m_manualBoundsFrom = 1;
    m_manualBoundsTo = 30;
    m_boundsFrom = 1;
    m_boundsTo = 30;
  }
  
  public void synchronizeMath2Screen() {
  }

  public void synchronizeWorld2Screen() {
  }

  public void getMathObjectFromScreen(
    double[] javaScreenCoordinates,
    NumberTypeDependentIF mathObject) {
  }

  public void getScreenPointFromMath(
    NumberTypeDependentIF entity,
    double[] javaScreenCoordinates) {
  }

  /** Returns the master object cast to the sequence interface. */
  protected MMSequenceIF getMasterObject() {
    return (MMSequenceIF)m_masterMMObject;
  }

  /** Returns true for automatic setting of bounds. */
  public boolean isAutoBounds() {
    return m_autoBounds;
  }

  /** Returns the index from which sequence values are being displayed from. */
  public int getBoundsFrom() {
    return isAutoBounds() ? m_boundsFrom : m_manualBoundsFrom;
  }

  /** Returns the index up to which sequence values are being displayed. */
  public int getBoundsTo() {
    return isAutoBounds() ? m_boundsTo : m_manualBoundsTo;
  }

  /** Sets whether automatic setting of bounds should be done. */
  public void setAutoBounds(boolean b) {
    m_autoBounds = b;
  }

  /** Sets the index from which sequence values are being displayed from. */
  public void setBoundsFrom(int i) {
    if(i > m_boundsTo)
      i = m_boundsTo;      
    m_boundsFrom = i;
    if (!isAutoBounds())
      m_manualBoundsFrom = i;
  }

  /** Sets the index up to which sequence values are being displayed. */
  public void setBoundsTo(int i) {
    if(i < m_boundsFrom)
      i = m_boundsFrom;      
    m_boundsTo = i;
    if (!isAutoBounds())
      m_manualBoundsTo = i;
  }

}
