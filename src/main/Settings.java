package main;

import java.awt.DisplayMode;
import java.util.HashMap;

/**
 * Data class for application settings.
 * @author John McCullock
 * @version 1.0 2015-08-07
 */
public class Settings
{
	public DisplayMode displayMode = null;
	public RenderQuality renderQuality = null;
	public RenderAntialias renderAntialias = null;
	public RenderColor renderColor = null;
	public RenderAlpha renderAlpha = null;
	public boolean showPFS = false;
	public boolean useVSync = false;
	public HashMap<KeyNames, Integer> keyBindings = new HashMap<KeyNames, Integer>();
	public boolean muteSound = false;
	public float volume = 0f;
}
