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

package net.mumie.font;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FontLoaderSAXHandler extends DefaultHandler
{
  /**
   * The parsed font
   */

  protected Font font;

  /**
   * The font id.
   */

  protected String id;

  /**
   * The name of the font
   */

  protected String name;

  /**
   * The font family
   */

  protected String family;

  /**
   * The font style (normal, italic, or oblique).
   */

  protected int style;

  /**
   * The font weight (normal or bold).
   */

  protected int weight;

  /**
   * The font size.
   */

  protected double size;

  /**
   * The glyphs of the font.
   */

  protected List<Glyph> glyphs;

  /**
   * Parses the code of the glyph outlines.
   */

  protected OutlineParser outlineParser;

  /**
   * Helper to obtain the <em>x</em>, <em>y</em>, <em>width</em>, and <em>height</em>
   * property of a glyph if not specified in the XML.
   */

  protected Path2D.Double path = null;

  /**
   * Resets all internal variables.
   */

  public void reset ()
  {
    this.font = null;
    this.id = "undefined";
    this.name = "undefined";;
    this.family = "undefined";
    this.style = FontStyle.UNDEFINED;
    this.weight = FontWeight.UNDEFINED;
    this.size = 1000;
    this.glyphs = new ArrayList<Glyph>();
    if ( this.path != null ) this.path.reset();
  }

  /**
   * Creates a new instance
   */

  public FontLoaderSAXHandler ()
  {
    this.outlineParser = new OutlineParser();
    this.path = null;
    this.reset();
  }

  /**
   * Called when the XML document starts.
   */

  public void startDocument()
    throws SAXException
  {
    this.reset();
  }

  /**
   * Called when the XML document ends.
   */

  public void endDocument()
    throws SAXException
  {
    Glyph[] glyphArray = this.glyphs.toArray(new Glyph[this.glyphs.size()]);
    this.font = new Font
      (this.id, this.name, this.family, this.style, this.weight, this.size, glyphArray);
  }

  /**
   * Called when an element starts.
   */

  public void startElement(String namespaceURI,
                           String localName,
                           String qualifiedName,
                           Attributes attributes)
    throws SAXException
  {
    if ( localName.equals(FontXMLElement.FONT) )
      {
        try
          {
            // Get attribute values:
            String id = attributes.getValue(FontXMLAttribute.ID);
            String name = attributes.getValue(FontXMLAttribute.NAME);
            String family = attributes.getValue(FontXMLAttribute.FAMILY);
            String style = attributes.getValue(FontXMLAttribute.STYLE);
            String weight = attributes.getValue(FontXMLAttribute.WEIGHT);
            String size = attributes.getValue(FontXMLAttribute.SIZE);

            // Set font properties if necessary:
            if ( id != null ) this.id = id;
            if ( name != null ) this.name = name;
            if ( family != null ) this.family = family;
            if ( style != null ) this.style = FontStyle.forName(style);
            if ( weight != null ) this.weight = FontWeight.forName(weight);
            if ( size != null ) this.size = Double.parseDouble(size);
          }
        catch (Exception exception)
          {
            throw new SAXException(exception);
          }
      }
    else if ( localName.equals(FontXMLElement.GLYPH) )
      {
        try
          {
            // Get name and unicode:
            String name = attributes.getValue(FontXMLAttribute.NAME);
            String unicode = attributes.getValue(FontXMLAttribute.UNICODE);

            // Get attribute values corresponding to non-string glyph data:
            String outlineSrc = attributes.getValue(FontXMLAttribute.OUTLINE);
            String advanceWidthSrc = attributes.getValue(FontXMLAttribute.ADVANCE_WIDTH);
            String xSrc = attributes.getValue(FontXMLAttribute.X);
            String ySrc = attributes.getValue(FontXMLAttribute.Y);
            String widthSrc = attributes.getValue(FontXMLAttribute.WIDTH);
            String heightSrc = attributes.getValue(FontXMLAttribute.HEIGHT);

            // Get outline:
            Outline outline = this.outlineParser.parse(outlineSrc);

            // Get x, y, width, and height:
            double x;
            double y;
            double width;
            double height;
            if ( xSrc == null || ySrc == null || widthSrc == null || heightSrc == null )
              {
                // x, y, width, and height are not specified explicitely; obtain them
                // from outline:
                if ( this.path == null ) this.path = new Path2D.Double();
                outline.toPath(this.path, 0, 0);
                Rectangle2D bounds = this.path.getBounds2D();
                width = bounds.getWidth();
                height = bounds.getHeight();
                x = bounds.getX();
                y = - (height + bounds.getY());
              }
            else
              {
                x = Double.parseDouble(xSrc);
                y = Double.parseDouble(ySrc);
                width = Double.parseDouble(widthSrc);
                height = Double.parseDouble(heightSrc);
              }

            // Get advance width:
            double advanceWidth = Double.parseDouble(advanceWidthSrc);

            // Create glyph:
            Glyph glyph = new Glyph(name, unicode, x, y, height, width, advanceWidth, outline);

            // Add glyph to list:
            this.glyphs.add(glyph);
          }
        catch (Exception exception)
          {
            throw new SAXException(exception);
          }
      }
  }

  /**
   * Returns the font obtained in the last parse process.
   */

  public Font getFont ()
  {
    return this.font;
  }

}
