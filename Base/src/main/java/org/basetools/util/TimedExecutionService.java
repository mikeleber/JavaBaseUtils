package org.basetools.util;

import java.util.TimerTask;

public class TimedExecutionService {
    private TimedExecutionService() {
        super();
    }

    private static TimerTask wrap(final Runnable r) {
        return new TimerTask() {

            @Override
            public void run() {
                r.run();
            }
        };
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param task  task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *                                  <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or
     *                                  cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException     if {@code task} is null
     */
    public static void schedule(Runnable task, int delay) {
        schedule(
                wrap(task),
                delay
        );
    }

    /**
     * Schedules the specified task for execution after the specified delay.
     *
     * @param task  task to be scheduled.
     * @param delay delay in milliseconds before task is to be executed.
     * @throws IllegalArgumentException if <tt>delay</tt> is negative, or
     *                                  <tt>delay + System.currentTimeMillis()</tt> is negative.
     * @throws IllegalStateException    if task was already scheduled or
     *                                  cancelled, timer was cancelled, or timer thread terminated.
     * @throws NullPointerException     if {@code task} is null
     */
    public static void schedule(TimerTask task, int delay) {
        new java.util.Timer().schedule(
                task,
                delay
        );
    }
}
