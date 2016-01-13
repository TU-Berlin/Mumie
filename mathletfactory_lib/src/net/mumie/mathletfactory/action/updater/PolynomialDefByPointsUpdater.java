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

package net.mumie.mathletfactory.action.updater;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.algebra.poly.MMPolynomial;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;

/**
 * This updater ensures the updating of a polynomial that interpolates a given set of points.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class PolynomialDefByPointsUpdater extends MMUpdater {
  
  
  private MMPolynomial m_polynomial;
//  private MMAffine2DPoint[]  m_points;
  private Dependant m_dependantType;
  
  private  MNumber[] m_xVals;
  private  MNumber[] m_yVals;
  
  private  int m_index = 1;
  
  
  public PolynomialDefByPointsUpdater(MMPolynomial aPolynome, MMAffine2DPoint[] points, Dependant dependantType) {
    super(aPolynome,points);
//    m_points = points;
    m_polynomial = (MMPolynomial)getSlave();
    m_xVals = new MNumber[points.length];
    m_yVals = new MNumber[points.length];
    
    for (int i=0; i<points.length; i++){            // setting links to coordinates
      m_xVals[i] = points[i].getXProjAsNumberRef(); // for finite points z remains = 1 ????
      m_yVals[i] = points[i].getYProjAsNumberRef(); // for finite points z remains = 1 ????
    }
    m_dependantType = dependantType;
		ActionManager.performActionCycleFromObjects(m_parents);
  }
  
  public PolynomialDefByPointsUpdater(MMPolynomial aPolynome, MMAffine2DPoint[] points, int index, Dependant dependantType) {
    super(aPolynome,points);
//    m_points = points;
    m_polynomial = (MMPolynomial)getSlave();
    m_index = index;
    m_xVals = new MNumber[points.length];
    m_yVals = new MNumber[points.length];
    for (int i=0; i<points.length; i++){            // setting links to coordinates
      m_xVals[i] = points[i].getXProjAsNumberRef(); // for finite points z remains = 1 ????
      m_yVals[i] = points[i].getYProjAsNumberRef(); // for finite points z remains = 1 ????
    }
    m_dependantType = dependantType;
    update();
    ActionManager.reset();
  }

  
  public void userDefinedUpdate() {
    m_dependantType.setDependant(m_polynomial,m_index,m_xVals,m_yVals);
  }
  
  public void setIndex(int i) {
    m_index = i;
  }
  
  public int getIndex() {
    return m_index;
  }
  
  
  public static final Dependant BASE = new Dependant() {
    public void setDependant(MMPolynomial p, int index, MNumber[] xVals, MNumber[] yVals) {
      p.setInterpolationPolynomial(xVals,yVals);
    }
  };
  
  public static final Dependant LAGRANGE = new Dependant() {
    public void setDependant(MMPolynomial p, int index, MNumber[] xVals, MNumber[] yVals) {
      p.setLagrangePolynomial(xVals,index,yVals[index-1]);
    }
  };
  
  public static final Dependant NEWTON = new Dependant() {
    public void setDependant(MMPolynomial p, int index, MNumber[] xVals, MNumber[] yVals) {
      p.setNewtonPolynomial(xVals,yVals,index-1);
    }
  };
  
  
  private interface Dependant{
    public void setDependant(MMPolynomial p, int index, MNumber[] xvals, MNumber[] yvals);
  }
  
}

