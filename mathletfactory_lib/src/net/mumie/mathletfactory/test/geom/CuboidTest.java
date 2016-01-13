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

/** Hey emacs, this is a -*- java -*- file!! **/

package net.mumie.mathletfactory.test.geom;

import java.awt.Color;

import net.mumie.mathletfactory.action.handler.Affine3DMouseTranslateHandler;
import net.mumie.mathletfactory.appletskeleton.j3d.SingleJ3DCanvasApplet;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.PolygonDisplayProperties;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DLineSegment;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPoint;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DPolygon;
import net.mumie.mathletfactory.util.BasicApplicationFrame;

/**
 * This applet draws an arbitrary user defined parameterized surface in $\mathbbm{R}^3$. The user may edit each of
 * the coordinate expressions and the boundaries of the domain intervals.
 * 
 * @author Eberlein
 * @mm.docstatus finished
 * @mm.status devel_ok
 * @mm.description -
 * @mm.rating none
 * @mm.changelog 2004-12-14: Initial
 */
public class CuboidTest extends SingleJ3DCanvasApplet {

    private Affine3DPoint A, B, C, D, E, F, G, H;
    private MMAffine3DPoint MMA, MMB, MMC, MMD;
    private MMAffine3DPolygon ABCD, ABFE, EFGH, BCGF, CDHG, DAHE;
    private MMAffine3DLineSegment AB, AD;
    private MMJ3DCanvas m_canvas;
    private Affine3DPoint[] pointsABCD, pointsABFE, pointsEFGH, pointsBCGF, 
	pointsCDHG, pointsDAHE;
    private PolygonDisplayProperties ppABCD, ppABFE, ppEFGH, ppBCGF, ppCDHG, ppDAHE;
    private LineDisplayProperties lpAB;
    private Affine3DMouseTranslateHandler amth;

    public void init() {
	super.init();
	setLoggerLevel(java.util.logging.Level.WARNING);
	setTitle("test_in_R3");
	
	m_canvas = getCanvas3D();
	m_canvas.setViewerPosition( new double[] { -3.0, -3.0, -3.0 } );
	m_canvas.setViewDirection( new double[] { 1.0, 1.0, 1.0 } ,  
				     new double[] { -1.0, 0.0, 1.0 } );

	createObjects();
	initializeObjects();
	
	m_canvas.addObject(ABCD);
	m_canvas.addObject(ABFE);
	m_canvas.addObject(EFGH);
	m_canvas.addObject(BCGF);
	m_canvas.addObject(CDHG);
	m_canvas.addObject(DAHE);

	m_canvas.addObject(AB);
	m_canvas.addObject(AD);
	
    }
    
    protected void initializeObjects() {
	
	ppABCD.setObjectColor(Color.blue);
	ppEFGH.setObjectColor(Color.red);
	ppABFE.setObjectColor(Color.green);
	ppBCGF.setObjectColor(Color.yellow);
	ppCDHG.setObjectColor(Color.black);
	ppDAHE.setObjectColor(Color.black);

	ppABCD.setTransparency(0.25);
	ppEFGH.setTransparency(0.25);
	ppABFE.setTransparency(0.25);
	ppBCGF.setTransparency(0.25);
	ppCDHG.setTransparency(0.25);
	ppDAHE.setTransparency(0.25);

	lpAB.setLineWidth(10.0);

	ABCD.setDisplayProperties(ppABCD);
	ABFE.setDisplayProperties(ppABFE);
	EFGH.setDisplayProperties(ppEFGH);
	BCGF.setDisplayProperties(ppBCGF);
	CDHG.setDisplayProperties(ppCDHG);
	DAHE.setDisplayProperties(ppDAHE);
	AB.setDisplayProperties(lpAB);
	
    }
    
    public void reset(){
	super.reset();
	initializeObjects();
    }
    
    public static void main(String[] args) {
	CuboidTest myApplet = new CuboidTest();
	myApplet.addScreenShotButton();
	BasicApplicationFrame f = new BasicApplicationFrame(myApplet, 600, 600);
	myApplet.init();
	myApplet.start();
	f.pack();
	f.setVisible(true);
    }
    
    private void createObjects() {

	amth = new Affine3DMouseTranslateHandler(getCanvas3D());

	ppABCD = new PolygonDisplayProperties();
	ppABFE = new PolygonDisplayProperties();
	ppEFGH = new PolygonDisplayProperties();
	ppBCGF = new PolygonDisplayProperties();
	ppCDHG = new PolygonDisplayProperties();
	ppDAHE = new PolygonDisplayProperties();
	lpAB = new LineDisplayProperties();

	A = new Affine3DPoint(MInteger.class, -1, -1, -1);
	B = new Affine3DPoint(MInteger.class,  1, -1, -1);
	C = new Affine3DPoint(MInteger.class,  1,  1, -1);
	D = new Affine3DPoint(MInteger.class, -1,  1, -1);
	E = new Affine3DPoint(MInteger.class, -1, -1,  1);
	F = new Affine3DPoint(MInteger.class,  1, -1,  1);
	G = new Affine3DPoint(MInteger.class,  1,  1,  1);
	H = new Affine3DPoint(MInteger.class, -1,  1,  1);

	MMA = new MMAffine3DPoint(MInteger.class, -1, -1, -1);
	MMB = new MMAffine3DPoint(MInteger.class,  1, -1, -1);
	MMC = new MMAffine3DPoint(MInteger.class,  1,  1, -1);
	MMD = new MMAffine3DPoint(MInteger.class, -1,  1, -1);

	AB = new MMAffine3DLineSegment(MMA, MMB);
	AD = new MMAffine3DLineSegment(MMA, MMD);

	AB.addHandler(amth);
       
	pointsABCD = new Affine3DPoint[] { A, B, C, D };
	pointsABFE = new Affine3DPoint[] { A, B, F, E };
	pointsEFGH = new Affine3DPoint[] { E, F, G, H };
	pointsBCGF = new Affine3DPoint[] { B, C, G, F };
	pointsCDHG = new Affine3DPoint[] { C, D, H, G };
	pointsDAHE = new Affine3DPoint[] { D, A, E, H };
	
	ABCD = new MMAffine3DPolygon( pointsABCD );
	ABFE = new MMAffine3DPolygon( pointsABFE );	
	EFGH = new MMAffine3DPolygon( pointsEFGH );	
	BCGF = new MMAffine3DPolygon( pointsBCGF );	
	CDHG = new MMAffine3DPolygon( pointsCDHG );	
	DAHE = new MMAffine3DPolygon( pointsDAHE );	
    }
}
