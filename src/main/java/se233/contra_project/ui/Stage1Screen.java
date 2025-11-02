package se233.contra_project.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import se233.contra_project.bosses.Boss;
import se233.contra_project.bosses.Boss1;

/**
 * Stage 1 gameplay screen – Defense Wall boss encounter.
 */
public class Stage1Screen extends BaseStageScreen {
    private static final String BACKGROUND_PATH = "/se233/contra_project/ui/Stage1.png";
    private static final String STAGE_TITLE = "Stage 1 - Boss Fight";
    private static final String BOSS_NAME = "Boss1 - Defense Wall";
    private static final String SPRITE_INFO = "Bosses1DefenseWall.png";
    private static final double GROUND_OFFSET = 140.0;

    @Override
    protected Boss createBoss() {
        return new Boss1(0, 0);
    }

    @Override
    protected String getBackgroundResourcePath() {
        return BACKGROUND_PATH;
    }

    @Override
    protected String getStageTitle() {
        return STAGE_TITLE;
    }

    @Override
    protected String getBossDisplayName() {
        return "Boss: " + BOSS_NAME;
    }

    @Override
    protected String getSpriteResourceInfo() {
        return SPRITE_INFO;
    }

    @Override
    protected String getResetHint() {
        return "R - Reset Boss1";
    }

    @Override
    protected String[] getAdditionalControlHints() {
        return new String[] { "SPACE - Shoot" };
    }

    @Override
    protected void onBossCreated(Boss boss) {
        positionBossRightEdge(boss);
    }

    @Override
    protected void handleCustomKey(KeyCode code) {
        onCustomKeyPressed(code, true);
    }

    @Override
    protected boolean drawCustomBackground(GraphicsContext context) {
        javafx.scene.image.Image background = getBackgroundImage();
        if (background == null) {
            return false;
        }

        double destWidth = getCanvasNode().getWidth();
        double destHeight = getCanvasNode().getHeight();
        double imgWidth = background.getWidth();
        double imgHeight = background.getHeight();

        if (imgWidth <= 0 || imgHeight <= 0) {
            return false;
        }

        double scale = destHeight / imgHeight;
        double scaledWidth = imgWidth * scale;
        double offsetX = (destWidth - scaledWidth) / 2.0;

        context.drawImage(background, offsetX, 0, scaledWidth, destHeight);
        return true;
    }

    private void positionBossRightEdge(Boss boss) {
        double canvasWidth = getCanvasNode().getWidth();
        double canvasHeight = getCanvasNode().getHeight();

        double marginRight = 20;
        double startX = Math.max(marginRight, canvasWidth - boss.getWidth() - marginRight);

        double startY = canvasHeight - boss.getHeight() - GROUND_OFFSET;
        if (startY < 0) {
            startY = 0;
        }

        boss.setPosition(startX, startY);
    }

    @Override
    protected double getFloorY() {
        return getCanvasNode().getHeight() - GROUND_OFFSET;
    }
}
