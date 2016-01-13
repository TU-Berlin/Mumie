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

package net.mumie.util;

import java.util.ArrayList;

/**
 * A list of integers. This is actually an {@link ArrayList ArrayList<Integer>} with
 * additional methods which allow to work with the primitive type <code>int</code> instead of
 * {@link Integer Integer}.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: IntList.java,v 1.2 2009/01/28 10:52:32 rassy Exp $</code>
 */

public class IntList extends ArrayList<Integer>
{
  /**
   * Adds the specified value to the end of the list.
   */

  public boolean add (int value)
  {
    return this.add(new Integer(value));
  }

  /**
   * Adds the specified value at the specified position of the list.
   */

  public void add (int index, int value)
  {
    this.add(index, new Integer(value));
  }

  /**
   * Returns true if the list contains the specified value.
   */

  public boolean contains (int value)
  {
    return this.contains(new Integer(value));
  }

  /**
   * Returns the values at the specified position in this list.
   */

  public int getInt (int position)
  {
    return this.get(position).intValue();
  }

  /**
   * Returns the index of the first occurrence of the specified value, or -1 if the
   * list does not contain the value.
   */

  public int indexOf (int value)
  {
    return this.indexOf(new Integer(value));
  }

  /**
   * Returns the index of the last occurrence of the specified value, or -1 if the
   * list does not contain the value.
   */

  public int lastIndexOf (int value)
  {
    return this.lastIndexOf(new Integer(value));
  }

  /**
   * Removes the value at the specified position and returns the removed value.
   */

  public int removeIntAt (int index)
  {
    return this.remove(index).intValue();
  }

  /**
   * Removes the first occurrence of the specified value.
   */

  public boolean removeInt (int value)
  {
    return this.remove(new Integer(value));
  }

  /**
   * Replaces the value at the specified position with the specified new value.
   */

  public int set (int position, int newValue)
  {
    return this.set(position, new Integer(newValue)).intValue();
  }

  /**
   * Converts this list to an array of int numbers and returns it.
   */

  public int[] toIntArray ()
  {
    int[] array = new int[this.size()];
    for (int i = 0; i < this.size(); i++)
      array[i] = this.getInt(i);
    return array;
  }

}