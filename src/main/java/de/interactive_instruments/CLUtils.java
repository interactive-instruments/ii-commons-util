/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments;

import java.lang.reflect.Field;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Classloader utilities
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
public final class CLUtils {

	private CLUtils() {

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Class> getLoadedClasses(final ClassLoader classLoader) throws 
		NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
            final Field f = ClassLoader.class.getDeclaredField("classes");
            f.setAccessible(true);
            return new ArrayList<>(((Vector<Class>) f.get(classLoader)));
	}
	
	@SuppressWarnings("rawtypes")
	public static void printLoadedClasses(final ClassLoader classLoader) 
	{
		try{
			for(final Class c : getLoadedClasses(classLoader)) {
				final CodeSource cs = c.getProtectionDomain().getCodeSource();
				if(cs!=null && cs.getLocation()!=null && cs.getLocation().getFile()!=null) {
					System.out.println(c.getCanonicalName() +" <- "+ c.getProtectionDomain().getCodeSource().getLocation().getFile());
				}else{
					System.out.println(c.getCanonicalName());
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String getImplVersionOrDefault(Class clasz, String defaultValue) {
		final String v = clasz.getPackage().getImplementationVersion();
		if(!SUtils.isNullOrEmpty(v)) {
			return v;
		}
		return defaultValue;
	}
}
