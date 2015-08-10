
package com.lin.spiderkiller;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView 
{
	private SurfaceHolder holder = null;
	int x, y;
	private MainThread t = null;
	Context context;
	
	// Constructor 
	public MainView (Context context) 
	{
		super(context);
		x = y = 0;
		holder = getHolder();
		this.context = context;
	}
	
	
	public void pause ()
	{
		t.setRunning(false);
		while (true) 
		{
			try 
			{
				t.join();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			break;
		}
		t = null;
	}
	
	public void resume () 
	{
		t = new MainThread (holder, context);
		t.setRunning(true);
		t.start();
		setFocusable(true); // make sure we get events
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		float x, y;
		int action = event.getAction();
		x = event.getX();
		y = event.getY();
		if (action == MotionEvent.ACTION_UP) 
		{
			t.setXY ((int)x, (int)y);
		}
		
		return true; 
	}
}