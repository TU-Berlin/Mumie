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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.cocoon.notions.PseudoDocType;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;

/**
 * Helper class to create sections.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Section.java,v 1.3 2007/09/16 21:15:42 rassy Exp $</code>
 */

public class Section
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The name of the section.
   */

  protected String name = null;

  /**
   * The description of the section.
   */

  protected String description = null;


  // --------------------------------------------------------------------------------
  // Constructor
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance with the specified name and description.
   */

  public Section (String name,
                  String description)
  {
    // Name:
    if ( name == null || name.trim().equals("") )
      throw new IllegalArgumentException("Name not spceified or void");
    this.name = name.trim();

    // Description:
    if ( description == null || description.trim().equals("") )
      throw new IllegalArgumentException("Description not spceified or void");
    this.description = description;
  }

  // --------------------------------------------------------------------------------
  // Output
  // --------------------------------------------------------------------------------

  /**
   * Prints the master XML of this section to the specified stream.
   */

  public void printMaster (PrintStream out)
  {
    String prefix = XMLNamespace.PREFIX_META;
    String rootElem = prefix + ":" + PseudoDocType.nameFor(PseudoDocType.SECTION);
    String nameElem = prefix + ":" + XMLElement.NAME;
    String descriptionElem = prefix + ":" + XMLElement.DESCRIPTION;

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
    out.println("</" + rootElem + ">");
    out.flush();
  }

  /**
   * Prints the master XML of this section to the specified file.
   */

  public void printMaster (File file)
    throws IOException 
  {
    PrintStream out = new PrintStream(new FileOutputStream(file));
    this.printMaster(out);
    out.close();
  }
}
