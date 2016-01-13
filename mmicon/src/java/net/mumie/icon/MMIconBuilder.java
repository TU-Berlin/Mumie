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

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import net.mumie.util.CmdlineParamHelper;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;


public class MMIconBuilder
{
  /**
   * Name when run as a standalone application.
   */

  protected static String appName =
    System.getProperty("net.mumie.font.MMIconBuilder.appName", "mmbuildicons");

  /**
   * Builds the icons.
   */

  public static void buildIcons (File inputFile,
                                 int imageType,
                                 boolean antiAliasing,
                                 String outputFormat,
                                 PrintStream logStream,
                                 int logLevel)
    throws Exception
  {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxParser = factory.newSAXParser();
    MMIconBuilderSAXHandler saxHandler = new MMIconBuilderSAXHandler
      (imageType, antiAliasing, outputFormat, logStream, logLevel);
    saxParser.parse(inputFile, saxHandler);
  }

  /**
   * Prints a help text to stdout.
   */

  public static void showHelp ()
  {
    final String[] HELP_TEXT =
    {    
      "Usage:",
      "  " + appName + " [OPTIONS] INPUT_FILE",
      "  " + appName + " --help | -h | --version | -v",
      "Description:",
      "  Creates Mumie icons. The icons a described in INPUT_FILE.",
      "Options:",
      "  --format=FORMAT, -f FORMAT",
      "      Sets the format of the image files. Default is \"png\"",
      "  --disable-antialias, -F",
      "      Switches anti-aliasing off. The default is to switch it on.",
      "  --log-level=LEVEL, -l LEVEL",
      "      Sets the log level. Possible values are 0, 1, and 2. Larger values",
      "      result in more detailed logging. Default is 1.",
      "  --help, -h",
      "      Prints this help text to stdout and exits.",
      "  --version, -v",
      "      Prints version information to stdout and exits.",
    };
    for (String line : HELP_TEXT)
      System.out.println(line);
  }

  /**
   * Prints version information to the shell output.
   */

  public static void showVersion ()
  {
    System.out.println("$Revision: 1.2 $");
  }

  /**
   * Main entry point.
   */

  public static void main (String[] params)
    throws Exception
  {
    final int BUILD_ICONS = 0;
    final int SHOW_HELP = 1;
    final int SHOW_VERSION = 2;
    int task = BUILD_ICONS;
    
    File inputFile = null;
    int imageType = BufferedImage.TYPE_INT_ARGB; // Fixed value
    boolean antiAliasing = true;
    String outputFormat = "png";
    PrintStream logStream = System.out; // Fixed value
    int logLevel = 1;

    CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
    while ( paramHelper.next() )
      {
        if ( paramHelper.checkOptionWithValue("--format", "-f") )
          outputFormat = paramHelper.getValue();
        else if ( paramHelper.checkParam("--disable-antialias", "-A") )
          antiAliasing = false;
        else if ( paramHelper.checkOptionWithValue("--log-level", "-l") )
          logLevel = Integer.parseInt(paramHelper.getValue());
        else if ( paramHelper.checkParam("--help", "-h") )
          task = SHOW_HELP;
        else if ( paramHelper.checkParam("--version", "-v") )
          task = SHOW_VERSION;
        else if ( paramHelper.checkArgument() && inputFile == null )
          inputFile = new File(paramHelper.getParam());
        else
          throw new IllegalArgumentException
            ("Illegal parameter: " + paramHelper.getParam());
      }
    
    switch ( task )
      {
      case BUILD_ICONS:
        if ( inputFile == null )
          throw new IllegalArgumentException("No input file specified");
        buildIcons(inputFile, imageType, antiAliasing, outputFormat, logStream, logLevel);
        break;
      case SHOW_HELP:
        showHelp();
        break;
      case SHOW_VERSION:
        showVersion();
        break;
      }
  }
}
