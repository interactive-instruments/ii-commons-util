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

import de.interactive_instruments.container.Pair;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class MultiThreadedFilteredFileVisitor implements FileVisitor<Path> {

	// simple filters that are executed before Threads are created
	private final PathFilter level1Filter;
	// expensive filters that are executed inside a Thread
	private final MultiFileFilter level2Filter;
	// visitor tasks that are executed inside a Thread
	private final Map<String, FileVisitor<Path>> visitors;
	private final List<Pair<Path, BasicFileAttributes>> files = new LinkedList<>();
	private final int workPackageDist = Runtime.getRuntime().availableProcessors();
	private final ExecutorService executorService = Executors.newWorkStealingPool();
	private static final int MAX_TIMEOUT_H = 24;

	public MultiThreadedFilteredFileVisitor(final PathFilter level1Filter, final MultiFileFilter level2Filter, final Map<String, FileVisitor<Path>> visitors) {
		this.level1Filter = level1Filter;
		this.level2Filter = level2Filter;
		this.visitors = visitors;
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
		// create threads
		final int remainingFiles = files.size() % workPackageDist;
		final int filesPerThreads;
		final int lastIteration;
		if(remainingFiles>0) {
			final int hfilesPerThread = (files.size() / workPackageDist);
			if(files.size() % (hfilesPerThread+1) == 0) {
				// last thread takes it all
				filesPerThreads = hfilesPerThread;
				lastIteration = filesPerThreads + remainingFiles;
			}else{
				// last threads takes less tasks
				filesPerThreads = hfilesPerThread + 1;
				lastIteration = files.size()-filesPerThreads*(workPackageDist-1);
			}
		}else{
			filesPerThreads = (files.size() / workPackageDist);
			lastIteration = filesPerThreads;
		}

		// create multiple threads
		final Iterator<Pair<Path, BasicFileAttributes>> fileIterator = files.iterator();
		for (int i = 0; i < workPackageDist -1; i++) {
			final Pair<Path, BasicFileAttributes>[] filesCopy = new Pair[filesPerThreads];
			for(int j = 0; j < filesPerThreads; j++) {
				filesCopy[j] = fileIterator.next();
			}
			createThread(filesCopy);
		}
		// last thread
		final Pair<Path, BasicFileAttributes>[] filesCopy = new Pair[lastIteration];
		for (int j = 0; j < lastIteration; j++) {
			filesCopy[j] = fileIterator.next();
		}
		createThread(filesCopy);

		files.clear();
		try {
			executorService.awaitTermination(MAX_TIMEOUT_H, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			LoggerFactory.getLogger(FilterAndVisitRunnable.class).error("Internal error waiting for task termination: ",e);
		}
		return FileVisitResult.CONTINUE;
	}

	private void createThread(final Pair<Path, BasicFileAttributes>[] files) {
		executorService.execute(new FilterAndVisitRunnable(files, this.level2Filter, this.visitors.values()));
	}

	private static class FilterAndVisitRunnable implements Runnable {
		private final Pair<Path, BasicFileAttributes>[] files;
		private final MultiFileFilter level2Filter;
		private final Collection<FileVisitor<Path>> visitors;

		FilterAndVisitRunnable(final Pair<Path, BasicFileAttributes>[] files, final MultiFileFilter level2Filter, final Collection<FileVisitor<Path>> visitors) {
			this.files = files;
			this.level2Filter = level2Filter;
			this.visitors = visitors;
		}

		@Override
		public void run() {
			for (final Pair<Path, BasicFileAttributes> file1 : files) {
				final Path file = file1.getLeft();
				if (this.level2Filter == null || this.level2Filter.accept(file)) {
					try {
						for (final FileVisitor<Path> visitor : visitors) {
							visitor.visitFile(file, file1.getRight());
						}
					} catch (IOException e) {
						LoggerFactory.getLogger(FilterAndVisitRunnable.class).error("Internal error visiting file: ", e);
					}
				}
			}
		}
	}

}
