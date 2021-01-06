package main.polycomp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * A general purpose, polygon-based scroll bar component.  PScrollBar2 is a different design and 
 * implementation that PScrollBar.
 * 
 * @author John McCullock
 * @version 1.0 2014-11-14
 */
public class PScrollBar implements PHoverable, PSelectable
{
	public static enum Orientation{VERTICAL, HORIZONTAL};
	
	private PScrollBar.Orientation mOrientation = PScrollBar.Orientation.VERTICAL;
	private Polygon mTrackTemplate = null;
	private Polygon mDecrementButtonTemplate = null;
	private Polygon mIncrementButtonTemplate = null;
	private Polygon mGripTemplate = null;
	private PolyShape mTrack = null;
	private PolyShape mDecrementButton = null;
	private PolyShape mIncrementButton = null;
	private Rectangle mDecrementBounds = new Rectangle();
	private Rectangle mIncrementBounds = new Rectangle();
	private PolyShape mGrip = null;
	private double mScrollValue = 0.0;
	private int mMouseX = 0;
	private int mMouseY = 0;
	private Rectangle mBounds = new Rectangle();
	private Color mNormalTrackBackground = new Color(60, 60, 60, 255);
	private Color mNormalTrackBorder = new Color(0, 0, 0, 255);
	private BasicStroke mNormalTrackStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color mNormalGripColor = new Color(0, 0, 0, 255);
	private Color mNormalDecrementColor = new Color(90, 90, 90, 255);
	private Color mNormalIncrementColor = new Color(90, 90, 90, 255);
	private Color mHoveredTrackBackground = new Color(90, 90, 90, 255);
	private Color mHoveredTrackBorder = new Color(128, 128, 128, 255);
	private BasicStroke mHoveredTrackStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color mHoveredGripColor = new Color(128, 128, 128, 255);
	private Color mHoveredDecrementColor = new Color(255, 255, 0, 255);
	private Color mHoveredIncrementColor = new Color(112, 112, 112, 255);
	private Color mSelectedTrackBackground = new Color(128, 128, 128, 255);
	private Color mSelectedTrackBorder = new Color(192, 192, 192, 255);
	private BasicStroke mSelectedTrackStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color mSelectedGripColor = new Color(192, 192, 192, 255);
	private Color mSelectedDecrementColor = new Color(60, 60, 60, 255);
	private Color mSelectedIncrementColor = new Color(60, 60, 60, 255);
	private int mMaxStrokeWidth = 0;
	private boolean mComponentIsHovered = false;
	private boolean mComponentIsSelected = false;
	private boolean mIsVisible = true;
	private int mTabIndex = 0;
	
	public PScrollBar(PScrollBar.Orientation orientation)
	{
		this.mOrientation = orientation;
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		return;
	}
	
	public void render(Graphics2D g2d)
	{
		this.renderTrackBackground(g2d);
		this.renderTrackBorder(g2d);
		this.renderDecrementBackground(g2d);
		this.renderDecrementButtonBorder(g2d);
		if(this.mOrientation.equals(PScrollBar.Orientation.VERTICAL)){
			this.renderIncrementBackgroundVertical(g2d);
			this.renderIncrementBorderVertical(g2d);
			this.renderGripBackgroundVertical(g2d);
			this.renderGripBorderVertical(g2d);
		}else{
			this.renderIncrementBackgroundHorizontal(g2d);
			this.renderIncrementBorderHorizontal(g2d);
			this.renderGripBackgroundHorizontal(g2d);
			this.renderGripBorderHorizontal(g2d);
		}
		return;
	}
	
	private void renderTrackBackground(Graphics2D g2d)
	{
		// If the expected background color is null, the method should simply return.
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBackground == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBackground);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBackground == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBackground);
		}else{
			if(this.mNormalTrackBackground == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBackground);
		}
		
		g2d.fillPolygon(this.mTrack.getPolygon());
		return;
	}
	
	private void renderTrackBorder(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBorder);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBackground == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBorder);
		}else{
			if(this.mNormalTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBorder);
		}
		g2d.drawPolygon(this.mTrack.getPolygon());
		return;
	}
	
	private void renderDecrementBackground(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedDecrementColor == null){
				return;
			}
			g2d.setPaint(this.mSelectedDecrementColor);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredDecrementColor == null){
				return;
			}
			g2d.setPaint(this.mHoveredDecrementColor);
		}else{
			if(this.mNormalDecrementColor == null){
				return;
			}
			g2d.setPaint(this.mNormalDecrementColor);
		}
		g2d.fillPolygon(this.mDecrementButton.getPolygon());
		return;
	}
	
	private void renderDecrementButtonBorder(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBorder);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBorder);
		}else{
			if(this.mNormalTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBorder);
		}
		g2d.drawPolygon(this.mDecrementButton.getPolygon());
		return;
	}
	
	private void renderIncrementBackgroundVertical(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedIncrementColor == null){
				return;
			}
			g2d.setPaint(this.mSelectedIncrementColor);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredIncrementColor == null){
				return;
			}
			g2d.setPaint(this.mHoveredIncrementColor);
		}else{
			if(this.mNormalIncrementColor == null){
				return;
			}
			g2d.setPaint(this.mNormalIncrementColor);
		}
		g2d.fillPolygon(this.mIncrementButton.getPolygon());
		return;
	}
	
	private void renderIncrementBorderVertical(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBorder);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBorder);
		}else{
			if(this.mNormalTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBorder);
		}
		g2d.drawPolygon(this.mIncrementButton.getPolygon());
		return;
	}
	
	private void renderIncrementBackgroundHorizontal(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedIncrementColor == null){
				return;
			}
			g2d.setPaint(this.mSelectedIncrementColor);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredIncrementColor == null){
				return;
			}
			g2d.setPaint(this.mHoveredIncrementColor);
		}else{
			if(this.mNormalIncrementColor == null){
				return;
			}
			g2d.setPaint(this.mNormalIncrementColor);
		}
		g2d.fillPolygon(this.mIncrementButton.getPolygon());
		return;
	}
	
	private void renderIncrementBorderHorizontal(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBorder);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBorder);
		}else{
			if(this.mNormalTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBorder);
		}
		g2d.drawPolygon(this.mIncrementButton.getPolygon());
		return;
	}
	
	private void renderGripBackgroundVertical(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedGripColor == null){
				return;
			}
			g2d.setPaint(this.mSelectedGripColor);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredGripColor == null){
				return;
			}
			g2d.setPaint(this.mHoveredGripColor);
		}else{
			if(this.mNormalGripColor == null){
				return;
			}
			g2d.setPaint(this.mNormalGripColor);
		}
		g2d.fillPolygon(this.mGrip.getPolygon());
		return;
	}
	
	private void renderGripBorderVertical(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBorder);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBorder);
		}else{
			if(this.mNormalTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBorder);
		}
		g2d.drawPolygon(this.mGrip.getPolygon());
		return;
	}
	
	private void renderGripBackgroundHorizontal(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedGripColor == null){
				return;
			}
			g2d.setPaint(this.mSelectedGripColor);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredGripColor == null){
				return;
			}
			g2d.setPaint(this.mHoveredGripColor);
		}else{
			if(this.mNormalGripColor == null){
				return;
			}
			g2d.setPaint(this.mNormalGripColor);
		}
		g2d.fillPolygon(this.mGrip.getPolygon());
		return;
	}
	
	private void renderGripBorderHorizontal(Graphics2D g2d)
	{
		if(this.mComponentIsSelected){
			if(this.mSelectedTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mSelectedTrackBorder);
		}else if(this.mComponentIsHovered){
			if(this.mHoveredTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mHoveredTrackBorder);
		}else{
			if(this.mNormalTrackBorder == null){
				return;
			}
			g2d.setPaint(this.mNormalTrackBorder);
		}
		g2d.drawPolygon(this.mGrip.getPolygon());
		return;
	}
	
	public void gripPerformed(int x, int y)
	{
		this.mMouseX = x;
		this.mMouseY = y;
		return;
	}
	
	public double scroll(final int amount)
	{
		double value = 0.0;
		if(this.mOrientation.equals(PScrollBar.Orientation.VERTICAL)){
			value = this.verticalScroll(amount);
		}else if(this.mOrientation.equals(PScrollBar.Orientation.HORIZONTAL)){
			value = this.horizontalScroll(amount);
		}
		value = value < 0.0 ? 0.0 : value;
		value = value > 1.0 ? 1.0 : value;
		return value;
	}
	
	public double smallVerticalScroll(int amount)
	{
		if(this.mGrip.getTop() + (amount * 5) < this.mDecrementButton.getBottom()){
			this.mGrip.setLocation(this.mBounds.x, this.mDecrementButton.getBottom());
		}else if(this.mGrip.getBottom() + (amount * 5) > this.mIncrementButton.getTop()){
			this.mGrip.setLocation(this.mBounds.x, this.mIncrementButton.getTop() - this.mGrip.getHeight());
		}else{
			this.mGrip.setLocation(this.mBounds.x, this.mGrip.getTop() + (amount * 5));
		}
		return this.mScrollValue = this.calculateVerticalScrollValue();
	}
	
	private double verticalScroll(final int y)
	{
		int delta = y - this.mMouseY;
		
		if(this.mGrip.getTop() + delta < this.mDecrementButton.getBottom()){
			this.mGrip.setLocation(this.mBounds.x,  this.mDecrementButton.getBottom());
		}else if(this.mGrip.getTop() + this.mGrip.getHeight() > this.mBounds.y + this.mBounds.height - this.mIncrementButton.getHeight()){
			this.mGrip.setLocation(this.mBounds.x,  this.mBounds.y + this.mBounds.height - this.mIncrementButton.getHeight() - this.mGrip.getHeight());
		}else{
			this.mGrip.setLocation(this.mBounds.x,  this.mGrip.getTop() + delta);
		}
		
		this.mMouseY += delta;
		
		return this.mScrollValue = this.calculateVerticalScrollValue();
	}
	
	// Unfinished - grip location has to be properly calculated.
	private double horizontalScroll(final int x)
	{
		int delta = x - this.mMouseX;
		
		this.mMouseX += delta;
		
		return this.mScrollValue = this.calculateHorizontalScrollValue();
	}
	
	private double calculateVerticalScrollValue()
	{
		double full = (this.mBounds.y + this.mBounds.height - this.mIncrementButton.getHeight()) - 
						(this.mBounds.y + this.mDecrementButton.getHeight() + this.mGrip.getHeight());
		//System.out.println("grip top: " + this.mGrip.getTop() + ", track top: " + this.mBounds.y + ", dec height: " + this.mDecrementButton.getHeight() + ", full: " + full);
		//System.out.println((this.mGrip.getTop() - (this.mBounds.y + this.mDecrementButton.getHeight())) / full);
		return this.mScrollValue = (this.mGrip.getTop() - (this.mBounds.y + this.mDecrementButton.getHeight())) / full;
	}
	
	private double calculateHorizontalScrollValue()
	{
		double full = (this.mTrack.getRight() - this.mIncrementButton.getWidth()) - 
						(this.mTrack.getLeft() + this.mDecrementButton.getWidth() + this.mGrip.getWidth());
		return this.mScrollValue = (this.mGrip.getLeft() - (this.mTrack.getLeft() + this.mDecrementButton.getWidth())) / full;
	}
	
	public double getScrollValue()
	{
		return this.mScrollValue;
	}
	
	public void invalidate()
	{
		if(this.mOrientation.equals(PScrollBar.Orientation.VERTICAL)){
			this.layoutVerticalScrollBar(this.mBounds.width, this.mBounds.height);
		}else{
			this.layoutHorizontalScrollBar(this.mBounds.width, this.mBounds.height);
		}
		this.mMaxStrokeWidth = this.getMaxStrokeWidth();
		return;
	}
	
	private void layoutVerticalScrollBar(final int width, final int height)
	{
		if(this.mTrack == null){
			this.mTrack = new PolyShape(this.mTrackTemplate.xpoints, this.mTrackTemplate.ypoints, this.mTrackTemplate.npoints);
			this.mDecrementButton = new PolyShape(this.mDecrementButtonTemplate.xpoints, this.mDecrementButtonTemplate.ypoints, this.mDecrementButtonTemplate.npoints);
			this.mIncrementButton = new PolyShape(this.mIncrementButtonTemplate.xpoints, this.mIncrementButtonTemplate.ypoints, this.mIncrementButtonTemplate.npoints);
			this.mGrip = new PolyShape(this.mGripTemplate.xpoints, this.mGripTemplate.ypoints, this.mGripTemplate.npoints);
		}
		this.mTrack.resizePolygon(width, height);
		this.mTrack.setLocation(this.mBounds.x, this.mBounds.y);
		this.mDecrementButton.resizePolygon(width, width);
		this.mDecrementButton.setLocation(this.mBounds.x, this.mBounds.y);
		this.mDecrementBounds = new Rectangle(this.mBounds.x, this.mBounds.y, width, width);
		this.mIncrementButton.resizePolygon(width, width);
		this.mIncrementButton.setLocation(this.mBounds.x, this.mBounds.y + this.mBounds.height - this.mDecrementButton.getHeight());
		this.mIncrementBounds = new Rectangle(this.mBounds.x, this.mBounds.y + this.mBounds.height - this.mDecrementButton.getHeight(), width, width);
		this.mGrip.resizePolygon(width, (int)Math.round(this.mBounds.height / 3f));
		this.mGrip.setLocation(this.mBounds.x, this.mTrack.getTop() + this.mDecrementButton.getHeight());
		return;
	}
	
	private void layoutHorizontalScrollBar(final int width, final int height)
	{
		if(this.mTrack == null){
			this.mTrack = new PolyShape(this.mTrackTemplate.xpoints, this.mTrackTemplate.ypoints, this.mTrackTemplate.npoints);
			this.mDecrementButton = new PolyShape(this.mDecrementButtonTemplate.xpoints, this.mDecrementButtonTemplate.ypoints, this.mDecrementButtonTemplate.npoints);
			this.mIncrementButton = new PolyShape(this.mIncrementButtonTemplate.xpoints, this.mIncrementButtonTemplate.ypoints, this.mIncrementButtonTemplate.npoints);
			this.mGrip = new PolyShape(this.mGripTemplate.xpoints, this.mGripTemplate.ypoints, this.mGripTemplate.npoints);
		}
		this.mTrack.resizePolygon(width, height);
		this.mDecrementButton.resizePolygon(height, height);
		this.mIncrementButton.resizePolygon(height, height);
		this.mGrip.resizePolygon(this.mBounds.width, height);
		return;
	}
	
	public void setTrackTemplate(Polygon template)
	{
		this.mTrackTemplate = template;
		return;
	}
	
	public Polygon getTrackTemplate()
	{
		return this.mTrackTemplate;
	}
	
	public void setDecrementButtonTemplate(Polygon template)
	{
		this.mDecrementButtonTemplate = template;
		return;
	}
	
	public Polygon getDecrementButtonTemplate()
	{
		return this.mDecrementButtonTemplate;
	}
	
	public void setIncrementButtonTemplate(Polygon template)
	{
		this.mIncrementButtonTemplate = template;
		return;
	}
	
	public Polygon getIncrementButtonTemplate()
	{
		return this.mIncrementButtonTemplate;
	}
	
	public void setGripTemplate(Polygon template)
	{
		this.mGripTemplate = template;
		return;
	}
	
	public Polygon getGripTemplate()
	{
		return this.mGripTemplate;
	}
	
	public Rectangle getBounds()
	{
		return this.mBounds;
	}
	
	public PolyShape getTrack()
	{
		return this.mTrack;
	}
	
	public PolyShape getDecrementButton()
	{
		return this.mDecrementButton;
	}
	
	public PolyShape getIncrementButton()
	{
		return this.mIncrementButton;
	}
	
	public Rectangle getDecrementButtonBounds()
	{
		return this.mDecrementBounds;
	}
	
	public Rectangle getIncrementButtonBounds()
	{
		return this.mIncrementBounds;
	}
	
	public PolyShape getGrip()
	{
		return this.mGrip;
	}
	
	public void setSize(int width, int height)
	{
		this.mBounds.width = width;
		this.mBounds.height = height;
		this.invalidate();
		return;
	}
	
	public Dimension getSize()
	{
		return new Dimension(this.mBounds.width, this.mBounds.height);
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
	
	private int getMaxStrokeWidth()
	{
		double value = 0;
		value = Math.max(this.mNormalTrackStroke.getLineWidth(), this.mHoveredTrackStroke.getLineWidth());
		value = Math.max(value, this.mSelectedTrackStroke.getLineWidth());
		return (int)Math.ceil(value);
	}
	
	public void setNormalTrackBackground(Color color)
	{
		this.mNormalTrackBackground = color;
		return;
	}
	
	public Color getNormalTrackBackground()
	{
		return this.mNormalTrackBackground;
	}
	
	public void setNormalTrackBorder(Color color)
	{
		this.mNormalTrackBorder = color;
		return;
	}
	
	public Color getNormalTrackBorder()
	{
		return this.mNormalTrackBorder;
	}
	
	public void setNormalTrackStroke(final BasicStroke stroke)
	{
		this.mNormalTrackStroke = stroke;
		return;
	}
	
	public BasicStroke getNormalTrackStroke()
	{
		return this.mNormalTrackStroke;
	}
	
	public void setNormalGripColor(final Color color)
	{
		this.mNormalGripColor = color;
		return;
	}
	
	public Color getNormalGripColor()
	{
		return this.mNormalGripColor;
	}
	
	public void setNormalDecrementColor(Color color)
	{
		this.mNormalDecrementColor = color;
		return;
	}
	
	public Color getNormalDecrementColor()
	{
		return this.mNormalDecrementColor;
	}
	
	public void setNormalIncrementColor(Color color)
	{
		this.mNormalIncrementColor = color;
		return;
	}
	
	public Color getNormalIncrementColor()
	{
		return this.mNormalIncrementColor;
	}
	
	public void setHoveredTrackBackground(final Color color)
	{
		this.mHoveredTrackBackground = color;
		return;
	}
	
	public Color getHoveredTrackBackground()
	{
		return this.mHoveredTrackBackground;
	}
	
	public void setHoveredTrackBorder(final Color color)
	{
		this.mHoveredTrackBorder = color;
		return;
	}
	
	public Color getHoveredTrackBorder()
	{
		return this.mHoveredTrackBorder;
	}
	
	public void setHoveredTrackStroke(final BasicStroke stroke)
	{
		this.mHoveredTrackStroke = stroke;
		return;
	}
	
	public BasicStroke getHoveredTrackStroke()
	{
		return this.mHoveredTrackStroke;
	}
	
	public void setHoveredGripColor(final Color color)
	{
		this.mHoveredGripColor = color;
		return;
	}
	
	public Color getHoveredGripColor()
	{
		return this.mHoveredGripColor;
	}
	
	public void setHoveredDecrementColor(Color color)
	{
		this.mHoveredDecrementColor = color;
		return;
	}
	
	public Color getHoveredDecrementColor()
	{
		return this.mHoveredDecrementColor;
	}
	
	public void setHoveredIncrementColor(Color color)
	{
		this.mHoveredIncrementColor = color;
		return;
	}
	
	public Color getHoveredIncrementColor()
	{
		return this.mHoveredIncrementColor;
	}
	
	public void setSelectedTrackBackground(final Color color)
	{
		this.mSelectedTrackBackground = color;
		return;
	}
	
	public Color getSelectedTrackBackground()
	{
		return this.mSelectedTrackBackground;
	}
	
	public void setSelectedTrackBorder(final Color color)
	{
		this.mSelectedTrackBorder = color;
		return;
	}
	
	public Color getSelectedTrackBorder()
	{
		return this.mSelectedTrackBorder;
	}
	
	public void setSelectedTrackStroke(final BasicStroke stroke)
	{
		this.mSelectedTrackStroke = stroke;
		return;
	}
	
	public BasicStroke getSelectedTrackStroke()
	{
		return this.mSelectedTrackStroke;
	}
	
	public void setSelectedGripColor(final Color color)
	{
		this.mSelectedGripColor = color;
		return;
	}
	
	public Color getSelectedGripColor()
	{
		return this.mSelectedGripColor;
	}
	
	public void setSelectedDecrementColor(Color color)
	{
		this.mSelectedDecrementColor = color;
		return;
	}
	
	public Color getSelectedDecrementColor()
	{
		return this.mSelectedDecrementColor;
	}
	
	public void setSelectedIncrementColor(Color color)
	{
		this.mSelectedIncrementColor = color;
		return;
	}
	
	public Color getSelectedIncrementColor(Color color)
	{
		return this.mSelectedIncrementColor;
	}
	
	public void setComponentSelected(boolean state)
	{
		this.mComponentIsSelected = state;
		return;
	}
	
	public boolean componentIsSelected()
	{
		return this.mComponentIsSelected;
	}
	
	public void setComponentHovered(boolean state)
	{
		this.mComponentIsHovered = state;
		return;
	}
	
	public boolean componentIsHovered()
	{
		return this.mComponentIsHovered;
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
