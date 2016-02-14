package de.interactive_instruments;

import de.interactive_instruments.properties.ConfigPropertyHolder;

/**
 * An interface for objects that possess properties that can be used for configuration.
 *
 * NOTE: configure the properties before calling the init() method.
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public interface Configurable extends Initializable {

  /**
   * Gets the configurable properties
   *
   * NOTE: The implementing class can clear the properties after init() has been called.
   *
   * @return ConfigPropertyHolder
   */
  ConfigPropertyHolder getConfigurationProperties();
}
