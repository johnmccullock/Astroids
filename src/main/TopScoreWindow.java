package main;

import java.awt.AlphaComposite;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import main.polycomp.PItem;
import main.polycomp.PolyShape;
import main.polycomp.PTextBox;
import main.unit.Player;

public class TopScoreWindow extends Lobby implements AppStatable, UITopScoreWindow.Listener
{
	public enum Button
	{
		// All factors are by screen size.
		OKAY("OKAY", 0.111f, 0.0476f),
		CANCEL("CANCEL", 0.111f, 0.0476f);
		
		private String mCaption = "";
		private float mWidthFactor = 0f;
		private float mHeightFactor = 0f;
		
		private Button(String caption, float width, float height)
		{
			this.mCaption = caption;
			this.mWidthFactor = width;
			this.mHeightFactor = height;
			return;
		}
		
		public String getCaption()
		{
			return this.mCaption;
		}
		
		public float getWidthFactor()
		{
			return this.mWidthFactor;
		}
		
		public float getHeightFactor()
		{
			return this.mHeightFactor;
		}
	}
	
	private final Polygon ENTRY_WINDOW_BORDER_TEMPLATE = new Polygon(new int[]{0, 100, 100, 0}, new int[]{0, 0, 50, 50}, 4);
	private final Rectangle2D ENTRY_WINDOW_FACTORS = new Rectangle2D.Float(0.5f, 0.5f, 0.333f, 0.25f); // by screen size
	private final Point2D.Float ENTRY_WINDOW_CAPTION_FACTORS = new Point2D.Float(0.5f, 0.333f); // by Entry Window size.
	private final Polygon TEXTBOX_BORDER_TEMPLATE = new Polygon(new int[]{0, 100, 100, 0}, new int[]{0, 0, 20, 20}, 4);
	private final Rectangle2D TEXTBOX_FACTORS = new Rectangle2D.Float(0.5f, 0.5f, 0.75f, 0.1666f); // by Entry Window size.
	private final float TEXTBOX_TOP_INSET_FACTOR = 0.0095f; // by screen height.
	private final float TEXTBOX_LEFT_INSET_FACTOR = 0.0119f; // by screen width.
	private final float TEXTBOX_BOTTOM_INSET_FACTOR = 0.0119f; // by screen height.
	private final float TEXTBOX_RIGHT_INSET_FACTOR = 0.0119f; // by screen width.
	private final int TEXTBOX_MAX_CHARACTERS = 10;
	private final int TEXTBOX_CURSOR_BLINK_RATE = 15;
	private final String ENTRY_WINDOW_CAPTION = "Enter your name to the high score list:";
	private final Polygon ENTRY_WINDOW_BUTTON_TEMPLATE = new Polygon(new int[]{0, 100, 100, 0}, new int[]{0, 0, 20, 20}, 4);
	private final float ENTRY_WINDOW_BUTTON_ROW_Y_FACTOR = 0.7f; //  by Entry Window size.
	private final float ENTRY_WINDOW_BUTTON_SPACING_FACTOR = 0.0119f; // by screen width.
	private final String[] BLANKS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
	
	private UITopScoreWindow mUI = null;
	private TopScoreWindow.Listener mListener = null;
	private int mListSize = 0;
	private Font mFont = null;
	private int mNumberLeft = 0;
	private int mNamesLeft = 0;
	private int mScoresLeft = 0;
	private int mListTop = 0;
	private int mYOffset = 0;
	private PolyShape mEntryWindowBorder = null;
	private Point mEntryWindowCaptionLocation = new Point();
	private PTextBox mEntryBox = null;
	private Font mEntryWindowFont = null;
	private Font mEntryTextBoxFont = null;
	private PItem[] mEntryButtons = null;
	private Font mNormalButtonFont = null;
	private Font mHoveredButtonFont = null;
	private Font mSelectedButtonFont = null;
	private Player mPlayer = null;
	private AppState mLastState = null;
	
	private NormalMode mNormalMode = new NormalMode();
	private NameEntryMode mNameEntryMode = new NameEntryMode();
	private RenderState mRenderState = this.mNormalMode;
	
	/**
	 * Creates a new instance of TopScoreWindow.  This constructor is used by the main menu's High Scores button, 
	 * and does not show the player name entry dialog.
	 * @param x int screen bounds x-value.  Usually zero.
	 * @param y int screen bounds y-value.  Usually zero.
	 * @param width int screen bounds width value.
	 * @param height int screen bounds height value.
	 * @param smListener ScreenManager.Listener references needed by the ScreenManager parent class.
	 * @param listener TopScoreWindow.Listener references needed by the TopScoreWindow class.
	 * @param listSize int maximum length of the high scores list.
	 * @param lastState AppState used to return program control to previous AppState. 
	 */
	public TopScoreWindow(int x, int y, int width, int height, ScreenManager.Listener smListener, TopScoreWindow.Listener listener, int listSize, AppState lastState)
	{
		super(x, y, width, height, smListener);
		this.mListener = listener;
		this.mListSize = listSize;
		this.mLastState = lastState;
		this.mUI = new UITopScoreWindow(this);
		this.initialize();
		return;
	}
	
	/**
	 * Creates a new instance of TopScoreWindow.  This constructor is used by the Game class, and shows the
	 * player name entry dialog.
	 * @param x int screen bounds x-value.  Usually zero.
	 * @param y int screen bounds y-value.  Usually zero.
	 * @param width int screen bounds width value.
	 * @param height int screen bounds height value.
	 * @param smListener ScreenManager.Listener references needed by the ScreenManager parent class.
	 * @param listener TopScoreWindow.Listener references needed by the TopScoreWindow class.
	 * @param listSize int maximum length of the high scores list.
	 * @param lastState AppState used to return program control to previous AppState.
	 * @param player Player reference containing the score of the player from Game class.
	 */
	public TopScoreWindow(int x, int y, int width, int height, ScreenManager.Listener smListener, TopScoreWindow.Listener listener, int listSize, AppState lastState, Player player)
	{
		super(x, y, width, height, smListener);
		this.mListener = listener;
		this.mListSize = listSize;
		this.mPlayer = player;
		this.mLastState = lastState;
		this.mUI = new UITopScoreWindow(this, false);
		this.initialize();
		this.mRenderState = this.mNameEntryMode;
		this.mUI.setToNameEntryMode();
		return;
	}
	
	private void initialize()
	{
		this.initializeHighScoreFonts();
		this.layoutList();
		this.initializeEntryWindow();
		this.mStartLobby.reset();
		this.mLobbyControl = this.mStartLobby;
		return;
	}
	
	public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
	{
		this.renderBackground(g2d);
		this.renderAsteroids(g2d, original);
		this.mRenderState.render(g2d, original, base);
		return;
	}
	
	private void renderList(Graphics2D g2d)
	{
		g2d.setFont(this.mFont);
		g2d.setPaint(Skin.HIGH_SCORE_WINDOW_FOREGROUND);
		int y = this.mListTop + this.mYOffset;
		for(int i = 0; i < this.mListener.getMaxScores(); i++)
		{
			g2d.drawString(String.format("%02d", (i + 1)) + ".", this.mNumberLeft, y + (this.mYOffset * i));
			if(i > this.mListener.getHighScores().size() - 1){
				g2d.drawString(BLANKS[i] + BLANKS[i] + BLANKS[i], this.mNamesLeft, y + (this.mYOffset * i));
				g2d.drawString("000", this.mScoresLeft, y + (this.mYOffset * i));
			}else{
				g2d.drawString(this.mListener.getHighScores().get(i).name, this.mNamesLeft, y + (this.mYOffset * i));
				g2d.drawString(String.valueOf(this.mListener.getHighScores().get(i).score), this.mScoresLeft, y + (this.mYOffset * i));
			}
		}
		return;
	}
	
	private void renderEntryWindow(Graphics2D g2d)
	{
		g2d.setPaint(Skin.ENTRY_WINDOW_BACKGROUND);
		g2d.fillPolygon(this.mEntryWindowBorder.getPolygon());
		
		g2d.setPaint(Skin.ENTRY_WINDOW_BORDER_COLOR);
		g2d.drawPolygon(this.mEntryWindowBorder.getPolygon());
		
		g2d.setFont(this.mEntryWindowFont);
		g2d.setPaint(Skin.ENTRY_WINDOW_FOREGROUND);
		g2d.drawString(ENTRY_WINDOW_CAPTION, this.mEntryWindowCaptionLocation.x, this.mEntryWindowCaptionLocation.y);
		
		this.mEntryBox.render(g2d);
		return;
	}
	
	private void renderEntryWindowButtons(Graphics2D g2d)
	{
		for(PItem button : this.mEntryButtons)
		{
			if(button.componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_ENTRY_WINDOW_BUTTON_BACKGROUND);
			}else if(button.componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_ENTRY_WINDOW_BUTTON_BACKGROUND);
			}else{
				g2d.setPaint(Skin.NORMAL_ENTRY_WINDOW_BUTTON_BACKGROUND);
			}
			g2d.fillPolygon(button.getPolygon());
			
			if(button.componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_ENTRY_WINDOW_BUTTON_BORDER);
			}else if(button.componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_ENTRY_WINDOW_BUTTON_BORDER);
			}else{
				g2d.setPaint(Skin.NORMAL_ENTRY_WINDOW_BUTTON_BORDER);
			}
			g2d.drawPolygon(button.getPolygon());
			
			if(button.componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_ENTRY_WINDOW_BUTTON_FOREGROUND);
				g2d.setFont(this.mSelectedButtonFont);
			}else if(button.componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_ENTRY_WINDOW_BUTTON_FOREGROUND);
				g2d.setFont(this.mHoveredButtonFont);
			}else{
				g2d.setPaint(Skin.NORMAL_ENTRY_WINDOW_BUTTON_FOREGROUND);
				g2d.setFont(this.mNormalButtonFont);
			}
			
			int y = (int)Math.round(g2d.getFontMetrics(g2d.getFont()).getHeight() - g2d.getFontMetrics(g2d.getFont()).getAscent());
			int yDiff = (int)Math.round((button.getHeight() + y) / 2f);
			int xDiff = (int)Math.round((button.getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth((String)button.getContents())) / 2.0);
			g2d.drawString((String)button.getContents(), button.getLeft() + xDiff, button.getTop() + y + yDiff);
		}
		return;
	}
	
	@Override
	public void update()
	{
		super.update();
		return;
	}
	
	private void initializeHighScoreFonts()
	{
		this.mFont = Skin.HIGH_SCORE_WINDOW_FONT;
		this.mFont = this.mFont.deriveFont(Skin.HIGH_SCORE_WINDOW_FONT_STYLE);
		this.mFont = this.mFont.deriveFont((float)FontSizer.getFontSize(Skin.HIGH_SCORE_WINDOW_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mEntryWindowFont = Skin.ENTRY_WINDOW_FONT;
		this.mEntryWindowFont = this.mEntryWindowFont.deriveFont(Skin.ENTRY_WINDOW_FONT_STYLE);
		this.mEntryWindowFont = this.mEntryWindowFont.deriveFont((float)FontSizer.getFontSize(Skin.ENTRY_WINDOW_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mEntryTextBoxFont = Skin.ENTRY_TEXTBOX_FONT;
		this.mEntryTextBoxFont = this.mEntryTextBoxFont.deriveFont(Skin.ENTRY_TEXTBOX_FONT_STYLE);
		this.mEntryTextBoxFont = this.mEntryTextBoxFont.deriveFont((float)FontSizer.getFontSize(Skin.ENTRY_TEXTBOX_FONT_FACTOR, this.mScreenBounds.height));
		return;
	}
	
	private void layoutList()
	{
		BufferedImage temp = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		this.mYOffset = g2d.getFontMetrics(this.mFont).getHeight();
		int length = g2d.getFontMetrics(this.mFont).stringWidth("00.--XXXXXXXXXX---000000");
		this.mNumberLeft = (int)Math.round((this.mScreenBounds.width - length) / 2f);
		this.mNamesLeft = this.mNumberLeft + g2d.getFontMetrics(this.mFont).stringWidth("00.--");
		this.mScoresLeft = this.mNamesLeft + g2d.getFontMetrics(this.mFont).stringWidth("XXXXXXXXXX---");
		this.mListTop = (int)Math.round((this.mScreenBounds.height - (this.mYOffset * this.mListSize)) / 2f);
		g2d.dispose();
		return;
	}
	
	private void initializeEntryWindow()
	{
		BufferedImage temp = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		int windowWidth = (int)Math.round(Math.max(this.mScreenBounds.width * ENTRY_WINDOW_FACTORS.getWidth(), 
													g2d.getFontMetrics(this.mEntryWindowFont).stringWidth(ENTRY_WINDOW_CAPTION)));
		this.mEntryWindowBorder = new PolyShape(ENTRY_WINDOW_BORDER_TEMPLATE.xpoints, ENTRY_WINDOW_BORDER_TEMPLATE.ypoints, ENTRY_WINDOW_BORDER_TEMPLATE.npoints);
		this.mEntryWindowBorder.resizePolygon(windowWidth, 
												(int)Math.round(this.mScreenBounds.height * ENTRY_WINDOW_FACTORS.getHeight()));
		this.mEntryWindowBorder.setLocation((int)Math.round((this.mScreenBounds.width * ENTRY_WINDOW_FACTORS.getX()) - (this.mEntryWindowBorder.getWidth() / 2f)), 
											(int)Math.round((this.mScreenBounds.height * ENTRY_WINDOW_FACTORS.getY() - (this.mEntryWindowBorder.getHeight() / 2f))));
		this.getEntryWindowCaptionLocation(this.mEntryWindowBorder);
		PolyShape boxBorder = new PolyShape(TEXTBOX_BORDER_TEMPLATE.xpoints, TEXTBOX_BORDER_TEMPLATE.ypoints, TEXTBOX_BORDER_TEMPLATE.npoints);
		this.mEntryBox = new PTextBox(boxBorder);
		this.mEntryBox.setCursorDelay(TEXTBOX_CURSOR_BLINK_RATE);
		int top = (int)Math.round(this.mScreenBounds.height * TEXTBOX_TOP_INSET_FACTOR);
		int left = (int)Math.round(this.mScreenBounds.width * TEXTBOX_LEFT_INSET_FACTOR);
		int bottom = (int)Math.round(this.mScreenBounds.height * TEXTBOX_BOTTOM_INSET_FACTOR);
		int right = (int)Math.round(this.mScreenBounds.width * TEXTBOX_RIGHT_INSET_FACTOR);
		this.mEntryBox.setInsets(new Insets(top, left, bottom, right));
		this.mEntryBox.showTextBounds(false); // TESTING ONLY.
		this.mEntryBox.setStretchToFitText(PTextBox.Fit.VERTICAL);
		this.mEntryBox.setMaxCharacters(TEXTBOX_MAX_CHARACTERS);
		this.mEntryBox.setSize((int)Math.round(this.mEntryWindowBorder.getWidth() * TEXTBOX_FACTORS.getWidth()), 
								(int)Math.round(this.mEntryWindowBorder.getHeight() * TEXTBOX_FACTORS.getHeight()));
		this.mEntryBox.setLocation((int)Math.round(this.mEntryWindowBorder.getLeft() + (this.mEntryWindowBorder.getWidth() * TEXTBOX_FACTORS.getX()) - (this.mEntryBox.getWidth() / 2f)), 
									(int)Math.round(this.mEntryWindowBorder.getTop() + (this.mEntryWindowBorder.getHeight() * TEXTBOX_FACTORS.getY()) - (this.mEntryBox.getHeight() / 2f)));
		this.mEntryBox.setBackground(Skin.ENTRY_TEXTBOX_BACKGROUND);
		this.mEntryBox.setForeground(Skin.ENTRY_TEXTBOX_FOREGROUND);
		this.mEntryBox.setBorderColor(Skin.ENTRY_TEXTBOX_BORDER);
		this.mEntryBox.setBorderStroke(Skin.ENTRY_TEXTBOX_STROKE);
		this.mEntryBox.setFont(this.mEntryTextBoxFont);
		this.mEntryBox.invalidate();
		
		this.mEntryButtons = new PItem[TopScoreWindow.Button.values().length];
		int count = 0;
		int width = 0;
		int height = 0;
		for(TopScoreWindow.Button type : TopScoreWindow.Button.values())
		{
			PItem button = new PItem(ENTRY_WINDOW_BUTTON_TEMPLATE, type.getCaption());
			button.setType(type);
			width = (int)Math.round(this.mScreenBounds.width * type.getWidthFactor());
			height = (int)Math.round(this.mScreenBounds.height * type.getHeightFactor());
			button.resizePolygon(width, height);
			this.mEntryButtons[count] = button;
			count++;
		}
		int spacing = (int)Math.round(this.mScreenBounds.width * ENTRY_WINDOW_BUTTON_SPACING_FACTOR);
		int x = (int)Math.round((this.mEntryWindowBorder.getWidth() / 2f) - (((width * this.mEntryButtons.length) + spacing) / 2f));
		for(int i = 0; i < this.mEntryButtons.length; i++)
		{
			this.mEntryButtons[i].setLocation(this.mEntryWindowBorder.getLeft() + x + ((width + spacing) * i), 
											(int)Math.round(this.mEntryWindowBorder.getTop() + (this.mEntryWindowBorder.getHeight() * ENTRY_WINDOW_BUTTON_ROW_Y_FACTOR)));
		}
		this.initializeEntryWindowButtonFonts(height);
		g2d.dispose();
		return;
	}
	
	private void getEntryWindowCaptionLocation(PolyShape parent)
	{
		BufferedImage temp = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)temp.getGraphics();
		int width = g2d.getFontMetrics(this.mEntryWindowFont).stringWidth(ENTRY_WINDOW_CAPTION);
		int height = g2d.getFontMetrics(this.mEntryWindowFont).getHeight();
		int x = (int)Math.round(this.mEntryWindowBorder.getLeft() + (parent.getWidth() * ENTRY_WINDOW_CAPTION_FACTORS.x) - (width / 2f));
		int y = (int)Math.round(this.mEntryWindowBorder.getTop() + (parent.getHeight() * ENTRY_WINDOW_CAPTION_FACTORS.y) - (height / 2f));
		this.mEntryWindowCaptionLocation.x = x;
		this.mEntryWindowCaptionLocation.y = y;
		g2d.dispose();
		return;
	}
	
	private void initializeEntryWindowButtonFonts(int buttonHeight)
	{
		this.mNormalButtonFont = Skin.NORMAL_ENTRY_WINDOW_BUTTON_FONT;
		this.mNormalButtonFont = this.mNormalButtonFont.deriveFont(Skin.NORMAL_ENTRY_WINDOW_BUTTON_FONT_STYLE);
		this.mNormalButtonFont = this.mNormalButtonFont.deriveFont((float)FontSizer.getFontSize(Skin.NORMAL_ENTRY_WINDOW_BUTTON_FONT_FACTOR, buttonHeight));
		
		this.mHoveredButtonFont = Skin.HOVERED_ENTRY_WINDOW_BUTTON_FONT;
		this.mHoveredButtonFont = this.mHoveredButtonFont.deriveFont(Skin.HOVERED_ENTRY_WINDOW_BUTTON_FONT_STYLE);
		this.mHoveredButtonFont = this.mHoveredButtonFont.deriveFont((float)FontSizer.getFontSize(Skin.HOVERED_ENTRY_WINDOW_BUTTON_FONT_FACTOR, buttonHeight));
		
		this.mSelectedButtonFont = Skin.SELECTED_ENTRY_WINDOW_BUTTON_FONT;
		this.mSelectedButtonFont = this.mSelectedButtonFont.deriveFont(Skin.SELECTED_ENTRY_WINDOW_BUTTON_FONT_STYLE);
		this.mSelectedButtonFont = this.mSelectedButtonFont.deriveFont((float)FontSizer.getFontSize(Skin.SELECTED_ENTRY_WINDOW_BUTTON_FONT_FACTOR, buttonHeight));
	}
	
	public void setToNormalMode()
	{
		this.mRenderState = this.mNormalMode;
		this.mUI.setToNormalMode();
		return;
	}
	
	public UI getUI()
	{
		return this.mUI;
	}
	
	public PTextBox getTextBox()
	{
		return this.mEntryBox;
	}
	
	public PItem[] getButtons()
	{
		return this.mEntryButtons;
	}
	
	public void escapePressed()
	{
		this.mListener.switchAppStates(this.mLastState);
		return;
	}
	
	public void okayButtonPressed()
	{
		this.mListener.addHighScore(new TopScoreWindow.ScoreData(this.mPlayer.getScore(), this.mEntryBox.getText()));
		this.setToNormalMode();
		return;
	}
	
	public void cancelButtonPressed()
	{
		this.setToNormalMode();
		return;
	}
	
	public interface Listener
	{
		abstract void addHighScore(TopScoreWindow.ScoreData score);
		abstract ArrayList<TopScoreWindow.ScoreData> getHighScores();
		abstract int getMaxScores();
		abstract void switchAppStates(AppState state);
	}
	
	public static class ScoreData implements Comparable<ScoreData>
	{
		public int score = 0;
		public String name = null;
		
		public ScoreData() { return; }
		
		public ScoreData(int score, String name)
		{
			this.score = score;
			this.name = name;
			return;
		}
		
		@Override
		public int compareTo(ScoreData that)
		{
			/*
			 * This is intended to naturally sort descending.
			 */
			if(that.score < this.score){
				return -1;
			}else if(that.score > this.score){
				return 1;
			}else{
				return 0;
			}
		}
	}
	
	private class NormalMode implements RenderState
	{
		public NormalMode() { return; }
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderList(g2d);
			renderCopyrightLabel(g2d);
			renderFPSLabel(g2d);
			return;
		}
	}
	
	private class NameEntryMode implements RenderState
	{
		public NameEntryMode() { return; }
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderList(g2d);
			renderCopyrightLabel(g2d);
			renderFPSLabel(g2d);
			renderEntryWindow(g2d);
			renderEntryWindowButtons(g2d);
			renderCursor(g2d, mUI.getMousePosition().x, mUI.getMousePosition().y);
			return;
		}
	}
}
