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

package net.mumie.mathletfactory.appletskeleton.layout;

import javax.swing.JComponent;

import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This class is the base for all cards in the MathletFactory Cardz Framework.
 * It defines a content that will be displayed and allows to define "sub cards" that 
 * may be displayed above this card's content (layer). <br>
 * Technically the "real" content will be added as the first sub card and its visualisation 
 * will therefore exist equal among all other sub cards. If no own content is defined, only the 
 * sub cards will be displayed. <br>
 * Every card must define a type that may influence the card's title and postion/layer.
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public abstract class BasicCard {

	/** Constant for a demo card with predefined title. */
	public final static int DEMO_CARD = 0;

	/** Constant for a training card with predefined title. */
	public final static int TRAINING_CARD = 1;

	/** Constant for an exercise card with predefined title. */
	public final static int EXERCISE_CARD = 2;
	
	/** Constant for an user card with user defined title. */
	public final static int USER_CARD = -1;
	
	/* Constant indicating that the type has not been set yet. */
	private final static int NOT_SET = -2;
	
	/* Field containing the type. */
	private int m_type = NOT_SET;
	
	/* Field containing the title. */
	private String m_title = "";
	
	/* Field containing the layout support class. */
	private CardSupport m_support;
	
	/**
	 * Creates a card with no type set.
	 * @see #setType(int)
	 * @see #getType()
	 */
	public BasicCard() {
		m_support = new CardSupport(this);
	}

	/**
	 * Creates a card with the named type.
	 * @see #getType()
	 */
	public BasicCard(int type) {
		setType(type);
		m_support = new CardSupport(this);
	}
	
	/**
	 * Creates an user card (i.e. a card with type {@link #USER_CARD}) with the named title.
	 */
	public BasicCard(String title) {
		m_title = title;
		m_type = USER_CARD;
		m_support = new CardSupport(this);
	}
	
	/**
	 * Returns this card's type as an integer.
	 * Types different to {@link #USER_CARD} influences this card's title.
	 * 
	 * @see #DEMO_CARD
	 * @see #TRAINING_CARD
	 * @see #EXERCISE_CARD
	 * @see #USER_CARD
	 */
	public int getType() {
		return m_type;
	}
	
	/**
	 * Sets the type of this card.
	 * @see #getType()
	 */
	public void setType(int type) {
		m_type = type;
		switch(type) {
		case DEMO_CARD:
			m_title = ResourceManager.getMessage("demo");
			break;
		case TRAINING_CARD:
			m_title = ResourceManager.getMessage("training");
			break;
		case EXERCISE_CARD:
			m_title = ResourceManager.getMessage("exercise");
			break;
		case USER_CARD:
			// do nothing, title was already set
			break;
			default: 
				throw new IllegalArgumentException("Unknown card type used!");
		}
	}
	
	/**
	 * Adds a sub card to this card. 
	 * Note that this card's content is visualized as the first of its sub cards at creation time. 
	 */
	public void addSubCard(BasicCard card) {
		m_support.addSubCard(card);
	}
	
	/**
	 * Returns the top level component of this card i.e. a component containing all sub cards.
	 */
	public JComponent getTopLevelComponent() {
		return m_support.getContent();
	}
	
	/**
	 * Returns this card's content or <code>null</code> if this card has no explicit content.
	 */
	public abstract JComponent getContent();
	
	/**
	 * Initializes this card's objects.
	 */
	public void createObjects() {}
	
	/**
	 * Adds this card's objects to the visualisation.
	 */
	public void addObjects() {}
	
	/**
	 * Defines dependencies between this card's objects.
	 */
	public void defineDependencies() {}
	
	/**
	 * Resets this card and all of its objects.
	 */
	public void reset() {}
	
	/**
	 * Destroys (i.e. disposes of) this card and all of its objects.
	 */
	public void destroy() {}
	
	/**
	 * Selects an XML element with the named path from a datasheet.
	 * 
	 * Note:This method may not be called from inside all cards. Refer to the card's specification
	 * for more information.
	 */
	public void selectElement(String xmlPath) {}
	
	/**
	 * Collect all answers and stores them into a datasheet.
	 * 
	 * Note:This method may not be called from inside all cards. Refer to the card's specification
	 * for more information.
	 */
	public boolean collectAnswers() {
		return true;
	}
	
	/**
	 * Returns this card's title.
	 */
	public String getTitle() {
		return m_title;
	}
//	
//	public BasicCard getCard() {
//		return this;
//	}
}
