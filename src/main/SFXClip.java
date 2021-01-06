package main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SFXClip implements LineListener
{
	private final int BUFFER_SIZE = 16384;
	
	private byte[] mAudioStream = null;
	private AudioFormat mFormat = null;
	/**
	 * The reason for using a circular list is because sometimes the same sound needs to be played in 
	 * rapid succession, but a single Clip won't allow a another start until it has fully completed.  Even
	 * resetting the frame to zero didn't help.  The Clip's stream just blocks until it's finished. Flush
	 * and drain didn't help much either.
	 * 
	 * So the idea is to play the next one in the circle when playClip is called, with the *guess* that by the
	 * time the Clip comes back around in the circle, it will be finished with it's previous play.
	 * 
	 * The added benefit is that successively called Clips will layer over each other - making use of 
	 * multi-channel sound.
 	 */
	private CircularLinkedList<Clip> mClips = new CircularLinkedList<Clip>();
	
	public SFXClip(String filepath, int numCopies) throws Exception
	{
		this.createStream(filepath);
		this.initializeClips(numCopies);
		return;
	}
	
	private void initializeClips(int numCopies) throws Exception
	{
		for(int i = 0; i < numCopies; i++)
		{
			Clip c = AudioSystem.getClip();
			c.open(this.mFormat, this.mAudioStream, 0, mAudioStream.length);
			this.mClips.insert(c);
		}
	}
	
	private void createStream(String filepath) throws Exception
	{
		try{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(filepath));
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[BUFFER_SIZE];
			while((nRead = audioInputStream.read(data, 0, data.length)) != -1)
			{
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			
			this.mAudioStream = buffer.toByteArray();
			this.mFormat = audioInputStream.getFormat();
			
			audioInputStream.close();
		}catch(IOException ex){
			throw new Exception("IOException: " + ex.getMessage());
		}catch(UnsupportedAudioFileException ex){
			throw new Exception("UnsupportedAudioFileException: " + ex.getMessage());
		}catch(Exception ex){
			throw ex;
		}
		return;
	}
	
	public void playClip()
	{
		Clip c = this.mClips.getNext();
		c.setFramePosition(0);
		c.start();
		return;
	}
	
	public AudioFormat getFormat()
	{
		return this.mFormat;
	}
	
	public byte[] getStream()
	{
		return this.mAudioStream;
	}
	
	public void update(LineEvent e)
	{
//		this.mClips.peekRewind();
//		while(this.mClips.hasNextPeek())
//		{
//			Clip c = this.mClips.peekNext();
//			if(e.getSource() == c){
//				
//			}
//		}
		return;
	}
}
