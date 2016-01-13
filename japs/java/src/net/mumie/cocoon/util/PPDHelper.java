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

package net.mumie.cocoon.util;

import org.w3c.dom.Document;
import java.util.Map;

/**
 * <p>
 *   Helper for dealing with personalized problem data (PPD). 
 * </p>
 */

public interface PPDHelper
{
  /**
   * Role as an Avalon service (<code>PPDHelper.class.getName()</code>).
   */

  public static final String ROLE = PPDHelper.class.getName();

  /**
   * Creates the PPD for a problem and saves them in a datasheet. The problem content is
   * passed as a DOM tree, represented by <code>document</code>. The map
   * <code>sources</code> contains any user-specific input data needed to create the PPD
   * (e.g., the matriculation number). If <code>force</code> is <code>true</code>, already
   * existing PPD in the datasheet will be overwritten; otherwise, it will be preserved. The
   * datasheet is given by <code>datasheet</code>.
   */

  public void create (Document document,
                      CocoonEnabledDataSheet dataSheet,
                      Map sources,
                      boolean force)
    throws PPDException;

  /**
   * Same as {@link #create(Document,CocoonEnabledDataSheet,Map,boolean) create(document,
   * dataSheet, sources, false)}.
   */

  public void create (Document document,
                      CocoonEnabledDataSheet dataSheet,
                      Map sources)
    throws PPDException;

  /**
   * Replaces all PPD elements in <code>document</code> with the data found in
   * <code>dataSheet</code>.
   */

  public void resolve (Document document,
                       CocoonEnabledDataSheet dataSheet)
    throws PPDException;
}
