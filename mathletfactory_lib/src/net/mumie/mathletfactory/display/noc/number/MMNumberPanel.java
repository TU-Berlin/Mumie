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

package net.mumie.mathletfactory.display.noc.number;

import java.awt.Dimension;

import net.mumie.mathletfactory.action.message.SpecialCaseEvent;
import net.mumie.mathletfactory.display.noc.op.OperationPanel;
import net.mumie.mathletfactory.math.algebra.op.OpParser;
import net.mumie.mathletfactory.math.algebra.op.Operation;
import net.mumie.mathletfactory.math.algebra.op.UsesOperationIF;
import net.mumie.mathletfactory.math.number.MComplex;
import net.mumie.mathletfactory.math.number.MComplexRational;
import net.mumie.mathletfactory.math.number.MDouble;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.math.number.MOpNumber;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.number.MMOpNumber;
import net.mumie.mathletfactory.transformer.noc.NumberTransformer;

/**
 * This class acts as a container drawable for all MM-numbers. It can handle
 * complex operations with numerical results.
 * 
 * @author Markus Gronau
 * @mm.docstatus finished
 */
public class MMNumberPanel extends OperationPanel {

	/**
	 * This flag is set, when the number panel should send the operation instead of its numeric value as property change event.
	 * @uml.property  name="m_sendOperation"
	 */
	private boolean m_sendOperation = false;

	/**
	 * @uml.property  name="ePSILON"
	 */
	private final double EPSILON = 1e-8;

  /**
   * A flag that indicates, that the maximum size of this panel should be
   * remembered (for numbers that change due to a slider, to eliminate jittering).
   * This flag is only applicable to MMNumberPanels.
   */
  private boolean m_rememberSize = false;

	/**
	* For displaying numbers, the maximum preferred size is stored, so that "bouncing" as a result
	* of continuous size changes is eliminated.
	*/
	private Dimension m_maxPreferredSize = new Dimension();

	public MMNumberPanel(MMObjectIF master, NumberTransformer transformer) {
		super(master, new Operation(master.getNumberClass(), master.toString(),
				true), transformer, null);
		if (master instanceof MMOpNumber) {
			MMOpNumber op_master = (MMOpNumber) master;
			setOperation(op_master.getOperation());
			setNormalForm(op_master.isNormalForm());
			setSendOperation(true);
		}
		if(((MMObjectIF)master).getDisplayProperties().isFontRenderedWithObjectColor())
			setForeground(master.getDisplayProperties().getObjectColor());
		setToolTipVisible(false);
	}

	protected void applyContent(String content) {
		Operation oldExpression = isEdited() ? getOperation().deepCopy() : null;
		Operation newExpression = OpParser.getOperation(getMaster()
				.getNumberClass(), content, isNormalForm());
		if (m_sendOperation) {
			firePropertyChange(PropertyHandlerIF.OPERATION, oldExpression,
					newExpression);
			log("old value: " + oldExpression + ", fire new value: " + newExpression);
		} else {
			MNumber newValue = newExpression.getResult().copy();
			MNumber oldValue = isEdited() ? ((MNumber) getMaster()).copy()
					: null;
			log("old value: " + oldValue + ", fire new value: " + newValue);
			firePropertyChange(PropertyHandlerIF.NUMBER, oldValue, newValue);
		}
	}

	protected boolean checkContent(String content) {
		if (super.checkContent(content)) {
			Operation op = null;
			if (MOpNumber.class.isAssignableFrom(getMaster().getClass())) {
				try {
					//op = ((MOpNumber) getMaster()).getOperation();
					op = OpParser.getOperation(getMaster().getNumberClass(), content, ((MOpNumber)getMaster()).isNormalForm());
				} catch (Throwable e) {//catches all eventually exceptions that may occur when parsing
					getMaster().fireSpecialCaseEvent(new SpecialCaseEvent(this, "no_operation"));
				}
				return op != null && ((MOpNumber)getMaster()).isValidOperation(op);
			}	
			else {
				try {
					op = OpParser.getOperation(getMaster().getNumberClass(),
							content, true);
				} catch (Throwable e) {//catches all eventually exceptions that may occur when parsing
					getMaster().fireSpecialCaseEvent(new SpecialCaseEvent(this, "no_operation"));
				}
			
				if (op != null)
					return true;
				else
					return false;
			}
		} else
			return false;
	}

	public void setValue(MNumber aNumber) {
		if (!aNumber.isEdited()) {
			return;
		}
		boolean hasChanged = false;
		if (aNumber instanceof MComplexRational) {
			// System.out.println("comparing "+aNumber+" and "+getOperation());
			MComplexRational aComplexNumber = (MComplexRational) aNumber;
			MComplexRational oldNumber = (MComplexRational) getOperation()
					.getResult();
			hasChanged = !oldNumber.getRe().copy().sub(aComplexNumber.getRe())
					.isZero()
					|| !oldNumber.getIm().copy().sub(aComplexNumber.getIm())
							.isZero();
			if (Math.abs(aComplexNumber.getRe().getDouble()) < 1e-14
					&& Math.abs(aComplexNumber.getRe().getDouble()) != 0.0) {
				((MComplex) aNumber).setRe(0.0);
			}
			if (Math.abs(aComplexNumber.getIm().getDouble()) < 1e-14
					&& Math.abs(aComplexNumber.getIm().getDouble()) != 0.0) {
				((MComplex) aNumber).setIm(0.0);
			}
		} else if (aNumber instanceof MComplex) {
			// System.out.println("comparing "+aNumber+" and "+getOperation());
			MComplex aComplexNumber = (MComplex) aNumber;
			MComplex oldNumber = (MComplex) getOperation().getResult();
			hasChanged = Math.abs(oldNumber.getRe() - aComplexNumber.getRe()) > EPSILON
					|| Math.abs(oldNumber.getIm() - aComplexNumber.getIm()) > EPSILON;
			if (Math.abs(aComplexNumber.getRe()) < 1e-14
					&& Math.abs(aComplexNumber.getRe()) != 0.0) {
				((MComplex) aNumber).setRe(0.0);
			}
			if (Math.abs(aComplexNumber.getIm()) < 1e-14
					&& Math.abs(aComplexNumber.getIm()) != 0.0) {
				((MComplex) aNumber).setIm(0.0);
			}
		} else {
			double result = Math.abs(aNumber.getDouble()
					- getOperation().getResult().getDouble());
			hasChanged = result > EPSILON || Double.isNaN(result);
			if (Math.abs(aNumber.getDouble()) < 1e-14
					&& Math.abs(aNumber.getDouble()) != 0.0) {
				aNumber.setDouble(0.0);
			}
		}
		if (hasChanged) {
			String numberString;
			if (aNumber instanceof MDouble) {
				if (Double.isNaN(aNumber.getDouble()))
					numberString = "nan";
				else
					numberString = ((MDouble) getMaster())
							.toString(getDecimalFormat());
			} else
				numberString = getMaster().toString();
			if(getMaster() instanceof UsesOperationIF) {
				setOperation(((UsesOperationIF) getMaster()).getOperation());
				// TODO not used because of explizit call of "setOperation" instead of "setValue" in NumberTransformer
				// "setNormalForm" will also be called by transformer
			} else
				//setOperation(new Operation(new NumberOp((MNumber) getMaster())));
				setOperation(new Operation(getMaster().getNumberClass(), numberString, true));
		}
	}
	
	public void setEditable(boolean isEditable) {
		// reset max. remembered size when changing editability
		if(isEditable != isEditable() && isRememberSize()) {
			m_maxPreferredSize.width = 0;
			m_maxPreferredSize.height = 0;
		}
		super.setEditable(isEditable);
	}
	
	public Dimension getPreferredPanelSize() {
    Dimension result = super.getPreferredPanelSize();
    // remember previous maximum values and use them if they were greater than the current ones
    if(isRememberSize() && isViewingMode()) {
    	result.width = Math.max(m_maxPreferredSize.width, result.width);
    	result.height = Math.max(m_maxPreferredSize.height, result.height);
    	log("remembering size: "+result);
    	m_maxPreferredSize.width = result.width;
    	m_maxPreferredSize.height = result.height;
    }
    return result;
  }

	/**
	 * This flag is set, when the number panel should send the operation instead
	 * of its numeric value as property change event.
	 */
	public void setSendOperation(boolean value) {
		m_sendOperation = value;
	}

	public Operation getRealMaster() {
		return (Operation) getMaster();
	}

  /**
   * Returns a flag that indicates, that the maximum size of this panel should be
   * remembered (for numbers that change due to a slider, to eliminate jittering).
   */
  public boolean isRememberSize() {
    return m_rememberSize;
  }

  /**
   * Sets the flag that indicates, that the maximum size of this panel should be
   * remembered (for numbers that change due to a slider, to eliminate jittering).
   */
  public void setRememberSize(boolean b) {
    m_rememberSize = b;
  }

	private void log(String message) {
//		System.out.println("MMNumberPanel@" + Integer.toHexString(hashCode()) + ": " + message);
	}
}
