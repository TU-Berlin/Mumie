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

package net.mumie.mathletfactory.display.g2d;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

import net.mumie.mathletfactory.display.DisplayProperties;

/**
 * Contains methods to draw brackets in a
 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class G2DBracketDrawable extends G2DDrawable {

  /**
   * defines the type of the bracket (left bracket)
   */
  public final static boolean LEFT = true;

  /**
   * defines the type of the bracket (right bracket)
   */
  public final static boolean RIGHT = false;

  /**
   * defines the form of the bracket (round bracket)
   */
  public final static boolean ROUND = true;

  /**
   * defines the form of the bracket (square bracket)
   */
  public final static boolean SQUARE = false;

  private GeneralPath m_bracket;
  private double[] m_point = new double[2];
  private boolean m_type; // left ("[") = true, right ("]") = false
  private boolean m_visible = true;
  private boolean m_form;
  // square brackets ("[...]") = false, round brackets ("(...)") = true

  /**
   * Sets the point where the bracket is drawn, the type (left or right bracket), 
   * the form (round or square bracket) and if the bracket should be visible or not
   * (if an interval has a boundary which is infinite it might be interesting to let the bracket 
   * be invisible).
   */
  public void setProps(
    double[] point,
    boolean type,
    boolean form,
    boolean visible) {
    setPoint(point);
    m_type = type;
    m_form = form;
    m_visible = visible;
  }

  /**Sets the point where the bracket is drawn. */
  public void setPoint(double[] point){
    m_point[0] = point[0];
    m_point[1] = point[1];
  }

  /**Returns a reference to the point where the bracket is drawn. */
  public double[] getPoint(){
    return m_point;
  }
  
  /**Sets the form (round or square bracket). */
  public void setForm(boolean isRound){
    m_form = isRound;
  }

  /**Sets the type (left or right bracket). */
  public void setType(boolean isLeft){
    m_type = isLeft;
  } 

  protected Shape getShape() {
    if(m_bracket == null)
      render(null);
    return m_bracket;
  }

  public void render(DisplayProperties properties) {
    if (m_visible) {
      if (!m_form) { // square brackets
        Line2D.Double line =
          new Line2D.Double(
            m_point[0],
            m_point[1] + 10.0,
            m_point[0],
            m_point[1] - 10.0);
        if (m_type) { // left bracket: [
          m_bracket = new GeneralPath();
          m_bracket.append(line, true);
          m_bracket.append(
            new Line2D.Double(
              m_point[0],
              m_point[1] + 10.0,
              m_point[0] + 5.0,
              m_point[1] + 10.0),
            false);
          m_bracket.append(
            new Line2D.Double(
              m_point[0],
              m_point[1] - 10.0,
              m_point[0] + 5.0,
              m_point[1] - 10.0),
            false);
        }
        else { // right bracket: ]
          m_bracket = new GeneralPath();
          m_bracket.append(line, true);
          m_bracket.append(line, true);
          m_bracket.append(
            new Line2D.Double(
              m_point[0],
              m_point[1] + 10.0,
              m_point[0] - 5.0,
              m_point[1] + 10.0),
            false);
          m_bracket.append(
            new Line2D.Double(
              m_point[0],
              m_point[1] - 10.0,
              m_point[0] - 5.0,
              m_point[1] - 10.0),
            false);
        }
      }
      else { // round brackets
        Arc2D.Double arc = new Arc2D.Double();
        if (m_type) { // left bracket: (
          arc.setArcByCenter(m_point[0]+15.0, m_point[1], 15.0, 135.0, 90.0, Arc2D.OPEN);
        }
        else { // right bracket: )
          arc.setArcByCenter(m_point[0]-15.0, m_point[1], 15.0, -45.0, 90.0, Arc2D.OPEN);
        }
        m_bracket = new GeneralPath();
        m_bracket.append(arc, true);
      }
    }
    else
      m_bracket = new GeneralPath();
  }

  public boolean isAtScreenLocation(int xOnScreen, int yOnScreen) {
    return m_bracket.contains(xOnScreen, yOnScreen);
  }
}
