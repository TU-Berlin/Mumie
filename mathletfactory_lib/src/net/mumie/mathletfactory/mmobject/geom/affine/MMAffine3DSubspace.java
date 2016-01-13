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

package net.mumie.mathletfactory.mmobject.geom.affine;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.action.handler.MMHandler;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.action.updater.DependencyIF;
import net.mumie.mathletfactory.action.updater.MMUpdater;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.SurfaceDisplayProperties;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.affine.Affine3DPoint;
import net.mumie.mathletfactory.math.geom.affine.AffinePoint;
import net.mumie.mathletfactory.math.geom.affine.AffineSpace;
import net.mumie.mathletfactory.math.number.numeric.Span;
import net.mumie.mathletfactory.mmobject.InteractivitySupport;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.VisualizeInCanvasSupport;
import net.mumie.mathletfactory.transformer.CanvasObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 *  This class represents an arbitrary real (i.e. dim <3) subspace of the
 *  affine 3d space. Depending on its specified basis it may be a zero
 *  space, a point, a line or a plane.
 *
 *  @author Paehler
 *  @mm.docstatus finished
 */
public class MMAffine3DSubspace extends AffineSpace implements MMAffineSubspaceIF {

  /** for temporal storage. */
  private NumberTuple[] m_tmpCoords = new NumberTuple[3];

  /**
   * Creates the subspace from the given spanning points, i.e. the space will
   * be the subspace that contains the given points and has the lowest
   * dimension.
   */
  public MMAffine3DSubspace(Class entryClass, Affine3DPoint[] spanningPoints) {
    super(entryClass, getSpanDimension(spanningPoints)-1, 3);
    setSpanningPoints(spanningPoints);
    setDisplayProperties(new SurfaceDisplayProperties());
  }

  /**
   * Copy constructor.
   * @see net.mumie.mathletfactory.math.geom.affine.AffineSpace#AffineSpace(AffineSpace)
   */
  public MMAffine3DSubspace (AffineSpace anAffineSpace) {
    super(anAffineSpace);
  }

  /**
   * A utility method that returns the dimension of the space containing
   * the given spanning points. Needed by the constructor.
   */
  private static int getSpanDimension(Affine3DPoint[] spanningPoints){
    NumberTuple[] tuples = new NumberTuple[spanningPoints.length];
    AffinePoint.AffinePoints2Tupels(spanningPoints, tuples);
    return Span.spanIndices(tuples).length;
  }

  /**
   * Returns the affine coordinates of the vectors that span this affine subspace. For an empty space or
   * a point this is an empty array, for lines the direction vector (spanning_points[1] - spanning_points[0]),
   * for planes two direction vectors (spanning_points[1] - spanning_points[0]) and
   * (spanning_points[2] - spanning_points[0]).
   */
  public NumberTuple[] getDirectionVectors(){
    NumberTuple[] points = getAffineCoordinates();
    if(points.length <= 1)
      return new NumberTuple[]{};
    else
      if(points.length == 2)
        return new NumberTuple[]{points[1].subFrom(points[0])};
    else
      if(points.length == 3)
        return new NumberTuple[]{points[1].subFrom(points[0]), points[2].subFrom(points[0])};
    else
      throw new IllegalStateException("Wrong dimension: "+points.length);
  }

  /**
   * Sets spanning_points[1] and spanning_points[2] (if applicable) by using the given direction vectors:
   * spanning_point[1] = spanning_point[0] + direction_vector[0] <br>
   * spanning_point[2] = spanning_point[0] + direction_vector[1].
   */
  public void setDirectionVectors(NumberTuple[] vectors){
    if(getAffineCoordinates().length == 0)
      throw new IllegalStateException("this method may not be called for dimension < 1");
    NumberTuple point0 = getAffineCoordinates()[0], point1=null, point2=null;
    if(vectors.length == 0)
      return;
    if(vectors.length >= 1)
      point1 = getAffineCoordinates()[0].addTo(vectors[0]);
    if(vectors.length == 2){
      point2 = getAffineCoordinates()[0].addTo(vectors[1]);
      setSpanningPoints(new NumberTuple[]{point0, point1, point2});
    } else // vectors.length == 1
      setSpanningPoints(new NumberTuple[]{point0, point1});
  }

  /**
   * Sets the spanning points of this subspace, i.e. the points that can be used to produce a basis.
   * To be more precise, they are the projective basis of this affine subspace, in affine coordinates.
   */
  public void setSpanningPoints(NumberTuple[] spanningPointCoords){
    Affine3DPoint[] points = new Affine3DPoint[spanningPointCoords.length];
    for(int i=0;i<spanningPointCoords.length;i++)
      points[i] = new Affine3DPoint(spanningPointCoords[i]);
    setSpanningPoints(points);
  }

  /**
   * Sets the spanning points of this subspace, i.e. the points that can be used to produce a basis.
   * To be more precise, they are the projective basis of this affine subspace, in affine coordinates.
   */
  public void setSpanningPoints(Affine3DPoint[] spanningPoints){
    int dimension = spanningPoints.length-1;
    m_tmpCoords = new NumberTuple[dimension+1];
    for(int i=0; i< dimension+1;i++)
      m_tmpCoords[i] = spanningPoints[i].getProjectiveCoordinatesOfPoint();

    int[] indices = Span.spanIndices(m_tmpCoords);
    NumberTuple[] basisProjCoords = new NumberTuple[indices.length];
    for(int i=0; i<indices.length; i++){
      basisProjCoords[i] = m_tmpCoords[indices[i]];
      basisProjCoords[i].getEntryRef(4).setDouble(1);
    }
    setProjectiveCoordinates(basisProjCoords);
  }

  /**
   * The list containing all
   * {@link net.mumie.mathletfactory.action.message.SpecialCaseListener}s.
   */
  private HashSet m_specialCaseListenersList = new HashSet();

  /**
   * This is the helper object, that really implements all methods due to
   * interaction purposes (handler, updater,...).
   * MMLinearMap must implement the same methods (of course), but these can
   * be implemented generically by invoking the methods of this helper.
   */
  private final InteractivitySupport m_interactionHelper =
    new InteractivitySupport(this);

  private final VisualizeInCanvasSupport m_visualizeHelper =
    new VisualizeInCanvasSupport(this);

  private DisplayProperties m_displayProperties = new DisplayProperties();

  private String m_label = null;

  private boolean m_isEditable = false;


  public DisplayProperties getDisplayProperties() {
    return m_displayProperties;
  }

  public void setDisplayProperties(DisplayProperties newProperties) {
    m_displayProperties = newProperties;
  }

  public void setLabel(String aLabel) {
    m_label = aLabel;
  }

  public String getLabel() {
    return m_label;
  }

  public int getDefaultTransformType() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public int getDefaultTransformTypeInCanvas() {
    return GeneralTransformer.AFFINE_DEFAULT_TRANSFORM;
  }

  public HashMap getDisplayTransformerMap() {
    return m_visualizeHelper.getDisplayTransformerMap();
  }

  public CanvasObjectTransformer getCanvasTransformer() {
    return m_visualizeHelper.getCanvasTransformer();
  }

  public void setCanvasTransformer(int transformType, int visualType) {
    m_visualizeHelper.setCanvasTransformer(transformType, visualType);
  }

  public int getTransformType(JComponent display) {
    return m_visualizeHelper.getTransformType(display);
  }

  public void draw() {
    m_visualizeHelper.draw();
  }

  public void render() {
    m_visualizeHelper.render();
  }

  public MMCanvasObjectIF getAsCanvasContent() {
    return m_visualizeHelper.getAsCanvasContent();
  }

  public boolean isAtScreenLocation(int x, int y) {
    return m_visualizeHelper.isAtScreenLocation(x, y);
  }

  public boolean isVisible() {
    return m_visualizeHelper.isVisible();
  }

  public void setVisible(boolean aFlag) {
    m_visualizeHelper.setVisible(aFlag);
  }

  public MMCanvas getCanvas() {
    return m_visualizeHelper.getCanvas();
  }

  public void setCanvas(MMCanvas aCanvas) {
    m_visualizeHelper.setCanvas(aCanvas);
  }

  public boolean doAction(MMEvent event) {
    return m_interactionHelper.doAction(event);
  }

  public void addHandler(MMHandler aHandler) {
    m_interactionHelper.addHandler(aHandler);
  }

  public MMHandler getActiveHandler() {
    return m_interactionHelper.getActiveHandler();
  }

  public void resetActiveHandler() {
    m_interactionHelper.resetActiveHandler();
  }

  public void addUpdater(MMUpdater anUpdater) {
    m_interactionHelper.addUpdater(anUpdater);
  }

  public void invokeUpdaters() {
    m_interactionHelper.invokeUpdaters();
  }

  public int getHandlerCount() {
    return m_interactionHelper.getHandlerCount();
  }

  public int getUpdaterCount() {
    return m_interactionHelper.getUpdaterCount();
  }

  public boolean isSelectable() {
    return m_interactionHelper.isSelectable();
  }

  public boolean isSelected() {
    return m_interactionHelper.isSelected();
  }

  public void dependsOn(MMObjectIF obj, DependencyIF dependency) {
    m_interactionHelper.dependsOn(obj, dependency);
  }

  public void dependsOn(MMObjectIF[] objects, DependencyIF dependency) {
    m_interactionHelper.dependsOn(objects, dependency);
  }

  public int getDefaultTransformTypeAsContainer() {
    return GeneralTransformer.AFFINE_3D_SUBSPACE_TRANSFORM;
  }

  public JComponent getAsContainerContent() {
    return m_visualizeHelper.getAsContainerContent();
  }

  public JComponent getAsContainerContent(int transformType) {
    return m_visualizeHelper.getAsContainerContent(transformType);
  }

  public boolean isEditable() {
    return m_isEditable;
  }

  public void setEditable(boolean editable) {
    if(editable == m_isEditable)
      return;
    m_isEditable = editable;
    render();
  }

  public void propertyChange(PropertyChangeEvent e) {
    //System.out.println("changed:"+e.getOldValue()+" to "+e.getNewValue());
    // check if the "rooting point" has changed:
    NumberTuple[] spanningPoints = getAffineCoordinates();
    if(e.getPropertyName().equals(ORIGIN_VECTOR)){
      spanningPoints[0] = new NumberTuple((NumberMatrix)e.getNewValue());
      for(int i=1;i<spanningPoints.length;i++)
        spanningPoints[i].addTo(spanningPoints[0].deepCopy().subFrom((NumberMatrix)e.getOldValue()));
      setSpanningPoints(spanningPoints);
    } else if(e.getPropertyName().equals(DIRECTION_VECTOR)){
      NumberTuple[] directionVectors = getDirectionVectors();
      // create backup in case of invalid input
      NumberTuple[] oldDirectionVectors = new NumberTuple[directionVectors.length];
      for(int i=0;i<directionVectors.length;i++)
        oldDirectionVectors[i] = directionVectors[i];
      if(directionVectors.length >= 1)
        if(directionVectors[0].equals(e.getOldValue()))
          directionVectors[0] = new NumberTuple((NumberMatrix)e.getNewValue());
      if(directionVectors.length == 2)
        if(directionVectors[1].equals(e.getOldValue()))
          directionVectors[1] = new NumberTuple((NumberMatrix)e.getNewValue());
      try {
        setDirectionVectors(directionVectors);
      } catch(Exception ex){
        ex.printStackTrace();
        setDirectionVectors(oldDirectionVectors);
        fireSpecialCaseEvent(new SpecialCaseEvent(this, "nullvector_disallowed"));
      }
    } else
      return;
    //System.out.println(e.getPropertyName()+" changed:"+e.getOldValue()+" to "+e.getNewValue());
    ActionManager.performActionCycleFromObject(this);
  }

  public Rectangle2D getWorldBoundingBox() {
    return null;
  }

  /** The <code>listener</code> will be informed if this handler produces a special case.*/
  public void addSpecialCaseListener(SpecialCaseListener listener) {
    m_specialCaseListenersList.add(listener);
  }

  public void removeSpecialCaseListener(SpecialCaseListener listener) {
    m_specialCaseListenersList.remove(listener);
  }

  /** Informs all registered special case listeners, that the given event has occurred. */
  public void fireSpecialCaseEvent(SpecialCaseEvent e) {
    Iterator i = m_specialCaseListenersList.iterator();
    while (i.hasNext())
       ((SpecialCaseListener) i.next()).displaySpecialCase(e);
  }

  /** Sets the object fill color (without border). */
  public void setObjectColor(Color color){
    DisplayProperties props = getDisplayProperties();
    props.setObjectColor(color);
    setDisplayProperties(props);
  }

  /** Sets the object border color. */
  public void setBorderColor(Color color){
    DisplayProperties props = getDisplayProperties();
    props.setBorderColor(color);
    setDisplayProperties(props);
  }

  /** Sets both the object's fill and border color. */
  public void setColor(Color color){
    setObjectColor(color);
    setBorderColor(color);
  }

  public void removeAllHandlers() {
    m_interactionHelper.removeAllHandlers();
  }

  public void removeAllUpdaters() {
    m_interactionHelper.removeAllUpdaters();
  }

  public void removeHandler(MMHandler aHandler) {
    m_interactionHelper.removeHandler(aHandler);
  }

  public void removeUpdater(MMUpdater anUpdater) {
    m_interactionHelper.removeUpdater(anUpdater);
  }
  
  public List getHandlers() {
		return this.m_interactionHelper.getHandlers();
  }
}

