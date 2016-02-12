/**
 * Copyright 2016 interactive instruments GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.interactive_instruments.concurrent;

import org.apache.log4j.Logger;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * An interface that will be used by a client to pull 
 * the status of a task. The Object (thread) that is running the task has
 * to implement this interface.
 * 
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 * 
 * @see de.interactive_instruments.concurrent.TaskPoolRegistry
 */
public interface TaskProgress<R> extends TaskState {
		
	/**
	 * Returns true if the task has new information that may be
	 * polled 
	 * @return
	 */
	boolean isStateChanged();
		
	/**
	 * Returns the logger that is used to log information about
	 * the progress of the task
	 * @return
	 */
	Logger getLogger();
	
	/**
	 * Returns the last messages of the TaskProgress
	 * @return
	 */
	List<String> getLastMessages();
	
	/**
	 * Number of known steps that have to be processed
	 * @return remaining steps 
	 */
	int getMaxSteps();
	
	/**
	 * Number of completed steps
	 * @return completed steps
	 */
	int getCurrentStepsCompleted();
	
	/**
	 * Number of completed steps in percent
	 * @return
	 */
	double getPercentStepsCompleted();
	
	/**
	 * Returns the date of the start of the task
	 * @return
	 */
	Date getStartDate();

	/**
	 * Returns the date of completion of the task or null
	 * if the task did not finish yet
	 * @return the date or null
	 */
	Date getCompletionDate();

	/**
	 * Returns the current duration or the duration after task completion.
	 */
	Duration getDuration();
		
	/**
	 * Returns a Future that can be used to get the result
	 * @return
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	R waitForResult() throws InterruptedException, ExecutionException;
	
	/**
	 * Used by the TaskPoolRegistry to set the Future after 
	 * submitting the task.
	 * @param future
	 * @throws IllegalStateException if the future is already set
	 */
	void setFuture(Future<R> future) throws IllegalStateException;
}
