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

package net.mumie.mathletfactory.display;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.util.RadialPaint;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;

/**
 * This class is used to define or change the display properties (e.g. the color, font) for a {@link net.mumie.mathletfactory.mmobject.MMObjectIF}.
 * These properties are common to all mm-objects, even if all of them are not used by a single class.
 *
 * @author Amsel, gronau
 * @mm.docstatus finished
 */
public class DisplayProperties extends DefaultPropertyMap implements Cloneable {
	
  public static final DisplayProperties DEFAULT = new DisplayProperties();
  
	/** Base path for all {@link DisplayProperties} properties. */
	public final static String DP_BASE_PATH = "display.";
	
	//
	// property names
	//

	/** 
	 * Name of the <i>object color</i> property.
	 * @see #getObjectColor()
	 */
	public final static String OBJECT_COLOR_PROPERTY = DP_BASE_PATH + "objectColor";

	/** 
	 * Name of the <i>fontRenderedWithObjectColor</i> property.
	 * @see #isFontRenderedWithObjectColor()
	 */
	public final static String FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY = DP_BASE_PATH + "fontRenderedWithObjectColor";

	/** 
	 * Name of the <i>light color</i> property.
	 * @see #getLightColor()
	 */
	public final static String LIGHT_COLOR_PROPERTY = DP_BASE_PATH + "lightColor";

	/** 
	 * Name of the <i>shadow color</i> property.
	 * @see #getShadowColor()
	 */
	public final static String SHADOW_COLOR_PROPERTY = DP_BASE_PATH + "shadowColor";

	/** 
	 * Name of the <i>shadow offset</i> property.
	 * @see #getShadowOffset()
	 */
	public final static String SHADOW_OFFSET_PROPERTY = DP_BASE_PATH + "shadowOffset";

	/** 
	 * Name of the <i>anti aliasing</i> property.
	 * @see #isAntiAliasing()
	 */
	public final static String ANTI_ALIASING_PROPERTY = DP_BASE_PATH + "antiAliasing";

	/** 
	 * Name of the <i>filled</i> property.
	 * @see #isFilled()
	 */
	public final static String FILLED_PROPERTY = DP_BASE_PATH + "filled";

	/** 
	 * Name of the <i>transparency</i> property.
	 * @see #getTransparency()
	 */
	public final static String TRANSPARENCY_PROPERTY = DP_BASE_PATH + "transparency";

	/** 
	 * Name of the <i>label displayed</i> property.
	 * @see #isLabelDisplayed()
	 */
	public final static String LABEL_DISPLAYED_PROPERTY = DP_BASE_PATH + "labelDisplayed";

	/** 
	 * Name of the <i>border color</i> property.
	 * @see #getBorderColor()
	 */
	public final static String BORDER_COLOR_PROPERTY = DP_BASE_PATH + "borderColor";

	/** 
	 * Name of the <i>border width</i> property.
	 * @see #getBorderWidth()
	 */
	public final static String BORDER_WIDTH_PROPERTY = DP_BASE_PATH + "borderWidth";

	/** 
	 * Name of the <i>selection color</i> property.
	 * @see #getSelectionColor()
	 */
	public final static String SELECTION_COLOR_PROPERTY = DP_BASE_PATH + "selectionColor";

	/** 
	 * Name of the <i>font</i> property.
	 * @see #getFont()
	 */
	public final static String FONT_PROPERTY = DP_BASE_PATH + "font";

	/** 
	 * Name of the <i>dash pattern</i> property.
	 * @see #getDashPattern()
	 */
	public final static String DASH_PATTERN_PROPERTY = DP_BASE_PATH + "dashPattern";

	/** 
	 * Name of the <i>border displayed</i> property.
	 * @see #isBorderDisplayed()
	 */
	public final static String BORDER_DISPLAYED_PROPERTY = DP_BASE_PATH + "drawBorder";

	//
	// default values
	//

  public static final Color SELECTION_COLOR_DEFAULT = Color.BLUE;
  public static Font STD_FONT = MumieTheme.DEFAULT_THEME.getControlTextFont(); //new Font("Serif", Font.PLAIN, 20);
  public static final Color OBJECT_DEFAULT_COLOR = Color.black;
  public static final Color BORDER_DEFAULT_COLOR = Color.black;
  public static final Color LIGHT_DEFAULT_COLOR = new Color(255, 255, 192);
  public static final Color SHADOW_DEFAULT_COLOR = Color.gray;
  public static final Color DARK_GREEN = new Color(0, 180, 0);
  public static final Color VIOLET = new Color(128, 0, 128);
  public static final Color DARK_BLUE = new Color(0, 0, 180);
  public static final Color BROWN = new Color(128, 64, 0);
  public static final Color DARK_RED = new Color(180, 0, 0);
  public static final double J3D_DEFAULT_TEXT_SIZE = 0.07;
  
	/** Default value for a non-dashed pattern. */
	public final static float[] NO_DASH_PATTERN = new float[] { 10f, 0f };
  public static final float[] DASH_PATTERN_DEFAULT = NO_DASH_PATTERN;
  public static final float[] STD_DASH_PATTERN = new float[] {10, 10};
  
  //
  // load default properties into static map
  //
  
  static {
  	loadDefaults();
  }
	

//  private Font m_font;// = STD_FONT;
  //private Point2D m_ShadowOffset = new Point2D.Double(0, 0);
//  private Point2D m_ShadowOffset = null;
  
  public static void loadDefaults() {
  	STD_FONT = MumieTheme.DEFAULT_THEME.getControlTextFont();
  	DEFAULT.setDefaultProperty(OBJECT_COLOR_PROPERTY, OBJECT_DEFAULT_COLOR);
  	DEFAULT.setDefaultProperty(FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY, true);
  	DEFAULT.setDefaultProperty(LIGHT_COLOR_PROPERTY, LIGHT_DEFAULT_COLOR);
  	DEFAULT.setDefaultProperty(SHADOW_COLOR_PROPERTY, SHADOW_DEFAULT_COLOR);
//  	DEFAULT.setDefaultProperty(SHADOW_OFFSET_PROPERTY, null);
  	DEFAULT.setDefaultProperty(ANTI_ALIASING_PROPERTY, true);
  	DEFAULT.setDefaultProperty(FILLED_PROPERTY, true);
  	DEFAULT.setDefaultProperty(TRANSPARENCY_PROPERTY, 0);
  	DEFAULT.setDefaultProperty(LABEL_DISPLAYED_PROPERTY, true);
  	DEFAULT.setDefaultProperty(BORDER_COLOR_PROPERTY, BORDER_DEFAULT_COLOR);
  	DEFAULT.setDefaultProperty(BORDER_WIDTH_PROPERTY, 1);
  	DEFAULT.setDefaultProperty(SELECTION_COLOR_PROPERTY, SELECTION_COLOR_DEFAULT);
  	DEFAULT.setDefaultProperty(FONT_PROPERTY, STD_FONT);
  	DEFAULT.setDefaultProperty(DASH_PATTERN_PROPERTY, DASH_PATTERN_DEFAULT);
  	DEFAULT.setDefaultProperty(BORDER_DISPLAYED_PROPERTY, true);
  }
  
  public DisplayProperties() {
  	this(null);
  }

  public DisplayProperties(DisplayProperties p) {
  	super(p);
//  	if(p != null)
//  		copyPropertiesFrom(p);
  }
  
	/**
	 * Returns the named {@link Color} property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	public Color getColorProperty(String name, Color defaultValue) {
		return (Color) getProperty(name, defaultValue);
	}

	/**
	 * Returns the named {@link Font} property from the internal map. When no value is found, the map will
	 * be quested for the default value. When no default value is found, <code>defaultValue</code>
	 * will be returned.
	 * 
	 * @param name name of a property
	 * @param defaultValue default value if neither an explicit nor a default value exist
	 * @return the property's (default) value or <code>defaultValue</code>
	 */
	public Font getFontProperty(String name, Font defaultValue) {
		return (Font) getProperty(name, defaultValue);
	}
  
  //berechnet die schnittstelle von zwei linien
  protected Point2D intersect(Line2D line1, Line2D line2) {
    double m1 =
      (line1.getP1().getY() - line1.getP2().getY())
      / (line1.getP1().getX() - line1.getP2().getX());
    double m2 =
      (line2.getP1().getY() - line2.getP2().getY())
      / (line2.getP1().getX() - line2.getP2().getX());

    if (Double.isInfinite(m1) && Double.isInfinite(m2))
      return null;

    double a1 = line1.getP1().getY() - m1 * line1.getP1().getX();
    double a2 = line2.getP1().getY() - m2 * line2.getP1().getX();
    //wenn eine linie senkrecht zu x achse.
    if (Double.isInfinite(m1)) {
      return new Point2D.Double(
        line1.getP1().getX(),
        a2 + m2 * line1.getP1().getX());
    }

    if (Double.isInfinite(m2)) {
      return new Point2D.Double(
        line2.getP2().getX(),
        a1 + m1 * line2.getP1().getX());
    }
    // wenn die zwei linien senkrecht zueinander
    if (m1 == m2)
      return null;

    return new Point2D.Double(
                               (a2 - a1) / (m1 - m2),
                               (m1 * a2 - m2 * a1) / (m1 - m2));
  }
  //berechnet die schnittstellen von einem rechteck und einer linie.
  protected Point2D[] intersect(Rectangle2D rect, Line2D line) {
  	Point2D shadowOffset = getShadowOffset();
    if ((shadowOffset.getX() == 0) && (shadowOffset.getY() == 0))
      return new Point2D[] { new Point2D.Double(), new Point2D.Double()};

    Point2D[] result = new Point2D[2];
    Point2D[] intersection =
      new Point2D[] {
      //ruf die methode intesect(line2d,lin2d) auf.
      intersect(
        new Line2D.Double(
                 rect.getX(),
                 rect.getY(),
                 rect.getX() + rect.getWidth(),
                 rect.getY()),
        line),
        intersect(
        new Line2D.Double(
                   rect.getX(),
                   rect.getY(),
                   rect.getX(),
                   rect.getY() + rect.getHeight()),
        line),
        intersect(
        new Line2D.Double(
                   rect.getX() + rect.getWidth(),
                   rect.getY(),
                   rect.getX() + rect.getWidth(),
                   rect.getY() + rect.getHeight()),
        line),
        intersect(
        new Line2D.Double(
                   rect.getX(),
                   rect.getY() + rect.getHeight(),
                   rect.getX() + rect.getWidth(),
                   rect.getY() + rect.getHeight()),
        line)};

    for (int i = 0; i < intersection.length; i++) {
      if (intersection[i] == null)
        continue;
      if (rect.outcode(intersection[i]) == 0)
        if (result[0] == null) {
          result[0] = intersection[i];
        }
        else {
          if (result[0].equals(intersection[i]))
            continue;
          result[1] = intersection[i];
          if ((shadowOffset.getY() < 0) ||
                ((shadowOffset.getY() == 0) && (shadowOffset.getX() < 0))) {
            Point2D temp = result[0];
            result[0] = result[1];
            result[1] = temp;
          }

          return result;
        }
    }
    return null;
  }

  public Paint createGradientPaint(Rectangle2D rect) {
    return createGradientPaint(rect, false);
  }

  public Paint createGradientPaint(Rectangle2D rect, boolean cyclic) {
    double width = rect.getWidth();
    double height = rect.getHeight();
    Point2D center = new Point2D.Double(
      rect.getCenterX() - getShadowOffset().getX(),
      rect.getCenterY() - getShadowOffset().getY());
    return new RadialPaint(center.getX(), center.getY(), getLightColor(),
                           new Point2D.Double(width, height), getObjectColor());
    /*    Point2D[] intersection =
     intersect(
     rect,
     new Line2D.Double(
     rect.getCenterX(),
     rect.getCenterY(),
     rect.getCenterX() + m_ShadowOffset.getX(),
     rect.getCenterY() + m_ShadowOffset.getY()));
     AffineTransform trafo = AffineTransform.getRotateInstance(90);
     intersection[0] = trafo.transform(intersection[0], null);
     intersection[1] = trafo.transform(intersection[1], null);
     return new GradientPaint(
     (float) intersection[0].getX(),
     (float) intersection[0].getY(),
     getLightColor(),
     (float) intersection[1].getX(),
     (float) intersection[1].getY(),
     getObjectColor(),
     cyclic);*/
  }

  /**
   * Sets the object's color.
   */
  public void setObjectColor(Color color) {
  	setProperty(OBJECT_COLOR_PROPERTY, color);
  }

  /**
   * Returns the object's color.
   */
  public Color getObjectColor() {
  	return getColorProperty(OBJECT_COLOR_PROPERTY, OBJECT_DEFAULT_COLOR);
  }

  /**
   * Set, whether any symbolic representation should be rendered in the
   * specified object color.
   */
  public void setFontRenderedWithObjectColor(boolean renderFontWithColor) {
    setProperty(FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY, renderFontWithColor);
  }

  /**
   * Returns true, when any symbolic representation should be rendered in the
   * specified object color.
   */
  public boolean isFontRenderedWithObjectColor() {
  	return getBooleanProperty(FONT_RENDERED_WITH_OBJECT_COLOR_PROPERTY, true);
  }

  /**
   * Sets the light color.
   */
  public void setLightColor(Color color) {
    setProperty(LIGHT_COLOR_PROPERTY, color);
  }

  /**
   * Returns the light color.
   */
  public Color getLightColor() {
    return getColorProperty(LIGHT_COLOR_PROPERTY, LIGHT_DEFAULT_COLOR);
  }

  /**
   * Sets the shadow color.
   */
  public void setShadowColor(Color color) {
    setProperty(SHADOW_COLOR_PROPERTY, color);
  }

  /**
   * Returns the shadow color.
   */
  public Color getShadowColor() {
    return getColorProperty(SHADOW_COLOR_PROPERTY, SHADOW_DEFAULT_COLOR);
  }

  /**
   * Sets the shadow offset.
   * @see #getShadowOffset()
   */
  public void setShadowOffset(Point2D shadowOffset) {
    if (shadowOffset == null)
    	setProperty(SHADOW_OFFSET_PROPERTY, new Point2D.Double());
    else
    	setProperty(SHADOW_OFFSET_PROPERTY, shadowOffset);
  }

  /**
   * Returns the shadow offset, i.e. a point containing the translation offset for the object's shadow
   * relative to the real object.
   * @return a point containing the translation offset or <code>null</code>
   */
  public Point2D getShadowOffset() {
    return (Point2D) getProperty(SHADOW_OFFSET_PROPERTY);
  }

  /**
   * Sets if anit-aliasing should be used.
   */
  public void setAntiAliasing(boolean value) {
    setProperty(ANTI_ALIASING_PROPERTY, value);
  }

  /**
   * Returns whether anit-aliasing is used.
   */
  public boolean isAntiAliasing() {
  	return getBooleanProperty(ANTI_ALIASING_PROPERTY, true);
  }

  /**
   * Sets if the object's visualization should be filled with the object's color.
   */
  public void setFilled(boolean filled) {
    setProperty(FILLED_PROPERTY, filled);
  }

  /**
   * Returns whether the object's visualization is filled with the object's color.
   */
  public boolean isFilled() {
  	return getBooleanProperty(FILLED_PROPERTY, true);
  }

  /**
   * Sets the transparency coefficient.
   * @param transparency a value between 0 and 1
   */
  public void setTransparency(double transparency){
    setProperty(TRANSPARENCY_PROPERTY, transparency);
  }

  /**
   * Returns the transparency coefficient.
   * @return a value between 0 and 1
   */
  public double getTransparency(){
    return getDoubleProperty(TRANSPARENCY_PROPERTY, 0);
  }

  /**
   * Returns whether the label is displayed.
   */
  public boolean isLabelDisplayed(){
  	return getBooleanProperty(LABEL_DISPLAYED_PROPERTY, true);
  }

  /**
   * Sets if the label should be displayed.
   */
  public void setLabelDisplayed(boolean displayed){
    setProperty(LABEL_DISPLAYED_PROPERTY, displayed);
  }

  /**
   * Returns the selection color (foreground color in selected state).
   */
  public Color getSelectionColor() {
  	return getColorProperty(SELECTION_COLOR_PROPERTY, SELECTION_COLOR_DEFAULT);
  }
  
  /**
   * Sets the selection color.
   */
  public void setSelectionColor(Color selectionColor) {
  	setProperty(SELECTION_COLOR_PROPERTY, selectionColor);
  }

  /**
   * Returns the border color.
   */
  public Color getBorderColor() {
  	return getColorProperty(BORDER_COLOR_PROPERTY, BORDER_DEFAULT_COLOR);
  }

  /**
   * Sets the border color.
   */
  public void setBorderColor(Color borderColor) {
  	setProperty(BORDER_COLOR_PROPERTY, borderColor);
  }

  /**
   * Returns the border width.
   */
  public int getBorderWidth() {
    return (int) getDoubleProperty(BORDER_WIDTH_PROPERTY, 1);
  }

  /**
   * Sets the border width.
   */
  public void setBorderWidth(int borderWidth) {
  	setProperty(BORDER_WIDTH_PROPERTY, borderWidth);
  }

  /**
   * Returns <code>true</code> if the shadow offset is not zero.
   * @see #getShadowOffset()
   */
  public boolean hasShadow() {
    if(getShadowOffset() == null)
      return false;
    else
      return (getShadowOffset().getX() != 0) || (getShadowOffset().getY() != 0);
  }

  protected DisplayProperties createInstance() {
    return new DisplayProperties();
  }

  public Object clone() {
    DisplayProperties result = new DisplayProperties();
    copyPropertiesInto(result);
    return result;
  }

  /**
   * Returns the font.
   */
  public Font getFont() {
  	return getFontProperty(FONT_PROPERTY, STD_FONT);
  }

  /**
   * Returns the font if it is not the default font. May return null.
   * @deprecated
   */
  public Font getUserFont() {
  	return null;
  }

  /**
   * Sets the font.
   */
  public void setFont(Font font) {
  	setProperty(FONT_PROPERTY, font);
  }

//  public boolean equals(Object obj){
//    if(!(obj instanceof DisplayProperties))
//      return false;
//    DisplayProperties props = (DisplayProperties) obj;
//    return m_AntiAliasing == props.m_AntiAliasing
//    && m_filled == props.m_filled
//    && m_labelDisplayed == props.m_labelDisplayed
//    && m_fontRenderedWithColor == props.m_fontRenderedWithColor
//    && m_objectColor.equals(props.m_objectColor)
//    && m_borderColor.equals(props.m_borderColor)
//    && m_lightColor.equals(props.m_lightColor)
//    && m_shadowColor.equals(props.m_shadowColor)
//    && (m_font != null && props.m_font != null ? m_font.equals(props.m_font) : true)
//    && m_transparency == props.m_transparency
//    && m_borderWidth == props.m_borderWidth
//    && m_ShadowOffset == props.m_ShadowOffset
//    && m_dashPattern == props.m_dashPattern;
//  }

  /**
   * Returns the g2d dash pattern (i.e. the length of the drawn and invisible stroke).
   * Default is no dashing.
   */
  public float[] getDashPattern() {
  	return (float[]) getProperty(DASH_PATTERN_PROPERTY, DASH_PATTERN_DEFAULT );
  }

  /**
   * Sets the g2d dash pattern (i.e. the length of the drawn and invisible stroke).
   * no dashing.
   */
  public void setDashPattern(float[] fs) {
  	setProperty(DASH_PATTERN_PROPERTY, fs);
  }

  /** 
   * Sets the dashPattern to {@link #STD_DASH_PATTERN}.
   * @deprecated use {@link #setDashed(boolean)} instead
   */
  public void setDashed() {
  	setDashed(true);
  }

  /** 
   * Sets whether a dashed pattern should be used.
   */
  public void setDashed(boolean dashed) {
  	if(dashed)
  		setDashPattern(STD_DASH_PATTERN);
  	else
  		setDashPattern(NO_DASH_PATTERN);
  }

  /** Returns whether the invisible part of the dash pattern is non-zero. */
  public boolean isDashed() {
    return getDashPattern() != NO_DASH_PATTERN;
  }
  
  /**
   * Returns if the border is drawn.
   */
  public boolean isBorderDisplayed() {
  	return getBooleanProperty(BORDER_DISPLAYED_PROPERTY, true);
  }
  
  /**
   * Sets whether the border should be drawn.
   */
  public void setBorderDisplayed( boolean drawBorder ) {
  	setProperty(BORDER_DISPLAYED_PROPERTY, drawBorder);
  }
}
