package se233.contra_project.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {
    private GameSession session;

    @BeforeEach
    void setUp() {
        session = new GameSession();
    }

    @Test
    void addScoreShouldIncreaseTotal() {
        assertEquals(0, session.getScore());
        session.addScore(100);
        assertEquals(100, session.getScore());
        session.addScore(50);
        assertEquals(150, session.getScore());
    }

    @Test
    void addScoreShouldIgnoreNonPositiveValues() {
        session.addScore(-10);
        session.addScore(0);
        assertEquals(0, session.getScore());
    }

    @Test
    void resetShouldClearScore() {
        session.addScore(75);
        session.reset();
        assertEquals(0, session.getScore());
    }
}
