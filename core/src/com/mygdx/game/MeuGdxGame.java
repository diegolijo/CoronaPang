package com.mygdx.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class MeuGdxGame extends Game {

    AssetManager manager;

    public AssetManager getManager() {
        return manager;
    }

    @Override
    public void create() {

        manager = new AssetManager();
        manager.load("virusAmarillo100.png", Texture.class);
        manager.load("virusRosa100.png", Texture.class);
        manager.load("avatar300x100.png", Texture.class);
        manager.load("papelCulo100x3600.png", Texture.class);
        manager.load("boton100pxL.png", Texture.class);
        manager.load("boton100pxR.png", Texture.class);
        manager.load("boton100pxPapel.png", Texture.class);
        manager.load("Alameda.png", Texture.class);
        manager.load("Patinete100px.png", Texture.class);

        manager.load("disparo.wav", Sound.class);
        manager.load("pedo.wav", Sound.class);
        manager.load ("LaVidaEsAsi.mp3", Music.class);
        manager.finishLoading();


        //creamos una pantalla box2d le mandanmos la referencia al juego
        Box2D box2d = new Box2D(this, 5);
        setScreen(box2d);

        //creamos un imputProcesora  le mandanmos la referencia a las pantalla box2d
        ProcesadorIn p = new ProcesadorIn(box2d);
        Gdx.input.setInputProcessor(p);

        //le pasamos el procesador de entrada
        box2d.setProcesadorIn(p);


    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {

    }
}
