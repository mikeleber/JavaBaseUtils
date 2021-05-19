package org.basetools.util.time;

import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;

public class StopWatch extends org.apache.commons.lang3.time.StopWatch {
    public static final String INTERIM_MESSAGE = "Total:{0} Interim {1}";
    public static final String STOP_MESSAGE = "Total:{0}";
    private static final long NANO_2_MILLIS = 1000000L;
    private long interim;

    public void printStopWithMessage(String message, Object... params) {
        super.stop();
        System.out.println(MessageFormat.format(message, ArrayUtils.add(params, getTime())));
        super.reset();
    }

    public void stopAndPrint() {
        printStopWithMessage(STOP_MESSAGE);
    }

    @Override
    public void start() {
        super.start();
        interim = System.nanoTime();
    }

    public void printInterimWithMessage(String message, Object... params) {
        message = (message == null ? INTERIM_MESSAGE : message);
        long interimOld = getInterimTime();
        long diffTime = System.nanoTime() - interimOld;
        System.out.println(MessageFormat.format(message, ArrayUtils.addAll(params, getTime(), (diffTime / NANO_2_MILLIS))));
        interim = System.nanoTime();
    }

    public void interimPrint(Object... params) {
        printInterimWithMessage("Total:%s Interim %s");
    }

    public long getInterimTime() {
        return interim;
    }
}
