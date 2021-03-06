package com.voltahackathon001.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.voltahackathon001.game.screens.PlayScreen;

// TODO Evaluate feasibility of each : Could use water-fill algorithm starting from spawn point
// TODO Name the Game
// TODO OPTIONAL
//      Menu Screen
public class CaveGame extends Game {
	public SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		this.setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		this.getScreen().dispose();
	}

	public void restart(){
		this.dispose();
		this.setScreen(new PlayScreen(this));
	}

}

