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

package net.mumie.mathletfactory.display.j3d;

import javax.media.j3d.TransparencyAttributes;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.ScreenTypeRenderingHints;

/**
 * This class contains rendering hints for Java3D drawables.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class J3DRenderingHints extends ScreenTypeRenderingHints {
	
	/** Default value for the <i>text size</i> property. */
  public static final double J3D_DEFAULT_TEXT_SIZE = 0.07;
  public static final int NONE = TransparencyAttributes.NONE;
  public static final int FASTEST = TransparencyAttributes.FASTEST;
  public static final int NICEST = TransparencyAttributes.NICEST;
  public static final int SCREEN_DOOR = TransparencyAttributes.SCREEN_DOOR;
  public static final int BLENDED = TransparencyAttributes.BLENDED;
  public static final String J3D_TEXT_SIZE_PROPERTY = "display.j3d.textSize";
  public static final String J3D_TRANSP_MODE_PROPERTY = "diplay.j3d.transparencyMode";
  public static final String J3D_LINE_DISPLAYED_PROPERTY = "diplay.j3d.lineDisplayed";
  public static final String J3D_POSITION_ALPHA = "display.j3d.positionAlpha";
  public static final String J3D_POSITION_START = "display.j3d.positionStart";
  public static final String J3D_POSITION_END = "display.j3d.positionEnd";
//  public static final String J3D_LABEL_POSITION = "display.j3d.labelPosition";
	public J3DRenderingHints(DisplayProperties displayProps) {
		super(displayProps);
	}
  
	/**
	 * Returns the text size.
	 */
  public double getJ3DTextSize() {
  	return getDoubleProperty("display.j3d.textSize", J3D_DEFAULT_TEXT_SIZE);
  }
  
	/**
	 * Sets the text size.
	 */
  public void setJ3DTextSize(double x) {
  	setProperty("display.j3d.textSize", x);
  }
  
  /**
   * Sets the transparency-mode
   * @param x - the transparency mode to be used, 
   *            one of NONE,FASTEST, NICEST, SCREEN_DOOR, or BLENDED
   */
  public void setTransparencyMode(int x){
	  setProperty("display.j3d.transparencyMode", x);
  }
  
  /**
   * returns the transparency-mode default ist Blended
   */
  public int getTransparencyMode() {
	  return (int) getDoubleProperty("display.j3d.transparencyMode", BLENDED);
  }
  
  public void initialize() {}
  
  public void setLineDisplayed(boolean b){
	  setProperty(J3D_LINE_DISPLAYED_PROPERTY, b);
  }
  
  public boolean isLineDisplayed(){
	  return (boolean) getBooleanProperty(J3D_LINE_DISPLAYED_PROPERTY, false);
  }
  
}
