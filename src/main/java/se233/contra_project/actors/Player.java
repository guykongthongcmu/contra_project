package se233.contra_project.actors;

import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;
import se233.contra_project.core.components.SpriteAnimation;
import javafx.geometry.Point2D;

/**
 * Player class represents the controllable character
 * Handles movement, shooting, and player-specific logic
 */
public class Player extends Entity {
    private static final double PLAYER_SPEED = 200.0; // pixels per second
    private static final double PLAYER_WIDTH = 32.0;
    private static final double PLAYER_HEIGHT = 32.0;
    private static final double JUMP_FORCE = -400.0; // negative for upward
    private static final double GRAVITY = 800.0; // pixels per second squared

    private Sprite sprite;
    private SpriteAnimation walkAnimation;
    private SpriteAnimation shootAnimation;

    private boolean facingRight;
    private boolean onGround;
    private boolean prone;
    private int lives;
    private double shootCooldown;
    private static final double SHOOT_COOLDOWN_TIME = 0.2; // seconds between shots

    public Player(double x, double y) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        this.facingRight = true;
        this.onGround = true;
        this.prone = false;
        this.lives = 3; // Start with 3 lives as per requirements
        this.shootCooldown = 0;

        // Initialize sprite (placeholder - actual sprite loading would depend on assets)
        // this.sprite = new Sprite("path/to/player.png", PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    @Override
    public void update(double deltaTime) {
        // Update shoot cooldown
        if (shootCooldown > 0) {
            shootCooldown -= deltaTime;
        }

        // Apply gravity if not on ground
        if (!onGround) {
            double newVy = this.velocity.getY() + GRAVITY * deltaTime;
            this.setVelocity(this.velocity.getX(), newVy);
        }

        // Update position based on velocity
        double newX = this.position.getX() + this.velocity.getX() * deltaTime;
        double newY = this.position.getY() + this.velocity.getY() * deltaTime;

        // Basic ground collision (simplified)
        if (newY >= 400) { // Assuming ground at y=400
            newY = 400;
            this.setVelocity(this.velocity.getX(), 0);
            onGround = true;
        } else {
            onGround = false;
        }

        this.setPosition(newX, newY);

        // Update sprite position and animation
        if (sprite != null) {
            sprite.setPosition(newX, newY);
        }

        // Update animations
        if (walkAnimation != null) {
            walkAnimation.update(System.nanoTime());
        }
        if (shootAnimation != null) {
            shootAnimation.update(System.nanoTime());
        }
    }

    /**
     * Move player left
     */
    public void moveLeft() {
        if (!prone) {
            this.setVelocity(-PLAYER_SPEED, this.velocity.getY());
            facingRight = false;
        }
    }

    /**
     * Move player right
     */
    public void moveRight() {
        if (!prone) {
            this.setVelocity(PLAYER_SPEED, this.velocity.getY());
            facingRight = true;
        }
    }

    /**
     * Stop horizontal movement
     */
    public void stopMoving() {
        this.setVelocity(0, this.velocity.getY());
    }

    /**
     * Make player jump
     */
    public void jump() {
        if (onGround && !prone) {
            this.setVelocity(this.velocity.getX(), JUMP_FORCE);
            onGround = false;
        }
    }

    /**
     * Toggle prone position
     */
    public void toggleProne() {
        prone = !prone;
        if (prone) {
            this.setVelocity(0, this.velocity.getY());
        }
    }

    /**
     * Shoot a bullet
     * @return new Bullet instance if shot is fired, null if on cooldown
     */
    public Bullet shoot() {
        if (shootCooldown <= 0) {
            double bulletX = facingRight ? this.position.getX() + this.width : this.position.getX();
            double bulletY = this.position.getY() + this.height / 2;
            Bullet bullet = new Bullet(bulletX, bulletY, facingRight, true);
            shootCooldown = SHOOT_COOLDOWN_TIME;
            return bullet;
        }
        return null;
    }

    /**
     * Lose a life
     */
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            this.alive = false;
        }
    }

    // Getters and setters
    public boolean isFacingRight() { return facingRight; }
    public boolean isOnGround() { return onGround; }
    public boolean isProne() { return prone; }
    public int getLives() { return lives; }

    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }

    public SpriteAnimation getWalkAnimation() { return walkAnimation; }
    public void setWalkAnimation(SpriteAnimation walkAnimation) { this.walkAnimation = walkAnimation; }

    public SpriteAnimation getShootAnimation() { return shootAnimation; }
    public void setShootAnimation(SpriteAnimation shootAnimation) { this.shootAnimation = shootAnimation; }
}