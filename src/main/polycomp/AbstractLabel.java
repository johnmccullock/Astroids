package main.polycomp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class AbstractLabel
{
	protected final PAlign DEFAULT_ALIGNMENT = PAlign.LEFT;
	protected final Color DEFAULT_BACKGROUND = new Color(255, 255, 255, 255);
	protected final Color DEFAULT_FOREGROUND = new Color(0, 0, 0, 255);
	protected final Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);
	protected final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	
	protected Rectangle mBounds = new Rectangle();
	protected Color mBackground = DEFAULT_BACKGROUND;
	protected Color mForeground = DEFAULT_FOREGROUND;
	protected PAlign mAlignment = DEFAULT_ALIGNMENT;
	protected Insets mInsets = DEFAULT_INSETS;
	protected String mText = new String();
	protected Font mFont = DEFAULT_FONT;
	protected boolean mIsVisible = true;
	protected boolean mIsOpaque = false;
	
	public abstract void render(Graphics2D g2d);
	public abstract void renderBackground(Graphics2D g2d);
	public abstract void renderCaption(Graphics2D g2d);
	
	public void setText(String text)
	{
		this.mText = text;
		return;
	}
	
	public String getText()
	{
		return this.mText;
	}
	
	public void setLocation(int x, int y)
	{
		this.mBounds.x = x;
		this.mBounds.y = y;
		return;
	}
	
	public Point getLocation()
	{
		return new Point(this.mBounds.x, this.mBounds.y);
	}
	
	public Dimension getSize()
	{
		return new Dimension(this.mBounds.width, this.mBounds.height);
	}
	
	public void setBackground(Color color)
	{
		this.mBackground = color;
		return;
	}
	
	public Color getBackground()
	{
		return this.mBackground;
	}
	
	public void setForeground(Color color)
	{
		this.mForeground = color;
		return;
	}
	
	public Color getForeground()
	{
		return this.mForeground;
	}
	
	public void setFont(Font font)
	{
		this.mFont = font;
		return;
	}
	
	public Font getFont()
	{
		return this.mFont;
	}
	
	public void setAlignment(PAlign alignment)
	{
		this.mAlignment = alignment;
		return;
	}
	
	public PAlign getAlignment()
	{
		return this.mAlignment;
	}
	
	public void setInsets(Insets insets)
	{
		this.mInsets = insets;
		return;
	}
	
	public Insets getInsets()
	{
		return this.mInsets;
	}
	
	public void setVisible(boolean value)
	{
		this.mIsVisible = value;
		return;
	}
	
	public boolean getVisible()
	{
		return this.mIsVisible;
	}
	
	public boolean isVisible()
	{
		return this.mIsVisible;
	}
	
	public void setOpaque(boolean isOpaque)
	{
		this.mIsOpaque = isOpaque;
		return;
	}
	
	public boolean getOpaque()
	{
		return this.mIsOpaque;
	}
	
	public boolean isOpaque()
	{
		return this.mIsOpaque;
	}
}
