/*Class name: Spider
 *Author: Li Lin & Shaomin Zhang
 *Date: 07/06/2014
 *Describe:this class generates bug and display it on screen. It uses TimerTask to change
 *its running Z coordinate randomly. The class TimerTask counts time not very preciously.  
 *I think maybe its low priority in system causes the reaction slow.
 *
 * 
 */
package com.lin.spiderkiller;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.lin.spiderkiller.R;
import com.lin.spiderkiller.MainThread.EattingSpider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;



import android.content.Context;
import android.content.SharedPreferences;

public class Spider extends TimerTask
{

	enum SpiderState
	{
		Idle,				//when spider's state is idle, which means it is either dead or eating food.
						    //it will be delete by invoker and generate a new one.
		
		Alive,				//state that spider is crossing the screen;
		Dying,			    //state that has been killed by user. It displays on screen a period time.
		Eating				//state that access food
		};
	
	protected Context v;
	private int locateX;
	protected int locateY;
	protected Bitmap spiderdead;
	private Timer timer;
	protected int ScreenHigh;
	protected int ScreenWidth = 0;
	private long starttime;
	private final int animateCount = 2; 
	protected Bitmap[] spider ;
	private int displayIndex = 0;
	private double angle = 0;
	protected SpiderState state;
	float previousX = 0;
	float previousY = 0;
	protected long lastTime = 0;
	
	Spider(Context context)
	{
		this.v = context;
		loadBugResource();
        timer = new Timer();
        reset();
	}
    void reset(){
        displayIndex = 0;
        state = SpiderState.Alive;
        locateX = -1;
        locateY = spider[0].getHeight()/2;
        previousX = locateX;
        previousY = locateY;

        Random r = new Random();
        int Inter = r.nextInt(800 - 200) + 200;
        timer.scheduleAtFixedRate(this, 1000, Inter);
        lastTime = 0;
    }

	/*
	 * 
	 */
	public void loadBugResource()
	{
		spider = new Bitmap[]
			{
				BitmapFactory.decodeResource (v.getResources(), R.drawable.spider1_80),
				BitmapFactory.decodeResource (v.getResources(), R.drawable.spider2_80),
				BitmapFactory.decodeResource (v.getResources(), R.drawable.spider3_80),
			};
		spiderdead = BitmapFactory.decodeResource (v.getResources(), R.drawable.spiderdead_80);

	}
	

	/*Function:Display()
	 *Describe: Draw bug on screen based on the random coordinate.
	 *if state is dying, which means bug has been killed, it will
	 *last a couple of milliseconds to display dead picture on screen.
	 *Then its state becomes idle.
	 */
	public void Display(Canvas canvas)
	{
		ScreenHigh = canvas.getHeight();
		ScreenWidth = canvas.getWidth();

        if(locateX == -1){
            Random t = new Random();
            int randNum = t.nextInt((10 - 3) + 3) + 3;
            locateX = spider[displayIndex].getWidth() +  ScreenWidth/randNum;
            //Log.i("SpiderKiller","screen="+ScreenWidth+" rand="+randNum+" locatex="+locateX);
        }

		if(state == SpiderState.Alive)
		{
			Matrix matrix = new Matrix();
			matrix.setRotate((float) angle);
			matrix.postTranslate(locateX - spider[displayIndex].getWidth() / 2,
                    locateY - spider[displayIndex].getHeight() / 2);
			canvas.drawBitmap(spider[displayIndex], matrix, null);
			DoReachBottom();
            //Log.i("SpiderKiller","screen="+ScreenWidth+" x="+locateX+" y="+locateY);
			/*
			 * This timer uses to count every 600 millisecond to
			 * change spider picture to display animation
			 */
			if((System.nanoTime()-lastTime)/1000000 >= 600)
			{
				lastTime = System.nanoTime();
				displayIndex++;
				if(displayIndex>animateCount)
				{
					displayIndex = 0;
				}
			}
		}
		else if(state == SpiderState.Dying)
		{
			/*
			 * This timer performed an act that the dead spider
			 * will display on screen 1 second 
			 */
			if((System.nanoTime() - starttime)/1000000000 <= 1)
			{
	        	canvas.drawBitmap(spiderdead, locateX-spiderdead.getWidth()/2,
	        			locateY-spiderdead.getHeight()/2, null);	
			}
			else
			{
				state = SpiderState.Idle;
			}
		}	
	}

	/*Function:DoReachBottom()
	 * Describe:Check whether bug reaches food. If yes, its state 
	 * becomes eating.
	 */
	protected void DoReachBottom()
	{
		if(locateY >= (ScreenHigh - ScreenHigh/7))
		{
			state = SpiderState.Eating;
		}
	}
	
	public boolean isAlive()
	{
		boolean isAlive = false;
		if(state == SpiderState.Alive) 
			isAlive = true;
		
		return isAlive;
	}
	
	/*Function:IsHitSpider()
	 *Describe: check whether bug has been kill. If yes, return true, else
	 *return false.
	 */
	public boolean IsHitSpider(int x, int y)
	{
		if(state != SpiderState.Alive) return false;
		
		boolean IsHit = false;
        double radius = spider[displayIndex].getWidth()/2;
        double distance = Math.sqrt(Math.pow(x-locateX,2) + Math.pow(y-locateY,2));
       
		if(distance <= radius)
		{
			IsHit = true;
			starttime = System.nanoTime();
			state = SpiderState.Dying;
			timer.cancel();
			//timer = null;
		}
	
		//Log.i("SpiderKiller", "IsHitSpider:"+String.valueOf(IsHit) );
		return IsHit;
	}
	
	public void getEatingInfo(EattingSpider obj)
	{
		if(state == SpiderState.Eating)
		{
			obj.x = locateX;
			obj.y = locateY;
			obj.angle = angle;
			if(timer != null)
			{
				timer.cancel();
			}
			state = SpiderState.Idle;
		}
	}
	
	public void drawEattingSpider(Canvas canvas, EattingSpider obj)
	{
		Matrix matrix = new Matrix();
		matrix.setRotate((float)obj.angle);
		matrix.postTranslate(obj.x-spider[0].getWidth()/2, 
				obj.y-spider[0].getHeight()/2);
		canvas.drawBitmap(spider[0], matrix, null);
	}
	
	/*Function:run()
	 *Describe: very interval time, if bug state is alive,
	 *it change x and y coordinate randomly.
	 */
	@Override
    public void run() 
	{
		//timer callback
		Random r = new Random();
		//int times = r.nextInt(50 - 30) + 30;
        int times = 50;
		int eslapse = ScreenHigh/times;
		

		if(state == SpiderState.Alive)
		{	
			locateY += eslapse;
			if(locateY >= ScreenHigh)
			{
				//Log.i("SpiderKiller", "Display:hit the bottom" );
				locateY = spider[displayIndex].getHeight()/2;
			}
			

			double delta_x = locateX - previousX;
			double delta_y = locateY - previousY;
			angle = Math.atan2(delta_y, delta_x)*10;
			previousX = locateX;
			previousY = locateY;
		}
		
	}
}
