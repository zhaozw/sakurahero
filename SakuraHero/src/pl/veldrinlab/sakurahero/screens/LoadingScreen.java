package pl.veldrinlab.sakurahero.screens;

import pl.veldrinlab.sakurahero.Configuration;
import pl.veldrinlab.sakurahero.SakuraHero;
import pl.veldrinlab.sakuraEngine.core.GameScreen;
import pl.veldrinlab.sakuraEngine.core.Renderer;
import pl.veldrinlab.sakuraEngine.core.SpriteActor;
import pl.veldrinlab.sakuraEngine.core.Timer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class LoadingScreen extends GameScreen implements GestureListener {

	public MenuScreen menuScreen;

	private SakuraHero game;
	private GestureDetector inputDetector; // po chuj to?

	private SpriteActor background;

	private SpriteActor logoAng;
	private SpriteActor katana;
	private SpriteActor shine;

	//TODO maybe change to only one later - when we will have TextureAtlas for all GUI elements. One SpriteActor - status
	//TODO SpriteActor is very bad name. We had it in Cows and Expendables but it is really but I think.
	private SpriteActor loading;
	private SpriteActor tapToContinue;


	private boolean fadeState;
	private boolean loadingState;
	private boolean katanaState;
	private boolean shineState;
	private boolean readyToGo;

	private float fadeInAlpha;
	private float blinking;



	// katana animation

	private Vector2 direction;
	private float timeDistance;
	private float velocity;

	// shine animation
	private float shineTime;



	// blinking - tap message animation

	public LoadingScreen(final SakuraHero game) {
		this.game = game;

		background = new SpriteActor(game.resources.getTexture("menuBackground"));
		loading = new SpriteActor(game.resources.getTexture("loadingAng"));
		tapToContinue = new SpriteActor(game.resources.getTexture("tapToAng"));

		//
		logoAng = new SpriteActor(game.resources.getTexture("logoAng"));
		katana = new SpriteActor(game.resources.getTexture("katana"));
		shine = new SpriteActor(game.resources.getTexture("shine"));

		shine.getSprite().setColor(1.0f, 1.0f, 1.0f, 0.0f);
		shine.getSprite().setSize(Configuration.getInstance().descriptor.width, Configuration.getInstance().descriptor.height);

		// katana
		direction = new Vector2(-1.0f,-1.0f);
		timeDistance = 0.75f;
		velocity = 750.0f;

		//shine
		shineTime = 1.0f;

		inputDetector = new GestureDetector(this);
		//		
		initializeInterface();
	}

	@Override
	public void processInput() {

	}

	@Override
	public void processLogic(final float deltaTime) {

		/*
		 * STATE data flow
		 * 
		 * Fade in with loading
		 * Loading 
		 * Finish loading - change to tap to start
		 * Perform katana animation
		 * Start blinking and up screen ready to finish flag
		 * 
		 */


		if(fadeState) {
			fadeInAlpha += deltaTime*1.0f;
			fadeInAlpha = MathUtils.clamp(fadeInAlpha, 0.0f, 1.0f);

			background.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);
			loading.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);
			logoAng.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);

			if(fadeInAlpha > 0.99f) {
				fadeState = false;
				loadingState = true;
			}
		}
		else if(loadingState) {
			//load

			//if loading completed
			loading.getSprite().setColor(1.0f,1.0f,1.0f,0.0f);
			loadingState = false;
			katanaState = true;
		}
		else if(katanaState) {
			timeDistance -= deltaTime;

			float x = katana.getSprite().getX();
			float y = katana.getSprite().getY();

			x += direction.x*velocity*deltaTime;
			y += direction.y*velocity*deltaTime;

			katana.getSprite().setPosition(x, y);
		
			if(timeDistance < 0.0f) {
				katanaState = false;
				shineState = true;	
			}

		}
		else if(shineState) {
			//TODO sword slash sound

			shine.getSprite().setColor(1.0f, 1.0f, 1.0f, shineTime);
			
			shineTime -= deltaTime;
//			shineTime = MathUtils.clamp(shineTime, 0.0f, 1.0f);

			if(shineTime < 0.0f) {
				shineState = false;
				readyToGo = true;				
			}
			
			

		}
		else if(readyToGo) {
			Gdx.app.log("test", "test");
			blinking += deltaTime*5.0f;
			tapToContinue.getSprite().setColor(1.0f, 1.0f, 1.0f, (float) ((Math.sin(blinking)+1.0f)/2.0f));
		}			
	}

	@Override
	public void processRendering() {	
		Renderer.clearScreen();
		Renderer.defaultStage.draw();
	}

	@Override
	public void render(final float deltaTime) {
		processInput();
		game.getTimer().updateTimer(deltaTime);
		while(game.getTimer().checkTimerAccumulator()) {
			processLogic(Timer.TIME_STEP);
			game.getTimer().eatAccumulatorTime();
		}
		processRendering();		
	}

	@Override
	public void resize(final int width, final int height) {
		//TODO to jest chyba i tak zb�dne??/
		Renderer.defaultStage.setViewport(Configuration.getInstance().descriptor.width, Configuration.getInstance().descriptor.height, false);	
	}

	@Override
	public void hide() {
		Renderer.defaultStage.clear();
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		//	 TODO Auto-generated method stub
	}

	@Override
	public void show() {	

		//		Renderer.enterOrthoMode();
		//		if(Configuration.getInstance().musicOn) {
		//			menuMusic.play();
		//			menuMusic.setVolume(0.1f);
		//		}
		//
		
		Renderer.defaultStage.clear();		
		Renderer.defaultStage.addActor(background);
		Renderer.defaultStage.addActor(katana);
		Renderer.defaultStage.addActor(logoAng);
		Renderer.defaultStage.addActor(loading);
		Renderer.defaultStage.addActor(tapToContinue);
		Renderer.defaultStage.addActor(shine);

		fadeState = true;

		background.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);
		logoAng.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);
		loading.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);
		tapToContinue.getSprite().setColor(1.0f,1.0f,1.0f,fadeInAlpha);
		Gdx.input.setInputProcessor(inputDetector);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean pinch(Vector2 arg0, Vector2 arg1, Vector2 arg2, Vector2 arg3) {
		return false;
	}

	@Override
	public boolean zoom(float arg0, float arg1) {
		return false;
	}

	private void initializeInterface() {

		logoAng.getSprite().setX((Configuration.getInstance().descriptor.width-logoAng.getSprite().getWidth())*0.5f);	
		logoAng.getSprite().setY(Configuration.getInstance().descriptor.height*0.80f - logoAng.getSprite().getHeight());

		katana.getSprite().setX((Configuration.getInstance().descriptor.width-katana.getSprite().getWidth())*0.5f);	
		katana.getSprite().setY(Configuration.getInstance().descriptor.height*1.00f - katana.getSprite().getHeight());

		//TODO smarter
		katana.getSprite().setPosition(325.0f, 480.0f-15.0f-katana.getSprite().getHeight());

		loading.getSprite().setX((Configuration.getInstance().descriptor.width-loading.getSprite().getWidth())*0.5f);
		loading.getSprite().setY(Configuration.getInstance().descriptor.height*0.25f -loading.getSprite().getHeight());

		tapToContinue.getSprite().setX((Configuration.getInstance().descriptor.width-tapToContinue.getSprite().getWidth())*0.5f);
		tapToContinue.getSprite().setY(Configuration.getInstance().descriptor.height*0.25f -tapToContinue.getSprite().getHeight());

		//ruch katany

		float x = katana.getSprite().getX();
		float y = katana.getSprite().getY();

		x += -direction.x*velocity*timeDistance;
		y += -direction.y*velocity*timeDistance;

		katana.getSprite().setPosition(x, y);
	}

	@Override
	public boolean fling(float arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean longPress(float arg0, float arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float arg0, float arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float arg0, float arg1, int arg2, int arg3) {	
		if(readyToGo)
			game.setScreen(menuScreen);

		return false;
	}

	@Override
	public boolean touchDown(float arg0, float arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}