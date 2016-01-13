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

import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.Iterator;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.environment.Request;
import net.mumie.cocoon.service.AbstractJapsService;
import net.mumie.cocoon.service.ServiceStatus;
import net.mumie.cocoon.service.ServiceInstanceStatus;

/**
 * <p>
 *   Default implementation of {@link MchoiceProblemAnswersHelper}.
 * </p>
 * <p>
 *   In this implementation, the answers are internally represented by a {@link
 *   java.util.Map Map} whose keys are the so-called <em>choices keys</em> and whose values
 *   are {@link DefaultMchoiceProblemAnswersHelper.Answer Answer} objects. Each key-value
 *   pair corresponds to an answerd question.
 *   {@link DefaultMchoiceProblemAnswersHelper.Answer Answer} is a helper class that simply
 *   comprises two fields of data: The <em>type</em> of the question (<code>unique</code>,
 *   <code>multiple</code>, or <code>yesno</code>), and the selected choices. The latter
 *   are represented by another {@link java.util.Map Map}. Each key-value pair corresponds
 *   to a selected choice of the corresponding question; and the keys and
 *   values are the so-called <em>choice-keys</em> and <em>values</em>, respectively.
 * </p>
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: DefaultMchoiceProblemAnswersHelper.java,v 1.6 2007/07/11 15:38:52 grudzin Exp $</code>
 */

public class DefaultMchoiceProblemAnswersHelper extends AbstractJapsService 
  implements MchoiceProblemAnswersHelper
{
  /**
   * The status object of this class.
   */

  private static ServiceStatus serviceStatus =
    new ServiceStatus(DefaultMchoiceProblemAnswersHelper.class);

  /**
   * Pattern to parse request parameters that represent multiple choice answers.
   */

  protected Pattern paramPattern = Pattern.compile
    (PREFIX
     + "\\.(" + UNIQUE + "|" + MULTIPLE + "|" + YESNO + ")"
     + "\\.([0-9]+)(?:\\.([0-9]+))?");

  /**
   * Pattern to check the values of request parameters that represent type="unique" questions.
   */

  protected Pattern uniqueValuePattern = Pattern.compile("[0-9]+");

  /**
   * Represents a single answer.
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

  /**
   * Creates a new instance.
   */

  public DefaultMchoiceProblemAnswersHelper ()
  {
    this.instanceStatus = new ServiceInstanceStatus(serviceStatus);
    this.instanceStatus.notifyCreation();
  }

  /**
   * Reads the answers from the request parameters in <code>request</code>.
   */

  protected Map parseRequest (Request request)
    throws MchoiceProblemAnswersException
  {
    final String METHOD_NAME = "parseRequest";
    this.getLogger().debug(METHOD_NAME + " 1/2: request = " + request);
    Map answers = new HashMap();
    Enumeration paramNames = request.getParameterNames();
    int count = 0;
    while ( paramNames.hasMoreElements() )
      {
	String paramName = (String)paramNames.nextElement();
        if ( paramName.startsWith(PREFIX + ".") )
          {
            count++;

            // Parsing the request parameter:
            Matcher matcher = this.paramPattern.matcher(paramName);
            if ( !matcher.matches() )
              throw new MchoiceProblemAnswersException
                ("Malformed request parameter name: " + paramName);
            String paramName_part1 = matcher.group(1);
            String paramName_part2 = matcher.group(2);
            String paramName_part3 = matcher.group(3);
            String paramValue = request.getParameter(paramName);
            this.getLogger().debug(METHOD_NAME + "1." + count + "/2: " +
                                   "paramName_part1 = " + paramName_part1 + ", " +
                                   "paramName_part2 = " + paramName_part2 + ", " +
                                   "paramName_part3 = " + paramName_part3 + ", " +
                                   "paramValue = " + paramValue);

            // Retrieving type, choices and choice key, value; checking:
            String type = paramName_part1;
            String choicesKey = paramName_part2;
            String choiceKey = null;
            String value = null;
            if ( type.equals(UNIQUE) )
              {
                if ( paramName_part3 != null )
                  throw new MchoiceProblemAnswersException
                    ("Malformed request parameter name: " + paramName +
                     " (No part 3 allowed)");
                if ( !this.uniqueValuePattern.matcher(paramValue).matches() )
                  throw new MchoiceProblemAnswersException
                    ("Illegal request parameter value with a type=\"unique\" question: "
                     + paramValue);
                choiceKey = paramValue;
                value = TRUE;
              }
            else if ( type.equals(MULTIPLE) )
              {
                if ( !paramValue.equals(SELECTED) )
                  throw new MchoiceProblemAnswersException
                    ("Illegal request parameter value with a type=\"multiple\" question: "
                     + paramValue);
                choiceKey = paramName_part3;
                value = TRUE;
              }
            else if ( type.equals(YESNO) )
              {
                if ( paramValue.equals(YES) )
                  value = TRUE;
                else if ( paramValue.equals(NO) )
                  value = FALSE;
                else
                  throw new MchoiceProblemAnswersException
                    ("Illegal request parameter value with a type=\"yesno\" question: "
                     + paramValue);
                choiceKey = paramName_part3;
              }
            else
              throw new MchoiceProblemAnswersException
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
              throw new MchoiceProblemAnswersException
                ("Different types for choices key \"" + choicesKey + "\": " +
                 "\"" + answer.type + "\", \"" + type + "\"");

            // Selected choices, checking:
            if ( answer.selectedChoices.containsKey(choiceKey) )
              throw new MchoiceProblemAnswersException
                ("Dublicate choice-key for choices-key \"" + choicesKey + "\": " + choiceKey);
            if ( type.equals(UNIQUE) &&
                 !answer.selectedChoices.isEmpty() )
              throw new MchoiceProblemAnswersException
                ("Multiple choices selected for the type=\"unique\" question with choices key: " +
                 choicesKey);

            answer.selectedChoices.put(choiceKey, value);
          }
      } 
    this.getLogger().debug(METHOD_NAME + " 2/2");
    return answers;
  }

  /**
   * Reads the answers from the request parameters in <code>request</code> stores them in
   * <code>dataSheet</code>.
   */

  public void requestToDataSheet (Request request, CocoonEnabledDataSheet dataSheet)
    throws MchoiceProblemAnswersException
  {
    final String METHOD_NAME = "requestToDataSheet";
    this.getLogger().debug(METHOD_NAME + " 1/2: request = " + request);
    Map answers = this.parseRequest(request);
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
    this.getLogger().debug(METHOD_NAME + " 2/2: Done");
  }
}
