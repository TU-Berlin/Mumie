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

package net.mumie.mathletfactory.math.number;

import java.util.logging.Logger;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.action.message.XMLParsingException;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class that is responsible for the creation of numbers for a given number class.
 *
 * @author Vossbeck, gronau
 * @mm.docstatus finished
 */
public class NumberFactory {

  private static Logger logger = Logger.getLogger(NumberFactory.class.getName());


  /** Creates a new MNumber instance of the given number class. */
  public static MNumber newInstance(Class aClass) {
    if (MNumber.class.isAssignableFrom(aClass))
      return createInstance(aClass);

    throw new IllegalArgumentException("The class \"" + aClass + "\" is not assignable to a number class");

  }
  
  /**
   * Creates a new MNumber instance of the given number class and tries to set the most appropriate 
   * value from the given parameter <code>value</code>. Note that imaginary parts may be truncated.
   * This method has the advantage to not produce any errors if the two number classes are not 
   * compatible (as {@link MNumber#set(MNumber)} does).
   */
  public static MNumber newInstance(Class numberClass, MNumber value) {
    if(value == null)
      throw new NullPointerException("Value must not be null");
    MNumber target = NumberFactory.newInstance(numberClass);
    boolean valueIsReal = value instanceof MRealNumber;
    boolean targetIsReal = target instanceof MRealNumber;
    // check if both numbers have compatible classes
    if((valueIsReal && targetIsReal) || (!valueIsReal && !targetIsReal))
      target.set(value);
    else if(!valueIsReal) { // only value is complex
      if(value instanceof MComplex)
        target.setDouble(((MComplex) value).getRe());
      else if(value instanceof MComplexRational)
        target.setDouble(((MComplexRational) value).getRe().getDouble());
      else
        throw new UnsupportedOperationException("Value is an unknown complex number:" + value);
    } else // only value is real
      target.set(value);
    return target;
  }

  /** Creates a new MNumber instance of the given number class and value. */
  public static MNumber newInstance(Class aClass, double value) {
    if (MNumber.class.isAssignableFrom(aClass)){
      MNumber instance = createInstance(aClass);
      instance.setDouble(value);
      return instance;
    }

    throw new IllegalArgumentException("The class \"" + aClass + "\" is not assignable to a number class");

  }

  /** Creates <code>count</code> new MNumber instances of the given number class. */
  public static MNumber[] newInstances(Class aClass, int count) {
    if (MNumber.class.isAssignableFrom(aClass)) {
      MNumber[] array = new MNumber[count];
      for(int i=0; i<count; i++)
        array[i] = createInstance(aClass);
      return array;
    }

    throw new IllegalArgumentException("The class \"" + aClass + "\" is not assignable to a number class");

  }

  private static MNumber createInstance(Class aClass) {
    try{
    	if(MOpDouble.class.isAssignableFrom(aClass)) 
    		return new MOpNumber(MDouble.class);
    	if(MOpRational.class.isAssignableFrom(aClass))
    		return new MOpNumber(MRational.class);
    	if(MOpComplex.class.isAssignableFrom(aClass))
    		return new MOpNumber(MComplex.class);
    	if(MOpComplexRational.class.isAssignableFrom(aClass))
    		return new MOpNumber(MComplexRational.class);
    	if(MOpInteger.class.isAssignableFrom(aClass))
    		return new MOpNumber(MInteger.class);
    	return (MNumber)aClass.newInstance();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    }
    throw new IllegalArgumentException("The class \"" + aClass + "\" could not be instantiated");
  }

  /**
   * Stores the content of a MathML node into the given number.
   */
  public static void setMathMLNode(Node node, MNumber result) {
    if(result == null)
      throw new NullPointerException("Resulting number must not be null!");
    if(node == null)
      throw new NullPointerException("XML node must not be null!");
    MNumber parsedNumber = parseMathMLNumber(result.getClass(), node);
    if(parsedNumber != null) {
      result.set(parsedNumber);
      result.setEdited(parsedNumber.isEdited());
    }
    else
      throw new XMLParsingException(node);
  }

  /**
   * Parses the given MathML-Node and returns the parsed number with the specified number class.
   * Returns null if parsing is successful.
   */
  public static MNumber parseMathMLNumber(Class numberClass, Node node) {
    MNumber n = null;
    if(node.getNodeName().equalsIgnoreCase("mfrac")) {//->rational
      n = createRationalFromNode(node);
    } else if(node.getNodeName().equalsIgnoreCase("mn") 
    		&& node.getChildNodes().getLength() == 0) {//empty number --> "0"
    	n = new MInteger(0);
    } else if(node.getNodeName().equalsIgnoreCase("mn")) {//->int, double, rational
    	try {
        String number = node.getChildNodes().item(0).getNodeValue().trim();
        // emty number field will become zero
        if(number.length() == 0)
          number = "0";
        // parse german comma instead of dot
        if(number.indexOf(",") > -1)
          number = number.replace(',', '.');
	      n = new MDouble(Double.parseDouble(number));
	      if(n.isInteger())
	      	n = new MInteger(n.getDouble());
    	} catch(NumberFormatException nfe) {
    		throw new TodoException("Cannot parse MathML node " + XMLUtils.nodeToString(node, true));
    	}
    } else if(node.getNodeName().equalsIgnoreCase("cnum")) {//->complex
      n = createComplexFromNode(node);
    } else if(node.getNodeName().equalsIgnoreCase("mrow")) {//->op number
    	Operation op = new Operation(numberClass, "", true);
    	op.setMathMLNode(node);
    	return op.getResult();
    } else if(node.getNodeName().equalsIgnoreCase("mi")
    		|| (node.getNodeName().equalsIgnoreCase("mo") && node.getChildNodes().item(0).getNodeValue().trim().equals("."))
    		|| (node.getNodeName().equalsIgnoreCase("mo") && node.getChildNodes().item(0).getNodeValue().trim().equals("?"))) {//->empty number
    	MNumber result = newInstance(numberClass);
    	result.setEdited(false);
    	return result;
    } else {
    	throw new IllegalArgumentException("NumberFactory: No mathing number class found for node \"<" + node.getNodeName()
          + ">\"! The complete node is: " + XMLUtils.nodeToString(node, true));
    }
    if(!n.getClass().isAssignableFrom(numberClass)) {
    	MNumber result = newInstance(numberClass);
    	result.set(n);
    	return result;
    }
    return n;
  }

//  public static MNumber parseMathMLNumber(Node node) {
//    MNumber n = null;
//    if(node.getNodeName().equalsIgnoreCase("mfrac")) {//->rational
//      n = createRationalFromNode(node);
//    } else if(node.getNodeName().equalsIgnoreCase("mn")) {//->int, double, rational
//    	int i;
//    	try {
//        // parse german comma instead of dot
//        String number = node.getChildNodes().item(0).getNodeValue();
//        if(number.indexOf(",") > -1)
//          number = number.replace(',', '.');
//	      n = new MDouble(Double.parseDouble(number));
//	      if(n.isInteger())
//	      	n = new MInteger(n.getDouble());
//    	} catch(NumberFormatException nfe) {
//    		throw new TodoException("Cannot parse MathML node " + XMLUtils.nodeToString(node, true));
//    	}
////    	try {
////	      i = Integer.parseInt(node.getChildNodes().item(0).getNodeValue());
////	      if(d == i) {//->nat, int
//////	        if(i >= 0) {//->nat
//////	          n = createNaturalFromNode(node);
//////	        } else {//->int
////	          n = createIntegerFromNode(node);
//////	        }
////	      } else {//->double,rat
////	        n = createDoubleFromNode(node);
////	      }
////    	} catch(NumberFormatException nfe) {
//////    		n = createOpNumberFromNode(node);
////    	}
//    } else if(node.getNodeName().equalsIgnoreCase("cnum")) {//->complex
//      n = createComplexFromNode(node);
//    } else if(node.getNodeName().equalsIgnoreCase("mrow")) {//->op number
//      n = createOpNumberFromNode(node);
//    } else {
//    	throw new IllegalArgumentException("NumberFactory: No mathing number class found for " + node.getNodeName()
//          + " with content " + XMLUtils.nodeToString(node));
//    }
//    return n;
//  }

//  private static MNatural createNaturalFromNode(Node node) {
//    MNatural n = new MNatural();
//    if(!node.getNodeName().equalsIgnoreCase("mn"))
//      throw new NumberFormatException("Node name must be <mn>");
//    n.setDouble(Integer.parseInt(node.getChildNodes().item(0).getNodeValue()));
//    return n;
//  }

//  private static MInteger createIntegerFromNode(Node node) {
//    MInteger i = new MInteger();
//    if(!node.getNodeName().equalsIgnoreCase("mn"))
//      throw new NumberFormatException("Node name must be <mn>");
////	int index = getNextNonTextNodeIndex(node, 0);
//    i.setDouble(Integer.parseInt(node.getChildNodes().item(0).getNodeValue()));
////    NodeList list = node.getChildNodes();
////    for(int j = 0; j < list.getLength(); j++) {
////      Node n = list.item(j);
////    }
//    return i;
//  }

//  private static MDouble createDoubleFromNode(Node node) {
//    MDouble d = new MDouble();
//    if(!node.getNodeName().equalsIgnoreCase("mn"))
//      throw new NumberFormatException("Node name must be <mn>");
//    d.setDouble(Double.parseDouble(node.getChildNodes().item(0).getNodeValue()));
//    return d;
//  }

//  private static MOpNumber createOpNumberFromNode(Node node) {
//    MOpNumber n = new MOpNumber();
//    if(!node.getNodeName().equalsIgnoreCase("mrow") && !node.getNodeName().equalsIgnoreCase("mn"))
//      throw new NumberFormatException("Node name must be <mrow> or <mn>");
//    n.set(node.getChildNodes().item(0).getNodeValue());
//    return n;
//  }

  private static MNumber createComplexFromNode(Node node) {
    if(!node.getNodeName().equalsIgnoreCase("cnum"))
      throw new NumberFormatException("Node name must be <cnum>");
    MRational re = new MRational(0);
    MRational im = new MRational(0);
    int i = XMLUtils.getNextNonTextNodeIndex(node, 0);
   	setMathMLNode(node.getChildNodes().item(i), re);
    i = XMLUtils.getNextNonTextNodeIndex(node, i+1);
    setMathMLNode(node.getChildNodes().item(i), im);
    MNumber cnum = null;
    if(re.isInteger() && im.isInteger()) {
    	cnum = new MComplex(re.getDouble(), im.getDouble());
    } else {
    	cnum = new MComplexRational(re, im);
    }
    return cnum;
  }

  private static MRational createRationalFromNode(Node node) {
    MRational r = new MRational();
    if(!node.getNodeName().equalsIgnoreCase("mfrac"))
      throw new NumberFormatException("Node name must be <mfrac>");

    boolean isNumerator = true;
    NodeList numberNode = node.getChildNodes();
    MInteger numerator = new MInteger();
    MInteger denominator = new MInteger();
    for(int i = 0; i < numberNode.getLength(); i++) {
      Node n = numberNode.item(i);
      if(n.getNodeType() == Node.TEXT_NODE)
        continue;
      if(isNumerator) {
        numerator.setMathMLNode(n);
        isNumerator = false;
      }
      else
        denominator.setMathMLNode(n);
    }
    r.setNumerator(numerator.getIntValue());
    r.setDenominator(denominator.getIntValue());
    return r;
  }

  /** Creates a new MMObject instance of the given MNumber. */
  public static MMObjectIF getNewMMInstanceFor(MNumber number) {
  	Class numberClass = number.getMMClass();
  	try {
	  	MMObjectIF mmobject = (MMObjectIF)numberClass.newInstance();
	  	((MNumber)mmobject).set(number);
			//System.out.println(mmobject);
	  	return mmobject;
  	}
		catch (InstantiationException e) {e.printStackTrace();}
		catch (IllegalAccessException e) {e.printStackTrace();}
		logger.severe("getMMInstanceFor():returns null mmobject for class "+numberClass.getName());
		return null;

  }
}

