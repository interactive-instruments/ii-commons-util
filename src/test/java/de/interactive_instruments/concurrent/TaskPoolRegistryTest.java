package de.interactive_instruments.concurrent;

import de.interactive_instruments.exceptions.InitializationException;
import de.interactive_instruments.exceptions.ObjectWithIdNotFoundException;
import de.interactive_instruments.logging.DefaultBufferedAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 
 * @author J. Herrmann ( herrmann <aT) interactive-instruments (doT> de )
 */
public class TaskPoolRegistryTest {

	class RestultType {
		private String result;
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result=result;
		}
	}
	
	
	class OneAbstractTaskProgress extends AbstractTaskProgress<RestultType> {

		public OneAbstractTaskProgress() throws IOException {
			super();
			logger= Logger.getLogger(this.getClass().getName()+"."+this.hashCode());
			logger.setLevel(Level.INFO);

			this.appender = new DefaultBufferedAppender(500);
			logger.addAppender(appender);
		}
	}
	
	class OneTask extends AbstractTask<RestultType> {

		private final UUID uuid;

		OneTask(final UUID uuid) throws IOException {
            super(new OneAbstractTaskProgress());
            this.uuid=uuid;
		}

        public void doNotify() {
            synchronized(this) {
                System.out.println("Notifying");
                notify();
            }
        }

        private void doWait() {
			synchronized(this){
				try {
                    System.out.println("Waiting...");
					wait(5000);
                    System.out.println("...done");
				} catch(InterruptedException e) {
                    e.printStackTrace();
				}
			}
		}
		
		@Override
		public RestultType call() throws Exception {
            System.out.println("fireInitializing()");
            fireInitializing();
            System.out.println("fireInitialized()");
            fireInitialized();
            System.out.println("fireRunning()");
            fireRunning();
			taskProgress.getLogger().info("Starting");
			RestultType rt = new RestultType();
			rt.setResult(this.uuid.toString());
			taskProgress.getLogger().info("Completed");
            System.out.println("waiting");
            doWait();
            System.out.println("fireCompleted()");
            fireCompleted();
			return rt;
		}

		@Override
		public UUID getID() {
			return uuid;
		}

		@Override
		public void release() {
		}

		@Override
		public void cancel() {

		}

        @Override
        public void init() throws InitializationException {

        }

		@Override
		public boolean isInitialized() {
			return true;
		}
	}
	
	
	
	private TaskPoolRegistry<RestultType> tpReg;
	
	@Before
	public void setUp() throws Exception {
		tpReg = new TaskPoolRegistry<RestultType>(2, 4);
	}

	@Test
	public void testTaskPoolRegistry() {
		assertTrue(tpReg!=null);
	}

	@Test
	public void testGetTaskById() {
		
		boolean thrown=false;
		try{
			tpReg.getTaskById(null);
		}catch(Exception e) {
			thrown=true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testSubmitTask() throws InterruptedException, ExecutionException, ObjectWithIdNotFoundException, IOException {
		final UUID id = UUID.randomUUID();
		OneTask c = new OneTask(id);
		tpReg.submitTask(c);
		final TaskProgress<RestultType> tp = tpReg.getTaskById(id).getTaskProgress();
        Thread.sleep(1500);
        c.doNotify();
        Thread.sleep(1500);
        assertEquals(TaskState.STATE.COMPLETED, tp.getState());
		assertEquals(id.toString(), tp.waitForResult().getResult());
	}

}
