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

package net.mumie.mathletfactory.mmobject;

import java.util.HashMap;

import javax.swing.JComponent;

/**
 * This interface defines the visualisation part of all {@link MMObjectIF MMObject}s. It is implemented
 * by all classes that take part of the mumie visualisation system.
 *
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public interface VisualizeIF {

	/**
	 * Returns the map display -&gt; {@link net.mumie.mathletfactory.transformer.GeneralTransformer transformer}.
	 */
	public HashMap getDisplayTransformerMap();

	/**
	 * Returns the transform type of this object. It is one of the constants
	 * listed in {@link net.mumie.mathletfactory.transformer.GeneralTransformer}.
	 */
	public int getTransformType(JComponent display);

	/**
	 * Returns the default transform type of this object. It is one of the
	 * constants listed in
	 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer}.
	 */
	public int getDefaultTransformType();

	/**
	 * Returns the default transform type of this object if it is rendered in
	 * a {@link net.mumie.mathletfactory.display.noc.MMPanel}. It is
	 * one of the constants listed in
	 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer}.
	 */
	public int getDefaultTransformTypeAsContainer();

	/**
	 * Returns a new container display of this object.
	 */
	public JComponent getAsContainerContent();

	/**
	 * Returns a new container display of this object for the specified transform type.
	 */
	public JComponent getAsContainerContent(int transformType);

	/**
	 * Renders this object in every display for which a transformer exists.
	 */
	public void render();

	/**
	 * Draws this object in every display for which a transformer exists.
	 */
	public void draw();

  /**
   * Returns true, if this object can be seen on the
   * {@link net.mumie.mathletfactory.display.MMCanvas}.
   */
  public boolean isVisible();

  /**
   * Sets whether this object can be seen on the
   * {@link net.mumie.mathletfactory.display.MMCanvas}.
   */
  public void setVisible(boolean aFlag);

}
