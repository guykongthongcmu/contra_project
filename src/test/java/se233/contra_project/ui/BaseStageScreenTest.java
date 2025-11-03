package se233.contra_project.ui;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se233.contra_project.Launcher;
import se233.contra_project.game.GameSession;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BaseStageScreenTest {
    private static final long FX_TIMEOUT_SECONDS = 5;

    @BeforeAll
    static void initialiseToolkit() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException alreadyStarted) {
            latch.countDown();
        }
        latch.await();
    }

    @Test
    void triggerGameOverPassesVictoryScoreToLauncher() throws Exception {
        TestLauncher launcher = new TestLauncher();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                TestStage1 screen = new TestStage1();
                screen.setLauncher(launcher);

                GameSession session = new GameSession();
                session.addScore(150);
                screen.bindSession(session);
                screen.syncScore();

                screen.forceGameOver(true);

                assertEquals(1, launcher.callCount);
                assertEquals(150, launcher.receivedScore);
                assertTrue(launcher.receivedVictory);
            } catch (Throwable t) {
                failure.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError("FX thread assertion failed", failure.get());
        }
    }

    @Test
    void triggerGameOverOnlyFiresOnce() throws Exception {
        TestLauncher launcher = new TestLauncher();
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                TestStage1 screen = new TestStage1();
                screen.setLauncher(launcher);
                screen.bindSession(new GameSession());
                screen.syncScore();

                screen.forceGameOver(false);
                screen.forceGameOver(false);

                assertEquals(1, launcher.callCount);
                assertFalse(launcher.receivedVictory);
            } catch (Throwable t) {
                failure.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError("FX thread assertion failed", failure.get());
        }
    }

    @Test
    void reachingExitInvokesAdvanceCallback() throws Exception {
        AtomicBoolean advanced = new AtomicBoolean(false);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                TestStage1 screen = new TestStage1(() -> advanced.set(true));
                screen.bindSession(new GameSession());
                screen.syncScore();

                screen.invokePlayerReachedExit();

                assertTrue(advanced.get());

                // Cleanly stop animations by triggering game over once to stop timers
                screen.setLauncher(new TestLauncher());
                screen.forceGameOver(false);
            } catch (Throwable t) {
                failure.set(t);
            } finally {
                latch.countDown();
            }
        });

        assertTrue(latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        if (failure.get() != null) {
            throw new AssertionError("FX thread assertion failed", failure.get());
        }
    }

    private static class TestLauncher extends Launcher {
        int callCount;
        int receivedScore;
        boolean receivedVictory;

        @Override
        public void switchToGameOver(int score, boolean victory) {
            callCount++;
            receivedScore = score;
            receivedVictory = victory;
        }

        @Override
        public void switchToStage(int stageIndex) {
            // no-op for tests
        }
    }

    private static class TestStage1 extends Stage1Screen {
        TestStage1() {
            super();
        }

        TestStage1(Runnable advanceToStage2) {
            super(advanceToStage2);
        }

        void forceGameOver(boolean victory) {
            triggerGameOver(victory);
        }

        void syncScore() {
            syncSessionScore();
        }

        void invokePlayerReachedExit() {
            onPlayerReachedExitAfterBossDefeat();
        }
    }
}
