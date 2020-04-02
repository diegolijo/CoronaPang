package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;


// implementa la interface InputProcessor;
public class ProcesadorIn extends InputAdapter {

    private Box2D box2d;
    private Fixture fixtureAvatar;
    private int medioPantallax, medioPantallaY;
    private boolean isTouchL, isTouchR  = false;
    private int velocidadAvatar = 20;
    private Vector2 ultimoTouch = new Vector2();

    private boolean puedoDisparar = true;


    public ProcesadorIn(Box2D box2d) {
        this.box2d = box2d;
    }



    public void setPuedoDisparar(boolean puedoDisparar) {
        this.puedoDisparar = puedoDisparar;
    }

    @Override               // posicion pantalla x - y  - numero de dedo - boton
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        medioPantallax = Gdx.graphics.getWidth() / 2;
        medioPantallaY = Gdx.graphics.getHeight() / 2;
        fixtureAvatar = box2d.getFixture(5);



        if (screenY < medioPantallaY) {

            if (box2d. isPuedoDisparar()) {
                box2d.disparo(true);
                puedoDisparar = false;
            }
        } else {

            if (screenX < medioPantallax) {
                isTouchL = true;
                fixtureAvatar.getBody().setLinearVelocity(-velocidadAvatar, 0f);

            } else {
                isTouchR = true;
                fixtureAvatar.getBody().setLinearVelocity(velocidadAvatar, 0f);


            }

        }


        return true;        // debemos indicarle con un true que hemos procesado la entrada
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {


        // si levantamos el derecho
        if (screenX < medioPantallax) {
            isTouchL = false;


            //comprueba el otro dedo
            if (!isTouchR) {
                fixtureAvatar.getBody().setLinearVelocity(0, 0f);
            } else {
                fixtureAvatar.getBody().setLinearVelocity(velocidadAvatar, 0f);
            }

            // si levantamos el izquierdo
        } else {
            isTouchR = false;


            //comprueba el otro dedo
            if (!isTouchL) {
                fixtureAvatar.getBody().setLinearVelocity(0, 0f);
            } else {
                fixtureAvatar.getBody().setLinearVelocity(-velocidadAvatar, 0f);
            }

        }


        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {




        return false;
    }


}