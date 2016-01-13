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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.mumie.mathletfactory.math.algebra.GroupElementIF;
import net.mumie.mathletfactory.math.algebra.geomgroup.AffineGroupElement;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.geom.GeometryIF;
import net.mumie.mathletfactory.math.geom.projective.ProjectivePoint;
import net.mumie.mathletfactory.math.geom.projective.ProjectiveSpace;

/**
 * This class represents an affine space with given dimension living in another space
 * with a given dimension.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public class AffineSpace implements AffineGeometryIF {

	// we use projective geometry to perform the actual computations;
	protected ProjectiveSpace m_projectiveHelper;

	/**
	 * Creates an <code>AffineSpace</code> of (internal) dimension <code>
	 * dim</code> living in a space of dimension <code>extDim</code>.
	 * If the internal dimension equals zero the <code>AffineSpace</code> is
	 * really an affine point, internal dimension equal to one describes an
	 * affine line,...
	 * <code>entryClass</code> is the number class the coordinates are based on,
	 * i.e. MDouble.class, MRational.class,...
	 */
	public AffineSpace(Class entryClass, int dim, int extDim) {
		m_projectiveHelper = new ProjectiveSpace(entryClass, dim, extDim);
	}

	public AffineSpace(AffineSpace anAffineSpace) {
		m_projectiveHelper =
			new ProjectiveSpace(
				anAffineSpace.getNumberClass(),
				anAffineSpace.getDimension(),
				anAffineSpace.getEnvDimension());
		setProjectiveCoordinates(anAffineSpace.getProjectiveCoordinates());
	}

	/**
	 * Performs the intersection of this <code>AffineSpace</code> and <code>
	 * anAffineSpace</code>: this <code>AffineSpace</code> will become the
	 * intersection space of both and will be returned by the method.
	 * @version  8/12/2002
	 */
	public AffineSpace intersect(AffineSpace anAffineSpace) {
		// to check if hyperplanes are the same...
		m_projectiveHelper =
			(ProjectiveSpace) m_projectiveHelper.intersected(
				anAffineSpace.m_projectiveHelper);
		return this;
	}

  /**
   * Returns a new affine space that is the intersection of this and the given affine space.
   */
  public AffineSpace intersected(AffineSpace anAffineSpace){
    return intersected(this, anAffineSpace);
  }

  /**
   * Returns a new affine space that is the intersection of the two given affine spaces.
   */
	public static AffineSpace intersected(
		AffineSpace affineSpace1,
		AffineSpace affineSpace2) {
		AffineSpace intersection = new AffineSpace(affineSpace1);
		// look for an affine point, that has no zero in the projective coordinate
		intersection.intersect(affineSpace2);
		NumberTuple[] base = intersection.getProjectiveCoordinates();
		NumberTuple pureAffinePointCoords = null;
		for (int i = 0; i < base.length; i++)
			if (!base[i].getEntryRef(base[i].getRowCount()).isZero())
				pureAffinePointCoords = base[i];
		if (pureAffinePointCoords == null)
			// all coords have a zero projective coordinate
			return intersection;
		// check if any new base vectors have the projective coordinate zero, if so
		// replace that vector by a linear combination of it and pureAffinePointCoords:
		for (int i = 0; i < base.length; i++)
			if (base[i].getEntryRef(base[i].getRowCount()).isZero())
				base[i].addTo(pureAffinePointCoords);
		intersection.setProjectiveCoordinates(base);
		return intersection;
	}

	/**
	 * Returns the (internal) dimension of this <code>AffineSpace</code>, i.e.
	 * zero for an affine point, one for an affine line, ...
	 *
	 * @version  8/12/2002
	 */
	public int getDimension() {
		return m_projectiveHelper.getDimension();
	}

	/**
	 * Returns the dimension of the environmental space this <code>AffineSpace
	 * </code> is living in.
   *
   * @version  8/12/2002
	 */
	public int getEnvDimension() {
		return m_projectiveHelper.getEnvDimension();
	}

  public boolean hasDefiningPointAtInfinity() {
    NumberTuple[] tuples = getProjectiveCoordinates();
    for (int i = 0; i < tuples.length; i++) {
      NumberTuple tuple = tuples[i];
      if (tuple.getEntry(tuple.getDimension()).isZero()) {
        return true;
      }
    }
    return false;
  }

	public AffineSpace setFromPoints(AffinePoint points[]) {
		ProjectivePoint pp[] = new ProjectivePoint[points.length];
		for (int i = 0; i < points.length; ++i) {
			pp[i] = (ProjectivePoint) points[i].getProjectivePoint();
		}
		m_projectiveHelper = new ProjectiveSpace(pp);
		return this;
	}

	public AffineSpace setProjectiveCoordinates(NumberTuple[] coords) {
		m_projectiveHelper.setProjectiveBasis(coords);
		return this;
	}

	public AffineSpace setAffineCoordinates(NumberTuple[] affineCoords) {
		m_projectiveHelper.setAffineBasis(affineCoords);
		return this;
	}

	/**
	 * Returns the geometry type for <code>AffineSpace</code>, which is stored
	 * as public static field <code>GeometryIF.AFFINE_GEOMETRY</code>.
	 *
	 * @see GeometryIF#AFFINE_GEOMETRY
	 * @version  8/12/2002
	 */
	public int getGeomType() {
		return AFFINE_GEOMETRY;
	}

	public NumberTuple[] getAffineCoordinates() {
		return m_projectiveHelper.getAffineBasis();
	}

	public NumberTuple[] getProjectiveCoordinates() {
		return m_projectiveHelper.getProjectiveBasis();
	}

  protected NumberMatrix getProjectiveBasisMatrixRef(){
    return m_projectiveHelper.getBasisMatrixRef();
  }

	public AffineGeometryIF groupAction(AffineGroupElement actingGroupElement) {
		m_projectiveHelper.groupAction(actingGroupElement);
		return this;
	}

	public GeometryIF groupAction(GroupElementIF actingGroupElement) {
		//TODO: check for group consistency
		return groupAction((AffineGroupElement) actingGroupElement);
	}

	public Class getNumberClass() {
		return m_projectiveHelper.getNumberClass();
	}

	public AffineSpace deepCopy() {
		try {
			Constructor constructor =
				getClass().getConstructor(new Class[] { getClass()});
			return (AffineSpace) constructor.newInstance(new Object[] { this });

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
				getClass() + " needs a constructor that takes a " + getClass());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

  public String toString(){
    String retVal = "[";
    NumberMatrix projMatrix = getProjectiveBasisMatrixRef();
    for(int i=1;i<projMatrix.getRowCount();i++){
      if(projMatrix.getColumnCount() > 1)
        retVal += "[";
      for(int j=1;j<=projMatrix.getColumnCount();j++){
        retVal += projMatrix.getEntryAsNumberRef(i, j).getDouble() /
          projMatrix.getEntryAsNumberRef(projMatrix.getRowCount(), j).getDouble();
        if(i<projMatrix.getRowCount()-1 || j < projMatrix.getColumnCount())
          retVal += ",";
        else
          retVal += "]";
      }
      if(projMatrix.getColumnCount() > 1)
        retVal += "]";
    }
    //System.out.println(retVal);
    return retVal;
  }

  public int hashCode(){
    int retVal = 0;
    NumberMatrix projMatrix = getProjectiveBasisMatrixRef();
    for(int i=1;i<projMatrix.getRowCount();i++)
      for(int j=1;j<=projMatrix.getColumnCount();j++)
        retVal += projMatrix.getEntryAsNumberRef(i, j).hashCode() + projMatrix.getEntryAsNumberRef(i, j).hashCode() >>> (i+j);
    return retVal;
  }
}
