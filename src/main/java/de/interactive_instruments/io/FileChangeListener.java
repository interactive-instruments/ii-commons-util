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
