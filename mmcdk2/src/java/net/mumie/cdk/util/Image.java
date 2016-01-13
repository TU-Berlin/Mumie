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

package net.mumie.cdk.util;

import imageinfo.ImageInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;
import net.mumie.cdk.CdkConfigParam;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.cocoon.notions.LangCode;

/**
 * Helper class to create images.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Image.java,v 1.2 2008/08/21 13:07:15 rassy Exp $</code>
 */

public class Image
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The name of the image.
   */

  protected String name;

  /**
   * The description of the image.
   */

  protected String description;

  /**
   * The copyright note of the image.
   */

  protected String copyright;

  /**
   * The image type (png, jpeg, or gif)
   */

  protected String imageType;

  /**
   * The width of the image.
   */

  protected int width;

  /**
   * The height of the image.
   */

  protected int height;

  /**
   * The GDIM entries for this image. The keys and values of this map are theme and generic
   * document paths, respectively. A null key represents the default theme.
   */

  protected Map<String,String> gdimEntries;

  /**
   * The <code>ImageInfo</code> object of this instance.
   */

  protected ImageInfo imageInfo = new ImageInfo();

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified name, description, copyrigt note, and GDIM
   * entries and the width, height, and content type of the specified image.
   */

  public Image (String name,
                String description,
                String copyright,
                Map<String,String> gdimEntries,
                InputStream rawImage)
  {
    // Name:
    if ( name == null || name.trim().equals("") )
      throw new IllegalArgumentException("Name not spceified or void");
    this.name = name.trim();

    // Description:
    if ( description == null || description.trim().equals("") )
      throw new IllegalArgumentException("Description not spceified or void");
    this.description = description;

    // Copyright note:
    if ( copyright == null || copyright.trim().equals("") )
      copyright = System.getProperty(CdkConfigParam.COPYRIGHT, "");
    this.copyright = copyright;

    // GDIM entries:
    this.gdimEntries = gdimEntries;

    this.imageInfo.setInput(rawImage);
    if ( !this.imageInfo.check() )
      throw new IllegalArgumentException("Unable to read image file");

    // Width and height:
    this.width = this.imageInfo.getWidth();
    this.height = this.imageInfo.getHeight();

    // Content type:
    switch ( this.imageInfo.getFormat() )
      {
      case ImageInfo.FORMAT_PNG:
        this.imageType = "png"; break;
      case ImageInfo.FORMAT_JPEG:
        this.imageType = "jpeg"; break;
      case ImageInfo.FORMAT_GIF:
        this.imageType = "gif"; break;
      default:
        throw new IllegalArgumentException("Unsupported image format");
      }
  }

  /**
   * Creates a new instance with the specified name, description, copyrigt note, and GDIM
   * entries and the width, height, and content type of the specified image.
   */

  public Image (String name,
                String description,
                String copyright,
                Map<String,String> gdimEntries,
                File imageFile)
    throws IOException
  {
    this(name, description, copyright, gdimEntries, new FileInputStream(imageFile));
  }

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * Prints the master XML of this image to the specified stream.
   */

  public void printMaster (PrintStream out)
  {
    String prefix = XMLNamespace.PREFIX_META;
    String rootElem = prefix + ":" + DocType.nameFor(DocType.IMAGE);
    String nameElem = prefix + ":" + XMLElement.NAME;
    String descriptionElem = prefix + ":" + XMLElement.DESCRIPTION;
    String copyrightElem = prefix + ":" + XMLElement.COPYRIGHT;
    String widthElem = prefix + ":" + XMLElement.WIDTH;
    String heightElem = prefix + ":" + XMLElement.HEIGHT;
    String contentTypeElem = prefix + ":" + XMLElement.CONTENT_TYPE;
    String gdimEntriesElem = prefix + ":" + XMLElement.GDIM_ENTRIES;
    String gdimEntryElem = prefix + ":" + XMLElement.GDIM_ENTRY;
    String valueAttr = XMLAttribute.VALUE;
    String typeAttr = XMLAttribute.TYPE;
    String subtypeAttr = XMLAttribute.SUBTYPE;
    String genDocPathAttrib = XMLAttribute.GENERIC_DOC_PATH;
    String themePathAttrib = XMLAttribute.THEME_PATH;
    String langCodeAttrib = XMLAttribute.LANG_CODE;

    out.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
    out.println("<" + rootElem + " xmlns:" + prefix + "=\"" + XMLNamespace.URI_META + "\">");
    out.print("  <" + nameElem + ">");
    CdkHelper.printXMLText(this.name, out);
    out.println("</" + nameElem + ">");
    out.println("  <" + descriptionElem + ">");
    out.print("    ");
    CdkHelper.printXMLText(this.description, out);
    out.println();
    out.println("  </" + descriptionElem + ">");
    out.print("  <" + copyrightElem + ">");
    CdkHelper.printXMLText(this.copyright, out);
    out.println("</" + copyrightElem + ">");
    out.println
      ("  <" + contentTypeElem + " " +
       typeAttr + "=\"image\" " + subtypeAttr + "=\"" + this.imageType + "\"/>");
    out.println("  <" + widthElem + " " + valueAttr + "=\"" + this.width + "\"/>");
    out.println("  <" + heightElem + " " + valueAttr + "=\"" + this.height + "\"/>");
    if ( this.gdimEntries != null && !this.gdimEntries.isEmpty() )
      {
        out.println("  <" + gdimEntriesElem + ">");
        for (Map.Entry<String,String> entry : this.gdimEntries.entrySet())
          {
            out.print("    <" + gdimEntryElem + " " +
                      genDocPathAttrib + "=\"" + entry.getValue() + "\" " +
                      langCodeAttrib + "=\"" + LangCode.NEUTRAL + "\"");
            if ( entry.getKey() != null )
              out.print(" " + themePathAttrib + "=\"" + entry.getKey() + "\"");
            out.println("/>");
          }
        out.println("  </" + gdimEntriesElem + ">");
      }
    out.println("</" + rootElem + ">");
    out.flush();
  }

  /**
   * Prints the master XML of this image to the specified file.
   */

  public void printMaster (File file)
    throws IOException 
  {
    PrintStream out = new PrintStream(new FileOutputStream(file));
    this.printMaster(out);
    out.close();
  }
}
