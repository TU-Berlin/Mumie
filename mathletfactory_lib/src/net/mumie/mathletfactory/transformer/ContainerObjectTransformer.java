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

import net.mumie.mathletfactory.display.Drawable;
import net.mumie.mathletfactory.display.noc.MMPanel;
import net.mumie.mathletfactory.display.util.LabelSupport;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.property.CombinedPropertyMap;


/**
 * This is the abstract super class for any transformer that visualizes a
 * mathematical entity directly as a (java-)Component (which can then typically be
 * added to a
 * {@link net.mumie.mathletfactory.appletskeleton.BaseApplet}.
 *
 * An example is a
 * {@link net.mumie.mathletfactory.mmobject.algebra.linalg.MMDefaultRNVector}
 * which might be visualised as it's coordinate tuple (due to a chosen basis) rather
 * than being drawn as an arrow within a <code>MMCanvas</code>.
 *
 * @author vossbeck
 * @mm.docstatus finished
 */
public abstract class ContainerObjectTransformer extends GeneralTransformer {

  /** The panel that is the drawable for this transformer. */
	protected MMPanel m_mmPanel;
	
	/** Flag indicating that the panel's properties were already initialized. */
	private boolean m_propertiesInitialized = false;
	
	private LabelSupport m_labelSupport = new LabelSupport(LabelSupport.ST_NO_CANVAS);
	
	public void initialize(MMObjectIF masterObject) {
    super.initialize(masterObject);
	}

	public void render() {
		// panel's properties cannot be loaded inside the initialize(...) method
		// --> panel is created inside sub classes after method call 
		if(m_propertiesInitialized == false) {
			initializeDrawables(new Drawable[] { getMMPanel() });
			m_propertiesInitialized = true;
		}
		if(getMMPanel().getDrawableProperties() instanceof CombinedPropertyMap)
			((CombinedPropertyMap) getMMPanel().getDrawableProperties()).setDefaultsMap(getMaster().getDisplayProperties());
		else
			getMMPanel().getDrawableProperties().copyPropertiesFrom(getMaster().getDisplayProperties());
		
		// set the appropriate label
		m_labelSupport.setLabel(getMaster().getLabel());
		getMMPanel().setLabel(m_labelSupport.getLabel());
	}
  /**
   *  For a <code>ContainerObjectTransformer</code> Drawable and Display are
   *  the same, so this method returns the {@link #m_mmPanel}.
   */
  public Drawable getActiveDrawable() {
    return (MMPanel)m_mmPanel;
  }

  /** Returns the panel that is the drawable for this transformer. */
  public MMPanel getMMPanel(){
  	return m_mmPanel;
  }

  /**
   * The method draw has an empty implementation for any instance of
   * <code>ContainerObjectTransformer</code>, because the
   * {@link GeneralTransformer#render()} method will already
   * does everything for a correct redisplay of the mathematical entity in use.
   */
  public final void draw() {}
}

