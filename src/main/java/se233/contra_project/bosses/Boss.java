package se233.contra_project.bosses;

import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;
import se233.contra_project.core.components.SpriteAnimation;
import se233.contra_project.actors.Projectile;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all boss enemies
 * Provides common boss functionality like health, attack patterns, and defeat conditions
 */
public abstract class Boss extends Entity {
    protected int maxHealth;
    protected int scoreValue;
    protected Sprite sprite;
    protected SpriteAnimation idleAnimation;
    protected SpriteAnimation attackAnimation;
    protected SpriteAnimation deathAnimation;

    protected BossState currentState;
    protected double stateTimer;
    protected List<Projectile> projectiles;

    public enum BossState {
        IDLE,
        ATTACKING,
        DYING,
        DEAD
    }

    public Boss(double x, double y, double width, double height, int maxHealth, int scoreValue) {
        super(x, y, width, height);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.scoreValue = scoreValue;
        this.currentState = BossState.IDLE;
        this.stateTimer = 0;
        this.projectiles = new ArrayList<>();

        // Initialize sprite (placeholder - actual sprite loading would depend on assets)
        // this.sprite = new Sprite("path/to/boss.png", width, height);
    }

    @Override
    public void update(double deltaTime) {
        stateTimer += deltaTime;

        switch (currentState) {
            case IDLE:
                updateIdle(deltaTime);
                break;
            case ATTACKING:
                updateAttacking(deltaTime);
                break;
            case DYING:
                updateDying(deltaTime);
                break;
            case DEAD:
                // Boss is dead, no updates needed
                break;
        }

        // Update sprite position if sprite exists
        if (sprite != null) {
            sprite.setPosition(this.position.getX(), this.position.getY());
        }

        // Update animations
        if (idleAnimation != null && currentState == BossState.IDLE) {
            idleAnimation.update(System.nanoTime());
        }
        if (attackAnimation != null && currentState == BossState.ATTACKING) {
            attackAnimation.update(System.nanoTime());
        }
        if (deathAnimation != null && currentState == BossState.DYING) {
            deathAnimation.update(System.nanoTime());
        }

        // Update projectiles
        projectiles.removeIf(projectile -> !projectile.isAlive());
        for (Projectile projectile : projectiles) {
            projectile.update(deltaTime);
        }
    }

    /**
     * Update logic when boss is idle
     * @param deltaTime time elapsed
     */
    protected abstract void updateIdle(double deltaTime);

    /**
     * Update logic when boss is attacking
     * @param deltaTime time elapsed
     */
    protected abstract void updateAttacking(double deltaTime);

    /**
     * Update logic when boss is dying
     * @param deltaTime time elapsed
     */
    protected abstract void updateDying(double deltaTime);

    /**
     * Perform attack pattern specific to this boss
     */
    protected abstract void performAttack();

    /**
     * Start the death sequence
     */
    public void startDeath() {
        if (currentState != BossState.DEAD) {
            currentState = BossState.DYING;
            stateTimer = 0;
            if (deathAnimation != null) {
                deathAnimation.play();
            }
        }
    }

    /**
     * Check if boss is defeated
     * @return true if boss is dead
     */
    public boolean isDefeated() {
        return currentState == BossState.DEAD;
    }

    /**
     * Get projectiles fired by this boss
     * @return list of active projectiles
     */
    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    /**
     * Add a projectile to the boss's projectile list
     * @param projectile projectile to add
     */
    protected void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
    }

    // Getters and setters
    public int getMaxHealth() { return maxHealth; }
    public int getScoreValue() { return scoreValue; }
    public BossState getCurrentState() { return currentState; }

    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }

    public SpriteAnimation getIdleAnimation() { return idleAnimation; }
    public void setIdleAnimation(SpriteAnimation idleAnimation) { this.idleAnimation = idleAnimation; }

    public SpriteAnimation getAttackAnimation() { return attackAnimation; }
    public void setAttackAnimation(SpriteAnimation attackAnimation) { this.attackAnimation = attackAnimation; }

    public SpriteAnimation getDeathAnimation() { return deathAnimation; }
    public void setDeathAnimation(SpriteAnimation deathAnimation) {
        this.deathAnimation = deathAnimation;
        if (deathAnimation != null) {
            deathAnimation.setOnAnimationEnd(() -> {
                currentState = BossState.DEAD;
                this.alive = false;
            });
        }
    }
}