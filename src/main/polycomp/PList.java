package main.polycomp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Vector;

/**
 * A general vertical list display component, consisting of a series of cells.
 *  
 *  Very important to use the invalidate() method after initial configuration and after every change made to the
 *  PList.
 *  
 *  Version 1.0.2 Bug fix: list background rendering in wrong location.
 *  
 *  Version 1.0.1 bug fix: last cell in the list wasn't being drawn in renderCells() method.
 *  
 * @author John McCullock
 * @version 1.0.2 2015-07-31
 */
public class PList implements PHoverable, PSelectable
{
	private Rectangle mBounds = new Rectangle();
	private Dimension mListSize = new Dimension();
	private Polygon mCellTemplate = null;
	private PAbstractListModel mModel = null;
	private int mVisibleItems = 10;
	private Vector<PItem> mItems = null;
	private Dimension mItemSize = new Dimension();
	private int mItemSpacing = 0;
	private Color mListBackground = new Color(0, 0, 0, 255);
	private Color mNormalCellBackground = new Color(255, 255, 255, 0);
	private Color mNormalCellBorder = new Color(0, 0, 0, 255);
	private BasicStroke mNormalCellStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color mNormalCellText = new Color(0, 0, 0, 255);
	private Font mNormalCellFont = new Font("Arial", Font.PLAIN, 16);
	private Color mHoveredCellBackground = new Color(255, 255, 255, 0);
	private Color mHoveredCellBorder = new Color(0, 0, 0, 255);
	private BasicStroke mHoveredCellStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color mHoveredCellText = new Color(0, 0, 0, 255);
	private Font mHoveredCellFont = new Font("Arial", Font.PLAIN, 16);
	private Color mSelectedCellBackground = new Color(0, 0, 0, 255);
	private Color mSelectedCellBorder = new Color(255, 255, 255, 255);
	private BasicStroke mSelectedCellStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	private Color mSelectedCellText = new Color(255, 255, 255, 255);
	private Font mSelectedCellFont = new Font("Arial", Font.PLAIN, 16);
	private int mMaxStrokeWidth = 0;
	private PAlign mCellAlignment = PAlign.LEFT;
	private Vector<Integer> mSelectedRows = new Vector<Integer>();
	private boolean mComponentIsSelected = false;
	private int mRowHovered = -1;
	private boolean mComponentIsHovered = false;
	private int mPointer = 0;
	private int mFirstDrawn = 0;
	private int mLastDrawn = 0;
	
	public PList()
	{
		this.initializeMain();
		return;
	}
	
	private void initializeMain()
	{
		return;
	}
	
	public void render(Graphics2D g2d)
	{
		this.renderListArea(g2d);
		this.renderCells(g2d, this.mFirstDrawn, this.mLastDrawn);
		
		//Testing:
		//g2d.setPaint(Color.WHITE);
		//g2d.drawRect(this.mBounds.x, this.mBounds.y, this.mBounds.width, this.mBounds.height);
		return;
	}
	
	private void renderListArea(Graphics2D g2d)
	{
		if(this.mListBackground != null){
			g2d.setPaint(this.mListBackground);
			g2d.fillRect(this.mBounds.x, this.mBounds.y, this.mBounds.width, this.mBounds.height);
		}
		
		return;
	}
	
	private void getFirstToBeDrawn()
	{
		int first = (int)Math.floor(this.mPointer / (this.mItemSize.height + this.mItemSpacing));
		if(first < 0){
			first = 0;
		}
		this.mFirstDrawn = first;
		return;
	}
	
	private void getLastToBeDrawn()
	{
		int last = this.mFirstDrawn + (this.mVisibleItems);
		last = last > this.mItems.size() - 1 ? last = this.mItems.size() - 1 : last;
		this.mLastDrawn = last;
		return;
	}
	
	private void renderCells(Graphics2D g2d, int first, int last)
	{
		for(int i = first; i <= last; i++)
		{
			if(!this.mBounds.contains(this.mItems.get(i).getLeft(), this.mItems.get(i).getTop())){
				continue;
			}
			
			if(this.mItems.get(i).componentIsSelected()){
				g2d.setPaint(this.mSelectedCellBackground);
				g2d.fillPolygon(this.mItems.get(i).getPolygon());
				g2d.setPaint(this.mSelectedCellBorder);
				g2d.setStroke(this.mSelectedCellStroke);
				g2d.drawPolygon(this.mItems.get(i).getPolygon());
				g2d.setPaint(this.mSelectedCellText);
				this.drawCellContents(g2d, this.mItems.get(i), this.mSelectedCellFont, this.mItems.get(i).getContents());
			}else if(this.mItems.get(i).componentIsHovered()){
				g2d.setPaint(this.mHoveredCellBackground);
				g2d.fillPolygon(this.mItems.get(i).getPolygon());
				g2d.setPaint(this.mHoveredCellBorder);
				g2d.setStroke(this.mHoveredCellStroke);
				g2d.drawPolygon(this.mItems.get(i).getPolygon());
				g2d.setPaint(this.mHoveredCellText);
				this.drawCellContents(g2d, this.mItems.get(i), this.mNormalCellFont, this.mItems.get(i).getContents());
			}else{
				g2d.setPaint(this.mNormalCellBackground);
				g2d.fillPolygon(this.mItems.get(i).getPolygon());
				g2d.setPaint(this.mNormalCellBorder);
				g2d.setStroke(this.mNormalCellStroke);
				g2d.drawPolygon(this.mItems.get(i).getPolygon());
				g2d.setPaint(this.mNormalCellText);
				this.drawCellContents(g2d, this.mItems.get(i), this.mNormalCellFont, this.mItems.get(i).getContents());
			}
		}
		return;
	}

	private void drawCellContents(Graphics2D g2d, PolyShape poly, Font font, Object contents)
	{
		int width = ContentToolkit.getContentWidth(g2d, font, contents);
		int height = ContentToolkit.getContentHeight(g2d, font, contents);
		int x = 0;
		int y = 0;
		if(this.mCellAlignment.equals(PAlign.LEFT)){
			x = poly.getLeft();
		}else if(this.mCellAlignment.equals(PAlign.CENTER)){
			x = poly.getLeft() + (int)Math.round((poly.getWidth() - width) / 2.0);
		}else if(this.mCellTemplate.equals(PAlign.RIGHT)){
			x = poly.getLeft() + poly.getWidth() - width;
		}
		y = poly.getTop() + height - (int)Math.round((poly.getHeight() - height) / 2.0);
		g2d.setFont(font);
		if(contents instanceof String){
			g2d.drawString((String)contents, x, y);
		}else if(contents instanceof Integer){
			g2d.drawString(String.valueOf((Integer)contents), x, y);
		}
		return;
	}
	
	public int getRowForPoint(int x, int y, int scrolled)
	{
		int firstVisibleRow = (int)Math.floor(scrolled / (this.mItemSize.height + this.mItemSpacing));
		int row = (int)Math.floor(y / (this.mItemSize.height + this.mItemSpacing)) + firstVisibleRow;
		if(row < 0 || row > this.mItems.size() - 1){
			row = -1;
		}
		return row;
	}
	
	public void invalidate()
	{
		if(this.mItems == null || this.mItems.isEmpty()){
			this.initializeItems();
		}
		this.layoutVisibleList();
		this.mListSize.width = this.mItemSize.width;
		this.mListSize.height = this.mItems.size() * (this.mItemSize.height + this.mItemSpacing);
		this.mBounds.height = this.mVisibleItems * (this.mItemSize.height + this.mItemSpacing);
		this.mMaxStrokeWidth = this.getMaxStrokeWidth();
		this.mFirstDrawn = 0;
		if(this.mItems.size() - 1 <= this.mVisibleItems){
			this.mLastDrawn = this.mItems.size() - 1;
		}else{
			this.mLastDrawn = this.mVisibleItems;
		}
		return;
	}
	
	private void initializeItems()
	{
		if(this.mModel == null){
			return;
		}
		
		this.mItems = new Vector<PItem>();
		for(int i = 0; i < this.mModel.getItemCount(); i++)
		{
			PolyShape p = new PolyShape(this.mCellTemplate.xpoints, this.mCellTemplate.ypoints, this.mCellTemplate.npoints);
			p.setLocation(this.mBounds.x, this.mBounds.y + (i * (this.mItemSize.height + this.mItemSpacing)));
			p.resizePolygon(this.mItemSize.width, this.mItemSize.height);
			this.mItems.add(new PItem(p.getPolygon(), this.mModel.getItemAt(i)));
		}
		
		return;
	}
	
	public void layoutVisibleList()
	{
		if(this.mModel == null){
			return;
		}
		for(int i = this.mFirstDrawn, j = 0; i <= this.mLastDrawn; i++, j++)
		{
			this.mItems.get(i).setLocation(this.mBounds.x, this.mBounds.y + (j * (this.mItemSize.height + this.mItemSpacing)));
		}
		return;
	}
	
	public void setCellTemplate(Polygon template)
	{
		this.mCellTemplate = template;
		return;
	}
	
	public Polygon getCellTemplate()
	{
		return this.mCellTemplate;
	}
	
	public void setModel(PAbstractListModel model)
	{
		this.mModel = model;
		return;
	}
	
	public PAbstractListModel getModel()
	{
		return this.mModel;
	}
	
	public void setLocation(int x, int y)
	{
		this.mBounds.x = x;
		this.mBounds.y = y;
		return;
	}
	
	public int getLocationX()
	{
		return this.mBounds.x;
	}
	
	public int getLocationY()
	{
		return this.mBounds.y;
	}
	
	public void setSize(int width, int height)
	{
		this.mBounds.width = width;
		this.mBounds.height = height;
		return;
	}
	
	public Rectangle getBounds()
	{
		return this.mBounds;
	}
	
	public Dimension getListSize()
	{
		return this.mListSize;
	}
	
	public Vector<PItem> getItems()
	{
		return this.mItems;
	}
	
	public void setVisibleItems(int visible)
	{
		this.mVisibleItems = visible;
		return;
	}
	
	public void setItemSize(int width, int height)
	{
		this.mItemSize.width = width;
		this.mItemSize.height = height;
		return;
	}
	
	public int getItemWidth()
	{
		return this.mItemSize.width;
	}
	
	public int getItemHeight()
	{
		return this.mItemSize.height;
	}
	
	public void setItemSpacing(int value)
	{
		this.mItemSpacing = value;
		return;
	}
	
	public int getItemSpacing()
	{
		return this.mItemSpacing;
	}
	
	public void setListBackground(Color color)
	{
		this.mListBackground = color;
		return;
	}
	
	public Color getListBackground()
	{
		return this.mListBackground;
	}
	
	public void setNormalCellBackground(Color color)
	{
		this.mNormalCellBackground = color;
		return;
	}
	
	public Color getNormalCellBackground()
	{
		return this.mNormalCellBackground;
	}
	
	public void setNormalCellBorder(Color color)
	{
		this.mNormalCellBorder = color;
		return;
	}
	
	public Color getNormalCellBorder()
	{
		return this.mNormalCellBorder;
	}
	
	public void setNormalCellStroke(BasicStroke stroke)
	{
		this.mNormalCellStroke = stroke;
		return;
	}
	
	public BasicStroke getNormalCellStroke()
	{
		return this.mNormalCellStroke;
	}
	
	public void setNormalCellText(Color color)
	{
		this.mNormalCellText = color;
		return;
	}
	
	public Color getNormalCellText()
	{
		return this.mNormalCellText;
	}
	
	public void setNormalCellFont(Font aFont)
	{
		this.mNormalCellFont = aFont;
		return;
	}
	
	public Font getNormalCellFont()
	{
		return this.mNormalCellFont;
	}
	
	public void setHoveredCellBackground(Color color)
	{
		this.mHoveredCellBackground = color;
		return;
	}
	
	public Color getHoveredCellBackground()
	{
		return this.mHoveredCellBackground;
	}
	
	public void setHoveredCellBorder(Color color)
	{
		this.mHoveredCellBorder = color;
		return;
	}
	
	public Color getHoveredCellBorder()
	{
		return this.mHoveredCellBorder;
	}
	
	public void setHoveredCellStroke(BasicStroke stroke)
	{
		this.mHoveredCellStroke = stroke;
		return;
	}
	
	public BasicStroke getHoveredCellStroke()
	{
		return this.mHoveredCellStroke;
	}
	
	public void setHoveredCellText(Color color)
	{
		this.mHoveredCellText = color;
		return;
	}
	
	public Color getHoveredCellText()
	{
		return this.mHoveredCellText;
	}
	
	public void setHoveredCellFont(Font aFont)
	{
		this.mHoveredCellFont = aFont;
		return;
	}
	
	public Font getHoveredCellFont()
	{
		return this.mHoveredCellFont;
	}
	
	public void setSelectedCellBackground(Color color)
	{
		this.mSelectedCellBackground = color;
		return;
	}
	
	public Color getSelectedCellBackground()
	{
		return this.mSelectedCellBackground;
	}
	
	public void setSelectedCellBorder(Color color)
	{
		this.mSelectedCellBorder = color;
		return;
	}
	
	public Color getSelectedCellBorder()
	{
		return this.mSelectedCellBorder;
	}
	
	public void setSelectedCellStroke(BasicStroke stroke)
	{
		this.mSelectedCellStroke = stroke;
		return;
	}
	
	public BasicStroke getSelectedCellStroke()
	{
		return this.mSelectedCellStroke;
	}
	
	public void setSelectedCellText(Color color)
	{
		this.mSelectedCellText = color;
		return;
	}
	
	public Color getSelectedCellText()
	{
		return this.mSelectedCellText;
	}
	
	public void setSelectedCellFont(Font aFont)
	{
		this.mSelectedCellFont = aFont;
		return;
	}
	
	public Font getSelectedCellFont()
	{
		return this.mSelectedCellFont;
	}
	
	public void setCellAlignment(PAlign alignment)
	{
		this.mCellAlignment = alignment;
		return;
	}
	
	public PAlign getCellAlignment()
	{
		return this.mCellAlignment;
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
	
	public void setRowHovered(int index)
	{
		this.mRowHovered = index;
		return;
	}
	
	public int getRowHovered()
	{
		return this.mRowHovered;
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
	
	private int getMaxStrokeWidth()
	{
		double value = 0;
		value = Math.max(this.mNormalCellStroke.getLineWidth(), this.mHoveredCellStroke.getLineWidth());
		value = Math.max(value, this.mSelectedCellStroke.getLineWidth());
		return (int)Math.ceil(value);
	}
	
	public boolean isSelected(int target)
	{
		boolean result = false;
		for(int value : this.mSelectedRows)
		{
			if(target == value){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public int[] getSelectedRows()
	{
		int[] selected = new int[this.mSelectedRows.size()];
		for(int i = 0; i < this.mSelectedRows.size(); i++)
		{
			selected[i] = this.mSelectedRows.get(i);
		}
		return selected;
	}
	
	public void select(int target)
	{
		this.mSelectedRows.clear();
		if(target < 0){
			return;
		}
		if(!this.isSelected(target)){
			this.mSelectedRows.add(target);
		}
		return;
	}
	
	public void ctrlSelect(int target)
	{
		if(target < 0){
			return;
		}
		if(!this.isSelected(target)){
			this.mSelectedRows.add(target);
		}else{
			this.unselect(target);
		}
		return;
	}
	
	public void shiftSelect(int target)
	{
		if(this.mSelectedRows.isEmpty() || target < 0){
			return;
		}
		int first = Math.min(target, this.mSelectedRows.get(this.mSelectedRows.size() - 1));
		int last = Math.max(target, this.mSelectedRows.get(this.mSelectedRows.size() - 1));
		this.mSelectedRows.clear();
		for(int i = first; i <= last; i++)
		{
			this.mSelectedRows.add(i);
		}
		return;
	}
	
	public void clearSelections()
	{
		this.mSelectedRows.clear();
		return;
	}
	
	public void unselect(int target)
	{
		for(int i = this.mSelectedRows.size() - 1; i >= 0; i--)
		{
			if(this.mSelectedRows.get(i) == target){
				this.mSelectedRows.remove(i);
			}
		}
		return;
	}
	
	public void setPointer(int pointer)
	{
		this.mPointer = pointer;
		this.getFirstToBeDrawn();
		this.getLastToBeDrawn();
		this.layoutVisibleList();
		//System.out.println(pointer);
		return;
	}
}
