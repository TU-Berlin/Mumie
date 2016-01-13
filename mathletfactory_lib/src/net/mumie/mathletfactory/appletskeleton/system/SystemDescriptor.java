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

package net.mumie.mathletfactory.appletskeleton.system;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.extension.Extension;
import net.mumie.mathletfactory.util.property.PropertyMapIF;
import net.mumie.mathletfactory.util.xml.XMLUtils;

public class SystemDescriptor implements SystemPropertyIF, MathletConstants {

	private final static String LIB_VERSION_FILE_LOCATION = "/resource/settings/lib.version";
	
	private final static Version m_jreVersion = new Version(Version.JVM_VERSION_TYPE, System.getProperty("java.version"));
	
	private final static String m_jreVendor = System.getProperty("java.vendor");
	
	private final static Version m_libVersion;
	
	static {
		try {
			m_libVersion = new Version(new BufferedReader(new InputStreamReader(
				SystemDescriptor.class.getResourceAsStream(LIB_VERSION_FILE_LOCATION))).readLine());
		} catch (IOException ioex) {
			throw new RuntimeException("Cannot read library version from file " + LIB_VERSION_FILE_LOCATION);
		}
	}
	
	public static void analyzeSystem(MathletRuntime runtime, PropertyMapIF map) {
		addProperty(map, JRE_VERSION_PROPERTY, getJREVersion().toString(false, false));
		addProperty(map, JRE_VENDOR_PROPERTY, getJREVendor());
		addProperty(map, LIB_VERSION_PROPERTY, getLibVersion().toString(true, true));
		addProperty(map, FULL_SCREEN_SUPPORTED_PROPERTY, isFullScreenSupported());
		addProperty(map, JMF_AVAILABLE_PROPERTY, isJMFAvailable());
		addProperty(map, JAVA_HELP_AVAILABLE_PROPERTY, isJavaHelpAvailable());
		addProperty(map, JAVA_3D_AVAILABLE_PROPERTY, isJava3DAvailable());
		addProperty(map, JREALITY_AVAILABLE_PROPERTY, isJRealityAvailable());
		addProperty(map, OMITS_XML_NAMESPACES_PROPERTY, XMLUtils.isOmittingXMLNamespaces());
	}
	
	public static Version getLibVersion() {
		return m_libVersion;
	}
	
	public static String getExtensionEnumeration(Extension[] extensions) {
		String result = new String();
		for(int i = 0; i < extensions.length; i++) {
			result += extensions[i].getName();
			if(i < extensions.length - 1)
				result += ", ";
		}
		return result;
	}
	
	public static String getExecutionModeName(int mode) {
		switch(mode) {
		case APPLET_EXECUTION_MODE:
			return "applet";
		case APPLICATION_EXECUTION_MODE:
			return "application";
		default:
			return "unknown";	
		}
	}
	
	private static void addProperty(PropertyMapIF map, String name, boolean value) {
		addProperty(map, name, Boolean.toString(value));
	}
	
	private static void addProperty(PropertyMapIF map, String name, String value) {
		map.setProperty(name, value);
	}
	
	private static boolean classExists(String className) {
  	try {
  		Class.forName(className);
  		return true;
  	} catch(Throwable e) {
  		return false;
  	}
	}
	
  /**
   * Returns whether the Java3D library is available.
   */
  public static boolean isJava3DAvailable() {
		return classExists("javax.media.j3d.Canvas3D");
  }
  
  /**
   * Returns whether the JReality library is available.
   */
  public static boolean isJRealityAvailable() {
		return classExists("de.jreality.scene.SceneGraphComponent");
  }
  
  /**
   * Returns whether the JavaHelp library is available.
   */
  public static boolean isJavaHelpAvailable() {
		return classExists("javax.help.HelpSet");
  }
  
  /**
   * Returns whether the JMF (=Java Media Framework) library is available.
   */
	public static boolean isJMFAvailable() {
		return classExists("javax.media.Manager");
	}

	/**
   * Returns whether full screen windows are supported.
   */
  public static boolean isFullScreenSupported() {
    return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported();
  }
  
	/**
   * Returns the version of the Java Runtime Environment.
   */
  public static Version getJREVersion() {
    return m_jreVersion;
  }
  
	/**
   * Returns the vendor's name of the Java Runtime Environment.
   */
  public static String getJREVendor() {
    return m_jreVendor;
  }
}
