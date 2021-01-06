package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import main.polycomp.PItem;
import main.polycomp.PList;
import main.polycomp.PMessage;
import main.polycomp.PolyShape;
import main.polycomp.PScrollBar;

/**
 * Class for controlling mouse and keyboard input for the OptionsWindow class.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class UIOptionsWindow extends UI
{
	private UIOptionsWindow.Listener mListener = null;
	private MenuMode mMenuMode = null;
	private GraphicsOptionsMode mGraphicsOptionsMode = null;
	private AdvancedOptionsMode mAdvancedOptionsMode = null;
	private AudioOptionsMode mAudioOptionsMode = null;
	private KeyBindingsMode mKeyBindingsMode = null;
	private BindingMode mBindingMode = null;
	private MessageWindowMode mMessageWindowMode = null;
	private UIState<UIOptionsWindow.Listener> mCurrentState = null;
	
	public UIOptionsWindow(UIOptionsWindow.Listener listener)
	{
		this.mListener = listener;
		this.mMenuMode = new MenuMode(listener);
		this.mGraphicsOptionsMode = new GraphicsOptionsMode(listener);
		this.mAdvancedOptionsMode = new AdvancedOptionsMode(listener);
		this.mAudioOptionsMode = new AudioOptionsMode(listener);
		this.mKeyBindingsMode = new KeyBindingsMode(listener);
		this.mBindingMode = new BindingMode(listener);
		this.mMessageWindowMode = new MessageWindowMode(listener);
		this.mCurrentState = this.mMenuMode;
		return;
	}
	
	public void setToMenuMode()
	{
		this.mCurrentState = this.mMenuMode;
		return;
	}
	
	public void setToGraphicsMode()
	{
		this.mCurrentState = this.mGraphicsOptionsMode;
		return;
	}
	
	public void setToAdvancedMode()
	{
		this.mCurrentState = this.mAdvancedOptionsMode;
		return;
	}
	
	public void setToMessageWindowMode()
	{
		this.mCurrentState = this.mMessageWindowMode;
		return;
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
	
	private class MenuMode extends UIState<UIOptionsWindow.Listener>
	{
		private Point mMouseDrag = new Point();
		
		public MenuMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_UP){
				
			}else if(e.getKeyCode() == KeyEvent.VK_DOWN){
				
			}else if(e.getKeyCode() == KeyEvent.VK_ENTER){
				this.mListener.enterPressed();
			}else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
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
			return;
		}
		
		@Override
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
			if(this.mListener.getControlArea().getPolygon().contains(e.getPoint())){
				if(this.mListener.getCategory() == OptionsWindow.Category.GRAPHICS){
					mCurrentState = mGraphicsOptionsMode;
				}else if(this.mListener.getCategory() == OptionsWindow.Category.ADVANCED){
					mCurrentState = mAdvancedOptionsMode;
				}else if(this.mListener.getCategory() == OptionsWindow.Category.SOUND){
					mCurrentState = mAudioOptionsMode;
				}else if(this.mListener.getCategory() == OptionsWindow.Category.CONTROLS){
					mCurrentState = mKeyBindingsMode;
				}
				return;
			}
			return;
		}
		
		@Override
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
			
//			if(this.mListener.getScrollBar().getGrip().getPolygon().contains(e.getPoint())){
//				this.mListener.getScrollBar().gripPerformed(e.getX(), e.getY());
//			}
			return;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			for(PItem button : this.mListener.getButtons())
			{
				button.setComponentSelected(false);
				if(button.getPolygon().contains(e.getPoint())){
					this.mListener.optionsMenuButtonPressed(button.getType());
				}
			}
			return;
		}
	}
	
	private class GraphicsOptionsMode extends UIState<UIOptionsWindow.Listener>
	{
		private boolean isDragging = false;
		private Point mMouseDrag = new Point();
		
		public GraphicsOptionsMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
				return;
			}
			return;
		}
		
		@Override
		public void mouseClicked(MouseEvent e)
		{
			return;
		}
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			int dx = e.getXOnScreen() - this.mMouseDrag.x;
			int dy = e.getYOnScreen() - this.mMouseDrag.y;
			
			if(this.isDragging){
				this.mListener.getScrollBar().setComponentSelected(true);
				this.mListener.setDisplayModeListScrollY(this.mListener.getScrollBar().scroll(e.getY()));
			}
			
			this.mMouseDrag.x += dx;
			this.mMouseDrag.y += dy;
			return;
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			if(!this.mListener.getDisplayModeList().getBounds().contains(e.getPoint())){
				this.resetAllListItems();
			}
			
			if(!this.mListener.getControlArea().getPolygon().contains(e.getPoint())){
				mCurrentState = mMenuMode;
				return;
			}
			
			for(PItem item : this.mListener.getDisplayModeList().getItems())
			{
				if(item.getPolygon().contains(e.getPoint())){
					item.setComponentHovered(true);
				}else{
					item.setComponentHovered(false);
				}
			}
			
			if(this.mListener.getScrollBar().getGrip().getPolygon().contains(e.getPoint())){
				this.mListener.getScrollBar().setComponentHovered(true);
			}else if(this.mListener.getScrollBar().getDecrementButtonBounds().contains(e.getPoint())){
				this.mListener.getScrollBar().setComponentHovered(true);
			}else if(this.mListener.getScrollBar().getIncrementButtonBounds().contains(e.getPoint())){
				this.mListener.getScrollBar().setComponentHovered(true);
			}else{
				this.mListener.getScrollBar().setComponentHovered(false);
			}
			
			if(this.mListener.getVSyncButtonArea().contains(e.getPoint())){
				this.mListener.getVSyncPrevButton().setComponentHovered(true);
				this.mListener.getVSyncNextButton().setComponentHovered(true);
			}else{
				this.mListener.getVSyncPrevButton().setComponentHovered(false);
				this.mListener.getVSyncNextButton().setComponentHovered(false);
			}
			return;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(this.mListener.getDisplayModeList().getBounds().contains(e.getPoint())){
				for(PItem item : this.mListener.getDisplayModeList().getItems())
				{
					if(item.getPolygon().contains(e.getPoint())){
						item.setComponentSelected(true);
					}else{
						item.setComponentSelected(false);
					}
				}
			}
			
			if(this.mListener.getScrollBar().getGrip().getPolygon().contains(e.getPoint())){
				this.mListener.getScrollBar().setComponentSelected(true);
				this.mListener.getScrollBar().gripPerformed(e.getX(), e.getY());
				this.isDragging = true;
			}else if(this.mListener.getScrollBar().getDecrementButtonBounds().contains(e.getPoint())){
				this.mListener.getScrollBar().setComponentSelected(true);
			}else if(this.mListener.getScrollBar().getIncrementButtonBounds().contains(e.getPoint())){
				this.mListener.getScrollBar().setComponentSelected(true);
			}else{
				this.mListener.getScrollBar().setComponentSelected(false);
			}
			
			if(this.mListener.getVSyncPrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getVSyncPrevButton().setComponentSelected(true);
			}
			if(this.mListener.getVSyncNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getVSyncNextButton().setComponentSelected(true);
			}
			return;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(this.mListener.getDisplayModeList().getBounds().contains(e.getPoint())){
				int count = 0;
				for(PItem item : this.mListener.getDisplayModeList().getItems())
				{
					if(item.getPolygon().contains(e.getPoint())){
						this.mListener.setModeBoxText(String.valueOf(item.getContents()));
						this.mListener.findDisplayModeValue(count);
					}
					item.setComponentSelected(false);
					count++;
				}
			}
			
			if(this.mListener.getScrollBar().getDecrementButtonBounds().contains(e.getPoint())){
				this.mListener.setDisplayModeListScrollY(this.mListener.getScrollBar().smallVerticalScroll(-1));
			}else if(this.mListener.getScrollBar().getIncrementButtonBounds().contains(e.getPoint())){
				this.mListener.setDisplayModeListScrollY(this.mListener.getScrollBar().smallVerticalScroll(1));
			}
			
			if(this.mListener.getVSyncButtonArea().contains(e.getPoint())){
				this.mListener.toggleVSyncValue();
			}
			this.mListener.getVSyncPrevButton().setComponentSelected(false);
			this.mListener.getVSyncNextButton().setComponentSelected(false);
			
			this.isDragging = false;
			this.mListener.getScrollBar().setComponentSelected(false);
			return;
		}
		
		private void resetAllListItems()
		{
			for(PItem item : this.mListener.getDisplayModeList().getItems())
			{
				item.setComponentHovered(false);
				item.setComponentSelected(false);
			}
			return;
		}
	}
	
	private class AdvancedOptionsMode extends UIState<UIOptionsWindow.Listener>
	{
		public AdvancedOptionsMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
				return;
			}
			return;
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			if(!this.mListener.getControlArea().getPolygon().contains(e.getPoint())){
				mCurrentState = mMenuMode;
				return;
			}
			if(this.mListener.getRenderQualityArea().contains(e.getPoint())){
				this.mListener.getRenderQualityPrevButton().setComponentHovered(true);
				this.mListener.getRenderQualityNextButton().setComponentHovered(true);
			}else{
				this.mListener.getRenderQualityPrevButton().setComponentHovered(false);
				this.mListener.getRenderQualityNextButton().setComponentHovered(false);
			}
			if(this.mListener.getAntialiasArea().contains(e.getPoint())){
				this.mListener.getAntialiasPrevButton().setComponentHovered(true);
				this.mListener.getAntialiasNextButton().setComponentHovered(true);
			}else{
				this.mListener.getAntialiasPrevButton().setComponentHovered(false);
				this.mListener.getAntialiasNextButton().setComponentHovered(false);
			}
			if(this.mListener.getRenderColorArea().contains(e.getPoint())){
				this.mListener.getRenderColorPrevButton().setComponentHovered(true);
				this.mListener.getRenderColorNextButton().setComponentHovered(true);
			}else{
				this.mListener.getRenderColorPrevButton().setComponentHovered(false);
				this.mListener.getRenderColorNextButton().setComponentHovered(false);
			}
			if(this.mListener.getRenderAlphaArea().contains(e.getPoint())){
				this.mListener.getRenderAlphaPrevButton().setComponentHovered(true);
				this.mListener.getRenderAlphaNextButton().setComponentHovered(true);
			}else{
				this.mListener.getRenderAlphaPrevButton().setComponentHovered(false);
				this.mListener.getRenderAlphaNextButton().setComponentHovered(false);
			}
			if(this.mListener.getShowFPSArea().contains(e.getPoint())){
				this.mListener.getShowFPSPrevButton().setComponentHovered(true);
				this.mListener.getShowFPSNextButton().setComponentHovered(true);
			}else{
				this.mListener.getShowFPSPrevButton().setComponentHovered(false);
				this.mListener.getShowFPSNextButton().setComponentHovered(false);
			}
			return;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(this.mListener.getRenderQualityPrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getRenderQualityPrevButton().setComponentSelected(true);
			}else if(this.mListener.getRenderQualityNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getRenderQualityNextButton().setComponentSelected(true);
			}else if(this.mListener.getAntialiasPrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getAntialiasPrevButton().setComponentSelected(true);
			}else if(this.mListener.getAntialiasNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getAntialiasNextButton().setComponentSelected(true);
			}else if(this.mListener.getRenderColorPrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getRenderColorPrevButton().setComponentSelected(true);
			}else if(this.mListener.getRenderColorNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getRenderColorNextButton().setComponentSelected(true);
			}else if(this.mListener.getRenderAlphaPrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getRenderAlphaPrevButton().setComponentSelected(true);
			}else if(this.mListener.getRenderAlphaNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getRenderAlphaNextButton().setComponentSelected(true);
			}else if(this.mListener.getShowFPSPrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getShowFPSPrevButton().setComponentSelected(true);
			}else if(this.mListener.getShowFPSNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getShowFPSNextButton().setComponentSelected(true);
			}
			return;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(this.mListener.getRenderQualityArea().contains(e.getPoint())){
				this.mListener.toggleRenderQualityValue();
			}
			if(this.mListener.getAntialiasArea().contains(e.getPoint())){
				this.mListener.toggleAntialiasValue();
			}
			if(this.mListener.getRenderColorArea().contains(e.getPoint())){
				this.mListener.toggleRenderColorValue();
			}
			if(this.mListener.getRenderAlphaArea().contains(e.getPoint())){
				this.mListener.toggleRenderAlphaValue();
			}
			if(this.mListener.getShowFPSArea().contains(e.getPoint())){
				this.mListener.toggleShowFPSValue();
			}
			
			this.mListener.getRenderQualityPrevButton().setComponentSelected(false);
			this.mListener.getRenderQualityNextButton().setComponentSelected(false);
			this.mListener.getAntialiasPrevButton().setComponentSelected(false);
			this.mListener.getAntialiasNextButton().setComponentSelected(false);
			this.mListener.getRenderColorPrevButton().setComponentSelected(false);
			this.mListener.getRenderColorNextButton().setComponentSelected(false);
			this.mListener.getRenderAlphaPrevButton().setComponentSelected(false);
			this.mListener.getRenderAlphaNextButton().setComponentSelected(false);
			this.mListener.getShowFPSPrevButton().setComponentSelected(false);
			this.mListener.getShowFPSNextButton().setComponentSelected(false);
			return;
		}
	}
	
	private class AudioOptionsMode extends UIState<UIOptionsWindow.Listener>
	{
		public AudioOptionsMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
				return;
			}
			return;
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			if(!this.mListener.getControlArea().getPolygon().contains(e.getPoint())){
				mCurrentState = mMenuMode;
				return;
			}
			
			if(this.mListener.getVolumeRectangle().contains(e.getPoint())){
				this.mListener.getVolumeDownButton().setComponentHovered(true);
				this.mListener.getVolumeUpButton().setComponentHovered(true);
			}else{
				this.mListener.getVolumeDownButton().setComponentHovered(false);
				this.mListener.getVolumeUpButton().setComponentHovered(false);
			}
			if(this.mListener.getMuteRectangle().contains(e.getPoint())){
				this.mListener.getMutePrevButton().setComponentHovered(true);
				this.mListener.getMuteNextButton().setComponentHovered(true);
			}else{
				this.mListener.getMutePrevButton().setComponentHovered(false);
				this.mListener.getMuteNextButton().setComponentHovered(false);
			}
			return;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(this.mListener.getVolumeDownArea().contains(e.getPoint())){
				this.mListener.getVolumeDownButton().setComponentSelected(true);
			}else if(this.mListener.getVolumeUpArea().contains(e.getPoint())){
				this.mListener.getVolumeUpButton().setComponentSelected(true);
			}else if(this.mListener.getMutePrevButton().getPolygon().contains(e.getPoint())){
				this.mListener.getMutePrevButton().setComponentSelected(true);
			}else if(this.mListener.getMuteNextButton().getPolygon().contains(e.getPoint())){
				this.mListener.getMuteNextButton().setComponentSelected(true);
			}
			return;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(this.mListener.getVolumeDownArea().contains(e.getPoint())){
				this.mListener.volumeDownPressed();
			}
			if(this.mListener.getVolumeUpArea().contains(e.getPoint())){
				this.mListener.volumeUpPressed();
			}
			if(this.mListener.getMuteRectangle().contains(e.getPoint())){
				this.mListener.toggleMuteValue();
			}
			this.mListener.getVolumeDownButton().setComponentSelected(false);
			this.mListener.getVolumeUpButton().setComponentSelected(false);
			this.mListener.getMutePrevButton().setComponentSelected(false);
			this.mListener.getMuteNextButton().setComponentSelected(false);
			return;
		}
	}
	
	private class KeyBindingsMode extends UIState<UIOptionsWindow.Listener>
	{
		public KeyBindingsMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.escapePressed();
				return;
			}
			return;
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			if(!this.mListener.getControlArea().getPolygon().contains(e.getPoint())){
				mCurrentState = mMenuMode;
				return;
			}
			
			for(PItem button : this.mListener.getKeyButtons())
			{
				if(button.getPolygon().contains(e.getPoint())){
					button.setComponentHovered(true);
				}else{
					button.setComponentHovered(false);
				}
			}
			return;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			for(PItem button : this.mListener.getKeyButtons())
			{
				if(button.getPolygon().contains(e.getPoint())){
					button.setComponentSelected(true);
				}else{
					button.setComponentSelected(false);
				}
			}
			return;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			int chosen = -1;
			for(int i = 0; i < this.mListener.getKeyButtons().length; i++)
			{
				this.mListener.getKeyButtons()[i].setComponentSelected(false);
				if(this.mListener.getKeyButtons()[i].getPolygon().contains(e.getPoint())){
					chosen = i;
				}
			}
			
			if(chosen > -1){
				this.mListener.getKeyButtons()[chosen].setComponentSelected(true);
				mBindingMode.setButtonIndex(chosen);
				mCurrentState = mBindingMode;
				return;
			}
			return;
		}
	}
	
	private class BindingMode extends UIState<UIOptionsWindow.Listener>
	{
		private int mButtonIndex = 0;
		
		public BindingMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		public void setButtonIndex(int index)
		{
			this.mButtonIndex = index;
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			this.resetButtons();
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				mCurrentState = mKeyBindingsMode;
				return;
			}
			this.mListener.setKeyBinding(this.mButtonIndex, e.getKeyCode());
			mCurrentState = mKeyBindingsMode;
			return;
		}
		
		private void resetButtons()
		{
			for(PItem button : this.mListener.getKeyButtons())
			{
				button.setComponentHovered(false);
				button.setComponentSelected(false);
			}
			return;
		}
	}
	
	private class MessageWindowMode extends UIState<UIOptionsWindow.Listener>
	{
		public MessageWindowMode(UIOptionsWindow.Listener listener)
		{
			super(listener);
			return;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
				this.mListener.closeMessageWindow();
				return;
			}
			return;
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			for(PItem button : this.mListener.getMessageWindow().getButtons())
			{
				if(button.getPolygon().contains(e.getPoint())){
					button.setComponentHovered(true);
				}else{
					button.setComponentHovered(false);
				}
			}
			return;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			for(PItem button : this.mListener.getMessageWindow().getButtons())
			{
				if(button.getPolygon().contains(e.getPoint())){
					button.setComponentSelected(true);
				}else{
					button.setComponentSelected(false);
				}
			}
			return;
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			PMessage.ButtonType buttonType = null;
			for(PItem button : this.mListener.getMessageWindow().getButtons())
			{
				button.setComponentSelected(false);
				if(button.getPolygon().contains(e.getPoint())){
					buttonType = (PMessage.ButtonType)button.getType();
				}
			}
			if(buttonType != null){
				this.mListener.messageWindowButtonPressed(buttonType);
			}
			return;
		}
	}
	
	public interface Listener
	{
		abstract void enterPressed();
		abstract void escapePressed();
		abstract PItem[] getButtons();
		abstract void optionsMenuButtonPressed(Object type);
		abstract OptionsWindow.Category getCategory();
		abstract PList getDisplayModeList();
		abstract PolyShape getControlArea();
		abstract PScrollBar getScrollBar();
		abstract void setDisplayModeListScrollY(double scroll);
		abstract void setModeBoxText(String text);
		abstract void findDisplayModeValue(int index);
		abstract Rectangle getVSyncButtonArea();
		abstract PItem getVSyncPrevButton();
		abstract PItem getVSyncNextButton();
		abstract void toggleVSyncValue();
		abstract Rectangle getRenderQualityArea();
		abstract PItem getRenderQualityPrevButton();
		abstract PItem getRenderQualityNextButton();
		abstract Rectangle getAntialiasArea();
		abstract PItem getAntialiasPrevButton();
		abstract PItem getAntialiasNextButton();
		abstract Rectangle getRenderColorArea();
		abstract PItem getRenderColorPrevButton();
		abstract PItem getRenderColorNextButton();
		abstract Rectangle getRenderAlphaArea();
		abstract PItem getRenderAlphaPrevButton();
		abstract PItem getRenderAlphaNextButton();
		abstract PItem getShowFPSPrevButton();
		abstract PItem getShowFPSNextButton();
		abstract Rectangle getShowFPSArea();
		abstract void toggleRenderQualityValue();
		abstract void toggleAntialiasValue();
		abstract void toggleRenderColorValue();
		abstract void toggleRenderAlphaValue();
		abstract void toggleShowFPSValue();
		abstract Rectangle getVolumeRectangle();
		abstract Rectangle getVolumeDownArea();
		abstract Rectangle getVolumeUpArea();
		abstract PItem getVolumeDownButton();
		abstract PItem getVolumeUpButton();
		abstract PItem getMutePrevButton();
		abstract PItem getMuteNextButton();
		abstract Rectangle getMuteRectangle();
		abstract void volumeDownPressed();
		abstract void volumeUpPressed();
		abstract void toggleMuteValue();
		abstract PItem[] getKeyButtons();
		abstract void setKeyBinding(int keyButton, int keyCode);
		abstract PMessage getMessageWindow();
		abstract void messageWindowButtonPressed(PMessage.ButtonType button);
		abstract  void closeMessageWindow();
	}
}
