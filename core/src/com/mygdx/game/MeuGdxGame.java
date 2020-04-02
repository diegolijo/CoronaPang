package com.mygdx.game;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class MeuGdxGame extends Game {

	@Override
	public void create() {

		//creamos una pantalla box2d le mandanmos la referencia al juego
		Box2D box2d = new Box2D(this);
		setScreen(box2d);

		//creamos un imputProcesora  le mandanmos la referencia a las pantalla box2d
		ProcesadorIn p  = new ProcesadorIn(box2d);
		Gdx.input.setInputProcessor(p);

	}

	@Override
	public void render() {
		super.render();



	}
}
