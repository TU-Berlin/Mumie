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

package net.mumie.mathletfactory.transformer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.mumie.mathletfactory.display.Drawable;
import net.mumie.mathletfactory.math.number.NumberTypeDependentIF;
import net.mumie.mathletfactory.mmobject.MMObjectIF;
import net.mumie.mathletfactory.util.logging.LogCategory;
import net.mumie.mathletfactory.util.logging.MumieLogger;
import net.mumie.mathletfactory.util.property.DefaultPropertyMap;
import net.mumie.mathletfactory.util.property.Property;
import net.mumie.mathletfactory.util.property.PropertyMapIF;

/**
 * The <code>GeneralTransformer</code> is the abstract super class for all
 * transformer classes - any {@link net.mumie.mathletfactory.mmobject.MMObjectIF} will use
 * at least one instance of a <code>transformer</code> (either directly or provided
 * by a helper class (see {@link net.mumie.mathletfactory.mmobject.VisualizeSupport}) and
 * the object's transformer performs the necessary stuff to derive and permanently adjust
 * (one of) the object's
 * {@link net.mumie.mathletfactory.Drawable}.
 * <br>
 * The <code>GeneralTransformer</code> mainly provides
 * Due to the visualisation scheme within the <b>AppletFactory</b>
 *
 * @author vossbeck, gronau
 * @mm.docstatus finished
 */
public abstract class GeneralTransformer implements TransformTypes {

	/** This constant describes an unknown screen type which was not yet registered. */
	public final static int ST_UNKNOWN = -1;
	
	/**
	 * Use this "screen type" to display
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s
	 * in their own container drawable rather than to be draw them within a
	 * {@link net.mumie.mathletfactory.display.MMCanvas}.
	 * <br>
	 * For example, this option is preferable when you want to render a
	 * {@link net.mumie.mathletfactory.mmobject.algebra.MMDefaultR2Vector} as its
	 * component tuple rather than as arrow.
	 */
	public static final int ST_NO_CANVAS = 1;

	/**
	 * Use this screen type to render
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s within a
	 * {@link net.mumie.mathletfactory.display.g2d.MMG2DCanvas}
	 */
	public static final int ST_GRAPHICS2D = 21;
	
	/**
	 * Use this screen type to display
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s within a
	 * {@link net.mumie.mathletfactory.display.j3d.MMJ3DCanvas}.
	 */
	public static final int ST_J3D = 31;
	
	/**
	 * Use this screen type to display
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}s within a
	 * {@link net.mumie.mathletfactory.display.jr3d.MMJR3DCanvas}.
	 */
	public static final int ST_JR3D = 32;

//	private final static String TRANSFORMER_FILES_DIRECTORY = "/resource/settings/transformer/";
//	
//	private final static String NOC_TRANSFORMER_FILE = "transformer.properties.noc";
//	
//	private final static String G2D_TRANSFORMER_FILE = "transformer.properties.g2d";
//	
//	private final static String J3D_TRANSFORMER_FILE = "transformer.properties.j3d";
//	
//	private final static String JR3D_TRANSFORMER_FILE = "transformer.properties.jr3d";
	
//	private static int count = 0;
//	private static Logger logger = Logger.getLogger(GeneralTransformer.class.getName());
	private final static MumieLogger LOGGER = MumieLogger.getLogger(GeneralTransformer.class);
	private final static LogCategory TRANSFORMER_LOAD_CATEGORY = LOGGER.getCategory("transformer.transformers");
	private final static LogCategory TRANSFORM_TYPE_CATEGORY = LOGGER.getCategory("transformer.transform-types");
	private final static LogCategory RENDERING_HINTS_CATEGORY = LOGGER.getCategory("display.rendering-hints");

	/** The master {@link MMObjectIF MM-object}</code> of this <code>GeneralTransformer</code> instance. */
	protected MMObjectIF m_masterMMObject;

	protected boolean m_isInitialized = false;
	
	/** Field holding the screen type of this transformer. */
	private int m_screenType;
	
	/** Field holding the transform type of this transformer. */
	private int m_transformType;

//	/** Flag indicating that container object transformer bindings were loaded. */
//	private static boolean m_noc_registered = false;
//
//	/** Flag indicating that 2D canvas object transformer bindings were loaded. */
//	private static boolean m_g2d_registered = false;
//
//	/** Flag indicating that 3D (Java3D) canvas object transformer bindings were loaded. */
//	private static boolean m_j3d_registered = false;
//	
//	/** Flag indicating that 3D (JReality) canvas object transformer bindings were loaded. */
//	private static boolean m_jr3d_registered = false;

	/** Map holding for every {@link TransformerKey} the class of the corresponding transformer. */
	private static final HashMap m_transformerHashMap = new HashMap();
	
	/** Map holding for every transformer class the corresponding integer value. */
	private static final HashMap m_transformerToTransformType = new HashMap();
	
	/** Map holding for every screen type name the corresponding integer value. */
	private static final HashMap m_screenTypes = new HashMap();
	
	/** Map holding for every transform type name the corresponding integer value. */
	private static final HashMap m_transformTypes = new HashMap();
	
	/** Map holding for every transformer class name a property map conaining the corresponding rendering hints. */
	private static HashMap m_renderingHints = new HashMap();
	
	private static int m_lastScreenTypeID = -1;
	private static int m_lastTransformTypeID = -1;
	
	static {
		try {
			// register predefined transform types
			Field[] fields = TransformTypes.class.getDeclaredFields();
			for(int i = 0; i < fields.length; i++) {
				addTransformType(fields[i].getName(), fields[i].getInt(null));
			}
			// register predefined screen types
			addScreenType("ST_NO_CANVAS", ST_NO_CANVAS);
			addScreenType("ST_GRAPHICS2D", ST_GRAPHICS2D);
			addScreenType("ST_J3D", ST_J3D);
			addScreenType("ST_JR3D", ST_JR3D);
		} catch(Exception e) {
			System.err.println("Exception occurred while loading predefined transform types:");
			e.printStackTrace();
		}
	}

//	public static void setTransformerFileName(String aName) {
//		m_transformerHashMap.clear();
//		fillHashMapFromFile(aName);
//	}

//	private static void fillHashFor(int screenType) {
//		if (screenType == ST_NO_CANVAS) {
//			if (!m_noc_registered) {
//				fillHashMapFromFile(TRANSFORMER_FILES_DIRECTORY + NOC_TRANSFORMER_FILE);
//				m_noc_registered = true;
//			}
//		}
//		else if (screenType == ST_GRAPHICS2D) {
//			if (!m_g2d_registered) {
//				fillHashMapFromFile(TRANSFORMER_FILES_DIRECTORY + G2D_TRANSFORMER_FILE);
//				m_g2d_registered = true;
//			}
//		}
//		else if (screenType == ST_J3D) {
//			if (!m_j3d_registered) {
//				fillHashMapFromFile(TRANSFORMER_FILES_DIRECTORY + J3D_TRANSFORMER_FILE);
//				m_j3d_registered = true;
//			}
//		} else if (screenType == ST_JR3D) {
//			if (!m_jr3d_registered) {
//				fillHashMapFromFile(TRANSFORMER_FILES_DIRECTORY + JR3D_TRANSFORMER_FILE);
//				m_jr3d_registered = true;
//			}
//		}
//		else {
//			if (logger.isLoggable(java.util.logging.Level.SEVERE))
//				logger.severe(
//					"tried to fill hash from non existing transformer.properties file"
//						+ "for screenType: "
//						+ screenType);
//		}
//	}

//	private static void fillHashMapFromFile(String fileName) {
//		count++;
////		if (logger.isLoggable(java.util.logging.Level.INFO)) {
////			logger.info(
////				"\n fillHashMapFromFile: static method called "
////					+ count
////					+ ". time, reading from file "
////					+ fileName);
////		}
//		Properties prop = new Properties();
//		try {
//			InputStream resource =
//				GeneralTransformer.class.getResourceAsStream(fileName);
//			if (resource == null) {
//				log(new Exception("resource: " + fileName + " not found"));
//			}
//			else {
//				prop.load(resource);
//				for (Enumeration enumeration = prop.propertyNames();
//        enumeration.hasMoreElements();
//					) {
//					String key = (String) enumeration.nextElement();
//					StringTokenizer tokenizer = new StringTokenizer(key, "#");
//					String transformType = tokenizer.nextToken();
//					String screenType = tokenizer.nextToken();
//					String objectName = tokenizer.nextToken();
//					String transformerName = prop.getProperty(key);
//					try {
//						Field transformField =
//							TransformTypes.class.getDeclaredField(transformType);
//						Field screenField =
//							GeneralTransformer.class.getDeclaredField(screenType);
//						register(
//							Class.forName(transformerName),
//							transformField.getInt(null),
//							screenField.getInt(null),
//							Class.forName(objectName));
//					}
//					catch (NoSuchFieldException e) {
//						log(e);
//					}
//					catch (ClassNotFoundException e) {
//						log(e);
//					}
//					catch (IllegalAccessException e) {
//						log(e);
//					}
//				}
//			}
//		}
//		catch (IOException e) {
//			log(e);
//		}
//	}

	/**
	 * This method is used to register a specific <code>GeneralTransformer</code>
	 * that can be later retrieved by the method {@link #getTransformer
	 * getTransformer()}. <br>
	 *
	 * We have a one-to-one correspondence <br>
	 *   (transformType,screenType,mathEntityClass) ---&gt; transformerClass, <br>
	 *
	 * To a given
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF} (which in fact is a
	 * representation of a mathematical object), a desired <b>transformation type</b> and a
	 * desired <b>screen type</b> you will (uniquely) assign a specific implementation
	 * of <code>GeneralTransformer</code>.
	 * transformation type (e.g. AFFINE2D_DEFAULT_TRANSFORM) and a given screen
	 * type (e.g. GRAPHICS2D) there exists a unique transformer class (here
	 * {@link net.mumie.mathletfactory.transformer.Affine2DPointTransformer}).
	 * <p>
	 * The possible values for <code>transformType</code> and
	 * <code>screenType</code> exist as public static fields in
	 * {@link GeneralTransformer}.
	 *
	 * @param    transformerClass    a  Class
	 * @param    transformType       an int
	 * @param    screenType          an int
	 * @param    mathEntityClass     a  Class
	 *
	 * @version  8/14/2002
	 */
	public static void register(Class transformerClass, int transformType, int screenType, Class mathEntityClass) {
		if (GeneralTransformer.class.isAssignableFrom(transformerClass)
			&& NumberTypeDependentIF.class.isAssignableFrom(mathEntityClass)) {
			m_transformerHashMap.put(
				new TransformerKey(transformType, screenType, mathEntityClass),
				transformerClass);
			m_transformerToTransformType.put(
				transformerClass,
				new Integer(transformType));
			LOGGER.log(TRANSFORMER_LOAD_CATEGORY, 
					"registering transformer " + transformerClass.getName() + " for " + "tt:" + transformType+ ", st:" + screenType
						+ ", mmobject:" + mathEntityClass.getName());
		}
		else
			throw new IllegalArgumentException(
				transformerClass
					+ " is not a assignable to "
					+ CanvasObjectTransformer.class);
	}
	
	public static int addTransformType(String transformTypeName) {
		return addTransformType(transformTypeName, ++m_lastTransformTypeID);
	}
	
	private static int addTransformType(String transformTypeName, int transformType) {
		int tt = getTransformType(transformTypeName);
//		if(tt != NO_TRANSFORM_TYPE && tt != transformType)
//			throw new IllegalArgumentException("Transform type \"" + transformTypeName + "\" was already registered with value \"" + tt + "\"");
		if(tt != NO_TRANSFORM_TYPE)
			return tt;
		LOGGER.log(TRANSFORM_TYPE_CATEGORY, "Adding transform type \"" + transformTypeName + "\" for value \"" + transformType + "\"");
		m_transformTypes.put(transformTypeName, new Integer(transformType));
		if(transformType > m_lastTransformTypeID)
			m_lastTransformTypeID = transformType;
		return transformType;
	}
	
	public static int getTransformType(String transformTypeName) {
		Integer transformType = (Integer) m_transformTypes.get(transformTypeName);
		return (transformType == null) ? NO_TRANSFORM_TYPE : transformType.intValue();
	}
	
	private static String getTransformTypeName(int transformType) {
		Set transformTypeNames = m_transformTypes.keySet();
		for(Iterator i = transformTypeNames.iterator(); i.hasNext(); ) {
			String ttName = (String) i.next();
			int tt = getTransformType(ttName);
			if(tt == transformType)
				return ttName;
		}
		return null;
	}
	
	public static int addScreenType(String screenTypeName) {
		return addScreenType(screenTypeName, ++m_lastScreenTypeID);
	}
	
	private static int addScreenType(String screenTypeName, int screenType) {
		int st = getScreenType(screenTypeName);
//		if(st != ST_UNKNOWN && st != screenType)
//			throw new IllegalArgumentException("Screen type \"" + screenTypeName + "\" was already registered with value \"" + st + "\"");
		if(st != ST_UNKNOWN)
			return st;
		m_screenTypes.put(screenTypeName, new Integer(screenType));
		if(screenType > m_lastScreenTypeID)
			m_lastScreenTypeID = screenType;
		return screenType;
	}
	
	public static int getScreenType(String screenTypeName) {
		Integer screenType = (Integer) m_screenTypes.get(screenTypeName);
		return (screenType == null) ? ST_UNKNOWN : screenType.intValue();
	}
	
	private static String getScreenTypeName(int screenType) {
		Set screenTypeNames = m_screenTypes.keySet();
		for(Iterator i = screenTypeNames.iterator(); i.hasNext(); ) {
			String stName = (String) i.next();
			int st = getScreenType(stName);
			if(st == screenType)
				return stName;
		}
		return null;
	}
	
	public static void addRenderingHints(String transformer, int screenType, PropertyMapIF hints) {
		LOGGER.log(RENDERING_HINTS_CATEGORY, "Adding rendering hints for drawables of transformer " + transformer);
		PropertyMapIF map = getRenderingHints(transformer, screenType);
		if(map == null)
			m_renderingHints.put(createRenderingHintsKey(transformer, screenType), hints);
		else
			map.copyPropertiesFrom(hints);
	}
	
	public static PropertyMapIF getRenderingHints(String transformer, int screenType) {
		DefaultPropertyMap propMap = (DefaultPropertyMap) m_renderingHints.get(createRenderingHintsKey(transformer, screenType));
		return propMap;
	}
	
	private static String createRenderingHintsKey(String transformer, int screenType) {
		return transformer + ";ST:" + screenType;
	}
	
	/**
	 * This method is used to retrieve a specific transformer object with respect
	 * to the mathematical object and in dependence of the transform and
	 * screen type.
	 *
	 * This method is typically called from within instances of
	 * {@link net.mumie.mathletfactory.mmobject.MMObjectIF}.
	 *
	 * @param    transformType       an int
	 * @param    screenType          an int
	 * @param    mathEntityClass     a  Class
	 *
	 * @return   a GeneralTransformer
	 */
	public static GeneralTransformer getTransformer(int transformType, int screenType, MMObjectIF mathEntity) {
		if (transformType == GeneralTransformer.NO_TRANSFORM_TYPE)
			return null;
//		fillHashFor(screenType);
		GeneralTransformer t;
		TransformerKey tk = new TransformerKey(transformType, screenType, mathEntity.getClass());
		Class c = (Class) m_transformerHashMap.get(tk);
		if(c == null)
			throw new IllegalArgumentException(
						"Cannot find transformer for \""+mathEntity.getClass().getName() 
					+ "\" with transform type \"" + getTransformTypeName(transformType) + "\" (" + transformType + ")"
					+", screen type \"" + getScreenTypeName(screenType) + "\" (" + screenType + ")"
			);
		t = createInstance(c);
		t.setTransformType(transformType);
		t.setScreenType(screenType);
		t.initialize(mathEntity);
//			if (logger.isLoggable(java.util.logging.Level.FINE))
//				logger.fine(
//					"returning "
//						+ t.getClass().getName()
//						+ " for "
//						+ mathEntity.getClass().getName());
			return t;
	}
	
	/**
	 * Creates and returns safely an instance of the given transformer class.
	 */
	private static GeneralTransformer createInstance(Class transformerClass) {
		try {
			return (GeneralTransformer) transformerClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the transform type belonging to <code>aTransformer</code>.
	 *
	 * @param <code>aTransformer</code> is the instance of a specific <code>
	 * GeneralTransformer</code>, whose transform type shall be found out.
	 * @return
	 */
	public static int getTransformType(GeneralTransformer aTransformer) {
		if (aTransformer != null) {
			Integer I =
				(Integer) m_transformerToTransformType.get(aTransformer.getClass());
			if(I != null)
				return I.intValue();
			else
				return NO_TRANSFORM_TYPE;
		}
		else
			return NO_TRANSFORM_TYPE;
	}

	/**
	 * This method sets up the master object for this instance of transformer.
	 * The master object is always an instance of <code>MMObjectIF</code>.
	 *
	 * @param    masterObject        a  MMObjectIF
	 *
	 * @version  8/14/2002
	 */
	public void initialize(MMObjectIF masterObject) {
		m_masterMMObject = masterObject;
		m_isInitialized = true;
	}

	public boolean isInitialized() {
		return m_isInitialized;
	}
	
	/* Sets the screen type. */
	private void setScreenType(int screenType) {
		m_screenType = screenType;
	}
	
	/**
	 * Returns the screen type of this transformer.
	 * @see TransformTypes
	 */
	public int getScreenType() {
		return m_screenType;
	}

	/* Sets the transform type. */
	private void setTransformType(int transformType) {
		m_transformType = transformType;
	}
	
	/**
	 * Returns the screen type of this transformer.
	 * @see TransformTypes
	 */
	public int getTransformType() {
		return m_transformType;
	}

	public abstract void render();

	public abstract void draw();

	public MMObjectIF getMaster() {
		return m_masterMMObject;
	}

	public abstract Drawable getActiveDrawable();
	
	protected void initializeDrawables(Drawable[] drawables) {
		if(drawables == null)
			return;
		PropertyMapIF hints = getRenderingHints();
		if(hints.getPropertiesCount() == 0)
			return;
		if(LOGGER.isActiveCategory(RENDERING_HINTS_CATEGORY)) {
			LOGGER.log(RENDERING_HINTS_CATEGORY, "Collecting compliance settings:");
			Property[] props = hints.getProperties();
			for(int i = 0; i < props.length; i++) {
				LOGGER.log(RENDERING_HINTS_CATEGORY, "Property #" + i + ": " + props[i]);
			}
		}
		for(int i = 0; i < drawables.length; i++) {
			LOGGER.log(RENDERING_HINTS_CATEGORY, "Copying compliance settings to drawable " + drawables[i].getClass().getName());
			drawables[i].getDrawableProperties().copyPropertiesFrom(hints);
		}
	}
	
	/**
	 * Returns a property map with all rendering hints for this transformer instance, or <code>null</code> if
	 * no rendering hints were registered.
	 */
	public PropertyMapIF getRenderingHints() {
		return getRenderingHintsRecursive(this.getClass(), getScreenType());
	}
	
	/**
	 * Returns a property map with all rendering hints for this transformer and all of its parent classes.
	 * If no rendering hints are found, an empty map will be returned.
	 */
	private PropertyMapIF getRenderingHintsRecursive(Class trafoClass, int screenType) {
		PropertyMapIF hints = new DefaultPropertyMap();
		PropertyMapIF classHints = getRenderingHints(trafoClass.getName(), screenType);
		if(classHints != null)
			hints.copyPropertiesFrom(classHints);
		if(trafoClass.getSuperclass() != null && !trafoClass.equals(GeneralTransformer.class)) {
			PropertyMapIF parentHints = getRenderingHintsRecursive(trafoClass.getSuperclass(), screenType);
			if(parentHints != null)
				hints.copyPropertiesFrom(parentHints);
		}
		return hints;
	}
	
	/**
	 * This class defines the key object used in the <code>HashMap</code>
	 * {@link #m_transformers}. The <code>TransformerKey</code> encapsulates
	 * the two integer variables <code>m_transformType</code> and <code>
	 * m_screenType</code> and the class type of the mathematical entities
	 * of our library. The methods {@link java.lang.Object#equals} and
	 * {@link java.lang.Object#hashCode} are overwritten appropirately.
	 */
	private static class TransformerKey {
		private int m_transformType;
		private int m_screenType;
		private Class m_mathEntityClass;

		public TransformerKey(
			int transformType,
			int screenType,
			Class mathEntityClass) {
			m_transformType = transformType;
			m_screenType = screenType;
			m_mathEntityClass = mathEntityClass;
		}

		public boolean equals(Object obj) {
			TransformerKey key = (TransformerKey) obj;
			return (key.m_mathEntityClass.equals(m_mathEntityClass))
				&& (key.m_screenType == m_screenType)
				&& (key.m_transformType == m_transformType);
		}

		public int hashCode() {
			return (int) Math.pow(m_transformType, m_screenType);
		}
	}
}
