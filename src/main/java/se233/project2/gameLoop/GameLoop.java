package se233.project2.gameLoop;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import se233.project2.controller.MainController;
import se233.project2.entity.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static se233.project2.controller.MainController.getPlayerInstance;
import static se233.project2.entity.Asteroid.asteroidSpawner;
import static se233.project2.entity.Asteroid.createAsteroidSpawner;
import static se233.project2.entity.Player.*;
import static se233.project2.entity.enemyShip.createEnemySpawner;
import static se233.project2.entity.enemyShip.enemySpawner;

public class GameLoop {
    private static AnimationTimer gameLoop;
    public static boolean running = false;
    public static StackPane mainPane;
    public static List<Bullet> bullets = new ArrayList<>();
    public static List<Asteroid> asteroids = new ArrayList<>();
    public static List<Timeline> activeEffect = new ArrayList<>();
    public static List<enemyShip> enemies = new ArrayList<>();
    public static List<Boss> bosses = new ArrayList<>();

    public static void startGameLoop() {
        running = true;

        for (Timeline effect : activeEffect) {
            effect.play();
        }

        if (progressTimeline != null) {
            progressTimeline.play();
        }
        if (cooldownTimer != null) {
            cooldownTimer.play();
        }
        for (Asteroid asteroid : asteroids) {
            asteroid.resumeCountdown();
        }
        createAsteroidSpawner();
        createEnemySpawner();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Player.updateMovement();
                Player.startShootingTimer();

                Iterator<enemyShip> enemyIterator1 = enemies.iterator();
                while (enemyIterator1.hasNext()){
                    enemyShip enemy = enemyIterator1.next();
                    enemy.startShootingTimer();
                }

                Iterator<Boss> bossIterator = bosses.iterator();
                while (bossIterator.hasNext()){
                    Boss boss = bossIterator.next();
                    boss.startShootingTimer();
                }

                if (abilityTimer != null && abilityTimer.getStatus() == Animation.Status.PAUSED) {
                    abilityTimer.play();
                }

                Iterator<Bullet> bulletIterator = bullets.iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    bullet.update();

                    if (bullet.isRemoved()) {
                        continue;
                    }

                    if(bullet.getType() == Bullet.BulletType.PLAYER){
                        Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                        while (asteroidIterator.hasNext()) {
                            Asteroid asteroid = asteroidIterator.next();

                            if (bullet.checkEnemyCollision(asteroid)) {
                                bullet.showExplosion();
                                bullet.removeBullet(mainPane);
                                bulletIterator.remove();
                                mainPane.getChildren().remove(bullet.getHitbox());
                                bullet.setRemoved(true);

                                if(bullet.isMissile){
                                    asteroid.setHp(asteroid.getHp()-getPlayerInstance().getDamage()*2);
                                }else{
                                    asteroid.setHp(asteroid.getHp()-getPlayerInstance().getDamage());
                                }
                                if(asteroid.getHp() <= 0){
                                    asteroidIterator.remove();
                                    asteroid.destroyed();
                                }
                                break;
                            }
                        }

                        Iterator<enemyShip> enemyIterator = enemies.iterator();
                        while (enemyIterator.hasNext()){
                            enemyShip enemy = enemyIterator.next();
                            if (bullet.checkEnemyCollision(enemy)) {
                                bullet.showExplosion();
                                bullet.removeBullet(mainPane);
                                bulletIterator.remove();
                                bullet.setRemoved(true);
                                mainPane.getChildren().remove(bullet.getHitbox());

                                if(bullet.isMissile){
                                    enemy.setHp(enemy.getHp()-getPlayerInstance().getDamage()*2);
                                }else{
                                    enemy.setHp(enemy.getHp()-getPlayerInstance().getDamage());
                                }
                                if(enemy.getHp() <= 0){
                                    enemyIterator.remove();
                                    enemy.destroyed();
                                }
                                break;
                            }
                        }

                        Iterator<Boss> bossIterator1 = bosses.iterator();
                        while (bossIterator1.hasNext()){
                            Boss boss = bossIterator1.next();
                            if (bullet.checkBossCollision(boss)) {
                                bullet.showExplosion();
                                bullet.removeBullet(mainPane);
                                bulletIterator.remove();
                                bullet.setRemoved(true);
                                mainPane.getChildren().remove(bullet.getHitbox());

                                if(bullet.isMissile){
                                    boss.setHp(boss.getHp()-getPlayerInstance().getDamage()*2);
                                }else{
                                    boss.setHp(boss.getHp()-getPlayerInstance().getDamage());
                                }
                                if(boss.getHp() <= 0){
                                    bossIterator1.remove();
                                    boss.destroyed();
                                }
                                break;
                            }
                        }
                    } else if (bullet.getType() == Bullet.BulletType.ENEMY) {
                        if(bullet.getHitbox().getBoundsInParent().intersects(Player.getHitbox().getBoundsInParent())){
                            bullet.showExplosion();
                            bullet.removeBullet(mainPane);
                            bulletIterator.remove();
                            mainPane.getChildren().remove(bullet.getHitbox());

                            MainController.reduceHp();
                        }
                    }

                    if (bullet.isOutOfBounds()) {
                        bullet.removeBullet(mainPane);
                        mainPane.getChildren().remove(bullet.getHitbox());
                        bulletIterator.remove();
                        bullet.setRemoved(true);
                    }
                }

                Iterator<Asteroid> asteroidIterator = asteroids.iterator();
                while (asteroidIterator.hasNext()) {
                    Asteroid asteroid = asteroidIterator.next();
                    asteroid.updatePosition();
                }

                Iterator<enemyShip> enemyIterator = enemies.iterator();
                while (enemyIterator.hasNext()) {
                    enemyShip enemy = enemyIterator.next();
                    enemy.updatePosition();
                }

                Iterator<Boss> bossIterator1 = bosses.iterator();
                while (bossIterator1.hasNext()) {
                    Boss boss = bossIterator1.next();
                    boss.updatePosition();
                }
            }
        };
        gameLoop.start();
    }

    public static void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public static void stopGameLoop() {
        running = false;
        Platform.runLater(() -> {
            if (gameLoop != null) {
                gameLoop.stop();
                Player.stopShootingTimer();
            }
            if (asteroidSpawner != null) {
                asteroidSpawner.stop();
            }
            if(enemySpawner != null){
                enemySpawner.stop();
            }
            Iterator<enemyShip> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()){
                enemyShip enemy = enemyIterator.next();
                enemy.stopShootingTimer();
            }
            Iterator<Boss> bossIterator = bosses.iterator();
            while (bossIterator.hasNext()){
                Boss boss = bossIterator.next();
                boss.stopShootingTimer();
            }
            for (Timeline effect : activeEffect) {
                effect.pause();
            }

            if (progressTimeline != null) {
                progressTimeline.pause();
            }
            if (cooldownTimer != null) {
                cooldownTimer.pause();
            }

            for (Asteroid asteroid : asteroids) {
                asteroid.pauseCountdown();
            }

            if (abilityTimer != null && abilityTimer.getStatus() == Animation.Status.RUNNING) {
                abilityTimer.pause();
            }
        });
    }
}
