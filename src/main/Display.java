package main;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Toolkit;
import java.awt.Point;
import javax.swing.JFrame;

/**
 * The Display class is one of the only javax.swing tools used in this project.  It's necessary for
 * accessing input listeners and a few window listeners.
 * 
 * @author John McCullock
 * @version 1.0 2015-02-23
 */
public class Display implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
	private Display.Listener mParent = null;
	private JFrame mFrame = null;
	private Canvas mCanvas = null;
	private Dimension mScreenSize = new Dimension();
	private UI mUI = null;
	
	public Display(Display.Listener parent, int width, int height)
	{
		this.mParent = parent;
		this.mScreenSize.width = width;
		this.mScreenSize.height = height;
		this.initializeMain();
		return;
	}
	
	private void initializeMain()
	{
		this.mFrame = new JFrame();
		this.mFrame.setFocusable(true); // required for keyboard events.
		this.mFrame.setUndecorated(true);
		this.mFrame.setIgnoreRepaint(true);
 		
		this.mFrame.addWindowListener(new WindowListener()
		{
			public void windowActivated(WindowEvent e)		{ return; }
			public void windowClosed(WindowEvent e)			{ return; }
		    public void windowDeactivated(WindowEvent e)	{ return; }
		    public void windowDeiconified(WindowEvent e)	{ return; }
		    public void windowIconified(WindowEvent e)		{ return; }
		    
		    public void windowOpened(WindowEvent e)
		    {
		    	mCanvas.requestFocus();
		    	mParent.displayOpenPerformed();
		    	return;
		    }
		    
		    public void windowClosing(WindowEvent e)
		    {
		    	mParent.displayClosePerformed();
		    	return;
		    }
		});
		
		this.mCanvas = new Canvas();
		this.mCanvas.setIgnoreRepaint(true);
		this.mCanvas.setFocusable(true);
		this.mCanvas.setSize(this.mScreenSize.width, this.mScreenSize.height);
		this.mFrame.add(this.mCanvas);
		
		// Hide the cursor by setting it's image to null.
		this.mFrame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().createImage(""), new Point(), null));
		
		this.mFrame.setVisible(true);
		this.mFrame.pack();
		return;
	}
	
	public JFrame getJFrame()
	{
		return this.mFrame;
	}
	
	public Canvas getCanvas()
	{
		return this.mCanvas;
	}
	
	public void setUI(UI ui)
	{
		this.mUI = ui;
		
		return;
	}
	
	public UI getUI()
	{
		return this.mUI;
	}
	
	public void toggleKeyListener(boolean state)
	{
		if(state){
			this.mCanvas.addKeyListener(this);
		}else{
			this.mCanvas.removeKeyListener(this);
		}
		return;
	}
	
	public void toggleMouseListener(boolean state)
	{
		if(state){
			this.mCanvas.addMouseListener(this);
		}else{
			this.mCanvas.removeMouseListener(this);
		}
		return;
	}
	
	public void toggleMouseMotionListener(boolean state)
	{
		if(state){
			this.mCanvas.addMouseMotionListener(this);
		}else{
			this.mCanvas.removeMouseMotionListener(this);
		}
		return;
	}
	
	public void toggleMouseWheelListener(boolean state)
	{
		if(state){
			this.mCanvas.addMouseWheelListener(this);
		}else{
			this.mCanvas.removeMouseWheelListener(this);
		}
		return;
	}
	
	public void keyPressed(KeyEvent e)
	{
		this.mUI.keyPressed(e);
		return;
	}
	
	public void keyReleased(KeyEvent e)
	{
		this.mUI.keyReleased(e);
		return;
	}
	
	public void keyTyped(KeyEvent e)
	{
		this.mUI.keyTyped(e);
		return;
	}
	
	public void mouseClicked(MouseEvent e)
	{
		this.mUI.mouseClicked(e);
		return;
	}
	
	public void mouseEntered(MouseEvent e)
	{
		this.mUI.mouseEntered(e);
		return;
	}
	
	public void mouseExited(MouseEvent e)
	{
		this.mUI.mouseExited(e);
		return;
	}
	
	public void mousePressed(MouseEvent e)
	{
		this.mUI.mousePressed(e);
		return;
	}
	
	public void mouseReleased(MouseEvent e)
	{
		this.mUI.mouseReleased(e);
		return;
	}
	
	public void mouseDragged(MouseEvent e)
	{
		this.mUI.mouseDragged(e);
		return;
	}
	
	public void mouseMoved(MouseEvent e)
	{
		this.mUI.mouseMoved(e);
		return;
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		this.mUI.mouseWheelMoved(e);
		return;
	}
	
	public interface Listener
	{
		abstract void displayOpenPerformed();
		abstract void displayClosePerformed();
	}
}
