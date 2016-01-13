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

import java.awt.Rectangle;

/**
 * Beschreibt die Position einer Komponente in einem Gitter von
 * <code>SimpleGridConstraints</code>.
 * 
 * @author Amsel
 * @mm.docstatus finished
 */
public class SimpleGridConstraints {
    private Rectangle position;

    /**
     * <code>SimpleGridConstraints</code> f&uuml;r eine Komponente in der oberen
     * linken Zelle.
     */
    public SimpleGridConstraints() {
        this(1, 1, 1, 1);
    }

    /**
     * <code>SimpleGridConstraints</code> f&uuml;r eine Komponente in der Spalte
     * <code>col</code> und der Zeile <code>row</code>.
     */
    public SimpleGridConstraints(int col, int row) {
        this(col, row, 1, 1);
    }

    /**
     * <code>SimpleGridConstraints</code> f&uuml;r eine Komponente die in der Spalte
     * <code>col</code> und der Zeile <code>row</code> beginnt und sich &uuml;ber
     * <code>width</code> Spalten und <code>height</code> Zeilen erstreckt.
     */
    public SimpleGridConstraints(int col, int row, int width, int height) {
        position = new Rectangle(col, row, width, height);
    }

    public void setPosition(int column, int row, int width, int height) {
        position.setBounds(column, row, width, height);
    }

    public int getRow() { return (int)position.getY(); }

    public void setRow(int row) {
        setPosition(getColumn(), row, getWidth(), getHeight());
    }

    public int getColumn() { return (int)position.getX(); }

    public void setColumn(int column) {
        setPosition(column, getRow(), getWidth(), getHeight());
    }

    public int getWidth() { return (int)position.getWidth(); }

    public void setWidth(int width) {
        setPosition(getColumn(), getRow(), width, getHeight());
    }

    public int getHeight() { return (int)position.getHeight(); }

    public void setHeight(int height) {
        setPosition(getColumn(), getRow(), getWidth(), height);
    }

}