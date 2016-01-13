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

package net.mumie.mathletfactory.transformer.j3d;

import java.awt.Color;

import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.LineDisplayProperties;
import net.mumie.mathletfactory.display.j3d.J3DCoordinateSystemDrawable;
import net.mumie.mathletfactory.display.j3d.J3DDrawable;
import net.mumie.mathletfactory.display.j3d.J3DLineSegmentDrawable;
import net.mumie.mathletfactory.display.j3d.J3DParameterizedSurfaceDrawable;
import net.mumie.mathletfactory.display.j3d.J3DPointDrawable;
import net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DQuadric;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * Transformer for
 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine3DQuadric}.
 * 
 * @author Mrose
 * @mm.docstatus finished
 */
public class Affine3DQuadricTransformer extends Affine3DDefaultTransformer {

	/** Every time the function is recalculated, the version number is incremented */
	private int m_version = 0;

	private J3DParameterizedSurfaceDrawable[] m_quadricDrawable =
		{
			new J3DParameterizedSurfaceDrawable(),
			new J3DParameterizedSurfaceDrawable()};
	private J3DLineSegmentDrawable m_lineDrawable = new J3DLineSegmentDrawable();
	private J3DCoordinateSystemDrawable m_coordinateSystemDrawable =
		new J3DCoordinateSystemDrawable();
	private J3DPointDrawable m_pointDrawable = new J3DPointDrawable();

	private boolean[] m_quadricVisible = { false, false };
	private boolean m_lineVisible = false;
	private boolean m_pointVisible = false;
	private boolean m_coordinateSystemVisible = false;

	public Affine3DQuadricTransformer() {
		m_activeDrawable = m_quadricDrawable[0];
		m_coordinateSystemDrawable.setUnitLength(5.0);
	}

	public void initialize(MMObjectIF masterObject) {
		m_masterMMObject = masterObject;
		m_isInitialized = true;
		((J3DDrawable) m_quadricDrawable[0]).setCanvas(
			(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		((J3DDrawable) m_quadricDrawable[1]).setCanvas(
			(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		((J3DDrawable) m_lineDrawable).setCanvas(
			(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		((J3DDrawable) m_pointDrawable).setCanvas(
			(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		initDrawables();
		((J3DDrawable) m_coordinateSystemDrawable).setCanvas(
			(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		initVisibleFlags();
		m_version = getRealMaster().getVersion();
	}

	private void initDrawables() {
		//init surface drawables
		m_quadricDrawable[0].setValues(getRealMaster().getValuesFirstComponent());
		m_quadricDrawable[1].setValues(getRealMaster().getValuesSecondComponent());

		//init point drawable
		m_pointDrawable.setPoint(getRealMaster().getPoint());

		//compute line drawable
		double[][] points = getRealMaster().getPoints();
		//get two points on the line
		double[] direction = new double[3];
		double[][] pointsOutOfRange = new double[2][3];
		;
		// calculate direction vector from first to second point
		Affine3DDouble.setFrom(direction, points[1]);
		Affine3DDouble.sub(direction, points[0]);
		// calculate the points, that are outside the view volume
		(
			(MMJ3DCanvas) getMasterAsCanvasObject()
				.getCanvas())
				.getPointOutOfViewRange(
			points[0],
			direction,
			pointsOutOfRange[0]);
		Affine3DDouble.scale(direction, -1);
		(
			(MMJ3DCanvas) getMasterAsCanvasObject()
				.getCanvas())
				.getPointOutOfViewRange(
			points[1],
			direction,
			pointsOutOfRange[1]);
		//init line drawable
		m_lineDrawable.setPoints(pointsOutOfRange[0], pointsOutOfRange[1]);
		//init coordinate system drawable
		m_coordinateSystemDrawable.setTransformation(
			getRealMaster().getTransformationMatrix());
	}

	private void initVisibleFlags() {
		if (getRealMaster().getQuadricType(getRealMaster().getMatrix())
			== ResourceManager.getMessage("point")) {
			m_quadricVisible[0] = false;
			m_quadricVisible[1] = false;
			m_lineVisible = false;
			m_pointVisible = true;
		}
		else if (
			getRealMaster().getQuadricType(getRealMaster().getMatrix())
				== ResourceManager.getMessage("straight_line")) {
			m_quadricVisible[0] = false;
			m_quadricVisible[1] = false;
			m_lineVisible = true;
			m_pointVisible = false;
		}
		else if (
			getRealMaster().getQuadricType(getRealMaster().getMatrix())
				== ResourceManager.getMessage("hyperboloid_of_two_sheets")
				|| getRealMaster().getQuadricType(getRealMaster().getMatrix())
					== ResourceManager.getMessage("hyperbolic_cylinder")
				|| getRealMaster().getQuadricType(getRealMaster().getMatrix())
					== ResourceManager.getMessage("pair_of_distinct_parallel_planes")
				|| getRealMaster().getQuadricType(getRealMaster().getMatrix())
					== ResourceManager.getMessage("pair_of_intersecting_planes")) {
			m_quadricVisible[0] = true;
			m_quadricVisible[1] = true;
			m_lineVisible = false;
			m_pointVisible = false;
		}
		else {
			m_quadricVisible[0] = true;
			m_quadricVisible[1] = false;
			m_lineVisible = false;
			m_pointVisible = false;
		}
	}

	public double[] getWorldPickPointFromMaster() {
		return new double[3];
	}

	public void synchronizeMath2Screen() {
		if (getRealMaster().getVersion() > m_version) {
			((J3DRemovableDrawable) m_quadricDrawable[0]).removeShape();
			((J3DRemovableDrawable) m_quadricDrawable[1]).removeShape();
			((J3DRemovableDrawable) m_lineDrawable).removeShape();
			((J3DRemovableDrawable) m_pointDrawable).removeShape();
			((J3DRemovableDrawable) m_coordinateSystemDrawable).removeShape();
		}
		initVisibleFlags();
		if (getRealMaster().getQuadricType(getRealMaster().getMatrix())
			== ResourceManager.getMessage("point")) {
			((J3DPointDrawable) m_pointDrawable).setPoint(getRealMaster().getPoint());
		}
		else if (
			getRealMaster().getQuadricType(getRealMaster().getMatrix())
				== ResourceManager.getMessage("straight_line")) {
			double[][] points = new double[2][3];
			points = getRealMaster().getPoints();
			double[] direction = new double[3];
			double[][] pointsOutOfRange = new double[2][3];
			;
			// calculate direction vector from first to second point
			Affine3DDouble.setFrom(direction, points[1]);
			Affine3DDouble.sub(direction, points[0]);
			// for line or plane calculate the points, that are outside the view volume
			(
				(MMJ3DCanvas) getMasterAsCanvasObject()
					.getCanvas())
					.getPointOutOfViewRange(
				points[0],
				direction,
				pointsOutOfRange[0]);
			Affine3DDouble.scale(direction, -1);
			(
				(MMJ3DCanvas) getMasterAsCanvasObject()
					.getCanvas())
					.getPointOutOfViewRange(
				points[1],
				direction,
				pointsOutOfRange[1]);
			((J3DLineSegmentDrawable) m_lineDrawable).setPoints(
				pointsOutOfRange[0],
				pointsOutOfRange[1]);
		}
		else {
			if (getRealMaster().getVersion() > m_version) {
				((J3DParameterizedSurfaceDrawable) m_quadricDrawable[0]).setValues(
					getRealMaster().getValuesFirstComponent());
				((J3DParameterizedSurfaceDrawable) m_quadricDrawable[1]).setValues(
					getRealMaster().getValuesSecondComponent());
				(
					(
						J3DCoordinateSystemDrawable) m_coordinateSystemDrawable)
							.setTransformation(
					getRealMaster().getTransformationMatrix());
			}
		}
		if (getRealMaster().getVersion() > m_version) {
			((J3DRemovableDrawable) m_quadricDrawable[0]).resetShape();
			((J3DRemovableDrawable) m_quadricDrawable[1]).resetShape();
			((J3DRemovableDrawable) m_lineDrawable).resetShape();
			((J3DRemovableDrawable) m_pointDrawable).resetShape();
			((J3DRemovableDrawable) m_coordinateSystemDrawable).resetShape();
			m_version = getRealMaster().getVersion();
		}
	}

	public void render() {
		synchronizeMath2Screen();
		DisplayProperties properties = getRealMaster().getDisplayProperties();
		if (m_quadricVisible[0] == true) {
			m_quadricDrawable[0].render(properties);
		}
		if (m_quadricVisible[1] == true) {
			m_quadricDrawable[1].render(properties);
		}
		if (m_pointVisible == true) {
			m_pointDrawable.render(properties);
		}
		if (m_lineVisible == true) {
			LineDisplayProperties lineproperties = new LineDisplayProperties();
			lineproperties.setLineWidth(10.0);
			lineproperties.setObjectColor(Color.blue);
			m_lineDrawable.render(lineproperties);
		}
		if (m_coordinateSystemVisible == true) {
			m_coordinateSystemDrawable.render(properties);
		}
		renderLabel(getWorldPickPointFromMaster());
	}

	public void draw() {
		MMAffine3DQuadric master = getRealMaster();
		if (m_quadricVisible[0] == true) {
			m_quadricDrawable[0].draw(master);
		}
		if (m_quadricVisible[1] == true) {
			m_quadricDrawable[1].draw(master);
		}
		if (m_pointVisible == true) {
			m_pointDrawable.draw(master);
		}
		if (m_lineVisible == true) {
			m_lineDrawable.draw(master);
		}

		if (m_coordinateSystemDrawable != null
			&& m_coordinateSystemVisible == true) {
			m_coordinateSystemDrawable.draw(master);
		}
	}

	public void updateFinished() {
		((J3DDrawable) m_quadricDrawable[0]).updateFinished();
		((J3DDrawable) m_quadricDrawable[1]).updateFinished();
		((J3DDrawable) m_lineDrawable).updateFinished();
		((J3DDrawable) m_pointDrawable).updateFinished();
		m_coordinateSystemDrawable.updateFinished();
	}

	private MMAffine3DQuadric getRealMaster() {
		return (MMAffine3DQuadric) m_masterMMObject;
	}
}
