package org.basetools.util.concurrent.worker;

import org.basetools.util.Statistics;
import org.basetools.util.StreamUtils;
import org.basetools.util.collection.ConcurrentList;
import org.basetools.util.concurrent.ThreadFactoryWithNamePrefix;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

/**
 * Generic Class to execute a multi queued, pooled unit of works. Thread locals has to be instances of InheritableThreadLocal.
 *
 * @param <I>
 */
public class WorkService<I> {
    public static final int INIT_STATE_NOT_INITIALIZED = 0;
    public static final int INIT_STATE_INITIALIZED = 1;
    public static final int INIT_STATE_RUNNING = 2;
    public static final int INIT_STATE_PAUSED = 3;
    public static final int INIT_STATE_THREAD_EXIT = 4;
    public static final int INIT_STATE_DESTROYED = 5;
    private int jobQueueSize = 10;
    private int corePoolSize = 10;
    private int maximumPoolSize = 100;
    private long keepAliveTime = 0L;
    private ExecutorService executorService;
    private BlockingQueue<Runnable> jobQueue = new ArrayBlockingQueue<>(jobQueueSize);
    private boolean isPaused = false;
    private boolean isRunning = false;
    private short initState = INIT_STATE_NOT_INITIALIZED;
    private boolean goesDown = false;
    private boolean isShutdowned = false;
    private Queue<Runnable> worklist = new ConcurrentLinkedQueue();
    private ExecutorService consumerService = Executors.newSingleThreadExecutor();
    private Statistics statistics;
    private boolean useStatistics;
    private boolean isIddle;
    private List<WorkListener> listeners = new ConcurrentList<>(new ArrayList<>());

    public WorkService() {
        super();
    }

    public WorkService<I> initialize() {

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
        initState = INIT_STATE_INITIALIZED;
        return this;
    }

    /**
     * @param size the number of threads to keep in the pool, even
     *             if they are idle, unless {@code allowCoreThreadTimeOut} is set
     */
    public WorkService<I> withCorePoolSize(int size) {
        corePoolSize = size;
        return this;
    }

    /**
     * @param size the maximum number of threads to allow in the
     *             pool
     */
    public WorkService<I> withMaximumPoolSize(int size) {
        maximumPoolSize = size;
        return this;
    }

    /**
     * @param use set to collect statistic data
     */
    public WorkService<I> withStatistics(boolean use) {
        useStatistics = use;
        return this;
    }

    /**
     * @param time when the number of threads is greater than
     *             the core, this is the maximum time that excess idle threads
     *             will wait for new tasks before terminating.
     */
    public WorkService<I> withKeepAliveTime(long time) {
        keepAliveTime = time;
        return this;
    }

    /**
     * @param size max size of the internal job queue
     */
    public WorkService<I> withJobQueueSize(int size) {
        jobQueueSize = size;
        return this;
    }

    public WorkService<I> startWork() {
        initialize();
        isRunning = true;
        consumerService.submit(() -> doWork());

        return this;
    }

    public WorkService<I> doWork() {
        while (isRunning) {
            initState = INIT_STATE_RUNNING;
            synchronized (worklist) {

                while (worklist.peek() != null) {
                    Object info = null;
                    if (isPaused) {
                        try {
                            isIddle = true;
                            worklist.wait(1000);
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
                            Future submitted = executorService.submit(toDo);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        addToStatistic(System.currentTimeMillis() - start, info);
                    }
                }
                try {
                    isIddle = true;
                    worklist.wait(1000);
                    if (goesDown && worklist.size() == 0) {
                        isRunning = false;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        initState = INIT_STATE_THREAD_EXIT;
        return this;
    }

    public boolean isIddle() {
        return isIddle && getWorklist().isEmpty();
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

    public WorkService<I> stopWork() {
        isRunning = false;
        return this;
    }

    public WorkService<I> pauseWork(boolean pause) {
        isPaused = pause;
        initState = (short) (isPaused ? INIT_STATE_PAUSED : INIT_STATE_RUNNING);
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
        goesDown = true;
        if (executorService != null)
            executorService.shutdown();
        if (consumerService != null)
            consumerService.shutdown();
        isShutdowned = true;
        initState = INIT_STATE_DESTROYED;
    }

    public boolean isShutdowned() {
        return isShutdowned;
    }


    public boolean shutdown(boolean force) {
        if (force) {
            shutdown();
        } else {
            while (initState != INIT_STATE_THREAD_EXIT) {
                try {
                    goesDown = true;
                    sleep(1000);
                    //   System.out.println("waiting for shutdown:" + initState);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

            shutdown();
        }
        return true;
    }

    public Queue<Runnable> getWorklist() {
        return worklist;
    }

    public void pushWorkUnit(Stream<Runnable> works, I info, int batchSize) {
        Stream<List<Runnable>> chunks = StreamUtils.chunked(works, batchSize);
        chunks.forEach(chunk -> pushWorkUnit(chunk, null, batchSize));
    }

    public void pushWorkUnit(Stream<Runnable> works, I info) {
        works.peek(runnable -> {
            if (runnable == null) throw new RuntimeException("Tombstone received");
        }).forEach(chunk -> pushWorkUnit(chunk, info));
    }

//    public void pushWorkUnit2(Stream<Runnable> works, I info, int batchSize) {
//        works.gather(Gatherers.windowFixed(batchSize)).forEach(batch -> {
//            pushWorkUnit(batch, null, batchSize);
//        });
//    }

    public WorkService<I> pushWorkUnit(List<Runnable> works, I info, int batchSize) {
        int totalSize = works.size();
        if (totalSize == 0) return this;
        final List<Runnable> batchWorks = new ArrayList<Runnable>(batchSize);
        AtomicInteger counter = new AtomicInteger(0);
        final Object monitor = worklist;

        WorkListener listener = rw -> {
            synchronized (monitor) {
                counter.incrementAndGet();
                monitor.notify();
            }
        };
        listeners.add(listener);
        try {
            int toProcess = totalSize;
            for (int i = 0; i < totalSize; ) {

                int todoSize = (toProcess >= batchSize) ? batchSize : toProcess;
                batchWorks.addAll(works.subList(i, i + todoSize));
                i = i + todoSize;
                toProcess = toProcess - todoSize;

                for (Runnable work : batchWorks) {
                    pushWorkUnitInternal(new RunnableWrapper(this, work, info));
                }
                while (counter.get() < todoSize) {
                    synchronized (monitor) {
                        try {
                            monitor.wait(200);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (goesDown) break;
                }
                batchWorks.clear();
                counter.set(0);
                if ((toProcess == totalSize) || goesDown) {
                    break;
                }
            }
        } finally {
            listeners.remove(listener);
        }
        return this;
    }

    public WorkService<I> pushWorkUnit(Runnable work, I info) {
        pushWorkUnitInternal(new RunnableWrapper(work, info));
        return this;
    }

    public WorkService<I> pushWorkUnit(Runnable work) {
        return pushWorkUnitInternal(work);
    }

    private WorkService<I> pushWorkUnitInternal(Runnable work) {
        checkShutdownd();
        worklist.add(work);
        synchronized (worklist) {
            worklist.notifyAll();
        }
        return this;
    }

    private void notifyDone(RunnableWrapper<I> iRunnableWrapper) {
        for (WorkListener listener : listeners) {
            listener.finished(iRunnableWrapper);
        }
    }

    private void checkShutdownd() {
        if (goesDown || isShutdowned()) {
            throw new IllegalStateException("Workservice shutdowned");
        }
    }

    class RunnableWrapper<I> implements Runnable {
        I info;
        Runnable run;
        WorkService<I> work;

        RunnableWrapper(Runnable runable, I info) {
            run = runable;
            this.info = info;
        }

        RunnableWrapper(WorkService<I> work, Runnable runable, I info) {
            run = runable;
            this.info = info;
            this.work = work;
        }

        public Runnable getRun() {
            return run;
        }

        public I getInfo() {
            return info;
        }

        @Override
        public void run() {
            try {
                run.run();
            } finally {
                if (work != null) {
                    work.notifyDone((WorkService<I>.RunnableWrapper<I>) this);
                }
            }
        }
    }


}
