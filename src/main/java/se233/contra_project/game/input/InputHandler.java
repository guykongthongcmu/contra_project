package se233.contra_project.game.input;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles keyboard input for the game
 */
public class InputHandler {
    private Set<Integer> pressedKeys;

    public enum Key {
        LEFT, RIGHT, UP, DOWN, SHOOT, JUMP, PRONE
    }

    public InputHandler() {
        this.pressedKeys = new HashSet<>();
    }

    /**
     * Update input state - called each frame
     */
    public void update() {
        // This method can be used for any per-frame input processing
        // For now, key states are managed by keyPressed/keyReleased events
    }

    /**
     * Handle key press event
     */
    public void keyPressed(int keyCode) {
        pressedKeys.add(keyCode);
    }

    /**
     * Handle key release event
     */
    public void keyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
    }

    /**
     * Check if a specific key is currently pressed
     */
    public boolean isKeyPressed(Key key) {
        switch (key) {
            case LEFT:
                return pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A);
            case RIGHT:
                return pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D);
            case UP:
                return pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W);
            case DOWN:
                return pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S);
            case SHOOT:
                return pressedKeys.contains(KeyEvent.VK_SPACE) || pressedKeys.contains(KeyEvent.VK_Z);
            case JUMP:
                return pressedKeys.contains(KeyEvent.VK_UP) || pressedKeys.contains(KeyEvent.VK_W);
            case PRONE:
                return pressedKeys.contains(KeyEvent.VK_DOWN) || pressedKeys.contains(KeyEvent.VK_S);
            default:
                return false;
        }
    }

    /**
     * Check if a specific key code is pressed
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * Clear all pressed keys
     */
    public void clear() {
        pressedKeys.clear();
    }
}