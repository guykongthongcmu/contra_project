package se233.contra_project.bosses;

import se233.contra_project.actors.Projectile;

/**
 * Boss1 - Defense Wall
 * A stationary boss that creates defensive barriers and shoots projectiles
 * Based on the Contra boss that builds walls for protection
 */
public class Boss1 extends Boss {
    private static final double BOSS_WIDTH = 64.0;
    private static final double BOSS_HEIGHT = 96.0;
    private static final int BOSS_HEALTH = 10;
    private static final int BOSS_SCORE = 2;

    private static final double ATTACK_COOLDOWN = 2.0; // seconds between attacks
    private static final double WALL_SPAWN_COOLDOWN = 5.0; // seconds between wall spawns
    private double attackTimer;
    private double wallSpawnTimer;
    private boolean hasWall;

    public Boss1(double x, double y) {
        super(x, y, BOSS_WIDTH, BOSS_HEIGHT, BOSS_HEALTH, BOSS_SCORE);
        this.attackTimer = 0;
        this.wallSpawnTimer = 0;
        this.hasWall = false;

        // Initialize sprite (placeholder - would load actual Defense Wall sprite)
        // this.sprite = new Sprite("path/to/defense_wall_boss.png", BOSS_WIDTH, BOSS_HEIGHT);
    }

    @Override
    protected void updateIdle(double deltaTime) {
        attackTimer += deltaTime;
        wallSpawnTimer += deltaTime;

        // Spawn defensive wall periodically
        if (wallSpawnTimer >= WALL_SPAWN_COOLDOWN && !hasWall) {
            spawnDefensiveWall();
            wallSpawnTimer = 0;
            hasWall = true;
        }

        // Attack periodically
        if (attackTimer >= ATTACK_COOLDOWN) {
            currentState = BossState.ATTACKING;
            stateTimer = 0;
            performAttack();
            attackTimer = 0;
        }
    }

    @Override
    protected void updateAttacking(double deltaTime) {
        // Attack animation/state lasts for 1 second
        if (stateTimer >= 1.0) {
            currentState = BossState.IDLE;
            stateTimer = 0;
        }
    }

    @Override
    protected void updateDying(double deltaTime) {
        // Death animation handled by base class
    }

    @Override
    protected void performAttack() {
        // Fire multiple projectiles in a spread pattern
        double centerX = this.position.getX() + this.width / 2;
        double centerY = this.position.getY() + this.height / 2;

        // Fire 3 projectiles in a fan pattern
        for (int i = -1; i <= 1; i++) {
            double angle = Math.toRadians(i * 15); // -15, 0, 15 degrees
            double vx = Math.cos(angle) * 200; // 200 pixels/second
            double vy = Math.sin(angle) * 200;

            Projectile projectile = new Projectile(centerX, centerY, vx, vy, Projectile.ProjectileType.STRAIGHT);
            addProjectile(projectile);
        }
    }

    /**
     * Spawn a defensive wall in front of the boss
     * This wall would block player bullets but allow boss projectiles through
     */
    private void spawnDefensiveWall() {
        // In a full implementation, this would create wall entities
        // For now, just set a flag and handle wall logic in collision system
        System.out.println("Boss1 spawned defensive wall");
    }

    /**
     * Check if boss currently has an active defensive wall
     * @return true if wall is active
     */
    public boolean hasDefensiveWall() {
        return hasWall;
    }

    /**
     * Remove the defensive wall (called when wall is destroyed or times out)
     */
    public void removeDefensiveWall() {
        hasWall = false;
        wallSpawnTimer = 0; // Reset timer to spawn new wall sooner
    }

    @Override
    public void takeDamage(int damage) {
        // If boss has a wall, it absorbs some damage
        if (hasWall) {
            // Wall takes 50% of the damage
            int wallDamage = damage / 2;
            damage -= wallDamage;

            // Chance to destroy wall when hit
            if (Math.random() < 0.3) { // 30% chance
                removeDefensiveWall();
            }
        }

        super.takeDamage(damage);
    }
}