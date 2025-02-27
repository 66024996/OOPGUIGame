package se233.project2.entity;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import se233.project2.controller.MainController;
import se233.project2.gameLoop.GameLoop;

import java.util.*;

import static se233.project2.controller.MainController.getPlayerInstance;
import static se233.project2.controller.MainController.selectedIndex;
import static se233.project2.gameLoop.GameLoop.*;
import static se233.project2.util.Effects.playAbilityEffect1Animation;
import static se233.project2.util.Hitbox.createHitbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.project2.util.Effects;

public class Player extends BaseCharacter {
    private static final Logger logger = LogManager.getLogger(Player.class);
    static Image spaceshipImage;
    static Image bulletImage;
    public static ImageView spaceshipView;
    private static Rectangle hitbox;
    public static Set<KeyCode> activeKeys = new HashSet<>();
    public static StackPane mainPane_P;
    private static double lastMouseX = 0;
    private static double lastMouseY = 0;
    private static Timeline shootTimer;
    public static double SHOOT_INTERVAL;
    public static double bulletSpeed;
    public static StackPane effectPane = new StackPane();
    private static Random random = new Random();
    public static boolean shielded = false;
    public static int level = 1;
    public static int exp = 0;
    public static int max_exp = 10;
    public static double ABILITY_DURATION;
    public static double ABILITY_COOLDOWN;
    public static boolean specialAbilityActive = false;
    public static boolean onCooldown = false;
    public static Timeline abilityTimer;
    public static Timeline cooldownTimer;
    public static ProgressBar abilityCooldownBar;
    public static Timeline progressTimeline;
    public static double currentCooldownProgress = 0;
    public static List<ImageView> activeAbilityView = new ArrayList<>();
    public static Timeline abilityAnimationTimeline;

    {
        try {
            this.setSpeed(7);
            spaceshipImage = new Image(Player.class.getResourceAsStream("/se233/project2/Ships/playerShip1.png"));
            spaceshipView = new ImageView(spaceshipImage);
            spaceshipView.setMouseTransparent(true);
            bulletImage = new Image(Player.class.getResourceAsStream("/se233/project2/Bullets/bullet_p.png"));
            hitbox = createHitbox(spaceshipView.getBoundsInLocal().getWidth() - 20, spaceshipView.getBoundsInLocal().getHeight());
            hitbox.setTranslateX(spaceshipView.getTranslateX());
            hitbox.setTranslateY(spaceshipView.getTranslateY());
            effectPane.setMaxSize(spaceshipView.getBoundsInLocal().getWidth() * 2, spaceshipView.getBoundsInLocal().getHeight() * 2);
            effectPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 3;");
            effectPane.setTranslateX(spaceshipView.getTranslateX());
            effectPane.setTranslateY(spaceshipView.getTranslateY());
            effectPane.setMouseTransparent(true);
            abilityCooldownBar = new ProgressBar(0);
            abilityCooldownBar.setPrefWidth(50);
            abilityCooldownBar.setPrefHeight(9);
            abilityCooldownBar.setVisible(false);
            if(mainPane != null){
                mainPane.getChildren().add(abilityCooldownBar);
                mainPane.getChildren().addAll(hitbox, effectPane);
            }
            effectPane.setVisible(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void startShootingTimer() {
        if (shootTimer == null || shootTimer.getStatus() == Timeline.Status.STOPPED) {
            switch (selectedIndex) {
                case 0:
                    shootTimer = new Timeline(
                            new KeyFrame(Duration.ZERO, e -> shootBullet(spaceshipView.getRotate())),
                            new KeyFrame(Duration.millis(SHOOT_INTERVAL))
                    );
                    break;
                case 1:
                    shootTimer = new Timeline(
                            new KeyFrame(Duration.ZERO, e -> shootBullet(spaceshipView.getRotate())),
                            new KeyFrame(Duration.millis(100), e -> shootBullet(spaceshipView.getRotate())),
                            new KeyFrame(Duration.millis(SHOOT_INTERVAL))
                    );
                    break;
                case 2:
                    shootTimer = new Timeline(
                            new KeyFrame(Duration.ZERO, e -> {
                                shootBullet(spaceshipView.getRotate());
                                shootBullet(spaceshipView.getRotate()-20);
                                shootBullet(spaceshipView.getRotate()+20);
                            }),
                            new KeyFrame(Duration.millis(SHOOT_INTERVAL))
                    );
                    break;
            }
            assert shootTimer != null;
            shootTimer.setCycleCount(Timeline.INDEFINITE);
            shootTimer.play();
        }
    }

    public static void checkCollision() {
        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
        while (asteroidIterator.hasNext()) {
            Asteroid asteroid = asteroidIterator.next();
            if(Player.hitbox.getBoundsInParent().intersects(asteroid.getHitbox().getBoundsInParent())){
                MainController.reduceHp();
                asteroidIterator.remove();
                asteroid.destroyed();
                break;
            }
        }

        Iterator<enemyShip> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()){
            enemyShip enemy = enemyIterator.next();
            if(Player.hitbox.getBoundsInParent().intersects(enemy.getHitbox().getBoundsInParent())){
                MainController.reduceHp();
                enemyIterator.remove();
                enemy.destroyed();
                break;
            }
        }

        Iterator<Boss> bossIterator = bosses.iterator();
        while (bossIterator.hasNext()){
            Boss boss = bossIterator.next();
            if(Player.hitbox.getBoundsInParent().intersects(boss.getHitbox().getBoundsInParent())){
                MainController.reduceHp();
                break;
            }
        }
    }

    private static void shootBullet(double angle) {
        if (running) {
            double spawnX = hitbox.getTranslateX();
            double spawnY = hitbox.getTranslateY();
            ImageView muzzleFlash = new ImageView(new Image(Player.class.getResourceAsStream("/se233/project2/Effects/Muzzle_p.png")));
            muzzleFlash.setFitHeight((muzzleFlash.getBoundsInLocal().getHeight() * (0.5 + random.nextDouble() * 0.5)) * 2);
            muzzleFlash.setFitWidth((muzzleFlash.getBoundsInLocal().getHeight() * (0.5 + random.nextDouble() * 0.5)) * 2);
            muzzleFlash.setPreserveRatio(true);

            effectPane.setAlignment(muzzleFlash, Pos.TOP_CENTER);

            effectPane.getChildren().add(muzzleFlash);

            Timeline muzzleFlashTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, e -> muzzleFlash.setVisible(true)),
                    new KeyFrame(Duration.millis(50), e -> muzzleFlash.setVisible(false))
            );
            muzzleFlashTimeline.setCycleCount(1);
            muzzleFlashTimeline.setOnFinished(event -> effectPane.getChildren().remove(muzzleFlash));

            muzzleFlashTimeline.play();

            Bullet bullet = new Bullet(
                    bulletImage,
                    angle,
                    bulletSpeed,
                    new Point2D(spawnX, spawnY),
                    Bullet.BulletType.PLAYER
            );

            bullet.addToPane(mainPane_P);
            GameLoop.addBullet(bullet);
            logger.info("Player fire a bullet - X: {}, Y: {}", spaceshipView.getTranslateX(), spaceshipView.getTranslateY());
        }
    }

    public static void stopShootingTimer() {
        if (shootTimer != null) {
            shootTimer.stop();
        }
    }

    public static double getX(){
        return spaceshipView.getTranslateX();
    }

    public static double getY(){
        return spaceshipView.getTranslateY();
    }

    public static void updateMovement() {
        if (running) {
            Player.checkCollision();
            spaceshipView.toFront();
            if(!activeAbilityView.isEmpty()){
                activeAbilityView.getFirst().toFront();
            }
            double deltaX = 0;
            double deltaY = 0;

            int speed = getPlayerInstance().getSpeed();
            if(activeKeys.contains(KeyCode.SHIFT)){
                speed = speed/2;
            }

            if (activeKeys.contains(KeyCode.W)) {
                deltaY -= speed;
            }
            if (activeKeys.contains(KeyCode.S)) {
                deltaY += speed;
            }
            if (activeKeys.contains(KeyCode.A)) {
                deltaX -= speed;
            }
            if (activeKeys.contains(KeyCode.D)) {
                deltaX += speed;
            }

            double paneWidth = spaceshipView.getParent().getLayoutBounds().getWidth();
            double paneHeight = spaceshipView.getParent().getLayoutBounds().getHeight();

            double currentX = spaceshipView.getLayoutX() + spaceshipView.getTranslateX();
            double currentY = spaceshipView.getLayoutY() + spaceshipView.getTranslateY();

            double newTranslateX = currentX + deltaX;
            double newTranslateY = currentY + deltaY;

            double spaceshipWidth = spaceshipView.getBoundsInLocal().getWidth();
            double spaceshipHeight = spaceshipView.getBoundsInLocal().getHeight();

            newTranslateX = Math.max(0, Math.min(newTranslateX, paneWidth - spaceshipWidth));
            newTranslateY = Math.max(0, Math.min(newTranslateY, paneHeight - spaceshipHeight));

            double finalDeltaX = newTranslateX - currentX;
            double finalDeltaY = newTranslateY - currentY;

            updateRotation();
            Platform.runLater(() -> {
                spaceshipView.setTranslateX(spaceshipView.getTranslateX() + finalDeltaX);
                spaceshipView.setTranslateY(spaceshipView.getTranslateY() + finalDeltaY);

                hitbox.setTranslateX(spaceshipView.getTranslateX());
                hitbox.setTranslateY(spaceshipView.getTranslateY());

                effectPane.setTranslateX(spaceshipView.getTranslateX());
                effectPane.setTranslateY(spaceshipView.getTranslateY());

                abilityCooldownBar.setTranslateX(spaceshipView.getTranslateX());
                abilityCooldownBar.setTranslateY(spaceshipView.getTranslateY() + 40);

                for(ImageView effect : activeAbilityView){
                    effect.setTranslateX(spaceshipView.getTranslateX());
                    effect.setTranslateY(spaceshipView.getTranslateY());
                }
                logger.trace("Player moved to position - X: {}, Y: {}", spaceshipView.getTranslateX(), spaceshipView.getTranslateY());
            });
        }
    }

    public static void activateSpecialAbility() {
        if (specialAbilityActive || onCooldown || !running) {
            return;
        }

        logger.info("Activate Special Ability - X: {}, Y: {}", spaceshipView.getTranslateX(), spaceshipView.getTranslateY());
        specialAbilityActive = true;
        switch (selectedIndex){
            case 0:
                double originalShootInterval = SHOOT_INTERVAL;
                double originalBulletSpeed = bulletSpeed;
                SHOOT_INTERVAL /= 2;
                bulletSpeed = bulletSpeed * 2;

                stopShootingTimer();
                startShootingTimer();

                abilityAnimationTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.15), event -> {
                            if (running) {
                                playAbilityEffect1Animation();
                            }
                        })
                );
                abilityAnimationTimeline.setCycleCount(Timeline.INDEFINITE);
                abilityAnimationTimeline.play();

                abilityTimer = new Timeline(new KeyFrame(Duration.millis(ABILITY_DURATION), event -> {
                    SHOOT_INTERVAL = originalShootInterval;
                    bulletSpeed = originalBulletSpeed;
                    specialAbilityActive = false;

                    stopShootingTimer();
                    startShootingTimer();
                    startCooldown();

                    abilityAnimationTimeline.stop();
                }));
                abilityTimer.setCycleCount(1);
                abilityTimer.play();
                break;
            case 1:
                Image missileImage = new Image(Player.class.getResourceAsStream("/se233/project2/Bullets/Missile.png"));
                Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                while (asteroidIterator.hasNext()) {
                    Asteroid asteroid = asteroidIterator.next();

                    Bullet bullet = new Bullet(
                            missileImage,
                            new Point2D(getPlayerShip().getTranslateX(), getPlayerShip().getTranslateY()),
                            new Point2D(asteroid.getHitbox().getTranslateX(), asteroid.getHitbox().getTranslateY()),
                            50,
                            Bullet.BulletType.PLAYER
                    );

                    bullet.addToPane(mainPane_P);
                    GameLoop.addBullet(bullet);
                }

                Iterator<enemyShip> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()){
                    enemyShip enemy = enemyIterator.next();
                    Bullet bullet = new Bullet(
                            missileImage,
                            new Point2D(getPlayerShip().getTranslateX(), getPlayerShip().getTranslateY()),
                            new Point2D(enemy.getHitbox().getTranslateX(), enemy.getHitbox().getTranslateY()),
                            bulletSpeed,
                            Bullet.BulletType.PLAYER
                    );

                    bullet.addToPane(mainPane_P);
                    GameLoop.addBullet(bullet);
                }

                Iterator<Boss> bossIterator1 = bosses.iterator();
                while (bossIterator1.hasNext()){
                    Boss boss = bossIterator1.next();
                    Bullet bullet = new Bullet(
                            missileImage,
                            new Point2D(getPlayerShip().getTranslateX(), getPlayerShip().getTranslateY()),
                            new Point2D(boss.getHitbox().getTranslateX(), boss.getHitbox().getTranslateY()),
                            bulletSpeed,
                            Bullet.BulletType.PLAYER
                    );

                    bullet.addToPane(mainPane_P);
                    GameLoop.addBullet(bullet);
                }
                Effects.playMissleEffectAnimation();
                specialAbilityActive = false;
                startCooldown();
                break;
            case 2:
                shielded = true;
                Effects.playAbilityEffect2Animation();
                abilityTimer = new Timeline(new KeyFrame(Duration.millis(ABILITY_DURATION-2), event -> {
                    specialAbilityActive = false;
                    shielded = false;
                    startCooldown();

                }));
                abilityTimer.setCycleCount(1);
                abilityTimer.play();
                break;
        }
    }

    private static void startCooldown() {
        onCooldown = true;
        abilityCooldownBar.setVisible(true);

        cooldownTimer = new Timeline(new KeyFrame(Duration.millis(ABILITY_COOLDOWN), event -> {
            onCooldown = false;
            abilityCooldownBar.setVisible(false);
        }));

        progressTimeline = new Timeline();
        for (int i = 0; i <= 100; i++) {
            final int progress = i;
            progressTimeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(ABILITY_COOLDOWN * (i / 100.0)),
                    e -> {
                        currentCooldownProgress = progress / 100.0;
                        abilityCooldownBar.setProgress(currentCooldownProgress);
                    }
            ));
        }

        cooldownTimer.setCycleCount(1);
        cooldownTimer.play();
        progressTimeline.setCycleCount(1);
        progressTimeline.play();
    }

    public static void updateRotation() {
        if (running) {
            double spaceshipSceneX = spaceshipView.localToScene(spaceshipView.getBoundsInLocal()).getMinX() + spaceshipView.getBoundsInLocal().getWidth() / 2;
            double spaceshipSceneY = spaceshipView.localToScene(spaceshipView.getBoundsInLocal()).getMinY() + spaceshipView.getBoundsInLocal().getHeight() / 2;

            double deltaX = lastMouseX - spaceshipSceneX;
            double deltaY = lastMouseY - spaceshipSceneY;

            double angle = Math.toDegrees(Math.atan2(deltaY, deltaX)) + 90;
            Platform.runLater(() -> {
                spaceshipView.setRotate(angle);
                hitbox.setRotate(angle);
                effectPane.setRotate(angle);
            });
        }
    }


    public static void handleMouseMove(MouseEvent event) {
        lastMouseX = event.getSceneX();
        lastMouseY = event.getSceneY();
    }
    public static void handleKeyPress(KeyEvent event) {
        activeKeys.add(event.getCode());
        if (event.getCode() == KeyCode.SPACE) {
            activateSpecialAbility();
        }
    }

    public static void handleKeyRelease(KeyEvent event) {
        activeKeys.remove(event.getCode());

    }

    public static void resetPlayerPosition() {
        getPlayerShip().setTranslateX(0);
        getPlayerShip().setTranslateY(0);
        getPlayerShip().setRotate(0);
        hitbox.setTranslateX(0);
        hitbox.setTranslateY(0);
        hitbox.setRotate(0);
    }

    public static ImageView getPlayerShip() {
        return spaceshipView;
    }

    public static Rectangle getHitbox() {
        return hitbox;
    }

    public static boolean isShielded() {
        return shielded;
    }

    public static void setShielded(boolean shielded) {
        Player.shielded = shielded;
    }

    public StackPane getEffectPane(){
        return effectPane;
    }

    public static int getExp() {
        return exp;
    }

    public static void setExp(int exp) {
        Player.exp = exp;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        Player.level = level;
    }

    public static int getMaxExp() {
        return max_exp;
    }

    public static void setMaxExp(int exp) {
        Player.max_exp = exp;
    }
}
