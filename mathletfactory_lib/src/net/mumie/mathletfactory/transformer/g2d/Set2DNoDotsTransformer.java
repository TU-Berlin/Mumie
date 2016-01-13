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

import net.mumie.mathletfactory.display.g2d.Set2DDrawable;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.set.MMSet;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.set.MMSet}.
 * 
 * This class is mainly a copy (extension) of 
 * {@link net.mumie.mathletfactory.transformer.g2d.Set2DTransformer}
 * overwriting the synchronizeWorld2Screen() method. 
 * Remark: very(!) slow but 'nicer' visualization of MMSets
 * 
 * @author Paehler
 * @author Michael Heimann <heimann@math.tu-berlin.de>
 * @mm.docstatus finished
 */
public class Set2DNoDotsTransformer extends Set2DTransformer {

	  private MComplex m_tmp = new MComplex();

		public void synchronizeWorld2Screen() {
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
			double grain = 2;
			double radius = 1;
			double xStep = grain * (xWorldMax - xWorldMin) / (width);
			double yStep = grain * (yWorldMax - yWorldMin) / (height);
			double xStep2 = xStep / 2;
			double[] vector = new double[2];
			((Set2DDrawable) m_activeDrawable).resetArea();
			for (int i = 0; i < width / grain; i++)
				for (int j = 0; j < height / grain; j++) {
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
							new Ellipse2D.Double(vector[0],vector[1],radius,radius);

						((Set2DDrawable) m_activeDrawable).addArea(newArea);
					}
				}
		}

		private MMSet getRealMaster() {
			return (MMSet) m_masterMMObject;
		}
		
}
