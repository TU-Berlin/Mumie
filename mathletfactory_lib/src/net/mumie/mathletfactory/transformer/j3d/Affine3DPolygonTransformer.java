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

package net.mumie.mathletfactory.transformer.j3d;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPolygonDrawable;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPolygon;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPolygon}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Affine3DPolygonTransformer
	extends Affine3DDefaultTransformer {

  int hashCode = 0;
  double[] labelPos = new double[3];

	public Affine3DPolygonTransformer() {
		m_allDrawables =
			new CanvasDrawable[] {
				new J3DPolygonDrawable()};
    m_activeDrawable = m_allDrawables[0];
	}
  
	public void synchronizeMath2Screen() {
    if(hashCode != getRealMasterObject().hashCode()){
      //System.out.println("hashCode differs!");    
      ((J3DPolygonDrawable) m_activeDrawable).setPoints(getRealMasterObject().getCoords());
      hashCode = getRealMasterObject().hashCode();
    } //else
      //System.out.println("hashCode equal!");
	}

  public void render() {
    labelPos = getWorldPickPointFromMaster();
    labelPos[0] += 0.1;  
    labelPos[1] -= 0.1;
    labelPos[2] += 0.1;
    renderLabel(labelPos);
    super.render();
  }

  public double[] getWorldPickPointFromMaster() {
    return ((J3DPolygonDrawable) m_activeDrawable).getCenter();
  }

	private MMAffine3DPolygon getRealMasterObject() {
		return (MMAffine3DPolygon) m_masterMMObject;
	}
}
