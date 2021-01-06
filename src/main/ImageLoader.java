package main;

import java.io.IOException;
import java.io.File;
import java.net.URL;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * This version includes JavaFX library functions.
 * @author John McCullock
 * @version 2.0 2013-07-08
 */
public class ImageLoader
{
	public static BufferedImage getImageFromFilePath(String path) throws IOException, Exception
	{
		BufferedImage aBMP = null;
		try{
			File aFile = new File(path);
			aBMP = ImageIO.read(aFile);
		}catch(IOException ioe){
			throw ioe;
		}catch(Exception ex){
			throw ex;
		}
		return aBMP;
	}
	
	public static BufferedImage getImageFromResourcePath(URL imageURL) throws Exception
	{
		BufferedImage aBMP = null;
		try{
			aBMP = ImageIO.read(imageURL);
		}catch(IOException ioe){
			throw ioe;
		}catch(Exception ex){
			throw ex;
		}
		return aBMP;
	}
}
