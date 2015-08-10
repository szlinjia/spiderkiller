/*Class name: MainThread
 *Author: Li Lin & Shaomin Zhang
 *Date: 07/06/2014
 *Describe: Toooooooo tired to write detail. I will optimize the drawing algorithm later
 *after Summer1. The spiders run not very smoothly. And there are some small bugs in 
 *program when it runs a long time. 
 *
 */

package com.lin.spiderkiller;

import java.util.ArrayList;
import java.util.List;

import com.lin.spiderkiller.R;
import com.lin.spiderkiller.Spider.SpiderState;

import android.R.color;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread 
{
	class EattingSpider
	{
		float x;
		float y;
		double angle;
	}
	
	enum GameState
	{
		Start,
		Running,
		Over
	}
	private final int MAX_LIVES = 3;
	private SurfaceHolder holder;
	private boolean isRunning = false;
	private int x, y;
	private static final Object lock = new Object(); 
    private Bitmap background,greenlive,bmpTouch,gameover,titlebar;
    private Typeface myTypeface;
    private Spider[] DisplaySpider;
    private Ladybug ladybug;
    final static int MaxSpider = 8;
	private Asset sound;
	private int generateCount;
	private long lastTime;
	private int curLives = 3;
	private List<EattingSpider> lstEattingSpider = new ArrayList<EattingSpider>();
	private Context context;
	private Leaf supplylives;
	private int score = 0;
	private GameState gamestate = GameState.Start;
	private GamePrepare startGame;
    
	public MainThread (SurfaceHolder surfaceHolder, Context context) 
	{
	   this.context = context;
	   holder = surfaceHolder;
	   x = y = 0;
	   
	   // Load the image
	   background = BitmapFactory.decodeResource (context.getResources(), R.drawable.wood);
	   greenlive = BitmapFactory.decodeResource(context.getResources(), R.drawable.live);
	   bmpTouch = BitmapFactory.decodeResource(context.getResources(), R.drawable.touch);
	   gameover = BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover);
	   titlebar = BitmapFactory.decodeResource(context.getResources(), R.drawable.titlebar_200x30);
	   myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/myfont.ttf");
	   
	   sound = Asset.getInstance();

	   //create spider
	   DisplaySpider = new Spider[MaxSpider];

	   for(int i=0;i<MaxSpider;i++)
	   {
		   DisplaySpider[i] = new Spider(context);
	   }
	   
	   ladybug = new Ladybug(context);
	   supplylives = new Leaf(context);
	   
	   generateCount = 0;
	   lastTime = 0;
	   curLives = MAX_LIVES;
	   
	   startGame = new GamePrepare(context);
		sound.playsound("go");
	}

	public void setRunning(boolean b) 
	{
		isRunning = b;
	}
	
	public void setXY (int x, int y) 
	{
		synchronized (lock) 
		{
			//click spider
			boolean isHit = false;
			for(int i=0;i<DisplaySpider.length;i++)
			{
				if(DisplaySpider[i] != null)
				{
					isHit |= DisplaySpider[i].IsHitSpider(x, y);
				}
			}
			
			if(!isHit)
			{
				sound.playsound("squish");
			}
			else
			{
				sound.playsound("hit");
				score++;
			}
			
			//click lady bug
			if(ladybug != null && ladybug.IsHitSpider(x, y))
			{
				sound.playsound("bomb");
			}
			
			//click leaf
			if(supplylives != null && supplylives.IsHitSpider(x, y))
			{
				sound.playsound("cheers");
				curLives++;
				if(curLives >MAX_LIVES)
				{
					curLives = MAX_LIVES;
				}
			}
		}
	}
	
	@Override
	public void run() 
	{
		while (isRunning) 
		{
			// Lock the canvas before drawing
			Canvas canvas = holder.lockCanvas();	
			if (canvas != null) 
			{
				if(gamestate == GameState.Start)
				{
					if(!startGame.display(canvas))
					{
						gamestate = GameState.Running;
					}
					
				}
				else if(gamestate == GameState.Over)
				{
					if(!startGame.displayGameOver(canvas, score))
					{
						isRunning = false;
					}
				}
				else
				{
					// Perform drawing operations on the canvas
					render(canvas);		
				}
				// After drawing, unlock the canvas and display it
				holder.unlockCanvasAndPost (canvas);	
			}
		}
		
		//terminate current activity
		int prescore = sound.getscore();
		if(score > prescore)
		{
			sound.setscore(score);
		}
		sound.stopSong("bg");
		((MainActivity) context).finish();
	}
	
	private void render (Canvas canvas) 
	{
		//check game over condition: lady bug has been hit or lives are less than 0
		if(curLives <= 0 || ladybug.state == SpiderState.Idle)
		{
			gamestate = GameState.Over;
		}
		
		
		//draw background
		Rect src = new Rect(), dst = new Rect();
        src.left = 0;
        src.top = 0;
        src.bottom = background.getHeight()-1;
        src.right = background.getWidth()-1;
        dst.left = 0;
        dst.top = 0;
        dst.right = canvas.getWidth()-1;
        dst.bottom = canvas.getHeight()-1;
        canvas.drawBitmap(background, src, dst, null);
		
        //draw title bar
		Rect title = new Rect();
		src.left = 0;
		src.top = 0;
		src.right = titlebar.getWidth();
		src.bottom = titlebar.getHeight();
		title.left = 1;
		title.top = 1;
		title.right = canvas.getWidth()-1;
		title.bottom = canvas.getHeight()/15;
		canvas.drawBitmap(titlebar, src, title,null);
		
		//draw text
		Paint paint = new Paint();
		paint.setARGB(255, 255, 255, 0);
		paint.setTypeface(myTypeface);
		paint.setTextSize(35.0f);
		int textY = (title.bottom - title.top)/2+ 35/2;
		int textX = title.left + 10;
		canvas.drawText(String.valueOf(score), textX, textY, paint);

        /*this piece of code makes sure that not all the 
         * spider drop down at the same time.It displays
         * from up screen one by one.
		 * Sometimes the random X coordinates are almost the same.*/
        long lCurrenttime = System.nanoTime() ;
        if((lCurrenttime - lastTime)/1000000000 >= 2 &&
        		generateCount < DisplaySpider.length)
        {
        	generateCount++;
        	lastTime = lCurrenttime;
        }
        else if(generateCount == DisplaySpider.length)
        {
        	generateCount = 1;
        }
        	
        
        //draw alive spider
        for(int i=0;i<generateCount;i++)
        {

        	if(DisplaySpider[i].state == SpiderState.Eating)
        	{
            	/*When spider reach bottom,which state is eating,
            	 * the object will be delete. But its coordinate
            	 * will be store in a list and then display an
            	 * eating spider at the food picture. 
            	 */
        		EattingSpider obj = new EattingSpider();
        		DisplaySpider[i].getEatingInfo(obj);
        		lstEattingSpider.add(obj);
        		
        		//if too much eating spiders were draw, that will be not cool.
        		if(lstEattingSpider.size() > 6)
        		{
        			lstEattingSpider.remove(0);
        		}
        		
        		synchronized (lock) 
        		{
	        		if(curLives > 0)
	        		{
	        			curLives--;
	        		}
        		}

        	}
        	if(DisplaySpider[i].state == SpiderState.Idle) {
				//if spider's state is idle, it will be delete and
				//generate a new one.
				Spider idle = DisplaySpider[i];
				idle = null;
				DisplaySpider[i] = new Spider(context);
			}
        	//if(DisplaySpider[i] != null)
			else
        	{
        		DisplaySpider[i].Display(canvas);
        	}
        }
        
        
        for (EattingSpider obj:lstEattingSpider)
        {
        	DisplaySpider[0].drawEattingSpider(canvas, obj);
        }
        
        //every 8 seconds display one lady bug
        if((System.nanoTime() - ladybug.lastDisplayTime)/1000000000 >= ladybug.ESLAPSE )
        {
        	ladybug.Display(canvas);

        	//reach bottom
        	if(ladybug.state == SpiderState.Idle)
        	{
        		gamestate = GameState.Over;
        		ladybug = null;
        		ladybug = new Ladybug(context);
        		ladybug.lastDisplayTime = System.nanoTime();
        	}
        }
        
        //every 5 seconds display supply lives
        if(curLives < 3 &&
        		(System.nanoTime() - supplylives.lastDisplayTime)/1000000000 >= supplylives.ESLAPSE )
        {
        	supplylives.Display(canvas);

        	//reach bottom
        	if(supplylives.state == SpiderState.Idle)
        	{
        		supplylives = null;
        		supplylives = new Leaf(context);
        		supplylives.lastDisplayTime = System.nanoTime();
        	}
        }
        
		//draw lives
        src.left = 0;
        src.top = 0;
        src.bottom = greenlive.getHeight()-1;
        src.right = greenlive.getWidth()-1;
        dst.top = title.bottom/2 - greenlive.getHeight()/2 ;
        dst.bottom = greenlive.getHeight()-1;
        for(int i=0; i < curLives; i++)
        {
        	dst.left = title.right - greenlive.getWidth() * (i+1)-2;
        	dst.right = dst.left + greenlive.getWidth()-1;
        	canvas.drawBitmap(greenlive, src, dst, null);
        }
        
	}
	
}
