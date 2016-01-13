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

package net.mumie.corrector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.mumie.cocoon.notions.TimeFormat;
import net.mumie.cocoon.util.CocoonEnabledDataSheet;
import net.mumie.cocoon.util.ProblemCorrectionException;
import net.mumie.cocoon.util.ProblemCorrector;
import net.mumie.japs.datasheet.DataSheetException;

/**
 * Generic corrector for traditional problems.
 *
 * @author Tilman Rassy
 *   <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 *   (org/users/usr_tilman.meta.xml)
 * @version
 *   <code>$Id: GenericTraditionalProblemCorrector.src.java,v 1.3 2007/07/16 11:23:27 grudzin Exp $</code>
 *
 * @mm.type java_class
 * @mm.category corrector
 * @mm.section system/problem
 * @mm.copyright Copyright (c) 2005 -2007, Technische Universitaet Berlin
 * @mm.buildClasspath mumie-japs-for-mmcdk.jar
 * @mm.buildClasspath mumie-japs-datasheet.jar
 * @mm.buildClasspath excalibur-xmlutil-2.1.jar
 * @mm.description Generischer Korrektor fuer schriftliche Aufgaben
 * @mm.changelog Erste Version
 */

public class GenericTraditionalProblemCorrector
  implements ProblemCorrector
{
  /**
   * The datasheet path <code>"user/meta/score"</code> as a constant.
   */

  static final String USER_META_SCORE = "user/meta/score";

  /**
   * The datasheet path <code>"user/meta/correction-log"</code> as a constant.
   */

  static final String USER_META_CORRECTIONLOG = "user/meta/correction-log";

  /**
   * Time format for the log messages.
   */

  protected DateFormat timeFormat = new SimpleDateFormat(TimeFormat.PRECISE);

  /**
   * Creates a formal correction datasheet. Since the problem is manually corrected, 
   */

  public void correct (CocoonEnabledDataSheet input,
                       CocoonEnabledDataSheet output)
    throws ProblemCorrectionException 
  {
    try
      {
        float score = input.getAsFloat(USER_META_SCORE, -1);
        String oldLogMessage = input.getAsString(USER_META_CORRECTIONLOG, "");
        String time = this.timeFormat.format(new Date(System.currentTimeMillis()));
        String logMessageStart = oldLogMessage + time + " GenericTraditionalProblemCorrector: ";

        // If a score exists, copy it to the output:
        if ( score >= 0 )
          {
            output.put(USER_META_SCORE, score);
            output.put
              (USER_META_CORRECTIONLOG, logMessageStart + "Copied score" + "\n");
          }
        else
          {
            output.put
              (USER_META_CORRECTIONLOG, logMessageStart + "No score found" + "\n");
          }
      }
    catch (Exception exception)
      {
        throw new ProblemCorrectionException(exception);
      }
  }
}
