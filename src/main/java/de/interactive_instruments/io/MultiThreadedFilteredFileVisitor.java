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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import de.interactive_instruments.Factory;
import de.interactive_instruments.container.Pair;

/**
 * Note only the visitFile method is proxied
 *
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class MultiThreadedFilteredFileVisitor implements FileVisitor<Path> {

	// simple filters that are executed before Threads are created
	private final PathFilter level1Filter;
	// expensive filters that are executed inside a Thread
	private final Factory<MultiFileFilter> level2Filter;
	// visitor tasks that are executed inside a Thread
	private final Map<String, FileVisitor<Path>> visitors = new HashMap<>();
	private final Queue<Pair<Path, BasicFileAttributes>> files = new ConcurrentLinkedQueue<>();
	private final ExecutorService executorService = Executors.newWorkStealingPool();
	private static final int MAX_TIMEOUT_H = 24;

	public MultiThreadedFilteredFileVisitor(final PathFilter level1Filter, final Factory<MultiFileFilter> level2Filter,
			final Collection<FileVisitor<Path>> visitors) {
		this.level1Filter = level1Filter;
		this.level2Filter = level2Filter;
		for (final FileVisitor<Path> visitor : visitors) {
			this.visitors.put(visitor.getClass().getSimpleName(), visitor);
		}
	}

	@Override
	public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
		if (level1Filter == null || level1Filter.accept(file)) {
			files.add(new Pair<>(file, attrs));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
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

	public void startWorkers() {
		final int threads = Math.min(files.size(), Runtime.getRuntime().availableProcessors());
		for (int i = 0; i < threads; i++) {
			executorService.execute(new FilterAndVisitRunnable(this.files, this.level2Filter.create(), visitors.values()));
		}
	}

	public void awaitTermination() throws InterruptedException {
		executorService.shutdown();
		executorService.awaitTermination(MAX_TIMEOUT_H, TimeUnit.HOURS);
		files.clear();
	}

	private static class FilterAndVisitRunnable implements Runnable {
		private final Queue<Pair<Path, BasicFileAttributes>> files;
		private final MultiFileFilter level2Filter;
		private final Collection<FileVisitor<Path>> visitors;

		FilterAndVisitRunnable(final Queue<Pair<Path, BasicFileAttributes>> files, final MultiFileFilter level2Filter,
				final Collection<FileVisitor<Path>> visitors) {
			this.files = files;
			this.level2Filter = level2Filter;
			this.visitors = visitors;
		}

		@Override
		public void run() {
			for (Pair<Path, BasicFileAttributes> filePair; (filePair = files.poll()) != null;) {
				final Path file = filePair.getLeft();
				if (this.level2Filter == null || this.level2Filter.accept(file)) {
					try {
						for (final FileVisitor<Path> visitor : visitors) {
							visitor.visitFile(file, filePair.getRight());
						}
					} catch (IOException e) {
						LoggerFactory.getLogger(FilterAndVisitRunnable.class).error("Internal error visiting file: ", e);
					}
				}
			}
		}
	}

}
