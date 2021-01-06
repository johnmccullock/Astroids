package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import main.polycomp.PItem;

/**
 * Class for controlling mouse and keyboard input for the MainMenu class.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class UIMainMenu extends UI
{
	private UIMainMenu.Listener mListener = null;
	
	public UIMainMenu(UIMainMenu.Listener listener)
	{
		this.mListener = listener;
		return;
	}
	
	public void keyPressed(KeyEvent e)
	{
		
		return;
	}
	
	public void keyReleased(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			this.mListener.escapePressed();
			return;
		}
		
		return;
	}
	
	public void keyTyped(KeyEvent e)
	{
		
		return;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		return;
	}

	public void mouseEntered(MouseEvent e)
	{
		return;
	}

	public void mouseExited(MouseEvent e)
	{
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
				this.mListener.mainMenuButtonPressed(button.getType());
			}
		}
		return;
	}

	public void mouseDragged(MouseEvent e)
	{
		this.mMousePosition = e.getPoint();
		return;
	}

	public void mouseMoved(MouseEvent e)
	{
		this.mMousePosition = e.getPoint();
		for(PItem button : mListener.getButtons())
		{
			if(button.getPolygon().contains(e.getPoint())){
				button.setComponentHovered(true);
			}else{
				button.setComponentHovered(false);
			}
		}
		return;
	}

	public void mouseWheelMoved(MouseWheelEvent e)
	{
		
		return;
	}
	
	public interface Listener
	{
		abstract void enterPressed();
		abstract void escapePressed();
		abstract void setSelectedButton(int selected);
		abstract int getSelectedButton();
		abstract  PItem[] getButtons();
		abstract void mainMenuButtonPressed(Object type);
	}
}
