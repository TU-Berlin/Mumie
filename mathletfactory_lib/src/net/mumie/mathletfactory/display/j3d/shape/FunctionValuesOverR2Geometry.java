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

import java.awt.Color;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 *  This object is the (3D) graphical representation of the graph
 *  of a function over R^2 or C
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class FunctionValuesOverR2Geometry{
  
  /**
   * Constructs a TriangleStripArray that triangulates the given m*n-quad grid
   * into n coherent strips of 4*m vertices by walking in zigzag between two
   * neighbouring lines of vertices.
   *
   * @param xMin the minimum x value of the grid
   * @param yMin the minimum y value of the grid
   * @param xStep the x-distance between to grid lines in
   * @param yStep the y-distance between to grid lines in
   * @param values the two dimensional array of z(x,y) values
   * @return GeometryArray a TriangleStripArray with calculated normals
   */
  public static GeometryArray getValueShapeGeometry(double xMin, double yMin, double xStep, double yStep,
                                                    MNumber[][] values){
    Class entryClass = values[0][0].getClass();
    int n = values.length-1;
    int m = values[0].length-1;
    int[] strips = new int[n];
    Color3f color = null;
    for(int i=0; i<n;i++)
      strips[i] = 4*m;
    TriangleStripArray qArr;
    if(entryClass == MComplex.class)
      qArr = new TriangleStripArray(n*m*4, TriangleStripArray.COORDINATES |
                                                         TriangleStripArray.NORMALS
                                                         | TriangleStripArray.COLOR_3
                                                         , strips);
    else
      qArr = new TriangleStripArray(n*m*4, TriangleStripArray.COORDINATES |
                                                         TriangleStripArray.NORMALS, strips);
    int index = 0;
    Vector3f normal = new Vector3f(0,0,1);
    MNumber lastValidValue = NumberFactory.newInstance(entryClass);
    for(int i = 0; i < n; i++)
      for(int j = 0; j < m; j++){
        if(!values[i][j].isNaN()){
          qArr.setCoordinate(index, new Point3d(xMin + xStep * i,
                                                yMin + yStep * j, values[i][j].getDouble() ));
          lastValidValue = values[i][j];
        }
        else
          qArr.setCoordinate(index, new Point3d(xMin + xStep * i,
                                                yMin + yStep * j, lastValidValue.getDouble()));
        
        if(entryClass == MComplex.class){
          color = new Color3f(new Color(Color.HSBtoRGB((float)((MComplex)values[i][j]).getArg(), 1.0f, 1.0f)));
          qArr.setColor(index, color);
        }
        qArr.setNormal(index++, normal);
        
        if(!values[i+1][j].isNaN()){
          qArr.setCoordinate(index, new Point3d(xMin + xStep * (i+1),
                                                yMin + yStep * j,
                                                values[i+1][j].getDouble()));
          lastValidValue = values[i+1][j];
        }
        else
          qArr.setCoordinate(index, new Point3d(xMin + xStep * i,
                                                yMin + yStep * j,
                                                lastValidValue.getDouble()));
        if(entryClass == MComplex.class){
          color = new Color3f(new Color(Color.HSBtoRGB((float)((MComplex)values[i+1][j]).getArg(), 1.0f, 1.0f)));
          qArr.setColor(index, color);
        }
        qArr.setNormal(index++, normal);
        
        if(!values[i][j+1].isNaN()){
          qArr.setCoordinate(index, new Point3d(xMin + xStep * i,
                                                yMin + yStep * (j+1),
                                                values[i][j+1].getDouble()));
          lastValidValue = values[i][j+1];
        }
        else
          qArr.setCoordinate(index, new Point3d(xMin + xStep * i,
                                                yMin + yStep * j,
                                                lastValidValue.getDouble()));
        if(entryClass == MComplex.class){
          color = new Color3f(new Color(Color.HSBtoRGB((float)((MComplex)values[i][j+1]).getArg(), 1.0f, 1.0f)));
          qArr.setColor(index, color);
        }
        qArr.setNormal(index++, normal);
        
        if(!values[i+1][j+1].isNaN()){
          qArr.setCoordinate(index, new Point3d(xMin + xStep * (i+1),
                                                yMin + yStep * (j+1),
                                                values[i+1][j+1].getDouble()));
          lastValidValue = values[i+1][j+1];
        }
        else
          qArr.setCoordinate(index, new Point3d(xMin + xStep * i,
                                                yMin + yStep * j,
                                                lastValidValue.getDouble()));
        if(entryClass == MComplex.class){
          color = new Color3f(new Color(Color.HSBtoRGB((float)((MComplex)values[i+1][j+1]).getArg(), 1.0f, 1.0f)));
          qArr.setColor(index, color);
        }
        qArr.setNormal(index++, normal);
      }
    GeometryInfo gInfo = new GeometryInfo(qArr);
    NormalGenerator normalGenerator = new NormalGenerator();
    normalGenerator.generateNormals(gInfo);
    GeometryArray gArray =  gInfo.getGeometryArray();
    return gArray;
  }
}
