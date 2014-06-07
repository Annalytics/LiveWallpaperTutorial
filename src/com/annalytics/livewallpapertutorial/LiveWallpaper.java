package com.annalytics.livewallpapertutorial;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class LiveWallpaper extends WallpaperService
{
	private static final String LOG_TAG = LiveWallpaper.class.getSimpleName();
			
	public LiveWallpaper()
	{
	}
	
	@Override
	public Engine onCreateEngine()
	{
		return new WallpaperEngine();
	}

	public class WallpaperEngine extends Engine
	{
		private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
					Log.d(LOG_TAG, "User present");
					restart();
				}
			}
		};

		private final Handler mHandler = new Handler();
		public class DrawRunner implements Runnable {
			
			@Override
			public void run()
			{
				if(mRestart)
				{
					mRestart = false;
					restart();
				}
				else
				{
					draw();
				}
			}

			public void setRestart(boolean restart)
			{
				mRestart = restart;
			}
		};
		private final DrawRunner mDrawRunner = new DrawRunner();
		
		private boolean mVisible = true;
		private boolean mRunning = false;
		private boolean mRestart = false;
		private long mLastDrawTime = System.currentTimeMillis();
		private int mWidth, mHeight;
		private CanvasPlayer mPlayer;
		
		public WallpaperEngine()
		{
		}		
		
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			mPlayer = new CanvasPlayer(getBaseContext());
			setTouchEventsEnabled(true);
			super.onCreate(surfaceHolder);
		}
		
		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{	
				registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
				restart();
			}
			else
			{
				mRunning = false;
				mHandler.removeCallbacks(mDrawRunner);
				try
				{
					unregisterReceiver(mReceiver);
				}
				catch(IllegalArgumentException e)
				{
					// .. carry on
				}
			}
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);

			mVisible = false;
			mHandler.removeCallbacks(mDrawRunner);
		}
	
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			Log.d(LOG_TAG, "onSurfaceChanged: (" + width + "x" + height + ")");	
			mWidth = width;
			mHeight = height;
			restart();			
			super.onSurfaceChanged(holder, format, width, height);
		}
	
		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset)
		{			
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
		}
		
		@Override
		public void onTouchEvent(MotionEvent event)
		{
			super.onTouchEvent(event);
		}
		
		private void draw()
		{			
			if(mRunning)
			{				
				mHandler.removeCallbacks(mDrawRunner);			

				long drawtime = System.currentTimeMillis();
				long delta = drawtime - mLastDrawTime;
				mLastDrawTime = drawtime;
				long delay = 10;
				
				SurfaceHolder holder = getSurfaceHolder();					
				
				Canvas canvas = null;
				try
				{
					canvas = holder.lockCanvas();
					if (canvas != null)
					{		
						// main draw loop code will go here
						mPlayer.draw(canvas, mHeight, mWidth, delta);				
					}
				}
				finally
				{
					if (canvas != null)
					{
						holder.unlockCanvasAndPost(canvas);
					}
				}
	
				if (mVisible)
				{									
					mHandler.postDelayed(mDrawRunner, delay);		
				}
			}
		}
		
		private void restart()
		{
			Log.d(LOG_TAG, "Restarting");
			mRunning = true;
			mHandler.post(mDrawRunner);
			Log.d(LOG_TAG, "Restarted");
		}
	}
}
