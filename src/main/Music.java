package main;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * Class which controls the game's background music.  Thankfully, Asteroids has a very simple soundtrack with
 * just two notes.
 * 
 * The "music" involves playing each note in alternating order.  The speed of the process can be sped up or slowed.
 * 
 * @author John McCullock
 * @version 2015-08-07
 */
public class Music
{
	protected enum Note
	{
		LOW("/assets/audio/low_note.au"),
		HIGH("/assets/audio/high_note.au");
		
		private String mURL = "";
		
		private Note(String url)
		{
			this.mURL = url;
			return;
		}
		
		public String getURL()
		{
			return this.mURL;
		}
	}
	
	protected final long SLOW = 900;
	protected final long FAST = 400;
	
	protected Clip mLowNote = null;
	protected Clip mHighNote = null;
	protected boolean mPlayHigh = false;
	protected long mCurrentRate = SLOW;
	protected long mRateStart = 0;
	protected boolean mIsRunning = false;
	
	public Music()
	{
		this.initialize();
		return;
	}
	
	private void initialize()
	{
		try{
			this.mLowNote = this.initializeClip(Note.LOW.getURL(), this.mLowNote);
			this.mHighNote = this.initializeClip(Note.HIGH.getURL(), this.mHighNote);
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return;
	}
	
	public void update()
	{
		if(!this.mIsRunning){
			return;
		}
		if(System.currentTimeMillis() - this.mRateStart > this.mCurrentRate){
			if(this.mPlayHigh){
				this.playHighNote();
			}else{
				this.playLowNote();
			}
			this.mPlayHigh = !this.mPlayHigh;
			this.mRateStart = System.currentTimeMillis();
		}
		return;
	}
	
	private Clip initializeClip(String url, Clip clip) throws Exception
	{
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(url));
		AudioFormat	format = audioInputStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		clip = (Clip)AudioSystem.getLine(info);
		clip.open(audioInputStream);
		return clip;
	}
	
	private void playLowNote()
	{
		this.mLowNote.setFramePosition(0);
		this.mLowNote.start();
		return;
	}
	
	private void playHighNote()
	{
		this.mHighNote.setFramePosition(0);
		this.mHighNote.start();
		return;
	}
	
	public void playSlowRate()
	{
		this.mCurrentRate = SLOW;
		return;
	}
	
	public void playFastRate()
	{
		this.mCurrentRate = FAST;
		return;
	}
	
	public void startMusic()
	{
		this.mIsRunning = true;
		return;
	}
	
	public void stopMusic()
	{
		this.mIsRunning = false;
		return;
	}
}
