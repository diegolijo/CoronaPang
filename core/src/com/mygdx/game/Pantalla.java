package com.mygdx.game;

import com.badlogic.gdx.Screen;

public abstract class Pantalla implements Screen {


    protected MeuGdxGame juego;

    public Pantalla(MeuGdxGame juego) {
        this.juego = juego;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }



    @Override
    public void dispose() {

    }
}
