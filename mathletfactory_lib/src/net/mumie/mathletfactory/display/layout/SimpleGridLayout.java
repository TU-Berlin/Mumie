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
import java.awt.LayoutManager2;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * <code>SimpleGridLayout</code> erm&ouml;glicht es Komponenten fast so flexibel wie
 * ein <code>GridBagLayout</code> anzuordnen, ohne dabei dessen Komplexit&auml;t zu
 * haben.F&uuml;r das Layout wird ein Gitter definiert auf dem die Komponenten
 * angeordnet werden sollen. Viele Komponenten sollen ihre Gr&ouml;&szlig;e in einer (in
 * beiden) Richtung(en) nicht &auml;ndern. Daf&uuml;r kann man eine Zeile (Spalte) als fix
 * definieren. Zus&auml;tzlicher Platz der horizontal (vertikal) vorhanden ist, wird
 * gleichm&auml;&szlig;ig auf alle &uuml;brigen Zeilen (Spalten) verteilt.
 * Zu jeder Komponente mu&szlig; ein <code>SimpleGridConstraints</code> angegeben
 * werden, welches festlegt, an welcher Postion sich die obere linke Ecke der
 * Komponente befindet und &uuml;ber wie viele Zeile (Spalten) sie sich ersteckt.
 * Um das die Breite (H&ouml;he) einer festen Zeile (Spalte) zu beeinflussen kann
 * man <code>Spacer</code> einf&uuml;gen.
 * <p>
 * Ein einfaches Beispiel:<br>
 * Ein Dialog mit einer Liste. Rechts von der Liste am oberen Rand sollen 3
 * Buttons (Hinzuf&uuml;gen, Bearbeiten, L&ouml;schen) stehen und unten rechts unten
 * 2 Buttons OK und abbrechen. Die Buttons sollen sich in der Gr&ouml;&szlig;e nicht &auml;ndern
 * und ihre relative Position (rechts oben bzw. rechts unten behalten). Die
 * Liste soll den zur Verf&uuml;gung stehenden Platz einnehmen.
 * <pre>
 * +------------+----+--------+
 * | list  qq        |  add   |
 * +                 +--------+
 * |                 |  edit  |
 * +                 +--------+
 * |                 |  del   |
 * +                 +--------+
 * |                 |        |
 * |                 |        |
 * +------------+----+--------+
 * |            | ok | cancel |
 * +------------+----+--------+
 * </pre>
 * Wir brauchen also ein 3x5 Gitter wobei die Zeile 1-3 sowie 5 und die
 * Spalten 2-3 fixe Gr&ouml;&szlig;e haben. Die Komponente m&ouml;gen die oben eingezeichneten
 * Namen haben. Der Container soll cont hei&szlig;en:
 * <pre>
 * SimpleGridLayout layout = new SimpleGridLayout(3, 5);
 * layout.fixColumns(2, 3);
 * layout.fixRows(1, 3);
 * layout.fixRow(5);
 * cont.setLayout(layout);
 * cont.add(list, new SimpleGridConstraints(1, 1, 2, 4));
 * cont.add(add, new SimpleGridConstraints(3, 1));
 * cont.add(edit, new SimpleGridConstraints(3, 2));
 * cont.add(del, new SimpleGridConstraints(3, 3));
 * cont.add(ok, new SimpleGridConstraints(2, 5));
 * cont.add(cancel, new SimpleGridConstraints(3, 5));
 * </pre
 * Wenn einem der OK-Button dann noch zu schmal ist, kann man noch eine fixe
 * Blindzeile anh&auml;ngen und dort einen Spacer einf&uuml;gen, der die H&ouml;he 0 und die
 * Breite des Abbrechenbuttons hat.
 *
 * @author Amsel
 * @mm.docstatus finished
 */
public class SimpleGridLayout implements LayoutManager2 {
    private int colCount = 1;
    private int rowCount = 1;
    private int gap = 4;
    private Vector fixedColumns = new Vector();
    private Vector fixedRows = new Vector();
    private Hashtable constraints = new Hashtable();
    private Container container;                                                //cnt

    /**
     * Neues Layout mit 1x1 gro&szlig;em Grid. Das Layout gilt nur f&uuml;r den Container
     * aContainer.
     */
    public SimpleGridLayout(Container aContainer) {                             //cnt
        setContainer(aContainer);                                               //cnt
    }                                                                           //cnt

    /**
     * Neues Layout mit cols x rows gro&szlig;em Grid. Das Layout arbeit nur mit
     * dem angegebenen Container und kann nicht von mehreren Containern
     * gemeinsam genutzt werden.
     */
    public SimpleGridLayout(int cols, int rows, Container aContainer) {         //cnt
        setColumnCount(cols);
        setRowCount(rows);
        setContainer(aContainer);                                               //cnt
    }

    protected void setContainer(Container aContainer) {                         //cnt
        container = aContainer;                                                 //cnt
        container.setLayout(this);                                              //cnt
    }                                                                           //cnt

    /**
     * Pr&uuml;fen, ob der &uuml;bergebene container mit dem Container des Layouts
     * &uuml;bereinstimmt.
     */
    protected void checkContainer(Container aContainer) {                       //cnt
        if (container != aContainer) {                                          //cnt
            String className = getClass().getName();                            //cnt
            throw new IllegalArgumentException(className + "can not be shared");//cnt
        }                                                                       //cnt
    }                                                                           //cnt

    /**
     * <code>SimpleGridConstraints</code> zur Komponent comp suchen
     */
    protected SimpleGridConstraints lookupConstraint(Component comp) {
        return (SimpleGridConstraints)constraints.get(comp);
    }

    /**
     * Erzeugt ein 2dim Objectarray in dem alle Komponenten eingetragen werden,
     * die eine horizontal oder vertikal genau eine Zelle belegen. Daraus wird
     * die Gr&ouml;&szlig;e der feste Zeilen (Spalten) ermittelt. Komponenten, die sich
     * &uuml;ber mehrere Zeilen (Spalten) Spalten erstrecken, gehen in die
     * Betrachtung nicht ein.
     */
    protected Object[][] createGrid() {
        Object[][] result = new Object[getColumnCount()][getRowCount()];
        Enumeration enumeration = constraints.keys();
        Component comp;
        SimpleGridConstraints con;
        int col, row;

        while (enumeration.hasMoreElements()) {
            comp = (Component)enumeration.nextElement();
            con = lookupConstraint(comp);
            //Komponenten muessen auch eingetragen werden, wenn ihre Zeile _oder_
            //Spalte Breite eins hat. Beides ist nicht zwingend. Siehe auch
            //Berechnung der Spalten- und Zeilenbreiten
            if ((con.getWidth() == 1) || (con.getHeight() == 1)) {              //bf1
                result[con.getColumn()-1][con.getRow()-1] = comp;
            }
        }
        return result;
    }

    /**
     * Berechnet die Breite der fixen Spalten. Das Ergebnis ist ein Array das
     * so lang ist, wie das Gitter breit ist. An den Positionen der variable
     * breiten Spalten steht eine -1 bei den fixen Spalten die preferredSize
     * der breitesten Komponente.
     */
    public int[] calcColumnWidths(Object[][] grid) {
        int[] result = new int[grid.length];
        Component comp;
        SimpleGridConstraints con;                                              //bf1 (siehe Kommentar bei createGrid
        int colWidth;
        for (int c = 0; c < result.length; c++) {
            colWidth = -1;
            if (isColumnFixed(c + 1)) {
                for (int r = 0; r < grid[c].length; r++) {
                    if (grid[c][r] != null) {
                        comp = (Component)grid[c][r];
                        con = lookupConstraint(comp);                           //bf1
                        if (con.getWidth() == 1)                                //bf1
                            colWidth = Math.max(colWidth, (int)comp.getPreferredSize().getWidth());
                    }
                }
                //Abstand nur hinzurechnen, wenn mind. 1 Komponente eine Breite
                //hat (Spacer koennen Breite 0 haben)
                if (colWidth > 0)                                               //bf1
                    colWidth += 2*gap;                                          //bf1
            }
            result[c] = colWidth;
        }
        return result;
    }

    /**
     * Berechnet die Breite der fixen Zeile. Das Ergebnis ist ein Array das
     * so lang ist, wie das Gitter hoch ist. An den Positionen der variable
     * hohen Zeilen steht eine -1 bei den fixen Zeile die preferredSize
     * der h&ouml;chsten Komponente.
     */
    public int[] calcRowHeights(Object[][] grid) {
        int[] result = new int[grid[0].length];
        Component comp;
        SimpleGridConstraints con;                                              //bf1 (siehe Kommentar bei createGrid
        int rowHeight;
        for (int r = 0; r < result.length; r++) {
            rowHeight = -1;
            if (isRowFixed(r + 1)) {
                for (int c = 0; c < grid.length; c++) {
                    if (grid[c][r] != null) {
                        comp = (Component)grid[c][r];
                        con = lookupConstraint(comp);                           //bf1
                        if (con.getHeight() == 1)                               //bf1
                            rowHeight = Math.max(rowHeight, (int)comp.getPreferredSize().getHeight());
                    }
                }
                //Abstand nur hinzurechnen, wenn mind. 1 Komponente eine Hoehe
                //hat (Spacer koennen Hoehe 0 haben)
                if (rowHeight > 0)                                              //bf1
                    rowHeight += 2*gap;                                         //bf1
            }
            result[r] = rowHeight;
        }
        return result;
    }

    /**
     * Berechnet f&uuml;r Zeilen (Spalten) die Anfangsposition der Komponenten in der
     * jeweiligen Zeile, Spalte unter Ber&uuml;cksichtigung des angegebenen
     * Komponentenabstand.
     */
    protected void calcPositions(int[] positions, int length, int fixedNum) {
        int varLength = 0, currentPosition = 0, currentLength;
        for (int i = 0; i < positions.length; i++)
            if (positions[i] > 0)
                varLength += positions[i];
        //bf1 Wenn alle Zeilen (Spalten fix) sind gibt
        //positions.length - fixedNum eine divByZero
        if (positions.length <= fixedNum) {                                     //bf1
            varLength = 0;                                                      //bf1
        } else {                                                                //bf1
            varLength += 2*gap;
            varLength = (length - varLength) / (positions.length - fixedNum);
            varLength -= 2*gap;
            varLength = Math.max(0, varLength);
        }                                                                       //bf1

        /*die gap steckt schon in den berechneten Positionen, also nicht jedes
          Mal aufwendig abziehen und zurechnen sondern nur einmal am Anfang
          setzen. Bei den Gittern variabler Laenge _muss_ die gap beruecksichtigt
          werden, es sei denn, das Gitter hat an der Stelle ohnehin die Laenge
          (Breite) 0*/
        currentPosition += 2*gap;                                               //bf3
        for (int i = 0; i < positions.length; i++) {
            currentLength = positions[i];
            positions[i] = currentPosition;
            if (currentLength == -1) {
                if (varLength > 0)                                              //bf3
                    currentPosition += varLength +2*gap;                        //bf3
            } else
                currentPosition += currentLength;                               //bf3
        }
    }

    /**
     * Anzahl der Spalten
     */
    public int getColumnCount() { return colCount; }

    /**
     * Anzahl der Spalten setzen. Die Spaltenzahl mu&szlig; mindestens eins sein.
     * Andernfalls wird eine <code>IllegalArgumentException</code> geworfen.
     */
    public void setColumnCount(int value) {
        if (value < 1)
            throw new IllegalArgumentException("Number of columns has to be at least 1");
        colCount = value;
    }

    /**
     * Anzahl der Zeilen.
     */
    public int getRowCount() { return rowCount; }

    /**
     * Anzahl der Zeilen setzen. Die Zeilenzahl mu&szlig; mindestens eins sein.
     * Andernfalls wird eine <code>IllegalArgumentException</code> geworfen.
     */
    public void setRowCount(int value) {
        if (value < 1)
            throw new IllegalArgumentException("Number of rows has to be at least 1");
        rowCount = value;
    }

    /**
     * Abstand Rand-Komponente bzw. Komponente-Komponente lesen
     */
    public int getGap() { return gap; }

    /**
     * Abstand Rand-Komponente bzw. Komponente-Komponente setzen. Der Abstand
     * darf nicht kleiner 0 werden. Andernfalls wird eine
     * <code>IllegalArgumentException</code> geworfen.
     */
    public void setGap(int value) {
        if (gap < 0)
            throw new IllegalArgumentException("gap can't be less than zero");
        gap = value;
    }

    /**
     * Eine Spalte <code>column</code>als unver&auml;nderlich in der Gr&ouml;&szlig;e markieren
     * Die Breite einer solchen Spalte richtet sich nach der preferredSize der
     * breitesten Komponente in dieser Spalte.
     */
    public void fixColumn(int column) {
        if ((column > 0) || (column < getColumnCount())) {                      //fp

            Integer col = new Integer(column);

            if (fixedColumns.indexOf(col) < 0)
                fixedColumns.add(col);
        }                                                                       //fp
    }

    /**
     * Spalten <code>firstColumn</code> bis einschlie&szlig;lich
     * <code>lastColumn</code> als fix markieren.
     */
    public void fixColumns(int firstColumn, int lastColumn) {
        for (int i = firstColumn; i <= lastColumn; i++)
            fixColumn(i);
    }

    /**
     * <code>true</code>, wenn die Spalte <code>column</code> als
     * gr&ouml;&szlig;enunver&auml;nderlich markiert wurde.
     */
    public boolean isColumnFixed(int column) {
        return fixedColumns.indexOf(new Integer(column)) > -1;
    }

    /**
     * Eine Zeile <code>row</code>als unver&auml;nderlich in der Gr&ouml;&szlig;e markieren
     * Die H&ouml;he einer solchen zeile richtet sich nach der preferredSize der
     * h&ouml;chsten Komponente in dieser Zeile.
     */
    public void fixRow(int row) {
        if ((row > 0) || (row < getRowCount())) {                               //fp

            Integer theRow = new Integer(row);

            if (fixedRows.indexOf(theRow) < 0)
                fixedRows.add(theRow);
        }                                                                       //fp
    }

    /**
     * Zeilen <code>firstRow</code> bis einschlie&szlig;lich
     * <code>lastRow</code> als fix markieren.
     */
    public void fixRows(int firstRow, int lastRow) {
        for (int i = firstRow; i <= lastRow; i++)
            fixRow(i);
    }

    /**
     * <code>true</code>, wenn die Zeile <code>row</code> als
     * gr&ouml;&szlig;enunver&auml;nderlich markiert wurde.
     */
    public boolean isRowFixed(int row) {
        return fixedRows.indexOf(new Integer(row)) > -1;
    }

    /**
     * Das Einf&uuml;gen einer Komponente mit einem String als constraint ist nicht
     * m&ouml;glich. Es wird eine IllegalArgumentException geworfen.
     */
    public void addLayoutComponent(String name, Component comp) {
        throw new IllegalArgumentException("addLayoutComponent(String name, Component comp) not allowed");
    }

    public void removeLayoutComponent(Component comp) {
        constraints.remove(comp);
    }

    /**
     * Berechnet die Gr&ouml;&szlig;e der nicht fix gekennzeichneten Zeilen (Spalten)
     */
    protected int calcVarSizes(int[] sizes, int start, int length,              //bf1
            int prefSize, Vector fixed) {                                       //bf1
    //Berechnung der prefSize nochmal auf den Kopf gestellt

        if (prefSize == 2*gap) {  //ohne Ausdehnung keine Berechnung notwendig  //bf1
            return -1;                                                          //bf1
        }                                                                       //bf1

        int fixedNum = 0;
        int size = 0;
        for (int i = start; i < start + length; i++) {
            if (fixed.indexOf(new Integer(i)) > -1) {
                size += sizes[i-1];
                fixedNum++;
            }
        }
        if (size > prefSize)
            return -1;
        if (length <= fixedNum)
            return -1;
        return (prefSize - size) / (length - fixedNum);                         //bf1
    }

    /**
     * Alle H&ouml;hen (Breiten) der Zeilen (Spalten) aufsummieren
     */
    protected int sumSizes(int[] sizes, int maxVarSize) {
        int result = 0;
        for (int i = 0; i < sizes.length; i++) {
            //variable Zeilen (Spalten) in die Berechnung einbeziehen
            if (sizes[i] == -1)                                                 //bf1
                result += maxVarSize;                                           //bf1
            else                                                                //bf1
                result += sizes[i];
        }
        return result;
    }

    public Dimension preferredLayoutSize(Container parent) {                    //ps: start
        Object[][] grid = createGrid();
        int[] colWidths = calcColumnWidths(grid);
        int[] rowHeights = calcRowHeights(grid);
        Enumeration enumeration = constraints.keys();
        Component comp;
        SimpleGridConstraints constraint;
        int maxVarWidth = 0;                                                    //bf1
        int maxVarHeight = 0;                                                   //bf1

        while (enumeration.hasMoreElements()) {
            comp = (Component)enumeration.nextElement();
            constraint = lookupConstraint(comp);

            maxVarWidth = Math.max(maxVarWidth, calcVarSizes(colWidths,         //bf1
                    constraint.getColumn(),  constraint.getWidth(),             //bf1
                    (int)comp.getPreferredSize().getWidth() + 2*gap,            //bf1
                    fixedColumns));                                             //bf1
            maxVarHeight = Math.max(maxVarHeight, calcVarSizes(rowHeights,      //bf1
                    constraint.getRow(),  constraint.getHeight(),               //bf1
                    (int)comp.getPreferredSize().getHeight() + 2*gap,           //bf1
                    fixedRows));                                                //bf1
        }

        return new Dimension(                                                   //bf1
                sumSizes(colWidths, maxVarWidth) + 2*gap,                       //bf1
                sumSizes(rowHeights, maxVarHeight) + 2*gap);                    //bf1
    }                                                                           //ps: stop

    public Dimension minimumLayoutSize(Container parent) {
        checkContainer(parent);                                                 //cnt
        return preferredLayoutSize(parent);                                     //ps
    }

    protected void setComponentBounds(Component comp,                           //agl
            int left, int top, int width, int height,                           //agl
            SimpleGridConstraints constraints) {                                //agl
        comp.setBounds(left, top, width, height);                               //agl
    }                                                                           //agl

    public void layoutContainer(Container parent) {
        checkContainer(parent);                                                 //cnt
        synchronized (parent.getTreeLock()) {
            Object[][] grid = createGrid();
            int[] colWidths = calcColumnWidths(grid);
            calcPositions(colWidths, parent.getWidth(), fixedColumns.size());
            int[] rowHeights = calcRowHeights(grid);
            calcPositions(rowHeights, parent.getHeight(), fixedRows.size());

            SimpleGridConstraints con;
            Component comp;
            int x, y, w, h;

            Enumeration enumeration = constraints.keys();
            while (enumeration.hasMoreElements()) {
                comp = (Component)enumeration.nextElement();
                con = lookupConstraint(comp);
                x = colWidths[con.getColumn() - 1];
                y = rowHeights[con.getRow() - 1];
                if (con.getColumn() - 1 + con.getWidth() < colWidths.length)
                    w = colWidths[con.getColumn() - 1 + con.getWidth()] - x;
                else
                    w = parent.getWidth() - x;
                if (con.getRow() - 1 + con.getHeight() < rowHeights.length)
                    h = rowHeights[con.getRow() - 1 + con.getHeight()] - y;
                else
                    h = parent.getHeight() - y;

                setComponentBounds(comp, x, y, w-2*gap, h-2*gap, con);          //agl
            }
        }
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof SimpleGridConstraints)                       //bf2
            this.constraints.put(comp, constraints);
        else                                                                    //bf2
            throw new IllegalArgumentException(                                 //bf2
                    "constraint has to be a SimpleGridConstraints");            //bf2
    }

    public Dimension maximumLayoutSize(Container target) {
        checkContainer(target);                                                 //cnt
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public float getLayoutAlignmentX(Container target) {
        checkContainer(target);                                                 //cnt
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container target) {
        checkContainer(target);                                                 //cnt
        return 0.5f;
    }

    public void invalidateLayout(Container target) {
        checkContainer(target);                                                 //cnt
    }

}