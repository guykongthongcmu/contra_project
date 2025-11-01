package se233.contra_project.game.systems;

import se233.contra_project.actors.Bullet;
import se233.contra_project.actors.Player;
import se233.contra_project.actors.Projectile;
import se233.contra_project.bosses.Boss;
import se233.contra_project.core.Entity;
import java.util.ArrayList;
import java.util.List;

/**
 * Collision system that resolves interactions between the player, bosses,
 * bullets, and projectiles. The implementation focuses on the entities
 * available in the project and keeps the behaviour consistent with the
 * simplified boss implementations.
 */
public class CollisionSystem {

    private Player player;
    private final List<Boss> activeBosses;
    private final List<Bullet> playerBullets;
    private final List<Projectile> bossProjectiles;
    private final List<VisualEffect> visualEffects;

    private enum CollisionSound {
        BULLET_HIT_BOSS,
        BOSS_HIT_PLAYER,
        PROJECTILE_HIT_PLAYER
    }

    /**
     * Simple visual effect descriptor so the UI can render feedback for collisions.
     */
    public static class VisualEffect {
        public final double x;
        public final double y;
        public final String type;
        public double duration;
        public final double maxDuration;

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
        if (boss != null && !activeBosses.contains(boss)) {
            activeBosses.add(boss);
        }
    }

    public void removeBoss(Boss boss) {
        if (boss == null) {
            return;
        }
        activeBosses.remove(boss);
        bossProjectiles.removeAll(boss.getProjectiles());
    }

    public void addPlayerBullet(Bullet bullet) {
        if (bullet != null) {
            playerBullets.add(bullet);
        }
    }

    public void addBossProjectile(Projectile projectile) {
        if (projectile != null && !bossProjectiles.contains(projectile)) {
            bossProjectiles.add(projectile);
        }
    }

    public void update(double deltaTime) {
        updateVisualEffects(deltaTime);

        playerBullets.removeIf(bullet -> bullet == null || !bullet.isAlive());
        bossProjectiles.removeIf(projectile -> projectile == null || !projectile.isAlive());

        checkBulletBossCollisions();
        checkPlayerBossCollisions();
        checkPlayerProjectileCollisions();
    }

    private void updateVisualEffects(double deltaTime) {
        visualEffects.removeIf(effect -> {
            effect.update(deltaTime);
            return !effect.isAlive();
        });
    }

    private void checkBulletBossCollisions() {
        for (Bullet bullet : playerBullets) {
            if (bullet == null || !bullet.isAlive()) {
                continue;
            }

            for (Boss boss : activeBosses) {
                if (boss == null || !boss.isAlive() || boss.isDefeated()) {
                    continue;
                }

                if (areColliding(bullet, boss)) {
                    boss.takeDamage(bullet.getDamage());
                    bullet.setAlive(false);
                    createHitEffect(bullet.getPosition().getX(), bullet.getPosition().getY(), "bullet_hit");
                    playCollisionSound(CollisionSound.BULLET_HIT_BOSS);
                    break;
                }
            }
        }

        playerBullets.removeIf(bullet -> bullet == null || !bullet.isAlive());
    }

    private void checkPlayerBossCollisions() {
        if (player == null || !player.isAlive()) {
            return;
        }

        for (Boss boss : activeBosses) {
            if (boss == null || !boss.isAlive() || boss.isDefeated()) {
                continue;
            }

            if (areColliding(player, boss)) {
                handlePlayerBossCollision();
            }
        }
    }

    private void handlePlayerBossCollision() {
        player.takeDamage(1);
        createHitEffect(player.getPosition().getX(), player.getPosition().getY(), "player_damage");
        playCollisionSound(CollisionSound.BOSS_HIT_PLAYER);
    }

    private void checkPlayerProjectileCollisions() {
        if (player == null || !player.isAlive()) {
            return;
        }

        for (Projectile projectile : bossProjectiles) {
            if (projectile == null || !projectile.isAlive()) {
                continue;
            }

            if (areColliding(player, projectile)) {
                handlePlayerProjectileCollision(projectile);
            }
        }

        bossProjectiles.removeIf(projectile -> projectile == null || !projectile.isAlive());
    }

    private void handlePlayerProjectileCollision(Projectile projectile) {
        player.takeDamage(projectile.getDamage());

        if (projectile.getEffect() != Projectile.ProjectileEffect.NONE) {
            applyProjectileEffect(projectile);
        }

        projectile.handleCollision(player);
        if (projectile.shouldDestroyOnCollision()) {
            projectile.setAlive(false);
        }

        createHitEffect(projectile.getPosition().getX(), projectile.getPosition().getY(), "projectile_hit");
        playCollisionSound(CollisionSound.PROJECTILE_HIT_PLAYER);
    }

    private void applyProjectileEffect(Projectile projectile) {
        switch (projectile.getEffect()) {
            case FIRE:
                createHitEffect(projectile.getPosition().getX(), projectile.getPosition().getY(), "fire_damage");
                break;
            case ICE:
                createHitEffect(projectile.getPosition().getX(), projectile.getPosition().getY(), "ice_slow");
                break;
            case SHOCK:
                createHitEffect(projectile.getPosition().getX(), projectile.getPosition().getY(), "stun");
                break;
            case POISON:
                createHitEffect(projectile.getPosition().getX(), projectile.getPosition().getY(), "poison");
                break;
            case EXPLOSIVE:
                createExplosionEffect(projectile.getPosition().getX(), projectile.getPosition().getY());
                break;
            case CHAIN_LIGHTNING:
                createHitEffect(projectile.getPosition().getX(), projectile.getPosition().getY(), "lightning");
                break;
            case NONE:
            default:
                break;
        }
    }

    private boolean areColliding(Entity first, Entity second) {
        if (first == null || second == null) {
            return false;
        }
        return first.collidesWith(second);
    }

    private void createHitEffect(double x, double y, String effectType) {
        visualEffects.add(new VisualEffect(x, y, effectType, 0.5));
    }

    private void createExplosionEffect(double x, double y) {
        visualEffects.add(new VisualEffect(x, y, "explosion", 0.8));
    }

    private void playCollisionSound(CollisionSound sound) {
        System.out.println("Playing sound: " + sound);
    }

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
