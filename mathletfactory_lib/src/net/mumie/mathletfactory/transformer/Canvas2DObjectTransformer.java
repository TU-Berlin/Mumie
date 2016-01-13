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

package net.mumie.mathletfactory.transformer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

import net.mumie.mathletfactory.display.MM2DCanvas;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;
import net.mumie.mathletfactory.display.util.LabelSupport;
import net.mumie.mathletfactory.display.util.StyledTextView;
import net.mumie.mathletfactory.display.util.ViewIF;
import net.mumie.mathletfactory.math.util.Affine2DDouble;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

/**
 *  This class is the base for all 2D transformer.
 * 
 *  @author vossbeck, Paehler
 *  @mm.docstatus finished
 */
public abstract class Canvas2DObjectTransformer
	extends CanvasObjectTransformer {

  /** The upper right corner in world draw coordinates */
	protected final double[] m_urInWorldDraw = new double[2];
  
  /** The lower left corner in world draw coordinates */
	protected final double[] m_llInWorldDraw = new double[2];
	
	private LabelSupport m_labelSupport = new LabelSupport(LabelSupport.ST_2D_CANVAS);
	
	/** Field holding the current label's view used for rendering the label in the canvas. */
	private ViewIF m_labelView;
	
	public Affine2DDouble getWorld2Screen() {
		return ((MM2DCanvas) getCanvas()).getWorld2Screen();
	}

	public Affine2DDouble getScreen2World() {
		return ((MM2DCanvas) getCanvas()).getScreen2World();
	}

	protected void world2Screen(
		double wx[],
		double wy[],
		float sx[],
		float sy[]) {
		getWorld2Screen().applyTo(wx, wy, sx, sy);
	}

	protected void world2Screen(
		double[] worldCoords,
		double[] javaScreenCoords) {
		getWorld2Screen().applyTo(worldCoords, javaScreenCoords);
	}

	protected void world2Screen(
		double xWorld,
		double yWorld,
		double[] javaScreenCoords) {
		getWorld2Screen().applyTo(xWorld, yWorld, javaScreenCoords);
	}

	protected void screen2World(
		double[] javaScreenCoords,
		double[] worldCoords) {
		getScreen2World().applyTo(javaScreenCoords, worldCoords);
	}

  protected void screen2World(
    double xScreen, double yScreen,
    double[] worldCoords) {
    getScreen2World().applyTo(xScreen, yScreen, worldCoords);
  }
  
	public final void recalcUpperRightInWorldDraw() {
		getScreen2World().applyTo(getCanvas().getDrawingBoard().getWidth(), 0, m_urInWorldDraw);
	}

	public final void recalcLowerLeftInWorldDraw() {
		getScreen2World().applyTo(0, getCanvas().getDrawingBoard().getHeight(), m_llInWorldDraw);
	}

	public final double getWorldXmin() {
		recalcLowerLeftInWorldDraw();
		return m_llInWorldDraw[0];
	}

	public final double getWorldXmax() {
		recalcUpperRightInWorldDraw();
		return m_urInWorldDraw[0];
	}

	public final void adjustWorldBounds() {
		recalcLowerLeftInWorldDraw();
		recalcUpperRightInWorldDraw();
	}

  protected void renderLabel(float endX, float endY) {
    if (getMaster().getLabel() != null
      && ((MMCanvasObjectIF) m_masterMMObject).isVisible() && getMaster().getDisplayProperties().isLabelDisplayed()) {
      Graphics2D gr = ((MMG2DCanvas) getMasterAsCanvasObject().getCanvas()).getGraphics2D();
      gr.setColor(Color.black);
      gr.setFont(new Font("helvetica", Font.BOLD, 12));
      String oldLabel = m_labelSupport.getLabel();
      m_labelSupport.setLabel(getMaster().getLabel());
      // label is empty
      if(m_labelSupport.getLabel().equals(""))
      	return;
      Dimension dim = null;
      // label is new
      if(m_labelView == null || dim == null || !m_labelSupport.getLabel().equals(oldLabel)) {
      	m_labelView = StyledTextView.createStyledTextView(m_labelSupport.getLabel());
        m_labelView.setAttribute(ViewIF.FONT_ATTRIBUTE, gr.getFont());
        dim = m_labelView.getPreferredSize(gr);
      }
      m_labelView.setSize(dim);
      m_labelView.paint(gr, (int) endX + 5, (int) endY + 5, dim.width, dim.height); 
    }
  }
}
