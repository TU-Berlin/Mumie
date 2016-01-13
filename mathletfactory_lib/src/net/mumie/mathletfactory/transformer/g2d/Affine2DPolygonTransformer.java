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

package net.mumie.mathletfactory.transformer.g2d;
import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPolygonDrawable;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPolygon;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPolygon}.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class Affine2DPolygonTransformer
	extends Affine2DDefaultTransformer {

	private double[][] m_worldP;
	private double[][] m_screenP;

	public Affine2DPolygonTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new G2DPointDrawable(),
				new G2DPolygonDrawable()};
	}

	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		m_worldP = new double[getMathMasterObject().getVerticesCount()][];
		m_screenP = new double[getMathMasterObject().getVerticesCount()][];
		for (int i = 0; i < getMathMasterObject().getVerticesCount(); i++) {
			m_worldP[i] = new double[2];
			m_screenP[i] = new double[2];
		}
		((G2DPolygonDrawable) m_allDrawables[1]).initLength(
			getMathMasterObject().getVerticesCount());

	}

	public void synchronizeMath2Screen() {
		if (masterIsDegenerated()) {
			//Polygon is degenerated to a point:
			m_activeDrawable = m_allDrawables[0];
			math2World(getMathMasterObject().getVertexRef(0), m_worldP[0]);
			world2Screen(m_worldP[0], m_screenP[0]);
			getPointDrawable().setPoint(m_screenP[0][0], m_screenP[0][1]);
		}
		else {
			m_activeDrawable = m_allDrawables[1];
      if(getMathMasterObject().getVerticesCount() != m_worldP.length)
        initialize(m_masterMMObject);
			for (int i = 0; i < getMathMasterObject().getVerticesCount(); i++) {
				math2World(getMathMasterObject().getVertexRef(i), m_worldP[i]);
				world2Screen(m_worldP[i], m_screenP[i]);
				getPolygonDrawable().setPoint(i, m_screenP[i][0], m_screenP[i][1]);
			}
		}
	}

	public void synchronizeWorld2Screen() {
		if (masterIsDegenerated()) {
			m_activeDrawable = m_allDrawables[0];
			math2World(getMathMasterObject().getVertexRef(0), m_worldP[0]);
			world2Screen(m_worldP[0], m_screenP[0]);
			getPointDrawable().setPoint(m_worldP[0][0], m_worldP[0][1]);
		}
		else {
			m_activeDrawable = m_allDrawables[1];
			for (int i = 0; i < getMathMasterObject().getVerticesCount(); i++) {
				world2Screen(m_worldP[i], m_screenP[i]);
				getPolygonDrawable().setPoint(i, m_screenP[i][0], m_screenP[i][1]);
			}
		}
	}
  
  public void draw() {
    super.draw();
    if(m_screenP.length < 1)
      return;
    int middleIndex = m_screenP.length/2-1;
    if(middleIndex < 0)
      renderLabel((float) m_screenP[0][0], (float) m_screenP[0][1]);
    else
      renderLabel((float) (m_screenP[middleIndex][0] + (m_screenP[middleIndex+1][0] - m_screenP[middleIndex][0])/2), 
                  (float) (m_screenP[middleIndex][1] + (m_screenP[middleIndex+1][1] - m_screenP[middleIndex][1])/2)); 
  }

	protected boolean masterIsDegenerated() {
		return getMathMasterObject().isDegenerated();
	}

	private MMAffine2DPolygon getMathMasterObject() {
		return (MMAffine2DPolygon) m_masterMMObject;
	}

	private G2DPointDrawable getPointDrawable() {
		return (G2DPointDrawable) m_allDrawables[0];
	}

	private G2DPolygonDrawable getPolygonDrawable() {
		return (G2DPolygonDrawable) m_allDrawables[1];
	}

	/**
	 * @see net.mumie.mathletfactory.transformer.GeneralTransformer#render()
	 */
	public void render() {
		if (getMathMasterObject().isClosed())
			getPolygonDrawable().setClosed();
		else
			getPolygonDrawable().setOpened();
		super.render();
	}
}
