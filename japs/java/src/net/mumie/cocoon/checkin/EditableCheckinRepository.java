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

package net.mumie.cocoon.checkin;

/**
 * A checkin repository to which new entries can be added by "add" methods, and
 * from which existing entries can be removed by "remove" methods.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: EditableCheckinRepository.java,v 1.3 2009/06/16 16:06:23 rassy Exp $</code>
 */

public interface EditableCheckinRepository
  extends CheckinRepository
{
  /**
   * Role as an Avalon service (<code>EditableCheckinRepository.class.getName()</code>).
   */

  public static final String ROLE = EditableCheckinRepository.class.getName();

  /**
   * Returns true if this repository contains a master with the specified path,
   * otherwise false.
   */

  public boolean hasMaster (String masterPath);

  /**
   * Returns true if this repository contains a content with the specified path,
   * otherwise false.
   */

  public boolean hasContent (String contentPath);

  /**
   * Returns true if this repository contains a source with the specified path,
   * otherwise false.
   */

  public boolean hasSource (String sourcePath);

  /**
   * Adds the specified master with the specified path to the checkin repository. If the
   * path is not a master checkin path, an exception is thrown.
   *
   * @throws CheckinException if something goes wrong
   */

  public void addMaster (String path, Master master)
    throws CheckinException;

  /**
   * Adds the specified content with the specified path to the checkin repository. If the
   * path is not a content checkin path, an exception is thrown.
   *
   * @throws CheckinException if something goes wrong
   */

  public void addContent (String path, Content content)
    throws CheckinException;

  /**
   * Adds the specified source with the specified path to the checkin repository. If the
   * path is not a source checkin path, an exception is thrown.
   *
   * @throws CheckinException if something goes wrong
   */

  public void addSource (String path, Source source)
    throws CheckinException;

  /**
   * Removes the master with the specified path. If no such master exists, throws an
   * exception.
   *
   * @throws CheckinException if something goes wrong
   */

  public void removeMaster (String path)
    throws CheckinException;

  /**
   * Removes the content with the specified path. If no such content exists, throws an
   * exception.
   *
   * @throws CheckinException if something goes wrong
   */

  public void removeContent (String path)
    throws CheckinException;

  /**
   * Removes the source with the specified path. If no such source exists, throws an
   * exception.
   *
   * @throws CheckinException if something goes wrong
   */

  public void removeSource (String path)
    throws CheckinException;
}