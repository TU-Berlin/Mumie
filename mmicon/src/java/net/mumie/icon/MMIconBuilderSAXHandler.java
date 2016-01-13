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

package net.mumie.icon;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import net.mumie.font.FontLoader;
import net.mumie.font.Font;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MMIconBuilderSAXHandler extends DefaultHandler
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The image type.
   */

  protected int imageType = BufferedImage.TYPE_INT_ARGB;

  /**
   * Whether anti-aliasing is enabled.
   */

  protected boolean antiAliasing = true;

  /**
   * The output file format.
   */

  protected String outputFormat = "png";

  /**
   * Map holding the styles
   */

  protected Map<String,Attributes> idsVsStyles = new HashMap<String,Attributes>();

  /**
   * Font loader
   */

  protected FontLoader fontLoader = new FontLoader();

  /**
   * Maps font source files (XML format) to font instances.
   */

  protected Map<File,Font> filesVsFonts = new HashMap<File,Font>();

  /**
   * Regular expression pattern to split comma-separated lists
   */

  protected Pattern splitPattern = Pattern.compile(",");

  /**
   * Regular expression pattern to parse color specifications
   */

  protected Pattern colorPattern = Pattern.compile
    ("([0-9a-fA-F][0-9a-fA-F])" + 
     "([0-9a-fA-F][0-9a-fA-F])" + 
     "([0-9a-fA-F][0-9a-fA-F])" +
     "(?:\\*([0-9]+(?:\\.[0-9]+)?))?");

  /**
   * The log stream.
   */

  protected PrintStream log = System.out;

  /**
   * The log level.
   */

  protected int logLevel = 1;

  // --------------------------------------------------------------------------------
  // h1: Logging
  // --------------------------------------------------------------------------------

  /**
   * Prints a log message
   */

  protected void logMsg (int level, String message, boolean addNewline)
  {
    if ( level <= this.logLevel )
      {
        this.log.print(message);
        if ( addNewline ) this.log.println();
      }
  }

  /**
   * Prints a log message
   */

  protected void logMsg (int level, String message)
  {
    this.logMsg(level, message, true);
  }

  // --------------------------------------------------------------------------------
  // h1: Accessing attributes
  // --------------------------------------------------------------------------------

  /**
   * Returns the value of the attribute with the specified name from the specified
   * attributes object, or from the defaults if not found in the attributes object.
   */

  protected String getAttrib (Attributes attribs, String name)
    throws SAXException
  {
    this.logMsg(2, "Getting value for \"" + name + "\""); 
    String maybeValue = null;
    String value = null;

    String styleValue = attribs.getValue("style");
    if ( styleValue != null )
      {
        for (String id : this.splitPattern.split(styleValue))
          {
            maybeValue = this.idsVsStyles.get(id).getValue(name);
            if ( maybeValue != null ) value = maybeValue;
            this.logMsg(2, "  Style \"" + id + "\": " + maybeValue); 
          }
      }

    maybeValue = attribs.getValue(name);
    if ( maybeValue != null ) value = maybeValue;

    if ( value == null )
      throw new SAXException("No value and no default for: " + name);

    this.logMsg(2, "  Value: " + value); 
    return value;
  }

  /**
   * Returns the value of the attribute as a <code>double</code> number.
   */

  protected double getAttribAsDouble (Attributes attribs, String name)
    throws SAXException
  {
    return Double.parseDouble(this.getAttrib(attribs, name));
  }

  /**
   * Returns the value of the attribute as a <code>Color</code> object.
   */

  protected Color getAttribAsColor (Attributes attribs, String name)
    throws SAXException
  {
    final int RED = 0;
    final int GREEN = 1;
    final int BLUE = 2;
    int[] rgb = new int[3];
    String value = this.getAttrib(attribs, name);
    Matcher matcher = this.colorPattern.matcher(value);
    if ( matcher.matches() )
      {
        rgb[RED] = Integer.parseInt(matcher.group(1), 16);
        rgb[GREEN] = Integer.parseInt(matcher.group(2), 16);
        rgb[BLUE] = Integer.parseInt(matcher.group(3), 16);
        if ( matcher.group(4) != null )
          {
            double scale = Double.parseDouble(matcher.group(4));
            for (int i = 0; i < rgb.length; i++)
              {
                rgb[i] *= scale;
                if ( rgb[i] > 255 ) rgb[i] = 255;
              }
          }
        return new Color(rgb[RED], rgb[GREEN], rgb[BLUE]);
      }
    else
      throw new SAXException
        ("Invalid color specification: " + value);
  }

  /**
   * Returns the value of the attribute as a <code>Font</code> object.
   */

  protected Font getAttribAsFont (Attributes attribs, String name)
    throws SAXException
  {
    try
      {
        File fontFile = new File(this.getAttrib(attribs, name));
        Font font = this.filesVsFonts.get(fontFile);
        if ( font == null )
          {
            font = this.fontLoader.loadXMLFont(fontFile);
            this.filesVsFonts.put(fontFile, font);
          }
        return font;
      }
    catch (Exception exception)
      {
        throw new SAXException(exception);
      }
    
  }

  // --------------------------------------------------------------------------------
  // h1: Start/end element
  // --------------------------------------------------------------------------------

  /**
   * Called when an element starts.
   */

  public void startElement(String namespaceURI,
                           String localName,
                           String qualifiedName,
                           Attributes attribs)
    throws SAXException
  {
    if ( qualifiedName.equals("style") )
      {
        String id = attribs.getValue("id");
        if ( id == null )
          throw new SAXException("Missing \"id\" attribute in \"style\" element");
        AttributesImpl styles = new AttributesImpl(attribs);
        this.idsVsStyles.put(id, styles);
        this.logMsg(2, "Registered style: " + toString(styles)); 
      }
    else if ( qualifiedName.equals("icon") )
      {
        double width = this.getAttribAsDouble(attribs, "width");
        double height = this.getAttribAsDouble(attribs, "height");
        Color color = this.getAttribAsColor(attribs, "color");
        double borderThickness = this.getAttribAsDouble(attribs, "border-thickness");
        Color borderColor = this.getAttribAsColor(attribs, "border-color");
        double extraBorderThickness = this.getAttribAsDouble(attribs, "extra-border-thickness");
        Color extraBorderColor = this.getAttribAsColor(attribs, "extra-border-color");
        String label = this.getAttrib(attribs, "label");
        Font font = this.getAttribAsFont(attribs, "font");
        double fontSize = this.getAttribAsDouble(attribs, "font-size");
        double glyphSpace = this.getAttribAsDouble(attribs, "glyph-space");
        Color labelColor = this.getAttribAsColor(attribs, "label-color");

        String filename = attribs.getValue("filename");
        if ( filename == null )
          throw new SAXException("Missing filename");

        this.logMsg(1, "Creating " + filename + " ... ", false);

        MMIcon icon = new MMIcon
          (this.getAttribAsDouble(attribs, "width"),
           this.getAttribAsDouble(attribs, "height"),
           this.getAttribAsColor(attribs, "color"),
           this.getAttribAsDouble(attribs, "border-thickness"),
           this.getAttribAsColor(attribs, "border-color"),
           this.getAttribAsDouble(attribs, "extra-border-thickness"),
           this.getAttribAsColor(attribs, "extra-border-color"),
           this.getAttrib(attribs, "label"),
           this.getAttribAsFont(attribs, "font"),
           this.getAttribAsDouble(attribs, "font-size"),
           this.getAttribAsDouble(attribs, "glyph-space"),
           this.getAttribAsColor(attribs, "label-color"));

        this.logMsg(2, "[" + icon.toString() + "]");

        try
          {
            icon.save
              (new File(filename), this.imageType, this.antiAliasing, this.outputFormat);
          }
        catch (Exception exception)
          {
            throw new SAXException(exception);
          }

        this.logMsg(1, "done");
      }
  }

  // --------------------------------------------------------------------------------
  // h1: Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified settings.
   */

  public MMIconBuilderSAXHandler (int imageType,
                                  boolean antiAliasing,
                                  String outputFormat,
                                  PrintStream log,
                                  int logLevel)
  {
    if ( logLevel < 0  || logLevel > 2 )
      throw new IllegalArgumentException("Illegal log level: " + logLevel);
    this.imageType = imageType;
    this.antiAliasing = antiAliasing;
    this.outputFormat = outputFormat;
    this.log = log;
    this.logLevel = logLevel;
  }

  /**
   * Creates a new instance with the specified settings. The log stream and the log level
   * are set to <code>System.out</code> and <code>1</code>, respectively.
   */

  public MMIconBuilderSAXHandler (int imageType,
                                  boolean antiAliasing,
                                  String outputFormat)
  {
    this(imageType, antiAliasing, outputFormat, System.out, 1);
  }

  /**
   * Creates a new instance with default settings.
   */

  public MMIconBuilderSAXHandler ()
  {
    this(BufferedImage.TYPE_INT_ARGB, true, "png", System.out, 1);
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected static String toString (Attributes attribs)
  {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < attribs.getLength(); i++)
      {
        if ( buffer.length() > 0 ) buffer.append(" ");
        buffer.append(attribs.getQName(i) + "=" + attribs.getValue(i));
      }
    return buffer.toString();
  }
}


