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

package net.mumie.jvmd.cmdlib;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.util.CmdlineParamHelper;
import net.mumie.util.xml.CachingTransformerProvider;

public class Xtrcache extends AbstractCommand
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The command name (<code>"xtrcache"</code>).
   */

  public static final String COMMAND_NAME = "xtrcache";

  /**
   * A short description of the command
   */

  public static final String COMMAND_DESCRIPTION =
    "Lists the XSL stylesheet cache";

  /**
   * Pattern for date formatting (<code>"yyyy-MM-dd HH:mm:ss S"</code>).
   */

  public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss S";

  /**
   * Date formatter.
   */

  protected DateFormat dateFormat = null;

  // --------------------------------------------------------------------------------
  // Getting name and descriptions
  // --------------------------------------------------------------------------------

  /**
   * Returns the command name (<code>"mmxtr"</code>).
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

  // --------------------------------------------------------------------------------
  // Task implementations
  // --------------------------------------------------------------------------------

  /**
   * Lists the URIs of all cached transformers.
   */

  public void list ()
    throws Exception
  {
    List<URI> list = CachingTransformerProvider.getSharedInstance().listCache();
    Collections.sort(list);
    for (URI uri : list)
      this.out.println(uri);
  }

  /**
   * Prints the creation time of the cache entry of the transformer with the specifed URI.
   */

  public void ctime (URI uri)
    throws Exception
  {
    long created = CachingTransformerProvider.getSharedInstance().getCreationTime(uri);
    if ( this.dateFormat == null )
      this.dateFormat = new SimpleDateFormat(DATE_PATTERN);
    this.out.println(this.dateFormat.format(new Date(created)));
  }

  /**
   * Removes the cache entry of the transformer with the specifed URI.
   */

  public void remove (URI uri)
  {
    // CachingTransformerProvider.getSharedInstance().remove(uri);
  }

  /**
   * Sets the maximum size of the cache.
   */

  public void setlimit (int limit)
    throws Exception
  {
    CachingTransformerProvider.getSharedInstance().setTemplatesCacheMaxSize(limit);
  }

  /**
   * Prints the maximum size of the cache.
   */

  public void getlimit ()
    throws Exception
  {
    this.out.println
      (CachingTransformerProvider.getSharedInstance().getTemplatesCacheMaxSize());
  }

  /**
   * Removes all entries from the cache.
   */

  public void clear ()
  {
    // CachingTransformerProvider.getSharedInstance().clearCache();
  }

  /**
   * Displayes a help text.
   */

  public void showHelp ()
    throws Exception
  {
    // TODO
  }

  /**
   * Displayes version information.
   */

  public void showVersion ()
    throws Exception
  {
    this.out.println("$Revision: 1.2 $");
  }

  // --------------------------------------------------------------------------------
  // Main entry point
  // --------------------------------------------------------------------------------

  /**
   * Execute method.
   */

  public int execute ()
    throws CommandExecutionException 
  {
    try
      {
        final int LIST = 0;
        final int CTIME = 1;
        final int REMOVE = 2;
        final int SETLIMIT = 3;
        final int GETLIMIT = 4;
        final int CLEAR = 5;
        final int SHOW_HELP = 6;
        final int SHOW_VERSION = 7;
        int task = -1;

        URI uri = null;
        int limit = -1;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        if ( !paramHelper.next() )
          throw new IllegalArgumentException("Missing task keyword");

        String firstParam = paramHelper.getParam();
        if ( firstParam.equals("list") )
          {
            task = LIST;
          }
        else if ( firstParam.equals("ctime") )
          {
            task = CTIME;
            if ( !paramHelper.next() )
              throw new IllegalArgumentException("Missing URI");
            uri = new URI(paramHelper.getParam());
          }
        else if ( firstParam.equals("remove") )
          {
            task = REMOVE;
            if ( !paramHelper.next() )
              throw new IllegalArgumentException("Missing URI");
            uri = new URI(paramHelper.getParam());
          }
        else if ( firstParam.equals("setlimit") )
          {
            task = SETLIMIT;
            if ( !paramHelper.next() )
              throw new IllegalArgumentException("Missing cache limit");
            limit = Integer.parseInt(paramHelper.getParam());
          }
        else if ( firstParam.equals("getlimit") )
          {
            task = GETLIMIT;
          }
        else if ( firstParam.equals("clear") )
          {
            task = CLEAR;
          }
        else if ( paramHelper.checkParam("--help", "-h") )
          {
            task = SHOW_HELP;
          }
        else if ( paramHelper.checkParam("--version", "-v") )
          {
            task = SHOW_VERSION;
          }
        else
          throw new IllegalArgumentException
            ("Unknown parameter: " + paramHelper.getParam());

        if ( paramHelper.next() )
          throw new IllegalArgumentException("Extra parameter: " + paramHelper.getParam());

        switch ( task )
          {
          case LIST:
            this.list();
            break;
          case CTIME:
            this.ctime(uri);
            break;
          case REMOVE:
            this.remove(uri);
            break;
          case SETLIMIT:
            this.setlimit(limit);
            break;
          case GETLIMIT:
            this.getlimit();
            break;
          case CLEAR:
            this.clear();
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
}
