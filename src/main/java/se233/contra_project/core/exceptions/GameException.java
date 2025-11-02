package se233.contra_project.core.exceptions;

/**
 * Unified runtime exception for gameplay failures.
 * All lower-level exceptions should be wrapped in this type so that
 * higher layers can handle failures consistently.
 */
public class GameException extends RuntimeException {
    public GameException(String message) {

        super(message);
    }

    public GameException(String message, Throwable cause) {

        super(message, cause);
    }
}
