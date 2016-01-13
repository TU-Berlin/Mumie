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

package net.mumie.mathletfactory.math.number.numeric.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.numeric.EchelonForm;

/**
 *  This class tests the functionality of 
 *  {@link net.mumie.mathletfactory.number.numeric.EchelonForm}.
 * 
 *  @see  <a href="http://junit.sourceforge.net">JUnit</a>
 *  @author Amiranashvili, Paehler
 *  @mm.docstatus finished
 */
public class EchelonTest extends TestCase{
  
  private int m;
  private int n;
  
  private MDouble eps;
  
  private MDouble[] a=null;
  private MDouble[] p=null;
  private MDouble[] e=null;
  
  
  public EchelonTest(String method){
    super(method);
  }
  
  protected void setUp(){
    // set up the Dimensions of the input matrix
    m=4; // number of rows...
    n=5; // number of columns...
    
    // the input matrix
    double[] aa={0, -3, -6, 4, 9,     // the first row
        -1, -2, -1, 3, 1,    // the second row
        -2, -3, 0, 3, -1,    // the third row
        1, 4, 5, -9, -7};
    //set up the input matrix...
    // allocate the output matrix
    a=new MDouble[m*n];
    e=new MDouble[m*n];
    for(int i=0; i<m*n; i++){
      a[i]=new MDouble(aa[i]);
      e[i]=new MDouble();
    }
    
    // set up the epsilon parameter to 10 pow -10...
    eps=new MDouble(0.0000000001);
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toRowEchelonForm}
   */
  public void testEchelonForm(){
    MDouble[] id = new MDouble[]{ new MDouble(3), new MDouble(2), new MDouble(1), new MDouble(1)};
    MDouble[] outPut = new MDouble[4];
    MDouble[] elment = new MDouble[4];
    try {
    EchelonForm.toRowEchelonForm(id, 2, 2, elment, outPut);
    } catch( Exception e){e.printStackTrace();}
    //System.out.println(outPut[0] + ", "+outPut[1]+", "+outPut[2] +", "+outPut[3]);
    double[] coeffs =
    {    0, -3, -6,  4,  9,
        -1, -2, -1,  3,  1,
        -2, -3,  0,  3, -1,
         1,  4,  5, -9, -7};
    double[] result = { -2, -3, 0,  3, -1,
                        0,  -3, -6,  4, 9,
                        0,  0,  0, -4.166666666666666,  0,
                        0,  0,  0,  0,  0};
    MDouble[] inputMatrix = new MDouble[coeffs.length];
    MDouble[] outputMatrix = new MDouble[coeffs.length];
    MDouble[] resultMatrix = new MDouble[coeffs.length];
        
    for(int i=0; i<coeffs.length; i++){
      inputMatrix[i] = new MDouble(coeffs[i]);
      resultMatrix[i] = new MDouble(result[i]);
    }
    
    try {
      EchelonForm.toRowEchelonForm(inputMatrix, 4, 5, e, outputMatrix);
    } catch( Exception e){e.printStackTrace();}
    boolean testPassed = true;
    for(int i=0; i<result.length; i++){
      if(outputMatrix[i].getDouble() != resultMatrix[i].getDouble()){
       System.out.println("["+ i/4 +","+ i%4 +" : "+ outputMatrix[i] +" != "+ resultMatrix[i]);
       testPassed = false;
       }
    }
    /*
    for(int i=0; i<4; i++)
    System.out.println("\n( "+ outputMatrix[i*5]+", "+ outputMatrix[i*5+1] +
                       ", " + outputMatrix[i*5+2] + ", "+ outputMatrix[i*5+3]+
                       ", "+ outputMatrix[i*5+4]+")");
     */
    Assert.assertTrue(testPassed);
  }
  
  // test cases...
  
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toRowEchelonForm}
   * for elementary matrices.
   */
  public void testElementaryMatricesForRowEchelonForm(){
    // setting up the dimensions of the product of elementary matrices...
    int pdim=m;
    
    // allocating the matrix, which will hold the product of elemntary matrices...
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    
    //now cool stuff begins... transform the input matrix into the row echelon form...
    try{
      EchelonForm.toRowEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    //now test the results...
    //mathematician guys say that if we multiply the input matrix from left by
    //the product of the elementary matrices, we get the row echelon matrix, so
    //lets check it...
    
    assertEquals(m,n,mult(pdim,m,n,p,a),e,eps);
    
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toColumnEchelonForm}
   * for elementary matrices.
   */
  public void testElementaryMatricesForColumnEchelon(){
    // setting up the dimensions of the product of elementary matrices...
    int pdim=n;
    
    // allocating the matrix, which will hold the product of elemntary matrices...
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    
    //now cool stuff begins... transform the input matrix into the column echelon form...
    try{
      EchelonForm.toColumnEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    //now test the results...
    //mathematician guys say that if we multiply the input matrix from right by
    //the product of the elementary matrices, we get the column echelon matrix, so
    //lets check it...
    
    assertEquals(m,n,mult(m,n,pdim,a,p),e,eps);
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toReducedRowEchelonForm}
   * for elementary matrices.
   */
  public void testElementaryMatricesForReducedRowEchelonForm(){
    // do the same stuff as for the RowEchelon matrix...
    int pdim=m;
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    try{
      EchelonForm.toReducedRowEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    assertEquals(m,n,mult(pdim,m,n,p,a),e,eps);
  }
  
  /**
   * Tests {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toReducedColumnEchelonForm}
   * for elementary matrices.
   */
  public void testElementaryMatricesForReducedColumnEchelonForm(){
    // do the same stuff as for the ColumnEchelon matrix...
    int pdim=n;
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    try{
      EchelonForm.toReducedColumnEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    assertEquals(m,n,mult(m,n,pdim,a,p),e,eps);
  }
  
  
  /**
   * Tests the row echelon property for a result of 
   * {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toRowEchelonForm}.
   */
  public void testRowEchelonProperty(){
    // setting up the dimensions of the product of elementary matrices...
    int pdim=m;
    
    // allocating the matrix, which will hold the product of elemntary matrices...
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    
    //now cool stuff begins... transform the input matrix into the row echelon form...
    try{
      EchelonForm.toRowEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    // here we should check whether the output matrix
    // has the row echelon property...
    int numZero=0;
    for(int i=0; i<m; i++){
      for(int j=0; j<numZero; j++){
        assertEquals(0.0,e[i*n+j].getDouble(),eps.getDouble());
      }
      for(int j=numZero; j<n; j++){
        numZero=numZero+1;
        if(Math.abs(0.0-e[i*n+j].getDouble())>Math.abs(eps.getDouble())){
          break;
        }
      }
    }
  }
  
  
  /**
   * Tests the column echelon property for a result of 
   * {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toColumnEchelonForm}.
   */
  public void testColumnEchelonProperty(){
    // setting up the dimensions of the product of elementary matrices...
    int pdim=n;
    
    // allocating the matrix, which will hold the product of elemntary matrices...
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    
    //now cool stuff begins... transform the input matrix into the column echelon form...
    try{
      EchelonForm.toColumnEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    // here we should check wheather the output matrix
    // has the column echelon property...
    int numZero=0;
    for(int j=0; j<n; j++){
      for(int i=0; i<numZero; i++){
        assertEquals(0.0,e[i*n+j].getDouble(),eps.getDouble());
      }
      for(int i=numZero; i<m; i++){
        numZero=numZero+1;
        if(Math.abs(0.0-e[i*n+j].getDouble())>Math.abs(eps.getDouble())){
          break;
        }
      }
    }
  }
  
  
  /**
   * Tests the row echelon property for a result of 
   * {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toReducedRowEchelonForm}.
   */
  public void testReducedRowEchelonProperty(){
    // setting up the dimensions of the product of elementary matrices...
    int pdim=m;
    
    // allocating the matrix, which will hold the product of elemntary matrices...
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    
    //now cool stuff begins... transform the input matrix into the row echelon form...
    try{
      EchelonForm.toReducedRowEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    // here we should check wheather the output matrix
    // has the reduced row echelon property. A little trickier stuff than with the
    // row echelon property test...
    int numZero=0;
    for(int i=0; i<m; i++){
      for(int j=0; j<numZero; j++){
        if(j>n-1){
          break;
        }
        assertEquals(0.0,e[i*n+j].getDouble(),eps.getDouble());
      }
      
      for(int j=numZero; j<n; j++){
        numZero=numZero+1;
        if(Math.abs(0.0-e[i*n+j].getDouble())>Math.abs(eps.getDouble())){
          break;
        }
        if(j==n-1){
          numZero=numZero+1;
        }
      }
      
      if(numZero<=n){
        for(int k=0; k<m; k++){
          if(k==i){
            assertEquals(1.0,e[k*n+numZero-1].getDouble(),eps.getDouble());
          }
          else{
            assertEquals(0.0,e[k*n+numZero-1].getDouble(),eps.getDouble());
          }
        }
      }
    }
  }
  
  
  /**
   * Tests the column echelon property for a result of 
   * {@link net.mumie.mathletfactory.number.numeric.EchelonForm#toReducedColumnEchelonForm}.
   */
  public void testReducedColumnEchelonProperty(){
    // setting up the dimensions of the product of elementary matrices...
    int pdim=n;
    
    // allocating the matrix, which will hold the product of elemntary matrices...
    p=new MDouble[pdim*pdim];
    for(int i=0; i<pdim*pdim; i++){
      p[i]=new MDouble();
    }
    
    //now cool stuff begins... transform the input matrix into the column echelon form...
    try{
      EchelonForm.toReducedColumnEchelonForm(a,m,n,p,e);
    }
    catch(Exception e){
      e.printStackTrace();
    }
    
    // here we should check whether the output matrix
    // has the reduced column echelon property. A little trickier stuff than with the
    // column echelon property test...
    int numZero=0;
    for(int j=0; j<n; j++){
      for(int i=0; i<numZero; i++){
        if(i>m-1){
          break;
        }
        assertEquals(0.0,e[i*n+j].getDouble(),eps.getDouble());
      }
      
      for(int i=numZero; i<m; i++){
        numZero=numZero+1;
        if(Math.abs(0.0-e[i*n+j].getDouble())>Math.abs(eps.getDouble())){
          break;
        }
        if(i==m-1){
          numZero=numZero+1;
        }
      }
      
      if(numZero<=m){
        for(int k=0; k<n; k++){
          if(k==j){
            assertEquals(1.0,e[(numZero-1)*n+k].getDouble(),eps.getDouble());
          }
          else{
            assertEquals(0.0,e[(numZero-1)*n+k].getDouble(),eps.getDouble());
          }
        }
      }
    }
  }
  
  
  
  // private auxiliary methods for matrix multiplication and equality checking...
  
  private MNumber[] mult(int l,int m,int n,MNumber[] a,MNumber[] b){
    MNumber[] mm=new MNumber[l*n];
    for(int i=0; i<l*n; i++){
      mm[i]=(MNumber)a[0].create();
    }
    
    MNumber t;
    for(int i=0; i<l; i++){
      for(int j=0; j<n; j++){
        t=a[0].create();
        for(int k=0; k<m; k++){
          t.add(a[i*m+k].copy().mult(b[k*n+j]));
        }
        mm[i*n+j]=t.copy();
      }
    }
    
    return mm;
  }
  
  private void assertEquals(int m,int n,MNumber[] a,MNumber[] b,MNumber eps){
    for(int i=0; i<m; i++){
      for(int j=0; j<n; j++){
        assertEquals(a[i*n+j].getDouble(),b[i*n+j].getDouble(),eps.getDouble());
      }
    }
  }
  /**
   * Runs the test.
   */
  public static void main(String args[]) {
    junit.textui.TestRunner.run(EchelonTest.class);
  }
}
