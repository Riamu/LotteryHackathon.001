package com.voltahackathon001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    private TextureAtlas jumpermanAtlas;
    private float elapsedTime = 0;
    private TiledMapTile filledTile;
    public boolean aPressed = false;
    public boolean dPressed = false;
    public boolean leftPressed = false;
    public boolean rightPressed = false;
    public boolean upPressed = false;
    public boolean downPressed = false;

    private Vector3 playerPos = new Vector3();

    // PlayScreen constructor initializes our Game World and such
    public PlayScreen(CaveGame game){
        // cave generator object, we initialize with 40 width and 100 height
        cg = new CaveGenerator(40,100);
        this.game = game;

        // init camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,400);

        // init map to our single tile map
        map = new TmxMapLoader().load("testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        collisionLayer = (TiledMapTileLayer)map.getLayers().get(0);

        // generate layer based on our cave generation
        filledTile = collisionLayer.getCell(0,0).getTile();
        layer1 = nextLayer(cg.getCaveInt());
        // add our layer to the map
        map.getLayers().add(layer1);
        // set our collision layer to the layer we added
        collisionLayer = layer1;
        // set layer 0 to invisible
        map.getLayers().get(0).setVisible(false);

        //add player
        float y = collisionLayer.getTileHeight();
        float x = 0;
        while (isCellBlocked(x,y)){
            x += collisionLayer.getTileWidth();
        }
        // set camera position to player position
        camera.position.x = x;
        camera.position.y = y;
        // set player TextureAtlas
        jumpermanAtlas = new TextureAtlas(Gdx.files.internal("jumperman.pack"));
        // init playerObject
        player = new Player(x,y,this,jumpermanAtlas);

        // set up renderer
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        // elapsed time is total game time let's hope the player doesn't play for like 2 billion seconds because
        // then we'll overflow
        elapsedTime+=delta;

        // flush screen
        Gdx.gl20.glClearColor(1,1,1,0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // get and set player positions
        playerPos.x = player.getX();
        playerPos.y = player.getY();

        camera.project(playerPos);

        player.update(delta);

        // Move camera if the player is on camera bounds
        if(player.getX() - camera.position.x > 0){ // player on right bound
            camera.translate((player.getVelX())*delta,0);
        }else if(player.getX() - camera.position.x < 0){ // player on left bound
            camera.translate((player.getVelX())*delta,0);
        }
        if(player.getY() - camera.position.y >
                camera.viewportHeight-camera.viewportHeight/1.05){ // player on bottom bound
            camera.translate(0,player.getVelY()*delta);
        }else if(player.getY()-camera.position.y <
                camera.viewportHeight / 1.2 - camera.viewportHeight){ // player on top bound
            camera.translate(0,player.getVelY()*delta);
        }

        // camera has to be updated every loop because we move it
        camera.update();
        // set the batch projection matrix so libGDX knows where to place things
        game.batch.setProjectionMatrix(camera.combined);
        // set TiledMap renderer view to camera
        renderer.setView(camera);
        // render tiledMap
        renderer.render();

        // Draw things
        game.batch.begin();
            game.batch.draw(player.getAnimation().getKeyFrame(elapsedTime, true),player.getX(),player.getY());
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
        //TODO: REMOVE
        else if(keycode==Input.Keys.LEFT) {
            leftPressed = true;
        } else if(keycode==Input.Keys.RIGHT) {
            rightPressed = true;
        } else if(keycode==Input.Keys.UP) {
            upPressed = true;
        } else if (keycode==Input.Keys.DOWN) {
            downPressed = true;
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
        //TODO: REMOVE
        else if(keycode==Input.Keys.LEFT) {
            leftPressed = false;
        } else if(keycode==Input.Keys.RIGHT) {
            rightPressed = false;
        } else if(keycode==Input.Keys.UP) {
            upPressed = false;
        } else if (keycode==Input.Keys.DOWN) {
            downPressed = false;
        }
        return false;
    }

    // give it a position on the screen and it will tell you if that cell is blocked
    public boolean isCellBlocked(float x, float y){
        if(collisionLayer.getCell((int)(x/collisionLayer.getTileWidth()),
                (int)(y/collisionLayer.getTileHeight()))!=null)
            return collisionLayer.getCell((int)(x/collisionLayer.getTileWidth()),
                    (int)(y/collisionLayer.getTileHeight()))
                    .getTile().getProperties().containsKey("collidable");
        else
            return false;
    }

    // Generate a map based on the cave generation algorithm
    public TiledMapTileLayer nextLayer(int[][] caveArray){
        TiledMapTileLayer returnMe = new TiledMapTileLayer(caveArray.length,caveArray[0].length,32,32);

        for(int x = 0 ; x < caveArray.length ; x++){
            for(int y = 0 ; y < caveArray[x].length ; y++){
                // if our cave array has returned a 1
                if(caveArray[x][y]==1) {
                    // Y values have to be reverse because libGDX is y-up and this array is y-down
                    returnMe.setCell(x, 99-y,new TiledMapTileLayer.Cell()); // create a new cell
                    returnMe.getCell(x, 99-y).setTile(filledTile); // add a new tile to that cell
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
