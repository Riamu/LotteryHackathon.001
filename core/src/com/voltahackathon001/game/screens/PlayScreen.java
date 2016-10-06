package com.voltahackathon001.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Clipboard;
import com.voltahackathon001.game.CaveGame;
import com.voltahackathon001.game.cavegeneration.CaveGenerator;
import com.voltahackathon001.game.cavegeneration.Cell;
import com.voltahackathon001.game.entities.Player;
import com.voltahackathon001.game.music.MusicPlayer;

public class PlayScreen implements Screen, InputProcessor{
    private OrthographicCamera camera;
    private CaveGame game;
    private TiledMapRenderer renderer;
    private TiledMap map;
    private Player player;
    private CaveGenerator cg;
    public TiledMapTileLayer collisionLayer;
    private TiledMapTileLayer layer1,bottomLayer;

    private float numOfChunks = 1;
    // music
    MusicPlayer music;

    private Texture background;
    private Color bareColour;

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
        // for now check if the clipboard contains a long (and assume it's a seed), later move
        // this to the menu
        boolean foundSeed = false;
        long desiredSeed = -1;
        String clippy = Gdx.app.getClipboard().getContents();
        try {
            desiredSeed = Long.parseLong(clippy);
            foundSeed = true;
            System.out.println("Found seed " + desiredSeed + ".");
        } catch(NumberFormatException e){
            System.out.println("Did not detect user desired seed.");
        }

        // cave generator object, we initialize with 40 width and 100 height
        if(foundSeed) {
            cg = new CaveGenerator(40, 100, desiredSeed);
        }else{
            cg = new CaveGenerator(40, 100);
        }

        generateName(cg.SEED);

        music = new MusicPlayer(cg.SEED);
        music.pumpUpTheMusic();

        System.out.println("Cave generation and music seed is " + cg.SEED);

        this.game = game;

        // init camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false,800,400);

        // load the background
        background = new Texture(Gdx.files.internal("background.png"));
        // make the background a repeating texture
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        //colour to display outside of the rendered cave
        bareColour = new Color(20/255f, 0f, 30/255f, 0f);

        // init map to our single tile map
        map = new TmxMapLoader().load("testmap.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, game.batch);
        collisionLayer = (TiledMapTileLayer)map.getLayers().get(0);

        // generate layer based on our cave generation
        bottomLayer = (TiledMapTileLayer)map.getLayers().get(0);
        filledTile = bottomLayer.getCell(0,0).getTile();
        layer1 = nextLayer(cg.getCaveInt());
        // add our layer to the map
        map.getLayers().add(layer1);
        // set our collision layer to the layer we added
        collisionLayer = layer1;
        // set layer 0 to invisible
        //map.getLayers().get(0).setVisible(false);

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

        // set input processor
        Gdx.input.setInputProcessor(this);
    }

    private void generateName(long seed){

    }

    @Override
    public void render(float delta) {
        // elapsed time is total game time let's hope the player doesn't play for like 340
        // sextillion seconds because then we'll overflow
        music.update();
        elapsedTime+=delta;

        // check if player is above the midpoint of the layer
        if(player.getY()>(collisionLayer.getHeight()*16-(50*16))){
            addNewChunk();
        }
        // flush screen
        Gdx.gl20.glClearColor(bareColour.r, bareColour.g, bareColour.b, bareColour.a);
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

        // draw the background first
        //drawBackground();

        // set TiledMap renderer view to camera
        renderer.setView(camera);
        game.batch.begin();
        renderer.renderImageLayer(new TiledMapImageLayer(new TextureRegion(background), 0, 0));
        game.batch.end();
        // render tiledMap
        renderer.render();

        // Draw things
        game.batch.begin();
            game.batch.draw(player.getAnimation().getKeyFrame(elapsedTime, true),player.getX(),player.getY());
        game.batch.end();
    }

    private void drawBackground(){
        game.batch.begin();
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        game.batch.draw(background,0,0);
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
        else if(keycode == Input.Keys.Q){
            addNewChunk();
        }
        else if (keycode == Input.Keys.M) {
            music.switchItUp();
        }else if(keycode == Input.Keys.ESCAPE){
            Gdx.app.exit();
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
    // get layers after the initial layer
    public TiledMapTileLayer nextLayer(int[][] caveArray, TiledMapTileLayer oldLayer){
        TiledMapTileLayer returnMe = new TiledMapTileLayer(oldLayer.getWidth(),oldLayer.getHeight()+caveArray[0].length,32,32);

        for(int x = 0 ; x < oldLayer.getWidth() ; x++){
            for(int y = 0 ; y < oldLayer.getHeight() ; y++){
                if(oldLayer.getCell(x,y)!=null){
                    returnMe.setCell(x,y, new TiledMapTileLayer.Cell());
                    returnMe.getCell(x,y).setTile(filledTile);
                }
            }
        }
        for(int x = 0 ; x < caveArray.length ; x++){
            for(int y = 0 ; y < caveArray[x].length ; y++){
                if(caveArray[x][caveArray[x].length-1-y] == 1){
                    returnMe.setCell(x, returnMe.getHeight()-1-y, new TiledMapTileLayer.Cell());

                    returnMe.getCell(x, returnMe.getHeight()-1-y).setTile(filledTile);
                }
            }
        }
        //Gdx.app.log("(x,y): ","("+returnMe.getWidth()+","+returnMe.getHeight()+")");
        return returnMe;
    }

    public void addNewChunk(){
        TiledMapTileLayer tempLayer = (TiledMapTileLayer)map.getLayers().get(1);
        TiledMapTileLayer newLayer = (nextLayer(cg.getNextInt(), tempLayer));
        map.getLayers().remove(1);
        map.getLayers().add(newLayer);
        collisionLayer = newLayer;
        numOfChunks++;
        Gdx.app.log("gened a new chunk",player.getX()+","+player.getY());
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
        music.turnDownForWhat();
        jumpermanAtlas.dispose();
        map.dispose();
        background.dispose();
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
