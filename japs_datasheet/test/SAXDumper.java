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

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;

public class SAXDumper extends DefaultHandler
{
  public void startDocument ()
    throws SAXException
  {
    System.out.print
      ("\n================================== start ==================================\n");
  }

  public void endDocument ()
    throws SAXException
  {
    System.out.print
      ("\n=================================== end ===================================\n");
  }

  public void startPrefixMapping (String prefix, String uri)
    throws SAXException
  {
    System.err.print
      ("\n<!-- ### startPrefixMapping: prefix = " + prefix + ", uri = " + uri + "### -->");
  }

  public void endPrefixMapping (String prefix)
    throws SAXException
  {
    System.err.print
      ("\n<!-- ### endPrefixMapping: prefix = " + prefix + "### -->");
  }

  protected String getName (String localName, String qualifiedName)
  {
    if ( qualifiedName != null && !qualifiedName.equals("") )
      return qualifiedName;
    else if ( localName != null && !localName.equals("") )
      return localName;
    else
      return "### EMPTY NAME ###";
  }

  public void startElement(String uri, String localName, String qualifiedName,
                           Attributes attributes)
    throws SAXException
  {
    System.out.print("\n<" + this.getName(localName, qualifiedName) + "\n");
    int length = attributes.getLength();
    for (int i = 0; i < length; i++)
      {
        String name = this.getName(attributes.getLocalName(i), attributes.getQName(i));
        System.out.print("  " + name + "=\"" + attributes.getValue(i) + "\"");
        if ( i != length-1 ) System.out.print("\n");
      }
    System.out.print(">\n");
  }

  public void endElement(String namespaceURI, String localName, String qualifiedName)
    throws SAXException
  {
    System.out.print("\n</" + this.getName(localName, qualifiedName) + ">\n");
  }

  public void characters (char[] chars, int start, int length)
    throws SAXException
  {
    System.out.print(new String(chars, start, length));
  }

  public void ignorableWhitespace (char[] chars, int start, int length)
    throws SAXException
  {
    System.out.print(new String(chars, start, length));
  }

  public void processingInstruction (String target, String data)
    throws SAXException
  {
    System.out.print("<?" + target + ' ' + data + "?>");
  }

  public void skippedEntity (String name)
    throws SAXException
  {
    System.out.print('&' + name + ';');
  }

  public void warning (SAXParseException exception)
    throws SAXException
  {
    System.err.print
      ("\n<!-- ### warning: exception = " + exception + "### -->");
  }

  public void error (SAXParseException exception)
    throws SAXException
  {
    throw new SAXException("ERROR", exception);
  }

  public void fatalError (SAXParseException exception)
    throws SAXException
  {
    throw new SAXException("FATAL ERROR", exception);
  }

}
