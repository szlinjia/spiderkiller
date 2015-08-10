/*
 * Author: Li Lin
 * Class Name: AppPreferenceActivity
 * History: 06/27/2014 created
 * describe: This class is used to create preference UI to control some app options, such as
 * background music and sound effect.
 */

package com.lin.spiderkiller;

import com.lin.spiderkiller.R;

import android.content.Context;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class AppPreferenceActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener	
{

	Context context;
	Asset soundmgr;
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate (savedInstanceState);
        
        context = this;
        
        addPreferencesFromResource(R.xml.preferences);
        soundmgr = Asset.getInstance();
        
    }	
	
	
    final OnPreferenceClickListener mPreference_OnClickListener = new OnPreferenceClickListener() 
    {
        public boolean onPreferenceClick(Preference preference) 
        {

    		return true;
        }
    };
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {

    }
    
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) 
	{
		Log.i("SpiderKiller", "onSharedPreferenceChanged "+ key);
		if (key.equals("prefs_music"))
		{
			boolean isPlay = sharedPreferences.getBoolean(key, true);
			if(!isPlay)
			{
				soundmgr.stopcurrentSong();
			}
			else
			{
				soundmgr.playcurrentSong();
			}
			Log.i("PreferenceLog", "change prefs_music:"+String.valueOf(isPlay));
			
		}
		else if(key.equals("prefs_sound"))
		{
			boolean isPlay = sharedPreferences.getBoolean(key, true);
			soundmgr.setPlaySoundFx(isPlay);
			Log.i("PreferenceLog", "change prefs_sound:"+String.valueOf(isPlay));
		}
	}
    
	@Override 
	protected void onResume() 
	{
		super.onResume(); 
		
		onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(), "prefs_music");
	    getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
        
        Preference pref = findPreference("prefs_score");
        int score = soundmgr.getscore();
        pref.setSummary(Integer.toString(score));
	}
	
	@Override 
	protected void onPause() 
	{
		super.onPause();
	    getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
	    Asset myset = Asset.getInstance();
	    myset.stopSong("bgstart");
	}
}
