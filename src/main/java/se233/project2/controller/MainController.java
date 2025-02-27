package se233.project2.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.project2.entity.*;
import se233.project2.gameLoop.GameLoop;
import se233.project2.item.*;
import se233.project2.util.Effects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static se233.project2.entity.Player.*;
import static se233.project2.gameLoop.GameLoop.*;
import static se233.project2.util.UISetup.*;

public class MainController {
    @FXML
    private ImageView backgroundImage, shipSprite_ImageView, card1Sprite, card2Sprite, card3Sprite;
    @FXML
    private Button start_Button, exit_Button, menu_Button, continue_Button, menu_Button1, left_Button, right_Button;
    @FXML
    private StackPane mainPane_StackPane;
    @FXML
    private Label paused_Label, gameover_Label, level_Label, exp_Label, card1Name, card2Name, card3Name, card1Description, card2Description, card3Description, condition3_Label;
    @FXML
    private HBox hp_HBox;
    @FXML
    private GridPane cards_GridPane;
    @FXML
    private VBox card1_VBox, card2_VBox, card3_VBox, playerStatus_VBox, pauseMenu_VBox, mainMenu_VBox, gameover_VBox;

    private static HBox hp;
    private static VBox gameoverScreen, pauseMenuScreen;
    private static Label exp, level, firstCardName, secondCardName, thirdCardName, firstCardDescription, secondCardDescription, thirdCardDescription;
    private static Player playerInstance;
    private static final Logger logger = LogManager.getLogger(Player.class);
    private static GridPane cardsPane;
    public static int selectedIndex = 0;
    public static int expAmplifier = 0;
    public static boolean condition_3 = false;
    private static ImageView firstCardSprite, secondCardSprite, thirdCardSprite;
    static List<Item> itemList = new ArrayList<>();
    private Image[] shipSprites = {
            new Image(MainController.class.getResourceAsStream("/se233/project2/Ships/playerShip1.png")),
            new Image(MainController.class.getResourceAsStream("/se233/project2/Ships/playerShip2.png")),
            new Image(MainController.class.getResourceAsStream("/se233/project2/Ships/playerShip3.png"))
    };

    @FXML
    public void initialize() {
        setPane();
        playerInstance = new Player();
        setupUI(backgroundImage, start_Button, exit_Button, pauseMenu_VBox, paused_Label, continue_Button, menu_Button, gameover_VBox, gameover_Label, menu_Button1, playerStatus_VBox,
                level_Label, exp_Label, left_Button, right_Button, cards_GridPane, card1_VBox, card2_VBox, card3_VBox, card1Sprite, card2Sprite, card3Sprite, card1Name, card2Name, card3Name,
                card1Description, card2Description, card3Description, condition3_Label);
        shipSprite_ImageView.setImage(new Image(MainController.class.getResourceAsStream("/se233/project2/Ships/playerShip1.png")));
        exit_Button.setOnAction(e -> handleExit());
        start_Button.setOnAction(e -> gameStart());
        continue_Button.setOnAction(e -> gameResume());
        menu_Button.setOnAction(e -> returnToMenu());
        menu_Button1.setOnAction(e -> returnToMenu());
        right_Button.setOnAction(e -> nextSprite());
        left_Button.setOnAction(e -> previousSprite());
        card3_VBox.setOnMouseReleased(e -> selectCard3());
        card1_VBox.setOnMouseReleased(e -> selectCard1());
        card2_VBox.setOnMouseReleased(e -> selectCard2());
    }

    private void handleExit() {
        Platform.exit();
        System.exit(0);
    }

    private void gameStart() {
        hp_HBox.getChildren().clear();
        hide(mainMenu_VBox);
        if (!mainPane_StackPane.getChildren().contains(getPlayerShip())) {
            mainPane_StackPane.getChildren().add(getPlayerShip());
        }
        changeShip();
        getPlayerShip().setVisible(true);
        level_Label.setText("Lv." + Player.getLevel());
        exp_Label.setText(Player.getExp() + "/" + Player.getMaxExp());

        for(int i = 1; i <= getPlayerInstance().getHp(); i++){
            increaseHp();
        }
        show(playerStatus_VBox);
        mainPane_StackPane.addEventFilter(MouseEvent.ANY, Player::handleMouseMove);
        mainPane_StackPane.getScene().setOnKeyReleased(Player::handleKeyRelease);
        mainPane_StackPane.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if(!gameoverScreen.isVisible() && !cardsPane.isVisible()){
                    handlePauseResume();
                }
            }
            else{
                handleKeyPress(event);
            }
        });
        startGameLoop();
    }

    private static void handlePauseResume() {
        if (running) {
            gamePause();
        } else {
            gameResume();
        }
    }

    private static void gamePause() {
        Platform.runLater(() -> {
            stopGameLoop();
            show(pauseMenuScreen);
        });
    }

    private static void gameResume() {
        Platform.runLater(() -> {
            startGameLoop();
            hide(pauseMenuScreen);
        });
    }

    private void returnToMenu(){
        Platform.runLater(() -> {
            hide(pauseMenu_VBox);
            hide(gameover_VBox);
            hide(playerStatus_VBox);
            stopGameLoop();
            effectPane.getChildren().clear();
            getPlayerShip().setVisible(false);

            for (Bullet bullet : bullets) {
                bullet.removeBullet(mainPane_StackPane);
                mainPane_StackPane.getChildren().remove(bullet.getHitbox());
            }
            bullets.clear();
            for(Asteroid asteroid : asteroids){
                mainPane_StackPane.getChildren().remove(asteroid.getSpriteView());
                mainPane_StackPane.getChildren().remove(asteroid.getHitbox());
            }
            asteroids.clear();
            for(enemyShip enemy : enemies){
                mainPane_StackPane.getChildren().remove(enemy.getSpriteView());
                mainPane_StackPane.getChildren().remove(enemy.getHitbox());
                mainPane_StackPane.getChildren().remove(enemy.getEffectPane());
            }
            enemies.clear();
            for(Boss boss : bosses){
                mainPane_StackPane.getChildren().remove(boss.getSpriteView());
                mainPane_StackPane.getChildren().remove(boss.getHitbox());
                mainPane_StackPane.getChildren().remove(boss.getEffectPane());
            }
            bosses.clear();

            for (Timeline effect : activeEffect) {
                effect.stop();
                effect.getKeyFrames().clear();
            }
            activeEffect.clear();

            for (ImageView explosion : Effects.activeEffectView) {
                mainPane_StackPane.getChildren().remove(explosion);
            }
            Effects.activeEffectView.clear();
            for (ImageView effect : activeAbilityView) {
                mainPane_StackPane.getChildren().remove(effect);
            }
            activeAbilityView.clear();
            if (abilityAnimationTimeline != null) {
                abilityAnimationTimeline.stop();
            }
            if (specialAbilityActive) {
                specialAbilityActive = false;
                stopShootingTimer();
                startShootingTimer();

                if (abilityTimer != null) {
                    abilityTimer.stop();
                }
            }
            onCooldown = false;
            currentCooldownProgress = 1.0;
            abilityCooldownBar.setProgress(currentCooldownProgress);
            abilityCooldownBar.setVisible(false);
        });
        show(mainMenu_VBox);
    }

    public void setPane(){
        mainPane = mainPane_StackPane;
        mainPane_P = mainPane_StackPane;
        hp = hp_HBox;
        gameoverScreen = gameover_VBox;
        pauseMenuScreen = pauseMenu_VBox;
        exp = exp_Label;
        level = level_Label;
        cardsPane = cards_GridPane;
        firstCardSprite = card1Sprite;
        firstCardName = card1Name;
        firstCardDescription = card1Description;
        secondCardSprite = card2Sprite;
        secondCardName = card2Name;
        secondCardDescription = card2Description;
        thirdCardSprite = card3Sprite;
        thirdCardName = card3Name;
        thirdCardDescription = card3Description;
    }

    public static void increaseHp(){
        Platform.runLater(() -> {
            if(hp.getChildren().size() < 15){
                hp.getChildren().add(new ImageView(new Image(MainController.class.getResourceAsStream("/se233/project2/Icons/hp.png"))));
            }
            if(getPlayerInstance().getHp() < 15){
                getPlayerInstance().setHp(getPlayerInstance().getHp() + 1);
            }
        });
    }

    public static void reduceHp(){
        if(!hp.getChildren().isEmpty()){
            if(Player.isShielded()){
                return;
            }

            Player.setShielded(true);
            hp.getChildren().removeFirst();
            getPlayerInstance().setHp(getPlayerInstance().getHp() - 1);
            Timeline flicker = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> getPlayerShip().setOpacity(0.5)), // Set opacity to 0.5
                    new KeyFrame(Duration.millis(100), e -> getPlayerShip().setOpacity(1)), // Restore opacity
                    new KeyFrame(Duration.millis(200), e -> getPlayerShip().setOpacity(0.5)), // Set opacity to 0.5
                    new KeyFrame(Duration.millis(300), e -> getPlayerShip().setOpacity(1)), // Restore opacity
                    new KeyFrame(Duration.millis(400), e -> getPlayerShip().setOpacity(0.5)), // Set opacity to 0.5
                    new KeyFrame(Duration.millis(500), e -> getPlayerShip().setOpacity(1)), // Restore opacity
                    new KeyFrame(Duration.millis(600), e -> getPlayerShip().setOpacity(0.5)), // Set opacity to 0.5
                    new KeyFrame(Duration.millis(700), e -> getPlayerShip().setOpacity(1)), // Restore opacity
                    new KeyFrame(Duration.millis(800), e -> getPlayerShip().setOpacity(0.5)), // Set opacity to 0.5
                    new KeyFrame(Duration.millis(900), e -> getPlayerShip().setOpacity(1)), // Restore opacity
                    new KeyFrame(Duration.millis(1000), e -> getPlayerShip().setOpacity(0.5)),
                    new KeyFrame(Duration.millis(1000), e -> getPlayerShip().setOpacity(1))
            );
            flicker.setCycleCount(1);
            flicker.play();
            activeEffect.add(flicker);
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                Player.setShielded(false);
                activeEffect.remove(flicker);
            });
            pause.play();
        }
        if(hp.getChildren().isEmpty()){
            Platform.runLater(() -> {
                show(gameoverScreen);
                Player.getPlayerShip().setVisible(false);
                stopGameLoop();
                Effects.playShipExplosionAnimation();
                logger.info("Game Over, level: {}", Player.getLevel());
            });
        }
    }

    public static void gainExp(int gain){
        if(Player.getExp() + gain + expAmplifier >= Player.getMaxExp()){
            Player.setExp(Player.getExp() + gain + expAmplifier - Player.getMaxExp());
            Player.setMaxExp((int) (getMaxExp() * 1.1));
            Player.setLevel(Player.getLevel() + 1);
            Platform.runLater(() -> {
                exp.setText(Player.getExp() + "/" + Player.getMaxExp());
                level.setText("Lv." + Player.getLevel());
            });
            levelUp();
        }else{
            Player.setExp(Player.getExp() + gain + expAmplifier);
            Platform.runLater(() -> {
                exp.setText(Player.getExp() + "/" + Player.getMaxExp());
            });
        }
        logger.info("Player gain {} exp", gain);
    }

    public static void levelUp(){
        Platform.runLater(() -> {
            randomiseCard();
            stopGameLoop();
            show(cardsPane);
        });
        if(Player.getLevel() % 5 == 0){
            Boss.spawnBoss();
        }
        logger.info("Player level up to {}", Player.getLevel());
    }

    private static void randomiseCard(){
        cardsPane.toFront();
        Collections.shuffle(itemList);
        for (int i = 0; i < 3; i++) {
            if (itemList.get(i) instanceof RepairKit && getPlayerInstance().getHp() >= 15) {
                itemList.remove(i);
                itemList.add(new RepairKit());
                break;
            }
        }
        firstCardSprite.setImage(itemList.getFirst().getSpriteImage());
        firstCardName.setText(itemList.getFirst().getName());
        firstCardDescription.setText(itemList.getFirst().getDescription());
        secondCardSprite.setImage(itemList.get(1).getSpriteImage());
        secondCardName.setText(itemList.get(1).getName());
        secondCardDescription.setText(itemList.get(1).getDescription());
        thirdCardSprite.setImage(itemList.get(2).getSpriteImage());
        thirdCardName.setText(itemList.get(2).getName());
        thirdCardDescription.setText(itemList.get(2).getDescription());
    }

    private void nextSprite() {
        selectedIndex = (selectedIndex + 1) % shipSprites.length;
        if(selectedIndex == 2 && !condition_3){
            start_Button.setDisable(true);
            shipSprite_ImageView.setOpacity(0.2);
            show(condition3_Label);
        }else{
            start_Button.setDisable(false);
            shipSprite_ImageView.setOpacity(1);
            hide(condition3_Label);
        }
        shipSprite_ImageView.setImage(shipSprites[selectedIndex]);
    }


    private void previousSprite() {
        selectedIndex = (selectedIndex - 1 + shipSprites.length) % shipSprites.length;
        if(selectedIndex == 2 && !condition_3){
            start_Button.setDisable(true);
            shipSprite_ImageView.setOpacity(0.2);
            show(condition3_Label);
        }else{
            start_Button.setDisable(false);
            shipSprite_ImageView.setOpacity(1);
            hide(condition3_Label);
        }
        shipSprite_ImageView.setImage(shipSprites[selectedIndex]);
    }

    public static Player getPlayerInstance() {
        return playerInstance;
    }

    private void changeShip(){
        resetPlayerPosition();
        itemList.clear();
        itemList.addAll(List.of(new eComputing(), new RepairKit(), new Railgun(), new Thruster()));
        Player.setMaxExp(10);
        Player.setExp(0);
        Player.setLevel(1);
        expAmplifier = 0;
        playerInstance.setHp(3);
        playerInstance.setSpeed(5);
        SHOOT_INTERVAL = 1000;
        ABILITY_DURATION = 5000;
        ABILITY_COOLDOWN = 10000;
        bulletSpeed = 20;
        playerInstance.setDamage(1);
        Player.getPlayerShip().setImage(shipSprites[selectedIndex]);
        Player.getHitbox().setWidth(Player.getPlayerShip().getBoundsInLocal().getWidth()-20);
        Player.getHitbox().setHeight(Player.getPlayerShip().getBoundsInLocal().getHeight());
        effectPane.setMaxSize(spaceshipView.getBoundsInLocal().getWidth() * 2, spaceshipView.getBoundsInLocal().getHeight() * 2);
        switch (selectedIndex){
            case 0:
                playerInstance.setSpeed(7);
                break;
            case 1:
                SHOOT_INTERVAL = 2000;
                bulletSpeed = 50;
                break;
            case 2:
                ABILITY_DURATION = 5000;
                increaseHp();
                playerInstance.setDamage(2);
                break;
        }
    }

    private void selectCard1(){
        itemList.getFirst().applyEffect();
        Platform.runLater(() -> {
            hide(cardsPane);
            startGameLoop();
        });
    }

    private void selectCard2(){
        itemList.get(1).applyEffect();
        Platform.runLater(() -> {
            hide(cardsPane);
            startGameLoop();
        });
    }

    private void selectCard3(){
        itemList.get(2).applyEffect();
        Platform.runLater(() -> {
            hide(cardsPane);
            startGameLoop();
        });
    }
}