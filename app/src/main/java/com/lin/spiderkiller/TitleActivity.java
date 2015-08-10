/*Game Name:SpiderKiller
 *Class name: TitleActivity
 *Author: Li Lin & Shaomin Zhang
 *Date: 07/06/2014
 *Describe: This class is the first screen that displays in game. User can click to play
 *or go to setting screen to turn off the background music or sound effect. The highest
 *score also is recorded in this screen.
 */
package com.lin.spiderkiller;

import com.lin.spiderkiller.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TitleActivity extends Activity 
{
	Button btnStart;
	Button btnExit;
	Button btnSetting;
	Asset sound;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    	// Disable the title
    	requestWindowFeature (Window.FEATURE_NO_TITLE);
    	// Make full screen
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	setContentView(R.layout.title);
    	
    	btnStart = (Button) findViewById(R.id.button1);
    	btnExit = (Button) findViewById(R.id.button2);
    	btnSetting = (Button) findViewById(R.id.button3);
    	
    	btnStart.setOnClickListener(mGlobal_OnClickListener);
    	btnExit.setOnClickListener(mGlobal_OnClickListener);
    	btnSetting.setOnClickListener(mGlobal_OnClickListener);  
    	
		sound = Asset.getInstance();
		sound.Initialize(this);

		sound.playSong("bgstart");

    }


    //Global On click listener for all views
    final OnClickListener mGlobal_OnClickListener = new OnClickListener() 
    {
    	@Override
        public void onClick(final View v) 
        {
            switch(v.getId()) 
            {
                case R.id.button1:
                	OnClickStartGame();
                	break;
                case R.id.button2:
                	OnClickExit();  
                    break;
                case R.id.button3:
                	OnClickSetting();
                	break;
                default:
                	break;
            }
        }
    };
    
    private void OnClickStartGame()
    {
    	sound.playsound("click");
    	sound.stopSong("bgstart");
    	startActivity(new Intent(this, MainActivity.class));
    }
    
    private void OnClickExit()
    {
    	sound.playsound("click");
    	finish();
    }
    
    private void OnClickSetting()
    {
    	sound.playsound("click");
    	startActivity (new Intent(this, AppPreferenceActivity.class));
    }
    
	@Override
	protected void onPause () 
	{
		super.onPause();
		//v.pause();
		
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();
        
        editor.putInt("high_score", sound.getscore());
        editor.commit();

		sound.stopSong("bgstart");
		
	}

	@Override
	protected void onResume () 
	{
		super.onResume();
		//v.resume();
		sound.playSong("bgstart");
        SharedPreferences settings = getPreferences(0);
        int score = settings.getInt("high_score", 0);
        
        //update high score
        if(sound.getscore() > score)
        {
            SharedPreferences.Editor editor = settings.edit();
            
            editor.putInt("high_score", sound.getscore());
            editor.commit();
        }
        else
        {
        	sound.setscore(score);
        }
        
	}
}
