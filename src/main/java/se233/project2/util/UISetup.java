package se233.project2.util;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class UISetup {
    private static final Rectangle2D screenBounds = Screen.getPrimary().getBounds();
    public static void setupUI(ImageView background, Button start_Button, Button exit_Button, VBox pauseMenu_VBox, Label paused_Label, Button continue_Button, Button menu_Button,
                               VBox gameover_VBox, Label gameover_Label, Button menu_button1, VBox playerStatus_VBox, Label level_Label, Label exp_Label, Button left_Button, Button right_Button, GridPane cars_GridPane, VBox card1_VBox, VBox card2_VBox, VBox card3_VBox,
                               ImageView card1Sprite, ImageView card2Sprite, ImageView card3Sprite, Label card1Name, Label card2Name, Label card3Name, Label card1Description, Label card2Description, Label card3Description, Label conditon3_Label){
        try{
            background.setFitWidth(screenBounds.getWidth());
            background.setFitHeight(screenBounds.getHeight());
            mainMenuButtonSetup(start_Button, exit_Button);
            hide(pauseMenu_VBox);
            hide(gameover_VBox);
            hide(cars_GridPane);
            gameoverScreenSetup(gameover_Label, menu_button1);
            pauseMenuSetup(paused_Label, continue_Button, menu_Button);
            hide(playerStatus_VBox);
            playerStatusSetup(level_Label, exp_Label);
            arrowButtonSetup(left_Button, right_Button);
            cardVboxSetup(card1_VBox, card1Sprite, card1Name, card1Description);
            cardVboxSetup(card2_VBox, card2Sprite, card2Name, card2Description);
            cardVboxSetup(card3_VBox, card3Sprite, card3Name, card3Description);
            hide(conditon3_Label);
            conditonLabelSetup(conditon3_Label);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void mainMenuButtonSetup(Button start_Button, Button exit_Button) throws FileNotFoundException {
        start_Button.setPrefWidth(screenBounds.getWidth()/5);
        exit_Button.setPrefWidth(screenBounds.getWidth()/5);
        start_Button.setPrefHeight(screenBounds.getHeight()/7);
        exit_Button.setPrefHeight(screenBounds.getHeight()/7);
        setupButton(exit_Button);
        setupButton(start_Button);
    }

    private static void cardVboxSetup(VBox card, ImageView sprite, Label name, Label description) throws FileNotFoundException{
        name.setFont(Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 50));
        description.setFont(Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 30));
        card.setOnMouseEntered(event -> {
            card.setStyle("-fx-background-color: #222222; -fx-border-color: White; -fx-opacity: 1");
            sprite.setOpacity(1);
            name.setStyle("-fx-text-fill: white;");
            description.setStyle("-fx-text-fill: white;");
        });
        card.setOnMouseExited(event -> {
            card.setStyle("-fx-background-color: #111111; -fx-border-color: Gray; -fx-opacity: 0.9");
            sprite.setOpacity(0.9);
            name.setStyle("-fx-text-fill: #888888;");
            description.setStyle("-fx-text-fill: #888888;");
        });
    }

    private static void conditonLabelSetup(Label conditon) throws FileNotFoundException{
        Font customFont = Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 30);
        conditon.setFont(customFont);
    }

    private static void arrowButtonSetup(Button left, Button right) throws FileNotFoundException{
        Font customFont = Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 200);
        left.setFont(customFont);
        right.setFont(customFont);

        left.setOnMouseEntered(event -> left.setStyle("-fx-text-fill: rgba(211, 211, 211, 0.7); -fx-background-color: Transparent;"));
        left.setOnMouseExited(event -> left.setStyle("-fx-text-fill: white; -fx-background-color: Transparent;"));

        right.setOnMouseEntered(event -> right.setStyle("-fx-text-fill: rgba(211, 211, 211, 0.7); -fx-background-color: Transparent;"));
        right.setOnMouseExited(event -> right.setStyle("-fx-text-fill: white; -fx-background-color: Transparent;"));
    }

    private static void gameoverScreenSetup(Label gameover_Label, Button menu_button) throws FileNotFoundException{
        Font customFont = Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 200);
        gameover_Label.setFont(customFont);
        setupButton(menu_button);
    }

    private static void pauseMenuSetup(Label pauseMenu_Label, Button continue_Button, Button menu_Button) throws FileNotFoundException{
        Font customFont = Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 200);
        pauseMenu_Label.setFont(customFont);
        setupButton(continue_Button);
        setupButton(menu_Button);
    }

    private static void setupButton(Button button) throws FileNotFoundException{
        button.setStyle("-fx-background-color: transparent; -fx-border-color: White; -fx-border-width: 10;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: rgba(211, 211, 211, 0.1); -fx-border-color: White; -fx-border-width: 10;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: transparent; -fx-border-color: White; -fx-border-width: 10;"));
        Font customFont = Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 70);
        button.setFont(customFont);
    }

    private static void playerStatusSetup(Label level_Label, Label exp_Label) throws FileNotFoundException{
        Font customFont = Font.loadFont(UISetup.class.getResourceAsStream("/se233/project2/MenuFont.ttf"), 50);
        level_Label.setFont(customFont);
        exp_Label.setFont(customFont);
    }

    public static void hide(Node node){
        node.setVisible(false);
        node.setManaged(false);
    }

    public static void show(Node node){
        node.setVisible(true);
        node.setManaged(true);
    }
}
