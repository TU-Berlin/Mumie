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

package net.mumie.mathletfactory.display.util.tex;

import java.awt.Color;
import java.awt.Font;

import net.mumie.mathletfactory.display.util.ViewIF;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.text.HTMLConverter;
import net.mumie.mathletfactory.util.text.TeXConverter;

public class TeXView {

	public final static char COMMAND_PREFIX = '\\';
	
	public final static char SUPER_SCRIPT_COMMAND = '^';

	public final static char SUB_SCRIPT_COMMAND = '_';
	
	/** Static view for all attribute commands. */
	private final static AttributeView ATTRIBUTE_VIEW = new AttributeView();
	
	/** Attributes for the <code>textbf</code> command. */
	private final static PropertyMapIF TEXTBF_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>textit</code> command. */
	private final static PropertyMapIF TEXTIT_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>tiny</code> command. */
	private final static PropertyMapIF TINY_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>small</code> command. */
	private final static PropertyMapIF SMALL_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>normalsize</code> command. */
	private final static PropertyMapIF NORMALSIZE_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>large</code> command. */
	private final static PropertyMapIF LARGE_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>Large</code> command. */
	private final static PropertyMapIF LLARGE_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>LARGE</code> command. */
	private final static PropertyMapIF LLLARGE_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for the <code>huge</code> command. */
	private final static PropertyMapIF HUGE_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Attributes for sub and super script commands. */
	private final static PropertyMapIF SUPERSCRIPT_ATTRIBUTES = new DefaultPropertyMap();
	
	/** Static argument for all default commands. */
	final static TexArgument COMMAND_ARGUMENT = new TexArgument();
	
	/** Static argument for all textcolor commands. */
	private final static TexArgument TEXTCOLOR_ARGUMENT = new TexArgument(ViewIF.FOREGROUND_ATTRIBUTE);
	
	// initialize default attributes
	static {
		SUPERSCRIPT_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_CHANGE_ATTRIBUTE, new Integer(-3));

		TEXTBF_ATTRIBUTES.setProperty(ViewIF.FONT_STYLE_ATTRIBUTE, new Integer(Font.BOLD));
		TEXTIT_ATTRIBUTES.setProperty(ViewIF.FONT_STYLE_ATTRIBUTE, new Integer(Font.ITALIC));
		
		TINY_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(8));
		SMALL_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(10));
		NORMALSIZE_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(12));
		LARGE_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(14));
		LLARGE_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(18));
		LLLARGE_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(20));
		HUGE_ATTRIBUTES.setProperty(ViewIF.FONT_SIZE_ATTRIBUTE, new Integer(24));
	}

	/** Array holding all registered TeX commands along with their pre-defined attributes. */
	private final static TexCommand[] COMMANDS = {
			new TexCommand("vec", 1, null, new TopCharView(TopCharView.VECTOR_TYPE)),
			new TexCommand("dot", 1, null, new TopCharView(TopCharView.DOT_TYPE)),
			new TexCommand("ddot", 1, null, new TopCharView(TopCharView.DDOT_TYPE)),
			new TexCommand("bar", 1, null, new TopBarView()),
			new TexCommand("tilde", 1, null, new TopCharView(TopCharView.TILDE_TYPE)),
			
			new TexCommand("norm", 1, null, new SideCharView(SideCharView.ABS_TYPE)),
			new TexCommand("det", 1, null, new SideCharView(SideCharView.DET_TYPE)),
			new TexCommand("abs", 1, null, new SideCharView(SideCharView.ABS_TYPE)),
			
			new TexCommand("frac", 2, null, new FractionView()),
			
			new TexCommand("textbf", 1, new PropertyMapIF[] {TEXTBF_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("textit", 1, new PropertyMapIF[] {TEXTIT_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("tiny", 1, new PropertyMapIF[] {TINY_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("small", 1, new PropertyMapIF[] {SMALL_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("normalsize", 1, new PropertyMapIF[] {NORMALSIZE_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("large", 1, new PropertyMapIF[] {LARGE_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("Large", 1, new PropertyMapIF[] {LLARGE_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("LARGE", 1, new PropertyMapIF[] {LLLARGE_ATTRIBUTES}, ATTRIBUTE_VIEW),
			new TexCommand("huge", 1, new PropertyMapIF[] {HUGE_ATTRIBUTES}, ATTRIBUTE_VIEW), 
			
			new TexCommand("textcolor", new TexArgument[] {TEXTCOLOR_ARGUMENT, COMMAND_ARGUMENT}, ATTRIBUTE_VIEW)
	};
		
	private final static PropertyMapIF ATTRIBUTES = new DefaultPropertyMap();
	
	static {
		ATTRIBUTES.setProperty("WHITE", Color.WHITE);
		ATTRIBUTES.setProperty("GRAY", Color.GRAY);
		ATTRIBUTES.setProperty("LIGHT_GRAY", Color.LIGHT_GRAY);
		ATTRIBUTES.setProperty("DARK_GRAY", Color.DARK_GRAY);
		ATTRIBUTES.setProperty("BLACK", Color.BLACK);
		ATTRIBUTES.setProperty("RED", Color.RED);
		ATTRIBUTES.setProperty("LIGHT_RED", Color.RED.brighter());
		ATTRIBUTES.setProperty("DARK_RED", Color.RED.darker());
		ATTRIBUTES.setProperty("PINK", Color.PINK);
		ATTRIBUTES.setProperty("LIGHT_PINK", Color.PINK.brighter());
		ATTRIBUTES.setProperty("DARK_PINK", Color.PINK.darker());
		ATTRIBUTES.setProperty("ORANGE", Color.ORANGE);
		ATTRIBUTES.setProperty("LIGHT_ORANGE", Color.ORANGE.brighter());
		ATTRIBUTES.setProperty("DARK_ORANGE", Color.ORANGE.darker());
		ATTRIBUTES.setProperty("YELLOW", Color.YELLOW);
		ATTRIBUTES.setProperty("LIGHT_YELLOW", Color.YELLOW.brighter());
		ATTRIBUTES.setProperty("DARK_YELLOW", Color.YELLOW.darker());
		ATTRIBUTES.setProperty("GREEN", Color.GREEN);
		ATTRIBUTES.setProperty("LIGHT_GREEN", Color.GREEN.brighter());
		ATTRIBUTES.setProperty("DARK_GREEN", Color.GREEN.darker().darker());
		ATTRIBUTES.setProperty("MAGENTA", Color.MAGENTA);
		ATTRIBUTES.setProperty("LIGHT_MAGENTA", Color.MAGENTA.brighter());
		ATTRIBUTES.setProperty("DARK_MAGENTA", Color.MAGENTA.darker());
		ATTRIBUTES.setProperty("CYAN", Color.CYAN);
		ATTRIBUTES.setProperty("LIGHT_CYAN", Color.CYAN.brighter());
		ATTRIBUTES.setProperty("DARK_CYAN", Color.CYAN.darker());
		ATTRIBUTES.setProperty("BLUE", Color.BLUE);
		ATTRIBUTES.setProperty("LIGHT_BLUE", Color.BLUE.brighter());
		ATTRIBUTES.setProperty("DARK_BLUE", Color.BLUE.darker());
	}
	
	private final static TeXView m_instance = new TeXView();
	
	private TeXView() {}
	
	/**
	 * Returns an appropriate {@link ViewIF} instance for the given TeX expression that can be used
	 * to render arbitrary TeX content with default attributes.
	 * 
	 * @param texContent a TeX expression
	 */
	public static ViewIF createTeXView(String texContent) {
		return createTeXView(texContent, null);
	}
	
	/**
	 * Returns an appropriate {@link ViewIF} instance for the given TeX expression that can be used
	 * to render arbitrary TeX content with the given attributes.
	 * 
	 * @param texContent a TeX expression
	 */
	public static ViewIF createTeXView(String texContent, PropertyMapIF attributes) {
		// handle empty texts explicitly
		if(texContent.trim().length() == 0)
			return new EmptyView();
		// preparse special characters and line breaks
		String[] preparsed = m_instance.preParseContent(texContent);
		for(int i = 0; i < preparsed.length; i++)
			log("Preparsed " + i + "=" + preparsed[i]);
		// add single row
		if(preparsed.length == 1) {
			ViewIF view = m_instance.getView(preparsed[0], attributes);
			// ensure that the root is always a row view (needed for line breaks)
			if((view instanceof RowView) == false) {
				RowView rv = new RowView();
				rv.addChild(view);
				return rv;
			} else
				return view;
		}
		// add multiple rows
		else {
			RowView rv = new RowView();
			for(int p = 0; p < preparsed.length; p++) {
				String text = preparsed[p].trim();
				ViewIF view = m_instance.getView(text, attributes);
				rv.addChild(view);
				if(p < preparsed.length - 1)
					rv.addNewRow();
			}
			return rv;
		}
	}
	
	protected String[] preParseContent(String text) {
		// remove white spaces and line breaks, replace special characters expressed in TeX and HTML
		String preparsed = new String(HTMLConverter.toUnicode(TeXConverter.toUnicode(TeXConverter.trim(text))));
		// split string around line breaks ("\\")
		return preparsed.split("\\\\\\\\");
	}
	
	private ViewIF getView(String texContent, PropertyMapIF attributes) {
		// handle empty texts explicitly
		if(texContent.trim().length() == 0)
			return new EmptyView();
		ParsePosition p = new ParsePosition(texContent, attributes);
		while(p.lastIndex <= texContent.length() - 1) {
			// get index of first command
			int cBegin = getNextCommandIndex(texContent, p.lastIndex);
			// simple text before 1st command? 
			if(cBegin > p.lastIndex) {
				p = parseText(p, cBegin);
				p = parseScript(p);
				// finsish current view
				p.view = addView(p.view, p.currentView);
				p.currentView = null;
				// get 1st command in next loop
				continue;
			}
			// no more commands/only text remaining?
			if(cBegin == -1) {
				p = parseText(p, -1);
				p.view = addView(p.view, p.currentView);
				p.currentView = null;
				// finish loop after this last text/content
				break;
			}
			// set pointer to command begin index
			p.lastIndex = cBegin;
			// parse real command
			p = parseCommand(p);
			p = parseScript(p);
			p.view = addView(p.view, p.currentView);
			p.currentView = null;
		}
		// remove attributes for this view/prepare for next view
		p.attributes.clear();
		// handle empty attributes explicitly
		if(p.view == null)
			p.view = new EmptyView();
		return p.view;
	}
	
	protected ParsePosition parseCommand(ParsePosition p) {
		int openingIndex = getOpeningBracketIndex(p.texContent, p.lastIndex+1);
		if(openingIndex == -1)
			throw new IllegalArgumentException("Missing opening bracket '{' after command \"" + p.texContent.substring(p.lastIndex) + "\" !");
		int closingIndex = getClosingBracketIndex(p.texContent, openingIndex+1);
		if(closingIndex == -1)
			throw new IllegalArgumentException("Missing closing bracket '}' after command \"" + p.texContent.substring(p.lastIndex) + "\" !");
		String commandName = p.texContent.substring(p.lastIndex+1, openingIndex).trim();
		log("Parsing command \""+ commandName + "\"");
		// search for command name in registered commands
		TexCommand command = null;
		for(int i = 0; i < COMMANDS.length; i++) {
			if(commandName.equals(COMMANDS[i].getCommandName())) {
				command = COMMANDS[i];
				break;
			}
		}
		if(command == null)
			throw new IllegalArgumentException("Command unknown: \"" + commandName + "\"");
		// create new view instance from command
		ViewIF commandView = command.createView();
    // copy parent attributes to new view
    commandView.setAttributes(p.attributes);
		// copy parent attributes to new map
    PropertyMapIF commandAttributes = p.attributes.copy();
    // copy original attributes to new map
    PropertyMapIF orgAttributes = p.attributes.copy();
		// parse arguments
		for(int a = 1; a <= command.getArgumentCount(); a++) {
			String substring = p.texContent.substring(openingIndex+1, closingIndex);
			log("Parsing argument #" + a + ": \""+ substring + "\"");
			TexArgument arg = command.getArgument(a-1);
			if(arg.isCommandArgument()) {
				// create new attribute map for view
				PropertyMapIF map = new DefaultPropertyMap();
				// copy pre-defined attributes to new map
				if(arg.getAttributes() != null)
					map.copyPropertiesFrom(arg.getAttributes());
				// copy command attributes to new map
        if(!commandAttributes.isEmpty())
          map.copyPropertiesFrom(commandAttributes);
				// create view of content between brackets
				ViewIF subCommands = getView(substring, map);
				// add child view to command view (recursive)
				commandView.addChild(subCommands);
			} else { // attribute argument
        commandAttributes.copyPropertiesFrom(parseAttribute(p, arg, substring));
			}
			// more arguments to parse? --> prepare for next loop
			if(command.getArgumentCount() > 1 && a < command.getArgumentCount()) { // parse next argument
				// get indices of next argument's brackets
				openingIndex = getOpeningBracketIndex(p.texContent, closingIndex+1);
				if(openingIndex == -1)
					throw new IllegalArgumentException("Missing opening bracket for argument #" + a + " '{' after command \"" + command.getCommandName() + "\" !");
				closingIndex = getClosingBracketIndex(p.texContent, openingIndex+1);
				if(closingIndex == -1)
					throw new IllegalArgumentException("Missing closing bracket for argument #" + a + " '}' after command \"" + command.getCommandName() + "\" !");
			}
		} // end of argument parsing
		// add command view
		if(commandView instanceof AttributeView)
			p.currentView = commandView.getChild(0);
		else
			p.currentView = commandView;
		// prepare for next command
		p.lastIndex = closingIndex + 1;
    // restore original attributes
    p.attributes = orgAttributes;
		return p;
	}
	
	protected ParsePosition parseText(ParsePosition p, int end) {
		if(end == -1)
			end = p.texContent.length();
		String text = p.texContent.substring(p.lastIndex, end);
		log("Parsing text \"" + text + "\"");
		ViewIF textView = new TextView(text);
		textView.setAttributes(p.attributes);
		p.currentView = textView;
		// move pointer
		p.lastIndex += text.length();
		return p;
	}
	
	protected PropertyMapIF parseAttribute(ParsePosition p, TexArgument arg, String text) {
		log("Parsing attribute \"" + text + "\"");
		Object property = ATTRIBUTES.getProperty(text);
		if(property == null)
			throw new IllegalArgumentException("Attribute unknown: \"" + text + "\"");
    PropertyMapIF map = new DefaultPropertyMap();
		map.setProperty(arg.getAttributeName(), property);
    return map;
	}
	
	protected ParsePosition parseScript(ParsePosition p) {
		int subIndex = getNextCharIndex(p.texContent, SUB_SCRIPT_COMMAND, p.lastIndex, -1);
		if(subIndex > -1) // sub script is at first
			p = parseScript(p, SUB_SCRIPT_COMMAND);
		int supIndex = getNextCharIndex(p.texContent, SUPER_SCRIPT_COMMAND, p.lastIndex, -1);
		if(supIndex > -1) // super script is at first or at second
			p = parseScript(p, SUPER_SCRIPT_COMMAND);
		if(subIndex == -1) { // sub script was not at first
			subIndex = getNextCharIndex(p.texContent, SUB_SCRIPT_COMMAND, p.lastIndex, -1);
			if(subIndex > -1) // sub script at last
				p = parseScript(p, SUB_SCRIPT_COMMAND);
		}
		return p;
	}
	
	protected ParsePosition parseScript(ParsePosition p, char scriptChar) {
		int subIndex = getNextCharIndex(p.texContent, scriptChar, p.lastIndex, -1);
		if(subIndex == -1)
			return p;
		int subBegin = subIndex + 1;
		int subEnd;
		String scriptString;
		char subBeginChar = p.texContent.charAt(subBegin);
		if(subBeginChar == '{') {
			subBegin++; // move pointer behind bracket
			subEnd = getClosingBracketIndex(p.texContent, subBegin);
			scriptString = p.texContent.substring(subBegin, subEnd);
		} else if(Character.isLetterOrDigit(subBeginChar)) { // char is a single sub script
			subEnd = subBegin;
			scriptString = Character.toString(subBeginChar);
		} else
			throw new IllegalArgumentException("A sub/super script must be a single character or included in brackets !");
		if(!(p.currentView instanceof SuperscriptView)) {
			ViewIF superScriptView = new SuperscriptView();
			superScriptView.addChild(p.currentView);
			p.currentView = superScriptView;
		}
		if(scriptChar == SUPER_SCRIPT_COMMAND) {
			log("Parsing super-script: \""+ scriptString + "\"");
			((SuperscriptView) p.currentView).setSuperScript(true);
		} else {
			log("Parsing sub-script: \""+ scriptString + "\"");
			((SuperscriptView) p.currentView).setSubScript(true);
		}
		// create new attribute map for view
		PropertyMapIF map = new DefaultPropertyMap();
		// copy pre-defined attributes to new map
		map.copyPropertiesFrom(SUPERSCRIPT_ATTRIBUTES);
		// copy parent attributes to new map
		if(!p.attributes.isEmpty())
			map.copyPropertiesFrom(p.attributes);
		p.currentView.addChild(getView(scriptString, map)).setAttributes(map);

		p.lastIndex = subEnd + 1;
		return p;
	}
	
	private static void log(String message) {
//		System.out.println(message);
	}
	
	/**
	 * Adds the given child view to the given parent view and returns the parent view.
	 * 
	 * If the parent view is <code>null</code>, the child view will be returned.
	 * If the parent is not an instance of {@link RowView}, an instance of {@link RowView}
	 * will be created and both the parent and child views will be added to.
	 * If the parent is an instance of {@link RowView}, the child view will be added to.
	 * 
	 * @param parent the parent view, may be <code>null</code>
	 * @param child the child view, must not be <code>null</code>
	 * @return a view containing both the parent and child views
	 */
	private ViewIF addView(ViewIF parent, ViewIF child) {
		if(parent == null)
			parent = child;
		else if(parent instanceof RowView)
			parent.addChild(child);
		else {
			ViewIF rowView = new RowView();
			rowView.addChild(parent);
			rowView.addChild(child);
			parent = rowView;
		}
		return parent;
	}
			
	private int getNextCharIndex(String s, char searchChar, int begin, int end) {
		if(end == -1)
			end = s.length() - 1;
		for(int i = begin; i <= end; i++) {
			char c = s.charAt(i);
			if(c == searchChar)
				return i;
			if(c != ' ')
				break;
		}
		return -1;
	}
	
	private int getNextCommandIndex(String s, int begin) {
		for(int i = begin; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == COMMAND_PREFIX || c == SUB_SCRIPT_COMMAND || c == SUPER_SCRIPT_COMMAND)
				return i;
		}
		return -1;
	}
	
	private int getOpeningBracketIndex(String s, int begin) {
		for(int i = begin; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '{')
				return i;
		}
		return -1;
	}
	
	private int getClosingBracketIndex(String s, int begin) {
		int m_openBrackets = 0;
		for(int i = begin; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '{')
				m_openBrackets++;
			else if(c == '}') {
				if(m_openBrackets == 0)
					return i;
				m_openBrackets--;
			}
		}
		return -1;
	}
		
	public static void printViewTree(ViewIF view) {
		printViewTreeNode(view, 0);
	}
	
	private static void printViewTreeNode(ViewIF view, int level) {
		if(level > 0) {
			for(int i = 1; i <= level; i++)
				System.out.print("      ");
			System.out.print("|--");
		}
		if(view instanceof TextView)
			System.out.println(view.getClass().getName() + "(\"" + ((TextView)view).getText() + "\")");
		else
			System.out.println(view.getClass().getName());
		for(int i = 0; i < view.getChildCount(); i++) {
			ViewIF childView = view.getChild(i);
			printViewTreeNode(childView, level + 1);
		}
	}
}
	class TexCommand {
		
		private String m_name;
		
		private TexArgument[] m_arguments;
		
		private ViewIF m_view;
		
		TexCommand(String shortName, int argumentCount, PropertyMapIF[] argumentsAttributes, ViewIF view) {
			m_name = shortName;
			m_arguments = new TexArgument[argumentCount];
			for(int i = 0; i < argumentCount; i++) {
				m_arguments[i] = new TexArgument();
				if(argumentsAttributes != null)
					m_arguments[i].setAttributes(argumentsAttributes[i]);
			}
			m_view = view;
		}
		
		TexCommand(String shortName, TexArgument[] arguments, ViewIF view) {
			m_name = shortName;
			m_arguments = arguments;
			m_view = view;
		}
		
		public ViewIF createView() {
			return m_view.copy();
		}
		
		public int getArgumentCount() {
			return m_arguments.length;
		}
		
		public TexArgument getArgument(int argument) {
			return m_arguments[argument];
		}
		
		public String getCommandName() {
			return m_name;
		}
		
		public String getCommandBegin() {
			return TeXView.COMMAND_PREFIX + m_name + "{";
		}
	}
	
	class TexArgument {
		private PropertyMapIF m_attributes;
		private final String m_attributeName;
		
		TexArgument() {
			this(null);
		}
		
		TexArgument(String attributeName) {
			m_attributeName = attributeName;
		}
		
		void setAttributes(PropertyMapIF attributes) {
			m_attributes = attributes;
		}
		
		public PropertyMapIF getAttributes() {
			return m_attributes;
		}
		
		public String getAttributeName() {
			return m_attributeName;
		}
		
		public boolean isAttributeArgument() {
			return m_attributeName != null;
		}
		
		public boolean isCommandArgument() {
			return m_attributeName == null;
		}
	}
	
	class ParsePosition {
		ViewIF view = null;
		int lastIndex = 0;
		String texContent;
		ViewIF currentView;
		PropertyMapIF attributes;
		
		ParsePosition(String texContent, PropertyMapIF attributes) {
			this.texContent = texContent;
			if(attributes != null)
				this.attributes = attributes;
			else
				this.attributes = new DefaultPropertyMap();
		}
	}


