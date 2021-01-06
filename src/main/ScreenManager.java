package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import main.polycomp.AbstractLabel;
import main.polycomp.PAlign;
import main.unit.Player;

/**
 * Abstract class which handles the most rudimentary graphics aspects (i.e.; screen size, cursor, etc.).
 * 
 * This class is also the basis for all AppStates (i.e.; main menu, options, game, etc.).
 * 
 * @author John McCullock
 * @version 1.0 20150-08-07
 */
abstract class ScreenManager
{
	public final float PI = (float)Math.PI;
	
	protected final Point2D.Float FPS_LABEL_FACTORS = new Point2D.Float(0.02381f, 0.15238f); // by screen width and height.
	protected final Polygon LABEL_TEMPLATE = new Polygon(new int[]{20, 180, 200, 180, 20, 0},
														new int[]{0, 0, 20, 40, 40, 20},
														6);
	protected final Insets LABEL_INSETS = new Insets(2, 15, 2, 15);
	
	private ScreenManager.Listener mListener = null;
	protected GraphicsConfiguration mGC = null;
	protected Rectangle mScreenBounds = new Rectangle();
	protected Color mBackgroundColor = Skin.SCREEN_BACKGROUND;
	protected BufferedImage mNormalCursor = null;
	protected Point mLastMouse = new Point();
	protected int mFPS = 0;
	protected ScreenManager.Label mFPSLabel = null;
	protected Font mFPSLabelFont = null;
	
	public ScreenManager(int x, int y, int width, int height, ScreenManager.Listener smListener)
	{
		this.mScreenBounds.x = x;
		this.mScreenBounds.y = y;
		this.mScreenBounds.width = width;
		this.mScreenBounds.height = height;
		this.mListener = smListener;
		this.initializeMain();
		return;
	}
	
	private void initializeMain()
	{
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = env.getDefaultScreenDevice();
		this.mGC = gd.getDefaultConfiguration();
		this.initializeCursors();
		
		this.mFPSLabelFont = Skin.FPS_LABEL_FONT;
		this.mFPSLabelFont = this.createLabelFont(this.mFPSLabelFont, Skin.FPS_LABEL_FONT_STYLE, Skin.FPS_LABEL_FONT_FACTOR);
		this.initializeFPSLabel();
		return;
	}
	
	public void renderBackground(Graphics2D g2d)
	{
		g2d.setPaint(this.mBackgroundColor);
		g2d.fillRect(this.mScreenBounds.x, this.mScreenBounds.y, this.mScreenBounds.width, this.mScreenBounds.height);
		return;
	}

	public void renderCursorDirty(Graphics2D g2d, BufferedImage backdrop)
	{
		g2d.drawImage(backdrop, 
						this.mLastMouse.x, 
						this.mLastMouse.y, 
						this.mLastMouse.x + this.mNormalCursor.getWidth(), 
						this.mLastMouse.y + this.mNormalCursor.getHeight(), 
						this.mLastMouse.x, 
						this.mLastMouse.y, 
						this.mLastMouse.x + this.mNormalCursor.getWidth(), 
						this.mLastMouse.y + this.mNormalCursor.getHeight(), 
						null);
		return;
	}
	
	public void renderCursor(Graphics2D g2d, int x, int y)
	{
		g2d.drawImage(this.mNormalCursor, x, y, null);
		this.mLastMouse.x = x;
		this.mLastMouse.y = y;
		return;
	}
	
	public void renderFPSLabel(Graphics2D g2d)
	{
		if(!this.mListener.getShowFPSOption()){
			return;
		}
		this.mFPSLabel.setText("FPS: " + String.valueOf(this.mFPS));
		this.mFPSLabel.render(g2d);
		return;
	}
	
	private void initializeCursors()
	{
		try{
			this.mNormalCursor = ImageLoader.getImageFromResourcePath(this.getClass().getResource("/assets/cursors/cursor2.png"));
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return;
	}
	
	protected Font createLabelFont(Font font, int style, float factor)
	{
		font = font.deriveFont(style);
		font = font.deriveFont((float)FontSizer.getFontSize(factor, this.mScreenBounds.height));
		return font;
	}
	
	protected void initializeFPSLabel()
	{
		this.mFPSLabel = new ScreenManager.Label();
		this.mFPSLabel.setText("FPS: ");
		this.mFPSLabel.setBackground(this.mBackgroundColor);
		this.mFPSLabel.setFont(this.mFPSLabelFont);
		this.mFPSLabel.setForeground(Skin.FPS_LABEL_FOREGROUND);
		this.mFPSLabel.setAlignment(PAlign.LEFT);
		this.mFPSLabel.setLocation((int)Math.round(this.mScreenBounds.width * FPS_LABEL_FACTORS.x), 
									(int)Math.round(this.mScreenBounds.height * FPS_LABEL_FACTORS.y));
		return;
	}
	
	public Rectangle getScreenBounds()
	{
		return this.mScreenBounds;
	}
	
	public void setBackgroundColor(Color color)
	{
		this.mBackgroundColor = color;
		return;
	}
	
	public void setFPS(int fps)
	{
		this.mFPS = fps;
		return;
	}
	
	public class Label extends AbstractLabel
	{
		public void render(Graphics2D g2d)
		{
			if(this.mIsOpaque){
				this.renderBackground(g2d);
			}
			this.renderCaption(g2d);
			return;
		}
		
		public void renderBackground(Graphics2D g2d)
		{
			g2d.setPaint(this.mBackground);
			g2d.fillRect(this.mBounds.x, this.mBounds.x, this.mBounds.width, this.mBounds.height);
			return;
		}
		
		public void renderCaption(Graphics2D g2d)
		{
			if(this.mForeground != null){
				g2d.setPaint(this.mForeground);
			}
			int x = this.mBounds.x;
			if(this.mAlignment == PAlign.RIGHT){
				x-= g2d.getFontMetrics(this.mFont).stringWidth(this.mText);
			}
			if(this.mText != null && !this.mText.isEmpty() && this.mFont != null){
				g2d.setFont(this.mFont);
				g2d.drawString(this.mText, x, this.mBounds.y);
			}
			return;
		}
	}
	
	public interface Listener
	{
		abstract Player getPlayer(Player.Num PlayerNum);
		abstract boolean getShowFPSOption();
	}
}
