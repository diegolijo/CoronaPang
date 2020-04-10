package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class GameOverScreen extends Pantallas {

    private Stage stage;
    private Image gameOver;
    private TextButton boton;
    private Skin skin;
    private EscenarioScreen escenarioScreen;



    public GameOverScreen(final MeuGdxGame juego) {
        super(juego);
        this.juego = juego;

    }


    @Override
    public void show() {


        stage = new Stage(new FitViewport(640, 360));

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        gameOver = new Image(juego.getManager().get("GameOver.png", Texture.class));
        gameOver.setWidth(200);
        gameOver.setHeight(150);
        gameOver.setPosition(320 - gameOver.getWidth() / 2, 180 - gameOver.getHeight() / 2);

        boton = new TextButton("Continuar", skin);
        boton.setPosition(320 - boton.getWidth() / 2, 50);

        stage.addActor(boton);
        stage.addActor(gameOver);


        Gdx.input.setInputProcessor(stage);


        boton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //pulsamos el boton
               juego.setScreen(juego.getMenuScreen());
            }
        });
    }




    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }


    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();


    }
}

