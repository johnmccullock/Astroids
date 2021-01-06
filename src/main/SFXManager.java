package main;

import java.util.Random;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class SFXManager
{
	public enum Sounds
	{
		CANNON("/assets/audio/fire1.au", 4),
		ENGINE("/assets/audio/engine_roar_short_x16.au", 0),
		EXPLOSION1("/assets/audio/explosion1.au", 1),
		EXPLOSION2("/assets/audio/explosion2.au", 1),
		SMALL_UFO("/assets/audio/small_ufo.au", 0),
		LARGE_UFO("/assets/audio/large_ufo.au", 0);
		
		private String mURL = "";
		private int mCopies = 0;
		
		private Sounds(String URL, int copies)
		{
			this.mURL = URL;
			this.mCopies = copies;
			return;
		}
		
		public String getURL()
		{
			return this.mURL;
		}
		
		public int getNumCopies()
		{
			return this.mCopies;
		}
	}
	
	protected SFXClip mCannon = null;
	protected Clip mEngine = null;
	protected Vector<SFXClip> mExplosions = new Vector<SFXClip>();
	protected Clip mSmallUFO = null;
	protected Clip mLargeUFO = null;
	
	public SFXManager()
	{
		this.initializeCannonSound();
		this.initializeEngineSound();
		this.initializeExplosions();
		this.initializeUFOs();
		return;
	}
	
	private void initializeCannonSound()
	{
		try{
			this.mCannon = new SFXClip(Sounds.CANNON.getURL(), Sounds.CANNON.getNumCopies());
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return;
	}
	
	private void initializeEngineSound()
	{
		this.mEngine = this.initializeClip(Sounds.ENGINE.getURL(), this.mEngine);
		return;
	}
	
	private void initializeExplosions()
	{
		try{
			this.mExplosions.add(new SFXClip(Sounds.EXPLOSION1.getURL(), Sounds.EXPLOSION1.getNumCopies()));
			this.mExplosions.add(new SFXClip(Sounds.EXPLOSION2.getURL(), Sounds.EXPLOSION2.getNumCopies()));
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return;
	}
	
	private void initializeUFOs()
	{
		this.mSmallUFO = this.initializeClip(Sounds.SMALL_UFO.getURL(), this.mSmallUFO);
		this.mLargeUFO = this.initializeClip(Sounds.LARGE_UFO.getURL(), this.mLargeUFO);
		return;
	}
	
	private Clip initializeClip(String url, Clip clip)
	{
		try{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource(url));
			AudioFormat	format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(audioInputStream);
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(1);
		}
		return clip;
	}
	
 	public void playCannonClip()
	{
		this.mCannon.playClip();
		return;
	}
	
	public void playExplosionClip()
	{
		this.mExplosions.get(new Random().nextInt(this.mExplosions.size())).playClip();
		return;
	}
	
	public void startEngineSound()
	{
		this.mEngine.loop(Clip.LOOP_CONTINUOUSLY);
		this.mEngine.setFramePosition(0);
		this.mEngine.start();
		return;
	}
	
	public void stopEngineSound()
	{
		this.mEngine.stop();
		return;
	}
	
	public boolean engineSoundIsPlaying()
	{
		return this.mEngine.isRunning();
	}
	
	public void startSmallUFO()
	{
		this.mSmallUFO.loop(Clip.LOOP_CONTINUOUSLY);
		this.mSmallUFO.setFramePosition(0);
		this.mSmallUFO.start();
		return;
	}
	
	public void stopSmallUFO()
	{
		this.mSmallUFO.stop();
		return;
	}
	
	public void startLargeUFO()
	{
		this.mLargeUFO.loop(Clip.LOOP_CONTINUOUSLY);
		this.mLargeUFO.setFramePosition(0);
		this.mLargeUFO.start();
		return;
	}
	
	public void stopLargeUFO()
	{
		this.mLargeUFO.stop();
		return;
	}
	
	public void setVolume(float value)
	{
		try{
			if(value < 0.0f){
				this.getVolumeControl().setValue(0.0f);
			}else if(value > 1.0f){
				this.getVolumeControl().setValue(1.0f);
			}else{
				this.getVolumeControl().setValue(value);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return;
	}
	
	public float getVolume()
	{
		float value = 0f;
		try{
			value = this.getVolumeControl().getValue();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return value;
	}
	
	private FloatControl getVolumeControl() throws Exception
	{
		try{
			Mixer.Info mixers[] = AudioSystem.getMixerInfo();
			for(Mixer.Info mixerInfo : mixers)
			{
				Mixer mixer = AudioSystem.getMixer(mixerInfo);
				mixer.open();
				
				//we check only target type lines, because we are looking for "SPEAKER target port"
				for(Line.Info info : mixer.getTargetLineInfo())
				{
					if(info.toString().contains("SPEAKER")){
						Line line = mixer.getLine(info);
						try{
							line.open();
						}catch(IllegalArgumentException iae){}
						return (FloatControl)line.getControl(FloatControl.Type.VOLUME);
					}
				}
			}
		}catch(Exception ex){
			System.out.println("problem creating volume control object:" + ex);
			throw ex;
		}
		throw new Exception("unknown problem creating volume control object");
	}
}
