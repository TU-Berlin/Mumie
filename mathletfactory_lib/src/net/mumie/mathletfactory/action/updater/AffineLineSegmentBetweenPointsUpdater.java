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
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint;

/**
 * An updater for keeping a line segment between two changing points.
 * 
 * @author vossbeck
 * @mm.docstatus finished
 */
public class AffineLineSegmentBetweenPointsUpdater extends MMUpdater{
  
  
  private MMAffine2DLineSegment m_lineSegment;
  private MMAffine2DPoint m_initialPoint, m_endPoint;
  
  
  
  public AffineLineSegmentBetweenPointsUpdater(MMAffine2DLineSegment lineSegment,
                                               MMAffine2DPoint initialPoint,
                                               MMAffine2DPoint endPoint) {
    super(lineSegment,new MMAffine2DPoint[]{initialPoint,endPoint});
    m_lineSegment = lineSegment;
    m_initialPoint = initialPoint;
    m_endPoint = endPoint;
    update();
    ActionManager.reset();
  }
  
  
  public void userDefinedUpdate() {
    // hope, both methods work without side-effects:
    m_lineSegment.setInitialPoint(m_initialPoint);
    m_lineSegment.setEndPoint(m_endPoint);
  }
  
}

