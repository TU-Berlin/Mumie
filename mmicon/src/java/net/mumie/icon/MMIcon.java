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

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import net.mumie.font.Font;
import net.mumie.font.GlyphString;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.RenderingHints;

public class MMIcon
{
  /**
   * Width of the icon
   */

  protected double width;

  /**
   * Height of the icon
   */

  protected double height;

  /**
   * Color of the icon
   */

  protected Color color;

  /**
   * Thickness of the icon border
   */

  protected double borderThickness;

  /**
   * Color of the icon border
   */

  protected Color borderColor;

  /**
   * Thickness of the icon "extra" border
   */

  protected double extraBorderThickness;

  /**
   * Color of the icon "extra" border
   */

  protected Color extraBorderColor;

  /**
   * Label on the icon.
   */

  protected String label;

  /**
   * Font the label is displayed in.
   */

  protected Font font;

  /**
   * Font size of the label.
   */

  protected double fontSize;

  /**
   * Space between the glyphs of the label
   */

  protected double glyphSpace;

  /**
   * Color of the label
   */

  protected Color labelColor;

  /**
   * Paints a rectangle.
   */

  protected void paintRectangle (Graphics2D g2d,
                                 double x, double y,
                                 double width, double height,
                                 Color color)
  {
    Color savedColor = g2d.getColor();
    g2d.setColor(color);
    g2d.fill(new Rectangle2D.Double(x, y, width, height));
    g2d.setColor(savedColor);
  }

  /**
   * Paints a frame
   */

  protected void paintFrame (Graphics2D g2d,
                             double x, double y,
                             double width, double height,
                             double thickness,
                             Color color)
  {
    Rectangle2D.Double outerRectangle =
      new Rectangle2D.Double(x, y, width, height);
    Rectangle2D.Double innerRectangle = new Rectangle2D.Double
      (x + thickness, y + thickness, width - 2*thickness, height - 2* thickness);
    Area frame = new Area(outerRectangle);
    frame.subtract(new Area(innerRectangle));
    Color savedColor = g2d.getColor();
    g2d.setColor(color);
    g2d.fill(frame);
    g2d.setColor(savedColor);
  }

  /**
   * Paints a text.
   */

  protected void paintText (Graphics2D g2d,
                            double x, double y,
                            String text,
                            Font font,
                            double size,
                            double glyphSpace,
                            Color color)
  {
    GlyphString glstr = new GlyphString(text, font, size, glyphSpace);
    Color savedColor = g2d.getColor();
    g2d.setColor(color);
    glstr.paint(g2d, x - glstr.getWidth() / 2, y + glstr.getHeight() / 2);
    g2d.setColor(savedColor);
  }

  /**
   * Paints this icon
   */

  public void paint (Graphics2D g2d, double x, double y)
  {
    // Background:
    this.paintRectangle(g2d, x, y, this.width, this.height, this.color);

    // Border(s):
    if ( this.extraBorderThickness > 0 )
      {
        // Extra border:
        this.paintFrame
          (g2d,
           x, y,
           this.width, this.height,
           this.extraBorderThickness,
           this.extraBorderColor);
        
        // Border:
        this.paintFrame
          (g2d,
           x + this.extraBorderThickness,
           y + this.extraBorderThickness, 
           this.width - 2*this.extraBorderThickness,
           this.height - 2*this.extraBorderThickness,
           this.borderThickness,
           this.borderColor);
      }
    else
      this.paintFrame
        (g2d, x, y, this.width, this.height, this.borderThickness, this.borderColor);

    // Label:
    if ( this.label != null && this.label.length() > 0 )
      {
        this.paintText
          (g2d, x + this.width / 2, y + this.height / 2, this.label, this.font, this.fontSize,
           this.glyphSpace, this.labelColor);
      }
  }

  /**
   * Creates a new icon
   */

  public MMIcon (double width,
                 double height,
                 Color color,
                 double borderThickness,
                 Color borderColor,
                 double extraBorderThickness,
                 Color extraBorderColor,
                 String label,
                 Font font,
                 double fontSize,
                 double glyphSpace,
                 Color labelColor)
  {
    this.width = width;
    this.height = height;
    this.color = color;
    this.borderThickness = borderThickness;
    this.borderColor = borderColor;
    this.extraBorderThickness = extraBorderThickness;
    this.extraBorderColor = extraBorderColor;
    this.label = label;
    this.font = font;
    this.fontSize = fontSize;
    this.glyphSpace = glyphSpace;
    this.labelColor = labelColor;
  }

  /**
   * Creates a new icon with no extra border.
   */

  public MMIcon (double width,
                 double height,
                 Color color,
                 double borderThickness,
                 Color borderColor,
                 String label,
                 Font font,
                 double fontSize,
                 double glyphSpace,
                 Color labelColor)
  {
    this
      (width,
       height,
       color,
       borderThickness,
       borderColor,
       0,
       null,
       label,
       font,
       fontSize,
       glyphSpace,
       labelColor);
  }

  /**
   * Saves this icon in the specified file.
   * @param file the file to save the icon in
   * @param imageType the image type. See the <code>TYPE_XXX</code> constants of the
   *  {@link BufferedImage BufferedImage} class for what this means.
   * @param antiAliasing if <code>true</code>, anti-aliasing is switched on, otherwise it
   *  is switched off
   * @param formatName a name specifying the format of the image file, for example
   *   <code>"png"</code> or <code>"jpeg"</code>.
   */

  public void save (File file, int imageType, boolean antiAliasing, String formatName)
    throws IOException
  {
    BufferedImage image = new BufferedImage
      ((int)Math.round(this.width), (int)Math.round(this.height), imageType);
    Graphics2D g2d = image.createGraphics();
    if ( antiAliasing )
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    this.paint(g2d, 0, 0);
    ImageIO.write(image, formatName, file);
  }

  /**
   * Saves this icon in the specified file. The format is guessed from the filename.
   * Same as
   * <pre>{@link #save(File,int,boolean,String) save(file, imageType, antiAliasing, guessFormatName(file))}</pre>
   */

  public void save (File file, int imageType, boolean antiAliasing)
    throws IOException
  {
    this.save(file, imageType, antiAliasing, guessFormatName(file));
  }

  /**
   * Saves this icon in the specified file. The image type is set to
   * {@link BufferedImage#TYPE_INT_ARGB TYPE_INT_ARGB}, and the format is guessed from the
   * filename. Same as
   * <pre>{@link #save(File,int,boolean,String) save(file, BufferedImage.TYPE_INT_ARGB, antiAliasing, guessFormatName(file))}</pre>
   */

  public void save (File file, boolean antiAliasing)
    throws IOException
  {
    this.save(file, BufferedImage.TYPE_INT_ARGB, antiAliasing, guessFormatName(file));
  }

  /**
   * Auxiliary method; guesses the image file format from the filename suffix.
   */

  public static String guessFormatName (File file)
  {
    String name = file.getName();
    int lastDot = name.lastIndexOf('.');
    if ( lastDot == -1 || lastDot == name.length()-1 )
      throw new IllegalArgumentException("Filename has no suffix: " + file);
    String formatName = name.substring(lastDot+1, name.length()).toLowerCase();
    if ( formatName.equals("jpg" ) )
      formatName = "jpeg";
    else if ( formatName.equals("tif" ) )
      formatName = "tiff";
    return formatName;
  }

  /**
   * 
   */

  protected static String format (Color color)
  {
    return
      (color == null
       ? "null"
       : color.getRed() + "," +
         color.getGreen() + "," +
         color.getBlue() + "," +
         color.getAlpha());
  }

  /**
   * 
   */

  public String toString ()
  {
    return
      "MMIcon" +
      " width=" + this.width +
      " height=" + this.height +
      " color=" + format(this.color) +
      " borderThickness=" + this.borderThickness +
      " borderColor=" + format(this.borderColor) +
      " extraBorderThickness=" + this.extraBorderThickness +
      " extraBorderColor=" + format(this.extraBorderColor) +
      " label=" + this.label +
      " font=" + this.font.getId() +
      " fontSize=" + this.fontSize +
      " glyphSpace=" + this.glyphSpace +
      " labelColor=" + format(this.labelColor);
  }
}
