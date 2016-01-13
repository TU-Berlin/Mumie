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

package net.mumie.mathletfactory.transformer.jr3d;


import net.mumie.mathletfactory.display.jr3d.JR3DCompositeDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DFaceSetDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DLineSetDrawable;
import net.mumie.mathletfactory.display.jr3d.JR3DPointSetDrawable;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.analysis.function.multivariate.MMParametricFunctionInR3;


/**
 * @author jweber
 * 
 */
public class JR3DParametricFunctionTransformer extends JR3DCanvasTransformer {

	public void initialize( MMObjectIF masterObject ) {
		super.initialize( masterObject );
		this.m_allDrawables = new JR3DDrawable[] { new JR3DPointSetDrawable( "point" ), new JR3DLineSetDrawable( "line" ), new JR3DFaceSetDrawable( "polygons" ) };
		this.m_activeDrawable = new JR3DCompositeDrawable( masterObject.getLabel(), (net.mumie.mathletfactory.display.jr3d.JR3DDrawable[] ) this.m_allDrawables );
	}

	public void synchronizeMath2Screen() {
		MMParametricFunctionInR3 master = ( MMParametricFunctionInR3 ) this.getMaster();
		double[][] values = master.getDoubleValues();

		if ( master.getParameters().length < 1 ) {
			( ( JR3DPointSetDrawable ) this.m_allDrawables[0] ).setPoints( values );
		} else if ( master.getParameters().length == 1 ) {
			// Anzahl Punkte des Polygonobjektes
			int verticesCount = master.getVerticesCount() + 1;
			// Anzahl Kanten des Polygonobjektes
			int linesCount = verticesCount - 1;

			int[][] gridLines = new int[linesCount][];

			for ( int i = 0; i < verticesCount - 1; i++ ) {
				gridLines[i] = new int[] { i, i + 1 };
			}

			( ( JR3DLineSetDrawable ) this.m_allDrawables[1] ).setLines( values, gridLines );
		} else {
			int fragmentation = master.getVerticesCount();
			JR3DFaceSetDrawable.createSurface( ( JR3DFaceSetDrawable ) this.m_allDrawables[2], values, fragmentation, fragmentation );
		}
		renderLabel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.mumie.mathletfactory.transformer.CanvasObjectTransformer#render()
	 * @override
	 */
//	@Override
	public void render() {
		int parameterCount = ( ( MMParametricFunctionInR3 ) this.getMaster() ).getParameters().length;

		( ( JR3DDrawable ) this.m_activeDrawable ).setSelected( this.getMaster().isSelected() );
		if ( parameterCount < 1 ) {
			( ( JR3DPointSetDrawable ) this.m_allDrawables[0] ).setVisible( true );
			( ( JR3DLineSetDrawable ) this.m_allDrawables[1] ).setVisible( false );
			( ( JR3DFaceSetDrawable ) this.m_allDrawables[2] ).setVisible( false );
		} else if ( parameterCount == 1 ) {
			( ( JR3DPointSetDrawable ) this.m_allDrawables[0] ).setVisible( false );
			( ( JR3DLineSetDrawable ) this.m_allDrawables[1] ).setVisible( true );
			( ( JR3DFaceSetDrawable ) this.m_allDrawables[2] ).setVisible( false );
		} else {
			( ( JR3DPointSetDrawable ) this.m_allDrawables[0] ).setVisible( false );
			( ( JR3DLineSetDrawable ) this.m_allDrawables[1] ).setVisible( false );
			( ( JR3DFaceSetDrawable ) this.m_allDrawables[2] ).setVisible( true );
		}
		super.render();
	}
}
