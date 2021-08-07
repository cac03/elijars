package com.caco3.elijars.logging;

import com.caco3.elijars.utils.Assert;

import java.io.PrintStream;
import java.time.Instant;
import java.util.function.Supplier;

public class Logger {
    public enum Level {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        OFF;
    }

    private final String tag;
    private final PrintStream output;
    private final LevelSupplier levelSupplier;

    public Logger(String tag, PrintStream output, LevelSupplier levelSupplier) {
        Assert.notNull(tag, "tag == null");
        Assert.notNull(output, "output == null");
        Assert.notNull(levelSupplier, "levelSupplier == null");

        this.tag = tag;
        this.output = output;
        this.levelSupplier = levelSupplier;
    }

    public static Logger forClass(Class<?> clazz) {
        Assert.notNull(clazz, "clazz == null");
        return new Logger(clazz.getCanonicalName(), System.out, new SystemPropertyLevelSupplier());
    }

    public void info(Supplier<String> messageSupplier) {
        log(Level.INFO, messageSupplier);
    }

    public void trace(Supplier<String> messageSupplier) {
        log(Level.TRACE, messageSupplier);
    }

    public void debug(Supplier<String> messageSupplier) {
        log(Level.DEBUG, messageSupplier);
    }

    public void warn(Supplier<String> messageSupplier) {
        log(Level.WARN, messageSupplier);
    }

    public void warn(Supplier<String> messageSupplier, Throwable throwable) {
        log(Level.WARN, messageSupplier, throwable);
    }

    public void error(Supplier<String> messageSupplier) {
        log(Level.ERROR, messageSupplier);
    }

    public void error(Supplier<String> messageSupplier, Throwable throwable) {
        log(Level.ERROR, messageSupplier, throwable);
    }

    private void log(Level level, Supplier<String> messageSupplier, Throwable throwable) {
        Assert.notNull(level, "level == null");
        Assert.notNull(messageSupplier, "messageSupplier == null");
        if (level.ordinal() < levelSupplier.getLevel().ordinal()) {
            return;
        }
        String message = Instant.now() + " [" + level + "] "
                         + Thread.currentThread() + " " + tag + " " + messageSupplier.get();
        output.println(message);
        if (throwable != null) {
            throwable.printStackTrace(output);
        }
    }

    private void log(Level level, Supplier<String> messageSupplier) {
        log(level, messageSupplier, null);
    }
}
