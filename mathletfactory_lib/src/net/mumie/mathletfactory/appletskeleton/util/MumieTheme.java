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

package net.mumie.mathletfactory.appletskeleton.util;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.UIDefaults;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

/**
 * This class allowes to read default values for GUI components (such as
 * background colors) from a file using an implementation of
 * {@link javax.swing.plaf.metal.DefaultMetalTheme}.
 * <p>
 * Three levels of manipulation exist:
 * <ol>
 * <li>using the primary[1..3] and secondary[1..3] keys and black/white which
 * are used to fill in the default values for all gui components. The values
 * are assigned by the Metal- and BasicLookAndFeel's (who call moreover the
 * primary/secondary/black/white getter methods).
 *
 * <li>using the theme proper getter-methods (such as getControl(),
 * getUserTextColor(),..). The keys to be used are called "control",
 * "userTextColor", ... (whitout "get", small first letter, w/o paranthesis :-] ).
 *
 * <li>using the gui table proper keys (e.g. "Button.background",
 * "Label.font"). All keys can be found in
 * {@link javax.swing.plaf.metal.MetalLookAndFeel MetalLookAndFeel}and
 * {@link javax.swing.plaf.metal.DefaultMetalTheme DefaultMetalTheme}.
 * </ol>
 * <p>
 * Colors must be written in RGB manner, with a comma seperating the numbers
 * (e.g. red: primary1=255,0,0 )
 * <p>
 * 
 * @see javax.swing.plaf.metal.MetalLookAndFeel
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class MumieTheme extends DefaultMetalTheme {

//	/** Location of default theme file. */
//	public static final String DEFAULT_THEME_LOCATION =
//		"/resource/settings/DefaultTheme.theme";

	public static final MumieTheme DEFAULT_THEME = new MumieTheme();

	/** Name of this theme. */
	private final String name = "Mumie Theme";

	/**
	 * Table containing theme relevant properties (color definitions, font names,
	 * ...) read from the theme file.
	 */
	private Hashtable m_themePropsTable = new Hashtable();

	/** Table containing custom entries. */
	private Hashtable m_customTableEntries = new Hashtable();

	/** Table containing default values. */
	private Hashtable m_cachedValues = new Hashtable();

	/** Creates an instance of MumieTheme using the default theme. */
	private MumieTheme() {
	}

	/**
	 * Puts the <code>custom table entries</code> into the Look&Feel table.
	 * This method is called from within the look and feel which allowes to
	 * change entries in the gui default table by giving a reference to it. Note:
	 * only valid keys are processed by the "UI-classes" of the gui components.
	 */
	public void addCustomEntriesToTable(UIDefaults table) {
		Enumeration keys = m_customTableEntries.keys();
		Enumeration values = m_customTableEntries.elements();
		while (keys.hasMoreElements()) {
			table.put(keys.nextElement(), values.nextElement());
		}
	}

	/**
	 * Adds or overwrites a property. This method works parallel to the theme
	 * files.
	 * @see #addCustomEntriesToTable(UIDefaults)
	 */
	public void addCustomProperty(Object key, Object value) {
		m_customTableEntries.put(key, value);
	}
	
	public static void addThemeProperties(PropertyMapIF map) {
		Property[] properties = map.getProperties();
		for(int i = 0; i < properties.length; i++) {
			Property p = properties[i];
			if(p.getType() == Property.STRING_PROPERTY || p.getType() == Property.INTEGER_PROPERTY) {
				DEFAULT_THEME.m_themePropsTable.put(p.getName(), p.getValue());
				DEFAULT_THEME.m_customTableEntries.put(p.getName(), p.getValue());
				continue;
			}
			UIResource uip = null;
			if(p.getType() == Property.COLOR_PROPERTY)
				uip = new ColorUIResource((Color) p.getValue());
			else if(p.getType() == Property.FONT_PROPERTY)
				uip = new FontUIResource((Font) p.getValue());
			else if(p.getType() == Property.BORDER_PROPERTY)
				uip = new BorderUIResource((Border) p.getValue());
			else
				throw new IllegalArgumentException("Property is not a MumieTheme property: " + p);
			DEFAULT_THEME.m_themePropsTable.put(p.getName(), uip);
			DEFAULT_THEME.m_customTableEntries.put(p.getName(), uip);
		}
		// remove cached values
		DEFAULT_THEME.m_cachedValues.clear();
	}

	/**
	 * Parses a comma delimited list of 3 strings into a ColorUIResource
	 * instance.
	 */
	public ColorUIResource parseColor(String s) {
		int red = 0;
		int green = 0;
		int blue = 0;
		try {
			StringTokenizer st = new StringTokenizer(s, ",");

			red = Integer.parseInt(st.nextToken());
			green = Integer.parseInt(st.nextToken());
			blue = Integer.parseInt(st.nextToken());

		} catch (Exception e) {
			return null;
		}
		return new ColorUIResource(red, green, blue);
	}

	public FontUIResource parseFont(String font) {
		String name = "Dialog";
		int style = 0, size = 12;
		try {
			StringTokenizer st = new StringTokenizer(font, "-, ");
			name = st.nextToken();
			String s = st.nextToken();
			if (s.equalsIgnoreCase("PLAIN"))
				style = Font.PLAIN;
			else if (s.equalsIgnoreCase("BOLD"))
				style = Font.BOLD;
			else if (s.equalsIgnoreCase("ITALIC"))
				style = Font.ITALIC;
			else if (s.equalsIgnoreCase("BOLD-ITALIC"))
				style = Font.BOLD | Font.ITALIC;
			else
				throw new IllegalUsageException();

			size = Integer.parseInt(st.nextToken());

		} catch (Exception e) {
			return null;
		}
		return new FontUIResource(new Font(name, style, size));
	}

	/**
	 * Returns the property with the given key or null if no such key is found.
	 */
	public static Object getThemeProperty(String key) {
		return DEFAULT_THEME.m_themePropsTable.get(key);
	}

	/**
	 * Returns the property with the given key or defaultValue if no such key is found.
	 */
	public static Object getThemeProperty(String key, Object defaultValue) {
		Object o = DEFAULT_THEME.m_themePropsTable.get(key);
		if(o == null)
			return defaultValue;
		else
			return o;
	}

	/**
	 * Sets a theme property from inside Java
	 */
	public static void setThemeProperty(String key, Object value) {
		DEFAULT_THEME.m_themePropsTable.put(key, value);
	}
	
	private static Object getCachedValue(String key) {
		return DEFAULT_THEME.m_cachedValues.get(key);
	}

	private static void setCachedValue(String key, Object value) {
		DEFAULT_THEME.m_cachedValues.put(key, value);
	}

	/*
	 * MathletFactory-methods
	 */
	public static Color getCanvasBackground() {
		ColorUIResource colorRes = ((ColorUIResource)getThemeProperty("canvasBackground"));
		if(colorRes != null)
			return new Color(colorRes.getRed(), colorRes.getGreen(), colorRes.getBlue());
		else
			return null;
	}
	
	public static Color getDefaultBackground() {
		return DEFAULT_THEME.getSecondary3();
	}
	
	public static Color getTextColor() {
		return DEFAULT_THEME.getControlTextColor();
	}
	
	public static String getDefaultFontName() {
		String fontName = (String) getThemeProperty("defaultFontName");
		if(fontName == null)
			fontName = "Dialog";
		return fontName;
	}
	
	public static int getTextFontSize() {
		Integer fontSize = (Integer) getThemeProperty("textFontSize");
		if(fontSize == null) {
			fontSize = (Integer) getCachedValue("textFontSize");
			if(fontSize == null) {
				fontSize = new Integer(14);
				setCachedValue("textFontSize", fontSize);
			}
		}
		return fontSize.intValue();
	}
	
	public static FontUIResource getTextFont() {
		FontUIResource font = (FontUIResource) getThemeProperty("textFont");
		if(font == null) {
			font = (FontUIResource) getCachedValue("textFont");
			if(font == null) {
				int fontSize = getTextFontSize();
				String fontStyle = "PLAIN";
				font = new FontUIResource(DEFAULT_THEME.parseFont(getDefaultFontName()+"-"+fontStyle+"-"+fontSize));
				setCachedValue("textFont", font);
			}
		}
		return font;
	}

	public static FontUIResource getBoldTextFont() {
		FontUIResource font = (FontUIResource) getThemeProperty("boldTextFont");
		if(font == null) {
			font = (FontUIResource) getCachedValue("boldTextFont");
			if(font == null) {
				font = new FontUIResource(getTextFont().deriveFont(Font.BOLD));
				setCachedValue("boldTextFont", font);
			}
		}
		return font;
	}

	public static FontUIResource getTitleFont() {
		FontUIResource font = (FontUIResource) getThemeProperty("titleFont");
		if(font == null) {
			font = (FontUIResource) getCachedValue("titleFont");
			if(font == null) {
				font = new FontUIResource(getTextFont().deriveFont(20f));
				setCachedValue("titleFont", font);
			}
		}
		return font;
	}

	public static FontUIResource getDialogTitleFont() {
		FontUIResource font = (FontUIResource) getThemeProperty("dialogTitleFont");
		if(font == null) {
			font = (FontUIResource) getCachedValue("dialogTitleFont");
			if(font == null) {
				font = new FontUIResource(getTextFont().deriveFont(14f).deriveFont(Font.BOLD));
				setCachedValue("dialogTitleFont", font);
			}
		}
		return font;
	}

	public String getName() {
		return name;
	}

	/*
	 * Font settings
	 */
	
	public FontUIResource getControlTextFont() {
		FontUIResource font = (FontUIResource)m_themePropsTable.get("controlTextFont");
		if(font == null) {
			font = (FontUIResource)m_cachedValues.get("controlTextFont");
			if(font == null) {
				font = new FontUIResource(getBoldTextFont());
				m_cachedValues.put("controlTextFont", font);
			}
		}
		return font;
	}

	public FontUIResource getSystemTextFont() {
		FontUIResource font = (FontUIResource)m_themePropsTable.get("systemTextFont");
		if(font == null) {
			font = (FontUIResource)m_cachedValues.get("systemTextFont");
			if(font == null) {
				font = new FontUIResource(getTextFont());
				m_cachedValues.put("systemTextFont", font);
			}
		}
		return font;
	}

	public FontUIResource getUserTextFont() {
		FontUIResource font = (FontUIResource)m_themePropsTable.get("userTextFont");
		if(font == null) {
			font = (FontUIResource)m_cachedValues.get("userTextFont");
			if(font == null) {
				font = new FontUIResource(getTextFont());
				m_cachedValues.put("userTextFont", font);
			}
		}
		return font;
	}

	public FontUIResource getMenuTextFont() {
		FontUIResource font = (FontUIResource)m_themePropsTable.get("menuTextFont");
		if(font == null) {
			font = (FontUIResource)m_cachedValues.get("menuTextFont");
			if(font == null) {
				font = new FontUIResource(getTextFont().deriveFont(12f));
				m_cachedValues.put("menuTextFont", font);
			}
		}
		return font;
	}

	public FontUIResource getWindowTitleFont() {
		FontUIResource font = (FontUIResource)m_themePropsTable.get("windowTitleFont");
		if(font == null) {
			font = (FontUIResource)m_cachedValues.get("windowTitleFont");
			if(font == null) {
				font = new FontUIResource(getTextFont().deriveFont(10f));
				m_cachedValues.put("windowTitleFont", font);
			}
		}
		return font;
	}

	public FontUIResource getSubTextFont() {
		FontUIResource font = (FontUIResource)m_themePropsTable.get("subTextFont");
		if(font == null) {
			font = (FontUIResource)m_cachedValues.get("subTextFont");
			if(font == null) {
				font = new FontUIResource(getTextFont().deriveFont(12f));
				m_cachedValues.put("subTextFont", font);
			}
		}
		return font;
	}

	/*
	 * Color settings
	 */

	protected ColorUIResource getPrimary1() {
		ColorUIResource value = (ColorUIResource)m_themePropsTable.get("primary1");
		if (value != null)
			return value;
		else
			return super.getPrimary1();
	}

	protected ColorUIResource getPrimary2() {
		ColorUIResource value = (ColorUIResource)m_themePropsTable.get("primary2");
		if (value != null)
			return value;
		else
			return super.getPrimary2();
	}

	protected ColorUIResource getPrimary3() {
		ColorUIResource value = (ColorUIResource)m_themePropsTable.get("primary3");
		if (value != null)
			return value;
		else
			return super.getPrimary3();
	}

	protected ColorUIResource getSecondary1() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("secondary1");
		if (value != null)
			return value;
		else
			return super.getSecondary1();
	}

	protected ColorUIResource getSecondary2() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("secondary2");
		if (value != null)
			return value;
		else
			return super.getSecondary2();
	}

	protected ColorUIResource getSecondary3() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("secondary3");
		if (value != null)
			return value;
		else
			return super.getSecondary3();
	}

	protected ColorUIResource getBlack() {
		ColorUIResource value = (ColorUIResource)m_themePropsTable.get("black");
		if (value != null)
			return value;
		else
			return super.getBlack();
	}

	protected ColorUIResource getWhite() {
		ColorUIResource value = (ColorUIResource)m_themePropsTable.get("white");
		if (value != null)
			return value;
		else
			return super.getWhite();
	}

	/*
	 *  specialized methods
	 */
	
	public ColorUIResource getControl() {
		ColorUIResource value = (ColorUIResource)m_themePropsTable.get("control");
		if (value != null)
			return value;
		else
			return super.getControl();
	}

	public ColorUIResource getControlTextColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("controlTextColor");
		if (value != null)
			return value;
		else
			return super.getControlTextColor();
	}

	public ColorUIResource getControlShadow() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("controlShadow");
		if (value != null)
			return value;
		else
			return super.getControlShadow();
	}

	public ColorUIResource getControlDarkShadow() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("controlDarkShadow");
		if (value != null)
			return value;
		else
			return super.getControlDarkShadow();
	}

	public ColorUIResource getControlInfo() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("controlInfo");
		if (value != null)
			return value;
		else
			return super.getControlInfo();
	}

	public ColorUIResource getControlHighlight() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("controlHighlight");
		if (value != null)
			return value;
		else
			return super.getControlHighlight();
	}

	public ColorUIResource getControlDisabled() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("controlDisabled");
		if (value != null)
			return value;
		else
			return super.getControlDisabled();
	}

	public ColorUIResource getPrimaryControl() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("primaryControl");
		if (value != null)
			return value;
		else
			return super.getPrimaryControl();
	}

	public ColorUIResource getPrimaryControlShadow() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("primaryControlShadow");
		if (value != null)
			return value;
		else
			return super.getPrimaryControlShadow();
	}

	public ColorUIResource getPrimaryControlDarkShadow() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("primaryControlDarkShadow");
		if (value != null)
			return value;
		else
			return super.getPrimaryControlDarkShadow();
	}

	public ColorUIResource getPrimaryControlInfo() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("primaryControlInfo");
		if (value != null)
			return value;
		else
			return super.getPrimaryControlInfo();
	}

	public ColorUIResource getPrimaryControlHighlight() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("primaryControlHighlight");
		if (value != null)
			return value;
		else
			return super.getPrimaryControlHighlight();
	}

	public ColorUIResource getFocusColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("focusColor");
		if (value != null)
			return value;
		else
			return super.getFocusColor();
	}

	public ColorUIResource getDesktopColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("desktopColor");
		if (value != null)
			return value;
		else
			return getWhite();
	}

	public ColorUIResource getSystemTextColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("systemTextColor");
		if (value != null)
			return value;
		else
			return super.getSystemTextColor();
	}

	public ColorUIResource getInactiveControlTextColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("inactiveControlTextColor");
		if (value != null)
			return value;
		else
			return super.getInactiveControlTextColor();
	}

	public ColorUIResource getInactiveSystemTextColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("inactiveSystemTextColor");
		if (value != null)
			return value;
		else
			return super.getInactiveSystemTextColor();
	}

	public ColorUIResource getUserTextColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("userTextColor");
		if (value != null)
			return value;
		else
			return super.getUserTextColor();
	}

	public ColorUIResource getTextHighlightColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("textHighlightColor");
		if (value != null)
			return value;
		else
			return super.getTextHighlightColor();
	}

	public ColorUIResource getHighlightedTextColor() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("highlightedTextColor");
		if (value != null)
			return value;
		else
			return super.getHighlightedTextColor();
	}

	public ColorUIResource getWindowBackground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("windowBackground");
		if (value != null)
			return value;
		else
			// "super.getWindowBackground()" does not return secondary3 color
			return (ColorUIResource) getDefaultBackground();
	}

	public ColorUIResource getWindowTitleBackground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("windowTitleBackground");
		if (value != null)
			return value;
		else
			return super.getWindowTitleBackground();
	}

	public ColorUIResource getWindowTitleForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("windowTitleForeground");
		if (value != null)
			return value;
		else
			return super.getWindowTitleForeground();
	}

	public ColorUIResource getWindowTitleInactiveBackground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("windowTitleInactiveBackground");
		if (value != null)
			return value;
		else
			return super.getWindowTitleInactiveBackground();
	}

	public ColorUIResource getWindowTitleInactiveForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("windowTitleInactiveForeground");
		if (value != null)
			return value;
		else
			return super.getWindowTitleInactiveForeground();
	}

	public ColorUIResource getMenuBackground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("menuBackground");
		if (value != null)
			return value;
		else
			return super.getMenuBackground();
	}

	public ColorUIResource getMenuForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("menuForeground");
		if (value != null)
			return value;
		else
			return super.getMenuForeground();
	}

	public ColorUIResource getMenuSelectedBackground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("menuSelectedBackground");
		if (value != null)
			return value;
		else
			return super.getMenuSelectedBackground();
	}

	public ColorUIResource getMenuSelectedForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("menuSelectedForeground");
		if (value != null)
			return value;
		else
			return super.getMenuSelectedForeground();
	}

	public ColorUIResource getMenuDisabledForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("menuDisabledForeground");
		if (value != null)
			return value;
		else
			return super.getMenuDisabledForeground();
	}

	public ColorUIResource getSeparatorBackground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("separatorBackground");
		if (value != null)
			return value;
		else
			return super.getSeparatorBackground();
	}

	public ColorUIResource getSeparatorForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("separatorForeground");
		if (value != null)
			return value;
		else
			return super.getSeparatorForeground();
	}

	public ColorUIResource getAcceleratorForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("acceleratorForeground");
		if (value != null)
			return value;
		else
			return super.getAcceleratorForeground();
	}

	public ColorUIResource getAcceleratorSelectedForeground() {
		ColorUIResource value =
			(ColorUIResource)m_themePropsTable.get("acceleratorSelectedForeground");
		if (value != null)
			return value;
		else
			return super.getAcceleratorSelectedForeground();
	}
}
