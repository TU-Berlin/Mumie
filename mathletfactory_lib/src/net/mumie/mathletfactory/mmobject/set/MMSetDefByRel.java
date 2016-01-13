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

package net.mumie.mathletfactory.mmobject.set;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.algebra.rel.Relation;
import net.mumie.mathletfactory.math.algebra.rel.UsesRelationIF;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents a set which is equivalent to the mathematical
 *  notation: <i>M</i> := { (<i>x</i>, <i>y</i>,...) | <i>r(x,y,...)</i>} where
 *  <i>r(x,y,...)</i> is an arbitrary
 *  {@link net.mumie.mathletfactory.math.algebra.rel.Relation}, that should be
 *  satisfied by (<i>x</i>,<i>y</i>,...) if it is in M.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */

public class MMSetDefByRel extends MMSet implements UsesRelationIF {

  /** The relation that specifies this set. */
  protected MMRelation m_relation;


  /**
   * Constructs the set from the given number class and the string to be
   * parsed as relation.
   */
  public MMSetDefByRel(Class numberClass, String relationString){
    setRelation(new Relation(numberClass, relationString, Relation.NORMALIZE_OP));
  }

  /** Constructs the set from the given relation. */
  public MMSetDefByRel(Relation relation){
    setRelation(relation);
  }

  /** Copy constructor */
  public MMSetDefByRel(MMSetDefByRel aNumberSet){
    setRelation(new MMRelation(aNumberSet.m_relation));
    setStdDimension(aNumberSet.getStdDimension());
    setDisplayProperties(aNumberSet.getDisplayProperties());
  }

  /** Returns the relation that specifies this set. */
  public Relation getRelation(){
    return m_relation;
  }

  /** Sets the relation that specifies this set. */
  public void setRelation(MMRelation relation){
    setRelation((Relation)relation);
  }

  /** Sets the relation that specifies this set. */
  public void setRelation(Relation relation){
    if(!(relation instanceof MMRelation))
      relation = new MMRelation(relation);
    m_relation = (MMRelation)relation;
    m_relation.prepareEvaluateFast();
    m_relation.setEditable(isEditable());
  }

  /**
   *  If the set has a std dimension, it returns <code>{"x"}</code>,
   * <code>{"x","y"}</code> or <code>{"x","y","z"}</code>. Otherwise it
   *  returns the array of variables used by the relation.
   */
  public String[] getUsedVariablesArray(){
    if(getStdDimension() == STD_ONE_DIMENSIONAL_SET)
      return new String[]{ getNumberClass().isAssignableFrom(MComplex.class) ? "z" : "x"};
    if(getStdDimension() == STD_TWO_DIMENSIONAL_SET)
      return new String[]{"x","y"};
    if(getStdDimension() == STD_THREE_DIMENSIONAL_SET)
      return new String[]{"x","y", "z"};
    return m_relation.getUsedVariablesArray();
  }

  /**
   * Returns true, if the given number is contained in the set.
   * @throws IllegalArgumentException the relation specifying the set has
   * more than one variable.
   * @see net.mumie.mathletfactory.math.set.NumberSetIF#contains(double)
   */
  public boolean contains(double aNumber){
    return contains(NumberFactory.newInstance(getNumberClass(), aNumber));
  }

  /**
   * Returns true, if the given number is contained in the set.
   * @throws IllegalArgumentException if the relation specifying the set has
   * more than one variable.
   * @see net.mumie.mathletfactory.math.set.NumberSetIF#contains(double)
   */
  public boolean contains(MNumber aNumber){
    return m_relation.evaluate(new MNumber[]{aNumber});
  }

  /**
   * Returns true, if the given number tuple is contained in the set.
   * @throws IllegalArgumentException if the relation specifying the set has
   * more variables than the
   * {@link net.mumie.mathletfactory.math.algebra.linalg.NumberTuple#getColumnCount() size}
   * of the argument.
   * @see net.mumie.mathletfactory.math.set.NumberSetIF#contains(double)
   */
  public boolean contains(NumberTuple aNumberTupel){
    return m_relation.evaluate(aNumberTupel.getEntriesAsNumberRef());
  }

  /**
   * Returns true, if the given number tuple is contained in the set.
   * @throws IllegalArgumentException if the relation specifying the set has
   * more variables than the
   * {@link net.mumie.mathletfactory.math.algebra.linalg.NumberTuple#getColumnCount() size}
   * of the argument.
   * @see net.mumie.mathletfactory.math.set.NumberSetIF#contains(double)
   */
   public boolean contains(double[] numbers){
     MNumber[] mmNumbers = new MNumber[numbers.length];
     for(int i=0;i<numbers.length;i++)
       mmNumbers[i] = NumberFactory.newInstance(getNumberClass(), numbers[i]);
     return m_relation.evaluate(mmNumbers);
   }

	/**
	 * This class catches
	 * {@link net.mumie.mathletfactory.mmobject.PropertyHandlerIF#RELATION}
	 * property changes.
	 * @see java.beans.PropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(RELATION)) {
			setRelation(new MMRelation((Relation) e.getNewValue()));
      //System.out.println("changed to "+getRelation());
			ActionManager.performActionCycleFromObject(this);
		}
	}

	public void setEditable(boolean editable) {
    if(editable == isEditable())
      return;
		((MMRelation) getRelation()).setEditable(editable);
		super.setEditable(editable);
    render();
	}

  public Class getNumberClass(){
    return m_relation.getNumberClass();
  }

	public int getDefaultTransformTypeAsContainer() {
		return GeneralTransformer.SET_TRANSFORM;
	}

	public Rectangle2D getWorldBoundingBox() {
		return null;
	}
}
