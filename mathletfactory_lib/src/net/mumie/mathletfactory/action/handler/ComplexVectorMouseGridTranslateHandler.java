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

package net.mumie.mathletfactory.action.handler;

import javax.swing.JComponent;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.geom.affine.Affine2DPoint;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.number.MMComplex;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * Mouse grid handler for a complex vector.
 * 
 * @author unknown
 * @mm.docstatus finished
 */
public class ComplexVectorMouseGridTranslateHandler extends MMHandler
{

    public ComplexVectorMouseGridTranslateHandler(JComponent display)
    {
        super(display);
        m_newScreenCoords = new double[2];
        m_newMathVector = new Affine2DPoint(net.mumie.mathletfactory.math.number.MDouble.class);
        m_event = new MMEvent(506, 16, 0, MMEvent.NO_KEY, MMEvent.NO_MODIFIER_SET);
        lengthOnGrid = false;
        angleOnGrid = false;
        stepForLength = 1.0D;
        stepForAngle = 5D;
        canvas = (MMCanvas)display;
    }

    public ComplexVectorMouseGridTranslateHandler(JComponent display, double stepForLength)
    {
        this(display);
        this.stepForLength = stepForLength;
    }

    public ComplexVectorMouseGridTranslateHandler(JComponent display, double stepForLength, double stepForAngle)
    {
        this(display, stepForLength);
        this.stepForAngle = stepForAngle;
    }

    public boolean userDefinedDealsWith(MMEvent event)
    {
        return m_event.equals(event);
    }

    public void userDefinedAction(MMEvent event)
    {
        m_newScreenCoords[0] = event.getX();
        m_newScreenCoords[1] = event.getY();
        ((Canvas2DObjectTransformer)((MMCanvasObjectIF)getMaster()).getCanvasTransformer()).getMathObjectFromScreen(m_newScreenCoords, m_newMathVector);
        double x = m_newMathVector.getXAsDouble();
        double y = m_newMathVector.getYAsDouble();
        double xy[] = new double[2];
        xy[0] = x;
        xy[1] = y;
        if(isOnGridOnly())
        {
            xy = roundAngle(xy[0], xy[1], stepForAngle);
            xy = roundLength(xy[0], xy[1], stepForLength);
        } else
        if(isLengthOnGridOnly())
            xy = roundLength(xy[0], xy[1], stepForLength);
        else
        if(isAngleOnGridOnly())
            xy = roundAngle(xy[0], xy[1], stepForAngle);
        x = xy[0];
        y = xy[1];
        ((MMComplex)m_master).setComplex(x, y);
        canvas.renderScene();
        canvas.repaint();
    }

    public void userDefinedFinish()
    {
    }

    public void setOnGridOnly(boolean value)
    {
        lengthOnGrid = value;
        angleOnGrid = value;
    }

    public boolean isOnGridOnly()
    {
        return lengthOnGrid & angleOnGrid;
    }

    public boolean isLengthOnGridOnly()
    {
        return lengthOnGrid;
    }

    public boolean isAngleOnGridOnly()
    {
        return angleOnGrid;
    }

    public void setLengthOnGrid(boolean value)
    {
        lengthOnGrid = value;
    }

    public void setAngleOnGridOnly(boolean value)
    {
        angleOnGrid = value;
    }

    public void setMaster(MMObjectIF master)
    {
        super.setMaster(master);
    }

    private double[] roundLength(double x, double y, double roundingfactor)
    {
        double xy[] = new double[2];
        double phi = Math.atan2(y, x);
        double length = Math.sqrt(x * x + y * y);
        double bottom = createStepLine(length, roundingfactor);
        double rest = length - bottom;
        if(rest < roundingfactor / 2D)
            length = bottom;
        else
            length = bottom + roundingfactor;
        xy[0] = length * Math.cos(phi);
        xy[1] = length * Math.sin(phi);
        return xy;
    }

    private double[] roundAngle(double x, double y, double roundingfactor)
    {
        double xy[] = new double[2];
        double length = Math.sqrt(x * x + y * y);
        double phi = Math.atan2(y, x);
        phi = (phi * 180D) / 3.1415926535897931D;
        double bottom = createStepLine(phi, roundingfactor);
        double rest = phi - bottom;
        if(rest < roundingfactor / 2D)
            phi = bottom;
        else
            phi = bottom + roundingfactor;
        phi = (phi * 3.1415926535897931D) / 180D;
        xy[0] = length * Math.cos(phi);
        xy[1] = length * Math.sin(phi);
        return xy;
    }

    private double createStepLine(double xcoord, double step)
    {
        double ycoord = step * Math.floor(xcoord / step);
        return ycoord;
    }

    private MMEvent m_event;
    private Affine2DPoint m_newMathVector;
    private final double m_newScreenCoords[];
    private boolean lengthOnGrid;
    private boolean angleOnGrid;
    private double stepForLength;
    private double stepForAngle;
    private MMCanvas canvas;
}
