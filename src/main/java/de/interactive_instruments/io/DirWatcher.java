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

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.interactive_instruments.Releasable;
import de.interactive_instruments.exceptions.ExcUtils;

/**
 * Implements the Observer pattern and notifies clients about file changes.
 * Singleton, registered listeners can be prioritized (see FileChangeListener).
 *
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public final class DirWatcher {

	private final static Logger logger = LoggerFactory.getLogger(DirWatcher.class);

	private final static int fireDelay = 4321;
	// private final Collection<FileChangeListener> listeners;
	// private final PathFilter filter;

	private AtomicBoolean running = new AtomicBoolean(false);

	private WatchService watchService = null;
	private Thread watchThread = null;
	private Timer timer = null;

	private final static MultiFileFilter defaultFileIgnoreFilter = new DefaultFileIgnoreFilter();

	private static class WatchKeyListeners implements Releasable {
		private final TreeSet<FileChangeListener> listeners;
		private final WatchKey watchKey;

		public WatchKeyListeners(final Path rootDir, final Collection<FileChangeListener> listeners,
				final WatchKey watchKey) {
			this.listeners = new TreeSet<>(listeners);
			this.watchKey = watchKey;
		}

		public TreeSet<FileChangeListener> getListeners() {
			return listeners;
		}

		public void addAll(final Collection<FileChangeListener> listeners) {
			this.listeners.addAll(listeners);
		}

		public void add(final FileChangeListener listener) {
			listeners.add(listener);
		}

		public int size() {
			return listeners.size();
		}

		public void remove(final FileChangeListener listener) {
			listeners.remove(listener);
		}

		@Override
		public void release() {
			if (watchKey != null) {
				watchKey.cancel();
			}
		}

		public boolean releaseStaleDir() {
			if (watchKey != null && watchKey.watchable() instanceof Path) {
				if (Files.exists((Path) watchKey.watchable(), LinkOption.NOFOLLOW_LINKS)) {
					return false;
				} else {
					release();
					return false;
				}
			}
			// Not sure if this may happen?
			return true;
		}
	}

	private static class ListenerRegistry implements Releasable {
		// Listeners observing root directories
		private final TreeMap<FileChangeListener, Set<Path>> registeredListenersForRootDirs = new TreeMap<>();
		// All observed sub-directories
		private final TreeMap<Path, WatchKeyListeners> watchedSubDirs = new TreeMap<>();

		private final Lock listenerLock = new ReentrantLock();

		private final WatchService watchService;

		private ListenerRegistry(final WatchService watchService) {
			this.watchService = watchService;
		}

		private void registerListeners(final Collection<FileChangeListener> listeners, final Path dir) {
			listenerLock.lock();
			// Check if dir is already watched by other listeners
			final WatchKeyListeners watchKeyListeners = watchedSubDirs.get(dir);
			if (watchKeyListeners != null) {
				watchKeyListeners.addAll(listeners);
			} else {
				final WatchKey watchKey;
				try {
					watchKey = dir.register(watchService,
							OVERFLOW, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				} catch (IOException e) {
					logger.error("Cannot watch directory", e);
					return;
				}
				watchedSubDirs.put(dir, new WatchKeyListeners(dir, listeners, watchKey));
			}
			for (final FileChangeListener listener : listeners) {
				final Set<Path> paths = registeredListenersForRootDirs.get(listener);
				if (paths != null) {
					paths.add(dir);
				} else {
					registeredListenersForRootDirs.put(listener, new TreeSet<Path>() {
						{
							add(dir);
						}
					});
				}
			}
			listenerLock.unlock();
		}

		private void unregisterListeners(final Collection<FileChangeListener> listeners) {
			for (final FileChangeListener listener : listeners) {
				listenerLock.lock();
				final Set<Path> dirs = registeredListenersForRootDirs.get(listener);
				if (dirs != null) {
					for (final Path dir : dirs) {
						// Get the watched directory
						final WatchKeyListeners watchKeyListeners = watchedSubDirs.get(dir);
						if (watchKeyListeners != null) {
							if (watchKeyListeners.size() > 1) {
								// The directory is observed by other listeners, just remove this listener
								watchKeyListeners.remove(listener);
							} else {
								// There is only one listener, cancel the observing and
								// remove the directory from the list
								watchKeyListeners.release();
								watchedSubDirs.remove(dir);
							}
						}
					}
					// Unregister listener
					registeredListenersForRootDirs.remove(listener);
				}
				listenerLock.unlock();
			}
		}

		private void unregisterStaleDirWatches() {
			watchedSubDirs.values().removeIf(e -> e.releaseStaleDir());
		}

		private void ensureDirectoryObserved(final Path dir, final Collection<FileChangeListener> listeners) {
			// not locked !
			for (final FileChangeListener listener : listeners) {
				final MultiFileFilter filter = listener.filter();
				if (filter == null || filter.accept(dir)) {
					final WatchKeyListeners watchKeyListeners = watchedSubDirs.get(dir);
					if (watchKeyListeners != null) {
						watchKeyListeners.add(listener);
					} else {
						try {
							final WatchKey watchKey = dir.register(watchService,
									OVERFLOW, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
							watchedSubDirs.put(dir, new WatchKeyListeners(dir, listeners, watchKey));
						} catch (IOException e) {
							logger.error("Cannot watch directory", e);
							return;
						}
					}
				}
			}
		}

		private void registerDirWatchesRecursively() {
			// Ensure that each FileChangeListener is notified when a subdirectory gets changed
			// groupd the listeners by their root directory
			final Map<Path, TreeSet<FileChangeListener>> groupedListeners = new TreeMap<>();
			for (final Map.Entry<FileChangeListener, Set<Path>> fileChangeListenerSetEntry : registeredListenersForRootDirs.entrySet()) {
				final Set<Path> paths = fileChangeListenerSetEntry.getValue();
				for (final Path path : paths) {
					final TreeSet<FileChangeListener> ls = groupedListeners.get(path);
					final FileChangeListener listener = fileChangeListenerSetEntry.getKey();
					if (ls == null) {
						groupedListeners.put(path, new TreeSet<FileChangeListener>() {
							{
								add(listener);
							}
						});
					} else {
						ls.add(listener);
					}
				}
			}

			for (final Map.Entry<Path, TreeSet<FileChangeListener>> groupedListenersEntry : groupedListeners.entrySet()) {
				final TreeSet<FileChangeListener> listeners = groupedListenersEntry.getValue();
				final Path path = groupedListenersEntry.getKey();
				try {
					ensureDirectoryObserved(path, listeners);
					Files.walkFileTree(path, new FileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
							if (defaultFileIgnoreFilter.accept(dir)) {
								ensureDirectoryObserved(dir, listeners);
							}
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
					ExcUtils.suppress(e);
				}
			}
		}

		private void updateDirectories() {
			listenerLock.lock();
			try {
				registerDirWatchesRecursively();
				unregisterStaleDirWatches();
			} catch (Exception ign) {
				ExcUtils.suppress(ign);
			} finally {
				listenerLock.unlock();
			}
		}

		public TreeSet<FileChangeListener> getListeners(final Set<Path> paths) {
			final TreeSet<FileChangeListener> res = new TreeSet<>();
			for (final Path path : paths) {
				final WatchKeyListeners l = watchedSubDirs.get(path);
				if (l != null) {
					res.addAll(l.getListeners());
				}
			}
			return res;
		}

		@Override
		public void release() {
			watchedSubDirs.values().forEach(e -> e.release());
		}
	}

	private final ListenerRegistry listenerRegistry = new ListenerRegistry(watchService);

	// Note that the types are important for the map (priority notification)
	// Directories observer by listeners
	// /// private final TreeMap<Path, TreeSet<FileChangeListener>> watchedDirs = new TreeMap<>();
	// Events fired for a path
	private final TreeMap<Path, List<WatchEvent<?>>> watchedEvents = new TreeMap<>();

	// Non-fair locking!
	private final Lock processEventLock = new ReentrantLock();

	private static class DelWatchEvent implements WatchEvent {
		private final Path watchable;

		public DelWatchEvent(final Path watchable) {
			this.watchable = watchable;
		}

		@Override
		public Kind kind() {
			return ENTRY_DELETE;
		}

		@Override
		public int count() {
			return 1;
		}

		@Override
		public Object context() {
			return watchable;
		}
	}

	private static final class InstanceHolder {
		static final DirWatcher INSTANCE = new DirWatcher();
	}

	public static void register(final Path rootDir, final FileChangeListener listener) {
		register(rootDir, Collections.singletonList(listener));
	}

	public static void register(final Path rootDir, final Collection<FileChangeListener> listeners) {
		if (rootDir == null) {
			throw new IllegalArgumentException("Root directory is null");
		}
		if (!Files.exists(rootDir)) {
			throw new IllegalArgumentException("Root directory '" + rootDir.toAbsolutePath().toString() + "' does not exists");
		}
		if (listeners == null || listeners.isEmpty() || listeners.iterator().next() == null) {
			throw new IllegalArgumentException("List of Listeners is empty or null");
		}
		InstanceHolder.INSTANCE.listenerRegistry.registerListeners(listeners, rootDir);

		// TODO sub dirs
	}

	public static void unregister(final Collection<FileChangeListener> listeners) {
		InstanceHolder.INSTANCE.listenerRegistry.unregisterListeners(listeners);

		// Todo sub dirs
	}

	private List<WatchEvent<?>> pollEvents(final WatchKey watchKey) {
		final List<WatchEvent<?>> events = watchKey.pollEvents();
		if (events.isEmpty()) {
			if (watchKey.watchable() instanceof Path) {
				return Collections.singletonList(new DelWatchEvent((Path) watchKey.watchable()));
			} else {
				// empty
				return events;
			}
		} else {
			return events.stream().filter(event -> event.context() instanceof Path &&
					defaultFileIgnoreFilter.accept((Path) event.context())).collect(
							Collectors.toList());
		}
	}

	public void start() throws IOException {
		if (running.get()) {
			throw new IllegalStateException("RecursiveDirWatcher already started!");
		}
		watchService = FileSystems.getDefault().newWatchService();
		watchThread = new Thread(() -> {
			running.set(true);
			listenerRegistry.registerDirWatchesRecursively();

			while (running.get()) {
				try {
					final WatchKey watchKey = watchService.take();
					final List<WatchEvent<?>> events = pollEvents(watchKey);
					watchKey.reset();
					if (!events.isEmpty()) {
						delayedFire(events, (Path) watchKey.watchable());
					}
				} catch (InterruptedException | ClosedWatchServiceException e) {
					running.set(false);
				}
			}
		}, this.getClass().getSimpleName());
		watchThread.start();
	}

	private synchronized void delayedFire(final List<WatchEvent<?>> events, final Path watchable) {
		// Redelay
		if (timer != null) {
			timer.cancel();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ExcUtils.suppress(e);
			}
		}

		// Add events to an already existing watchable
		processEventLock.lock();
		final List<WatchEvent<?>> entries = watchedEvents.get(watchable);
		if (entries != null) {
			entries.addAll(events);
		} else {
			watchedEvents.put(watchable, events);
		}
		processEventLock.unlock();

		timer = new Timer("RecursiveDirWatchDelay");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				listenerRegistry.updateDirectories();
				processEventLock.lock();
				final TreeMap<Path, List<WatchEvent<?>>> watchedEventsCopy = new TreeMap<>(watchedEvents);
				watchedEvents.clear();
				processEventLock.unlock();
				// Fire
				final TreeSet<FileChangeListener> targetListeners = listenerRegistry.getListeners(watchedEventsCopy.keySet());
				for (final FileChangeListener targetListener : targetListeners) {
					try {
						targetListener.fileChanged(watchedEventsCopy);
					} catch (Exception e) {
						logger.error("Failed to trigger " + FileChangeListener.class.getName(), e);
					}
				}
			}
		}, fireDelay);
	}

	public synchronized void stop() {
		if (watchThread != null) {
			try {
				if (timer != null) {
					timer.cancel();
				}
				listenerRegistry.release();
				watchService.close();
				running.set(false);
				watchThread.interrupt();
			} catch (IOException e) {
				ExcUtils.suppress(e);
			}
		}
	}

}
