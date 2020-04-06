package com.mygdx.game;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.mygdx.game.Pantallas.TO_PIXELES;


public class ActorScene2d extends Actor {

    private boolean vivo;
    private Texture texture;
    private float width;
    private float heigth;




    public ActorScene2d(Texture avatar, float width, float heigth, boolean vivo) {
        this.texture = avatar;
   //     this.sprite = new Sprite(texture);
        this.width = width;
        this.heigth = heigth;
        this.vivo= vivo;
        setSize(width * TO_PIXELES, heigth * TO_PIXELES);
    }

    public boolean isVivo() {
        return vivo;
    }

    public void setVivo(boolean vivo) {
        this.vivo = vivo;
    }



    public Texture getTexture() {
        return texture;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeigth(float heigth) {
        this.heigth = heigth;
    }


    @Override
    public void act(float delta) {


    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        //dibuja actores en pantalla
       batch.draw(texture, getX(), getY(), TO_PIXELES * width, TO_PIXELES * heigth);
    }
}

