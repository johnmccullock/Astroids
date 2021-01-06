package main.polycomp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import java.util.Vector;

/**
 * Limited, single-line text entry box.
 * 
 * The text is printed from left to right until either the right text boundary rectangle is met, or the character
 * limit is reached.  This version does not scroll a viewport to keep cursor and visible text in sight.
 * 
 * @author John McCullock
 * @version 1.0 2015-07-31
 */
public class PTextBox
{
	public enum Fit{NONE, VERTICAL, HORIZONTAL, BOTH};
	
	private final PTextBox.Fit DEFAULT_FIT = PTextBox.Fit.NONE;
	private final Font DEFAULT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	private final Insets DEFAULT_INSETS = new Insets(2, 4, 2, 4);
	private final Color DEFAULT_BACKGROUND = new Color(192, 192, 192, 255);
	private final Color DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 255);
	private final BasicStroke DEFAULT_BORDER_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private final Color DEFAULT_FOREGROUND = new Color(0, 0, 0, 255);
	private final Color DEFAULT_TEXTRECT_COLOR = new Color(255, 255, 0, 255);
	
	private GraphicsConfiguration mGC = null;
	private PolyShape mBounds = null;
	private Rectangle mTextRect = new Rectangle();
	private PTextBox.Fit mFitText = DEFAULT_FIT;
	private Font mFont = DEFAULT_FONT;
	private int mFontHeight = 0;
	private Insets mInsets = DEFAULT_INSETS;
	private Color mBackground = DEFAULT_BACKGROUND;
	private Color mBorderColor = DEFAULT_BORDER_COLOR;
	private BasicStroke mBorderStroke = DEFAULT_BORDER_STROKE;
	private Color mForeground = DEFAULT_FOREGROUND;
	private int mCursorDelay = 0;
	private int mCursorStep = 0;
	private boolean mShowCursor = true;
	private boolean mShowTextRect = false;
	private Color mTextRectColor = DEFAULT_TEXTRECT_COLOR;
	private int mMaxCharacters = 0;
	
	private Vector<PChar> mText = new Vector<PChar>();
	private int mCarat = 0;
	
	public PTextBox(PolyShape bounds)
	{
		this.mBounds = bounds;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		this.mGC = gd.getDefaultConfiguration();
		return;
	}
	
	public void render(Graphics2D g2d)
	{
		this.renderBackground(g2d);
		this.renderBorder(g2d);
		if(this.mShowTextRect){
			this.renderTextRect(g2d);
		}
		this.renderText(g2d);
		
		if(this.mCursorDelay <= 0){
			this.renderCursor(g2d);
		}else if(this.mCursorStep >= this.mCursorDelay){
			this.mShowCursor = !this.mShowCursor;
			this.mCursorStep = 0;
		}else{
			if(this.mShowCursor){
				this.renderCursor(g2d);
			}
			this.mCursorStep++;
		}
		return;
	}
	
	private void renderBackground(Graphics2D g2d)
	{
		if(this.mBackground == null){
			return;
		}
		g2d.setPaint(this.mBackground);
		g2d.fillPolygon(this.mBounds.getPolygon());
		return;
	}
	
	private void renderBorder(Graphics2D g2d)
	{
		if(this.mBorderColor == null){
			return;
		}
		if(this.mBorderStroke == null){
			return;
		}
		g2d.setPaint(this.mBorderColor);
		g2d.setStroke(this.mBorderStroke);
		g2d.drawPolygon(this.mBounds.getPolygon());
		return;
	}
	
	private void renderTextRect(Graphics2D g2d)
	{
		g2d.setPaint(this.mTextRectColor);
		g2d.setStroke(DEFAULT_BORDER_STROKE);
		g2d.drawRect(this.mTextRect.x, this.mTextRect.y, this.mTextRect.width, this.mTextRect.height);
		return;
	}
	
	private void renderText(Graphics2D g2d)
	{
		int width = 0;
		int x = this.mTextRect.x;
		int y = this.mTextRect.y + this.mFontHeight;
		g2d.setFont(this.mFont);
		g2d.setPaint(this.mForeground);
		for(int i = 0; i < this.mText.size(); i++)
		{
			width = this.mText.get(i).width;
			if(x + width > this.mTextRect.x + this.mTextRect.width){
				break;
			}
			g2d.drawString(String.valueOf(this.mText.get(i).code), x, y);
			x += width;
		}
		return;
	}
	
	private void renderCursor(Graphics2D g2d)
	{
		int x = this.mTextRect.x;
		for(int i = 0; i < this.mCarat; i++)
		{
			if(x + this.mText.get(i).width > this.mTextRect.x + this.mTextRect.width){
				break;
			}else{
				x += this.mText.get(i).width;
			}
		}
		int y = this.mTextRect.y + this.mFontHeight;
		g2d.setFont(this.mFont);
		g2d.setPaint(this.mForeground);
		g2d.drawString("_", x, y);
		return;
	}
	
	public void invalidate()
	{
		BufferedImage temp = this.mGC.createCompatibleImage(300, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		this.mFontHeight = g2d.getFontMetrics(this.mFont).getHeight();
		
		if(this.mFitText.equals(PTextBox.Fit.VERTICAL)){
			this.layoutStretchVertical();
		}else if(this.mFitText.equals(PTextBox.Fit.HORIZONTAL)){
			this.layoutStretchHorizontal();
		}else if(this.mFitText.equals(PTextBox.Fit.BOTH)){
			this.layoutStretchBoth();
		}else{
			this.layoutNoStretch();
		}
		return;
	}
	
	private void layoutNoStretch()
	{
		if(this.mInsets == null){
			return;
		}
		this.mTextRect.x = this.mBounds.getLeft() + this.mInsets.left;
		this.mTextRect.y = this.mBounds.getTop() + this.mInsets.top;
		this.mTextRect.width = this.mBounds.getWidth() - (this.mInsets.left + this.mInsets.right);
		this.mTextRect.height = this.mBounds.getHeight() - (this.mInsets.top + this.mInsets.bottom);
		return;
	}
	
	private void layoutStretchVertical()
	{
		this.mTextRect.height = this.mFontHeight;
		this.mBounds.resizePolygon(this.mBounds.getWidth(), this.mFontHeight + this.mInsets.top + this.mInsets.bottom);
		
		// Now set the rest neutral.
		this.mTextRect.x = this.mBounds.getLeft() + this.mInsets.left;
		this.mTextRect.y = this.mBounds.getTop() + this.mInsets.top;
		this.mTextRect.width = this.mBounds.getWidth() - (this.mInsets.left + this.mInsets.right);
		return;
	}
	
	private void layoutStretchHorizontal()
	{
		int width = 0;
		for(int i = 0; i < this.mText.size(); i++)
		{
			width += this.mText.get(i).width;
		}
		this.mTextRect.width = width;
		this.mBounds.resizePolygon(width + this.mInsets.left + this.mInsets.right, this.mBounds.getHeight());
		
		// Now set the rest neutral.
		this.mTextRect.x = this.mBounds.getLeft() + this.mInsets.left;
		this.mTextRect.y = this.mBounds.getTop() + this.mInsets.top;
		this.mTextRect.height = this.mBounds.getHeight() - (this.mInsets.top + this.mInsets.bottom);
		return;
	}
	
	private void layoutStretchBoth()
	{
		int width = 0;
		for(int i = 0; i < this.mText.size(); i++)
		{
			width += this.mText.get(i).width;
		}
		this.mTextRect.width = width;
		this.mTextRect.height = this.mFontHeight;
		this.mBounds.resizePolygon(width + this.mInsets.left + this.mInsets.right, this.mFontHeight + this.mInsets.top + this.mInsets.bottom);
		this.mTextRect.x = this.mBounds.getLeft() + this.mInsets.left;
		this.mTextRect.y = this.mBounds.getTop() + this.mInsets.top;
		return;
	}
	
	public void setSize(int width, int height)
	{
		this.mBounds.resizePolygon(width, height);
		this.invalidate();
		return;
	}
	
	public int getWidth()
	{
		return this.mBounds.getWidth();
	}
	
	public int getHeight()
	{
		return this.mBounds.getHeight();
	}
	
	public void setLocation(int x, int y)
	{
		this.mBounds.setLocation(x, y);
		this.invalidate();
		return;
	}
	
	public Point getLocation()
	{
		return new Point(this.mBounds.getLeft(), this.mBounds.getTop());
	}
	
	public void setInsets(Insets insets)
	{
		this.mInsets = insets;
		this.invalidate();
		return;
	}
	
	/**
	 * Read-only.
	 * @return
	 */
	public Insets getInsets()
	{
		return new Insets(this.mInsets.top, this.mInsets.left, this.mInsets.bottom, this.mInsets.right);
	}
	
	public void setText(String text)
	{
		this.mText.clear();
		this.mCarat = 0;
		for(int i = 0; i < text.length(); i++)
		{
			this.enterCharacter(text.charAt(i));
		}
		return;
	}
	
	public String getText()
	{
		StringBuilder results = new StringBuilder();
		for(int i = 0; i < this.mText.size(); i++)
		{
			results.append(this.mText.get(i).code);
		}
		return results.toString();
	}
	
	/**
	 * Read-only.
	 * @return
	 */
	public Rectangle getTextBounds()
	{
		return new Rectangle(this.mTextRect.x, this.mTextRect.y, this.mTextRect.width, this.mTextRect.height);
	}
	
	public void setBackground(Color background)
	{
		this.mBackground = background;
		return;
	}
	
	public Color getBackground()
	{
		return this.mBackground;
	}
	
	public void setForeground(Color foreground)
	{
		this.mForeground = foreground;
		return;
	}
	
	public Color getForeground()
	{
		return this.mForeground;
	}
	
	/**
	 * The delay is measured on the number of times the render() method is called.  Not milliseconds or other clock time.
	 * When the render() method has been called as many times as the delay value, the rendering of the cursor 
	 * alternates between visible and not.
	 * @param delay int The number of times render() is called before alternating a visible cursor.
	 */
	public void setCursorDelay(int delay)
	{
		if(delay < 0){
			this.mCursorDelay = 0;
		}else{
			this.mCursorDelay = delay;
		}
		return;
	}
	
	public void setBorderColor(Color color)
	{
		this.mBorderColor = color;
		return;
	}
	
	public Color getBorderColor()
	{
		return this.mBorderColor;
	}
	
	public void setBorderStroke(BasicStroke stroke)
	{
		this.mBorderStroke = stroke;
		return;
	}
	
	public BasicStroke getBorderStroke()
	{
		return this.mBorderStroke;
	}
	
	public void setFont(Font font)
	{
		this.mFont = font;
		BufferedImage temp = this.mGC.createCompatibleImage(300, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		this.mFontHeight = g2d.getFontMetrics(this.mFont).getHeight();
		return;
	}
	
	public Font getFont()
	{
		return this.mFont;
	}
	
	public void setMaxCharacters(int max)
	{
		this.mMaxCharacters = max;
		return;
	}
	
	public int getMaxCharacters()
	{
		return this.mMaxCharacters;
	}
	
	/**
	 * Very useful for debugging.
	 * @param value boolean true to show text bounds rectangle, false otherwise.
	 */
	public void showTextBounds(boolean value)
	{
		this.mShowTextRect = value;
		return;
	}
	
	public boolean isTextBoundsVisible()
	{
		return this.mShowTextRect;
	}
	
	public void setStretchToFitText(PTextBox.Fit option)
	{
		this.mFitText = option;
		return;
	}
	
	public PTextBox.Fit getStretchToFit()
	{
		return this.mFitText;
	}
	
	private boolean isPrintableChar(char input)
	{
		if(Character.isSpaceChar(input)){
			return true;
		}
		if(Character.isLetterOrDigit(input)){
			return true;
		}
		
		return false;
	}
	
	private int getTotalTextWidth()
	{
		int length = 0;
		for(int i = 0; i < this.mText.size(); i++)
		{
			length += this.mText.get(i).width;
		}
		return length;
	}
	
	private void moveCaratLeft()
	{
		this.mCarat--;
		this.mCarat = this.mCarat < 0 ? 0 : this.mCarat;
		
		return;
	}
	
	private void moveCaratRight()
	{
		this.mCarat++;
		this.mCarat = this.mCarat > this.mText.size() ? this.mText.size() : this.mCarat;
	}
	
	private void resetCursorDelay()
	{
		this.mShowCursor = true;
		this.mCursorStep = 0;
		return;
	}
	
	private void performBackspace()
	{
		if(this.mCarat <= 0){
			return;
		}
		this.mText.remove(this.mCarat - 1);
		this.moveCaratLeft();
		if(this.mFitText.equals(PTextBox.Fit.BOTH) || this.mFitText.equals(PTextBox.Fit.HORIZONTAL)){
			this.invalidate();
		}
		return;
	}
	
	private void performDelete()
	{
		if(this.mCarat >= this.mText.size()){
			return;
		}
		this.mText.remove(this.mCarat);
		if(this.mFitText.equals(PTextBox.Fit.BOTH) || this.mFitText.equals(PTextBox.Fit.HORIZONTAL)){
			this.invalidate();
		}
		return;
	}
	
	private void performHome()
	{
		this.mCarat = 0;
		return;
	}
	
	private void performEnd()
	{
		this.mCarat = this.mText.size();
		return;
	}
	
	private void enterCharacter(char c)
	{
		if(!this.isPrintableChar(c)){
			return;
		}
		if(this.mMaxCharacters > 0 && this.mText.size() >= this.mMaxCharacters){
			return;
		}
		
		BufferedImage temp = this.mGC.createCompatibleImage(300, 200, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		PChar pc = new PChar(c);
		pc.width = g2d.getFontMetrics(this.mFont).stringWidth(String.valueOf(c));
		pc.height = g2d.getFontMetrics(this.mFont).getHeight();
		
		if(this.mFitText.equals(PTextBox.Fit.NONE) || this.mFitText.equals(PTextBox.Fit.VERTICAL)){
			if(this.getTotalTextWidth() + pc.width > this.mTextRect.width){
				return;
			}
		}
		this.mText.insertElementAt(pc, this.mCarat);
		this.moveCaratRight();
		if(this.mFitText.equals(PTextBox.Fit.BOTH) || this.mFitText.equals(PTextBox.Fit.HORIZONTAL)){
			this.invalidate();
		}
		return;
	}
	
	public void keyPressed(KeyEvent e)
	{
		
		return;
	}
	
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_LEFT){
			this.moveCaratLeft();
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			this.moveCaratRight();
		}else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			this.performBackspace();
		}else if(e.getKeyCode() == KeyEvent.VK_DELETE){
			this.performDelete();
		}else if(e.getKeyCode() == KeyEvent.VK_HOME){
			this.performHome();
		}else if(e.getKeyCode() == KeyEvent.VK_END){
			this.performEnd();
		}else{
			this.enterCharacter(e.getKeyChar());
		}
		this.resetCursorDelay();
		return;
	}
	
	public void keyTyped(KeyEvent e)
	{
		
		return;
	}
}
