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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MRealNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 * This class represents a finite union of disjoint 
 * {@link net.mumie.mathletfactory.math.set.Interval}s.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class FiniteBorelSet implements NumberSetIF {

	/** 
   * To store all Intervals in numerical order, i.e. we expect for any i
	 * upperBound(i+1) <= lowerBound(i).
	 */
	private ArrayList m_Intervals = new ArrayList();

  /**
   * Constructs a set that has the complete axis as interval.
   */
	public FiniteBorelSet(Class entryClass) {
    if(MRealNumber.class.isAssignableFrom(entryClass))
      m_Intervals.add(Interval.getRealAxis(entryClass));
	}

  /**
   * Constructs a set that is the union of the given intervals.
   */
	public FiniteBorelSet(Interval[] intervals) {
		intervals = Interval.union(intervals);
		addIntervals(intervals);
		//sorting the intervals by their borders in
		//ascending order:
		Collections.sort(m_Intervals);
	}

  /**
   * Constructs a set that consists of the given interval.
   */
	public FiniteBorelSet(Interval aInterval) {
		m_Intervals.add(aInterval.deepCopy());
	}

  /** Returns true, if this set consists of intervals that are overlapping .*/
	public boolean hasOverlappingIntervals() {
    return Interval.overlappingIntervals((Interval[])m_Intervals.toArray());
	}

  /** Adds all the given intervals to this set. */
	public void addIntervals(Interval[] intervals) {
    if(intervals == null || intervals.length == 0)
      return;
		for (int i = 0; i < intervals.length; i++) {
			addInterval(intervals[i]);
		}
	}

	public boolean contains(double x) {
		if (!m_Intervals.isEmpty()) {
			for (int i = 0; i < getIntervalCount(); i++)
				if (getInterval(i).contains(x))
					return true;
			return false;
		}
		else
			throw new IllegalUsageException("tried to ask a not initialized BorelSet");
	}

	public boolean contains(MNumber aNumber) {
		if (!m_Intervals.isEmpty()) {
			for (int i = 0; i < getIntervalCount(); i++)
				if (getInterval(i).contains(aNumber))
					return true;
			return false;
		}
		else
			throw new IllegalUsageException("tried to ask a not initialized BorelSet");
	}

  /** Returns the number of intervals that this set consists of. */
	public int getIntervalCount() {
		return m_Intervals.size();
	}

  /** Returns a reference to the nth interval. */
	public Interval getInterval(int index) {
		//ArrayList will throw an exception
		//if index<0 or index>=getIntervalCount()
		return (Interval) m_Intervals.get(index);
	}

  /** Returns an array of references to the intervals of this set. */
  public Interval[] getIntervals(){
    Interval[] intervals = new Interval[m_Intervals.size()];
    return (Interval[])m_Intervals.toArray(intervals);
  }

  /** Returns a copy of this set with a copy of the intervals contained. */
	public FiniteBorelSet deepCopy() {
		Interval[] intervals = new Interval[getIntervalCount()];
		for (int i = 0; i < getIntervalCount(); i++)
			intervals[i] = getInterval(i).deepCopy();
		return new FiniteBorelSet(intervals);
	}

  /** Returns the lower boundary of this set or <code>null</code> if empty. */
  public MNumber getLowerBound(){
    if(!isEmpty())
      return getInterval(0).getLowerBoundaryVal();
    return null;
  }

  /** Returns the upper boundary of this set or <code>null</code> if empty. */
  public MNumber getUpperBound(){
    if(!isEmpty())
      return getInterval(getIntervalCount()-1).getUpperBoundaryVal();
    return null;
  }

  /** Joins the given interval with the intervals of this set. */
	public void addInterval(Interval newInterval) {
		if (isEmpty()) {
			m_Intervals.add(newInterval);
		}
		else {
			int oldIntervalCount = m_Intervals.size();
			boolean isDisjointFromAny = true;
			for (int i = 0; i < oldIntervalCount; i++) {
				if (!newInterval.isDisjointWith(getInterval(i))) {
					isDisjointFromAny = false;
          m_Intervals.set(i, getInterval(i).joinWith(newInterval));
				}
			}
			if (isDisjointFromAny) {
				m_Intervals.add(newInterval);
				Collections.sort(m_Intervals);
			}
		}
	}

  /** Sets this set to the intersection of this set and <code>aSet</code>. */
  public FiniteBorelSet intersect(FiniteBorelSet aSet){
    return intersect(aSet.getIntervals());
  }

  /** Sets this set to the intersection of this set and <code>intervals</code>. */
	public FiniteBorelSet intersect(Interval[] intervals) {
    if(isEmpty())
      return this;
    //System.out.println("intersecting "+this+" with "+aSet);
		FiniteBorelSet tmp = new FiniteBorelSet(getNumberClass());
		tmp.m_Intervals.clear();
		for (int i = 0; i < getIntervalCount(); i++)
			for (int j = 0; j < intervals.length; j++) {
				Interval section = Interval.intersect(getInterval(i), intervals[j]);
				if (!section.isEmpty())
					tmp.addInterval(section);
			}
		m_Intervals.clear();
		/*sorting is not necessary*/
		m_Intervals.addAll(tmp.m_Intervals);
    return this;
	}

  /** Sets this set to the union of this set and <code>aSet</code>. */
  public void join(FiniteBorelSet aSet){
    addIntervals(aSet.getIntervals());
  }
  
  /**
   * Returns true, if the set consists of zero intervals. 
   */
	public boolean isEmpty() {
		return m_Intervals.size() == 0;
	}

  /** Removes all intervals. */
	public void setEmpty() {
		m_Intervals.clear();
	}
  
  public FiniteBorelSet getComplement(){
    FiniteBorelSet complement = new FiniteBorelSet(getNumberClass());
    if(isEmpty())
      return complement;
    complement.setEmpty();
    Interval interval;
    int count = getIntervalCount();
    // check for infinity on lower end
    if(!getInterval(0).isLowerInfinite()){
      interval = new Interval(getNumberClass());
      interval.setUpperBoundary(getInterval(0).getLowerBoundaryVal(), !getInterval(0).getLowerBoundaryType());
      interval.setLowerInfinite();
      interval.setLowerBoundaryType(Interval.CLOSED);
      complement.addInterval(interval);
    } else if(!getInterval(0).isLowerClosed()){
      MRealNumber negInfinity = (MRealNumber)NumberFactory.newInstance(getNumberClass());
      negInfinity.setNegInfinity();
      complement.addInterval(new Interval(negInfinity, Interval.CLOSED, negInfinity, Interval.CLOSED, Interval.SQUARE));
    }
    // iterate through definitely non-infinite intervals:
    for(int i=1;i<count;i++){
      interval = new Interval(getNumberClass());
      interval.setLowerBoundary(getInterval(i-1).getUpperBoundaryVal(), !getInterval(i-1).getUpperBoundaryType());
      interval.setUpperBoundary(getInterval(i).getLowerBoundaryVal(), !getInterval(i).getUpperBoundaryType());
      complement.addInterval(interval);
    }
    // check for infinity on upper end
    if(!getInterval(count-1).isUpperInfinite()){
      interval = new Interval(getNumberClass());
      interval.setLowerBoundary(getInterval(count-1).getUpperBoundaryVal(), !getInterval(count-1).getUpperBoundaryType());
      interval.setUpperInfinite();
      complement.addInterval(interval);
    } else if(!getInterval(count-1).isUpperClosed()){
      MRealNumber infinity = (MRealNumber)NumberFactory.newInstance(getNumberClass());
      infinity.setPosInfinity();
      complement.addInterval(new Interval(infinity, Interval.CLOSED, infinity, Interval.CLOSED, Interval.SQUARE));
  }
      return complement;
  }

	public Class getNumberClass() {
		/* expecting all intervals must have the same entryClass*/
		return getInterval(0).getNumberClass();
	}
  
  /** Returns all the intervals string representation enclosed in curly braces. */
  public String toString(){
    StringBuffer retVal = new StringBuffer(50);
    retVal.append("{");
    Iterator i = m_Intervals.iterator();
    while(i.hasNext()){
      retVal.append(i.next().toString());
      if(i.hasNext())
        retVal.append(", ");
    }
    retVal.append("}");
    return retVal.toString();
  }
  
  /** Returns a set that consists of the real axis as only interval. */
  public static FiniteBorelSet getRealAxis(Class numberClass) {
    return new FiniteBorelSet(
      new Interval(
        numberClass,
        Double.NEGATIVE_INFINITY,
        Interval.OPEN,
        Double.POSITIVE_INFINITY,
        Interval.OPEN, 
        Interval.SQUARE));
  }

}
