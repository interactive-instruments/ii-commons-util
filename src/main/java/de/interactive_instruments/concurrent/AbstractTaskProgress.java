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

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.print.attribute.UnmodifiableSetException;

import de.interactive_instruments.exceptions.ExcUtils;
import de.interactive_instruments.logging.BufferedLogAppender;
import de.interactive_instruments.logging.WLogAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Abstract task progress
 * 
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 *
 * @param <R>
 */
public abstract class AbstractTaskProgress<R> implements TaskProgress<R> {

    // Callback, set by abstract task
    TaskWithProgressIndication<R> task;
	private Future<R> future;
	protected int stepsCompleted;
	protected int remainingSteps;
	protected BufferedLogAppender appender;
	protected Logger logger;

    private STATE currentState=STATE.CREATED;
	private STATE oldState=null;
	// Used for the hasStatusChanged() method
	private STATE lastKnownState=STATE.CREATED;
    private Instant startInstant=null;
    private Instant stopInstant =null;
    private List<TaskStateEventListener> eventListeners=null;
    Exception exception;


	public AbstractTaskProgress()
    {
        logger = Logger.getLogger(this.getClass().getName() + "." + this.hashCode());
        logger.setLevel(Level.INFO);
    }

    void setAppender(BufferedLogAppender appender) {
        // new WLogAppender(logFile, 250);
        if(appender!=null) {
            logger.addAppender(appender);
            this.appender=appender;
        }
    }

    @Override
	public synchronized boolean isStateChanged() {
				
		// If the current status is "running" check
		// the future for an error. In this case
		// update the current status to failed
		if(this.currentState.isRunningOrInitializing()
			&& (this.future==null || this.future.isDone())) {
				// No the task is already done without a
				// status change. This indicates an error
				this.currentState=STATE.FAILED;
		}
		if(currentState!=lastKnownState) {
			lastKnownState=currentState;
			return true;
		}else if(appender.hasNewMessages()) {
			return true;
		}
		return false;
	}
	
	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public List<String> getLastMessages() {
        if(this.appender!=null) {
            return appender.getLastMessages();
        }
        return null;
	}

	@Override
	public int getMaxSteps() {
		return remainingSteps;
	}

	@Override
	public int getCurrentStepsCompleted() {
		return stepsCompleted;
	}

	@Override
	public double getPercentStepsCompleted() {
		return  getCurrentStepsCompleted()/getMaxSteps()*100;
	}

	@Override
	public R waitForResult() throws InterruptedException, ExecutionException {
		return this.future.get();
	}

	@Override
	public final void setFuture(Future<R> future) throws UnmodifiableSetException {
		if(this.future!=null) {
			throw new UnmodifiableSetException(
					"The already set Future can't be changed!");
		}
		this.future=future;
	}

    @Override
    public final Date getStartDate() {
        return Date.from(startInstant);
    }

    @Override
    public final Date getCompletionDate() {
        if(stopInstant !=null) {
            return Date.from(stopInstant);
        }
        return null;
    }

    @Override
    public final Duration getDuration() {
        if(stopInstant !=null) {
            return Duration.between(startInstant, stopInstant);
        }
        return Duration.between(startInstant, Instant.now());
    }

    protected final void logInfo(final String msg) {
        if(this.logger!=null) {
            this.logger.info(msg);
        }
    }

    // STATE implementations
    ///////////////////////////

    @Override
    public STATE getState() {
        return this.currentState;
    }

    @Override
    public synchronized void addStateEventListener(TaskStateEventListener listener) {
        if(this.eventListeners==null) {
            this.eventListeners=new ArrayList<>();
        }
        this.eventListeners.add(listener);
    }

    void changeState(STATE state, boolean reqCondition) throws InvalidStateTransitionException {
		if(!reqCondition || this.currentState==state) {
            final String errorMsg = "Illegal state transition in task "+this.task.getID()+
                    " from "+this.currentState+" to "+state;
            logInfo(errorMsg);
            throw new InvalidStateTransitionException(errorMsg);
		}
        if(this.oldState!=null) {
            logInfo("Changed state from " + this.oldState + " to " + this.currentState);
        }else{
            logInfo("Setting state to " + this.currentState);
        }
        synchronized (this) {
            this.oldState = this.currentState;
            this.currentState = state;
            // Notify observers
            if (this.eventListeners != null) {
                this.eventListeners.forEach(l ->
                        l.taskStateChangedEvent(task, this.currentState, this.oldState));
            }
        }
	}

    /**
     * Sets the start timestamp and the state to INITIALIZING
     * @throws InvalidStateTransitionException
     */
	final void fireInitializing() throws InvalidStateTransitionException {
		changeState(STATE.INITIALIZING,
				(currentState==STATE.CREATED));
        this.startInstant=Instant.now();
	}

    final void fireInitialized() throws InvalidStateTransitionException {
		changeState(STATE.INITIALIZED,
				(currentState==STATE.INITIALIZING));
	}

    final void fireRunning() throws InvalidStateTransitionException {
		changeState(STATE.RUNNING,
                (currentState==STATE.INITIALIZED));
	}

    final void fireCompleted() throws InvalidStateTransitionException {
        changeState(STATE.COMPLETED,
                (currentState==STATE.RUNNING));
        this.stopInstant=Instant.now();
    }


    final void fireFinalizing() throws InvalidStateTransitionException {
        changeState(STATE.FINALIZING,
                (currentState==STATE.COMPLETED ||
                    currentState==STATE.CANCELED ||
                    currentState==STATE.FAILED));
    }

    /**
     * Puts the task into a final state.
     *
     * Does not throw an exception!
     */
    final void fireFailed()  {
		try {
			changeState(STATE.FAILED,
                    (currentState==STATE.CREATED) ||
                            (currentState==STATE.INITIALIZING) ||
                            (currentState==STATE.INITIALIZED)  ||
                            (currentState==STATE.RUNNING));
		} catch (InvalidStateTransitionException e) {
            ExcUtils.suppress(e);
		}
        this.stopInstant=Instant.now();
    }

    final void fireCanceling() throws InvalidStateTransitionException {
		changeState(STATE.CANCELING,
				(currentState==STATE.CREATED ||
                        currentState==STATE.INITIALIZING ||
                        currentState==STATE.INITIALIZED ||
                        currentState==STATE.RUNNING) );
	}

    /**
     * Puts the task into a final state.
     *
     * @throws InvalidStateTransitionException
     */
    final void fireCanceled() throws InvalidStateTransitionException {
        changeState(STATE.CANCELED,
                (currentState==STATE.CANCELING));
        this.stopInstant=Instant.now();
    }

    public Exception getException() {
        return exception;
    }

    void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        if(this.task!=null && task.getID()!=null && this.currentState!=null) {
            return getClass().getName() + "@" + Integer.toHexString(hashCode()) +
                    "."+this.task.getID().toString()+" : "+currentState+
                    " ("+stepsCompleted+"/"+remainingSteps+")";
        }
        return super.toString();
    }
}
