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

package net.mumie.cocoon.search;

import net.mumie.cocoon.notions.Id;
import net.mumie.cocoon.util.Identifyable;
import org.apache.cocoon.ProcessingException;
import org.xml.sax.ContentHandler;

public interface UserSearcher
  extends Identifyable
{
  /**
   * Role as an Avalon service (<code>UserSearcher.class.getName()</code>).
   */

  public static final String ROLE = UserSearcher.class.getName();

  /**
   * Searches for user(s) and writes the result as XML to the specified content handler. The
   * search may be restricted by the id, login name, first name, surname of the user. If the
   * <code>requestOnly</code> flag is true, only the search request is created as XML, but
   * the search itself is suppressed.
   *
   * @param id id of the user to search. If {@link Id.UNDEFINED Id.UNDEFINED}, users of any
   *   id are searched
   * @param loginName login name of the user to search. If null, users of any login name are
   *   searched.
   * @param firstName first name of the users to search. If null, users of any first name
   *   are searched.
   * @param surame surname of the users to search. If null, users of any surname are
   *   searched.
   * @param surame surname of the users to search. If null, users of any surname are
   *   searched.
   * @param requestOnly whether only the search request is created (see above).
   * @param useMode the use mode of the users in the output
   * @param withPath whether the path of the users is contained in the output
   * @param contentHandler content handler where the XML is written to.
   * @param ownDocument whether the output is treated as a compete XML document. If this is
   *   false, the startDocument/endDocument calls are suppressed.
   *
   * @throws ProcessingException if something goes wrong.
   */

  public void search (int id, String loginName, String firstName, String surname,
                      boolean requestOnly,
                      int useMode,
                      boolean withPath,
                      ContentHandler contentHandler,
                      boolean ownDocument)
    throws ProcessingException;
}