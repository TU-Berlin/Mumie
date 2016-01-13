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

package net.mumie.mathletfactory.util;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import net.mumie.mathletfactory.action.message.TodoException;
import net.mumie.mathletfactory.display.MM3DCanvas;
import net.mumie.mathletfactory.display.MMCanvas;
import net.mumie.mathletfactory.display.g2d.MMG2DCanvas;

/**
 * This class is used to dispay custom messages in a {@link net.mumie.mathletfactory.display.MMCanvas}.
 * 
 * @author gronau
 * @mm.docstatus finished
 *
 */
public class CanvasMessage {

	class MessageTimerTask extends TimerTask {

		/*
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			m_message = null;
			if (m_position == AT_MOUSE_CURSOR)
				m_canvas.getDrawingBoard().removeMouseMotionListener(m_motionListener);
		}
	}

	/** Constant for a message in the top left canvas's corner. */
	public static final int TOP_LEFT = 0;

	/** Constant for a message in center of the canvas. */
	public static final int CENTERED = 1;

	/** Constant for a message following the mouse cursor. */
	public static final int AT_MOUSE_CURSOR = 2;

	public final static int PLAIN_MESSAGE = 0;
	public final static int INFO_MESSAGE = 1;
	public final static int ALERT_MESSAGE = 2;

	/** The canvas in which the message will be displayed. */
	private MMCanvas m_canvas;

	/** The message that should be displayed. */
	private String m_message;

	/** The message's type. */
	private int m_messageType = PLAIN_MESSAGE;

	/** The position of the message (choosen from constants). */
	private int m_position;

	/** The insets (top, left, bottom, right margin) of message's background. */
	private Insets m_insets = new Insets(2, 2, 2, 2);

	/** The background color. */
	private Color m_alertColor = new Color(240, 200, 200);

	private Color m_infoColor = new Color(240, 240, 200);

	private int m_mouseX, m_mouseY;
	private MouseMotionListener m_motionListener;

	/**
	 * The duration for the time the message will be displayed. 0 means forever.
	 */
	private long m_duration;

	/**
	 * Creates a new CanvasMessage instance for the canvas <code>canvas</code>
	 * at the top left corner with infinite display time.
	 */
	public CanvasMessage() {
		this(null, null, 0, TOP_LEFT, PLAIN_MESSAGE);
	}

	/**
	 * Creates a new CanvasMessage instance for the canvas <code>canvas</code>
	 * at the top left corner with infinite display time.
	 *
	 * @param canvas
	 *          the canvas in which it will be displayed
	 */
	public CanvasMessage(MMCanvas canvas) {
		this(canvas, null, 0, TOP_LEFT, PLAIN_MESSAGE);
	}
	/**
	 * Creates a new CanvasMessage instance for the canvas <code>canvas</code>
	 * at the top left corner with infinite display time.
	 *
	 * @param canvas
	 *          the canvas in which it will be displayed
	 * @param message
	 *          the message to be displayed
	 */
	public CanvasMessage(MMCanvas canvas, String message) {
		this(canvas, message, 0, TOP_LEFT, PLAIN_MESSAGE);
	}

	/**
	 * Creates a new CanvasMessage instance for the canvas <code>canvas</code>
	 * at the specified position with <code>duration</code> milliseconds
	 * display time.
	 *
	 * @param canvas
	 *          the canvas in which it will be displayed
	 * @param message
	 *          the message to be displayed
	 * @param position
	 *          choosen from constants
	 * @param duration
	 *          the time the message will be showing (in milliseconds)
	 *
	 * @see #TOP_LEFT
	 * @see #CENTERED
	 * @see #AT_MOUSE_CURSOR
	 */
	public CanvasMessage(
		MMCanvas canvas,
		String message,
		long duration,
		int position,
		int messageType) {
		m_canvas = canvas;
		m_message = message;
		m_duration = duration;
		m_position = position;
		m_messageType = messageType;
		if (m_canvas != null) {
			m_canvas.repaint();
			if (m_position == AT_MOUSE_CURSOR) {
				m_motionListener = new MouseMotionAdapter() {
					public void mouseMoved(MouseEvent e) {
						m_mouseX = e.getX();
						m_mouseY = e.getY();
						m_canvas.repaint();
					}
				};
				m_canvas.getDrawingBoard().addMouseMotionListener(m_motionListener);
			}
		}
	}

	public void drawMessage(int lineNr) {
		if (m_canvas == null)
			throw new IllegalStateException("Canvas is null! .setCanvas(Canvas) must be called before.");
		Graphics2D g2;
		if (m_canvas instanceof MMG2DCanvas)
			g2 = (Graphics2D) ((MMG2DCanvas)m_canvas).getGraphics2D();
		else if (m_canvas instanceof MM3DCanvas)
			g2 = (Graphics2D)m_canvas.getDrawingBoard().getGraphics();
		else {
			throw new TodoException("Canvas is neither a MMG2DCanvas, nor a MM3DCanvas!");
		}
		FontMetrics metrics = m_canvas.getFontMetrics(m_canvas.getFont());
		int stringWidth = metrics.stringWidth(m_message);
		int stringAscent = metrics.getAscent();
		int stringDescent = metrics.getDescent();
		int x = 0, y = 0;
		if (getPosition() == TOP_LEFT) {
			x = 10;
			y = 20 * lineNr;
		} else if (getPosition() == CENTERED) {
			x = (m_canvas.getDrawingBoard().getWidth() / 2) - (stringWidth / 2);
			y = (m_canvas.getDrawingBoard().getHeight() / 2) - (stringAscent / 2);
		} else if (getPosition() == AT_MOUSE_CURSOR) {
			x = m_mouseX;
			y = m_mouseY;
		}
		if (getMessageType() != PLAIN_MESSAGE) {
			if (getMessageType() == ALERT_MESSAGE)
				g2.setColor(m_alertColor);
			else if (getMessageType() == INFO_MESSAGE)
				g2.setColor(m_infoColor);
			g2.fillRect(
				x - m_insets.left,
				y - stringAscent - m_insets.top,
				stringWidth + m_insets.right + m_insets.left,
				stringAscent + stringDescent + m_insets.bottom);
			g2.setColor(Color.BLACK);
			g2.drawRect(
				x - m_insets.left,
				y - stringAscent - m_insets.top,
				stringWidth + m_insets.right + m_insets.left,
				stringAscent + stringDescent + m_insets.bottom);
		}
		g2.drawString(m_message, x, y);
		if (m_duration > 0)
			new Timer(true).schedule(new MessageTimerTask(), m_duration);
	}

	public int getPosition() {
		return m_position;
	}

	public long getDuration() {
		return m_duration;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return m_message;
	}

	/**
	 * Sets the message text, <code>null</code> will make it invisible.
	 * @param message
	 *          The message to set.
	 */
	public void setMessage(String message) {
		m_message = message;
	}

	/**
	 * @param duration
	 *          The duration to set.
	 */
	public void setDuration(long duration) {
		m_duration = duration;
	}

	/**
	 * @param position
	 *          The position to set.
	 */
	public void setPosition(int position) {
		if (getPosition() == AT_MOUSE_CURSOR && position != AT_MOUSE_CURSOR)
			m_canvas.getDrawingBoard().removeMouseMotionListener(m_motionListener);
		m_position = position;
	}

	public void setCanvas(MMCanvas canvas) {
		m_canvas = canvas;
	}

	/**
	 * @return Returns the messageType.
	 */
	public int getMessageType() {
		return m_messageType;
	}

	/**
	 * @param messageType
	 *          The messageType to set.
	 */
	public void setMessageType(int messageType) {
		m_messageType = messageType;
	}

	/**
	 * @return Returns the canvas.
	 */
	public MMCanvas getCanvas() {
		return m_canvas;
	}

}
