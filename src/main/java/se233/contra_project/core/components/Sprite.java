package se233.contra_project.core.components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Component for handling sprite images and rendering
 */
public class Sprite {
    private Image image;
    private ImageView imageView;
    private double width;
    private double height;

    public Sprite(String imagePath, double width, double height) {
        this.image = new Image(imagePath);
        this.imageView = new ImageView(image);
        this.width = width;
        this.height = height;
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
    }

    public Sprite(Image image, double width, double height) {
        this.image = image;
        this.imageView = new ImageView(image);
        this.width = width;
        this.height = height;
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
    }

    /**
     * Set the position of the sprite
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void setPosition(double x, double y) {
        imageView.setX(x);
        imageView.setY(y);
    }

    /**
     * Set the rotation of the sprite
     * @param angle rotation angle in degrees
     */
    public void setRotation(double angle) {
        imageView.setRotate(angle);
    }

    /**
     * Set visibility of the sprite
     * @param visible true to show, false to hide
     */
    public void setVisible(boolean visible) {
        imageView.setVisible(visible);
    }

    // Getters
    public Image getImage() { return image; }
    public ImageView getImageView() { return imageView; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}