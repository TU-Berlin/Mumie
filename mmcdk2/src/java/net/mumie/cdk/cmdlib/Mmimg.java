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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cdk.util.GenericDocument;
import net.mumie.cdk.util.Section;
import net.mumie.cocoon.notions.Category;
import net.mumie.cocoon.notions.DocType;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.io.IOUtil;
import net.mumie.cdk.util.Image;
import java.util.Map;
import java.util.HashMap;
import net.mumie.cdk.CdkConfigParam;

/**
 * Creates an image document from an existing image file.
 *
 * <h4>Usage:</h4>
 * <pre><!-- 
     -->  mmimg <var>IMG_FILE</var> <var>OPTIONS</var>
<!-- -->  mmimg [ --help | -h | --version | -v ]</pre>
 * <h4>Description:</h4>
 * <p>
 *   Creates an image document from <var>IMG_FILE</var>, which may be an image file of one of
 *   the media types supported by Mumie (e.g., png, jpeg). The command creates the
 *   master and content files of the new document. Some of the data needed for the
 *   master file are obtained automatically from <var>IMG_FILE</var> (width, height, media
 *   type), the remaining ones must be specified in <var>OPTIONS</var> (see below). The
 *   content file is created by simply copying <var>IMG_FILE</var>.
 * </p>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--output=<var>FILE</var>, -o <var>FILE</var></code></dt>
 *   <dd>
 *     Name of the master file, possibly with path. This also determines the
 *     name of the content file. If omitted, a default is contructed from
 *     <var>IMG_FILE</var> as follows: the suffix is replaced by the suffix of master
 *     files, and the local name is prefixed with "img_" provided it does not
 *     start with that prefix already.
 *   </dd>
 *   <dt><code>--name=<var>NAME</var>, -n <var>NAME</var></code></dt>
 *   <dd>
 *     The name of the image.
 *   </dd>
 *   <dt><code>--description=<var>DESCRIPTION</var>, -d <var>DESCRIPTION</var></code></dt>
 *   <dd>
 *     The description of the image.
 *   </dd>
 *   <dt><code>--copyright=<var>COPYRIGHT</var>, -c <var>COPYRIGHT</var></code></dt>
 *   <dd>
 *     The copyright statement of the image. Defaults to the system property
 *     <code>net.mumie.cdk.copyright</code>, or the empty string if the latter is not set.
 *   </dd>
 *   <dt><code>--gdim-entry=<var>THEME</var>,<var>GENDOC</var>, -g
 *   <var>THEME</var>,<var>GENDOC</var></code></dt>
 *   <dd>
 *     Specifies an entry in the generic document implementation map (GDIM).
 *     <var>THEME</var> and <var>GENDOC</var> are the master files of the theme and generic document,
 *     respectively, possibly with paths. The language is always set to "zxx"
 *     (non-lingual content). <var>THEME</var> and the delimiting comma may be dropped, in
 *     which case the default theme is used.
 *     This option may be set several times
 *   </dd>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints this help message and exit.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information and exit.
 *   </dd>
 * </dl>
 *  
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmimg.java,v 1.2 2008/09/01 09:51:50 rassy Exp $</code>
 */

public class Mmimg extends AbstractCommand
{
  /**
   * The command name (<code>"mmimg"</code>).
   */

  public static final String COMMAND_NAME = "mmimg";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION =
    "Creates an image document from an existing image file";

  /**
   * Returns the command name (<code>"mmimg"</code>).
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
   * Whether messages are suppressed.
   */

  protected boolean quiet = false;

  /**
   * Prints a message to stdout provided {@link #quiet quiet} is false.
   */

  protected void msg (String message)
  {
    if ( !this.quiet )
      this.out.println(message);
  }

  /**
   * Main task, creates the image document.
   */

  protected void mmimg (CdkFile masterFile,
                        String name,
                        String description,
                        String copyright,
                        Map<String,String> gdimEntries,
                        File imageFile)

    throws Exception
  {
    Image image = new Image(name, description, copyright, gdimEntries, imageFile);
    image.printMaster(masterFile);
    this.msg("Created " + masterFile);
    CdkFile contentFile = masterFile.getContentFile();

    IOUtil.copyFile(imageFile, contentFile);
    this.msg("Created " + contentFile);
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
      "  " + COMMAND_NAME + " IMG_FILE OPTIONS",
      "  " + COMMAND_NAME + " [ --help | -h | --version | -v ]",
      "Description:",
      "  Creates an image document from IMG_FILE, which may be an image file of one of",
      "  the media types supported by Mumie (e.g., png, jpeg). The command creates the",
      "  master and content files of the new document. Some of the data needed for the",
      "  master file are obtained automatically from IMG_FILE (width, height, media",
      "  type), the remaining ones must be specified in OPTIONS (see below). The",
      "  content file is created by simply copying IMG_FILE.",
      "Options:",
      "  --output=FILE, -o FILE",
      "      Name of the master file, possibly with path. This also determines the",
      "      name of the content file. If omitted, a default is contructed from",
      "      IMG_FILE as follows: the suffix is replaced by the suffix of master",
      "      files, and the local name is prefixed with \"img_\" provided it does not",
      "      start with that prefix already.",
      "  --name=NAME, -n NAME",
      "      The name of the image.",
      "  --description=DESCRIPTION, -d DESCRIPTION",
      "      The description of the image.",
      "  --copyright=COPYRIGHT, -c COPYRIGHT",
      "      The copyright statement of the image. Defaults to the system property",
      "      " + CdkConfigParam.COPYRIGHT + ", or the empty string if the latter is not set.",
      "  --gdim-entry=THEME,GENDOC, -g THEME,GENDOC",
      "      Specifies an entry in the generic document implementation map (GDIM).",
      "      THEME and GENDOC are the master files of the theme and generic document,",
      "      respectively, possibly with paths. The language is always set to \"zxx\"",
      "      (non-lingual content). THEME and the delimiting comma may be dropped, in",
      "      which case the default theme is used.",
      "        This option may be set several times",
      "  --help, -h",
      "      Prints this help message and exits.",
      "  --version, -v",
      "      Prints version information and exits.",
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
    this.out.println("$Revision: 1.2 $");
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

        final int MMIMG = 0;
        final int SHOW_HELP = 2;
        final int SHOW_VERSION = 3;
        int task = MMIMG;

        CdkFile masterFile = null;
        File imageFile = null;
        String name = null;
        String description = null;
        String copyright = null;
        Map<String,String> gdimEntries = new HashMap<String,String>();

        this.expandShortOptions("ndcogqhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkOptionWithValue("--name", "-n") )
              name = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--description", "-d") )
              description = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--copyright", "-c") )
              copyright = paramHelper.getValue();
            else if ( paramHelper.checkOptionWithValue("--output", "-o") )
              {
                String value = paramHelper.getValue();
                if ( !value.endsWith("." + CdkHelper.MASTER_SUFFIX) )
                  value += "." + CdkHelper.MASTER_SUFFIX;
                masterFile = new CdkFile(this.makeAbsoluteFile(value));
              }
            else if ( paramHelper.checkOptionWithValue("--gdim-entry", "-g") )
              {
                String value = paramHelper.getValue();
                int sep = value.indexOf(',');
                if ( sep == 0 || sep == value.length()-1 )
                  throw new IllegalArgumentException
                    ("Invalid gdim entry specifyer: " + value);
                String themePath = (sep == -1 ? null : value.substring(0, sep));
                String genDocPath = (sep == -1 ? value : value.substring(sep+1));
                if ( themePath != null )
                  themePath = new CdkFile(this.makeAbsoluteFile(themePath)).getCdkPath();
                genDocPath = new CdkFile(this.makeAbsoluteFile(genDocPath)).getCdkPath();
                gdimEntries.put(themePath, genDocPath);
              }
            else if ( paramHelper.checkParam("--quiet", "-q") )
              this.quiet = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( paramHelper.checkArgument() && imageFile == null )
              imageFile = this.makeAbsoluteFile(paramHelper.getParam());
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMIMG:
            if ( imageFile == null )
              throw new IllegalArgumentException("Missing image file");
            if ( masterFile == null )
              masterFile = this.makeDefaultMasterFile(imageFile);
            this.mmimg(masterFile, name, description, copyright, gdimEntries, imageFile);
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
   * Creates a default for the master filename from the image filename.
   */

  protected CdkFile makeDefaultMasterFile (File imageFile)
    throws Exception
  {
    String name = imageFile.getName();
    int lastDot = name.lastIndexOf('.');
    if ( lastDot != -1 ) name = name.substring(0, lastDot);
    if ( name.indexOf('.') != -1 )
      throw new IllegalArgumentException
        ("Can not create default master filename from: " + imageFile +
         " (default would contain dots)");
    if ( !name.startsWith("img_") ) name = "img_" + name;
    name += "." + CdkHelper.MASTER_SUFFIX;
    return new CdkFile(new File(imageFile.getParent(), name));
  }

  /**
   * Resets this instance for later reuse. Sets {@link #quiet quiet} to false
   * and calls the superclass reset method.
   */

  public void reset ()
  {
    this.quiet = false;
    super.reset();
  }

}
