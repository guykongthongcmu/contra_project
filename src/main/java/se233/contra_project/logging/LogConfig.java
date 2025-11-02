package se233.contra_project.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Central logging configuration for the Contra project.
 * Ensures a consistent formatter and log level for all loggers.
 */
public final class LogConfig {
    private static volatile boolean initialized = false;

    private LogConfig() {
        // Utility class
    }

    /**
     * Configure the root logger with a concise formatter suited for gameplay logs.
     */
    public static synchronized void configure() {
        if (initialized) {
            return;
        }

        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new Formatter() {
            private static final String LINE_SEPARATOR = System.lineSeparator();

            @Override
            public synchronized String format(LogRecord record) {
                StringBuilder builder = new StringBuilder();
                builder.append(String.format(
                        "%1$tF %1$tT [%2$-7s] %3$s",
                        new Date(record.getMillis()),
                        record.getLevel().getName(),
                        formatMessage(record)
                ));

                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    try (PrintWriter pw = new PrintWriter(sw)) {
                        record.getThrown().printStackTrace(pw);
                    }
                    builder.append(LINE_SEPARATOR).append(sw);
                }

                builder.append(LINE_SEPARATOR);
                return builder.toString();
            }
        });

        rootLogger.addHandler(consoleHandler);
        rootLogger.setLevel(Level.ALL);

        initialized = true;
    }

    /**
     * Retrieve a logger for the given class ensuring the logging system is configured.
     *
     * @param clazz class requesting a logger
     * @return configured logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        configure();
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setLevel(Level.ALL);
        return logger;
    }
}
