package main;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import main.unit.Player;
import main.polycomp.PItem;

/**
 * 
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class MainMenu extends Lobby implements AppStatable, UIMainMenu.Listener
{
	public enum ButtonType
	{
		RESUME("RESUME", "Continue current game"),
		SINGLE_PLAYER("SINGLE PLAYER", "Start a new single-player game"),
		TWO_PLAYER("TWO-PLAYER", "Start a new two-player game"),
		HIGH_SCORES("HIGH SCORES", "See who has the highest score."),
		OPTIONS("OPTIONS", "Change graphics, audio and keyboard settings"),
		EXIT("EXIT", "Quit");
		
		private String mCaption = "";
		private String mDescription = "";
		
		private ButtonType(String caption, String description)
		{
			this.mCaption = caption;
			this.mDescription = description;
			return;
		}
		
		public String getCaption()
		{
			return this.mCaption;
		}
		
		public String getDescription()
		{
			return this.mDescription;
		}
	}
	
	private MainMenu.Listener mListener = null;
	private UIMainMenu mUI = null;
	private final Polygon BUTTON_TEMPLATE = new Polygon(new int[]{0, 200, 200, 0}, new int[]{0, 0, 50, 50}, 4);
	private final Point2D.Float BUTTON_SIZE_FACTORS = new Point2D.Float(0.15625f, 0.0555f); // by screen size.
	private final float BUTTON_Y_FACTOR = 0.08333f;
	private final float BUTTON_COLUMN_TOP_FACTOR = 0.27f; // by screen height.
	private final float BUTTON_FONT_FACTOR = 0.333f; // by button height.
	
	protected PItem[] mButtons = new PItem[ButtonType.values().length];
	protected Font mNormalMenuFont = null;
	protected Font mHoveredMenuFont = null;
	protected Font mSelectedMenuFont = null;
	protected boolean mShowResumeButton = false;
	protected int mSelectedButton = -1;
	
	public MainMenu(int x, int y, int width, int height, ScreenManager.Listener smListener, MainMenu.Listener listener)
	{
		super(x, y, width, height, smListener);
		this.mListener = listener;
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		this.mUI = new UIMainMenu(this);
		this.initializeButtons();
		this.mStartLobby.reset();
		this.mLobbyControl = this.mStartLobby;
		return;
	}
	
	public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
	{
		this.renderBackground(g2d);
		this.renderAsteroids(g2d, original);
		this.renderCopyrightLabel(g2d);
		this.renderButtons(g2d);
		this.renderCursor(g2d, this.mUI.getMousePosition().x, this.mUI.getMousePosition().y);
		this.renderFPSLabel(g2d);
		return;
	}
	
	private void renderButtons(Graphics2D g2d)
	{
		for(int i = 0; i < this.mButtons.length; i++)
		{
			if(this.mButtons[i].getType().equals(ButtonType.RESUME) && !this.mListener.gameinProgress()){
				continue;
			}
			
			if(this.mButtons[i].componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_BUTTON_BACKGROUND);
			}else if(this.mButtons[i].componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_BUTTON_BACKGROUND);
			}else{
				g2d.setPaint(Skin.NORMAL_BUTTON_BACKGROUND);
			}
			g2d.fillPolygon(this.mButtons[i].getPolygon());
			
			if(this.mButtons[i].componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
				g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			}else if(this.mButtons[i].componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
				g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			}else{
				g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
				g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			}
			g2d.drawPolygon(this.mButtons[i].getPolygon());
			
			if(this.mButtons[i].componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_BUTTON_FOREGROUND);
				g2d.setFont(this.mSelectedMenuFont);
			}else if(this.mButtons[i].componentIsHovered() || this.mSelectedButton == i){
				g2d.setPaint(Skin.HOVERED_BUTTON_FOREGROUND);
				g2d.setFont(this.mHoveredMenuFont);
			}else{
				g2d.setPaint(Skin.NORMAL_BUTTON_FOREGROUND);
				g2d.setFont(this.mNormalMenuFont);
			}
			
			int y = g2d.getFontMetrics(g2d.getFont()).getHeight() - g2d.getFontMetrics(g2d.getFont()).getAscent();
			int yDiff = (int)Math.round((this.mButtons[i].getHeight() - y) / 2.0);
			int xDiff = (int)Math.round((this.mButtons[i].getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth((String)this.mButtons[i].getContents())) / 2.0);
			g2d.drawString((String)this.mButtons[i].getContents(), this.mButtons[i].getLeft() + xDiff, this.mButtons[i].getTop() + y + yDiff);
		}
		return;
	}
	
	@Override
	public void update()
	{
		super.update();
		return;
	}
	
	public UI getUI()
	{
		return this.mUI;
	}
	
	public void enterPressed()
	{
		return;
	}
	
	public void escapePressed()
	{
		this.mListener.beginApplicationShutdown();
		return;
	}
	
	public void mainMenuButtonPressed(Object type)
	{
		if(!(type instanceof MainMenu.ButtonType)){
			return;
		}
		if(MainMenu.ButtonType.RESUME.equals(type)){
			if(this.mListener.getCurrentPlayer().getPlayerNum() == Player.Num.PLAYER1){
				this.mListener.switchAppStates(AppState.PLAYER1);
			}else if(this.mListener.getCurrentPlayer().getPlayerNum() == Player.Num.PLAYER2){
				this.mListener.switchAppStates(AppState.PLAYER2);
			}
		}else if(MainMenu.ButtonType.SINGLE_PLAYER.equals(type)){
			this.mListener.startNew(1);
		}else if(MainMenu.ButtonType.TWO_PLAYER.equals(type)){
			this.mListener.startNew(2);
		}else if(MainMenu.ButtonType.HIGH_SCORES.equals(type)){
			this.mListener.switchAppStates(AppState.HIGH_SCORES);
		}else if(MainMenu.ButtonType.OPTIONS.equals(type)){
			this.mListener.switchAppStates(AppState.OPTIONS);
			this.resetButtons();
		}else if(MainMenu.ButtonType.EXIT.equals(type)){
			this.mListener.beginApplicationShutdown();
		}
	}
	
	protected void initializeButtons()
	{
		int count = 0;
		for(ButtonType type : ButtonType.values())
		{
			PItem button = new PItem(BUTTON_TEMPLATE, type.getCaption());
			button.setType(type);
			this.mButtons[count] = button;
			count++;
		}
		
		int width = (int)Math.round(this.mScreenBounds.width * BUTTON_SIZE_FACTORS.x);
		int height = (int)Math.round(this.mScreenBounds.height * BUTTON_SIZE_FACTORS.y);
		int x = (int)Math.round((this.mScreenBounds.width / 2f) - (width / 2f));
		int y = (int)Math.round(this.mScreenBounds.height * BUTTON_COLUMN_TOP_FACTOR);
		int increment = (int)Math.round(this.mScreenBounds.height * BUTTON_Y_FACTOR);
		this.initializeMenuFont(height);
		
		for(int i = 0 ; i < this.mButtons.length; i++)
		{
			this.mButtons[i].setLocation(x, y + (increment * i));
			this.mButtons[i].resizePolygon(width, height);
		}
		return;
	}
	
	protected void initializeMenuFont(int buttonHeight)
	{
		this.mNormalMenuFont = Skin.MENU_FONT;
		this.mNormalMenuFont = this.mNormalMenuFont.deriveFont(Skin.NORMAL_FONT_STYLE);
		this.mNormalMenuFont = this.mNormalMenuFont.deriveFont((float)FontSizer.getFontSize(BUTTON_FONT_FACTOR, buttonHeight));
		
		this.mHoveredMenuFont = Skin.MENU_FONT;
		this.mHoveredMenuFont = this.mHoveredMenuFont.deriveFont(Skin.HOVERED_FONT_STYLE);
		this.mHoveredMenuFont = this.mHoveredMenuFont.deriveFont((float)FontSizer.getFontSize(BUTTON_FONT_FACTOR, buttonHeight));
		
		this.mSelectedMenuFont = Skin.MENU_FONT;
		this.mSelectedMenuFont = this.mSelectedMenuFont.deriveFont(Skin.SELECTED_FONT_STYLE);
		this.mSelectedMenuFont = this.mSelectedMenuFont.deriveFont((float)FontSizer.getFontSize(BUTTON_FONT_FACTOR, buttonHeight));
		return;
	}
	
	public void resetButtons()
	{
		this.mSelectedButton = -1;
		for(PItem button : this.mButtons)
		{
			button.setComponentHovered(false);
			button.setComponentSelected(false);
		}
		return;
	}
	
	public PItem[] getButtons()
	{
		return this.mButtons;
	}
	
	public void setSelectedButton(int selected)
	{
		this.mSelectedButton = selected;
		return;
	}
	
	public int getSelectedButton()
	{
		return this.mSelectedButton;
	}
	
	public interface Listener
	{
		abstract void beginApplicationShutdown();
		abstract void startNew(int players);
		abstract boolean gameinProgress();
		abstract Player getCurrentPlayer();
		abstract void switchAppStates(AppState state);
	}
}
