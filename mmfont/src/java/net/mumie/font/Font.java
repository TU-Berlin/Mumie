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

package net.mumie.font;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Font
{
  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The font id.
   */

  protected String id;

  /**
   * The name of the font.
   */

  protected String name;

  /**
   * The font family.
   */

  protected String family;

  /**
   * The font style (normal, italic, or oblique).
   */

  protected int style;

  /**
   * The font weight (normal or bold).
   */

  protected int weight;

  /**
   * The font size.
   */

  protected double size;

  /**
   * The glyphs of the font.
   */

  protected Glyph[] glyphs;

  /**
   * Maps glyph names to glyphs
   */

  protected Map<String,Glyph> glyphsPerName;

  /**
   * Maps unicode sequences to glyphs
   */

  protected Map<String,Glyph> glyphsPerUnicode;

  // --------------------------------------------------------------------------------
  // h1: Accessing font properties
  // --------------------------------------------------------------------------------

  /**
   * Returns the font id.
   */

  public final String getId ()
  {
    return this.id;
  }

  /**
   * Returns the font name.
   */

  public final String getName ()
  {
    return this.name;
  }

  /**
   * Returns the font family.
   */

  public final String getFamily ()
  {
    return this.family;
  }

  /**
   * Returns the font style (normal, italic, or oblique). The returned value is one of the
   * values in {@link FontStyle FontStyle}.
   */

  public final int getStyle ()
  {
    return this.style;
  }

  /**
   * Returns the font weight (normal, italic, or oblique). The returned value is one of the
   * values in {@link FontWeight FontWeight}.
   */

  public final int getWeight ()
  {
    return this.weight;
  }

  /**
   * Returns the font size.
   */

  public final double getSize ()
  {
    return this.size;
  }

  // --------------------------------------------------------------------------------
  // h1: Getting glyphs
  // --------------------------------------------------------------------------------

  /**
   * Returns the glyphs of this font. The glyphs array is copied, so changes in the returned
   * array do not affect this object.
   */

  public Glyph[] getGlyphs ()
  {
    Glyph[] glyphs = new Glyph[this.glyphs.length];
    System.arraycopy(this.glyphs, 0, glyphs, 0, this.glyphs.length);
    return glyphs;
  }

  /**
   * Returns the glyph for the specified name, or null if no such glyph exists in this
   * font. 
   */

  public Glyph getGlyphForName (String name)
  {
    return this.glyphsPerName.get(name);
  }

  /**
   * Returns the glyph for the specified unicode sequence, or null if no such glyph exists
   * in this font. 
   */

  public Glyph getGlyphForUnicode (String unicode)
  {
    return this.glyphsPerUnicode.get(unicode);
  }

  /**
   * Returns the glyph for the specified unicode character, or null if no such glyph exists
   * in this font. 
   */

  public Glyph getGlyphForUnicode (char c)
  {
    return this.glyphsPerUnicode.get(Character.toString(c));
  }

  /**
   * Returns all glyph names as an array.
   */

  public String[] getGlyphNames ()
  {
    Set<String> nameSet = this.glyphsPerName.keySet();
    return nameSet.toArray(new String[nameSet.size()]);
  }

  // --------------------------------------------------------------------------------
  // h1: Constructors
  // --------------------------------------------------------------------------------

  /**
   * Creates a new instance from the specified data.
   */

  public Font (String id,
               String name,
               String family,
               int style,
               int weight,
               double size,
               Glyph[] glyphs)
  {
    this.id = id;
    this.name = name;
    this.family = family;
    this.style = style;
    this.weight = weight;
    this.size = size;
    this.glyphs = glyphs;

    this.glyphsPerName = new HashMap<String,Glyph>();
    for (Glyph glyph : glyphs)
      this.glyphsPerName.put(glyph.getName(), glyph);

    this.glyphsPerUnicode = new HashMap<String,Glyph>();
    for (Glyph glyph : glyphs)
      this.glyphsPerUnicode.put(glyph.getUnicode(), glyph);
  }
}
