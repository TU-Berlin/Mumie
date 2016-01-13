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

package net.mumie.mathletfactory.display.noc.set;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;

import javax.swing.plaf.PanelUI;

import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.display.layout.AlignablePanel;
import net.mumie.mathletfactory.display.layout.AlignerLayout;
import net.mumie.mathletfactory.display.noc.MMCompoundPanel;
import net.mumie.mathletfactory.display.noc.rel.MMRelationPanel;
import net.mumie.mathletfactory.display.noc.symbol.AttributedStringLabel;
import net.mumie.mathletfactory.display.noc.symbol.ParenSymbolLabel;
import net.mumie.mathletfactory.math.number.NumberFactory;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.mmobject.PropertyHandlerIF;
import net.mumie.mathletfactory.mmobject.algebra.MMRelation;
import net.mumie.mathletfactory.mmobject.set.MMSetDefByRel;
import net.mumie.mathletfactory.transformer.noc.SetDefByRelTransformer;

/** 
 *  This class renders the symbolical representation of a  
 *  {@link net.mumie.mathletfactory.mmobject.set.MMSetDefByRel}.
 * 
 *  @author Paehler, Gronau
 *  @mm.docstatus finished
 */

public class MMSetPanel extends MMCompoundPanel {

  private MMRelationPanel m_relationPanel;
  private ParenSymbolLabel[] m_parens = new ParenSymbolLabel[3];
    
  public MMSetPanel(MMObjectIF master, SetDefByRelTransformer transformer) {
    super(master, transformer, new AlignablePanel());
    // all components (also symbols) with max height
//    ((AlignablePanel) getViewerComponent()).setFillHeight(true);
    setRelation((MMRelation) getRealMaster().getRelation());
  }

  /**
   * This method should not be called by an application. Instead
   * {@link net.mumie.mathletfactory.mmobject.set.MMSetDefinedByRelation#setRelation}
   * should be called, after which this method is called internally by the 
   * transformer.
   */
  public void setRelation(MMRelation relation) {
  	getViewerComponent().removeAll(); 
    m_relationPanel = (MMRelationPanel)relation.getAsContainerContent();
    m_relationPanel.setFont(getFont());
    
    m_relationPanel.addPropertyChangeListener((PropertyHandlerIF) relation);
    m_relationPanel.addPropertyChangeListener(getMaster());
    m_parens[0] = new ParenSymbolLabel("{", getFont());
    m_parens[1] = new ParenSymbolLabel("|", getFont());
    m_parens[2] = new ParenSymbolLabel("}", getFont());
    String[] usedVariables = getRealMaster().getUsedVariablesArray();
    AttributedStringLabel setLabel = new AttributedStringLabel("");
    if (usedVariables.length > 1)
      setLabel.addSubstring("(");
    for(int i=0;i<usedVariables.length;i++){
      setLabel.addSubstring(usedVariables[i], TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
      if(i+1 < usedVariables.length)
        setLabel.addSubstring(" , ");
    }
    if (usedVariables.length > 1)
      setLabel.addSubstring(")");
    setLabel.addSubstring(" \u2208 ");

    String powerString = "";
    if(usedVariables.length == 1)
      powerString = " ";
    else
      powerString= Integer.toString(usedVariables.length)+" ";
      
    AttributedStringLabel setDomain =
      new AttributedStringLabel(NumberFactory.newInstance(relation.getNumberClass()).getDomainString());
    setDomain.addSubstring(powerString, TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER );
    
    if(relation.isAll()){
    	getViewerComponent().add(setDomain);
      revalidate();
      repaint(); 
      return;
    }
    if(relation.isNull()){
      AttributedStringLabel emptySet = new AttributedStringLabel(" \u00d8 "); 
      emptySet.getFont().deriveFont(Font.BOLD, getFont().getSize() * 1.2f);
      getViewerComponent().add(emptySet);
      revalidate();
      repaint(); 
      return;
    }
    setLabel.setForeground(getForeground());
    m_parens[0].setForeground(getForeground());
    setDomain.setForeground(getForeground());
    m_parens[1].setForeground(getForeground());
    m_relationPanel.setForeground(getForeground());
    m_parens[2].setForeground(getForeground());
    
    getViewerComponent().add(m_parens[0], AlignerLayout.MAXIMUM_HEIGHT);
    getViewerComponent().add(setLabel);
    getViewerComponent().add(setDomain);
    getViewerComponent().add(m_parens[1], AlignerLayout.MAXIMUM_HEIGHT);
    getViewerComponent().add(m_relationPanel);
    getViewerComponent().add(m_parens[2], AlignerLayout.MAXIMUM_HEIGHT);
    revalidate();
    repaint();
  }

  /** 
   * Sets the font for the function expression to be rendered. This method 
   * is invoked by the transformer of an MMObject and should not be called
   * by an application programmer, since the font will be overwritten by the
   * font property of the master. 
   */
  public void setFont(Font font) {
    super.setFont(font);
    if (m_relationPanel != null)
      m_relationPanel.setFont(font);
  }

  /**
   * Sets the color of the font.
   */
  public void setForeground(Color color){
    if(!color.equals(getForeground()) && getRealMaster() != null){
      super.setForeground(color);
      setRelation((MMRelation) getRealMaster().getRelation());
    } else
      super.setForeground(color);
    
  }

  /**
   * This method should not be called by an application. Instead
   * {@link net.mumie.mathletfactory.mmobject.set.MMSetDefinedByRelation#setEditable}
   * should be called, after which this method is called internally by the 
   * transformer (and by this any former value will be overwritten).
   */
  public void setEditable(boolean editable) {
  	super.setEditable(editable);
    m_relationPanel.setEditable(editable);
  }

  private MMSetDefByRel getRealMaster() {
    return (MMSetDefByRel) getMaster();
  }
  
  /**
   * Additionally sets the theme property "OperationPanel.font".
   */
  public void setUI(PanelUI ui) {
    super.setUI(ui);
    setFont((Font)MumieTheme.getThemeProperty("OperationPanel.font"));
  }

}
