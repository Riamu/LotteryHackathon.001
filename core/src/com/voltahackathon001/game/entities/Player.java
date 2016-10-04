package com.voltahackathon001.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class Player extends Sprite{
    private float GRAVITY = 1.5f;
    private float ACCELERATION;
    private float TOP_SPEED;
    private Vector2 pos,vel,accel;
    private Animation animation,jumpleft,jumpright,runleft,runright,idle;
    private TextureAtlas atlas;
    private static Texture texture = new Texture(Gdx.files.internal("baddie16.png"));
    private TiledMapTileLayer collisionLayer;

    public Player(float x, float y, TiledMapTileLayer collisionLayer){
        super(texture);
        // atlas = new TextureAtlas(gdx.files.internal("atlas.atlas"));
        pos = new Vector2(x,y);
        setX(pos.x);
        setY(pos.y);
        vel = new Vector2(0,0);
        accel = new Vector2(0,0);
        this.collisionLayer = collisionLayer;
    }

    public void update(float delta){

        // apply gravity acceleration
        accel.y -= GRAVITY;
        // apply acceleration to velocity
        vel.y+=accel.y;
        vel.x+=accel.x;
        // Cap velocity in both directions to 10
        // TODO TEST ME (maybe have to be combined velocity
        /*
        if(vel.x>10){
            vel.x = 10;
        }
        if(vel.y>10){
            vel.y = 10;
        }
        if(vel.y<-10){
            vel.y = -10;
        }
        if(vel.x<-10){
            vel.y = -10;
        }
        */

        // fix with collision detection
        // TODO TEST ME

        if(isCollidingX()){
            vel.x = 0;
        }
        if(isCollidingY()){
            Gdx.app.log("Collision Detected on Y","");
            vel.y = 0;
        }

        // move based on velocity and delta
        pos.x+=vel.x*delta;
        //pos.y+=vel.y*delta;
        pos.y-=1;
        setX(pos.x);
        setY(pos.y);


    }

    private void setUpAnimations(){
        // Set up the different animations here
    }
    public boolean isCollidingX(){

        return false;

    }
    public boolean isCollidingY(){
        if(vel.y>0){
            if(collisionLayer.getCell(
                    (int)getX(),(int)getY()
            )!=null){
                Gdx.app.log("Collided on Y","");
            }
        }if(vel.y<0){

        }
        return false;
    }

}
