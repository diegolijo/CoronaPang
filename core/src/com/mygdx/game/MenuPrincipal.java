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

public class MenuPrincipal extends Pantallas {


    private Stage stage;
    private Skin skin;
    private Image logo;
    private TextButton boton;
    //
    EscenarioScreen escenarioScreen;

    private int nivel = 1;


    public void siguienteNivel() {
        this.nivel = nivel + 1;
        escenarioScreen = new EscenarioScreen(juego, nivel);
        juego.setScreen(escenarioScreen);
    }

    public MenuPrincipal(final MeuGdxGame juego) {
        super(juego);
        this.juego = juego;


        stage = new Stage(new FitViewport(640, 360));

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        logo = new Image(juego.getManager().get("Logo2.png", Texture.class));
        logo.setWidth(300);
        logo.setHeight(200);
        logo.setPosition(320 - logo.getWidth() / 2, 350 - logo.getHeight());

        boton = new TextButton("Empezar", skin);
        boton.setPosition(320 - boton.getWidth() / 2, 100);


        stage.addActor(logo);
        stage.addActor(boton);


        boton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //pulsamos el boton

                escenarioScreen = new EscenarioScreen(juego, nivel);
                juego.setScreen(escenarioScreen);




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
