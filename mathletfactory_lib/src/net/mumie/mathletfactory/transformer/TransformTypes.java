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

package net.mumie.mathletfactory.transformer;

/**
 * This interface contains all pre-defined constants for screen and transform types.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public interface TransformTypes {

	/** Constant for an empty transform. */
	public static final int NO_TRANSFORM_TYPE = -1;

  /** Standard transform for MMStrings */
  public static final int STRING_TRANSFORM = 5;

	/**
	 * Defines the default transformation type for affine geometrical objects. An
	 * {@link net.mumie.mathletfactory.mmobject.geom.affine.MMAffine2DPoint} with internal
	 * (cartesian) coordinates <it>(x,y)</it> will be "converted" to world view just by
	 * overtaking these cartesian coordinates.
	 */
	public static final int AFFINE_DEFAULT_TRANSFORM = 100;

	/**
	 *
	 */
	public static final int LINEAR_MAP_TO_MATRIX_TRANSFORM = 1000;

	public static final int RELATION_TRANSFORM = 1001;

  /** Standard transform for functions */
	public static final int FUNCTION_TRANSFORM = 1002;

  /** Standard transform for sets */
	public static final int SET_TRANSFORM = 1003;

  /** Standard transform for intervals */
	public static final int INTERVAL_TRANSFORM = 1004;

  /** Standard transform for intervals containing variables */
  public static final int INTERVAL_VARIABLE_TRANSFORM = 1005;

  /** Standard transform for number matrices */
	public static final int NUMBER_MATRIX_TRANSFORM = 1010;

  /** Standard transform for string matrices */
  public static final int STRING_MATRIX_TRANSFORM = 1011;

  /** Standard transform for numbers */
	public static final int NUMBER_STD_TRANSFORM = 1100;

  /** Operation transform for numbers */
	public static final int NUMBER_OP_TRANSFORM = 1110;

  /** Simple transform for numbers */
	public static final int NUMBER_SIMPLE_TRANSFORM = 1101;

  /** Slider transform for numbers */
	public static final int NUMBER_SLIDER_TRANSFORM = 1102;

  /** Standard transform for equation systems */
	public static final int EQUATION_SYSTEM_TRANSFORM = 1103;

  /** Standard transform for dimensions */
	public static final int DIMENSION_TRANSFORM = 1104;

	/**
	 * Use this transformtype to visualise "MM" instances of <code>NumberVectorSpace</code>
	 * (for example {@link net.mumie.mathletfactory.mmobject.algebra.MMDefaultR2}) as the
	 * pointed affine space of appropriate dimension.
	 */
	public static final int NUMBERVECTORSPACE_DEFAULT_TO_DEFAULTBASE_TRANSFORM =
		10000;

	/**
	 * Use this transform type to visualise instances of
	 * {@link net.mumie.mathletfactory.mmobject.analysis.function.MMOneChainInRIF} as
	 * a two dimensional affine graph, treating the parameter value <code>t</code> as the
	 * x coordinate.
	 */
	public static final int ONE_CHAIN_1D_AFFINE_GRAPH_TRANSFORM = 1201;

	/**
	 * Use this transform type to visualise instances of
	 * {@link net.mumie.mathletfactory.mmobject.geom.MMOneChainInRNIF}
	 * as a two dimensional affine graph, more precise, visualise the curves
	 * <code>t --&gt; (x<sub>i</sub>(t),y<sub>i</sub>(t))</code> (with <code>t</code> being
	 * element of the given domain).
	 */
	public static final int ONE_CHAIN_2D_AFFINE_GRAPH_TRANSFORM = 1202;

	public static final int FUNCTION_OVER_RN_AFFINE_GRAPH_TRANSFORM = 1205;

	/**
	 * Use this transform type to visualise "MM"-implementations of
	 * {@link net.mumie.mathletfactory.algebra.VectorField2DOverR2IF} as a grid of vector
	 * arrows representing the field strength and direction.
	 */
	public static final int VECTOR_FIELD2D_ARROW_TRANSFORM = 1210;

	public static final int NUMBERSET2D_TRANSFORM = 1300;

  public static final int AFFINE_3D_SUBSPACE_TRANSFORM = 1400;
  public static final int AFFINE_2D_SUBSPACE_TRANSFORM = 1401;

	/**
	 * This transform type is dedicted to be used for test runs with new type transformers.
	 */
	public static final int TEST_TRANSFORM = 2000;

	public static final int COMPLEX_VECTOR_TRANSFORM = 3000;

  public static final int SEQUENCE_TRANSFORM_1D = 4000;
  public static final int SEQUENCE_TRANSFORM_2D = 4001;
  public static final int SERIES_TRANSFORM_2D = 4001;
  
  /** Transform type for visualizing images. */
  public static final int IMAGE_TRANSFORM = 5000;

  /** Transform type for visualizing matrices as gray tone images (matrix bitmaps). */
  public static final int MATRIX_IMAGE_TRANSFORM = 5010;
  
	/**
	 * If a user wants to extend the <code>GeneralTransformer</code> by additional
	 * transform types, these new types should be assigned integer values bigger than
	 * <code>USER_TRANSFORM_TYPE</code> to avoid conflicts with already existing types.
	 */
	public static final int USER_TRANSFORM_TYPE = Integer.MAX_VALUE / 2;

	/**
	 * This transform type is dedicted to experimental use for external developers, who
	 * want to test a new, self generated objects and/or transformers.
	 * <p>
	 * The user will have to do (at least) the following steps to get his setup working.
	 * We assume that the user has generated a class "MyObject" implementing
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF} and a transformer
	 * "MyTransformer" inheriting from
	 * {@link net.mumie.mathletfactory.transformer.GeneralTransformer}:
	 * <ol>
	 * <li>
	 * Register your object and your transformer:<br>
	 * GeneralTransformer.register("MyTransformer".class,GeneralTransformer.USER_TEST_TRANSFORM,
	 * GeneralTransformer.<screen type>, "MyObject".class);
	 * </li>
	 * <li>
	 * Create an instance of "MyObject":<br>
	 * MyObject o = new MyObject();
	 * </li>
	 * <li>
	 * Assign the transform type to your object instance (display will be the JComponent
	 * ,for example the <code>MMCanvas</code>, in which the object shall be displayed):<br>
	 * o.addTransformer(GeneralTransformer.USER_TEST_TRANSFORM,GeneralTransformer.<st>, display);
	 * </li>
	 */
	public static final int USER_TEST_TRANSFORM = USER_TRANSFORM_TYPE + 10;
}
