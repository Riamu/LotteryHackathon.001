package com.voltahackathon001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.voltahackathon001.game.CaveGame;
import com.voltahackathon001.game.cavegeneration.CaveGenerator;
import com.voltahackathon001.game.cavegeneration.Cell;
import com.voltahackathon001.game.entities.Player;

public class PlayScreen implements Screen, InputProcessor{
    private OrthographicCamera camera;
    private CaveGame game;
    private TiledMapRenderer renderer;
    private TiledMap map;
    private Player player;
    private CaveGenerator cg;
    public TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer layer0,layer1,layer2;


    private TiledMapTile filledTile;
    public boolean aPressed = false;
    public boolean dPressed = false;

    private Vector3 playerPos = new Vector3();
    public PlayScreen(CaveGame game){
        cg = new CaveGenerator(40,100);

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,400);
        map = new TmxMapLoader().load("testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        collisionLayer = (TiledMapTileLayer)map.getLayers().get(0);

        // generate map based on 2D array input
        filledTile = collisionLayer.getCell(0,0).getTile();
        layer1 = nextLayer(cg.getCaveInt());
        map.getLayers().add(layer1);
        collisionLayer = layer1;
        map.getLayers().get(0).setVisible(false);

        //add player
        float offset = 10;
        float y = collisionLayer.getTileHeight()+offset;
        float x = 0+offset;
        while (isCellBlocked(x,y)){
            x += collisionLayer.getTileWidth();
        }
        camera.position.x = x-offset;
        camera.position.y = y-offset;
        player = new Player(x-offset,y-offset,this);

        // set up renderer
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0,0,0,0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        playerPos.x = player.getX();
        playerPos.y = player.getY();

        camera.project(playerPos);

        player.update(delta);
        if(player.getX() - camera.position.x > 0){ // player on right bound
            camera.translate((player.getVelX())*delta,0);
        }else if(player.getX() - camera.position.x < 0){
            camera.translate((player.getVelX())*delta,0);
        }
        if(player.getY() - camera.position.y >
                camera.viewportHeight-camera.viewportHeight/1.05){
            camera.translate(0,player.getVelY()*delta);
        }else if(player.getY()-camera.position.y <
                camera.viewportHeight / 1.2 - camera.viewportHeight){
            camera.translate(0,player.getVelY()*delta);
        }
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
        if(keycode==Input.Keys.SPACE){
            player.jump();
        }
        else if(keycode==Input.Keys.A){
            aPressed = true;
        }
        else if(keycode==Input.Keys.D){
            dPressed = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode==Input.Keys.A){
            aPressed = false;
        }
        else if(keycode==Input.Keys.D){
            dPressed = false;
        }
        return false;
    }

    public boolean isCellBlocked(float x, float y){
        if(collisionLayer.getCell((int)(x/32),(int)(y/32))!=null)
            return collisionLayer.getCell((int)(x/32),(int)(y/32))
                    .getTile().getProperties().containsKey("collidable");
        else
            return false;
    }

    public TiledMapTileLayer nextLayer(int[][] caveArray){
        TiledMapTileLayer returnMe = new TiledMapTileLayer(caveArray.length,caveArray[0].length,32,32);

        for(int x = 0 ; x < caveArray.length ; x++){
            for(int y = 0 ; y < caveArray[x].length ; y++){
                if(caveArray[x][y]==1) {
                    returnMe.setCell(x, 99-y,new TiledMapTileLayer.Cell());
                    returnMe.getCell(x, 99-y).setTile(filledTile);
                }

            }
        }
        return returnMe;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        camera.update();
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
