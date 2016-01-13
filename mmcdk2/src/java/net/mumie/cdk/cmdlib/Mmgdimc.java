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

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import net.mumie.cdk.CdkHelper;
import net.mumie.cdk.util.GenericDocumentResolver;
import net.mumie.jvmd.cmd.AbstractCommand;
import net.mumie.jvmd.cmd.CommandExecutionException;
import net.mumie.util.CmdlineParamHelper;

/**
 * <h4>Usage:</h4>
 * <pre><!--
     -->  mmgdimc list | setlimit <var>LIMIT</var> | getlimit | clear
<!-- -->  mmgdimc --help | -h | --version | -v</pre>
 * <h4>Description:</h4>
 * <p>
 *   Controls the GDIM cache. Unless called with the --help, -h, --version, or -v
 *   option, the first argument specifies the task to execute.
 * </p>
 * <h4>Tasks:</h4>
 * <dl>
 *   <dt><code>list</code></dt>
 *   <dd>
 *     Lists the cache entries. The output is a list of items of the form
 *     <pre><!--
 * -->  <var>PATH_OF_GENERIC</var>
 * <!-- -->   ---&gt; <var>PATH_OF_REAL</var> <var>TIMESTAMP</var></pre>
 *     where <var>PATH_OF_GENERIC</var> and <var>PATH_OF_REAL</var> are the paths of the generic
 *     and corresponding real document, respectively. <var>TIMESTAMP</var> is the time when
 *     the cache entry was created.
 *   </dd>
 *   <dt><code>setlimit <var>LIMIT</var></code></dt>
 *   <dd>
 *     Sets the maximum number cache entries to <var>LIMIT</var>. -1 means unlimited.
 *   </dd>
 *   <dt><code>getlimit</code></dt>
 *   <dd>
 *     Prints the maximum number of cache entries.
 *   </dd>
 *   <dt><code>clear</code></dt>
 *   <dd>
 *     Clears the cache.
 *   </dd>
 * </dl>
 * <h4>Options:</h4>
 * <dl>
 *   <dt><code>--help, -h</code></dt>
 *   <dd>
 *     Prints this help message and exits.
 *   </dd>
 *   <dt><code>--version, -v</code></dt>
 *   <dd>
 *     Prints version information and exits.
 *   </dd>
 * </dl>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: Mmgdimc.java,v 1.3 2008/01/28 23:59:38 rassy Exp $</code> 
 */

public class Mmgdimc extends AbstractCommand
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The command name (<code>"mmgdimc"</code>).
   */

  public static final String COMMAND_NAME = "mmgdimc";

  /**
   * A short description of the command
   */

  public static final String COMMAND_DESCRIPTION =
    "Controls the GDIM cache";

  /**
   * Pattern for date formatting (<code>"yyyy-MM-dd HH:mm:ss S"</code>).
   */

  public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss S";

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
   * Lists the cache.
   */

  public void list ()
    throws Exception
  {
    // Get the generic document resolver:
    GenericDocumentResolver genDocResolver =
      CdkHelper.getSharedInstance().getGenericDocumentResolver();

    // Setup date formatter:
    DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    synchronized (genDocResolver)
      {
        // Get a sorted array of all cached generic document paths:
        String[] genDocPaths = genDocResolver.listCache();
        Arrays.sort(genDocPaths);

        // List the cache:
        for (String genDocPath : genDocPaths)
          {
            this.checkStop();
            GenericDocumentResolver.CacheRecord cacheRecord =
              genDocResolver.getCacheRecord(genDocPath);
            String realDocPath = cacheRecord.getPathOfReal();
            long created = cacheRecord.getCreated();
            this.out.println(stripMasterSuffix(genDocPath));
            this.out.println
              (" ---> " + stripMasterSuffix(realDocPath) +
               " " + dateFormat.format(new Date(created)));
            this.out.println();
          }

        // Print total cache size:
        this.out.println(genDocPaths.length + " entries");
      }
  }

  /**
   * Sets the maximum size of the cache.
   */

  public void setlimit (int limit)
    throws Exception
  {
    CdkHelper.getSharedInstance().getGenericDocumentResolver().setCacheMaxSize(limit);
  }

  /**
   * Prints the maximum size of the cache.
   */

  public void getlimit ()
    throws Exception
  {
    this.out.println
      (CdkHelper.getSharedInstance().getGenericDocumentResolver().getCacheMaxSize());
  }

  /**
   * Removes all entries from the cache.
   */

  public void clear ()
    throws Exception
  {
    CdkHelper.getSharedInstance().getGenericDocumentResolver().clearCache();
  }

  /**
   * Displayes a help text.
   */

  public void showHelp ()
    throws Exception
  {
    final String[] HELP_TEXT =
    {
      "Usage:",
      "  " + COMMAND_NAME + " list | setlimit LIMIT | getlimit | clear",
      "  " + COMMAND_NAME + " --help | -h | --version | -v",
      "Description:",
      "  Controls the GDIM cache. Unless called with the --help, -h, --version, or -v",
      "  option, the first argument specifies the task to execute.",
      "Tasks:",
      "  list",
      "      Lists the cache entries. The output is a list of items of the form",
      "          PATH_OF_GENERIC",
      "           ---> PATH_OF_REAL TIMESTAMP",
      "      where PATH_OF_GENERIC and PATH_OF_REAL are the paths of the generic",
      "      and corresponding real document, respectively. TIMESTAMP is the time when",
      "      the cache entry was created.",
      "  setlimit LIMIT",
      "      Sets the maximum number cache entries to LIMIT. -1 means unlimited.",
      "  getlimit",
      "      Prints the maximum number of cache entries.",
      "  clear",
      "      Clears the cache.",
      "Options:",
      "  --help, -h",
      "      Prints this help message and exits.",
      "  --version, -v",
      "      Prints version information and exits.",
    };
    for (String line : HELP_TEXT)
      this.out.println(line);
  }

  /**
   * Displayes version information.
   */

  public void showVersion ()
    throws Exception
  {
    this.out.println("$Revision: 1.3 $");
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
        final int SETLIMIT = 1;
        final int GETLIMIT = 2;
        final int CLEAR = 3;
        final int SHOW_HELP = 4;
        final int SHOW_VERSION = 5;
        int task = -1;

        int limit = -1;

        CmdlineParamHelper paramHelper = new CmdlineParamHelper(this.params);
        if ( !paramHelper.next() )
          throw new IllegalArgumentException("Missing task keyword");

        String firstParam = paramHelper.getParam();
        if ( firstParam.equals("list") )
          {
            task = LIST;
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

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  protected static String stripMasterSuffix (String path)
  {
    return
      (path.endsWith(CdkHelper.MASTER_SUFFIX)
       ? path.substring(0, path.length() - CdkHelper.MASTER_SUFFIX.length() -1)
       : path);
  }
}
