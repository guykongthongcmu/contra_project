package se233.contra_project.actors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private static final double DELTA = 1e-3;

    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player(0, 0);
        player.setGroundLevel(0);
        player.setPosition(0, 0);
    }

    @Test
    void moveLeftShouldSetVelocityAndFacingLeft() {
        player.moveLeft();
        assertEquals(-200.0, player.getVelocity().getX(), DELTA);
        assertFalse(player.isFacingRight());
    }

    @Test
    void moveRightShouldSetVelocityAndFacingRight() {
        player.moveRight();
        assertEquals(200.0, player.getVelocity().getX(), DELTA);
        assertTrue(player.isFacingRight());
    }

    @Test
    void stopMovingShouldZeroHorizontalVelocity() {
        player.moveRight();
        player.stopMoving();
        assertEquals(0.0, player.getVelocity().getX(), DELTA);
    }

    @Test
    void jumpShouldApplyUpwardForceWhenOnGround() {
        player.jump();
        assertEquals(-400.0, player.getVelocity().getY(), DELTA);
        assertFalse(player.isOnGround());
    }

    @Test
    void jumpWhileAirborneShouldNotChangeVelocity() {
        player.jump(); // first jump leaves player airborne
        double previousVy = player.getVelocity().getY();
        player.jump(); // second jump should be ignored
        assertEquals(previousVy, player.getVelocity().getY(), DELTA);
    }

    @Test
    void proneShouldShrinkHitboxAndRestoreOnStandUp() {
        double originalHeight = player.getHeight();
        double originalWidth = player.getWidth();
        double originalBottom = player.getPosition().getY() + player.getHeight();

        player.setProne(true);
        assertTrue(player.isProne());
        assertEquals(originalWidth * 0.95, player.getWidth(), DELTA);
        assertEquals(originalHeight * 0.5, player.getHeight(), DELTA);
        double proneBottom = player.getPosition().getY() + player.getHeight();
        assertEquals(originalBottom, proneBottom, DELTA, "Feet should stay anchored while proning");

        player.setProne(false);
        assertFalse(player.isProne());
        assertEquals(originalWidth, player.getWidth(), DELTA);
        assertEquals(originalHeight, player.getHeight(), DELTA);
        double standingBottom = player.getPosition().getY() + player.getHeight();
        assertEquals(originalBottom, standingBottom, DELTA);
    }

    @Test
    void shootStandingShouldSpawnBulletAtMidHeight() {
        Bullet bullet = player.shoot();
        assertNotNull(bullet);
        assertEquals(player.getPosition().getX() + player.getWidth() - 6, bullet.getPosition().getX(), DELTA);
        assertEquals(player.getPosition().getY() + player.getHeight() * 0.45, bullet.getPosition().getY(), DELTA);
    }

    @Test
    void shootProneShouldSpawnBulletCloserToGround() {
        player.setProne(true);
        Bullet bullet = player.shoot();
        assertNotNull(bullet);
        assertEquals(player.getPosition().getX() + player.getWidth() - 6, bullet.getPosition().getX(), DELTA);
        assertEquals(player.getPosition().getY() + player.getHeight() * 0.8, bullet.getPosition().getY(), DELTA);
    }

    @Test
    void shootDuringCooldownShouldReturnNull() {
        assertNotNull(player.shoot());
        assertNull(player.shoot(), "Second shot should be blocked by cooldown");
    }

    @Test
    void loseLifeShouldReduceLivesAndFlagDeath() {
        player.loseLife();
        player.loseLife();
        assertTrue(player.isAlive());
        player.loseLife();
        assertFalse(player.isAlive());
    }
}
