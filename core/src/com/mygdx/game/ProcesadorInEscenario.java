package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.Fixture;

import static com.mygdx.game.EscenarioScreen.*;


// implementa la interface InputProcessor;
public class ProcesadorInEscenario extends InputAdapter {

    private EscenarioScreen escenarioScreen;
    private Fixture fixtureAvatar;
    private boolean isTouchL = false;
    private boolean isTouchR = false;
    private int xL, xR,  Lw,buttonR,buttonL;
    private int velocidadAvatar = 20;




    public ProcesadorInEscenario(EscenarioScreen escenarioScreen) {
        this.escenarioScreen = escenarioScreen;
    }


    public void setPuedoDisparar(boolean puedoDisparar) {
    }





    @Override               // posicion pantalla x - y  - numero de dedo - boton
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.buttonL = button;


        //disparo
        if (screenX > 500 ){
            if (escenarioScreen.isPuedoDisparar()) {
                escenarioScreen.disparo(true);
            }
        }


        return true;        // debemos indicarle con un true que hemos procesado la entrada
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {




        fixtureAvatar = escenarioScreen.getFixtureArray(AVATAR);


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



        if (escenarioScreen.juego.getSO()== 1){
            screenX = screenX*2;
            screenY = screenY*2;
        }

        fixtureAvatar = escenarioScreen.getFixtureArray(AVATAR);

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