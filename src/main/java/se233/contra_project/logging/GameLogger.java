package se233.contra_project.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Centralised gameplay logger.
 * <p>
 *  - Movement updates are logged at {@link Level#FINE}.<br>
 *  - Character actions (jumping, shooting, state changes) are logged at {@link Level#INFO}.<br>
 *  - Score updates are logged at {@link Level#CONFIG}.<br>
 */
public final class GameLogger {
    private static final Path LOG_DIRECTORY = Paths.get("logs");
    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneId.systemDefault());

    private static final Logger MOVEMENT_LOGGER = Logger.getLogger("se233.contra_project.gameplay.movement");
    private static final Logger ACTION_LOGGER = Logger.getLogger("se233.contra_project.gameplay.action");
    private static final Logger SCORE_LOGGER = Logger.getLogger("se233.contra_project.gameplay.score");

    private static volatile boolean configured = false;

    private GameLogger() {
        // Utility class
    }

    private static void configure() {
        if (configured) {
            return;
        }

        synchronized (GameLogger.class) {
            if (configured) {
                return;
            }

            try {
                Files.createDirectories(LOG_DIRECTORY);
                setupLogger(MOVEMENT_LOGGER, "movement.log", Level.FINE);
                setupLogger(ACTION_LOGGER, "action.log", Level.INFO);
                setupLogger(SCORE_LOGGER, "score.log", Level.CONFIG);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to initialise gameplay loggers", e);
            }

            configured = true;
        }
    }

    private static void setupLogger(Logger logger, String fileName, Level level) throws IOException {
        logger.setUseParentHandlers(false);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        Path filePath = LOG_DIRECTORY.resolve(fileName);
        FileHandler fileHandler = new FileHandler(filePath.toString(), false);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return TIMESTAMP.format(Instant.ofEpochMilli(record.getMillis())) +
                        " " + formatMessage(record) +
                        System.lineSeparator();
            }
        });
        logger.addHandler(fileHandler);
        logger.setLevel(level);
    }

    private static void log(Logger logger, Level level, String message, Object... args) {
        configure();
        Objects.requireNonNull(logger, "logger");
        Objects.requireNonNull(level, "level");
        String formattedMessage = (args == null || args.length == 0)
                ? message
                : String.format(message, args);
        if (logger.isLoggable(level)) {
            logger.log(level, formattedMessage);
        }
    }

    public static void logMovement(String message, Object... args) {
        log(MOVEMENT_LOGGER, Level.FINE, message, args);
    }

    public static void logAction(String message, Object... args) {
        log(ACTION_LOGGER, Level.INFO, message, args);
    }

    public static void logScore(String message, Object... args) {
        log(SCORE_LOGGER, Level.CONFIG, message, args);
    }
}
