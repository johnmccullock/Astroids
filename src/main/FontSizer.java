package main;

/**
 * Determines a font size based on a factor of some height.  For example, a factor of 0.5 of the current screen height.
 * The returned font size is the one closest in a series of pre-chosen font sizes.
 * 
 * Version 1.1 adds getFontSize(double, int).
 * Version 1.3 corrects getFontSize(double, int) to include checks for out-of-range input.
 * Version 1.4 uses floats instead of doubles.
 * 
 * @author John McCullock
 * @version 1.4 2015-07-07
 */
public class FontSizer
{
	private static final int[] sizes = new int[]{10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
	
	public static int getFontSize(float factor, int newHeight)
	{
		int result = 0;
		float newSize = factor * (float)newHeight;
		int found = -1;
		if(newSize < sizes[0]){
			return sizes[0];
		}else if(newSize > sizes[sizes.length - 1]){
			return sizes[sizes.length - 1];
		}
		for(int i = 0; i < sizes.length - 1; i++)
		{
			if(newSize > sizes[i] && newSize < sizes[i + 1]){
				found = i;
				break;
			}else if(newSize == sizes[i]){
				return sizes[i];
			}
		}
		if(found > -1){
			result = closerTo(newSize, sizes[found], sizes[found + 1]);
		}
		return result;
	}
	
	public static int getFontSize(int currentHeight, int currentFontSize, int newHeight)
	{
		int result = 0;
		float newSize = (currentFontSize / currentHeight) * newHeight;
		int found = -1;
		for(int i = 0; i < sizes.length - 1; i++)
		{
			if(sizes[i] < newSize && sizes[i + 1] > newSize){
				found = i;
				break;
			}else if(newSize == sizes[i]){
				return sizes[i];
			}
		}
		if(found > -1){
			result = closerTo(newSize, sizes[found], sizes[found + 1]);
		}
		return result;
	}
	
	private static int closerTo(float value, float lower, float higher)
	{
		int result = 0;
		float middle = (lower + higher) / 2f;
		if(value < middle){
			result = (int)Math.round(lower);
		}else{
			result = (int)Math.round(higher);
		}
		return result;
	}
}
