/**
 * Copyright 2010-2017 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.interactive_instruments.exceptions.ExcUtils;

/**
 * Classloader utilities
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
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
		}catch (final IOException e) {
			return "";
		}finally {
			IFile.closeQuietly(stream);
		}
	}
}
