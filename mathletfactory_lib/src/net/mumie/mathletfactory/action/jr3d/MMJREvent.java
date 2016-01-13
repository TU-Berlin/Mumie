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

package net.mumie.mathletfactory.action.jr3d;


import net.mumie.mathletfactory.action.MMEvent;
import net.mumie.mathletfactory.math.algebra.linalg.NumberTuple;
import net.mumie.mathletfactory.math.number.MDouble;
import de.jreality.scene.Geometry;


/**
 * @author jweber
 * 
 */
public class MMJREvent extends MMEvent {

	// TODO Die Werte noch mal genauer analysieren
	public static final int POINT_DRAG_EVENT = 1000;
	public static final int LINE_DRAG_EVENT = 1001;
	public static final int FACE_DRAG_EVENT = 1002;

	// PointDragEvent, LineDragEvent, FaceDragEvent: die einzelnen Geometry-Objekte sind die Quellen
	protected int index; // zeigt an, welches Objekt modifiziert wurde
	protected NumberTuple translation; // erstmal fuer das Ziehen der Linie und Flaeche double[4] Array, das letzte Feld ist irrelevant

	/**
	 * 
	 */
	public MMJREvent( Geometry geometrySet, int index, double[] translation, int type ) {
		if ( type >= MMJREvent.POINT_DRAG_EVENT && type <= MMJREvent.FACE_DRAG_EVENT ) {
			this.setEventType( type );
			this.setSource( geometrySet );
			this.index = index;
			this.translation = new NumberTuple( MDouble.class, translation[0], translation[1], translation[2] );
		} else {
			throw new IllegalArgumentException( "Kein gueltiges Ereignis" ); // TODO Uebersetzung ins Englische
		}
		// TODO Logger, welches Eregnis gerade erstellt wurde
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}

	public NumberTuple getTranslation() {
		return this.translation;
	}
}
