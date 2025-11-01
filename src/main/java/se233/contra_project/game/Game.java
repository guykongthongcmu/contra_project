package se233.contra_project.game;

import se233.contra_project.actors.Player;
import se233.contra_project.actors.Bullet;
import se233.contra_project.actors.Projectile;
import se233.contra_project.bosses.Boss;
import se233.contra_project.core.Entity;
import se233.contra_project.game.input.InputHandler;
import se233.contra_project.game.systems.RenderSystem;
import se233.contra_project.ui.GameScreen;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game class that manages the game loop, entities, and game state
 */
public class Game {
    private GameScreen gameScreen;
    private InputHandler inputHandler;
    private RenderSystem renderSystem;

    private Player player;
    private Boss currentBoss;
    private List<Bullet> playerBullets;
    private List<Projectile> enemyProjectiles;
    private List<Entity> entities;

    private long lastUpdateTime;
    private boolean running;

    // Game constants
    private static final int TARGET_FPS = 60;
    private static final double TARGET_FRAME_TIME = 1.0 / TARGET_FPS;

    public Game(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.inputHandler = new InputHandler();
        this.renderSystem = new RenderSystem();

        this.playerBullets = new ArrayList<>();
        this.enemyProjectiles = new ArrayList<>();
        this.entities = new ArrayList<>();

        this.running = false;
        this.lastUpdateTime = System.nanoTime();

        initializeGame();
    }

    /**
     * Initialize game entities and state
     */
    private void initializeGame() {
        // Create player
        player = new Player(100, 400); // Start position
        entities.add(player);

        // TODO: Initialize current boss based on level
        // currentBoss = new Boss1(...);
        // entities.add(currentBoss);
    }

    /**
     * Start the game loop
     */
    public void start() {
        running = true;
        gameLoop();
    }

    /**
     * Stop the game loop
     */
    public void stop() {
        running = false;
    }

    /**
     * Main game loop with fixed timestep
     */
    private void gameLoop() {
        while (running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0; // Convert to seconds

            // Cap delta time to prevent spiral of death
            if (deltaTime > 0.1) {
                deltaTime = 0.1;
            }

            // Update game logic
            update(deltaTime);

            // Render game
            render();

            // Calculate sleep time to maintain target FPS
            long frameTime = System.nanoTime() - currentTime;
            double sleepTime = TARGET_FRAME_TIME - (frameTime / 1_000_000_000.0);

            if (sleepTime > 0) {
                try {
                    Thread.sleep((long)(sleepTime * 1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            lastUpdateTime = currentTime;
        }
    }

    /**
     * Update all game entities and systems
     * @param deltaTime time elapsed since last update
     */
    private void update(double deltaTime) {
        // Update input
        inputHandler.update();

        // Process input for player
        processPlayerInput();

        // Update all entities
        for (Entity entity : entities) {
            if (entity.isAlive()) {
                entity.update(deltaTime);
            }
        }

        // Update bullets
        playerBullets.removeIf(bullet -> !bullet.isAlive());
        for (Bullet bullet : playerBullets) {
            bullet.update(deltaTime);
        }

        // Update enemy projectiles
        enemyProjectiles.removeIf(projectile -> !projectile.isAlive());
        for (Projectile projectile : enemyProjectiles) {
            projectile.update(deltaTime);
        }

        // Update boss if exists
        if (currentBoss != null && currentBoss.isAlive()) {
            currentBoss.update(deltaTime);
            // Add boss projectiles to game
            enemyProjectiles.addAll(currentBoss.getProjectiles());
            currentBoss.getProjectiles().clear();
        }

        // TODO: Collision detection will be handled by CollisionSystem
        // TODO: Physics updates will be handled by PhysicsSystem
    }

    /**
     * Process player input and update player state
     */
    private void processPlayerInput() {
        if (player == null) return;

        // Movement input
        if (inputHandler.isKeyPressed(InputHandler.Key.LEFT)) {
            player.moveLeft();
        } else if (inputHandler.isKeyPressed(InputHandler.Key.RIGHT)) {
            player.moveRight();
        } else {
            player.stopMoving();
        }

        // Jump input
        if (inputHandler.isKeyPressed(InputHandler.Key.UP)) {
            player.jump();
        }

        // Prone input
        if (inputHandler.isKeyPressed(InputHandler.Key.DOWN)) {
            player.toggleProne();
        }

        // Shooting input
        if (inputHandler.isKeyPressed(InputHandler.Key.SHOOT)) {
            Bullet bullet = player.shoot();
            if (bullet != null) {
                playerBullets.add(bullet);
                entities.add(bullet);
            }
        }
    }

    /**
     * Render the game
     */
    private void render() {
        // Trigger repaint on game screen
        if (gameScreen != null) {
            gameScreen.repaint();
        }
    }

    /**
     * Add a bullet to the game
     */
    public void addBullet(Bullet bullet) {
        playerBullets.add(bullet);
        entities.add(bullet);
    }

    /**
     * Add an enemy projectile to the game
     */
    public void addEnemyProjectile(Projectile projectile) {
        enemyProjectiles.add(projectile);
        entities.add(projectile);
    }

    /**
     * Set the current boss
     */
    public void setCurrentBoss(Boss boss) {
        this.currentBoss = boss;
        entities.add(boss);
    }

    // Getters
    public Player getPlayer() { return player; }
    public Boss getCurrentBoss() { return currentBoss; }
    public List<Bullet> getPlayerBullets() { return playerBullets; }
    public List<Projectile> getEnemyProjectiles() { return enemyProjectiles; }
    public List<Entity> getEntities() { return entities; }
    public boolean isRunning() { return running; }
}