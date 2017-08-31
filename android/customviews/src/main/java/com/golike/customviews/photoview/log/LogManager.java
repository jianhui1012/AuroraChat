package com.golike.customviews.photoview.log;

/**
 * Created by admin on 2017/8/31.
 */

public final class LogManager {
    private static Logger logger = new LoggerDefault();

    public LogManager() {
    }

    public static void setLogger(Logger newLogger) {
        logger = newLogger;
    }

    public static Logger getLogger() {
        return logger;
    }
}