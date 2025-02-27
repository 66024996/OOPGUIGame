package se233.project2.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import se233.project2.entity.Player;
import se233.project2.gameLoop.GameLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static se233.project2.controller.MainController.getPlayerInstance;
import static se233.project2.entity.Player.activeAbilityView;
import static se233.project2.entity.Player.effectPane;
import static se233.project2.gameLoop.GameLoop.*;

public class Effects {
    public static List<ImageView> activeEffectView = new ArrayList<>();
    private static Random random = new Random();

    public static void playAsteroidExplosionAnimation(double x, double y, double height, double width) {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/explosion_Asteroids.png"));


        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setViewport(new Rectangle2D(0, 0, 32, 32));
        explosionView.setTranslateX(x);
        explosionView.setTranslateY(y);
        explosionView.setRotate(random.nextDouble() * 360);
        explosionView.setFitWidth(width);
        explosionView.setFitHeight(height);


        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline explosionTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(0, 0, 32, 32))),
                new KeyFrame(Duration.millis(200), e -> explosionView.setViewport(new Rectangle2D(32, 0, 32, 32))),
                new KeyFrame(Duration.millis(300), e -> explosionView.setViewport(new Rectangle2D(64, 0, 32, 32))),
                new KeyFrame(Duration.millis(400), e -> explosionView.setViewport(new Rectangle2D(96, 0, 32, 32))),
                new KeyFrame(Duration.millis(500), e -> explosionView.setViewport(new Rectangle2D(128, 0, 32, 32))),
                new KeyFrame(Duration.millis(600), e -> explosionView.setViewport(new Rectangle2D(160, 0, 32, 32))),
                new KeyFrame(Duration.millis(700), e -> explosionView.setViewport(new Rectangle2D(192, 0, 32, 32))),
                new KeyFrame(Duration.millis(800), e -> explosionView.setViewport(new Rectangle2D(224, 0, 32, 32)))
        );
        explosionTimeline.setCycleCount(1);
        explosionTimeline.setOnFinished(event -> mainPane.getChildren().remove(explosionView));
        GameLoop.activeEffect.add(explosionTimeline);
        explosionTimeline.play();
    }

    public static void playBulletExplosionAnimation(double x, double y, double height, double width, double angle) {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/explosion_Bullets.png"));

        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setViewport(new Rectangle2D(0, 0, 64, 64));
        explosionView.setTranslateX(x);
        explosionView.setTranslateY(y);
        explosionView.setFitWidth((width * 3) * (1 + random.nextDouble() * 1));
        explosionView.setFitHeight((height * 3) * (1 + random.nextDouble() * 1));
        explosionView.setRotate(angle + 180);

        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline explosionTimeline = new Timeline(
                new KeyFrame(Duration.millis(50), e -> explosionView.setViewport(new Rectangle2D(0, 0, 64, 64))),
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(64, 0, 64, 64))),
                new KeyFrame(Duration.millis(150), e -> explosionView.setViewport(new Rectangle2D(128, 0, 64, 64))),
                new KeyFrame(Duration.millis(200), e -> explosionView.setViewport(new Rectangle2D(192, 0, 64, 64))),
                new KeyFrame(Duration.millis(250), e -> explosionView.setViewport(new Rectangle2D(256, 0, 64, 64))),
                new KeyFrame(Duration.millis(300), e -> explosionView.setViewport(new Rectangle2D(320, 0, 64, 64))),
                new KeyFrame(Duration.millis(350), e -> explosionView.setViewport(new Rectangle2D(384, 0, 64, 64))),
                new KeyFrame(Duration.millis(400), e -> explosionView.setViewport(new Rectangle2D(448, 0, 64, 64)))
        );
        explosionTimeline.setCycleCount(1);
        explosionTimeline.setOnFinished(event -> mainPane.getChildren().remove(explosionView));
        GameLoop.activeEffect.add(explosionTimeline);
        explosionTimeline.play();
    }

    public static void playShipExplosionAnimation() {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/explosion_Ships.png"));

        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setViewport(new Rectangle2D(0, 0, 48, 48));
        explosionView.setFitWidth(500);
        explosionView.setFitHeight(500);
        explosionView.setRotate(random.nextDouble() * 360);
        explosionView.setTranslateX(Player.getX());
        explosionView.setTranslateY(Player.getY());

        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline explosionTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(0, 0, 48, 48))),
                new KeyFrame(Duration.millis(200), e -> explosionView.setViewport(new Rectangle2D(48, 0, 48, 48))),
                new KeyFrame(Duration.millis(300), e -> explosionView.setViewport(new Rectangle2D(96, 0, 48, 48))),
                new KeyFrame(Duration.millis(400), e -> explosionView.setViewport(new Rectangle2D(144, 0, 48, 48))),
                new KeyFrame(Duration.millis(500), e -> explosionView.setViewport(new Rectangle2D(192, 0, 48, 48))),
                new KeyFrame(Duration.millis(600), e -> explosionView.setViewport(new Rectangle2D(240, 0, 48, 48)))
        );
        explosionTimeline.setCycleCount(1);
        explosionTimeline.setOnFinished(event -> mainPane.getChildren().remove(explosionView));

        GameLoop.activeEffect.add(explosionTimeline);
        explosionTimeline.play();
    }

    public static void playShipExplosionAnimation(double x, double y, double width, double height) {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/explosion_Ships.png"));

        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setViewport(new Rectangle2D(0, 0, 48, 48));
        explosionView.setFitWidth(200);
        explosionView.setFitHeight(200);
        explosionView.setRotate(random.nextDouble() * 360);
        explosionView.setTranslateX(x);
        explosionView.setTranslateY(y);

        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline explosionTimeline = new Timeline(
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(0, 0, 48, 48))),
                new KeyFrame(Duration.millis(200), e -> explosionView.setViewport(new Rectangle2D(48, 0, 48, 48))),
                new KeyFrame(Duration.millis(300), e -> explosionView.setViewport(new Rectangle2D(96, 0, 48, 48))),
                new KeyFrame(Duration.millis(400), e -> explosionView.setViewport(new Rectangle2D(144, 0, 48, 48))),
                new KeyFrame(Duration.millis(500), e -> explosionView.setViewport(new Rectangle2D(192, 0, 48, 48))),
                new KeyFrame(Duration.millis(600), e -> explosionView.setViewport(new Rectangle2D(240, 0, 48, 48)))
        );
        explosionTimeline.setCycleCount(1);
        explosionTimeline.setOnFinished(event -> mainPane.getChildren().remove(explosionView));

        GameLoop.activeEffect.add(explosionTimeline);
        explosionTimeline.play();
    }

    public static void playAbilityEffect1Animation() {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/abilityEffects/abilityEffect3.png"));

        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setPreserveRatio(true);
        explosionView.setViewport(new Rectangle2D(512, 192, 32, 32));
        explosionView.setFitWidth(120);
        explosionView.setFitHeight(120);
        explosionView.setRotate(random.nextDouble() * 360);
        explosionView.setTranslateX(Player.getX());
        explosionView.setTranslateY(Player.getY());

        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        activeAbilityView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline explosionTimeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> explosionView.setViewport(new Rectangle2D(512, 192, 32, 32))),
                new KeyFrame(Duration.millis(50), e -> explosionView.setViewport(new Rectangle2D(544, 192, 32, 32))),
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(576, 192, 32, 32))),
                new KeyFrame(Duration.millis(150), e -> explosionView.setViewport(new Rectangle2D(608, 192, 32, 32)))
        );
        explosionTimeline.setCycleCount(1);
        explosionTimeline.setOnFinished(event -> {
            mainPane.getChildren().remove(explosionView);
        });
        GameLoop.activeEffect.add(explosionTimeline);
        explosionTimeline.play();
    }

    public static void playMissleEffectAnimation() {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/abilityEffects/abilityEffect2.png"));
        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setViewport(new Rectangle2D(512, 256, 64, 64));
        explosionView.setFitWidth(400);
        explosionView.setFitHeight(400);
        explosionView.setRotate(random.nextDouble() * 360);
        explosionView.setTranslateX(Player.getX());
        explosionView.setTranslateY(Player.getY());

        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline explosionTimeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> explosionView.setViewport(new Rectangle2D(512, 256, 64, 64))),
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(448, 256, 64, 64))),
                new KeyFrame(Duration.millis(200), e -> explosionView.setViewport(new Rectangle2D(384, 256, 64, 64))),
                new KeyFrame(Duration.millis(300), e -> explosionView.setViewport(new Rectangle2D(320, 256, 64, 64))),
                new KeyFrame(Duration.millis(400), e -> explosionView.setViewport(new Rectangle2D(256, 256, 64, 64))),
                new KeyFrame(Duration.millis(500), e -> explosionView.setViewport(new Rectangle2D(192, 256, 64, 64))),
                new KeyFrame(Duration.millis(600), e -> explosionView.setViewport(new Rectangle2D(128, 256, 64, 64))),
                new KeyFrame(Duration.millis(700), e -> explosionView.setViewport(new Rectangle2D(64, 256, 64, 64))),
                new KeyFrame(Duration.millis(800), e -> explosionView.setViewport(new Rectangle2D(0, 256, 64, 64)))
        );

        explosionTimeline.setCycleCount(1);
        explosionTimeline.setOnFinished(event -> {
            mainPane.getChildren().remove(explosionView);
        });

        GameLoop.activeEffect.add(explosionTimeline);
        explosionTimeline.play();
    }

    public static void playAbilityEffect2Animation() {
        Image explosionImage = new Image(Effects.class.getResourceAsStream("/se233/project2/Effects/abilityEffects/abilityEffect4.png"));

        ImageView explosionView = new ImageView(explosionImage);
        explosionView.setPreserveRatio(true);
        explosionView.setViewport(new Rectangle2D(584, 171, 146, 171));
        explosionView.setFitWidth(200);
        explosionView.setFitHeight(200);
        explosionView.setRotate(random.nextDouble() * 360);
        explosionView.setTranslateX(Player.getX());
        explosionView.setTranslateY(Player.getY());
        explosionView.setOpacity(0.5);

        mainPane.getChildren().add(explosionView);
        activeEffectView.add(explosionView);
        activeAbilityView.add(explosionView);
        explosionView.setMouseTransparent(true);

        Timeline initialTimeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> explosionView.setViewport(new Rectangle2D(584, 171, 146, 171))),
                new KeyFrame(Duration.millis(50), e -> explosionView.setViewport(new Rectangle2D(438, 171, 146, 171))),
                new KeyFrame(Duration.millis(100), e -> explosionView.setViewport(new Rectangle2D(292, 171, 146, 171))),
                new KeyFrame(Duration.millis(150), e -> explosionView.setViewport(new Rectangle2D(146, 171, 146, 171))),
                new KeyFrame(Duration.millis(200), e -> explosionView.setViewport(new Rectangle2D(0, 171, 146, 171))),
                new KeyFrame(Duration.millis(250), e -> explosionView.setViewport(new Rectangle2D(584, 0, 146, 171))),
                new KeyFrame(Duration.millis(300), e -> explosionView.setViewport(new Rectangle2D(438, 0, 146, 171)))
        );
        GameLoop.activeEffect.add(initialTimeline);

        Timeline loopingTimeline = new Timeline(
                new KeyFrame(Duration.millis(0), e -> {
                    explosionView.setViewport(new Rectangle2D(146, 0, 146, 171));
                    explosionView.setRotate(random.nextDouble() * 360);
                }),
                new KeyFrame(Duration.millis(200), e -> {
                    explosionView.setViewport(new Rectangle2D(0, 0, 146, 171));
                    explosionView.setRotate(random.nextDouble() * 360);
                })
        );
        loopingTimeline.setCycleCount(7);

        initialTimeline.setOnFinished(event -> {
            loopingTimeline.play();
            activeEffect.remove(initialTimeline);
            GameLoop.activeEffect.add(loopingTimeline);
        });

        loopingTimeline.setOnFinished(e -> {
            mainPane.getChildren().remove(explosionView);
            activeEffect.remove(loopingTimeline);
        });

        initialTimeline.play();
    }
}
