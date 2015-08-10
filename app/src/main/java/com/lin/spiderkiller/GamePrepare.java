/*Class name: Ladybug
 *Author: Li Lin & Shaomin Zhang
 *Date: 07/06/2014
 *Describe:This class only used for display "Ready go" sound and game over picture.
 */
package com.lin.spiderkiller;

import java.util.Timer;
import java.util.TimerTask;

import com.lin.spiderkiller.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

public class GamePrepare 
{
	private int elaspse = 1;
	private Bitmap[] pic = new Bitmap[3];
	private Timer timer;
	public long previous = 0;
	private Asset sound;
	private Bitmap gameOver;
	SharedPreferences settings;
	
	
	GamePrepare(Context v)
	{
		pic[0] = BitmapFactory.decodeResource (v.getResources(), R.drawable.start1_104);
		pic[1] = BitmapFactory.decodeResource (v.getResources(), R.drawable.start2_104);
		pic[2] = BitmapFactory.decodeResource (v.getResources(), R.drawable.start3_104);
		
		gameOver = BitmapFactory.decodeResource (v.getResources(), R.drawable.gameover);
		
		sound = Asset.getInstance();
		
	}

	/*
	 * Function:display()
	 * Describe: This function control  the count down "3,2,1" 
	 * and then game starts.
	 */
	public boolean display(Canvas canvas)
	{
		int locateX = canvas.getWidth()/2 - pic[0].getWidth()/2;
		int locateY = canvas.getHeight()/2 - pic[0].getHeight()/2;
		
		//
		if(previous == 0)
		{
			previous = System.nanoTime() ;
		}
		
		if((System.nanoTime() - previous)/1000000 > 2400)
		{
			previous = 0;
			return false;
		}
		else if((System.nanoTime() - previous)/1000000  > 1800)	
		{
			canvas.drawBitmap(pic[0], locateX, locateY, null);
			sound.playSong("bg");
		}
		else if((System.nanoTime() - previous)/1000000  > 1200)
		{
			canvas.drawBitmap(pic[1], locateX, locateY, null);
		}
		else if((System.nanoTime() - previous)/1000000  > 600)
		{
			canvas.drawBitmap(pic[2], locateX, locateY, null);
		}
	
		return true;
	}
	
	/*
	 *Function: displayGameOver()
	 *Describe: Display game over picture and play sound.
	 *Also, it displays the user current score and compares
	 *to the highest score in history.
	 */
	public boolean displayGameOver(Canvas canvas, int score)
	{
		int locateX = canvas.getWidth()/2;
		int locateY = canvas.getHeight()/2;
		if(previous == 0)
		{
			previous = System.nanoTime() ;
			sound.stopSong("bg");
			sound.playsound("gameover");	
		}

		if((System.nanoTime() - previous)/1000000000  > 3)
		{
			return false;
			
		}
		else
		{
			Rect src = new Rect(), dst = new Rect();
	        src.left = 0;
	        src.top = 0;
	        src.bottom = gameOver.getHeight()-1;
	        src.right = gameOver.getWidth()-1;
	        dst.left = 0;
	        dst.top = 0;
	        dst.right = canvas.getWidth()-1;
	        dst.bottom = canvas.getHeight()-1;
			canvas.drawBitmap(gameOver, src, dst, null);
			
			//draw score:
			String text = "Your score: " + String.valueOf(score);

			Paint paint = new Paint();
			paint.setARGB(255, 50, 177, 108);
			paint.setTextSize(35.0f);
			float textLen = paint.measureText(text);
			int textY = 2*canvas.getHeight()/3;
			int textX = 10;
			canvas.drawText(text, textX, textY, paint);
			
			if(score >= sound.getscore() && score != 0)
			{
				textY += 45;
				text = "Congretulation! You Got New High Score!";
				canvas.drawText(text, textX, textY, paint);
			}
		}
	
		return true;
	}

}
