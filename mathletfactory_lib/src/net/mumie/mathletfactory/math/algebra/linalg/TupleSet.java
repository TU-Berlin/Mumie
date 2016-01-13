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

package net.mumie.mathletfactory.math.algebra.linalg;

import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.math.number.MRational;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.NumberTupelSetIF;
import net.mumie.mathletfactory.math.util.MatrixUtils;
import net.mumie.mathletfactory.util.xml.ExerciseObjectFactory;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.MathMLSerializable;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class represents a number tuple set. A TupleSet-object is defined by the
 * the sum of vectors (NumberTuple of MOpNumber) muliplied by scalars (MOpNumber) 
 * and the definition of the variables (MOpNumber).
 * 
 * e.g.   /   /1\   /b\ |         \
 * 		M = | a*|0| + |2| | a,b in C|
 * 		    \   \1/   \1/ |         / 
 *
 * @author heimann
 * @mm.docstatus finished
 */
public class TupleSet implements Cloneable, MathMLSerializable, ExerciseObjectIF, NumberTypeDependentIF, NumberTupelSetIF {

	private int m_numOfComponents;
	private int m_numOfVariables;
	private Class m_numberClass,m_opnumberClass;
	private int m_dimension;
	private NumberTuple[] m_vectors;
	private MOpNumber[] m_factors;
	private MOpNumber[] m_variables;
	private boolean m_useFactors;
	private boolean m_useRightVariables;
	private boolean m_newElementsEdited;
	private boolean m_hidden;
	private String m_label;
	private boolean m_eAsVariable;
	
	/**
	 * creates a new TupleSet
	 * @param numberClass: number class internally used for the MOpNumbers
	 * @param components: 	number of the left-hand side components (vectors, factors)
	 * @param variables:	number of the right-hand side components (variables) 
	 * @param dimension:	dimension of the vectors used in the tuple set 
	 */
	public TupleSet(Class numberClass, int components, int variables, int dimension){
		m_numberClass=numberClass;
		m_opnumberClass=MOpNumber.getOpClass(numberClass);
		m_numOfVariables=variables;
		m_numOfComponents=components;
		m_dimension=dimension;
		init();
	}
	
	/**
	 * creates a new TupleSet 
	 * @param numberClass: number class internally used for the MOpNumbers 
	 * @param dimension:	dimension of the vectors used in the tuple set 
	 */
	public TupleSet(Class numberClass, int dimension){
		this(numberClass,0,0,dimension);
	}
	
	/**
	 * Constructs a new instance with the given MathML/XML content.
	 * @param mathMLnode a MathML/XML node
	 * @throws Exception when an error while instantiation occurrs
	 */
	public TupleSet(Node content) throws Exception {
		this(ExerciseObjectFactory.getNumberClass(content),0,0,0);
		setMathMLNode(content);
  }
	
	private void init(){
		//default values
		m_useFactors=true;
		m_useRightVariables=true;
		m_newElementsEdited=true;
		m_hidden = false;
		m_label = null;
		m_eAsVariable = false;
		//initialize components
		m_vectors = new NumberTuple[m_numOfComponents];
		m_factors = new MOpNumber[m_numOfComponents];
		m_variables = new MOpNumber[m_numOfVariables];
		for(int i=0; i<m_numOfComponents;i++){
			m_vectors[i] = getNewOpNumberTuple();
			m_factors[i] = getNewOpNumber();
		}
		for(int i=0; i<m_numOfVariables;i++){
			m_variables[i] = getNewOpNumber();
		}
	}
	
	/**
	 * if true, factors (scalars) for left-hand side components are enabled 
	 */
	public void setFactorsEnabled(boolean aFlag){
		if(m_useFactors==aFlag)return;
		m_useFactors=aFlag;
		if(!m_useFactors){
			m_factors=new MOpNumber[0];
		}else{
			m_factors=new MOpNumber[m_numOfComponents];
			for(int i=0;i<m_numOfComponents;i++){
				m_factors[i]=getNewOpNumber();
				m_factors[i].setEdited(m_newElementsEdited);
			}
		}
	}
	
	/**
	 * @return true, if the edited flag of new added components is set to true,
	 * 			false otherwise  
	 */
	public boolean isNewElementsEdited(){
		return m_newElementsEdited;
	}
	
	/**
	 * if true, the edited flag of new added components is set to true 
	 */
	public void setNewElementsEdited(boolean aFlag){
		m_newElementsEdited=aFlag;
	}
	
	/**
	 * @return true, if factors (scalars) for left-hand side components are enabled 
	 */
	public boolean isFactorsEnabled(){
		return m_useFactors;
	}
	
	/**
	 * if true, right-hand side components (variables) are enabled 
	 */
	public void setRightVariablesEnabled(boolean flag){
		if(m_useRightVariables==flag)return;
		m_useRightVariables = flag;
		if(!m_useRightVariables){
			m_variables=new MOpNumber[0];
			m_numOfVariables=0;
		}
	}
	
	/**
	 * @return true, if right-hand side components (variables) are enabled 
	 */
	public boolean isRightVariablesEnabled(){
		return m_useRightVariables;
	}
	
	/**
	 * @return number of left-hand side components 
	 */
	public int getNumberOfComponents(){
		return m_numOfComponents;
	}
	
	/**
	 * @return dimension of the vectors used in the tuple set 
	 */
	public int getDimension(){
		return m_dimension;
	}
	
	/**
	 * @return number of right-hand side components (variables) 
	 */
	public int getNumberOfVariables(){
		return m_numOfVariables;
	}
	
	/**
	 * sets the dimension for the vectors used in the tuple set 
	 */
	public void setDimension(int dimension){
		if(dimension==m_dimension)return;
		m_dimension=dimension;
		for(int i=0;i<m_numOfComponents;i++)
			if(m_vectors[i].getRowCount()!=m_dimension)m_vectors[i].resize(dimension, 1);
	}
	
	/**
	 * sets the number of left-hand side components 
	 */
	public void setNumberOfComponents(int components){
		if(components<0)return;
		while (components != m_numOfComponents) {
			if(components > m_numOfComponents) addLeftComponent();
			else removeLeftComponent();
		}
	}
	
	/**
	 * sets the number of right-hand side components (variables) 
	 */
	public void setNumberOfVariables(int variables){
		if(variables<0||!m_useRightVariables)return;
		while (variables!=m_numOfVariables) {
			if(variables>m_numOfVariables) addRightComponent();
			else removeRightComponent();
		}
	}
	
	/**
	 * adds one left-hand side component of the tuple set 
	 */
	public void addLeftComponent(){
		NumberTuple[] temp1 = (NumberTuple[])m_vectors.clone();
		MOpNumber[] temp2 = (MOpNumber[])m_factors.clone();
		m_numOfComponents++;
		m_vectors = new NumberTuple[m_numOfComponents];
		if(m_useFactors)m_factors = new MOpNumber[m_numOfComponents];
		for(int i=0;i<m_numOfComponents-1;i++){
			m_vectors[i]=(NumberTuple)temp1[i].deepCopy();
			for(int j=1; j<=m_dimension;j++)m_vectors[i].setEdited(j,temp1[i].isEdited(j));
			if(m_useFactors)m_factors[i]=(MOpNumber)temp2[i].copy();
			if(m_useFactors)m_factors[i].setEdited(temp2[i].isEdited());
		}
		m_vectors[m_numOfComponents-1]=getNewOpNumberTuple();
		if(m_useFactors)m_factors[m_numOfComponents-1]=getNewOpNumber();
		m_vectors[m_numOfComponents-1].setEdited(m_newElementsEdited);
		if(m_useFactors)m_factors[m_numOfComponents-1].setEdited(m_newElementsEdited);
	}
	
	/**
	 * removes the last added left-hand side component of the tuple set 
	 */
	public void removeLeftComponent(){
		if(m_numOfComponents<1)return;
		m_numOfComponents--;
		NumberTuple[] temp1 = (NumberTuple[])m_vectors.clone();
		MOpNumber[] temp2 = (MOpNumber[])m_factors.clone();
		m_vectors = new NumberTuple[m_numOfComponents];
		if(m_useFactors)m_factors = new MOpNumber[m_numOfComponents];
		for(int i=0;i<m_numOfComponents;i++){
			m_vectors[i]=(NumberTuple)temp1[i].deepCopy();
			for(int j=1; j<=m_dimension;j++)m_vectors[i].setEdited(j,temp1[i].isEdited(j));
			if(m_useFactors)m_factors[i]=(MOpNumber)temp2[i].copy();
			if(m_useFactors)m_factors[i].setEdited(temp2[i].isEdited());
		}
	}

	/**
	 * adds one right-hand side component to the tuple set 
	 */
	public void addRightComponent(){
		if(!m_useRightVariables)return;
		MOpNumber[] temp = (MOpNumber[])m_variables.clone();
		m_numOfVariables++;
		m_variables = new MOpNumber[m_numOfVariables];
		for(int i=0;i<m_numOfVariables-1;i++){
			m_variables[i]=(MOpNumber)temp[i].copy();
			m_variables[i].setEdited(temp[i].isEdited());
		}
		m_variables[m_numOfVariables-1]=getNewOpNumber();
		m_variables[m_numOfVariables-1].setEdited(m_newElementsEdited);
	}
	
	/**
	 * removes the last added right-hand side component of the tuple set 
	 */
	public void removeRightComponent(){
		if(m_numOfVariables<1||!m_useRightVariables)return;
		m_numOfVariables--;
		MOpNumber[] temp = (MOpNumber[])m_variables.clone();
		m_variables = new MOpNumber[m_numOfVariables];
		for(int i=0;i<m_numOfVariables;i++){
			m_variables[i]=(MOpNumber)temp[i].copy();
			m_variables[i].setEdited(temp[i].isEdited());
		}
	}
	
	private NumberTuple getNewOpNumberTuple(){
		NumberTuple result = new NumberTuple(m_opnumberClass, m_dimension);
		result.setUsedVariables(true);
		return result;
	}
	
	private MOpNumber getNewOpNumber(){
		MOpNumber result = new MOpNumber(m_numberClass);
		result.setUsedVariables(true);
		return result;
	}
	
	/**
	 * 
	 * @return the left-hand side expression of the tuple set as one NumberTuple 
	 */
	public NumberTuple getAsOneVector(){
		if(m_numOfComponents==0)return new NumberTuple(m_opnumberClass,0);
		NumberTuple result = new NumberTuple(m_opnumberClass,m_dimension);
		if(!isCompletelyEdited()){
			return null;
		}else{
			for(int i=0;i<m_numOfComponents;i++){
				if(isFactorsEnabled()){
					if(i==0)result=((NumberTuple)m_vectors[i].deepCopy()).multiplyWithScalar(m_factors[i]);
					else result.addTo(((NumberTuple)m_vectors[i].deepCopy()).multiplyWithScalar(m_factors[i]));
				}else{
					if(i==0)result=((NumberTuple)m_vectors[i].deepCopy());
					else result.addTo(((NumberTuple)m_vectors[i].deepCopy()));
				}
			}
			result.setNormalForm(true);
			return result;
		}
	}	
	
	/**
	 * 
	 * @return true, if all MOpNumbers in the variables array are in fact variables
	 * 			(e.g. 'a' and 'a2' are valid entries, '2*a' and 'a*b' are not) 
	 */
	public boolean rightVariablesValid(){
		if(!m_useRightVariables)return true;
		for(int i=0;i<getRightVariables().length;i++){
			boolean case1=getRightVariables()[i].getOperation().getUsedVariables().length!=1||!getRightVariables()[i].getOperation().isOnlyAVariable();
			boolean case2=isEAsVariable()&&checkForE(getRightVariables()[i].getOperation());
			boolean unique=true;
			for(int j=0;j<i;j++)unique=unique&&!getRightVariables()[i].equals(getRightVariables()[j]);
			if((case1&&!case2)||(!case1&&case2)||!unique)return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return true, if all MOpNumbers in the variables array are in fact variables and the
	 * 			variables used in the left and the right part of the tuple set are the same
	 */
	public boolean variablesValid(){
		if(!rightVariablesValid())return false;
		String[] right = getRightVariablesAsStringArray();
		String[] left = getLeftVariables();
		if(right.length!=left.length)return false;
		if(left.length!=0&&!m_useRightVariables)return false;
		for(int i=0;i<left.length;i++){
			boolean b=false;
			for(int j=0;j<right.length;j++)
				if(left[i].compareTo(right[j])==0){
					b=true;
					break;
				}
			if(!b)return false;
		}
		return true;
	}
	
	//true, if 'e' or 'E' is used in the operation op
	private boolean checkForE(Operation op){
		String[]temp=op.getUsedConstants();
		if(temp.length!=1)return false;
		if(temp[0].compareToIgnoreCase("e")==0&&op.getUsedVariables().length==0)return true;			
		else return false;
	}
	
	/**
	 * 
	 * @return the variables of the right part of the tuple set as String array, if valid
	 */
	public String[] getRightVariablesAsStringArray(){
		if(!rightVariablesValid())return new String[0];
		String[] result;
		if(getRightVariables()!=null)
			result=new String[getRightVariables().length];
		else return new String[0];
		for(int i=0;i<getRightVariables().length;i++)
			result[i]=getRightVariables()[i].toString();
		return result;
	}
	
	/**
	 * 
	 * @return the used variables of the left part of the tuple set as String array
	 */
	public String[] getLeftVariables(){
		String[] result=new String[0];
		MOpNumber temp=new MOpNumber(m_numberClass);
		temp.setUsedVariables(true);
		for(int i=0;i<getNumberOfComponents();i++){
			if(isFactorsEnabled()&&getFactors()[i].isEdited()){
				temp.add(getFactors()[i]);
			}
			if(getVectors()[i].isEdited())
				for(int j=0;j<getDimension();j++){
					if(getVectors()[i].getEntryRef(j+1).isEdited())
						temp.add(getVectors()[i].getEntryRef(j+1));
			}
		}
		result=temp.getOperation().getUsedVariables();
		String[] tempConstants=temp.getOperation().getUsedConstants();
		if(isEAsVariable())for(int i=0;i<tempConstants.length;i++)
			if(tempConstants[i].compareToIgnoreCase("e")==0){
				result=connectStringArray(result, new String[]{"e"});
				break;
			}
		return result;
	}
	
	//connects to String arrays
	private String[] connectStringArray(String list1[], String list2[]){
		String result[] = null;
		if((list1 == null) && (list2 == null))return result;
		else if((list1 == null) && (list2.length >= 0))result = list2;
		else if((list1.length >= 0) && (list2 == null))result = list1;
		else if((list1.length >= 0) && (list2.length >= 0)){
			result = new String[list1.length + list2.length];
			System.arraycopy(list1, 0, result, 0, list1.length);
			System.arraycopy(list2, 0, result, list1.length, list2.length);
		}
		return result;
	}
	
	/**
	 * @return basis vectors as an array of NumberTuple of the tuple set, if vector space,
	 * 			null otherwise
	 */
	public NumberTuple[] getBasisVectors(){
		return getBasisVectors(getAsOneVector(),true);
	}

	/**
	 * @return basis vectors as an array of NumberTuple of the tuple set, if vector space,
	 * 			null otherwise 
	 * @param	checkVariables
	 */
	public NumberTuple[] getBasisVectors(boolean checkVariables){
		return getBasisVectors(getAsOneVector(),checkVariables);
	}
	
	private NumberTuple[] getBasisVectors(NumberTuple vecfac, boolean checkVariables){
		if(!m_numberClass.isAssignableFrom(MRational.class)&&!m_numberClass.isAssignableFrom(MDouble.class)&&
				!m_numberClass.isAssignableFrom(MComplex.class)&&!m_numberClass.isAssignableFrom(MComplexRational.class)) {
			throw new IllegalArgumentException("Can only return the basis of a vector space defined by a TupleSet, if numberclass" +
					" is MDouble, MRational, MComplex or MComplexRational!");
		}
		//vollstaendig editiert? Variablen valide?
		if(!isCompletelyEdited()||(checkVariables&&!variablesValid()))return null;
		//leere Menge?
		if(m_numOfComponents==0)return new NumberTuple[0];
		String[] vars = getLeftVariables();
		if(vars.length>0||vecfac.isZero()){
			int valuesLength=(int)Math.pow(2,vars.length);
			//Generierung von 0/1-Kombinationen fuer die Variablenbelegung
			int[][] values = new int[valuesLength][vars.length];
			for(int i=0;i<vars.length;i++){
				int c=1;
				for(int j=0;j<valuesLength;j++){
					if(c>=Math.pow(2,i)){
						values[j][i]=1;
						if(c==2*Math.pow(2,i)-1){
							c=0;
						}else c++;
					}else{
						values[j][i]=0;
						c++;
					}
				}
			}
			NumberTuple[] result = new NumberTuple[valuesLength];
			for(int i=0;i<valuesLength;i++){
				result[i] = new NumberTuple(m_numberClass,getDimension());
				MOpNumber temp2 = new MOpNumber(m_numberClass);
				temp2.setUsedVariables(true);
				for(int k=1;k<=getDimension();k++){
					temp2.set(vecfac.getEntryRef(k));
					if(isEAsVariable())temp2.getOperation().setVariable("e");
					for(int j=0;j<vars.length;j++)
						temp2.getOperation().assignValue(vars[j],values[i][j]);
					result[i].setEntry(k,temp2.getOperation().getResult());	
				}
			}
			//pruefe ob Nullvektor bei einer Nullbelegung aller Variablen
			if(!result[valuesLength-1].isZero())return null;
			NumberMatrix m = new NumberMatrix(m_numberClass,true,result);
			m = MatrixUtils.getNormalizedEchelonForm(m);
			if(m.isZero())return new NumberTuple[]{m.getRowVector(1)};
			int rank=m.rank();
			//generieren der zurueckgegebenen Loesung
			NumberTuple[] basisResult = new NumberTuple[rank];
			for(int i=0;i<rank;i++)
				basisResult[i]=m.getRowVector(i+1);
			return basisResult;
		}else return null;
	}
	
	/**
	 * @return basis vectors of the tuple set as row matrix, if vector space 
	 * 			null otherwise
	 */
	public NumberMatrix getBasisVectorsAsRowMatrix(){
		return getBasisVectorsAsRowMatrix(getAsOneVector(),true);
	}
	
	/**
	 * @return basis vectors of the tuple set as row matrix, if vector space 
	 * 			null otherwise
	 * @param	checkVariables
	 */
	public NumberMatrix getBasisVectorsAsRowMatrix(boolean checkVariables){
		return getBasisVectorsAsRowMatrix(getAsOneVector(),checkVariables);
	}
	
	private NumberMatrix getBasisVectorsAsRowMatrix(NumberTuple vecfac, boolean checkVariables){
		NumberTuple[] temp = getBasisVectors(vecfac,checkVariables);
		if(temp==null)return null;
		else if(temp.length==0) return new NumberMatrix(m_numberClass,0);
			else return new NumberMatrix(m_numberClass,true,temp);
	}
	
	/**
	 * @return vector tuple as array of NumberTuple
	 */
	public NumberTuple[] getVectors(){
		return m_vectors;
	}

	/**
	 * @return factors of the vector tuple if enabled as array of MOpNumber,
	 * 			null otherwise
	 */
	public MOpNumber[] getFactors(){
		if(isFactorsEnabled())return m_factors;
		else return null;
	}
	
	/**
	 * @return right tuple set entries as array of MOpNumber
	 */
	public MOpNumber[] getRightVariables(){
		if(isRightVariablesEnabled())return m_variables;
		else return null;
	}
	
	/**
	 * sets the entries of the tuple set
	 * remarks: if factors and/or right-hand side variables are disabled the parameter factors 
	 * 			and/or variables have to be null
	 * 			the number class (internal number class of the MOpNumber) of the commited parameters
	 * 			has to be valid
	 * @param vectors
	 * @param factors
	 * @param variables
	 */
	public void setEntries(NumberTuple[] vectors, MOpNumber[] factors, MOpNumber[] variables){
		if(!isFactorsEnabled()&&factors!=null)throw new IllegalArgumentException("factors should be null");
		if(isFactorsEnabled()&&factors==null)throw new IllegalArgumentException("factors should not be null");
		if(!isRightVariablesEnabled()&&variables!=null)throw new IllegalArgumentException("variables should be null");
		if(isRightVariablesEnabled()&&variables==null)throw new IllegalArgumentException("variables should not be null");
		if(vectors==null)throw new IllegalArgumentException("vectors should not be null");
		if(isFactorsEnabled()&&factors.length!=vectors.length)throw new IllegalArgumentException("the factor and the vector" +
				" array should have the same length");
		//check number class and dimension
		boolean checkNC = true;
		boolean checkDim = true;
		int dim = 0;
		for(int i=0;i<vectors.length;i++){
			if(i==0) dim=vectors[i].getRowCount();
			else checkDim=checkDim&&dim==vectors[i].getRowCount();
			if(isFactorsEnabled())checkNC=checkNC&&factors[i].getNumberClass()==getNumberClass();
			if(!MOpNumber.class.isAssignableFrom(vectors[i].getNumberClass()))checkNC=false;
			for(int j=1;j<=dim;j++){
				checkNC=checkNC&&vectors[i].getEntryRef(j).getNumberClass()==getNumberClass();
			}
		}
		if(isRightVariablesEnabled())
			for(int i=0;i<variables.length;i++)
				checkNC=checkNC&&variables[i].getNumberClass()==getNumberClass();
		if(!checkNC||!checkDim) throw new IllegalArgumentException("number class error");
		
		//set entries
		setDimension(dim);
		setNumberOfComponents(vectors.length);
		if(isRightVariablesEnabled())setNumberOfVariables(variables.length);
		for(int i=0;i<vectors.length;i++){
			for(int j=1;j<=dim;j++){
				m_vectors[i].setEntry(j, vectors[i].getEntryRef(j));
				m_vectors[i].setEdited(j, vectors[i].isEdited(j));
			}
			m_vectors[i].setUsedVariables(true);
			if(isFactorsEnabled()){
				m_factors[i].set(factors[i]);
				m_factors[i].setUsedVariables(true);
				m_factors[i].setEdited(factors[i].isEdited());
			}
		}
		if(isRightVariablesEnabled())
			for(int i=0;i<variables.length;i++){
				m_variables[i].set(variables[i]);
				m_variables[i].setUsedVariables(true);
				m_variables[i].setEdited(variables[i].isEdited());
			}
	}
	
	public void setEdited(boolean edited) {
		for(int i=0; i<m_numOfComponents;i++){
			m_vectors[i].setEdited(edited);
			if(m_useFactors)m_factors[i].setEdited(edited);
		}
		if(m_useRightVariables)for(int i=0; i<m_numOfVariables;i++)
			m_variables[i].setEdited(edited);
	}
	
	public boolean isEdited() {
		boolean edited=false;
		if(isFactorsEnabled())for(int i=0; i<m_numOfComponents;i++){
			edited=edited||m_vectors[i].isEdited()||m_factors[i].isEdited();
		}else for(int i=0; i<m_numOfComponents;i++){
			edited=edited||m_vectors[i].isEdited();
		}
		if(isRightVariablesEnabled())for(int i=0; i<m_numOfVariables;i++)
			edited=edited||m_variables[i].isEdited();
		return edited;
	}
	
	/**
	 * @return true, if all entries in the tuple set are edited
	 */
	public boolean isCompletelyEdited() {
		boolean edited=true;
		if(isFactorsEnabled())for(int i=0; i<m_numOfComponents;i++){
			edited=edited&&m_vectors[i].isEdited()&&m_factors[i].isEdited();
		}else for(int i=0; i<m_numOfComponents;i++){
			edited=edited&&m_vectors[i].isCompletelyEdited();
		}
		if(isRightVariablesEnabled())for(int i=0; i<m_numOfVariables;i++)
			edited=edited&&m_variables[i].isEdited();
		return edited;
	}
	
	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
	
	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}
	
	/**
	 * @return true, if the tuple set defines a vector space with respect to the number class
	 * 			false, if the tuple set doesn't define a vector space, the set isn't completeley
	 * 				   edited or the used variable are not valid
	 */
	public boolean isVectorSpace(){
		return getBasisVectors()!=null;
	}
	
	/**
	 * @return the number class (generally a sub class of MOpNumber)
	 */
	public Class getNumberClass() {
		return m_numberClass;
	}
	
	/**
	 * @param eAsVariable: If true 'e' will be interpreted as a variable in the set entries.
	 * 					   	Otherwise 'e' is the euler number.
	 */
	public void setEAsVariable(boolean eAsVariable){
		m_eAsVariable=eAsVariable;
	}

	/**
	 * @return true, if 'e' is interpreted as a variable in the set entries
	 * 			and false otherwise.
	 */
	public boolean isEAsVariable(){
		return m_eAsVariable;
	}
	
	public boolean contains(double[] vector) {
		if(vector.length!=m_dimension)return false;
		else{
			NumberTuple ant=new NumberTuple(getNumberClass(),m_dimension);
			for(int i=1;i<=m_dimension;i++){
				ant.setEntry(i, new MOpNumber(m_numberClass));
				ant.setEntry(i, vector[i-1]);
			}
			return contains(ant);
		}
		
	}
	
	public boolean contains(NumberTuple aNumberTupel) {
		
		//case -1 (falsche dimension oder number class oder nicht vollstaendig editiert oder Fehler bei den Variablen in der Menge
		//		   oder leere Menge)
		if(aNumberTupel.getRowCount()!=m_dimension||(MOpNumber.class.isAssignableFrom(aNumberTupel.getNumberClass())&&
				((MOpNumber)(aNumberTupel.getEntryRef(1))).getOperation().getNumberClass()!=m_numberClass)||
				aNumberTupel.getNumberClass()!=m_numberClass||
				!aNumberTupel.isCompletelyEdited()||!isCompletelyEdited()||!variablesValid())return false;
		NumberTuple nt = getAsOneVector();
		//leere Menge
		if(nt.getRowCount()==0)return false;
		
		//case 0 (nt nicht vollstaendig editiert)
		if(nt==null)return false;
		
		//aufbereiten des NumberTuple
		NumberTuple ant,ant_no_operation;
		if(aNumberTupel.getNumberClass()==MOpNumber.class){
			ant=aNumberTupel;
			ant_no_operation=new NumberTuple(m_numberClass,m_dimension);
			for(int i=1;i<=m_dimension;i++){
				ant_no_operation.setEntry(i, ((MOpNumber)aNumberTupel.getEntryRef(i)).getOperation().getResult());
			}
		}else{
			ant_no_operation = aNumberTupel;
			ant=new NumberTuple(MOpNumber.class,m_dimension);
			for(int i=1;i<=m_dimension;i++){
				ant.setEntry(i, new MOpNumber(aNumberTupel.getEntryRef(i)));
			}
		}
		
		//case 1 (keine variablen mit einfluss auf die menge)
		boolean ntContainsVariables=false;
		for(int i=1;i<=m_dimension;i++){
			if(isEAsVariable())((MOpNumber)nt.getEntryRef(i)).getOperation().setVariable("e");
			
			ntContainsVariables=ntContainsVariables||
				((MOpNumber)nt.getEntryRef(i)).getOperation().getUsedVariables().length!=0;
		}
		if(!ntContainsVariables)return ant.equals(nt);
		//case 2 (variablen mit einfluss, TupleSet ist VR)
		else if(isVectorSpace()){
			NumberMatrix nm = getBasisVectorsAsRowMatrix(nt,true);
			NumberMatrix nm_ant = nm.deepCopy();
			nm_ant.resize(nm_ant.getRowCount()+1, nm_ant.getColumnCount());
			nm_ant.setRowVector(nm_ant.getRowCount(), ant_no_operation);
			return nm.rank()==nm_ant.rank();
		//case 3 (variablen mit einfluss, TupleSet ist kein VR)
		}else{
			NumberTuple ntwzv =(NumberTuple)nt.deepCopy();
			NumberTuple nt_no_vars =(NumberTuple)nt.deepCopy();
			for(int i=1;i<=m_dimension;i++){
				if(isEAsVariable()){
					((MOpNumber)nt.getEntryRef(i)).getOperation().setVariable("e");
					((MOpNumber)nt.getEntryRef(i)).getOperation().assignValue("e", 0);
				}
				ntwzv.setEntry(i, ntwzv.getEntryRef(i).sub(((MOpNumber)ntwzv.getEntryRef(i)).getOperation().getResult()));
				String[] tmp = ((MOpNumber)nt_no_vars.getEntryRef(i)).getOperation().getUsedVariables();
				for(int j=0;j<tmp.length;j++){
					((MOpNumber)nt_no_vars.getEntryRef(i)).setOperation(((MOpNumber)nt_no_vars.getEntryRef(i)).getOperation().getComposed(tmp[j], new Operation(m_numberClass,"0",true)));
				}
			}
			NumberMatrix nm = getBasisVectorsAsRowMatrix(ntwzv,true);
			NumberMatrix nm_ant = nm.deepCopy();
			nm_ant.resize(nm_ant.getRowCount()+1, nm_ant.getColumnCount());
			NumberTuple ant_no_operation_reduced = (NumberTuple)ant_no_operation.deepCopy();
			for(int i=1;i<=m_dimension;i++)
				ant_no_operation_reduced.setEntry(i, ant_no_operation_reduced.getEntryRef(i).sub(((MOpNumber)nt.getEntryRef(i)).getOperation().getResult()));
			if(ant_no_operation.isZero())nm_ant.setRowVector(nm_ant.getRowCount(), nt_no_vars);
			else nm_ant.setRowVector(nm_ant.getRowCount(), ant_no_operation_reduced);
//			return nm.rank()==nm_ant.rank();
			return MatrixUtils.getNormalizedEchelonForm(nm).rank()==MatrixUtils.getNormalizedEchelonForm(nm_ant).rank();
		}
	}
	
	//provisorisch
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("\n________________________________\n");
		buffer.append("vectors:\n");
		for (int i = 0; i < m_numOfComponents; i++) {
			buffer.append("  ");
			buffer.append(m_vectors[i].toString());
			buffer.append("\n");
		}
		if (isFactorsEnabled()) {
			buffer.append("\nfactors:\n");
			for (int i = 0; i < m_numOfComponents; i++) {
				buffer.append("  ");
				buffer.append(m_factors[i].toString());
				buffer.append("\n");
			}
		}
		if (isRightVariablesEnabled()) {
			buffer.append("\nright variables:\n");
			for (int i = 0; i < m_numOfVariables; i++) {
				buffer.append("  ");
				buffer.append(m_variables[i].toString());
				buffer.append("\n");
			}
		}
		buffer.append("\nnumber class: " + m_numberClass.toString());
		buffer.append("\n________________________________\n");
		return buffer.toString();
	}
	
	public Node getMathMLNode() {
		return getMathMLNode(XMLUtils.getDefaultDocument());
	}

	public Node getMathMLNode(Document doc) {
		String inLabel;
		if(m_numberClass==MComplex.class||m_numberClass==MComplexRational.class)
			inLabel="\u2208 \u2102";
		else if(m_numberClass==MDouble.class||m_numberClass==MRational.class)
			inLabel="\u2208 \u211D";
		else if(m_numberClass==MInteger.class)inLabel="\u2208 \u2124";
		else inLabel="\u2208 K";
		Element mrow = ExerciseObjectFactory.createNode((ExerciseObjectIF) this, "mrow", doc);
		mrow.setAttribute("xmlns", XMLUtils.MATHML_NAMESPACE);
		mrow.setAttribute("class", "tuple-set");
		Element mo1 = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
		Element mo2 = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
		Element mo3 = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
		Element mo4 = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
		Element mo5 = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
		Element mo6 = doc.createElementNS(XMLUtils.MATHML_NAMESPACE, "mo");
		mo1.appendChild(doc.createTextNode("{"));
		mo2.appendChild(doc.createTextNode("+"));
		mo3.appendChild(doc.createTextNode("|"));
		mo4.appendChild(doc.createTextNode(","));
		mo5.appendChild(doc.createTextNode(inLabel));
		mo6.appendChild(doc.createTextNode("}"));
		mrow.appendChild(mo1);
	  for(int i=0;i<getNumberOfComponents();i++){
	  	if(i>0)mrow.appendChild(mo2);
	   	if(isFactorsEnabled())mrow.appendChild(getFactors()[i].getMathMLNode(doc));
	   	mrow.appendChild(getVectors()[i].getMathMLNode(doc));
	  }
	  if(isRightVariablesEnabled()){
	   	if(getNumberOfVariables()>0)mrow.appendChild(mo3);	
	   	for(int i=0;i<getNumberOfVariables();i++){
		   	if(i>0)mrow.appendChild(mo4);
		   	mrow.appendChild(getRightVariables()[i].getMathMLNode(doc));
		  }
	   	if(getNumberOfVariables()>0)mrow.appendChild(mo5);
	  }
	  mrow.appendChild(mo6);
	  return mrow;
	}

	public void setMathMLNode(Node content) {
		if(!content.getNodeName().equalsIgnoreCase("mrow"))
		      throw new XMLParsingException("Node name must be \"<mrow>\"!", content);
		int index = 0;
		for(int i=0;i<content.getChildNodes().getLength();i++)
			if(XMLUtils.getNextNonTextNodeIndex(content, i)==i){
				index++;
			}
		Node[] nodes = new Node[index];
		int tmp=0;
		for(int i=0;i<content.getChildNodes().getLength();){
			if(XMLUtils.getNextNonTextNodeIndex(content, i)==-1)break;
			nodes[tmp]=XMLUtils.getNextNonTextNode(content, i);
			i=XMLUtils.getNextNonTextNodeIndex(content, i)+1;
			tmp++;
		}
		if(!nodes[0].getFirstChild().getNodeValue().equals("{")||
				!nodes[index-1].getFirstChild().getNodeValue().equals("}"))
		      throw new XMLParsingException("no tuple set", content);
		setNumberOfComponents(0);
		setNumberOfVariables(0);
		setRightVariablesEnabled(false);
		setFactorsEnabled(false);
		m_dimension=0;
		boolean leftSideDone=false;
		for(int i=1;i<content.getChildNodes().getLength()-1;i++){
			if(!leftSideDone&&content.getChildNodes().item(i).getNodeName().equalsIgnoreCase("mo")&&
					content.getChildNodes().item(i).getFirstChild().getNodeValue().equals("|")){
				leftSideDone=true;
				continue;
			}
			if(!leftSideDone){
				if(content.getChildNodes().item(i).getNodeName().equalsIgnoreCase("mi")||
						(content.getChildNodes().item(i).getNodeName().equalsIgnoreCase("mrow"))
						&&content.getChildNodes().item(i).getAttributes().getNamedItem("mf:type")!=null&&
						content.getChildNodes().item(i).getAttributes().getNamedItem("mf:type").
							getFirstChild().getNodeValue().equalsIgnoreCase("op-number")){
					if(!isFactorsEnabled())setFactorsEnabled(true);
					addLeftComponent();
					m_factors[m_numOfComponents-1].setMathMLNode(content.getChildNodes().item(i));
					m_factors[m_numOfComponents-1].setUsedVariables(true);
				}else if(content.getChildNodes().item(i).getNodeName().equalsIgnoreCase("mrow")&&
						content.getChildNodes().item(i).hasAttributes()&&
						content.getChildNodes().item(i).getAttributes().getNamedItem("class")!=null&&
						content.getChildNodes().item(i).getAttributes().getNamedItem("class").
							getFirstChild().getNodeValue().equalsIgnoreCase("column-vector")){
					if(!isFactorsEnabled())addLeftComponent();
					m_vectors[m_numOfComponents-1].setMathMLNode(content.getChildNodes().item(i));
					m_vectors[m_numOfComponents-1].setUsedVariables(true);
					if(m_dimension!=m_vectors[m_numOfComponents-1].getRowCount())setDimension(m_vectors[m_numOfComponents-1].getRowCount());
				}
			}else{
				if(content.getChildNodes().item(i).getNodeName().equalsIgnoreCase("mrow")||
						content.getChildNodes().item(i).getNodeName().equalsIgnoreCase("mi")){
					if(!isRightVariablesEnabled())setRightVariablesEnabled(true);
					addRightComponent();
					m_variables[m_numOfVariables-1].setMathMLNode(content.getChildNodes().item(i));
					m_variables[m_numOfVariables-1].setUsedVariables(true);
				}
			}
		}
		ExerciseObjectFactory.importExerciseAttributes(content, (ExerciseObjectIF) this);
		
	}

}
