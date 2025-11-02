package se233.contra_project.ui;

import javafx.animation.AnimationTimer;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import se233.contra_project.actors.Bullet;
import se233.contra_project.actors.Player;
import se233.contra_project.actors.Projectile;
import se233.contra_project.bosses.Boss;
import se233.contra_project.core.components.Sprite;
import se233.contra_project.core.components.SpriteAnimation;

import javax.swing.SwingUtilities;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import se233.contra_project.logging.LogConfig;

/**
 * Shared behaviour for individual stage screens.
 * Handles canvas setup, background rendering, boss lifecycle, and HUD overlays.
 */
public abstract class BaseStageScreen extends StackPane {
    private static final Logger LOGGER = LogConfig.getLogger(BaseStageScreen.class);
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private Image backgroundImage;

    private Boss boss;
    private BossHealthBar healthBar;
    private boolean showHealthBar = true;
    private boolean showInfo = true;
    private Player player;
    private final Set<KeyCode> activeKeys = EnumSet.noneOf(KeyCode.class);
    private final List<Bullet> playerBullets = new ArrayList<>();
    private final List<Projectile> bossProjectiles = new ArrayList<>();
    private Image bulletImage;
    private Image bulletImpactFrame;
    private Image bossProjectileImage;
    private final List<Image> bossProjectileFrames = new ArrayList<>();
    private final SwingNode hudNode;
    private final HUDOverlay hudOverlay;
    private int playerScore = 0;
    private int lastHudLives = -1;
    private int lastHudScore = -1;

    private boolean bossDefeatedNotified = false;
    private boolean exitTriggered = false;

    private static final int PLAYER_FRAME_PADDING = 2;
    private static final FrameRect[] PLAYER_IDLE_FRAMES = {
            frame(230, 13, 270, 125)   // row 1, column 3 (standing)
    };
    private static final FrameRect[] PLAYER_WALK_FRAMES = {
            frame(57, 13, 104, 125),   // column 1
            frame(220, 13, 227, 125),  // column 2
            frame(230, 13, 270, 125),  // column 3
            frame(389, 13, 428, 125),  // column 4
            frame(547, 13, 591, 125),  // column 5
            frame(711, 13, 717, 125),  // column 6
            frame(721, 13, 760, 125),  // column 7
            frame(882, 13, 921, 125),  // column 8
            frame(1037, 13, 1099, 125),// column 9
            frame(1201, 13, 1263, 125),// column 10
            frame(1372, 13, 1426, 125),// column 11
            frame(1528, 13, 1590, 125) // column 12
    };
    private static final FrameRect[] PLAYER_JUMP_FRAMES = {
            frame(1860, 202, 1906, 286),
            frame(2020, 202, 2055, 286),
            frame(2176, 202, 2221, 286),
            frame(2347, 202, 2381, 286)
    };
    private static final FrameRect[] PLAYER_PRONE_FRAMES = {
            frame(2325, 13, 2404, 125),
            frame(2488, 13, 2568, 125)
    };

    protected BaseStageScreen() {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        hudOverlay = new HUDOverlay();
        hudNode = new SwingNode();
        hudNode.setMouseTransparent(true);
        hudNode.setFocusTraversable(false);
        hudNode.setContent(hudOverlay);
        StackPane.setAlignment(hudNode, Pos.TOP_LEFT);

        getChildren().add(canvas);
        getChildren().add(hudNode);

        loadBackground();
        loadBulletSprite();
        loadBulletImpactSprite();
        loadBossProjectileSprite();
        initializeBoss();
        initializePlayer();
        initializeHealthBar();
        setupInputHandling();
        startAnimationTimer();
        draw(); // Ensure initial frame renders immediately
    }

    private void loadBackground() {
        try {
            String path = getBackgroundResourcePath();
            if (path != null) {
                try (InputStream stream = getClass().getResourceAsStream(path)) {
                    if (stream != null) {
                        backgroundImage = new Image(stream);
                    } else {
                        backgroundImage = null;
                        System.err.println("Background resource not found at " + path);
                    }
                }
            } else {
                backgroundImage = null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load stage background: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void loadBulletSprite() {
        try (InputStream stream = getClass().getResourceAsStream(getBulletSpritePath())) {
            if (stream != null) {
                bulletImage = new Image(stream);
            } else {
                System.err.println("Bullet sprite not found at " + getBulletSpritePath());
                bulletImage = null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load bullet sprite: " + e.getMessage());
            bulletImage = null;
        }
    }

    private void loadBulletImpactSprite() {
        bulletImpactFrame = null;
        String path = getBulletImpactSpritePath();
        if (path == null) {
            return;
        }

        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream != null) {
                Image sheet = new Image(stream);
                bulletImpactFrame = extractBulletImpactFrame(sheet);
                if (bulletImpactFrame == null) {
                    System.err.println("Failed to extract bullet impact frame from sheet at " + path);
                }
            } else {
                System.err.println("Bullet impact sprite not found at " + path);
            }
        } catch (Exception e) {
            System.err.println("Failed to load bullet impact sprite: " + e.getMessage());
        }
    }

    private void loadBossProjectileSprite() {
        bossProjectileFrames.clear();
        try (InputStream stream = getClass().getResourceAsStream(getBossProjectileSpritePath())) {
            if (stream != null) {
                bossProjectileImage = new Image(stream);
                bossProjectileFrames.addAll(extractBossProjectileFrames(bossProjectileImage));
                if (bossProjectileFrames.isEmpty() && bossProjectileImage != null) {
                    bossProjectileFrames.add(bossProjectileImage);
                }
            } else {
                System.err.println("Boss projectile sprite not found at " + getBossProjectileSpritePath());
                bossProjectileImage = null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load boss projectile sprite: " + e.getMessage());
            bossProjectileImage = null;
        }
    }

    private void initializeBoss() {
        boss = createBoss();
        if (boss == null) {
            throw new IllegalStateException("Stage must provide a boss instance.");
        }
        bossProjectiles.clear();
        bossDefeatedNotified = false;
        exitTriggered = false;
        onBossCreated(boss);
    }

    private void initializePlayer() {
        player = createPlayer();
        if (player == null) {
            throw new IllegalStateException("Stage must provide a player instance.");
        }

        playerBullets.clear();
        configurePlayerSprite(player);
        positionPlayer(player);
        onPlayerCreated(player);
        refreshHud();
    }

    private void initializeHealthBar() {
        healthBar = new BossHealthBar(canvas, boss);
    }

    private void configurePlayerSprite(Player player) {
        try (InputStream stream = getClass().getResourceAsStream(getPlayerSpriteSheetPath())) {
            if (stream == null) {
                System.err.println("Player sprite sheet not found at " + getPlayerSpriteSheetPath());
                return;
            }

            Image sheet = new Image(stream);
            PixelReader reader = sheet.getPixelReader();
            if (reader == null) {
                System.err.println("Player sprite sheet pixel reader was null.");
                return;
            }

            Image[] idleFrames = extractFrames(sheet, PLAYER_IDLE_FRAMES);
            Image[] walkFrames = extractFrames(sheet, PLAYER_WALK_FRAMES);
            Image[] jumpFrames = extractFrames(sheet, PLAYER_JUMP_FRAMES);
            Image[] proneFrames = extractFrames(sheet, PLAYER_PRONE_FRAMES);

            Image baseFrame;
            if (idleFrames.length > 0) {
                baseFrame = idleFrames[0];
            } else {
                int frameX = getPlayerSpriteFrameX();
                int frameY = getPlayerSpriteFrameY();
                int frameWidth = getPlayerSpriteFrameWidth();
                int frameHeight = getPlayerSpriteFrameHeight();
                baseFrame = new WritableImage(reader, frameX, frameY, frameWidth, frameHeight);
            }

            double scale = getPlayerSpriteScale();
            Sprite sprite = new Sprite(baseFrame, baseFrame.getWidth() * scale, baseFrame.getHeight() * scale);
            player.setSprite(sprite);

            player.setIdleAnimation(buildAnimation(idleFrames, 300, true));
            player.setWalkAnimation(buildAnimation(walkFrames, 80, true));
            player.setJumpAnimation(buildAnimation(jumpFrames, 120, false));
            player.setProneAnimation(buildAnimation(proneFrames, 250, true));
            if (player.getIdleAnimation() != null) {
                player.startIdleAnimation();
            }
        } catch (Exception e) {
            System.err.println("Failed to configure player sprite: " + e.getMessage());
        }
    }

    private void configureBulletSprite(Bullet bullet) {
        if (bulletImage == null) {
            return;
        }

        double scale = getBulletSpriteScale();
        Image frame = bulletImage;
        Sprite sprite = new Sprite(frame, frame.getWidth() * scale, frame.getHeight() * scale);
        bullet.setSprite(sprite);

        double adjustedX = bullet.getPosition().getX();
        double adjustedY = bullet.getPosition().getY() - sprite.getHeight() / 2.0;

        if (bullet.getVelocity().getX() < 0) {
            adjustedX -= sprite.getWidth();
        }

        bullet.setPosition(adjustedX, adjustedY);
        sprite.setPosition(adjustedX, adjustedY);
    }

    private Image extractBulletImpactFrame(Image sheet) {
        if (sheet == null) {
            return null;
        }

        PixelReader reader = sheet.getPixelReader();
        if (reader == null) {
            return null;
        }

        int columns = Math.max(1, getBulletImpactColumnCount());
        int columnIndex = Math.max(0, Math.min(getBulletImpactColumnIndex(), columns - 1));

        int sheetWidth = (int) Math.round(sheet.getWidth());
        int sheetHeight = (int) Math.round(sheet.getHeight());
        if (sheetWidth <= 0 || sheetHeight <= 0) {
            return null;
        }

        double exactFrameWidth = sheetWidth / (double) columns;
        int xStart = (int) Math.round(columnIndex * exactFrameWidth);
        int xEnd = (int) Math.round((columnIndex + 1) * exactFrameWidth);
        xStart = Math.max(0, Math.min(sheetWidth - 1, xStart));
        xEnd = Math.max(xStart + 1, Math.min(sheetWidth, xEnd));
        int frameWidth = xEnd - xStart;
        if (frameWidth <= 0) {
            return null;
        }

        return new WritableImage(reader, xStart, 0, frameWidth, sheetHeight);
    }

    private void triggerBulletImpact(Bullet bullet) {
        if (bullet == null || bullet.isImpactActive()) {
            return;
        }

        if (bulletImpactFrame == null) {
            bullet.setAlive(false);
            return;
        }

        double scale = getBulletImpactSpriteScale();
        double duration = getBulletImpactDuration();
        bullet.triggerImpact(bulletImpactFrame, scale, duration);
    }

    private void refreshHud() {
        if (hudOverlay == null) {
            return;
        }
        final int lives = player != null ? Math.max(0, player.getLives()) : 0;
        final int scoreSnapshot = playerScore;
        if (lives == lastHudLives && scoreSnapshot == lastHudScore) {
            return;
        }
        lastHudLives = lives;
        lastHudScore = scoreSnapshot;
        SwingUtilities.invokeLater(() -> hudOverlay.updateHUD(lives, scoreSnapshot));
    }

    protected void addScore(int points) {
        if (points <= 0) {
            LOGGER.fine(() -> String.format("Ignoring non-positive score increment: %d", points));
            return;
        }
        playerScore += points;
        LOGGER.info(() -> String.format(
                "Score increased by %d | total=%d",
                points, playerScore
        ));
        refreshHud();
    }

    private List<Image> extractBossProjectileFrames(Image sheet) {
        List<Image> frames = new ArrayList<>();
        if (sheet == null) {
            return frames;
        }

        PixelReader reader = sheet.getPixelReader();
        if (reader == null) {
            return frames;
        }

        int width = (int) Math.round(sheet.getWidth());
        int height = (int) Math.round(sheet.getHeight());
        final int minBlankRun = 2;
        final int padding = 2;

        int startContentRow = -1;
        int blankRun = 0;

        for (int y = 0; y < height; y++) {
            boolean hasContent = rowHasContent(reader, width, y);
            if (hasContent) {
                if (startContentRow == -1) {
                    startContentRow = y;
                }
                blankRun = 0;
            } else if (startContentRow != -1) {
                blankRun++;
                if (blankRun >= minBlankRun) {
                    int endRow = y - blankRun;
                    addBossProjectileFrame(frames, reader, width, height, startContentRow, endRow, padding);
                    startContentRow = -1;
                    blankRun = 0;
                }
            }
        }

        if (startContentRow != -1) {
            addBossProjectileFrame(frames, reader, width, height, startContentRow, height - 1, padding);
        }

        return frames;
    }

    private void addBossProjectileFrame(List<Image> frames, PixelReader reader, int width, int height,
                                        int startRow, int endRow, int padding) {
        if (startRow > endRow) {
            return;
        }

        int sliceY = Math.max(0, startRow - padding);
        int sliceHeight = Math.min(height - sliceY, endRow - startRow + 1 + padding * 2);
        if (sliceHeight <= 0) {
            return;
        }

        frames.add(new WritableImage(reader, 0, sliceY, width, sliceHeight));
    }

    private boolean rowHasContent(PixelReader reader, int width, int y) {
        for (int x = 0; x < width; x++) {
            int argb = reader.getArgb(x, y);
            int alpha = (argb >> 24) & 0xFF;
            if (alpha == 0) {
                continue;
            }

            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;

            if (r > 10 || g > 10 || b > 10) {
                return true;
            }
        }
        return false;
    }

    private Image chooseBossProjectileFrame() {
        if (!bossProjectileFrames.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(bossProjectileFrames.size());
            return bossProjectileFrames.get(index);
        }
        return bossProjectileImage;
    }

    private void configureBossProjectileSprite(Projectile projectile) {
        if (bossProjectileImage == null) {
            return;
        }

        double scale = getBossProjectileSpriteScale();
        Image frame = chooseBossProjectileFrame();
        if (frame == null) {
            return;
        }
        Sprite sprite = new Sprite(frame, frame.getWidth() * scale, frame.getHeight() * scale);
        projectile.setSprite(sprite);
        projectile.setWidth(sprite.getWidth());
        projectile.setHeight(sprite.getHeight());
        sprite.setPosition(projectile.getPosition().getX(), projectile.getPosition().getY());
    }

    private void positionPlayer(Player player) {
        double startX = getPlayerStartX();
        double floorY = getFloorY();
        double groundLevel = floorY - player.getHeight();

        player.setGroundLevel(groundLevel);
        player.setPosition(startX, groundLevel);
        player.stopMoving();

        if (player.getSprite() != null) {
            player.getSprite().setPosition(startX, groundLevel);
        }
    }

    private void applyPlayerInput() {
        if (player == null) {
            return;
        }

        boolean moveLeft = isKeyDown(KeyCode.LEFT) || isKeyDown(KeyCode.A);
        boolean moveRight = isKeyDown(KeyCode.RIGHT) || isKeyDown(KeyCode.D);

        if (moveLeft && !moveRight) {
            player.moveLeft();
        } else if (moveRight && !moveLeft) {
            player.moveRight();
        } else {
            player.stopMoving();
        }

        boolean crouch = (isKeyDown(KeyCode.DOWN) || isKeyDown(KeyCode.S)) && player.isOnGround();
        player.setProne(crouch);

        if (isKeyDown(KeyCode.SPACE) || isKeyDown(KeyCode.K)) {
            attemptPlayerShoot();
        }
    }

    private void clampPlayerWithinBounds() {
        if (player == null) {
            return;
        }

        double minX = getPlayerMinX();
        double maxX = getCanvasNode().getWidth() - player.getWidth() - getPlayerRightMargin();

        double clampedX = Math.max(minX, Math.min(player.getPosition().getX(), maxX));
        double clampedY = Math.min(player.getPosition().getY(), player.getGroundLevel());

        if (clampedX != player.getPosition().getX() || clampedY != player.getPosition().getY()) {
            player.setPosition(clampedX, clampedY);
            if (player.getSprite() != null) {
                player.getSprite().setPosition(clampedX, clampedY);
            }
        }
    }

    private void attemptPlayerShoot() {
        if (player == null) {
            return;
        }

        Bullet bullet = player.shoot();
        if (bullet != null) {
            configureBulletSprite(bullet);
            playerBullets.add(bullet);
        }
    }

    private void updateBullets(double deltaTime) {
        if (playerBullets.isEmpty()) {
            return;
        }

        Iterator<Bullet> iterator = playerBullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.update(deltaTime);

            if (!bullet.isAlive()) {
                iterator.remove();
                continue;
            }

            if (boss != null && boss.isAlive() && !bullet.isImpactActive() && bullet.collidesWith(boss)) {
                damageBoss(bullet.getDamage());
                triggerBulletImpact(bullet);
            }
        }
    }

    private void handlePlayerHit(Projectile projectile) {
        projectile.setAlive(false);
        if (player == null) {
            return;
        }

        player.loseLife();
        System.out.println("Player hit! Lives remaining: " + player.getLives());
        refreshHud();

        if (player.isAlive()) {
            positionPlayer(player);
        } else {
            System.out.println("Player defeated!");
        }
    }

    private void checkStageProgression() {
        if (!bossDefeatedNotified && boss != null && !boss.isAlive()) {
            bossDefeatedNotified = true;
            onBossDefeated();
        }

        if (bossDefeatedNotified && !exitTriggered && player != null) {
            double playerRight = player.getPosition().getX() + player.getWidth();
            double threshold = getStageExitTriggerX();
            if (playerRight >= threshold) {
                exitTriggered = true;
                onPlayerReachedExitAfterBossDefeat();
            }
        }
    }

    private void syncBossProjectiles() {
        if (boss == null) {
            return;
        }

        List<Projectile> bossList = boss.getProjectiles();
        boolean bossAlive = boss.isAlive();
        if (bossAlive) {
            for (Projectile projectile : bossList) {
                if (!bossProjectiles.contains(projectile)) {
                    configureBossProjectileSprite(projectile);
                    bossProjectiles.add(projectile);
                }
            }
        }

        Iterator<Projectile> iterator = bossProjectiles.iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            if (!projectile.isAlive() || !bossAlive) {
                projectile.setAlive(false);
                iterator.remove();
                continue;
            }

            if (player != null && player.isAlive() && projectile.collidesWith(player)) {
                handlePlayerHit(projectile);
                iterator.remove();
            }
        }

        if (!bossAlive) {
            bossList.clear();
        }
    }

    private boolean isKeyDown(KeyCode code) {
        return activeKeys.contains(code);
    }

    private void setupInputHandling() {
        setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            boolean firstPress = activeKeys.add(code);

            if (firstPress) {
                switch (code) {
                    case H:
                        showHealthBar = !showHealthBar;
                        break;
                    case I:
                        showInfo = !showInfo;
                        break;
                    case R:
                        resetBoss();
                        break;
                    case ESCAPE:
                        System.exit(0);
                        break;
                    default:
                        break;
                }
            }

            if (player != null && firstPress) {
                if (code == KeyCode.UP || code == KeyCode.W) {
                    player.jump();
                }
            }

            onCustomKeyPressed(code, firstPress);
            draw();
        });

        setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            activeKeys.remove(code);
            if (player != null && (code == KeyCode.DOWN || code == KeyCode.S)) {
                player.setProne(false);
            }
            onCustomKeyReleased(code);
        });

        setFocusTraversable(true);
        requestFocus();
    }

    private void resetBoss() {
        boss = createBoss();
        onBossCreated(boss);
        healthBar.setBoss(boss);
        if (player != null) {
            positionPlayer(player);
        }
        playerBullets.clear();
        bossProjectiles.clear();
        bossDefeatedNotified = false;
        exitTriggered = false;
        refreshHud();
    }

    private void startAnimationTimer() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (player != null) {
                    applyPlayerInput();
                    player.update(deltaTime);
                    clampPlayerWithinBounds();
                }

                updateBullets(deltaTime);

                if (boss != null) {
                    if (boss.isAlive()) {
                        boss.update(deltaTime);
                    }
                    syncBossProjectiles();
                }
                if (healthBar != null) {
                    healthBar.update(deltaTime);
                }

                checkStageProgression();
                refreshHud();

                draw();
            }
        };
        timer.start();
    }

    private void draw() {
        drawBackgroundLayer();
        drawPlayer();
        drawPlayerBullets();
        drawBossProjectiles();
        drawBoss();
        if (showHealthBar && healthBar != null) {
            healthBar.draw();
        }
        if (showInfo) {
            drawInfoPanel();
        }
        drawControlsHelp();
    }

    private void drawBackgroundLayer() {
        if (!drawCustomBackground(gc)) {
            if (backgroundImage != null) {
                gc.drawImage(backgroundImage, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            } else {
                gc.setFill(Color.DARKGRAY);
                gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            }
        }
    }

    private void drawPlayer() {
        if (player == null) {
            return;
        }

        try {
            if (player.getSprite() != null) {
                Image frame = player.getSprite().getImage();
                double drawX = player.getPosition().getX();
                double drawY = player.getPosition().getY();
                double drawWidth = player.getWidth();
                double drawHeight = player.getHeight();

                if (player.isFacingRight()) {
                    gc.drawImage(frame, 0, 0, frame.getWidth(), frame.getHeight(), drawX, drawY, drawWidth, drawHeight);
                } else {
                    gc.drawImage(frame, 0, 0, frame.getWidth(), frame.getHeight(), drawX + drawWidth, drawY, -drawWidth, drawHeight);
                }
            } else {
                gc.setFill(Color.RED);
                gc.fillRect(
                        player.getPosition().getX(),
                        player.getPosition().getY(),
                        player.getWidth(),
                        player.getHeight()
                );
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeRect(
                        player.getPosition().getX(),
                        player.getPosition().getY(),
                        player.getWidth(),
                        player.getHeight()
                );
            }

            // Bounding box intentionally omitted; use debug overlays if needed.
        } catch (Exception e) {
            System.err.println("Error drawing player: " + e.getMessage());
        }
    }

    private void drawPlayerBullets() {
        if (playerBullets.isEmpty()) {
            return;
        }

        for (Bullet bullet : playerBullets) {
            Sprite sprite = bullet.getSprite();
            if (sprite != null) {
                Image image = sprite.getImage();
                double drawX = bullet.getPosition().getX();
                double drawY = bullet.getPosition().getY();
                double drawWidth = sprite.getWidth();
                double drawHeight = sprite.getHeight();

                if (bullet.getVelocity().getX() >= 0) {
                    gc.drawImage(image, drawX, drawY, drawWidth, drawHeight);
                } else {
                    gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), drawX + drawWidth, drawY, -drawWidth, drawHeight);
                }
            } else {
                gc.setFill(Color.ORANGE);
                gc.fillOval(bullet.getPosition().getX(), bullet.getPosition().getY(), bullet.getWidth(), bullet.getHeight());
            }
        }
    }

    private void drawBossProjectiles() {
        if (bossProjectiles.isEmpty()) {
            return;
        }

        for (Projectile projectile : bossProjectiles) {
            Sprite sprite = projectile.getSprite();
            if (sprite != null) {
                Image image = sprite.getImage();
                double drawWidth = sprite.getWidth();
                double drawHeight = sprite.getHeight();
                double drawX = projectile.getPosition().getX();
                double drawY = projectile.getPosition().getY();

                gc.drawImage(image, drawX, drawY, drawWidth, drawHeight);
            } else {
                gc.setFill(Color.RED);
                gc.fillOval(projectile.getPosition().getX(),
                            projectile.getPosition().getY(),
                            projectile.getWidth(), projectile.getHeight());
            }
        }
    }

    private void drawBoss() {
        if (boss == null) {
            return;
        }

        try {
            if (boss.getSprite() != null) {
                gc.drawImage(
                        boss.getSprite().getImage(),
                        boss.getPosition().getX(),
                        boss.getPosition().getY(),
                        boss.getWidth(),
                        boss.getHeight()
                );
            } else {
                gc.setFill(Color.BLUE);
                gc.fillRect(
                        boss.getPosition().getX(),
                        boss.getPosition().getY(),
                        boss.getWidth(),
                        boss.getHeight()
                );
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeRect(
                        boss.getPosition().getX(),
                        boss.getPosition().getY(),
                        boss.getWidth(),
                        boss.getHeight()
                );
            }

        } catch (Exception e) {
            System.err.println("Error drawing boss: " + e.getMessage());
        }
    }

    private void drawInfoPanel() {
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(10, 10, 320, 135);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));

        double y = 30;
        gc.fillText(getStageTitle(), 20, y);
        y += 20;

        gc.fillText(getBossDisplayName(), 20, y);
        y += 20;

        if (boss != null) {
            gc.fillText("Health: " + boss.getHealth() + "/" + boss.getMaxHealth(), 20, y);
            y += 20;

            gc.fillText("State: " + boss.getCurrentState(), 20, y);
            y += 20;

            gc.fillText("Sprite: " + (boss.getSprite() != null ? "Loaded" : "Not Loaded"), 20, y);
            y += 15;

            String spriteInfo = getSpriteResourceInfo();
            if (spriteInfo != null && !spriteInfo.isEmpty()) {
                gc.fillText("Sprite Path: " + spriteInfo, 20, y);
            }
        }
    }

    private void drawControlsHelp() {
        gc.setFill(Color.color(0, 0, 0, 0.7));
        gc.fillRect(10, CANVAS_HEIGHT - 150, 360, 140);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));

        double y = CANVAS_HEIGHT - 130;
        gc.fillText(getStageTitle() + " Controls:", 20, y);
        y += 15;
        gc.fillText("Arrow Keys / A,D - Move", 20, y);
        y += 15;
        gc.fillText("W / UP - Jump", 20, y);
        y += 15;
        gc.fillText("S / DOWN - Prone", 20, y);
        y += 15;

        gc.fillText("H - Toggle Health Bar", 20, y);
        y += 15;
        gc.fillText("I - Toggle Info Panel", 20, y);
        y += 15;
        gc.fillText(getResetHint(), 20, y);
        y += 15;

        for (String hint : getAdditionalControlHints()) {
            gc.fillText(hint, 20, y);
            y += 15;
        }

        gc.fillText("ESC - Exit Game", 20, y);
    }

    protected void damageBoss(int damage) {
        if (boss == null || !boss.isAlive()) {
            return;
        }
        boss.takeDamage(damage);
        if (!boss.isAlive()) {
            addScore(boss.getScoreValue());
        }
        if (healthBar != null) {
            healthBar.triggerDamageFlash();
        }
    }

    protected Image getBackgroundImage() {
        return backgroundImage;
    }

    protected Canvas getCanvasNode() {
        return canvas;
    }

    protected Boss getBoss() {
        return boss;
    }

    protected Player getPlayer() {
        return player;
    }

    protected abstract Boss createBoss();

    protected Player createPlayer() {
        return new Player(getPlayerStartX(), 0);
    }

    protected abstract String getBackgroundResourcePath();

    protected abstract String getStageTitle();

    protected abstract String getBossDisplayName();

    protected abstract String getSpriteResourceInfo();

    protected abstract String getResetHint();

    protected abstract String[] getAdditionalControlHints();

    protected abstract void onBossCreated(Boss boss);

    protected void onPlayerCreated(Player player) {
        // Optional hook for subclasses
    }

    protected void onCustomKeyPressed(KeyCode code, boolean firstPress) {
        // Optional hook for subclasses
    }

    protected void onCustomKeyReleased(KeyCode code) {
        // Optional hook for subclasses
    }

    protected void onBossDefeated() {
        // Optional hook for subclasses
    }

    protected void onPlayerReachedExitAfterBossDefeat() {
        // Optional hook for subclasses
    }

    // Backwards-compatible hook; subclasses can override if needed
    protected void handleCustomKey(KeyCode code) {
        onCustomKeyPressed(code, true);
    }

    protected double getPlayerStartX() {
        return 80;
    }

    protected double getFloorY() {
        return CANVAS_HEIGHT - 140;
    }

    protected double getPlayerMinX() {
        return 20;
    }

    protected double getPlayerRightMargin() {
        return 20;
    }

    protected String getPlayerSpriteSheetPath() {
        return "/se233/contra_project/sprites/Characters1.png";
    }

    protected int getPlayerSpriteFrameX() {
        return 57;
    }

    protected int getPlayerSpriteFrameY() {
        return 38;
    }

    protected int getPlayerSpriteFrameWidth() {
        return 48;
    }

    protected int getPlayerSpriteFrameHeight() {
        return 86;
    }

    protected double getPlayerSpriteScale() {
        return 1.15;
    }

    protected String getBulletSpritePath() {
        return "/se233/contra_project/sprites/PLayerBullet.png";
    }

    protected double getBulletSpriteScale() {
        return 1.6;
    }

    protected String getBulletImpactSpritePath() {
        return "/se233/contra_project/sprites/Enemies bomb.png";
    }

    protected int getBulletImpactColumnCount() {
        return 3;
    }

    protected int getBulletImpactColumnIndex() {
        return 1;
    }

    protected double getBulletImpactSpriteScale() {
        return 1.6;
    }

    protected double getBulletImpactDuration() {
        return 0.25;
    }

    protected String getBossProjectileSpritePath() {
        return "/se233/contra_project/sprites/EnemiesBullet.png";
    }

    protected double getBossProjectileSpriteScale() {
        return 1.8;
    }

    protected double getStageExitTriggerX() {
        return getCanvasNode().getWidth() - 40;
    }

    private Image[] extractFrames(Image sheet, FrameRect[] rects) {
        if (sheet == null || rects == null || rects.length == 0) {
            return new Image[0];
        }

        PixelReader reader = sheet.getPixelReader();
        if (reader == null) {
            return new Image[0];
        }

        int sheetWidth = (int) sheet.getWidth();
        int sheetHeight = (int) sheet.getHeight();
        Image[] frames = new Image[rects.length];

        for (int i = 0; i < rects.length; i++) {
            FrameRect rect = rects[i];
            int startX = Math.max(0, rect.x - PLAYER_FRAME_PADDING);
            int startY = Math.max(0, rect.y - PLAYER_FRAME_PADDING);
            int width = Math.min(rect.width + PLAYER_FRAME_PADDING * 2, sheetWidth - startX);
            int height = Math.min(rect.height + PLAYER_FRAME_PADDING * 2, sheetHeight - startY);
            frames[i] = new WritableImage(reader, startX, startY, width, height);
        }

        return frames;
    }

    private SpriteAnimation buildAnimation(Image[] frames, long frameDurationMillis, boolean loop) {
        if (frames == null || frames.length == 0) {
            return null;
        }
        return new SpriteAnimation(frames, frameDurationMillis, loop);
    }

    private static FrameRect frame(int x1, int y1, int x2, int y2) {
        return new FrameRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
    }

    private static final class FrameRect {
        final int x;
        final int y;
        final int width;
        final int height;

        FrameRect(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Allow subclasses to draw custom backgrounds. Return true if handled, false to fallback.
     */
    protected boolean drawCustomBackground(GraphicsContext context) {
        return false;
    }
}
