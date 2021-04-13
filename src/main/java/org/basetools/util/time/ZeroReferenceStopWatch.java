package org.basetools.util.time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ZeroReferenceStopWatch {

    private final Object sync = new Object();
    private long beginTime = 0;
    private long endTime = 0;
    private long overallDuration = 0;
    private long overallIntermediateDuration = 0;
    private long[] laps = new long[1 << 4];
    private int lapCount = 0;
    private int createdLaps = 0;
    private int maxConcurrentCount = 0;

    public static long toMilliseconds(long nano) {
        return nano / 1000000;
    }

    public static long toSeconds(long nano) {
        return toMilliseconds(nano) / 1000;
    }

    /**
     * <p>
     * Start the stopwatch.
     * </p>
     *
     * <p>
     * This method starts a new timing session, clearing any previous values.
     * </p>
     */
    public void start() {
        synchronized (sync) {
            if (beginTime != 0) {
                // already started
                return;
            }
            beginTime = System.nanoTime();
            laps[lapCount++] = beginTime;
        }
    }

    public long timeElapsed() {
        synchronized (sync) {
            if (beginTime == 0) {
                return 0;
            }
            return toMilliseconds(System.nanoTime() - beginTime);
        }
    }

    public void lapAndStart() {
        if (beginTime == 0) {
            start();
        }
        lap();
    }

    public void lap() {
        synchronized (sync) {
            if (beginTime == 0) {
                return;
            }
            if (lapCount == laps.length) {
                laps = Arrays.copyOf(laps, lapCount + (lapCount >> 1) + 8);
            }
            createdLaps++;
            laps[lapCount++] = System.nanoTime();

            if (lapCount > maxConcurrentCount) {
                //-1 because start cause a lapCount increment
                maxConcurrentCount = lapCount - 1;
            }
        }
    }

    public int getCreatedLapCount() {
        return createdLaps;
    }

    public int getLapCount() {
        return lapCount;
    }

    public int getMaxConcurrentCount() {

        return maxConcurrentCount;
    }

    public long getDuration(TimeUnit unit) {
        Objects.requireNonNull(unit);
        if (overallDuration == 0) {
            return unit.convert(overallIntermediateDuration, TimeUnit.NANOSECONDS);
        } else {
            return unit.convert(overallDuration, TimeUnit.NANOSECONDS);
        }
    }

    public long getOverallIntermediateDuration() {
        return overallIntermediateDuration;
    }

    public long getOverallDuration() {
        return TimeUnit.NANOSECONDS.toSeconds(overallDuration);
    }

    public long removeLap() {
        synchronized (sync) {
            if (beginTime == 0) {
                return 0;
            }
            lapCount--;
            // ==1 means only start left!
            if (lapCount == 1) {
                laps = new long[1 << 4];
                endTime = System.nanoTime();
                long duration = endTime - beginTime;
                overallDuration += duration;
                overallIntermediateDuration = overallDuration;
                beginTime = 0;
                lapCount = 0;
                return duration;
            } else if (lapCount <= laps.length) {
                long intermediateEndTime = System.nanoTime();
                long intermediateDuration = intermediateEndTime - beginTime;
                overallIntermediateDuration = overallDuration + intermediateDuration;
                laps = Arrays.copyOf(laps, lapCount + (lapCount >> 1) + 8);
            }
        }

        return 0;
    }

    public void reset() {
        synchronized (sync) {
            beginTime = 0;
            lapCount = 0;
        }
    }

    public List<Long> getLaps() {
        long[] times;
        synchronized (sync) {
            times = Arrays.copyOf(laps, lapCount);
        }
        List<Long> data = new ArrayList<>(times.length);
        for (int i = 1; i < times.length; i++) {
            data.add(toMilliseconds(times[i] - times[i - 1]));
        }
        return data;
    }
}
