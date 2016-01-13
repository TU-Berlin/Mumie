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

package net.mumie.mathletfactory.util.exercise;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AccessControlException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import net.mumie.mathletfactory.action.ActionManager;
import net.mumie.mathletfactory.math.algebra.linalg.NumberMatrix;
import net.mumie.mathletfactory.math.number.MInteger;
import net.mumie.mathletfactory.math.number.MNumber;
import net.mumie.mathletfactory.mmobject.util.MMImage;
import net.mumie.mathletfactory.util.Graphics2DUtils;
import net.mumie.mathletfactory.util.ResourceManager;

/**
 * This class provides controls to load and save images where the file location is choosen by the user.
 * Image operations (e.g. resizing, croping or converting to gray tones) will be performed after loading.
 * The class works for an image master (a {@link net.mumie.mathletfactory.mmobject.util.MMImage})
 * which will be updated on changes and holds internally the actual image content.
 * 
 * @see #getMasterImage()
 * @see #performOperations(BufferedImage)
 * 
 * @author gronau
 * @mm.docstatus finished
 */
public class ImageHelper {
	
	/** Field that contains an array with other <code>ImageHelper</code>s
	 * whos load and save paths(location, directory) schould be changed 
	 * to the last used one*/
	private ImageHelper[] m_otherHelpers;
	
	/** Field holding the master this helper is working for. */
	private MMImage m_masterImage;
	
	/** Field holding the image's name that will be used in dialogs and on buttons. */
	private String m_imageName;
	
	private JButton m_loadImageButton, m_loadMatrixButton, m_writeMatrixButton;
//	private File m_oldLoadFile, m_oldSaveFile;
	
	/** The width the image should have at the end. The value <code>-1</code> stands for the orginal image width. */ 
	private int m_width = -1;

	/** The height the image should have at the end. The value <code>-1</code> stands for the orginal image height. */ 
	private int m_height = -1;
	
	/** max width used for preventing {@link OutOfMemoryError} of Java heap space */
	private final static int MAX_WIDTH = 1024;
	
	/** max height used for preventing {@link OutOfMemoryError} of Java heap space */
	private final static int MAX_HEIGHT = 768;
	
	/** Flag indicating that the image should be turned to gray tones. */
	private boolean m_grayScale = false;
	
	/** File Filters needed in dialogs .*/
	private FileFilter m_matrixImageFileFilter, m_textFileFilter, m_imagesFileFilter;
	
	/** This field contais the path (Directory), which the JFileChooser in 
	 * {@link #loadImage()}, {@link #loadMatrix()} and {@link #saveMatrix()} starts with.*/
	private File m_currentDirectory;
	
	/**
	 * Constructs a new helper working for an internally created "master image" that will be updated after changes.
	 * The master can be accessed via {@link #getMasterImage()}.
	 * Loaded images will be resized and croped to fit 256x192 and then turned to gray tones.
	 */
	public ImageHelper() {
		this(null, null);
	}

	/**
	 * Constructs a new helper working for an internally created "master image" that will be updated after changes.
	 * The image name is used to propose a file name in the open and save dialog.
	 * The master can be accessed via {@link #getMasterImage()}.
	 * Loaded images will be resized and croped to fit 256x192 and then turned to gray tones.
	 * 
	 * @param imageName the name used for proposing a file name
	 */
	public ImageHelper(String imageName) {
		this(imageName, null);
	}
	
	/**
	 * Constructs a new helper working for <code>masterImage</code> that will be updated after changes.
	 * The image name is used to propose a file name in the open and save dialog.
	 * Loaded images will be resized and croped to fit 256x192 and then turned to gray tones.
	 * 
	 * @param imageName the name used for proposing a file name
	 * @param masterImage the "master image" that will be updated after changes, or null
	 */
	public ImageHelper(String imageName, MMImage masterImage) {
		this(imageName, masterImage, 256, 192, true);
	}

	/**
	 * Constructs a new helper working for <code>masterImage</code> that will be updated after changes.
	 * The image name is used to propose a file name in the open and save dialog.
	 * Loaded images can be resized and croped to fit the given size and/or turned to gray tones.
	 * 
	 * @param imageName the name used for proposing a file name
	 * @param masterImage the "master image" that will be updated after changes, or null
	 * @param width the desired width of the image or -1 for the image's original size
	 * @param height the desired height of the image or -1 for the image's original size
	 * @param toGrayScale flag for turning the image into a gray tone image.
	 */
	public ImageHelper(String imageName, MMImage masterImage, int width, int height, boolean toGrayScale) {
		m_imageName = imageName;
		String nameOnButton = imageName == null ? " " : "<" + imageName + "> ";
		if(masterImage == null)
			m_masterImage = new MMImage(width, height);
		else
			m_masterImage = masterImage;
		m_width = width;
		m_height = height;
		m_grayScale = toGrayScale;
		
		// button for loading binary images
		m_loadImageButton = new JButton("Load picture from local disk...");
		m_loadImageButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				loadImage();
			}
		});
		
		// button for loading ascii images (gray tone matrices)
		m_loadMatrixButton = new JButton("Load "+ nameOnButton + "from local disk...");
		m_loadMatrixButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				loadMatrix();
			}
		});
		
		// button for writing ascii images (gray tone matrices)
		m_writeMatrixButton = new JButton("Save "+ nameOnButton + "to local disk...");
		m_writeMatrixButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				saveMatrix();
			}
		});
		m_textFileFilter = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() 
				|| endsWith(f.getName(), ".txt");
			}

			public String getDescription() {
				return "Text files (*.txt)";
			}
		};
		m_matrixImageFileFilter = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() 
				|| endsWith(f.getName(), "_matrix.txt");
			}

			public String getDescription() {
				return "Images encoded as ASCII matrices (*_matrix.txt)";
			}
		};
		m_imagesFileFilter = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() 
				|| endsWith(f.getName(), ".jpg")
				|| endsWith(f.getName(), ".gif")
				|| endsWith(f.getName(), ".png")
				|| endsWith(f.getName(), ".bmp");
			}

			public String getDescription() {
				return "Pictures (*.jpg, *.gif, *.png, *.bmp)";
			}
		};
	}
	
	/**
	 * This method binds all ImageHelpers in helperArray together so that when
	 * loading or saving of matrices or loading of images executed is the file
	 * choosing window opens the same location(directory).
	 * 
	 * @param helperArray includes all ImageHelpers that are to be bind together
	 */
	public static void currentWorkingDirectoryDependsOn(ImageHelper[] helperArray){
		for (int i = 0; i < helperArray.length; i++) {
			helperArray[i].setOtherHelpers(helperArray);
		}
	}
	/**
	 * Sets in this ImageHelper the other ImageHelpers in the group so that when
	 * loading or saving of a matrix or loading of an image executed is the file
	 * choosing window opens the same location(directory).
	 * 
	 * @param helperArray is the group
	 */
	private void setOtherHelpers(ImageHelper[] helperArray) {
		m_otherHelpers = helperArray;
	}
	
	private void updateDefaultDirectoryOfAllOtherHelpers(){
		if(m_otherHelpers!=null)
		for (int i = 0; i < m_otherHelpers.length; i++) {
			m_otherHelpers[i].setCurrentDirectory(m_currentDirectory);
		}
	}
	
	/**
	 * Sets in this ImageHelper the a default directory so that when
	 * loading or saving of a matrix or loading of an image executed is
	 * the file choosing window opens the same location(directory).
	 * 
	 * @param directory
	 */
	private void setCurrentDirectory(File directory){
		if(m_currentDirectory == null || !m_currentDirectory.equals(directory))
		m_currentDirectory = directory;
	}

	private static boolean endsWith(String s, String suffix) {
		if(s.indexOf(suffix) == -1)// suffix is contained in string
			return false;
		String sub = s.substring(s.length()-suffix.length());// at the end?
		return sub.equalsIgnoreCase(suffix);
	}
	
	class ImageThumbnail extends JPanel implements PropertyChangeListener {
		
		private MMImage m_image = new MMImage(256, 192);
		private JLabel m_statusLabel;
		private boolean m_readable = false;
		
		ImageThumbnail(JFileChooser fc) {
			super(new BorderLayout());
			fc.addPropertyChangeListener(this);
			add(m_image.getAsContainerContent(), BorderLayout.CENTER);
			m_statusLabel = new JLabel("<html><b>No image selected", JLabel.CENTER);
			add(m_statusLabel, BorderLayout.SOUTH);
		}
		
		public void propertyChange(PropertyChangeEvent e) {
	    boolean update = false;
	    String prop = e.getPropertyName();

	    //If the directory changed, don't show an image.
	    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
	    	showImage(null);

	    //If a file became selected, find out which one.
	    } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
	    	showImage((File) e.getNewValue());
	    }
		}
		
		private boolean isReadable() {
			return m_readable;
		}
		
		private void showImage(File f) {
			if(f == null) {
				m_image.setRealImage(null);
				m_statusLabel.setText("<html><b>No image selected");
				m_readable = false;
			}
			else {
				BufferedImage img = Graphics2DUtils.loadImageFromFile(f);
				if(img == null) {
					m_image.setRealImage(Graphics2DUtils.loadImageFromClasspath("/resource/icon/error.png"));
					m_statusLabel.setText("<html><b>Cannot read image!");
					m_readable = false;
				} else {
					m_image.setRealImage(img);
					m_statusLabel.setText("<html><b>Size: " + m_image.getRealImage().getWidth() + "x" + m_image.getRealImage().getHeight());
					m_readable = true;
				}
			}
		}
		
		private BufferedImage getRealImage() {
			return m_image.getRealImage();
		}
	}
	
	private void loadImage() {
		try {
			JFileChooser fileChooser = new JFileChooser(m_currentDirectory);
			ImageThumbnail thumbnail = new ImageThumbnail(fileChooser);
			fileChooser.setAccessory(thumbnail);
			fileChooser.addChoosableFileFilter(m_imagesFileFilter);
			fileChooser.setFileFilter(m_imagesFileFilter);
			fileChooser.setDialogTitle(m_loadImageButton.getText());
			int result = fileChooser.showOpenDialog(null);
			if(result == JFileChooser.APPROVE_OPTION) {
				BufferedImage img = thumbnail.getRealImage();//Graphics2DUtils.loadImageFromFile(fileChooser.getSelectedFile());
				if(thumbnail.isReadable() == false || img == null) {
					JOptionPane.showMessageDialog(null, ResourceManager.getMessage("imagehelper.error_reading_image"), ResourceManager.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
					loadImage();
					return;
				}
				performOperations(img);
				ActionManager.performActionCycleFromObject(getMasterImage());
				m_currentDirectory = fileChooser.getCurrentDirectory();
				updateDefaultDirectoryOfAllOtherHelpers();
			}
		} catch(AccessControlException ace) {
  		JOptionPane.showMessageDialog(null, ResourceManager.getMessage("imagehelper.norights2load")); 
		}
	}
	
	private void loadMatrix() {
		try {
			JFileChooser fileChooser = new JFileChooser(m_currentDirectory);//m_oldLoadDirectory + "/" + m_imageName + "_matrix.txt");
			fileChooser.setSelectedFile(new File(m_imageName + "_matrix.txt"));
			fileChooser.addChoosableFileFilter(m_matrixImageFileFilter);
			fileChooser.addChoosableFileFilter(m_textFileFilter);
			fileChooser.setFileFilter(m_matrixImageFileFilter);
			fileChooser.setDialogTitle(m_loadMatrixButton.getText());
			int result = fileChooser.showOpenDialog(m_loadImageButton);
			if(result == JFileChooser.APPROVE_OPTION) {
	//			m_oldLoadFile = fileChooser.getSelectedFile();
				readASCIIImage(fileChooser.getSelectedFile());
				ActionManager.performActionCycleFromObject(getMasterImage());
				m_currentDirectory = fileChooser.getCurrentDirectory();
				updateDefaultDirectoryOfAllOtherHelpers();
			}
		} catch(AccessControlException ace) {
  		JOptionPane.showMessageDialog(null, ResourceManager.getMessage("imagehelper.norights2load")); 
		}
	}
	
	private void saveMatrix() {
		try {
			JFileChooser fileChooser = new JFileChooser(m_currentDirectory);
			fileChooser.setSelectedFile(new File(m_imageName + "_matrix.txt"));
			fileChooser.addChoosableFileFilter(m_matrixImageFileFilter);
			fileChooser.addChoosableFileFilter(m_textFileFilter);
			fileChooser.setFileFilter(m_matrixImageFileFilter);
			fileChooser.setDialogTitle(m_writeMatrixButton.getText());
			int saveQuery;
			do {
				saveQuery = fileChooser.showSaveDialog(m_loadImageButton);
				if(saveQuery == JFileChooser.APPROVE_OPTION 
						&& fileChooser.getSelectedFile().exists()) {
					int overrideQuery = JOptionPane.showConfirmDialog(null, ResourceManager.getMessage("imagehelper.overwrite"), null, JOptionPane.YES_NO_OPTION);
					if(overrideQuery == JOptionPane.YES_OPTION)
						break;
				} else
					break;
			}	while(true);
			if(saveQuery == JFileChooser.APPROVE_OPTION) {
	//			m_oldSaveFile = fileChooser.getSelectedFile();
				saveASCIIImage(fileChooser.getSelectedFile());
				m_currentDirectory = fileChooser.getCurrentDirectory();
				updateDefaultDirectoryOfAllOtherHelpers();
			}
		} catch(AccessControlException ace) {
  		JOptionPane.showMessageDialog(null, ResourceManager.getMessage("imagehelper.norights2save")); 
		}
	}
	
	/**
	 *  Reads in and tries to parse the content from the specified location into a gray tone image.
	 *  The master image will be updated.
	 */
	public NumberMatrix readASCIIImage(File location) {
		final String COMMENT = "#";
		final String SEPARATOR = ",";
		NumberMatrix result = new NumberMatrix(MInteger.class, 0, 0);
		try {
			FileInputStream fin = new FileInputStream(location);
			BufferedReader r = new BufferedReader(new InputStreamReader(fin));
			String line = null;
			while((line = r.readLine()) != null) {
				if(line.startsWith(COMMENT) || line.trim().length() == 0)
					continue;
				String[] entries = line.split(SEPARATOR);
//				System.out.println(":" + entries[0] + ":" + entries[1] + ":" + entries[2] + ":" + entries[3] + ":");
				if(result.getColumnCount() == 0) // first row -> col count still unknown
					result.resize(result.getRowCount(), entries.length);
				else if(result.getColumnCount() != entries.length)
					throw new IllegalArgumentException("The number of entries per row must be constant!");
				// read in line entries
				result.resize(result.getRowCount()+1, result.getColumnCount());
				for(int i = 0; i < entries.length; i++) {
					String valueString = entries[i].trim();
					MInteger value = new MInteger();
					if(valueString.equals("?"))
						value.setEdited(false);
					else
						value.setDouble(Integer.parseInt(valueString));
					result.setEntryRef(result.getRowCount(), i+1, value);
				}
			}
		} catch(NumberFormatException nfex) {
			JOptionPane.showMessageDialog(null, "Cannot read image file! Image data is not in ASCII format.");
			return null;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File not found!","Java Applet",JOptionPane.ERROR_MESSAGE);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		getMasterImage().setGrayToneMatrix(result);
		return result;
	}
	
	/**
	 * Saves the master image under the specified location.
	 * The master image must be a gray tone image.
	 */
	public void saveASCIIImage(File location) {
		if(getMasterImage() == null)
			throw new NullPointerException("Master image must be set for saving!");
		try {
			byte[] header = ("#\n# Solution matrix " + m_imageName + "\n#\n\n").getBytes();
			byte[] asciiData = createASCIIMatrix(getMasterImage().getGrayToneMatrix()).getBytes();
			FileOutputStream fout = new FileOutputStream(location);
			fout.write(header);
			fout.write(asciiData);
			fout.close();
//			ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByMIMEType("image/bmp").next();
//			writer.setOutput(ImageIO.createImageOutputStream(new FileOutputStream(location)));
//			writer.write(m_realImage);
		} catch(IOException ioex) {
			ioex.printStackTrace();
		}
	}
	
	/**
	 * Returns an ASCII representation of the given matrix.
	 * <pre>
	 * Entry format conditions:
	 *  (1) leading space (max 3 character)
	 *  (2) possibility of "empty" (i.e. not edited) values ("?" instead of any character)
	 *  (3) minus sign possible (1 char)
	 *  (4) separated by space (1 char)
	 * Examples: "   1", "-255", " 255", "   ?"
	 * </pre>
	 */
	public static String createASCIIMatrix(NumberMatrix m) {
		final char LEADING_CHARACTER = ' ';
		final char SEPARATOR = ',';
		final char NEGATIVE_SIGN = '-';
		final char POSITIVE_SIGN = ' ';
		final char NOT_EDITED_SIGN = '?';
		StringBuffer result = new StringBuffer(m.getColumnCount()*m.getRowCount()*5);
		for(int r = 1; r <= m.getRowCount(); r++) {
			for(int c = 1; c <= m.getColumnCount(); c++) {
				StringBuffer entryString = new StringBuffer(5);
				// condition (1)
				if(c > 1)
					entryString.append(SEPARATOR);
				// condition (2)
				if(!((MNumber)m.getEntryRef(r, c)).isEdited()) {
					if(c > 1)
						result.append(SEPARATOR);
					result.append(LEADING_CHARACTER);
					result.append(LEADING_CHARACTER);
					result.append(LEADING_CHARACTER);
					result.append(NOT_EDITED_SIGN);
					continue;
				}
				int value = (int) ((MNumber)m.getEntryRef(r, c)).getDouble();
				int posValue = Math.abs(value);
				// condition (4)
				if(posValue < 100) { // max 2 digits
					entryString.append(LEADING_CHARACTER);
					if(posValue < 10) // only 1 digit
						entryString.append(LEADING_CHARACTER);
				}
				// condition (3)
				if(value < 0)
					entryString.append(NEGATIVE_SIGN);
				else
					entryString.append(POSITIVE_SIGN);
				// write the number
				entryString.append(posValue);
				result.append(entryString);
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	/**
	 * Returns the master image that will be updatet after changes.
	 */
	public MMImage getMasterImage() {
		return m_masterImage;
	}
	
	/**
	 * Returns a button permitting to load an arbitrary image from the local disk via an open file dialog.
	 */
	public JButton getLoadImageButton() {
		return m_loadImageButton;
	}
	
	/**
	 * Returns a button permitting to load a gray tone image (matrix) from the local disk via an open file dialog.
	 */
	public JButton getLoadMatrixButton() {
		return m_loadMatrixButton;
	}

	/**
	 * Returns a button permitting to save a gray tone image (matrix) to the local disk via a save file dialog.
	 */
	public JButton getSaveMatrixButton() {
		return m_writeMatrixButton;
	}
		
	/**
	 * Performs the requested operations on the input image and stores the result in the master image.
	 * This method can be used to store an image manually (i.e. from "outside" this class).
	 */
	public void performOperations(BufferedImage img) {
		if(img == null) {
			return;
		}
		int width = m_width;
		if(m_width == -1)
			width = img.getWidth();
		int height = m_height;
		if(m_height == -1)
			height = img.getHeight();
		if(width > MAX_WIDTH || height > MAX_HEIGHT) {
			double orgWidth = img.getWidth();
			double orgHeight = img.getHeight();
			if(orgWidth > MAX_WIDTH || orgHeight > MAX_HEIGHT) {
				double ratio = (double)MAX_WIDTH / (double)orgWidth;
				if(orgHeight * ratio > MAX_HEIGHT)
					ratio = (double)MAX_HEIGHT / (double)orgHeight;
			  width = (int) (orgWidth * ratio);
			  height = (int) (orgHeight * ratio);
			}
		}
		BufferedImage result = Graphics2DUtils.createFilledImage(width, height, img);
		if(m_grayScale) {
			result = Graphics2DUtils.createGrayscaledImage(result);
		}
		setRealImage(result);
	}
	
	/**
	 * Sets the internally stored image in the master.
	 */
	public void setRealImage(BufferedImage img) {
			getMasterImage().setRealImage(img);
	}
}
