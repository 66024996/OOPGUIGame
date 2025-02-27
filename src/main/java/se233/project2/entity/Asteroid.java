package se233.project2.entity;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import se233.project2.controller.MainController;
import se233.project2.gameLoop.GameLoop;
import se233.project2.util.Effects;

import java.util.Random;

import static se233.project2.gameLoop.GameLoop.asteroids;
import static se233.project2.gameLoop.GameLoop.mainPane;
import static se233.project2.util.Hitbox.createCircularHitbox;
import static se233.project2.util.Hitbox.createHitbox;

public class Asteroid extends BaseCharacter {
    private double x;
    private double y;
    private double directionX;
    private double directionY;
    private ImageView sprite;
    private Circle hitbox;
    public static Timeline asteroidSpawner;
    private static Random random = new Random();
    private double rotate = 1;
    private Timeline countdownTimer;
    private boolean isOutOfBounds = false;
    private boolean countdownRunning = false;

    public Asteroid(double x, double y, int speed, Image spriteImage) {
        this.x = x;
        this.y = y;
        this.setSpeed(speed);
        this.sprite = new ImageView(spriteImage);
        this.sprite.setTranslateX(x);
        this.sprite.setTranslateY(y);
        this.sprite.setFitWidth(this.sprite.getBoundsInLocal().getWidth() * (0.9 + random.nextDouble() * 0.2));
        this.sprite.setFitHeight(this.sprite.getBoundsInLocal().getHeight() * (0.9 + random.nextDouble() * 0.2));
        this.sprite.setRotate(random.nextDouble() * 360);
        rotate = (0.8 + random.nextDouble() * 0.3);
        this.setHp(Player.getLevel()/10 + 1);
        if(this.getSpriteView().getBoundsInLocal().getHeight() >= 200 || this.getSpriteView().getBoundsInLocal().getWidth() >= 200){
            this.setHp((Player.getLevel()/10 + 1) * 2);
        }

        hitbox = createCircularHitbox(this.sprite.getBoundsInLocal().getWidth(), this.sprite.getBoundsInLocal().getHeight());
        hitbox.setTranslateX(this.sprite.getTranslateX());
        hitbox.setTranslateY(this.sprite.getTranslateY());
        hitbox.setRotate(this.sprite.getRotate());
        mainPane.getChildren().add(hitbox);

        double playerX = Player.getX() * (0.9 + random.nextDouble() * 0.2);
        double playerY = Player.getY() * (0.9 + random.nextDouble() * 0.2);
        calculateDirection(playerX, playerY);
    }

    private void calculateDirection(double targetX, double targetY) {
        double deltaX = targetX - this.x;
        double deltaY = targetY - this.y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        this.directionX = deltaX / distance;
        this.directionY = deltaY / distance;
    }

    public void updatePosition() {
        this.x += directionX * this.getSpeed();
        this.y += directionY * this.getSpeed();

        this.sprite.setTranslateX(x);
        this.sprite.setTranslateY(y);
        this.sprite.setRotate(this.sprite.getRotate() + 0.1 * rotate);

        hitbox.setRotate(this.sprite.getRotate());
        hitbox.setTranslateX(x);
        hitbox.setTranslateY(y);

        if (x > 932 || x < -932 || y > 518 || y < -518) {
            if (!isOutOfBounds) {
                startCountdown();
                isOutOfBounds = true;
            }
        } else {
            if (isOutOfBounds) {
                stopCountdown();
                isOutOfBounds = false;
            }
        }
    }

    private void startCountdown() {
        countdownTimer = new Timeline(new KeyFrame(Duration.seconds(10), event -> {
            mainPane.getChildren().remove(sprite);
            mainPane.getChildren().remove(hitbox);
            asteroids.remove(this);
            countdownRunning = false;
        }));
        countdownTimer.setCycleCount(1);
        countdownTimer.play();
        countdownRunning = true;
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
            countdownRunning = false;
        }
    }


    public void pauseCountdown() {
        if (countdownTimer != null && countdownRunning) {
            countdownTimer.pause();
        }
    }

    public void resumeCountdown() {
        if (countdownTimer != null && !countdownRunning) {
            countdownTimer.play();
            countdownRunning = true;
        }
    }

    public ImageView getSpriteView() {
        return this.sprite;
    }

    public Circle getHitbox(){
        return hitbox;
    }

    public static void createAsteroidSpawner() {
        asteroidSpawner = new Timeline(new KeyFrame(Duration.seconds(random.nextInt(3) + 1), event -> {
            spawnAsteroid();
            asteroidSpawner.setDelay(Duration.seconds(random.nextInt(3) + 1));
            asteroidSpawner.playFromStart();
        }));
        asteroidSpawner.setCycleCount(Timeline.INDEFINITE);
        asteroidSpawner.play();
    }

    private static void spawnAsteroid() {
        Image asteroidImage = null;
        double offset = 0;
        double sizeMultiplier = 0.9 + random.nextDouble() * 0.2;
        double x = 0;
        double y = 0;
        switch (random.nextInt(4) + 1) {
            case 1:
                asteroidImage = new Image(GameLoop.class.getResourceAsStream("/se233/project2/Asteroids/Asteroids#01.png"));
                offset = 228 * sizeMultiplier;
                break;
            case 2:
                asteroidImage = new Image(GameLoop.class.getResourceAsStream("/se233/project2/Asteroids/Asteroids#02.png"));
                offset = 225 * sizeMultiplier;
                break;
            case 3:
                asteroidImage = new Image(GameLoop.class.getResourceAsStream("/se233/project2/Asteroids/Asteroids#03.png"));
                offset = 98 * sizeMultiplier;
                break;
            case 4:
                asteroidImage = new Image(GameLoop.class.getResourceAsStream("/se233/project2/Asteroids/Asteroids#04.png"));
                offset = 108 * sizeMultiplier;
                break;
        }
        switch (random.nextInt(4) + 1) {
            case 1:
                x = -952 + random.nextDouble() * (952 - (-952));
                y = -538 - offset;
                break;
            case 2:
                x = 952 + offset;
                y = -538 + random.nextDouble() * (538 - (-538));
                break;
            case 3:
                x = -952 + random.nextDouble() * (952 - (-952));
                y = 538 + offset;
                break;
            case 4:
                x = -952 - offset;
                y = -538 + random.nextDouble() * (538 - (-538));
                break;
        }

        Asteroid newAsteroid = new Asteroid(x, y, random.nextInt(2) + 1, asteroidImage);
        newAsteroid.getSpriteView().setMouseTransparent(true);
        Platform.runLater(() -> {
            asteroids.add(newAsteroid);
            mainPane.getChildren().add(newAsteroid.getSpriteView());
        });
    }

    public void destroyed(){
        Platform.runLater(() -> {
            Effects.playAsteroidExplosionAnimation(sprite.getTranslateX(), sprite.getTranslateY(), sprite.getFitHeight(), sprite.getFitWidth());
            mainPane.getChildren().remove(this.getHitbox());
            mainPane.getChildren().remove(this.getSpriteView());
        });
        if(this.getSpriteView().getBoundsInLocal().getHeight() >= 200 || this.getSpriteView().getBoundsInLocal().getWidth() >= 200){
            MainController.gainExp(4);
        }else{
            MainController.gainExp(2);
        }
    }
}
