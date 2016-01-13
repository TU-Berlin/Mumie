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

/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author gronau
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class ShowUIDefaults extends JFrame implements ActionListener
{
	JFrame frame;
	JTabbedPane tabbedPane;
	SampleRenderer sampleRenderer;

	public ShowUIDefaults(String title)
	{
		super(title);
		frame = this;

		sampleRenderer = new SampleRenderer();

		getContentPane().setLayout( new BorderLayout() );
		tabbedPane = getTabbedPane();
		getContentPane().add( tabbedPane );

		JPanel buttons = new JPanel();
		buttons.setLayout( new GridLayout( 1, 3) );
		getContentPane().add(buttons, BorderLayout.SOUTH);

		JButton metal = new JButton( "Metal" );
		metal.setActionCommand( "javax.swing.plaf.metal.MetalLookAndFeel" );
		metal.addActionListener( this );
		buttons.add(metal);

		JButton windows = new JButton( "Windows" );
		windows.setActionCommand( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );
		windows.addActionListener( this );
		buttons.add(windows);

		JButton motif = new JButton( "Motif" );
		motif.setActionCommand( "com.sun.java.swing.plaf.motif.MotifLookAndFeel" );
		motif.addActionListener( this );
		buttons.add(motif);

	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
	   		UIManager.setLookAndFeel(e.getActionCommand());
		}
		catch (Exception e2)
		{
			System.out.println(e2);
		}

		getContentPane().remove(tabbedPane);
		tabbedPane = getTabbedPane();
		getContentPane().add( tabbedPane );
		SwingUtilities.updateComponentTreeUI(frame);
		frame.pack();
	}

	private JTabbedPane getTabbedPane()
	{
		Map components = new TreeMap();
		UIDefaults defaults = UIManager.getLookAndFeelDefaults();

		//  Build of Map of attributes for each component

		for ( Enumeration enum1 = defaults.keys(); enum1.hasMoreElements(); )
		{
			Object key = enum1.nextElement();
			Object value = defaults.get( key );

			Map componentMap = getComponentMap( components, key.toString() );

			if ( componentMap != null )
				componentMap.put( key, value );
		}

		JTabbedPane pane = new JTabbedPane( SwingConstants.BOTTOM );
		pane.setPreferredSize( new Dimension( 800, 400 ) );
		addComponentTabs( pane, components );

		return pane;
	}

	private Map getComponentMap( Map components, String key )
	{
		if ( key.startsWith("class") | key.startsWith("javax") )
			return null;

		//  Component name is found before the first "."

		String componentName;

		int pos = key.indexOf( "." );

		if (pos == -1)
			if (key.endsWith( "UI" ) )
				componentName = key.substring( 0, key.length() - 2 );
			else
				componentName = "System Colors";
		else
			componentName = key.substring( 0, pos );

		//  Fix inconsistency

		if (componentName.equals("Checkbox"))
			componentName = "CheckBox";

		//  Get the Map for this particular component

		Object componentMap = components.get( componentName );

		if (componentMap == null)
		{
			componentMap = new TreeMap();
			components.put( componentName, componentMap );
		}

		return (Map)componentMap;
	}

	private void addComponentTabs(JTabbedPane pane, Map components)
	{
		String[ ] colName = {"Key", "Value", "Sample"};
		Set c = components.keySet();

		for (Iterator ci = c.iterator(); ci.hasNext();)
		{
			String component = (String)ci.next();
			Map attributes = (Map)components.get( component );

			Object[ ][ ] rowData = new Object[ attributes.size() ][ 3 ];
			int i = 0;

			Set a = attributes.keySet();

			for(Iterator ai = a.iterator(); ai.hasNext(); i++)

			{
				String attribute = (String)ai.next();
				rowData[ i ] [ 0 ] = attribute;
				Object o = attributes.get(attribute);

				if (o != null)
				{
					rowData[ i ] [ 1 ] = o.toString();
					rowData[ i ] [ 2 ] = o;
				}
				else
				{
					rowData[ i ] [ 1 ] = "null";
					rowData[ i ] [ 2 ] = "";
				}

			}

			JTable table = new JTable(rowData, colName);
			table.getColumnModel().getColumn(2).setCellRenderer( sampleRenderer );
			table.getColumnModel().getColumn(0).setPreferredWidth(250);
			table.getColumnModel().getColumn(1).setPreferredWidth(500);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);

			pane.addTab( component, new JScrollPane( table ) );
		}
	}

	class SampleRenderer extends JLabel implements TableCellRenderer
	{
		public SampleRenderer()
		{
			super();
			setHorizontalAlignment( SwingConstants.CENTER );
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(JTable table,
														 Object sample,
														 boolean isSelected,
														 boolean hasFocus,
														 int row,
														 int column)
		{
			setBackground( null );
			setBorder( null );
			setIcon( null );
			setText( "" );

			if ( sample instanceof Color )
			{
				setBackground( (Color)sample );
			}
			else if ( sample instanceof Border )
			{
				setBorder( (Border)sample );
			}
			else if ( sample instanceof Font )
			{
				setText( "Sample" );
				setFont( (Font)sample );
			}
			else if ( sample instanceof Icon )
			{
				setIcon( (Icon)sample );
			}

			return this;
		}

		public void paint(Graphics g)
		{
			try
			{
				super.paint(g);
			}
			catch(Exception e)
			{
//				System.out.println(e);
//				System.out.println(e.getStackTrace()[0]);
			}
		}
	}

	public static void main(String[] args)
	{
		JFrame f = new ShowUIDefaults( "UI Defaults" );
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setLocationRelativeTo( null );
		f.setVisible(true);
	}

}
