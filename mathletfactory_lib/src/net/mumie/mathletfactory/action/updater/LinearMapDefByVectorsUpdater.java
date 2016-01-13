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

package net.mumie.mathletfactory.action.updater;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.action.message.MathContentException;
import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.action.message.SpecialCaseListener;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism;
import net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector;

/**
 * This class is a standard <code>MMUpdater</code> working between an instance of
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNEndomorphism} 
 * and a {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector}
 * to easily provide standard functionality  to construct (dynamically) the linear
 * map that maps a given set of vectors onto another set of specified vectors.
 *  
 * 
 * @author amsel
 * @mm.docstatus finished
 */
public class LinearMapDefByVectorsUpdater extends MMUpdater {

	MMDefaultRNEndomorphism m_linearMap;
	MMDefaultRNVector[] m_domain;
	MMDefaultRNVector[] m_range;
	private boolean m_fireExceptionNextTime = true;

	public LinearMapDefByVectorsUpdater(
		MMDefaultRNEndomorphism L,
		MMDefaultRNVector[] domain,
		MMDefaultRNVector[] range,
		SpecialCaseListener listener) {
		super(
			L,
			(domain.length == 3
				? new MMObjectIF[] {
					domain[0],
					domain[1],
					domain[2],
					range[0],
					range[1],
					range[2] }
		: new MMObjectIF[] { domain[0], domain[1], range[0], range[1] }));
		m_linearMap = L;
		m_domain = domain;
		m_range = range;
		if (listener != null)
			addSpecialCaseListener(listener);
		ActionManager.performActionCycleFromObjects(m_parents);
	}

	public void userDefinedUpdate() {
		try {
			m_linearMap.setFrom(m_domain, m_range);
			m_fireExceptionNextTime = true;
		}
		catch (MathContentException e) {
			if (getInvocationCount() == 1) {
				if(m_fireExceptionNextTime) {
					fireSpecialCaseEvent(new SpecialCaseEvent(this, "not_linear"));
					m_fireExceptionNextTime = false;
				}
			}
		}
//		NumberTuple[] dom = new NumberTuple[m_domain.length];
//		for (int i = 0; i < m_domain.length; i++)
//			dom[i] = ((MMDefaultRNVector) m_domain[i]).getDefaultCoordinates();
//		NumberMatrix domainVecMatrix =
//			new NumberMatrix(
//				m_linearMap.getNumberClass(),
//				NumberMatrix.NUMBERTUPEL_COLUMNWISE,
//				dom);
//		if (domainVecMatrix.rank() < m_domain.length) {
//			// domain vectors are linearly depend.
//			if (getInvocationCount() == 1)
//				fireSpecialCaseEvent(new SpecialCaseEvent(this, "not_linear"));
//			m_linearMap.setMap(false);
//		}
//		else {
//			for (int i = 0; i < m_domain.length; i++) {
//			}
//			if (m_linearMap.isMap() == false)
//				m_linearMap.setMap(true);
//			m_linearMap.setFrom(m_domain, m_range);
//		}
	}
}
