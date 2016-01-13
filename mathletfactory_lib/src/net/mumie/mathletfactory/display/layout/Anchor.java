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


/**
 * <code>Anchor</code> legt fest, an welchen Punkten sich eine Komponenten
 * festhalten kann. Die Komponente h&auml;lt sich innerhalb ihrer Zelle immer an
 * ihrer Ankerpunkten fest. H&auml;lt sie sich an zwei gegen&uuml;berliegen Punkten fest,
 * &auml;ndert sich ihre Gr&ouml;&szlig;e auf Zellenbreite (bzw.-h&ouml;he). H&auml;lt sie sich an keinen
 * der beiden Punkte fest, wird sie in der Zelle zentriert.
 *
 * @author Amsel
 * @mm.docstatus finished
 */
public class Anchor {
    public static final int LEFT_ANCHOR = 1;
    public static final int RIGHT_ANCHOR = 2;
    public static final int TOP_ANCHOR = 4;
    public static final int BOTTOM_ANCHOR = 8;
    public static final int ALL_ANCHORS =
            LEFT_ANCHOR | RIGHT_ANCHOR | TOP_ANCHOR | BOTTOM_ANCHOR;

    private int anchor = LEFT_ANCHOR | TOP_ANCHOR;

    public Anchor() {
    }

    public Anchor(int aAnchor) {
        anchor = aAnchor;
    }

    public void setAllAnchors() {
        anchor = ALL_ANCHORS;
    }

    public void setAnchor(int aAnchor) {
        anchor |= aAnchor;
    }

    public void unsetAnchor(int aAnchor) {
        anchor &= Integer.MAX_VALUE ^ aAnchor;
    }

    public boolean hasAnchor(int aAnchor) {
        return (anchor & aAnchor) == aAnchor;
    }

}