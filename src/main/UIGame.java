package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Class for controlling mouse and keyboard input for the Game class.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class UIGame extends UI
{
	private UIGame.Listener mListener = null;
	private boolean mLeftIsPressed = false;
	private boolean mRightIsPressed = false;
	private boolean mThrustIsPressed = false;
	private GameMode mGameMode = null;
	private WaitMode mWaitMode = null;
	private PressAnyKeyMode mPressAnyKeyMode = null;
	private UIState<UIGame.Listener> mCurrentState = null;
	
	public UIGame(UIGame.Listener listener)
	{
		this.mListener = listener;
		this.mGameMode = new GameMode(listener);
		this.mWaitMode = new WaitMode(listener);
		this.mPressAnyKeyMode = new PressAnyKeyMode(listener);
		this.mCurrentState = this.mGameMode;
		return;
	}
	
	public void setToGameMode()
	{
		this.mCurrentState = this.mGameMode;
		return;
	}
	
	public void setToWaitMode()
	{
		this.mLeftIsPressed = false;
		this.mRightIsPressed = false;
		this.mThrustIsPressed = false;
		this.mCurrentState = this.mWaitMode;
		return;
	}
	
	public void setToPressAnyKeyMode()
	{
		this.mLeftIsPressed = false;
		this.mRightIsPressed = false;
		this.mThrustIsPressed = false;
		this.mCurrentState = this.mPressAnyKeyMode;
		return;
	}
	
	public boolean leftIsPressed()
	{
		return this.mLeftIsPressed;
	}
	
	public boolean rightIsPressed()
	{
		return this.mRightIsPressed;
	}
	
	public boolean thrustIsPressed()
	{
		return this.mThrustIsPressed;
	}
	
	public void keyPressed(KeyEvent e)
	{
		this.mCurrentState.keyPressed(e);
		return;
	}
	
	public void keyReleased(KeyEvent e)
	{
		this.mCurrentState.keyReleased(e);
		return;
	}
	
	public void keyTyped(KeyEvent e)
	{
		this.mCurrentState.keyTyped(e);
		return;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		this.mCurrentState.mouseClicked(e);
		return;
	}

	public void mouseEntered(MouseEvent e)
	{
		this.mCurrentState.mouseEntered(e);
		return;
	}

	public void mouseExited(MouseEvent e)
	{
		this.mCurrentState.mouseExited(e);
		return;
	}

	public void mousePressed(MouseEvent e)
	{
		this.mCurrentState.mousePressed(e);
		return;
	}

	public void mouseReleased(MouseEvent e)
	{
		this.mCurrentState.mouseReleased(e);
		return;
	}

	public void mouseDragged(MouseEvent e)
	{
		this.mMousePosition = e.getPoint();
		this.mCurrentState.mouseDragged(e);
		return;
	}

	public void mouseMoved(MouseEvent e)
	{
		this.mMousePosition = e.getPoint();
		this.mCurrentState.mouseMoved(e);
		return;
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		this.mCurrentState.mouseWheelMoved(e);
		return;
	}
	
	private class GameMode extends UIState<UIGame.Listener>
	{
		
		public GameMode(UIGame.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == this.mListener.getLeftKeycode()){
				mLeftIsPressed = true;
			}
			if(e.getKeyCode() == this.mListener.getRightKeyCode()){
				mRightIsPressed = true;
			}
			if(e.getKeyCode() == this.mListener.getThrustKeyCode()){
				mThrustIsPressed = true;
			}
			if(e.getKeyCode() == this.mListener.getFireKeyCode()){
				this.mListener.playerFirePerformed();
			}
			if(e.getKeyCode() == this.mListener.getHyperspaceKeyCode()){
				this.mListener.performHyperspace();
			}
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
				return;
			}
			if(e.getKeyCode() == this.mListener.getLeftKeycode()){
				mLeftIsPressed = false;
			}
			if(e.getKeyCode() == this.mListener.getRightKeyCode()){
				mRightIsPressed = false;
			}
			if(e.getKeyCode() == this.mListener.getThrustKeyCode()){
				mThrustIsPressed = false;
			}
			return;
		}
	}
	
	private class WaitMode extends UIState<UIGame.Listener>
	{
		public WaitMode(UIGame.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
				return;
			}
			return;
		}
	}
	
	private class PressAnyKeyMode extends UIState<UIGame.Listener>
	{
		public PressAnyKeyMode(UIGame.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			mListener.anyKeyEventPerformed();
			return;
		}
	}
	
	public interface Listener
	{
		abstract void enterPressed();
		abstract void escapePressed();
		abstract void playerFirePerformed();
		abstract void anyKeyEventPerformed();
		abstract int getLeftKeycode();
		abstract int getRightKeyCode();
		abstract int getThrustKeyCode();
		abstract int getFireKeyCode();
		abstract int getHyperspaceKeyCode();
		abstract void performHyperspace();
	}
}
