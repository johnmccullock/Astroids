package main;

import java.awt.AlphaComposite;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Vector;

import main.polycomp.PAbstractListModel;
import main.polycomp.PAlign;
import main.polycomp.PItem;
import main.polycomp.PList;
import main.polycomp.PMessage;
import main.polycomp.PolyShape;
import main.polycomp.PScrollBar;
import main.unit.entity.util.Polygon2;

/**
 * 
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class OptionsWindow extends Lobby implements AppStatable, UIOptionsWindow.Listener
{
	public enum ButtonType
	{
		GRAPHICS("GRAPHICS"),
		ADVANCED("ADVANCED"),
		SOUND("SOUND"),
		CONTROLS("CONTROLS"),
		APPLY("APPLY"),
		BACK("BACK");
		
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
	
	public enum KeyButton
	{
		LEFT(KeyNames.LEFT, "ROTATE LEFT: ", 0.577f, 0.2f, 0.6f, 0.2f, 0.1f, 0.05f),
		RIGHT(KeyNames.RIGHT, "ROTATE RIGHT: ", 0.577f, 0.314f, 0.6f, 0.314f, 0.1f, 0.05f),
		THRUST(KeyNames.THRUST, "THRUST: ", 0.577f, 0.4285f, 0.6f, 0.4285f, 0.1f, 0.05f),
		FIRE(KeyNames.FIRE, "FIRE: ", 0.577f, 0.5428f, 0.6f, 0.5428f, 0.1f, 0.05f),
		HYPERSPACE(KeyNames.HYPERSPACE, "HYPERSPACE: ", 0.577f, 0.656f, 0.6f, 0.656f, 0.1f, 0.05f);
		
		private KeyNames mName = null;
		private String mCaption = "";
		private float mLabelX = 0f;
		private float mLabelY = 0f;
		private float mXFactor = 0f;
		private float mYFactor = 0f;
		private float mWidthFactor = 0f;
		private float mHeightFactor = 0f;
		
		private KeyButton(KeyNames name, String caption, float labelX, float labelY, float xFactor, float yFactor, float widthFactor, float heightFactor)
		{
			this.mName = name;
			this.mCaption = caption;
			this.mLabelX = labelX;
			this.mLabelY = labelY;
			this.mXFactor = xFactor;
			this.mYFactor = yFactor;
			this.mWidthFactor = widthFactor;
			this.mHeightFactor = heightFactor;
			return;
		}
		
		public KeyNames getKeyName()
		{
			return this.mName;
		}
		
		public String getCaption()
		{
			return this.mCaption;
		}
		
		public float getLabelXFactor()
		{
			return this.mLabelX;
		}
		
		public float getLabelYFactor()
		{
			return this.mLabelY;
		}
		
		public float getXFactor()
		{
			return this.mXFactor;
		}
		
		public float getYFactor()
		{
			return this.mYFactor;
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
	
	public enum Category{GRAPHICS, ADVANCED, SOUND, CONTROLS};
	
	private final Polygon2 BUTTON_TEMPLATE = new Polygon2(new int[]{0, 100, 100, 0}, new int[]{0, 0, 50, 50}, 4);
	private final float BUTTON_COLUMN_X_FACTOR = 0.1f; // by screen width.
	private final float BUTTON_COLUMN_Y_FACTOR = 0.27f; // by screen height.
	private final float BUTTON_Y_FACTOR = 0.0857f; // by screen height.
	private final Point2D.Float BUTTON_SIZE_FACTORS = new Point2D.Float(0.15625f, 0.0555f); // by screen size.
	private final float BUTTON_FONT_FACTOR = 0.333f; // by button height.
	private final Polygon CONTROL_AREA_TEMPLATE = new Polygon(new int[]{0, 200, 200, 0}, new int[]{0, 0, 200, 200}, 4);
	private final Rectangle2D.Float CONTROL_AREA_FACTORS = new Rectangle2D.Float(0.35f, 0.1f, 0.6f, 0.8f);
	private final String DISPLAY_MODE_LABEL_CAPTION = "DISPLAY MODE :";
	private final Polygon DISPLAY_MODE_TEXT_BOX_TEMPLATE = new Polygon(new int[]{0, 200, 200, 0}, new int[]{0, 0, 100, 100}, 4);
	private final Rectangle2D.Float DISPLAY_MODE_TEXT_BOX_FACTORS = new Rectangle2D.Float(0.4f, 0.15f, 0.5f, 0.0333f);
	private final Polygon MODE_LIST_CELL_TEMPLATE = new Polygon(new int[]{4, 668, 672, 672, 668, 4, 0, 0},
																new int[]{0, 0, 4, 20, 24, 24, 20, 4},
																8);
	private final int MODE_LIST_Y_OFFSET = 5;
	private final float MODE_LIST_CELL_WIDTH_FACTOR = 0.48f;
	private final float MODE_LIST_CELL_HEIGHT_FACTOR = 0.025f;
	private final int DISPLAY_MODE_LIST_VISIBLE_ROWS = 10;
	private final Rectangle2D.Float DISPLAY_MODE_SCROLLPANE_FACTORS = new Rectangle2D.Float(0.4f, 0.0f, 0.48f, 0.6f);
	private final Polygon VERTICAL_SCROLL_TRACK_TEMPLATE = new Polygon(new int[]{0, 10, 10, 0},
																		new int[]{0, 0, 100, 100},
																		4);
	private final Polygon VERTICAL_SCROLL_GRIP_TEMPLATE = new Polygon(new int[]{0, 10, 10, 0}, new int[]{0, 0, 95, 95}, 4);
	private final Polygon VERTICAL_SCROLL_DECREMENT_TEMPLATE = new Polygon(new int[]{5, 10, 0}, new int[]{0, 10, 10}, 3);
	private final Polygon VERTICAL_SCROLL_INCREMENT_TEMPLATE = new Polygon(new int[]{0, 10, 5}, new int[]{0, 0, 10}, 3);
	private final Rectangle2D.Float VERTICAL_SCROLL_FACTORS = new Rectangle2D.Float(0.0f, 0.0f, 0.0119f, 0.0f);
	private final int VERTICAL_SCROLL_X_OFFSET = 12;
	
	private final String VERTICAL_SYNC_LABEL_CAPTION = "USE VERTICAL SYNC :";
	private final Point2D.Float VERTICAL_SYNC_LABEL_FACTORS = new Point2D.Float(0.0f, 0.6f);
	private final Polygon2 PREV_BUTTON_TEMPLATE = new Polygon2(new int[]{0, 20, 20}, new int[]{10, 0, 20}, 3);
	private final Polygon2 NEXT_BUTTON_TEMPLATE = new Polygon2(new int[]{0, 20, 0}, new int[]{0, 10, 20}, 3);
	private final float PREV_NEXT_BUTTON_WIDTH_FACTOR = 0.017857f; // by screen width
	private final float PREV_NEXT_BUTTON_HEIGHT_FACTOR = 0.02857f; // by screen height
	
	private final String RENDER_QUALITY_LABEL_CAPTION = "RENDER QUALITY :";
	private final Point2D.Float RENDER_QUALITY_LABEL_FACTORS = new Point2D.Float(0.4f, 0.2f);
	private final Point2D.Float RENDER_QUALITY_CONTROL_FACTORS = new Point2D.Float(0.6f, 0.25f);
	private final String ANTIALIAS_LABEL_CAPTION = "ANTIALIAS :";
	private final Point2D.Float ANTIALIAS_LABEL_FACTORS = new Point2D.Float(0.4f, 0.35f);
	private final Point2D.Float ANTIALIAS_CONTROL_FACTORS = new Point2D.Float(0.6f, 0.4f);
	private final String RENDER_COLOR_LABEL_CAPTION = "COLOR RENDERING :";
	private final Point2D.Float RENDER_COLOR_LABEL_FACTORS = new Point2D.Float(0.4f, 0.5f);
	private final Point2D.Float RENDER_COLOR_CONTROL_FACTORS = new Point2D.Float(0.6f, 0.55f);
	private final String RENDER_ALPHA_LABEL_CAPTION = "ALPHA RENDERING :";
	private final Point2D.Float RENDER_ALPHA_LABEL_FACTORS = new Point2D.Float(0.4f, 0.65f);
	private final Point2D.Float RENDER_ALPHA_CONTROL_FACTORS = new Point2D.Float(0.6f, 0.7f);
	private final String SHOW_FPS_LABEL_CAPTION = "SHOW FRAMES PER SECOND (FPS) :";
	private final Point2D.Float SHOW_FPS_LABEL_FACTORS = new Point2D.Float(0.4f, 0.8f);
	private final Point2D.Float SHOW_FPS_CONTROL_FACTORS = new Point2D.Float(0.6f, 0.85f);
	
	private final String VOLUME_LABEL_CAPTION = "VOLUME LEVEL :";
	private final Point2D.Float VOLUME_LABEL_FACTORS = new Point2D.Float(0.4f, 0.2f);
	private final Point2D.Float VOLUME_CONTROL_FACTORS = new Point2D.Float(0.6f, 0.25f);
	private final String MUTE_SOUND_LABEL_CAPTION = "MUTE :";
	private final Point2D.Float MUTE_LABEL_FACTORS = new Point2D.Float(0.4f, 0.35f);
	private final Point2D.Float MUTE_SOUND_FACTORS = new Point2D.Float(0.6f, 0.365f);
	private final float VOLUME_INCREMENT = 1.0f;
	
	private final Polygon2 KEY_BUTTON_TEMPLATE = new Polygon2(new int[]{0, 100, 100, 0}, new int[]{0, 0, 50, 50}, 4);
	private final int UNDEFINED_KEY_CODE = 65535; // see KeyEvent.CHAR_UNDEFINED.
	private final String UNDEFINED_ITEM_CAPTION = "undefined";
	
	private PolyShape MESSAGE_WINDOW_TEMPLATE = new PolyShape(new int[]{0, 200, 200, 0}, new int[]{0, 0, 100, 100}, 4);
	private PolyShape MESSAGE_WINDOW_BUTTON_TEMPLATE = new PolyShape(new int[]{0, 100, 100, 0}, new int[]{0, 0, 30, 30}, 4);
	
	private UIOptionsWindow mUI = null;
	private OptionsWindow.Listener mListener = null;
	private Category mCategory = Category.GRAPHICS;
	private PItem[] mButtons = new PItem[ButtonType.values().length];
	private Font mNormalMenuFont = null;
	private Font mHoveredMenuFont = null;
	private Font mSelectedMenuFont = null;
	private int mSelectedButton = -1;
	private Vector<DisplayMode> mModes = null;
	private Font mLabelFont = null;
	private Font mPlainFont = null;
	private PolyShape mControlArea = null;
	private PolyShape mDisplayModeTextBox = null;
	private String mModeBoxText = new String();
	private DisplayMode mDisplayModeValue = null;
	private boolean mDisplayModeChanged = false;
	private PList mDisplayModeList = null;
	private DisplayModeListModel mDisplayModeListModel = null;
	private Rectangle mDisplayListArea = new Rectangle();
	private PScrollBar mDisplayModeScroll = null;
	private int mDisplayModeScrollY = 0;
	private PItem mVSyncPrevButton = null;
	private PItem mVSyncNextButton = null;
	private Rectangle mVSyncButtonArea = new Rectangle();
	private boolean mVSyncChecked = false;
	private boolean mVSyncValueHasChanged = false;
	private PItem mRenderQualityPrevButton = null;
	private PItem mRenderQualityNextButton = null;
	private Rectangle mRenderQualityArea = new Rectangle();
	private RenderQuality mRenderQualityValue = RenderQuality.HIGH;
	private boolean mRenderQualityHasChanged = false;
	private PItem mAntialiasPrevButton = null;
	private PItem mAntialiasNextButton = null;
	private Rectangle mAntialiasArea = new Rectangle();
	private RenderAntialias mAntialiasValue = RenderAntialias.ON;
	private boolean mAntialiasHasChanged = false;
	private PItem mRenderColorPrevButton = null;
	private PItem mRenderColorNextButton = null;
	private Rectangle mRenderColorArea = new Rectangle();
	private RenderColor mRenderColorValue = RenderColor.HIGH;
	private boolean mRenderColorHasChanged = false;
	private PItem mRenderAlphaPrevButton = null;
	private PItem mRenderAlphaNextButton = null;
	private Rectangle mRenderAlphaArea = new Rectangle();
	private RenderAlpha mRenderAlphaValue = RenderAlpha.HIGH;
	private boolean mRenderAlphaHasChanged = false;
	private PItem mShowFPSPrevButton = null;
	private PItem mShowFPSNextButton = null;
	private Rectangle mShowFPSArea = new Rectangle();
	private boolean mShowFPSValue = false;
	private boolean mShowFPSHasChanged = false;
	private PItem mVolumeDownButton = null;
	private PItem mVolumeUpButton = null;
	private float mCurrentVolumeLevel = 1.0f;
	private boolean mVolumeChanged = false;
	private Rectangle mVolumeRectangle = new Rectangle();
	private Rectangle mVolumeDownArea = new Rectangle();
	private Rectangle mVolumeUpArea = new Rectangle();
	private PItem mMutePrevButton = null;
	private PItem mMuteNextButton = null;
	private Rectangle mMuteRectangle = new Rectangle();
	private boolean mMuteOn = false;
	private boolean mMuteValueHasChanged = false;
	private PItem[] mKeyButtons = new PItem[KeyButton.values().length];
	private HashMap<KeyNames, Integer> mKeyBindings = new HashMap<KeyNames, Integer>();
	private boolean mKeyBindingHasChanged = false;
	private Font mNormalKeyButtonFont = null;
	private Font mHoveredKeyButtonFont = null;
	private Font mSelectedKeyButtonFont = null;
	private PMessage mMessageWindow = null;
	private Font mMessageWindowFont = null;
	private Font mNormalMessageButtonFont = null;
	private Font mHoveredMessageButtonFont = null;
	private Font mSelectedMessageButtonFont = null;
	
	private GraphicsOptionsMode mGraphicsOptionsMode = new GraphicsOptionsMode();
	private AdvancedOptionsMode mAdvancedOptionsMode = new AdvancedOptionsMode();
	private AudioOptionsMode mAudioOptionsMode = new AudioOptionsMode();
	private KeyBindingsMode mKeyBindingsMode = new KeyBindingsMode();
	private RenderState mControlState = this.mGraphicsOptionsMode;
	
	public OptionsWindow(int x, int y, int width, int height, ScreenManager.Listener smListener, OptionsWindow.Listener listener, Settings settings)
	{
		super(x, y, width, height, smListener);
		this.mListener = listener;
		this.setSettings(settings);
		this.initialize();
		return;
	}
	
	public void setSettings(Settings settings)
	{
		this.mDisplayModeValue = settings.displayMode;
		this.mRenderQualityValue = settings.renderQuality;
		this.mRenderColorValue = settings.renderColor;
		this.mRenderAlphaValue = settings.renderAlpha;
		this.mAntialiasValue = settings.renderAntialias;
		this.mVSyncChecked = settings.useVSync;
		for(KeyNames name : KeyNames.values())
		{
			this.mKeyBindings.put(name, settings.keyBindings.get(name));
		}
		this.mMuteOn = settings.muteSound;
		this.mCurrentVolumeLevel = settings.volume;
		return;
	}
	
	private void initialize()
	{
		this.mUI = new UIOptionsWindow(this);
		this.mModes = DisplayModes.get(DisplayModes.Sorted.DESCENDING);
		this.mModeBoxText = this.printDisplayMode(this.mDisplayModeValue);
		
		this.initializeOptionsFonts();
		this.initializeButtons();
		this.initializeControlArea();
		this.initializeGraphicsModeTextBox();
		this.initializeDisplayModeList();
		this.initializeVSyncItems();
		this.initializeAdvancedItems();
		this.initializeVolumeLevelItems();
		this.initializeKeyboardItems();
		this.mStartLobby.reset();
		this.mLobbyControl = this.mStartLobby;
		return;
	}
	
	public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
	{
		this.renderBackground(g2d);
		this.renderAsteroids(g2d, original);
		this.mControlState.render(g2d, original, base);
		this.renderButtons(g2d);
		this.renderMessageWindow(g2d);
		this.renderCursor(g2d, this.mUI.getMousePosition().x, this.mUI.getMousePosition().y);
		this.renderFPSLabel(g2d);
		return;
	}
	
	private void renderButtons(Graphics2D g2d)
	{
		for(int i = 0; i < this.mButtons.length; i++)
		{
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
	
	private void renderDisplayModeTextBox(Graphics2D g2d)
	{
		g2d.setPaint(Skin.SCREEN_BACKGROUND);
		g2d.fillPolygon(this.mDisplayModeTextBox.getPolygon());
		
		g2d.setPaint(Skin.TEXT_BOX_BORDER);
		g2d.setStroke(Skin.TEXT_BOX_STROKE);
		g2d.drawPolygon(this.mDisplayModeTextBox.getPolygon());
		
		if(this.mModeBoxText.length() > 0){
			if(this.mDisplayModeChanged){
				g2d.setPaint(Skin.TEXT_BOX_CHANGED_FOREGROUND);
			}else{
				g2d.setPaint(Skin.TEXT_BOX_NORMAL_FOREGROUND);
			}
			g2d.setFont(this.mPlainFont);
			int width = g2d.getFontMetrics(g2d.getFont()).stringWidth(this.mModeBoxText);
			int x = (int)Math.round((this.mDisplayModeTextBox.getWidth() - width) / 2.0);
			int y = g2d.getFontMetrics(g2d.getFont()).getHeight() - (g2d.getFontMetrics(g2d.getFont()).getDescent() + g2d.getFontMetrics(g2d.getFont()).getDescent());
			int yDiff = (int)Math.round((this.mDisplayModeTextBox.getHeight() - y) / 2.0);
			g2d.drawString(this.mModeBoxText, this.mDisplayModeTextBox.getLeft() + x, this.mDisplayModeTextBox.getTop() + y + yDiff);
		}
		return;
	}
	
	private void renderDisplayModeList(Graphics2D g2d)
	{
		this.mDisplayModeList.render(g2d);
		this.mDisplayModeScroll.render(g2d);
		return;
	}
	
	private void renderVSyncItems(Graphics2D g2d)
	{
		if(this.mVSyncPrevButton.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(this.mVSyncPrevButton.getPolygon());
		}else if(this.mVSyncPrevButton.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVSyncPrevButton.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVSyncPrevButton.getPolygon());
		}
		
		if(this.mVSyncNextButton.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(this.mVSyncNextButton.getPolygon());
		}else if(this.mVSyncNextButton.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVSyncNextButton.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVSyncNextButton.getPolygon());
		}
		
		g2d.setFont(this.mPlainFont);
		if(this.mVSyncValueHasChanged){
			g2d.setPaint(Skin.CHANGED_SETTINGS_VALUE);
		}else{
			g2d.setPaint(Skin.NORMAL_SETTINGS_VALUE);
		}
		String text = this.mVSyncChecked ? "YES" : "NO";
		int buttonMiddle = (int)Math.round((this.mVSyncPrevButton.getRight() + this.mVSyncNextButton.getLeft()) / 2.0);
		int xDiff = (int)Math.round(g2d.getFontMetrics(g2d.getFont()).stringWidth(text) / 2.0);
		g2d.drawString(text, buttonMiddle - xDiff, this.mVSyncPrevButton.getBottom());
		
		return;
	}
	
	private void renderGraphicsSectionLabels(Graphics2D g2d)
	{
		g2d.setFont(this.mLabelFont);
		int y = g2d.getFontMetrics(g2d.getFont()).getDescent();
		
		g2d.setPaint(Skin.LARGE_LABEL_FONT_COLOR);
		g2d.drawString(DISPLAY_MODE_LABEL_CAPTION, this.mDisplayModeTextBox.getLeft(), this.mDisplayModeTextBox.getTop() - y);
		
		g2d.setPaint(Skin.LARGE_LABEL_FONT_COLOR);
		g2d.drawString(VERTICAL_SYNC_LABEL_CAPTION, this.mDisplayModeTextBox.getLeft(), (int)Math.round(this.mScreenBounds.height * VERTICAL_SYNC_LABEL_FACTORS.y));
		return;
	}
	
	private void renderBooleanControl(Graphics2D g2d, PItem prev, PItem next, String label, Point2D.Float labelFactors, boolean changedFlag, String value)
	{
		g2d.setFont(this.mLabelFont);
		g2d.setPaint(Skin.LARGE_LABEL_FONT_COLOR);
		g2d.drawString(label, (int)Math.round(this.mScreenBounds.getWidth() * labelFactors.x), (int)Math.round(this.mScreenBounds.getHeight() * labelFactors.y));
		
		g2d.setPaint(Skin.SCREEN_BACKGROUND);
		g2d.fillRect(prev.getRight(), prev.getTop(), next.getLeft() - prev.getRight(), prev.getHeight() + 10);
		
		if(prev.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(prev.getPolygon());
		}else if(prev.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(prev.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(prev.getPolygon());
		}
		
		if(next.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(next.getPolygon());
		}else if(next.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(next.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(next.getPolygon());
		}
		
		g2d.setFont(this.mPlainFont);
		if(changedFlag){
			g2d.setPaint(Skin.CHANGED_SETTINGS_VALUE);
		}else{
			g2d.setPaint(Skin.NORMAL_SETTINGS_VALUE);
		}
		String text = value;
		int buttonMiddle = (int)Math.round((prev.getRight() + next.getLeft()) / 2.0);
		int xDiff = (int)Math.round(g2d.getFontMetrics(g2d.getFont()).stringWidth(text) / 2.0);
		g2d.drawString(text, buttonMiddle - xDiff, prev.getBottom());
		return;
	}
	
	private void renderVolumeControls(Graphics2D g2d)
	{
		if(this.mVolumeDownButton.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(this.mVolumeDownButton.getPolygon());
		}else if(this.mVolumeDownButton.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVolumeDownButton.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVolumeDownButton.getPolygon());
		}
		
		if(this.mVolumeUpButton.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(this.mVolumeUpButton.getPolygon());
		}else if(this.mVolumeUpButton.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVolumeUpButton.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mVolumeUpButton.getPolygon());
		}
		
		g2d.setFont(this.mPlainFont);
		if(this.mVolumeChanged){
			g2d.setPaint(Skin.CHANGED_SETTINGS_VALUE);
		}else{
			g2d.setPaint(Skin.NORMAL_SETTINGS_VALUE);
		}
		String text = String.valueOf(100 * this.mCurrentVolumeLevel) + "%";
		int buttonMiddle = (int)Math.round((this.mVolumeDownButton.getRight() + this.mVolumeUpButton.getLeft()) / 2.0);
		int xDiff = (int)Math.round(g2d.getFontMetrics(g2d.getFont()).stringWidth(text) / 2.0);
		g2d.drawString(text, buttonMiddle - xDiff, this.mVolumeDownButton.getBottom());
		
		return;
	}
	
	private void renderMuteControls(Graphics2D g2d)
	{
		if(this.mMutePrevButton.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(this.mMutePrevButton.getPolygon());
		}else if(this.mMutePrevButton.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mMutePrevButton.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mMutePrevButton.getPolygon());
		}
		
		if(this.mMuteNextButton.componentIsSelected()){
			g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			g2d.setStroke(Skin.SELECTED_BUTTON_BORDER_STROKE);
			g2d.fillPolygon(this.mMutePrevButton.getPolygon());
		}else if(this.mMuteNextButton.componentIsHovered()){
			g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			g2d.setStroke(Skin.HOVERED_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mMuteNextButton.getPolygon());
		}else{
			g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			g2d.setStroke(Skin.NORMAL_BUTTON_BORDER_STROKE);
			g2d.drawPolygon(this.mMuteNextButton.getPolygon());
		}
		
		g2d.setFont(this.mPlainFont);
		if(this.mMuteValueHasChanged){
			g2d.setPaint(Skin.CHANGED_SETTINGS_VALUE);
		}else{
			g2d.setPaint(Skin.NORMAL_SETTINGS_VALUE);
		}
		String text = this.mMuteOn ? "YES" : "NO";
		int buttonMiddle = (int)Math.round((this.mMutePrevButton.getRight() + this.mMuteNextButton.getLeft()) / 2.0);
		int xDiff = (int)Math.round(g2d.getFontMetrics(g2d.getFont()).stringWidth(text) / 2.0);
		g2d.drawString(text, buttonMiddle - xDiff, this.mMutePrevButton.getBottom());
		
		return;
	}
	
	private void renderSoundSectionLabels(Graphics2D g2d)
	{
		g2d.setFont(this.mLabelFont);
		g2d.setPaint(Skin.LARGE_LABEL_FONT_COLOR);
		g2d.drawString(VOLUME_LABEL_CAPTION, (int)Math.round(this.mScreenBounds.width * VOLUME_LABEL_FACTORS.x), (int)Math.round(this.mScreenBounds.height * VOLUME_LABEL_FACTORS.y));
		g2d.drawString(MUTE_SOUND_LABEL_CAPTION, (int)Math.round(this.mScreenBounds.width * MUTE_LABEL_FACTORS.x), (int)Math.round(this.mScreenBounds.height * MUTE_LABEL_FACTORS.y));
		return;
	}
	
	private void renderKeyBindingItems(Graphics2D g2d)
	{
		for(int i = 0; i < this.mKeyButtons.length; i++)
		{
			if(this.mKeyButtons[i].componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_BUTTON_BACKGROUND);
			}else if(this.mKeyButtons[i].componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_BUTTON_BACKGROUND);
			}else{
				g2d.setPaint(Skin.NORMAL_BUTTON_BACKGROUND);
			}
			g2d.fillPolygon(this.mKeyButtons[i].getPolygon());
			
			if(this.mKeyButtons[i].componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_BUTTON_BORDER);
			}else if(this.mKeyButtons[i].componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_BUTTON_BORDER);
			}else{
				g2d.setPaint(Skin.NORMAL_BUTTON_BORDER);
			}
			g2d.drawPolygon(this.mKeyButtons[i].getPolygon());
			
			if(this.mKeyButtons[i].componentIsSelected()){
				g2d.setPaint(Skin.SELECTED_BUTTON_FOREGROUND);
				g2d.setFont(this.mSelectedKeyButtonFont);
			}else if(this.mKeyButtons[i].componentIsHovered()){
				g2d.setPaint(Skin.HOVERED_BUTTON_FOREGROUND);
				g2d.setFont(this.mHoveredKeyButtonFont);
			}else{
				g2d.setPaint(Skin.NORMAL_BUTTON_FOREGROUND);
				g2d.setFont(this.mNormalKeyButtonFont);
			}
			
			int y = g2d.getFontMetrics(g2d.getFont()).getHeight() - g2d.getFontMetrics(g2d.getFont()).getAscent();
			int yDiff = (int)Math.round((this.mKeyButtons[i].getHeight() - y) / 2.0f);
			int xDiff = (int)Math.round((this.mKeyButtons[i].getWidth() - g2d.getFontMetrics(g2d.getFont()).stringWidth((String)this.mKeyButtons[i].getContents())) / 2.0f);
			g2d.drawString((String)this.mKeyButtons[i].getContents(), this.mKeyButtons[i].getLeft() + xDiff, this.mKeyButtons[i].getTop() + y + yDiff);
		}
		return;
	}
	
	private void renderKeyBindingLabels(Graphics2D g2d)
	{
		FontMetrics metrics = null;
		g2d.setFont(this.mLabelFont);
		g2d.setPaint(Skin.LARGE_LABEL_FONT_COLOR);
		for(int i = 0; i < KeyButton.values().length; i++)
		{
			metrics = g2d.getFontMetrics(g2d.getFont());
			int xOffset = metrics.stringWidth(KeyButton.values()[i].getCaption());
			int yOffset = metrics.getHeight();
			g2d.drawString(KeyButton.values()[i].getCaption(), 
							(int)Math.round(this.mScreenBounds.width * KeyButton.values()[i].getLabelXFactor()) - xOffset, 
							(int)Math.round(this.mScreenBounds.height * KeyButton.values()[i].getLabelYFactor()) + yOffset);
		}
		return;
	}
	
	private void renderMessageWindow(Graphics2D g2d)
	{
		if(this.mMessageWindow == null){
			return;
		}
		this.mMessageWindow.render(g2d);
		return;
	}
	
	@Override
	public void update()
	{
		super.update();
		return;
	}
	
	private void initializeOptionsFonts()
	{
		this.mPlainFont = Skin.TEXT_BOX_FONT; 
		this.mPlainFont = this.mPlainFont.deriveFont(Skin.TEXT_BOX_FONT_STYLE); 
		this.mPlainFont = this.mPlainFont.deriveFont((float)FontSizer.getFontSize(Skin.TEXT_BOX_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mLabelFont = Skin.OPTIONS_LABEL_FONT;
		this.mLabelFont = this.mLabelFont.deriveFont(Skin.OPTIONS_LABEL_FONT_STYLE);
		this.mLabelFont = this.mLabelFont.deriveFont((float)FontSizer.getFontSize(Skin.OPTIONS_LABEL_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mMessageWindowFont = Skin.MESSAGE_WINDOW_FONT;
		this.mMessageWindowFont = this.mMessageWindowFont.deriveFont(Skin.MESSAGE_WINDOW_FONT_STYLE);
		this.mMessageWindowFont = this.mMessageWindowFont.deriveFont((float)FontSizer.getFontSize(Skin.MESSAGE_WINDOW_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mNormalMessageButtonFont = Skin.NORMAL_MESSAGE_WINDOW_BUTTON_FONT;
		this.mNormalMessageButtonFont = this.mNormalMessageButtonFont.deriveFont(Skin.NORMAL_MESSAGE_WINDOW_BUTTON_FONT_STYLE);
		this.mNormalMessageButtonFont = this.mNormalMessageButtonFont.deriveFont((float)FontSizer.getFontSize(Skin.NORMAL_MESSAGE_WINDOW_BUTTON_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mHoveredMessageButtonFont = Skin.HOVERED_MESSAGE_WINDOW_BUTTON_FONT;
		this.mHoveredMessageButtonFont = this.mHoveredMessageButtonFont.deriveFont(Skin.HOVERED_MESSAGE_WINDOW_BUTTON_FONT_STYLE);
		this.mHoveredMessageButtonFont = this.mHoveredMessageButtonFont.deriveFont((float)FontSizer.getFontSize(Skin.HOVERED_MESSAGE_WINDOW_BUTTON_FONT_FACTOR, this.mScreenBounds.height));
		
		this.mSelectedMessageButtonFont = Skin.SELECTED_MESSAGE_WINDOW_BUTTON_FONT;
		this.mSelectedMessageButtonFont = this.mSelectedMessageButtonFont.deriveFont(Skin.SELECTED_MESSAGE_WINDOW_BUTTON_FONT_STYLE);
		this.mSelectedMessageButtonFont = this.mSelectedMessageButtonFont.deriveFont((float)FontSizer.getFontSize(Skin.SELECTED_MESSAGE_WINDOW_BUTTON_FONT_FACTOR, this.mScreenBounds.height));
		return;
	}
	
	private void initializeButtons()
	{
		int count = 0;
		for(ButtonType type : ButtonType.values())
		{
			PItem button = new PItem(BUTTON_TEMPLATE, type.getCaption());
			button.setType(type);
			this.mButtons[count] = button;
			count++;
		}
		
		int x = (int)Math.round(this.mScreenBounds.width * BUTTON_COLUMN_X_FACTOR);
		int y = (int)Math.round(this.mScreenBounds.height * BUTTON_COLUMN_Y_FACTOR);
		int width = (int)Math.round(this.mScreenBounds.width * BUTTON_SIZE_FACTORS.x);
		int height = (int)Math.round(this.mScreenBounds.height * BUTTON_SIZE_FACTORS.y);
		int increment = (int)Math.round(this.mScreenBounds.height * BUTTON_Y_FACTOR);
		this.initializeMenuFont(height);
		
		for(int i = 0 ; i < this.mButtons.length; i++)
		{
			this.mButtons[i].setLocation(x, y + (increment * i));
			this.mButtons[i].resizePolygon(width, height);
		}
		return;
	}
	
	private void initializeMenuFont(int buttonHeight)
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
	
	private void initializeControlArea()
	{
		this.mControlArea = new PolyShape(CONTROL_AREA_TEMPLATE.xpoints, CONTROL_AREA_TEMPLATE.ypoints, CONTROL_AREA_TEMPLATE.npoints);
		this.mControlArea.setLocation((int)Math.round(this.mScreenBounds.width * CONTROL_AREA_FACTORS.x),
										(int)Math.round(this.mScreenBounds.height * CONTROL_AREA_FACTORS.y));
		this.mControlArea.resizePolygon((int)Math.round(this.mScreenBounds.width * CONTROL_AREA_FACTORS.width),
										(int)Math.round(this.mScreenBounds.height * CONTROL_AREA_FACTORS.height));
		return;
	}
	
	private void initializeGraphicsModeTextBox()
	{	
		this.mDisplayModeTextBox = new PolyShape(DISPLAY_MODE_TEXT_BOX_TEMPLATE.xpoints, DISPLAY_MODE_TEXT_BOX_TEMPLATE.ypoints, DISPLAY_MODE_TEXT_BOX_TEMPLATE.npoints);
		this.mDisplayModeTextBox.setLocation((int)Math.round(this.mScreenBounds.width * DISPLAY_MODE_TEXT_BOX_FACTORS.getX()), 
											(int)Math.round(this.mScreenBounds.height * DISPLAY_MODE_TEXT_BOX_FACTORS.getY()));
		this.mDisplayModeTextBox.resizePolygon((int)Math.round(this.mScreenBounds.width * DISPLAY_MODE_TEXT_BOX_FACTORS.getWidth()), 
												(int)Math.round(this.mScreenBounds.height * DISPLAY_MODE_TEXT_BOX_FACTORS.getHeight()));
		
		return;
	}
	
	private void initializeDisplayModeList()
	{
		Vector<ModeListRow> rows = this.initializeDisplayModeListItems();
		
		this.mDisplayModeList = new PList();
		this.mDisplayModeList.setCellTemplate(MODE_LIST_CELL_TEMPLATE);
		this.mDisplayModeListModel = new DisplayModeListModel(rows);
		this.mDisplayModeList.setModel(this.mDisplayModeListModel);
		this.mDisplayModeList.setLocation((int)Math.round(this.mScreenBounds.width * DISPLAY_MODE_SCROLLPANE_FACTORS.x), this.mDisplayModeTextBox.getBottom() + MODE_LIST_Y_OFFSET);
		this.mDisplayModeList.setSize((int)Math.round(this.mScreenBounds.width * DISPLAY_MODE_SCROLLPANE_FACTORS.width), (int)Math.round(this.mScreenBounds.height * DISPLAY_MODE_SCROLLPANE_FACTORS.height));
		
		this.mDisplayModeList.setListBackground(null);
		this.mDisplayModeList.setItemSpacing(4);
		this.mDisplayModeList.setItemSize((int)Math.round(this.mScreenBounds.width * MODE_LIST_CELL_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * MODE_LIST_CELL_HEIGHT_FACTOR));
		this.mDisplayModeList.setCellAlignment(PAlign.CENTER);
		this.mDisplayModeList.setNormalCellBackground(Skin.NORMAL_CELL_BACKGROUND);
		this.mDisplayModeList.setNormalCellBorder(Skin.NORMAL_CELL_BORDER);
		this.mDisplayModeList.setNormalCellFont(Skin.NORMAL_CELL_FONT);
		this.mDisplayModeList.setNormalCellFont(this.mDisplayModeList.getNormalCellFont().deriveFont(Skin.NORMAL_CELL_FONT_STYLE));
		this.mDisplayModeList.setNormalCellFont(this.mDisplayModeList.getNormalCellFont().deriveFont((float)FontSizer.getFontSize(Skin.NORMAL_CELL_FONT_FACTOR, this.mScreenBounds.height)));
		this.mDisplayModeList.setNormalCellStroke(Skin.NORMAL_CELL_STROKE);
		this.mDisplayModeList.setNormalCellText(Skin.NORMAL_CELL_TEXT);
		this.mDisplayModeList.setHoveredCellBackground(Skin.HOVERED_CELL_BACKGROUND);
		this.mDisplayModeList.setHoveredCellBorder(Skin.HOVERED_CELL_BORDER);
		this.mDisplayModeList.setHoveredCellFont(Skin.HOVERED_CELL_FONT);
		this.mDisplayModeList.setHoveredCellFont(this.mDisplayModeList.getHoveredCellFont().deriveFont(Skin.HOVERED_CELL_FONT_STYLE));
		this.mDisplayModeList.setHoveredCellFont(this.mDisplayModeList.getHoveredCellFont().deriveFont((float)FontSizer.getFontSize(Skin.HOVERED_CELL_FONT_FACTOR, this.mScreenBounds.height)));
		this.mDisplayModeList.setHoveredCellStroke(Skin.HOVERED_CELL_STROKE);
		this.mDisplayModeList.setHoveredCellText(Skin.HOVERED_CELL_TEXT);
		this.mDisplayModeList.setSelectedCellBackground(Skin.SELECTED_CELL_BACKGROUND);
		this.mDisplayModeList.setSelectedCellBorder(Skin.SELECTED_CELL_BORDER);
		this.mDisplayModeList.setSelectedCellFont(Skin.SELECTED_CELL_FONT);
		this.mDisplayModeList.setSelectedCellFont(this.mDisplayModeList.getSelectedCellFont().deriveFont(Skin.SELECTED_CELL_FONT_STYLE));
		this.mDisplayModeList.setSelectedCellFont(this.mDisplayModeList.getSelectedCellFont().deriveFont((float)FontSizer.getFontSize(Skin.SELECTED_CELL_FONT_FACTOR, this.mScreenBounds.height)));
		this.mDisplayModeList.setSelectedCellStroke(Skin.SELECTED_CELL_STROKE);
		this.mDisplayModeList.setSelectedCellText(Skin.SELECTED_CELL_TEXT);
		this.mDisplayModeList.invalidate();
		//this.mDisplayModeList.layoutVisibleList();
		
		this.mDisplayListArea.x = this.mDisplayModeList.getBounds().x;
		this.mDisplayListArea.y = this.mDisplayModeList.getBounds().y;
		this.mDisplayListArea.width = this.mDisplayModeList.getBounds().width;
		// Item height + spacing * visible rows -- hides list transition flicker.
		int height = (this.mDisplayModeList.getItemHeight() + this.mDisplayModeList.getItemSpacing()) * DISPLAY_MODE_LIST_VISIBLE_ROWS;
		this.mDisplayListArea.height = height;
		
		this.mDisplayModeScroll = new PScrollBar(PScrollBar.Orientation.VERTICAL);
		this.mDisplayModeScroll.setTrackTemplate(VERTICAL_SCROLL_TRACK_TEMPLATE);
		this.mDisplayModeScroll.setGripTemplate(VERTICAL_SCROLL_GRIP_TEMPLATE);
		this.mDisplayModeScroll.setDecrementButtonTemplate(VERTICAL_SCROLL_DECREMENT_TEMPLATE);
		this.mDisplayModeScroll.setIncrementButtonTemplate(VERTICAL_SCROLL_INCREMENT_TEMPLATE);
		this.mDisplayModeScroll.setNormalTrackBackground(Skin.NORMAL_SCROLL_TRACK_BACKGROUND);
		this.mDisplayModeScroll.setNormalTrackBorder(Skin.NORMAL_SCROLL_TRACK_BORDER);
		this.mDisplayModeScroll.setNormalTrackStroke(Skin.NORMAL_SCROLL_TRACK_STROKE);
		this.mDisplayModeScroll.setNormalDecrementColor(Skin.NORMAL_INCREMENT_DECREMENT_BUTTON_BACKGROUND);
		this.mDisplayModeScroll.setNormalIncrementColor(Skin.NORMAL_INCREMENT_DECREMENT_BUTTON_BACKGROUND);
		this.mDisplayModeScroll.setNormalGripColor(Skin.NORMAL_SCROLL_GRIP_COLOR);
		this.mDisplayModeScroll.setHoveredTrackBackground(Skin.HOVERED_SCROLL_TRACK_BACKGROUND);
		this.mDisplayModeScroll.setHoveredTrackBorder(Skin.HOVERED_SCROLL_TRACK_BORDER);
		this.mDisplayModeScroll.setHoveredTrackStroke(Skin.HOVERED_SCROLL_TRACK_STROKE);
		this.mDisplayModeScroll.setHoveredDecrementColor(Skin.HOVERED_INCREMENT_DECREMENT_BACKGROUND);
		this.mDisplayModeScroll.setHoveredIncrementColor(Skin.HOVERED_INCREMENT_DECREMENT_BACKGROUND);
		this.mDisplayModeScroll.setHoveredGripColor(Skin.HOVERED_SCROLL_GRIP_COLOR);
		this.mDisplayModeScroll.setSelectedTrackBackground(Skin.SELECTED_SCROLL_TRACK_BACKGROUND);
		this.mDisplayModeScroll.setSelectedTrackBorder(Skin.SELECTED_SCROLL_TRACK_BORDER);
		this.mDisplayModeScroll.setSelectedTrackStroke(Skin.SELECTED_SCROLL_TRACK_STROKE);
		this.mDisplayModeScroll.setSelectedDecrementColor(Skin.SELECTED_DECREMENT_INCREMENT_BACKGROUND);
		this.mDisplayModeScroll.setSelectedIncrementColor(Skin.SELECTED_DECREMENT_INCREMENT_BACKGROUND);
		this.mDisplayModeScroll.setSelectedGripColor(Skin.SELECTED_SCROLL_GRIP_COLOR);
		this.mDisplayModeScroll.setLocation(this.mDisplayModeList.getLocationX() + this.mDisplayModeList.getBounds().width + VERTICAL_SCROLL_X_OFFSET, this.mDisplayModeTextBox.getBottom() + MODE_LIST_Y_OFFSET);
		height = (this.mDisplayModeList.getItemHeight() + this.mDisplayModeList.getItemSpacing()) * DISPLAY_MODE_LIST_VISIBLE_ROWS;
		this.mDisplayModeScroll.setSize((int)Math.round(this.mScreenBounds.width * VERTICAL_SCROLL_FACTORS.width), height);
		return;
	}
	
	private Vector<ModeListRow> initializeDisplayModeListItems()
	{
		Vector<ModeListRow> rows = new Vector<ModeListRow>();
		for(int i = 0; i < mModes.size(); i++)
		{
			ModeListRow row = new ModeListRow();
			row.mode = mModes.get(i);
			row.text = this.printDisplayMode(mModes.get(i));
			rows.add(row);
		}
		return rows;
	}
	
	private void initializeBooleanControl(PolyShape prev, PolyShape next, Point2D.Float factors, Rectangle mouseArea)
	{
		prev.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		next.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		
		BufferedImage result = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)result.getGraphics();
		g2d.setFont(this.mLabelFont);
		int height = g2d.getFontMetrics(g2d.getFont()).getHeight();
		int y = g2d.getFontMetrics(g2d.getFont()).getDescent();
		prev.setLocation((int)Math.round(this.mScreenBounds.getWidth() * factors.x), (int)Math.round(this.mScreenBounds.height * factors.y) - (height - y));
		next.setLocation((this.mDisplayModeList.getBounds().x + this.mDisplayModeList.getBounds().width) - next.getWidth(), prev.getTop());
		
		mouseArea.x = prev.getLeft();
		mouseArea.y = prev.getTop();
		mouseArea.width = next.getRight() - prev.getLeft();
		mouseArea.height = prev.getHeight();
		
		g2d.dispose();
		return;
	}
	
	private void initializeVSyncItems()
	{
		this.mVSyncPrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mVSyncPrevButton.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		this.mVSyncNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.mVSyncNextButton.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		
		BufferedImage result = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)result.getGraphics();
		g2d.setFont(this.mLabelFont);
		int width = g2d.getFontMetrics(g2d.getFont()).stringWidth(VERTICAL_SYNC_LABEL_CAPTION);
		int height = g2d.getFontMetrics(g2d.getFont()).getHeight();
		int y = g2d.getFontMetrics(g2d.getFont()).getDescent();
		this.mVSyncPrevButton.setLocation(this.mDisplayModeTextBox.getLeft() + width + 30, (int)Math.round(this.mScreenBounds.height * VERTICAL_SYNC_LABEL_FACTORS.y) - (height - y));
		this.mVSyncNextButton.setLocation((this.mDisplayModeList.getBounds().x + this.mDisplayModeList.getBounds().width) - this.mVSyncNextButton.getWidth(), this.mVSyncPrevButton.getTop());
		
		this.mVSyncButtonArea.x = this.mVSyncPrevButton.getLeft();
		this.mVSyncButtonArea.y = this.mVSyncPrevButton.getTop();
		this.mVSyncButtonArea.width = this.mVSyncNextButton.getRight() - this.mVSyncPrevButton.getLeft();
		this.mVSyncButtonArea.height = this.mVSyncPrevButton.getHeight();
		
		g2d.dispose();
		return;
	}
	
	private void initializeAdvancedItems()
	{
		this.mRenderQualityPrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mRenderQualityNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.initializeBooleanControl(this.mRenderQualityPrevButton, this.mRenderQualityNextButton, RENDER_QUALITY_CONTROL_FACTORS, this.mRenderQualityArea);
		
		this.mAntialiasPrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mAntialiasNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.initializeBooleanControl(this.mAntialiasPrevButton, this.mAntialiasNextButton, ANTIALIAS_CONTROL_FACTORS, this.mAntialiasArea);
		
		this.mRenderColorPrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mRenderColorNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.initializeBooleanControl(this.mRenderColorPrevButton, this.mRenderColorNextButton, RENDER_COLOR_CONTROL_FACTORS, this.mRenderColorArea);
		
		this.mRenderAlphaPrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mRenderAlphaNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.initializeBooleanControl(this.mRenderAlphaPrevButton, this.mRenderAlphaNextButton, RENDER_ALPHA_CONTROL_FACTORS, this.mRenderAlphaArea);
		
		this.mShowFPSPrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mShowFPSNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.initializeBooleanControl(this.mShowFPSPrevButton, this.mShowFPSNextButton, SHOW_FPS_CONTROL_FACTORS, this.mShowFPSArea);
		return;
	}
	
	private void initializeVolumeLevelItems()
	{
		this.mVolumeDownButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mVolumeDownButton.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		this.mVolumeUpButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.mVolumeUpButton.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		
		BufferedImage result = this.mGC.createCompatibleImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)result.getGraphics();
		g2d.setFont(this.mLabelFont);
		int height = g2d.getFontMetrics(g2d.getFont()).getHeight();
		int y = g2d.getFontMetrics(g2d.getFont()).getDescent();
		this.mVolumeDownButton.setLocation((int)Math.round(this.mScreenBounds.getWidth() * VOLUME_CONTROL_FACTORS.x), (int)Math.round(this.mScreenBounds.height * VOLUME_CONTROL_FACTORS.y) - (height - y));
		this.mVolumeUpButton.setLocation((this.mDisplayModeList.getBounds().x + this.mDisplayModeList.getBounds().width) - this.mVolumeUpButton.getWidth(), this.mVolumeDownButton.getTop());
		
		this.mVolumeRectangle.x = this.mVolumeDownButton.getLeft();
		this.mVolumeRectangle.y = this.mVolumeDownButton.getTop();
		this.mVolumeRectangle.width = this.mVolumeUpButton.getRight() - this.mVolumeDownButton.getLeft();
		this.mVolumeRectangle.height = this.mVolumeDownButton.getHeight();
		
		this.mVolumeDownArea.x = this.mVolumeDownButton.getLeft();
		this.mVolumeDownArea.y = this.mVolumeDownButton.getTop();
		this.mVolumeDownArea.width = this.mVolumeDownButton.getWidth();
		this.mVolumeDownArea.height = this.mVolumeDownButton.getHeight();
		
		this.mVolumeUpArea.x = this.mVolumeUpButton.getLeft();
		this.mVolumeUpArea.y = this.mVolumeUpButton.getTop();
		this.mVolumeUpArea.width = this.mVolumeUpButton.getWidth();
		this.mVolumeUpArea.height = this.mVolumeUpButton.getHeight();
		
		this.mMutePrevButton = new PItem(PREV_BUTTON_TEMPLATE, null);
		this.mMutePrevButton.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		this.mMuteNextButton = new PItem(NEXT_BUTTON_TEMPLATE, null);
		this.mMuteNextButton.resizePolygon((int)Math.round(this.mScreenBounds.width * PREV_NEXT_BUTTON_WIDTH_FACTOR), (int)Math.round(this.mScreenBounds.height * PREV_NEXT_BUTTON_HEIGHT_FACTOR));
		
		this.mMutePrevButton.setLocation((int)Math.round(this.mScreenBounds.getWidth() * MUTE_SOUND_FACTORS.x), (int)Math.round(this.mScreenBounds.height * MUTE_SOUND_FACTORS.y));
		this.mMuteNextButton.setLocation((this.mDisplayModeList.getBounds().x + this.mDisplayModeList.getBounds().width) - this.mMuteNextButton.getWidth(), (int)Math.round(this.mScreenBounds.height * MUTE_SOUND_FACTORS.y));
		
		this.mMuteRectangle.x = this.mMutePrevButton.getLeft();
		this.mMuteRectangle.y = this.mMutePrevButton.getTop();
		this.mMuteRectangle.width = this.mMuteNextButton.getRight() - this.mMutePrevButton.getLeft();
		this.mMuteRectangle.height = this.mMutePrevButton.getHeight();
		
		g2d.dispose();
		return;
	}
	
	private void initializeKeyboardItems()
	{
		int height = 0;
		for(int i = 0; i < KeyButton.values().length; i++)
		{
			if(this.mKeyBindings.get(KeyButton.values()[i].getKeyName()) == UNDEFINED_KEY_CODE){
				this.mKeyButtons[i] = new PItem(KEY_BUTTON_TEMPLATE, UNDEFINED_ITEM_CAPTION);
			}else{
				this.mKeyButtons[i] = new PItem(KEY_BUTTON_TEMPLATE, KeyEvent.getKeyText(this.mKeyBindings.get(KeyButton.values()[i].getKeyName())));
			}
			this.mKeyButtons[i].setType(KeyButton.values()[i]);
			int x = (int)Math.round(this.mScreenBounds.width * KeyButton.values()[i].getXFactor());
			int y = (int)Math.round(this.mScreenBounds.height * KeyButton.values()[i].getYFactor());
			int width = (int)Math.round(this.mScreenBounds.width * KeyButton.values()[i].getWidthFactor());
			height = (int)Math.round(this.mScreenBounds.height * KeyButton.values()[i].getHeightFactor());
			this.mKeyButtons[i].resizePolygon(width, height);
			this.mKeyButtons[i].setLocation(x, y);
		}
		this.initializeKeyButtonFonts(height);
		return;
	}
	
	private void initializeKeyButtonFonts(int buttonHeight)
	{
		this.mNormalKeyButtonFont = Skin.KEY_BINDING_BUTTON_FONT;
		this.mNormalKeyButtonFont = this.mNormalKeyButtonFont.deriveFont(Skin.KEY_BINDING_BUTTON_FONT_STYLE);
		this.mNormalKeyButtonFont = this.mNormalKeyButtonFont.deriveFont((float)FontSizer.getFontSize(BUTTON_FONT_FACTOR, buttonHeight));
		
		this.mHoveredKeyButtonFont = Skin.KEY_BINDING_BUTTON_FONT;
		this.mHoveredKeyButtonFont = this.mHoveredKeyButtonFont.deriveFont(Skin.KEY_BINDING_BUTTON_FONT_STYLE);
		this.mHoveredKeyButtonFont = this.mHoveredKeyButtonFont.deriveFont((float)FontSizer.getFontSize(BUTTON_FONT_FACTOR, buttonHeight));
		
		this.mSelectedKeyButtonFont = Skin.KEY_BINDING_BUTTON_FONT;
		this.mSelectedKeyButtonFont = this.mSelectedKeyButtonFont.deriveFont(Skin.KEY_BINDING_BUTTON_FONT_STYLE);
		this.mSelectedKeyButtonFont = this.mSelectedKeyButtonFont.deriveFont((float)FontSizer.getFontSize(BUTTON_FONT_FACTOR, buttonHeight));
		return;
	}
	
	private String printDisplayMode(DisplayMode mode)
	{
		return new String(mode.getWidth() + " x " + mode.getHeight() + " " + mode.getBitDepth() + "-bit " + mode.getRefreshRate() + "Hz");
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
		this.mListener.switchAppStates(AppState.MAIN_MENU);
		return;
	}
	
	public PItem[] getButtons()
	{
		return this.mButtons;
	}
	
	public PList getDisplayModeList()
	{
		return this.mDisplayModeList;
	}
	
	public Category getCategory()
	{
		return this.mCategory;
	}
	
	public PolyShape getControlArea()
	{
		return this.mControlArea;
	}
	
	public PScrollBar getScrollBar()
	{
		return this.mDisplayModeScroll;
	}
	
	public void setDisplayModeListScrollY(double scroll)
	{
		this.mDisplayModeScrollY = (int)Math.round(this.mDisplayModeList.getListSize().height * scroll);
		this.mDisplayModeList.setPointer(this.mDisplayModeScrollY);
		return;
	}
	
	public int getDisplayModeListScrollY()
	{
		return this.mDisplayModeScrollY;
	}
	
	public void setModeBoxText(String text)
	{
		this.mModeBoxText = text;
		this.mDisplayModeChanged = true;
		return;
	}
	
	public void findDisplayModeValue(int index)
	{
		this.mDisplayModeValue = ((DisplayModeListModel)this.mDisplayModeList.getModel()).getDisplayMode(index);
		return;
	}
	
	public Rectangle getVSyncButtonArea()
	{
		return this.mVSyncButtonArea;
	}
	
	public PItem getVSyncPrevButton()
	{
		return this.mVSyncPrevButton;
	}
	
	public PItem getVSyncNextButton()
	{
		return this.mVSyncNextButton;
	}
	
	public void toggleVSyncValue()
	{
		this.mVSyncChecked = !this.mVSyncChecked;
		this.mVSyncValueHasChanged = true;
		return;
	}
	
	public void toggleRenderQualityValue()
	{
		this.mRenderQualityValue = this.mRenderQualityValue == RenderQuality.HIGH ? RenderQuality.LOW : RenderQuality.HIGH;
		this.mRenderQualityHasChanged = true;
		return;
	}
	
	public void toggleAntialiasValue()
	{
		this.mAntialiasValue = this.mAntialiasValue == RenderAntialias.ON ? RenderAntialias.OFF : RenderAntialias.ON;
		this.mAntialiasHasChanged = true;
		return;
	}
	
	public void toggleRenderColorValue()
	{
		this.mRenderColorValue = this.mRenderColorValue == RenderColor.HIGH ? RenderColor.LOW : RenderColor.HIGH;
		this.mRenderColorHasChanged = true;
		return;
	}
	
	public void toggleRenderAlphaValue()
	{
		this.mRenderAlphaValue = this.mRenderAlphaValue == RenderAlpha.HIGH ? RenderAlpha.LOW : RenderAlpha.HIGH;
		this.mRenderAlphaHasChanged = true;
		return;
	}
	
	public void toggleShowFPSValue()
	{
		this.mShowFPSValue = !this.mShowFPSValue;
		this.mShowFPSHasChanged = true;
		return;
	}
	
	public Rectangle getRenderQualityArea()
	{
		return this.mRenderQualityArea;
	}
	
	public PItem getRenderQualityPrevButton()
	{
		return this.mRenderQualityPrevButton;
	}
	
	public PItem getRenderQualityNextButton()
	{
		return this.mRenderQualityNextButton;
	}
	
	public Rectangle getAntialiasArea()
	{
		return this.mAntialiasArea;
	}
	
	public PItem getAntialiasPrevButton()
	{
		return this.mAntialiasPrevButton;
	}
	
	public PItem getAntialiasNextButton()
	{
		return this.mAntialiasNextButton;
	}
	
	public Rectangle getRenderColorArea()
	{
		return this.mRenderColorArea;
	}
	
	public PItem getRenderColorPrevButton()
	{
		return this.mRenderColorPrevButton;
	}
	
	public PItem getRenderColorNextButton()
	{
		return this.mRenderColorNextButton;
	}
	
	public Rectangle getRenderAlphaArea()
	{
		return this.mRenderAlphaArea;
	}
	
	public PItem getRenderAlphaPrevButton()
	{
		return this.mRenderAlphaPrevButton;
	}
	
	public PItem getRenderAlphaNextButton()
	{
		return this.mRenderAlphaNextButton;
	}
	
	public PItem getShowFPSPrevButton()
	{
		return this.mShowFPSPrevButton;
	}
	
	public PItem getShowFPSNextButton()
	{
		return this.mShowFPSNextButton;
	}
	
	public Rectangle getShowFPSArea()
	{
		return this.mShowFPSArea;
	}
	
	public Rectangle getVolumeRectangle()
	{
		return this.mVolumeRectangle;
	}
	
	public Rectangle getVolumeDownArea()
	{
		return this.mVolumeDownArea;
	}
	
	public Rectangle getVolumeUpArea()
	{
		return this.mVolumeUpArea;
	}
	
	public PItem getVolumeDownButton()
	{
		return this.mVolumeDownButton;
	}
	
	public PItem getVolumeUpButton()
	{
		return this.mVolumeUpButton;
	}
	
	public PItem getMutePrevButton()
	{
		return this.mMutePrevButton;
	}
	
	public PItem getMuteNextButton()
	{
		return this.mMuteNextButton;
	}
	
	public Rectangle getMuteRectangle()
	{
		return this.mMuteRectangle;
	}
	
	public void volumeDownPressed()
	{
		/*
		 * Worst math ever.  I really need to work this differently in the future. 
		 */
		float x = (float)Math.floor(this.mCurrentVolumeLevel * 10.0f);
		x = x - VOLUME_INCREMENT < 0.0f ? 0.0f : x - VOLUME_INCREMENT;
		this.mCurrentVolumeLevel = x / 10.0f;
		this.mVolumeChanged = true;
		this.playTestSound();
		return;
	}
	
	public void volumeUpPressed()
	{
		/*
		 * Worst math ever.  I really need to work this differently in the future.
		 */
		float x = (float)Math.floor(this.mCurrentVolumeLevel * 10.0f);
		x = x + VOLUME_INCREMENT > 10.0f ? 10.0f : x + VOLUME_INCREMENT;
		this.mCurrentVolumeLevel = x / 10.0f;
		this.mVolumeChanged = true;
		this.playTestSound();
		return;
	}
	
	private void playTestSound()
	{
		this.mListener.getSFX().setVolume(this.mCurrentVolumeLevel);
		this.mListener.getSFX().playCannonClip();
		return;
	}
	
	public void toggleMuteValue()
	{
		this.mMuteOn = !this.mMuteOn;
		this.mMuteValueHasChanged = true;
		return;
	}
	
	public PItem[] getKeyButtons()
	{
		return this.mKeyButtons;
	}
	
	public void setKeyBinding(int keyButton, int keyCode)
	{
		this.mKeyBindings.put(KeyButton.values()[keyButton].getKeyName(), keyCode);
		this.adjustKeyBindings(keyButton, keyCode);
		this.mKeyBindingHasChanged = true;
		this.mKeyButtons = new PItem[KeyButton.values().length];
		this.initializeKeyboardItems();
		return;
	}
	
	/**
	 * This function checks for duplicate key bindings and assigns the UNDEFINED_KEY_CODE value to them,
	 * except for the binding associated with buttonIndex.
	 * @param buttonIndex the button which the key code is to be bound.
	 * @param keyCode int returned by KeyEvent.
	 */
	private void adjustKeyBindings(int buttonIndex, int keyCode)
	{
		for(int i = 0; i < KeyButton.values().length; i++)
		{
			if(i == buttonIndex){
				continue;
			}
			if(this.mKeyBindings.get(KeyButton.values()[i].getKeyName()) == keyCode){
				this.mKeyBindings.put(KeyButton.values()[i].getKeyName(), UNDEFINED_KEY_CODE);
			}
		}
		return;
	}
	
	public PMessage getMessageWindow()
	{
		return this.mMessageWindow;
	}
	
	public void messageWindowButtonPressed(PMessage.ButtonType button)
	{
		this.closeMessageWindow();
		if(button == PMessage.ButtonType.YES){
			this.mListener.switchAppStates(AppState.MAIN_MENU);
		}
		return;
	}
	
	private void showMessageWindow()
	{
		this.mMessageWindow = new PMessage("Discard the changes you've made?", PMessage.MessageType.YES_NO, MESSAGE_WINDOW_TEMPLATE, MESSAGE_WINDOW_BUTTON_TEMPLATE);
		this.mMessageWindow.setBackground(Skin.MESSAGE_WINDOW_BACKGROUND);
		this.mMessageWindow.setBorderColor(Skin.MESSAGE_WINDOW_BORDER_COLOR);
		this.mMessageWindow.setForeground(Skin.MESSAGE_WINDOW_FOREGROUND);
		this.mMessageWindow.setMessageFont(this.mMessageWindowFont);
		this.mMessageWindow.setNormalButtonBackground(Skin.NORMAL_MESSAGE_WINDOW_BUTTON_BACKGROUND);
		this.mMessageWindow.setNormalButtonForeground(Skin.NORMAL_MESSAGE_WINDOW_BUTTON_FOREGROUND);
		this.mMessageWindow.setNormalButtonBorderColor(Skin.NORMAL_MESSAGE_WINDOW_BUTTON_BORDER);
		this.mMessageWindow.setNormalButtonFont(this.mNormalMessageButtonFont);
		this.mMessageWindow.setHoveredButtonBackground(Skin.HOVERED_MESSAGE_WINDOW_BUTTON_BACKGROUND);
		this.mMessageWindow.setHoveredButtonForeground(Skin.HOVERED_MESSAGE_WINDOW_BUTTON_FOREGROUND);
		this.mMessageWindow.setHoveredButtonBorderColor(Skin.HOVERED_MESSAGE_WINDOW_BUTTON_BORDER);
		this.mMessageWindow.setHoveredButtonFont(this.mHoveredMessageButtonFont);
		this.mMessageWindow.setSelectedButtonBackground(Skin.SELECTED_MESSAGE_WINDOW_BUTTON_BACKGROUND);
		this.mMessageWindow.setSelectedButtonForeground(Skin.SELECTED_MESSAGE_WINDOW_BUTTON_FOREGROUND);
		this.mMessageWindow.setSelectedButtonBorderColor(Skin.SELECTED_MESSAGE_WINDOW_BUTTON_BORDER);
		this.mMessageWindow.setSelectedButtonFont(this.mSelectedMessageButtonFont);
		this.mMessageWindow.setPosition(PMessage.Position.CENTER_SCREEN);
		this.mMessageWindow.invalidate();
		this.mUI.setToMessageWindowMode();
		return;
	}
	
	public void closeMessageWindow()
	{
		this.mMessageWindow = null;
		this.mUI.setToMenuMode();
		return;
	}
	
	private boolean checkForChanges()
	{
		if(this.mDisplayModeChanged){
			return true;
		}else if(this.mAntialiasHasChanged){
			return true;
		}else if(this.mMuteValueHasChanged){
			return true;
		}else if(this.mRenderAlphaHasChanged){
			return true;
		}else if(this.mRenderColorHasChanged){
			return true;
		}else if(this.mRenderQualityHasChanged){
			return true;
		}else if(this.mShowFPSHasChanged){
			return true;
		}else if(this.mVolumeChanged){
			return true;
		}else if(this.mVSyncValueHasChanged){
			return true;
		}else if(this.mKeyBindingHasChanged){
			return true;
		}
		return false;
	}
	
	public void applyChanges()
	{
		Settings changes = new Settings();
		changes.displayMode = this.mDisplayModeValue;
		changes.renderQuality = this.mRenderQualityValue;
		changes.renderAntialias = this.mAntialiasValue;
		changes.renderAlpha = this.mRenderAlphaValue;
		changes.renderColor = this.mRenderColorValue;
		changes.showPFS = this.mShowFPSValue;
		changes.useVSync = this.mVSyncChecked;
		for(KeyNames name : KeyNames.values())
		{
			changes.keyBindings.put(name, this.mKeyBindings.get(name));
		}
		changes.volume = this.mCurrentVolumeLevel;
		changes.muteSound = this.mMuteOn;
		this.mListener.reinitializeApp(changes);
		return;
	}
	
	public void optionsMenuButtonPressed(Object type)
	{
		if(!(type instanceof OptionsWindow.ButtonType)){
			return;
		}
		if(OptionsWindow.ButtonType.ADVANCED == type){
			this.mControlState = this.mAdvancedOptionsMode;
			this.mCategory = Category.ADVANCED;
		}else if(OptionsWindow.ButtonType.APPLY == type){
			this.applyChanges();
		}else if(OptionsWindow.ButtonType.BACK == type){
			if(this.checkForChanges()){
				this.showMessageWindow();
			}else{
				this.mListener.switchAppStates(AppState.MAIN_MENU);
			}
		}else if(OptionsWindow.ButtonType.CONTROLS == type){
			this.mControlState = this.mKeyBindingsMode;
			this.mCategory = Category.CONTROLS;
		}else if(OptionsWindow.ButtonType.GRAPHICS == type){
			this.mControlState = this.mGraphicsOptionsMode;
			this.mCategory = Category.GRAPHICS;
		}else if(OptionsWindow.ButtonType.SOUND == type){
			this.mControlState = this.mAudioOptionsMode;
			this.mCategory = Category.SOUND;
		}
		return;
	}
	
	private class DisplayModeListModel implements PAbstractListModel
	{
		private final String[] COLUMN_NAMES = new String[]{"Display Mode"};
		private Vector<ModeListRow> mRows = null;
		
		public DisplayModeListModel()
		{
			this.mRows = new Vector<ModeListRow>();
		}
		
		public DisplayModeListModel(Vector<ModeListRow> rows)
		{
			this.setData(rows);
		}
		
		public void setData(Vector<ModeListRow> rows)
		{
			this.mRows = rows;
			return;
		}
		
		public int getItemCount()
		{
			return this.mRows.size();
		}
		
		public int getColumnCount()
		{
			return COLUMN_NAMES.length;
		}
		
		public String getColumnName(int index)
		{
			return COLUMN_NAMES[index];
		}
		
		public Object getItemAt(int row)
		{
			return this.mRows.get(row).text;
		}
		
		public DisplayMode getDisplayMode(int index)
		{
			return this.mRows.get(index).mode;
		}
	}
	
	private class ModeListRow
	{
		public DisplayMode mode = null;
		public String text = "";
	}
	
	private class GraphicsOptionsMode implements RenderState
	{
		public GraphicsOptionsMode() { return; }
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderDisplayModeTextBox(g2d);
			renderDisplayModeList(g2d);
			renderVSyncItems(g2d);
			renderGraphicsSectionLabels(g2d);
			return;
		}
	}
	
	private class AdvancedOptionsMode implements RenderState
	{
		public AdvancedOptionsMode() { return; }
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderBooleanControl(g2d, mRenderQualityPrevButton, mRenderQualityNextButton, RENDER_QUALITY_LABEL_CAPTION, RENDER_QUALITY_LABEL_FACTORS, mRenderQualityHasChanged, mRenderQualityValue.name());
			renderBooleanControl(g2d, mAntialiasPrevButton, mAntialiasNextButton, ANTIALIAS_LABEL_CAPTION, ANTIALIAS_LABEL_FACTORS, mAntialiasHasChanged, mAntialiasValue.name());
			renderBooleanControl(g2d, mRenderColorPrevButton, mRenderColorNextButton, RENDER_COLOR_LABEL_CAPTION, RENDER_COLOR_LABEL_FACTORS, mRenderColorHasChanged, mRenderColorValue.name());
			renderBooleanControl(g2d, mRenderAlphaPrevButton, mRenderAlphaNextButton, RENDER_ALPHA_LABEL_CAPTION, RENDER_ALPHA_LABEL_FACTORS, mRenderAlphaHasChanged, mRenderAlphaValue.name());
			renderBooleanControl(g2d, mShowFPSPrevButton, mShowFPSNextButton, SHOW_FPS_LABEL_CAPTION, SHOW_FPS_LABEL_FACTORS, mShowFPSHasChanged, (mShowFPSValue == true ? "YES" : "NO"));
			return;
		}
	}
	
	private class AudioOptionsMode implements RenderState
	{
		public AudioOptionsMode() { return; }
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderVolumeControls(g2d);
			renderMuteControls(g2d);
			renderSoundSectionLabels(g2d);
			return;
		}
	}
	
	private class KeyBindingsMode implements RenderState
	{
		public KeyBindingsMode() { return; }
		
		public void render(Graphics2D g2d, AffineTransform original, AlphaComposite base)
		{
			renderKeyBindingItems(g2d);
			renderKeyBindingLabels(g2d);
			return;
		}
	}
	
	public interface Listener
	{
		abstract void switchAppStates(AppState state);
		abstract void setDisplayMode(DisplayMode mode);
		abstract void setRenderQuality(RenderQuality value);
		abstract RenderQuality getRenderQuality();
		abstract void setRenderAntialias(RenderAntialias value);
		abstract RenderAntialias getRenderAntialias();
		abstract void setRenderColor(RenderColor value);
		abstract RenderColor getRenderColor();
		abstract void setRenderAlpha(RenderAlpha value);
		abstract RenderAlpha getRenderAlpha();
		abstract SFXManager getSFX();
		abstract int getKeyBinding(KeyNames keyName);
		abstract void reinitializeApp(Settings settings);
	}
}
