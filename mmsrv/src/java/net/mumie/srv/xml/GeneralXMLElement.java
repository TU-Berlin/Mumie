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

package net.mumie.srv.xml;

import java.util.regex.Pattern;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import java.util.Date;
import java.text.SimpleDateFormat;
import net.mumie.srv.notions.TimeFormat;

/**
 * <p>
 *   Helper classes for sending XML elements to SAX.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: GeneralXMLElement.java,v 1.1 2008/10/20 16:00:50 rassy Exp $</code>
 */

public class GeneralXMLElement
{
  /**
   * The namespace of this element.
   */

  protected final String namespace;

  /**
   * The namespace prefix of this element.
   */

  protected final String namespacePrefix;

  /**
   * The local name of the element.
   */

  protected String localName = null;

  /**
   * The attributes of the element.
   */

  protected AttributesImpl attributes =  new AttributesImpl();

  /**
   * The content of the text node of the element, or <code>null</code> if the element has no
   * text node. 
   */

  protected String text = null;

  /**
   * The date format used by default.
   */

  protected SimpleDateFormat dateFormat = new SimpleDateFormat(TimeFormat.DEFAULT);

  /**
   * Creates a new <code>GeneralXMLElement</code> with the given namespace and namespace prefix
   */

  public GeneralXMLElement (String namespace, String namespacePrefix)
  {
    this.namespace = namespace;
    this.namespacePrefix = namespacePrefix;
  }

  /**
   * Get method for {@link #namespace namespace}.
   */

  public String getNamespace ()
  {
    return this.namespace;
  }

  /**
   * Get method for {@link #namespacePrefix namespacePrefix}.
   */

  public String getNamespacePrefix ()
  {
    return this.namespacePrefix;
  }

  /**
   * Get method for {@link #localName localName}.
   */

  public String getLocalName ()
  {
    return this.localName;
  }

  /**
   * Set method for {@link #localName localName}.
   */

  public void setLocalName (String localName)
  {
    this.localName = localName;
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name and
   * value are <code>localName</code> and <code>value</code>, respectively. No namespace
   * URI or prefix are given to the attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, String value)
  {
    if ( value == null ) return;
    this.attributes.addAttribute("", localName, localName, "CDATA", value);
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name and
   * value are <code>localName</code> and <code>value</code>, respectively. No namespace
   * URI or prefix are given to the attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, boolean value)
  {
    this.addAttribute(localName, (value ? "true" : "false"));
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name and
   * value are <code>localName</code> and <code>value</code>, respectively. No namespace
   * URI or prefix are given to the attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, int value)
  {
    this.addAttribute(localName, Integer.toString(value));
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name and
   * value are <code>localName</code> and <code>value</code>, respectively. No namespace
   * URI or prefix are given to the attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, long value)
  {
    this.addAttribute(localName, Long.toString(value));
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name and
   * value are <code>localName</code> and <code>value</code>, respectively. No namespace
   * URI or prefix are given to the attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, float value)
  {
    this.addAttribute(localName, Float.toString(value));
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name and
   * value are <code>localName</code> and <code>value</code>, respectively. No namespace
   * URI or prefix are given to the attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, double value)
  {
    this.addAttribute(localName, Double.toString(value));
  }

  /**
   * Adds an attribute to {@link #attributes attributes}. The attribute's local name is
   * <code>localName</code>, its value <code>date</code> formatted by
   * {@link #dateFormat dateFormat}. No namespace URI or prefix are given to the
   * attribute. Its type is set to <code>"CDATA"</code>.
   */

  public void addAttribute (String localName, Date date)
  {
    this.addAttribute(localName, this.dateFormat.format(date));
  }

  /**
   * Get method for {@link #text text}.
   */

  public String getText ()
  {
    return this.text;
  }

  /**
   * Set method for {@link #text text}.
   */

  public void setText (String text)
  {
    this.text = text;
  }

  /**
   * Returns the qualified name of this element. This is {@link #localName} if
   * {@link #namespacePrefix} is null or the empty string, and
   * {@link #namespacePrefix}<code> + ":" + </code>{@link #localName} otherwise.
   */

  protected String getQualifiedName ()
  {
    return
      (( this.namespacePrefix == null || this.namespacePrefix.length() == 0 )
       ? this.localName
       : this.namespacePrefix + ":" + this.localName);
  }

  /**
   * Sends the start of this element as an SAX event to <code>contentHandler</code>.
   */

  public void startToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    contentHandler.startElement
      (this.namespace, this.localName, this.getQualifiedName() , this.attributes);
  }

  /**
   * Sends the end of this element as an SAX event to <code>contentHandler</code>.
   */

  public void endToSAX (ContentHandler contentHandler)
    throws SAXException
  {
    contentHandler.endElement
      (this.namespace, this.localName, this.getQualifiedName());
  }

  /**
   * Sends <code>string</code> as a text node to SAX. This is done via the
   * <code>characters</code> method. <code>contentHandler</code> receieves the SAX events.
   */

  protected void stringToSAX (String string, ContentHandler contentHandler)
    throws SAXException
  {
    contentHandler.characters(string.toCharArray(), 0, string.length());
  }

  /**
   * Sends the this element as SAX events to <code>contentHandler</code>.
   */

  public void toSAX (ContentHandler contentHandler)
    throws SAXException
  {
    this.startToSAX(contentHandler);
    if ( this.text != null )
      this.stringToSAX(this.text, contentHandler);
    this.endToSAX(contentHandler);
  }

  /**
   * Clears the local name, the attributes and the text content of this element. The
   * namespce and namespace prefix are preserved.
   */

  public void reset ()
  {
    this.localName = null;
    this.attributes.clear();
    this.text = null;
  }

  /**
   * Sets the date format to the specified pattern. The default is
   * {@link TimeFormat#DEFAULT TimeFormat.DEFAULT}. The {@link #reset reset} method does not
   * change the date format. To recover the dafault, call this method with <code>null</code>
   * as argument.
   *
   * @param pattern The date pattern as described in
   * {@link SimpleDateFormat SimpleDateFormat}, or <code>null</code> to set the default pattern.
   */

  public void setDateFormat (String pattern)
  {
    this.dateFormat = new SimpleDateFormat
      (pattern != null ? pattern : TimeFormat.DEFAULT);
  }

  /**
   * Dumps {@link #namespace}, {@link #namespacePrefix}, {@link #localName},
   * {@link #attributes}, and {@link #text} to a string and returns it.
   */

  public String statusToString ()
  {
    StringBuffer buffer = new StringBuffer();
    buffer
      .append("namespace = " + this.namespace)
      .append(", namespacePrefix = " + this.namespacePrefix)
      .append(", localName = " + this.localName)
      .append(", attributes = {");
    int attribsLength = this.attributes.getLength();
    if ( attribsLength > 0 )
      {
        for (int i = 0; i <= attribsLength; i++)
          {
            buffer
              .append(this.attributes.getQName(i))
              .append("=")
              .append(this.attributes.getValue(i));
          }
      }
    buffer
      .append("}")
      .append(", text = " + this.text);
    return buffer.toString();
  }
}
