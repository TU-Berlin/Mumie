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

package net.mumie.mathletfactory.mmobject;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JPanel;

import net.mumie.mathletfactory.action.message.IllegalUsageException;
import net.mumie.mathletfactory.transformer.ContainerObjectTransformer;
import net.mumie.mathletfactory.transformer.GeneralTransformer;

/**
 * This class serves as support for instances of
 * {@link MMObjectIF} -- these must implement
 * methods from {@link VisualizeIF}.
 * The <code>VisualizeSupport</code> is a correct implementation of
 * an <code>VisualizeIF</code> and instances of <code>MMObjectIF</code>
 * may delegate those methods to this support.
 *
 * @author vossbeck, Paehler
 * @mm.docstatus finished
 */

public class VisualizeSupport implements VisualizeIF {

	private final MMObjectIF m_client;

  protected boolean visible = true;

	protected HashMap m_transformers = new HashMap();
	protected static final JComponent m_canvasKey = new JPanel();

	public VisualizeSupport(MMObjectIF aClientObject) {
		m_client = aClientObject;
	}

	protected final MMObjectIF getClient() {
		return m_client;
	}

	public JComponent getAsContainerContent() {
		return getAsContainerContent(getClient().getTransformType(null));
	}

	public JComponent getAsContainerContent(int transformType) {
		MMObjectIF client = getClient();
		int screenType = GeneralTransformer.ST_NO_CANVAS;
		GeneralTransformer t =
			GeneralTransformer.getTransformer(transformType, screenType, client);
		// it is clear, that the returned transformer is instance of ContainerObjectTransformer
		ContainerObjectTransformer ct = (ContainerObjectTransformer) t;
		if(ct == null)
			throw new RuntimeException("No transformer available for \"" + getClient().getClass().getName()
					+ "\" with transform type \"" + transformType+"\" and screen type \""+screenType + "\"");
		m_transformers.put(ct.getMMPanel(), ct);
		JComponent c = ct.getMMPanel();
		return c;
	}

	public HashMap getDisplayTransformerMap() {
		return m_transformers;
	}

	public int getDefaultTransformType() {
		throw new IllegalUsageException("method must be called from within a MMObject");
	}

	public int getDefaultTransformTypeAsContainer() {
		throw new IllegalUsageException("method must be called from within a MMObject");
	}

	public final int getTransformType(JComponent display) {
		if (display == null) // transform as container content
			return getClient().getDefaultTransformTypeAsContainer();
		GeneralTransformer t = (GeneralTransformer) m_transformers.get(display);
		return GeneralTransformer.getTransformType(t);
	}

	public void draw() {
    if(getClient().getDisplayProperties() == null)
      System.out.println(getClient()+" has null properties!");
		Iterator transformers = m_transformers.values().iterator();
		while (transformers.hasNext()) {
			((GeneralTransformer) transformers.next()).draw();
		}
	}

	public void render() {
		Iterator transformers = m_transformers.values().iterator();
		while (transformers.hasNext()) {
			((GeneralTransformer) transformers.next()).render();
		}
	}

  public final void setVisible(boolean aFlag) {
    visible = aFlag;
  }

  public final boolean isVisible() {
    return visible;
  }
}
