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

package net.mumie.mathletfactory.mmobject;

/**
 * This interface is implemented by all classes that map a onedimensional continuous 
 * structure onto a discrete set (e.g. function graphs or parameterised curves, etc.)
 * 
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public interface Discretizable1DIF {
  
  /** The default number of vertices on which this continuous structure is mapped. */
  public static final int DEFAULT_VERTICES_COUNT = 150;
  
  /** Returns the number of vertices on which this continuous structure is mapped. */
  public int getVerticesCount();
  
  /** Sets the number of vertices on which this continuous structure is mapped. */
  public void setVerticesCount(int vertexCount);
  
}

