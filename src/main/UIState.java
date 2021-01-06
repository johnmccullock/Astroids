package main;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Used for further separating input into more specific states.
 * 
 * @author John McCullock
 * @version 1.0 2015-08-07
 * @param <T> The parent class which the generic will make reference to.
 */
public class UIState<T>
{
	protected T mListener = null;
	
	public UIState(T listener)
	{
		this.mListener = listener;
		return;
	}
	
	protected void enter(KeyEvent k, MouseEvent m, MouseWheelEvent w) { return; }
	protected void exit() { return; }
	protected void keyPressed(KeyEvent e) { return; }
	protected void keyReleased(KeyEvent e) { return; }
	protected void keyTyped(KeyEvent e) { return; }
	protected void mouseClicked(MouseEvent e) { return; }
	protected void mouseEntered(MouseEvent e) { return; }
	protected void mouseExited(MouseEvent e) { return; }
	protected void mousePressed(MouseEvent e) { return; }
	protected void mouseReleased(MouseEvent e) { return; }
	protected void mouseDragged(MouseEvent e) { return; }
	protected void mouseMoved(MouseEvent e) { return; }
	protected void mouseWheelMoved(MouseWheelEvent e) { return; }
}
