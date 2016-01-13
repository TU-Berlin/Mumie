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

import org.apache.excalibur.source.SourceValidity;

/**
 * A {@link SourceValidity} for {@link UserProblemData} objects.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: UserProblemDataValidity.java,v 1.6 2008/05/15 12:11:53 rassy Exp $</code>
 */

public class UserProblemDataValidity
  implements SourceValidity
{
  /**
   * Whether the data includes the common data.
   */

  protected boolean includesCommonData;

  /**
   * Whether the data includes the personalized data.
   */

  protected boolean includesPersonalizedData;

  /**
   * Whether the data includes the answers.
   */

  protected boolean includesAnswers;

  /**
   * Whether the data includes the correction.
   */

  protected boolean includesCorrection;

  /**
   * Whether PPD references are resolved
   */

  protected boolean ppdResolved;

  /**
   * Whether the <code>common/solution</code> unit is removed.
   */

  protected boolean solutionsRemoved;

  /**
   * Last modification time of the problem, or <code>-1</code> if the data do not
   * contain the common or personalized data.
   */

  protected long problemLastModified;

  /**
   * Last modification time of the PPD, or <code>-1</code> if the data do not contain the
   * PPD.
   */

  protected long personalizedDataLastModified;

  /**
   * Last modification time of the answers, or <code>-1</code> if the data do not
   * contain the answers.
   */

  protected long answersLastModified;

  /**
   * Last modification time of the correction, or <code>-1</code> if the data do not
   * contain the correction.
   */

  protected long correctionLastModified;

  /**
   * Get method for {@link #includesCommonData includesCommonData}.
   */

  public boolean includesCommonData ()
  {
    return this.includesCommonData;
  }

  /**
   * Get method for {@link #includesPersonalizedData includesPersonalizedData}.
   */

  public boolean includesPersonalizedData ()
  {
    return this.includesPersonalizedData;
  }

  /**
   * Get method for {@link #includesAnswers includesAnswers}.
   */

  public boolean includesAnswers ()
  {
    return this.includesAnswers;
  }

  /**
   * Get method for {@link #includesCorrection includesCorrection}.
   */

  public boolean includesCorrection ()
  {
    return this.includesCorrection;
  }

  /**
   * Get method for {@link #ppdResolved ppdResolved}.
   */

  public boolean ppdResolved ()
  {
    return this.ppdResolved;
  }

  /**
   * Get method for {@link #solutionsRemoved solutionsRemoved}.
   */

  public boolean solutionsRemoved ()
  {
    return this.solutionsRemoved;
  }

  /**
   * Get method for {@link #problemLastModified problemLastModified}.
   */

  public long getProblemLastModified ()
  {
    return this.problemLastModified;
  }

  /**
   * Get method for {@link #personalizedDataLastModified personalizedDataLastModified}.
   */

  public long getPersonalizedDataLastModified ()
  {
    return this.personalizedDataLastModified;
  }

  /**
   * Get method for {@link #answersLastModified answersLastModified}.
   */

  public long getAnswersLastModified ()
  {
    return this.answersLastModified;
  }

  /**
   * Get method for {@link #correctionLastModified correctionLastModified}.
   */

  public long getCorrectionLastModified ()
  {
    return this.correctionLastModified;
  }

  /**
   * Always returns {@link SourceValidity#UNKNOWN}, so the validity check is delegated to
   * {@link #isValid(SourceValidity) isValid(SourceValidity)}.
   */

  public int isValid ()
  {
    return SourceValidity.UNKNOWN;
  }

  /**
   * Checks if the problem data object is still valid.
   */

  public int isValid (SourceValidity newerValidity)
  {
    if ( newerValidity instanceof UserProblemDataValidity )
      {
        UserProblemDataValidity validity = (UserProblemDataValidity)newerValidity;

        if ( this.includesCommonData != validity.includesCommonData() ||
             this.includesPersonalizedData != validity.includesPersonalizedData() ||
             this.includesAnswers != validity.includesAnswers() ||
             this.includesCorrection != validity.includesCorrection() ||
             this.ppdResolved != validity.ppdResolved() ||
             this.solutionsRemoved != validity.solutionsRemoved() )
          {
            // The cached resource is invalid because it does not contain the
            // same parts of the data
            return SourceValidity.INVALID;
          }

        if ( this.includesCommonData )
          {
            if ( this.problemLastModified != validity.getProblemLastModified() )
              {
                // The cached resource is invalid because the problem has changed
                return SourceValidity.INVALID;
              }
          }

        if ( this.includesPersonalizedData )
          {
            if ( this.personalizedDataLastModified != validity.getPersonalizedDataLastModified() )
              // The cached resource is invalid because the ppd have changed
              return SourceValidity.INVALID;
          }

        if ( this.includesAnswers )
          {
            if ( this.answersLastModified != validity.getAnswersLastModified() )
              {
                // The cached resource is invalid because the answers have changed
                return SourceValidity.INVALID;
              }
          }

        if ( this.includesCorrection )
          {
            if ( this.correctionLastModified != validity.getCorrectionLastModified() )
              {
                // The cached resource is invalid because the correction has changed
                return SourceValidity.INVALID;
              }
          }

        return SourceValidity.VALID;
      }

    return SourceValidity.INVALID;
  }

  /**
   * Creates a new <code>UserProblemDataValidity</code> object with the specified flags and
   * timestamps.
   */

  public UserProblemDataValidity (boolean includesCommonData,
                                  boolean includesPersonalizedData,
                                  boolean includesAnswers,
                                  boolean includesCorrection,
                                  boolean ppdResolved,
                                  boolean solutionsRemoved,
                                  long problemLastModified,
                                  long personalizedDataLastModified,
                                  long answersLastModified,
                                  long correctionLastModified)
  {
    this.includesCommonData = includesCommonData;
    this.includesPersonalizedData = includesPersonalizedData;
    this.includesAnswers = includesAnswers;
    this.includesCorrection = includesCorrection;
    this.ppdResolved = ppdResolved;
    this.solutionsRemoved = solutionsRemoved;
    this.problemLastModified = problemLastModified;
    this.personalizedDataLastModified = personalizedDataLastModified;
    this.answersLastModified = answersLastModified;
    this.correctionLastModified = correctionLastModified;
  }

  /**
   *   Returns a string representation of this object. It has the form
   *   <pre>
   *      this.getClass().getName() +
   *      " " + {@link #includesCommonData this.includesCommonData} +
   *      " " + {@link #includesPersonalizedData this.includesPersonalizedData} +
   *      " " + {@link #includesAnswers this.includesAnswers} +
   *      " " + {@link #includesCorrection this.includesCorrection} +
   *      " " + {@link #problemLastModified this.problemLastModified} +
   *      " " + {@link #personalizedDataLastModified this.personalizedDataLastModified} +
   *      " " + {@link #answersLastModified this.answersLastModified} +
   *      " " + {@link #correctionLastModified this.correctionLastModified}</pre>
   */

  public String toString ()
  {
    final String SEP = " ";
    return
      this.getClass().getName() +
      SEP + includesCommonData +
      SEP + includesPersonalizedData +
      SEP + includesAnswers +
      SEP + includesCorrection +
      SEP + this.problemLastModified +
      SEP + this.personalizedDataLastModified +
      SEP + this.answersLastModified +
      SEP + this.correctionLastModified;
  }

}
