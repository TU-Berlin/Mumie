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

/**
 * Corrects and marks the student's answers to a problem.
 *
 * @author Tilman Rassy <a href="mailto:rassy@math.tu-berlin.de">rassy@math.tu-berlin.de</a>
 * @version <code>$Id: ProblemCorrector.java,v 1.7 2007/07/11 15:38:53 grudzin Exp $</code>
 */

public interface ProblemCorrector
{

/**
 * Corrects and marks the student's answers to a problem. The needed problem data and the
 * users answers are provided by <code>inputDataSheet</code>, whereas
 * <code>outputDataSheet</code> should recieve the result of the correction/marking.
 *
 * @param inputDataSheet the datasheet containing the problem data and the users answers.
 * @param outputDataSheet the datasheet that recieves the result of the correction/marking.
 */

  public void correct (CocoonEnabledDataSheet inputDataSheet,
                       CocoonEnabledDataSheet outputDataSheet)
    throws ProblemCorrectionException;
}
