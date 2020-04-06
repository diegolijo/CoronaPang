package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class EscenarioScreen extends Pantalla {

    public static int SUELO, PARED_R, TECHO, PARED_L, AVATAR, PIERNAS, PAPEL, BOTON_L, BOTON_R, BOTON_DISPARO, PRIMER_VIRUS, ELEMENT_COLISION1 = 498, ELEMENT_COLISION2 = 499;

    //FILTROS DE MCOLISION
    private final short CATEGORY_ESCENARIO = 0000000000000001;
    private final short CATEGORY_AVATAR = 0000000000000010;
    private short CATEGORY_BOLA = 0000000000000100;

    // activa colisiones
    final short MASK_ESCENARIO = 0000000000000111;
    final short MASK_AVATAR = 0000000000000111;
    final short MASK_BOLA = 0000000000000011;

    //----------  scene2d  --------------
    private Stage stage;
    private Texture textVirusVerde, textVirusRosa, textFondo, textActorAvatar, textPapel, textBotonL, textBotonR, textBotonDisparo, textPiernas;
    private ActorScene2d[] actorArray = new ActorScene2d[500];
    private Sound sonidoBola, sonidoPedo, sonidoDisparo;
    private Music musicaLaVida;

    //------------  box2d  ------------
    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camara;
    private Body[] bodyArray = new Body[500];
    private Fixture[] fixtureArray = new Fixture[500];
    private PolygonShape shape;
    private CircleShape circleShape;
    private BodyDef def = new BodyDef();
    private int siguieteElemento = 0;
    private float velocidadInicialX = 10;
    private boolean colisionBox2d;

    private Contact contactoBox2d;


    //
    private boolean puedoDisparar = true;
    private boolean disparoIniciado, tocoTecho, papelUsado = false;


    // ------------  fuses -------------
    private int numBolas;
    private boolean camaraBox2d = false, debugStage = false, invencible = false;
    private int velocidadSubida;
    private float posicionPapel;


    public EscenarioScreen(MeuGdxGame juego, int numBolas) {
        super(juego);

        this.numBolas = numBolas;
        fixtureArray[ELEMENT_COLISION1] = null;
        fixtureArray[ELEMENT_COLISION2] = null;


    }


    public Fixture getFixtureArray(int indice) {
        return fixtureArray[indice];
    }


    public void setPuedoDisparar(boolean puedoDisparar) {
        this.puedoDisparar = puedoDisparar;
    }

    public boolean isPuedoDisparar() {
        return puedoDisparar;
    }


    //--- cear actores y fixtures --
    public void crearEscenario() {

        actorArray[siguieteElemento] = new ActorScene2d(textFondo, 64, 36, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;

        shape = new PolygonShape();

        float[] x = {32, 64, 32, 0};
        float[] y = {5, 18, 36, 18};
        float[] w = {32, 1, 32, 1};
        float[] h = {1, 18, 1, 18};
        SUELO = 1;
        PARED_R = 2;
        TECHO = 3;
        PARED_L = 4;

        for (int i = 0; i < 4; i++) {
            def.position.set(x[i], y[i]);
            bodyArray[i] = world.createBody(def);
            shape.setAsBox(w[i], h[i]);
            fixtureArray[i] = bodyArray[i].createFixture(shape, 1);

            //filtros colision box2d
            Filter filter = fixtureArray[i].getFilterData();
            filter.categoryBits = CATEGORY_ESCENARIO;
            filter.maskBits = MASK_ESCENARIO;
            fixtureArray[i].setFilterData(filter);

            siguieteElemento = siguieteElemento + 1;
        }

    }

    public void creatBotones() {


        //izquierda
        BOTON_L = siguieteElemento;
        actorArray[siguieteElemento] = new ActorScene2d(textBotonL, 5, 5, false);
        stage.addActor(actorArray[siguieteElemento]);
        actorArray[siguieteElemento].setPosition(0, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;

        //derecha
        BOTON_R = siguieteElemento;
        actorArray[siguieteElemento] = new ActorScene2d(textBotonR, 5, 5, false);
        stage.addActor(actorArray[siguieteElemento]);
        actorArray[siguieteElemento].setPosition(100, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;

        //disparar
        BOTON_DISPARO = siguieteElemento;
        actorArray[siguieteElemento] = new ActorScene2d(textBotonDisparo, 5, 5, false);
        stage.addActor(actorArray[siguieteElemento]);
        actorArray[siguieteElemento].setPosition(550, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;


    }

    public void crearPapel() {

        PAPEL = siguieteElemento;
        actorArray[PAPEL] = new ActorScene2d(textPapel, 2f, 36, false);
        stage.addActor(actorArray[PAPEL]);
        actorArray[PAPEL].setPosition(0, -440); // ocultamos el papel debajo del suelo
        siguieteElemento = siguieteElemento + 1;
    }

    public void crearAvatar() {

        AVATAR = siguieteElemento;
        //dimensiones en metros 2x6
        float w = 1.5f;
        float h = 4;

        //posicionamos el avatar centrado en la pantalla
        float x = 32f;
        float y = 5f;


        shape = new PolygonShape();
        def.position.set(x + w, y + h);
        def.type = BodyDef.BodyType.DynamicBody;
        bodyArray[siguieteElemento] = world.createBody(def);

        // almacenamos un array de vertices
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-w, -h);
        vertices[1] = new Vector2(w, -h);
        vertices[2] = new Vector2(0, 0.9f * h);

        //introducimos los vertices de la forma
        shape.set(vertices);

        //creamos fixtureArray
        fixtureArray[siguieteElemento] = bodyArray[siguieteElemento].createFixture(shape, 1000000);

        // filtro colision box2d
        Filter filter = fixtureArray[siguieteElemento].getFilterData();
        filter.categoryBits = CATEGORY_AVATAR;
        filter.maskBits = MASK_AVATAR;
        fixtureArray[siguieteElemento].setFilterData(filter);

        //creamos actor scene2d
        actorArray[siguieteElemento] = new ActorScene2d(textActorAvatar, 2f * w, 2f * h, false);
        stage.addActor(actorArray[siguieteElemento]);


        siguieteElemento = siguieteElemento + 1;

        // piernas

        PIERNAS = siguieteElemento;

        actorArray[siguieteElemento] = new ActorScene2d(textPiernas, 4, 4, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;


    }

    public void crearBolasIniciales() {
        PRIMER_VIRUS = siguieteElemento;


        float[] radio = {1, 2.5f, 2f, 1.2f, 2.1f, 2, 2, 2, 3, 3};
        int[] x = {2, 10, 20, 30, 40, 50, 55, -3, 0, 1};
        int y = 25;

        for (int i = 0; i < numBolas; i++) {

            crearBola(siguieteElemento, radio[i], x[i], y, velocidadInicialX * 5, 0, 1, textVirusVerde);


        }
    }

    public void crearBola(int numeroBola, float radio, float posX, float posY, float velX,
                          float velY, float restitution, Texture texture) {

        circleShape = new CircleShape();
        def.type = BodyDef.BodyType.DynamicBody;

        //creando bodys
        def.position.set(posX, posY);
        bodyArray[numeroBola] = world.createBody(def);

        //creando fixtures
        circleShape.setRadius(radio);
        fixtureArray[numeroBola] = bodyArray[numeroBola].createFixture(circleShape, 1);

        //filtro colision box2d
        Filter filter = fixtureArray[numeroBola].getFilterData();
        filter.categoryBits = CATEGORY_BOLA;
        filter.maskBits = MASK_BOLA;
        fixtureArray[numeroBola].setFilterData(filter);

        //conservamos la velocidad cunto choca el suelo
        fixtureArray[numeroBola].setFriction(0);
        fixtureArray[numeroBola].setRestitution(restitution);


        //cremos los actores
        actorArray[numeroBola] = new ActorScene2d(texture, radio * 2f, radio * 2f, true);
        stage.addActor(actorArray[numeroBola]);

        //impulso inicial
        bodyArray[numeroBola].setLinearVelocity(velX, velY);


        siguieteElemento = siguieteElemento + 1;

    }

    // ----------------------------

    private void actualizarPosicionActores() {


        float w = actorArray[AVATAR].getWidth() / 2;
        float h = actorArray[AVATAR].getHeight() / 2;

        ///avatar
        float x = (bodyArray[AVATAR].getPosition().x * TO_PIXELES) - w;
        float y = (bodyArray[AVATAR].getPosition().y * TO_PIXELES) - h;

        actorArray[AVATAR].setPosition(x, y);
        //piernas
        actorArray[PIERNAS].setPosition(x, y - actorArray[PIERNAS].getHeight());


        //viruses
        for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {

            w = actorArray[i].getWidth() / 2;
            h = actorArray[i].getHeight() / 2;
            x = (bodyArray[i].getPosition().x * TO_PIXELES) - w;
            y = (bodyArray[i].getPosition().y * TO_PIXELES) - h;
            actorArray[i].setPosition(x, y);

        }


    }


    private void colisionPapel() {


        //recoremos todas las bolas
        for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {


            // posicionPapel del rollo
            float x = actorArray[PAPEL].getX();
            float y = actorArray[PAPEL].getY();

            if (actorArray[i].isVivo() && disparoIniciado && !tocoTecho) {

                //comprobamos eje y, si el actor se encuentra entre el limete superior e inferior del papel
                if (actorArray[i].getY() < y + actorArray[PAPEL].getHeight() && y < actorArray[i].getY() + actorArray[i].getHeight()) {
                    // //comprobamos eje x
                    float distanciaX = x - actorArray[i].getX();
                    if (actorArray[i].getWidth() > distanciaX && distanciaX > -actorArray[PAPEL].getWidth()) {

                        romperBola(i);

                        //ocultamos el papel
                        actorArray[PAPEL].setPosition(actorArray[PAPEL].getX(), -500);
                        tocoTecho = true;
                        disparoIniciado = false;
                        setPuedoDisparar(true);

                    }
                }
            }
        }
    }

    private void romperBola(int i) {

        sonidoPedo.play();

        Gdx.input.vibrate(100);


        //
        float reduccion = 2f;
        float radioMinimo = 0.5f;
        float radio = fixtureArray[i].getShape().getRadius();
        float vel = fixtureArray[i].getBody().getLinearVelocity().y;
        float nuevaVel;
        Vector2 pos = fixtureArray[i].getBody().getPosition();

        if (Math.abs(vel) < 3) {
            nuevaVel = Math.abs(vel);
        } else if (vel > 0) {
            nuevaVel = vel;
        } else {
            nuevaVel = -vel * 1.2f;
        }


        fixtureArray[i].getShape().setRadius(radio / reduccion);
        actorArray[i].setWidth((radio / reduccion) * 2);
        actorArray[i].setHeigth((radio / reduccion) * 2);
        actorArray[i].setSize(radio * 2 / reduccion * TO_PIXELES, radio * 2 / reduccion * TO_PIXELES);
        //impulso despues de romper
        fixtureArray[i].getBody().setLinearVelocity(velocidadInicialX, nuevaVel);
        // velociodad nueva bola
        crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, -velocidadInicialX, nuevaVel, 1f, actorArray[i].getTexture());

        //mata la bola
        if (fixtureArray[siguieteElemento - 1].getShape().getRadius() < radioMinimo) { // comprueba el tamaño de la bola para matar bola

            // matamos las 2 bolas
            actorArray[i].setVivo(false);
            actorArray[siguieteElemento - 1].setVivo(false);


            Filter filter = fixtureArray[i].getFilterData();
            filter.maskBits = 000000000000000;
            fixtureArray[i].setFilterData(filter);
            fixtureArray[siguieteElemento - 1].setFilterData(filter);
        }
    }


    public void disparo(boolean inicio) {


        if (inicio) {
            posicionPapel = actorArray[AVATAR].getX() + actorArray[AVATAR].getWidth() / 2 - actorArray[PAPEL].getWidth() / 2;   //almacemanos la posicion del avatrar en el momento del diaparo
            velocidadSubida = 50;                          //altuda desde la que empieza a sascender
            disparoIniciado = true;

            actorArray[PAPEL].setPosition(posicionPapel, velocidadSubida - actorArray[PAPEL].getHeight());

            sonidoDisparo.play();

        } else {
            velocidadSubida += 7;
            actorArray[PAPEL].setPosition(posicionPapel, velocidadSubida - actorArray[PAPEL].getHeight());

            if (velocidadSubida > 700) {
                tocoTecho = true;
                disparoIniciado = false;
                setPuedoDisparar(true);
            }

        }

    }


    public void contactoBolas() {

        float reduccion = 1.2f;
        float radioMinimo = 0.5f;
        float radio;

        //contacto entre bolas
        {
            for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {
                if (fixtureArray[ELEMENT_COLISION1] == fixtureArray[i]) {
                    for (int j = PRIMER_VIRUS; j < siguieteElemento; j++) {
                        if (fixtureArray[ELEMENT_COLISION2] == fixtureArray[j]) {


                            // comprueba el tamaño de las bolas
                            if (fixtureArray[i].getShape().getRadius() > radioMinimo) {
                                radio = fixtureArray[ELEMENT_COLISION1].getShape().getRadius();
                                fixtureArray[i].getShape().setRadius(radio / reduccion);
                                actorArray[i].setX((radio / reduccion) * 2);
                                actorArray[i].setY((radio / reduccion) * 2);


                                Vector2 vel = fixtureArray[i].getBody().getLinearVelocity();
                                Vector2 pos = fixtureArray[i].getBody().getPosition();
                                //        for (int k = 0; k < 5; k++) {
                                crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, vel.x * 10, -vel.y * 10, 1f, actorArray[i].getTexture());
                                //       }

                            } else {
                                fixtureArray[i].getShape().setRadius(0.1f);
                                actorArray[i].setX(0.1f);
                                actorArray[i].setY(0.1f);
                                fixtureArray[i].setRestitution(0f);
                            }

                            if (fixtureArray[j].getShape().getRadius() > radioMinimo) {
                                radio = fixtureArray[ELEMENT_COLISION2].getShape().getRadius();
                                fixtureArray[j].getShape().setRadius(radio / reduccion);
                                actorArray[j].setX((radio / reduccion) * 2);
                                actorArray[j].setY((radio / reduccion) * 2);

                                Vector2 vel = fixtureArray[j].getBody().getLinearVelocity();
                                Vector2 pos = fixtureArray[j].getBody().getPosition();
                                //          for (int k = 5; k < 5; k++) {
                                crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, vel.x * 10, -vel.y * 10, 1f, actorArray[j].getTexture());

                                //        }


                            } else {
                                fixtureArray[j].getShape().setRadius(0.1f);
                                actorArray[j].setX(0.1f);
                                actorArray[j].setY(0.1f);
                                fixtureArray[j].setRestitution(0f);
                            }


                        }
                    }
                }
            }
        }
    }


    private void muerte() {
        if (!invencible) {
            Filter filter = fixtureArray[AVATAR].getFilterData();
            filter.maskBits = 000000000000110;
            fixtureArray[AVATAR].setFilterData(filter);

            //disparar musica de muerte
            // esperar 2 segundos

      /*      juego.gameOver();

            juego.setScreen(juego.getGameOverScreen());*/

        }

    }

//-------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void show() {


        //creamos un imputProcesor  le mandanmos la referencia de esta pantalla
        ProcesadorInEscenario p = new ProcesadorInEscenario(this);
        Gdx.input.setInputProcessor(p);


        if (juego.getSO() == 0) {
            //android
        }
        if (juego.getSO() == 1) {
            //desktop
        }


        stage = new Stage(new FitViewport(640, 360));
        stage.setDebugAll(debugStage);  //marca bordes de objeto


        textVirusVerde = juego.getManager().get("virusAmarillo100.png");
        textVirusRosa = juego.getManager().get("virusRosa100.png");
        textActorAvatar = juego.getManager().get("avatar300x100.png");
        textPapel = juego.getManager().get("papelCulo100x3600.png");
        textBotonL = juego.getManager().get("boton100pxL.png");
        textBotonR = juego.getManager().get("boton100pxR.png");
        textBotonDisparo = juego.getManager().get("boton100pxPapel.png");
        textFondo = juego.getManager().get("Alameda.png");
        textPiernas = juego.getManager().get("Patinete100px.png");

        sonidoDisparo = juego.getManager().get("audio/disparo.wav");
        sonidoPedo = juego.getManager().get("audio/pedo.wav");
        musicaLaVida = juego.getManager().get("audio/LaVidaEsAsi.mp3");

        musicaLaVida.play();
        musicaLaVida.setVolume(0.2f);
        musicaLaVida.setLooping(true);


        // creamos world render y camara
        world = new World(new Vector2(0, -9.8f), true);

        world.setGravity(new Vector2(0, -40f));                 // aumentamos gravedad

        renderer = new Box2DDebugRenderer();
        camara = new OrthographicCamera(64, 36);
        camara.translate(32, 18);  //y = 8

        crearEscenario();
        crearAvatar();
        crearPapel();
        creatBotones();
        crearBolasIniciales();


        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                //almacena en las posiciones ELEMENT_COLISION1,ELEMENT_COLISION2 las fixtureArray en contacto
                fixtureArray[ELEMENT_COLISION1] = contact.getFixtureA();
                fixtureArray[ELEMENT_COLISION2] = contact.getFixtureB();
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


        if (colisionBox2d) {  // -----  entra si se produjo una colision Box2d
            colisionBox2d = false;


            // ------------  si colisiona  avatar  contra una bola
            if (fixtureArray[ELEMENT_COLISION1] == fixtureArray[AVATAR]) {

                for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {
                    if (fixtureArray[ELEMENT_COLISION2] == fixtureArray[i]) {
                        muerte();
                    }
                }
            }
            if (fixtureArray[ELEMENT_COLISION2] == fixtureArray[AVATAR]) {

                for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {
                    if (fixtureArray[ELEMENT_COLISION1] == fixtureArray[i]) {
                        muerte();
                    }
                }
            }


        }//----------------------------------------------------------------

        actualizarPosicionActores();


        if (disparoIniciado) {
            disparo(false);
            setPuedoDisparar(false);
            if (tocoTecho) {
                actorArray[PAPEL].setPosition(0, -400); // oculramos el papel
                tocoTecho = false;
                puedoDisparar = true;
            }
        }

        colisionPapel();


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

  /*      super.hide();
        super.dispose();

        Gdx.input.setInputProcessor(null);

        for (int i = 0; i < siguieteElemento; i++) {
            if (bodyArray[i] != null) {
                bodyArray[i].destroyFixture(fixtureArray[i]);
                world.destroyBody(bodyArray[i]);
            }
            if (actorArray[i] != null) {
                actorArray[i].remove();
            }
        }
*//*
        fixtureArray[ELEMENT_COLISION1].getBody().destroyFixture(fixtureArray[ELEMENT_COLISION1]);
        fixtureArray[ELEMENT_COLISION2].getBody().destroyFixture(fixtureArray[ELEMENT_COLISION2]);
*//*
        shape.dispose();
        renderer.dispose();
        world.dispose();*/

    }

    @Override
    public void dispose() {
        super.dispose();

        Gdx.input.setInputProcessor(null);

        for (int i = 0; i < siguieteElemento; i++) {
            if (bodyArray[i] != null) {
                bodyArray[i].destroyFixture(fixtureArray[i]);
                world.destroyBody(bodyArray[i]);
            }
            if (actorArray[i] != null) {
                actorArray[i].remove();
            }
        }

        shape.dispose();
        renderer.dispose();
        world.dispose();
    }
}
//--------------------------------------------------------------------------------------------------------------------------------

