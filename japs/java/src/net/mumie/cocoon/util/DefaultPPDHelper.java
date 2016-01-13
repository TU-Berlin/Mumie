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

package net.mumie.cocoon.util;

import java.util.Map;
import java.util.Random;
import net.mumie.cocoon.notions.XMLAttribute;
import net.mumie.cocoon.notions.XMLElement;
import net.mumie.cocoon.notions.XMLNamespace;
import net.mumie.japs.datasheet.DataSheet;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MRational;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Default implementation of {@link PPDHelper}
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultPPDHelper.java,v 1.9 2008/08/07 12:14:39 rassy Exp $</code>
 */

public class DefaultPPDHelper extends AbstractLogEnabled 
  implements PPDHelper
{
  // --------------------------------------------------------------------------------
  // Static variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The random number generator of this helper.
   */

  protected static Random random = new Random();

  // --------------------------------------------------------------------------------
  // Utilities
  // --------------------------------------------------------------------------------

  /**
   * Returns all PPD elements in <code>document</code>.
   */

  protected Element[] getPPDElements (Document document)
  {
    NodeList ppdNodeList = document.getElementsByTagNameNS(XMLNamespace.URI_PPD, "*");
    Element[] ppdElements = new Element[ppdNodeList.getLength()];
    for (int i = 0; i < ppdNodeList.getLength(); i++)
      ppdElements[i] = (Element)ppdNodeList.item(i);
    return ppdElements;
  }

  /**
   * Replaces <code>oldNode</code> by <code>newNode</code> in the document of
   * <code>oldNode</code>.
   */

  protected void replaceNode (Node oldNode, Node newNode)
  {
    Node parent = oldNode.getParentNode();
    Document document = oldNode.getOwnerDocument();
    parent.replaceChild(document.importNode(newNode, true), oldNode);
  }

  /**
   * <p>
   *   Converts <code>nodeList</code> to a {@link Node Node} and returns
   *   it. <code>document</code> is the {@link Document Document} the new node belogs to.
   * </p>
   * <p>
   *   Currently, the returned node is always a {@link DocumentFragment DocumentFragment}.
   *   In future versions, this may be a simple {@link Node Node} if <code>nodeList</code> has
   *   length 1.
   * </p>
   */

  protected Node nodeListToNode (NodeList nodeList, Document document)
  {
    DocumentFragment documentFragment = document.createDocumentFragment();
    for (int i = 0; i < nodeList.getLength(); i++)
      {
        Node child = document.importNode(nodeList.item(i), true);
        documentFragment.appendChild(child);
      }
    return documentFragment;
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as a string. If the attribute does not exist or is void (i.e., the
   * empty string), the following happens: If <code>defaultValue</code> is not
   * <code>null</code>, returns <code>defaultValue</code>, otherwise, throws a
   * {@link PPDException}. 
   *
   * @throws PPDException if the attribute does not exist or is void and
   * <code>defaultValue</code> is <code>null</code>.
   */

  protected String getAttribValue (Element element,
                                   String attribName,
                                   String defaultValue)
    throws PPDException
  {
    String attribValue = element.getAttribute(attribName);
    if ( attribValue.equals("") )
      {
        if ( defaultValue != null )
          return defaultValue;
        else
          throw new PPDException
            ("Attribute void or missing: " + attribName);
      }
    else
      {
        return attribValue;
      }
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as a string. If the attribute does not exist or is void (i.e., the
   * empty string), throws a {@link PPDException}. 
   *
   * @throws PPDException if the attribute does not exist or is void.
   */

  protected String getAttribValue (Element element,
                                   String attribName)
    throws PPDException
  {
    return this.getAttribValue(element, attribName, null);
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as an integer. If the attribute does not exist or is void (i.e.,
   * the empty string), the following happens: If <code>defaultValue</code> is not
   * <code>null</code>, returns <code>defaultValue</code>, otherwise, throws a
   * {@link PPDException}. 
   *
   * @throws PPDException if the attribute does not exist or is void and
   * <code>defaultValue</code> is <code>null</code>, or if the value of the attribute can
   * not be converted into an integer.
   */

  protected int getAttribValueAsInt (Element element,
                                     String attribName,
                                     Integer defaultValue)
    throws PPDException
  {
    String attribValueAsString = element.getAttribute(attribName);
    if ( attribValueAsString.equals("") )
      {
        if ( defaultValue != null )
          return defaultValue.intValue();
        else
          throw new PPDException
            ("Attribute void or missing: " + attribName);
      }
    else
      {
        try
          {
            return Integer.parseInt(attribValueAsString);
          }
        catch (NumberFormatException exception)
          {
            throw new PPDException
              ("Problem parsing attribute: " + attribName, exception);
          }
      }
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as an integer. If the attribute does not exist or is void (i.e.,
   * the empty string), returns <code>defaultValue</code>. 
   *
   * @throws PPDException If the value of the attribute can not be converted into an
   * integer.
   */

  protected int getAttribValueAsInt (Element element,
                                     String attribName,
                                     int defaultValue)
    throws PPDException
  {
    return this.getAttribValueAsInt(element, attribName, new Integer(defaultValue));
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as an integer. If the attribute does not exist or is void (i.e.,
   * the empty string), throws a {@link PPDException}. 
   *
   * @throws PPDException if the attribute does not exist or is void, or if the value of the
   * attribute can not be converted into an integer.
   */

  protected int getAttribValueAsInt (Element element,
                                     String attribName)
    throws PPDException
  {
    return this.getAttribValueAsInt(element, attribName, null);
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as a double. If the attribute does not exist or is void (i.e.,
   * the empty string), the following happens: If <code>defaultValue</code> is not
   * <code>null</code>, returns <code>defaultValue</code>, otherwise, throws a
   * {@link PPDException}. 
   *
   * @throws PPDException if the attribute does not exist or is void and
   * <code>defaultValue</code> is <code>null</code>, or if the value of the attribute can
   * not be converted into a double.
   */

  protected double getAttribValueAsDouble (Element element,
                                           String attribName,
                                           Double defaultValue)
    throws PPDException
  {
    String attribValueAsString = element.getAttribute(attribName);
    if ( attribValueAsString.equals("") )
      {
        if ( defaultValue != null )
          return defaultValue.doubleValue();
        else
          throw new PPDException
            ("Attribute void or missing: " + attribName);
      }
    else
      {
        try
          {
            return Double.parseDouble(attribValueAsString);
          }
        catch (NumberFormatException exception)
          {
            throw new PPDException
              ("Problem parsing attribute: " + attribName, exception);
          }
      }
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as a double. If the attribute does not exist or is void (i.e.,
   * the empty string), returns <code>defaultValue</code>. 
   *
   * @throws PPDException If the value of the attribute can not be converted into a
   * double.
   */

  protected double getAttribValueAsDouble (Element element,
                                           String attribName,
                                           double defaultValue)
    throws PPDException
  {
    return this.getAttribValueAsDouble(element, attribName, new Double(defaultValue));
  }

  /**
   * Returns the value of the attribute with name <code>attribName</code> of
   * <code>element</code> as a double. If the attribute does not exist or is void (i.e.,
   * the empty string), throws a {@link PPDException}. 
   *
   * @throws PPDException if the attribute does not exist or is void, or if the value of the
   * attribute can not be converted into a double.
   */

  protected double getAttribValueAsDouble (Element element,
                                           String attribName)
    throws PPDException
  {
    return this.getAttribValueAsDouble(element, attribName, null);
  }

  /**
   * <p>
   *   Returns the value of the attribute with name <code>attribName</code> of
   *   <code>element</code> as a booelan. If the attribute does not exist or is void (i.e.,
   *   the empty string), the following happens: If <code>defaultValue</code> is not
   *   <code>null</code>, returns <code>defaultValue</code>, otherwise, throws a
   *   {@link PPDException}.
   * </p>
   * <p>
   *   The conversion of the attribute value into a boolean is done if follows: If the
   *   attribute value is <code>"yes"</code> or <code>"true"</code>, the boolean value is
   *   <code>true</code>. If the attribute value is <code>"no"</code> or
   *   <code>"false"</code>, the boolean value is <code>false</code>. Any other attribute
   *   value will cause a {@link PPDException}.
   * </p>
   *
   * @throws PPDException if the attribute does not exist or is void and
   * <code>defaultValue</code> is <code>null</code>, or if the value of the attribute can
   * not be converted into a boolean.
   */

  protected boolean getAttribValueAsBoolean (Element element,
                                             String attribName,
                                             Boolean defaultValue)
    throws PPDException
  {
    String attribValueAsString = element.getAttribute(attribName);
    if ( attribValueAsString.equals("") )
      {
        if ( defaultValue != null )
          return defaultValue.booleanValue();
        else
          throw new PPDException
            ("Attribute void or missing: " + attribName);
      }
    else if ( attribValueAsString.equalsIgnoreCase("yes") ||
              attribValueAsString.equalsIgnoreCase("true") )
      return true;
    else if ( attribValueAsString.equalsIgnoreCase("no") ||
              attribValueAsString.equalsIgnoreCase("false") )
      return false;
    else
      throw new PPDException
        ("Problem parsing attribute: " + attribName +
         ": invalid boolean specifier: " + attribValueAsString);
  }

  /**
   * <p>
   *   Returns the value of the attribute with name <code>attribName</code> of
   *   <code>element</code> as a boolean. If the attribute does not exist or is void (i.e.,
   *   the empty string), returns <code>defaultValue</code>. 
   * </p>
   * <p>
   *   See {@link #getAttribValueAsBoolean(Element,String,Boolean)} for how the attribute
   *   value is converted into a boolean.
   * </p>
   *
   * @throws PPDException If the value of the attribute can not be converted into a
   * boolean.
   */

  protected boolean getAttribValueAsBoolean (Element element,
                                             String attribName,
                                             boolean defaultValue)
    throws PPDException
  {
    return this.getAttribValueAsBoolean(element, attribName, new Boolean(defaultValue));
  }

  /**
   * <p>
   *   Returns the value of the attribute with name <code>attribName</code> of
   *   <code>element</code> as a boolean. If the attribute does not exist or is void (i.e.,
   *   the empty string), throws a {@link PPDException}. 
   * </p>
   * <p>
   *   See {@link #getAttribValueAsBoolean(Element,String,Boolean)} for how the attribute
   *   value is converted into a boolean.
   * </p>
   *
   * @throws PPDException if the attribute does not exist or is void, or if the value of the
   * attribute can not be converted into a boolean.
   */

  protected boolean getAttribValueAsBoolean (Element element,
                                             String attribName)
    throws PPDException
  {
    return this.getAttribValueAsBoolean(element, attribName, null);
  }

  // --------------------------------------------------------------------------------
  // Creating random data
  // --------------------------------------------------------------------------------

  /**
   * Creates a random integer between <code>min</code> and <code>max</code> (both
   * inclusive). If <code>nonZero</code> is <code>true</code>, the integer is guaranteed to
   * be non-zero. Otherwise, is may be zero.
   */

  protected synchronized int createRandomInt (int min, int max, boolean nonZero)
  {
    if ( max < min )
      throw new IllegalArgumentException("max < min");

    if ( nonZero && min < 0 && max > 0)
      {
        int index = random.nextInt(max - min);
        return
          (index < -min
           ? min + index
           : min + index + 1);
      }

    if ( nonZero && min == 0 && max == 0 )
      throw new IllegalArgumentException
        ("non-zero value requested, but both min and max zero");

    return
      (min == max
       ? min
       : min + random.nextInt(max - min + 1));
  }

  /**
   * Creates a random integer between <code>min</code> and <code>max</code> (both
   * inclusive).
   */

  protected int createRandomInt (int min, int max)
  {
    return this.createRandomInt(min, max, false);
  }

  /**
   * <p>
   *  Creates a random integer in a range determined by the attributes of
   *   <code>element</code>. 
   * </p>
   * <p>
   *   The random number will be between <var>min</var> and <var>max</var> (both inclusive),
   *   where <var>min</var> and <var>max</var> are the values of the attributes
   *   <code>minAttribName</code> and <code>maxAttribName</code>, respectively.
   * </p>
   * <p>
   *   The parameters <code>nonZeroAttribName</code> and <code>nonZeroDefault</code> control
   *   wether the random number can be zero or not:
   * </p>
   * <p>
   *   If <code>nonZeroAttribName</code> is not <code>null</code>, and an attribute with
   *   that name exists, its value must be <code>"yes"</code>, <code>"true"</code>,
   *   <code>"no"</code>, or <code>"false"</code>. In the first two cases, the random number
   *   is guaranteed to be non-zero. In the latter two cases it may be zero.
   * </p>
   * <p>
   *   If <code>nonZeroAttribName</code> is <code>null</code>, or if it is not
   *   <code>null</code> but no attribute with that name exists, the random number is
   *   guaranteed to be non-zero if <code>nonZeroDefault</code> is <code>true</code> and may
   *   be zero otherwise.
   * </p>
   */

  protected int createRandomInt (Element element,
                                 String minAttribName,
                                 String maxAttribName,
                                 String nonZeroAttribName,
                                 boolean nonZeroDefault)
    throws PPDException
  {
    int min = this.getAttribValueAsInt(element, minAttribName);
    int max = this.getAttribValueAsInt(element, maxAttribName);
    boolean nonZero = 
      (nonZeroAttribName == null
       ? nonZeroDefault
       : this.getAttribValueAsBoolean(element, nonZeroAttribName, nonZeroDefault));
    return this.createRandomInt(min, max, nonZero);
  }

  /**
   * Creates a random double between <code>min</code> and <code>max</code> (inclusive).
   */

  protected synchronized double createRandomDouble (double min, double max)
  {
    if ( max < min )
      throw new IllegalArgumentException("max < min");
    return min + this.random.nextFloat() * (max -min);
  }

  /**
   * <p>
   *   Creates a random double in a range determined by the attributes of
   *   <code>element</code>.
   * </p>
   * <p>
   *   The random number will be between <var>min</var> and <var>max</var> (both inclusive),
   *   where <var>min</var> and <var>max</var> are the values of the attributes
   *   <code>minAttribName</code> and <code>maxAttribName</code>, respectively.
   * </p>
   */

  protected double createRandomDouble (Element element,
                                       String minAttribName,
                                       String maxAttribName)
    throws PPDException
  {
    double min = this.getAttribValueAsDouble(element, minAttribName);
    double max = this.getAttribValueAsDouble(element, maxAttribName);
    return this.createRandomDouble(min, max);
  }

  /**
   * Randomly selects the specified number of values from the specified range.
   */

  public int[] randomSelect (int number, int min, int max)
  {
    int[] result = new int[number];
    int[] values = new int[max-min+1];

    for (int pos = 0; pos < values.length; pos++)
      values[pos] = min + pos;

    int count = 0;

    while (true )
      {
        int posOfSelected = this.random.nextInt(values.length);
        result[count] = values[posOfSelected];

        if ( count == result.length-1 ) break;

        count++;

        int[] newValues = new int[values.length-1];
        int newPos = 0;
        for (int pos = 0; pos < values.length; pos++)
          {
            if ( pos != posOfSelected )
              {
                newValues[newPos] = values[pos];
                newPos++;
              }
          }

        values = newValues;
      }

    return result;
  }

  // --------------------------------------------------------------------------------
  // Processing elements for PPD creation
  // --------------------------------------------------------------------------------

  /**
   * Processes a {@link XMLElement#RANDOM_INTEGER RANDOM_INTEGER} element.
   */

  protected void processElement_RANDOM_INTEGER (Element element,
                                                CocoonEnabledDataSheet dataSheet,
                                                boolean force)
    throws PPDException
  {
    try
      {
        String path = this.getAttribValue(element, XMLAttribute.PATH);
        if ( force || dataSheet.getDataElement(path) == null )
          {
            int value = this.createRandomInt
              (element,
               XMLAttribute.MIN, XMLAttribute.MAX,
               XMLAttribute.NON_ZERO, false);
            MInteger mInteger = new MInteger(value);
            Node node = mInteger.getMathMLNode(dataSheet.getDocument());
            dataSheet.put(path, node);
          }
      }
    catch (Exception exception)
      {
        throw new PPDException(exception);
      }
  }                                                   

  /**
   * Processes a {@link XMLElement#RANDOM_RATIONAL RANDOM_RATIONAL} element.
   */

  protected void processElement_RANDOM_RATIONAL (Element element,
                                                 CocoonEnabledDataSheet dataSheet,
                                                 boolean force)
    throws PPDException
  {
    try
      {
        String path = this.getAttribValue(element, XMLAttribute.PATH);
        if ( force || dataSheet.getDataElement(path) == null )
          {
            int numeratorValue = this.createRandomInt
              (element,
               XMLAttribute.NUMERATOR_MIN, XMLAttribute.NUMERATOR_MAX,
               XMLAttribute.NON_ZERO, false);
            int denominatorValue = this.createRandomInt
              (element,
               XMLAttribute.DENOMINATOR_MIN, XMLAttribute.DENOMINATOR_MAX,
               null, true);
            boolean reduce = this.getAttribValueAsBoolean(element, XMLAttribute.REDUCE, true);
            MRational mRational = new MRational(numeratorValue, denominatorValue);
            if ( reduce ) mRational.cancelDown();
            Node node = mRational.getMathMLNode(dataSheet.getDocument());
            dataSheet.put(path, node);
          }
      }
    catch (Exception exception)
      {
        throw new PPDException(exception);
      }
  }                                                   

  /**
   * Processes a {@link XMLElement#RANDOM_REAL RANDOM_REAL} element.
   */

  protected void processElement_RANDOM_REAL (Element element,
                                             CocoonEnabledDataSheet dataSheet,
                                             boolean force)
    throws PPDException
  {
    try
      {
        String path = this.getAttribValue(element, XMLAttribute.PATH);
        if ( force || dataSheet.getDataElement(path) == null )
          {
            double value =
              this.createRandomDouble(element, XMLAttribute.MIN, XMLAttribute.MAX);
            MDouble mDouble = new MDouble(value);
            Node node = mDouble.getMathMLNode(dataSheet.getDocument());
            dataSheet.put(path, node);
          }
      }
    catch (Exception exception)
      {
        throw new PPDException(exception);
      }
  }

  /**
   * Processes a {@link XMLElement#RANDOM_SELECT RANDOM_SELECT} element.
   */

  protected void processElement_RANDOM_SELECT (Element element,
                                               CocoonEnabledDataSheet dataSheet,
                                               boolean force)
    throws PPDException
  {
    final String METHOD_NAME = "processElement_RANDOM_SELECT";
    this.getLogger().debug(METHOD_NAME + " 1/5: Started");

    try
      {
        // Get key:
        int selectionKey = this.getAttribValueAsInt(element, XMLAttribute.KEY);
        this.getLogger().debug(METHOD_NAME + " 2/5: selectionKey = " + selectionKey);

        // Path of "selection" unit:
        String selectionPath = "user/problem/selection-" + selectionKey;

        // Element of "selection" unit, or null if inexistent:
        Element selectionUnit = dataSheet.getUnitElement(selectionPath, false);

        this.getLogger().debug
          (METHOD_NAME + " 3/5:" +
           " force = " + force +
           ", selectionUnit " + (selectionUnit != null ? "exists" : "does not exist"));

        // Check if selection must be carried out:
        if ( force || selectionUnit == null )
          {
            this.getLogger().debug(METHOD_NAME + " 4/5: Creating selection");

            // Delete existing "selection" unit element, if any:
            if ( selectionUnit != null )
              selectionUnit.getParentNode().removeChild(selectionUnit);

            // Get all ppd:option elements:
            NodeList optionElements =
              element.getElementsByTagNameNS(XMLNamespace.URI_PPD, XMLElement.OPTION);
            int optionNumber = optionElements.getLength();

            // Get the number of options to select:
            int number = this.getAttribValueAsInt(element, XMLAttribute.NUMBER);

            // Check:
            if ( number < 1 ) throw new PPDException
              ("PPD random select: number of selections must be positive (is " + number + ")");
            if ( number > optionNumber ) throw new PPDException
              ("PPD random select: number of selections is greater then number of options " +
               "(" + number + " vs. " + optionNumber + ")");

            // Select options:
            int[] selectedOptionKeys = this.randomSelect(number, 1, optionNumber);

            // Save in datasheet:
            for (int i = 0; i < optionNumber; i++)
              {
                Element optionElement = (Element)optionElements.item(i);
                int optionKey = this.getAttribValueAsInt(optionElement, XMLAttribute.KEY);
                boolean selected = contains(selectedOptionKeys, optionKey);
                String optionPath =
                  "user/problem/selection-" + selectionKey + "/option-" + optionKey + "/selected";
                dataSheet.put(optionPath, selected);

                // Check which prb:choices elements are in this option:
                NodeList choicesElements = optionElement.getElementsByTagNameNS
                  (XMLNamespace.URI_PROBLEM, XMLElement.CHOICES);
                for (int k = 0; k < choicesElements.getLength(); k++)
                  {
                    Element choicesElement = (Element)choicesElements.item(k);
                    int choicesKey = this.getAttribValueAsInt(choicesElement, XMLAttribute.KEY);
                    String choicesPath = "user/problem/choices-" + choicesKey + "/selected";
                    dataSheet.put(choicesPath, selected);
                  }
              }
          }
        else
          {
            this.getLogger().debug(METHOD_NAME + " 4/5: Skipping selection");
          }
        this.getLogger().debug(METHOD_NAME + " 5/5: Done");
      }
    catch (Exception exception)
      {
        throw new PPDException(exception);
      }
  }                                                   

  // --------------------------------------------------------------------------------
  // Implemetation of the "create" methods
  // --------------------------------------------------------------------------------

  /**
   * Creates the PPD for a problem and saves them in a datasheet. The problem content is
   * passed as a DOM tree, represented by <code>document</code>. The map
   * <code>sources</code> contains any user-specific input data needed to create the PPD
   * (e.g., the matriculation number). If <code>force</code> is <code>true</code>, already
   * existing PPD in the datasheet will be overwritten; otherwise, it will be preserved. The
   * datasheet is given by <code>datasheet</code>.
   */

  public void create (Document document,
                      CocoonEnabledDataSheet dataSheet,
                      Map sources,
                      boolean force)
    throws PPDException
  {
    try
      {
        Element[] elements = this.getPPDElements(document);
        for (int i = 0; i < elements.length; i++)
          {
            Element element = elements[i];
            String name = element.getLocalName();
            if ( name.equals(XMLElement.RANDOM_INTEGER) )
              this.processElement_RANDOM_INTEGER(element, dataSheet, force);
            else if ( name.equals(XMLElement.RANDOM_RATIONAL) )
              this.processElement_RANDOM_RATIONAL(element, dataSheet, force);
            else if ( name.equals(XMLElement.RANDOM_REAL) )
              this.processElement_RANDOM_REAL(element, dataSheet, force);
            else if ( name.equals(XMLElement.COPY) )
              /* not processed here */ ;
            else if ( name.equals(XMLElement.RANDOM_SELECT) )
              this.processElement_RANDOM_SELECT(element, dataSheet, force);
            else if ( name.equals(XMLElement.OPTION) )
              /* not processed here */ ;
            else
              throw new PPDException("Unknown PPD element: " + name);
          }
      } 
    catch (Exception exception)
      {
        throw new PPDException(exception);
      }
  }

  /**
   * Same as {@link #create(Document,CocoonEnabledDataSheet,Map,boolean) create(document,
   * dataSheet, sources, false)}.
   */

  public void create (Document document,
                      CocoonEnabledDataSheet dataSheet,
                      Map sources)
    throws PPDException
  {
    this.create(document, dataSheet, sources, false);
  }

  // --------------------------------------------------------------------------------
  // Implemetation of the "resolve" method
  // --------------------------------------------------------------------------------

  /**
   * Replaces all PPD elements in <code>document</code> with the data found in
   * <code>dataSheet</code>.
   */

  public void resolve (Document document,
                       CocoonEnabledDataSheet dataSheet)
    throws PPDException
  {
    try
      {
        Element[] elements = this.getPPDElements(document);
        for (int i = 0; i < elements.length; i++)
          {
            Element element = elements[i];
            String path = this.getAttribValue(element, XMLAttribute.PATH);
            NodeList data = dataSheet.getAsNodeList(path);
            if ( data == null )
              throw new PPDException("Path not found in datasheet: " + path);
            this.replaceNode(element, this.nodeListToNode(data, document));
          }
      }
    catch (Exception exception)
      {
        throw new PPDException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * Returns true if the specific value is in the specific array, otherwise false.
   */

  protected static boolean contains (int[] array, int value)
  {
    boolean contains = false;
    for (int i = 0; i < array.length && !contains; i++)
      {
        if ( array[i] == value ) contains = true;
      }
    return contains;
  }
}
