package de.interactive_instruments;

/**
 * An interface for versionable Objects
 * 
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 **
 * @see de.interactive_instruments.Version
 */
public interface Versionable extends Comparable {

	/**
	 * Returns the version of the object.
	 *
	 * @return version as Version object
	 */
	ImmutableVersion getVersion();

	/**
	 * Compares this object with a Versionable or a Version object for order.
	 * Returns a negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 *
	 * @param o the Versionable or Version object to be compared.
	 * @return  a negative integer, zero, or a positive integer as this object
	 *          is less than, equal to, or greater than the specified object.
   */
	@Override default int compareTo(final Object o) {
		if(o instanceof ImmutableVersion) {
			return getVersion().compareTo(((ImmutableVersion)o));
		}
		return getVersion().compareTo(((Versionable)o).getVersion());
	}
}
