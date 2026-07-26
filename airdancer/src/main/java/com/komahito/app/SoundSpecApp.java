
package com.komahito.app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.File;

import javafx.application.Application.Parameters;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SoundSpecApp extends GameApplication {

    private static final int HEIGHT = 300;
    private static final int WIDTH = 400;

    private static final int SPEED = 150;
    private static final String wavPath = null;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("SoundSpecApp");

        // Parameters params = Parameters.getParameters();
        // List<String> rawArgs = params.getRaw();
        // if (rawArgs.size() != 2) throw new Exception("Please include path to .wav file.");
        // String path = (String) rawArgs.get(0);
        // FGLX.set("Path2WavFile", new File(path).toURI().toString());
    }

    @Override
    protected void initUI() {
        // Label dropTarget = new Label("Drop wav file here");
        // dropTarget.setFont(new Font("MS Gothic", 12.0));
        // dropTarget.setTextFill(Color.WHITE);
        // dropTarget.setStyle("-fx-border-style: dashed; -fx-border-width: 2; -fx-padding: 50; -fx-background-color: transparent;");        dragnDrop(dropTarget);
        // dragnDrop(dropTarget);
        // FXGL.addUINode(dropTarget, 3, 3);
    }

    
    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new SoundSpecEntityFactory());

        FXGL.spawn("SpecAnalyzer"); // 座標(0, 0)にスペクトル解析器を配置

        FXGL.spawn("Background", 0, 0); // 座標(0, 0)に背景を配置
        
        FXGL.spawn("Floor", 0, 620); // 座標(0, 580)に床を配置

        FXGL.spawn("Block", 20, 200).addComponent(new BlockComponent(0));
        FXGL.spawn("Block", 20+43, 200).addComponent(new BlockComponent(1));
        FXGL.spawn("Block", 20+43*2, 200).addComponent(new BlockComponent(2));
        FXGL.spawn("Block", 20+43*3, 200).addComponent(new BlockComponent(3));
        FXGL.spawn("Block", 20+43*4, 200).addComponent(new BlockComponent(4));
        FXGL.spawn("Block", 20+43*5, 200).addComponent(new BlockComponent(5));
        FXGL.spawn("Block", 20+43*6, 200).addComponent(new BlockComponent(6));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("SpecAmplitudes", new double[]{200, 300, 400, 500, 500, 400, 300, 200, 100, 50}); // 初期値として10個の振幅を設定
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld physics = FXGL.getPhysicsWorld();
        physics.setGravity(0, 500); // 重力を設定
    }

    public static void main(String[] args) {
        launch(args);
    }
}