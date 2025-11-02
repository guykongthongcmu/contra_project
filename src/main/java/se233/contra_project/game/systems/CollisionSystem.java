package se233.contra_project.game.systems;

import se233.contra_project.actors.Player;
import se233.contra_project.actors.Bullet;
import se233.contra_project.actors.Projectile;
import se233.contra_project.bosses.Boss;
import se233.contra_project.bosses.Boss1;
import se233.contra_project.bosses.Boss2;
import se233.contra_project.bosses.Boss3;
import se233.contra_project.core.Entity;
import se233.contra_project.core.components.Sprite;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced Collision System for advanced boss mechanics
 * Handles complex collision detection including defensive walls, special projectiles,
 * and boss-specific attack patterns
 */
public class CollisionSystem {

    private Player player;
    private List<Boss> activeBosses;
    private List<Bullet> playerBullets;
    private List<Projectile> bossProjectiles;

    // Collision sound effects (placeholder for audio system integration)
    private enum CollisionSound {
        BULLET_HIT_BOSS,
        BOSS_HIT_PLAYER,
        WALL_DESTROYED,
        SPECIAL_ATTACK,
        PROJECTILE_BOUNCE,
        SHIELD_ACTIVATE
    }

    // Visual effects for collisions
    private List<VisualEffect> visualEffects;

    private static class VisualEffect {
        double x, y;
        String type;
        double duration;
        double maxDuration;

        VisualEffect(double x, double y, String type, double duration) {
            this.x = x;
            this.y = y;
            this.type = type;
            this.duration = duration;
            this.maxDuration = duration;
        }

        boolean isAlive() {
            return duration > 0;
        }

        void update(double deltaTime) {
            duration -= deltaTime;
        }
    }

    public CollisionSystem() {
        this.player = null;
        this.activeBosses = new ArrayList<>();
        this.playerBullets = new ArrayList<>();
        this.bossProjectiles = new ArrayList<>();
        this.visualEffects = new ArrayList<>();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addBoss(Boss boss) {
        this.activeBosses.add(boss);
    }

    public void removeBoss(Boss boss) {
        this.activeBosses.remove(boss);
    }

    public void addPlayerBullet(Bullet bullet) {
        this.playerBullets.add(bullet);
    }

    public void addBossProjectile(Projectile projectile) {
        this.bossProjectiles.add(projectile);
    }

    public void update(double deltaTime) {
        // Update visual effects
        visualEffects.removeIf(effect -> {
            effect.update(deltaTime);
            return !effect.isAlive();
        });

        // Perform collision checks
        checkBulletBossCollisions();
        checkPlayerBossCollisions();
        checkPlayerProjectileCollisions();
        checkWallCollisions();
        checkAdvancedProjectileEffects();
    }

    private void checkBulletBossCollisions() {
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            if (!bullet.isAlive()) continue;

            for (Boss boss : activeBosses) {
                if (!boss.isAlive() || boss.isDefeated()) continue;

                if (checkCollision(bullet, boss)) {
                    handleBulletBossCollision(bullet, boss);
                    break; // Bullet can only hit one boss
                }
            }
        }

        // Remove dead bullets
        playerBullets.removeIf(bullet -> !bullet.isAlive());
    }

    private void handleBulletBossCollision(Bullet bullet, Boss boss) {
        // Basic bullet damage
        int damage = bullet.getDamage();

        // Handle special boss mechanics
        if (boss instanceof Boss1) {
            Boss1 boss1 = (Boss1) boss;
            handleBoss1Collision(bullet, boss1, damage);
        } else if (boss instanceof Boss2) {
            Boss2 boss2 = (Boss2) boss;
            handleBoss2Collision(bullet, boss2, damage);
        } else if (boss instanceof Boss3) {
            Boss3 boss3 = (Boss3) boss;
            handleBoss3Collision(bullet, boss3, damage);
        }

        // Remove bullet on hit
        bullet.setAlive(false);

        // Create hit effect
        createHitEffect(bullet.getPosition().getX(), bullet.getPosition().getY(), "bullet_hit");

        // Play collision sound
        playCollisionSound(CollisionSound.BULLET_HIT_BOSS);
    }

    private void handleBoss1Collision(Bullet bullet, Boss1 boss, int damage) {
        // Check if bullet hits defensive walls first
        if (boss.hasDefensiveWall()) {
            // Wall absorbs damage
            boss.takeDamage(damage / 2); // Reduced damage through wall
            bullet.setAlive(false);

            // Chance to destroy wall when hit
            if (Math.random() < 0.3) { // 30% chance
                boss.removeDefensiveWall();
            }
            return;
        }

        // Direct hit on boss
        boss.takeDamage(damage);
    }

    private void handleBoss2Collision(Bullet bullet, Boss2 boss, int damage) {
        // Boss2 has no special collision mechanics in simplified version
        boss.takeDamage(damage);
    }

    private void handleBoss3Collision(Bullet bullet, Boss3 boss, int damage) {
        // Boss3 has no special collision mechanics in simplified version
        boss.takeDamage(damage);
    }

    private void checkPlayerBossCollisions() {
        if (player == null || !player.isAlive()) return;

        for (Boss boss : activeBosses) {
            if (!boss.isAlive() || boss.isDefeated()) continue;

            if (checkCollision(player, boss)) {
                // Player takes damage from boss contact
                player.takeDamage(1);
                createHitEffect(player.getPosition().getX(), player.getPosition().getY(), "player_hit");
                playCollisionSound(CollisionSound.BOSS_HIT_PLAYER);
            }
        }
    }

    private void checkPlayerProjectileCollisions() {
        if (player == null || !player.isAlive()) return;

        for (Projectile projectile : bossProjectiles) {
            if (!projectile.isAlive()) continue;

            if (checkCollision(player, projectile)) {
                // Player takes damage from projectile
                player.takeDamage(projectile.getDamage());
                projectile.setMaxLifetime(0); // Remove projectile
                createHitEffect(player.getPosition().getX(), player.getPosition().getY(), "projectile_hit");
            }
        }

        // Remove dead projectiles
        bossProjectiles.removeIf(projectile -> !projectile.isAlive());
    }

    private void checkWallCollisions() {
        // Handle collisions with defensive walls from Boss1
        for (Boss boss : activeBosses) {
            if (boss instanceof Boss1) {
                Boss1 boss1 = (Boss1) boss;
                checkBoss1WallCollisions(boss1);
            }
        }
    }

    private void checkBoss1WallCollisions(Boss1 boss1) {
        // Check player collision with walls
        if (player != null && player.isAlive() && boss1.hasDefensiveWall()) {
            // Simple wall collision check (would need actual wall coordinates in full implementation)
            // For now, just reduce player movement when near boss with wall
            double distance = Math.sqrt(
                Math.pow(player.getPosition().getX() - boss1.getPosition().getX(), 2) +
                Math.pow(player.getPosition().getY() - boss1.getPosition().getY(), 2)
            );

            if (distance < 100) { // Within wall range
                player.takeDamage(1); // Wall contact damage
                createHitEffect(player.getPosition().getX(), player.getPosition().getY(), "wall_contact");
            }
        }
    }

    private void checkAdvancedProjectileEffects() {
        // Handle special projectile behaviors
        for (int i = bossProjectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = bossProjectiles.get(i);
            if (!projectile.isAlive()) continue;

            switch (projectile.getType()) {
                case BOUNCING:
                    // Bouncing projectiles are handled in their update method
                    break;
                default:
                    break;
            }
        }
    }

    // Collision detection methods
    private boolean checkCollision(Entity entity1, Entity entity2) {
        // Simple bounding box collision detection
        return entity1.getPosition().getX() < entity2.getPosition().getX() + entity2.getWidth() &&
               entity1.getPosition().getX() + entity1.getWidth() > entity2.getPosition().getX() &&
               entity1.getPosition().getY() < entity2.getPosition().getY() + entity2.getHeight() &&
               entity1.getPosition().getY() + entity1.getHeight() > entity2.getPosition().getY();
    }

    // Visual effects creation
    private void createHitEffect(double x, double y, String effectType) {
        visualEffects.add(new VisualEffect(x, y, effectType, 0.5));
    }

    // Sound effects (placeholder)
    private void playCollisionSound(CollisionSound sound) {
        // In a full implementation, this would trigger audio system
        System.out.println("Playing sound: " + sound);
    }

    // Getters for external systems
    public List<VisualEffect> getVisualEffects() {
        return new ArrayList<>(visualEffects);
    }

    public List<Boss> getActiveBosses() {
        return new ArrayList<>(activeBosses);
    }

    public void clear() {
        activeBosses.clear();
        playerBullets.clear();
        bossProjectiles.clear();
        visualEffects.clear();
    }
}
