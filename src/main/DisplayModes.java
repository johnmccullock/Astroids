package main;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * Comprehensive class for retrieving all possible display modes.  This class uses 
 * java.awt.GraphicsDevice.getDisplayModes() to retrieve the DisplayMode array, but further
 * checks the output for duplicates and optionally sorts the list in ascending or descending
 * by width->bit depth->refresh rate.
 * 
 * @author John McCullock
 * @version 1.0 2014-11-14
 */
public class DisplayModes
{
	public static enum Sorted{ASCENDING, DESCENDING, NONE};
	
	public static Vector<DisplayMode> get()
	{
		return DisplayModes.getModes(DisplayModes.Sorted.NONE);
	}
	
	public static Vector<DisplayMode> get(DisplayModes.Sorted sortValue)
	{
		return DisplayModes.getModes(sortValue);
	}
	
	private static Vector<DisplayMode> getModes(DisplayModes.Sorted sortValue)
	{
		Vector<DisplayMode> result = new Vector<DisplayMode>();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = env.getDefaultScreenDevice();
		DisplayMode[] raw = gd.getDisplayModes();
		
		if(sortValue.equals(DisplayModes.Sorted.ASCENDING)){
			Arrays.sort(raw, new Ascending());
		}else if(sortValue.equals(DisplayModes.Sorted.DESCENDING)){
			Arrays.sort(raw, new Descending());
		}
		
		for(int i = 0; i < raw.length; i++)
		{
			boolean found = false;
			for(int j = 0; j < result.size(); j++)
			{
				if(raw[i].getWidth() == result.get(j).getWidth() &&
					raw[i].getHeight() == result.get(j).getHeight() && 
					raw[i].getBitDepth() == result.get(j).getBitDepth() && 
					raw[i].getRefreshRate() == result.get(j).getRefreshRate()){
					found = true;
				}
			}
			if(!found){
				result.add(raw[i]);
			}
		}
		return result;
	}
	
	private static class Ascending implements Comparator<DisplayMode>
	{
		public int compare(DisplayMode a, DisplayMode b)
		{
			if(a.getWidth() < b.getWidth()){
				return -1;
			}else if(a.getWidth() > b.getWidth()){
				return 1;
			}else{
				if(a.getBitDepth() < b.getBitDepth()){
					return -1;
				}else if(a.getBitDepth() > b.getBitDepth()){
					return 1;
				}else{
					if(a.getRefreshRate() < b.getRefreshRate()){
						return -1;
					}else if(a.getRefreshRate() > b.getRefreshRate()){
						return 1;
					}else{
						return 0;
					}
				}
			}
		}
	}
	
	private static class Descending implements Comparator<DisplayMode>
	{
		public int compare(DisplayMode a, DisplayMode b)
		{
			if(a.getWidth() > b.getWidth()){
				return -1;
			}else if(a.getWidth() < b.getWidth()){
				return 1;
			}else{
				if(a.getBitDepth() > b.getBitDepth()){
					return -1;
				}else if(a.getBitDepth() < b.getBitDepth()){
					return 1;
				}else{
					if(a.getRefreshRate() > b.getRefreshRate()){
						return -1;
					}else if(a.getRefreshRate() < b.getRefreshRate()){
						return 1;
					}else{
						return 0;
					}
				}
			}
		}
	}
}
