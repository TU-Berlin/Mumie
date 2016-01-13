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

package net.mumie.mathletfactory.display.jr3d;


import net.mumie.mathletfactory.display.DisplayProperties;


/**
 * @author jweber
 * 
 */
public class JR3DCompositeDrawable extends JR3DDrawable {

	protected JR3DDrawable m_allDrawables[];

	public JR3DCompositeDrawable( JR3DDrawable[] drawables ) {
		this( null, drawables );
	}

	public JR3DCompositeDrawable( final String name, JR3DDrawable[] drawables ) {
		super( name );
		this.m_allDrawables = drawables;

		for ( int i = 0; i < drawables.length; i++ ) {
			this.m_sceneContent.addChild( drawables[i].getViewContent() );
		}
	}

	public JR3DDrawable getJR3DDrawable( int index ) {
		try {
			return this.m_allDrawables[index];
		} catch ( ArrayIndexOutOfBoundsException e ) {
			return null;
		}
	}

	public void setSelected( boolean b ) {
		for ( int i = 0; i < this.m_allDrawables.length; i++ ) {
			this.m_allDrawables[i].setSelected( b );
		}
	}

	public void render( DisplayProperties properties ) {
		for ( int i = 0; i < this.m_allDrawables.length; i++ ) {
			this.m_allDrawables[i].getDrawableProperties().copyPropertiesFrom(properties);
			this.m_allDrawables[i].render( properties );
		}
	}
}
