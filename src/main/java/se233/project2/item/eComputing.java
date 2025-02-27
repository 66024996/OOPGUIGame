package se233.project2.item;

import javafx.scene.image.Image;
import se233.project2.controller.MainController;

public class eComputing implements Item {
    private final String description = "+1 Exp Gain";
    private final Image spriteImage = new Image(RepairKit.class.getResourceAsStream("/se233/project2/Icons/Items/enhancedComputing.png"));
    private final String name = "Enhanced\nComputing";

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
        MainController.expAmplifier = MainController.expAmplifier + 1;
    }

    @Override
    public Image getSpriteImage() {
        return spriteImage;
    }
}
