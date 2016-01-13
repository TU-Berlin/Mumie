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

package net.mumie.mathletfactory.math.geom.affine;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.projective.ProjectivePoint;
import net.mumie.mathletfactory.util.xml.ExerciseObjectIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;


/**
 * This class acts as a base class for all affine points.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class AffinePoint extends AffineSpace implements ExerciseObjectIF{

	private boolean[] m_isEdited;

	/**	This field holds the current label. */
	private String m_label = null;
	
	/**	This field holds the current <code>hidden</code> flag. */
	private boolean m_hidden = false;

  // internal computations use projective coordinates!

  /**
   * Creates an AffinePoint living in a space of dimension
   * <code>externalDimension</code>. The coordinates are based on the
   * Number class type <code>entryClass</code>.
   */
  public AffinePoint (Class entryClass, int externalDimension){
    super(entryClass,0,externalDimension);
		m_isEdited = new boolean[externalDimension];
		for(int r = 0; r < m_isEdited.length; r++) {
			m_isEdited[r] = true;
		}
  }


  public AffinePoint (AffinePoint anAffinePoint) {
    this(anAffinePoint.getNumberClass(),anAffinePoint.getEnvDimension());
    setProjectiveCoordinates(anAffinePoint.getProjectiveCoordinates());
  }

  /**
   * Creates an AffinePoint with <code>projectiveCoords</code> as homogeneous
   * coordinates.
   */
  public AffinePoint (NumberTuple projectiveCoords){
    // internal space dimension of a point is zero:
    super (projectiveCoords.getNumberClass(), 0, projectiveCoords.getDimension()-1);
    setProjectiveCoordinates(projectiveCoords);
		m_isEdited = new boolean[projectiveCoords.getDimension()-1];
		for(int r = 0; r < m_isEdited.length; r++) {
			m_isEdited[r] = true;
		}
  }



  /**
   * Method setProjectiveCoordinates
   *
   * Internally we store homogeneous coordinates for any AffinePoint. These
   * may be set by this method. The affine coordinates can later be retrieved
   * by calling the method
   * @see #getAffineCoordinatesOfPoint()
   *
   * This method works without side effects.
   *
   * @param    projectiveCoords a  Vector
   *
   * @return   an AffinePoint
   *
   * @version  8/9/2002
   */
  public AffinePoint setProjectiveCoordinates(NumberTuple projectiveCoords){
    //NumberTuple[] tmp = new NumberTuple[]{(NumberTuple)projectiveCoords.deepCopy()};
    NumberTuple[] tmp = new NumberTuple[]{projectiveCoords};
    super.setProjectiveCoordinates(tmp);

    return this;
  }

  public AffinePoint setAffineCoordinates(NumberTuple affineCoordinates) {
    NumberTuple[] tmp = new NumberTuple[]{affineCoordinates};
    super.setAffineCoordinates(tmp);
    return this;
  }

  public AffinePoint setFromPoint(AffinePoint aPoint) {
    if( aPoint.getEnvDimension() == getEnvDimension() )
      setProjectiveCoordinates((NumberTuple)aPoint.getProjectiveCoordinatesOfPoint().deepCopy());
    else
      throw new IllegalArgumentException("space dimension of both affine points must coincide");
    return this;
  }


  public ProjectivePoint getProjectivePoint(){
    return new ProjectivePoint(getProjectiveCoordinatesOfPoint());
  }

  /**
   * Method getAffineCoordinatesOfPoint
   *
   * Returns the affine coordinates of this AffinePoint. Because internally
   * the AffinePoint is based on homogeneous coordinates we have to normalize
   * by dividing by the last coordinate.
   *
   * Method works without side effects.
   *
   * @return   a Vector
   *
   * @version  8/9/2002
   */
  public NumberTuple getAffineCoordinatesOfPoint(){
    //return (NumberTuple)super.getAffineCoordinates()[0].deepCopy();
	NumberTuple coordinates = super.getAffineCoordinates()[0];
	coordinates.setLabel(getLabel());
	return coordinates;
//    return super.getAffineCoordinates()[0];
  }

  /**
   * Method getProjectiveCoordinatesOfPoint returns the (internally stored)
   * homogeneous coordinates of this AffinePoint.
   *
   * Method does work without side effects.
   *
   * @return   a Vector
   *
   * @version  8/9/2002
   */
  public NumberTuple getProjectiveCoordinatesOfPoint(){
    //return (NumberTuple)m_projectiveHelper.getProjectiveBasis()[0].deepCopy();
    return m_projectiveHelper.getProjectiveBasis()[0];
  }

  public boolean equals(AffinePoint anAffinePoint) {
    return getProjectiveCoordinatesOfPoint().equals(anAffinePoint.getProjectiveCoordinatesOfPoint());
  }

  public boolean isInfinity() {
    int projIndex = getProjectiveCoordinatesOfPoint().getDimension();
    return getProjectiveCoordinatesOfPoint().getEntry(projIndex).isZero();
  }

  /**
   *  Utility method that converts an
   *  {@link net.mumie.mathletfactory.math.geom.affine.AffinePoint}
   *  Array to a {@link net.mumie.mathletfactory.math.algebra.linalg.NumberTuple} Array.
   */
  public static void AffinePoints2Tupels(AffinePoint[] points, NumberTuple[] tuples){
    for(int i=0;i<points.length; i++)
      tuples[i] = points[i].getProjectiveCoordinatesOfPoint();
  }

  public static NumberTuple[] affinePoints2Tuples(AffinePoint[] points){
    NumberTuple[] tuples = new NumberTuple[points.length];
    AffinePoints2Tupels(points, tuples);
    return tuples;
  }

  /**
   *  Utility method that converts a
   *  {@link net.mumie.mathletfactory.math.algebra.linalg.NumberTuple} Array to a
   *  {@link net.mumie.mathletfactory.math.geom.affine.AffinePoint} Array.
   */
  public static void tuples2AffinePoints(NumberTuple[] tuples, AffinePoint[] points){
    for(int i=0;i<points.length; i++)
      points[i].setProjectiveCoordinates(new NumberTuple[]{tuples[i]});
  }
  
  /**
   * Returns true if at least one entry was edited by the user.
   */
  public boolean isEdited() {
		for(int row = 1; row <= getEnvDimension(); row++) {
			if(isEdited(row))
				return true;
    }
    return false;
  }
  
  /**
   * Returns if the cell at the specified position was edited.
   */
  public boolean isEdited(int row) {
		return m_isEdited[row-1];
  }
  
  /**
   * Returns true if all entries were edited by the user.
   */
  public boolean isCompletelyEdited() {
		for(int row = 1; row <= getEnvDimension(); row++) {
			if( !isEdited(row))
				return false;
    }
    return true;
  }
  
	/**
	 * Sets the edited-flag for one cell.
	 */
	public void setEdited(int row, boolean edited) {
		m_isEdited[row-1] = edited;
	}

	/**
	 * Sets the edited-flag for all entries.
	 */
	public void setEdited(boolean edited) {
		// No global field "edited" is needed as for setEditable(boolean)
		for(int row = 1; row <= getEnvDimension(); row++) {
			setEdited(row, edited);
		}
	}
  
  public Node getMathMLNode() {
  	return getMathMLNode(XMLUtils.getDefaultDocument());
  }
  
  public Node getMathMLNode(Document doc) {
  	NumberTuple coordinates = getAffineCoordinatesOfPoint();
		for(int r = 1; r <= coordinates.getDimension(); r++) {
			coordinates.setEdited(r, isEdited(r));
		}
  	return coordinates.getMathMLNode(doc);
  }
  
  public void setMathMLNode(Node content) {
  	NumberTuple coordinates = new NumberTuple(getNumberClass(), 1);
  	coordinates.setMathMLNode(content);
  	setAffineCoordinates(coordinates);
		for(int r = 1; r <= coordinates.getDimension(); r++) {
			setEdited(r, coordinates.isEdited(r));
		}
  }

	public String getLabel() {
		return m_label;
	}
	
	public void setLabel(String label) {
		m_label = label;
	}

	public boolean isHidden() {
		return m_hidden;
	}
	
	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}
}