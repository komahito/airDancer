package com.komahito.app;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SoundSpecEntityFactory implements EntityFactory {
    @Spawns("SpecAnalyzer")
    public Entity spawnSpecAnalyzer(SpawnData data) {
        return FXGL.entityBuilder(data)
            .type(EntityType.SPEC_ANALYZER)
            .with(new CalcSpecComponent())
            .build();
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData data) {
        return FXGL.entityBuilder(data)
            .view(new Rectangle(800, 600, Color.MIDNIGHTBLUE))
            .with(new IrremovableComponent())
            .zIndex(-100)
            .build();
    }

    @Spawns("Block")
    public Entity spawnBlock(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        BodyDef bd = new BodyDef();
        bd.setFixedRotation(false);
        bd.setType(BodyType.DYNAMIC);
        physics.setBodyDef(bd);

        return FXGL.entityBuilder(data)
            .type(EntityType.BLOCK)
            .bbox(new HitBox(BoundingShape.box(40, 400)))
            .view(new Rectangle(40, 400, Color.MEDIUMVIOLETRED))
            .collidable()
            .with(physics)
            .build();
    }

    @Spawns("Floor")
    public Entity spawnFloor(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        BodyDef bd = new BodyDef();
        bd.setFixedRotation(false);
        bd.setType(BodyType.STATIC);
        physics.setBodyDef(bd);

        return FXGL.entityBuilder(data)
            .type(EntityType.FLOOR)
            .bbox(new HitBox(BoundingShape.box(800, 20)))
            .view(new Rectangle(800, 20, Color.MEDIUMTURQUOISE))
            .collidable()
            .with(physics)
            .build();
    }
}