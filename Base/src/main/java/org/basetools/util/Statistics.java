package org.basetools.util;

import org.basetools.util.array.ArrayUtil;
import org.basetools.util.collection.LimitedQueue;
import org.basetools.util.json.JSONUtilities;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;

public class Statistics<I> extends DoubleSummaryStatistics {
    private I maxInfo;
    private I minInfo;
    private long minCreated;
    private long maxCreated;
    private LimitedQueue<Object[]> history;
    private int historyLimit = 5;

    public Statistics() {
        super();
    }

    /**
     * set the limit of the recorded history entries
     *
     * @param limit
     * @return instance of Statictics
     */
    public Statistics withHistoryLimit(int limit) {
        setHistoryLimit(limit);
        return this;
    }

    /**
     * Records another value into the statistics information including an optional info string.
     *
     * @param value the input value
     * @param value additional info describing the value
     */
    public void accept(double value, I info) {
        if (value <= getMin()) {
            minInfo = info;
            minCreated = System.currentTimeMillis();
        }
        if (value >= getMax()) {
            if (maxCreated != 0) {
                //add only if not first!
                addToHistory(maxInfo, getMax());
            }
            maxInfo = info;
            maxCreated = System.currentTimeMillis();
        }
        super.accept(value);
    }

    public void addToHistory(I info, double value) {
        if (historyLimit > 0) {
            getHistory().add(new Object[]{info, value});
        }
    }

    public LimitedQueue<Object[]> getHistory() {
        if (history == null) {
            history = new LimitedQueue<>(historyLimit);
        }
        return history;
    }

    /**
     * Returns the info entry for the minimum value
     *
     * @return the min info
     */
    public I getMinInfo() {
        return minInfo;
    }

    /**
     * Returns the info entry for the maximum value
     *
     * @return the max info
     */
    public I getMaxInfo() {
        return maxInfo;
    }

    /**
     * Returns the entry creation timestamp of the maximum recorded value
     *
     * @return the insertion timestamp as long value
     */
    public long getMaxCreated() {
        return maxCreated;
    }

    /**
     * Returns the entry creation timestamp of the minimum recorded value
     *
     * @return the insertion timestamp as long value
     */
    public long getMinCreated() {
        return minCreated;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a non-empty string representation of this object suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     */
    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%f, min=%f,minInfo=%s,minCreated=%s, average=%f, max=%f,maxInfo=%s,maxCreated=%s}",
                getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getMinInfo(),
                getMinCreated(),
                getAverage(),
                getMax(),
                getMaxInfo(),
                getMaxCreated());
    }

    /**
     * Combines the state of another {@code Statistics} into this
     * one.
     *
     * @param other another {@code Statistics}
     */
    public void combine(Statistics<I> other) {
        if (other != null) {
            super.combine(other);
            if (other.getMin() <= getMin()) {
                minInfo = other.getMinInfo();
                minCreated = other.getMinCreated();
            }
            if (other.getMax() >= getMax()) {
                maxInfo = other.getMaxInfo();
                maxCreated = other.getMaxCreated();
            }
        }
    }

    /**
     * Prints the statistics as JSON
     *
     * @return JSON Object containing statistic informations
     */
    public JsonObjectBuilder toJSON() {
        JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("Count", getCount());
        result.add("Sum", getSum());
        if (!Double.isNaN(getMin()) && !Double.isInfinite(getMin())) {
            result.add("Min", getMin());
        }
        result.add("MinCreated", toLocalDateTime(new Date(getMinCreated())).toString());
        JSONUtilities.add(result, "MinInfo", getMinInfo());
        result.add("Average", getAverage());
        if (!Double.isNaN(getMax()) && !Double.isInfinite(getMax())) {
            result.add("Max", getMax());
        }

        JSONUtilities.add(result, "MaxInfo", getMaxInfo());
        result.add("MaxCreated", toLocalDateTime(new Date(getMaxCreated())).toString());
        Iterator<Object[]> lasts = getHistory().iterator();
        int pos = getHistory().size();
        JsonArrayBuilder historyArrayJSON = Json.createArrayBuilder();
        while (lasts.hasNext()) {
            JsonObjectBuilder historyJSON = Json.createObjectBuilder();
            Object[] entry = lasts.next();
            JSONUtilities.add(historyJSON, "Pos", pos--);
            JSONUtilities.add(historyJSON, "MaxInfo", ArrayUtil.get(entry, 0));
            JSONUtilities.add(historyJSON, "Max", ArrayUtil.get(entry, 1));
            historyArrayJSON.add(historyJSON);
        }
        result.add("SlowestHistory", historyArrayJSON);
        return result;
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public void clear() {
        maxInfo = null;
        minInfo = null;
        minCreated = 0;
        maxCreated = 0;
        clear();
    }

    public void setHistoryLimit(int limit) {
        historyLimit = limit;
        //set history to null to reinitialize Que again
        history = null;
    }
}
