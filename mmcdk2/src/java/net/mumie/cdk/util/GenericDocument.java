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

import java.io.PrintStream;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;

/**
 * Helper class to create generic documents.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: GenericDocument.java,v 1.8 2007/09/16 21:15:42 rassy Exp $</code>
 */

public class GenericDocument
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The type of the generic document.
   */

  protected int type = DocType.UNDEFINED;

  /**
   * The category of the generic document, or
   * {@link Category#UNDEFINED Category.UNDEFINED} if the type of this document does not
   * allow categories. 
   */

  protected int category = Category.UNDEFINED;

  /**
   * The name of the generic document.
   */

  protected String name = null;

  /**
   * The description of the generic document.
   */

  protected String description = null;

  /**
   * The width of the generic document, or -1 if the type of this document does not
   * allow width and height. 
   */

  protected int width = -1;

  /**
   * The height of the generic document, or -1 if the type of this document does not
   * allow width and height.
   */

  protected int height = -1;

  // --------------------------------------------------------------------------------
  // Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified type, category, name, description, width and
   * height. The category must be {@link Category#UNDEFINED Category.UNDEFINED} if the type
   * does not have a category. Width and height must be -1 if the type can not have width
   * and height.
   */

  public GenericDocument (int type,
                          int category,
                          String name,
                          String description,
                          int width,
                          int height)
  {
    // Type:
    if ( !DocType.exists(type)  )
      throw new IllegalArgumentException
        ("Unknown document type code: " + type);
    if ( !DocType.isGeneric(type) )
      throw new IllegalArgumentException
        ("Not a generic document type: " + DocType.nameFor(type));
    this.type = type;

    // Category:
    if ( DocType.hasCategory[type] )
      {
        if ( category == Category.UNDEFINED )
          throw new IllegalArgumentException
            ("Category must be specified for document type: " + DocType.nameFor(type));
        this.category = category;
      }
    else if ( category != Category.UNDEFINED )
      throw new IllegalArgumentException
        ("Category must not be specified for document type: " + DocType.nameFor(type));

    // Name:
    if ( name == null || name.trim().equals("") )
      throw new IllegalArgumentException("Name not spceified or void");
    this.name = name.trim();

    // Description:
    if ( description == null || description.trim().equals("") )
      throw new IllegalArgumentException("Description not spceified or void");
    this.description = description;

    // Width and height:
    if ( DocType.hasWidthAndHeight[type] )
      {
        if ( width == -1 )
          throw new IllegalArgumentException
            ("Width must be specified for document type: " + DocType.nameFor(type));
        if ( height == -1 )
          throw new IllegalArgumentException
            ("Height must be specified for document type: " + DocType.nameFor(type));
        if ( width < 0 )
          throw new IllegalArgumentException
            ("Illegal width value: " + width + " (must be non-negative)");
        if ( height < 0 )
          throw new IllegalArgumentException
            ("Illegal height value: " + height + " (must be non-negative)");
        this.width = width;
        this.height = height;
      }
    else
      {
        if ( width != -1 )
          throw new IllegalArgumentException
            ("Width must not be specified for document type: " + DocType.nameFor(type));
        if ( height != -1 )
          throw new IllegalArgumentException
            ("Height must not be specified for document type: " + DocType.nameFor(type));
      }
  }

  /**
   * Creates a new instance with the specified type, category, name, and description. Width
   * and height are set to -1.
   */

  public GenericDocument (int type,
                          int category,
                          String name,
                          String description)
  {
    this(type, category, name, description, -1, -1);
  }

  /**
   * Creates a new instance with the specified type, name, and description. The category is
   * set to {@link Category#UNDEFINED Category.UNDEFINED}. Width and height are set to -1.
   */

  public GenericDocument (int type,
                          String name,
                          String description)
  {
    this(type, Category.UNDEFINED, name, description, -1, -1);
  }

  /**
   * Creates a new instance with the specified type, category, name, description, width and
   * height. The latter two must be -1 if the type can not have width and height. Otherwise,
   * they must be distinct from -1.
   */

  public GenericDocument (String typeName,
                          String categoryName,
                          String name,
                          String description,
                          int width,
                          int height)
  {
    this(typeNameToCode(typeName), categoryNameToCode(categoryName),
         name, description, width, height);
  }

  /**
   * Creates a new instance with the specified type, category, name, and description. Width
   * and height are set to -1.
   */

  public GenericDocument (String typeName,
                          String categoryName,
                          String name,
                          String description)
  {
    this(typeNameToCode(typeName), categoryNameToCode(categoryName),
         name, description, -1, -1);
  }

  /**
   * Creates a new instance with the specified type, name, and description.  The category is
   * set to {@link Category#UNDEFINED Category.UNDEFINED}. Width and height are set to -1.
   */

  public GenericDocument (String typeName,
                          String name,
                          String description)
  {
    this(typeNameToCode(typeName), Category.UNDEFINED,
         name, description, -1, -1);
  }

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * Prints the master file of this generic document to the specified stream.
   */

  public void printMaster (PrintStream out)
  {
    String prefix = XMLNamespace.PREFIX_META;
    String rootElem = prefix + ":" + DocType.nameFor(this.type);
    String categoryElem = prefix + ":" + XMLElement.CATEGORY;
    String nameElem = prefix + ":" + XMLElement.NAME;
    String descriptionElem = prefix + ":" + XMLElement.DESCRIPTION;
    String widthElem = prefix + ":" + XMLElement.WIDTH;
    String heightElem = prefix + ":" + XMLElement.HEIGHT;
    String valueAttr = XMLAttribute.VALUE;
    String nameAttr = XMLAttribute.NAME;

    out.println("<?xml version=\"1.0\" encoding=\"ASCII\"?>");
    out.println("<" + rootElem + " xmlns:" + prefix + "=\"" + XMLNamespace.URI_META + "\">");
    if ( DocType.hasCategory[type] )
      out.println("<" + categoryElem + " " + nameAttr + "=\"" + Category.nameFor[this.category] + "\"/>");
    out.print("  <" + nameElem + ">");
    CdkHelper.printXMLText(this.name, out);
    out.println("</" + nameElem + ">");
    out.println("  <" + descriptionElem + ">");
    out.print("    ");
    CdkHelper.printXMLText(this.description, out);
    out.println();
    out.println("  </" + descriptionElem + ">");
    if ( DocType.hasWidthAndHeight[this.type] )
      {
        out.println("  <" + widthElem + " " + valueAttr + "=\"" + this.width + "\"/>");
        out.println("  <" + heightElem + " " + valueAttr + "=\"" + this.height + "\"/>");
      }
    out.println("</" + rootElem + ">");
  }

  // --------------------------------------------------------------------------------
  // Guessing types from filenames
  // --------------------------------------------------------------------------------

  /**
   * Tries to guess the document type from the specified filename. The guess is made on the
   * basis of the beginning of the filename, which should be one of the standard type
   * indicators like <code>"g_css"</code> for <code>generic_css_stylesheet</code>. If the
   * type can not be guessed, returns {@link DocType#UNDEFINED DocType.UNDEFINED}.
   */

  public static int guessType (String filename)
  {
    if ( filename.startsWith("g_css") )
      return DocType.GENERIC_CSS_STYLESHEET;
    else if ( filename.startsWith("g_img") )
      return DocType.GENERIC_IMAGE ;
    else if ( filename.startsWith("g_mov") )
      return DocType.GENERIC_MOVIE; 
    else if ( filename.startsWith("g_pge") )
      return DocType.GENERIC_PAGE; 
    else if ( filename.startsWith("g_snd") )
      return DocType.GENERIC_SOUND; 
    else if ( filename.startsWith("g_xsl") )
      return DocType.GENERIC_XSL_STYLESHEET;
    else if ( filename.startsWith("g_def") ||
              filename.startsWith("g_thm") ||
              filename.startsWith("g_lmm") ||
              filename.startsWith("g_apl") ||
              filename.startsWith("g_alg") )
      return DocType.GENERIC_ELEMENT; 
    else if ( filename.startsWith("g_ded") ||
              filename.startsWith("g_his") ||
              filename.startsWith("g_mot") ||
              filename.startsWith("g_prf") ||
              filename.startsWith("g_rmk") ||
              filename.startsWith("g_his") ||
              filename.startsWith("g_exm") )
      return DocType.GENERIC_SUBELEMENT; 
    else if ( filename.startsWith("g_prb") ||
              filename.startsWith("g_prl") )
      return DocType.GENERIC_PROBLEM; 
    else if ( filename.startsWith("g_sum") )
      return DocType.GENERIC_SUMMARY; 
    else
      return DocType.UNDEFINED;
  }

  /**
   * Tries to guess the category from the specified filename. The guess is made on the
   * basis of the beginning of the filename, which should be one of the common category
   * indicators like <code>"thm"</code> for <code>theorem</code>. If the
   * type can not be guessed, returns {@link Category#UNDEFINED Category.UNDEFINED}.
   */

  public static int guessCategory (String filename)
  {
    if ( filename.startsWith("thm") )
      return Category.THEOREM;
    else if ( filename.startsWith("def") )
      return Category.DEFINITION;
    else if ( filename.startsWith("lmm") )
      return Category.LEMMA;
    else if ( filename.startsWith("apl") )
      return Category.APPLICATION;
    else if ( filename.startsWith("alg") )
      return Category.ALGORITHM;
    else if ( filename.startsWith("vis") )
      return Category.VISUALIZATION;
    else if ( filename.startsWith("prf") )
      return Category.PROOF;
    else if ( filename.startsWith("ded") )
      return Category.DEDUCTION;
    else if ( filename.startsWith("rmk") )
      return Category.REMARK;
    else if ( filename.startsWith("his") )
      return Category.HISTORY;
    else if ( filename.startsWith("exm") )
      return Category.EXAMPLE;
    else
      return Category.UNDEFINED;
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns the document type code for the specified document type name. Throws an
   * excaption if the specified string is not a known document type.
   */

  protected static int typeNameToCode (String typeName)
  {
    int type = DocType.codeFor(typeName);
    if ( type == DocType.UNDEFINED )
      throw new IllegalArgumentException("Unknown document type: " + typeName);
    return type;
  }

  /**
   * Returns the category code for the specified category name. Throws an
   * excaption if the specified string is not a known category.
   */

  protected static int categoryNameToCode (String categoryName)
  {
    int category = Category.codeFor(categoryName);
    if ( category == Category.UNDEFINED )
      throw new IllegalArgumentException("Unknown category: " + categoryName);
    return category;
  }
}
