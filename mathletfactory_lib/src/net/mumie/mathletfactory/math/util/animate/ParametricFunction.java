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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.EventObject;

import javax.swing.Timer;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverRAdapter;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.NumberFactory;

/**
 * Parametric function (e.g. time depended) that takes values from R in [0,1] and
 * evaluates to R^N for a given dimension N
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public abstract class ParametricFunction extends VectorFunctionOverRAdapter {
  
  /** Constructs this parametric function for the given number class and dimension. */
  public ParametricFunction(Class entryClass, int dimension) {
    super(entryClass, dimension);
  }
 
  /** To be implemented by the concrete function class. Note that xin should be in [0,1]. */
  public abstract void evaluate(double xin, double[] yout);

  /**
   * The event objects created by the {@link ParametricTimer}. 
   *  
   * @author Amsel
   * @mm.docstatus finished
   */
  public static class ParametricEvent extends EventObject {
    private MNumber param;
    private NumberTuple y;
    
    /** 
     * Creates this event object with the given event source, parameter in [0,1] and the associated vector 
     * value (determined by the parametric function of the timer). 
     */
    public ParametricEvent(Object source, MNumber param, NumberTuple y) {
      super(source);
      this.param = param;
      this.y = y;
    }
    
    /** Returns the current Parameter in [0,1] */
    public MNumber getParam() {
      return param;
    }  
      
    /** Returns the i-th component of the vector <code>y</code>. */
    public MNumber getY_i(int index) {
      return y.getEntry(index);
    }
    
    /** Returns the vector <code>y</code>. */
    public NumberTuple getY() {
      return y;
    }
  }
  
  /** Implemented by all classes that receive parametric events to implement an animation. */
  public static interface ParametricListener extends EventListener {
    /** Performs a step depending on the given parametric event. */
    public void step(ParametricEvent event);
  }
  
  /** 
   * This class creates and fires parametric events for the execution of animation steps. 
   * 
   * @author Amsel
   * @mm.docstatus finished
   */
  public static class ParametricTimer extends Timer implements ActionListener {
//    private final int timeInterval;
    private final MNumber stepLength;
    private final ParametricListener listener;
    private final ParametricFunction function;
    private MNumber current;
    private int currentStep;	// steps executed so far
    private int nsteps;
    
    /**
     *  Creates a parametric timer that fires for the given interval <code>steps</code> number of events
     *  at the specified listener, giving them the function values of <code>function</code>.
     */
    public ParametricTimer(int timeInterval, int steps, 
        ParametricListener listener, ParametricFunction function) {
      super(timeInterval/(steps == 0 ? 1 : steps), null);
      addActionListener(this);
      setInitialDelay(0);
//      this.timeInterval = timeInterval;
      this.stepLength = NumberFactory.newInstance(function.getNumberClass());
      this.stepLength.setDouble(1d/(double)(steps-1));
      this.nsteps = steps;
      this.currentStep = 0;
      this.listener = listener;
      this.function = function;
    }
    
    /** Starts the timer. */
    public void start() {
      if (isRunning()) {
        return;
      }
      current = NumberFactory.newInstance(function.getNumberClass());
      super.start();
    }
    
    /** Fires a new {@link ParametricEvent}. */
    public void actionPerformed(ActionEvent event) {
      currentStep++;
      NumberTuple tuple = new NumberTuple(function.getNumberClass(), function.getDimension());
      function.evaluate(current, tuple);
      if (currentStep >= nsteps) {
      	current.setDouble(1);	// make sure current is exactly 1, so listener.step can determinate its last call
      	stop();
      }
      listener.step(new ParametricEvent(this, current, tuple));
      current.add(stepLength);
    }
  }
  
  /** 
   * Runs through this function with a parametric timer in the given time interval and the given number
   * of steps, firing parametric events at the given listener.
   */
  public void runThrough(int timeInterval, int steps, ParametricListener listener) {
    new ParametricTimer(timeInterval, steps, listener, this).start();
  }
}
