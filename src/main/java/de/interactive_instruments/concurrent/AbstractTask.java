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
package de.interactive_instruments.concurrent;

import java.io.IOException;
import java.util.UUID;

import de.interactive_instruments.logging.WLogAppender;

/**
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public abstract class AbstractTask<R> implements TaskWithProgressIndication<R> {

	protected final AbstractTaskProgress<R> taskProgress;

	public AbstractTask(AbstractTaskProgress<R> taskProgress) {
		this.taskProgress = taskProgress;
		this.taskProgress.task = this;
	}

	// Call WlogAppender here: workaround for log4j library problem in test drivers
	protected void setWlogAppender(final String logFile) throws IOException {
		this.taskProgress.setAppender(new WLogAppender(logFile, 250));
	}

	@Override
	public final TaskProgress<R> getTaskProgress() {
		return this.taskProgress;
	}

	protected final void fireInitializing() throws InvalidStateTransitionException {
		taskProgress.fireInitializing();
	}

	protected final void fireInitialized() throws InvalidStateTransitionException {
		taskProgress.fireInitialized();

	}

	protected final void fireRunning() throws InvalidStateTransitionException {
		taskProgress.fireRunning();
	}

	protected final void fireCompleted() throws InvalidStateTransitionException {
		taskProgress.fireCompleted();
	}

	protected final void fireFinalizing() throws InvalidStateTransitionException {
		taskProgress.fireFinalizing();
	}

	/**
	 * Puts the task into a final state.
	 *
	 * Does not throw an exception!
	 */
	protected final void fireFailed() {
		taskProgress.fireFailed();
	}

	protected final void fireCanceling() throws InvalidStateTransitionException {
		taskProgress.fireCanceling();
	}

	/**
	 * Puts the task into a final state.
	 *
	 * @throws InvalidStateTransitionException
	 */
	protected final void fireCanceled() throws InvalidStateTransitionException {
		taskProgress.fireCanceled();
	}

	protected void setException(Exception e) {
		taskProgress.setException(e);
	}
}
