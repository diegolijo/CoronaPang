package com.mygdx.game;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.mygdx.game.Box2D.TO_PIXELES;

public class ActorScene2d extends Actor {

    private boolean vivo;
    private Texture texture;
    private float x;
    private float y;


    public ActorScene2d(Texture avatar,  float x, float y) {
        this.texture = avatar;
        this.x = x;
        this.y = y;
        setSize(x * TO_PIXELES, y * TO_PIXELES);
    }

    public boolean isVivo() {
        return vivo;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }



    @Override
    public void act(float delta) {

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), TO_PIXELES * x, TO_PIXELES * y);
    }
}

