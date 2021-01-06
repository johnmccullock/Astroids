package main.polycomp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * General class to display a single line of text.
 * 
 * Very Important to use the invalidate() method after initial configuration and after any changes have been
 * made to the class properties.
 * 
 * The invalidate() method is designed to expand the the label bounds to fit the text.  So invalidate() can be 
 * called periodically to adjust the label size when the text changes.
 * 
 * For display purposes only: this class is not hoverable or focusable.
 * 
 * @author John McCullock
 * @version 1.0 2014-11-28
 */
public class PLabel extends AbstractLabel
{
	private final Color DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 255);
	private final BasicStroke DEFAULT_BORDER_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	
	protected PolyShape mBorder = null;
	protected Color mBorderColor = DEFAULT_BORDER_COLOR;
	protected BasicStroke mBorderStroke = DEFAULT_BORDER_STROKE;
	protected int mTabIndex = 0;
	
	private Rectangle mTextBounds = new Rectangle();
	
	public PLabel()
	{
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		return;
	}
	
	public void render(Graphics2D g2d)
	{
		this.renderBackground(g2d);
		this.renderFrame(g2d);
		this.renderCaption(g2d);
		return;
	}
	
	public void renderBackground(Graphics2D g2d)
	{
		if(this.mBackground != null && this.mBorder != null){
			g2d.setPaint(this.mBackground);
			g2d.fillPolygon(this.mBorder.getPolygon());
		}
		return;
	}
	
	private void renderFrame(Graphics2D g2d)
	{
		if(this.mBorder != null && this.mBorderColor != null){
			g2d.setPaint(this.mBorderColor);
			g2d.setStroke(this.mBorderStroke);
			g2d.drawPolygon(this.mBorder.getPolygon());
		}
		return;
	}
	
	public void renderCaption(Graphics2D g2d)
	{
		if(this.mForeground != null){
			g2d.setPaint(this.mForeground);
		}
		if(this.mText != null && !this.mText.isEmpty() && this.mFont != null){
			g2d.setFont(this.mFont);
			g2d.drawString(this.mText, this.mBounds.x + this.mTextBounds.x, this.mBounds.y + this.mTextBounds.y);
		}
		return;
	}
	
	/**
	 * Very important to use after every property change.  This keeps the PLabel component's properties current.
	 * This method should also be used after initial configuration. 
	 */
	public void invalidate()
	{
		this.mBorder.setLocation(this.mBounds.x, this.mBounds.y);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage temp = gc.createCompatibleImage(200, 200);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		g2d.setFont(this.mFont);
		this.mTextBounds.width = g2d.getFontMetrics().stringWidth(this.mText);
		this.mTextBounds.height = g2d.getFontMetrics().getHeight();
		
		int strokeWidth = this.mBorderStroke != null ? (int)Math.round(this.mBorderStroke.getLineWidth()) : 0;
		int top = this.mInsets != null ? this.mInsets.top : 0;
		int left = this.mInsets != null ? this.mInsets.left : 0;
		int bottom = this.mInsets != null ? this.mInsets.bottom : 0;
		int right = this.mInsets != null ? this.mInsets.right : 0;
		
		this.mBounds.width = this.mTextBounds.width + 
								left + 
								right + 
								strokeWidth;
		this.mBounds.height = this.mTextBounds.height +
								top +
								bottom + 
								strokeWidth;
		
		this.mBorder.resizePolygon(this.mBounds.width, this.mBounds.height);
		
		
		this.mTextBounds.y = this.mTextBounds.height + top - g2d.getFontMetrics().getDescent();
		if(this.mAlignment.equals(PAlign.LEFT)){
			this.mTextBounds.x = left;
		}else if(this.mAlignment.equals(PAlign.CENTER)){
			this.mTextBounds.x = (int)Math.round((this.mBounds.width - this.mTextBounds.width) / 2.0);
		}else{
			this.mTextBounds.x = this.mBounds.width - right - this.mTextBounds.width;
		}
		
		return;
	}
	
	/**
	 * The setSize method won't do anything because the PLabel is auto-sized with the invalidate method.
	 */
	public void setSize(int width, int height)
	{
		return;
	}
	
	public void setBorder(PolyShape border)
	{
		this.mBorder = border;
		return;
	}
	
	public PolyShape getBorder()
	{
		return this.mBorder;
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
	
	public void setTabIndex(int index)
	{
		this.mTabIndex = index;
		return;
	}
	
	public int getTabIndex()
	{
		return this.mTabIndex;
	}
}
