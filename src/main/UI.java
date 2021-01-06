package main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Abstract class for redirecting keyboard and mouse input.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
abstract class UI
{
	protected Point mMousePosition = new Point();
	
	public void setMousePosition(Point position)
	{
		this.mMousePosition = position;
		return;
	}
	
	public Point getMousePosition()
	{
		return this.mMousePosition;
	}
	
	abstract void keyPressed(KeyEvent e);
	abstract void keyReleased(KeyEvent e);
	abstract void keyTyped(KeyEvent e);
	abstract void mouseClicked(MouseEvent e);
	abstract void mouseEntered(MouseEvent e);
	abstract void mouseExited(MouseEvent e);
	abstract void mousePressed(MouseEvent e);
	abstract void mouseReleased(MouseEvent e);
	abstract void mouseDragged(MouseEvent e);
	abstract void mouseMoved(MouseEvent e);
	abstract void mouseWheelMoved(MouseWheelEvent e);
}
