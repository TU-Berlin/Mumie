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

package net.mumie.mathletfactory.display.j3d.shape;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 *  This object is the (3D) graphical representation of an arbitrary parameterized surface in R^3.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class ParameterizedSurfaceGeometry{
  
  /**
   * Returns a triangulated and normalized geometry array of the given values. 
   * @param values the values as a twodimensional array of 3-dim tuples.
   */
  public static GeometryArray getValueShapeGeometry(NumberTuple[][] values){
    Class entryClass = values[0][0].getNumberClass();
    int n = values.length;
    int m = values[0].length;
    
    Color3f color = null;
//    TriangleStripArray qArr = new TriangleStripArray((2*n-1)*(m-1)+1, TriangleStripArray.COORDINATES, new int[]{(2*n-1)*(m-1)+1});
//    int index = 0;
//    for(int i = 0; i < n; i+=2){
//      // forward triangulation
//      for(int j = 0; j < m; j++){
//        if(i < n-1 || j == 0)
//          qArr.setCoordinate(index++, numberTupel2Point3d(values[i][j]));
//        if(i < n-1)
//          qArr.setCoordinate(index++,numberTupel2Point3d(values[i+1][j]));
//      }
//      if(i+2 < n){
//        // backward triangulation
//        for(int j = m-1; j>0;j--){
//          qArr.setCoordinate(index++, numberTupel2Point3d(values[i+2][j]));
//          qArr.setCoordinate(index++, numberTupel2Point3d(values[i+1][j-1]));
//        }
//      }
//    }
    QuadArray qArr = new QuadArray((n-1)*(m-1)*4,QuadArray.COORDINATES);
    int index = 0;
    for(int i=0;i<values.length-1;i++){
    	for(int j=0;j<values[0].length-1;j++){
    		qArr.setCoordinate(index++, numberTupel2Point3d(values[i][j]));
    		qArr.setCoordinate(index++, numberTupel2Point3d(values[i][j+1]));
    		qArr.setCoordinate(index++, numberTupel2Point3d(values[i+1][j+1]));
    		qArr.setCoordinate(index++, numberTupel2Point3d(values[i+1][j]));
        }
    }
    GeometryInfo gInfo = new GeometryInfo(qArr);
    NormalGenerator normalGenerator = new NormalGenerator();
    normalGenerator.generateNormals(gInfo);
    GeometryArray gArray =  gInfo.getGeometryArray();
    return gArray;
  }
  
  private static Point3d numberTupel2Point3d(NumberTuple t){
    return new Point3d(t.getEntryRef(1).getDouble(), t.getEntryRef(2).getDouble(), t.getEntryRef(3).getDouble());
  }
}
