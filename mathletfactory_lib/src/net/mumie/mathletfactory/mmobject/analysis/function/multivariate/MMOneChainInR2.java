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

package net.mumie.mathletfactory.mmobject.analysis.function.multivariate;

import java.util.ArrayList;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverBorelSet;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverBorelSetIF;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverRIF;
import net.mumie.mathletfactory.math.set.FiniteBorelSet;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.mmobject.analysis.function.MMFunctionDefByOp;
import net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class realises our "1-chain" in the <b>R</b><sup>2</sup>.
 *
 * The internal data structure is based on a list of
 * {@link net.mumie.mathletfactory.analysis.function.multivariate.VectorFunctionOverBorelSetIF}s,
 * each of them defining one element of the 1-chain.
 * <br>
 * <b>Observe</b> that it is up to the user to ensure that all these vector functions do
 * map into the same-dimensional space.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class MMOneChainInR2 extends MMDefaultCanvasObject implements MMOneChainInRNIF {

  
  /**
   * Internal data structure: a list of VectorFunctionOverBorelSetIF's,
   * for utility reasons stored within an ArrayList
   * In the usage it must hold at least 1 vector function.
   */
  protected final ArrayList m_vectorFunctions = new ArrayList();


  /**
   * Constructs a <code>MMOneChainInR2</code> based on the given <code>VectorFunctionOverRIF
   * </code> as "evaluation" expression and with domain being the whole real axis.
   */
  public MMOneChainInR2(VectorFunctionOverRIF aFunc) {
    VectorFunctionOverBorelSet f = new VectorFunctionOverBorelSet(aFunc);
        m_vectorFunctions.add(f);
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
  }

  /**
   * Constructs a <code>MMOneChainInR2</code> based on the given <code>VectorFunctionOverRIF
   * </code> as "evaluation" expression and with domain specified.
   */
  public MMOneChainInR2(
    VectorFunctionOverRIF aVectorFunction,
    Interval domain) {
    this(new VectorFunctionOverBorelSet(aVectorFunction,domain));
  }

  /**
   * Constructs a <code>MMOneChainInR2</code> based on the given <code>FunctionOverRIF
   * </code>s as "evaluation" expressions for x and y and with domain specified.
   */
  public MMOneChainInR2(
    FunctionOverRIF fx,
    FunctionOverRIF fy,
    Interval domain) {
    this(fx.getNumberClass(), new FunctionOverRIF[] { fx, fy }, domain);
  }

  /**
   * Constructs a <code>MMOneChainInR2</code> based on the given <code>FunctionOverRIF
   * </code>s as "evaluation" expression for y and the identity for x. The BorelSet of
   * <code>f</code> used for this chain.
   */
  public MMOneChainInR2(FunctionOverBorelSetIF f) {
    this(new VectorFunctionOverBorelSet(new FunctionOverBorelSetIF[]{new MMFunctionDefByOp(f.getNumberClass(),"x",f.getBorelSet()), f}));
  }

  /**
   * Constructs a new <code>OneChainInRN</code> consisting of the given <code>
   * VectorFunctionOverBorelSetIF</code>s as chain elements.
   * The traversed <code>VectorFunctionOverBorelSetIF</code>'s held within
   * this chain will be deep copied, no references to the traversed functions will be
   * stored.
   *
   * @param funcs are the defining elements of this <code>OneChainInRN</code>.
   */
  public MMOneChainInR2(VectorFunctionOverBorelSetIF[] funcs){
    for (int i=0; i< funcs.length; i++){
      m_vectorFunctions.add(funcs[i].deepCopy());
    }
    PolygonDisplayProperties p = new PolygonDisplayProperties();
    p.setFilled(false);
    setDisplayProperties(p);
  }

  /**
   * Copy constructor.
   */
  public MMOneChainInR2(MMOneChainInRIF oneChain){
    for(int i=0;i<oneChain.getComponentsCount();i++){
      FiniteBorelSet domain = oneChain.getBorelSetInComponent(i);
      MMFunctionDefByOp fx = new MMFunctionDefByOp(oneChain.getNumberClass(),"x",domain);
      VectorFunctionOverBorelSet vf = new VectorFunctionOverBorelSet(oneChain.getNumberClass(), new FunctionOverRIF[]{fx, oneChain.getEvaluateExpressionInComponent(i)}, domain);
      m_vectorFunctions.add(vf);
    }    
    setDisplayProperties((DisplayProperties)oneChain.getDisplayProperties().clone());      
  }
  
  public MMOneChainInR2(Class entryClass, FunctionOverRIF[] funcs, Interval domain){
    this(new VectorFunctionOverBorelSet(entryClass,funcs,domain));
    ((VectorFunctionOverBorelSet)getEvaluateExpressionInPart(0)).setBorelSet(new FiniteBorelSet(domain));
  }  
  
  /**
   * Adding two chains will change the calling <code>MMOneChainInR2</code>, which will
   * additionally contain the chain elements of <code>anotherChain</code> after this
   * mehtod call.
   *
   * @param anotherChain
   * @return the calling <code>OneChainInRN</code>
   */
  public MMOneChainInR2 add(MMOneChainInR2 anotherChain){
    for(int i=0; i<anotherChain.getPartCount(); i++)
      m_vectorFunctions.add((VectorFunctionOverBorelSetIF)anotherChain.getEvalExpression(i).deepCopy());
    //not appropriate because both chains would hold references to the same vectorfunctions
    //after the call
    //m_vectorFunctions.addAll(anotherChain.m_vectorFunctions);
    return this;
  }
  
  public FiniteBorelSet getBorelSetInPart(int index) {
    return getEvalExpression(index).getBorelSet();
  }
  
  public Interval getInterval(int expressionCount, int intervalIndexInBorelSet){
    return getEvalExpression(expressionCount).getBorelSet().getInterval(intervalIndexInBorelSet);
  }
  
  public int getIntervalCountInPart(int i){
    return getEvalExpression(i).getBorelSet().getIntervalCount();
  }
  
  public int getPartCount() {
    return m_vectorFunctions.size();
  }
  
  public VectorFunctionOverRIF getEvaluateExpressionInPart(int i) {
    return getEvalExpression(i);
  }
  
  public int getAllIntervalCount(){
    int result = 0;
    for(int i=0; i<getPartCount(); i++)
    result += getIntervalCountInPart(i);
    return result;
  }
  
  
  public Class getNumberClass() {
    // all evaluation expressions must be of the same number class,
    // so we may return the entry class of the first expression.
    return getEvalExpression(0).getNumberClass();
  }


  private VectorFunctionOverBorelSetIF getEvalExpression(int index){
    return (VectorFunctionOverBorelSetIF)m_vectorFunctions.get(index);
  }

	private boolean m_BoundaryVisibility = true;

	private int m_verticesCount = MMOneChainInRIF.DEFAULT_VERTICES_COUNT;

	public void getBoundingBox(
		final double[][] InCorners,
		final double[] outTminTmax) {
		//TODO: must be modified
		outTminTmax[0] = -1;
		outTminTmax[1] = +1;
	}

	public int getDefaultTransformType() {
		return GeneralTransformer.ONE_CHAIN_2D_AFFINE_GRAPH_TRANSFORM;
	}

	public int getDefaultTransformTypeInCanvas() {
		return GeneralTransformer.ONE_CHAIN_2D_AFFINE_GRAPH_TRANSFORM;
	}

  public void setVerticesCount(int vertexCount) {
    m_verticesCount = vertexCount;
  }

  public int getVerticesCount() {
    return m_verticesCount;
  }

  public boolean areBoundarysVisible() {
    return m_BoundaryVisibility;
  }

  public void setBoundarysVisible(boolean aFlag) {
    m_BoundaryVisibility = aFlag;
  }

	public void replacePartByFunctionGraph(int index, FunctionOverBorelSetIF f) {
		MMFunctionDefByOp fx =
			new MMFunctionDefByOp(f.getNumberClass(), "x", f.getBorelSet());
		VectorFunctionOverBorelSet vf =
			new VectorFunctionOverBorelSet(new FunctionOverBorelSetIF[] { fx, f });
		replacePart(index, vf);
	}

	public void replacePart(
		int index,
		VectorFunctionOverBorelSetIF vectorFunction) {
		m_vectorFunctions.remove(index);
		m_vectorFunctions.add(index, vectorFunction);
	}
}
