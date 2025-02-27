package se233.project2.item;

import javafx.scene.image.Image;
import se233.project2.controller.MainController;

public class RepairKit implements Item {
    private final String description = "+1 HP";
    private final Image spriteImage = new Image(RepairKit.class.getResourceAsStream("/se233/project2/Icons/Items/repairkit.png"));
    private final String name = "Repair kit";

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
        MainController.increaseHp();
    }

    @Override
    public Image getSpriteImage() {
        return spriteImage;
    }
}
