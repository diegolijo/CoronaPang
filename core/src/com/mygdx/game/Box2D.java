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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;


public class Box2D extends Pantalla {

    public static final float TO_PIXELES = 10f;
    public static int SUELO, PARED_R, TECHO, PARED_L, AVATAR, PAPEL, BOTON_L, BOTON_R, BOTON_DISPARO, PRIMER_VIRUS, ELEMENT_COLISION1 = 498, ELEMENT_COLISION2 = 499;


    //----------  scene2d  --------------
    private Stage stage;
    private Texture textVirusVerde, textVirusRosa, textActorAvatar, textPapel, textBotonL, textBotonR, textBotonDisparo;
    private ActorScene2d[] actoresArray = new ActorScene2d[500];


    //------------  box2d  ------------
    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camara;
    private Body[] body = new Body[500];
    private Fixture[] fixture = new Fixture[500];
    private PolygonShape shape;
    private CircleShape circleShape;
    private BodyDef def = new BodyDef();
    private int siguieteElemento = 0;
    private float velocidadInicialX = 10;
    private boolean colisionBox2d;

    private Contact contactoBox2d;

    //FILTROS DE MCOLISION
    private final short CATEGORY_ESCENARIO = 0000000000000001;
    private final short CATEGORY_AVATAR = 0000000000000010;
    private short CATEGORY_BOLA = 0000000000000100;

    // activa colisiones
    final short MASK_ESCENARIO = 0000000000000111;
    final short MASK_AVATAR = 0000000000000111;
    final short MASK_BOLA = 0000000000000011;

    //
    private boolean puedoDisparar = true;
    private boolean disparoIniciado, tocoTecho, papelUsado = false;

    //---------  Procesador  ------------
    ProcesadorIn procesadorIn;


    // ------------  fuses -------------
    private int numBolas;
    private boolean camaraBox2d = false, debugStage = false;
    private int velocidadSubida;
    private float posicionPapel;
    private boolean invencible = true;


    public Box2D(MeuGdxGame juego, int numBolas) {
        super(juego);

        this.numBolas = numBolas;
        fixture[ELEMENT_COLISION1] = null;
        fixture[ELEMENT_COLISION2] = null;

    }

    public ActorScene2d getActoresBolas(int indice) {
        return actoresArray[indice];
    }

    public Fixture getFixture(int indice) {

        return fixture[indice];
    }


    @Override
    public void show() {


        stage = new Stage(new FitViewport(640, 360));

        stage.setDebugAll(debugStage);  //marca bordes de objeto se debe dartamaño a los actores

        textVirusVerde = new Texture("virusAmarillo100.png");
        textVirusRosa = new Texture("virusRosa100.png");
        textActorAvatar = new Texture("avatar300x100.png");
        textPapel = new Texture("papelCulo100x3600.png");
        textBotonL = new Texture("boton100pxL.png");
        textBotonR = new Texture("boton100pxR.png");
        textBotonDisparo = new Texture("boton100pxPapel.png");


        // creamos world render y camara
        world = new World(new Vector2(0, -9.8f), true);

        world.setGravity(new Vector2(0, -40f));                 // aumentamos velocidad de la caida

        renderer = new Box2DDebugRenderer();
        camara = new OrthographicCamera(64, 36);
        camara.translate(32, 18);  //y = 8

        crearEscenario();
        crearFixAvatar();
        crearPapel();
        creatBotones();
        crearBolasIniciales();


        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                //almacena en las posiciones ELEMENT_COLISION1,ELEMENT_COLISION2 las fixture en contacto
                fixture[ELEMENT_COLISION1] = contact.getFixtureA();
                fixture[ELEMENT_COLISION2] = contact.getFixtureB();
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


        if (colisionBox2d) {  // -----  entra si se produgo una colision Box2d
            colisionBox2d = false;

            // ------------  si colisiona  avatar  contra una bola
            if (fixture[ELEMENT_COLISION1] == fixture[AVATAR] && fixture[ELEMENT_COLISION2] != fixture[SUELO] && fixture[ELEMENT_COLISION2] != fixture[PARED_R] && fixture[ELEMENT_COLISION2] != fixture[PARED_L] && fixture[ELEMENT_COLISION2] != fixture[TECHO]) {
                muerte();
            } else if (fixture[ELEMENT_COLISION2] == fixture[AVATAR] && fixture[ELEMENT_COLISION1] != fixture[SUELO] && fixture[ELEMENT_COLISION1] != fixture[PARED_R] && fixture[ELEMENT_COLISION1] != fixture[PARED_L] && fixture[ELEMENT_COLISION1] != fixture[TECHO]) {
                muerte();
            }//-------------   --------------------   -----------


        }//----------------------------------------------------------------------------------------------------------------------------------

        actualizarPosicionActores();


        if (disparoIniciado) {
            disparo(false);
            setPuedoDisparar(false);
            if (tocoTecho) {
                actoresArray[PAPEL].setPosition(0, -400); // oculramos el papel
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


    public void setPuedoDisparar(boolean puedoDisparar) {
        this.puedoDisparar = puedoDisparar;
    }

    public boolean isPuedoDisparar() {
        return puedoDisparar;
    }


    public void setProcesadorIn(ProcesadorIn p) {
        this.procesadorIn = p;
    }


    //--- cear actores y fixtures --

    public void crearEscenario() {

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
            body[i] = world.createBody(def);
            shape.setAsBox(w[i], h[i]);
            fixture[i] = body[i].createFixture(shape, 1);

            //filtros colision box2d
            Filter filter = fixture[i].getFilterData();
            filter.categoryBits = CATEGORY_ESCENARIO;
            filter.maskBits = MASK_ESCENARIO;
            fixture[i].setFilterData(filter);

            siguieteElemento = siguieteElemento + 1;
        }

    }

    public void creatBotones() {


        //izquierda
        BOTON_L = siguieteElemento;
        actoresArray[siguieteElemento] = new ActorScene2d(textBotonL, 5, 5, false);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(0, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;

        //derecha
        BOTON_R = siguieteElemento;
        actoresArray[siguieteElemento] = new ActorScene2d(textBotonR, 5, 5, false);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(100, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;

        //disparar
        BOTON_DISPARO = siguieteElemento;
        actoresArray[siguieteElemento] = new ActorScene2d(textBotonDisparo, 5, 5, false);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(550, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;


    }

    public void crearPapel() {

        PAPEL = siguieteElemento;
        actoresArray[PAPEL] = new ActorScene2d(textPapel, 2f, 36, false);
        stage.addActor(actoresArray[PAPEL]);
        actoresArray[PAPEL].setPosition(0, -440); // ocultamos el papel debajo del suelo
        siguieteElemento = siguieteElemento + 1;
    }

    public void crearFixAvatar() {

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
        body[siguieteElemento] = world.createBody(def);

        // almacenamos un array de vertices
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-w, -h);
        vertices[1] = new Vector2(w, -h);
        vertices[2] = new Vector2(0, 0.9f * h);

        //introducimos los vertices de la forma
        shape.set(vertices);

        //creamos fixture
        fixture[siguieteElemento] = body[siguieteElemento].createFixture(shape, 1000000);

        // filtro colision box2d
        Filter filter = fixture[siguieteElemento].getFilterData();
        filter.categoryBits = CATEGORY_AVATAR;
        filter.maskBits = MASK_AVATAR;
        fixture[siguieteElemento].setFilterData(filter);

        //creamos actor scene2d
        actoresArray[siguieteElemento] = new ActorScene2d(textActorAvatar, 2f * w, 2f * h, false);
        stage.addActor(actoresArray[siguieteElemento]);


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
        body[numeroBola] = world.createBody(def);

        //creando fixtures
        circleShape.setRadius(radio);
        fixture[numeroBola] = body[numeroBola].createFixture(circleShape, 1);

        //filtro colision box2d
        Filter filter = fixture[numeroBola].getFilterData();
        filter.categoryBits = CATEGORY_BOLA;
        filter.maskBits = MASK_BOLA;
        fixture[numeroBola].setFilterData(filter);

        //conservamos la velocidad cunto choca el suelo
        fixture[numeroBola].setFriction(0);
        fixture[numeroBola].setRestitution(restitution);


        //cremos los actores
        actoresArray[numeroBola] = new ActorScene2d(texture, radio * 2f, radio * 2f, true);
        stage.addActor(actoresArray[numeroBola]);

        //impulso inicial
        body[numeroBola].setLinearVelocity(velX, velY);


        siguieteElemento = siguieteElemento + 1;

    }

    // ----------------------------

    private void actualizarPosicionActores() {


        float w = actoresArray[AVATAR].getWidth() / 2;
        float h = actoresArray[AVATAR].getHeight() / 2;

        ///avatar
        float x = (body[AVATAR].getPosition().x * TO_PIXELES) - w;
        float y = (body[AVATAR].getPosition().y * TO_PIXELES) - h;
        actoresArray[AVATAR].setPosition(x, y);


        //viruses
        for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {

            w = actoresArray[i].getWidth() / 2;
            h = actoresArray[i].getHeight() / 2;
            x = (body[i].getPosition().x * TO_PIXELES) - w;
            y = (body[i].getPosition().y * TO_PIXELES) - h;
            actoresArray[i].setPosition(x, y);

        }


    }


    private void colisionPapel() {


        //recoremos todas las bolas
        for (int i = PRIMER_VIRUS; i < siguieteElemento; i++) {


            // posicionPapel del rollo
            float x = actoresArray[PAPEL].getX();
            float y = actoresArray[PAPEL].getY();

            //comprobamos eje y
            if (actoresArray[i].getY() < y + actoresArray[PAPEL].getHeight() && y < actoresArray[i].getY() + actoresArray[i].getHeight() && disparoIniciado && !tocoTecho) {
                // //comprobamos eje x
                float contacto = x - actoresArray[i].getX();
                if (actoresArray[i].getWidth() > contacto && contacto > -actoresArray[PAPEL].getWidth() && actoresArray[i].isVivo()) {

                    romperBola(i);

                    //ocultamos el papel
                    actoresArray[PAPEL].setPosition(actoresArray[PAPEL].getX(), -500);
                    tocoTecho = true;
                    disparoIniciado = false;
                    setPuedoDisparar(true);

                }
            }
        }
    }

    private void romperBola(int i) {

        Gdx.input.vibrate(100);

        //
        float reduccion = 2f;
        float radioMinimo = 0.5f;
        float radio = fixture[i].getShape().getRadius();
        float vel = fixture[i].getBody().getLinearVelocity().y;
        float nuevaVel;
        Vector2 pos = fixture[i].getBody().getPosition();

        if (Math.abs(vel) < 3) {
            nuevaVel = Math.abs(vel);
        } else if (vel > 0) {
            nuevaVel = vel;
        } else {
            nuevaVel = -vel * 1.2f;
        }


        fixture[i].getShape().setRadius(radio / reduccion);
        actoresArray[i].setX((radio / reduccion) * 2);
        actoresArray[i].setY((radio / reduccion) * 2);
        actoresArray[i].setSize(radio * 2 / reduccion * TO_PIXELES, radio * 2 / reduccion * TO_PIXELES);
        //impulso despues de romper
        fixture[i].getBody().setLinearVelocity(velocidadInicialX, nuevaVel);
        // velociodad nueva bola
        crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, -velocidadInicialX, nuevaVel, 1f, actoresArray[i].getTexture());

        //mata la bola
        if (fixture[siguieteElemento - 1].getShape().getRadius() < radioMinimo) { // comprueba el tamaño de la bola para matar bola

            // matamos las 2 bolas
            actoresArray[i].setVivo(false);
            actoresArray[siguieteElemento - 1].setVivo(false);


            Filter filter = fixture[i].getFilterData();
            filter.maskBits = 000000000000000;
            fixture[i].setFilterData(filter);
            fixture[siguieteElemento - 1].setFilterData(filter);
        }
    }


    public void disparo(boolean inicio) {


        if (inicio) {
            posicionPapel = actoresArray[AVATAR].getX() + actoresArray[AVATAR].getWidth() / 2 - actoresArray[PAPEL].getWidth() / 2;   //almacemanos la posicion del avatrar en el momento del diaparo
            velocidadSubida = 50;                          //altuda desde la que empieza a sascender
            disparoIniciado = true;

            actoresArray[PAPEL].setPosition(posicionPapel, velocidadSubida - actoresArray[PAPEL].getHeight());
        } else {
            velocidadSubida += 7;
            actoresArray[PAPEL].setPosition(posicionPapel, velocidadSubida - actoresArray[PAPEL].getHeight());

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
                if (fixture[ELEMENT_COLISION1] == fixture[i]) {
                    for (int j = PRIMER_VIRUS; j < siguieteElemento; j++) {
                        if (fixture[ELEMENT_COLISION2] == fixture[j]) {


                            // comprueba el tamaño de las bolas
                            if (fixture[i].getShape().getRadius() > radioMinimo) {
                                radio = fixture[ELEMENT_COLISION1].getShape().getRadius();
                                fixture[i].getShape().setRadius(radio / reduccion);
                                actoresArray[i].setX((radio / reduccion) * 2);
                                actoresArray[i].setY((radio / reduccion) * 2);


                                Vector2 vel = fixture[i].getBody().getLinearVelocity();
                                Vector2 pos = fixture[i].getBody().getPosition();
                                //        for (int k = 0; k < 5; k++) {
                                crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, vel.x * 10, -vel.y * 10, 1f, actoresArray[i].getTexture());
                                //       }

                            } else {
                                fixture[i].getShape().setRadius(0.1f);
                                actoresArray[i].setX(0.1f);
                                actoresArray[i].setY(0.1f);
                                fixture[i].setRestitution(0f);
                            }

                            if (fixture[j].getShape().getRadius() > radioMinimo) {
                                radio = fixture[ELEMENT_COLISION2].getShape().getRadius();
                                fixture[j].getShape().setRadius(radio / reduccion);
                                actoresArray[j].setX((radio / reduccion) * 2);
                                actoresArray[j].setY((radio / reduccion) * 2);

                                Vector2 vel = fixture[j].getBody().getLinearVelocity();
                                Vector2 pos = fixture[j].getBody().getPosition();
                                //          for (int k = 5; k < 5; k++) {
                                crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, vel.x * 10, -vel.y * 10, 1f, actoresArray[j].getTexture());

                                //        }


                            } else {
                                fixture[j].getShape().setRadius(0.1f);
                                actoresArray[j].setX(0.1f);
                                actoresArray[j].setY(0.1f);
                                fixture[j].setRestitution(0f);
                            }


                        }
                    }
                }
            }
        }
    }


    private void muerte() {
        if (!invencible) {
            Filter filter = fixture[AVATAR].getFilterData();
            filter.maskBits = 000000000000110;
            fixture[AVATAR].setFilterData(filter);
        }

    }

}







