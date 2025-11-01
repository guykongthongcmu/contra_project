package se233.contra_project.ui;

import javafx.scene.input.KeyCode;
import se233.contra_project.bosses.Boss;
import se233.contra_project.bosses.Boss2;

/**
 * Stage 2 gameplay screen – Java Core boss encounter.
 */
public class Stage2Screen extends BaseStageScreen {
    private static final String BACKGROUND_PATH = "/se233/contra_project/ui/Stage2.png";
    private static final String STAGE_TITLE = "Stage 2 - Boss Fight";
    private static final String BOSS_NAME = "Boss2 - Java Core";
    private static final String SPRITE_INFO = "Bosses2Java.png";

    @Override
    protected Boss createBoss() {
        return new Boss2(0, 0);
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
        return "R - Reset Boss2";
    }

    @Override
    protected String[] getAdditionalControlHints() {
        return new String[] { "SPACE - Damage Boss2 (5 HP)" };
    }

    @Override
    protected void onBossCreated(Boss boss) {
        positionBossCenter(boss);
    }

    @Override
    protected void onCustomKeyPressed(KeyCode code, boolean firstPress) {
        if (code == KeyCode.SPACE && firstPress) {
            damageBoss(5);
        }
    }

    @Override
    protected void handleCustomKey(KeyCode code) {
        onCustomKeyPressed(code, true);
    }

    private void positionBossCenter(Boss boss) {
        double canvasWidth = getCanvasNode().getWidth();
        double canvasHeight = getCanvasNode().getHeight();

        double startX = (canvasWidth - boss.getWidth()) / 2;
        double startY = (canvasHeight - boss.getHeight()) / 2;

        boss.setPosition(Math.max(0, startX), Math.max(0, startY));
    }
}
