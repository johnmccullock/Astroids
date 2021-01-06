package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import main.polycomp.PItem;
import main.polycomp.PTextBox;

/**
 * Class for controlling mouse and keyboard input for the TopScoreWindow class.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class UITopScoreWindow extends UI
{
	private UITopScoreWindow.Listener mListener = null;
	private boolean mPressedDebounced = true;
	private boolean mReleasedDebounced = true;
	private NormalMode mNormalMode = null;
	private NameEntryMode mNameEntryMode = null;
	private UIState<UITopScoreWindow.Listener> mCurrentState = null;
	
	public UITopScoreWindow(UITopScoreWindow.Listener listener)
	{
		this.mListener = listener;
		this.mNormalMode = new NormalMode(listener);
		this.mNameEntryMode = new NameEntryMode(listener);
		this.mCurrentState = this.mNormalMode;
		return;
	}
	
	public UITopScoreWindow(UITopScoreWindow.Listener listener, boolean bounceValues)
	{
		this.mListener = listener;
		this.mPressedDebounced = bounceValues;
		this.mReleasedDebounced = bounceValues;
		this.mNormalMode = new NormalMode(listener);
		this.mNameEntryMode = new NameEntryMode(listener);
		this.mCurrentState = this.mNormalMode;
		return;
	}
	
	public void setToNormalMode()
	{
		this.mCurrentState = this.mNormalMode;
		return;
	}
	
	public void setToNameEntryMode()
	{
		this.mCurrentState = this.mNameEntryMode;
		return;
	}
	
	public void keyPressed(KeyEvent e)
	{
		/*
		 * keyboard input is carrying over from the previous AppState into this one, interfering with the okay/cancel buttons.
		 */
		if(!this.mPressedDebounced){
			this.mPressedDebounced = true;
			return;
		}
		this.mCurrentState.keyPressed(e);
		return;
	}
	
	public void keyReleased(KeyEvent e)
	{
		/*
		 * keyboard input is carrying over from the previous AppState into this one, interfering with the okay/cancel buttons.
		 */
		if(!this.mReleasedDebounced){
			this.mReleasedDebounced = true;
			return;
		}
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
	
	private class NormalMode extends UIState<UITopScoreWindow.Listener>
	{
		public NormalMode(UITopScoreWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
			}
			return;
		}
	}
	
	private class NameEntryMode extends UIState<UITopScoreWindow.Listener>
	{
		public NameEntryMode(UITopScoreWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			this.mListener.getTextBox().keyPressed(e);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				this.mListener.okayButtonPressed();
			}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.cancelButtonPressed();
			}
			this.mListener.getTextBox().keyReleased(e);
			return;
		}
		
		@Override
		public void keyTyped(KeyEvent e)
		{
			this.mListener.getTextBox().keyTyped(e);
			return;
		}
		
		public void mouseMoved(MouseEvent e)
		{
			for(PItem button : this.mListener.getButtons())
			{
				if(button.getPolygon().contains(e.getPoint())){
					button.setComponentHovered(true);
				}else{
					button.setComponentHovered(false);
				}
			}
			return;
		}
		
		public void mousePressed(MouseEvent e)
		{
			for(PItem button : this.mListener.getButtons())
			{
				if(button.getPolygon().contains(e.getPoint())){
					button.setComponentSelected(true);
				}else{
					button.setComponentSelected(false);
				}
			}
			return;
		}
		
		public void mouseReleased(MouseEvent e)
		{
			for(PItem button : this.mListener.getButtons())
			{
				button.setComponentSelected(false);
				if(button.getPolygon().contains(e.getPoint())){
					if(button.getType() == TopScoreWindow.Button.OKAY){
						this.mListener.okayButtonPressed();
					}else if(button.getType() == TopScoreWindow.Button.CANCEL){
						this.mListener.cancelButtonPressed();
					}
				}
			}
			return;
		}
	}
	
	public interface Listener
	{
		abstract void escapePressed();
		abstract PTextBox getTextBox();
		abstract PItem[] getButtons();
		abstract void okayButtonPressed();
		abstract void cancelButtonPressed();
	}
}
