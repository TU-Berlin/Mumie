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

package net.mumie.mathletfactory.display.noc.symbol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.layout.Alignable;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

/**
 *  This class implements a stylable string panel using an
 *  {@link java.text.AttributedString}. It can be aligned (i.e. sharing
 *  the same baseline in a horizontal arrangement) with another
 *  component implementing {@link net.mumie.mathletfactory.display.noc.util.Alignable}.
 *  Along with the usual style attributes, a tex like syntax is allowed for adding
 *  sub- and superscripts. e.g. calling {@link #addSubstring} with a_{xy}^z as argument
 *  makes xy rendered as subscript and z as superscript.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */
public class AttributedStringLabel extends JPanel implements Alignable {

  /**
   * This class implements a styled text "atom": a substring with 
   * {@link java.awt.font.TextAttribute}s that are to be applied onto it.
   */
  private class AttributedSubstring {
    private String m_string;
    private Map m_attributes;
    private int m_begin, m_end;
    
    
    /** Constructs the attributed substring with the given string and attributes. */
    public AttributedSubstring(String string, Map attributes) {
      m_string = string;
      m_attributes = attributes;
      m_begin = m_stringList.size() > 0 ? ((AttributedSubstring)m_stringList.get(m_stringList.size()-1)).m_end : 0;
      m_end = m_begin + m_string.length();
    }
    
    public String toString() {
      return m_string+"; "+m_attributes.toString();
    }
  }

  /** Used for temporary offline-rendering (e.g. determining the size of the label) */
  private static final Graphics2D OFFLINEGRAPHICS = ((Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY).getGraphics());
   
  /** The List of strings with style attributes (bold, italic, etc.) */
  protected List m_stringList = new LinkedList();
  
  private final static MumieLogger LOGGER = MumieLogger.getLogger(AttributedStringLabel.class);
  private final static LogCategory DRAW_BASELINE = LOGGER.getCategory("ui.draw-baseline");
  private final static LogCategory PRINT_BASELINE = LOGGER.getCategory("ui.print-baseline");
  private final static LogCategory DRAW_OUTLINE = LOGGER.getCategory("ui.draw-outline");
  
  /** Creates an attributed string label. */
  public AttributedStringLabel(String string) {
  	super();
  	if(string != null)
  		addSubstring(string);
    OFFLINEGRAPHICS.setFont(getFont());
    if(LOGGER.isActiveCategory(DRAW_OUTLINE))
    	setBorder(new LineBorder(Color.BLUE));
		setOpaque(false);
		setFocusable(false);
  }
  
  /** Draws the styled string provided by {@link #getAttributedString} above the baseline. */
  protected void paintComponent(Graphics g) {
  	super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.setFont(getFont());
      g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      AttributedCharacterIterator iterator = getAttributedString().getIterator();
      int baseline = (int) getBaseline() + getInsets().top;
      g2.drawString(iterator, getInsets().left, baseline);
      if(LOGGER.isActiveCategory(DRAW_BASELINE)) {
      	// drawing debug baseline
      	g2.drawLine(getInsets().left, baseline, getWidth(), baseline);
      }
    	LOGGER.log(PRINT_BASELINE, "baseline="+baseline);
   }
  
  /** Constructs the attributed string from the {@link #m_stringList list} and returns it. */
  public AttributedString getAttributedString(){
    String complete = "";
    Iterator i = m_stringList.iterator();
    // construct string
    while(i.hasNext())
      complete += ((AttributedSubstring)i.next()).m_string;
    // avoid errors if text length is zero
    if(complete.length() == 0)
    	complete = " ";
    Font font = getFont() == null ? OFFLINEGRAPHICS.getFont() : getFont();
    AttributedString attributedString = new AttributedString(complete);
    attributedString.addAttribute(TextAttribute.FAMILY, font.getFamily());
    attributedString.addAttribute(TextAttribute.SIZE, new Float(font.getSize()+2));
    attributedString.addAttribute(TextAttribute.WEIGHT, font.getStyle() == Font.BOLD ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
    i = m_stringList.iterator();
    // add attributes
    while(i.hasNext()){
      AttributedSubstring next = (AttributedSubstring)i.next();
      // workaround for super-/subscript
      Map attMap = next.m_attributes;
      if(attMap.get(TextAttribute.SUPERSCRIPT) != null){
        if(attMap.get(TextAttribute.SUPERSCRIPT).equals(TextAttribute.SUPERSCRIPT_SUPER)){
          attMap.remove(TextAttribute.SUPERSCRIPT);
          attMap.put(TextAttribute.TRANSFORM, new TransformAttribute(new AffineTransform(
          0.7, 0, 0, 0.6, 0, -font.getSize()*0.6)));  
        } else if(attMap.get(TextAttribute.SUPERSCRIPT).equals(TextAttribute.SUPERSCRIPT_SUB)){
          attMap.remove(TextAttribute.SUPERSCRIPT);
          attMap.put(TextAttribute.TRANSFORM, new TransformAttribute(new AffineTransform(
          0.7, 0, 0, 0.7, 0, font.getSize()*0.4)));
        }
      }
      //System.out.println(next.m_string);
      if(!next.m_string.matches("\\s*"))
        attributedString.addAttributes(next.m_attributes, next.m_begin, next.m_end);
    }  
    return attributedString;
  }
  
  /** 
   * Adds the given attribute and value to the substring that begins with or 
   * immediately after <code>begin</code>. 
   */
  public void addAttribute(AttributedCharacterIterator.Attribute attribute, Object value, int begin){
    Iterator i = m_stringList.iterator();
    while(i.hasNext()){
      AttributedSubstring next = (AttributedSubstring)i.next();
      if(next.m_begin <= begin){
        next.m_attributes.put(attribute, value);
        return;
      }
    }
  }
  
  /** Adds the given substring with no attributes to the list. */
  public void addSubstring(String substring){
    addSubstring(substring, new HashMap());
  }
  
  /**
   *  Adds the given substring with no attributes to the list. 
   */
  public void addSubstring(String substring, Map attributes){
  	if(substring == null || substring.equals(""))
  		return;
    // warning: this is quick hack style!
    StringBuffer output = new StringBuffer(substring.length());
    StringBuffer input = new StringBuffer(substring);
    while(input.length() > 0){
    
      // render subscript
      if(input.charAt(0) == '_'){
        
        // render previosly created output and clear queue
        m_stringList.add(new AttributedSubstring(output.toString()+"\u200b", attributes));
        output = new StringBuffer();
        
        input.deleteCharAt(0);
        if(input.charAt(0) == '{'){
          input.deleteCharAt(0);
          while(input.charAt(0) != '}'){
            output.append(input.charAt(0));
            input.deleteCharAt(0);
          }
          input.deleteCharAt(0); 
        } else{
          output.append(input.charAt(0));
          input.deleteCharAt(0);
        }
        // add output as subscript
        HashMap subAttributes = new HashMap(attributes); 
        subAttributes.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
        m_stringList.add(new AttributedSubstring(output.toString(), subAttributes)); 
        // clear output buffer
        output = new StringBuffer();
      } else 
       
      // render superscript
      if(input.charAt(0) == '^'){
        
        // render previosly created output and clear queue
        m_stringList.add(new AttributedSubstring(output.toString()+"\u200b", attributes));
        output = new StringBuffer();
                
        input.deleteCharAt(0);
        if(input.charAt(0) == '{'){
          input.deleteCharAt(0);
          while(input.charAt(0) != '}'){
            output.append(input.charAt(0));
            input.deleteCharAt(0);
          }
         input.deleteCharAt(0); 
        } else{
          output.append(input.charAt(0));
          input.deleteCharAt(0);
        }
        // add output as subscript
        HashMap supAttributes = new HashMap(attributes);
        supAttributes.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
        m_stringList.add(new AttributedSubstring(output.toString(), supAttributes)); 
        // clear output buffer
        output = new StringBuffer();
      } else {
        output.append(input.charAt(0));
        input.deleteCharAt(0);
      }
    }
    m_stringList.add(new AttributedSubstring(output.toString(), attributes));  
  }
    
  public void addSubstring(String substring, AttributedCharacterIterator.Attribute attribute, Object value) {
    Map attributes = new HashMap();
    attributes.put(attribute, value);
    addSubstring(substring, attributes);
  }
  
  /** Removes and discards all substrings and attributes. */
  public void clear() {
    m_stringList.clear();
  }
  
  /** Clears the current and sets the new text as label content. */
  public void setText(String text) {
  	clear();
  	addSubstring(text);
  	revalidate();
  	repaint();
  }
  
  /** 
   * For debugging purposes, outputs the class name and the dimensions. 
   */
  public String toString() {
    Iterator i = m_stringList.iterator();
    String toString = "";
    while(i.hasNext())
      toString += ((AttributedSubstring)i.next()).m_string;
    return toString;
  }
  
  public double getAscent() {
  	return new TextLayout(getAttributedString().getIterator(), OFFLINEGRAPHICS.getFontRenderContext()).getAscent();
  }

  public double getDescent() {
  	return new TextLayout(getAttributedString().getIterator(), OFFLINEGRAPHICS.getFontRenderContext()).getDescent();
  }
  
  public Dimension getPreferredSize() {
    Dimension minSize = new Dimension(0, 0);
    if(m_stringList.size() == 0)
      return new Dimension(0, 0);
    // get the last substring and test it for spaces (are not considered by TextLayout)
    String last = ((AttributedSubstring)m_stringList.get(m_stringList.size()-1)).m_string;
    int lastSpaces = 0;
    while(last.length() > 0 && lastSpaces < last.length() && last.charAt(last.length()-1-lastSpaces) == ' ')
      lastSpaces++;
      //System.out.println("'"+last+"': lastSpaces: "+lastSpaces);
    TextLayout layout = new TextLayout(getAttributedString().getIterator(), OFFLINEGRAPHICS.getFontRenderContext());
    Rectangle2D bounds = layout.getBounds();
    layout = new TextLayout("_",OFFLINEGRAPHICS.getFont(),OFFLINEGRAPHICS.getFontRenderContext());
    double exWidth = layout.getBounds().getWidth() + 3;
    minSize.width = (int)bounds.getWidth() + 2 + lastSpaces * (int)exWidth;
    //minSize.width = (int)Math.max(bounds.getWidth(), exWidth*toString().length());
    minSize.height = (int)Math.max(bounds.getHeight(), OFFLINEGRAPHICS.getFont().getLineMetrics("X", OFFLINEGRAPHICS.getFontRenderContext()).getHeight() + 2); // bugfix for some unicode characters
    // add border size
    Insets i = getInsets();
    minSize.width += i.left + i.right;
    minSize.height += i.top + i.bottom;
    return minSize;
  }
  
	public double getBaseline() {
		return getAscent();
	}

  public void updateUI() {
    super.updateUI();
    if(getFont() != null)
      setFont(MumieTheme.getBoldTextFont());
  }
  
  public void setFont(Font font){
//    System.out.println("setting font to "+font+(m_stringList != null ? " for "+this : ""));
    super.setFont(font);
    if(OFFLINEGRAPHICS != null){
      OFFLINEGRAPHICS.setFont(font);
    }
    revalidate();
    repaint();
  }    
}
