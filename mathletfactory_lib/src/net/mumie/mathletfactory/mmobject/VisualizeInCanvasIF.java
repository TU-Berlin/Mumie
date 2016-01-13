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

import java.awt.geom.Rectangle2D;

import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;

/**
 * This interface defines the visualisation in canvas part of all {@link MMObjectIF MMObject}s.
 * It is implemented by all classes that are visualisable in a
 * {@link net.mumie.mathletfactory.display.MMCanvas}.
 *
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public interface VisualizeInCanvasIF {

	/**
	 * Returns this object as a canvas object.
	 */
	public MMCanvasObjectIF getAsCanvasContent();

	/**
	 * Returns the canvas, that this object is rendered onto.
	 */
	public MMCanvas getCanvas();

	/**
	 * Sets the canvas, that this object is rendered onto.
	 */
	public void setCanvas(MMCanvas aCanvas);

	/**
		 * Adds the transformer specified by the transform and visual types and the
		 * display.
		 * @param transformType must be one of the constants listed in
		 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer}.
		 * @param screenType must be one of the constants listed in
		 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer}.
		 */
	public void setCanvasTransformer(int transformType, int screenType);

	/**
	 * Returns the transformer that is responsible for rendering the object
	 * in the given display.
	 */
	public CanvasObjectTransformer getCanvasTransformer();

	/**
	 * Returns true if the mathematical coordinates of this correspond to the
	 * given screen coordinates. The decision is left to the
	 * {@link net.mumie.mathletfactory.transformer.CanvasObjectTransformer}.
	 */
	public boolean isAtScreenLocation(int x, int y);

	/**
	 * Returns the default transform type when rendered in a canvas. The return
	 * value must be one of the constants listed in
	 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer}.
	 */
	public int getDefaultTransformTypeInCanvas();

	/**
	 * The 2d bounding box of the object in world coordinates. The result maybe null.
	 */
	public Rectangle2D getWorldBoundingBox();
}
