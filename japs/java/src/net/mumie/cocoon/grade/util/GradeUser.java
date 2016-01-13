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

package net.mumie.cocoon.grade.util;

import net.mumie.cocoon.xml.GeneralXMLElement;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLAttribute;

public class GradeUser
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Id of the user
   */

  protected int id;

  /**
   * The first name of the user.
   */

  protected String firstName;

  /**
   * The surname of the user.
   */

  protected String surname;

  /**
   * The sync id of the user, or null if the user has no sync id.
   */

  protected String syncId;

  // --------------------------------------------------------------------------------
  // h1: Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified data.
   */

  public GradeUser (int id, String syncId, String firstName, String surname)
  {
    this.id = id;
    this.syncId = syncId;
    this.firstName = firstName;
    this.surname = surname;
  }

  // --------------------------------------------------------------------------------
  // h1: Get methods
  // --------------------------------------------------------------------------------

  /**
   * Returns the id.
   */

  public final int getId ()
  {
    return this.id;
  }

  /**
   * Returns the first name.
   */

  public final String getFirstName ()
  {
    return this.firstName;
  }

  /**
   * Returns the surname.
   */

  public final String getSurname ()
  {
    return this.surname;
  }

  /**
   * Returns the sync id of the user, or null if the user has no sync id.
   */

  public final String getSyncId ()
  {
    return this.syncId;
  }

  // --------------------------------------------------------------------------------
  // h1: To-SAX method
  // --------------------------------------------------------------------------------

  /**
   * Outputs this user as SAX events to the specified content handler using the
   * specified {@link GeneralXMLElement GeneralXMLElement} instance. 
   */

  public void toSAX (ContentHandler contentHandler, GeneralXMLElement xmlElement)
    throws SAXException
  {
    xmlElement.reset();
    xmlElement.setLocalName(XMLElement.USER);
    xmlElement.addAttribute(XMLAttribute.ID, this.id);
    if ( this.syncId != null )
      xmlElement.addAttribute(XMLAttribute.SYNC_ID, this.syncId);
    xmlElement.addAttribute(XMLAttribute.FIRST_NAME, this.firstName);
    xmlElement.addAttribute(XMLAttribute.SURNAME, this.surname);
    xmlElement.toSAX(contentHandler);    
  }
}