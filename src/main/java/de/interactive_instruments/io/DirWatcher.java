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

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
 * Singleton. Registered listeners can be prioritized (see FileChangeListener).
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class DirWatcher {

	private final static Logger logger = LoggerFactory.getLogger(DirWatcher.class);

	private final static int fireDelay = 3321;
	// private final Collection<FileChangeListener> listeners;
	// private final PathFilter filter;

	private AtomicBoolean serviceRunning = new AtomicBoolean(false);
	private AtomicBoolean eventFireInProgress = new AtomicBoolean(false);

	private static WatchService watchService = null;
	private Thread watchThread = null;
	private Timer timer = null;

	private final static MultiFileFilter defaultFileIgnoreFilter = new DefaultFileIgnoreFilter();

	private final ListenerRegistry listenerRegistry = new ListenerRegistry();

	// Events fired for a path
	private final TreeMap<Path, List<WatchEvent<?>>> watchedEvents = new TreeMap<>();

	// Non-fair locking!
	private final Lock processEventLock = new ReentrantLock();
	private final Lock timerLock = new ReentrantLock();

	private static class WatchKeyListeners implements Releasable {
		private final TreeSet<FileChangeListener> listeners;
		private final WatchKey watchKey;

		public WatchKeyListeners(final Collection<FileChangeListener> listeners,
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
			watchKey.cancel();
		}

		public boolean releaseStaleDir() {
			if (watchKey != null && watchKey.watchable() instanceof Path) {
				if (Files.exists((Path) watchKey.watchable(), LinkOption.NOFOLLOW_LINKS)) {
					return false;
				} else {
					release();
					return true;
				}
			}
			// Not sure if this may happen?
			return true;
		}
	}

	private static class ListenerRegistry {
		// Note that the types are important for the map (priority notification)
		// Listeners observing root directories
		private final TreeMap<FileChangeListener, Set<Path>> registeredListenersForRootDirs = new TreeMap<>();
		// All observed sub-directories
		private final TreeMap<Path, WatchKeyListeners> watchedSubDirs = new TreeMap<>();

		private final Lock listenerLock = new ReentrantLock();

		private int listenerSize() {
			return registeredListenersForRootDirs.size();
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
					if (watchService == null) {
						throw new IllegalStateException("Watch Service not serviceRunning");
					}
					watchKey = dir.register(watchService,
							OVERFLOW, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				} catch (IOException e) {
					logger.error("Cannot watch directory", e);
					return;
				}
				watchedSubDirs.put(dir, new WatchKeyListeners(listeners, watchKey));
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
				try {
					listenerLock.tryLock(30, TimeUnit.SECONDS);
				} catch (InterruptedException ign) {
					ExcUtils.suppress(ign);
				}
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
			listenerLock.lock();
			watchedSubDirs.values().removeIf(e -> e.releaseStaleDir());
			listenerLock.unlock();
		}

		private void ensureDirectoryObserved(final Path dir, final TreeSet<FileChangeListener> listeners) {
			listenerLock.lock();
			for (final FileChangeListener listener : new TreeSet<>(listeners)) {
				final MultiFileFilter filter = listener.fileChangePreFilter();
				if (filter == null || filter.accept(dir)) {
					final WatchKeyListeners watchKeyListeners = watchedSubDirs.get(dir);
					if (watchKeyListeners != null) {
						watchKeyListeners.add(listener);
					} else {
						try {
							final WatchKey watchKey = dir.register(watchService,
									OVERFLOW, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
							watchedSubDirs.put(dir, new WatchKeyListeners(listeners, watchKey));
						} catch (IOException e) {
							logger.error("Cannot watch directory", e);
							return;
						}
					}
				} else {
					listeners.remove(listener);
				}
			}
			listenerLock.unlock();
		}

		private void registerDirWatchesRecursively() {
			// Ensure that each FileChangeListener is notified when a subdirectory gets changed
			// groupd the listeners by their root directory
			final Map<Path, TreeSet<FileChangeListener>> groupedListeners = new TreeMap<>();
			listenerLock.lock();
			for (final Map.Entry<FileChangeListener, Set<Path>> fileChangeListenerSetEntry : registeredListenersForRootDirs
					.entrySet()) {
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
			listenerLock.unlock();

			for (final Map.Entry<Path, TreeSet<FileChangeListener>> groupedListenersEntry : groupedListeners.entrySet()) {
				final TreeSet<FileChangeListener> listeners = groupedListenersEntry.getValue();
				final Path path = groupedListenersEntry.getKey();
				try {
					ensureDirectoryObserved(path, listeners);
					Files.walkFileTree(path, new FileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
								throws IOException {
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
			registerDirWatchesRecursively();
			unregisterStaleDirWatches();
		}

		public TreeSet<FileChangeListener> getListeners(final Set<Path> paths) {
			final TreeSet<FileChangeListener> res = new TreeSet<>();
			listenerLock.lock();
			for (final Path path : paths) {
				final WatchKeyListeners l = watchedSubDirs.get(path);
				if (l != null) {
					res.addAll(l.getListeners());
				}
			}
			listenerLock.unlock();
			return res;
		}
	}

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
		final DirWatcher instance = InstanceHolder.INSTANCE;
		final boolean start = instance.listenerRegistry.listenerSize() < 1;

		if (start) {
			instance.prepareStart();
		}
		instance.listenerRegistry.registerListeners(listeners, rootDir);
		instance.listenerRegistry.registerDirWatchesRecursively();
		if (start) {
			instance.start();
		}
	}

	public static void unregister(final FileChangeListener listener) {
		unregister(Collections.singletonList(listener));
	}

	public static void unregister(final Collection<FileChangeListener> listeners) {
		final DirWatcher instance = InstanceHolder.INSTANCE;
		instance.listenerRegistry.unregisterListeners(listeners);
		if (instance.listenerRegistry.listenerSize() < 1) {
			instance.stop();
		}
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

	private void prepareStart() {
		if (watchService == null) {
			try {
				watchService = FileSystems.getDefault().newWatchService();
			} catch (IOException ign) {
				ExcUtils.suppress(ign);
			}
		}
	}

	private synchronized void start() {
		if (watchService == null) {
			throw new IllegalStateException("Watch Service is null!");
		}
		if (serviceRunning.get()) {
			logger.error("RecursiveDirWatcher already started!");
			return;
		}
		watchThread = new Thread(() -> {
			serviceRunning.set(true);
			while (serviceRunning.get()) {
				try {
					final WatchKey watchKey = watchService.take();
					final List<WatchEvent<?>> events = pollEvents(watchKey);
					watchKey.reset();
					if (!events.isEmpty()) {
						delayedFire(events, (Path) watchKey.watchable());
					}
				} catch (InterruptedException | ClosedWatchServiceException e) {
					serviceRunning.set(false);
				}
			}
		}, this.getClass().getSimpleName());
		watchThread.start();
	}

	private synchronized void stop() {
		if (watchThread != null) {
			try {
				if (timer != null) {
					timer.cancel();
				}
				watchService.close();
				watchService = null;
				serviceRunning.set(false);
				watchThread.interrupt();
			} catch (IOException e) {
				ExcUtils.suppress(e);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.stop();
	}

	private synchronized void delayedFire(final List<WatchEvent<?>> events, final Path watchable) throws InterruptedException {
		if (timer != null) {
			// Kill scheduled threads but wait for started once
			if (eventFireInProgress.get()) {
				// wait for it
				logger.trace("Waiting for event trigger to complete");
				synchronized (eventFireInProgress) {
					eventFireInProgress.wait();
				}
			} else {
				timer.cancel();
				logger.trace("Canceled file event trigger preparation");
			}
		}

		// Add events to an existing watchable. Applicable, when a
		// scheduled task was canceled.
		final List<WatchEvent<?>> entries = watchedEvents.get(watchable);
		if (entries != null) {
			entries.addAll(events);
		} else {
			watchedEvents.put(watchable, events);
		}

		timer = new Timer("RecursiveDirWatchDelay");
		logger.trace("Preparing to trigger changed file events");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					eventFireInProgress.set(true);
					// Fire
					final TreeSet<FileChangeListener> targetListeners = listenerRegistry.getListeners(watchedEvents.keySet());
					for (final FileChangeListener targetListener : targetListeners) {
						logger.trace("Triggering changed file events for {}", targetListener.toString());
						try {
							targetListener.fileChanged(watchedEvents);
						} catch (Exception e) {
							logger.error("Failed to invoke " + FileChangeListener.class.getSimpleName() + " "
									+ targetListener.getClass().getName(), e);
						}
					}
					listenerRegistry.updateDirectories();
				} finally {
					watchedEvents.clear();
					eventFireInProgress.set(false);
					// notify waiting threads
					synchronized (eventFireInProgress) {
						eventFireInProgress.notify();
					}
				}
			}
		}, fireDelay);
	}
}
