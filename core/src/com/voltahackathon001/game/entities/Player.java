package com.voltahackathon001.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.voltahackathon001.game.screens.PlayScreen;

public class Player extends Sprite{
    private final float GRAVITY = 1000f;
    private float ACCELERATION = 1000;
    private final float TOP_SPEED = 600;

    private float jumptimer = 0;

    private float oldPosX,oldPosY;
    private Vector2 pos,vel,accel;
    private Animation animation,jumpleft,jumpright,runleft,runright,idle;
    private TextureAtlas atlas;
    private static Texture texture = new Texture(Gdx.files.internal("baddie16.png"));
    private PlayScreen screen;
    private boolean isJumping = false;
    private boolean collisionX,collisionY;
    public Player(float x, float y, PlayScreen screen){
        super(texture);
        // atlas = new TextureAtlas(gdx.files.internal("atlas.atlas"));
        pos = new Vector2(x,y);
        setX(pos.x);
        setY(pos.y);
        vel = new Vector2(0,0);
        accel = new Vector2(0,0);
        this.screen = screen;
    }
    public void jump(){
        isJumping=true;
        jumptimer = 0;
    }
    public void update(float delta){
        jumptimer+=delta;
        if(jumptimer>0.25){
            isJumping = false;
        }
        // apply gravity acceleration
        accel.y = -GRAVITY;
        accel.x = 0;
        if(screen.aPressed){
            accel.x = -ACCELERATION;
        }
        if(screen.dPressed){
            accel.x = +ACCELERATION;
        }

        // apply acceleration to velocity
        vel.y += accel.y*delta;
        vel.x += accel.x*delta;

        if(vel.x>0){
            vel.x-= 10;
        }
        else if(vel.x<0){
            vel.x+= 10;
        }


        // Cap velocity in both directions to 10
        // TODO TEST ME (maybe have to be combined velocity
        // TODO add ) to above comment

        if(vel.x>TOP_SPEED){
            vel.x = TOP_SPEED;
        }

        if(vel.y>TOP_SPEED){
            vel.y = TOP_SPEED;
        }
        if(vel.y<-TOP_SPEED){
            vel.y = -TOP_SPEED;
        }
        if(vel.x<-TOP_SPEED){
            vel.y = -TOP_SPEED;
        }
        if(Math.abs(vel.y)<0.1f){
            vel.y=0;
        }
        if(Math.abs(vel.x)<0.1f){
            vel.x=0;
        }
        // fix with collision detection
        // TODO TEST ME

        // move in x
        oldPosX = pos.x;
        pos.x+=vel.x*delta;

        // check collision in x
        if(vel.x<0){ // going left
            collisionX = collidesLeft();
        }else if(vel.x>0){
            collisionX = collidesRight();
        }
        // react to collision in x
        if(collisionX){
            pos.x = oldPosX;
            if(isJumping&&!collisionY){
                if(vel.x>0){
                    vel.x = -500;
                    vel.y = 1000;
                    isJumping = false;
                }else if(vel.x<0){
                    vel.x = 500;
                    vel.y = 1000;
                    isJumping = false;
                }
            }else{
                vel.x = 0;
            }
        }

        collisionY = false;

        // move in y
        oldPosY = pos.y;
        pos.y+=vel.y*delta;

        // check collision in y
        if(vel.y<0){ // going down (yelling timber)
            collisionY = collidesBottom();

        }else if(vel.y>0){ // going up (whispering timber)
            collisionY = collidesTop();
        }
        // react to collision
        if(collisionY){
            pos.y = oldPosY;
            vel.y = 0;
        }

        if(collisionY&&vel.y==0&&isJumping){
            vel.y=5000;
            isJumping = false;
        }

        // set sprite position
        setX(pos.x);
        setY(pos.y);

    }

    public boolean collidesLeft(){
        if(screen.isCellBlocked(pos.x, pos.y)){
            return true;
        }else return false;
    }
    public boolean collidesRight(){
        if(screen.isCellBlocked(pos.x + getWidth(), pos.y)){
            return true;
        }else return false;
    }
    public boolean collidesTop(){
        if(screen.isCellBlocked(pos.x + getWidth()/2, pos.y+getHeight())){
            return true;
        }else return false;
    }
    public boolean collidesBottom(){
        if(screen.isCellBlocked(pos.x + getWidth()/2, pos.y)){
            return true;
        }else return false;
    }
    private void setUpAnimations(){
        // Set up the different animations here
    }
    public float getVelX(){
        return vel.x;
    }
    public float getVelY() { return vel.y;}
    public String getVitals(){
        return(
                "VelX: "+vel.x+"\n"+
                "VelY: "+vel.y+"\n"+
                "AccX: "+accel.x+"\n"+
                "AccY: "+accel.y
                );
    }
}
