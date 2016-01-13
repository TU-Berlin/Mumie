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

/**
 * <code>AnchorGridLayout</code> erweitert
 * <code>SimpleGridLayout</code> um die M&ouml;glichkeit, den Platz, den eine Zelle
 * im Gitter ausf&uuml;llt zu definieren. Es ist weiterhin m&ouml;glich einer Komponente
 * ein <code>SimpleGridConstraints</code> mitzugeben. In diesem Fall f&uuml;llt die
 * Komponente den ganzen Platz der Zelle aus. Wird einer Komponente ein
 * <code>AnchorGridConstraints</code> &uuml;bergeben, wird die Komponente wie bei
 * <code>Anchor</code> beschrieben ausgerichtet.
 *
 * @see de.dclj.gridLayout.Anchor
 *
 * @author Amsel
 * @mm.docstatus finished
 */
public class AnchorGridLayout extends SimpleGridLayout {
    public AnchorGridLayout(Container aContainer) {                             //cnt
        super(1, 1, aContainer);                                                //cnt
    }

    public AnchorGridLayout(int cols, int rows, Container aContainer) {         //cnt
        super(cols, rows, aContainer);                                          //cnt
    }

    protected void setComponentBounds(Component comp,
            int left, int top, int width, int height,
            SimpleGridConstraints constraints) {
        if (constraints instanceof AnchorGridConstraints) {
            Anchor anchor = ((AnchorGridConstraints)constraints).getAnchor();
            Dimension prefSize = comp.getPreferredSize();
            Dimension maxSize = new Dimension(
                    Math.min(prefSize.width, width),
                    Math.min(prefSize.height, height));
            if (anchor.hasAnchor(Anchor.LEFT_ANCHOR)) {
                if (!anchor.hasAnchor(Anchor.RIGHT_ANCHOR)) {
                    //links ausrichten
                    width = (int)maxSize.getWidth();
                }
            } else {
                if (anchor.hasAnchor(Anchor.RIGHT_ANCHOR)) {
                    //rechts ausrichten
                    left += width - (int)maxSize.getWidth();
                } else {
                    //zentrieren
                    left += Math.max(0, (width - (int)prefSize.getWidth()) / 2);
                }
                width = (int)maxSize.getWidth();
            }

            if (anchor.hasAnchor(Anchor.TOP_ANCHOR)) {
                if (!anchor.hasAnchor(Anchor.BOTTOM_ANCHOR)) {
                    //oben ausrichten
                    height = (int)maxSize.getHeight();
                }
            } else {
                if (anchor.hasAnchor(Anchor.BOTTOM_ANCHOR)) {
                    //unten ausrichten
                    top += height - (int)maxSize.getHeight();
                } else {
                    //zentrieren
                    top += Math.max(0, (height - (int)prefSize.getHeight()) / 2);
                }
                height = (int)maxSize.getHeight();
            }
        }
        super.setComponentBounds(comp, left, top, width, height, constraints);
    }
}