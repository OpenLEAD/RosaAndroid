package com.lead.inser;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lead.rosa.R;

public class InsertActivity extends Activity implements SensorEventListener, MonitoringDisplay {

	private float theta=0;
	private SensorManager Smg;
	@SuppressWarnings("unused")
	private Sensor gravity;
	private float[] gdir;
	private View level;
	private TextView txt_level;
	private ImageView garra_dir;
	private ImageView stoplog_still;
	private ObjectAnimator anim_fade;
	private ObjectAnimator anim_appear;
	private AnimatorSet stoplog_move;
	private ObjectAnimator submerge1;
	private ObjectAnimator submerge2;
	private AnimatorSet submerge;
	private ImageView stoplog_moving;
	private ImageView regua_dir;
	private ImageView garra_esq;
	private ImageView Inductive2;
	private boolean Inductive2value;
	private ImageView Inductive1;
	private boolean Inductive1value;
	private FrameLayout display;
	private FrameLayout water;
	private RelativeLayout viga;
	private final float lvlmin = 2*SensorManager.STANDARD_GRAVITY/3;
	
	private RockDataReceiver Monitorreceiver;
	private Intent mServiceStartIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inser);

		level = findViewById(R.id.View3);
		
		
		display = (FrameLayout) findViewById(R.id.animationFrame);
		water = (FrameLayout) findViewById(R.id.water);
		
		stoplog_still = (ImageView) findViewById(R.id.imageView14);
		stoplog_moving = (ImageView) findViewById(R.id.ImageView02);

		anim_fade = ObjectAnimator.ofFloat(null, "alpha", 0f).setDuration(1000);
		anim_appear = ObjectAnimator.ofFloat(null, "alpha", 1f).setDuration(1000);

		stoplog_move = new AnimatorSet();
		stoplog_move.play(anim_fade).with(anim_appear);

		
		submerge1 = ObjectAnimator.ofFloat(water, "ScaleY",1).setDuration(2000);
		submerge2 = ObjectAnimator.ofFloat(water, "TranslationY",0).setDuration(2000);
		
		submerge = new AnimatorSet();
		submerge.play(submerge1).with(submerge2);
		
		
		txt_level = (TextView) findViewById(R.id.textView2);
		
		regua_dir = (ImageView) findViewById(R.id.imageView10);
		
		viga = (RelativeLayout) findViewById(R.id.relativelayoutgarra);
		garra_esq = (ImageView) findViewById(R.id.imageView12);
		garra_dir = (ImageView) findViewById(R.id.imageView13);
		
		
		Inductive2 = (ImageView) findViewById(R.id.Inductive2);
		Inductive2value = false;		
		Inductive1 = (ImageView) findViewById(R.id.Inductive1);
		Inductive1value = false;		
		
		
		
		Monitorreceiver = new RockDataReceiver(this);
		IntentFilter mStatusIntentFilter = new IntentFilter(MonitoringDisplay.NEW_MONITOR_DATA);
		LocalBroadcastManager.getInstance(this).registerReceiver(Monitorreceiver, mStatusIntentFilter);

		
		mServiceStartIntent = new Intent(this, BGJloop.class);
		mServiceStartIntent.putExtra("url","http://192.168.1.75:9292/api/tasks/localhost/");
		mServiceStartIntent.putExtra(INDUCTIVE1,"bus1/ports/inductive1/read.json");
		mServiceStartIntent.putExtra(INDUCTIVE2,"bus1/ports/inductive2/read.json");
		mServiceStartIntent.putExtra(INCLINATION,"inclination/ports/angle/read.json");
		
		
		
		viga.post(new Runnable(){

			@Override
			public void run() {
				viga.setPivotX(viga.getMeasuredWidth()/2);
				viga.setPivotY(0);
			}
			
		});
		
		garra_esq.post(new Runnable(){

			@Override
			public void run() {
				garra_esq.setPivotX((int) (garra_esq.getMeasuredWidth()*0.357));
				garra_esq.setPivotY((int) (garra_esq.getMeasuredHeight()*0.175));
			}
			
		});
		
		garra_dir.post(new Runnable(){

			@Override
			public void run() {
				garra_dir.setPivotX((int) (garra_dir.getMeasuredWidth()*0.643));
				garra_dir.setPivotY((int) (garra_dir.getMeasuredHeight()*0.175));
			}
			
		});
		
		Smg = (SensorManager) getSystemService(SENSOR_SERVICE);

		gravity = Smg.getDefaultSensor(Sensor.TYPE_GRAVITY);
		
	}
	
	
	
	@SuppressWarnings("unused")
	private void initAnimation() {
//      R.drawable.tile1 is PNG
        Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.regua_unity_dir);
        AnimationDrawable shiftedAnimation = getAnimation(b);

//      R.id.img_3 is ImageView in my application
        View v = findViewById(R.id.imageView10);
        v.setBackground(shiftedAnimation);
        shiftedAnimation.start();
}

private Bitmap getShiftedBitmap(Bitmap bitmap, int shiftY) {
    Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    Canvas newBitmapCanvas = new Canvas(newBitmap);

    Rect srcRect1 = new Rect(0, shiftY, bitmap.getWidth(), bitmap.getHeight());
    Rect destRect1 = new Rect(srcRect1);
    destRect1.offset(0,-shiftY);
    newBitmapCanvas.drawBitmap(bitmap, srcRect1, destRect1, null);

    Rect srcRect2 = new Rect(0, 0, bitmap.getWidth(), shiftY);
    Rect destRect2 = new Rect(srcRect2);
    destRect2.offset(0, bitmap.getHeight() - shiftY);
    newBitmapCanvas.drawBitmap(bitmap, srcRect2, destRect2, null);

    return newBitmap;
}

private List<Bitmap> getShiftedBitmaps(Bitmap bitmap) {
    List<Bitmap> shiftedBitmaps = new ArrayList<Bitmap>();
    int fragments = 10;
    int shiftLength = bitmap.getWidth() / fragments;

    for(int i = 0 ; i < fragments; ++i){
        shiftedBitmaps.add( getShiftedBitmap(bitmap,shiftLength * i));
    }

    return shiftedBitmaps;
}

private AnimationDrawable getAnimation(Bitmap bitmap) {
    AnimationDrawable animation = new AnimationDrawable();
    animation.setOneShot(false);

    List<Bitmap> shiftedBitmaps = getShiftedBitmaps(bitmap);
    int duration = 50;

    for(Bitmap image: shiftedBitmaps){
        BitmapDrawable navigationBackground = new BitmapDrawable(getResources(), image);
        navigationBackground.setTileModeX(TileMode.REPEAT);

        animation.addFrame(navigationBackground, duration);
    }
    return animation;
}


	@Override
	protected void onPause() {
		super.onPause();
		Intent mServiceIntent = new Intent(this, BGJloop.class);
		stopService(mServiceIntent);
		//Smg.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Smg.registerListener(this, gravity,SensorManager.SENSOR_DELAY_GAME);
		startService(mServiceStartIntent);
	}

	public void rot_garra_esq(View view){
		theta+=5;
		garra_esq.setRotation(-theta);
		garra_dir.setRotation(theta);
		
	}

	public void rot_garra_dir(View view){
		theta-=5;
		garra_esq.setRotation(-theta);
		garra_dir.setRotation(theta);
		
		BitmapDrawable bitmapDrawable  = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.regua_unity_dir));
		bitmapDrawable.setTileModeY(TileMode.REPEAT);
		
		Matrix roll = new Matrix();
		roll.postTranslate(0, theta);
		regua_dir.setScaleType(ScaleType.MATRIX);
		regua_dir.setScaleType(ScaleType.FIT_XY);
		regua_dir.setImageMatrix(roll);
		regua_dir.setImageDrawable(bitmapDrawable);
		
	}
	
	
	public void change_induc(View view){

		if(stoplog_move.isRunning())
			stoplog_move.cancel();
		
		if(view.getTag().equals("grey"))
		{
			anim_fade.setTarget(stoplog_still);
			anim_appear.setTarget(stoplog_moving);
			//anim_fade.getValues()[0]
			
			((ImageView) view).setImageResource(R.drawable.contato_verde);
			view.setTag("green");
			}
		else
		{
			anim_fade.setTarget(stoplog_moving);
			anim_appear.setTarget(stoplog_still);
			
			view.setTag("grey");
			((ImageView) view).setImageResource(R.drawable.contato_cinza);
			}

		anim_fade.setFloatValues(0);
		anim_appear.setFloatValues(1);
		stoplog_move.start();
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	public void submerge(View view){
		
		if(submerge.isRunning())
			submerge.cancel();
		
		if(view.getTag().equals("full")){
			view.setTag("empty");
			
			submerge1.setFloatValues(1);
			submerge2.setFloatValues(0);
			
		}
		else{
			
			view.setTag("full");
			submerge1.setFloatValues(display.getMeasuredHeight()/2);
			submerge2.setFloatValues(-display.getMeasuredHeight()/2);
		}
		
		submerge.start();
		
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {

		case Sensor.TYPE_GRAVITY:
			gdir = (float[]) event.values.clone();
			break;

		default:
			return;
		}
		if(gdir[2] > lvlmin)
			return;
		
		float theta = (float)Math.toDegrees(Math.atan2((double)gdir[0],(double)gdir[1]));
		
		float abs_theta = Math.abs(theta);

		txt_level.setText(String.valueOf((int) abs_theta)+"°");
		
		if(abs_theta>10 && level.getTag().equals("green"))
		{
			//level.setImageResource(R.drawable.nivel_vermelho);
			level.setBackgroundColor(0xFFFF4444);
			txt_level.setTextColor(0xFFFF4444);
			level.setTag("red");
			}
		
		if(abs_theta<10 && level.getTag().equals("red"))
		{
			//level.setImageResource(R.drawable.nivel_verde);
			level.setBackgroundColor(0xFF41E020);
			txt_level.setTextColor(0xFF41E020);
			level.setTag("green");
			}

		
		level.setRotation(theta);

		viga.setRotation(theta);
		
	}



	@Override
	public void inductive2(boolean value) {
		if (Inductive2value ^ value){
			Inductive2value = value;
			change_induc(Inductive2);
		}
		
	}



	@Override
	public void inclination(double value) {
		
		
		
		float inc = (float) Math.toDegrees(value);

				
		level.setRotation(inc);
	
		viga.setRotation(inc);
		
		inc = Math.abs(inc);

		txt_level.setText(String.valueOf((int) inc)+"°");
		
		if(inc>10 && level.getTag().equals("green"))
		{
			//level.setImageResource(R.drawable.nivel_vermelho);
			level.setBackgroundColor(0xFFFF4444);
			txt_level.setTextColor(0xFFFF4444);
			level.setTag("red");
			}
		
		if(inc<10 && level.getTag().equals("red"))
		{
			//level.setImageResource(R.drawable.nivel_verde);
			level.setBackgroundColor(0xFF41E020);
			txt_level.setTextColor(0xFF41E020);
			level.setTag("green");
			}
		
	}



	@Override
	public void inductive1(boolean value) {
		if (Inductive1value ^ value){
			Inductive1value = value;
			change_induc(Inductive1);
		}
		
	}
}
