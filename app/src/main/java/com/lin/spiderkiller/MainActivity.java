/*Class name: MainActivity
 *Author: Li Lin & Shaomin Zhang
 *Date: 07/06/2014
 *Describe: this is the game activity that will run game logic.
 *
 */
package com.lin.spiderkiller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity 
{
	MainView v;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    	// Disable the title
    	requestWindowFeature (Window.FEATURE_NO_TITLE);
    	// Make full screen
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    	// Start the view
    	v = new MainView(this);
    	setContentView(v);
        
    }

	@Override
	protected void onPause () 
	{
		super.onPause();
		//v.pause();
		Asset sound = Asset.getInstance();
		sound.stopSong("bg");
		
		finish();
	}

	@Override
	protected void onResume () 
	{
		super.onResume();
		v.resume();
	}
}
