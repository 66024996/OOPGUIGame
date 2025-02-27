package se233.project2.util;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Hitbox {
    public static Rectangle createHitbox(double width, double height) {
        Rectangle hitbox = new Rectangle(width, height);
        hitbox.setStroke(Color.RED);
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setVisible(false);
        hitbox.setMouseTransparent(true);
        return hitbox;
    }

    public static Circle createCircularHitbox(double width, double height) {
        double radius = Math.min(width, height) / 2.0;
        Circle hitbox = new Circle(radius);
        hitbox.setStroke(Color.RED);
        hitbox.setFill(Color.TRANSPARENT);
        hitbox.setVisible(false);
        hitbox.setMouseTransparent(true);
        return hitbox;
    }
}
