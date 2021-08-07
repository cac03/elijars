package com.caco3.elijars.logging;

import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class LoggerTest {
    private static final String TAG = "My tag";
    private static final String TEST_MESSAGE = "Test message is not so long";
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final PrintStream printStream = new PrintStream(output);

    @Nested
    class Trace {
        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.INCLUDE, names = {"TRACE"})
        void logged(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.trace(() -> TEST_MESSAGE);
            assertTestMessageLoggedAtLevel(level);
        }

        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE, names = {"TRACE"})
        void ignored(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.trace(() -> TEST_MESSAGE);
            assertNothingLogged();
        }
    }

    @Nested
    class Debug {
        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.INCLUDE, names = {"DEBUG", "TRACE"})
        void debugMessageLogged(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.debug(() -> TEST_MESSAGE);
            assertTestMessageLoggedAtLevel(Logger.Level.DEBUG);
        }

        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE, names = {"DEBUG", "TRACE"})
        void debugMessageIgnored(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.debug(() -> TEST_MESSAGE);
            assertNothingLogged();
        }
    }

    @Nested
    class Info {
        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.INCLUDE, names = {"DEBUG", "TRACE", "INFO"})
        void logged(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.info(() -> TEST_MESSAGE);
            assertTestMessageLoggedAtLevel(Logger.Level.INFO);
        }

        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE, names = {"DEBUG", "TRACE", "INFO"})
        void ignored(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.info(() -> TEST_MESSAGE);
            assertNothingLogged();
        }
    }

    @Nested
    class Warn {
        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.INCLUDE, names = {"DEBUG", "TRACE", "INFO", "WARN"})
        void logged(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.warn(() -> TEST_MESSAGE);
            assertTestMessageLoggedAtLevel(Logger.Level.WARN);
        }

        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE, names = {"DEBUG", "TRACE", "INFO", "WARN"})
        void ignored(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.warn(() -> TEST_MESSAGE);
            assertNothingLogged();
        }

        @Test
        void exceptionLogged() {
            String nestedExceptionMessage = "Nested exception message";
            Exception nestedException = new Exception(nestedExceptionMessage);
            String outerExceptionMessage = "Outer exception message";
            Exception outerException = new Exception(outerExceptionMessage, nestedException);
            Logger logger = newLogger(Logger.Level.WARN);
            String message = "Something went wrong";
            logger.warn(() -> message, outerException);

            assertThat(getOutput())
                    .contains(message)
                    .contains(outerExceptionMessage)
                    .contains(nestedExceptionMessage)
                    .contains(getStackTraceAsString(outerException));
        }
    }

    @Nested
    class Error {
        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.INCLUDE, names = {"DEBUG", "TRACE", "INFO", "WARN", "ERROR"})
        void logged(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.error(() -> TEST_MESSAGE);
            assertTestMessageLoggedAtLevel(Logger.Level.ERROR);
        }

        @ParameterizedTest
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE, names = {"DEBUG", "TRACE", "INFO", "WARN", "ERROR"})
        void ignored(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.error(() -> TEST_MESSAGE);
            assertNothingLogged();
        }

        @Test
        void exceptionLogged() {
            String nestedExceptionMessage = "Nested exception message";
            Exception nestedException = new Exception(nestedExceptionMessage);
            String outerExceptionMessage = "Outer exception message";
            Exception outerException = new Exception(outerExceptionMessage, nestedException);
            Logger logger = newLogger(Logger.Level.ERROR);
            String message = "Something went wrong";
            logger.error(() -> message, outerException);

            assertThat(getOutput())
                    .contains(message)
                    .contains(outerExceptionMessage)
                    .contains(nestedExceptionMessage)
                    .contains(getStackTraceAsString(outerException));
        }
    }

    @Nested
    class Off {
        @EnumSource(value = Logger.Level.class, mode = EnumSource.Mode.EXCLUDE)
        void ignored(Logger.Level level) {
            Logger logger = newLogger(level);
            logger.error(() -> TEST_MESSAGE, new Throwable());

            assertNothingLogged();
        }
    }

    private String getOutput() {
        printStream.flush();
        return output.toString(StandardCharsets.UTF_8);
    }

    private Logger newLogger(Logger.Level level) {
        return new Logger(TAG, printStream, constantLevelSupplier(level));
    }

    private static LevelSupplier constantLevelSupplier(Logger.Level level) {
        return () -> level;
    }

    private void assertNothingLogged() {
        assertThat(getOutput()).isEmpty();
    }

    private AbstractStringAssert<?> assertTestMessageLoggedAtLevel(Logger.Level level) {
        return assertThat(getOutput())
                .contains(level.toString())
                .contains(TEST_MESSAGE);
    }

    private static String getStackTraceAsString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }
}