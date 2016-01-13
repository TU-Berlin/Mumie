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

package net.mumie.srv.entities;

import java.util.Date;
import net.mumie.srv.notions.EntityType;
import net.mumie.srv.notions.Id;
import net.mumie.srv.notions.UseMode;
import net.mumie.srv.util.Identifyable;
import org.apache.cocoon.ProcessingException;
import org.apache.excalibur.xml.sax.XMLizable;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import java.sql.ResultSet;
import java.io.IOException;
import java.io.OutputStream;

public interface Entity
  extends Identifyable, XMLizable
{
  /**
   * Role of an implementing class as an Avalon component (<code>Entity.class.getName()</code>).
   */

  final static String ROLE = Entity.class.getName();

  /**
   * Returns the type of this entity, or {@link EntityType#UNDEFINED EntityType.UNDEFINED}
   * if the type is not set.
   */

  public int getType ();

  /**
   * Returns the id of this entity, or {@link Id#UNDEFINED Id.UNDEFINED} if the id id not
   * set.
   */

  public int getId ();

  /**
   * Returns the use mode of this instance, or {@link UseMode#UNDEFINED UseMode.UNDEFINED}
   * if the use mode is not set.
   */

  public int getUseMode ();

  /**
   * Returns whether the path of the entity is included in the XML output.
   */

  public boolean getWithPath ();

  /**
   * Sets the id of this entity.
   */

  public void setId (int id);

  /**
   * Sets the use mode of this instance.
   */

  public void setUseMode (int useMode);

  /**
   * Sets whether the path of the entity is included in the XML output.
   */

  public void setWithPath (boolean withPath);

  /**
   * Returns the database columns needed by the <code>toSAX</code> methods.
   */

  public String[] getDbColumns ();

  /**
   * Makes this instance ready to represent another entity.
   */

  public void reset();

  /**
   * <p>
   *   Returns the time this entity was created.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>null</code>, which means that the last
   *   modification time is unknown.
   * </p>
   *
   * @return the last modification time of the document.
   *
   * @throws ProcessingException if something goes wrong
   */

  public Date getCreated ()
    throws ProcessingException;

  /**
   * <p>
   *   Returns the time this entity was last modified.
   * </p>
   * <p>
   *   It is allowed that the method returns <code>null</code>, which means that the last
   *   modification time is unknown.
   * </p>
   *
   * @return the last modification time of the document.
   *
   * @throws ProcessingException if something goes wrong
   */

  public Date getLastModified ()
    throws ProcessingException;

  /**
   * Sends this entity as SAX events to the specified content handler. If
   * <code>resultSet</code> is non-null, the method requests the necessary data from the
   * database; otherwise, it tries to extract them from <code>resultSet</code>. If
   * <code>ownDocument</code> is true, the <code>startDocument</code> and
   * <code>endDocument</code> method of <code>contentHandler</code> is called before
   * resp. after the XML is sent to SAX. Otherwise, these methods are suppressed.
   */

  public void toSAX (ResultSet resultSet, ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;

  /**
   * Sends this entity as SAX events to the specified content handler. If
   * <code>ownDocument</code> is true, the <code>startDocument</code> and
   * <code>endDocument</code> method of <code>contentHandler</code> is called before
   * resp. after the XML is sent to SAX. Otherwise, these methods are suppressed.
   */

  public void toSAX (ContentHandler contentHandler, boolean ownDocument)
    throws SAXException;

  /**
   * Writes a representation of this entity to the specified output stream.
   *
   * @throws java.io.IOException if something goes wrong.
   */

  public void toStream (OutputStream out)
    throws IOException;
}