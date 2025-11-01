package se233.contra_project.actors;

import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;
import se233.contra_project.core.components.SpriteAnimation;

/**
 * Player class represents the controllable character
 * Handles movement, shooting, and player-specific logic
 */
public class Player extends Entity {
    private static final double PLAYER_SPEED = 200.0; // pixels per second
    private static final double PLAYER_WIDTH = 48.0;
    private static final double PLAYER_HEIGHT = 48.0;
    private static final double JUMP_FORCE = -400.0; // negative for upward
    private static final double GRAVITY = 800.0; // pixels per second squared

    private Sprite sprite;
    private SpriteAnimation idleAnimation;
    private SpriteAnimation walkAnimation;
    private SpriteAnimation jumpAnimation;
    private SpriteAnimation proneAnimation;

    private boolean facingRight;
    private boolean onGround;
    private boolean prone;
    private int lives;
    private double shootCooldown;
    private static final double SHOOT_COOLDOWN_TIME = 0.2; // seconds between shots
    private double groundLevel = 400.0;

    public Player(double x, double y) {
        super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
        this.facingRight = true;
        this.onGround = true;
        this.prone = false;
        this.lives = 3; // Start with 3 lives as per requirements
        this.shootCooldown = 0;
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
        if (newY >= groundLevel) {
            newY = groundLevel;
            this.setVelocity(this.velocity.getX(), 0);
            onGround = true;
        } else {
            onGround = false;
        }

        this.setPosition(newX, newY);

        updateAnimations();
    }

    /**
     * Move player left
     */
    public void moveLeft() {
        if (prone) {
            return;
        }
        if (this.velocity.getX() != -PLAYER_SPEED) {
            this.setVelocity(-PLAYER_SPEED, this.velocity.getY());
        }
        if (facingRight) {
            facingRight = false;
        }
        if (onGround) {
            playWalkAnimation();
        }
    }

    /**
     * Move player right
     */
    public void moveRight() {
        if (prone) {
            return;
        }
        if (this.velocity.getX() != PLAYER_SPEED) {
            this.setVelocity(PLAYER_SPEED, this.velocity.getY());
        }
        if (!facingRight) {
            facingRight = true;
        }
        if (onGround) {
            playWalkAnimation();
        }
    }

    /**
     * Stop horizontal movement
     */
    public void stopMoving() {
        if (Math.abs(this.velocity.getX()) > 1e-3) {
            this.setVelocity(0, this.velocity.getY());
        }
        if (!prone && onGround) {
            playIdleAnimation();
        }
    }

    /**
     * Make player jump
     */
    public void jump() {
        if (onGround && !prone) {
            this.setVelocity(this.velocity.getX(), JUMP_FORCE);
            onGround = false;
            if (jumpAnimation != null) {
                playJumpAnimation();
            }
        }
    }

    /**
     * Toggle prone position
     */
    public void toggleProne() {
        setProne(!prone);
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
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        if (sprite != null) {
            this.width = sprite.getWidth();
            this.height = sprite.getHeight();
            sprite.setPosition(this.position.getX(), this.position.getY());
        }
    }

    public SpriteAnimation getIdleAnimation() { return idleAnimation; }
    public void setIdleAnimation(SpriteAnimation idleAnimation) {
        this.idleAnimation = idleAnimation;
        if (idleAnimation != null) {
            idleAnimation.stop();
        }
    }

    public SpriteAnimation getWalkAnimation() { return walkAnimation; }
    public void setWalkAnimation(SpriteAnimation walkAnimation) {
        this.walkAnimation = walkAnimation;
        if (walkAnimation != null) {
            walkAnimation.stop();
        }
    }

    public SpriteAnimation getJumpAnimation() { return jumpAnimation; }
    public void setJumpAnimation(SpriteAnimation jumpAnimation) {
        this.jumpAnimation = jumpAnimation;
        if (jumpAnimation != null) {
            jumpAnimation.stop();
            jumpAnimation.setOnAnimationEnd(() -> {
                if (!onGround) {
                    jumpAnimation.play();
                    return;
                }
                if (prone) {
                    playProneAnimation();
                } else if (Math.abs(this.velocity.getX()) > 1e-3) {
                    playWalkAnimation();
                } else {
                    playIdleAnimation();
                }
            });
        }
    }

    public SpriteAnimation getProneAnimation() { return proneAnimation; }
    public void setProneAnimation(SpriteAnimation proneAnimation) {
        this.proneAnimation = proneAnimation;
        if (proneAnimation != null) {
            proneAnimation.stop();
        }
    }

    public void startIdleAnimation() {
        playIdleAnimation();
    }

    public void setGroundLevel(double groundLevel) {
        this.groundLevel = groundLevel;
    }

    public double getGroundLevel() {
        return groundLevel;
    }

    public void setProne(boolean value) {
        if (this.prone == value) {
            return;
        }
        this.prone = value;
        if (prone) {
            this.setVelocity(0, this.velocity.getY());
            playProneAnimation();
        } else if (this.velocity.getX() != 0) {
            playWalkAnimation();
        } else {
            playIdleAnimation();
        }
    }

    private void updateAnimations() {
        long now = System.nanoTime();
        if (idleAnimation != null && idleAnimation.isPlaying()) {
            idleAnimation.update(now);
            setCurrentSpriteFrame(idleAnimation.getCurrentFrame());
        } else if (walkAnimation != null && walkAnimation.isPlaying()) {
            walkAnimation.update(now);
            setCurrentSpriteFrame(walkAnimation.getCurrentFrame());
        } else if (jumpAnimation != null && jumpAnimation.isPlaying()) {
            jumpAnimation.update(now);
            setCurrentSpriteFrame(jumpAnimation.getCurrentFrame());
        } else if (proneAnimation != null && proneAnimation.isPlaying()) {
            proneAnimation.update(now);
            setCurrentSpriteFrame(proneAnimation.getCurrentFrame());
        } else if (sprite != null) {
            sprite.setPosition(this.position.getX(), this.position.getY());
        }
    }

    private void setCurrentSpriteFrame(javafx.scene.image.Image frame) {
        if (sprite != null && frame != null) {
            sprite.setImage(frame);
            sprite.setPosition(this.position.getX(), this.position.getY());
        }
    }

    private void playIdleAnimation() {
        if (idleAnimation != null) {
            switchAnimation(idleAnimation);
        } else {
            switchAnimation(null);
        }
    }

    private void playWalkAnimation() {
        if (walkAnimation != null) {
            switchAnimation(walkAnimation);
        } else {
            playIdleAnimation();
        }
    }

    private void playJumpAnimation() {
        if (jumpAnimation != null) {
            switchAnimation(jumpAnimation);
        }
    }

    private void playProneAnimation() {
        if (proneAnimation != null) {
            switchAnimation(proneAnimation);
        } else {
            switchAnimation(null);
        }
    }

    private void switchAnimation(SpriteAnimation target) {
        if (target != null && !target.isPlaying()) {
            target.play();
        }
        if (idleAnimation != null && idleAnimation != target && idleAnimation.isPlaying()) {
            idleAnimation.stop();
        }
        if (walkAnimation != null && walkAnimation != target && walkAnimation.isPlaying()) {
            walkAnimation.stop();
        }
        if (jumpAnimation != null && jumpAnimation != target && jumpAnimation.isPlaying()) {
            jumpAnimation.stop();
        }
        if (proneAnimation != null && proneAnimation != target && proneAnimation.isPlaying()) {
            proneAnimation.stop();
        }
    }
}
