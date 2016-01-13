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

package net.mumie.mathletfactory.mmobject.analysis.sequence;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.OpTransform;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.algebra.op.rule.normalize.RemoveNeutralElementRule;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.analysis.sequence.SequenceIF;
import net.mumie.mathletfactory.math.number.MNatural;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMDefaultCanvasObject;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class represents a sequence that is defined by an
 * {@link net.mumie.mathletfactory.math.algebra.op.Operation}.
 *  
 * @author Paehler
 * @mm.docstatus finished
 */
public class MMSequenceDefByOp extends MMDefaultCanvasObject implements MMSequenceIF, UsesOperationIF {

  private Class m_numberClass;
  
  private Vector m_variableXOps;
  
  /** The operation that defines this sequence. */
  protected Operation m_operation = null;
  
  protected boolean m_normalize = true;
  
  private Map m_parameters = new HashMap();
  
  /** The name of the identifier representing the counting variable (default is "n"). */
  private String m_variableId = "n";
  
  /** Constructs the sequence from the given number class and expression. */
  public MMSequenceDefByOp(Class numberClass, String expression) {
    this(new Operation(numberClass, expression, true));
  }

  /** Constructs the sequence from the given operation. */
  public MMSequenceDefByOp(Operation operation){
    m_numberClass = operation.getNumberClass();
    setOperation(operation);
    setDisplayProperties(new DisplayProperties());
    setLabel("a");
  }
  
  /** Sets the operation for this sequence. */
  public void setOperation(String expr) {
    setOperation(OpParser.getOperation(getNumberClass(), expr, m_normalize));
  }

  /** Sets the operation for this sequence. */
  public void setOperation(Operation operation) {
    m_operation = operation;
    m_operation.setNormalForm(m_normalize);
    update();
  }

  /** Returns the operation for this sequence. */
  public Operation getOperation() {
    return m_operation;
  }

  /** Adds a parameter with the specified value to the function. */
  public void setParameter(String parameterName, MNumber value){
    m_parameters.put(parameterName, value);
    m_operation.setParameter(parameterName);
    m_operation.assignValue(parameterName, value);
    setOperation(m_operation);
  }

  /** Returns all parameters with values as a map. */
  public Map getParameters(){
    return m_parameters;
  }

  private void update() {
    m_variableXOps = new Vector();
    m_operation.putVariableOps(m_variableId, m_variableXOps);
    // check for other variables than m_variableId and mark them as parameters
    String[] variables = m_operation.getUsedVariables();
    for (int i = 0; i < variables.length; i++) {
      if(!variables[i].equals(m_variableId)){        
        m_operation.setParameter(variables[i]);
        m_operation.assignValue(variables[i], (MNumber)m_parameters.get(variables[i]));
      }
    }
  }

  public MNumber getSequenceValue(MNatural natural) {
    MNumber result = NumberFactory.newInstance(getNumberClass());
    MNumber n = NumberFactory.newInstance(getNumberClass(), natural.getDouble());
    m_operation.evaluate(n, result);
    return result;
  }

  public MNumber getSequenceValue(double index){
    MNatural _index = new MNatural();
    _index.setDouble(index);
    return getSequenceValue(_index);
  }
  
  /** Returns the set for which this sequence can be maximally defined. */
  public MMSetDefByRel getDefinedSet(){
    MMSetDefByRel set = new MMSetDefByRel(m_operation.getDefinedRelation());
    set.setStdDimension(MMSetDefByRel.STD_ONE_DIMENSIONAL_SET);
    return set;
  }

  /** Returns the relation that must be true for a member of the definition range.*/
  public Relation getDefinedRelation(){
    return m_operation.getDefinedRelation();
  }

  /** Returns {1,50} */
  public int[] getBounds(double fMin, double fMax, double epsilon, int[] bounds) {
    return new int[]{1,50};
  }
  
  /** Not yet implemented. */
  public MNumber getLimesInferior() {
    throw new TodoException();
  }
  
  /** Not yet implemented. */
  public MNumber getLimesSuperior() {
    throw new TodoException();
  }
  
  /** Not yet implemented. */
  public MNumber[] getAccumulationPoints() {
    throw new TodoException();
  }
  
  /** Not yet implemented. */
  public SequenceIF getSubSebquence(SequenceIF indexSequence) {
    throw new TodoException();
  }
    
  public Class getNumberClass(){
    return m_numberClass;
  }

  public void setEditable(boolean isEditable) {
    if(isEditable == isEditable())
      return;
    super.setEditable(isEditable);
    render();
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.SEQUENCE_TRANSFORM_1D;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.SEQUENCE_TRANSFORM_1D;
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.FUNCTION_TRANSFORM;
  }

  /** Listens exclusively for OPERATION events and adjusts its operation to the new value. */
  public void propertyChange(PropertyChangeEvent evt) {
    if (!evt.getPropertyName().equals(OPERATION))
      return;
    setOperation(new Operation((Operation) evt.getNewValue()));
    ActionManager.performActionCycleFromObject(this);
  }

  /** Returns the name of the identifier representing the counting variable (default is "n"). */
  public String getVariableId() {
    return m_variableId;
  }

  /** Sets the name of the identifier representing the counting variable (default is "n"). */
  public void setVariableId(String string) {
    m_variableId = string;
  }
  
	public void setNormalForm(boolean normalize) {
		m_normalize = normalize;
		m_operation.setNormalForm(normalize);
		if (!m_normalize)
			m_operation.setOpRoot(OpTransform.applyRule(m_operation.getOpRoot(), new RemoveNeutralElementRule()));
	}
	
	public boolean isNormalForm() {
		return m_normalize;
	}
}
