package org.basetools.util.concurrent.worker;

import org.basetools.util.Statistics;
import org.basetools.util.concurrent.ThreadFactoryWithNamePrefix;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Generic Class to execute a multi queued, pooled unit of works. Thread locals has to be instances of InheritableThreadLocal.
 *
 * @param <I>
 */
public class WorkService<I> {
    private int jobQueueSize = 10;
    private int corePoolSize = 10;
    private int maximumPoolSize = 100;
    private long keepAliveTime = 0L;
    private ExecutorService executorService;
    private BlockingQueue<Runnable> jobQueue = new ArrayBlockingQueue<>(jobQueueSize);
    private boolean isPaused = false;
    private boolean isRunning = false;
    private boolean isShutdowned = false;
    private Queue<Runnable> worklist = new ConcurrentLinkedQueue();
    private ExecutorService consumerService = Executors.newSingleThreadExecutor();
    private Statistics statistics;
    private boolean useStatistics;
    private boolean isIddle;

    public WorkService() {
        super();
    }

    public WorkService initialize() {

        RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
        executorService = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                jobQueue,
                new ThreadFactoryWithNamePrefix("WorkService"),
                rejectedExecutionHandler
        );
        return this;
    }

    /**
     * @param size the number of threads to keep in the pool, even
     *             if they are idle, unless {@code allowCoreThreadTimeOut} is set
     */
    public WorkService withCorePoolSize(int size) {
        corePoolSize = size;
        return this;
    }

    /**
     * @param size the maximum number of threads to allow in the
     *             pool
     */
    public WorkService withMaximumPoolSize(int size) {
        maximumPoolSize = size;
        return this;
    }

    /**
     * @param use set to collect statistic data
     */
    public WorkService withStatistics(boolean use) {
        useStatistics = use;
        return this;
    }

    /**
     * @param time when the number of threads is greater than
     *             the core, this is the maximum time that excess idle threads
     *             will wait for new tasks before terminating.
     */
    public WorkService withKeepAliveTime(long time) {
        keepAliveTime = time;
        return this;
    }

    /**
     * @param size max size of the internal job queue
     */
    public WorkService withJobQueueSize(int size) {
        jobQueueSize = size;
        return this;
    }

    public WorkService startWork() {
        initialize();
        isRunning = true;
        consumerService.submit(() -> doWork());
        return this;
    }

    public WorkService doWork() {
        while (isRunning) {
            synchronized (worklist) {

                while (worklist.peek() != null) {
                    Object info = null;
                    if (isPaused) {
                        try {
                            isIddle = true;
                            worklist.wait();
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    } else {
                        long start = System.currentTimeMillis();
                        isIddle = false;
                        try {

                            Runnable toDo = worklist.poll();
                            if (toDo instanceof RunnableWrapper) {
                                info = ((RunnableWrapper) toDo).getInfo();
                            }
                            executorService.submit(toDo);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        long timeComsumption = System.currentTimeMillis() - start;
                        addToStatistic(timeComsumption, info);
                    }
                }
                try {
                    isIddle = true;
                    worklist.wait();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return this;
    }

    public boolean isIddle() {
        return isIddle && getWorklist().size() == 0;
    }

    public Statistics getStatistics() {
        if (statistics == null) {
            statistics = new Statistics();
        }
        return statistics;
    }

    private void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    private void addToStatistic(long timeComsumption, Object o) {
        if (useStatistics) {
            getStatistics().accept(timeComsumption, o);
        }
    }

    public WorkService stopWork() {
        isRunning = false;
        return this;
    }

    public WorkService pauseWork(boolean pause) {
        isPaused = pause;
        synchronized (worklist) {
            getWorklist().notifyAll();
        }
        return this;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void shutdown() {
        isRunning = false;
        executorService.shutdown();
        consumerService.shutdown();
        isShutdowned = true;
    }

    public boolean isShutdowned() {
        return isShutdowned;
    }

    public void shutdown(boolean force) {
        if (force) {
            shutdown();
        } else {
            while (!isIddle()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

            shutdown();
        }
    }

    public Queue<Runnable> getWorklist() {
        return worklist;
    }

    public WorkService pushWorkUnit(Runnable work, I info) {
        pushWorkUnit(new RunnableWrapper(work, info));
        return this;
    }

    public WorkService pushWorkUnit(Runnable work) {
        checkShutdowned();
        worklist.add(work);
        synchronized (worklist) {
            worklist.notifyAll();
        }
        return this;
    }

    private void checkShutdowned() {
        if (isShutdowned()) {
            throw new IllegalStateException("Workservice shutdowned");
        }
    }

    class RunnableWrapper<I> implements Runnable {
        I info;
        Runnable run;

        RunnableWrapper(Runnable runable, I info) {
            run = runable;
            this.info = info;
        }

        public I getInfo() {
            return info;
        }

        @Override
        public void run() {
            run.run();
        }
    }
}
