package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.Vector;

import main.polycomp.PAlign;
import main.unit.Asteroid;
import main.unit.entity.util.MathUtil;
import main.unit.entity.util.Polygon2;

/**
 * Lobby class which plays background content for non-game windows.  Since it already has variables for asteroids,
 * it seemed best to house the game's asteroid values here.
 * 
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class Lobby extends ScreenManager
{
	protected final int MINIMUM_ASTEROIDS = 4;
	protected final float MINIMUM_ASTEROID_SPEED_FACTOR = 0.00119f; // by screen width;
	protected final float MAXIMUM_ASTEROID_SPEED_FACTOR = 0.003f; // by screen width
	protected final String COPYRIGHT_CAPTION = "1979 ATARI INC " + Character.toString((char)169);
	protected final Point2D.Float COPYRIGHT_LABEL_FACTORS = new Point2D.Float(0.5f, 0.9f);
	
	protected Vector<Asteroid> mAsteroids = new Vector<Asteroid>();
	protected float mMinAsteroidSpeed = 0f;
	protected float mMaxAsteroidSpeed = 0f;
	protected ScreenManager.Label mCopyrightLabel = null;
	protected Font mLargeFont = null;
	protected Font mSmallFont = null;
	protected Font mMegaFont = null;
	
	protected LobbyIdle mLobbyIdle = new LobbyIdle();
	protected StartLobby mStartLobby = new StartLobby();
	protected NormalLobbyState mNormalLobbyState = new NormalLobbyState();
	protected LevelState mLobbyControl = this.mLobbyIdle;
	
	public Lobby(int x, int y, int width, int height, ScreenManager.Listener smListener)
	{
		super(x, y, width, height, smListener);
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		this.mMinAsteroidSpeed = this.mScreenBounds.width * MINIMUM_ASTEROID_SPEED_FACTOR;
		this.mMaxAsteroidSpeed = this.mScreenBounds.width * MAXIMUM_ASTEROID_SPEED_FACTOR;
		this.initializeFonts();
		this.mCopyrightLabel = this.initializeLabel(new ScreenManager.Label(), this.mSmallFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.CENTER, COPYRIGHT_LABEL_FACTORS);
		this.mCopyrightLabel.setText(COPYRIGHT_CAPTION);
		this.mCopyrightLabel = this.centerLabel(this.mCopyrightLabel, COPYRIGHT_LABEL_FACTORS);
		return;
	}
	
	public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
	{
		this.mLobbyControl.render(g2d, original, base);
		return;
	}
	
	protected void renderAsteroids(Graphics2D g2d, AffineTransform original)
	{
		for(int i = this.mAsteroids.size() - 1; i >= 0; i--)
		{
			if(this.mAsteroids.get(i).isDisposing()){
				this.mAsteroids.remove(i);
				continue;
			}
			
			g2d.setPaint(Color.WHITE);
			g2d.drawPolygon(this.mAsteroids.get(i).getShape());
			
			this.renderWraps(g2d, original, this.mAsteroids.get(i).getShape());
			
			//Testing: show true location.
			//g2d.fillOval((int)Math.round(this.mAsteroids.get(i).x - 2), (int)Math.round(this.mAsteroids.get(i).y - 2), 4, 4);
		}
		return;
	}
	
	protected void renderWraps(Graphics2D g2d, AffineTransform original, Polygon2 poly)
	{
		boolean wrapping = false;
		float halfWidth = poly.getBounds().width / 2f;
		float halfHeight = poly.getBounds().height / 2f;
		int deltaX = 0;
		int deltaY = 0;
		if(poly.getX() - halfWidth < 0){
			deltaX = this.mScreenBounds.width;
			wrapping = true;
		}else if(poly.getX() + halfWidth > this.mScreenBounds.width){
			deltaX = -this.mScreenBounds.width;
			wrapping = true;
		}
		if(poly.getY() - halfHeight < 0){
			deltaY = this.mScreenBounds.height;
			wrapping = true;
		}else if(poly.getY() + halfHeight > this.mScreenBounds.height){
			deltaY = -this.mScreenBounds.height;
			wrapping = true;
		}
		if(wrapping){
			g2d.translate(deltaX, deltaY);
			g2d.drawPolygon(poly);
		}
		g2d.setTransform(original);
		return;
	}
	
	protected void renderCopyrightLabel(Graphics2D g2d)
	{
		this.mCopyrightLabel.render(g2d);
		return;
	}
	
	public void update()
	{
		this.mLobbyControl.update();
		return;
	}
	
	protected void updateAsteroids()
	{
		for(int i = 0; i < this.mAsteroids.size(); i++)
		{
			if(this.mAsteroids.get(i).isDisposing()){
				continue;
			}
			
			float dirX = (float)Math.cos(this.mAsteroids.get(i).getDirection());
			float dirY = (float)-Math.sin(this.mAsteroids.get(i).getDirection());
			float newX = this.mAsteroids.get(i).x + (dirX * this.mAsteroids.get(i).getSpeed());
			float newY = this.mAsteroids.get(i).y + (dirY * this.mAsteroids.get(i).getSpeed());
			newX = newX < 0f ? this.mScreenBounds.width : newX;
			newX = newX > this.mScreenBounds.width ? 0f : newX;
			newY = newY < 0f ? this.mScreenBounds.height : newY;
			newY = newY > this.mScreenBounds.height ? 0f : newY;
			this.mAsteroids.get(i).x = newX;
			this.mAsteroids.get(i).y = newY;
			this.mAsteroids.get(i).invalidate();
			
			this.mAsteroids.get(i).getShape().setLocation((int)Math.round(this.mAsteroids.get(i).x), (int)Math.round(this.mAsteroids.get(i).y));
		}
		return;
	}
	
	protected void initializeFonts()
	{
		this.mLargeFont = Skin.LARGE_LABEL_FONT;
		this.mLargeFont = this.mLargeFont.deriveFont(Skin.LARGE_LABEL_FONT_STYLE);
		this.mLargeFont = this.mLargeFont.deriveFont((float)FontSizer.getFontSize(Skin.LARGE_LABEL_FONT_FACTOR, this.getScreenBounds().height));
		
		this.mSmallFont = Skin.SMALL_LABEL_FONT;
		this.mSmallFont = this.mSmallFont.deriveFont(Skin.SMALL_LABEL_FONT_STYLE);
		this.mSmallFont = this.mSmallFont.deriveFont((float)FontSizer.getFontSize(Skin.SMALL_LABEL_FONT_FACTOR, this.getScreenBounds().height));
		
		this.mMegaFont = Skin.GAME_OVER_FONT;
		this.mMegaFont = this.mMegaFont.deriveFont(Skin.GAME_OVER_FONT_STYLE);
		this.mMegaFont = this.mMegaFont.deriveFont((float)FontSizer.getFontSize(Skin.MEGA_FONT_FACTOR, this.getScreenBounds().height));
		return;
	}
	
	protected void initializeAsteroids(int total)
	{
		for(int i = 0; i < total; i++)
		{
			this.mAsteroids.add(this.createAsteroid());
		}
		return;
	}
	
	/**
	 * Randomly placed for the beginning of each level.
	 * @param size
	 * @return
	 */
	protected Asteroid createAsteroid()
	{
		Asteroid a = new Asteroid(UUID.randomUUID());
		a.setSize(Asteroid.Size.LARGE);
		float x = 0f;
		float y = 0f;
		if(Math.random() < 0.5f){
			// start along the top or bottom.
			if(Math.random() > 0.5f){
				y = this.mScreenBounds.height;
			}
			x = (float)Math.floor(Math.random() * this.mScreenBounds.width);
		}else{
			// start along the left or right.
			if(Math.random() > 0.5f){
				x = this.mScreenBounds.width;
			}
			y = (float)Math.floor(Math.random() * this.mScreenBounds.height);
		}
		a.x = x;
		a.y = y;
		a.setDirection(MathUtil.getRandomDirection());
		a.setSpeed(this.mMinAsteroidSpeed);
		int choice = (int)Math.floor(Math.random() * 4f);
		Polygon2 poly = EntityData.getShape(choice);
		float width = (float)poly.getBounds2D().getWidth();
		float height = (float)poly.getBounds2D().getHeight();
		Dimension d = this.getAsteroidDimension(Asteroid.Size.LARGE, poly, new Dimension());
		poly.resizePolygon(d.width, d.height);
		a.width = width;
		a.height = height;
		a.invalidate();
		poly.setLocation((int)Math.round(a.x), (int)Math.round(a.y));
		a.setShape(poly);
		return a;
	}
	
	protected Dimension getAsteroidDimension(Asteroid.Size size, Polygon2 poly, Dimension result)
	{
		float width = (float)poly.getBounds2D().getWidth();
		float height = (float)poly.getBounds2D().getHeight();
		float factor = 0f;
		
		if(size == Asteroid.Size.LARGE){
			factor = (this.mScreenBounds.height * EntityData.ASTEROID_LARGE_HEIGHT_FACTOR) / width;
		}else if(size == Asteroid.Size.MEDIUM){
			factor = (this.mScreenBounds.height * EntityData.ASTEROID_MEDIUM_HEIGHT_FACTOR) / width;
		}else if(size == Asteroid.Size.SMALL){
			factor = (this.mScreenBounds.height * EntityData.ASTEROID_SMALL_HEIGHT_FACTOR) / width;
		}
		result.width = (int)Math.round(width * factor);
		result.height = (int)Math.round(height * factor);
		return result;
	}
	
	protected ScreenManager.Label initializeLabel(ScreenManager.Label label, Font font, Color foreground, PAlign align, Point2D.Float factors)
	{
		if(this.mLargeFont == null){
			return null;
		}
		
		label = new ScreenManager.Label();
		label.setText("");
		label.setBackground(this.mBackgroundColor);
		label.setFont(font);
		label.setForeground(foreground);
		label.setAlignment(align);
		label.setLocation((int)Math.round((this.mScreenBounds.width * factors.x) - (label.getSize().width / 2f)),
							(int)Math.round(this.mScreenBounds.height * factors.y));
		return label;
	}
	
	protected ScreenManager.Label centerLabel(ScreenManager.Label label, Point2D.Float factors)
	{
		BufferedImage temp = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		int offset = (int)Math.round(g2d.getFontMetrics(label.getFont()).stringWidth(label.getText()) / 2f);
		int x = (int)Math.round((this.mScreenBounds.width * factors.x) - (label.getSize().width / 2f));
		label.setLocation(x - offset, (int)Math.round(this.mScreenBounds.height * factors.y));
		return label;
	}
	
	protected class LobbyIdle implements LevelState
	{
		public LobbyIdle() { return; }
		
		public void reset()
		{
			return;
		}
		
		public void update()
		{
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			return;
		}
	}
	
	protected class StartLobby implements LevelState
	{
		public StartLobby() { return; }
		
		public void reset()
		{
			return;
		}
		
		public void update()
		{
			initializeAsteroids(MINIMUM_ASTEROIDS);
			mNormalLobbyState.reset();
			mLobbyControl = mNormalLobbyState;
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderAsteroids(g2d, original);
			return;
		}
	}
	
	protected class NormalLobbyState implements LevelState
	{
		public NormalLobbyState() { return; }
		
		public void reset()
		{
			return;
		}
		
		public void update()
		{
			updateAsteroids();
			return;
		}
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderAsteroids(g2d, original);
			return;
		}
	}
}
