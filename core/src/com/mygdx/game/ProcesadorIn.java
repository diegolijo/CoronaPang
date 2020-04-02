package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.physics.box2d.Fixture;


// implementa la interface InputProcessor;
public class ProcesadorIn extends InputAdapter {

    private Box2D box2d;
    private Fixture fixtureAvatar;
    private int medioPantalla;
    private boolean isTouchL, isTouchR = false;
    private int velocidad;


    public ProcesadorIn(Box2D box2d) {
        this.box2d = box2d;
    }

    @Override               // posicion pantalla x - y  - numero de dedo - boton
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        medioPantalla = Gdx.graphics.getWidth() / 2;
        fixtureAvatar = box2d.getFixture(4);
        velocidad = 15;

        if (screenX < medioPantalla) {
            isTouchL = true;
            fixtureAvatar.getBody().setLinearVelocity(-velocidad, 0f);

        } else {
            isTouchR = true;
            fixtureAvatar.getBody().setLinearVelocity(velocidad, 0f);


        }
        return true;        // debemos indicarle con un true que hemos procesado la entrada
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {


        // si levantamos el derecho
        if (screenX < medioPantalla) {
            isTouchL = false;
            //comprueba el otro dedo
            if (!isTouchR) {
                fixtureAvatar.getBody().setLinearVelocity(0, 0f);
            }else{
                fixtureAvatar.getBody().setLinearVelocity(velocidad, 0f);
            }

            // si levantamos el izquierdo
        } else {
            isTouchR = false;
            //comprueba el otro dedo
            if (!isTouchL) {
                fixtureAvatar.getBody().setLinearVelocity(0, 0f);
            }else{
                fixtureAvatar.getBody().setLinearVelocity(-velocidad, 0f);
            }

        }


        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        System.out.println("soltar pulsacion" + screenX + "-" + screenY + "-" + pointer);

        return super.touchDragged(screenX, screenY, pointer);


    }
}