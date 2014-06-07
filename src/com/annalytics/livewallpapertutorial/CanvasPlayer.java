package com.annalytics.livewallpapertutorial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.animation.LinearInterpolator;

public class CanvasPlayer
{
	private static final long ROTATE_TIME = 4000;
	
	private Context mContext;
	private float mAngle = 0;
	private LinearInterpolator mInter = new LinearInterpolator();
	private long mTime = 0;
	private Bitmap mBitmap;
	private int mBitmapWidth, mBitmapHeight;
	private int mWidth, mHeight;
	
	protected BitmapFactory.Options mOptions;
	
	public CanvasPlayer(Context c)
	{
		mContext = c;
		mOptions = new BitmapFactory.Options();
	}
	
	public void draw(Canvas canvas, int height, int width, long delta)
	{
		mWidth = width;
		mHeight = height;
		if(mBitmap == null)
		{
			mBitmap = getScaledImage(R.drawable.ic_launcher, true);
			mBitmapWidth = mBitmap.getWidth();
			mBitmapHeight = mBitmap.getHeight();
		}
		
		canvas.drawColor(Color.BLACK);
		
		mTime += delta;
		if(mTime > ROTATE_TIME)
		{
			mTime -= ROTATE_TIME;
		}
		float perc = mInter.getInterpolation(mTime/(float)ROTATE_TIME);
		mAngle = 360*perc;
		
		int squaresize = 100;

		Paint p = new Paint();
		p.setColor(Color.RED);
		p.setStyle(Style.FILL_AND_STROKE);
		
		Rect r = new Rect(0, 0, squaresize, squaresize);
		r.offsetTo(width/2 - squaresize/2, height/2 - squaresize/2);
				
		canvas.rotate(mAngle, width/2, height/2);
		canvas.drawRect(r, p);
		
		canvas.drawBitmap(mBitmap, width/2, height/2, null);
	}
	
	protected Bitmap getScaledImage(int drawable, boolean width)
	{
		mOptions.inTargetDensity = 0;
		mOptions.inScaled = false;
		mOptions.inSampleSize = 1;
		mOptions.inJustDecodeBounds = true;

		BitmapFactory.decodeResource(mContext.getResources(), drawable, mOptions);
		if (width)
		{
			if (mOptions.outWidth > mWidth)
			{
				mOptions.inSampleSize = (int) Math.round((float) mOptions.outWidth / (float) mWidth);
			}
		}
		else
		{
			if (mOptions.outHeight > mHeight)
			{
				mOptions.inSampleSize = (int) Math.round((float) mOptions.outHeight / (float) mHeight);
			}
		}

		return getBitmapUsingPreviousSettings(drawable);
	}
	
	protected Bitmap getBitmapUsingPreviousSettings(int drawable)
	{
		mOptions.inJustDecodeBounds = false;
		Bitmap b = BitmapFactory.decodeResource(mContext.getResources(), drawable, mOptions);
		return b;
	}
}
