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

package net.mumie.mathletfactory.transformer;

import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.util.Affine3DDouble;

/**
 *  This class is the base for all 3D transformer, regardless of their
 *  implementation.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public abstract class Canvas3DObjectTransformer extends CanvasObjectTransformer{
  
  /**
   * Returns the Transformation from world draw to the real screen 
   * @see #getScreen2World
   */
  public Affine3DDouble getWorld2Screen () {
    return ((MM3DCanvas)getCanvas()).getWorld2Screen();
  }
  
  /**
   * Returns the Transformation from the real screen to world draw. 
   * @see #getWorld2Screen
   */
  public Affine3DDouble getScreen2World () {
    return ((MM3DCanvas)getCanvas()).getScreen2World();
  }
   
  /**
   *  Returns the 3D world coordinates of the point, in which the object is
   *  picked. Should be something like the center of gravity of the object.
   */
  public abstract double[] getWorldPickPointFromMaster();
  
  /**
   * Transforms the world draw coordinates to screen coordinates using {@link #getWorld2Screen}.
   */  
  protected void world2Screen(double[] worldCoords, double[] javaScreenCoords) {
    getWorld2Screen().applyTo(worldCoords,javaScreenCoords);
  }
 
  /**
   * Transforms the given <code>mathCoords</code> into <code>worldCoords</code>.
   * The implemented default behavior is to simply identify the world 
   * coordinates with the math coordinates. Subclasses, which use non-euclidean 
   * geometry can override this behavior.
   */
  protected void math2World(NumberTuple mathCoords, double[] worldCoords) {
    worldCoords[0] = mathCoords.getEntryRef(1).getDouble();
    worldCoords[1] = mathCoords.getEntryRef(2).getDouble();
    worldCoords[2] = mathCoords.getEntryRef(3).getDouble();
  }
  
  /**
   * Transforms the given <code>worldCoords</code> into <code>mathCoords</code>. 
   * The implemented default behavior is to simply identify the math
   * coordinates with the world coordinates. Subclasses, which use non-euclidean 
   * geometry can override this behavior.
   */
  protected void world2Math(double[] worldCoords, NumberTuple mathCoords){
    mathCoords.getEntryRef(1).setDouble(worldCoords[0]);
    mathCoords.getEntryRef(2).setDouble(worldCoords[1]);
    mathCoords.getEntryRef(3).setDouble(worldCoords[2]);
  }
   
  public abstract void getMathObjectFromScreen(double[] javaScreenCoordinates, NumberTypeDependentIF mathObject);
  
  public abstract void getScreenPointFromMath(NumberTypeDependentIF entity, double[] javaScreenCoordinates);
  
  /**
   *  It may be necessary for a 3D Drawable to do some recalculations (bounds,
   *  etc.) that would be to expensive to do while updating (e.g. dragging a
   *  vector), so this method can be called by updaters to signal, that the
   *  update event cycle has finished.
   */
  public abstract void updateFinished();
}

