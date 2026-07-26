package com.komahito.app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;


public class BlockComponent extends Component {
    private int blockIndex;

    public BlockComponent(int blockIndex) {
        this.blockIndex = blockIndex;
    }

    @Override
    public void onAdded() {
        // ブロックの初期位置を設定
    }

    @Override
    public void onUpdate(double tpf) {
        // スペクトル解析結果を取得
        double[] spectrumAmplitudes = FXGL.geto("SpecAmplitudes");

        // ブロックの位置をスペクトル解析結果に基づいて更新
        if (spectrumAmplitudes != null && blockIndex < spectrumAmplitudes.length) {
            double amplitude = spectrumAmplitudes[blockIndex];
            double newVY = Math.log(amplitude + 1) * 70; // 振幅に応じて鉛直方向の速度を調整
            if (entity.getY() >= 40)
                entity.getComponent(PhysicsComponent.class).applyForceToCenter(new Point2D(0,-newVY));
        }
    
    }
}