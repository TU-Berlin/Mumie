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

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.g2d.G2DBracketDrawable;
import net.mumie.mathletfactory.display.g2d.G2DLineDrawable;
import net.mumie.mathletfactory.display.g2d.G2DPointDrawable;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.math.util.Graphics2DHelper;
import net.mumie.mathletfactory.mmobject.set.MMInterval;
import net.mumie.mathletfactory.transformer.Affine2DDefaultTransformer;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.set.MMInterval}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class IntervalTransformer extends Affine2DDefaultTransformer {

  private double[] m_initInWorld = new double[2];
  private double[] m_initOnScreen = new double[2];
  private double[] m_endInWorld = new double[2];
  private double[] m_endOnScreen = new double[2];
  private DisplayProperties bracketProp = new DisplayProperties();

  public IntervalTransformer() {
    bracketProp.setFilled(false);
    m_allDrawables =
      new CanvasDrawable[] {
        new G2DPointDrawable(),
        new G2DLineDrawable()};
    m_additionalDrawables =
      new CanvasDrawable[] {
        new G2DBracketDrawable(),
        new G2DBracketDrawable()};
    m_additionalProperties =
      new DisplayProperties[] { bracketProp, bracketProp };
  }

  public void synchronizeMath2Screen() {
    if (getRealMasterObject().isPoint()) { //[a, a]
      m_activeDrawable = m_allDrawables[0];
      math2World(
        new Affine2DPoint(
          getRealMasterObject().getNumberClass(),
          getRealMasterObject().getLowerBoundaryVal().getDouble(),
          0.0),
        m_initInWorld);
      world2Screen(m_initInWorld, m_initOnScreen);
      getPointDrawable().setPoint(m_initOnScreen[0], m_initOnScreen[1]);
      getLeftBracketDrawable().setProps(
        m_initOnScreen,
        !getRealMasterObject().getLowerBoundaryType()
          || (getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm()),
        getRealMasterObject().getLowerBoundaryType()
          && getRealMasterObject().getForm(),
        false);
      getRightBracketDrawable().setProps(
        m_endOnScreen,
        getRealMasterObject().getUpperBoundaryType()
          && !(getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm()),
        getRealMasterObject().getUpperBoundaryType()
          && getRealMasterObject().getForm(),
        false);
    }
    else {
      m_activeDrawable = m_allDrawables[1];
      if (!getRealMasterObject().isLowerInfinite()
        && !getRealMasterObject().isUpperInfinite()) { // (a, b)
        math2World(
          new Affine2DPoint(
            getRealMasterObject().getNumberClass(),
            getRealMasterObject().getLowerBoundaryVal().getDouble(),
            0.0),
          m_initInWorld);
        math2World(
          new Affine2DPoint(
            getRealMasterObject().getNumberClass(),
            getRealMasterObject().getUpperBoundaryVal().getDouble(),
            0.0),
          m_endInWorld);
        world2Screen(m_initInWorld, m_initOnScreen);
        world2Screen(m_endInWorld, m_endOnScreen);
        //set bracket left and right:
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          true);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          true);
      }
      else if (
        !getRealMasterObject().isLowerInfinite()
          && getRealMasterObject().isUpperInfinite()) { // (a, infty)
        math2World(
          new Affine2DPoint(
            getRealMasterObject().getNumberClass(),
            getRealMasterObject().getLowerBoundaryVal().getDouble(),
            0.0),
          m_initInWorld);
        math2World(
          new Affine2DPoint(
            getRealMasterObject().getNumberClass(),
            getRealMasterObject().getLowerBoundaryVal().getDouble() + 1.0,
            0.0),
          m_endInWorld);
        world2Screen(m_initInWorld, m_initOnScreen);
        world2Screen(m_endInWorld, m_endOnScreen);
        //set bracket left:
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          true);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          false);
      }
      else if (
        getRealMasterObject().isLowerInfinite()
          && !getRealMasterObject().isUpperInfinite()) { // (-infty, b)
        math2World(
          new Affine2DPoint(
            getRealMasterObject().getNumberClass(),
            getRealMasterObject().getUpperBoundaryVal().getDouble() - 1.0,
            0.0),
          m_initInWorld);
        math2World(
          new Affine2DPoint(
            getRealMasterObject().getNumberClass(),
            getRealMasterObject().getUpperBoundaryVal().getDouble(),
            0.0),
          m_endInWorld);
        world2Screen(m_initInWorld, m_initOnScreen);
        world2Screen(m_endInWorld, m_endOnScreen);
        //set bracket right:
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          true);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          false);
      }
      else { //(-infty, infty)
        math2World(
          new Affine2DPoint(getRealMasterObject().getNumberClass(), -1.0, 0.0),
          m_initInWorld);
        math2World(
          new Affine2DPoint(getRealMasterObject().getNumberClass(), 1.0, 0.0),
          m_endInWorld);
        world2Screen(m_initInWorld, m_initOnScreen);
        world2Screen(m_endInWorld, m_endOnScreen);
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          false);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          false);
      }
      getIntervalDrawable().setPoints(
        m_initOnScreen[0],
        m_initOnScreen[1],
        m_endOnScreen[0],
        m_endOnScreen[1]);
    }
    synchronizeWorld2Screen();
  }

  public void synchronizeWorld2Screen() {
    if (getRealMasterObject().isPoint()) {
      world2Screen(m_initInWorld, m_initOnScreen);
      getPointDrawable().setPoint(m_initOnScreen);
      getLeftBracketDrawable().setProps(
        m_initOnScreen,
        !getRealMasterObject().getLowerBoundaryType()
          || (getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm()),
        getRealMasterObject().getLowerBoundaryType()
          && getRealMasterObject().getForm(),
        false);
      getRightBracketDrawable().setProps(
        m_endOnScreen,
        getRealMasterObject().getUpperBoundaryType()
          && !(getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm()),
        getRealMasterObject().getUpperBoundaryType()
          && getRealMasterObject().getForm(),
        false);
    }
    else {
      world2Screen(m_initInWorld, m_initOnScreen);
      world2Screen(m_endInWorld, m_endOnScreen);
      if (!(getRealMasterObject().isLowerInfinite())
        && !(getRealMasterObject().isUpperInfinite())) { // (a, b)
        getIntervalDrawable().setPoints(
          m_initOnScreen[0],
          m_initOnScreen[1],
          m_endOnScreen[0],
          m_endOnScreen[1]);
        //set bracket left and right:
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          true);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          true);
      }
      else if (
        !getRealMasterObject().isLowerInfinite()
          && getRealMasterObject().isUpperInfinite()) { // (a, infty)
        double[] coord = new double[4];
        MMCanvas canvas = getMasterAsCanvasObject().getCanvas();
        double[] rect = new double[4];
        rect =
          new double[] {
            m_initOnScreen[0],
            canvas.getBounds().getY(),
            canvas.getWidth(),
            Math.abs(canvas.getY() - m_initOnScreen[1])};
        int res =
          Graphics2DHelper.lineRectIntersection2D(
            new double[] {
              m_initOnScreen[0],
              m_initOnScreen[1],
              m_endOnScreen[0],
              m_endOnScreen[1] },
            rect,
            coord);
        getIntervalDrawable().setPoints(coord[0], coord[1], coord[2], coord[3]);
        //set bracket left:
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          true);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          false);
      }
      else if (
        getRealMasterObject().isLowerInfinite()
          && !getRealMasterObject().isUpperInfinite()) { // (-infty, b)
        double[] coord = new double[4];
        MMCanvas canvas = getMasterAsCanvasObject().getCanvas();
        double[] rect = new double[4];
        rect =
          new double[] {
            canvas.getBounds().getX(),
            m_initOnScreen[1],
            Math.abs(canvas.getX() - m_endOnScreen[0]),
            canvas.getHeight()};
        int res =
          Graphics2DHelper.lineRectIntersection2D(
            new double[] {
              m_endOnScreen[0],
              m_endOnScreen[1],
              m_initOnScreen[0],
              m_initOnScreen[1] },
            rect,
            coord);
        getIntervalDrawable().setPoints(coord[0], coord[1], coord[2], coord[3]);
        //set bracket right:
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          false);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          true);
      }
      else { //(-infty, infty)
        double[] coord = new double[4];
        MMCanvas canvas = getMasterAsCanvasObject().getCanvas();
        int res =
          Graphics2DHelper.lineRectIntersection2D(
            new double[] {
              m_initOnScreen[0],
              m_initOnScreen[1],
              m_endOnScreen[0],
              m_endOnScreen[1] },
            new double[] { 0.0, 0.0, canvas.getWidth(), canvas.getHeight()},
            coord);
        getLeftBracketDrawable().setProps(
          m_initOnScreen,
          !getRealMasterObject().getLowerBoundaryType()
            || (getRealMasterObject().getLowerBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getLowerBoundaryType()
            && getRealMasterObject().getForm(),
          false);
        getRightBracketDrawable().setProps(
          m_endOnScreen,
          getRealMasterObject().getUpperBoundaryType()
            && !(getRealMasterObject().getUpperBoundaryType()
              && getRealMasterObject().getForm()),
          getRealMasterObject().getUpperBoundaryType()
            && getRealMasterObject().getForm(),
          false);
        switch (res) {
          case 0 :
            getIntervalDrawable().setPoints(0, 0, 0, 0);
            break;
          case 1 :
            getIntervalDrawable().setPoints(
              coord[0],
              coord[1],
              coord[0],
              coord[1]);
            break;
          case 2 :
            getIntervalDrawable().setPoints(
              coord[0],
              coord[1],
              coord[2],
              coord[3]);
            break;
        }
      }
    }
  }

  private MMInterval getRealMasterObject() {
    return (MMInterval) m_masterMMObject;
  }

  private G2DPointDrawable getPointDrawable() {
    return (G2DPointDrawable) m_allDrawables[0];
  }

  private G2DLineDrawable getIntervalDrawable() {
    return (G2DLineDrawable) m_allDrawables[1];
  }

  private G2DBracketDrawable getLeftBracketDrawable() {
    return (G2DBracketDrawable) m_additionalDrawables[0];
  }

  private G2DBracketDrawable getRightBracketDrawable() {
    return (G2DBracketDrawable) m_additionalDrawables[1];
  }
}
