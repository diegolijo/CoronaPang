package com.mygdx.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;


public class MeuGdxGame extends Game {

    private int SO;
    AssetManager manager;


    //pantallas
    private EscenarioScreen escenarioScreen;
    private GameOverScreen  gameOverScreen;
    private MenuPrincipal menuScreen ;


    public MenuPrincipal getMenuScreen() {
        return menuScreen;
    }



    public EscenarioScreen getEscenarioScreen() {
        return escenarioScreen;
    }


    public MeuGdxGame(int SO) {
        this.SO = SO;
    }

    public int getSO() {
        return SO;
    }


    public AssetManager getManager() {
        return manager;
    }

    @Override
    public void create() {

        System.out.println(this.getClass().getClassLoader().getParent().toString());

        manager = new AssetManager();
        manager.load("virusAmarillo100.png", Texture.class);
        manager.load("virusRosa100.png", Texture.class);
        manager.load("avatar300x100.png", Texture.class);
        manager.load("papelCulo100x3600.png", Texture.class);
        manager.load("boton100pxL.png", Texture.class);
        manager.load("boton100pxR.png", Texture.class);
        manager.load("boton100pxPapel.png", Texture.class);
        manager.load("Alameda.png", Texture.class);
        manager.load("Belvis.png", Texture.class);
        manager.load("Castros.png", Texture.class);
        manager.load("VillaParaiso.png", Texture.class);
        manager.load("Patinete100px.png", Texture.class);
        manager.load("GameOver.png", Texture.class);
        manager.load("Logo2.png", Texture.class);


        manager.load("audio/disparo.wav", Sound.class);
        manager.load("audio/pedo.wav", Sound.class);
        manager.load("audio/gritoMuerte.mp3", Sound.class);
        manager.load("audio/LaVidaEsAsi.mp3", Music.class);
        manager.load("audio/bajoLoop.mp3", Music.class);
        manager.finishLoading();



         menuScreen = new MenuPrincipal(this);
        setScreen(menuScreen);




    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {

    }


}