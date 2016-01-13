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

package net.mumie.mathletfactory.math.util.animate;

/**
 * A Parameterized circle.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class ParameterizedCircle extends ParametricFunction {

//  private final double startx, starty;
  private final double stepx, stepy;

  public ParameterizedCircle(Class numberClass, 
    double startx, double starty, double stepx, double stepy) {
    super(numberClass, 2);
//    this.startx = startx;
//    this.starty = starty;
    this.stepx = stepx;
    this.stepy = stepy;
  }
  
  public void evaluate(double xin, double[] yout) {
    yout[0] = Math.cos(stepx * xin);
    yout[1] = Math.sin(stepy * xin);
  }
}
