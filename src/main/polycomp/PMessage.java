package main.polycomp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class PMessage
{
	public enum MessageType
	{
		CLOSE,
		OKAY_ONLY,
		OKAY_CANCEL,
		OKAY_CANCEL_HELP,
		YES_NO,
		YES_NO_HELP;
	}
	
	public enum ButtonType
	{
		CANCEL("Cancel"),
		CLOSE("Close"),
		HELP("Help"),
		OKAY("Okay"),
		NO("No"),
		YES("Yes");
		
		private String mCaption = "";
		
		private ButtonType(String caption)
		{
			this.mCaption = caption;
			return;
		}
		
		public String getCaption()
		{
			return this.mCaption;
		}
	}
	
	public enum Position{CENTER_SCREEN, CUSTOM};
	
	private final Color DEFAULT_BACKGROUND = new Color(255, 255, 255, 255);
	private final Color DEFAULT_FOREGROUND = new Color(0, 0, 0, 255);
	private final BasicStroke DEFAULT_BORDER_STROKE = new BasicStroke(1.0f);
	private final Color DEFAULT_BORDER_COLOR = new Color(0, 0, 0, 255);
	private final Font DEFAULT_MESSAGE_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private final Color DEFAULT_NORMAL_BUTTON_BACKGROUND = new Color(128, 128, 128, 255);
	private final Color DEFAULT_NORMAL_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	private final Color DEFAULT_NORMAL_BUTTON_BORDER_COLOR = new Color(0, 0, 0, 255);
	private final BasicStroke DEFAULT_NORMAL_BUTTON_BORDER_STROKE = new BasicStroke(1.0f);
	private final Font DEFAULT_NORMAL_BUTTON_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private final Color DEFAULT_HOVERED_BUTTON_BACKGROUND = new Color(192, 192, 192, 255);
	private final Color DEFAULT_HOVERED_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	private final Color DEFAULT_HOVERED_BUTTON_BORDER_COLOR = new Color(0, 0, 0, 255);
	private final BasicStroke DEFAULT_HOVERED_BUTTON_BORDER_STROKE = new BasicStroke(1.0f);
	private final Font DEFAULT_HOVERED_BUTTON_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private final Color DEFAULT_SELECTED_BUTTON_BACKGROUND = new Color(255, 255, 255, 255);
	private final Color DEFAULT_SELECTED_BUTTON_FOREGROUND = new Color(0, 0, 0, 255);
	private final Color DEFAULT_SELECTED_BUTTON_BORDER_COLOR = new Color(0, 0, 0, 255);
	private final BasicStroke DEFAULT_SELECTED_BUTTON_BORDER_STROKE = new BasicStroke(1.0f);
	private final Font DEFAULT_SELECTED_BUTTON_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
	private final PMessage.Position DEFAULT_POSITION = PMessage.Position.CENTER_SCREEN;
	
	private GraphicsConfiguration mGC = null;
	private PMessage.MessageType mType = null;
	private PolyShape mBounds = null;
	private Color mBackground = DEFAULT_BACKGROUND;
	private Color mForeground = DEFAULT_FOREGROUND;
	private Color mBorderColor = DEFAULT_BORDER_COLOR;
	private BasicStroke mBorderStroke = DEFAULT_BORDER_STROKE;
	private Font mMessageFont = DEFAULT_MESSAGE_FONT;
	private String mMessage = null;
	private PItem[] mButtons = null;
	private PolyShape mButtonTemplate = null;
	private Color mNormalButtonBackground = DEFAULT_NORMAL_BUTTON_BACKGROUND;
	private Color mNormalButtonForeground = DEFAULT_NORMAL_BUTTON_FOREGROUND;
	private Color mNormalButtonBorderColor = DEFAULT_NORMAL_BUTTON_BORDER_COLOR;
	private BasicStroke mNormalButtonBorderStroke = DEFAULT_NORMAL_BUTTON_BORDER_STROKE;
	private Font mNormalButtonFont = DEFAULT_NORMAL_BUTTON_FONT;
	private Color mHoveredButtonBackground = DEFAULT_HOVERED_BUTTON_BACKGROUND;
	private Color mHoveredButtonForeground = DEFAULT_HOVERED_BUTTON_FOREGROUND;
	private Color mHoveredButtonBorderColor = DEFAULT_HOVERED_BUTTON_BORDER_COLOR;
	private BasicStroke mHoveredButtonBorderStroke = DEFAULT_HOVERED_BUTTON_BORDER_STROKE;
	private Font mHoveredButtonFont = DEFAULT_HOVERED_BUTTON_FONT;
	private Color mSelectedButtonBackground = DEFAULT_SELECTED_BUTTON_BACKGROUND;
	private Color mSelectedButtonForeground = DEFAULT_SELECTED_BUTTON_FOREGROUND;
	private Color mSelectedButtonBorderColor = DEFAULT_SELECTED_BUTTON_BORDER_COLOR;
	private BasicStroke mSelectedButtonBorderStroke = DEFAULT_SELECTED_BUTTON_BORDER_STROKE;
	private Font mSelectedButtonFont = DEFAULT_SELECTED_BUTTON_FONT;
	private PMessage.Position mPosition = DEFAULT_POSITION;
	private Point mCustomPosition = new Point();
	
	public PMessage(String message, PMessage.MessageType messageType, PolyShape bounds, PolyShape buttonTemplate)
	{
		this.setMessage(message);
		this.setMessageType(messageType);
		this.setBounds(bounds);
		this.setButtonTemplate(buttonTemplate);
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		this.mGC = gd.getDefaultConfiguration();
		return;
	}
	
	public void render(Graphics2D g2d)
	{
		this.renderBackground(g2d);
		this.renderBorder(g2d);
		this.renderMessage(g2d);
		this.renderButtons(g2d);
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
	
	private void renderMessage(Graphics2D g2d)
	{
		if(this.mMessage == null){
			return;
		}
		if(this.mMessage.isEmpty()){
			return;
		}
		if(this.mForeground == null){
			return;
		}
		if(this.mMessageFont == null){
			return;
		}
		g2d.setPaint(this.mForeground);
		g2d.setFont(this.mMessageFont);
		int x = (int)Math.round((this.mBounds.getWidth() - g2d.getFontMetrics(this.mMessageFont).stringWidth(this.mMessage)) / 2f);
		int y = g2d.getFontMetrics(this.mMessageFont).getHeight() * 3;
		g2d.drawString(this.mMessage, this.mBounds.getLeft() + x, this.mBounds.getTop() + y);
		return;
	}
	
	private void renderButtons(Graphics2D g2d)
	{
		if(this.mButtons == null){
			return;
		}
		for(int i = 0; i < this.mButtons.length; i++)
		{
			if(this.mButtons[i].componentIsSelected()){
				g2d.setPaint(this.mSelectedButtonBackground);
			}else if(this.mButtons[i].componentIsHovered()){
				g2d.setPaint(this.mHoveredButtonBackground);
			}else{
				g2d.setPaint(this.mNormalButtonBackground);
			}
			g2d.fillPolygon(this.mButtons[i].getPolygon());
			
			if(this.mButtons[i].componentIsSelected()){
				g2d.setPaint(this.mSelectedButtonBorderColor);
				g2d.setStroke(this.mSelectedButtonBorderStroke);
			}else if(this.mButtons[i].componentIsHovered()){
				g2d.setPaint(this.mHoveredButtonBorderColor);
				g2d.setStroke(this.mHoveredButtonBorderStroke);
			}else{
				g2d.setPaint(this.mNormalButtonBorderColor);
				g2d.setStroke(this.mNormalButtonBorderStroke);
			}
			g2d.drawPolygon(this.mButtons[i].getPolygon());
			
			int x = this.mButtons[i].getWidth();
			int y = 0;
			if(this.mButtons[i].componentIsSelected()){
				g2d.setPaint(this.mSelectedButtonForeground);
				g2d.setFont(this.mSelectedButtonFont);
				x -= g2d.getFontMetrics(this.mSelectedButtonFont).stringWidth((String)this.mButtons[i].getContents());
				y = Math.round(g2d.getFontMetrics(this.mSelectedButtonFont).getAscent() * 1.5f);
			}else if(this.mButtons[i].componentIsHovered()){
				g2d.setPaint(this.mHoveredButtonForeground);
				g2d.setFont(this.mHoveredButtonFont);
				x -= g2d.getFontMetrics(this.mHoveredButtonFont).stringWidth((String)this.mButtons[i].getContents());
				y = Math.round(g2d.getFontMetrics(this.mHoveredButtonFont).getAscent() * 1.5f);
			}else{
				g2d.setPaint(this.mNormalButtonForeground);
				g2d.setFont(this.mNormalButtonFont);
				x -= g2d.getFontMetrics(this.mNormalButtonFont).stringWidth((String)this.mButtons[i].getContents());
				y = Math.round(g2d.getFontMetrics(this.mNormalButtonFont).getAscent() * 1.5f);
			}
			g2d.drawString(String.valueOf(this.mButtons[i].getContents()), this.mButtons[i].getLeft() + (int)Math.round(x / 2f), this.mButtons[i].getTop() + y);
		}
		return;
	}
	
	public void invalidate()
	{
		BufferedImage temp = this.mGC.createCompatibleImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		int margin = g2d.getFontMetrics(this.mMessageFont).getHeight() * 2;
		this.createButtons();
		
		int buttonSpacing = g2d.getFontMetrics(this.mNormalButtonFont).getHeight();
		int buttonRowWidth = (this.mButtons.length * this.mButtons[0].getWidth()) + (buttonSpacing * (this.mButtons.length - 1)) + (margin * 2);
		int messageRowWidth = g2d.getFontMetrics(this.mMessageFont).stringWidth(this.mMessage) + (margin * 2);
		int totalHeight = g2d.getFontMetrics(this.mMessageFont).getHeight() + this.mButtons[0].getHeight() + (margin * 3);
		this.mBounds.resizePolygon(Math.max(buttonRowWidth, messageRowWidth), totalHeight);
		
		if(this.mPosition == PMessage.Position.CENTER_SCREEN){
			int x = (int)Math.round((Toolkit.getDefaultToolkit().getScreenSize().width - this.mBounds.getWidth()) / 2f);
			int y = (int)Math.round((Toolkit.getDefaultToolkit().getScreenSize().height - this.mBounds.getHeight()) / 2f);
			this.mBounds.setLocation(x, y);
		}else if(this.mPosition == PMessage.Position.CUSTOM){
			this.mBounds.setLocation(this.mCustomPosition.x, this.mCustomPosition.y);
		}
		
		this.layoutButtons(margin, buttonSpacing);
		return;
	}
	
	private void layoutButtons(int margin, int buttonSpacing)
	{
		int totalWidth = (this.mButtons.length * this.mButtons[0].getWidth()) + (buttonSpacing * (this.mButtons.length - 1));
		int x = (int)Math.round((this.mBounds.getWidth() - totalWidth) / 2f);
		int y = this.mBounds.getBottom() - (margin + this.mButtons[0].getHeight());
		for(int i = 0; i < this.mButtons.length; i++)
		{
			this.mButtons[i].setLocation(this.mBounds.getLeft() + x, y);
			x += (this.mButtons[i].getWidth() + buttonSpacing);
		}
		return;
	}
	
	protected void createButtons()
	{
		BufferedImage temp = this.mGC.createCompatibleImage(600, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		Dimension size = this.getButtonSize(g2d);
		if(this.mType == MessageType.CLOSE){
			this.mButtons = new PItem[1];
			this.mButtons[0] = this.createButton(ButtonType.CLOSE, size);
		}else if(this.mType == MessageType.OKAY_CANCEL){
			this.mButtons = new PItem[2];
			this.mButtons[0] = this.createButton(ButtonType.OKAY, size);
			this.mButtons[1] = this.createButton(ButtonType.CANCEL, size);
		}else if(this.mType == MessageType.OKAY_CANCEL_HELP){
			this.mButtons = new PItem[3];
			this.mButtons[0] = this.createButton(ButtonType.OKAY, size);
			this.mButtons[1] = this.createButton(ButtonType.CANCEL, size);
			this.mButtons[2] = this.createButton(ButtonType.HELP, size);
		}else if(this.mType == MessageType.OKAY_ONLY){
			this.mButtons = new PItem[1];
			this.mButtons[0] = this.createButton(ButtonType.OKAY, size);
		}else if(this.mType == MessageType.YES_NO){
			this.mButtons = new PItem[2];
			this.mButtons[0] = this.createButton(ButtonType.YES, size);
			this.mButtons[1] = this.createButton(ButtonType.NO, size);
		}else if(this.mType == MessageType.YES_NO_HELP){
			this.mButtons = new PItem[3];
			this.mButtons[0] = this.createButton(ButtonType.YES, size);
			this.mButtons[1] = this.createButton(ButtonType.NO, size);
			this.mButtons[2] = this.createButton(ButtonType.HELP, size);
		}
		return;
	}
	
	protected PItem createButton(PMessage.ButtonType type, Dimension size)
	{
		PItem button = new PItem(this.mButtonTemplate.getPolygon(), type.getCaption());
		button.setType(type);
		button.resizePolygon(size.width, size.height);
		return button;
	}
	
	public PItem[] getButtons()
	{
		return this.mButtons;
	}
	
	private Dimension getButtonSize(Graphics2D g2d)
	{
		/*
		 * The width of a set of buttons is based on the set's longest caption.
		 */
		int width = 0;
		int height = g2d.getFontMetrics(this.mNormalButtonFont).getHeight();
		if(this.mType == MessageType.OKAY_ONLY){
			width = g2d.getFontMetrics(this.mNormalButtonFont).stringWidth(ButtonType.OKAY.getCaption());
		}else if(this.mType == MessageType.OKAY_CANCEL || this.mType == MessageType.OKAY_CANCEL_HELP){
			width = g2d.getFontMetrics(this.mNormalButtonFont).stringWidth(ButtonType.CANCEL.getCaption());
		}else if(this.mType == MessageType.CLOSE){
			width = g2d.getFontMetrics(this.mNormalButtonFont).stringWidth(ButtonType.CANCEL.getCaption());
		}else if(this.mType == MessageType.YES_NO){
			width = g2d.getFontMetrics(this.mNormalButtonFont).stringWidth(ButtonType.YES.getCaption());
		}else if(this.mType == MessageType.YES_NO_HELP){
			width = g2d.getFontMetrics(this.mNormalButtonFont).stringWidth(ButtonType.HELP.getCaption());
		}
		return new Dimension(width + (height * 2), height * 2);
	}
	
	protected void setMessageType(PMessage.MessageType messageType)
	{
		this.mType = messageType;
		return;
	}
	
	public PMessage.MessageType getMessageType()
	{
		return this.mType;
	}
	
	protected void setMessage(String message)
	{
		this.mMessage = message;
		return;
	}
	
	public String getMessage()
	{
		return this.mMessage;
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
	
	public void setBorderColor(Color color)
	{
		this.mBorderColor = color;
		return;
	}
	
	public Color getBorderColor()
	{
		return this.mBorderColor;
	}
	
	protected void setBounds(PolyShape bounds)
	{
		this.mBounds = bounds;
		return;
	}
	
	public PolyShape getBounds()
	{
		return this.mBounds;
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
	
	public void setMessageFont(Font messageFont)
	{
		this.mMessageFont = messageFont;
		return;
	}
	
	public Font getMessageFont()
	{
		return this.mMessageFont;
	}
	
	protected void setButtonTemplate(PolyShape template)
	{
		this.mButtonTemplate = template;
		return;
	}
	
	public PolyShape getButtonTemplate()
	{
		return this.mButtonTemplate;
	}
	
	public void setNormalButtonStroke(BasicStroke stroke)
	{
		this.mNormalButtonBorderStroke = stroke;
		return;
	}
	
	public BasicStroke getNormalButtonStroke()
	{
		return this.mNormalButtonBorderStroke;
	}
	
	public void setNormalButtonFont(Font buttonFont)
	{
		this.mNormalButtonFont = buttonFont;
		return;
	}
	
	public Font getNormalButtonFont()
	{
		return this.mNormalButtonFont;
	}
	
	public void setNormalButtonBackground(Color color)
	{
		this.mNormalButtonBackground = color;
		return;
	}
	
	public Color getNormalButtonBackground()
	{
		return this.mNormalButtonBackground;
	}
	
	public void setNormalButtonForeground(Color color)
	{
		this.mNormalButtonForeground = color;
		return;
	}
	
	public Color getNormalButtonForeground()
	{
		return this.mNormalButtonForeground;
	}
	
	public void setNormalButtonBorderColor(Color color)
	{
		this.mNormalButtonBorderColor = color;
		return;
	}
	
	public Color getNormalButtonBorderColor()
	{
		return this.mNormalButtonBorderColor;
	}
	
	public void setHoveredButtonStroke(BasicStroke stroke)
	{
		this.mHoveredButtonBorderStroke = stroke;
		return;
	}
	
	public BasicStroke getHoveredButtonStroke()
	{
		return this.mHoveredButtonBorderStroke;
	}
	
	public void setHoveredButtonFont(Font buttonFont)
	{
		this.mHoveredButtonFont = buttonFont;
		return;
	}
	
	public Font getHoveredButtonFont()
	{
		return this.mHoveredButtonFont;
	}
	
	public void setHoveredButtonBackground(Color color)
	{
		this.mHoveredButtonBackground = color;
		return;
	}
	
	public Color getHoveredButtonBackground()
	{
		return this.mHoveredButtonBackground;
	}
	
	public void setHoveredButtonForeground(Color color)
	{
		this.mHoveredButtonForeground = color;
		return;
	}
	
	public Color getHoveredButtonForeground()
	{
		return this.mHoveredButtonForeground;
	}
	
	public void setHoveredButtonBorderColor(Color color)
	{
		this.mHoveredButtonBorderColor = color;
		return;
	}
	
	public Color getHoveredButtonBorderColor()
	{
		return this.mHoveredButtonBorderColor;
	}
	
	public void setSelectedButtonStroke(BasicStroke stroke)
	{
		this.mSelectedButtonBorderStroke = stroke;
		return;
	}
	
	public BasicStroke getSelectedButtonStroke()
	{
		return this.mSelectedButtonBorderStroke;
	}
	
	public void setSelectedButtonFont(Font buttonFont)
	{
		this.mSelectedButtonFont = buttonFont;
		return;
	}
	
	public Font getSelectedButtonFont()
	{
		return this.mSelectedButtonFont;
	}
	
	public void setSelectedButtonBackground(Color color)
	{
		this.mSelectedButtonBackground = color;
		return;
	}
	
	public Color getSelectedButtonBackground()
	{
		return this.mSelectedButtonBackground;
	}
	
	public void setSelectedButtonForeground(Color color)
	{
		this.mSelectedButtonForeground = color;
		return;
	}
	
	public Color getSelectedButtonForeground()
	{
		return this.mSelectedButtonForeground;
	}
	
	public void setSelectedButtonBorderColor(Color color)
	{
		this.mSelectedButtonBorderColor = color;
		return;
	}
	
	public Color getSelectedButtonBorderColor()
	{
		return this.mSelectedButtonBorderColor;
	}
	
	public void setPosition(PMessage.Position position)
	{
		this.mPosition = position;
	}
	
	public PMessage.Position getPosition()
	{
		return this.mPosition;
	}
	
	public void setCustomPosition(int x, int y)
	{
		this.mCustomPosition.x = x;
		this.mCustomPosition.y = y;
		return;
	}
	
	public Point getCustomPosition()
	{
		return this.mCustomPosition;
	}
}
