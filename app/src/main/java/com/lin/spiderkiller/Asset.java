/*
 * Author: Li Lin
 * Class Name: Asset
 * Describe: This Asset is a singleton that provides service such as play sound or access high score
 * to any other activity.
 * How to use:
 * It doesn't need to create. The right way to access it is to invoke this function:
 * Asset mp = Asset.getInstance();
 * History:
 *  06/27/2014 created
 * 	07/06/2014 add interface to store game score
 */
package com.lin.spiderkiller;

import java.io.IOException;

import com.lin.spiderkiller.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;


public class Asset
{
	class SoundInfo
	{
		private int soundId = -1;			    //load sound id 
		private boolean bload= true;			//if is loaded available
		private int resId = -1;
		private String resName;
		private SoundPool sp;
	}


	private Context context;
	private static Asset singleton = null;
	private SoundInfo[] arrSound;
	private MediaPlayer[] mp;
	private boolean isPlaySoundFx = true;
	private static int referenceCount = 0;
	private String[] music = {"bg","bgstart"};
	private String[] soundName = {"hit", "squish", "bomb","cheers","gameover","click","go"};
	private int score = 0;
	private SharedPreferences settting;
	private int curplaysongindex = 0;
	
	public static Asset getInstance()
	{
		if(singleton == null)
		{
			singleton = new Asset();
		}
		else
		{
			referenceCount++;
		}
		
		return singleton;
	}

	public void Initialize(Context context) 
	{
		this.context = context;
		
		Log.i ("SpiderKiller", "start to create sound instance."+ String.valueOf(soundName.length)); 
		
		arrSound = new SoundInfo[soundName.length];
        for(int i=0;i<soundName.length;i++)
        {
        	arrSound[i] = new SoundInfo();
        	if(arrSound[i].soundId == -1)
        	{
        		arrSound[i].sp =  new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
	        	arrSound[i].resName = soundName[i];
		    	arrSound[i].resId = context.getResources().getIdentifier(arrSound[i].resName, "raw", context.getPackageName());
		    	arrSound[i].soundId = arrSound[i].sp.load(context, arrSound[i].resId, 1); 
		    	arrSound[i].bload = false; 
		    	
	        }
        	
        	arrSound[i].sp.setOnLoadCompleteListener(mGloabal_OnCompleteListener);
            
        }
        
        mp = new MediaPlayer[music.length];
        
        for(int i=0; i<music.length;i++)
        {
	        mp[i] = MediaPlayer.create(context, context.getResources().getIdentifier(music[i], "raw", context.getPackageName())); 
	        mp[i].setLooping(true); // Set looping 
        }
        
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		settting = PreferenceManager.getDefaultSharedPreferences(context);

	}
	
   final SoundPool.OnLoadCompleteListener mGloabal_OnCompleteListener= new SoundPool.OnLoadCompleteListener()
   {
	        @Override
	        public void onLoadComplete(SoundPool soundPool, int mySoundId, int status) 
	        {
		    	Log.i ("SoundManager", "onLoadComplete"); 
				
				for(int i=0;i<soundName.length; i++)
				{
					if(soundPool.equals(arrSound[i].sp))
					{
				    	String s = "load sound successfully: " + arrSound[i]; 
				    	Log.i ("SoundManager", s); 
						arrSound[i].bload = true;
						break;
					}
				}
				
				Log.i ("SoundManager", "OnCompleteListener"); 
			}
	    
	};
	
	public void stopsound(String sound)
	{
		if(!isPlaySoundFx) return;
		
		for(int i=0;i<=soundName.length; i++)
		{
			if(soundName[i].equalsIgnoreCase(sound))
			{
				arrSound[i].sp.stop(arrSound[i].soundId);
				break;
			}
		}
	}
	
	public void playsound(String sound)
	{
		if(!isPlaySoundFx) return;
		
		float volume = getVolume();
		for(int i=0;i<=soundName.length; i++)
		{ 
			if(soundName[i].equalsIgnoreCase(sound) && arrSound[i].bload)
			{				
				arrSound[i].sp.play(arrSound[i].soundId, volume, volume, 1, 0, 1f);
				break;
			}
		}	

	}

	
	public void playSong(String songname) 
	{
		boolean isPlay = settting.getBoolean("prefs_music", true);
		if(!isPlay) return;
		
		float volume = getVolume();
		for(int i=0; i< music.length;i++)
		{
			if(music[i].equalsIgnoreCase(songname))
			{
				mp[i].setVolume(volume,volume);        
				if(!mp[i].isPlaying())
				{
					mp[i].start();
					curplaysongindex = i;
					break;
				}
			}
		}
		Log.i ("SoundManager", "start to play background");
	}
	
	
	public void stopSong(String songname)
	{
		boolean isPlay = settting.getBoolean("prefs_sound", true);
		if(!isPlay) return;
		
		for(int i=0; i< music.length;i++)
		{
			if(music[i].equalsIgnoreCase(songname))
			{
				if(mp[i].isPlaying())
				{
					mp[i].pause();
					break;
				}
			}
		}
		Log.i ("SoundManager", "stop to play background");
	}
	
	public void stopcurrentSong()
	{
		stopSong(music[curplaysongindex]);
	}
	
	public void playcurrentSong()
	{
		playSong(music[curplaysongindex]);
	}
		
	private float getVolume()
	{
    	AudioManager audioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		
		return volume;
	}
	
	
	public void setPlaySoundFx(boolean bflag)
	{
		Log.i ("SoundManager", "setPlaySoundFx:"+ String.valueOf(bflag));
		isPlaySoundFx = bflag;
	}
	
	public boolean getPlaySoundFx()
	{
		return isPlaySoundFx;
	}
	
	public int getscore()
	{
		return score;
	}
	
	public void setscore(int toscore)
	{
		score = toscore;
	}
}
