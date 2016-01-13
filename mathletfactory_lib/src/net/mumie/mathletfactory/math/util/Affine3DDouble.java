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

package net.mumie.mathletfactory.math.util;

import net.mumie.mathletfactory.util.History;

/**
 *  This class models an element of the affine group(R^3): its data is kept in 
 *  a 3x3 deformation matrix and a 3d translation vector. It is needed for 
 *  fast 3d rendering within a subclass of
 *  {@link net.mumie.mathletfactory.display.MM3DCanvas}.
 *  This class is highly internal and you should use it only if you know, what
 *  you are doing. For the undocumented methods, consult the source code.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class Affine3DDouble extends AffineDouble {
  
  
  /** The 3x3 deformation matrix. */
  protected double[][] a = new double[3][3];
  /** The 3d translation vector. */
  protected double[] t = new double[3];

  private History m_history;

  /** Returns the history of this affine group element. */
  public History getHistory() {
    return m_history;
  }

  /** Adds a snapshot to the history of this affine group element. */
  public void addSnapshotToHistory() {
    m_history.addItem(new double[] { a[0][0], a[0][1], a[0][2], a[1][0], a[1][1], a[1][2], a[2][0], a[2][1], a[2][2], t[0], t[1], t[2] });
  }

  /** Sets this affine group element to the latest history item. */
  public void setFromHistory() {
    double[] historyItem = (double[]) m_history.getCurrentItem();
    a[0][0] = historyItem[0];
    a[0][1] = historyItem[1];
    a[0][2] = historyItem[2];
    a[1][0] = historyItem[3];
    a[1][1] = historyItem[4];
    a[1][2] = historyItem[5];
    a[2][0] = historyItem[6];
    a[2][1] = historyItem[7];
    a[2][2] = historyItem[8];
    t[0] = historyItem[9];
    t[1] = historyItem[10];
    t[2] = historyItem[11];
  }
  
  /** Constructing the affine group element as the neutral element. */
  public Affine3DDouble(){
    m_history = new History();
    a[0][0] = 1;
    a[1][1] = 1;
    a[2][2] = 1;
  }
  
  /** Sets the affine group element to the given values of the deformation matrix. */
  public void set( double a00, double a01, double a02,
                   double a10, double a11, double a12,
                   double a20, double a21, double a22){
    this.a[0][0] = a00; this.a[0][1] = a01; this.a[0][2] = a[0][2];
    this.a[1][0] = a10; this.a[1][1] = a11; this.a[1][2] = a12;
    this.a[2][0] = a20; this.a[2][1] = a21; this.a[2][2] = a22;
    t[0] = 0; t[1] = 0; t[2] = 0;
  }
  
  /**
   * Sets the affine group element to the given values of the deformation 
   * matrix and of the translation vector. 
   */ 
  public void set( double a00, double a01, double a02,
                   double a10, double a11, double a12,
                   double a20, double a21, double a22,
                   double t0, double t1, double t2){
    this.a[0][0] = a00; this.a[0][1] = a01; this.a[0][2] = a[0][2];
    this.a[1][0] = a10; this.a[1][1] = a11; this.a[1][2] = a12;
    this.a[2][0] = a20; this.a[2][1] = a21; this.a[2][2] = a22;
    t[0] = t0; t[1] = t1; t[2] = t2;
  }
  
  /**
   * Sets the deformation matrix to identity and the translation vector to zero.
   */
  public void setToIdentity(){
    for(int i=0; i<3;i++){
      for(int j=0; j<3;j++)
        a[i][j] = ( i==j ? 1 : 0);
      t[i] = 0;
    }
  }
    
  /**
   *  Returns a(i,j) for  j<3 and t(i) otherwise.
   */
  public double get(int i, int j){
    if(j<3)
      return a[i][j];
    else
      return t[i];
  }
  
  /**
   *  Sets a(i,j) for  j<3 and t(i) otherwise.
   */
  public void set(int i, int j, double value){
    if(j<3)
      a[i][j] = value;
    else
      t[i] = value;
  }
  
  /**
   *  Sets the affine group element so that the the rectangle formed by the 
   *  given screen parameters is transformed to the union square. 
   */  
  public Affine3DDouble setScreenToNormalized(double width, double height){
    a[0][0] = 1./width;
    a[1][1] = -1./height;
    a[0][1] = 0;
    a[1][0] = 0;
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    t[0] = 0;
    t[1] = 1;
    return this;
  }
  
  
  /**
   *  Sets the affine group element so that the union square is transformed 
   *  into a square of best size fitting completely inside the screen.
   */
  public Affine3DDouble setNormalizedToScreenBestFit(double width, double height){
    double lambda = Math.min(width, height);
    a[0][0] = lambda;
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = -a[0][0];
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    t[0] = 0;
    t[1] = height;
    t[2] = 0;
    return this;
  }
  
  /**
   *  Sets the affine group element so that the the rectangle formed by the 
   *  screen is transformed to the specified rectangle. 
   */  
  public Affine3DDouble  setScreenToRectangle(double[] LL, double UR[],
    double width, double height){
    // not optimally implemented yet
    Affine3DDouble tmp = new Affine3DDouble();
    tmp.setScreenToNormalized(width,height);
    this.setNormalizedToRectangle(LL,UR);
    this.multRight(tmp);
    return this;
  }
  
  /**
   *  Sets the affine group element so that the the rectangle formed by the 
   *  screen is transformed to the specified rectangle. 
   */  
  public Affine3DDouble  setRectangleToScreen(double[] LL, double UR[],
      double width, double height){
    // not optimally implemented yet
    Affine3DDouble tmp = new Affine3DDouble();
    tmp.setNormalizedToScreen(width,height);
    this.setRectangleToNormalized(LL,UR);
    this.multLeft(tmp);
    return this;
  }
  
  /**
   *  Sets the affine group element so that the union square is transformed 
   *  into the screen rectangle specified by the given parameters.
   */
  public Affine3DDouble setNormalizedToScreen(double width, double height){
    a[0][0] = width;
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = -height;
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    t[0] = 0;
    t[1] = height;
    t[2] = 0;
    return this;
  }
  
  /**
   *  Sets the affine group element so that the union square is transformed 
   *  into the rectangle specified by the given parameters.
   */
  public Affine3DDouble setNormalizedToRectangle(double[] LL, double UR[]){
    t[0] = LL[0];
    t[1] = LL[1];
    t[2] = 0;
    a[0][0] = UR[0]-LL[0];
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = UR[1]-LL[1];
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    
    return this;
  }
  
  /**
   * This method sets this instance of <code>Affine3DDouble</code> to be the
   * map, that has uniform scale and maps (0,0) -&gt; (LL[0], LL[1]) and
   * (1,1) -&gt; (max{UR[0],UR[1]}, max{UR[0],UR[1]}). It is expected that
   * LL[i] &lt; UR[i] strictly for i=1,2.
   */
  public Affine3DDouble setNormalizedToRectUniform(double[] LL, double[] UR) {
    t[0] = LL[0];
    t[1] = LL[1];
    t[2] = 0;
    a[0][0] = Math.max(UR[0],UR[1]);
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = Math.max(UR[0],UR[1]);
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    
    return this;
  }

  public Affine3DDouble setRectToScreenUF(double[] LL, double[] UR, double screenWidth, double screenHeight) {
    return setRectToScreenUF(LL,UR,screenWidth,screenHeight,LL[0],LL[1],0,screenHeight);
  }
  
  public Affine3DDouble setRectToScreenUF(double[] LL, double[] UR,
                                               double screenWidth, double screenHeight,
                                               double pX, double pY,
                                               double xOnScreen, double yOnScreen) {
    double lambda = Math.max( screenWidth/(UR[0]-LL[0]), screenHeight/(UR[1]-LL[1]) );
    a[0][0] = lambda;
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = -lambda;
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    t[0] = xOnScreen - a[0][0]*pX;
    t[1] = yOnScreen - a[1][1]*pY;
    t[2] = 0;
    return this;
  }
  
  public Affine3DDouble setPointToCanvasCentreUF(double pointX, double pointY, double ufScale, double screenWidth, double screenHeight) {
    a[0][0] = ufScale;
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = -ufScale;
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    t[0] = screenWidth/2. - a[0][0]*pointX;
    t[1] = screenHeight/2. - a[1][1]*pointY;
    t[2] = 0;
    
    return this;
  }
  
  public Affine3DDouble setRectangleToNormalized(double [] LL, double UR[]){
    t[0] = -LL[0];
    t[1] = -LL[1];
    t[2] = 0;
    a[0][0] = 1./ (UR[0]-LL[0]);
    a[0][1] = 0;
    a[0][2] = 0;
    a[1][0] = 0;
    a[1][1] = 1./ (UR[1]-LL[1]);
    a[1][2] = 0;
    a[2][0] = 0;
    a[2][1] = 0;
    a[2][2] = 1;
    
    return this;
    
  }
  
  
  public void setFromTranslationAndScale(double t0, double t1, double t2,  double a00){
    this.t[0]  = t[0];
    this.t[1]  = t[1];
    this.t[2]  = t[2];
    this.a[0][0] = a00;
    this.a[0][1] = 0;
    this.a[0][2] = 0;
    this.a[1][0] = 0;
    this.a[1][1] = a00;
    this.a[1][2] = 0;
    this.a[2][0] = 0;
    this.a[2][1] = 0;
    this.a[2][1] = a00;
    
  }
  /**
   *   this -> this * map2
   */
  public Affine3DDouble multRight(Affine3DDouble map2){
    t[0] = a[0][0]*map2.t[0]+a[0][1]*map2.t[1]+a[0][2]*map2.t[2]+t[0];
    t[1] = a[1][0]*map2.t[0]+a[1][1]*map2.t[1]+a[1][2]*map2.t[2]+t[1];
    t[2] = a[2][0]*map2.t[0]+a[2][1]*map2.t[1]+a[2][2]*map2.t[2]+t[2];
    double tmp00 = a[0][0];
    double tmp01 = a[0][1];
    double tmp10 = a[1][0];
    double tmp11 = a[1][1];
    double tmp20 = a[2][0];
    double tmp21 = a[2][1];
    a[0][0] = tmp00*map2.a[0][0]+  a[0][1]*map2.a[1][0]+a[0][2]*map2.a[2][0];
    a[0][1] = tmp00*map2.a[0][1]+  a[0][1]*map2.a[1][1]+a[0][2]*map2.a[2][1];
    a[0][2] = tmp00*map2.a[0][2]+tmp01*map2.a[1][2]+a[0][2]*map2.a[2][2];
    a[1][0] = tmp10*map2.a[0][0]+  a[1][1]*map2.a[1][0]+a[1][2]*map2.a[2][0];
    a[1][1] = tmp10*map2.a[0][1]+  a[1][1]*map2.a[1][1]+a[1][2]*map2.a[2][1];
    a[1][2] = tmp10*map2.a[0][2]+tmp11*map2.a[1][2]+a[1][2]*map2.a[2][2];
    a[2][0] = tmp20*map2.a[0][0]+  a[2][1]*map2.a[1][0]+a[2][2]*map2.a[2][0];
    a[2][1] = tmp20*map2.a[0][1]+  a[2][1]*map2.a[1][1]+a[2][2]*map2.a[2][1];
    a[2][2] = tmp20*map2.a[0][2]+tmp21*map2.a[1][2]+a[2][2]*map2.a[2][2];
    
    return this;
  }
  
  /**
   *   this -> map2 * this
   */
  public Affine3DDouble multLeft(Affine3DDouble map2){
    t[0] = map2.t[0] + map2.a[0][0] * t[0] + map2.a[0][1] * t[1] + map2.a[0][2] * t[2];
    t[1] = map2.t[1] + map2.a[1][0] * t[0] + map2.a[1][1] * t[1] + map2.a[1][2] * t[2];
    t[2] = map2.t[2] + map2.a[2][0] * t[0] + map2.a[2][1] * t[1] + map2.a[2][2] * t[2];
    
    double tmp00 = a[0][0];
    double tmp01 = a[0][1];
    double tmp02 = a[0][2];
    double tmp10 = a[1][0];
    double tmp11 = a[1][1];
    double tmp12 = a[1][2];
    double tmp20 = a[2][0];
    double tmp21 = a[2][1];
    
    a[0][0] = map2.a[0][0]*tmp00 + map2.a[0][1] * tmp10 + map2.a[0][2] * tmp20;
    a[1][0] = map2.a[1][0]*tmp00 + map2.a[1][1] * tmp10 + map2.a[1][2] * tmp20;
    a[2][0] = map2.a[2][0]*tmp00 + map2.a[2][1] * tmp10 + map2.a[2][2] * tmp20;
    a[0][1] = map2.a[0][0]*tmp01 + map2.a[0][1] * tmp11 + map2.a[0][2] * tmp21;
    a[1][1] = map2.a[1][0]*tmp01 + map2.a[1][1] * tmp11 + map2.a[1][2] * tmp21;
    a[2][1] = map2.a[2][0]*tmp01 + map2.a[2][1] * tmp11 + map2.a[2][2] * tmp21;
    a[0][2] = map2.a[0][0]*tmp02 + map2.a[0][1] * tmp12 + map2.a[0][2] * a[2][2];
    a[1][2] = map2.a[1][0]*tmp02 + map2.a[1][1] * tmp12 + map2.a[1][2] * a[2][2];
    a[2][2] = map2.a[2][0]*tmp02 + map2.a[2][1] * tmp12 + map2.a[2][2] * a[2][2];
    return this;
  }
  
  
  public void applyTo(double xIn, double yIn, double zIn, double[] out) {
    out[0] = a[0][0]*xIn + a[0][1]*yIn + a[0][2] * zIn + t[0];
    out[1] = a[1][0]*xIn + a[1][1]*yIn + a[1][2] * zIn + t[1];
    out[2] = a[2][0]*xIn + a[2][1]*yIn + a[2][2] * zIn + t[2];
  }
  
  public void applyDeformationTo(double xIn, double yIn, double zIn, double[] out) {
    out[0] = a[0][0]*xIn + a[0][1]*yIn + a[0][2] * zIn;
    out[1] = a[1][0]*xIn + a[1][1]*yIn + a[1][2] * zIn;
    out[2] = a[2][0]*xIn + a[2][1]*yIn + a[2][2] * zIn;
  }
  
  // beware of reentrant code! must have in != out !!!
  public void applyTo(double[] in, double[] out){
    out[0] = a[0][0]*in[0] + a[0][1]*in[1] + a[0][2]*in[2] + t[0];
    out[1] = a[1][0]*in[0] + a[1][1]*in[1] + a[1][2]*in[2] + t[1];
    out[2] = a[2][0]*in[0] + a[2][1]*in[1] + a[2][2]*in[2] + t[2];
  }
  
  public void applyDeformationPartTo(double[] in, double[] out){
    out[0] = a[0][0]*in[0] + a[0][1]*in[1] + a[0][2]*in[2];
    out[1] = a[1][0]*in[0] + a[1][1]*in[1] + a[1][2]*in[2];
    out[2] = a[2][0]*in[0] + a[2][1]*in[1] + a[2][2]*in[2];
  }
  
  public void applyDeformationPartTo(double[] inout){
    double tmp0 = inout[0];
    double tmp1 = inout[1];
    double tmp2 = inout[2];
    inout[0] = a[0][0]*tmp0 + a[0][1]*tmp1 + a[0][2]*tmp2;
    inout[1] = a[1][0]*tmp0 + a[1][1]*tmp1 + a[1][2]*tmp2;
    inout[2] = a[2][0]*tmp0 + a[2][1]*tmp1 + a[2][2]*tmp2;
  }
  
  public void applyTo(double[] xin, double[] yin, double[] zin, double[] xout, double[] yout, double[] zout){
    for (int i=0; i<xin.length; ++i){
      xout[i] = a[0][0]*xin[i] + a[0][1]*yin[i] + a[0][2]*zin[i] + t[0];
      yout[i] = a[1][0]*xin[i] + a[1][1]*yin[i] + a[1][2]*zin[i] + t[1];
      zout[i] = a[2][0]*xin[i] + a[2][1]*yin[i] + a[2][2]*zin[i] + t[2];
    }
  }
  
  public void applyTo(double[][] inArray, double[][] outArray) {
    for(int i=0; i<inArray.length; i++)
      applyTo(inArray[i],outArray[i]);
  }

  public double getDeterminant() {
    return 1. / ( a[0][0] * (a[1][1]*a[2][2] - a[2][1]*a[1][2]) - a[1][0] * (a[0][1]*a[2][2] - a[2][1]*a[0][2])
                            + a[2][0] * (a[0][1]*a[1][2] - a[1][1]*a[0][2]));
  }
  
  //beware this != out !!!!!
  public Affine3DDouble putInverseOfMeInto(Affine3DDouble out){
    double invdet = getDeterminant();
    
    out.a[0][0] =  invdet * (a[1][1]*a[2][2] - a[2][1]*a[1][2]);
    out.a[0][1] = -invdet * (a[0][1]*a[2][2] - a[2][1]*a[0][2]);
    out.a[0][2] =  invdet * (a[0][1]*a[1][2] - a[1][1]*a[0][2]);
    out.a[1][0] = -invdet * (a[1][0]*a[2][2] - a[2][0]*a[1][2]);
    out.a[1][1] =  invdet * (a[0][0]*a[2][2] - a[2][0]*a[0][2]);
    out.a[1][2] = -invdet * (a[0][0]*a[1][2] - a[1][0]*a[0][2]);
    out.a[2][0] =  invdet * (a[1][0]*a[2][1] - a[2][0]*a[1][1]);
    out.a[2][1] = -invdet * (a[0][0]*a[2][1] - a[2][0]*a[0][1]);
    out.a[2][2] =  invdet * (a[0][0]*a[1][1] - a[1][0]*a[0][1]);
    
    out.t[0] = - (out.a[0][0] * t[0] + out.a[0][1] * t[1] + out.a[0][2] * t[2]);
    out.t[1] = - (out.a[1][0] * t[0] + out.a[1][1] * t[1] + out.a[1][2] * t[2]);
    out.t[2] = - (out.a[2][0] * t[0] + out.a[2][1] * t[1] + out.a[2][2] * t[2]);
    return this;
  }
  
  public Affine3DDouble set(Affine3DDouble anOther){
    a[0][0] = anOther.a[0][0];
    a[0][1] = anOther.a[0][1];
    a[0][2] = anOther.a[0][2];
    a[1][0] = anOther.a[1][0];
    a[1][1] = anOther.a[1][1];
    a[1][2] = anOther.a[1][2];
    a[2][0] = anOther.a[2][0];
    a[2][1] = anOther.a[2][1];
    a[2][2] = anOther.a[2][2];
    
    t[0] = anOther.t[0];
    t[1] = anOther.t[1];
    t[2] = anOther.t[2];
    return this;
  }
  
  // not optimally implememnted yet...
  public Affine3DDouble invertMe(){
    Affine3DDouble tmp_helper = new Affine3DDouble();
    putInverseOfMeInto(tmp_helper);
    this.set(tmp_helper);
    return this;
  }
  
  public Affine3DDouble setTranslation(double[] t){
    this.t[0] = t[0];
    this.t[1] = t[1];
    this.t[2] = t[2];
    return this;
  }
  
  public double[] getTranslation(){
    return t;
  }
  
  public Affine3DDouble setDeformationPart(Affine3DDouble anAffineDouble){
    for(int i=0;i<3;i++)
      for(int j=0;j<3;j++)
        this.a[i][j] = anAffineDouble.a[i][j];
    return this;
  }
  
  public Affine3DDouble leftTranslate(double [] b){
    t[0] += b[0];
    t[1] += b[1];
    t[2] += b[2];
    return this;
  }
  
  public Affine3DDouble leftTranslate(double b0, double b1, double b2){
    t[0] += b0;
    t[1] += b1;
    t[2] += b2;
    return this;
  }
  
  public Affine3DDouble rightTranslate(double [] b){
    t[0] += b[0]*a[0][0] + b[1]*a[0][1] + b[2]*a[0][2];
    t[1] += b[0]*a[1][0] + b[1]*a[1][1] + b[2]*a[1][2];
    t[2] += b[0]*a[2][0] + b[1]*a[2][1] + b[2]*a[2][2];
    return this;
  }
  
  public Affine3DDouble leftScale(double lambda){
    a[0][0] *= lambda;
    a[0][1] *= lambda;
    a[0][2] *= lambda;
    a[1][0] *= lambda;
    a[1][1] *= lambda;
    a[1][2] *= lambda;
    a[2][0] *= lambda;
    a[2][1] *= lambda;
    a[2][2] *= lambda;
    
    return this;
  }
  
  public Affine3DDouble rightScale(double lambda){
    a[0][0] *= lambda;
    a[0][1] *= lambda;
    a[0][2] *= lambda;
    a[1][0] *= lambda;
    a[1][1] *= lambda;
    a[1][2] *= lambda;
    a[2][0] *= lambda;
    a[2][1] *= lambda;
    a[2][2] *= lambda;
    
    t[0] *= lambda;
    t[1] *= lambda;
    t[2] *= lambda;
    
    return this;
  }
  
  // result is: map1 becomes  "map1 applied after map2":
  public static void compose(Affine3DDouble map1, Affine3DDouble map2) {
    map1.t[0] = map1.a[0][0]*map2.t[0]+map1.a[0][1]*map2.t[1]+map1.a[0][2]*map2.t[2]+map1.t[0];
    map1.t[1] = map1.a[1][0]*map2.t[0]+map1.a[1][1]*map2.t[1]+map1.a[1][2]*map2.t[2]+map1.t[1];
    map1.t[2] = map1.a[2][0]*map2.t[0]+map1.a[2][1]*map2.t[1]+map1.a[2][2]*map2.t[2]+map1.t[2];
    double tmp00 = map1.a[0][0];
    double tmp01 = map1.a[0][1];
    double tmp10 = map1.a[1][0];
    double tmp11 = map1.a[1][1];
    double tmp20 = map1.a[2][0];
    double tmp21 = map1.a[2][1];
    map1.a[0][0] = tmp00*map2.a[0][0]+map1.a[0][1]*map2.a[1][0]+map1.a[0][2]*map2.a[2][0];
    map1.a[0][1] = tmp00*map2.a[0][1]+map1.a[0][1]*map2.a[1][1]+map1.a[0][2]*map2.a[2][1];
    map1.a[0][2] = tmp00*map2.a[0][2]+       tmp01*map2.a[1][2]+map1.a[0][2]*map2.a[2][2];
    map1.a[1][0] = tmp10*map2.a[0][0]+map1.a[1][1]*map2.a[1][0]+map1.a[1][2]*map2.a[2][0];
    map1.a[1][1] = tmp10*map2.a[0][1]+map1.a[1][1]*map2.a[1][1]+map1.a[1][2]*map2.a[2][1];
    map1.a[1][2] = tmp10*map2.a[0][2]+       tmp11*map2.a[1][2]+map1.a[1][2]*map2.a[2][2];
    map1.a[2][0] = tmp20*map2.a[0][0]+map1.a[2][1]*map2.a[1][0]+map1.a[2][2]*map2.a[2][0];
    map1.a[2][1] = tmp20*map2.a[0][1]+map1.a[2][1]*map2.a[1][1]+map1.a[2][2]*map2.a[2][1];
    map1.a[2][2] = tmp20*map2.a[0][2]+       tmp21*map2.a[1][2]+map1.a[2][2]*map2.a[2][2];
  }
  
  public static void compose(Affine3DDouble result, Affine3DDouble map1, Affine3DDouble map2) {
    result.t[0] = map1.a[0][0]*map2.t[0]+map1.a[0][1]*map2.t[1]+map1.a[0][2]*map2.t[2]+map1.t[0];
    result.t[1] = map1.a[1][0]*map2.t[0]+map1.a[1][1]*map2.t[1]+map1.a[1][2]*map2.t[2]+map1.t[1];
    result.t[2] = map1.a[2][0]*map2.t[0]+map1.a[2][1]*map2.t[1]+map1.a[2][2]*map2.t[2]+map1.t[2];
  
    result.a[0][0] = map1.a[0][0]*map2.a[0][0]+map1.a[0][1]*map2.a[1][0]+map1.a[0][2]*map2.a[2][0];
    result.a[0][1] = map1.a[0][0]*map2.a[0][1]+map1.a[0][1]*map2.a[1][1]+map1.a[0][2]*map2.a[2][1];
    result.a[0][2] = map1.a[0][0]*map2.a[0][2]+map1.a[0][1]*map2.a[1][2]+map1.a[0][2]*map2.a[2][2];
    result.a[1][0] = map1.a[1][0]*map2.a[0][0]+map1.a[1][1]*map2.a[1][0]+map1.a[1][2]*map2.a[2][0];
    result.a[1][1] = map1.a[1][0]*map2.a[0][1]+map1.a[1][1]*map2.a[1][1]+map1.a[1][2]*map2.a[2][1];
    result.a[1][2] = map1.a[1][0]*map2.a[0][2]+map1.a[1][1]*map2.a[1][2]+map1.a[1][2]*map2.a[2][2];
    result.a[2][0] = map1.a[2][0]*map2.a[0][0]+map1.a[2][1]*map2.a[1][0]+map1.a[2][2]*map2.a[2][0];
    result.a[2][1] = map1.a[2][0]*map2.a[0][1]+map1.a[2][1]*map2.a[1][1]+map1.a[2][2]*map2.a[2][1];
    result.a[2][2] = map1.a[2][0]*map2.a[0][2]+map1.a[2][1]*map2.a[1][2]+map1.a[2][2]*map2.a[2][2];
  }
  
  public static void invert(Affine3DDouble map, Affine3DDouble invMap) {
    map.putInverseOfMeInto(invMap);
  }
  
  public String toString(){
    return "[ "+"[ "+a[0][0]+", "+a[0][1]+", "+a[0][2]+" ], "+
                "[ "+a[1][0]+", "+a[1][1]+", "+a[1][2]+" ], "+
                "[ "+a[2][0]+", "+a[2][1]+", "+a[2][2]+" ] ], "+
                "[ "+t[0]+", "+t[1]+", "+t[2]+" ]";
  }
  
  public void setToRotation(double[] axis, double phi){
    double cos = Math.cos(phi);
    double sin = Math.sin(phi);
    double oneMinusCos = 1- cos;
    a[0][0] = cos + oneMinusCos * axis[0] * axis[0];
    a[0][1] = oneMinusCos * axis[0] * axis[1] - axis[2] * sin;
    a[0][2] = oneMinusCos * axis[0] * axis[2] + axis[1] * sin;
    a[1][0] = oneMinusCos * axis[0] * axis[1] + axis[2] * sin;
    a[1][1] = cos + oneMinusCos * axis[1] * axis[1];
    a[1][2] = oneMinusCos * axis[1] * axis[2] - axis[0] * sin;
    a[2][0] = oneMinusCos * axis[0] * axis[2] - axis[1] * sin;
    a[2][1] = oneMinusCos * axis[1] * axis[2] + axis[0] * sin;
    a[2][2] = cos + oneMinusCos * axis[2] * axis[2];
  }
  
  public static double getTheta(double[] vector3d) {
    // theta = acos( z/r )
    double theta = Math.acos(vector3d[2]/standardNorm(vector3d));
    if(Double.isNaN(theta))
      return 0;
    return theta;

  }

  public static double getPhi(double[] vector3d) {
    // phi = atan( y/x )
    double phi = Math.atan(vector3d[1]/vector3d[0]);
    if(Double.isNaN(phi))
      return 0;
    return phi;
  }  
  
  public static double[] crossProduct(double[] x1, double[] x2){
    return new double[]{ x1[1] * x2[2] - x1[2] * x2[1],
                         x2[0] * x1[2] - x2[2] * x1[0],
                         x1[0] * x2[1] - x1[1] * x2[0]};
  }
  
  /**
   * Small testing unit.
   */
  public static void main(String[] args){
    double[] cross = crossProduct(new double[]{1,0,1}, new double[]{1,1,0});
    System.out.println(cross[0]+","+cross[1]+","+cross[2]);
    Affine3DDouble test = new Affine3DDouble();
    test.a[0][0] = 0; test.a[0][1] = 1; test.a[0][2] = -4;
    test.a[1][0] = 1; test.a[1][1] = 2; test.a[1][2] = -1;
    test.a[2][0] = 1; test.a[2][1] = 1; test.a[2][2] = 2;
    test.t[0] = 1;  test.t[1]  = 5; test.t[2] = 7;
    Affine3DDouble inverse = new Affine3DDouble();
    test.putInverseOfMeInto(inverse);
    System.out.println("inverse : "+inverse);
    Affine3DDouble result = new Affine3DDouble();
    compose(result, test,inverse);
    System.out.println(result);
    Affine3DDouble test2 = new Affine3DDouble();
    test.setToRotation(new double[]{1./3,2./3,2./3}, 3* Math.PI/2);
    test2.setToRotation(new double[]{1./3,2./3,2./3}, Math.PI/2);
    
    compose(result, test2,test);
    System.out.println(result);
    
  }
}

