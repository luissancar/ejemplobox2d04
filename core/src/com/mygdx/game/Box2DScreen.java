package com.mygdx.game;

/**
 * Created by luissancar on 29/12/16.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by luissancar on 28/12/16.
 */

public class Box2DScreen extends BaseScreen {

    private World world;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera; //cámara 2d;
    private Body objeto01Body, sueloBody, pinchoBody;  // entedad de nuestro mundo, posición, velocidad, no tiene forma
    private Fixture objeto01Fixture, sueloFixture, pinchoFixture; // forma del body
    private boolean debeSaltar,objeto01Saltando;





    public Box2DScreen(MyGdxGame game) {
        super(game);
    }

    @Override
    public void show() {
        world=new World(new Vector2(0,-10), true); // Gravedad puede ser negativa, positiva, izquierda derecha;
        // segundo parámetro: si no tenemos nada que simular lo ponemos false y ahorramos recursos

        //You generally will not use this in a released version of your game, but for testing purposes we will set it up now like so:
        renderer=new Box2DDebugRenderer();
        camera=new OrthographicCamera(26,9);
        camera.translate(0,1); // mueve la cámara 1 metro hacia arriba



        world.setContactListener(new ContactListener() {  //Creamos un listener para contactos
            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();
                if ((fixtureA==objeto01Fixture && fixtureB==sueloFixture) || (fixtureB==objeto01Fixture && fixtureA==sueloFixture)){
                    if (Gdx.input.isTouched()){  // si sigue pulsado continua saltando
                        debeSaltar=true;
                    }
                    objeto01Saltando=false; //no se puede llamar a saltar directamente
                }




            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA(), fixtureB = contact.getFixtureB();
                if ((fixtureA==objeto01Fixture && fixtureB==sueloFixture) || (fixtureB==objeto01Fixture && fixtureA==sueloFixture)){
                    objeto01Saltando=true; //no se puede llamar a saltar directamente
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
        BodyDef objeto01Def=createObjeto01Def();
        objeto01Body=world.createBody(objeto01Def);
        sueloBody=world.createBody(createSueloDef());
        pinchoBody=world.createBody(createPinchoDef(10));
        PolygonShape pinchoShape=new PolygonShape();
        pinchoFixture=pinchoBody.createFixture(pinchoShape,1);
        pinchoShape.dispose();

        PolygonShape objeto01Shape=new PolygonShape();
        objeto01Shape.setAsBox(0.5f,0.5f); // en metros
        objeto01Fixture=objeto01Body.createFixture(objeto01Shape,1); //1= densidad
        objeto01Shape.dispose(); // no lo necesitamos
        PolygonShape sueloShape=new PolygonShape();
        sueloShape.setAsBox(500,1); // las dimesiones se multiplican por 2, sería 1000,2
        sueloFixture=sueloBody.createFixture(sueloShape,1);
        sueloShape.dispose();


        pinchoFixture=createPinchoFixture(pinchoBody);




    }

    private Fixture createPinchoFixture(Body pinchoBody){
        Vector2[] vertices= new Vector2[3]; //Creamos las coordenadas del triángulo en sentido contrario reloj.
        vertices[0]=new Vector2(-0.5f, -0.5f);
        vertices[1]=new Vector2(0.5f, -0.5f);
        vertices[2]=new Vector2(0, 0.5f);
        PolygonShape shape=new PolygonShape();
        shape.set(vertices);
        Fixture fix=pinchoBody.createFixture(shape,1);
        shape.dispose();
        return fix;
    }

    private BodyDef createPinchoDef(float x) {
        BodyDef defCuerpo=new BodyDef();
        defCuerpo.position.set(x,0.5f);
        defCuerpo.type= BodyDef.BodyType.StaticBody; // por defecto todos son estáticos.
        return defCuerpo;
    }

    private BodyDef createSueloDef() {
        BodyDef defCuerpo=new BodyDef();
        defCuerpo.position.set(0,-1);
        defCuerpo.type= BodyDef.BodyType.StaticBody; // por defecto todos son estáticos.
        return defCuerpo;
    }


    private BodyDef createObjeto01Def() {
        BodyDef defCuerpo=new BodyDef();
        defCuerpo.position.set(0,0);
        defCuerpo.type= BodyDef.BodyType.DynamicBody;
        return defCuerpo;
    }

    @Override
    public void dispose() {
        objeto01Body.destroyFixture(objeto01Fixture);
        pinchoBody.destroyFixture(pinchoFixture);
        world.destroyBody(objeto01Body);
        world.destroyBody(pinchoBody);
        world.dispose();
        renderer.dispose();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (debeSaltar){  // para no hacer saltos en el aire
            debeSaltar=false;
            saltar();
        }
        if (Gdx.input.justTouched() && !objeto01Saltando){
            debeSaltar=true;
        }
        float velocidadY=objeto01Body.getLinearVelocity().y;
        objeto01Body.setLinearVelocity(8, velocidadY);  //8 m/s horizontal, y mantenemos la actual
    /*
    To update our simulation we need to tell our world to step. Stepping basically updates the world objects through time.
     The first argument is the time-step, or the amount of time you want your world to simulate. In most cases you want this to be a fixed time step. libgdx recommends using either 1/45f (which is 1/45th of a second) or 1/300f (1/300th of a second).
     The other two arguments are velocityIterations and positionIterations. For now we will leave these at 6 and 2,
    * */
        //world.step(1/60f, 6, 2);
        world.step(delta,6,2);  // con estos valores funciona bien
        camera.update();
        renderer.render(world,camera.combined);  // matriz de proyección
    }

    private void saltar() {
        Vector2 position=objeto01Body.getPosition();
        objeto01Body.applyLinearImpulse(0,5,position.x,position.y,true); // fuerza aplicada, posición y que lo despierte
    }
}