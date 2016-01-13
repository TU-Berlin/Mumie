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

package net.mumie.mathletfactory.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class provides the basic functionality for a history list as known from
 * many webbrowsers.
 * The history consists of a list and a position in this list. So one can 
 * navigate through history as known from the browsers. Adding a new item to
 * the history deletes all items beyond the current history position. 
 * ChangeListeners can be added to the History to keep views on history up to 
 * date.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class History {
  private int m_position;
  private ArrayList m_history;

  private ArrayList m_changeListeners; 
  
  /** Creates this history with empty content. */
  public History() {
    m_history = new ArrayList();
    m_changeListeners = new ArrayList();
  }

  /** Return true if the history's current item is the first. */
  public boolean isFirst() {
    return m_position == 0;
  }
  
  /** Return true if the history's current item is the last. */
  public boolean isLast() {
    return m_position >= m_history.size() - 1;
  }
  
  /** Sets the history' current item to the first. */
  public void setFirst() {
    m_position = 0;
    stateChanged();
  }
  
  /** Sets the history' current item to the last. */
  public void setLast() {
    m_position = Math.max(0, m_history.size() - 1);
    stateChanged();
  }
  
  /** Sets the history' current item to one previous in the queue. */
  public void prev() {
    if (!isFirst()) {
      m_position--;
    }
    stateChanged();
  }
  
  /** Sets the history' current item to one next in the queue. */
  public void next() {
    if (!isLast()) {
      m_position++;
    }
    stateChanged();
  }
  
  /** Removes all items from the history. */
  public void clear() {
    m_position = 0;
    m_history.clear();
    stateChanged();
  }
  
  /**  Removes all items beyond the current item and adds the given item there. */
  public void addItem(Object item) {
    //remove all items beyond the current position
    while (!isLast()) {
      m_history.remove(m_position+1);
    }
    if (m_history.size() > 0)
      m_position++;
    m_history.add(item);
    stateChanged();
  }
  
  /** Returns the number of items before the current item. */
  public int getPrevItemCount() {
    return m_position;
  }
  
  /** Returns the number of items beyond the current item. */
  public int getNextItemCount() {
    return m_history.size() - m_position;
  }
  
  /** Returns the item at the given index.  */
  public Object getItem(int index) {
    return m_history.get(index);
  }
  
  /** Returns the current item in the queue. */
  public Object getCurrentItem() {
    return getItem(m_position);
  }
  
  private class PartialListIterator implements Iterator {
    private int index;
    private int indexTo;
    private List list;
    public PartialListIterator(int indexFrom, int indexTo, List list) {
      index = indexFrom;
      this.indexTo = indexTo;
      this.list = list;
    }
    public boolean hasNext() {
      return index < indexTo; 
    }
    public Object next() {
      return list.get(index++);
    }
    public void remove() {
      throw new UnsupportedOperationException(
          "PartialListIterator doesn't support remove");
    }
  }
  
  public Iterator getPrevIterator() {
    return new PartialListIterator(0, m_position, m_history);
  }
  
  public Iterator getNextIterator() {
    return new PartialListIterator(m_position, m_history.size() - 1, m_history);
  }
  
  public void addChangeListener(ChangeListener listener) {
    m_changeListeners.add(listener);    
  }

  public void removeChangeListener(ChangeListener listener) {
    m_changeListeners.remove(listener);    
  }
  
  public void stateChanged() {
    ChangeEvent changeEvent = new ChangeEvent(this);
    for (Iterator iterator = m_changeListeners.iterator(); iterator.hasNext();) {
      ChangeListener listener = (ChangeListener) iterator.next();
      listener.stateChanged(changeEvent);
    }
  }
}
