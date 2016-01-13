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

package net.mumie.mathletfactory.display.j3d;

import javax.media.j3d.Geometry;

import net.mumie.mathletfactory.display.j3d.shape.ParameterizedSurfaceGeometry;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;

/**
 *  This class acts as a drawable for an arbitrary parameterized surface in the
 *  3d space.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class J3DParameterizedSurfaceDrawable extends J3DSurfaceDrawable {
  private NumberTuple[][] m_values;

  /**
   *  Sets the number tuples as a twodimensional array, where every vertex will be connected
   *  to its neighbours by a triangulation. This method is called by 
   *  {@link net.mumie.mathletfactory.transformer.j3d.ParametricFunctionInR3Transformer#synchronizeMath2Screen}. 
   */
  public void setValues(NumberTuple[][] values){
    m_values = values;
    resetShape();
  }
  
  /** Returns the current set values of the surface drawable. */
  public NumberTuple[][] getValues(){
    return m_values;
  }
    
  protected Geometry getGeometry(){
    return ParameterizedSurfaceGeometry.getValueShapeGeometry(m_values);
  }
  
  /** Returns true if the given values are different from the ones currently set to, false otherwise. */
  public static boolean valuesChanged(NumberTuple[][] oldValues, NumberTuple[][] newValues){
    if(newValues == null || oldValues == null || newValues.length != oldValues.length || newValues[0].length != oldValues[0].length)
      return true;
    for(int i=0;i<newValues.length;i++)
      for(int j=0;j<newValues[0].length;j++){
        //System.out.println("comparing "+newValues[i][j]+" with "+oldValues[i][j]+":");      
        if(!newValues[i][j].equals(oldValues[i][j])){
          //System.out.println("changed!");        
          return true;         
        }
      }
    return false;
  }
}
