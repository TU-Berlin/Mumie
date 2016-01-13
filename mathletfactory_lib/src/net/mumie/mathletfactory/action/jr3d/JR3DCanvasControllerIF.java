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


import net.mumie.mathletfactory.action.CanvasControllerIF;
import de.jreality.scene.tool.Tool;


/**
 * Das Interface bildet die Grundlage fuer die Behandlung der eingehenden JReality-Events.<br>
 * JReality bietet umfangreiche Moeglichkeiten zur Interaktion zwischen dem Benutzer und der Componente.<br>
 * Das Ganze wird mit Hilfe eines Tools-Konzeptes realisiert. Die Tools werden durch bestimmte Ereignisse (Maus, Tastatur) angesprochen.<br>
 * Die Kontroller definieren die Tools, welche dann die Behandlung der Ereignisse uebernehmen und somit die Manipulation der Objekte ermoeglichen sollen. <br>
 * 
 * @see CanvasControllerIF
 * 
 * @author Juergen Weber
 */
public interface JR3DCanvasControllerIF extends CanvasControllerIF {

	public Tool[] getTools();
}
