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

package net.mumie.mathletfactory.display.j3d;

import java.awt.Rectangle;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import net.mumie.mathletfactory.display.CanvasDrawable;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.mmobject.MMCanvasObjectIF;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;

/**
 *  This class serves as base for all subclasses of {@link CanvasDrawable}s that
 *  use the Java 3D-API.
 * 
 *  @author Paehler
 *  @mm.docstatus finished
 */
public abstract class J3DDrawable extends CanvasDrawable {


  private final TransformGroup m_transformGroup = new TransformGroup();
  private MMJ3DCanvas m_canvas;

  private Vector3d m_position = new Vector3d();
  private Matrix3d m_rotationScale = new Matrix3d();
  private Matrix4d m_transformationFromOrigin = new Matrix4d();

  /** A tool for determining picking operations. */
  protected PickCanvas m_pickCanvas = null;

  /** A util class that contains the result of a picking operation. */
  protected PickResult m_pickResult = null;

  /** This flag is set to true if an action changes the object's geometry (picking won't
   *  work if the Shape3D is not reconstructed). 
   */
  protected boolean m_rebuildNeeded = true;

  //for temporarily use
  private Transform3D m_transform3D = new Transform3D();
  private Vector3f m_vector = new Vector3f();
  private Vector3d m_v1 = new Vector3d();
  private Vector3d m_v2 = new Vector3d();
  private Point3d m_p1 = new Point3d(), m_p2 = new Point3d(), m_p3 = new Point3d();

  /** Usually called by a subclass of {@link net.mumie.mathletfactory.transformer.j3d.CanvasJ3DObjectTransformer}. */
  public J3DDrawable() {
    m_rotationScale.setIdentity();
    m_transformationFromOrigin.setIdentity();
    m_transformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
  }

  /**
   *  Determines, by how many pixels you may miss the object, when picking it
   *  with a mouse.
   */
  public static final int PICKING_TOLERANCE = 4;

  /**
   *  The Java3D-<code>Group</code>, that acts as graphical representation
   *  of the corresponding mathematical {@link net.mumie.mathletfactory.mmobject.MMObjectIF
   *  MMObject}.
   */
  protected abstract Group getShape(DisplayProperties properties);

  /**
   *  Returns the Rotation from the standard Orientation (as specified for
   *  each concrete drawable) in (World/Java3D universe-) coordinates.
   */
  protected Matrix3d getDeformation() {
    m_transformationFromOrigin.getRotationScale(m_rotationScale);
    return m_rotationScale;
  }

  /** 
   * If you wish to deform a J3D Drawable on this level (alternatively you can change
   * the geometry of the Shape3D in question), you call this method.
   */
  protected void setDeformation(Matrix3d rotationScale) {
    m_rotationScale = rotationScale;
    m_transformationFromOrigin.setRotationScale(m_rotationScale);
  }

  /**
   *  Returns the translation in (World/Java3D universe-) coordinates from origin
   *  it usually consists  of the coordinates of the corresponding mathematical
   *  {@link net.mumie.mathletfactory.mmobject.MMObjectIF MMObject}.
   */
  protected Vector3d getPosition() {
    m_transformationFromOrigin.get(m_position);
    return m_position;
  }

  /**
   *  Set the position of the drawable.
   * @see #getPosition
   */
  protected void setPosition(Vector3d position) {
    m_position = position;
    m_transformationFromOrigin.setTranslation(m_position);
  }

  /**
   *  Returns the complete Transformation (rotation, translation and scaling),
   *  which is applied on the standard dimensions specified for each concrete
   *  Drawable. WARNING: Do not work on the state of the reference without
   *  setting it with {@link #setTransformationFromOrigin} afterwards, for
   *  changes will be lost otherwise.
   */
  protected Matrix4d getTrafoFromOrigin() {
    // update from other state variables
    return m_transformationFromOrigin;
  }
  /**
   * In some cases, the Drawable's Shape is created in the origin and then translated 
   * (and maybe even deformed) by a <code>TransformGroup</code> above the Shape-node.
   * If you want this for your Drawable, call this method. 
   */
  protected void setTrafoFromOrigin(Matrix4d transformation) {
    m_transformationFromOrigin = transformation;
  }

  /** 
   * Simply evaluates {@link net.mumie.mathletfactory.display.DisplayProperties#isFilled}
   * of the argument.
   */
  protected boolean isFilled(DisplayProperties properties) {
    return properties.isFilled();
  }

  /**
   * Set the canvas, this drawable is rendered in.
   */
  public void setCanvas(MMJ3DCanvas canvas) {
    m_canvas = canvas;
  }

  /**
   * Returns the canvas, this drawable is rendered in.
   */
  protected MMJ3DCanvas getCanvas() {
    return m_canvas;
  }

  /** 
   * Each drawable has its own transform group that stores a transformation from origin.
   * @see #setTrafoFromOrigin
   * @see #getTrafoFromOrigin
   */
  protected abstract void setTransformGroup(TransformGroup tg);

  /**
   *  In this method the actual drawing on the canvas is performed. It
   *  considers not only the shape of the corresponding
   *  {@link m_master master} MMObject, but also evaluates its
   *  {@link DisplayProperties} and thus creates shadows, etc. accordingly.
   */
  public void render(DisplayProperties properties) {
    //System.out.println(">>>>render called for "+this);

    if (m_rebuildNeeded) {
      //System.out.println("rebuild needed!");
      // the object(s) to be drawn are simply inserted into the (content-)
      // scenegraph:
      BranchGroup contentBranch = m_canvas.getContentBranch();
      setTransformGroup(m_transformGroup);
      Group shape = getShape(properties);
      //System.out.println((shape.isLive() ? "is alive!" : "is not alive!"));
      m_transform3D = new Transform3D();
      //System.out.println(this+" has \n"+getTrafoFromOrigin()+ " and type is "+m_transform3D.getType());
      m_transform3D.set(getTrafoFromOrigin());
      m_transformGroup.setTransform(m_transform3D);
      //System.out.println("adding "+((TransformGroup)shape.getChild(0)).getChild(0));
      contentBranch.addChild(shape);
      m_rebuildNeeded = false;
    } else {
      //System.out.println(this+" has \n"+getTrafoFromOrigin()+ " and type is "+m_transform3D.getType());
      //System.out.println("alive="+getShape(properties).isLive()+"\n<<<<");
      m_transform3D.set(getTrafoFromOrigin());
      m_transformGroup.setTransform(m_transform3D);
    }
  }

  /**
   *  It may be necessary for a J3D Drawable to do some recalculations (bounds,
   *  etc.) that would be to expensive to do while updating (e.g. dragging a
   *  vector), so this method can be called by updaters to signal, that the
   *  update event cycle has finished.
   */
  public void updateFinished() {
  }

  /**
   *  This is solely used for setting the selection.
   */
  public void draw(MMCanvasObjectIF object) {
    J3DDrawable selectedDrawable = getCanvas().getSelectedDrawable();
    //System.out.println("selectedDrawable "+selectedDrawable);
    if (object.isSelected()) {
      //System.out.println("selected Object "+object);
      if (selectedDrawable != this) { //object has been selected, but is not yet rendered as selected
        getCanvas().setSelectedDrawable(this);
        setSelectedState();
      }
    } else if (selectedDrawable == this) { // check if object is still rendered as being selected
      removeSelectedState();
      selectedDrawable = null;
    }
  }

  /**
   * This method is called by the system, if this drawable is selected.
   * @see #removeSelectedState 
   */
  protected abstract void setSelectedState();

  /**
   * This method is called by the system, if another drawable after this drawable was selected.
   * @see #setSelectedState 
   */
  protected abstract void removeSelectedState();

  /**
   *  Since the actual drawing is done by the Java3D renderer, this method
   *  is not needed.
   */
  public final void draw(Object destination, DisplayProperties properties) {
  }

  /**
   *  Since the actual drawing is done by the Java3D renderer, this method
   *  is not needed.
   */

  public final void drawObject(MMCanvas destination, DisplayProperties properties) {
  }

  /**
   *  Selected Objects will be represented by a different appearance, so this
   *  method is not needed.
   */
  public final void drawSelection(Object destination, DisplayProperties properties) {
  }

  /**
   * Find out if the ray from viewer extended through canvas point (x,y) intersects 
   * with the shape of this object. This is done by using the Java3D picking-API.
   */
  public boolean isAtScreenLocation(int x, int y) {
    if (m_rebuildNeeded || !m_transformGroup.isLive())
      return false;
    //System.out.println("isAt.. called for "+this);
    m_pickCanvas.setShapeLocation(x, y);
    m_pickResult = m_pickCanvas.pickClosest();

    if (m_pickResult != null) { // something has been picked
      SceneGraphPath path = m_pickResult.getSceneGraphPath();
      for (int i = 0; i < path.nodeCount(); i++) {
        if (path.getNode(i).equals(getShape(new DisplayProperties()))) { // we have been picked
          //System.out.println(this+" was picked!");
          return true;
        }
      }
      //System.out.println("pickResult is "+m_pickResult.getObject() +" not "+getShape(new DisplayProperties()));
    }
    //System.out.println("PickResult is null!");
    return false;
  }

  /**
   * this method makes no sense in J3D.
   */
  public final Rectangle getBoundingBox() {
    return null;
  }

  /**
   *  Utility method for conversion between mumie and J3D
   */
  public static final Point3d MMDoubleTupel2Point3f(NumberTuple tuple) {
    return new Point3d(tuple.getEntry(1).getDouble(), tuple.getEntry(2).getDouble(), tuple.getEntry(3).getDouble());
  }

  private static final double EPSILON = 1e-10;

  /** Utility method. */
  public static final boolean isEqual(double[] array, Tuple3d tuple) {
    return Math.abs(tuple.x - array[0]) < EPSILON && Math.abs(tuple.y - array[1]) < EPSILON && Math.abs(tuple.z - array[2]) < EPSILON;
  }

  // some utility methods for building scene graphs follow
  /** Utility method. */
  protected TransformGroup translate(double x, double y, double z, Node node) {
    TransformGroup TG = new TransformGroup();
    m_vector.set((float) x, (float) y, (float) z);
    m_transform3D.set(m_vector);

    TG.setTransform(m_transform3D);
    TG.addChild(node);
    return TG;
  }
  /** Utility method. */
  protected TransformGroup translate(Tuple3d vector, Node node) {
    return translate(vector.x, vector.y, vector.z, node);
  }
  /** Utility method. */
  protected TransformGroup rotate(AxisAngle4d axisAngle, Node node) {
    TransformGroup TG = new TransformGroup();
    m_transform3D.set(axisAngle);
    TG.setTransform(m_transform3D);
    TG.addChild(node);
    return TG;
  }
  /** Utility method. */
  protected Vector3f generateNormal(Point3d p1, Point3d p2, Point3d p3) {

    m_v1.sub(p1, p2);
    m_v2.sub(p2, p3);
    m_vector.set((float) (m_v1.y * m_v2.z - m_v1.z * m_v2.y), (float) (m_v1.z * m_v2.x - m_v1.x * m_v2.z), (float) (m_v1.x * m_v2.y - m_v1.y * m_v2.x));
    m_vector.normalize();
    return m_vector;
  }
  /** Utility method. */
  protected Vector3f generateNormal(double[] p1, double[] p2, double[] p3) {
    m_p1.set(p1);
    m_p2.set(p2);
    m_p3.set(p3);
    return generateNormal(m_p1, m_p2, m_p3);
  }

  /*
   public static void main(String[] args){
   System.out.println(generateNormal(new Point3f(1.0f,1.0f,0),
   new Point3f(0,1.0f ,0),
   new Point3f(0,0,0)));
   }
   */
}
