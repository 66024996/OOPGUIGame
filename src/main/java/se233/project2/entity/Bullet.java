package se233.project2.entity;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import se233.project2.util.Effects;

import static se233.project2.gameLoop.GameLoop.mainPane;
import static se233.project2.util.Hitbox.createHitbox;

public class Bullet {
    public enum BulletType {
        PLAYER,
        ENEMY
    }
    private ImageView bulletView;
    private double angle;
    private double speed;
    private Point2D position;
    private Rectangle hitbox;
    private BulletType type;
    public boolean isMissile;
    private boolean isRemoved = false;

    public Bullet(Image bulletImage, double angle, double speed, Point2D spawnLocation, BulletType type) {
        this.bulletView = new ImageView(bulletImage);
        bulletView.setFitWidth(bulletImage.getWidth()*3);
        bulletView.setFitHeight(bulletImage.getHeight()*3);
        this.angle = angle;
        this.speed = speed;
        this.position = spawnLocation;
        this.type = type;

        bulletView.setMouseTransparent(true);
        bulletView.setTranslateX(spawnLocation.getX());
        bulletView.setTranslateY(spawnLocation.getY());
        bulletView.setRotate(angle);
        hitbox = createHitbox(bulletView.getBoundsInLocal().getWidth(), bulletView.getBoundsInLocal().getHeight());
        hitbox.setTranslateX(bulletView.getTranslateX());
        hitbox.setTranslateY(bulletView.getTranslateY());
        hitbox.setRotate(bulletView.getRotate());
        mainPane.getChildren().add(hitbox);
    }

    public Bullet(Image bulletImage, Point2D spawnLocation, Point2D targetLocation, double speed, BulletType type) {
        this.bulletView = new ImageView(bulletImage);
        bulletView.setFitWidth(bulletImage.getWidth());
        bulletView.setFitHeight(bulletImage.getHeight());
        this.speed = speed;
        this.position = spawnLocation;
        this.type = type;
        this.isMissile = true;

        double deltaX = targetLocation.getX() - spawnLocation.getX();
        double deltaY = targetLocation.getY() - spawnLocation.getY();
        this.angle = Math.toDegrees(Math.atan2(deltaY, deltaX)) + 90;

        bulletView.setMouseTransparent(true);
        bulletView.setTranslateX(spawnLocation.getX());
        bulletView.setTranslateY(spawnLocation.getY());
        bulletView.setRotate(angle);
        hitbox = createHitbox(bulletView.getBoundsInLocal().getWidth(), bulletView.getBoundsInLocal().getHeight());
        hitbox.setTranslateX(bulletView.getTranslateX());
        hitbox.setTranslateY(bulletView.getTranslateY());
        hitbox.setRotate(bulletView.getRotate());
        mainPane.getChildren().add(hitbox);
    }

    public BulletType getType() {
        return type;
    }

    public void addToPane(StackPane mainPane) {
        Platform.runLater(() -> {
            mainPane.getChildren().add(bulletView);
        });
    }

    public void removeBullet(StackPane mainPane) {
        Platform.runLater(() -> {
            mainPane.getChildren().remove(bulletView);
        });
    }

    public void showExplosion(){
        Effects.playBulletExplosionAnimation(bulletView.getTranslateX(), bulletView.getTranslateY(), bulletView.getBoundsInLocal().getHeight(), bulletView.getBoundsInLocal().getWidth(), bulletView.getRotate());
    }

    public void update() {
        this.bulletView.toFront();
        double deltaX = Math.cos(Math.toRadians(angle - 90)) * speed;
        double deltaY = Math.sin(Math.toRadians(angle - 90)) * speed;

        position = new Point2D(position.getX() + deltaX, position.getY() + deltaY);

        Platform.runLater(() -> {
            bulletView.setTranslateX(position.getX());
            bulletView.setTranslateY(position.getY());
            hitbox.setTranslateX(position.getX());
            hitbox.setTranslateY(position.getY());
        });
    }

    public boolean checkEnemyCollision(Asteroid asteroid) {
        return this.hitbox.getBoundsInParent().intersects(asteroid.getHitbox().getBoundsInParent());
    }

    public boolean checkEnemyCollision(enemyShip enemy) {
        return this.hitbox.getBoundsInParent().intersects(enemy.getHitbox().getBoundsInParent());
    }

    public boolean checkBossCollision(Boss boss){
        return this.hitbox.getBoundsInParent().intersects(boss.getHitbox().getBoundsInParent());
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean isOutOfBounds() {
        return bulletView.getTranslateX() < -932 || bulletView.getTranslateX() > 932 ||
                bulletView.getTranslateY() < -518 || bulletView.getTranslateY() > 518;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }
}


