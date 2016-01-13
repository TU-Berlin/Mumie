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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.g2d.Set2DDrawable;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.set.MMSet;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.set.MMSet}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class Set2DTransformer extends Canvas2DObjectTransformer {

  private MComplex m_tmp = new MComplex();

	public Set2DTransformer() {
		m_allDrawables = new CanvasDrawable[] { new Set2DDrawable()};
		m_activeDrawable = m_allDrawables[0];
	}

	public void synchronizeMath2Screen() {
		synchronizeWorld2Screen();
	}

	public void synchronizeWorld2Screen() {
		
		//if(getRealMaster().getRelation().getVersion())
		//getRealMaster().getRelation().prepareEvaluateFast();
		//double[] xyBoundaryValues = new double[2];

		// bring the fields m_ll*, m_ur* up-to-date. Currently this only works 
		// properly when the canvas was not rotated before. 
		adjustWorldBounds();
		double xWorldMin = m_llInWorldDraw[0];
		double xWorldMax = m_urInWorldDraw[0];
		double yWorldMin = m_llInWorldDraw[1];
		double yWorldMax = m_urInWorldDraw[1];
		int width =
			getMasterAsCanvasObject().getCanvas().getDrawingBoard().getSize().width;
		int height =
			getMasterAsCanvasObject().getCanvas().getDrawingBoard().getSize().height;
		double xStep = 8 * (xWorldMax - xWorldMin) / (width);
		double yStep = 8 * (yWorldMax - yWorldMin) / (height);
		double xStep2 = xStep / 2;
		double[] vector = new double[2];
		((Set2DDrawable) m_activeDrawable).resetArea();
		//SimpleRel[] simpleRelations = getRealMaster().getRelation().getSimpleRelations();
		for (int i = 0; i < width / 8; i++)
			for (int j = 0; j < height / 8; j++) {
				//vector[0] = xWorldMin + xStep * i;
				vector[0] = xWorldMin + xStep * i - xStep2 * (j % 2);
        vector[1] = yWorldMin + yStep * j;
        boolean eval = false;
        try {
          if (getRealMaster() instanceof MMSetDefByRel) { // for faster evaluation
            // check if the set contains the point:
            Relation relation = ((MMSetDefByRel)getRealMaster()).getRelation();
            double[] arg = vector;
            // check for special cases that only one identifier is used
            String[] usedIdentifiers = relation.getPreparedIdentifiers();
            if (usedIdentifiers.length == 1)
              if (usedIdentifiers[0].equals("x"))
                arg = new double[] { vector[0] };
              else
                arg = new double[] { vector[1] };
            if (relation.getNumberClass().isAssignableFrom(MComplex.class)) {     
              m_tmp.setComplex(vector[0], vector[1]);
              eval = relation.evaluate(new MNumber[] { new MComplex(m_tmp.getRe(), m_tmp.getIm())});
            } else
              eval = relation.evaluateFast(arg);
          } else { // use standard contains method
           if (getRealMaster().getNumberClass().isAssignableFrom(MComplex.class)) {
              m_tmp.setComplex(vector[0], vector[1]);
              eval = getRealMaster().contains(new MComplex(m_tmp.getRe(), m_tmp.getIm()));
            } else
              eval = getRealMaster().contains(vector);
          }
        } catch(UnsupportedOperationException e){}
				if(eval) {
					getWorld2Screen().applyTo(vector);
					Shape newArea =
//						new Rectangle2D.Double(
//							vector[0] - xStep / 2,
//							vector[1] - yStep / 2,
//							 xStep, //aenderung
//							 yStep);  //aenderung
						new Ellipse2D.Double(vector[0],vector[1],2,2);

					((Set2DDrawable) m_activeDrawable).addArea(newArea);
					//System.out.println("adding area about "+vector[0]+","+vector[1]);
				}
			}
	}

	private MMSet getRealMaster() {
		return (MMSet) m_masterMMObject;
	}

	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF mathObject) {

	}

	public void getScreenPointFromMath(
		NumberTypeDependentIF entity,
		double[] javaScreenCoordinates) {

	}

}
