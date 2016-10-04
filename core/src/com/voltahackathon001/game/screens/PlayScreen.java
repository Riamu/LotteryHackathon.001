package com.voltahackathon001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.voltahackathon001.game.CaveGame;
import com.voltahackathon001.game.entities.Player;

public class PlayScreen implements Screen, InputProcessor{
    private OrthographicCamera camera;
    private CaveGame game;
    private TiledMapRenderer renderer;
    private TiledMap map;
    private Player player;
    private TiledMapTileLayer collisionLayer;



    public PlayScreen(CaveGame game){
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false,1280,720);
        map = new TmxMapLoader().load("testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        collisionLayer = (TiledMapTileLayer)map.getLayers().get(0);
        player = new Player(700,400, collisionLayer);
        // generate map based on 2D array input
        // set up renderer
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0,0,0,0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render();

        game.batch.begin();
            game.batch.draw(player.getTexture(),player.getX(),player.getY());
        game.batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public void show() {

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

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
