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

package net.mumie.mathletfactory.display.noc.geom;

import java.awt.FlowLayout;

import javax.swing.JLabel;

import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle;
import net.mumie.mathletfactory.mmobject.number.MMDouble;
import net.mumie.mathletfactory.transformer.noc.AngleTransformer;

/**
 *  This class renders the symbolical representation of an
 *  {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DAngle}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class MMAnglePanel extends MMCompoundPanel {

  private MMDouble m_degree;
  private MMDouble m_radian;  

  /**
    * Class constructor.
    */
  public MMAnglePanel(MMObjectIF masterObject, AngleTransformer transformer) {
    super(masterObject, transformer);
    m_degree = new MMDouble(((MMAffine2DAngle)masterObject).getDegrees());
    m_radian = new MMDouble(((MMAffine2DAngle)masterObject).getRadians());
    getViewerComponent().setLayout(new FlowLayout());
    add(new JLabel("deg: "));
    addMMPanel((MMPanel) add(m_degree.getAsContainerContent()));
    add(new JLabel(",   rad: "));
    addMMPanel((MMPanel) add(m_radian.getAsContainerContent()));
  }

  /**
   * Sets the measure af the angle in degrees.
   */
  public void setDegree(double degree) {
    m_degree.set(new MDouble(degree));
  }

  /**
   * Sets the measure af the angle in radians.
   */
  public void setRadian(double radian) {
    m_radian.set(new MDouble(radian));
  }
}
