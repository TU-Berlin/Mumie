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

package net.mumie.mathletfactory.transformer.g2d;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPolygonDrawable;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DQuadric;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DQuadric}.
 * 
 * @author Liu
 * @mm.docstatus finished
 */
public class Affine2DQuadricTransformer
  extends Affine2DDefaultTransformer {

  private double[][] m_worldP;

  private double[][] m_screenP;

  private G2DPolygonDrawable[] m_quadricDrawable =
    { new G2DPolygonDrawable(), new G2DPolygonDrawable()};

  private G2DPointDrawable m_pointDrawable =
    new G2DPointDrawable();

  //private G2DLineDrawable m_lineDrawable = new G2DLineDrawable();

  private boolean[] m_quadricVisible = { false, false };

  //private boolean m_lineVisible = false;

  private boolean m_pointVisible = false;

  //private LineDisplayProperties ll;
  //private PointDisplayProperties cc;

  public Affine2DQuadricTransformer() {
    super();
    m_activeDrawable = m_quadricDrawable[0];
  }

  public void initialize(MMObjectIF masterObject) {

    super.initialize(masterObject);

    if (getMathMasterObject()
      .getQuadricType(getMathMasterObject().getMatrix())
      .equals(ResourceManager.getMessage("hyperbola"))
      || getMathMasterObject().getQuadricType(
        getMathMasterObject().getMatrix()).equals(
        		ResourceManager.getMessage("pair_of_intersecting_lines"))
      || getMathMasterObject().getQuadricType(
        getMathMasterObject().getMatrix()).equals(
        		ResourceManager.getMessage("pair_of_distinct_parallel_lines"))) {

      m_worldP =
        new double[getMathMasterObject().getValuesFirstComponent().length
          + getMathMasterObject().getValuesSecondComponent().length][];
      m_screenP =
        new double[getMathMasterObject().getValuesFirstComponent().length
          + getMathMasterObject().getValuesSecondComponent().length][];
      for (int i = 0;
        i
          < getMathMasterObject().getValuesFirstComponent().length
            + getMathMasterObject().getValuesSecondComponent().length;
        i++) {
        m_worldP[i] = new double[2];
        m_screenP[i] = new double[2];
      }
      m_quadricDrawable[0].initLength(
        getMathMasterObject().getValuesFirstComponent().length);
      m_quadricDrawable[1].initLength(
        getMathMasterObject().getValuesSecondComponent().length);
               m_activeDrawable = m_quadricDrawable[0];
      		   m_additionalProperties= new DisplayProperties[]{new DisplayProperties()};
      		   m_additionalDrawables = new G2DPolygonDrawable[]{m_quadricDrawable[1]};
    }

    else {
      m_worldP =
        new double[getMathMasterObject().getValuesFirstComponent().length][];
      m_screenP =
        new double[getMathMasterObject().getValuesFirstComponent().length][];
      for (int i = 0;
        i < getMathMasterObject().getValuesFirstComponent().length;
        i++) {
        m_worldP[i] = new double[2];
        m_screenP[i] = new double[2];
      }
      m_quadricDrawable[0].initLength(
        getMathMasterObject().getValuesFirstComponent().length);
      m_quadricDrawable[1].initLength(0);

    }
  }



  private void initVisibleFlags() {

    if (getMathMasterObject()
      .getQuadricType(getMathMasterObject().getMatrix())
      .equals(ResourceManager.getMessage("point"))) {
      m_quadricVisible[0] = false;
      m_quadricVisible[1] = false;
      //m_lineVisible = false;
      m_pointVisible = true;
    }
    //		else if(getMathMasterObject().getQuadricType(getMathMasterObject().getMatrix()).equals("pair of coincident parallel lines")){
    //		    m_quadricVisible[0] = false;
    //            m_quadricVisible[1] = false;
    //            //m_lineVisible = true;
    //            m_pointVisible = false;
    //		}
    else if (
      getMathMasterObject().getQuadricType(
        getMathMasterObject().getMatrix()).equals(
        		ResourceManager.getMessage("hyperbola"))
        || getMathMasterObject().getQuadricType(
          getMathMasterObject().getMatrix()).equals(
          		ResourceManager.getMessage("pair_of_distinct_parallel_lines"))
        || getMathMasterObject().getQuadricType(
          getMathMasterObject().getMatrix()).equals(
          		ResourceManager.getMessage("pair_of_intersecting_lines"))) {
      m_quadricVisible[0] = true;
      m_quadricVisible[1] = true;
      //m_lineVisible = false;
      m_pointVisible = false;
    }
    else {
      m_quadricVisible[0] = true;
      m_quadricVisible[1] = false;
      //m_lineVisible = false;
      m_pointVisible = false;
    }
  }

  public void synchronizeMath2Screen() {
    int first = getMathMasterObject().getValuesFirstComponent().length;
    int second = getMathMasterObject().getValuesSecondComponent().length;
//    if (getMathMasterObject().getVersion() > m_version) {
//      m_pointDrawable = new G2DPointDrawable();
//      m_quadricDrawable[0] = new G2DPolygonDrawable();
//      m_quadricDrawable[1] = new G2DPolygonDrawable();
//    }
    initVisibleFlags();
    if (masterIsDegenerated()) {
      m_activeDrawable = m_pointDrawable;
      math2World(getMathMasterObject().getPoint(), m_worldP[0]);
      world2Screen(m_worldP[0], m_screenP[0]);
      getPointDrawable().setPoint(m_screenP[0][0], m_screenP[0][1]);
    }
    else {
      m_quadricDrawable[0].initLength(first);
      for (int i = 0; i < first; i++) {
        math2World(
          new Affine2DPoint(
            MDouble.class,
            getMathMasterObject()
              .getValuesFirstComponent()[i]
              .getEntryRef(1)
              .getDouble(),
            getMathMasterObject()
              .getValuesFirstComponent()[i]
              .getEntryRef(2)
              .getDouble()),
          m_worldP[i]);
        world2Screen(m_worldP[i], m_screenP[i]);
        m_quadricDrawable[0].setPoint(i, m_screenP[i][0], m_screenP[i][1]);
        //System.out.println(m_worldP[i][0]+"   ,  "+m_worldP[i][1]);
         }
	  m_quadricDrawable[0].setOpened();
	  m_activeDrawable = m_quadricDrawable[0];
	  
    if (m_quadricVisible[1]) {
      m_quadricDrawable[1].initLength(second);
      for (int i = first; i < first + second; i++) {
        math2World(
          new Affine2DPoint(
            MDouble.class,
            getMathMasterObject().getValuesSecondComponent()[i
              - first].getEntryRef(1).getDouble(),
            getMathMasterObject().getValuesSecondComponent()[i
              - first].getEntryRef(2).getDouble()),
          m_worldP[i - first]);
        world2Screen(m_worldP[i - first], m_screenP[i - first]);
        m_quadricDrawable[1].setPoint(
          i - first,
          m_screenP[i - first][0],
          m_screenP[i - first][1]);
      }
	  m_quadricDrawable[1].setOpened();
    }
    else {
      m_quadricDrawable[1].initLength(2);
      m_quadricDrawable[1].setPoint(1, 0, 0);
      m_quadricDrawable[1].setPoint(0, 1, 0);
      m_quadricDrawable[1].setValid(1, false);
    }
    m_additionalProperties = new DisplayProperties[]{new DisplayProperties()};
    m_additionalDrawables =
     new G2DPolygonDrawable[] { m_quadricDrawable[1] };
    }
  }

  public void synchronizeWorld2Screen() {
	synchronizeMath2Screen();
//    int first = getMathMasterObject().getValuesFirstComponent().length;
//    int second = getMathMasterObject().getValuesSecondComponent().length;
////    if (getMathMasterObject().getVersion() > m_version) {
////      m_pointDrawable = new G2DPointDrawable();
////      m_quadricDrawable[0] = new G2DPolygonDrawable();
////      m_quadricDrawable[1] = new G2DPolygonDrawable();
////    }
//    initVisibleFlags();
//    if (masterIsDegenerated()) {
//      world2Screen(m_worldP[0], m_screenP[0]);
//      getPointDrawable().setPoint(m_screenP[0][0], m_screenP[0][1]);
//    }
//    else {
//      m_quadricDrawable[0].initLength(first);
//      for (int i = 0; i < first; i++) {
//		math2World(
//				  new Affine2DPoint(
//					MDouble.class,
//					getMathMasterObject()
//					  .getValuesFirstComponent()[i]
//					  .getEntryRef(1)
//					  .getDouble(),
//					getMathMasterObject()
//					  .getValuesFirstComponent()[i]
//					  .getEntryRef(2)
//					  .getDouble()),
//				  m_worldP[i]);
//        world2Screen(m_worldP[i], m_screenP[i]);
//        m_quadricDrawable[0].setPoint(i, m_screenP[i][0], m_screenP[i][1]);
//		System.out.println("���    "+m_worldP[i][0]+"   ,  "+m_worldP[i][1]);
//      }
//	  m_quadricDrawable[0].setOpened(); 
//	  m_activeDrawable = m_quadricDrawable[0];
//    if (m_quadricVisible[1]) {
//      m_quadricDrawable[1].initLength(second);
//      for (int i = first; i < first + second; i++) {
//		math2World(
//				  new Affine2DPoint(
//					MDouble.class,
//					getMathMasterObject().getValuesSecondComponent()[i
//					  - first].getEntryRef(1).getDouble(),
//					getMathMasterObject().getValuesSecondComponent()[i
//					  - first].getEntryRef(2).getDouble()),
//				  m_worldP[i - first]);
//        world2Screen(m_worldP[i - first], m_screenP[i - first]);
//        m_quadricDrawable[1].setPoint(
//          i - first,
//          m_screenP[i - first][0],
//          m_screenP[i - first][1]);
//      }
//	  m_quadricDrawable[1].setOpened();
//    }
//    else {
//      m_quadricDrawable[1].initLength(2);
//      m_quadricDrawable[1].setPoint(1, 0, 0);
//      m_quadricDrawable[1].setPoint(0, 1, 0);
//      m_quadricDrawable[1].setValid(1, false);
//    }
//    m_additionalProperties = new DisplayProperties[] { new DisplayProperties()};
//    m_additionalDrawables =
//      new G2DPolygonDrawable[] {m_quadricDrawable[1] };
//    }
  }
  
  boolean isPointVisible() {
  	return m_pointVisible;
  }

  private G2DPointDrawable getPointDrawable() {
    return (G2DPointDrawable) m_pointDrawable;
  }

  private MMAffine2DQuadric getMathMasterObject() {
    return (MMAffine2DQuadric) m_masterMMObject;
  }

  protected boolean masterIsDegenerated() {
    return getMathMasterObject().isDegenerated();
  }

}
