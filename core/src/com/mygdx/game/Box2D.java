package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Box2D extends Pantalla {
    public static final float TO_PIXELES = 10f;

    //----------  scene2d  --------------
    private Stage stage;
    private Texture textVirusVerde, textVirusRosa, textActorAvatar;
    private ActorScene2d[] actoresBolas = new ActorScene2d[500];


    //------------  box2d  ------------
    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camara;
    private Body[] body = new Body[500];
    private Fixture[] fixture = new Fixture[500];
    private PolygonShape shape;
    private CircleShape circleShape;
    private BodyDef def = new BodyDef();
    private boolean colisionBox2d;
    private Contact contactoBox2d;
    private int siguieteFixture = 0;
    private boolean pulsacion = false;

    // fuses
    private boolean debugStage = true;
    private int numBolas = 5;
    private boolean camaraBox2d = true;


    public Box2D(MeuGdxGame juego) {
        super(juego);
    }

    @Override
    public void show() {


        stage = new Stage(new FitViewport(640, 360));

        stage.setDebugAll(debugStage);  //marca bordes de objeto se debe dartamaño a los actores

        textVirusVerde = new Texture("virusAmarillo100.png");
        textVirusRosa = new Texture("virusRosa100.png");
        textActorAvatar = new Texture("avatar300x100.png");


        // creamos world render y camara
        world = new World(new Vector2(0, -9.8f), true);
        renderer = new Box2DDebugRenderer();
        camara = new OrthographicCamera(64, 36);
        camara.translate(32, 18);  //y = 8

        crearEscenario();

        crearFixAvatar();


        crearBolasIniciales();


        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {

                //almacena en las posiciones 498,499 las fixture en contacto
                fixture[498] = contact.getFixtureA();
                fixture[499] = contact.getFixtureB();
                contactoBox2d = contact;
                colisionBox2d = true;


            }


            @Override
            public void endContact(Contact contact) {

            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });


    }


    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (colisionBox2d) {
            colisionBox2d();
            colisionBox2d = false;
        }

        moverActoresVirus();

        stage.act();
        stage.draw();
        world.step(delta, 6, 2);

        if (camaraBox2d) {
            camara.update();
            renderer.render(world, camara.combined);

        }


    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {

        for (int i = 0; i < body.length; i++) {
            body[i].destroyFixture(fixture[i]);
            world.destroyBody(body[i]);
        }

        shape.dispose();


        renderer.dispose();
        world.dispose();
    }


    public void crearEscenario() {

        shape = new PolygonShape();

        float[] x = {32, 64, 32, 0};
        float[] y = {0, 18, 36, 18};
        float[] w = {32, 1, 32, 1};
        float[] h = {1, 18, 1, 18};


        for (int i = 0; i < 4; i++) {
            def.position.set(x[i], y[i]);
            body[i] = world.createBody(def);
            shape.setAsBox(w[i], h[i]);
            fixture[i] = body[i].createFixture(shape, 1);

            siguieteFixture = siguieteFixture + 1;
        }

    }


    public void crearFixAvatar() {

        //dimensiones en metros 2x6
        float w = 1;
        float h = 3;

        //posicionamos el avatar centrado en la pantalla
        float x = 32f;
        float y = 1f;


        shape = new PolygonShape();
        def.position.set(x + w, y + h);
        def.type = BodyDef.BodyType.DynamicBody;
        body[siguieteFixture] = world.createBody(def);

        // almacenamos un array de vertices
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-w, -h);
        vertices[1] = new Vector2(w, -h);
        vertices[2] = new Vector2(0, 0.9f * h);

        //introducimos los vertices de la forma
        shape.set(vertices);


        fixture[siguieteFixture] = body[siguieteFixture].createFixture(shape, 10000);
        actoresBolas[siguieteFixture] = new ActorScene2d(textActorAvatar, 2f * w, 2f * h);
        stage.addActor(actoresBolas[siguieteFixture]);


        siguieteFixture = siguieteFixture + 1;


    }

    public void crearBolasIniciales() {


        float[] radio = {1, 0.5f, 1f, 1.2f, 2.1f, 2, 2, 2, 3, 3};
        int[] x = {2, 10, 20, 30, 40, 50, 55, -3, 0, 1};
        int y = 25;

        for (int i = 0; i < numBolas; i++) {

            crearBola(siguieteFixture, radio[i], x[i], y, 20, 0, 1, textVirusVerde);

            if (i == 1 || i == 3) {
                crearBola(siguieteFixture, radio[i], x[i], y, 20, 0, 1, textVirusRosa);
            }


        }
    }

    public void crearBola(int numeroBola, float radio, float posX, float posY, float velX, float velY, float restitution, Texture texture) {

        circleShape = new CircleShape();
        def.type = BodyDef.BodyType.DynamicBody;

        //creando bodys
        def.position.set(posX, posY);
        body[numeroBola] = world.createBody(def);

        //creando fixtures
        circleShape.setRadius(radio);
        fixture[numeroBola] = body[numeroBola].createFixture(circleShape, 1);
        fixture[numeroBola].setRestitution(restitution);


        //cremos los actores
        actoresBolas[numeroBola] = new ActorScene2d(texture, radio * 2f, radio * 2f);
        stage.addActor(actoresBolas[numeroBola]);

        //impulso inicial
        body[numeroBola].applyLinearImpulse(body[numeroBola].getMass() * velX, body[numeroBola].getMass() * velY, posX, posY, true);


        siguieteFixture = siguieteFixture + 1;

    }


    private void moverActoresVirus() {


        float w = actoresBolas[4].getWidth() / 2;
        float h = actoresBolas[4].getHeight() / 2;

        ///avatar
        float x = (body[4].getPosition().x * TO_PIXELES) - w;
        float y = (body[4].getPosition().y * TO_PIXELES) - h;
        actoresBolas[4].setPosition(x, y);


        //viruses
        for (int i = 5; i < siguieteFixture; i++) {

            w = actoresBolas[i].getWidth() / 2;
            h = actoresBolas[i].getHeight() / 2;
            x = (body[i].getPosition().x * TO_PIXELES) - w;
            y = (body[i].getPosition().y * TO_PIXELES) - h;
            actoresBolas[i].setPosition(x, y);

        }

    }


    private void colisionBox2d() {

        float reduccion = 1.2f;
        float radioMinimo = 0.5f;
        float radio;


    /*    //contacto entre bolas
        if (fixture[498] == fixture[siguieteFixture - 1] || fixture[499] == fixture[siguieteFixture - 1] && ultimaBola == fixture[498] || fixture[499] == ultimaBola) {
            // si alguna de las bolas es la ultima generada
        } else {
            for (int i = 5; i < siguieteFixture; i++) {
                if (fixture[498] == fixture[i]) {
                    for (int j = 5; j < siguieteFixture; j++) {
                        if (fixture[499] == fixture[j]) {


                            // comprueba el tamaño de las bolas
                            if (fixture[i].getShape().getRadius() > radioMinimo) {
                                radio = fixture[498].getShape().getRadius();
                                fixture[i].getShape().setRadius(radio / reduccion);
                                actoresBolas[i].setX((radio / reduccion) * 2);
                                actoresBolas[i].setY((radio / reduccion) * 2);
                                ultimaBola = fixture[i];

                                Vector2 vel = fixture[i].getBody().getLinearVelocity();
                                Vector2 pos = fixture[i].getBody().getPosition();
                                crearBola(siguieteFixture, radio / reduccion, pos.x, pos.y, vel.x, -vel.y, 1f, actoresBolas[i].getTexture());

                            } else {
                                fixture[i].getShape().setRadius(0.1f);
                                actoresBolas[i].setX(0.1f);
                                actoresBolas[i].setY(0.1f);
                                fixture[i].setRestitution(0f);
                            }

                            if (fixture[j].getShape().getRadius() > radioMinimo) {
                                radio = fixture[499].getShape().getRadius();
                                fixture[j].getShape().setRadius(radio / reduccion);
                                actoresBolas[j].setX((radio / reduccion) * 2);
                                actoresBolas[j].setY((radio / reduccion) * 2);

                                Vector2 vel = fixture[j].getBody().getLinearVelocity();
                                Vector2 pos = fixture[j].getBody().getPosition();
                                crearBola(siguieteFixture, radio / reduccion, pos.x, pos.y, vel.x, -vel.y, 1f, actoresBolas[j].getTexture());
                                ultimaBola = fixture[i];

                            } else {
                                fixture[j].getShape().setRadius(0.1f);
                                actoresBolas[j].setX(0.1f);
                                actoresBolas[j].setY(0.1f);
                                fixture[j].setRestitution(0f);
                            }




                        }
                    }
                }
            }
        }*/
    }

    public void movimiento(int x) {

 /*
        float reduccion = 2f;
        float radioMinimo = .3f;
        float radio;

        //contacto entre bolas
        for (int i = 4; i < siguieteFixture; i++) {
            if (fixture[4] == fixture[i]) {

                // comprueba el tamaño de las bolas
                if (fixture[i].getShape().getRadius() > radioMinimo) {
                    radio = fixture[4].getShape().getRadius();
                    fixture[i].getShape().setRadius(radio / reduccion);
                    actoresBolas[i].setX((radio / reduccion) * 2);
                    actoresBolas[i].setY((radio / reduccion) * 2);


                    Vector2 vel = fixture[i].getBody().getLinearVelocity();
                    Vector2 pos = fixture[i].getBody().getPosition();
                    crearBola(siguieteFixture, radio / reduccion, pos.x, pos.y, -vel.x, -vel.y, 1f, textVirusRosa);

                } else {
                    fixture[i].getShape().setRadius(0.1f);
                    actoresBolas[i].setX(0.1f);
                    actoresBolas[i].setY(0.1f);
                    fixture[i].setRestitution(0f);
                }


            }
        }*/


    }


    public Fixture getFixture(int index) {
        return fixture[index];
    }
}




