package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import main.polycomp.PAlign;
import main.unit.Player;

abstract class HeadsUpDisplay extends Lobby
{
	protected enum LabelType{INTRO, SCORE, LIVES};
	
	protected final Point2D.Float PLAYER1_SCORE_LABEL_FACTORS = new Point2D.Float(0.02381f, 0.0381f); // by screen width and height.
	protected final Point2D.Float PLAYER2_SCORE_LABEL_FACTORS = new Point2D.Float(0.97619f, 0.0381f); // by screen width - label width - x and height.
	protected final Point2D.Float HIGH_SCORE_LABEL_FACTORS = new Point2D.Float(0.5f, 0.0381f); // by screen width and height.
	protected final Point2D.Float PLAYER1_LIVES_LABEL_FACTORS = new Point2D.Float(0.02381f, 0.09523f); // by screen width and height.
	protected final Point2D.Float PLAYER2_LIVES_LABEL_FACTORS = new Point2D.Float(0.97619f, 0.09523f); // by screen width - label width - x and height.
	protected final Point2D.Float GAME_OVER_LABEL_FACTORS = new Point2D.Float(0.5f, 0.5f);
	protected final Point2D.Float PRESS_KEY_LABEL_FACTORS = new Point2D.Float(0.5f, 0.75f);
	protected final Point2D.Float PLAYER1_INTRO_LABEL_FACTORS = new Point2D.Float(0.5f, 0.5f);
	protected final Point2D.Float PLAYER2_INTRO_LABEL_FACTORS = new Point2D.Float(0.5f, 0.5f);
	protected final String GAME_OVER_CAPTION = "GAME OVER";
	protected final String PRESS_KEY_CAPTION = "Press Any Key To Continue";
	protected final String PLAYER1_INTRO_CAPTION = "PLAYER 1";
	protected final String PLAYER2_INTRO_CAPTION = "PLAYER 2";
	
	private HeadsUpDisplay.Listener mListener = null;
	protected HashMap<Player.Num, HashMap<LabelType, ScreenManager.Label>> mLabels = new HashMap<Player.Num, HashMap<LabelType, ScreenManager.Label>>();
	protected ScreenManager.Label mHighScoreLabel = null;
	protected ScreenManager.Label mGameOverLabel = null;
	protected ScreenManager.Label mPressAnyKeyLabel = null;
	
	public HeadsUpDisplay(int x, int y, int width, int height, ScreenManager.Listener smListener, HeadsUpDisplay.Listener hudListener)
	{
		super(x, y, width, height, smListener);
		this.mListener = hudListener;
		return;
	}
	
	protected void initialize()
	{
		HashMap<HeadsUpDisplay.LabelType, ScreenManager.Label> types = new HashMap<HeadsUpDisplay.LabelType, ScreenManager.Label>();
		types.put(LabelType.SCORE, this.initializeLabel(new ScreenManager.Label(), this.mLargeFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.LEFT, PLAYER1_SCORE_LABEL_FACTORS));
		types.put(LabelType.LIVES, this.initializeLabel(new ScreenManager.Label(), this.mLargeFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.LEFT, PLAYER1_LIVES_LABEL_FACTORS));
		types.put(LabelType.INTRO, this.initializeLabel(new ScreenManager.Label(), this.mMegaFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.CENTER, PLAYER1_INTRO_LABEL_FACTORS));
		this.mLabels.put(Player.Num.PLAYER1, types);
		this.mLabels.get(Player.Num.PLAYER1).get(LabelType.INTRO).setText(PLAYER1_INTRO_CAPTION);
		this.centerLabel(this.mLabels.get(Player.Num.PLAYER1).get(LabelType.INTRO), PLAYER1_INTRO_LABEL_FACTORS);
		
		types = new HashMap<HeadsUpDisplay.LabelType, ScreenManager.Label>();
		types.put(LabelType.SCORE, this.initializeLabel(new ScreenManager.Label(), this.mLargeFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.RIGHT, PLAYER2_SCORE_LABEL_FACTORS));
		types.put(LabelType.LIVES, this.initializeLabel(new ScreenManager.Label(), this.mLargeFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.RIGHT, PLAYER2_LIVES_LABEL_FACTORS));
		types.put(LabelType.INTRO, this.initializeLabel(new ScreenManager.Label(), this.mMegaFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.CENTER, PLAYER2_INTRO_LABEL_FACTORS));
		this.mLabels.put(Player.Num.PLAYER2, types);
		this.mLabels.get(Player.Num.PLAYER2).get(LabelType.INTRO).setText(PLAYER2_INTRO_CAPTION);
		this.centerLabel(this.mLabels.get(Player.Num.PLAYER2).get(LabelType.INTRO), PLAYER2_INTRO_LABEL_FACTORS);
		
		this.mLabels.get(Player.Num.PLAYER1).get(LabelType.SCORE).setText("0");
		this.mLabels.get(Player.Num.PLAYER2).get(LabelType.SCORE).setText("0");
		
		this.mHighScoreLabel = this.initializeLabel(new ScreenManager.Label(), this.mSmallFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.CENTER, HIGH_SCORE_LABEL_FACTORS);
		this.mGameOverLabel = this.initializeLabel(new ScreenManager.Label(), this.mMegaFont, Skin.GAME_OVER_FOREGROUND, PAlign.CENTER, GAME_OVER_LABEL_FACTORS);
		this.mGameOverLabel.setText(GAME_OVER_CAPTION);
		this.centerLabel(this.mGameOverLabel, GAME_OVER_LABEL_FACTORS);
		this.mPressAnyKeyLabel = this.initializeLabel(new ScreenManager.Label(), this.mLargeFont, Skin.LARGE_LABEL_FONT_COLOR, PAlign.CENTER, PRESS_KEY_LABEL_FACTORS);
		this.mPressAnyKeyLabel.setText(PRESS_KEY_CAPTION);
		this.centerLabel(this.mPressAnyKeyLabel, PRESS_KEY_LABEL_FACTORS);
		return;
	}
	
	protected void renderLabels(Graphics2D g2d, AffineTransform original, AlphaComposite base)
	{
		if(this.mListener.getPlayer(Player.Num.PLAYER1) == null){
			this.mLabels.get(Player.Num.PLAYER1).get(LabelType.SCORE).setText("0");
			this.mLabels.get(Player.Num.PLAYER1).get(LabelType.LIVES).setText("");
		}else{
			this.mLabels.get(Player.Num.PLAYER1).get(LabelType.SCORE).setText(String.valueOf(this.mListener.getPlayer(Player.Num.PLAYER1).getScore()));
			this.mLabels.get(Player.Num.PLAYER1).get(LabelType.LIVES).setText(String.valueOf(this.getLivesLeftCharacters(this.mListener.getPlayer(Player.Num.PLAYER1).getLives())));
		}
		
		if(this.mListener.getPlayer(Player.Num.PLAYER2) == null){
			this.mLabels.get(Player.Num.PLAYER2).get(LabelType.SCORE).setText("0");
			this.mLabels.get(Player.Num.PLAYER2).get(LabelType.LIVES).setText("");
		}else{
			this.mLabels.get(Player.Num.PLAYER2).get(LabelType.SCORE).setText(String.valueOf(this.mListener.getPlayer(Player.Num.PLAYER2).getScore()));
			this.mLabels.get(Player.Num.PLAYER2).get(LabelType.LIVES).setText(String.valueOf(this.getLivesLeftCharacters(this.mListener.getPlayer(Player.Num.PLAYER2).getLives())));
		}
		
		this.mLabels.get(Player.Num.PLAYER1).get(LabelType.SCORE).render(g2d);
		this.mLabels.get(Player.Num.PLAYER1).get(LabelType.LIVES).render(g2d);
		this.mLabels.get(Player.Num.PLAYER2).get(LabelType.SCORE).render(g2d);
		this.mLabels.get(Player.Num.PLAYER2).get(LabelType.LIVES).render(g2d);
		this.mHighScoreLabel.render(g2d);
		return;
	}
	
	protected void renderIntroLabel(Graphics2D g2d, Player.Num currentPlayer)
	{
		this.mLabels.get(currentPlayer).get(LabelType.INTRO).render(g2d);
		return;
	}
	
	protected String getLivesLeftCharacters(int livesLeft)
	{
		StringBuilder results = new StringBuilder();
		for(int i = 0; i < livesLeft; i++)
		{
			results.append((char)197);
		}
		return results.toString();
	}
	
	public interface Listener
	{
		abstract Player getPlayer(Player.Num PlayerNum);
	}
}
