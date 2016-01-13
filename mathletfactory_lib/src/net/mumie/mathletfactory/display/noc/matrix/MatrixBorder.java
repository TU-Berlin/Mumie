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

package net.mumie.mathletfactory.display.noc.matrix;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import javax.swing.border.AbstractBorder;


/**
 * This class is used by MMMatrixPanel to render a border for the matrix. Four
 * different border styles are avaible.
 *
 * @author Gronau
 * @mm.docstatus finished
 */
public class MatrixBorder extends AbstractBorder {


  /** Constant for an empty border type. */
  public final static int NO_BORDER = 0;

  /** Constant for a parentheses ('(' shaped) border type. */
  public final static int PARENTHESES = 1;

  /** Constant for a square bracket ('[' shaped) border type. */
  public final static int BRACKETS = 2;

  /** Constant for a vertical line border type (like used in determinants). */
  public final static int DETERMINANT = 3;

  public final static int LEFT_AND_RIGHT_BORDER = 0;
  public final static int ONLY_LEFT_BORDER = 1;
  public final static int ONLY_RIGHT_BORDER = 2;

	/** Field containing the color of the matrice brackets. */
	private Color m_bracketColor = Color.BLACK;

	/** Field containing the insets of this border. */
	private Insets m_insets = new Insets(0, 0, 0, 0);

	/** Constant for fine tuning of control point's location. */
	public static final double CONTROL_POINT_FACTOR = 0.4;

	/** Field containing the component for this border. */
	private Component m_component;

	/** Field containing the border type of this matrix panel (square, determinant style, etc.). */
	private int m_borderType;

	/** Field indicating whether the left or right or both borders should be visible.*/
	private int m_showBorders = LEFT_AND_RIGHT_BORDER;

	/**
	 * Creates a new MMMatrixBorder object for the given component <code>c</code>
	 * and the defined border type.
	 *
   * @see MatrixBorder#NO_BORDER
	 * @see MatrixBorder#PARENTHESES
	 * @see MatrixBorder#BRACKETS
   * @see MatrixBorder#DETERMINANT
	 */
	public MatrixBorder(Component component, int borderType) {
	  this(component, borderType, LEFT_AND_RIGHT_BORDER);
	}

	public MatrixBorder(Component component, int borderType, int showBorders) {
		m_component = component;
    setBorderType(borderType);
	  m_showBorders = showBorders;
	}

	/**
	 * Paints the border of the component <code>c</code>. This method is
	 * called internally by java and draws the border of the component <code>c</code>
	 * using the Graphicsobject <code>g</code>. x,y,width and height are not
	 * used!
	 */
	public void paintBorder(
		Component c,
		Graphics g,
		int x,
		int y,
		int width,
		int height) {
    // no painting of border necessary in this case, all arguments may rest
    // untreated!
    if (m_borderType == NO_BORDER) {
      return;
    }
//    g.setColor(m_bracketColor);
    int compWidth = m_component.getWidth(); // width of ALL columns
    int compHeight = m_component.getHeight(); // height of ALL rows
    //System.out.println("brackets of "+m_component+": "+m_borderType);
    if (m_borderType == DETERMINANT || m_borderType == BRACKETS) {
      if(m_showBorders == LEFT_AND_RIGHT_BORDER || m_showBorders == ONLY_LEFT_BORDER)
	      g.drawLine(
	        1,
	        m_insets.top/2,
	        1,
	        compHeight - m_insets.bottom/2);
      if(m_showBorders == LEFT_AND_RIGHT_BORDER || m_showBorders == ONLY_RIGHT_BORDER)
	      g.drawLine(
	        compWidth - 1,
	        m_insets.top/2,
	        compWidth - 1,
	        compHeight - m_insets.bottom/2);

    }

    if (m_borderType == BRACKETS) {
      // left bracket
      if(m_showBorders == LEFT_AND_RIGHT_BORDER || m_showBorders == ONLY_LEFT_BORDER) {
	      g.drawLine(1, m_insets.top/2, m_insets.left/2 + 1, m_insets.top/2);
	      g.drawLine(1, compHeight - m_insets.bottom/2, m_insets.left/2 + 1, compHeight - m_insets.bottom/2);
      }

      // right bracket
      if(m_showBorders == LEFT_AND_RIGHT_BORDER || m_showBorders == ONLY_RIGHT_BORDER) {
	      g.drawLine(compWidth - 1, m_insets.top/2, compWidth - (m_insets.right/2 + 1), m_insets.top/2);
	      g.drawLine(compWidth - 1, compHeight - m_insets.bottom/2, compWidth - (m_insets.right/2 + 1), compHeight - m_insets.bottom/2);
      }
    }

    if (m_borderType == PARENTHESES) {
      g.setColor(m_bracketColor);
      Graphics2D g2 = (Graphics2D) g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      //			left paranthesis
      if(m_showBorders == LEFT_AND_RIGHT_BORDER || m_showBorders == ONLY_LEFT_BORDER) {
	      Point2D.Double startLeft = new Point2D.Double(m_insets.left - 1, 1);

	      Point2D.Double controlLeft =
	        new Point2D.Double(-m_insets.left * CONTROL_POINT_FACTOR, compHeight / 2);

	      Point2D.Double endLeft = new Point2D.Double(m_insets.left - 1, compHeight - 1);

	      QuadCurve2D.Double quadLeft = new QuadCurve2D.Double();
	      quadLeft.setCurve(startLeft, controlLeft, endLeft);
	      g2.draw(quadLeft);
      }
      //			right paranthesis
      if(m_showBorders == LEFT_AND_RIGHT_BORDER || m_showBorders == ONLY_RIGHT_BORDER) {
	      Point2D.Double startRight = new Point2D.Double(compWidth - m_insets.right, 0);

	      Point2D.Double controlRight =
	        new Point2D.Double(compWidth + m_insets.right * CONTROL_POINT_FACTOR, compHeight / 2);

	      Point2D.Double endRight = new Point2D.Double(compWidth - m_insets.right, compHeight);

	      QuadCurve2D.Double quadRight = new QuadCurve2D.Double();
	      quadRight.setCurve(startRight, controlRight, endRight);
	      g2.draw(quadRight);
      }
    }
  }

	/**
	 * Sets the color of the component's "bracket".
	 */
	public void setBracketColor(Color curveColor) {
		this.m_bracketColor = curveColor;
	}

	/**
	 * Returns (a copy of) the color of the components "bracket".
	 */
	public Color getBracketColor() {
		return new Color(
			m_bracketColor.getRed(),
			m_bracketColor.getGreen(),
			m_bracketColor.getBlue());
	}

	/**
	 * Returns (a copy of) the insets of the border.
	 */
	public Insets getBorderInsets() {
		return new Insets(
			m_insets.top,
			m_insets.left,
			m_insets.bottom,
			m_insets.right);
	}

	// Methode fehlerhaft
	/**
	 * Returns (a copy of) the insets of the border.
	 *
	 * @param comp
	 *          the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component comp) {
		return new Insets(
			m_insets.top,
			m_insets.left,
			m_insets.bottom,
			m_insets.right);
	}

	/** Sets the border size to 'insets'. */
	public void setBorderInsets(Insets insets) {
		m_insets =
			new Insets(insets.top, insets.left, insets.bottom, insets.right);
	}

  /** Returns the border type of this matrix panel (square, determinant style, etc.). */
  public int getBorderType() {
    return m_borderType;
  }

  /** Sets the border type of this matrix panel (square, determinant style, etc.). */
  public void setBorderType(int i) {
    m_borderType = i;
    if (m_borderType == DETERMINANT || m_borderType == BRACKETS) {
      m_insets.left = 5;
      m_insets.right = 5;
      m_insets.top = 2;
      m_insets.bottom = 2;
    }

    if (m_borderType == PARENTHESES) {
      m_insets.left = 12;
      m_insets.right = 12;
      m_insets.top = 0;
      m_insets.bottom = 0;
    }

    if (m_borderType == NO_BORDER) {
      m_insets.left = 0;
      m_insets.right = 0;
      m_insets.top = 0;
      m_insets.bottom = 0;
    }

  }
}