package com.mygdx.game;

/**
 * Created by luissancar on 29/12/16.
 */
import com.badlogic.gdx.Screen;

/**
 * Created by luissancar on 26/12/16.
 */

public abstract class BaseScreen implements Screen {

    protected MyGdxGame game;
    public BaseScreen(MyGdxGame game) { //para conectar con la aplicación principal
        this.game=game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}