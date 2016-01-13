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

package net.mumie.mathletfactory.math.util;

import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverRIF;

/**
 * Library for function rendering algorithms.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public final class FunctionRenderLib {

	private static boolean m_debug = false;

	/*
	 * used in method findIntervals() when trying to draw near to function singularities
	 */
	private static double m_refinement = 1000;

	// wird fuer getFunctionSpeedOnScreen benoetigt
	private static double[] m_tmp1 = new double[2], m_tmp2 = new double[2];

	private static int m_currentArraySize = 16;

	//Diese arrays werden fuer das FindIntervalsAdvanced benoetigt, da sie dort
	//nicht immerwieder neu angelegt werden sollten
	// storage for the function values of the IntegerFunction:
	private static double[] visValues = new double[m_currentArraySize];
	// array fuer die dazugehoerigen funktionswerte
	private static double[] tValues = new double[m_currentArraySize];
	// array fuer die intervalbounds
	private static double[][] outLimits = new double[m_currentArraySize+1][2];
	// wir brauchen diese Punkte zur Verfeinerten Suche
	private static double[][] outLimitHelpPoints = new double[m_currentArraySize][2];

  /**
   * Description:
   * This algorithm computes a set of subintervals of the interval
   * [minT, maxT], i. e. a set of double values tmin[i], tmax[i] for
   * 0<=i<verticesCount with minT<=tmin[i]<= tmax[i]<= maxT. These
   * boundary values of the resulting intervals are held in the two
   * dimensional array outLimits with the following correspondence: double [i]
   * [0] = tmin [i], double[i] [1] = tmax [i] for 0<=i<verticesCount. Observe
   * the algorithm will in general compute essentially less separation values
   * as given verticesCount.
   *
   *
   * From the input we derive the following equidistant decomposition of the
   * interval [minT, maxT] with t values called t[k] and t[k] = minT+k*dT
   * 0<=k<=verticesCount-1 with dT:=(maxT-minT)/(verticesCount-1) defining
   * the stepwidth.
   *
   * The subintervals [ tmin[i],tmax[i] ] shall have the following properties:
   *
   *
   * If all points are visible leave the interval unchanged, i.e. outlimits
   * [0][0] = minT, outLimits[0][1] = maxT and return 1.
   *
   * Otherwise the resulting interval borders shall have the following
   * properties:
   *
   * Let M:= the number of resulting subintervals (M>=1 but perhaps equal
   * to 1!)
   *
   * minT <= tmin[0]<tmax[0]<tmin[1]<....<tmin[m]<tmax[M]
   *
   * f(tmin[i]) > 0
   * < 0 f (t [k1])<0 of f(t[k1])>0 && f(t[k1-1]) <0
   *
   *
   *
   * Usage:
   * We do need this algorithm mainly for the following problem: Given a
   * vector-valued function t --> (x(t),y(t)) with x(t),y(t) being
   * (essentially) coordinates on a two dimensional screen. Define an
   * interger-valued function t-->i(t) like the following:
   * i(t)<0 := x(t),y(t) is visible on the screen.
   * i(t)>0 := x(t),y(t) is not visible on the screen but x(t),y(t) are
   * finite
   * i(t) := 0 x(t) or y(t) is infinite or NaN.
   *
   * Remark:
   * Of course here is a simple extension for any vector-valued functions
   * with Range in R^n and the check if the values are contained within a n-
   * dimensional quad.
   *
   *
   * @param f the indicator function, an instance of
   * {@link IntegerFunctionOverRIF}
   *
   * @param minT the lower bound of the domain interval to examine
   * @param maxT the upper bound of the domain interval to examine
   *
   * @param verticesCount the desired discretizaion
   * @param outLimits array for the computed sub-interval bounds
   * @return the number of split intervals
   */
	public static int findIntervals(
		IntegerFunctionOverRIF f,
		double minT,
		double maxT,
		int verticesCount,
		final double[][] outLimits) {

		// storage for the function values of the IntegerFunction:
		int[] values = new int[verticesCount];

		// dt: the starting step width for the first sampling
		double dt = (maxT - minT) / (verticesCount - 1);
		// initial sampling of the index function:
		for (int i = 0; i < verticesCount; i++) {
			values[i] = f.evaluate(minT + i * dt);
		}

		int M = 0;

		if (values[0] < 0)
			outLimits[0][0] = minT;
		for (int i = 1; i < verticesCount; i++) {
			if ((values[i] < 0) && (values[i - 1] >= 0)) {
				outLimits[M][0] = minT + i * dt;
				if (m_debug) {
					System.out.println("index " + i + " notvisible-->visible");
				}
			}
			if ((values[i] >= 0) && (values[i - 1] < 0)) {
				outLimits[M][1] = minT + (i - 1) * dt;
				if (m_debug) {
					System.out.println("index " + i + " notvisible-->visible");
				}
				M++;
			}
		}

		if (values[verticesCount - 1] < 0) {
			outLimits[M][1] = maxT;
			M++;
		}

		for (int i = 0; i < M; i++) {
			if (outLimits[i][0] != minT) {
				// that is: f(b) < 0:
				double b = outLimits[i][0];
				int pos = (int) Math.round(((outLimits[i][0] - minT) / dt));
				double a = minT + (pos - 1) * dt;

				while (b - a > dt / m_refinement) {
					if (f.evaluate((a + b) / 2) < 0)
						b = (a + b) / 2;
					else
						a = (a + b) / 2;
				}
				if (f.evaluate(a) == 0)
					outLimits[i][0] = b;
				else
					outLimits[i][0] = a;
			}
			if (outLimits[i][1] != maxT) {
				// f(a) < 0, i.e. visible:
				double a = outLimits[i][1];
				int pos = (int) Math.round(((outLimits[i][1] - minT) / dt));
				double b = minT + (pos + 1) * dt;
				while (b - a > dt / m_refinement) {
					if (f.evaluate((a + b) / 2) < 0)
						a = (a + b) / 2;
					else
						b = (a + b) / 2;
				}
				if (f.evaluate(b) == 0)
					outLimits[i][1] = a;
				else
					outLimits[i][1] = b;
			}
		}

		for (int i = 0; i < M; i++) {
			if (outLimits[i][0] == outLimits[i][1]) {
				for (int h = i; h < M; h++) {
					outLimits[h][0] = outLimits[h + 1][0];
					outLimits[h][1] = outLimits[h + 1][1];
				}
				M--;
			}
		}

		return M;
	}

//	public static int findIntervalsAdaptive(
//		FunctionOverRIF visIndicatorFunction,
//		VectorFunctionOverRIF masterFunction,
//		double eps,
//		double m_oneMathUnitDistanceOnScreenX,
//		double m_oneMathUnitDistanceOnScreenY,
//		double minT,
//		double maxT,
//		double screenRadius,
//		double[][] outLimits) {
//
//		// startAnzahl der Intervalle  - wird dynamisch angepasst
//		int maxIntervalCount = 100;
//		// Zaehlvariable fuer die Intervalle
//		int verticesCount = 0;
//		// the starting step width for the first sampling
//		double stepWidth = 0;
//		// das interval wird auf t durchlaufen
//		double t = minT;
//		// storage for the function values of the IntegerFunction:
//		double[] visValues = new double[maxIntervalCount];
//		// array fuer die dazugehoerigen funktionswerte
//		double[] tValues = new double[maxIntervalCount];
//		// ersten Wert berechnen
//		tValues[0] = t;
//		visValues[0] = visIndicatorFunction.evaluate(t);
//
//
//		while (t < maxT) {
//
//			// hier passiert die dynamische Anpassung der Schrittweite
//			// in Abhaenigigkeit zur Sichtbarkeit bzw. zum Abstand zwischen dem
//			// letzten Punkt und dem sichtbaren Bereich und der Funktionsgeschwindigkeit an dieser Stelle
//
////			System.out.println("t: "+t+" functionSpeed: "+getFunctionSpeedOnScreen(t,masterFunction,eps,m_oneMathUnitDistanceOnScreenX,m_oneMathUnitDistanceOnScreenY)+" visIndicatorFunction: "+visIndicatorFunction.evaluate(t)+" screenRadius: "+screenRadius);
//
//			// wenn ausserhalb des sichtbaren bereiches (vis(x)>0) -> 1/speedIndicatorFunction(x)*visIndicatorFunction(x)
//			if (visValues[verticesCount] > 0) stepWidth = 1/getFunctionSpeedOnScreen(t,masterFunction,eps,m_oneMathUnitDistanceOnScreenX,m_oneMathUnitDistanceOnScreenY)*visIndicatorFunction.evaluate(t);
//			// wenn innerhalb des sichtbaren bereiches (vis(x)<0) -> 1/speedIndicatorFunction(x)*screenradius
////hier kann man die genauigkeit variieren
//			if (visValues[verticesCount] < 0) stepWidth = 1/getFunctionSpeedOnScreen(t,masterFunction,eps,m_oneMathUnitDistanceOnScreenX,m_oneMathUnitDistanceOnScreenY)*screenRadius;
//			// wenn unendlich (vis(x)=0): -> dann tMax-tMin/1000
//			if (visValues[verticesCount] == 0) stepWidth = (maxT-minT)/1000;
//
//			verticesCount++;
////			System.out.println("verticesCount: "+verticesCount+ " stepwidth: "+stepWidth);
//			// dynamische verdopplung des Arrays wenn mehr Samplepunkte benoetigt werden
//			if (verticesCount >= maxIntervalCount) {
//				maxIntervalCount *= 2;
//				double[] visTmp = visValues;
//				double[] tTmp = tValues;
//				visValues = new double[maxIntervalCount];
//				tValues = new double[maxIntervalCount];
//				for (int i=0; i<tTmp.length; i++) {
//					tValues[i] = tTmp[i];
//					visValues[i] = visTmp[i];
//				}
//			}
//			t += stepWidth;
//			tValues[verticesCount] = t;
//			visValues[verticesCount] = visIndicatorFunction.evaluate(t);
//		}
//
//		maxIntervalCount = verticesCount;
//		verticesCount = 0;
//
////		outLimits = new double[maxIntervalCount][2];
//		// wir brauchen diese Punkte zur Verfeinerten Suche
//		double[][] outLimitHelpPoints = new double[maxIntervalCount][2];
//
//		// wenn der erste punkt im interval sichtbar ist, dann wird das der erste punkt der sichtbaren intervalle
//		if (visValues[0] < 0)
//			outLimits[0][0] = minT;
////			System.out.println("i: " +0+ " visValue: "+ visValues[0]);
//
//		// durchlauf der funktionswerte
//		for (int i = 1; i < maxIntervalCount; i++) {
//			// wenn der aktuelle punkt sichtbar ist und der punkt davor unsichbar war, dann ist dies ein neuer intervalanfang
////			System.out.println("i: " +i+ " visValue: "+ visValues[i]);
//			if ((visValues[i] < 0) && (visValues[i - 1] >= 0)) {
//				outLimits[verticesCount][0] = tValues[i];
//				outLimitHelpPoints[verticesCount][0] = tValues[i-1];
//				if (m_debug) {
//					System.out.println("index " + i + " notvisible-->visible");
//				}
//			}
//			// wenn der aktuelle punkt unsichtbar ist und der punkt davor sichtbar war, dann ist dies ein intervalende
//			// und verticesCount wird eins hochgezaehlt da ein weiteres Interval abgeschlossen ist
//			if ((visValues[i] >= 0) && (visValues[i - 1] < 0)) {
//				outLimits[verticesCount][1] = tValues[i];
//				outLimitHelpPoints[verticesCount][1] = tValues[i-1];
//				if (m_debug) {
//					System.out.println("index " + i + " visible-->notvisible");
//				}
//				verticesCount++;
//			}
//		}
//		// wenn der letzte wert sichtbar war, dann wird hier das letzte intervalende gesetzt
//		if (visValues[maxIntervalCount - 1] < 0) {
//			outLimits[verticesCount][1] = maxT;
//			verticesCount++;
//		}
//
//		maxIntervalCount = verticesCount;
//		verticesCount = 0;
//
//		// hier wird die suche verfeinert
//		for (int i = 0; i < maxIntervalCount; i++) {
//			// feinere suche macht nur sinn wenn der startpunkt des intervals
//			// nicht der startpunkt des definitionsintervals ist
//			if (outLimits[i][0] != minT) {
//				// that is: f(b) < 0:
//				double b = outLimits[i][0];
//				double a = outLimitHelpPoints[i][0];
//				double dt = Math.abs(b - a);
//				while (b - a > dt / m_refinement) {
//					if (visIndicatorFunction.evaluate((a + b) / 2) < 0)
//						b = (a + b) / 2;
//					else
//						a = (a + b) / 2;
//				}
//				if (visIndicatorFunction.evaluate(a) == 0)
//					outLimits[i][0] = b;
//				else
//					outLimits[i][0] = a;
//			}
//			// feinere suche macht nur sinn wenn der endpunkt des intervals
//			// nicht der endpunkt des definitionsintervals ist
//			if (outLimits[i][1] != maxT) {
//				// f(a) < 0, i.e. visible:
//				double a = outLimitHelpPoints[i][1];
//				double b = outLimits[i][1];
//				double dt = Math.abs(b - a);
//				while (b - a > dt / m_refinement) {
//					if (visIndicatorFunction.evaluate((a + b) / 2) < 0)
//						a = (a + b) / 2;
//					else
//						b = (a + b) / 2;
//				}
//				if (visIndicatorFunction.evaluate(b) == 0)
//					outLimits[i][1] = a;
//				else
//					outLimits[i][1] = b;
//			}
//		}
//
//		for (int i = 0; i < maxIntervalCount; i++) {
//			if (outLimits[i][0] == outLimits[i][1]) {
//				for (int h = i; h < maxIntervalCount; h++) {
//					outLimits[h][0] = outLimits[h + 1][0];
//					outLimits[h][1] = outLimits[h + 1][1];
//				}
//				maxIntervalCount--;
//			}
//		}
////		System.out.println("maxIntervalCount: "+maxIntervalCount);
//
//		return maxIntervalCount;
//	}
	public static double[][] findIntervalsAdaptive(
		FunctionOverRIF visIndicatorFunction,
		VectorFunctionOverRIF masterFunction,
		double eps,
		double m_oneMathUnitDistanceOnScreenX,
		double m_oneMathUnitDistanceOnScreenY,
		double minT,
		double maxT,
		double screenRadius) {

		// startAnzahl der Intervalle  - wird dynamisch angepasst
		int maxIntervalCount = m_currentArraySize;
		// Zaehlvariable fuer die Intervalle
		int verticesCount = 0;
		// the starting step width for the first sampling
		double stepWidth = 0;
		// das interval wird auf t durchlaufen
		double t = minT;
		// ersten Wert berechnen
		tValues[0] = t;
		visValues[0] = visIndicatorFunction.evaluate(t);


		while (t < maxT) {

			// hier passiert die dynamische Anpassung der Schrittweite
			// in Abhaenigigkeit zur Sichtbarkeit bzw. zum Abstand zwischen dem
			// letzten Punkt und dem sichtbaren Bereich und der Funktionsgeschwindigkeit an dieser Stelle

//			System.out.println("t: "+t+" functionSpeed: "+getFunctionSpeedOnScreen(t,masterFunction,eps,m_oneMathUnitDistanceOnScreenX,m_oneMathUnitDistanceOnScreenY)+" visIndicatorFunction: "+visIndicatorFunction.evaluate(t)+" screenRadius: "+screenRadius);

			// wenn ausserhalb des sichtbaren bereiches (vis(x)>0) -> 1/speedIndicatorFunction(x)*visIndicatorFunction(x)
			if (visValues[verticesCount] > 0) stepWidth = 1/getFunctionSpeedOnScreen(t,masterFunction,eps,m_oneMathUnitDistanceOnScreenX,m_oneMathUnitDistanceOnScreenY)*visIndicatorFunction.evaluate(t);
			// wenn innerhalb des sichtbaren bereiches (vis(x)<0) -> 1/speedIndicatorFunction(x)*screenradius
//hier kann man die genauigkeit variieren
			if (visValues[verticesCount] < 0) stepWidth = 1/getFunctionSpeedOnScreen(t,masterFunction,eps,m_oneMathUnitDistanceOnScreenX,m_oneMathUnitDistanceOnScreenY)*screenRadius;
			// wenn unendlich (vis(x)=0): -> dann tMax-tMin/1000
			if (visValues[verticesCount] == 0) stepWidth = (maxT-minT)/1000;

			verticesCount++;
//			System.out.println("verticesCount: "+verticesCount+ " stepwidth: "+stepWidth);
			// dynamische verdopplung des Arrays wenn mehr Samplepunkte benoetigt werden
			if (verticesCount >= maxIntervalCount) {
				maxIntervalCount *= 2;
				double[] visTmp = visValues;
				double[] tTmp = tValues;
				visValues = new double[maxIntervalCount];
				tValues = new double[maxIntervalCount];
				for (int i=0; i<tTmp.length; i++) {
					tValues[i] = tTmp[i];
					visValues[i] = visTmp[i];
				}
			}
			t += stepWidth;
			tValues[verticesCount] = t;
			visValues[verticesCount] = visIndicatorFunction.evaluate(t);
		}

		maxIntervalCount = verticesCount;
		verticesCount = 0;

		if (m_currentArraySize<maxIntervalCount) {
			m_currentArraySize = maxIntervalCount;
			//!!!muss ein feld groesser sein, da wir den letzten punkt zur uebergabe der realen feldgroesse brauchen
			outLimits = new double[m_currentArraySize+1][2];
			// wir brauchen diese Punkte zur Verfeinerten Suche
			outLimitHelpPoints = new double[maxIntervalCount][2];
		}

		// wenn der erste punkt im interval sichtbar ist, dann wird das der erste punkt der sichtbaren intervalle
		if (visValues[0] < 0)
			outLimits[0][0] = minT;
//			System.out.println("i: " +0+ " visValue: "+ visValues[0]);

		// durchlauf der funktionswerte
		for (int i = 1; i < maxIntervalCount; i++) {
			// wenn der aktuelle punkt sichtbar ist und der punkt davor unsichbar war, dann ist dies ein neuer intervalanfang
//			System.out.println("i: " +i+ " visValue: "+ visValues[i]);
			if ((visValues[i] < 0) && (visValues[i - 1] >= 0)) {
				outLimits[verticesCount][0] = tValues[i];
				outLimitHelpPoints[verticesCount][0] = tValues[i-1];
				if (m_debug) {
					System.out.println("index " + i + " notvisible-->visible");
				}
			}
			// wenn der aktuelle punkt unsichtbar ist und der punkt davor sichtbar war, dann ist dies ein intervalende
			// und verticesCount wird eins hochgezaehlt da ein weiteres Interval abgeschlossen ist
			if ((visValues[i] >= 0) && (visValues[i - 1] < 0)) {
				outLimits[verticesCount][1] = tValues[i];
				outLimitHelpPoints[verticesCount][1] = tValues[i-1];
				if (m_debug) {
					System.out.println("index " + i + " visible-->notvisible");
				}
				verticesCount++;
			}
		}
		// wenn der letzte wert sichtbar war, dann wird hier das letzte intervalende gesetzt
		if (visValues[maxIntervalCount - 1] < 0) {
			outLimits[verticesCount][1] = maxT;
			verticesCount++;
		}

		maxIntervalCount = verticesCount;
		verticesCount = 0;

		// hier wird die suche verfeinert
		for (int i = 0; i < maxIntervalCount; i++) {
			// feinere suche macht nur sinn wenn der startpunkt des intervals
			// nicht der startpunkt des definitionsintervals ist
			if (outLimits[i][0] != minT) {
				// that is: f(b) < 0:
				double b = outLimits[i][0];
				double a = outLimitHelpPoints[i][0];
				double dt = Math.abs(b - a);
				while (b - a > dt / m_refinement) {
					if (visIndicatorFunction.evaluate((a + b) / 2) < 0)
						b = (a + b) / 2;
					else
						a = (a + b) / 2;
				}
				if (visIndicatorFunction.evaluate(a) == 0)
					outLimits[i][0] = b;
				else
					outLimits[i][0] = a;
			}
			// feinere suche macht nur sinn wenn der endpunkt des intervals
			// nicht der endpunkt des definitionsintervals ist
			if (outLimits[i][1] != maxT) {
				// f(a) < 0, i.e. visible:
				double a = outLimitHelpPoints[i][1];
				double b = outLimits[i][1];
				double dt = Math.abs(b - a);
				while (b - a > dt / m_refinement) {
					if (visIndicatorFunction.evaluate((a + b) / 2) < 0)
						a = (a + b) / 2;
					else
						b = (a + b) / 2;
				}
				if (visIndicatorFunction.evaluate(b) == 0)
					outLimits[i][1] = a;
				else
					outLimits[i][1] = b;
			}
		}

		for (int i = 0; i < maxIntervalCount; i++) {
			if (outLimits[i][0] == outLimits[i][1]) {
				for (int h = i; h < maxIntervalCount; h++) {
					outLimits[h][0] = outLimits[h + 1][0];
					outLimits[h][1] = outLimits[h + 1][1];
				}
				maxIntervalCount--;
			}
		}
		outLimits[outLimits.length-1][0]=maxIntervalCount;
		return outLimits;
	}

	/**
	 * Given a set of intervals [tmin[i], tmax[i] ] with 0 &le; i &le;
	 * <code>splitCount</code> and a (complete) discretisation count of
	 * <code>allVerticesCount</code> this method computes how much vertices should
	 * be used on each subinterval. As result the number of vertices used within
	 * the i-th interval is (nearly) proportional to the ratio
   * (tmax [i]- tmin[i])/ (length of all intervals).
	 * <br>
	 * The algorithm shall compute at least a vertex count of 2 for each
	 * subinterval (so in further use it will be guaranteed that the vertices
	 * corresponding to the tmin [i], tmax[i] will belong to the discretisation).
	 * Consequently we have the constraint that <code>allVerticesCount</code> &ge;
	 * <code>2*splitCount</code>.
	 * <br>
	 * <code>splitBounds</code> is the double array holding the interval
	 * boundaries tmin[i], tmax[i], i.e. <code>splitBounds[i][0]</code>=<code>tmin
	 * [i]</code> and <code>splitBounds[i][1]</code>=<code>tmax[i]</code>. By that
	 * this array must have at least the length of <code>splitCount</code> in the
	 * first parameter. <code>splitCount</code> will hold the number of vertices
	 * for the i-th interval and must therefor be an array with dimension at least
	 * equal to <code>splitCount</code>.
	 * <br>
	 * @param allVerticesCount
	 * @param splitCount
	 * @param splitBounds
	 * @param splitVerticesCount
	 *
	 */
	public static void computeVertexCountForSplits(
		int allVerticesCount,
		int splitCount,
		double[][] splitBounds,
		int[] splitVerticesCount) {
		double dT = 0;
		for (int i = 0; i < splitCount; i++)
			dT += (splitBounds[i][1] - splitBounds[i][0]);
		/* from here we expect dT>0 */
		int lost = allVerticesCount;
		for (int i = 0; i < splitCount; i++) {
			double dti = (splitBounds[i][1] - splitBounds[i][0]) / dT;
			splitVerticesCount[i] = Math.max((int) (dti * allVerticesCount), 2);
			lost -= splitVerticesCount[i];
		}
		splitVerticesCount[0] += lost;
	}

  /**
   * Implemented by classes that return an integer value for every x in R.
   *
   * @author vossbeck
   * @mm.docstatus finished
   */
	public interface IntegerFunctionOverRIF {

		public int evaluate(double xin);

	}

	public static double getMathPointDistOnScreen(
		double x1,
		double y1,
		double x2,
		double y2,
		double m_oneMathUnitDistanceOnScreenX,
		double m_oneMathUnitDistanceOnScreenY) {
		return Math.sqrt(
				((x2-x1)*m_oneMathUnitDistanceOnScreenX)
				*((x2-x1)*m_oneMathUnitDistanceOnScreenX)
			+	((y2-y1)*m_oneMathUnitDistanceOnScreenY)
				*((y2-y1)*m_oneMathUnitDistanceOnScreenY)
		);
	}

	public static double getPointDistInMath(double x1, double y1, double x2, double y2) {
		return Math.sqrt(
				(x2-x1)
				*(x2-x1)
			+	(y2-y1)
				*(y2-y1)
		);
	}

	public static double getFunctionSpeedOnScreen(
		double t,
		VectorFunctionOverRIF func,
		double eps,
		double m_oneMathUnitDistanceOnScreenX,
		double m_oneMathUnitDistanceOnScreenY) {
		func.evaluate(t, m_tmp1);
		return getFunctionSpeedOnScreen(t, m_tmp1, func, eps, m_oneMathUnitDistanceOnScreenX, m_oneMathUnitDistanceOnScreenY);
	}

	public static double getFunctionSpeedOnScreen(
		double t,
		double thisPoint[],
		VectorFunctionOverRIF func,
		double eps,
		double m_oneMathUnitDistanceOnScreenX,
		double m_oneMathUnitDistanceOnScreenY) {
		m_tmp1 = thisPoint;
		func.evaluate(t+eps, m_tmp2);
		return getMathPointDistOnScreen(
			m_tmp1[0],
			m_tmp1[1],
			m_tmp2[0],
			m_tmp2[1],
			m_oneMathUnitDistanceOnScreenX,
			m_oneMathUnitDistanceOnScreenY) / eps;
	}

}
