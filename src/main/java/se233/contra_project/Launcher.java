package se233.contra_project;

import se233.contra_project.ui.StartScreen;

import javax.swing.*;

public class Launcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Contra");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            StartScreen startScreen = new StartScreen();
            window.add(startScreen);

            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            startScreen.requestFocusInWindow();

        });
    }
}