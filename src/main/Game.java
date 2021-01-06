package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.UUID;
import java.util.Vector;

import main.unit.Asteroid;
import main.unit.Explosion;
import main.unit.Player;
import main.unit.Ship;
import main.unit.Shot;
import main.unit.UFO;
import main.unit.entity.util.GeomPath;
import main.unit.entity.util.MathUtil;
import main.unit.entity.util.Polygon2;
import main.unit.entity.util.QuadTree2D;
import main.unit.entity.util.Rect2D;

/**
 * The Game class implements the game logic
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class Game extends HeadsUpDisplay implements AppStatable, Ship.Listener, Shot.Listener, UFO.Listener, UIGame.Listener
{
	private final float PLAYER_SHIP_SIZE_FACTOR = 0.02291f; // by screen height.
	private final float PLAYER_SHIP_ROTATION_INCREMENT = PI / 75f;
	private final float PLAYER_SHIP_ACCELERATION_RATE = 1.5f;
	private final float PLAYER_MAX_SPEED_FACTOR = 0.0053f; // by screen width.
	private final float PLAYER_SHOT_SPEED_FACTOR = 0.005f; // by screen width.
	private final int MAX_ACTIVE_PLAYER_SHOTS = 10;
	private final float SHOT_RANGE_FACTOR = 0.595f; // *** by screen height. ***
	private final float PLAYER_MASS = 20f;
	
	private final float SHOT_HALF_SIZE_FACTOR = 0.00119f; // by screen width.
	private final float SHOT_QUERY_RADIUS_FACTOR = 0.09523f; // by screen height.
	
	private final int MAXIMUM_ASTEROIDS = 12;
	private final float ASTEROID_COLLISION_FIELD_SIZE_FACTOR = 0.1428f; // by screen height;
	
	private final int EXPLOSION_STEPS = 10;
	private final int EXPLOSION_RATE = 2;
	
	private final float SAFE_RESPAWN_RADIUS_FACTOR = 0.1904f; // by screen height.
	
	private final float UFO_START_Y_OFFSET_FACTOR = 0.125f; // by screen height;
	private final float LARGE_UFO_SHOT_SPEED_FACTOR = 0.0045f; // by screen width;
	private final float SMALL_UFO_SHOT_SPEED_FACTOR = 0.006f; // by screen width;
	private final long MINIMUM_UFO_DELAY = 15000;
	private final long MAXIMUM_UFO_DELAY = 45000;
	
	private final float HYPERSPACE_BOUNDARY_FACTOR = 0.08928f; // by screen width;
	private final float CHANCE_OF_HYPERSPACE_SELF_DESTRUCTION = 0.2f; // out of 1.0
	
	private final long START_NEW_LEVEL_DELAY = 100;
	private final long DOUBLES_INTRO_STATE_DELAY = 2000;
	private final long LEVEL_COMPLETE_STATE_DELAY = 3000;
	
	private Game.Listener mListener = null;
	private UIGame mUI = null;
	private QuadTree2D mQuadTree = null;
	private SFXManager mSFXManager = null;
	private Music mMusic = null;
	private Player mPlayer = null;
	private Ship mShip = null;
	private float mShotRange = 0f;
	private float mPlayerShotSpeed = 0f;
	private int mShotHalfSize = 0;
	private int mCurrentStartAsteroids = MINIMUM_ASTEROIDS;
	private Vector<Shot> mPlayerShots = new Vector<Shot>();
	private Vector<Explosion> mExplosions = new Vector<Explosion>();
	private int mShotQueryRadius = 0;
	private int mAsteroidQueryRadius = 0;
	private int mSafeRadius = 0;
	private UFO mUFO = null;
	private Vector<Shot> mUFOShots = new Vector<Shot>();
	private float mLargeUFOShotSpeed = 0f;
	private float mSmallUFOShotSpeed = 0f;
	private float mBonus = 0;
	private int mHyperspaceBoundary = 0;
	
	private StartNewLevel mStartNewLevel = new StartNewLevel();
	private DoublesGameIntroState mDoublesGameIntroState = new DoublesGameIntroState();
	private NormalPlayState mNormalPlayState = new NormalPlayState();
	private UFONormalPlayState mUFONormalPlayState = new UFONormalPlayState();
	private PlayerJustDiedState mPlayerJustDiedState = new PlayerJustDiedState();
	private PlayerRespawnState mPlayerRespawnState = new PlayerRespawnState();
	private LevelComplete mLevelComplete = new LevelComplete();
	private PreGameOver mPreGameOver = new PreGameOver();
	private GameOver mGameOver = new GameOver();
	private LevelState mLevelControl = null;
	
	public Game(Player player, int x, int y, int width, int height, ScreenManager.Listener smListener, HeadsUpDisplay.Listener hudListener, Game.Listener listener, SFXManager sfx, int bonus)
	{
		super(x, y, width, height, smListener, hudListener);
		this.mPlayer = player;
		this.mListener = listener;
		this.mSFXManager = sfx;
		this.mBonus = bonus;
		this.initialize();
		return;
	}
	
	@Override
	protected void initialize()
	{
		super.initialize();
		this.mUI = new UIGame(this);
		// Expanded boundary helps wrap-around issues with bounding boxes.
		this.mQuadTree = new QuadTree2D(-this.mShotQueryRadius, 
										-this.mShotQueryRadius, 
										this.mScreenBounds.width + this.mShotQueryRadius, 
										this.mScreenBounds.height + this.mShotQueryRadius, 
										1);
		this.mMusic = new Music();
		
		this.initializeShip();
		this.mPlayerShotSpeed = this.mScreenBounds.width * PLAYER_SHOT_SPEED_FACTOR;
		this.mShotRange = this.mScreenBounds.height * SHOT_RANGE_FACTOR;
		this.mShotHalfSize = (int)Math.ceil(this.mScreenBounds.width * SHOT_HALF_SIZE_FACTOR);
		this.mShotQueryRadius = (int)Math.round(this.mScreenBounds.height * SHOT_QUERY_RADIUS_FACTOR);
		this.mLargeUFOShotSpeed = this.mScreenBounds.width * LARGE_UFO_SHOT_SPEED_FACTOR;
		this.mSmallUFOShotSpeed = this.mScreenBounds.width * SMALL_UFO_SHOT_SPEED_FACTOR;
		this.mHyperspaceBoundary = (int)Math.round(this.mScreenBounds.width * HYPERSPACE_BOUNDARY_FACTOR);
		this.mAsteroidQueryRadius = (int)Math.round(this.mScreenBounds.height * ASTEROID_COLLISION_FIELD_SIZE_FACTOR);
		this.mSafeRadius = (int)Math.round(this.mScreenBounds.height * SAFE_RESPAWN_RADIUS_FACTOR);
		
		this.mLabels.get(this.mPlayer.getPlayerNum()).get(HeadsUpDisplay.LabelType.SCORE).setText(String.valueOf(this.mPlayer.getScore()));
		this.mLabels.get(this.mPlayer.getPlayerNum()).get(HeadsUpDisplay.LabelType.LIVES).setText(this.getLivesLeftCharacters(this.mPlayer.getLives() - 1));
		
		this.mStartNewLevel.reset();
		this.mLevelControl = this.mStartNewLevel;
		return;
	}
	
	public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
	{
		g2d.setPaint(this.mBackgroundColor);
		g2d.fillRect(this.mScreenBounds.x, this.mScreenBounds.y, this.mScreenBounds.width, this.mScreenBounds.height);
		this.mLevelControl.render(g2d, original, base);
		return;
	}
	
	private void renderShip(Graphics2D g2d, AffineTransform original)
	{
		g2d.setPaint(Color.WHITE);
		g2d.drawPolygon(this.mShip.getShape());
		this.renderWraps(g2d, original, this.mShip.getShape());
		return;
	}
	
	private void renderPlayerShots(Graphics2D g2d)
	{
		for(int i = 0; i < this.mPlayerShots.size(); i++)
		{
			g2d.fillOval((int)Math.round(this.mPlayerShots.get(i).x - this.mShotHalfSize), 
						(int)Math.round(this.mPlayerShots.get(i).y - this.mShotHalfSize), 
						this.mShotHalfSize * 2, 
						this.mShotHalfSize * 2);
		}
	}
	
	private void renderUFOShots(Graphics2D g2d)
	{
		for(int i = 0; i < this.mUFOShots.size(); i++)
		{
			g2d.fillOval((int)Math.round(this.mUFOShots.get(i).x - this.mShotHalfSize), 
						(int)Math.round(this.mUFOShots.get(i).y - this.mShotHalfSize), 
						this.mShotHalfSize * 2, 
						this.mShotHalfSize * 2);
		}
		return;
	}
	
	private void renderExplosions(Graphics2D g2d)
	{
		for(int i = 0; i < this.mExplosions.size(); i++)
		{
			for(int j = 0; j < this.mExplosions.get(i).npoints; j++)
			{
				g2d.fillOval(this.mExplosions.get(i).xpoints[j] - 1, this.mExplosions.get(i).ypoints[j] - 1, 2, 2);
			}
		}
		return;
	}
	
	private void renderUFO(Graphics2D g2d)
	{
		if(this.mUFO == null){
			return;
		}
		if(this.mUFO.isDisposing()){
			return;
		}
		
		g2d.setPaint(Color.WHITE);
		g2d.drawPolyline(this.mUFO.getShape().xpoints, this.mUFO.getShape().ypoints, this.mUFO.getShape().npoints);
		
		//Testing: show true location.
		//g2d.fillOval((int)Math.round(this.mUFO.x - 2), (int)Math.round(this.mUFO.y - 2), 4, 4);
		
		//Testing : show bounding box.
		//g2d.setPaint(Color.BLUE);
		//g2d.drawPolygon(this.mUFO.getBounds());
		return;
	}
	
	public void update()
	{
		this.mLevelControl.update();
		return;
	}
	
	private void initializeShip()
	{
		this.mShip = new Ship(UUID.randomUUID(), this);
		this.mShip.x = this.mScreenBounds.width / 2f;
		this.mShip.y = this.mScreenBounds.height / 2f;
		Polygon2 poly = EntityData.getPlayerShape();
		float width = (float)poly.getBounds2D().getWidth();
		float height = (float)poly.getBounds2D().getHeight();
		float factor = (this.mScreenBounds.height * PLAYER_SHIP_SIZE_FACTOR) / width;
		poly.resizePolygon((int)Math.round(width * factor), (int)Math.round(height * factor));
		this.mShip.width = width;
		this.mShip.height = height;
		this.mShip.invalidate();
		this.mShip.setShape(poly);
		this.mShip.setMaxSpeed(this.mScreenBounds.width * PLAYER_MAX_SPEED_FACTOR);
		this.mShip.setMass(PLAYER_MASS);
		this.mShip.setAccelerationRate(PLAYER_SHIP_ACCELERATION_RATE);
		this.mShip.setRotationIncrement(PLAYER_SHIP_ROTATION_INCREMENT);
		this.mShip.setRotation(0f);
		return;
	}
	
	private void createChildAsteroids(Asteroid.Size parentSize, float x, float y)
	{
		if(parentSize == Asteroid.Size.SMALL){
			return;
		}
		Asteroid.Size childSize = this.getChildSize(parentSize);
		
		float[] directions = new float[2];
		directions[0] = MathUtil.getRandomDirection();
		directions[1] = MathUtil.getRandomDirection(directions[0]);
		for(int i = 0; i < 2; i++)
		{
			Asteroid a = new Asteroid(UUID.randomUUID(), childSize);
			a.x = x;
			a.y = y;
			a.setDirection(directions[i]);
			a.setSpeed(((float)Math.random() * (this.mMaxAsteroidSpeed - this.mMinAsteroidSpeed)) + this.mMinAsteroidSpeed);
			int choice = (int)Math.floor(Math.random() * 4f);
			Polygon2 poly = EntityData.getShape(choice);
			Dimension d = this.getAsteroidDimension(childSize, poly, new Dimension());
			poly.resizePolygon(d.width, d.height);
			a.width = d.width;
			a.height = d.height;
			a.invalidate();
			poly.setLocation((int)Math.round(a.x), (int)Math.round(a.y));
			a.setShape(poly);
			this.mAsteroids.add(a);
		}
		return;
	}
	
	private Asteroid.Size getChildSize(Asteroid.Size parentSize)
	{
		if(parentSize == Asteroid.Size.LARGE){
			return Asteroid.Size.MEDIUM;
		}else if(parentSize == Asteroid.Size.MEDIUM){
			return Asteroid.Size.SMALL;
		}else{
			return null;
		}
	}
	
	private void createUFO()
	{
		int x = 0;
		int y = 0;
		if(Math.random() < 0.5){
			this.mUFO = new UFO(UUID.randomUUID(), this, UFO.Size.SMALL);
		}else{
			this.mUFO = new UFO(UUID.randomUUID(), this, UFO.Size.LARGE);
		}
		if(Math.random() < 0.5){
			x = 0;
			this.mUFO.setDirection(0f);
		}else{
			x = this.mScreenBounds.width;
			this.mUFO.setDirection(PI);
		}
		if(Math.random() < 0.5){
			y = (int)Math.round(this.mScreenBounds.height * UFO_START_Y_OFFSET_FACTOR);
		}else{
			y = (int)Math.round(this.mScreenBounds.height - (this.mScreenBounds.height * UFO_START_Y_OFFSET_FACTOR));
		}
		this.mUFO.x = x;
		this.mUFO.y = y;
		this.mUFO.initializeDestination();
		this.mUFO.setSpeed(((float)Math.random() * (this.mMaxAsteroidSpeed - this.mMinAsteroidSpeed)) + this.mMinAsteroidSpeed);
		GeomPath path = EntityData.getUFOShape();
		float width = path.getRight() - path.getLeft();
		float height = path.getBottom() - path.getTop();
		Dimension size = this.getUFODimension(this.mUFO, width, height, new Dimension());
		path.resize(size.width, size.height);
		this.mUFO.width = size.width;
		this.mUFO.height = size.height;
		this.mUFO.invalidate();
		path.setLocation(x, y);
		this.mUFO.setShape(path);
		Polygon2 poly = EntityData.getUFOBounds();
		poly.resizePolygon(size.width, size.height);
		poly.setLocation(x, y);
		this.mUFO.setBounds(poly);
		return;
	}
	
	private Dimension getUFODimension(UFO u, float width, float height, Dimension result)
	{
		float factor = 0f;
		if(u.getSize() == UFO.Size.LARGE){
			factor = (this.mScreenBounds.height * EntityData.UFO_LARGE_HEIGHT_FACTOR) / width;
		}else if(u.getSize() == UFO.Size.SMALL){
			factor = (this.mScreenBounds.height * EntityData.UFO_SMALL_HEIGHT_FACTOR) / width;
		}
		result.width = (int)Math.round(width * factor);
		result.height = (int)Math.round(height * factor);
		return result;
	}
	
	private void updateShip()
	{
		if(this.mUI.leftIsPressed()){
			this.mShip.rotateLeft();
		}
		if(this.mUI.rightIsPressed()){
			this.mShip.rotateRight();
		}
		if(this.mUI.thrustIsPressed()){
			this.mShip.applyThrust(this.mShip.getMaxSpeed());
			if(!this.mSFXManager.engineSoundIsPlaying()){
				this.mSFXManager.startEngineSound();
			}
		}else{
			if(this.mSFXManager.engineSoundIsPlaying()){
				this.mSFXManager.stopEngineSound();
			}
		}
		this.mShip.update();
		return;
	}
	
	private void updatePlayerShots()
	{
		for(int i = this.mPlayerShots.size() - 1; i >= 0; i--)
		{
			if(this.mPlayerShots.get(i).isDisposing()){
				this.mPlayerShots.remove(i);
				continue;
			}
			this.mPlayerShots.get(i).update();
			Vector<Rect2D> rects = this.mQuadTree.query((int)Math.round(this.mPlayerShots.get(i).x - this.mShotQueryRadius),
														(int)Math.round(this.mPlayerShots.get(i).y - this.mShotQueryRadius),
														this.mShotQueryRadius * 2,
														this.mShotQueryRadius * 2);
			for(int j = 0; j < rects.size(); j++)
			{
				if(rects.get(j) instanceof Shot){
					continue;
				}
				if(rects.get(j) == this.mShip){
					continue;
				}
				if(rects.get(j) instanceof Asteroid){
					this.destroyAsteroid((Asteroid)rects.get(j), this.mPlayerShots.get(i));
				}else if(rects.get(j) instanceof UFO){
					this.destroyUFO((UFO)rects.get(j), this.mPlayerShots.get(i));
				}
			}
		}
	}
	
	private void updateUFOShots()
	{
		for(int i = this.mUFOShots.size() - 1; i >= 0; i--)
		{
			if(this.mUFOShots.get(i).isDisposing()){
				this.mUFOShots.remove(i);
				continue;
			}
			this.mUFOShots.get(i).update();
			Vector<Rect2D> rects = this.mQuadTree.query((int)Math.round(this.mUFOShots.get(i).x - this.mShotQueryRadius),
														(int)Math.round(this.mUFOShots.get(i).y - this.mShotQueryRadius),
														this.mShotQueryRadius * 2,
														this.mShotQueryRadius * 2);
			for(int j = 0; j < rects.size(); j++)
			{
				if(rects.get(j) instanceof Shot){
					continue;
				}
				if(rects.get(j) == this.mUFO){
					continue;
				}
				if(rects.get(j) instanceof Asteroid){
					this.destroyAsteroid((Asteroid)rects.get(j), this.mUFOShots.get(i));
				}else if(rects.get(j) instanceof Ship){
					this.destroyShip(this.mUFOShots.get(i));
				}
			}
		}
		return;
	}
	
	private void updateExplosions()
	{
		for(int i = this.mExplosions.size() - 1; i >= 0; i--)
		{
			if(this.mExplosions.get(i).isDisposing()){
				this.mExplosions.remove(i);
				continue;
			}
			this.mExplosions.get(i).update();
		}
		return;
	}
	
	private void updateUFO()
	{
		if(this.mUFO == null){
			return;
		}
		if(this.mUFO.isDisposing()){
			return;
		}
		if(this.mUFO.x < 0 || this.mUFO.x > this.mScreenBounds.width){
			this.mUFO.setDisposing(true);
			this.stopUFOSound();
			return;
		}
		this.mUFO.update();
		this.mUFO.getShape().setLocation((int)Math.round(this.mUFO.x), (int)Math.round(this.mUFO.y));
		this.mUFO.getBounds().setLocation((int)Math.round(this.mUFO.x), (int)Math.round(this.mUFO.y));
		return;
	}
	
	private boolean checkBonus(int currentScore, int newScore)
	{
		float x = (float)Math.floor(currentScore / this.mBonus);
		if(newScore >= (x + 1) * this.mBonus){
			return true;
		}
		return false;
	}
	
	private void destroyAsteroid(Asteroid a, Shot shot)
	{
		if(a.isDisposing()){
			return;
		}
		if(!a.getShape().contains(shot.x, shot.y) && 
			!a.getShape().contains(shot.getLastX(), shot.getLastY()) &&
			!a.getShape().finiteLineIntersects(shot.x, shot.y, shot.getLastX(), shot.getLastY())){
			return;
		}
		a.setDisposing(true);
		shot.setDisposing(true);
		if(this.checkBonus(this.mPlayer.getScore(), this.mPlayer.getScore() + a.getSize().getScore())){
			this.mPlayer.addLife();
		}
		this.mPlayer.addScore(a.getSize().getScore());
		this.createChildAsteroids(a.getSize(), a.x, a.y);
		this.createExplosion(a.x, a.y);
		return;
	}
	
	private void destroyAsteroid(Asteroid a)
	{
		if(a.isDisposing()){
			return;
		}
		a.setDisposing(true);
		this.createChildAsteroids(a.getSize(), a.x, a.y);
		this.createExplosion(a.x, a.y);
		return;
	}
		
	private void checkAsteroidCollisions()
	{
		for(int i = 0; i < this.mAsteroids.size(); i++)
		{
			if(this.mAsteroids.get(i).isDisposing()){
				this.mAsteroids.remove(i);
				continue;
			}
			Vector<Rect2D> rects = this.mQuadTree.query((int)Math.round(this.mAsteroids.get(i).x - this.mAsteroidQueryRadius), 
														(int)Math.round(this.mAsteroids.get(i).y - this.mAsteroidQueryRadius), 
														this.mAsteroidQueryRadius * 2, 
														this.mAsteroidQueryRadius * 2);
			for(int j = 0; j < rects.size(); j++)
			{
				if(rects.get(j) instanceof Asteroid){
					continue;
				}
				if(rects.get(j) == this.mShip){
					this.checkShipCollision(this.mAsteroids.get(i));
				}
				if(rects.get(j) instanceof UFO){
					this.checkUFOCollision(this.mAsteroids.get(i));
				}
			}
		}
		
	}
	
	private void checkShipCollision(Asteroid a)
	{
		if(mShip.isDisposing()){
			return;
		}
		for(int i = 0; i < this.mShip.getShape().npoints; i++)
		{
			if(i == this.mShip.getShape().npoints - 1){
				if(a.getShape().finiteLineIntersects(this.mShip.getShape().xpoints[i], this.mShip.getShape().ypoints[i], this.mShip.getShape().xpoints[0], this.mShip.getShape().ypoints[0])){
					this.destroyShip();
					this.destroyAsteroid(a);
					return;
				}
			}else{
				if(a.getShape().finiteLineIntersects(this.mShip.getShape().xpoints[i], this.mShip.getShape().ypoints[i], this.mShip.getShape().xpoints[i + 1], this.mShip.getShape().ypoints[i + 1])){
					this.destroyShip();
					this.destroyAsteroid(a);
					return;
				}
			}
		}
		return;
	}
	
	private void checkUFOCollision(Asteroid a)
	{
		if(this.mUFO == null || this.mUFO.isDisposing()){
			return;
		}
		for(int i = 0; i < this.mUFO.getBounds().npoints; i++)
		{
			if(i == this.mUFO.getBounds().npoints - 1){
				if(a.getShape().finiteLineIntersects(this.mUFO.getBounds().xpoints[i], this.mUFO.getBounds().ypoints[i], this.mUFO.getBounds().xpoints[0], this.mUFO.getBounds().ypoints[0])){
					this.destroyUFO();
					this.destroyAsteroid(a);
					return;
				}
			}else{
				if(a.getShape().finiteLineIntersects(this.mUFO.getBounds().xpoints[i], this.mUFO.getBounds().ypoints[i], this.mUFO.getBounds().xpoints[i + 1], this.mUFO.getBounds().ypoints[i + 1])){
					this.destroyUFO();
					this.destroyAsteroid(a);
					return;
				}
			}
		}
		return;
	}
	
	private void checkShipUFOCollision()
	{
		if(this.mUFO == null || this.mUFO.isDisposing()){
			return;
		}
		if(this.mShip.isDisposing()){
			return;
		}
		for(int i = 0; i < this.mShip.getShape().npoints; i++)
		{
			if(i == this.mShip.getShape().npoints - 1){
				if(this.mUFO.getBounds().finiteLineIntersects(this.mShip.getShape().xpoints[i], this.mShip.getShape().ypoints[i], this.mShip.getShape().xpoints[0], this.mShip.getShape().ypoints[0])){
					this.destroyShip();
					this.destroyUFO();
					return;
				}
			}else{
				if(this.mUFO.getBounds().finiteLineIntersects(this.mShip.getShape().xpoints[i], this.mShip.getShape().ypoints[i], this.mShip.getShape().xpoints[i + 1], this.mShip.getShape().ypoints[i + 1])){
					this.destroyShip();
					this.destroyUFO();
					return;
				}
			}
		}
		return;
	}
	
	private void destroyShip(Shot s)
	{
		if(s.isDisposing()){
			return;
		}
		if(this.mShip.isDisposing()){
			return;
		}
		if(!this.mShip.getShape().contains(s.x, s.y) && 
			!this.mShip.getShape().contains(s.getLastX(), s.getLastY()) &&
			!this.mShip.getShape().finiteLineIntersects(s.x, s.y, s.getLastX(), s.getLastY())){
			return;
		}
		s.setDisposing(true);
		this.mShip.setDisposing(true);
		this.mSFXManager.stopEngineSound();
		this.createExplosion(this.mShip.x, this.mShip.y);
		return;
	}
	
	private void destroyShip()
	{
		this.mShip.setDisposing(true);
		this.mSFXManager.stopEngineSound();
		this.createExplosion(this.mShip.x, this.mShip.y);
		return;
	}
	
	private void destroyUFO(UFO u, Shot s)
	{
		if(u.isDisposing()){
			return;
		}
		if(s.isDisposing()){
			return;
		}
		if(!u.getBounds().contains(s.x, s.y) && 
			!u.getBounds().contains(s.getLastX(), s.getLastY()) &&
			!u.getBounds().finiteLineIntersects(s.x, s.y, s.getLastX(), s.getLastY())){
			return;
		}
		u.setDisposing(true);
		this.stopUFOSound();
		s.setDisposing(true);
		if(this.checkBonus(this.mPlayer.getScore(), this.mPlayer.getScore() + u.getSize().getScore())){
			this.mPlayer.addLife();
		}
		this.mPlayer.addScore(u.getSize().getScore());
		this.createExplosion(u.x, u.y);
		return;
	}
	
	private void destroyUFO()
	{
		this.mUFO.setDisposing(true);
		this.stopUFOSound();
		this.createExplosion(this.mUFO.x, this.mUFO.y);
		return;
	}
	
	private void stopUFOSound()
	{
		if(this.mUFO.getSize() == UFO.Size.SMALL){
			this.mSFXManager.stopSmallUFO();
		}else if(this.mUFO.getSize() == UFO.Size.LARGE){
			this.mSFXManager.stopLargeUFO();
		}
		return;
	}
	
	private void createExplosion(float x, float y)
	{
		Polygon2 poly = EntityData.getExplosion1();
		Explosion e = new Explosion(poly.xpoints, poly.ypoints, poly.npoints, EXPLOSION_STEPS, EXPLOSION_RATE);
		e.setLocation((int)Math.round(x), (int)Math.round(y));
		this.mExplosions.add(e);
		this.mSFXManager.playExplosionClip();
		return;
	}
	
	private void updateMusic()
	{
		if(this.mAsteroids.size() > this.mCurrentStartAsteroids * 2){
			this.mMusic.playFastRate();
		}else{
			this.mMusic.playSlowRate();
		}
		this.mMusic.update();
		return;
	}
	
	private boolean safeToRespawn()
	{
		if(this.mUFO != null && !this.mUFO.isDisposing()){
			return false;
		}
		
		Vector<Rect2D> rects = this.mQuadTree.query((int)Math.round((this.mScreenBounds.width / 2f) - this.mSafeRadius), 
													(int)Math.round((this.mScreenBounds.height / 2f) - this.mSafeRadius), 
													this.mSafeRadius * 2, 
													this.mSafeRadius * 2);
		if(rects.isEmpty()){
			return true;
		}
		return false;
	}
	
	private void refillQuadTree()
	{
		this.mQuadTree.clear();
		if(!this.mShip.isDisposing()){
			this.mQuadTree.insert(this.mShip);
		}
		if(this.mUFO != null && !this.mUFO.isDisposing()){
			this.mQuadTree.insert(this.mUFO);
		}
		for(int i = 0; i < this.mAsteroids.size(); i++)
		{
			if(this.mAsteroids.get(i).isDisposing()){
				continue;
			}
			this.mQuadTree.insert(this.mAsteroids.get(i));
		}
		for(int i = 0; i < this.mPlayerShots.size(); i++)
		{
			if(this.mPlayerShots.get(i).isDisposing()){
				continue;
			}
			this.mQuadTree.insert(this.mPlayerShots.get(i));
		}
		for(int i = 0; i < this.mUFOShots.size(); i++)
		{
			if(this.mUFOShots.get(i).isDisposing()){
				continue;
			}
			this.mQuadTree.insert(this.mUFOShots.get(i));
		}
		return;
	}
	
	public UI getUI()
	{
		return this.mUI;
	}
	
	public void setFPS(int fps)
	{
		this.mFPS = fps;
		return;
	}
	
	public void enterPressed()
	{
		return;
	}
	
	public void escapePressed()
	{
		this.mListener.switchAppStates(AppState.MAIN_MENU);
		return;
	}
	
	/**
	 * In the original game, the large UFOs fired in random directions, but the small UFOs aimed directly at the player.
	 */
	public void UFOFirePerformed(UFO u)
	{
		if(u.isDisposing()){
			return;
		}
		if(u.getSize() == UFO.Size.LARGE){
			float angle = MathUtil.getRandomDirection();
			this.mUFOShots.add(new Shot(UUID.randomUUID(), this, u.x, u.y, angle, this.mLargeUFOShotSpeed, this.mShotRange));
		}else if(u.getSize() == UFO.Size.SMALL){
			float angle = MathUtil.getAngleFromPoints(u.x, u.y, this.mShip.x, this.mShip.y);
			this.mUFOShots.add(new Shot(UUID.randomUUID(), this, u.x, u.y, angle, this.mSmallUFOShotSpeed, this.mShotRange));
		}
		this.mSFXManager.playCannonClip();
		return;
	}
	
	public void playerFirePerformed()
	{
		if(this.mPlayerShots.size() >= MAX_ACTIVE_PLAYER_SHOTS){
			return;
		}
		float dirX = (float)Math.cos(this.mShip.getRotation());
		float dirY = -(float)Math.sin(this.mShip.getRotation());
		float offset = Math.max(this.mShip.getShape().getBounds().width / 2f, this.mShip.getShape().getBounds().height / 2f);
		float startX = this.mShip.x + (dirX * offset);
		float startY = this.mShip.y + (dirY * offset);
		this.mPlayerShots.add(new Shot(UUID.randomUUID(), this, startX, startY, this.mShip.getRotation(), this.mPlayerShotSpeed, this.mShotRange));
		this.mSFXManager.playCannonClip();
		return;
	}
	
	public void performHyperspace()
	{
		if(Math.random() <= CHANCE_OF_HYPERSPACE_SELF_DESTRUCTION){
			this.destroyShip();
			return;
		}
		
		// If the ship doesn't self-destruct, hyperspace it somewhere.
		int x = (int)Math.round((Math.random() * ((this.mScreenBounds.width - this.mHyperspaceBoundary) - this.mHyperspaceBoundary)) + this.mHyperspaceBoundary);
		int y = (int)Math.round((Math.random() * ((this.mScreenBounds.height - this.mHyperspaceBoundary) - this.mHyperspaceBoundary)) + this.mHyperspaceBoundary);
		this.mShip.x = x;
		this.mShip.y = y;
		return;
	}
	
	public void anyKeyEventPerformed()
	{
		this.mListener.checkScore();
		this.mGameOver.reset();
		this.mLevelControl = this.mGameOver;
		this.mUI.setToWaitMode();
		return;
	}
	
	public int getLeftKeycode()
	{
		return this.mListener.getKeyBinding(KeyNames.LEFT);
	}
	
	public int getRightKeyCode()
	{
		return this.mListener.getKeyBinding(KeyNames.RIGHT);
	}
	
	public int getThrustKeyCode()
	{
		return this.mListener.getKeyBinding(KeyNames.THRUST);
	}
	
	public int getFireKeyCode()
	{
		return this.mListener.getKeyBinding(KeyNames.FIRE);
	}
	
	public int getHyperspaceKeyCode()
	{
		return this.mListener.getKeyBinding(KeyNames.HYPERSPACE);
	}
	
	public void setToGameOverMode()
	{
		this.mGameOver.reset();
		this.mLevelControl = this.mGameOver;
	}
	
	private class StartNewLevel implements LevelState
	{
		private long mDelayDuration = START_NEW_LEVEL_DELAY;
		private long mDelayStart = 0;
		
		public StartNewLevel() { return; }
		
		public void reset()
		{
			this.mDelayStart = System.currentTimeMillis();
			mHighScoreLabel.setText("0");
			return;
		}
		
		public void update()
		{
			if(System.currentTimeMillis() - this.mDelayStart > this.mDelayDuration){
				initializeAsteroids(mCurrentStartAsteroids);
				mCurrentStartAsteroids++;
				mCurrentStartAsteroids = mCurrentStartAsteroids > MAXIMUM_ASTEROIDS ? MAXIMUM_ASTEROIDS : mCurrentStartAsteroids;
				
				if(mListener.isDoublesGame()){
					mDoublesGameIntroState.reset();
					mLevelControl = mDoublesGameIntroState;
				}else{
					mNormalPlayState.reset();
					mLevelControl = mNormalPlayState;
				}
				return;
			}
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderAsteroids(g2d, original);
			renderPlayerShots(g2d);
			renderShip(g2d, original);
			return;
		}
	}
	
	private class DoublesGameIntroState implements LevelState
	{
		private long duration = DOUBLES_INTRO_STATE_DELAY;
		private long start = 0;
		
		public DoublesGameIntroState() { return; }
		
		public void reset()
		{
			this.start = System.currentTimeMillis();
			mUI.setToGameMode();
			mMusic.startMusic();
			return;
		}
		
		public void update()
		{
			if(System.currentTimeMillis() - this.start > this.duration){
				mNormalPlayState.reset();
				mLevelControl = mNormalPlayState;
				return;
			}
			
			if(mShip.isDisposing()){
				mPlayerJustDiedState.reset();
				mLevelControl = mPlayerJustDiedState;
				return;
			}
			
			if(mAsteroids.isEmpty() && (mUFO == null || mUFO.isDisposing())){
				mLevelComplete.reset();
				mLevelControl = mLevelComplete;
				return;
			}
			
			refillQuadTree();
			
			updateAsteroids();
			updateShip();
			updatePlayerShots();
			updateUFOShots();
			updateExplosions();
			updateMusic();
			
			checkAsteroidCollisions();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderUFOShots(g2d);
			renderAsteroids(g2d, original);
			renderUFO(g2d);
			renderExplosions(g2d);
			renderShip(g2d, original);
			renderIntroLabel(g2d, mPlayer.getPlayerNum());
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class NormalPlayState implements LevelState
	{
		long ufoDelayStart = 0;
		long ufoDelay = 0;
		
		public NormalPlayState() { return; }
		
		public void reset()
		{
			mUI.setToGameMode();
			this.ufoDelay = (long)Math.round((Math.random() * (MAXIMUM_UFO_DELAY - MINIMUM_UFO_DELAY)) + MINIMUM_UFO_DELAY);
			this.ufoDelayStart = System.currentTimeMillis();
			mMusic.startMusic();
			return;
		}
		
		public void update()
		{
			if(mShip.isDisposing()){
				mPlayerJustDiedState.reset();
				mLevelControl = mPlayerJustDiedState;
				return;
			}
			
			if(mAsteroids.isEmpty() && (mUFO == null || mUFO.isDisposing())){
				mLevelComplete.reset();
				mLevelControl = mLevelComplete;
				return;
			}
			
			if(System.currentTimeMillis() - this.ufoDelayStart > this.ufoDelay){
				mUFONormalPlayState.reset();
				mLevelControl = mUFONormalPlayState;
				return;
			}
			
			refillQuadTree();
			
			updateAsteroids();
			updateShip();
			updatePlayerShots();
			updateUFOShots();
			updateExplosions();
			updateMusic();
			
			checkAsteroidCollisions();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderUFOShots(g2d);
			renderAsteroids(g2d, original);
			renderUFO(g2d);
			renderExplosions(g2d);
			renderShip(g2d, original);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class UFONormalPlayState implements LevelState
	{
		public UFONormalPlayState() { return; }
		
		public void reset()
		{
			createUFO();
			if(mUFO.getSize() == UFO.Size.SMALL){
				mSFXManager.startSmallUFO();
			}else if(mUFO.getSize() == UFO.Size.LARGE){
				mSFXManager.startLargeUFO();
			}
			return;
		}
		
		public void update()
		{
			if(mShip.isDisposing()){
				mUFO.setToWeaponIdle();
				mPlayerJustDiedState.reset();
				mLevelControl = mPlayerJustDiedState;
				return;
			}
			
			if(mAsteroids.isEmpty() && (mUFO == null || mUFO.isDisposing())){
				mLevelComplete.reset();
				mLevelControl = mLevelComplete;
				return;
			}
			
			if(mUFO.isDisposing()){
				if(mUFO.getSize() == UFO.Size.SMALL){
					mSFXManager.stopSmallUFO();
				}else if(mUFO.getSize() == UFO.Size.LARGE){
					mSFXManager.stopLargeUFO();
				}
				mNormalPlayState.reset();
				mLevelControl = mNormalPlayState;
				return;
			}
			
			refillQuadTree();
			
			updateAsteroids();
			updateUFO();
			updateShip();
			updatePlayerShots();
			updateUFOShots();
			updateExplosions();
			updateMusic();
			
			checkAsteroidCollisions();
			checkShipUFOCollision();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderUFOShots(g2d);
			renderAsteroids(g2d, original);
			renderUFO(g2d);
			renderExplosions(g2d);
			renderShip(g2d, original);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class PlayerJustDiedState implements LevelState
	{
		private long mDelayDuration = 1500;
		private long mDelayStart = 0;
		
		public PlayerJustDiedState() { return; }
		
		public void reset()
		{
			mUI.setToWaitMode();
			this.mDelayStart = System.currentTimeMillis();
			mMusic.stopMusic();
			return;
		}
		
		public void update()
		{
			if(System.currentTimeMillis() - this.mDelayStart >= this.mDelayDuration){
				if(mPlayer.getLives() <= 0){
					mPreGameOver.reset();
					mLevelControl = mPreGameOver;
					mUI.setToPressAnyKeyMode();
					return;
				}else{
					mPlayerRespawnState.reset();
					mLevelControl = mPlayerRespawnState;
					return;
				}
			}
			
			refillQuadTree();
			
			updateAsteroids();
			updateUFO();
			updatePlayerShots();
			updateUFOShots();
			updateExplosions();
			checkAsteroidCollisions();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderUFOShots(g2d);
			renderAsteroids(g2d, original);
			renderUFO(g2d);
			renderExplosions(g2d);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class PlayerRespawnState implements LevelState
	{
		public PlayerRespawnState() { return; }
		
		public void reset()
		{
			if(mListener.isDoublesGame()){
				mListener.manageDoublesGame();
			}
			return;
		}
		
		public void update()
		{
			refillQuadTree();
			if(safeToRespawn()){
				mShip.reset(mScreenBounds.width / 2f, mScreenBounds.height / 2f, 0f);
				mShip.setDisposing(false);
				mPlayer.removeLife();
				if(mListener.isDoublesGame()){
					mDoublesGameIntroState.reset();
					mLevelControl = mDoublesGameIntroState;
				}else{
					mNormalPlayState.reset();
					mLevelControl = mNormalPlayState;
				}
				return;
			}
			
			refillQuadTree();
			
			updateAsteroids();
			updateUFO();
			updatePlayerShots();
			updateExplosions();
			checkAsteroidCollisions();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderAsteroids(g2d, original);
			renderUFO(g2d);
			renderExplosions(g2d);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class LevelComplete implements LevelState
	{
		private long mDelayDuration = LEVEL_COMPLETE_STATE_DELAY;
		private long mDelayStart = 0;
		
		public LevelComplete() { return; }
		
		public void reset()
		{
			this.mDelayStart = System.currentTimeMillis();
			mMusic.stopMusic();
			return;
		}
		
		public void update()
		{
			if(System.currentTimeMillis() - this.mDelayStart >= this.mDelayDuration){
				mStartNewLevel.reset();
				mLevelControl = mStartNewLevel;
				return;
			}
			
			updatePlayerShots();
			updateUFOShots();
			updateShip();
			updateExplosions();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderUFOShots(g2d);
			renderExplosions(g2d);
			renderShip(g2d, original);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class PreGameOver implements LevelState
	{
		public PreGameOver() { return; }
		
		public void reset()
		{
			mMusic.stopMusic();
			return;
		}
		
		public void update()
		{
			updateAsteroids();
			updateUFO();
			updatePlayerShots();
			updateUFOShots();
			updateExplosions();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderAsteroids(g2d, original);
			renderUFO(g2d);
			renderLabels(g2d, original, base);
			renderPlayerShots(g2d);
			renderUFOShots(g2d);
			renderExplosions(g2d);
			mGameOverLabel.render(g2d);
			mPressAnyKeyLabel.render(g2d);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class GameOver implements LevelState
	{
		public GameOver() { return; }
		
		public void reset()
		{
			return;
		}
		
		public void update()
		{
			if(mListener.isDoublesGame()){
				mListener.manageDoublesPlayerGameOver(mPlayer.getPlayerNum());
			}else{
				mListener.manageSinglePlayerGameOver();
			}
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			
			return;
		}
	}
	
	public interface Listener
	{
		abstract int getKeyBinding(KeyNames keyName);
		abstract void beginApplicationShutdown();
		abstract boolean isDoublesGame();
		abstract Player getPlayer(Player.Num PlayerNum);
		abstract void manageSinglePlayerGameOver();
		abstract void manageDoublesGame();
		abstract void manageDoublesPlayerGameOver(Player.Num player);
		abstract void checkScore();
		abstract void switchAppStates(AppState state);
	}
}
