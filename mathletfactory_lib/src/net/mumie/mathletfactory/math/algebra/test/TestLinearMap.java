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

package net.mumie.mathletfactory.math.algebra.test;


import net.mumie.mathletfactory.math.algebra.linalg.LinearMap;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVector;
import net.mumie.mathletfactory.math.algebra.linalg.NumberVectorSpace;
import net.mumie.mathletfactory.math.number.MRational;


public class TestLinearMap {
  
  
  public static void main(String args[]){
    
    // creates a standard two dimensional vector space:
    NumberVectorSpace R2D = new NumberVectorSpace(MRational.class,2,2);
    
    // change the actual basis:
    // b1 = e2, b2 = e1 (where b signs the actual and e the default bases)
    Object[] entries = new Object[]{new MRational(1),new MRational(1),new MRational(1),new MRational(0)};
    R2D.setBasis(new NumberMatrix(MRational.class,2,entries));
    
    NumberVector[] v = new NumberVector[2];
    NumberVector[] u = new NumberVector[2];
    
    NumberTuple v1_coords = new NumberTuple(MRational.class,new Object[]{new MRational(1),new MRational(0)});
    NumberTuple v2_coords = new NumberTuple(MRational.class,new Object[]{new MRational(0),new MRational(1)});
    
    NumberTuple u1_coords = new NumberTuple(MRational.class,new Object[]{new MRational(1),new MRational(0)});
    NumberTuple u2_coords = new NumberTuple(MRational.class,new Object[]{new MRational(0),new MRational(5)});
    
    
    v[0] = (NumberVector) R2D.getNewFromCoordinates(v1_coords);
    v[1] = (NumberVector) R2D.getNewFromCoordinates(v2_coords);
    u[0] = (NumberVector) R2D.getNewFromCoordinates(u1_coords);
    u[1] = (NumberVector) R2D.getNewFromCoordinates(u2_coords);

    // create linear map, that maps v[i] --> u[i], i=1,2:
    LinearMap L = new LinearMap(v,u);
    LinearMap l = new LinearMap(u,v);
    
    
    
    
    System.out.println("R2D, actual base coefficients: \n"+R2D.getBasisAsMatrixRef());

    System.out.println("v1, coefficients relative to default base: \n"+v[0].getDefaultCoordinates());
    System.out.println("v1, coefficients relative to actual base: \n"+v[0].getCoordinates());
    System.out.println("v1, absolute coordinates: \n"+v[0].getCoordinatesInSurroundingSpace());
    System.out.println("v2, coefficients relative to default base: \n"+v[1].getDefaultCoordinates());
    System.out.println("v2, coefficients relative to actual base: \n"+v[1].getCoordinates());
    System.out.println("v2, absolute coordinates: \n"+v[1].getCoordinatesInSurroundingSpace());
    
    System.out.println("LinearMap, representation relative to default bases: \n"+L.getDefaultMatrixRepresentationRef());
    System.out.println("inverse linear map, in default bases representation; \n"+l.getDefaultMatrixRepresentationRef());
    System.out.println("LinearMap, representation relative to actual bases: \n"+L.getActualMatrixRepresentation());
    System.out.println("inverse linear map, in actual bases representation; \n"+l.getActualMatrixRepresentation());
    
    
    System.out.println(l.getActualMatrixRepresentation().mult(L.getActualMatrixRepresentation()));
  }
  
  
}

