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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class EscenarioScreen extends Pantallas {

    public static int SUELO, PARED_R, TECHO, PARED_L, AVATAR, PATAS, PAPEL, BOTON_L, BOTON_R, BOTON_DISPARO, PRIMER_VIRUS, ELEMENT_COLISION1 = 498, ELEMENT_COLISION2 = 499;
    private int PATAS_ACTUAL;

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
    private Texture textVirusVerde, textVirusRosa, textFondo, textFondo2, textFondo3, textFondo4, textActorAvatar, textPapel, textBotonL, textBotonR, textBotonDisparo, textPatas0, textPatas1, textPatas2, textPatas3, textPatas4;
    private ActorScene2d[] actorArray = new ActorScene2d[500];
    private Sound sonidoBola, sonidoPedo, sonidoDisparo, sonidoMuerte;
    private Music musicaLaVida;
    private Skin skin;
    private TextButton boton;
    private int tiempo = 60;

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

    //pantallas


    // banderas
    private boolean puedoDisparar = true;
    private boolean disparoIniciado;
    private boolean tocoTecho;
    private int nivel;



    // ------------  fuses -------------

    private boolean camaraBox2d = false, debugStage = false, invencible = false;
    private int velocidadSubida;
    private float posicionPapel;
    private boolean dispose = true;


    public EscenarioScreen(MeuGdxGame juego, int nivel) {
        super(juego);
        this.nivel = nivel;
        fixtureArray[ELEMENT_COLISION1] = null;
        fixtureArray[ELEMENT_COLISION2] = null;

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
        textFondo2 = juego.getManager().get("Belvis.png");
        textFondo3 = juego.getManager().get("Castros.png");
        textFondo4 = juego.getManager().get("VillaParaiso.png");

        textPatas0 = juego.getManager().get("Patas200px0.png");
        textPatas1 = juego.getManager().get("Patas200px1.png");
        textPatas2 = juego.getManager().get("Patas200px2.png");
        textPatas3 = juego.getManager().get("Patas200px3.png");
        textPatas4 = juego.getManager().get("Patas200px4.png");


        sonidoDisparo = juego.getManager().get("audio/disparo.wav");
        sonidoPedo = juego.getManager().get("audio/pedo.wav");
        musicaLaVida = juego.getManager().get("audio/LaVidaEsAsi.mp3");
        sonidoMuerte = juego.getManager().get("audio/gritoMuerte.mp3");

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


        startTimer();


        //temporizador
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        //   Window boton = new Window(""+tiempo,skin);
        boton = new TextButton(tiempo + "", skin);
        boton.setPosition(320 - boton.getWidth() / 2, 320);
        stage.addActor(boton);


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

        if (dispose) {


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

        //    Gdx.input.setInputProcessor(null);

        for (int i = 0; i < siguieteElemento; i++) {
            if (bodyArray[i] != null) {
                bodyArray[i].destroyFixture(fixtureArray[i]);
                world.destroyBody(bodyArray[i]);
            }
            if (actorArray[i] != null) {
                actorArray[i].remove();
            }
        }

        if (bodyArray[498] != null) {
            bodyArray[498].destroyFixture(fixtureArray[498]);
        }
        if (bodyArray[499] != null) {
            bodyArray[499].destroyFixture(fixtureArray[499]);
        }
        shape.dispose();
        renderer.dispose();
        world.dispose();

    }

//--------------------------------------------------------------------------------------------------------------------------------


    private Timer.Task taskReloj = new Timer.Task() {
        @Override
        public void run() {
            cuentaRelloj();
        }


    };

    public void startTimer() {
        Timer.schedule(taskReloj, 1f, 1f);
    }

    public void cuentaRelloj() {

        tiempo = tiempo - 1;
        boton.setText(tiempo + "");
        if (tiempo == 0) {
            muerte();
        }
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


        //seleccoionamos el fondo
        switch (nivel) {

            case 1:
                actorArray[siguieteElemento] = new ActorScene2d(textFondo, 64, 36, false);
                stage.addActor(actorArray[siguieteElemento]);
                siguieteElemento = siguieteElemento + 1;
                break;
            case 2:
                actorArray[siguieteElemento] = new ActorScene2d(textFondo2, 64, 36, false);
                stage.addActor(actorArray[siguieteElemento]);
                siguieteElemento = siguieteElemento + 1;
                break;
            case 3:
                actorArray[siguieteElemento] = new ActorScene2d(textFondo3, 64, 36, false);
                stage.addActor(actorArray[siguieteElemento]);
                siguieteElemento = siguieteElemento + 1;
                break;
            default:
                actorArray[siguieteElemento] = new ActorScene2d(textFondo4, 64, 36, false);
                stage.addActor(actorArray[siguieteElemento]);
                siguieteElemento = siguieteElemento + 1;
                break;
        }


        //paredes
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

        //

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


        // patas
        PATAS = siguieteElemento;
        PATAS_ACTUAL = siguieteElemento;
        actorArray[siguieteElemento] = new ActorScene2d(textPatas0, 3, 4, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;

        actorArray[siguieteElemento] = new ActorScene2d(textPatas1, 3, 4, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;

        actorArray[siguieteElemento] = new ActorScene2d(textPatas2, 3, 4, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;

        actorArray[siguieteElemento] = new ActorScene2d(textPatas3, 3, 4, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;

        actorArray[siguieteElemento] = new ActorScene2d(textPatas4, 3, 4, false);
        stage.addActor(actorArray[siguieteElemento]);
        siguieteElemento = siguieteElemento + 1;


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


        startTPiernas();


    }

    //movimiento de la patas

    private int cuentaPatas = 0;

    private Timer.Task taskPiernas = new Timer.Task() {
        @Override
        public void run() {

            //si el avatar se mueve
            if (fixtureArray[AVATAR].getBody().getLinearVelocity().x > 1) {
                PATAS_ACTUAL += 1;
                if (PATAS_ACTUAL == PATAS + 4) {
                    PATAS_ACTUAL = PATAS;
                }
            } else if (fixtureArray[AVATAR].getBody().getLinearVelocity().x < -1) {

                if (PATAS_ACTUAL == PATAS) {
                    PATAS_ACTUAL = PATAS + 4;
                }
                PATAS_ACTUAL -= 1;
            } else {

                PATAS_ACTUAL = PATAS
                ;
            }
        }
    };


    public void startTPiernas() {
        Timer.schedule(taskPiernas, 1f, 0.1f);
    }


    public void crearBolasIniciales() {
        PRIMER_VIRUS = siguieteElemento;


        float[] radio = {2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] posX = {5, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] posY = {20, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] velInicialX = {velocidadInicialX, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        float[] restitucion = {1, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        int numBolas;
        Texture texture;


        //creamos la bolas de cada nivel
        switch (nivel) {
            case (1): {

                numBolas = 1;
                radio[0] = 3;
                posX[0] = 10;
                posY[0] = 25;
                texture = juego.getManager().get("virusAmarillo100.png");
                velInicialX[0] = velocidadInicialX;
                restitucion[0] = 0.95f;


                radio[1] = 0; //indicamos al for cuantas bolas tiene que hacer

                break;
            }
            case (2): {

                numBolas = 2;

                radio[1] = 3;
                posX[1] = 59;
                posY[1] = 20;
                velInicialX[1] = -velocidadInicialX;
                texture = juego.getManager().get("virusAmarillo100.png");
                restitucion[1] = 0.95f;


                radio[2] = 0;

                break;
            }
            case (3): {

                numBolas = 1;

                radio[0] = 4;
                posX[0] = 32;
                posY[0] = 25;
                velInicialX[0] = 0;
                restitucion[0] = 0.95f;
                texture = juego.getManager().get("virusRosa100.png");

                radio[1] = 0;

                break;
            }
            case (4): {

                numBolas = 1;

                radio[0] = 5;
                posX[0] = 5;
                posY[0] = 25;
                velInicialX[0] = 0;
                restitucion[0] = 0.95f;
                texture = juego.getManager().get("virusRosa100.png");

                radio[1] = 0;

                break;
            }
            default: {


                numBolas = 1;

                radio[0] = 1;
                posX[0] = 5;
                posY[0] = 20;
                velInicialX[0] = velocidadInicialX;
                texture = juego.getManager().get("virusRosa100.png");
                restitucion[0] = 0.95f;

                radio[1] = 0;
                break;
            }

        }

        for (int i = 0; i < numBolas; i++) {
            crearBola(siguieteElemento, radio[i], posX[i], posY[i], velInicialX[i], 0, restitucion[i], 1, texture);
        }








/*        for (int i = 0; i < nivel; i++) {
            int x = (int) (Math.random() * 20);
            crearBola(siguieteElemento, (int) (Math.random() * 2) + 0.5f, x, 18, velocidadInicialX, 0, 1, textVirusVerde);
        }*/
    }

    public void crearBola(int numeroBola, float radio, float posX, float posY, float velX,
                          float velY, float restitution, float densidad, Texture texture) {

        circleShape = new CircleShape();
        def.type = BodyDef.BodyType.DynamicBody;

        //creando bodys
        def.position.set(posX, posY);
        bodyArray[numeroBola] = world.createBody(def);

        //creando fixtures
        circleShape.setRadius(radio);
        fixtureArray[numeroBola] = bodyArray[numeroBola].createFixture(circleShape, densidad);

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
        actorArray[numeroBola].setVivo(true);
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

        //patas
        //ocultamos las demas paras
        actorArray[PATAS].setPosition(x, -200);
        actorArray[PATAS + 1].setPosition(x, -200);
        actorArray[PATAS + 2].setPosition(x, -200);
        actorArray[PATAS + 3].setPosition(x, -200);
        actorArray[PATAS + 4].setPosition(x, -200);

        actorArray[PATAS_ACTUAL].setPosition(x - 1, y - 7);


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
        Vector2 pos = fixtureArray[i].getBody().getPosition();


        fixtureArray[i].getShape().setRadius(radio / reduccion);
        actorArray[i].setWidth((radio / reduccion) * 2);
        actorArray[i].setHeigth((radio / reduccion) * 2);
        actorArray[i].setSize(radio * 2 / reduccion * TO_PIXELES, radio * 2 / reduccion * TO_PIXELES);
        //impulso despues de romper
        fixtureArray[i].getBody().setLinearVelocity(velocidadInicialX, Math.abs(vel));
        // velociodad nueva bola
        crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, -velocidadInicialX, Math.abs(vel), 1f, 0.95f, actorArray[i].getTexture());

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


        //----------------------- comprueba se se rompieron todas la bolas -------------------------
        int bolas = siguieteElemento - PRIMER_VIRUS;
        for (int j = PRIMER_VIRUS; j < siguieteElemento; j++) {
            if (!actorArray[j].isVivo()) {
                bolas -= 1;
            }

            if (bolas == 0) {
                siguiente();
            }

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
                                crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, vel.x * 10, -vel.y * 10, 1f, 0.95f, actorArray[i].getTexture());
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
                                crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, vel.x * 10, -vel.y * 10, 1f, 0.95f, actorArray[j].getTexture());

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

    private void siguiente() {

        taskReloj.cancel();


        musicaLaVida.stop();
        //disparar musica de muerte
        // esperar 2 segundos
        dispose = false;


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {

                //     menuScreen.set
                juego.setScreen(juego.getMenuScreen());

                dispose();

                juego.getMenuScreen().siguienteNivel();


            }
        }, 1);


    }


    private void muerte() {


        taskReloj.cancel();


        if (!invencible) {
            Filter filter = fixtureArray[AVATAR].getFilterData();
            filter.maskBits = 000000000000110;
            fixtureArray[AVATAR].setFilterData(filter);


            sonidoMuerte.play();
            musicaLaVida.stop();


            //sigue renderizando
            dispose = true;


            // esperar x segundos
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    GameOverScreen gameOverScreen = new GameOverScreen(juego);
                    juego.setScreen(gameOverScreen);
                    //      dispose();
                }
            }, 0.5f);


        }

    }
}