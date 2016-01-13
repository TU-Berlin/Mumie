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

package net.mumie.font.extra;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import net.mumie.font.Font;
import net.mumie.font.FontLoader;
import net.mumie.font.Glyph;
import net.mumie.util.gui.GridBagStyleUtil;
import java.awt.Color;

public class GlyphBrowser extends JFrame
{
  // --------------------------------------------------------------------------------
  // h1: Inner class: Canvas to show glyph
  // --------------------------------------------------------------------------------

  /**
   * A special canvas to display the selected glyph.
   */

  protected class GlyphCanvas extends Canvas
  {
    /**
     * The shape of the glyph as a {@link Path2D.Double Path2D.Double} object.
     */

    protected Path2D.Double path;

    /**
     * The x coordinate of the glyph on the canvas.
     */

    protected double x = 100.0;

    /**
     * The y coordinate of the glyph on the canvas.
     */

    protected double y= 200.0;

    /**
     * Sets the glyph to display.
     */

    protected void setGlyph (Glyph glyph)
    {
      if ( glyph != null )
        glyph.getOutline().toPath(this.path, this.x, this.y);
      this.repaint();
    }

    /**
     * Sets the position on the canvas where the glyph appears.
     */

    public void setPos (double x, double y)
    {
      this.x = x;
      this.y = y;
    }

    /**
     * Paints the canvas.
     */

    public void paint (Graphics graphics)
    {
      try
        {
          Graphics2D g2d = (Graphics2D)graphics;
          g2d.setRenderingHint
            (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          if ( this.path != null ) g2d.fill(this.path);
        }
      catch (Exception exception)
        {
          throw new RuntimeException(exception);
        }
    }
  }

  // --------------------------------------------------------------------------------
  // h1: Global variables and constants
  // --------------------------------------------------------------------------------

  /**
   * The font loader.
   */

  protected FontLoader fontLoader = new FontLoader();

  /**
   * The selected font
   */

  protected Font font = null;

  /**
   * Display of the font id.
   */

  protected JLabel fontIdDisplay;

  /**
   * Button to popup the font info window
   */

  protected JButton fontInfoButton;

  /**
   * Button to change the font
   */

  protected JButton fontChangeButton;

  /**
   * Selection list for glyphs.
   */

  protected JList glyphList;

  /**
   * Display of the glyph name
   */

  protected JLabel glyphNameDisplay;

  /**
   * Display of the unicode value of the glyph:
   */

  protected JLabel glyphUnicodeDisplay;

  /**
   * Display of the glyph width
   */

  protected JLabel glyphWidthDisplay;

  /**
   * Display of the glyph height
   */

  protected JLabel glyphHeightDisplay;

  /**
   * Display of the glyph advance width
   */

  protected JLabel glyphAdvanceWidthDisplay;

  /**
   * Display of the glyph's x value
   */

  protected JLabel glyphXDisplay;

  /**
   * Display of the glyph's y value
   */

  protected JLabel glyphYDisplay;

  /**
   * Display of the glyph itself.
   */

  protected GlyphCanvas glyphCanvas;

  /**
   * Auxiliary to arrange the components.
   */

  protected GridBagConstraints style = new GridBagConstraints();

  // --------------------------------------------------------------------------------
  // h1: Selecting glyphs
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected void setGlyph (Glyph glyph)
  {
    this.glyphNameDisplay.setText(glyph.getName());
    this.glyphUnicodeDisplay.setText(glyph.getUnicode());
    this.glyphXDisplay.setText(Double.toString(glyph.getX()));
    this.glyphYDisplay.setText(Double.toString(glyph.getY()));
    this.glyphWidthDisplay.setText(Double.toString(glyph.getWidth()));
    this.glyphHeightDisplay.setText(Double.toString(glyph.getHeight()));
    this.glyphAdvanceWidthDisplay.setText(Double.toString(glyph.getAdvanceWidth()));
    this.glyphCanvas.setGlyph(glyph);
  }

  // --------------------------------------------------------------------------------
  // h1: Loading fonts
  // --------------------------------------------------------------------------------

  /**
   * Loads a (new) font from the specified file.
   */

  protected void setFont (File fontFile)
  {
    try
      {
        this.font = this.fontLoader.loadXMLFont(fontFile);
        this.fontIdDisplay.setText(this.font.getId());
        this.fontInfoButton.setEnabled(true);
        String[] glyphNames = this.font.getGlyphNames();
        Arrays.sort(glyphNames);
        this.glyphList.setListData(glyphNames);
        this.setGlyph(this.font.getGlyphForName(glyphNames[0]));
      }
    catch (Exception exception)
      {
        System.err.println(exception);
      }
  }

  // --------------------------------------------------------------------------------
  // h1: GUI
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected void createGUI ()
  {
    // Font id display:
    JLabel fontIdLabel = new JLabel("Font:");
    this.fontIdDisplay = new JLabel();

    // Font info button:
    this.fontInfoButton = new JButton("Font info");
    this.fontInfoButton.setEnabled(false);

    // Font change button:
    this.fontChangeButton = new JButton("Change font");
    this.fontChangeButton.addActionListener
      (new ActionListener ()
        {
          protected JFileChooser fileChooser = new JFileChooser();
          public void actionPerformed (ActionEvent event)
          {
            final GlyphBrowser parent = GlyphBrowser.this;
            if ( this.fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION )
              parent.setFont(this.fileChooser.getSelectedFile());
          }
        }
      );

    // Glyph selection list:
    this.glyphList = new JList();
    this.glyphList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // this.glyphList.setVisibleRowCount(20);
    JScrollPane glyphListScrollPane = new JScrollPane(this.glyphList);
    // glyphListScrollPane.setPreferredSize(new Dimension(150, 300));

    // Glyph name display:
    JLabel glyphNameLabel = new JLabel("Name:");
    this.glyphNameDisplay = new JLabel();

    // Glyph unicode display:
    JLabel glyphUnicodeLabel = new JLabel("Unicode:");
    this.glyphUnicodeDisplay = new JLabel();

    // Glyph width display:
    JLabel glyphWidthLabel = new JLabel("Width:");
    this.glyphWidthDisplay = new JLabel();

    // Glyph height display:
    JLabel glyphHeightLabel = new JLabel("Height:");
    this.glyphHeightDisplay = new JLabel();

    // Glyph advance width display:
    JLabel glyphAdvanceWidthLabel = new JLabel("Adv. width:");
    this.glyphAdvanceWidthDisplay = new JLabel();

    // Glyph x value display:
    JLabel glyphXLabel = new JLabel("X:");
    this.glyphXDisplay = new JLabel();

    // Glyph y value display:
    JLabel glyphYLabel = new JLabel("Y:");
    this.glyphYDisplay = new JLabel();

    // Glyph canvas:
    this.glyphCanvas = new GlyphCanvas();
    this.glyphCanvas.setBackground(Color.WHITE);
    this.glyphCanvas.setSize(300, 300);

    // Container for font controls:
    JPanel fontPanel = new JPanel();

    // Container for glyph infos:
    JPanel glyphInfoPanel = new JPanel();

    // Set layout managers:
    this.setLayout(new GridBagLayout());
    fontPanel.setLayout(new GridBagLayout());
    glyphInfoPanel.setLayout(new GridBagLayout());

    // Fill font panel:
    fontPanel.add(fontIdLabel, this.style("gridxy=0,0"));
    fontPanel.add(fontIdDisplay, this.style("gridxy=1,0"));
    fontPanel.add(fontInfoButton, this.style("gridxy=2,0"));
    fontPanel.add(fontChangeButton, this.style("gridxy=3,0"));

    // Fill glyph info panel:
    glyphInfoPanel.add
      (glyphNameLabel, this.style("gridxy=0,0"));
    glyphInfoPanel.add
      (glyphNameDisplay, this.style("gridxy=1,0"));

    glyphInfoPanel.add
      (glyphUnicodeLabel, this.style("gridxy=0,1"));
    glyphInfoPanel.add
      (glyphUnicodeDisplay, this.style("gridxy=1,1"));

    glyphInfoPanel.add
      (glyphWidthLabel, this.style("gridxy=0,2"));
    glyphInfoPanel.add
      (glyphWidthDisplay, this.style("gridxy=1,2"));

    glyphInfoPanel.add
      (glyphHeightLabel, this.style("gridxy=0,3"));
    glyphInfoPanel.add
      (glyphHeightDisplay, this.style("gridxy=1,3"));

    glyphInfoPanel.add
      (glyphAdvanceWidthLabel, this.style("gridxy=0,4"));
    glyphInfoPanel.add
      (glyphAdvanceWidthDisplay, this.style("gridxy=1,4"));

    glyphInfoPanel.add
      (glyphXLabel, this.style("gridxy=0,5"));
    glyphInfoPanel.add
      (glyphXDisplay, this.style("gridxy=1,5"));

    glyphInfoPanel.add
      (glyphYLabel, this.style("gridxy=0,6"));
    glyphInfoPanel.add
      (glyphYDisplay, this.style("gridxy=1,6"));

    // Add containers to root frame:
    this.add(fontPanel, this.style("gridxy=0,0", "gridwidth=3"));
    this.add(glyphListScrollPane, this.style("gridxy=0,1"));
    this.add(glyphInfoPanel, this.style("gridxy=1,1"));
    this.add(glyphCanvas, this.style("gridxy=2,1"));
  }

  // --------------------------------------------------------------------------------
  // h1: Constructor
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  public GlyphBrowser (File fontFile)
  {
    this.createGUI();
    if ( fontFile != null ) this.setFont(fontFile);
    this.pack();
    this.setVisible(true);
  }

  // --------------------------------------------------------------------------------
  // h1: Main method
  // --------------------------------------------------------------------------------

  /**
   * Main method to run the glyph viewer as a standalone appllication.
   */

  public static void main (String[] params)
  {
    SwingUtilities.invokeLater
      (new Runnable ()
        {
          public void run()
          {
            new GlyphBrowser(null);
          }
        });
  }

  // --------------------------------------------------------------------------------
  // h1: Auxiliaries
  // --------------------------------------------------------------------------------

  /**
   * 
   */

  protected GridBagConstraints style (String... expressions)
  {
    GridBagStyleUtil.reset(this.style);
    GridBagStyleUtil.add(this.style, expressions);
    return this.style;
  }
}

