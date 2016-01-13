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

package net.mumie.font.extra;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import net.mumie.font.Font;
import net.mumie.font.FontLoader;
import net.mumie.font.Glyph;
import java.awt.geom.Rectangle2D;
import net.mumie.util.CmdlineParamHelper;

public class GlyphViewer extends JFrame
{
  /**
   * The path of the glyph
   */

  protected Path2D.Double path = new Path2D.Double();

  /**
   * Outline of the bounding box.
   */

  protected Rectangle2D boundingBox = null;

  /**
   * Outline of the size box.
   */

  protected Rectangle2D sizeBox = null;

  /**
   * Name when run as a standalone application.
   */

  protected static String appName =
    System.getProperty("net.mumie.font.extra.GlyphViewer.appName", "mmviewglyph");

  /**
   * Paints the window.
   */

  public void paint (Graphics graphics)
  {
    try
      {
        Graphics2D g2d = (Graphics2D)graphics;
        g2d.setRenderingHint
          (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint bounding box:
        g2d.setColor(Color.GREEN);
        g2d.draw(this.boundingBox);

        // Paint size box:
        g2d.setColor(Color.RED);
        g2d.draw(this.sizeBox);

        // Paint glyph:
        g2d.setColor(Color.BLACK);
        g2d.fill(this.path);
      }
    catch (Exception exception)
      {
        throw new RuntimeException(exception);
      }
  }

  /**
   * Creates a new instance
   */

  public GlyphViewer (Font font,
                      String glyphName,
                      double size,
                      int frameWidth,
                      int frameHeight,
                      double x,
                      double y)
  {
    super("Glyph Viewer");
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(frameWidth, frameHeight);
    this.setBackground(Color.WHITE);
    Glyph glyph = new Glyph(font.getGlyphForName(glyphName), size/font.getSize());
    glyph.getOutline().toPath(this.path, x, y);
    this.boundingBox = new Rectangle2D.Double
      (x + glyph.getX(),
       y - glyph.getY() - glyph.getHeight(),
       glyph.getWidth(),
       glyph.getHeight());
    this.sizeBox = new Rectangle2D.Double(x, y - size, size, size);
    this.pack();
  }

  /**
   * Prints a help text to stdout.
   */

  public static void showHelp ()
  {
    final String[] HELP_TEXT =
    {    
      "Usage:",
      "  " + appName + " [OPTIONS] FONT_FILE GLYPH_NAME",
      "  " + appName + " --help | -h | --version | -v",
      "Description:",
      "  Displays a glyph on the screen. FONT_FILE is the filename of the font which",
      "  contains the glyph, GLYPH_NAME the name of the glyph.",
      "Options:",
      "  --size=SIZE, -s SIZE",
      "      Sets the size of the displayed glyph. May be any non-negative number.",
      "      Non-integral values are allowed; however, the value is interpreted as a",
      "      pixel size. Defauls to 100.",
      "  --frame-width=WIDTH, -W WIDTH",
      "      Sets the width of the window which displays the glyph. Defauls to 300.",
      "  --frame-height=HEIGHT, -H HEIGHT",
      "      Sets the height of the window which displays the glyph. Defauls to 300.",
      "  --glyph-pos-x=POS_X, -x POS_X",
      "      Sets the x coordinate of the bottom-left edge of the glyph. Defaults to",
      "      100",
      "  --glyph-pos-y=POS_Y, -y POS_Y",
      "      Sets the y coordinate of the bottom-left edge of the glyph. Defaults to",
      "      200",
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

  public static void main (final String[] params)
  {
    SwingUtilities.invokeLater
      (new Runnable ()
        {
          public void run ()
          {
            try
              {
                final int VIEW_GLYPH = 0;
                final int SHOW_HELP = 1;
                final int SHOW_VERSION = 2;
                int task = VIEW_GLYPH;
    
                File fontFile = null;
                String glyphName = null;
                double size = 100.0;
                int frameWidth = 300;
                int frameHeight = 300;
                double x = 100;
                double y = 200;

                CmdlineParamHelper paramHelper = new CmdlineParamHelper(params);
                while ( paramHelper.next() )
                  {
                    if ( paramHelper.checkOptionWithValue("--size", "-s") )
                      size = Double.parseDouble(paramHelper.getValue());
                    else if ( paramHelper.checkOptionWithValue("--frame-width", "-W") )
                      frameWidth = Integer.parseInt(paramHelper.getValue());
                    else if ( paramHelper.checkOptionWithValue("--frame-height", "-H") )
                      frameHeight = Integer.parseInt(paramHelper.getValue());
                    else if ( paramHelper.checkOptionWithValue("--glyph-pos-x", "-x") )
                      x = Double.parseDouble(paramHelper.getValue());
                    else if ( paramHelper.checkOptionWithValue("--glyph-pos-y", "-y") )
                      y = Double.parseDouble(paramHelper.getValue());
                    else if ( paramHelper.checkParam("--help", "-h") )
                      task = SHOW_HELP;
                    else if ( paramHelper.checkParam("--version", "-v") )
                      task = SHOW_VERSION;
                    else if ( paramHelper.checkArgument() && fontFile == null )
                      fontFile = new File(paramHelper.getParam());
                    else if ( paramHelper.checkArgument() && glyphName == null )
                      glyphName = paramHelper.getParam();
                    else
                      throw new IllegalArgumentException
                        ("Illegal parameter: " + paramHelper.getParam());
                  }
    
                switch ( task )
                  {
                  case VIEW_GLYPH:
                    if ( fontFile == null )
                      throw new IllegalArgumentException("No font specified");
                    if ( glyphName == null )
                      throw new IllegalArgumentException("No glyph specified");
                    FontLoader fontLoader = new FontLoader();
                    Font font = fontLoader.loadXMLFont(fontFile);
                    final GlyphViewer glyphViewer = new GlyphViewer
                      (font, glyphName, size, frameWidth, frameHeight, x, y);
                    glyphViewer.setVisible(true);
                    break;
                  case SHOW_HELP:
                    showHelp();
                    break;
                  case SHOW_VERSION:
                    showVersion();
                    break;
                  }
              }
            catch (Exception exception)
              {
                throw new RuntimeException(exception);
              }
          }
        });
  }
}
