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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Der Spacer kann verwendet werden, um im <code>SimpleGridLayout</code> eine
 * fixe Zeile (Spalte) auf die gew&uuml;nschte Gr&ouml;&szlig;e zu zwingen. Dazu gibt man im
 * Konstruktor gew&uuml;nschte H&ouml;he und Breite an. Es kann auch 0 &uuml;bergeben werden.
 * Dann hat der Spacer auf die H&ouml;he (bzw. Breite) der Zeile (bzw. Spalte) keinen
 * Einflu&szlig;.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class Spacer extends Component {
    private Dimension preferredSize;

    public Spacer(int aWidth, int aHeight) {
        this(new Dimension(aWidth, aHeight));
    }

    public Spacer(Dimension aPreferredSize) {
        preferredSize = aPreferredSize;
    }

    public Spacer(int aWidth, int aHeight, Color background) {                  //bgc
        this(new Dimension(aWidth, aHeight), background);                       //bgc
    }                                                                           //bgc
                                                                                //bgc
    public Spacer(Dimension aPreferredSize, Color background) {                 //bgc
        this(aPreferredSize);                                                   //bgc
        setBackground(background);                                              //bgc
    }                                                                           //bgc

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public void paint(Graphics gr) {                                            //bgc
        super.paint(gr);                                                        //bgc
        gr.setColor(getBackground());                                           //bgc
        gr.fillRect(0, 0, this.getWidth(), this.getHeight());                   //bgc
    }

}