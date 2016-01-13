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

package net.mumie.mathletfactory.util.exercise;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mumie.japs.datasheet.DataSheet;

/**
 * <p>
 *   Represents a multiple choice form. Intended to be synchronized with the XHTML form of
 *   a multiple choice problem. Can write and read the form to resp. from datasheets.
 * </p>
 * <p>
 *   This class holds an internal representation of the XHTML form of a multiple choice
 *   problem. As described in the corresponding
 *   <a href="http://www.w3.org/TR/html4/interact/forms.html">HTML specification</a>, XHTML
 *   forms are made up of so-called <em>controls</em>. Each control has a <em>name</em> and
 *   a <em>value</em>. The internal representation is simply a <code>Map</code> relating
 *   control names to values.
 * </p>
 * <p>
 *   There are three public methods by means of which the internal representation can
 *   be manipulated: {@link #setControl setControl}, {@link #unsetControl unsetControl}, and
 *   {@link #toggleControl toggleControl}. The synchronization mentioned above is best done
 *   by JavaScript functions calling these methods (to this end, the methods should be
 *   wrapped by public methods of the applet so JavaScrit can access them).
 * </p>
 * <p>
 *   For writing the from to a datasheet, the class provides the public method
 *   {@link #toDataSheet toDataSheet}. For reading form settings from a datasheet, the class
 *   provides the public method {@link #readDataSheet readDataSheet}.
 * </p>
 * <p>
 *   This class can write log messages. By default, logging is turned on and the messages
 *   are written to <code>System.out</code>. This can be changed by means of the
 *   {@link #setLogEnabled setLogEnabled} and {@link #setLogStream setLogStream} methods.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: MchoiceForm.java,v 1.5 2007/07/16 11:40:16 grudzin Exp $</code>
 */

public class MchoiceForm
{
  // --------------------------------------------------------------------------------
  // Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * Common prefix of all request parameters that represent a multiple choice answer
   * (<code>"answer"</code>). 
   */

  static final String PREFIX = "answer";

  /**
   * Keyword for the "unique" answer type
   * (<code>"unique"</code>).
   */

  static final String UNIQUE = "unique";

  /**
   * Keyword for the "multiple" answer type
   * (<code>"multiple"</code>).
   */

  static final String MULTIPLE = "multiple";

  /**
   * Keyword for the "yesno" answer type
   * (<code>"yesno"</code>).
   */

  static final String YESNO = "yesno";

  /**
   * Value of request parameters corresponding to a "yesno" question with a positive answer
   * (<code>"yes"</code>). 
   */

  static final String YES = "yes";

  /**
   * Value of request parameters corresponding to a "yesno" question with a negative answer
   * (<code>"no"</code>). 
   */

  static final String NO = "no";

  /**
   * Value of request parameters corresponding to a selected "multiple" question
   * (<code>"selected"</code>).  
   */

  static final String SELECTED = "selected";

  /**
   * Value of a positive entry in the datasheet
   * (<code>"true"</code>).
   */

  static final String TRUE = "true";

  /**
   * Value of a negative entry in the datasheet
   * (<code>"false"</code>).
   */

  static final String FALSE = "false";

  /**
   * The map that represents the from data.
   */

  protected Map form = new HashMap();

  /**
   * Pattern to parse the names of form controls that represent multiple choice answers.
   */

  protected Pattern formCtrlNamePattern = Pattern.compile
    (PREFIX
     + "\\.(" + UNIQUE + "|" + MULTIPLE + "|" + YESNO + ")"
     + "\\.([0-9]+)(?:\\.([0-9]+))?");

  /**
   * Pattern to check the values of form controls that represent type="unique" questions.
   */

  protected Pattern uniqueValuePattern = Pattern.compile("[0-9]+");

  /**
   * Pattern to parse datasheet paths correxponding to multiple choice answers.
   */

  protected Pattern choicePathPattern = Pattern.compile
    ("^user/answer/choices-([0-9]+)/choice-([0-9]+)$");

  /**
   * The print stream to which the log messages are written. Defaults to
   * {@link System#out System.out}.
   */

  protected PrintStream logStream = System.out;

  /**
   * Whether logging is enabled. Defaults to <code>true</code>.
   */

  protected boolean logEnabled = true;

  // --------------------------------------------------------------------------------
  // Inner classes
  // --------------------------------------------------------------------------------

  /**
   * Auxiliary class representing a single answer. Needed in
   * {@link #toDataSheet toDataSheet} and {@link #parseForm parseForm}. Comprises the type
   * of the answer and the selected choices.
   */

  protected class Answer
  {
    /**
     * The type.
     */

    String type = null;

    /**
     * The selected choices.
     */

    Map selectedChoices = new HashMap();
  }

  // --------------------------------------------------------------------------------
  // Controlling form data
  // --------------------------------------------------------------------------------

  /**
   * Sets the value of the form control with the specified name to the specified value. If
   * the control has already a value, the old value is overwritten.
   *
   * @param name the form control name
   * @param value the form control value
   */

  public void setControl (String name, String value)
  {
    final String METHOD_NAME = "setControl";
    this.log(METHOD_NAME + " 1/1: name = " + name + ", value = " + value);
    this.form.put(name, value);
  }

  /**
   * Unsets the value of the form control with the specified name. It is not an error if the
   * control has no value (is already unset). In that case, the method does nothing.
   * 
   * @param name the form control name
   */

  public void unsetControl (String name)
  {
    final String METHOD_NAME = "unsetControl";
    this.log(METHOD_NAME + " 1/2: name = " + name);
    if ( this.form.containsKey(name) )
      {
        this.log(METHOD_NAME + "2/2: Entry exists. Will be removed");
        this.form.remove(name);
      }
    else
      this.log(METHOD_NAME + "2/2: Entry does not exist");
  }

  /**
   * Toggles the form control with the specified name and value. That means the following:
   * If the control is set and has the specified value, the control is unset. Otherwise, the
   * control is set to the specifed value.
   *
   * @param name the form control name
   * @param value the form control value
   */

  public void toggleControl (String name, String value)
  {
    final String METHOD_NAME = "toggleControl";
    this.log(METHOD_NAME + " 1/2: name = " + name + ", value = " + value);
    if ( this.form.containsKey(name) && ((String)this.form.get(name)).equals(value) )
      {
        this.log(METHOD_NAME + "2/2: Entry exists. Will be removed");
        this.form.remove(name);
      }
    else
      {
        this.log(METHOD_NAME + "2/2: Entry does not exist. Will be created");
        this.form.put(name, value);
      }
  }

  // --------------------------------------------------------------------------------
  // Convering the form data to a datasheet
  // --------------------------------------------------------------------------------

  /**
   * Reads the answers from the specified form and stores them in the specified datasheet.
   */

  public void toDataSheet (DataSheet dataSheet)
    throws MchoiceFormException
  {
    final String METHOD_NAME = "toDataSheet";
    this.log(METHOD_NAME + " 1/2: Started");
    Map answers = this.parseForm();
    Iterator answerIterator = answers.entrySet().iterator();
    while ( answerIterator.hasNext() )
      {
        Map.Entry answerEntry = (Map.Entry)answerIterator.next();
        String choicesKey = (String)answerEntry.getKey();

        Answer answer = (Answer)answerEntry.getValue();
        
        Iterator selectedChoiceIterator = answer.selectedChoices.entrySet().iterator();
        while ( selectedChoiceIterator.hasNext() )
          {
            Map.Entry selectedChoiceEntry = (Map.Entry)selectedChoiceIterator.next();
            String choiceKey = (String)selectedChoiceEntry.getKey();
            String value = (String)selectedChoiceEntry.getValue();
            String path = "user/answer/choices-" + choicesKey + "/choice-" + choiceKey;
            dataSheet.put(path, value);
          }        
      }      
    this.log(METHOD_NAME + " 2/2: Done");
  }

  /**
   * Reads the answers from the specified form.
   */

  protected Map parseForm ()
    throws MchoiceFormException
  {
    final String METHOD_NAME = "parseForm";
    this.log(METHOD_NAME + " 1/2: this.form = " + this.form);
    Map answers = new HashMap();
    Iterator controls = this.form.entrySet().iterator();
    int count = 0;
    while ( controls.hasNext() )
      {
        Map.Entry control = (Map.Entry)controls.next();
        String controlName = (String)control.getKey();
        if ( controlName.startsWith(PREFIX + ".") )
          {
            count++;

            // Parsing the control name:
            Matcher matcher = this.formCtrlNamePattern.matcher(controlName);
            if ( !matcher.matches() )
              throw new MchoiceFormException
                ("Malformed control name: " + controlName);
            String controlName_part1 = matcher.group(1);
            String controlName_part2 = matcher.group(2);
            String controlName_part3 = matcher.group(3);
            String controlValue = (String)control.getValue();
            this.log
              (METHOD_NAME + " 1." + count + "/2: " +
               "controlName_part1 = " + controlName_part1 + ", " +
               "controlName_part2 = " + controlName_part2 + ", " +
               "controlName_part3 = " + controlName_part3 + ", " +
               "controlValue = " + controlValue);
            
            // Retrieving type, choices and choice key, value; checking:
            String type = controlName_part1;
            String choicesKey = controlName_part2;
            String choiceKey = null;
            String value = null;
            if ( type.equals(UNIQUE) )
              {
                if ( controlName_part3 != null )
                  throw new MchoiceFormException
                    ("Malformed form control name: " + controlName +
                     " (No part 3 allowed)");
                if ( !this.uniqueValuePattern.matcher(controlValue).matches() )
                  throw new MchoiceFormException
                    ("Illegal form control value with a type=\"unique\" question: "
                     + controlValue);
                choiceKey = controlValue;
                value = TRUE;
              }
            else if ( type.equals(MULTIPLE) )
              {
                if ( !controlValue.equals(SELECTED) )
                  throw new MchoiceFormException
                    ("Illegal form control value with a type=\"multiple\" question: "
                     + controlValue);
                choiceKey = controlName_part3;
                value = TRUE;
              }
            else if ( type.equals(YESNO) )
              {
                // TODO: Use either yes|no or true|false
                if ( controlValue.equals(YES) )
                  value = TRUE;
                else if ( controlValue.equals(NO) )
                  value = FALSE;
                else
                  throw new MchoiceFormException
                    ("Illegal form control value with a type=\"yesno\" question: "
                     + controlValue);
                choiceKey = controlName_part3;
              }
            else
              throw new MchoiceFormException
                ("BUG: Unexpected question type: " + type);

            // Answer object; checking:
            Answer answer;
            if ( answers.containsKey(choicesKey) )
              answer = (Answer)answers.get(choicesKey);
            else
              {
                answer = new Answer();
                answer.type = type;
                answers.put(choicesKey, answer);
              }
            if ( !answer.type.equals(type) )
              throw new MchoiceFormException
                ("Different types for choices key \"" + choicesKey + "\": " +
                 "\"" + answer.type + "\", \"" + type + "\"");

            // Selected choices, checking:
            if ( answer.selectedChoices.containsKey(choiceKey) )
              throw new MchoiceFormException
                ("Dublicate choice-key for choices-key \"" + choicesKey + "\": " + choiceKey);
            if ( type.equals(UNIQUE) &&
                 !answer.selectedChoices.isEmpty() )
              throw new MchoiceFormException
                ("Multiple choices selected for the type=\"unique\" question with choices key: " +
                 choicesKey);

            answer.selectedChoices.put(choiceKey, value);
          }
      }
    this.log(METHOD_NAME + " 2/2");
    return answers;
  }

  // --------------------------------------------------------------------------------
  // Setting form controls from a datasheet
  // --------------------------------------------------------------------------------

  /**
   * Reads the answers from the specified datasheet and updates the from according to them.
   */

  public void readDataSheet (DataSheet datasheet)
    throws MchoiceFormException
  {
    final String METHOD_NAME = "readDataSheet";
    this.log(METHOD_NAME + " 1/2: Started");

    try
      {
        String[] paths = datasheet.getAllDataPaths();
        int count = 0;
        for (int i = 0; i < paths.length; i++)
          {
            Matcher matcher = this.choicePathPattern.matcher(paths[i]);
            if ( matcher.matches() )
              {
                count++;

                String choicesKey = matcher.group(1);
                String choiceKey = matcher.group(2);
                String type = datasheet.getAsString
                  ("common/problem/choices-" + choicesKey + "/type");
                String value = datasheet.getAsString
                  ("user/answer/choices-" + choicesKey + "/choice-" + choiceKey);

                this.log
                  (METHOD_NAME + " 1." + count + "/2:" +
                   " choicesKey = " + choicesKey + 
                   ", choiceKey = " + choiceKey +
                   ", type = " + type +
                   ", value = " + value);

                if ( type.equals(UNIQUE) && value.equals(TRUE) )
                  {
                    String controlName = PREFIX + "." + UNIQUE + "." + choicesKey;
                    String controlValue = choiceKey;
                    this.setControl(controlName, controlValue);  
                  }
                else if ( type.equals(MULTIPLE)  && value.equals(TRUE) )
                  {
                    String controlName =
                      PREFIX + "." + MULTIPLE + "." + choicesKey + "." + choiceKey;
                    String controlValue = SELECTED;
                    this.toggleControl(controlName, controlValue);  
                  }
                else if ( type.equals(YESNO) )
                  {
                    String controlName =
                      PREFIX + "." + YESNO +  "." + choicesKey + "." + choiceKey;
                    // TODO: Use either yes|no or true|false
                    String controlValue = (value.equals(TRUE) ? YES : NO);
                    this.setControl(controlName, controlValue);  
                  }
              }
          }

        this.log(METHOD_NAME + " 2/2: Done");
      }
    catch (Exception exception)
      {
        throw new MchoiceFormException(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // Logging
  // --------------------------------------------------------------------------------

  /**
   * Prints a log message unless {@link #logEnabled logEnabled} is false.
   */

  protected void log (String message)
  {
    if ( logEnabled )
      this.logStream.println(this.getClass().getName() + ": " + message);
  }

  /**
   * Enables or disables logging.
   */

  public void setLogEnabled (boolean logEnabled)
  {
    this.logEnabled = logEnabled;
  }

  /**
   * Sets the print stream to which the log messages are written.
   */

  public void setLogStream (PrintStream logStream)
  {
    if ( logStream == null )
      throw new IllegalArgumentException("Log stream null");
    this.logStream = logStream;
  }
}
