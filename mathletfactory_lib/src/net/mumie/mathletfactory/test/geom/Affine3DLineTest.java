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

package net.mumie.mathletfactory.test.geom;

import net.mumie.mathletfactory.action.handler.Affine3DKeyboardTranslateHandler;
import net.mumie.mathletfactory.action.handler.Affine3DMouseTranslateHandler;
import net.mumie.mathletfactory.appletskeleton.j3d.SingleJ3DCanvasApplet;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultR3;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLine;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 *  Test of {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLine}.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class Affine3DLineTest extends SingleJ3DCanvasApplet {

  public void init() {
    super.init();
    initializeObjects();
  }

  public void initializeObjects() {
    getCanvas3D().setProjectionType(MMJ3DCanvas.PERSPECTIVE_PROJECTION);

    MMAffine3DLine l1, l2;
    Affine3DMouseTranslateHandler amth1 =
      new Affine3DMouseTranslateHandler(m_canvas);
    Affine3DMouseTranslateHandler amth2 =
      new Affine3DMouseTranslateHandler(m_canvas);
    Affine3DKeyboardTranslateHandler kth1 =
      new Affine3DKeyboardTranslateHandler(m_canvas);
    Affine3DKeyboardTranslateHandler kth2 =
      new Affine3DKeyboardTranslateHandler(m_canvas);

    l1 =
      new MMAffine3DLine(
        new MMAffine3DPoint(MDouble.class, 0, 0, 0),
        new MMAffine3DPoint(MDouble.class, 0.2, 0.2, 0.2));
    l2 =
      new MMAffine3DLine(
        new MMAffine3DPoint(MDouble.class, 0.3, -0.3, 0.3),
        new MMAffine3DPoint(MDouble.class, 0.2, 0.2, 0.2));

    l1.addHandler(kth1);
    l1.addHandler(amth1);
    l1.setLabel("l1");
    l2.addHandler(kth2);
    l2.addHandler(amth2);
    l2.setLabel("l2");
    l1.setEditable(true);

    // for orientation:
    MMDefaultR3 r3 = new MMDefaultR3(MDouble.class);
    m_canvas.addObject(r3);

    //p.addHandler(akth);
    m_canvas.addObject(l1);
    m_canvas.addObject(l2);
    addMMObjectAsContainerContent(l1);
    insertLineBreak();
    addMMObjectAsContainerContent(l2);
  }

  public static void main(String[] args) {
    java.util.logging.Logger.getLogger("").setLevel(
      java.util.logging.Level.SEVERE);

    Affine3DLineTest myApplet = new Affine3DLineTest();
    BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600);
    myApplet.init();
    myApplet.start();
    f.pack();
    f.setVisible(true);
  }
}
