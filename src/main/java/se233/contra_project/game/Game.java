package se233.contra_project.game;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Game {

    private Canvas canvas;
    private GraphicsContext gc;

    public void start(Stage stage) {
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new javafx.scene.Group(canvas));
        stage.setScene(scene);

        // เริ่มเกมลูป
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                render();
            }
        };
        gameLoop.start();
    }

    private void update() {
        // TODO: เพิ่ม logic ของเกม เช่น การขยับ player
        // ตอนนี้ยังปล่อยว่างได้
    }

    private void render() {
        // ลบเฟรมเก่า
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // วาดตัวหนังสือไว้เช็คว่าเข้าเกมแล้วจริง
        gc.setFill(Color.WHITE);
        gc.fillText("GAME STARTED - Contra Clone", 300, 300);
    }
}
