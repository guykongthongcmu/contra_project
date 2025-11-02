package se233.contra_project.core.exceptions;

/**
 * Factory helpers for constructing {@link GameException} instances.
 */
public final class GameExceptions {
    private GameExceptions() {
        // Utility class
    }


    // sprite loading failures
    public static GameException failure(String message, Throwable cause) {

        return new GameException(message, cause);
    }

    public static GameException failure(String message) {

        return new GameException(message);
    }
}
