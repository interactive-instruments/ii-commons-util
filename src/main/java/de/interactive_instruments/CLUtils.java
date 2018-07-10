/**
 * Copyright 2017-2018 European Union, interactive instruments GmbH
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * This work was supported by the EU Interoperability Solutions for
 * European Public Administrations Programme (http://ec.europa.eu/isa)
 * through Action 1.17: A Reusable INSPIRE Reference Platform (ARE3NA).
 */
package de.interactive_instruments;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.interactive_instruments.exceptions.ExcUtils;

/**
 * Classloader utilities
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 *
 */
public final class CLUtils {

	private CLUtils() {

	}

	public static List<Class> getLoadedClasses(final ClassLoader classLoader)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		final Field f = ClassLoader.class.getDeclaredField("classes");
		f.setAccessible(true);
		return new ArrayList<>(((Vector<Class>) f.get(classLoader)));
	}

	/**
	 * Logs all loaded classes of a ClassLoader. Uses the info level.
	 *
	 * @param classLoader
	 */
	public static void logLoadedClasses(final ClassLoader classLoader) {
		try {
			final Logger logger = LoggerFactory.getLogger(CLUtils.class);
			logger.info("Classes loaded by {} ({}) ", classLoader.getClass().getSimpleName(), classLoader.hashCode());
			for (final Class c : getLoadedClasses(classLoader)) {
				final CodeSource cs = c.getProtectionDomain().getCodeSource();
				if (cs != null && cs.getLocation() != null && cs.getLocation().getFile() != null) {
					logger.info(" {} <- {} ", c.getCanonicalName(),
							c.getProtectionDomain().getCodeSource().getLocation().getFile());
				} else {
					logger.info(c.getCanonicalName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 	Workaround for Windows: close all jar file handles
	 *
	 * 	@param classLoader any URLClassLoader
	 */
	public static void forceCloseUcp(final URLClassLoader classLoader) {
		if (classLoader != null) {
			try {
				classLoader.close();
			} catch (final IOException ignore) {
				ExcUtils.suppress(ignore);
			}
			try {
				final Class<? extends URLClassLoader> clazz = classLoader.getClass();
				final Field ucp = clazz.getDeclaredField("ucp");
				ucp.setAccessible(true);
				final Object sunMiscURLClassPath = ucp.get(classLoader);
				final Field loaders = sunMiscURLClassPath.getClass().getDeclaredField("loaders");
				loaders.setAccessible(true);
				final Collection<?> collection = (Collection<?>) loaders.get(sunMiscURLClassPath);
				for (final Object sunMiscURLClassPathJarLoader : collection.toArray()) {
					try {
						final Field loader = sunMiscURLClassPathJarLoader.getClass().getDeclaredField("jar");
						loader.setAccessible(true);
						final Object jarFile = loader.get(sunMiscURLClassPathJarLoader);
						((JarFile) jarFile).close();
					} catch (Throwable ignore) {
						ExcUtils.suppress(ignore);
					}
				}
			} catch (Throwable ignore) {
				ExcUtils.suppress(ignore);
			}
		}
	}

	public static String getImplVersionOrDefault(final Class clasz, String defaultValue) {
		final String v = clasz.getPackage().getImplementationVersion();
		if (!SUtils.isNullOrEmpty(v)) {
			return v;
		}
		return defaultValue;
	}

	public static String getManifestAttributeValue(final Class clasz, final String value) {
		final String className = clasz.getSimpleName() + ".class";
		final String classPath = clasz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
			return "";
		}
		final String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
				"/META-INF/MANIFEST.MF";
		InputStream stream = null;
		try {
			stream = new URL(manifestPath).openStream();
			final Manifest manifest = new Manifest(stream);
			return manifest.getMainAttributes().getValue(value);
		} catch (final IOException e) {
			return "";
		} finally {
			IoUtils.closeQuietly(stream);
		}
	}
}
