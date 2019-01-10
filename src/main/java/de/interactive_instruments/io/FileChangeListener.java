/**
 * Copyright 2017-2019 European Union, interactive instruments GmbH
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
package de.interactive_instruments.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.*;

/**
 * Informs the implementing client about changed files.
 *
 * @author herrmann@interactive-instruments.de.
 */
@FunctionalInterface
public interface FileChangeListener extends Comparable<FileChangeListener> {

	/**
	 * Default fileChanged() delegates the call to the filesChanged() method.
	 *
	 * @param watchableEventMap
	 */
	default void fileChanged(final Map<Path, List<WatchEvent<?>>> watchableEventMap) {
		final Set<Path> dirSet = new TreeSet<>();
		final Map<Path, WatchEvent.Kind> pathEventMap = new TreeMap<>();
		watchableEventMap.entrySet().forEach(wE -> wE.getValue().forEach(event -> {
			if (event.context() instanceof Path) {
				final Path path = wE.getKey().resolve((Path) event.context());
				if (Files.isDirectory(path)) {
					dirSet.add(path);
				} else {
					dirSet.add(path.getParent());
				}
				pathEventMap.put(path, event.kind());
			}
		}));
		filesChanged(pathEventMap, dirSet);
	}

	/**
	 * Informs the observer that files have changed.
	 *
	 * @param eventMap a map containing all full file paths that changed and
	 *                    the corresponding event kinds.
	 * @param dirs a set of the directories that changed or in which files changed.
	 */
	void filesChanged(final Map<Path, WatchEvent.Kind> eventMap, final Set<Path> dirs);

	/**
	 * A FileChangeListener with a higher priority is fired first before a FileChangeListener with
	 * a lower priority.
	 *
	 * @return
	 */
	default int fileChangeNotificationPriority() {
		return 100;
	}

	/**
	 * Filter before event is fired
	 *
	 * @return
	 */
	default MultiFileFilter fileChangePreFilter() {
		return null;
	}

	@Override
	default int compareTo(final FileChangeListener fileChangeListener) {
		final int cmp = -Integer.compare(fileChangeNotificationPriority(),
				fileChangeListener.fileChangeNotificationPriority());
		if (cmp == 0) {
			return this.toString().compareTo(fileChangeListener.toString());
		} else {
			return cmp;
		}
	}
}
