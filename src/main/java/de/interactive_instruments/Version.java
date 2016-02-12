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

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * An object for comparing version numbers with respect to the 
 * major, minor and bugfix version number.
 *
 * TODO immutable Version interface
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 */
@XmlRootElement(name="Version", namespace=II_Constants.II_COMMON_UTILS_NS)
public class Version implements Comparable<Version> {

	private int major;
	private int minor;
	private int bugfix;

	public Version() {
        minor=1;
    }

	public Version(int major, int minor, int bugfix) {
		this.major=major;
		this.minor=minor;
		this.bugfix=bugfix;
	}
	
	/**
	 * Parse a version String and construct a new Version
	 * object
	 * @param version
	 * @throws IllegalArgumentException
	 */
	public Version(String version) throws IllegalArgumentException {
		set(version);
	}
	
	/**
	 * Parse a version String and return a new Version object
	 * @param version
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static Version parse(String version) throws IllegalArgumentException {
		return new Version(version);
	}
	
	/**
	 * Parse and set Version form string
	 * @param version
	 * @throws IllegalArgumentException 
	 */
	public void set(String version) throws IllegalArgumentException {
		// Split into x.x.x.x and ignore the last .
		final String[] splittedVersion = version.split("\\.", 4);
		if(splittedVersion.length>=2) {
            int failPos=0;
			try{
                failPos=splittedVersion[0].length();
				major=Integer.parseInt(splittedVersion[0]);
                failPos+=splittedVersion[1].length();
				minor=Integer.parseInt(splittedVersion[1]);
				if(splittedVersion.length==3) {
						// x.x.x
                        failPos+=splittedVersion[2].length();
						bugfix=Integer.parseInt(splittedVersion[2]);
				}
			}catch(NumberFormatException e) {
				throw new IllegalArgumentException("Unparseable Version: \""+version+"\" Pos:"+failPos);
			}
		}else{
			throw new IllegalArgumentException("Unparseable Version: \""+version+"\"");
		}
	}
	

	/**
	 * Compare a version 
	 * @param version the version to be compared
	 * @return a negative integer, zero, or a positive integer as this 
	 * object is less than, equal to, or greater than the specified version.
	 */
	@Override
	public int compareTo(Version version) {
	    if(version==null) {
	    	return 1;
	    }
		if(this.major<version.major || 
				this.minor<version.minor || 
				this.bugfix<version.bugfix)
		{
			return -1;
		}else if(this.major>version.major || 
					this.minor>version.minor || 
						this.bugfix>version.bugfix)
		{
			return 1;
		}
		return 0;
	}
	
	/**
	 * Returns true if the version is equal to the compared version
	 * @param version the version to be compared
	 * @return
	 */
	@Override
	public boolean equals(Object version) {
		if(!(version instanceof Version)) {
			return false;
		}
		final Version v = (Version) version;
		return this.major==v.major &&
				this.minor==v.minor &&
					this.bugfix==v.bugfix; 
	}

    @XmlValue
    String getString() {
        return toString();
    }

	void setString(String ver) throws IllegalArgumentException {
		set(ver);
	}

    @Override
	public String toString() {
		return this.major+"."+this.minor+"."+this.bugfix;
	}

	@Override public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Increments the bugfix version number
	 */
	public Version incBugfix() {
		this.bugfix++;
		return this;
	}
		
}
