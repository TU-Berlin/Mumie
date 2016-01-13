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

package net.mumie.cdk.cmdlib;

import java.io.FileOutputStream;
import java.io.PrintStream;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.util.GenericDocument;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.notions.Nature;
import java.io.File;
import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cdk.CdkHelper;
import java.util.StringTokenizer;
import net.mumie.cdk.CdkConfigParam;

/**
 * Creates generic documents for real documents.
 *
 * <h4>Usage:</h4>
 * <pre><!-- 
     -->  mmr2gdoc <var>MASTER_OF_REAL</var> [<var>OPTIONS</var>]
<!-- -->  mmr2gdoc --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Creates a generic document for a given real document. <var>MASTER_OF_REAL</var> is the
 *   master file of the real document. mmr2gdoc creates the master file of the
 *   corresponding generic document and writes it to a file or to stdout (see
 *   "Options" below for details). The type of the generic document is deduced
 *   from that of the real document. The category, if any, is the same for both
 *   documents. Name, description, width and height (if any) are copied from the
 *   real document by default. They can be overwrittem by command line options
 *   (see below).
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--output=<var>FILE</var>, -o <var>FILE</var></code></dt>
 *   <dd>
 *     File where the output is written to. If "-", the output goes to stdout.
 *     If not "-", <var>FILE</var> must be a master filename. If omitted, a default is
 *     used. See "Default output filname" below for how the default is
 *     constructed. 
 *   </dd>
 *   <dt><code>--name=<var>NAME</var>, -n <var>NAME</var></code></dt>
 *   <dd>
 *     The name of the generic document. Defaults to the name of the real document
 *   </dd>
 *   <dt><code>--description=<var>DESCRIPTION</var>, -n <var>DESCRIPTION</var></code></dt>
 *   <dd>
 *     The description of the generic document. Defaults to the description of the
 *     real document
 *   </dd>
 *   <dt><code>--width=<var>WIDTH</var>, -W <var>WIDTH</var></code></dt>
 *   <dd>
 *     The width of the generic document, in pixels. This option is only allowed
 *     with document types witch have width and height (e.g., images). Defaults
 *     to the width of the real document.
 *   </dd>
 *   <dt><code>--height=<var>HEIGHT</var>, -W <var>HEIGHT</var></code></dt>
 *   <dd>
 *     The height of the generic document, in pixels. This option is only allowed
 *     with document types witch have height and height (e.g., images). Defaults
 *     to the height of the real document.
 *   </dd>
 *   <dt><code>--force, -f</code></dt>
 *   <dd>
 *     Overwrites the output file if it exists already. If this option is not
 *     set and the output file already exists, an error is signaled.
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints this help message and exits.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information and exits.
 *   </dd>
 * </dl>
 * <h4>Default output filname:</h4>
 * <p>
 *   The default output filename is obtained as follows: If the master file of the
 *   real document has "gdim_entry" elements, the filename is obtained from the
 *   "generic_doc_path" attribute of the first "gdim_entry" element. Otherwise, it
 *   is obtained from the real document master filename by adding "g_" to the
 *   local part and stripping the language code, if any, from the local part. For
 *   the latter mmr2gdoc must know the language codes. To this end, it reads the
 *   system property net.mumie.cdk.langCodes.
 * </p>
 * <h4>Examples:</h4>
 * <dl>
 *   <dt><code>$ mmr2gdoc xsl_foo_de.meta.xml</code></dt>
 *   <dd>
 *     Provided xsl_foo_de.meta.xml does not contain "gdim_entry" elements, the
 *     above command creates a generic XSL stylesheet for the specified real XSL
 *     stylesheet and writes the master to the file g_xsl_foo.meta.xml.
 *   </dd>
 *   <dt><code>$ mmr2gdoc pge_foo_en.meta.xml -o g_pge_bar.meta.xml</code></dt>
 *   <dd>
 *     Creates a generic page for the specified real page and writes the master
 *     to the file g_pge_bar.meta.xml
 *   </dd>
 *   <dt><code>$ mmr2gdoc pge_foo_en.meta.xml -o -</code></dt>
 *   <dd>
 *     Creates a generic page for the specified real page and writes the master
 *     to stdout.
 *   </dd>
 * </dl>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmr2gdoc.java,v 1.5 2007/07/31 23:25:24 rassy Exp $</code>
 */

public class Mmr2gdoc extends AbstractCommand
{
  /**
   * The command name (<code>"mmr2gdoc"</code>).
   */

  public static final String COMMAND_NAME = "mmr2gdoc";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Creates a generic document for a given real document";

  /**
   * Returns the command name (<code>"mmr2gdoc"</code>).
   */

  public String getName ()
  {
    return COMMAND_NAME;
  }

  /**
   * Returns a short description of the command
   */

  public String getDescription ()
  {
    return COMMAND_DESCRIPTION;
  }

  /**
   * Main task, creates the generic document.
   */

  protected void mmr2gdoc (CdkFile masterFileOfReal,
                           CdkFile masterFileOfGeneric,
                           boolean toStdout,
                           String name,
                           String description,
                           int width,
                           int height,
                           boolean force)

    throws Exception
  {
    // Get master object:
    Master masterOfReal = masterFileOfReal.getMaster();

    // Some checks:
    if ( masterOfReal.getNature() != Nature.DOCUMENT )
      throw new IllegalArgumentException("Not a document: " + masterFileOfReal);
    int typeOfReal = masterOfReal.getType();
    if ( DocType.isGeneric[typeOfReal] )
      throw new IllegalArgumentException
        ("Unallowed document type: " + DocType.nameFor[typeOfReal] +
         " (can not create a generic document from another generic document)");
    if ( !DocType.hasGeneric[typeOfReal] )
      throw new IllegalArgumentException
        ("Document type \"" + DocType.nameFor[typeOfReal] + "\" has no generic counterpart");

    // Get type:
    int type = DocType.genericOf[typeOfReal];

    // Get category if necessary:
    int category =
      (DocType.hasCategory[type] ? masterOfReal.getCategory() : Category.UNDEFINED);

    // Get name if not set already:
    if ( name == null ) name = masterOfReal.getName();

    // Get description if not set already:
    if ( description == null ) description = masterOfReal.getDescription();

    // Set with and height if necessary:
    if ( DocType.hasWidthAndHeight[type] )
      {
        if ( width == -1 ) width = masterOfReal.getWidth();
        if ( height == -1 ) height = masterOfReal.getHeight();
      }

    // Create a GenericDocument instance:
    GenericDocument document =
      new GenericDocument(type, category, name, description, width, height);

    // Contruct masterFileOfGeneric if necessary:
    if ( !toStdout && masterFileOfGeneric == null )
      masterFileOfGeneric = getDefaultMasterFileOfGeneric(masterOfReal, masterFileOfReal);

    // Check if masterFileOfGeneric already exists:
    if ( masterFileOfGeneric != null && masterFileOfGeneric.exists() && !force )
      throw new IllegalStateException("File exists: " + masterFileOfGeneric);

    // Get output stream:
    PrintStream out =
      (toStdout
       ? this.out
       : new PrintStream(new FileOutputStream(masterFileOfGeneric)));

    // Write master file:
    document.printMaster(out);

    if ( !toStdout )
      this.out.println("Created " + masterFileOfGeneric);
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    final String[] HELP_TEXT =
    {
      "Usage:",
      "  " + COMMAND_NAME + " MASTER_OF_REAL [OPTIONS]",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Creates a generic document for a given real document. MASTER_OF_REAL is the",
      "  master file of the real document. " + COMMAND_NAME + " creates the master file of the",
      "  corresponding generic document and writes it to a file or to stdout (see",
      "  \"Options\" below for details). The type of the generic document is deduced",
      "  from that of the real document. The category, if any, is the same for both",
      "  documents. Name, description, width and height (if any) are copied from the",
      "  real document by default. They can be overwrittem by command line options",
      "  (see below).",
      "Options:",
      "  --output=FILE, -o FILE",
      "      File where the output is written to. If \"-\", the output goes to stdout.",
      "      If not \"-\", FILE must be a master filename. If omitted, a default is",
      "      used. See \"Default output filname\" below for how the default is",
      "      constructed. ",
      "  --name=NAME, -n NAME",
      "      The name of the generic document. Defaults to the name of the real document",
      "  --description=DESCRIPTION, -n DESCRIPTION",
      "      The description of the generic document. Defaults to the description of the",
      "      real document",
      "  --width=WIDTH, -W WIDTH",
      "      The width of the generic document, in pixels. This option is only allowed",
      "      with document types witch have width and height (e.g., images). Defaults",
      "      to the width of the real document.",
      "  --height=HEIGHT, -W HEIGHT",
      "      The height of the generic document, in pixels. This option is only allowed",
      "      with document types witch have height and height (e.g., images). Defaults",
      "      to the height of the real document.",
      "  --force, -f",
      "      Overwrites the output file if it exists already. If this option is not",
      "      set and the output file already exists, an error is signaled.",
      "  --help, -h",
      "      Prints this help message and exits.",
      "  --version, -v",
      "      Prints version information and exits.",
      "Default output filname:",
      "  The default output filename is obtained as follows: If the master file of the",
      "  real document has \"gdim_entry\" elements, the filename is obtained from the",
      "  \"generic_doc_path\" attribute of the first \"gdim_entry\" element. Otherwise, it",
      "  is obtained from the real document master filename by adding \"g_\" to the",
      "  local part and stripping the language code, if any, from the local part. For",
      "  the latter " + COMMAND_NAME + " must know the language codes. To this end, it reads the",
      "  system property net.mumie.cdk.langCodes.",
      "Examples:",
      "  $ " + COMMAND_NAME + " xsl_foo_de.meta.xml",
      "      Provided xsl_foo_de.meta.xml does not contain \"gdim_entry\" elements, the",
      "      above command creates a generic XSL stylesheet for the specified real XSL",
      "      stylesheet and writes the master to the file g_xsl_foo.meta.xml.",
      "  $ " + COMMAND_NAME + " pge_foo_en.meta.xml -o g_pge_bar.meta.xml",
      "      Creates a generic page for the specified real page and writes the master",
      "      to the file g_pge_bar.meta.xml",
      "  $ " + COMMAND_NAME + " pge_foo_en.meta.xml -o -",
      "      Creates a generic page for the specified real page and writes the master",
      "      to stdout.",
    };
    for (String line : HELP_TEXT)
      this.out.println(line);
  }

  /**
   * Prints version information to the shell output.
   */

  public void showVersion ()
    throws Exception
  {
    this.out.println("$Revision: 1.5 $");
  }

  /**
   * Executes the command
   */

  public int execute ()
    throws CommandExecutionException
  {
    try
      {
        this.checkStop();

        final int MMR2GDOC = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMR2GDOC;

        CdkFile masterFileOfReal = null;
        CdkFile masterFileOfGeneric = null;
        String name = null;
        String description = null;
        int width = -1;
        int height = -1;
        boolean force = false;
        boolean toStdout = false;

        this.expandShortOptions("ondWHfhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--output", "-o") )
              {
                String value = paramHelper.getValue();
                if ( value.equals("-") )
                  toStdout = true;
                else
                  masterFileOfGeneric = new CdkFile(this.makeAbsoluteFile(value));
              }
            else if ( paramHelper.checkOptionWithValue("--name", "-n") )
              name = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--description", "-d") )
              description = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--width", "-W") )
              width = Integer.parseInt(paramHelper.getValue());
            else if ( paramHelper.checkOptionWithValue("--height", "-H") )
              height = Integer.parseInt(paramHelper.getValue());
            else if ( paramHelper.checkParam("--force", "-f") )
              force = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( masterFileOfReal == null )
              masterFileOfReal = new CdkFile(this.makeAbsoluteFile(paramHelper.getParam()));
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMR2GDOC:
            if ( masterFileOfReal == null )
              throw new IllegalArgumentException("No real document master file specified");
            this.mmr2gdoc
              (masterFileOfReal,
               masterFileOfGeneric,
               toStdout,
               name, description, width, height,
               force);
            break;
          case SHOW_HELP:
            this.showHelp();
            break;
          case SHOW_VERSION:
            this.showVersion();
            break;
          }

        return 0;
      }
    catch (Exception exception)
      {
        throw new CommandExecutionException(exception);
      }
  }

  /**
   * Auxiliary method; creates the default output file.
   */

  protected CdkFile getDefaultMasterFileOfGeneric (Master masterOfReal,
                                                   CdkFile masterFileOfReal)
    throws Exception
  {
    GDIMEntry[] gdimEntries = masterOfReal.getGDIMEntries();
    if ( gdimEntries.length != 0 )
      return new CdkFile(gdimEntries[0].getGenericDocPath());
    else
      {
        String localName =
          "g_" +
          CdkHelper.getSharedInstance().stripLangCode(masterFileOfReal.getPureName()) +
          "." +
          CdkHelper.MASTER_SUFFIX;
        return new CdkFile(new File(masterFileOfReal.getParent(), localName));
      }
  }
}
