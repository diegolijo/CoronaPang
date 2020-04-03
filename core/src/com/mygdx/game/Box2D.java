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
    private static int PRIMER_VIRUS;

    //----------  scene2d  --------------
    private Stage stage;
    private Texture textVirusVerde, textVirusRosa, textActorAvatar, textPapel, textBoton;
    private ActorScene2d[] actoresArray = new ActorScene2d[500];
    private boolean contactoAvatar = false;


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
    private boolean pulsacion = false;
    private boolean colisionBox2d;
    private boolean muriendo;
    private Contact contactoBox2d;

    //FILTROS DE MCOLISION
    private final short CATEGORY_ESCENARIO = 0000000000000001;
    private final short CATEGORY_AVATAR = 0000000000000010;
    private short CATEGORY_BOLA = 0000000000000100;

    // activa colisiones
    final short MASK_ESCENARIO = 0000000000000111;
    final short MASK_AVATAR = 0000000000000111;
    final short MASK_BOLA = 0000000000000111;

    //
    private boolean puedoDisparar = true;
    private boolean disparoIniciado, tocoTecho, papelUsado = false;

    //---------  Procesador  ------------
    ProcesadorIn procesadorIn;


    // ------------  fuses -------------
    private int numBolas;
    private boolean camaraBox2d = true, debugStage = true;
    private int velocidadSubida;
    private float posicionPapel;
    private int count = 0;
    private boolean invencible = true;


    public Box2D(MeuGdxGame juego, int numBolas) {
        super(juego);

        this.numBolas = numBolas;
        fixture[498] = null;
        fixture[499] = null;

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
        textBoton = new Texture("boton100px.png");


        // creamos world render y camara
        world = new World(new Vector2(0, -9.8f), true);
        renderer = new Box2DDebugRenderer();
        camara = new OrthographicCamera(64, 36);
        camara.translate(32, 18);  //y = 8

        crearEscenario();
        crearPapel();
        crearFixAvatar();
        creatBotones();
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


        if (colisionBox2d) {  // -----  entra si se produgo una colision Box2d  las fistures se almacenan en fixture[498] fixture[499]------
            colisionBox2d = false;

            // ------------  si colisiona  avatar  contra una bola
            if (fixture[498] == fixture[5] && fixture[499] != fixture[0] && fixture[499] != fixture[1] && fixture[499] != fixture[3]) {
                muerte();
            } else if (fixture[499] == fixture[5] && fixture[498] != fixture[0] && fixture[498] != fixture[1] && fixture[498] != fixture[3]) {
                muerte();
            }//-------------   --------------------   -----------


        }//----------------------------------------------------------------------------------------------------------------------------------


        if (disparoIniciado) {
            disparo(false);
            setPuedoDisparar(false);
            if (tocoTecho) {
                actoresArray[4].setPosition(0, -400); // oculramos el papel
                tocoTecho = false;
                puedoDisparar = true;
            }
        }

        actualizarPosicionActores();

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


    public void crearPapel() {

        actoresArray[siguieteElemento] = new ActorScene2d(textPapel, 2f, 36);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(0, -440); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;
    }

    public void creatBotones() {


        //izquierda
        actoresArray[siguieteElemento] = new ActorScene2d(textBoton, 5, 5);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(0, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;

        //derecha
        actoresArray[siguieteElemento] = new ActorScene2d(textBoton, 5, 5);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(100, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;

        //disparar
        actoresArray[siguieteElemento] = new ActorScene2d(textBoton, 5, 5);
        stage.addActor(actoresArray[siguieteElemento]);
        actoresArray[siguieteElemento].setPosition(550, 5); // oculramos el papel
        siguieteElemento = siguieteElemento + 1;


    }


    public void crearFixAvatar() {

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
        actoresArray[siguieteElemento] = new ActorScene2d(textActorAvatar, 2f * w, 2f * h);
        stage.addActor(actoresArray[siguieteElemento]);


        siguieteElemento = siguieteElemento + 1;


    }

    public void crearBolasIniciales() {
        PRIMER_VIRUS = siguieteElemento;


        float[] radio = {1, 2.5f, 2f, 1.2f, 2.1f, 2, 2, 2, 3, 3};
        int[] x = {2, 10, 20, 30, 40, 50, 55, -3, 0, 1};
        int y = 25;

        for (int i = 0; i < numBolas; i++) {

            crearBola(siguieteElemento, radio[i], x[i], y, 20, 0, 1, textVirusVerde);


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
        filter.categoryBits = CATEGORY_AVATAR;
        filter.maskBits = MASK_BOLA;
        fixture[numeroBola].setFilterData(filter);


        fixture[numeroBola].setRestitution(restitution);


        //cremos los actores
        actoresArray[numeroBola] = new ActorScene2d(texture, radio * 2f, radio * 2f);
        stage.addActor(actoresArray[numeroBola]);

        //impulso inicial
        body[numeroBola].applyLinearImpulse(body[numeroBola].getMass() * velX, body[numeroBola].getMass() * velY, posX, posY, true);


        siguieteElemento = siguieteElemento + 1;

    }

    // ----------------------------

    private void actualizarPosicionActores() {


        float w = actoresArray[5].getWidth() / 2;
        float h = actoresArray[5].getHeight() / 2;

        ///avatar
        float x = (body[5].getPosition().x * TO_PIXELES) - w;
        float y = (body[5].getPosition().y * TO_PIXELES) - h;
        actoresArray[5].setPosition(x, y);


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
        for (int i = 9; i < siguieteElemento; i++) {
           /* if (fixture[i] ==){

            }*/

            // posicionPapel del rollo
            float x = actoresArray[4].getX() + actoresArray[4].getWidth() / 2;
            float y = actoresArray[4].getY() + actoresArray[4].getHeight();

            actoresArray[4].getHeight();

            // posicionPapel bola
            float Ox = actoresArray[i].getX();
            float Oy = actoresArray[i].getY();
            float w = actoresArray[i].getWidth();


            //bola por debajo del rollo¿¿??
            if (Oy < y && y > actoresArray[4].getY() && disparoIniciado && !tocoTecho) {
                //restamos a las posicionPapel del papel a de la bola
                // si estan tocando el valor debe ser menor que el ancho de la bola
                float contacto = x - Ox;
                if (contacto > 0 && contacto < w) {
                    romperBola(i);
                }
            }
        }
    }

    private void romperBola(int i) {

        //
        float reduccion = 2f;
        float radioMinimo = 0.5f;
        float radio = fixture[i].getShape().getRadius();
        Vector2 vel = fixture[i].getBody().getLinearVelocity();
        Vector2 pos = fixture[i].getBody().getPosition();
        float mas = fixture[i].getBody().getMass();


        fixture[i].getShape().setRadius(radio / reduccion);
        actoresArray[i].setX((radio / reduccion) * 2);
        actoresArray[i].setY((radio / reduccion) * 2);
        actoresArray[i].setSize(radio * 2 / reduccion * TO_PIXELES, radio * 2 / reduccion * TO_PIXELES);
        fixture[i].getBody().applyLinearImpulse(mas, Math.abs(mas), pos.y, pos.x, true);

        crearBola(siguieteElemento, radio / reduccion, pos.x, pos.y, 0, 0, 1f, actoresArray[i].getTexture());
        fixture[siguieteElemento - 1].getBody().applyLinearImpulse(-1 * mas, Math.abs(mas), pos.y, pos.x, true);

        if (fixture[siguieteElemento - 1].getShape().getRadius() < radioMinimo) { // comprueba el tamaño de la bola para matar bola
            fixture[i].getShape().setRadius(radio / reduccion);
            actoresArray[i].setX((radio / reduccion) * 2);
            actoresArray[i].setY((radio / reduccion) * 2);
            actoresArray[i].setSize(radio * 2 / reduccion * TO_PIXELES, radio * 2 / reduccion * TO_PIXELES);


            Filter filter = fixture[i].getFilterData();
            filter.maskBits = 000000000000000;
            fixture[i].setFilterData(filter);
            fixture[siguieteElemento - 1].setFilterData(filter);
        }

        actoresArray[4].setPosition(actoresArray[4].getX(), -3600);
        tocoTecho = true;
        disparoIniciado = false;
        setPuedoDisparar(true);
    }


    public void disparo(boolean inicio) {


        if (inicio) {
            posicionPapel = actoresArray[5].getX();     //almacemanos la posicion del avatrar en el momento del diaparo
            velocidadSubida = 30;                          //altuda desde la que empieza a sascender
            disparoIniciado = true;

            actoresArray[4].setPosition(posicionPapel, velocidadSubida - actoresArray[4].getHeight());
        } else {
            velocidadSubida += 7;
            actoresArray[4].setPosition(posicionPapel, velocidadSubida - actoresArray[4].getHeight());

            if (velocidadSubida > 700) {
                actoresArray[4].setPosition(actoresArray[4].getX(), -3600);
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
            for (int i = 5; i < siguieteElemento; i++) {
                if (fixture[498] == fixture[i]) {
                    for (int j = 5; j < siguieteElemento; j++) {
                        if (fixture[499] == fixture[j]) {


                            // comprueba el tamaño de las bolas
                            if (fixture[i].getShape().getRadius() > radioMinimo) {
                                radio = fixture[498].getShape().getRadius();
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
                                radio = fixture[499].getShape().getRadius();
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
        if (!invencible){
            Filter filter = fixture[5].getFilterData();
            filter.maskBits = 000000000000110;
            fixture[5].setFilterData(filter);
        }

    }

}







