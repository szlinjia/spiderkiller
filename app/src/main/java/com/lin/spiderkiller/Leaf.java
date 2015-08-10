/*Class name: Ladybug
 *Author: Li Lin & Shaomin Zhang
 *Date: 07/06/2014
 *Describe: The class extends from spider. It has the same behaviors
 *as spider except those skin picture. So the function loadBugResource()
 *is override. Another override is DoReachBottom(). When leaf reach bottom,
 *its state is idle instead of eating. 
 */

package com.lin.spiderkiller;

import com.lin.spiderkiller.R;
import com.lin.spiderkiller.Spider.SpiderState;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Leaf extends Spider 
{
	public long lastDisplayTime = 0;	//the last time it display
	public final int ESLAPSE = 4;		
	
	Leaf(Context context)
	{
		super(context);
	}
	
	@Override
	public void loadBugResource()
	{
		spider = new Bitmap[]
				{
					BitmapFactory.decodeResource (v.getResources(), R.drawable.lives1_80),
					BitmapFactory.decodeResource (v.getResources(), R.drawable.lives2_80),
					BitmapFactory.decodeResource (v.getResources(), R.drawable.lives2_80),
				};
		spiderdead = BitmapFactory.decodeResource (v.getResources(), R.drawable.greenheart);
	}
	
	@Override
	protected void DoReachBottom()
	{
		if(locateY >= (ScreenHigh + spider[0].getHeight()/2))
		{
			state = SpiderState.Idle;
		}
	}
}
