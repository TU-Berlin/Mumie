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
import java.util.logging.Logger;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class serves as support for instances of
 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF} -- these must implement
 * methods from {@link VisualizeInCanvasIF}.
 * The <code>VisualizeInCanvasSupport</code> is a correct implementation of
 * an <code>VisualizeInCanvasIF</code> and instances of <code>MMObjectIF</code>
 * may delegate those methods to this support.
 *
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */
public class VisualizeInCanvasSupport
	extends VisualizeSupport
	implements VisualizeInCanvasIF {

	private static Logger logger =
		Logger.getLogger(VisualizeInCanvasSupport.class.getName());

	private MMCanvas m_canvas;

	public VisualizeInCanvasSupport(MMCanvasObjectIF aClientObject) {
		super(aClientObject);
	}

	public final void setCanvas(MMCanvas aCanvas) {
		if (m_canvas != aCanvas) {
			m_canvas = aCanvas;
			if (getCanvasTransformer() != null)
				setCanvasTransformer(
					getTransformType(getCanvas()),
					getCanvas().getScreenType());
		}
	}

	public final MMCanvas getCanvas() {
		return m_canvas;
	}

	public final void setCanvasTransformer(int transformType, int visualType) {
		if (transformType != GeneralTransformer.NO_TRANSFORM_TYPE)
			m_transformers.put(
				m_canvasKey,
				GeneralTransformer.getTransformer(
					transformType,
					visualType,
					getClient()));
	}

	public CanvasObjectTransformer getCanvasTransformer() {
		return (CanvasObjectTransformer) m_transformers.get(m_canvasKey);
	}

	public boolean isAtScreenLocation(int x, int y) {
		if (getCanvasTransformer() instanceof CanvasObjectTransformer)
			return (
				(CanvasObjectTransformer) getCanvasTransformer()).isAtScreenLocation(
				x,
				y);
		else if (logger.isLoggable(java.util.logging.Level.SEVERE))
			logger.severe(
				"tried to get canvas position of object, that is rendered as component");
		return false;
	}

	public final MMCanvasObjectIF getAsCanvasContent() {
		return getClientAsCanvasObject();
	}

	public int getDefaultTransformTypeInCanvas() {
		throw new IllegalUsageException(
			"method should never be reached and must be"
				+ " implemented in particular mmobject");
	}

	private final MMCanvasObjectIF getClientAsCanvasObject() {
		return (MMCanvasObjectIF) getClient();
	}

	public Rectangle2D getWorldBoundingBox() {
		return null;
	}

}
