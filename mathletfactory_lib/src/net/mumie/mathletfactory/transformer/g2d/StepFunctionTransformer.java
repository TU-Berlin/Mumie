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

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DPolygonDrawable;
import net.mumie.mathletfactory.display.g2d.G2DRectDrawable;
import net.mumie.mathletfactory.mmobject.analysis.function.MMStepFunction;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.analysis.function.MMStepFunction}.
 * 
 * @author Paehler
 * @mm.docstatus finished
 */
public class StepFunctionTransformer extends Affine2DDefaultTransformer {

  private final double[] m_javaScreenCoordinates = new double[2];
  
  public StepFunctionTransformer(){
  }

  public void synchronizeMath2Screen() {
    synchronizeWorld2Screen();
  }

  public void synchronizeWorld2Screen() {
    int distributionLength = getRealMaster().getDistribution().length;
    m_allDrawables = new G2DPolygonDrawable[]{new G2DPolygonDrawable(getRealMaster().getDistribution().length*2-1)};    
    m_additionalDrawables = new G2DRectDrawable[distributionLength];
    m_additionalProperties = new DisplayProperties[distributionLength];
    for(int i=0;i<m_additionalDrawables.length;i++){
      m_additionalDrawables[i] = new G2DRectDrawable();
      m_additionalProperties[i] = new DisplayProperties(getMaster().getDisplayProperties());
    }
    m_activeDrawable = m_allDrawables[0];
    getPath().setOpened();

    int points = 0;
    double[] dim = new double[2];
    for(int i=0;i<distributionLength-1;i++){
      
      // calculate left point of constant line
      world2Screen(new double[]{getRealMaster().getDistribution()[i], 
                   getRealMaster().getDistributionValues()[i]}, m_javaScreenCoordinates);
      getPath().setPoint(points++, m_javaScreenCoordinates[0], m_javaScreenCoordinates[1]);
      
      // calculate rectangle
      dim[0] = getRealMaster().getDistribution()[i+1] - getRealMaster().getDistribution()[i];
      dim[1] = - getRealMaster().getDistributionValues()[i];
      getWorld2Screen().applyDeformationPartTo(dim); 
                  
      //System.out.println("width of "+(getRealMaster().getDistribution()[i+1] - getRealMaster().getDistribution()[i])+" is "+dim[0]);
      getRect(i).setPoints(m_javaScreenCoordinates[0], m_javaScreenCoordinates[1], dim[0], dim[1]);
      
      // calculate right point of constant line
      world2Screen(new double[]{getRealMaster().getDistribution()[i+1], 
                   getRealMaster().getDistributionValues()[i]}, m_javaScreenCoordinates);
      getPath().setPoint(points++, m_javaScreenCoordinates[0], m_javaScreenCoordinates[1]);
      getPath().setValid(points, false);
    }
        
    world2Screen(new double[]{getRealMaster().getDistribution()[distributionLength-1], 
                 getRealMaster().getDistributionValues()[distributionLength-2]}, m_javaScreenCoordinates);
    // the vertical line should not be drawn
    getPath().setPoint(points++, m_javaScreenCoordinates[0], m_javaScreenCoordinates[1]);
  }

  private MMStepFunction getRealMaster(){
    return (MMStepFunction)getMaster();
  }
  
  private G2DPolygonDrawable getPath(){
    return (G2DPolygonDrawable) m_allDrawables[0];
  }
  
  private G2DRectDrawable getRect(int i){
    return (G2DRectDrawable)m_additionalDrawables[i];
  }
}
