package se233.contra_project.game.input;

/**
 * Input Handler for managing keyboard and mouse input
 * Handles key states and input events for the game
 */
public class InputHandler {
    private boolean[] keyStates;
    private boolean[] keyPressed;
    private boolean[] keyReleased;

    // Key constants
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;
    public static final int KEY_SPACE = 4;
    public static final int KEY_ENTER = 5;
    public static final int KEY_ESCAPE = 6;
    public static final int KEY_SHIFT = 7;
    public static final int KEY_CONTROL = 8;
    public static final int KEY_ALT = 9;
    public static final int KEY_A = 10;
    public static final int KEY_B = 11;
    public static final int KEY_C = 12;
    public static final int KEY_D = 13;
    public static final int KEY_E = 14;
    public static final int KEY_F = 15;
    public static final int KEY_G = 16;
    public static final int KEY_H = 17;
    public static final int KEY_I = 18;
    public static final int KEY_J = 19;
    public static final int KEY_K = 20;
    public static final int KEY_L = 21;
    public static final int KEY_M = 22;
    public static final int KEY_N = 23;
    public static final int KEY_O = 24;
    public static final int KEY_P = 25;
    public static final int KEY_Q = 26;
    public static final int KEY_R = 27;
    public static final int KEY_S = 28;
    public static final int KEY_T = 29;
    public static final int KEY_U = 30;
    public static final int KEY_V = 31;
    public static final int KEY_W = 32;
    public static final int KEY_X = 33;
    public static final int KEY_Y = 34;
    public static final int KEY_Z = 35;
    public static final int KEY_0 = 36;
    public static final int KEY_1 = 37;
    public static final int KEY_2 = 38;
    public static final int KEY_3 = 39;
    public static final int KEY_4 = 40;
    public static final int KEY_5 = 41;
    public static final int KEY_6 = 42;
    public static final int KEY_7 = 43;
    public static final int KEY_8 = 44;
    public static final int KEY_9 = 45;

    private static final int NUM_KEYS = 46;

    public InputHandler() {
        keyStates = new boolean[NUM_KEYS];
        keyPressed = new boolean[NUM_KEYS];
        keyReleased = new boolean[NUM_KEYS];
    }

    /**
     * Update input state - call this once per frame
     */
    public void update() {
        // Reset pressed/released states
        for (int i = 0; i < NUM_KEYS; i++) {
            keyPressed[i] = false;
            keyReleased[i] = false;
        }
    }

    /**
     * Set key pressed state
     */
    public void setKeyPressed(int keyCode, boolean pressed) {
        if (keyCode >= 0 && keyCode < NUM_KEYS) {
            if (pressed && !keyStates[keyCode]) {
                keyPressed[keyCode] = true;
            } else if (!pressed && keyStates[keyCode]) {
                keyReleased[keyCode] = true;
            }
            keyStates[keyCode] = pressed;
        }
    }

    /**
     * Handle key pressed event (for compatibility with existing code)
     */
    public void keyPressed(int keyCode) {
        setKeyPressed(convertKeyCode(keyCode), true);
    }

    /**
     * Handle key released event (for compatibility with existing code)
     */
    public void keyReleased(int keyCode) {
        setKeyPressed(convertKeyCode(keyCode), false);
    }

    /**
     * Check if key is currently pressed
     */
    public boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < NUM_KEYS) {
            return keyStates[keyCode];
        }
        return false;
    }

    /**
     * Check if key was just pressed this frame
     */
    public boolean isKeyJustPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < NUM_KEYS) {
            return keyPressed[keyCode];
        }
        return false;
    }

    /**
     * Check if key was just released this frame
     */
    public boolean isKeyJustReleased(int keyCode) {
        if (keyCode >= 0 && keyCode < NUM_KEYS) {
            return keyReleased[keyCode];
        }
        return false;
    }

    /**
     * Convert AWT key code to internal key code
     */
    public static int convertKeyCode(int awtKeyCode) {
        switch (awtKeyCode) {
            case java.awt.event.KeyEvent.VK_UP: return KEY_UP;
            case java.awt.event.KeyEvent.VK_DOWN: return KEY_DOWN;
            case java.awt.event.KeyEvent.VK_LEFT: return KEY_LEFT;
            case java.awt.event.KeyEvent.VK_RIGHT: return KEY_RIGHT;
            case java.awt.event.KeyEvent.VK_SPACE: return KEY_SPACE;
            case java.awt.event.KeyEvent.VK_ENTER: return KEY_ENTER;
            case java.awt.event.KeyEvent.VK_ESCAPE: return KEY_ESCAPE;
            case java.awt.event.KeyEvent.VK_SHIFT: return KEY_SHIFT;
            case java.awt.event.KeyEvent.VK_CONTROL: return KEY_CONTROL;
            case java.awt.event.KeyEvent.VK_ALT: return KEY_ALT;
            case java.awt.event.KeyEvent.VK_A: return KEY_A;
            case java.awt.event.KeyEvent.VK_B: return KEY_B;
            case java.awt.event.KeyEvent.VK_C: return KEY_C;
            case java.awt.event.KeyEvent.VK_D: return KEY_D;
            case java.awt.event.KeyEvent.VK_E: return KEY_E;
            case java.awt.event.KeyEvent.VK_F: return KEY_F;
            case java.awt.event.KeyEvent.VK_G: return KEY_G;
            case java.awt.event.KeyEvent.VK_H: return KEY_H;
            case java.awt.event.KeyEvent.VK_I: return KEY_I;
            case java.awt.event.KeyEvent.VK_J: return KEY_J;
            case java.awt.event.KeyEvent.VK_K: return KEY_K;
            case java.awt.event.KeyEvent.VK_L: return KEY_L;
            case java.awt.event.KeyEvent.VK_M: return KEY_M;
            case java.awt.event.KeyEvent.VK_N: return KEY_N;
            case java.awt.event.KeyEvent.VK_O: return KEY_O;
            case java.awt.event.KeyEvent.VK_P: return KEY_P;
            case java.awt.event.KeyEvent.VK_Q: return KEY_Q;
            case java.awt.event.KeyEvent.VK_R: return KEY_R;
            case java.awt.event.KeyEvent.VK_S: return KEY_S;
            case java.awt.event.KeyEvent.VK_T: return KEY_T;
            case java.awt.event.KeyEvent.VK_U: return KEY_U;
            case java.awt.event.KeyEvent.VK_V: return KEY_V;
            case java.awt.event.KeyEvent.VK_W: return KEY_W;
            case java.awt.event.KeyEvent.VK_X: return KEY_X;
            case java.awt.event.KeyEvent.VK_Y: return KEY_Y;
            case java.awt.event.KeyEvent.VK_Z: return KEY_Z;
            case java.awt.event.KeyEvent.VK_0: return KEY_0;
            case java.awt.event.KeyEvent.VK_1: return KEY_1;
            case java.awt.event.KeyEvent.VK_2: return KEY_2;
            case java.awt.event.KeyEvent.VK_3: return KEY_3;
            case java.awt.event.KeyEvent.VK_4: return KEY_4;
            case java.awt.event.KeyEvent.VK_5: return KEY_5;
            case java.awt.event.KeyEvent.VK_6: return KEY_6;
            case java.awt.event.KeyEvent.VK_7: return KEY_7;
            case java.awt.event.KeyEvent.VK_8: return KEY_8;
            case java.awt.event.KeyEvent.VK_9: return KEY_9;
            default: return -1;
        }
    }

    /**
     * Get movement input as normalized vector
     */
    public double[] getMovementInput() {
        double x = 0;
        double y = 0;

        if (isKeyPressed(KEY_LEFT) || isKeyPressed(KEY_A)) x -= 1;
        if (isKeyPressed(KEY_RIGHT) || isKeyPressed(KEY_D)) x += 1;
        if (isKeyPressed(KEY_UP) || isKeyPressed(KEY_W)) y -= 1;
        if (isKeyPressed(KEY_DOWN) || isKeyPressed(KEY_S)) y += 1;

        // Normalize diagonal movement
        if (x != 0 && y != 0) {
            double length = Math.sqrt(x * x + y * y);
            x /= length;
            y /= length;
        }

        return new double[]{x, y};
    }

    /**
     * Check if any movement key is pressed
     */
    public boolean isMovementInput() {
        return isKeyPressed(KEY_LEFT) || isKeyPressed(KEY_RIGHT) ||
               isKeyPressed(KEY_UP) || isKeyPressed(KEY_DOWN) ||
               isKeyPressed(KEY_A) || isKeyPressed(KEY_D) ||
               isKeyPressed(KEY_W) || isKeyPressed(KEY_S);
    }

    /**
     * Check if action key is pressed
     */
    public boolean isActionPressed() {
        return isKeyPressed(KEY_SPACE) || isKeyPressed(KEY_ENTER);
    }

    /**
     * Check if action key was just pressed
     */
    public boolean isActionJustPressed() {
        return isKeyJustPressed(KEY_SPACE) || isKeyJustPressed(KEY_ENTER);
    }

    /**
     * Check if pause/escape is pressed
     */
    public boolean isPausePressed() {
        return isKeyPressed(KEY_ESCAPE);
    }

    /**
     * Check if pause/escape was just pressed
     */
    public boolean isPauseJustPressed() {
        return isKeyJustPressed(KEY_ESCAPE);
    }
}