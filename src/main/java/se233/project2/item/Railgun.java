package se233.project2.item;

import javafx.scene.image.Image;
import se233.project2.controller.MainController;
import se233.project2.entity.Player;

public class Railgun implements Item {
    private final String description = "+1 Damage";
    private final Image spriteImage = new Image(RepairKit.class.getResourceAsStream("/se233/project2/Icons/Items/railgun.png"));
    private final String name = "Railgun";

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
        MainController.getPlayerInstance().setDamage(MainController.getPlayerInstance().getDamage() + 1);
    }

    @Override
    public Image getSpriteImage() {
        return spriteImage;
    }
}
