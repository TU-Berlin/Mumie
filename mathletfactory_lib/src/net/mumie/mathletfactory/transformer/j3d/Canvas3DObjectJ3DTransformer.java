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
import java.awt.Font;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.Text2D;


import net.mumie.mathletfactory.display.j3d.J3DDrawable;
import net.mumie.mathletfactory.display.j3d.J3DRemovableDrawable;
import net.mumie.mathletfactory.display.j3d.J3DRenderingHints;
import net.mumie.mathletfactory.display.j3d.MMJ3DCanvas;
import net.mumie.mathletfactory.display.util.LabelSupport;
import net.mumie.mathletfactory.display.util.ViewIF;
import net.mumie.mathletfactory.math.util.Affine3DDouble;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.transformer.Canvas3DObjectTransformer;

/**
 *  This class is the base for all 3D rendering that occurs within a 
 *  {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas}.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */

public abstract class Canvas3DObjectJ3DTransformer
	extends Canvas3DObjectTransformer {

	/** 
	 * A constant for the font size 3D Text rendering. If a String is converted
	 * into an {@link javax.media.j3d.Text3D}, it is scaled by this factor to
	 * keep the text small but readable.   
	 * @value 0.07  
	 */
	protected static double J3D_TEXTSIZE = 0.07;

	private final double[] m_viewPosToWorldPointVector = new double[3];
	private final TransformGroup m_transformGroup = new TransformGroup();
	private final BranchGroup m_labelBranch = new BranchGroup();
	private final Transform3D m_transform = new Transform3D();
	private final Appearance blackapp = new Appearance();
	private final Vector3f labelPos = new Vector3f();
	
	private LabelSupport m_labelSupport = new LabelSupport(LabelSupport.ST_3D_CANVAS);
	/** 
	 * Creates this transformer with the 
	 * {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas} display.
	 */
	public Canvas3DObjectJ3DTransformer() {
		m_transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		m_labelBranch.setCapability(BranchGroup.ALLOW_DETACH);
	}

	/**
	 * Sets the master MMObject for this transformer and sets the canvas for all drawables.
	 * @see net.mumie.mathletfactory.transformer.GeneralTransformer#initialize(MMObjectIF)
	 */
	public void initialize(MMObjectIF masterObject) {
		super.initialize(masterObject);
		for (int i = 0; i < m_allDrawables.length; i++)
			((J3DDrawable) m_allDrawables[i]).setCanvas(
				(MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
	}

	/**
	 *  Since in Java3D the world2screen rendering (in
	 *  {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas#renderScene})is done by
	 *  the Java3D Renderer, nothing needs to be done here.
	 */
	public void synchronizeWorld2Screen() {
	}

	/**
	 * This method converts screen coordinates to world coordinates by using an 
	 * additional z value which is calculated as the distance vector from viewer 
	 * projected onto the view direction vector. 
	 */
	protected void screen2World(
		double[] javaScreenCoords,
		double[] worldCoords,
		double distanceInZProjFromViewPos) {
		(
			(MMJ3DCanvas) getMasterAsCanvasObject()
				.getCanvas())
				.getWorldPointFromScreenLocation(
			(int) javaScreenCoords[0],
			(int) javaScreenCoords[1],
			distanceInZProjFromViewPos,
			worldCoords);
	}

	/**
	 *  Puts the label of the Vector
	 *  {@link net.mumie.mathletfactory.mmobject.MMDefaultR3Vector Vector} (if defined)
	 *  in <code>position</code>.
	 *
	 *  @see net.mumie.mathletfactory.mmobject.MMObjectIF#getLabel
	 */
	protected void renderVectorLabel(double[] position) {
		if(getMaster().getLabel() == null)
			return;
		m_labelSupport.setLabel(getMaster().getLabel());
		labelPos.set(
			(float) (position[0]),
			(float) (position[1]),
			(float) (position[2]));
		J3D_TEXTSIZE = new J3DRenderingHints(getMaster().getDisplayProperties()).getJ3DTextSize();
		m_transform.setScale(J3D_TEXTSIZE);
		m_transform.setTranslation(labelPos);
		m_transformGroup.setTransform(m_transform);
		if (!((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas())
			.getContentBranch()
			.isLive()
			&& m_transformGroup.getParent() == null
      && getMaster().getDisplayProperties().isLabelDisplayed()) {
			BranchGroup bg =
				((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas())
					.getContentBranch();
			blackapp.setColoringAttributes(
				new ColoringAttributes(
					new Color3f(.0f, .0f, .0f),
					ColoringAttributes.FASTEST));
			Shape3D labelShape =
				new OrientedShape3D(
					new Text3D(
						new Font3D(new Font("Monospaced", Font.BOLD,1), null),
						m_labelSupport.getLabel()),
					blackapp,
					OrientedShape3D.ROTATE_ABOUT_POINT,
					labelPos);
			int count = m_labelSupport.getLabel().length()-1;
			String arrow="";
			float x=0;
			for(int i=0;i<=count;i++){
				labelShape.addGeometry(new Text3D(
						new Font3D(new Font("Monospaced", Font.BOLD,1), null),
						"\u2014",new Point3f(x,0.75f,0)));
				x+=0.5f;
			}
			arrow=">";
			labelShape.addGeometry(new Text3D(
					new Font3D(new Font("Monospaced", Font.BOLD,1), null),
					arrow,new Point3f(x,0.75f,0)));
			labelShape.setPickable(false);
			m_transformGroup.addChild(labelShape);
			m_labelBranch.addChild(m_transformGroup);
			bg.addChild(m_labelBranch);
		}
	}
	
	/**
	 *  Puts the label of the Master
	 *  {@link net.mumie.mathletfactory.mmobject.MMObjectIF MMObject} (if defined)
	 *  in <code>position</code>.
	 *
	 *  @see net.mumie.mathletfactory.mmobject.MMObjectIF#getLabel
	 */
	protected void renderLabel(double[] position) {
		if(getMaster().getLabel() == null)
			return;
		m_labelSupport.setLabel(getMaster().getLabel());
		labelPos.set(
			(float) (position[0]),
			(float) (position[1]),
			(float) (position[2]));
		J3D_TEXTSIZE = new J3DRenderingHints(getMaster().getDisplayProperties()).getJ3DTextSize();
		m_transform.setScale(J3D_TEXTSIZE);
		m_transform.setTranslation(labelPos);
		m_transformGroup.setTransform(m_transform);
		if (!((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas())
			.getContentBranch()
			.isLive()
			&& m_transformGroup.getParent() == null
      && getMaster().getDisplayProperties().isLabelDisplayed()) {
			BranchGroup bg =
				((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas())
					.getContentBranch();
			blackapp.setColoringAttributes(
				new ColoringAttributes(
					new Color3f(.0f, .0f, .0f),
					ColoringAttributes.FASTEST));
			Shape3D labelShape =
				new OrientedShape3D(
					new Text3D(
						new Font3D(new Font("Monospaced", Font.BOLD,1), null),
						m_labelSupport.getLabel()),
					blackapp,
					OrientedShape3D.ROTATE_ABOUT_POINT,
					labelPos);
			labelShape.setPickable(false);
			m_transformGroup.addChild(labelShape);
			m_labelBranch.addChild(m_transformGroup);
			bg.addChild(m_labelBranch);
		}
	}
	/**
	 *  It may be necessary for a J3D Drawable to do some recalculations (bounds,
	 *  etc.) that would be to expensive to do while updating (e.g. dragging a
	 *  vector), so this method can be called by updaters to signal, that the
	 *  update event cycle has finished.
	 */
	public void updateFinished() {
		if(m_activeDrawable != null)
		 ((J3DDrawable) m_activeDrawable).updateFinished();
	}

	public void remove() {
		if (m_activeDrawable instanceof J3DRemovableDrawable) {
			((J3DRemovableDrawable) m_activeDrawable).removeShape();
		}
		m_labelBranch.detach();
		((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas())
			.getContentBranch()
			.removeChild(m_labelBranch);
		m_labelBranch.removeAllChildren();
	}

	/**
	 * Calculates the distance of the vector from viewers position to 
	 * {@link #getWorldPickPointFromMaster} from the viewer projected onto 
	 * the view direction vector. 
	 */
	public double getDistanceInZProj() {
		MMJ3DCanvas canvas = ((MMJ3DCanvas) getMasterAsCanvasObject().getCanvas());
		System.arraycopy(
			getWorldPickPointFromMaster(),
			0,
			m_viewPosToWorldPointVector,
			0,
			3);
		Affine3DDouble.sub(m_viewPosToWorldPointVector, canvas.getViewerPosition());
		return Affine3DDouble.dotProduct(
			m_viewPosToWorldPointVector,
			canvas.getViewDirection());
	}

}
