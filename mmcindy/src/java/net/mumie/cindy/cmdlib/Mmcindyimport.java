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

package net.mumie.cindy.cmdlib;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.io.CdkFile;
import net.mumie.cindy.CindyData;
import net.mumie.cindy.CindyUtil;
import net.mumie.cocoon.notions.FileRole;
import net.mumie.cocoon.notions.MediaType;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.jvmd.cmd.CommandStoppedException;
import net.mumie.util.CmdlineParamHelper;
import java.util.Map;
import java.util.regex.Pattern;

public class Mmcindyimport extends AbstractCommand
{
  /**
   * The command name (<code>"mmcindyimport"</code>).
   */

  public static final String COMMAND_NAME = "mmcindyimport";

  /**
   * A short description of the command.
   */

  public static final String COMMAND_DESCRIPTION = "Imports a Cinderella visualization";

  /**
   * Whether messages to stdout are suppressed.
   */

  protected boolean quiet = false;

  /**
   * Pattern to replace illegal characters in names.
   */

  protected static Pattern suggestNamePattern = null;

  /**
   * Returns the command name (<code>"mmcindyimport"</code>).
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
   * Prints a message to stdout and flushes the stream. If {@link #quiet quiet} is true,
   * does nothing (simply returns).
   */

  protected void msg (String message)
  {
    if ( this.quiet ) return;
    this.out.print(message);
    this.out.flush();
  }

  /**
   * Prints a message to stdout and adds a newline. If {@link #quiet quiet} is true, does
   * nothing (simply returns).
   */

  protected void msgln (String message)
  {
    if ( this.quiet ) return;
    this.out.println(message);
  }

  /**
   * Main task, checks the generic document implementations.
   */

  protected int mmcindyimport (String location)
    throws Exception
  {
    URL url = this.makeURL(location);
    CindyData[] docs = CindyUtil.extract(url);

    switch ( docs.length )
      {
      case 0:
        this.msgln("No Cinderella applet found"); break;
      case 1:
        this.msgln("1 Cinderella applet found"); break;
      default:
        this.msgln(docs.length + " Cinderella applets found"); break;
      }
    
    for (CindyData doc : docs)
      {
        // Name of the Cinderella visualization:
        String name = suggestName(doc.getScriptLocation());

        // Needed file objects:
        CdkFile masterFile = this.makeCdkFile
          (name + "." + FileRole.MASTER_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX);
        CdkFile contentFile = this.makeCdkFile
          (name + "." + FileRole.CONTENT_SUFFIX + "." + "jar");
        CdkFile infoPageMasterFile = this.makeCdkFile
          ("pge_" + name + "_info." + FileRole.MASTER_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX);
        CdkFile infoPageContentFile = this.makeCdkFile
          ("pge_" + name + "_info." + FileRole.CONTENT_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX);
        CdkFile genInfoPageMasterFile = this.makeCdkFile
          ("g_pge_" + name + "_info." + FileRole.MASTER_SUFFIX + "." + MediaType.TEXT_XML_SUFFIX);

        try
          {
            // Create content file:
            this.msg("Creating " + contentFile + " ... ");
            URL scriptURL = this.makeURL(doc.getScriptLocation());
            CindyUtil.createContentFile(scriptURL, contentFile);
            this.msgln("done");

            // Create master file:
            this.msg("Creating " + masterFile + " ... ");
            String infoPagePath = genInfoPageMasterFile.getCdkPath();
            CindyUtil.createMasterFile
              (name, doc.getWidth(), doc.getHeight(), infoPagePath, masterFile);
            this.msgln("done");

            // Create info page content file:
            this.msg("Creating " + infoPageContentFile + " ... ");
            Map params = doc.getParams();
            CindyUtil.createInfoPageContentFile(name, params, infoPageContentFile); 
            this.msgln("done");

            // Create info page master file:
            this.msg("Creating " + infoPageMasterFile + " ... ");
            String appletPath = masterFile.getCdkPath();
            String genDocPath = genInfoPageMasterFile.getCdkPath();
            CindyUtil.createInfoPageMasterFile(name, appletPath, genDocPath, infoPageMasterFile); 
            this.msgln("done");

            // Create generic info page master file:
            this.msg("Creating " + genInfoPageMasterFile + " ... ");
            CindyUtil.createGenInfoPageMasterFile(name, genInfoPageMasterFile); 
            this.msgln("done");
          }
        catch(Exception exception)
          {
            this.msgln("failed");
            throw exception;
          }
      }

    return 0;
  }

  /**
   * Prints a help text to stdout.
   */

  public void showHelp ()
    throws Exception
  {
    final String[] HELP_TEXT =
    {    
      "",
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
    this.out.println("$Revision: 1.6 $");
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

        final int MMCINDYIMPORT = 0;
        final int SHOW_HELP = 1;
        final int SHOW_VERSION = 2;
        int task = MMCINDYIMPORT;

        String location = null;

        this.expandShortOptions("qhv");
        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        while ( paramHelper.next() )
          {
            this.checkStop();
            if ( paramHelper.checkParam("--quiet", "-q") )
              this.quiet = true;
            else if ( paramHelper.checkParam("--help", "-h") )
              task = SHOW_HELP;
            else if ( paramHelper.checkParam("--version", "-v") )
              task = SHOW_VERSION;
            else if ( location == null && paramHelper.checkArgument() )
              location = paramHelper.getParam();
            else
              throw new IllegalArgumentException
                ("Illegal parameter: " + paramHelper.getParam());
          }

        switch ( task )
          {
          case MMCINDYIMPORT:
            if ( location == null )
              throw new CommandExecutionException("No source location specified");
            this.mmcindyimport(location);
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
   * Calls the super-class reset method and sets the internal {@link #quiet quiet} flag to
   * false.
   */

  public void reset ()
  {
    super.reset();
    this.quiet = false;
  }

  /**
   * Creates a name for the Cinderella document from the location of the Cindy script.
   */

  protected static String suggestName (String scriptLocation)
  {
    String name = scriptLocation;
    int startPos = name.lastIndexOf('/') + 1;
    name = name.substring(startPos, name.length());
    if ( name.endsWith(".cdy") ) name = name.substring(0, name.length() - ".cdy".length());
//     if ( suggestNamePattern == null )
//       suggestNamePattern = Pattern.compile("[ .-:]+");
//     name = suggestNamePattern.matcher(name).replaceAll("_");
    return name;
  }

  /**
   * Creates an URL object from the specified location string.
   */

  protected URL makeURL (String location)
    throws Exception
  {
    URL url = null;
    try
      {
        url = new URL(location);
      }
    catch (MalformedURLException ignoredException)
      {
        url = this.makeAbsoluteFile(location).toURI().toURL();
      }
    return url;
  }

  /**
   * Creates an cdk file object from the specified filename.
   */

  protected CdkFile makeCdkFile (String filename)
    throws Exception
  {
    return new CdkFile(this.makeAbsoluteFile(filename));
  }
}
