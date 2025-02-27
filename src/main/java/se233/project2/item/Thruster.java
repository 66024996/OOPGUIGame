package se233.project2.item;

import javafx.scene.image.Image;
import se233.project2.controller.MainController;

public class Thruster implements Item {
    private final String description = "+1 Speed";
    private final Image spriteImage = new Image(RepairKit.class.getResourceAsStream("/se233/project2/Icons/Items/thruster.png"));
    private final String name = "Thruster";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void applyEffect() {
        MainController.getPlayerInstance().setSpeed(MainController.getPlayerInstance().getSpeed() + 1);
    }

    @Override
    public Image getSpriteImage() {
        return spriteImage;
    }
}
