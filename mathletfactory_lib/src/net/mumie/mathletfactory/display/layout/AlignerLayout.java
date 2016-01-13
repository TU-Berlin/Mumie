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

package net.mumie.mathletfactory.display.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Vector;

import javax.swing.SwingConstants;

import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;

public class AlignerLayout implements LayoutManager, SwingConstants {
	
	public final static String DEFAULT_HEIGHT = "default-height";
	public final static String MAXIMUM_HEIGHT = "maximum-height";
	
	private final static MumieLogger LOGGER = MumieLogger.getLogger(AlignerLayout.class);
  private final static LogCategory PRINT_BASELINE = LOGGER.getCategory("ui.print-baseline");
  
  private int m_hGap;
	
	private int m_hAlign;
	
	private int m_vAlign;
	
	private Vector m_scaledComponents = new Vector();
	
	private Container m_target;

	public AlignerLayout() {
		this(5, CENTER, CENTER);
	}
	
	public AlignerLayout(int hgap) {
		this(hgap, CENTER, CENTER);		
	}
	
	public AlignerLayout(int hgap, int halign, int valign) {
		m_hGap = hgap;
		m_hAlign = halign;
		m_vAlign = valign;
	}
	
	/**
	 * Returns layout information for the provided target container.
	 * Synchronization should be provided by the calling method.
	 */
	protected LayoutInformation getLayoutInformation(Container target) {
		// create result
		LayoutInformation li = new LayoutInformation(target);
  	// getting max ascent and descent of child components
  	for (int i = 0 ; i < li.nrComp ; i++) {
  		Component c = target.getComponent(i);
  		if (c.isVisible() == false)
  			continue;
  		Dimension prefSize = c.getPreferredSize();
  		if(prefSize.width == 0 && prefSize.height == 0)
  			continue;
  		li.width += prefSize.width;
  		if(i < li.nrComp - 1)
  			li.width += m_hGap; // add horizontal gap between 2 components
  		if(c instanceof Alignable) {
  			Alignable al = (Alignable) c;
  			double albl = al.getBaseline();
  			double ascent = albl + al.getInsets().top;
  			double descent = prefSize.height - al.getInsets().top - albl;
  			if(ascent > li.maxAscent) {
  				li.maxAscent = ascent;
  				li.baseline = albl;
  			}
  			li.maxDescent = (descent > li.maxDescent) ? descent : li.maxDescent;
  		} else {
  			// calculate simple component's baseline (based on component's font ascent)
  			FontMetrics metrics = (c.getFont() == null) ? null : c.getFontMetrics(c.getFont());
				int maxFontAscent = (metrics == null) ? 0 : metrics.getMaxAscent();
				int maxFontDescent = (metrics == null) ? 0 : metrics.getMaxDescent();
				int maxFontHeight = maxFontAscent + maxFontDescent;
				double ascent = 0, descent = 0;
				if(prefSize.height < maxFontHeight) {// font's size is bigger than component's height
					ascent = prefSize.height / 2; // centered baseline
					descent = ascent;
				} else {
					ascent = (prefSize.height + maxFontAscent) / 2;
					descent = prefSize.height - ascent;
				}
  			if(ascent > li.maxAscent) {
  				li.maxAscent = ascent;
  				li.baseline = ascent;
  			}
  			li.maxDescent = (descent > li.maxDescent) ? descent : li.maxDescent;
  		}
  	}
  	// calculate max height of child components and store layout data
  	li.maxHeight = li.maxAscent + li.maxDescent;
  	
  	// horizontal alignment
  	int x = 0;
  	if(m_hAlign == LEFT)
  		x = 0;//insets.left;
  	else if(m_hAlign == CENTER)
  		x = (target.getWidth() - li.insets.left - li.insets.right - li.width) / 2;
  	else if(m_hAlign == RIGHT)
  		x = target.getWidth() - li.insets.left - li.insets.right - li.width;
  	// store layout data
		li.startX = x;
  	
  	// vertical alignment
  	double y = 0;
  	if(m_vAlign == TOP)
  		y = 0;//insets.top;
  	else if(m_vAlign == CENTER)
  		y = ((li.targetInnerHeight - li.maxHeight) / 2);
  	else if(m_vAlign == BOTTOM)
  		y = (li.targetInnerHeight - li.maxHeight);
  	// y coordinate must be at least 0
		y = (y < 0) ? 0 : y;
  	// update baseline field
		li.baseline += y;
  	// store layout data
		li.yOffset = y;
		
  	return li;
	}

	public void layoutContainer(Container target) {
    synchronized (target.getTreeLock()) {
    	setTarget(target);
    	LayoutInformation li = getLayoutInformation(target);
    	// retrieve left starting point
    	int x = li.startX;
    	if(LOGGER.isActiveCategory(PRINT_BASELINE)) {
				LOGGER.log(PRINT_BASELINE, hashCode() + "-baseline: target=" + target.getClass().getName());
				LOGGER.log(PRINT_BASELINE, hashCode() + "-baseline: height=" + target.getHeight());
				LOGGER.log(PRINT_BASELINE, hashCode() + "-baseline: y=" + li.yOffset);
				LOGGER.log(PRINT_BASELINE, hashCode() + "-baseline: maxAscent=" + li.maxAscent);
				LOGGER.log(PRINT_BASELINE, hashCode() + "-baseline: insets-top=" + li.insets.top);
    	}
    	// setting size and location of child components
    	for (int i = 0 ; i < li.nrComp ; i++) {
    		Component c = target.getComponent(i);
    		if (c.isVisible() == false)
    			continue;
    		Dimension prefSize = c.getPreferredSize();
    		if(prefSize.width == 0 && prefSize.height == 0)
    			continue;
    		// 
    		boolean isFillHeight = isScaledComponent(c);
    		if(isFillHeight)
    			c.setSize(prefSize.width, li.targetInnerHeight);
    		else
    			c.setSize(prefSize);
    		if(c instanceof Alignable) {
    			Alignable a = (Alignable) c;
      		if(isFillHeight)
      			c.setLocation(li.insets.left + x, li.insets.top);
      		else
      			c.setLocation(li.insets.left + x, li.insets.top + (int) (li.yOffset + li.maxAscent - a.getBaseline() - a.getInsets().top));
        	if(LOGGER.isActiveCategory(PRINT_BASELINE)) {
        		LOGGER.log(PRINT_BASELINE, "Baseline of child #" + i + ": class=" + a.getClass().getName());
        		LOGGER.log(PRINT_BASELINE, "Baseline of child #" + i + ": baseline=" + a.getBaseline());
        		LOGGER.log(PRINT_BASELINE, "Baseline of child #" + i + ": insets-top=" + a.getInsets().top);
        		LOGGER.log(PRINT_BASELINE, "Baseline of child #" + i + ": fill-height=" + isFillHeight);
        		LOGGER.log(PRINT_BASELINE, "Position of child #" + i + "=" + c.getLocation());
        	}
    		} else {
      		if(isFillHeight)
      			c.setLocation(li.insets.left + x, li.insets.top);
      		else {
      			// layout simple component
      			FontMetrics metrics = (c.getFont() == null) ? null : c.getFontMetrics(c.getFont());
    				int maxFontAscent = (metrics == null) ? 0 : metrics.getMaxAscent();
    				int maxFontDescent = (metrics == null) ? 0 : metrics.getMaxDescent();
    				int maxFontHeight = maxFontAscent + maxFontDescent;
    				double ascent = 0;
    				if(prefSize.height < maxFontHeight) {// font's size is bigger than component's height
    					ascent = prefSize.height / 2; // centered baseline
    				} else {
    					ascent = (prefSize.height + maxFontAscent) / 2;
    				}
      			c.setLocation(li.insets.left + x, li.insets.top + (int) (li.yOffset + li.maxAscent - ascent));
      		}
    		}
  			x += prefSize.width;
    		if(i < li.nrComp - 1)
    			x += m_hGap; // add horizontal gap between 2 components
     	}
    }
	}

	public Dimension preferredLayoutSize(Container target) {
    synchronized (target.getTreeLock()) {
    	setTarget(target);
    	LayoutInformation li = getLayoutInformation(target);
	  	Insets insets = target.getInsets();
			Dimension dim = new Dimension(insets.left + insets.right, insets.top + insets.bottom);
			dim.width += li.width;
			dim.height += li.maxHeight;
			return dim;
    }
	}
	
	protected void setTarget(Container target) {
		if(m_target == null)
			m_target = target;
		else if(m_target != target)
			throw new IllegalArgumentException("Cannot change the target of an AlignerLayout !");
	}

	public Dimension minimumLayoutSize(Container target) {
		return preferredLayoutSize(target);
	}

	public void addLayoutComponent(String name, Component comp) {
		if(MAXIMUM_HEIGHT.equals(name))
			m_scaledComponents.add(comp);
	}

	public void removeLayoutComponent(Component comp) {
		m_scaledComponents.remove(comp);
	}
	
	public void setHorizontalAlignment(int halign) {
		m_hAlign = halign;
	}
	
	public void setVerticalAlignment(int valign) {
		m_vAlign = valign;
	}

	public double getBaseline() {
		// should not happen
		if(m_target == null)
			return 0;
		// synchronize call with target's default lock
		synchronized (m_target.getTreeLock()) {
			return getLayoutInformation(m_target).baseline;
		}
	}
	
	private boolean isScaledComponent(Component c) {
		return m_scaledComponents.contains(c);
	}
	
	protected class LayoutInformation {

		Container target;
		Insets insets;
		double maxAscent, maxDescent, maxHeight, baseline, yOffset;
		int nrComp, width, startX, targetInnerHeight;
		
		LayoutInformation(Container target) {
			this.target = target;
			insets = target.getInsets();
			nrComp = target.getComponentCount();
			targetInnerHeight = target.getHeight() - insets.top - insets.bottom;
		}

    public double getBaseline() {
      return baseline;
    }

    public void setBaseline(double baseline) {
      this.baseline = baseline;
    }

    public Insets getInsets() {
      return insets;
    }

    public void setInsets(Insets insets) {
      this.insets = insets;
    }

    public double getMaxAscent() {
      return maxAscent;
    }

    public void setMaxAscent(double maxAscent) {
      this.maxAscent = maxAscent;
    }

    public double getMaxDescent() {
      return maxDescent;
    }

    public void setMaxDescent(double maxDescent) {
      this.maxDescent = maxDescent;
    }

    public double getMaxHeight() {
      return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
      this.maxHeight = maxHeight;
    }

    public int getNrComp() {
      return nrComp;
    }

    public void setNrComp(int nrComp) {
      this.nrComp = nrComp;
    }

    public int getStartX() {
      return startX;
    }

    public void setStartX(int startX) {
      this.startX = startX;
    }

    public Container getTarget() {
      return target;
    }

    public void setTarget(Container target) {
      this.target = target;
    }

    public int getTargetInnerHeight() {
      return targetInnerHeight;
    }

    public void setTargetInnerHeight(int targetInnerHeight) {
      this.targetInnerHeight = targetInnerHeight;
    }

    public int getWidth() {
      return width;
    }

    public void setWidth(int width) {
      this.width = width;
    }

    public double getYOffset() {
      return yOffset;
    }

    public void setYOffset(double offset) {
      yOffset = offset;
    }
	}
}
