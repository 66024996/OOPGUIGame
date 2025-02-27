package se233.project2.item;

import javafx.scene.image.Image;
import se233.project2.entity.Player;

public interface Item {
    String getName();
    String getDescription();
    void applyEffect();
    Image getSpriteImage();
}
