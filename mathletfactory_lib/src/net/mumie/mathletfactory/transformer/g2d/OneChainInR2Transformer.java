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

package net.mumie.mathletfactory.transformer.g2d;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.g2d.G2DPolygonDrawable;
import net.mumie.mathletfactory.math.analysis.function.FunctionOverRIF;
import net.mumie.mathletfactory.math.analysis.function.multivariate.VectorFunctionOverRIF;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.math.set.Interval;
import net.mumie.mathletfactory.math.util.FunctionRenderLib;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMOneChainInRNIF;
import net.mumie.mathletfactory.transformer.Canvas2DObjectTransformer;

/**
 * Transformer for classes that implement 
 * {@link net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMOneChainInRNIF}.
 * 
 * @author vossbeck, ahlborg, manya
 * @mm.docstatus finished
 */
public class OneChainInR2Transformer
	extends Canvas2DObjectTransformer {
	
	private boolean m_debug = false;
	private boolean m_showStatisticDebug = false;
	private int debugverdoppeln = 0;
	private int debughalbieren = 0;
	
	private int m_exprCountInChain, m_intervalCountInExpr[], indexInPolygonDrawable,
		//wird dynamisch verdoppelt bei bedarf
		m_maxVertices = 1500;
	private double minDerivative = 3/*niemals 0 setzen*/;
	private double wx[], wy[], m_epsFactor = 1000000, m_eps = 1/m_epsFactor,
		m_splittedIntervalLimits[][], m_wxAll[], m_wyAll[],
		m_oneMathUnitDistanceOnScreenX = 1, m_oneMathUnitDistanceOnScreenY = 1;
	private MMOneChainInRNIF m_master;
	private IndicatorFunction m_indicatorFunction;
	
	//we need this variables for the evaluation of the derivative
	private double[] m_tmp, m_tmp1, m_tmp2;
	//zum speichern der valid flags fuer jeden einzelnen punkt
	private boolean m_wxyAllValid[];
	
  public void initialize(MMObjectIF masterObject) {
    if (masterObject instanceof MMOneChainInRNIF) {
      super.initialize(masterObject);
      m_indicatorFunction = new IndicatorFunction();
      initAllDataFields();
    }
    else
      throw new IllegalArgumentException(
        getClass().getName()
          + "::init(): can only work for instances of MMOneChainInRNIF, but I got "
          + masterObject.getClass().getName());
  }
	
	private void initAllDataFields() {
		m_master = (MMOneChainInRNIF) m_masterMMObject;
		m_exprCountInChain = m_master.getPartCount();
		m_intervalCountInExpr = new int[m_exprCountInChain];
		for (int expr = 0; expr < m_exprCountInChain; expr++) {
			m_intervalCountInExpr[expr] = m_master.getIntervalCountInPart(expr);
		}
		wx = new double[m_maxVertices];
		wy = new double[m_maxVertices];
		m_wxyAllValid = new boolean[m_maxVertices];
		m_tmp = new double[2];
		m_tmp1 = new double[2];
		m_tmp2 = new double[2];
	}
	
	//zaehlt den Index hoch und vergroessert bei bedarf die arrays zum verwalten der punkte
	private void incrementIndexInPolygonDrawable() {
		indexInPolygonDrawable++;
		if (indexInPolygonDrawable >= m_maxVertices-2) {
			m_maxVertices *= 2;
			double[] tmpwx = wx;
			double[] tmpwy = wy;
			boolean[] tmpvalid = m_wxyAllValid;
			wx = new double[m_maxVertices];
			wy = new double[m_maxVertices];
			m_wxyAllValid = new boolean[m_maxVertices];
			for (int i = 0; i<tmpwx.length; i++) {
				wx[i] = tmpwx[i];
				wy[i] = tmpwy[i];
				m_wxyAllValid[i] = tmpvalid[i];
			}
		}
	}
	
	public void synchronizeWorld2Screen() {
		if (m_debug) {
			System.out.println("MMOneChainInR2TransformerAdaptive: start synchronizeWorld2Screen()");
		}
		//zaehlvariable fuer die funktionsvorschriften
		int expr,
			//zaehlvariable fuer die intervale
			interval,
			//zaehlvariable fuer die splitintervale nach der intervalteilung durch findIntervals()
			splitInt,
			//anzahl der splitintervale (ermittelt durch findIntervals())
			splitCount = 0,
			//aktuelles splitinterval
			currentIndexInSplitInterval = 0;
		//aktuelles interval
		Interval currentInterval;
		//art der schachtelsuche
		boolean rightHandSearch, leftHandSearch,
			//zum merken von ungueltigen punkten
			pointInvalid = false, lastPointInvalid = false;
		//ober und untergrenze des aktuellen intervals
		double lowerB, upperB,
			//hilfsvariable zur abstandsberechnung zwischen punkten
			pointDist,
			//hilfsvariable zur abstandsberechnung zwischen punkten auf dem monitor
			pixelDist,
			//einheitsvektor zwischen den beiden vorhergehenden punkten
			unitVector[] = new double[2],
			//letzter geschaetzter punkt - zur ueberpruefung ob der naechste punkt sinnvoll ist
			lastPoint[] = new double[2],
			//naechster punkt, wenn er auf einer geraden mit den beiden vorherigen liegen wuerde
			calcPosOfNextPoint[] = new double[2],
			//beide funktionen werden auf ihren intervalen mit t durchlaufen
			t, firstT = 0, secondT = 0,
			//anstieg am jeweiligen punkt
			h,
			//schrittweite zwischen der letzten und der aktuellen berechnung
			evaluationStep;
		
		//worldBounds ermitteln und indikatorfunktion setzen
		adjustWorldBounds();
		double xWorldMin = m_llInWorldDraw[0];
		double xWorldMax = m_urInWorldDraw[0];
		double yWorldMin = m_llInWorldDraw[1];
		double yWorldMax = m_urInWorldDraw[1];
		m_indicatorFunction.setBounds(xWorldMin, xWorldMax, yWorldMin, yWorldMax);
		
		world2Screen(0, 0, m_tmp1);
		//ermittlung des pixelabstandes in x richtung
		world2Screen(1, 0, m_tmp2);
		m_oneMathUnitDistanceOnScreenX = Math.sqrt((m_tmp2[0]-m_tmp1[0])*(m_tmp2[0]-m_tmp1[0])+(m_tmp2[1]-m_tmp1[1])*(m_tmp2[1]-m_tmp1[1]));
		//ermittlung des pixelabstandes in y richtung
		world2Screen(0, 1, m_tmp2);
		m_oneMathUnitDistanceOnScreenY = Math.sqrt((m_tmp2[0]-m_tmp1[0])*(m_tmp2[0]-m_tmp1[0])+(m_tmp2[1]-m_tmp1[1])*(m_tmp2[1]-m_tmp1[1]));
		//epsilon fuer die nummerische ableitung in abhaengigkeit zum mathematischen abstand 2er pixel
		m_eps = Math.sqrt(m_oneMathUnitDistanceOnScreenX*m_oneMathUnitDistanceOnScreenX+m_oneMathUnitDistanceOnScreenY*m_oneMathUnitDistanceOnScreenY) / m_epsFactor;
		
		double screenRadius = ((xWorldMax-xWorldMin)*(xWorldMax-xWorldMin)+(yWorldMax-yWorldMin)*(yWorldMax-yWorldMin))/2;
		indexInPolygonDrawable = 0;
		//durchlaufen der funktionsvorschriften
		for (expr = 0; expr < m_exprCountInChain; expr++) {
			if (m_debug) {
				System.out.println();
				System.out.println("MMOneChainInR2TransformerAdaptive: start rendering Expression: "+expr);
			}
			//indicatorfunktion wird auf die aktuelle funktionsvorschrift gesetzt
			m_indicatorFunction.setFunction(m_master.getEvaluateExpressionInPart(expr));
			//durchlaufen der intervale auf denen die aktuelle funktionsvorschrift definiert ist
			for (interval = 0; interval < m_intervalCountInExpr[expr]; interval++) {
				//ermittlung des aktuellen intervals und dessen grenzen
				currentInterval = m_master.getInterval(expr, interval);
				lowerB = currentInterval.getLowerBoundaryVal().getDouble();
				upperB = currentInterval.getUpperBoundaryVal().getDouble();
				//ermittlung der sichtbaren intervale
				m_splittedIntervalLimits =
					FunctionRenderLib.findIntervalsAdaptive(
					m_indicatorFunction,
					m_master.getEvaluateExpressionInPart(expr),
					m_eps,
					m_oneMathUnitDistanceOnScreenX,
					m_oneMathUnitDistanceOnScreenY,
					lowerB,
					upperB,
					screenRadius);
				splitCount = (int)m_splittedIntervalLimits[m_splittedIntervalLimits.length-1][0];
				if (m_debug) {
					System.out.println();
					System.out.println("MMOneChainInR2TransformerAdaptive: start rendering Interval: "+interval);
					System.out.println("MMOneChainInR2TransformerAdaptive: splitCount: "+splitCount);
				}
				//durchlaufen der sichtbaren intervale
				for (splitInt = 0; splitInt < splitCount ;splitInt++) {
					if (m_debug) {
						System.out.println();
						System.out.println("MMOneChainInR2TransformerAdaptive: start rendering SplitInterval: "+splitInt);
					}
					//t wird auf den anfang des intervals gesetzt
					t = m_splittedIntervalLimits[splitInt][0];
					//
					//ersten 2 punkte berechnen um ersten Anstieg zu bilden
					//
					for(currentIndexInSplitInterval = 0;
						currentIndexInSplitInterval < 2;
						currentIndexInSplitInterval++) {
						//beim 2. punkt muss t weitergezaehlt werden
						if(currentIndexInSplitInterval==1) {
							//ermittlung des aktuellen anstiegs
							h = FunctionRenderLib.getFunctionSpeedOnScreen(
								t,
								m_tmp,
								m_master.getEvaluateExpressionInPart(expr),
								m_eps, m_oneMathUnitDistanceOnScreenX,
								m_oneMathUnitDistanceOnScreenY
							);
							//einschraenken des anstiegs auf einen bestimmten bereich
							if (h < minDerivative) h = minDerivative;
							t += 1 / h;
						}
						//berechnung des punktes
						m_master.getEvaluateExpressionInPart(expr).evaluate(t, m_tmp);
						wx[indexInPolygonDrawable] = m_tmp[0];
						wy[indexInPolygonDrawable] = m_tmp[1];
						//ist der punkt ungueltig, wird nach dem naechsten
						//gueltigen punkt gesucht
						//ungueltigekeit muss nur beim 2. punkt untersucht werden,
						//da findIntervals keine ungueltigen startpunkte liefert
						if(currentIndexInSplitInterval==1) {
							while (Double.isNaN(wy[indexInPolygonDrawable])
								   || Double.isInfinite(wy[indexInPolygonDrawable])
								   || Double.isNaN(wx[indexInPolygonDrawable])
								   || Double.isInfinite(wx[indexInPolygonDrawable])) {
								//ermittlung des aktuellen anstiegs
								h = FunctionRenderLib.getFunctionSpeedOnScreen(
									t,
									m_tmp,
									m_master.getEvaluateExpressionInPart(expr),
									m_eps, m_oneMathUnitDistanceOnScreenX,
									m_oneMathUnitDistanceOnScreenY
								);
								//einschraenken des anstiegs auf einen bestimmten bereich
								if (h < minDerivative) h = minDerivative;
								t += 1 / h;
								//wenn bis zum intervalende kein gueltiger punkt
								//gefunden wurde, kann die funktion nicht gezeichnet werden
								if (t>m_splittedIntervalLimits[splitInt][1]) break;
								//neuberechnung des punktes
								m_master.getEvaluateExpressionInPart(expr).evaluate(t, m_tmp);
								wx[indexInPolygonDrawable] = m_tmp[0];
								wy[indexInPolygonDrawable] = m_tmp[1];
							}
						}
						if(currentIndexInSplitInterval==0) {
							firstT = t;
							m_wxyAllValid[indexInPolygonDrawable] = false;
						}
						if(currentIndexInSplitInterval==1) {
							secondT = t;
							m_wxyAllValid[indexInPolygonDrawable] = true;
						}
						if (m_debug) {
							System.out.println();
							System.out.println("MMOneChainInR2TransformerAdaptive: indexInPolygonDrawable="+indexInPolygonDrawable);
							System.out.println("MMOneChainInR2TransformerAdaptive: t="+t);
							System.out.println("MMOneChainInR2TransformerAdaptive: Point(kanonisch) = ( "+wx[indexInPolygonDrawable]+" | "+wy[indexInPolygonDrawable]+" )");
						}
						incrementIndexInPolygonDrawable();
					}
					//wenn bis zum intervalende kein gueltiger punkt
					//gefunden wurde, kann die funktion nicht gezeichnet werden
					if (t>m_splittedIntervalLimits[splitInt][1]) {
						m_wxyAllValid[indexInPolygonDrawable] = false;
						continue;
					}
					//erste schrittweite ist vorraussichtlich aehnlich wie die bisherige
					evaluationStep = secondT - firstT;
					//
					//start des t durchlaufs
					//
					while (t < m_splittedIntervalLimits[splitInt][1]) {
						if (m_debug) {
							System.out.println();
							System.out.println("MMOneChainInR2TransformerAdaptive: start search for next Point. t = "+t);
							System.out.println("MMOneChainInR2TransformerAdaptive: indexInPolygonDrawable = "+indexInPolygonDrawable);
						}
						//einheitsvector zwischen den letzten beiden punkten berechnen
						pointDist = FunctionRenderLib.getPointDistInMath(wx[indexInPolygonDrawable-1],
													   wy[indexInPolygonDrawable-1],
													   wx[indexInPolygonDrawable-2],
													   wy[indexInPolygonDrawable-2]);
						unitVector[0] = (wx[indexInPolygonDrawable-1] - wx[indexInPolygonDrawable-2])/pointDist;
						unitVector[1] = (wy[indexInPolygonDrawable-1] - wy[indexInPolygonDrawable-2])/pointDist;
						//ermittlung des aktuellen anstiegs
//t auf gueltigkeit pruefen
						h = FunctionRenderLib.getFunctionSpeedOnScreen(
							t,
							m_tmp,
							m_master.getEvaluateExpressionInPart(expr),
							m_eps, m_oneMathUnitDistanceOnScreenX,
							m_oneMathUnitDistanceOnScreenY
						);
						//einschraenken des anstiegs auf einen bestimmten bereich
						if (h < minDerivative) h = minDerivative;
						//neues t mit hilfe von h und dem pixelabstand berechnen
						//3 hat sich als guter wert ergeben - aber warum?
						evaluationStep = 3 / h;
						//beide arten der schachtelsuche auf false setzen
						rightHandSearch = false;
						leftHandSearch = false;
						//start der schachtelsuche nach dem wirklichen punkt
						while (true) {
							//koordinaten fuer das neue t berechnen
							m_master.getEvaluateExpressionInPart(expr).evaluate(t+evaluationStep, m_tmp);
							wx[indexInPolygonDrawable] = m_tmp[0];
							wy[indexInPolygonDrawable] = m_tmp[1];
							//wenn diese werte ungultig sind
							if (Double.isNaN(wy[indexInPolygonDrawable])
								|| Double.isInfinite(wy[indexInPolygonDrawable])
								|| Double.isNaN(wx[indexInPolygonDrawable])
								|| Double.isInfinite(wx[indexInPolygonDrawable])) {
								//wenn beim letzten Schritt halbiert wurde soll
								//einfach nochmal halbiert werden
								if (leftHandSearch) {
									t /= 2;
									continue;
								}
								//ansonsten wird abgebrochen und der punkt als ungueltig markiert
								else {
									pointInvalid = true;
									break;
								}
							}
							//wenn letztes mal verdoppelt wurde und der vector zwischen
							//dem letzten versuch und diesen versuch andere vorzeichen
							//hat als der einheitsvector, dann geht dieser versuch in
							//die falsche richtung und muss rueckgaengig gemacht werden
							if ((rightHandSearch)
								&&(((wx[indexInPolygonDrawable]-lastPoint[0]<0)!=(unitVector[0]<0))
									||((wy[indexInPolygonDrawable]-lastPoint[1]<0)!=(unitVector[1]<0)))) {
										evaluationStep /= 2;
										break;
									}
							lastPoint[0] = wx[indexInPolygonDrawable];
							lastPoint[1] = wy[indexInPolygonDrawable];
							//abstand zwischen dem letzten und dem punkt t+schrittweite
							pointDist = FunctionRenderLib.getPointDistInMath(wx[indexInPolygonDrawable-1],
														   wy[indexInPolygonDrawable-1],
														   wx[indexInPolygonDrawable],
														   wy[indexInPolygonDrawable]);
							//berechnen des fiktiven punktes vector*pointdist+point[index-1]
							calcPosOfNextPoint[0] = unitVector[0]*pointDist+wx[indexInPolygonDrawable-1];
							calcPosOfNextPoint[1] = unitVector[1]*pointDist+wy[indexInPolygonDrawable-1];
							//abstandsberechnung zwischen fiktiven punkt und kanonischen punkt
							pixelDist = FunctionRenderLib.getMathPointDistOnScreen(wx[indexInPolygonDrawable],
																 wy[indexInPolygonDrawable],
																 calcPosOfNextPoint[0],
																 calcPosOfNextPoint[1],
																 m_oneMathUnitDistanceOnScreenX,
																 m_oneMathUnitDistanceOnScreenY);
							if (m_debug) {
								System.out.println();
								System.out.println("MMOneChainInR2TransformerAdaptive: next Point could be at t+step = "+(t+evaluationStep));
								System.out.println("MMOneChainInR2TransformerAdaptive: Point(kanonisch) = ( "+wx[indexInPolygonDrawable]+" | "+wy[indexInPolygonDrawable]+" )");
								System.out.println("MMOneChainInR2TransformerAdaptive: Point(fiktiv) = ( "+calcPosOfNextPoint[0]+" | "+calcPosOfNextPoint[1]+" )");
								System.out.println("MMOneChainInR2TransformerAdaptive: unitVector between the last two Points = ( "+unitVector[0]+" | "+unitVector[1]+" )");
							}
							//wenn abstand groesser als 2 pixel dann schrittweite
							//halbieren und neu versuchen
							if (pixelDist >= 1) {
								evaluationStep /= 2;
								if (m_debug) {
									System.out.println("MMOneChainInR2TransformerAdaptive: halve StepWidth="+evaluationStep);
									System.out.println("MMOneChainInR2TransformerAdaptive: new t+step="+(t+evaluationStep));
									debughalbieren++;
								}
								else if (m_showStatisticDebug) debughalbieren++;
								//wenn schonmal in die andere Richtung gesucht
								//wurde, macht es keinen sinn genauer zu suchen
								if (rightHandSearch) break;
								leftHandSearch = true;
								//ansonsten weiter suchen
								continue;
							}
							else {
								//falls dieser gueltige Wert quaaerhalb des Intervals liegt
								if (t+evaluationStep > m_splittedIntervalLimits[splitInt][1]) {
									t = m_splittedIntervalLimits[splitInt][1];
									evaluationStep = 0;
									break;
								}
								//wenn abstand kleiner als 0.5 pixel dann schrittweite
								//verdoppeln
								if (pixelDist < 0.5) {
									evaluationStep *= 2;
									if (m_debug) {
										System.out.println("MMOneChainInR2TransformerAdaptive: double StepWidth="+evaluationStep);
										System.out.println("MMOneChainInR2TransformerAdaptive: new t+step="+(t+evaluationStep));
										debugverdoppeln++;
									}
									else if (m_showStatisticDebug) debugverdoppeln++;
									//wenn schonmal in die andere Richtung gesucht
									//wurde, macht es keinen sinn groeber zu suchen
									if (leftHandSearch) break;
									rightHandSearch = true;
									//ansonsten weiter suchen
									continue;
								}
								//wenn abstand korrekt break
								else break;
							}
						} //ende der schachtelsuche
						//wenn dieser punkt ungultig ist
						if (pointInvalid) {
							pointInvalid = false;
							//wenn vorher die schrittweite verdoppelt wurde,
							//soll sie nun wieder halbiert werden und dieser wert
							//(der gueltig sein muesste) uebernommen werden
							if (rightHandSearch)
								t += evaluationStep/2;
							//falls die schrittweite vorher halbiert wurde, wurde
							//dies schon weiter oben abgefangen
							else {
								//wenn also gleich der erste tipp auf dieses t ungueltig war,
								//soll zu diesem punkt ein moveto
								//vom drawable gemacht werden
								m_wxyAllValid[indexInPolygonDrawable] = false;
								lastPointInvalid = true;
								t += evaluationStep;
							}
						}
						else {
							lastPointInvalid = false;
							//neues t uebernehmen
							t += evaluationStep;
							//koordinaten fuer das neue t berechnen
							m_master.getEvaluateExpressionInPart(expr).evaluate(t, m_tmp);
							//neuen Punkt eintragen
							wx[indexInPolygonDrawable] = m_tmp[0];
							wy[indexInPolygonDrawable] = m_tmp[1];
							m_wxyAllValid[indexInPolygonDrawable] = true;
							//wenn der letzte Punkt ungueltig war,
							//soll zu diesem punkt ein moveto
							//vom drawable gemacht werden
							if(lastPointInvalid)
								m_wxyAllValid[indexInPolygonDrawable] = false;
						}
						incrementIndexInPolygonDrawable();
					} //ende des t durchlaufs
				}
			}
		}
		//wird 0 wenn die funktion nicht im bild ist bzw. wenn finintervals keine
		//im bild befindlichen funktionsteile gefunden hat
		if (indexInPolygonDrawable == 0) {
			wx[0] = 0;
			wy[0] = 0;
			m_wxyAllValid[indexInPolygonDrawable] = false;
			incrementIndexInPolygonDrawable();
		}
		//brauchen wir, weil das array wx/wy teilweise bis zu doppelt so gross sein kann wie benoetigt
		m_wxAll = new double[indexInPolygonDrawable];
		m_wyAll = new double[indexInPolygonDrawable];
		m_activeDrawable = new G2DPolygonDrawable(indexInPolygonDrawable);
		if (m_debug||m_showStatisticDebug) {
			System.out.println();
			System.out.println("MMOneChainInR2TransformerAdaptive: list of final points:");
		}
		for (int i=0; i<indexInPolygonDrawable; i++) {
			((G2DPolygonDrawable) m_activeDrawable).setValid(
				i,
				m_wxyAllValid[i]);
			m_wxAll[i] = wx[i];
			m_wyAll[i] = wy[i];
			if (m_debug||m_showStatisticDebug) System.out.println("MMOneChainInR2TransformerAdaptive: i="+i+" ( "+wx[i]+" | "+wy[i]+" ) valid="+m_wxyAllValid[i]);
		}
		if (m_debug||m_showStatisticDebug) {
			System.out.println();
			System.out.println("MMOneChainInR2TransformerAdaptive: finish synchronizeWorld2Screen()");
			System.out.println("MMOneChainInR2TransformerAdaptive: vertices = "+indexInPolygonDrawable);
			System.out.println("MMOneChainInR2TransformerAdaptive: stepWidth doubled = "+debugverdoppeln);
			System.out.println("MMOneChainInR2TransformerAdaptive: stepWidth halved = "+debughalbieren);
			System.out.println();
			System.out.println();
		}
		world2Screen(
			m_wxAll,
			m_wyAll,
						((G2DPolygonDrawable) m_activeDrawable).getXValsRef(),
						((G2DPolygonDrawable) m_activeDrawable).getYValsRef());
	}
	
	public void synchronizeMath2Screen() {
		initAllDataFields();
		synchronizeWorld2Screen();
	}
	
	public void getScreenPointFromMath(
		NumberTypeDependentIF entity,
		double[] javaScreenCoordinates) {
		throw new TodoException();
	}
	
	public void getMathObjectFromScreen(
		double[] javaScreenCoordinates,
		NumberTypeDependentIF mathObject) {
		throw new TodoException();
	}
	
	private class IndicatorFunction
		implements FunctionOverRIF {
		public Class getNumberClass()
		{
			return null;
		}
		
		public void evaluate(double[] xin, double[] yout)
		{
		}
		
		public void evaluate(net.mumie.mathletfactory.math.number.MNumber xin, net.mumie.mathletfactory.math.number.MNumber yout)
		{
		}
		
		
		private double m_xmin, m_xmax, m_ymin, m_ymax;
		private VectorFunctionOverRIF m_function;
		
		public void setFunction(VectorFunctionOverRIF f) {
			m_function = f;
		}
		
		public void setBounds(double xmin, double xmax, double ymin, double ymax) {
			m_xmin = xmin;
			m_xmax = xmax;
			m_ymin = ymin;
			m_ymax = ymax;
			//System.out.println("xmin: "+xmin+" xmax: "+xmax+" ymin: "+ymin+" ymax: "+ymax);
		}
		
		public double evaluate(double tin) {
			/*
			 * two dimensional:
			 * - hold a VectorFunctionOverRIF f, with t-->(x(t),y(t)):= (ft[0],ft[1]
			 * - initialize double [] ft = new double[2]
			 * - v.evalute(t,ft) - ft[0]
			 * and ft [1] are both visible (m_xin<= ft [0]
			 * <= m_xmax),    m_ymin <= ft[1] <= m_ymax) -- > return -1 - ft[0] or
			 * ft [1] are NaN or Infinity --> return 0 - ft[0]>m_xmax or ft[0] <
			 * m_xmin --> return 1 - ft[1]>y_max or ft[1]<m_ymin --> return 1
			 */
			m_function.evaluate(tin, m_tmp);
			if (Double.isNaN(m_tmp[0])
				|| Double.isNaN(m_tmp[1])
				|| Double.isInfinite(m_tmp[0])
				|| Double.isInfinite(m_tmp[1]))
				return 0;
			if (m_xmin <= m_tmp[0]
				&& m_xmax >= m_tmp[0]
				&& m_ymin <= m_tmp[1]
				&& m_ymax >= m_tmp[1])
				return -1;
			double[] screenCenter = new double[2];
			screenCenter[0] = m_xmin+(m_xmax-m_xmin)/2;
			screenCenter[1] = m_ymin+(m_ymax-m_ymin)/2;
			return FunctionRenderLib.getPointDistInMath(m_tmp[0], m_tmp[1], screenCenter[0], screenCenter[1]);
		}
	}
}
