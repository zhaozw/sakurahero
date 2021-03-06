package pl.veldrinlab.sakurahero.screens;

import pl.veldrinlab.sakurahero.Configuration;
import pl.veldrinlab.sakurahero.Language;
import pl.veldrinlab.sakurahero.SakuraHero;
import pl.veldrinlab.sakuraEngine.core.GameScreen;
import pl.veldrinlab.sakuraEngine.core.Renderer;
import pl.veldrinlab.sakuraEngine.core.SpriteActor;
import pl.veldrinlab.sakuraEngine.core.Timer;
import pl.veldrinlab.sakuraEngine.fx.FadeEffectParameters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Class represents LanguageSelection screen. It is used to select game language.
 * @author Szymon Jab�o�ski
 *
 */
public class LanguageSelectionScreen extends GameScreen implements GestureListener {

	private GameScreen nextScreen;
	
	private SakuraHero game;
	private GestureDetector inputDetector;
	private FadeEffectParameters fade;

	private SpriteActor background;
	private SpriteActor selection;
	private SpriteActor english;
	private SpriteActor japanese;

	private boolean fadeInState;
	private boolean selectState;
	private boolean fadeOutState;

	private float elapsedTime;
	private float blinking;

	public LanguageSelectionScreen(final SakuraHero game, final FadeEffectParameters fadeParams, final GameScreen nextScreen) {
		this.game = game;
		this.fade = fadeParams;
		this.nextScreen = nextScreen;

		background = new SpriteActor(game.resources.getTexture("menuBackground"));
		selection = new SpriteActor(game.resources.getTexture("selectLanguage"));
		english = new SpriteActor(game.resources.getTexture("english"),"English");
		japanese = new SpriteActor(game.resources.getTexture("japanese"),"Japanese");
		blinking = 1.0f;
		inputDetector = new GestureDetector(this);    
		
		initializeInterface();
	}

	@Override
	public void processInput() {		

	}

	@Override
	public void processLogic(final float deltaTime) {

		if(fadeInState) {
			elapsedTime += deltaTime;
			elapsedTime = MathUtils.clamp(elapsedTime, 0.0f, fade.fadeInTime);

			background.getSprite().setColor(1.0f, 1.0f, 1.0f, elapsedTime);
			selection.getSprite().setColor(1.0f, 1.0f, 1.0f, elapsedTime);
			english.getSprite().setColor(1.0f, 1.0f, 1.0f, elapsedTime);
			japanese.getSprite().setColor(1.0f, 1.0f, 1.0f, elapsedTime);

			if(elapsedTime > fade.fadeInTime-0.001f) {
				fadeInState = false;
				selectState = true;
			}
		}
		else if(selectState) {
			blinking += deltaTime*5.0f;

			background.getSprite().setColor(1.0f, 1.0f, 1.0f, 1.0f);
			selection.getSprite().setColor(1.0f, 1.0f, 1.0f, 1.0f);
			english.getSprite().setColor(1.0f, 1.0f, 1.0f, (float) ((Math.sin(blinking)+1.0f)/2.0f));
			japanese.getSprite().setColor(1.0f, 1.0f, 1.0f, (float) ((Math.sin(blinking)+1.0f)/2.0f));
		}
		else if(fadeOutState) {
			elapsedTime += deltaTime;
			elapsedTime = MathUtils.clamp(elapsedTime, 0.0f, fade.fadeOutTime);

			background.getSprite().setColor(1.0f, 1.0f, 1.0f, fade.fadeOutTime-elapsedTime);
			selection.getSprite().setColor(1.0f, 1.0f, 1.0f, fade.fadeOutTime-elapsedTime);

			if(Configuration.getInstance().getSelectedLanguage() == Language.ENGLISH) {
				english.getSprite().setColor(1.0f, 1.0f, 1.0f, fade.fadeOutTime-elapsedTime);
				japanese.getSprite().setColor(1.0f, 1.0f, 1.0f, 0.0f);
			}
			else {
				english.getSprite().setColor(1.0f, 1.0f, 1.0f, 0.0f);
				japanese.getSprite().setColor(1.0f, 1.0f, 1.0f, fade.fadeOutTime-elapsedTime);
			}

			if(elapsedTime > fade.fadeOutTime-0.001f)
				game.setScreen(nextScreen);
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
	public void resize(int width, int height) {
		Renderer.defaultStage.setViewport(Configuration.getWidth(), Configuration.getHeight(), false);
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
		// TODO Auto-generated method stub
	}

	@Override
	public void show() {

		fadeInState = true;
		background.getSprite().setColor(1.0f,1.0f,1.0f,0.0f);
		selection.getSprite().setColor(1.0f, 1.0f, 1.0f, 0.0f);
		english.getSprite().setColor(1.0f, 1.0f, 1.0f, 0.0f);
		japanese.getSprite().setColor(1.0f, 1.0f, 1.0f, 0.0f);

		Renderer.defaultStage.addActor(background);
		Renderer.defaultStage.addActor(selection);
		Renderer.defaultStage.addActor(english);
		Renderer.defaultStage.addActor(japanese);

		Gdx.input.setInputProcessor(inputDetector);
	}

	@Override
	public void dispose() {

	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		Vector2 stageCoords = Vector2.Zero;
		Renderer.defaultStage.screenToStageCoordinates(stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));
		Actor actor = Renderer.defaultStage.hit(stageCoords.x, stageCoords.y, true);

		if(actor == null)
			return false;

		if(selectState && (actor.getName().equals("English") || actor.getName().equals("Japanese"))) {
			selectState = false;
			fadeOutState = true;
			elapsedTime = 0.0f;

			if(actor.getName().equals("English"))
				Configuration.getInstance().setLanguage(Language.ENGLISH);
			else
				Configuration.getInstance().setLanguage(Language.JAPANESE);
		}

		return true;
	}

	@Override
	public boolean longPress(float x, float y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void initializeInterface()	{
		selection.getSprite().setX((Configuration.getWidth()-selection.getSprite().getWidth())*0.5f);	
		selection.getSprite().setY(Configuration.getHeight()*0.90f - selection.getSprite().getHeight());

		english.getSprite().setX((Configuration.getWidth()-english.getSprite().getWidth())*0.20f);	
		english.getSprite().setY(Configuration.getHeight()*0.40f - english.getSprite().getHeight());

		japanese.getSprite().setX((Configuration.getWidth()-japanese.getSprite().getWidth())*0.80f);	
		japanese.getSprite().setY(Configuration.getHeight()*0.40f - japanese.getSprite().getHeight());

		english.setBounds(english.getSprite().getX(), english.getSprite().getY(), english.getSprite().getWidth(), english.getSprite().getHeight());
		japanese.setBounds(japanese.getSprite().getX(), japanese.getSprite().getY(), japanese.getSprite().getWidth(), japanese.getSprite().getHeight());
	}
}
