package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

import static com.mygdx.game.Box2d_Scene2d.*;


// implementa la interface InputProcessor;
public class ProcesadorEntreda extends InputAdapter {

    private Box2d_Scene2d box2dScene2D;
    private Fixture fixtureAvatar;
    private boolean isTouchL = false, isTouchR = false, puedoDisparar = true;
    private int xL, xR,  Lw,buttonR,buttonL;
    private int velocidadAvatar = 20;
    private Vector2 ultimoTouch = new Vector2();




    public ProcesadorEntreda(Box2d_Scene2d box2dScene2D) {
        this.box2dScene2D = box2dScene2D;
    }


    public void setPuedoDisparar(boolean puedoDisparar) {
        this.puedoDisparar = puedoDisparar;
    }





    @Override               // posicion pantalla x - y  - numero de dedo - boton
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.buttonL = button;


        //disparo
        if (screenX > 500 ){
            if (box2dScene2D.isPuedoDisparar()) {
                box2dScene2D.disparo(true);
                puedoDisparar = false;
            }
        }


        return true;        // debemos indicarle con un true que hemos procesado la entrada
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {




        fixtureAvatar = box2dScene2D.getFixtureArray(AVATAR);


        // dar valores a las variables Lw , xR                          <-------------------------------------------------------------------------

        //botonL
        if (screenX < Lw * 2){
            isTouchL = false;
            fixtureAvatar.getBody().setLinearVelocity(0, 0f);
            this.buttonL = button;
        }

        //botonR
        if (screenX > xR &&  screenX < Gdx.graphics.getWidth()/2 ){
            isTouchR = false;
            fixtureAvatar.getBody().setLinearVelocity(0, 0f);
            this.buttonR = button;
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {


        // utilizar  Gdx.graphics.getWidth() para saber el temalo de la pantalla segun la plataforma     <------------------------------------------------



        if (box2dScene2D.juego.getSO()== 1){
            screenX = screenX*2;
            screenY = screenY*2;
        }

        fixtureAvatar = box2dScene2D.getFixtureArray(AVATAR);

        // ** el (0,0) suerior izquiera
        if (screenX < 100){
            isTouchL = true;
            fixtureAvatar.getBody().setLinearVelocity(-velocidadAvatar, 0f);

        }
        if (screenX > 100 && screenX < 200){
            isTouchL = true;
            fixtureAvatar.getBody().setLinearVelocity(0, 0f);

        }

        if ( screenX > 200 && screenX < 350){
            isTouchL = true;
            fixtureAvatar.getBody().setLinearVelocity(velocidadAvatar, 0f);

        }





        return true;
    }


}