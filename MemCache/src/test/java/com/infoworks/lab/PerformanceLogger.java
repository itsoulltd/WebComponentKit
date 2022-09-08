package com.infoworks.lab;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class PerformanceLogger {

    private Stopwatch watch = Stopwatch.createStarted();

    public void printMillis(String tag) {
        if (tag == null) tag = "";
        System.out.println("Time of execution in millis:" + tag + ": " + watch.elapsed(TimeUnit.MILLISECONDS));
    }

    public void printSeconds(String tag) {
        if (tag == null) tag = "";
        System.out.println("Time of execution in seconds:" + tag + ": " + watch.elapsed(TimeUnit.SECONDS));
    }
}
