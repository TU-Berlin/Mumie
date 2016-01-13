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

package net.mumie.mathletfactory.math.set;

import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 * This class represents a set defined by a starting and an ending value 
 * and by the type of the two braces.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class Interval implements NumberSetIF, Comparable {

  /**
   * defines the closed interval border, i.e. the border value belongs to the
   * interval.
   */
  public final static boolean CLOSED = false;

  /**
     * defines the open interval border, i.e. the border value does not belong to the
     * interval.
     */
  public final static boolean OPEN = true;

  /**
   * defines the form of the open interval border, i.e. if the interval has an open border
   * round brackets are used.
   */
  public final static boolean ROUND = true;

  /**
   * defines the form of the open interval border, i.e. if the interval has an open border
   * square brackets are used.
   */
  public final static boolean SQUARE = false;

  /**
   * we must ensure that the entryType of both arrays is always the same.
   * we hold the lower and upper boundary of an interval as two dimensional
   * array, because we want to treat these boundarys as &quot;projective&quot;
   * values during calculations.
   * Observe that we have in inconsistency here, because we cannot differentiate
   * between negative and positive infinity in projective coordinates.
   *
   * @author vossbeck
   *
  */
  private MRealNumber m_lowerBoundary, m_upperBoundary;

  private boolean m_lowerBoundaryType, m_upperBoundaryType;
  private boolean m_form;

  public Interval(
      MRealNumber lowBoundVal,
      boolean lowerBoundType,
      MRealNumber upBoundVal,
      boolean upperBoundType) {
    this(lowBoundVal, lowerBoundType, upBoundVal, upperBoundType, SQUARE);
    
  }
  
  public Interval(
    MRealNumber lowBoundVal,
    boolean lowerBoundType,
    MRealNumber upBoundVal,
    boolean upperBoundType,
    boolean form) {
    if (lowBoundVal.lessOrEqualThan(upBoundVal)) {
      m_lowerBoundary = lowBoundVal;
      m_upperBoundary = upBoundVal;
    }
    else {
      // just in case the user had a bad day...
      m_lowerBoundary = upBoundVal;
      m_upperBoundary = lowBoundVal;
    }
    m_lowerBoundaryType = lowerBoundType;
    m_upperBoundaryType = upperBoundType;
    m_form = form;
  }

  public Interval(
      Class numberClass,
      double lowBoundVal,
      boolean lowerBoundType,
      double upBoundVal,
      boolean upperBoundType) {
    this(numberClass, lowBoundVal, lowerBoundType, upBoundVal, upperBoundType, SQUARE);
    
  }

  /**
   * Creates an interval with the given parameters. 
   */
  public Interval(
    Class numberClass,
    double lowBoundVal,
    boolean lowerBoundType,
    double upBoundVal,
    boolean upperBoundType,
    boolean form) {
    this(
      (MRealNumber) NumberFactory.newInstance(numberClass, lowBoundVal),
      lowerBoundType,
      (MRealNumber) NumberFactory.newInstance(numberClass, upBoundVal),
      upperBoundType,
      form);
  }

  /** 
   * Constructs an interval with the given string. This string must be of the form
   * "[a;b]", "(a;b]", "(a;b]" or "(a;b)" where a and b are string representations 
   * of double values. 
   */
  public Interval(Class numberClass, String intervalString) {
    this(numberClass, 0, OPEN, 0, OPEN, SQUARE);
    setBoundary(intervalString);
  }

  /**
   *  Creates the empty interval ]0,0[.
   */
  public Interval(Class numberClass) { //Creates an empty interval
    this((MRealNumber) NumberFactory.newInstance(numberClass, 0),
    OPEN,
    (MRealNumber) NumberFactory.newInstance(numberClass, 0),
    OPEN,
    SQUARE);
  }

  /**
   *  Copy constructor.
   */
  public Interval(Interval anInterval) {
    this((MRealNumber) anInterval.m_lowerBoundary.copy(),
    anInterval.m_lowerBoundaryType,
    (MRealNumber) anInterval.m_upperBoundary.copy(),
    anInterval.m_upperBoundaryType,
    anInterval.m_form);
  }

  // For creating other types of intervalls one of the following utility methods should be used !

  public Class getNumberClass() {
    /* we do always ensure that all 4 MNumber within the lower and upper boundary are of the same number type*/
    return m_lowerBoundary.getClass();
  }

  public boolean getForm() {
    return m_form;
  }

  /** 
   * Sets the boundaries of the interval according to the given string. This 
   * string must be of the form "[a;b]", "(a;b]", "(a;b]" or "(a;b)" where 
   * a and b are string representations of double values. 
   */
  public void setBoundary(String intervalString) {
    StringBuffer string = new StringBuffer(intervalString);
    int helpForm = 1;
    if (string.charAt(0) == '[') {
      m_lowerBoundaryType = CLOSED;
      helpForm *= 1;
    }
    else if (string.charAt(0) == '(') {
      m_lowerBoundaryType = OPEN;
      helpForm *= 0;
    } 
    else if (string.charAt(0) == ']') {
     m_lowerBoundaryType = OPEN;
     helpForm *= 1;
   } 
   else
      throw new IllegalArgumentException("illegal argument: " + intervalString);
    string.deleteCharAt(0);
    int pos = 0;
    while (string.charAt(pos) != ';')
      pos++;
    if (string.substring(0, pos).toLowerCase().equals("-infinity"))
      m_lowerBoundary.setNegInfinity();
    else
      m_lowerBoundary.setDouble(Double.parseDouble(string.substring(0, pos)));
    string.delete(0, pos);
    if (string.charAt(0) != ';')
      throw new IllegalArgumentException("missing ';' in " + intervalString);
    string.deleteCharAt(0);
    pos = 0;
    while (string.charAt(pos) != ')' && string.charAt(pos) != ']' && string.charAt(pos) != '[' )
      pos++;
    if (string.substring(0, pos).toLowerCase().equals("infinity"))
      m_upperBoundary.setPosInfinity();
    else
      m_upperBoundary.setDouble(Double.parseDouble(string.substring(0, pos)));
    string.delete(0, pos);
    if (string.charAt(0) == ']') {
      m_upperBoundaryType = CLOSED;
      helpForm *= 1;
    }
    else if (string.charAt(0) == ')') {
      m_upperBoundaryType = OPEN;
      helpForm *= 0;
    }
    else if (string.charAt(0) == '[') {
      m_upperBoundaryType = OPEN;
      helpForm *= 1;
    }
    else
      throw new IllegalArgumentException("illegal argument: " + intervalString);
    if (helpForm == 0)
      m_form =ROUND;
    else
      m_form = SQUARE;
  }

  public void setBoundary(
    MNumber lowBoundVal,
    boolean openOrClosed,
    MNumber upBoundVal,
    boolean closedOrOpen) {
      setBoundary(lowBoundVal, openOrClosed, upBoundVal, closedOrOpen, SQUARE);
  }

  public void setBoundary(
    MNumber lowBoundVal,
    boolean openOrClosed,
    MNumber upBoundVal,
    boolean closedOrOpen,
    boolean roundOrSquare) {
    if (lowBoundVal.lessOrEqualThan(upBoundVal)) {
      m_lowerBoundary.setDouble(lowBoundVal.getDouble());
      m_upperBoundary.setDouble(upBoundVal.getDouble());
    }
    else {
      // Anstelle der Vertauschung koennte auch das Intervall als leer gesetzt werden
      m_lowerBoundary.setDouble(upBoundVal.getDouble());
      m_upperBoundary.setDouble(lowBoundVal.getDouble());
    }
    m_lowerBoundaryType = openOrClosed;
    m_upperBoundaryType = closedOrOpen;
    m_form = roundOrSquare;
  }

  public void setBoundary(
    double lowBoundVal,
    boolean openOrClosed,
    double upBoundVal,
    boolean closedOrOpen) {
      setBoundary(lowBoundVal, openOrClosed, upBoundVal, closedOrOpen, SQUARE);
  }

  public void setBoundary(
    double lowBoundVal,
    boolean openOrClosed,
    double upBoundVal,
    boolean closedOrOpen,
    boolean roundOrSquare) {
    if (lowBoundVal <= upBoundVal) {
      m_lowerBoundary.setDouble(lowBoundVal);
      m_upperBoundary.setDouble(upBoundVal);
    }
    else {
      // Anstelle der Vertauschung koennte auch das Intervall als leer gesetzt werden
      m_lowerBoundary.setDouble(upBoundVal);
      m_upperBoundary.setDouble(lowBoundVal);
    }
    m_lowerBoundaryType = openOrClosed;
    m_upperBoundaryType = closedOrOpen;
    m_form = roundOrSquare;
  }

  public void setBoundaryValues(MNumber lowBoundVal, MNumber upBoundVal) {
    if (lowBoundVal.lessOrEqualThan(upBoundVal)) {
      m_lowerBoundary.setDouble(lowBoundVal.getDouble());
      m_upperBoundary.setDouble(upBoundVal.getDouble());
    }
    else {
      m_lowerBoundary.setDouble(upBoundVal.getDouble());
      // Anstelle der Vertauschung koennte auch das Intervall als leer gesetzt werden
      m_upperBoundary.setDouble(lowBoundVal.getDouble());
    }
  }

  public void setLowerBoundary(MNumber lowBoundVal, boolean lowBoundType) {
    m_lowerBoundary.setDouble(lowBoundVal.getDouble());
    m_lowerBoundaryType = lowBoundType;
  }

  public void setLowerBoundaryVal(MNumber lowBoundVal) {
    m_lowerBoundary.setDouble(lowBoundVal.getDouble());
  }

  public void setUpperBoundary(MNumber upBoundVal, boolean upBoundType) {
    m_upperBoundary.setDouble(upBoundVal.getDouble());
    m_upperBoundaryType = upBoundType;
  }

  public void setUpperBoundaryVal(MNumber upBoundVal) {
    m_upperBoundary.setDouble(upBoundVal.getDouble());
  }

  public MNumber getLowerBoundaryVal() {
    return m_lowerBoundary;
  }

  public MNumber getUpperBoundaryVal() {
    return m_upperBoundary;
  }

  public void setBoundaryType(boolean lowTypeVal, boolean upTypeVal) {
    m_lowerBoundaryType = lowTypeVal;
    m_upperBoundaryType = upTypeVal;
  }

  public void setLowerBoundaryType(boolean lowTypeVal) {
    m_lowerBoundaryType = lowTypeVal;
  }

  public void setUpperBoundaryType(boolean upTypeVal) {
    m_upperBoundaryType = upTypeVal;
  }

  public void setForm(boolean form) {
    m_form = form;
  }

  public boolean getLowerBoundaryType() {
    return m_lowerBoundaryType;
  }

  public boolean getUpperBoundaryType() {
    return m_upperBoundaryType;
  }

  public boolean isLowerClosed() {
    if (m_lowerBoundaryType == CLOSED)
      return true;
    else
      return false;
  }

  public boolean isUpperClosed() {
    if (m_upperBoundaryType == CLOSED)
      return true;
    else
      return false;
  }

  public void setEmpty() { //Empty interval is characterized by an one element open or semi-open  interval
    m_lowerBoundary.setDouble(0.0);
    m_upperBoundary.setDouble(0.0);
    setLowerBoundaryType(OPEN);
    setUpperBoundaryType(OPEN);
  }

  public boolean isEmpty() {
    return (
      m_lowerBoundary.equals(m_upperBoundary)
        && (getLowerBoundaryType() == OPEN || getUpperBoundaryType() == OPEN));
  }

  public boolean isPoint() {
    return (
      m_lowerBoundary.equals(m_upperBoundary)
        && (getLowerBoundaryType() == CLOSED || getUpperBoundaryType() == CLOSED)
        && !m_lowerBoundary.isInfinity());
  }

  public void setLowerInfinite() {
    m_lowerBoundary.setDouble(Double.NEGATIVE_INFINITY);
    setLowerBoundaryType(OPEN);
  }

  public boolean isLowerInfinite() {
    return m_lowerBoundary.isNegInfinity();
  }

  public void setUpperInfinite() {
    m_upperBoundary.setDouble(Double.POSITIVE_INFINITY);
    setUpperBoundaryType(OPEN);
  }

  public boolean isUpperInfinite() {
    return m_upperBoundary.isPosInfinity();
  }

  public boolean contains(MNumber aNumber) {
    if (!isEmpty())
      return (
        aNumber.greaterThan(getLowerBoundaryVal())
          && aNumber.lessThan(getUpperBoundaryVal()))
        || (aNumber.equals(getLowerBoundaryVal()) && isLowerClosed())
        || (aNumber.equals(getUpperBoundaryVal()) && isUpperClosed());
    else
      return false;
  }

  public boolean contains(double aNumber) {
    if(getUpperBoundaryVal() instanceof MDouble)
      if (!isEmpty())
        return (
          aNumber > getLowerBoundaryVal().getDouble()
            && aNumber < getUpperBoundaryVal().getDouble()
          || aNumber == getLowerBoundaryVal().getDouble() && isLowerClosed()
          || aNumber == getUpperBoundaryVal().getDouble() && isUpperClosed());
      else
        return false;
    else      
      return contains(
        NumberFactory.newInstance(getNumberClass()).setDouble(aNumber));
  }

  public boolean equals(Object shouldBeAnInterval) {
    if (!(shouldBeAnInterval instanceof Interval))
      return false;
    Interval b = (Interval) shouldBeAnInterval;
    return (
      (getLowerBoundaryVal().equals(b.getLowerBoundaryVal()))
        && (getUpperBoundaryVal().equals(b.getUpperBoundaryVal()))
        && (getLowerBoundaryType() == b.getLowerBoundaryType())
        && (getUpperBoundaryType() == b.getUpperBoundaryType())
        && (!isEmpty() && !isEmpty()))
      || (isEmpty() && b.isEmpty());
  }

  public boolean isSubIntervalOf(Interval b) {
    Interval tmp = (Interval) this.deepCopy();
    return tmp.intersectWith(b).equals(this);
  }

  public boolean isDisjointWith(Interval b) {
    if (!isEmpty() && !b.isEmpty()) {
      return (getUpperBoundaryVal()).lessThan(b.getLowerBoundaryVal())
        || (getLowerBoundaryVal().greaterThan(b.getUpperBoundaryVal()))
        || ((getUpperBoundaryVal().equals(b.getLowerBoundaryVal()))
          && (!isUpperClosed() || !b.isLowerClosed()))
        || ((getLowerBoundaryVal().equals(b.getUpperBoundaryVal()))
          && (!isLowerClosed() || !b.isUpperClosed()));
    }
    else {
      return true;
    }
  }

  public Interval joinWith(Interval b) {

    if (!isEmpty() && !b.isEmpty()) {

      if (getLowerBoundaryVal().greaterThan(b.getLowerBoundaryVal())) {
        if (!b.isLowerInfinite())
          setLowerBoundary(b.m_lowerBoundary, b.m_lowerBoundaryType);
        else
          setLowerInfinite();
      }
      else if (getLowerBoundaryVal().equals(b.getLowerBoundaryVal())) {
        if (b.isLowerClosed())
          setLowerBoundary(b.m_lowerBoundary, b.m_lowerBoundaryType);
      }
      if (getUpperBoundaryVal().lessThan(b.getUpperBoundaryVal())) {
        if (!b.isUpperInfinite())
          setUpperBoundary(b.m_upperBoundary, b.m_upperBoundaryType);
        else
          setUpperInfinite();
      }
      else if (getUpperBoundaryVal().equals(b.getUpperBoundaryVal())) {
        if (b.isUpperClosed())
          setUpperBoundary(b.m_upperBoundary, b.m_upperBoundaryType);
      }
    }
    else if (isEmpty() && !b.isEmpty()) {
      setLowerBoundary(b.m_lowerBoundary, b.m_lowerBoundaryType);
      setUpperBoundary(b.m_upperBoundary, b.m_upperBoundaryType);
    }
    return this;
  }

  public Interval intersectWith(Interval b) {
    if (!this.isDisjointWith(b)) {
      if (b.getLowerBoundaryVal().greaterThan(getLowerBoundaryVal()))
        setLowerBoundary(b.m_lowerBoundary, b.m_lowerBoundaryType);
      else if (b.getLowerBoundaryVal().equals(getLowerBoundaryVal()))
        if (b.getLowerBoundaryType() == OPEN)
          m_lowerBoundaryType = b.m_lowerBoundaryType;
      if (b.getUpperBoundaryVal().lessThan(getUpperBoundaryVal()))
        setUpperBoundary(b.m_upperBoundary, b.m_upperBoundaryType);
      else if (b.getUpperBoundaryVal().equals(getUpperBoundaryVal()))
        if (b.getUpperBoundaryType() == OPEN)
          m_upperBoundaryType = b.m_upperBoundaryType;
    }
    else
      this.setEmpty();

    return this;
  }

  public int compareTo(Object c) {
    if (getUpperBoundaryVal().lessThan(((Interval) c).getUpperBoundaryVal()))
      return -1;
    else if (
      getUpperBoundaryVal().greaterThan(((Interval) c).getUpperBoundaryVal()))
      return +1;
    else
      return 0;
  }

  public Interval deepCopy() {
    return new Interval(this);
  }

  public static Interval getRealAxis(Class entryClass) {
    return new Interval(
      entryClass,
      Double.NEGATIVE_INFINITY,
      Interval.OPEN,
      Double.POSITIVE_INFINITY,
      Interval.OPEN,
      Interval.SQUARE);
  }

  public static Interval intersect(Interval a, Interval b) {
    Interval tmp = (Interval) a.deepCopy();
    tmp.intersectWith(b);
    return tmp;
  }

  public static Interval intersect(Interval[] intervallist) {
    int i;
    Interval tmp = (Interval) intervallist[0].deepCopy();
    for (i = 1; i < intervallist.length; i++)
      tmp.intersectWith(intervallist[i]);
    return tmp;
  }

  public static Interval join(Interval a, Interval b) {
    Interval tmp = (Interval) a.deepCopy();
    tmp.joinWith(b);
    return tmp;
  }

  public static Interval[] union(Interval[] intervals) {
    int i, j, k, l = 0, newLength = 0;

    Interval[] tmpIntervals = new Interval[intervals.length];
    for (k = 0; k < intervals.length; k++)
      tmpIntervals[k] = (Interval) intervals[k].deepCopy();

    for (i = 0; i < tmpIntervals.length; i++) {
      for (j = i + 1; j < tmpIntervals.length; j++) {
        if (tmpIntervals[i] != null
          && tmpIntervals[j] != null
          && !tmpIntervals[i].isDisjointWith(tmpIntervals[j])) {
          tmpIntervals[i].joinWith(tmpIntervals[j]);
          newLength++;
          tmpIntervals[j] = null;
          j = tmpIntervals.length;
          i--;
        }
      }
    }

    Interval[] newIntervals = new Interval[tmpIntervals.length - newLength];
    for (k = 0; k < tmpIntervals.length; k++)
      if (tmpIntervals[k] != null) {
        newIntervals[l] = tmpIntervals[k];
        l++;
      }

    return newIntervals;
  }

  /**
   *  Returns the lower and upper boundary enclosed in parentheses (for open 
   *  intervals) or square brackets (for closed intervals). 
   */
  public String toString() {
    return (m_lowerBoundaryType == OPEN ? "(" : "[")
      + m_lowerBoundary.toString()
      + ";"
      + m_upperBoundary.toString()
      + (m_upperBoundaryType == OPEN ? ")" : "]");
  }
  
  /** Returns true, if the given intervals overlap, false if they are disjoint. */
  public static boolean overlappingIntervals(Interval[] m_Intervals){
  for (int i = 0; i < m_Intervals.length; i++)
    for (int j = i + 1; j < m_Intervals.length; j++)
      if (!((Interval) m_Intervals[j])
        .isDisjointWith((Interval) m_Intervals[i]))
        return true;
    return false;
  }


}
