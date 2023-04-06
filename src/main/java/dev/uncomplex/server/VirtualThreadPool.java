package dev.uncomplex.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.thread.ThreadPool;

/**
 *
 * @author James Thorpe <james@uncomplex.dev>
 */
public class VirtualThreadPool implements ThreadPool {

        private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        @Override
        public void execute(Runnable command) {
            executor.execute(command);
        }

        @Override
        public void join() throws InterruptedException {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }

        // those are hopefully only used for stats/dashboards etc
        @Override
        public int getThreads() {
            return 1;
        }

        @Override
        public int getIdleThreads() {
            return 0;
        }

        @Override
        public boolean isLowOnThreads() {
            return false;
        }
    }
