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


public class GameOverScreen extends Pantalla {

    private Stage stage;
    private Image gameOver;
    private TextButton boton;
    private Skin skin;



    public GameOverScreen(final MeuGdxGame juego) {
        super(juego);


        stage = new Stage(new FitViewport(640, 360));

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        gameOver = new Image(juego.getManager().get("GameOver.png", Texture.class));
        gameOver.setWidth(200);
        gameOver.setHeight(150);
        gameOver.setPosition(320 - gameOver.getWidth()/ 2, 360 - gameOver.getHeight());

        boton = new TextButton("Continuar", skin);
        boton.setPosition(320 - boton.getWidth()/ 2, 150);

        stage.addActor(boton);
        stage.addActor(gameOver);

        boton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //pulsamos el boton
                juego.setScreen( juego.getEscenarioScreen());
            }
        });
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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

