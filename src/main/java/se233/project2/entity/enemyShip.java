package se233.project2.entity;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import se233.project2.controller.MainController;
import se233.project2.gameLoop.GameLoop;
import se233.project2.util.Effects;

import java.util.Random;

import static se233.project2.gameLoop.GameLoop.*;
import static se233.project2.util.Hitbox.createHitbox;

public class enemyShip extends BaseCharacter{
    private double x;
    private double y;
    private ImageView sprite;
    private Rectangle hitbox;
    public static Timeline enemySpawner;
    private static Random random = new Random();
    private StackPane effectPane = new StackPane();
    private Timeline shootTimer;
    private static final double SHOOT_INTERVAL = 1000;
    private static Image bulletImage = new Image(GameLoop.class.getResourceAsStream("/se233/project2/Bullets/bullet_e.png"));
    private static double bulletSpeed = 10;

    public enemyShip(double x, double y) {
        this.x = x;
        this.y = y;
        this.setSpeed(5);
        this.sprite = new ImageView(new Image(GameLoop.class.getResourceAsStream("/se233/project2/Ships/enemyShip1.png")));
        this.sprite.setTranslateX(x);
        this.sprite.setTranslateY(y);
        this.setHp(Player.getLevel()/10 + 1);

        effectPane.setMaxSize(sprite.getBoundsInLocal().getWidth() * 2, sprite.getBoundsInLocal().getHeight() * 2);
        effectPane.setStyle("-fx-background-color: transparent; -fx-border-color: Transparent; -fx-border-width: 3;");
        effectPane.setTranslateX(sprite.getTranslateX());
        effectPane.setTranslateY(sprite.getTranslateY());
        effectPane.setMouseTransparent(true);

        hitbox = createHitbox(this.sprite.getBoundsInLocal().getWidth()-20, this.sprite.getBoundsInLocal().getHeight());
        hitbox.setTranslateX(this.sprite.getTranslateX());
        hitbox.setTranslateY(this.sprite.getTranslateY());
        mainPane.getChildren().addAll(hitbox, effectPane);
        effectPane.setVisible(true);

        updateRotation();
    }

    public void startShootingTimer() {
        if (shootTimer == null || shootTimer.getStatus() == Timeline.Status.STOPPED) {
            shootTimer = new Timeline(new KeyFrame(Duration.millis(SHOOT_INTERVAL), event -> shootBullet()));
            shootTimer.setCycleCount(Timeline.INDEFINITE);
            shootTimer.play();
        }
    }

    private void shootBullet() {
        if (running) {
            double spawnX = hitbox.getTranslateX();
            double spawnY = hitbox.getTranslateY();
            ImageView muzzleFlash = new ImageView(new Image(Player.class.getResourceAsStream("/se233/project2/Effects/Muzzle_e.png")));
            muzzleFlash.setFitHeight((muzzleFlash.getBoundsInLocal().getHeight() * (0.5 + random.nextDouble() * 0.5)) * 2);
            muzzleFlash.setFitWidth((muzzleFlash.getBoundsInLocal().getHeight() * (0.5 + random.nextDouble() * 0.5)) * 2);
            muzzleFlash.setPreserveRatio(true);
            effectPane.setAlignment(muzzleFlash, Pos.TOP_CENTER);

            effectPane.getChildren().add(muzzleFlash);

            Timeline muzzleFlashTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> muzzleFlash.setVisible(true)),
                    new KeyFrame(Duration.millis(100), e -> muzzleFlash.setVisible(false))
            );
            muzzleFlashTimeline.setCycleCount(1);
            muzzleFlashTimeline.setOnFinished(event -> effectPane.getChildren().remove(muzzleFlash));
            muzzleFlashTimeline.play();

            Bullet bullet = new Bullet(
                    bulletImage,
                    sprite.getRotate(),
                    bulletSpeed,
                    new Point2D(spawnX, spawnY),
                    Bullet.BulletType.ENEMY
            );

            bullet.addToPane(mainPane);
            GameLoop.addBullet(bullet);
        }
    }

    public void stopShootingTimer() {
        if (shootTimer != null) {
            shootTimer.stop();
        }
    }

    private void updateRotation() {
        double playerX = Player.getX();
        double playerY = Player.getY();
        double angleToPlayer = Math.toDegrees(Math.atan2(playerY - y, playerX - x)) + 90;
        this.sprite.setRotate(angleToPlayer);
        hitbox.setRotate(angleToPlayer);
        effectPane.setRotate(angleToPlayer);
    }

    public void updatePosition() {
        updateRotation();

        double playerX = Player.getX();
        double playerY = Player.getY();
        double deltaX = playerX - x;
        double deltaY = playerY - y;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        double directionX = deltaX / distance;
        double directionY = deltaY / distance;

        this.x += directionX * this.getSpeed();
        this.y += directionY * this.getSpeed();

        this.sprite.setTranslateX(x);
        this.sprite.setTranslateY(y);

        hitbox.setTranslateX(x);
        hitbox.setTranslateY(y);

        effectPane.setTranslateX(x);
        effectPane.setTranslateY(y);
    }

    public ImageView getSpriteView() {
        return this.sprite;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    public static void createEnemySpawner() {
        enemySpawner = new Timeline(new KeyFrame(Duration.seconds(random.nextInt(3) + 1), event -> {
            spawnEnemy();
            enemySpawner.setDelay(Duration.seconds(random.nextInt(3) + 1));
            enemySpawner.playFromStart();
        }));
        enemySpawner.setCycleCount(Timeline.INDEFINITE);
        enemySpawner.play();
    }

    private static void spawnEnemy() {
        double x = 0;
        double y = 0;
        switch (random.nextInt(4) + 1) {
            case 1:
                x = -952 + random.nextDouble() * (952 - (-952));
                y = -538;
                break;
            case 2:
                x = 952;
                y = -538 + random.nextDouble() * (538 - (-538));
                break;
            case 3:
                x = -952 + random.nextDouble() * (952 - (-952));
                y = 538;
                break;
            case 4:
                x = -952;
                y = -538 + random.nextDouble() * (538 - (-538));
                break;
        }

        enemyShip newEnemy = new enemyShip(x, y);
        newEnemy.getSpriteView().setMouseTransparent(true);
        Platform.runLater(() -> {
            enemies.add(newEnemy);
            mainPane.getChildren().add(newEnemy.getSpriteView());
        });
    }

    public void destroyed() {
        Platform.runLater(() -> {
            Effects.playShipExplosionAnimation(sprite.getTranslateX(), sprite.getTranslateY(), sprite.getFitHeight(), sprite.getFitWidth());
            mainPane.getChildren().remove(this.getHitbox());
            mainPane.getChildren().remove(this.getSpriteView());
            mainPane.getChildren().remove(effectPane);
            this.stopShootingTimer();
        });
        MainController.gainExp(4);
    }

    public StackPane getEffectPane() {
        return effectPane;
    }
}
