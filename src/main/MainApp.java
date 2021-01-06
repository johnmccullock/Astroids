package main;

import java.awt.AlphaComposite;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferStrategy;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import main.unit.Player;

/**
 * Asteroids game recreation based on the original Atari game from 1979.
 *  
 * @author John McCullock
 * @version 1.0 2015-08-08
 */
public class MainApp implements Display.Listener, Game.Listener, HeadsUpDisplay.Listener, MainMenu.Listener, OptionsWindow.Listener, ScreenManager.Listener, Ticker.Listener, TopScoreWindow.Listener
{
	private static final boolean DEFAULT_USE_VSYNC = false;
	private static final RenderQuality DEFAULT_RENDER_QUALITY_VALUE = RenderQuality.HIGH;
	private static final RenderAntialias DEFAULT_ANTIALIAS_VALUE = RenderAntialias.ON;
	private static final RenderColor DEFAULT_RENDER_COLOR_VALUE = RenderColor.HIGH;
	private static final RenderAlpha DEFAULT_RENDER_ALPHA_VALUE = RenderAlpha.HIGH;
	private static final boolean DEFAULT_SHOW_FPS_VALUE = false;
	private static final float DEFAULT_VOLUME_LEVEL = 1.0f;
	private static final boolean DEFAULT_MUTE_STATE = false;
	private final int DEFAULT_LEFT_KEY = KeyEvent.VK_A;
	private final int DEFAULT_RIGHT_KEY = KeyEvent.VK_D;
	private final int DEFAULT_THRUST_KEY = KeyEvent.VK_W;
	private final int DEFAULT_FIRE_KEY = KeyEvent.VK_ENTER;
	private final int DEFAULT_HYPERSPACE_KEY = KeyEvent.VK_BACK_SPACE;
	private final int DEFAULT_PLAYER_LIVES = 3;
	private final int DEFAULT_BONUS_VALUE = 10000;
	private final int DEFAULT_MAX_SCORES = 10;
	
	private GraphicsDevice mGraphicsDevice = null;
	private GraphicsConfiguration mGraphicsConfiguration = null;
	private DisplayMode mDisplayMode = null;
	private BufferedImage mBuffer = null;
	private Graphics mGraphics = null;
	private Graphics2D g2d = null;
	private BufferStrategy mBufferStrategy = null;
	private boolean mUseVSync = DEFAULT_USE_VSYNC;
	private HintPair[] mHints = null;
	private RenderQuality mRenderQuality = DEFAULT_RENDER_QUALITY_VALUE;
	private RenderAntialias mRenderAntialias = DEFAULT_ANTIALIAS_VALUE;
	private RenderColor mRenderColor = DEFAULT_RENDER_COLOR_VALUE;
	private RenderAlpha mRenderAlpha = DEFAULT_RENDER_ALPHA_VALUE;
	private boolean mShowFPS = DEFAULT_SHOW_FPS_VALUE;
	private boolean mMuteSound = false;
	private float mVolume = DEFAULT_VOLUME_LEVEL;
	
	private HashMap<KeyNames, Integer> mKeyBindings = new HashMap<KeyNames, Integer>();
	private SFXManager mSFXManager = null;
	private Ticker mTicker = null;
	private Display mDisplay = null;
	private MainMenu mMainMenu = null;
	private OptionsWindow mOptionsWindow = null;
	private TopScoreWindow mTopScoreWindow = null;
	private Player mCurrentPlayer = null;
	private Player mPlayer1 = null;
	private Player mPlayer2 = null;
	private Game mPlayer1Game = null;
	private Game mPlayer2Game = null;
	private AppStatable mCurrentState = null;
	private ArrayList<TopScoreWindow.ScoreData> mHighScores = new ArrayList<TopScoreWindow.ScoreData>();
	
	public MainApp()
	{
		Settings coldSettings = this.initializeCold();
		this.initializeGraphics();
		this.initializeAudio();
		this.doStartupTests();
		this.reinitializeApp(coldSettings);
		return;
	}
	
	private Settings initializeCold()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.mGraphicsDevice = ge.getDefaultScreenDevice();
		Settings coldStart = new Settings();
		coldStart.renderQuality = DEFAULT_RENDER_QUALITY_VALUE;
		coldStart.renderAntialias = DEFAULT_ANTIALIAS_VALUE;
		coldStart.renderColor = DEFAULT_RENDER_COLOR_VALUE;
		coldStart.renderAlpha = DEFAULT_RENDER_ALPHA_VALUE;
		coldStart.showPFS = DEFAULT_SHOW_FPS_VALUE;
		coldStart.useVSync = DEFAULT_USE_VSYNC;
		coldStart.keyBindings.put(KeyNames.LEFT, DEFAULT_LEFT_KEY);
		coldStart.keyBindings.put(KeyNames.RIGHT, DEFAULT_RIGHT_KEY);
		coldStart.keyBindings.put(KeyNames.THRUST, DEFAULT_THRUST_KEY);
		coldStart.keyBindings.put(KeyNames.FIRE, DEFAULT_FIRE_KEY);
		coldStart.keyBindings.put(KeyNames.HYPERSPACE, DEFAULT_HYPERSPACE_KEY);
		coldStart.volume = DEFAULT_VOLUME_LEVEL;
		coldStart.muteSound = DEFAULT_MUTE_STATE;
		try{
			coldStart.displayMode = this.mGraphicsDevice.getDisplayMode();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null, "Failed to obtain display mode.  Program cannot continue.", "Display Mode Error", JOptionPane.OK_OPTION);
			System.exit(1);
		}
		return coldStart;
	}
	
	public void initializeGraphics()
	{
		this.mGraphicsConfiguration = this.mGraphicsDevice.getDefaultConfiguration();
		this.mHints = new HintPair[8];
		this.mHints[0] = new HintPair(RenderingHints.KEY_RENDERING, DEFAULT_RENDER_QUALITY_VALUE.getValue());
		if(DEFAULT_RENDER_QUALITY_VALUE.equals(RenderQuality.HIGH)){
			this.mHints[1] = new HintPair(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			this.mHints[2] = new HintPair(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		}else{
			this.mHints[1] = new HintPair(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			this.mHints[2] = new HintPair(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		}
		this.mHints[3] = new HintPair(RenderingHints.KEY_ANTIALIASING, DEFAULT_ANTIALIAS_VALUE.getValue());
		if(DEFAULT_ANTIALIAS_VALUE.equals(RenderAntialias.ON)){
			this.mHints[4] = new HintPair(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}else{
			this.mHints[4] = new HintPair(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		this.mHints[5] = new HintPair(RenderingHints.KEY_COLOR_RENDERING, DEFAULT_RENDER_COLOR_VALUE.getValue());
		this.mHints[6] = new HintPair(RenderingHints.KEY_ALPHA_INTERPOLATION, DEFAULT_RENDER_ALPHA_VALUE.getValue());
		this.mHints[7] = new HintPair(RenderingHints.KEY_RENDERING, DEFAULT_RENDER_QUALITY_VALUE.getValue());
		return;
	}
	
	public void initializeAudio()
	{
		try{
			this.mSFXManager = new SFXManager();
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return;
	}
	
	private void doStartupTests()
	{
		if(!this.mGraphicsDevice.isFullScreenSupported()){
			JOptionPane.showMessageDialog(null, "This program cannot run because this machine does not support full screen mode.", "Program Load Error", JOptionPane.OK_OPTION);
			System.exit(1);
		}
		
		
		return;
	}
	
	private void render()
	{
		try{
			g2d = this.mBuffer.createGraphics();
			for(HintPair hint : this.mHints)
			{
				g2d.setRenderingHint(hint.key, hint.value);
			}
			AffineTransform original = g2d.getTransform();
			AlphaComposite base = (AlphaComposite)g2d.getComposite();
			this.mCurrentState.render(g2d, original, base);
			
			// Render the buffer to the screen.
			this.mGraphics = this.mBufferStrategy.getDrawGraphics();
			this.mGraphics.drawImage(this.mBuffer, 0, 0, null);
			if(!this.mBufferStrategy.contentsLost()){
				this.mBufferStrategy.show();
			}
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}finally{
			if(this.mGraphics != null){
				this.mGraphics.dispose();
			}
			if(g2d != null){
				g2d.dispose();
			}
		}
		return;
	}
	
	public void switchAppStates(AppState state)
	{
		if(state.equals(AppState.MAIN_MENU)){
			// Will be null on startup.
			if(this.mDisplay.getUI() != null){
				this.mMainMenu.getUI().setMousePosition(this.mDisplay.getUI().getMousePosition());
			}else{
				this.mMainMenu.getUI().getMousePosition().x = (int)Math.round(this.mDisplayMode.getWidth() / 2.0f);
				this.mMainMenu.getUI().getMousePosition().y = (int)Math.round(this.mDisplayMode.getHeight() / 2.0f);
			}
			this.mDisplay.setUI(this.mMainMenu.getUI());
			this.mCurrentState = this.mMainMenu;
		}else if(state.equals(AppState.OPTIONS)){
			this.mOptionsWindow = null;
			Settings s = new Settings();
			s.displayMode = this.mDisplayMode;
			s.renderQuality = this.mRenderQuality;
			s.renderAntialias = this.mRenderAntialias;
			s.renderAlpha = this.mRenderAlpha;
			s.renderColor = this.mRenderColor;
			s.useVSync = this.mUseVSync;
			for(KeyNames name : KeyNames.values())
			{
				s.keyBindings.put(name, this.mKeyBindings.get(name));
			}
			s.muteSound = this.mMuteSound;
			s.volume = this.mVolume;
			this.mOptionsWindow = new OptionsWindow(0, 0, this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight(), this, this, s);
			this.mDisplay.setUI(this.mOptionsWindow.getUI());
			this.mCurrentState = this.mOptionsWindow;
		}else if(state.equals(AppState.PLAYER1)){
			this.mCurrentPlayer = this.mPlayer1;
			this.mDisplay.setUI(this.mPlayer1Game.getUI());
			this.mCurrentState = this.mPlayer1Game;
		}else if(state.equals(AppState.PLAYER2)){
			this.mCurrentPlayer = this.mPlayer2;
			this.mDisplay.setUI(this.mPlayer2Game.getUI());
			this.mCurrentState = this.mPlayer2Game;
		}else if(state.equals(AppState.SHUTDOWN_TEST)){
			this.beginApplicationShutdown();
		}else if(state.equals(AppState.HIGH_SCORES)){
			this.mTopScoreWindow = null;
			this.mTopScoreWindow = new TopScoreWindow(0, 0, this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight(), this, this, DEFAULT_MAX_SCORES, AppState.MAIN_MENU);
			this.mDisplay.setUI(this.mTopScoreWindow.getUI());
			this.mCurrentState = this.mTopScoreWindow;
		}else if(state.equals(AppState.HIGH_SCORES_ADD)){
			this.mTopScoreWindow = null;
			if(this.mCurrentPlayer == this.mPlayer1){
				this.mTopScoreWindow = new TopScoreWindow(0, 0, this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight(), this, this, DEFAULT_MAX_SCORES, AppState.PLAYER1, this.mCurrentPlayer);
			}else if(this.mCurrentPlayer == this.mPlayer2){
				this.mTopScoreWindow = new TopScoreWindow(0, 0, this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight(), this, this, DEFAULT_MAX_SCORES, AppState.PLAYER2, this.mCurrentPlayer);
			}
			this.mTopScoreWindow.getUI().getMousePosition().x = (int)Math.round(this.mDisplayMode.getWidth() / 2.0f);
			this.mTopScoreWindow.getUI().getMousePosition().y = (int)Math.round(this.mDisplayMode.getHeight() / 2.0f);
			this.mDisplay.setUI(this.mTopScoreWindow.getUI());
			this.mCurrentState = this.mTopScoreWindow;
		}
		return;
	}
	
	public void reinitializeApp(Settings settings)
	{
		try{
			if(this.mTicker != null){
				this.mTicker.stopTimer();
			}
			this.mDisplayMode = settings.displayMode;
			this.mDisplay = new Display(this, this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight());
			this.mGraphicsDevice.setFullScreenWindow(this.mDisplay.getJFrame());
			if(!this.mGraphicsDevice.isDisplayChangeSupported()){
				throw new DisplayChangeNotSupportedException("This system does not allow display changes.");
			}
			this.mGraphicsDevice.setDisplayMode(this.mDisplayMode = settings.displayMode);
			this.mDisplay.getCanvas().createBufferStrategy(2);
			this.mBufferStrategy = this.mDisplay.getCanvas().getBufferStrategy();
			this.mBuffer = this.mGraphicsConfiguration.createCompatibleImage(this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight());
			this.mDisplay.getCanvas().setSize(this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight());
			this.toggleUIListeners(false);
			
			this.mUseVSync = settings.useVSync;
			if(this.mUseVSync){
				Toolkit.getDefaultToolkit().sync();
			}
			
			this.mRenderQuality = settings.renderQuality;
			this.mRenderColor = settings.renderColor;
			this.mRenderAntialias = settings.renderAntialias;
			this.mRenderAlpha = settings.renderAlpha;
			this.mShowFPS = settings.showPFS;
			
			for(KeyNames name : KeyNames.values())
			{
				this.mKeyBindings.put(name, settings.keyBindings.get(name));
			}
			
			this.mMuteSound = settings.muteSound;
			this.mVolume = settings.volume;
			if(this.mMuteSound){
				this.mSFXManager.setVolume(0.0f);
			}else{
				this.mSFXManager.setVolume(this.mVolume);
			}
			
			this.mPlayer1 = null;
			this.mPlayer2 = null;
			this.mPlayer1Game = null;
			this.mPlayer2Game = null;
			
			this.mMainMenu = new MainMenu(0, 0, this.mDisplayMode.getWidth(), this.mDisplayMode.getHeight(), this, this);
			this.switchAppStates(AppState.MAIN_MENU);
			
			this.toggleUIListeners(true);
			
			this.mTicker = new Ticker(this, 60, 60, 5);
			if(this.mShowFPS){
				this.mTicker.enableStatisticsMode(true);
			}
			this.mTicker.startTimer();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return;
	}
	
	public void startNew(int players)
	{
		this.mPlayer1 = new Player(Player.Num.PLAYER1);
		this.mPlayer1.setLives(DEFAULT_PLAYER_LIVES);
		
		this.mPlayer1Game = new Game(this.mPlayer1, 
									0, 
									0, 
									this.mDisplayMode.getWidth(), 
									this.mDisplayMode.getHeight(), 
									this, 
									this, 
									this, 
									this.mSFXManager,
									DEFAULT_BONUS_VALUE);
		if(players > 1){
			this.mPlayer2 = new Player(Player.Num.PLAYER2);
			this.mPlayer2.setLives(DEFAULT_PLAYER_LIVES);
			this.mPlayer2Game = new Game(this.mPlayer2, 
										0, 
										0, 
										this.mDisplayMode.getWidth(), 
										this.mDisplayMode.getHeight(), 
										this, 
										this, 
										this, 
										this.mSFXManager,
										DEFAULT_BONUS_VALUE);
		}
		this.switchAppStates(AppState.PLAYER1);
		return;
	}
	
	private void toggleUIListeners(boolean value)
	{
		this.mDisplay.toggleKeyListener(value);
		this.mDisplay.toggleMouseListener(value);
		this.mDisplay.toggleMouseMotionListener(value);
		this.mDisplay.toggleMouseWheelListener(value);
		return;
	}
	
	public void manageSinglePlayerGameOver()
	{
		this.mPlayer1Game = null;
		this.mPlayer1 = null;
		if(this.mPlayer2Game != null){
			this.mPlayer2Game = null;
		}
		if(this.mPlayer2 != null){
			this.mPlayer2 = null;
		}
		this.switchAppStates(AppState.MAIN_MENU);
		return;
	}
	
	public void manageDoublesGame()
	{
		if(this.mPlayer1Game != null && this.mCurrentPlayer == this.mPlayer1 && this.mPlayer1.getLives() <= 0){
			this.mPlayer1Game.setToGameOverMode();
			return;
		}
		if(this.mPlayer2Game != null && this.mCurrentPlayer == this.mPlayer2 && this.mPlayer2.getLives() <= 0){
			this.mPlayer2Game.setToGameOverMode();
			return;
		}
		if(this.mPlayer1Game == null && this.mPlayer2Game == null){
			this.switchAppStates(AppState.MAIN_MENU);
			return;
		}
		if(this.mCurrentPlayer == this.mPlayer1 && this.mPlayer2.getLives() > 0){
			this.switchAppStates(AppState.PLAYER2);
			return;
		}
		if(this.mCurrentPlayer == this.mPlayer2 && this.mPlayer1.getLives() > 0){
			this.switchAppStates(AppState.PLAYER1);
			return;
		}
		return;
	}
	
	public void manageDoublesPlayerGameOver(Player.Num player)
	{
		Player other = null;
		AppState state = null;
		if(player == Player.Num.PLAYER1){
			this.mPlayer1Game = null;
			this.mPlayer1 = null;
			other = this.mPlayer2;
			state = AppState.PLAYER2;
		}else{
			this.mPlayer2Game = null;
			this.mPlayer2 = null;
			other = this.mPlayer1;
			state = AppState.PLAYER1;
		}
		if(other.getLives() > 0){
			this.switchAppStates(state);
		}else{
			if(other == this.mPlayer1){
				this.mPlayer1Game = null;
			}else{
				this.mPlayer2Game = null;
			}
			this.switchAppStates(AppState.MAIN_MENU);
		}
		return;
	}
	
	public void checkScore()
	{
		if(this.mHighScores.size() == 0 || this.mCurrentPlayer.getScore() > this.mHighScores.get(this.mHighScores.size() - 1).score){
			this.switchAppStates(AppState.HIGH_SCORES_ADD);
		}
		return;
	}
	
	/**
	 * Does three things:
	 * (1) add a score to the list.
	 * (2) sort the list in descending order by score.
	 * (3) trim off all but the first ten items.
	 * @param score
	 */
	public void addHighScore(TopScoreWindow.ScoreData score)
	{
		this.mHighScores.add(score);
		Collections.sort(this.mHighScores);
		if(this.mHighScores.size() > 10){
			for(int i = 10; i < this.mHighScores.size(); i++)
			{
				this.mHighScores.remove(i);
			}
		}
		return;
	}
	
	public ArrayList<TopScoreWindow.ScoreData> getHighScores()
	{
		return this.mHighScores;
	}
	
	public int getMaxScores()
	{
		return DEFAULT_MAX_SCORES;
	}
	
	public void beginApplicationShutdown()
	{
		if(this.mTicker != null){
			this.mTicker.stopTimer();
			//this.mTicker = null; // probably should not use; Ticker thread needs to dispose itself.
		}
		this.mGraphicsDevice.setFullScreenWindow(null);
		
		System.exit(0);
		return;
	}
	
	public void setDisplayMode(DisplayMode mode)
	{
		this.mDisplayMode = mode;
		return;
	}
	
	public void setRenderQuality(RenderQuality value)
	{
		this.mRenderQuality = value;
		return;
	}
	
	public RenderQuality getRenderQuality()
	{
		return this.mRenderQuality;
	}
	
	public void setRenderAntialias(RenderAntialias value)
	{
		this.mRenderAntialias = value;
		return;
	}
	
	public RenderAntialias getRenderAntialias()
	{
		return this.mRenderAntialias;
	}
	
	public void setRenderColor(RenderColor value)
	{
		this.mRenderColor = value;
		return;
	}
	
	public RenderColor getRenderColor()
	{
		return this.mRenderColor;
	}
	
	public void setRenderAlpha(RenderAlpha value)
	{
		this.mRenderAlpha = value;
		return;
	}
	
	public RenderAlpha getRenderAlpha()
	{
		return this.mRenderAlpha;
	}
	
	public void setShowFPSOption(boolean value)
	{
		this.mShowFPS = value;
		return;
	}
	
	public boolean getShowFPSOption()
	{
		return this.mShowFPS;
	}
	
	public int getKeyBinding(KeyNames keyName)
	{
		return this.mKeyBindings.get(keyName);
	}
	
	public boolean isDoublesGame()
	{
		return this.mPlayer2Game != null;
	}
	
	public boolean gameinProgress()
	{
		if(this.mPlayer1Game != null || this.mPlayer2Game != null){
			return true;
		}
		return false;
	}
	
	public Player getCurrentPlayer()
	{
		return this.mCurrentPlayer;
	}
	
	public Player getPlayer(Player.Num PlayerNum)
	{
		if(PlayerNum == Player.Num.PLAYER1){
			return this.mPlayer1;
		}else if(PlayerNum == Player.Num.PLAYER2){
			return this.mPlayer2;
		}
		return null;
	}
	
	public SFXManager getSFX()
	{
		return this.mSFXManager;
	}
	
	public void displayOpenPerformed()
	{
		return;
	}
	
	public void displayClosePerformed()
	{
		return;
	}
	
	public void tickerUpdateRequested()
	{
		this.mCurrentState.update();
		return;
	}
	
	public void tickerRenderRequested()
	{
		if(this.mShowFPS){
			this.mCurrentState.setFPS(this.mTicker.getFPS());
		}
		this.render();
		return;
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new MainApp();
				return;
			}
		});
		return;
	}
		
	@SuppressWarnings("serial")
	public class DisplayChangeNotSupportedException extends Exception
	{
		public DisplayChangeNotSupportedException(String message)
		{
			super(message);
			return;
		}
	}
}
