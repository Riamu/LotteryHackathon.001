package com.voltahackathon001.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.voltahackathon001.game.screens.PlayScreen;

public class Player extends Sprite{
    // physics constants
    private final float GRAVITY = 1000f;
    private float ACCELERATION = 1000;
    private final float TOP_SPEED = 600;

    private float ANIMATION_SPEED = 0.1f;
    private float jumptimer = 0;

    private float oldPosX,oldPosY;
    private Vector2 pos,vel,accel;
    private Animation animation,jumpleft,jumpright,runleft,runright,idle;
    private TextureAtlas jumpermanAtlas;
    private PlayScreen screen;
    private boolean isJumping = false;
    private boolean collisionX,collisionY,flying;

    // Constructor
    public Player(float x, float y, PlayScreen screen, TextureAtlas atlas){
        // call Sprite constructor with a texureRegion param
        super(atlas.getRegions().get(0));
        jumpermanAtlas = atlas;
        setUpAnimations();

        // create our pos vector and set position to x,y
        pos = new Vector2(x,y);
        setX(pos.x);
        setY(pos.y);

        // instantiate vel and accel
        vel = new Vector2(0,0);
        accel = new Vector2(0,0);
        this.screen = screen;
    }
    public void jump(){
        isJumping=true;
        jumptimer = 0;
    }

    // updates EVERYTHING
    public void update(float delta){
        // jumping timer code
        jumptimer+=delta;
        if(jumptimer>0.25){
            isJumping = false;
        }

        // apply gravity acceleration
        accel.y = -GRAVITY;
        accel.x = 0;

        // if a is pressed we accelerate in x towards the left
        if(screen.aPressed){
            if(!flying) {
                animation = runleft;
            }
            accel.x = -ACCELERATION;
        }

        // if d is pressed we accelerate in x towards the right
        if(screen.dPressed){
            if(!flying) {
                animation = runright;
            }
            accel.x = ACCELERATION;
        }

        // apply acceleration to velocity
        vel.y += accel.y*delta;
        vel.x += accel.x*delta;

        // decay our x velocity stuff
        if(vel.x>0){
            vel.x-= 10;
        }
        else if(vel.x<0){
            vel.x+= 10;
        }

        // cap velocity in both direction
        // TODO TEST ME (maybe have to be combined velocity)
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

        // if our velocity is sufficiently low, just stop movement
        // this prevents gliding on surfaces
        final float GLIDE_PREVENTION = 4f;
        if(Math.abs(vel.y)<GLIDE_PREVENTION){
            vel.y=0;
        }
        if(Math.abs(vel.x)<GLIDE_PREVENTION){
            vel.x=0;
        }

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
                // wall jumping from a right wall
                if(vel.x>0){
                    vel.x = -500;
                    vel.y = 1000;
                    isJumping = false;
                    animation = jumpleft;
                    flying = true;
                // wall jumping from a left wall
                }else if(vel.x<0){
                    vel.x = 500;
                    vel.y = 1000;
                    isJumping = false;
                    animation = jumpright;
                    flying = true;
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
            if(collisionY){
                flying = false;
            }

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

        //TODO: REMOVE
        if (screen.leftPressed) {
            pos.x -= 10;
            setX(pos.x);
        } else if (screen.rightPressed) {
            pos.x += 10;
            setX(pos.x);
        } else if (screen.upPressed) {
            pos.y += 10;
            setY(pos.y);
        } else if (screen.downPressed) {
            pos.y -= 10;
            setY(pos.y);
        }
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

    // just sets up animations, pretty self explanatory
    private void setUpAnimations(){
        //animation,jumpleft,jumpright,runleft,runright,idle;

        // set jumpleft animation to anew animation with ANIMATION_SPEED
        // and the atlas regions defined by our findRegion.
        jumpleft = new Animation(ANIMATION_SPEED,
                jumpermanAtlas.findRegion("jumperman13")
                );

        jumpright = new Animation(ANIMATION_SPEED,
                jumpermanAtlas.findRegion("jumperman12")
                );

        runleft = new Animation(ANIMATION_SPEED,
                jumpermanAtlas.findRegion("jumperman06"),
                jumpermanAtlas.findRegion("jumperman07"),
                jumpermanAtlas.findRegion("jumperman08"),
                jumpermanAtlas.findRegion("jumperman09"),
                jumpermanAtlas.findRegion("jumperman10"),
                jumpermanAtlas.findRegion("jumperman11")
                );
        runright = new Animation(0.1f,
                jumpermanAtlas.findRegion("jumperman00"),
                jumpermanAtlas.findRegion("jumperman01"),
                jumpermanAtlas.findRegion("jumperman02"),
                jumpermanAtlas.findRegion("jumperman03"),
                jumpermanAtlas.findRegion("jumperman04"),
                jumpermanAtlas.findRegion("jumperman05")
                );
        animation = runright;
    }
    public Animation getAnimation(){
        return animation;
    }
    public float getVelX(){
        return vel.x;
    }
    public float getVelY() { return vel.y;}

    // returns a string of player's stats for testing
    public String getVitals(){
        return(
                "VelX: "+vel.x+"\n"+
                "VelY: "+vel.y+"\n"+
                "AccX: "+accel.x+"\n"+
                "AccY: "+accel.y
                );
    }
}
