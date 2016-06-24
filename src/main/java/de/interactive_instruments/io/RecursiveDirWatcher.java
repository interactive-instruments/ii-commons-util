/**
 * Copyright 2010-2016 interactive instruments GmbH
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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;

import de.interactive_instruments.Releasable;
import de.interactive_instruments.exceptions.ExcUtils;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class RecursiveDirWatcher implements Releasable {

	private final Path rootDir;
	private final int fireDelay = 4000;
	private final Collection<FileChangeListener> listeners;

	private AtomicBoolean running = new AtomicBoolean(false);

	private final Map<Path, WatchKey> pathWatchKeyMap = new HashMap<>();
	private WatchService watchService = null;
	private Thread watchThread = null;
	private Timer timer = null;

	private Map<Path, List<WatchEvent<?>>> watchableEventMap = new TreeMap<>();

	public static RecursiveDirWatcher create(final Path rootDir, FileChangeListener listener) {
		return new RecursiveDirWatcher(rootDir, new ArrayList<FileChangeListener>() {
			{
				add(listener);
			}
		});
	}

	public static RecursiveDirWatcher create(final Path rootDir, Collection<FileChangeListener> listener) {
		return new RecursiveDirWatcher(rootDir, listener);
	}

	private RecursiveDirWatcher(final Path rootDir, final Collection<FileChangeListener> listeners) {
		if (rootDir == null) {
			throw new IllegalArgumentException("Root directory is null");
		}
		if (!Files.exists(rootDir)) {
			throw new IllegalArgumentException("Root directory '" + rootDir.toAbsolutePath().toString() + "' does not exists");
		}
		if (listeners == null) {
			throw new IllegalArgumentException("Listener list is null");
		}
		this.listeners = listeners;
		this.rootDir = rootDir;
	}

	public void start() throws IOException {
		if (running.get()) {
			throw new IllegalStateException("RecursiveDirWatcher already started!");
		}

		watchService = FileSystems.getDefault().newWatchService();
		watchThread = new Thread(() -> {
			running.set(true);
			registerDirWatchesRecursively();

			while (running.get()) {
				try {
					final WatchKey watchKey = watchService.take();
					final List<WatchEvent<?>> events = watchKey.pollEvents();
					watchKey.reset();
					delayedFire(events, (Path) watchKey.watchable());
				} catch (InterruptedException | ClosedWatchServiceException e) {
					running.set(false);
				}
			}
		}, this.getClass().getSimpleName());

		watchThread.start();
	}

	private synchronized void delayedFire(final List<WatchEvent<?>> events, final Path watchable) {
		if (timer != null) {
			timer.cancel();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ExcUtils.supress(e);
			}
		}

		final List<WatchEvent<?>> entries = watchableEventMap.get(watchable);
		if (entries != null) {
			entries.addAll(events);
		} else {
			watchableEventMap.put(watchable, events);
		}

		timer = new Timer("RecursiveDirWatchDelay");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				registerDirWatchesRecursively();
				unregisterStaleDirWatches();
				final TreeMap<Path, List<WatchEvent<?>>> watchableEventMapCopy = new TreeMap<>(watchableEventMap);
				watchableEventMap.clear();
				listeners.forEach(l -> l.fileChanged(watchableEventMapCopy));
			}
		}, fireDelay);
	}

	private synchronized void registerDirWatchesRecursively() {
		try {
			Files.walkFileTree(rootDir, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
					registerWatch(dir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			ExcUtils.supress(e);
		}
	}

	private synchronized void registerWatch(final Path dir) {
		if (!pathWatchKeyMap.containsKey(dir)) {
			try {
				final WatchKey watchKey = dir.register(watchService,
						OVERFLOW, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				pathWatchKeyMap.put(dir, watchKey);
			} catch (IOException e) {
				ExcUtils.supress(e);
			}
		}
	}

	private synchronized void unregisterStaleDirWatches() {
		pathWatchKeyMap.entrySet().removeIf(e -> {
			if (!Files.exists(e.getKey(), LinkOption.NOFOLLOW_LINKS)) {
				e.getValue().cancel();
				return true;
			}
			return false;
		});
	}

	@Override
	public synchronized void release() {
		if (watchThread != null) {
			try {
				if (timer != null) {
					timer.cancel();
				}
				watchService.close();
				running.set(false);
				watchThread.interrupt();
			} catch (IOException e) {
				ExcUtils.supress(e);
			}
		}
	}

}
