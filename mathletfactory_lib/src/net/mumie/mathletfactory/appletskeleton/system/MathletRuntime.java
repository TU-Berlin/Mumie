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

import java.applet.Applet;
import java.applet.AppletContext;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.mumie.mathletfactory.appletskeleton.MathletContext;
import net.mumie.mathletfactory.appletskeleton.util.MumieTheme;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogAction;
import net.mumie.mathletfactory.appletskeleton.util.dialog.DialogManager;
import net.mumie.mathletfactory.display.DisplayProperties;
import net.mumie.mathletfactory.util.Version;
import net.mumie.mathletfactory.util.error.ErrorHandler;
import net.mumie.mathletfactory.util.extension.Extension;
import net.mumie.mathletfactory.util.extension.MessagesExtensionMap;
import net.mumie.mathletfactory.util.extension.ParameterDefinition;
import net.mumie.mathletfactory.util.extension.ScreenTypeExtensionMap;
import net.mumie.mathletfactory.util.extension.UnsupportedExtensionException;
import net.mumie.mathletfactory.util.help.MumieHelp;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

public class MathletRuntime implements MathletConstants, SystemPropertyIF {

	public final static Version DEFAULT_COMPLIANCE_LEVEL = new Version("2.0");
	
	/** Name of the base lib extension. */
	public final static String BASE_LIB_EXTENSION_NAME = "mf-lib_base";
	
	/** Name of the noc screen type extension. */
	public final static String NOC_LIB_EXTENSION_NAME = "mf-lib_noc";
	
	/** Name of the Graphics2D screen type extension. */
	public final static String G2D_LIB_EXTENSION_NAME = "mf-lib_g2d";
	
	/** Name of the Java3D screen type extension. */
	public final static String J3D_LIB_EXTENSION_NAME = "mf-lib_j3d";
	
	/** Name of the JReality3D screen type extension. */
	public final static String JR3D_LIB_EXTENSION_NAME = "mf-lib_jr3d";
	
  // Logging
  private final static MumieLogger LOGGER = MumieLogger.getLogger(MathletRuntime.class);
  private final static LogCategory RUNTIME_CATEGORY = LOGGER.getCategory("mathlet.runtime");
  private final static LogCategory PARAMETERS_CATEGORY = LOGGER.getCategory("mathlet.parameters");
  private final static LogCategory LANGUAGE_CATEGORY = LOGGER.getCategory("language");
  private final static LogCategory METHOD_ENTERING_CATEGORY = LOGGER.getCategory("method-entering");

	private static MathletRuntime m_instance;
	
	public static MathletRuntime getRuntime() {
		if(isInitialized() == false)
			throw new RuntimeException("Mathlet runtime is not initialized !");
		return m_instance;
	}
	
	public static boolean isInitialized() {
		return m_instance != null;
	}
  
  public static MathletRuntime createStaticRuntime() {
    return createStaticRuntime(null);
  }
	
	public static MathletRuntime createStaticRuntime(MathletContext mathlet) {
		if(m_instance != null)
			throw new RuntimeException("Static mathlet runtime cannot be created more than once !");
		MathletRuntime runtime = new MathletRuntime(mathlet);
		m_instance = runtime;
		return runtime;
	}

	private Vector m_registeredExtensions = new Vector(2);
	
	private PropertyMapIF m_parameters = new DefaultPropertyMap();
//	private PropertyMapIF m_loggingProperties = new DefaultPropertyMap();
	
	private ScreenTypeExtensionMap m_screenTypes = new ScreenTypeExtensionMap();
	
	/** Field holding the messages for all languages. */
	private MessagesExtensionMap m_allMessages = new MessagesExtensionMap();
	
	/** Field pointing to a map in <code>m_allMessages</code> .*/
	private PropertyMapIF m_messages;
	
	private PropertyMapIF m_registeredParameters = new DefaultPropertyMap();
	
	private String[][] m_parameterInfo;
	
	private PropertyMapIF m_systemProperties = new DefaultPropertyMap();
	
	private LinkedHashMap m_extensions = new LinkedHashMap();
	
	private LinkedHashMap m_extensionParameters = new LinkedHashMap();
	
	private MathletContext m_mathlet;
	
	private Version m_complianceLevel = MathletRuntime.DEFAULT_COMPLIANCE_LEVEL;
	
	private Version m_jvmMinVersion;
	
	private int m_executionMode = -1;
	
  private ErrorHandler m_errorHandler;
  	
  private Locale m_locale;
  
  private MumieHelp m_help;
  
  private DialogManager m_dialogManager;
  
  /**
   * Initializes a new runtime instance for the given mathlet context.
   * 
   * @param mathlet the mathlet context; may be <code>null</code> for applications
   */
	private MathletRuntime(MathletContext mathlet) {
    LOGGER.log(METHOD_ENTERING_CATEGORY, "creating runtime for " + (mathlet == null ? "null" : mathlet.getAppletClass().getName()) + "...");
    
		// sets the context (may be null!)
		setExecutionContext(mathlet);
		
		// read in system properties
		SystemDescriptor.analyzeSystem(this, getSystemProperties());
		
		// register extensions (recursive)
		registerExtensions();
		
		// read in minimum system requirements
		generateSystemRequirements();

		// load all registered extensions
		initializeExtensions();
		
//		// retrieve at first the logging properties 
//		collectLoggingProperties();
		
		// register the known parameters from all extensions
		registerKnownParameters();
		
		// collect the property sheet parameters from all extensions
		collectParameters();

		// set up the client language
		registerLanguage();
		
		// register screen types from all extensions 
		registerScreenTypes();
		
		// register messages from all extensions
		registerMessages();
		
		// collect theme properties from all extensions and set up Look&Feel
		createLookAndFeel();
		
		// propagate values to GeneralTransformer class
		m_screenTypes.propagateValues();
		
		// load help information from all extension and create appropriate help system
		createHelpSystem();
		
		// register dialogs from all extensions
		registerDialogs();
	}
	
  /**
   * Sets the MathletContext and initializes the EXECUTION mode.
   */
	private void setExecutionContext(MathletContext mathlet) {
		m_mathlet = mathlet;
    try {
      AppletContext context = m_mathlet.getJApplet().getAppletContext();
      m_executionMode = APPLET_EXECUTION_MODE;
    	LOGGER.log(RUNTIME_CATEGORY, "initializing applet execution mode");
    } catch (NullPointerException e) {
      m_executionMode = APPLICATION_EXECUTION_MODE;
    	LOGGER.log(RUNTIME_CATEGORY, "initializing application execution mode");
    }
    // add system property
		getSystemProperties().setProperty(SystemPropertyIF.EXECUTION_MODE_PROPERTY, SystemDescriptor.getExecutionModeName(m_executionMode));
	}
	
	private void registerKnownParameters() {
		Set extensionNames = m_extensions.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			PropertyMapIF map = getExtension(name).getRegisteredParameters();
			m_registeredParameters.copyPropertiesFrom(map);
			LOGGER.log(PARAMETERS_CATEGORY, map.getPropertiesCount() + " known parameters found in extension \"" + name + "\"");
		}
		// propagate values to String array
		Property[] parameterDefinitions = m_registeredParameters.getProperties();
		m_parameterInfo = new String[parameterDefinitions.length][3];
		LOGGER.log(PARAMETERS_CATEGORY, "Total number of known parameters: " + parameterDefinitions.length);
		for(int i = 0; i < parameterDefinitions.length; i++) {
			ParameterDefinition def = (ParameterDefinition) parameterDefinitions[i].getValue();
			m_parameterInfo[i] = new String[] { def.getName(), def.getType(), def.getDescription() };
		}
		// register known parameters (i.e. read from applet context)
		if(isAppletExecutionMode()) {
			for(int i = 0; i < m_parameterInfo.length; i++) {
				String parameterName = m_parameterInfo[i][0];
				String parameterValue = m_mathlet.readParameter(parameterName);
				if(parameterValue != null) {
					m_parameters.setProperty(parameterName, parameterValue);
					LOGGER.log(PARAMETERS_CATEGORY, "Parameter \"" + parameterName + "\" has value \"" + (parameterValue != null ? parameterValue : "not set") + "\"");
				}
			}
		} else if(isApplicationExecutionMode()) {
			// TODO read in arguments from application's main method
		}
		LOGGER.log(PARAMETERS_CATEGORY, m_parameters.getPropertiesCount() + " parameters registered");
	}
	
  /**
   * Initializes the language settings.
   */
	private void registerLanguage() {
  	// setting the locale
  	m_locale = Locale.getDefault();
    if (isAppletExecutionMode() && getParameter("lang") != null) {
    	m_locale = new Locale(getParameter("lang"));
    	m_mathlet.getJApplet().setLocale(m_locale);
    	LOGGER.log(LANGUAGE_CATEGORY, "reading locale \"" + m_locale.getLanguage() + "\" from parameters");
    } else {
    	LOGGER.log(LANGUAGE_CATEGORY, "reading locale \"" + m_locale.getLanguage() + "\" from system defaults");
    }
  	LOGGER.log(RUNTIME_CATEGORY.and(LANGUAGE_CATEGORY), "switching to locale \"" + m_locale.getLanguage() + "\" ("+m_locale.getDisplayLanguage()+")");
  	// add system property
		getSystemProperties().setProperty(SystemPropertyIF.LANGUAGE_PROPERTY, m_locale.getLanguage());
	}
	
	private void collectParameters() {
		Set extensionNames = m_extensions.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			m_parameters.copyPropertiesFrom(getExtension(name).getParameters());
			Extension complianceSettings = getExtension(name).getComplianceExtensions().getComplianceExtension(getComplianceLevel());
			if(complianceSettings != null)
				m_parameters.copyPropertiesFrom(complianceSettings.getParameters());
		}
	}
	
//	private void collectLoggingProperties() {
//		Set extensionNames = m_extensions.keySet();
//		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
//			String name = (String) i.next();
//			m_loggingProperties.copyPropertiesFrom(getExtension(name).getLoggingProperties());
//		}
//	}
	
	private void registerScreenTypes() {
		Set extensionNames = m_extensions.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			m_screenTypes.copyFrom(getExtension(name).getScreenTypes());
			Extension complianceSettings = getExtension(name).getComplianceExtensions().getComplianceExtension(getComplianceLevel());
			if(complianceSettings != null)
				m_screenTypes.copyFrom(complianceSettings.getScreenTypes());
		}
	}
	
	private void registerMessages() {
		// register extensions's messages
		Set extensionNames = m_extensions.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			m_allMessages.copyFrom(getExtension(name).getMessages());
		}
  	m_messages = m_allMessages.getMessages(m_locale);
	}
	
	private void createLookAndFeel() {
		// only proceed if MumieTheme should be used
		if(getParameter("useMumieTheme", "true").equalsIgnoreCase("true")) {
			// collect theme properties
			Set extensionNames = m_extensions.keySet();
			for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
				String name = (String) i.next();
				MumieTheme.addThemeProperties(getExtension(name).getThemeProperties());
			}
		}
		// update display properties' defaults
		DisplayProperties.loadDefaults();
		// set up Metal Look&Feel
    try {
    	MumieTheme mumieTheme = MumieTheme.DEFAULT_THEME;
      MetalLookAndFeel.setCurrentTheme(mumieTheme);
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      if(getContext() != null) // TODO implement applications
      	SwingUtilities.updateComponentTreeUI(getContext().getAppletContentPane());
    } catch (Exception ex) {
      System.err.println(ex);
    }
	}
  
	private void createHelpSystem() {
		try {
			// create appropriate help system
			m_help = MumieHelp.createHelpSystem(this);
			if(m_help == null) {
				System.err.println("No appropriate help system could be found !");
				return;
			}
			// collect help information from all extensions
			collectHelpInformation();
			// add help support in mathlet ui context
			if(getContext() != null) // TODO implement applications
				m_help.addHelpSupport(getContext());
		} catch(Throwable t) {
			System.err.println("Error occurred while initializing help system !");
			t.printStackTrace();
		}
	}
		
	private void collectHelpInformation() throws Exception {
		Set extensionNames = m_extensions.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			Extension e = getExtension(name);
			m_help.addHelpExtension(e, getLocale());
		}
	}
	
	private void registerDialogs() {
		m_dialogManager = new DialogManager(getContext());
		Set extensionNames = m_extensions.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			m_dialogManager.addDialogs(getExtension(name).getDialogs());
		}
	}
	
	private void registerExtensions() {
		// pre-register MathletFactory library extensions (only supported extensions)
		registerExtension(BASE_LIB_EXTENSION_NAME);
		registerExtension(NOC_LIB_EXTENSION_NAME);
		registerExtension(G2D_LIB_EXTENSION_NAME);
		if(SystemDescriptor.isJava3DAvailable()) // Java3D may not be available!
			registerExtension(J3D_LIB_EXTENSION_NAME);
		if(SystemDescriptor.isJRealityAvailable()) // JReality may not be available!
			if(SystemDescriptor.getJREVersion().newer(JVMVersionIF.VERSION_1_4_2)) // JReality is not Java 1.4 compatible!
				registerExtension(JR3D_LIB_EXTENSION_NAME);
		// load "extensions" parameter from pre-registered lib extensions
		while(m_registeredExtensions.isEmpty() == false) {
			String name = (String) m_registeredExtensions.remove(0);
			PropertyMapIF params = Extension.loadParameters(name);
			if(params != null) {
				addExtension(name, params);
			}
		}
		// load parameters from mathlet extension (if available)
		if(getContext() != null) { // TODO implement applications
			addExtension("mathlet", Extension.loadParameters(getContext()));
		}
	}
	
	private void registerExtension(String name) {
		// only add "new" extensions
		if(m_registeredExtensions.contains(name) == false) {
			m_registeredExtensions.add(name);
			LOGGER.log(RUNTIME_CATEGORY, "Registering extension \"" + name + "\"...");
		}
	}
	
	private void generateSystemRequirements() {
		// generate minimal Java VM version
		m_jvmMinVersion = JVMVersionIF.VERSION_1_4_2;
		Collection extensionNames = m_extensionParameters.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			PropertyMapIF params = (PropertyMapIF) m_extensionParameters.get(name);
			Version v = (Version) params.getProperty("version.jvm.min", JVMVersionIF.VERSION_1_4_2);
			if(v.newer(m_jvmMinVersion))
				m_jvmMinVersion = v;
		}
		LOGGER.log(RUNTIME_CATEGORY, "version of MathletFactory library is: \"" + SystemDescriptor.getLibVersion() + "\"");
		LOGGER.log(RUNTIME_CATEGORY, "version of Java VM is: \"" + SystemDescriptor.getJREVersion() + "\"");
  	LOGGER.log(RUNTIME_CATEGORY, "vendor of Java VM is: \"" + SystemDescriptor.getJREVendor() + "\"");
  	LOGGER.log(RUNTIME_CATEGORY, "min Java VM version is: " + m_jvmMinVersion);
  	if(SystemDescriptor.getJREVersion().older(getMinJVMVersion()))
			addError(new UnsupportedExtensionException(UnsupportedExtensionException.NEWER_JVM_VERSION_NEEDED_ERROR));
	}
	
	private void initializeExtensions() {
		Collection extensionNames = m_extensionParameters.keySet();
		for(Iterator i = extensionNames.iterator(); i.hasNext(); ) {
			String name = (String) i.next();
			PropertyMapIF params = (PropertyMapIF) m_extensionParameters.get(name);
			Extension e = null;
			if(name.equals("mathlet")) {
				try {
					e = Extension.forMathlet(getContext(), params);
					m_complianceLevel = e.getComplianceLevel();
				} catch(Throwable t) {
					addError(t);
					continue;
				}
			} else {
				try {
					e = Extension.forName(name, params);
				} catch(Throwable t) {
					addError(t);
					continue;
				}
			}
			m_extensions.put(name, e);
		}
		// no need to maintain references to the parameter maps
		m_extensionParameters.clear();
		m_extensionParameters = null;
		// add system properties
		getSystemProperties().setProperty(SystemPropertyIF.EXTENSIONS_PROPERTY, SystemDescriptor.getExtensionEnumeration(getExtensions()));
		if(isAppletExecutionMode())
			getSystemProperties().setProperty(SystemPropertyIF.MATHLET_BASE_PATH_PROPERTY, getExtension(Extension.MATHLET_EXTENSION_NAME).getBasePath());
	}
	
	private void addExtension(String name, PropertyMapIF params) {
		// check if extension was allready installed (recursive referencing possible)
		PropertyMapIF oldParams = (PropertyMapIF) m_extensionParameters.get(name);
		if(oldParams != null) {
			return;
		}
    LOGGER.log(RUNTIME_CATEGORY, "Loading extension \"" + name + "\"...");
		// register sub-extensions in extension "e" recursivly
		String extensions = (String) params.getProperty("extensions");
		if(extensions != null) {
			StringTokenizer tok = new StringTokenizer(extensions, ",; ");
			while(tok.hasMoreTokens()) {
				String extName = tok.nextToken().trim();
				PropertyMapIF extParams = Extension.loadParameters(extName);
				if(extParams != null)
					addExtension(extName, extParams);
			}
		}
		m_extensionParameters.put(name, params);
	}
	
	public MathletContext getContext() {
		return m_mathlet;
	}
	
	public Extension getExtension(String name) {
		return (Extension) m_extensions.get(name);
	}
	
	public Version getComplianceLevel() {
		return m_complianceLevel;
	}
	
	Extension[] getExtensions() {
		Extension[] result = new Extension[m_extensions.size()];
		m_extensions.values().toArray(result);
		return result;
	}
	
	/**
	 * Returns the EXECUTION mode as an integer.
	 */
	public int getExecutionMode() {
		return m_executionMode;
	}
	
	/**
	 * Returns whether the mathlet was started as an applet.
	 */
	public boolean isAppletExecutionMode() {
		return m_executionMode == APPLET_EXECUTION_MODE;
	}
	
	/**
	 * Returns whether the mathlet was started as an application.
	 */
	public boolean isApplicationExecutionMode() {
		return m_executionMode == APPLICATION_EXECUTION_MODE;
	}
	
  /**
   * Returns a parameter's value from the proper internal map, containing
   * a key-value pair for each registered parameter which was preloaded
	 * during applet initialization.
   * If the named parameter was not registered, the applet's raw implementation of 
   * {@link Applet#getParameter(String)} will be used to retrieve the value.
   * Returns <code>null</code> if no such parameter was set.
   */
  public String getParameter(String name) {
  	String parameterValue = null;
  	Object o = m_parameters.getProperty(name);
  	if(o == null || o instanceof String)
  		parameterValue = (String) o;
  	else
  		parameterValue = o.toString();
  	// try to read in parameter explicitly from applet
  	// (if parameter is not registered -> user parameter)
  	if(parameterValue == null && isAppletExecutionMode()) {
  		parameterValue = m_mathlet.readParameter(name);
  	}
  	return parameterValue;
  }
  
  /**
   * Returns the parameter with the given name if it exists or <code>defaultValue</code> otherwise.
   * The <code>name</code> argument is case insensitive.
   */
  public String getParameter(String name, String defaultValue) {
  	if(getParameter(name) != null)
  		return getParameter(name);
  	else
  		return defaultValue;
  }
  
  public String getSystemProperty(String name) {
  	return (String) m_systemProperties.getProperty(name);
  }
  
  public PropertyMapIF getSystemProperties() {
  	return m_systemProperties;
  }
  
	/**
	 * Returns the current {@see Locale} instance of this support.
	 */
  public Locale getLocale() {
  	if(m_locale == null)
  		return Locale.getDefault();
  	else
  		return m_locale;
  }
  
  public MumieHelp getHelpSystem() {
  	return m_help;
  }
  
  public DialogManager getDialogManager() {
  	return m_dialogManager;
  }

	public String getMessage(String key) {
		if(key == null)
			return null;
		return (String) m_messages.getProperty(key);
	}
	
	public String getMessage(String key, Object[] parameters) {
		String message = getMessage(key);
		if(message == null)
			return null;
		if(parameters == null)
			return message;
		/*
		 * explanation of choosen regular expression:
		 * ------------------------------------------
		 * "\\$"					'$' character
		 * "([\\d])"		group for one single digit, starting with 1
		*/
		String expression = "\\$([\\d])";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		boolean found = false;
		do {
			Matcher matcher = pattern.matcher(message);
			found = matcher.find();
			if(found) {
				String numberString = matcher.group(1);
				int number = Integer.parseInt(numberString);
				int index = number - 1;
				if(parameters.length < number || parameters[index] == null)
					throw new IllegalArgumentException("Parameter " + number + " (index " + index + ") must not be null !");
				message = matcher.replaceFirst(parameters[index].toString());
			}
		} while(found);
		return message;
	}
	
	public String getBasePath() {
		return getSystemProperty(MATHLET_BASE_PATH_PROPERTY);
	}
	
	public Version getMinJVMVersion() {
		return m_jvmMinVersion;
	}
	
  /**
   * Forwards an error to the internal error handler.
   */
  public void addError(Throwable error) {
  	getErrorHandler().addError(error);
  }
  
  /**
   * Returns the error handler instance associated with this runtime.
   */
  public ErrorHandler getErrorHandler() {
  	if(m_errorHandler == null) {
  		m_errorHandler = new ErrorHandler(getContext());
  	}
  	return m_errorHandler;
  }

  /**
   * Returns whether errors have been reported to the internal error handler.
   */
  public boolean hasErrors() {
		return m_errorHandler != null && m_errorHandler.hasErrors();
	}
	
	public URL getResource(String name) {
		URL result = null;
		if(getBasePath() != null)
			result = getClass().getResource(getBasePath() + "/" + name);
		if(result == null)
			result = getClass().getResource(name);
		return result;
	}
	
	public InputStream getResourceAsStream(String name) {
		InputStream result = null;
		if(getBasePath() != null)
			result = getClass().getResourceAsStream(getBasePath() + "/" + name);
		if(result == null)
			result = getClass().getResourceAsStream(name);
		return result;
	}
	
  /**
   * Returns information about the registered parameters.
   * Each entry consists of 3 information (in this order): name, type, description
   * 
   * @see MathletRuntime#getParameterInfo()
	 * @see java.applet.Applet#getParameterInfo()
   */
	public String[][] getParameterInfo() {
		return m_parameterInfo;
	}
	
	/**
	 * Brings up the dialog with the given ID, executes and returns the selected action.
	 * The parameters' types are dependant of the choosen dialog ID.
	 *  
	 * @param dialogID an ID referencing a dialog
	 * @param params a list of parameters; dependant of the dialog ID; may be <code>null</code>
	 * @return an instance of <code>DialogAction</code> describing the action
	 */
	public DialogAction showDialog(int dialogID, Object[] params) {
		return getDialogManager().showDialog(dialogID, params);
	}
	
  /**
   * Disposes of any resources hold by this runtime.
   */
  public void dispose() {
    LOGGER.log(METHOD_ENTERING_CATEGORY, "entering dispose()...");
    m_locale = null;
    m_help = null;
    m_parameterInfo = null;
    m_messages = null;
    m_complianceLevel = null;
    // clear maps
    if(m_parameters != null) {
    	m_parameters.clear(); 
    	m_parameters = null;
    }
    if(m_allMessages != null) {
    	m_allMessages.clear();
    	m_allMessages = null;
    }
    if(m_extensions != null) {
    	m_extensions.clear();
    	m_extensions = null;
    }
    if(m_registeredParameters != null) {
    	m_registeredParameters.clear();
    	m_registeredParameters = null;
    }
    if(m_screenTypes != null) {
    	m_screenTypes.clear();
    	m_screenTypes = null;
    }
    if(m_systemProperties != null) {
    	m_systemProperties.clear();
    	m_systemProperties = null;
    }
    m_errorHandler = null;
    // destroy "this" runtime
    m_mathlet = null;
    m_instance = null;
  }
}
