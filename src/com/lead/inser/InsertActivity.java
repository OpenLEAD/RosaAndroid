package com.lead.inser;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lead.rosa.R;

public class InsertActivity extends Activity implements SensorEventListener,
		MonitoringDisplay {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_inser, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Set new ip");
		alert.setMessage("IP address e.g. 192.168.1.75");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String ip = input.getText().toString();
		  mServiceStartIntent.removeExtra("url");
		  mServiceStartIntent.putExtra("url","http://"+ip+":9292/api/tasks/localhost/");
			stopService(mServiceStartIntent);
			startService(mServiceStartIntent);
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled.
		  }
		});

		alert.show();
		return super.onOptionsItemSelected(item);
	}

	private float theta = 0;
	private double pressure = 100000;
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
	private ImageView inductive_right;
	private ImageView still;
	private ImageView move_up;
	private ImageView move_down;
	private boolean inductive_rightvalue;
	private ImageView inductive_left;
	private boolean inductive_leftvalue;
	private FrameLayout display;
	private FrameLayout water;
	private RelativeLayout viga;
	private final float lvlmin = 2 * SensorManager.STANDARD_GRAVITY / 3;

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
		anim_appear = ObjectAnimator.ofFloat(null, "alpha", 1f).setDuration(
				1000);

		stoplog_move = new AnimatorSet();
		stoplog_move.play(anim_fade).with(anim_appear);

		submerge1 = ObjectAnimator.ofFloat(water, "ScaleY", 1)
				.setDuration(2000);
		submerge2 = ObjectAnimator.ofFloat(water, "TranslationY", 0)
				.setDuration(2000);

		submerge = new AnimatorSet();
		submerge.play(submerge1).with(submerge2);

		still = (ImageView) findViewById(R.id.still_depth);
		move_down = (ImageView) findViewById(R.id.move_down_depth);
		move_up = (ImageView) findViewById(R.id.move_up_depth);

		txt_level = (TextView) findViewById(R.id.textView2);

		regua_dir = (ImageView) findViewById(R.id.imageView10);

		viga = (RelativeLayout) findViewById(R.id.relativelayoutgarra);
		garra_esq = (ImageView) findViewById(R.id.imageView12);
		garra_dir = (ImageView) findViewById(R.id.imageView13);

		inductive_right = (ImageView) findViewById(R.id.Inductive_right);
		inductive_rightvalue = false;
		inductive_left = (ImageView) findViewById(R.id.Inductive_left);
		inductive_leftvalue = false;

		Monitorreceiver = new RockDataReceiver(this);
		IntentFilter mStatusIntentFilter = new IntentFilter(
				MonitoringDisplay.NEW_MONITOR_DATA);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				Monitorreceiver, mStatusIntentFilter);

		mServiceStartIntent = new Intent(this, BGJloop.class);
		mServiceStartIntent.putExtra("url",
				"http://192.168.1.75:9292/api/tasks/localhost/");
		mServiceStartIntent.putExtra(INDUCTIVE_LEFT,
				"bus1/ports/inductive_left/read.json");
		mServiceStartIntent.putExtra(INDUCTIVE_RIGHT,
				"bus1/ports/inductive_right/read.json");
		mServiceStartIntent.putExtra(INDUCTIVE_KEY,
				"bus1/ports/inductive_key/read.json");
		mServiceStartIntent.putExtra(INCLINATION_BODY,
				"inclination_body/ports/angle/read.json");
		mServiceStartIntent.putExtra(INCLINATION_RIGHT,
				"inclination_right/ports/angle/read.json");
		mServiceStartIntent.putExtra(INCLINATION_KEY,
				"inclination_key/ports/angle/read.json");
		mServiceStartIntent.putExtra(PRESSURE,
				"pressure/ports/pressure_samples/read.json");

		viga.post(new Runnable() {

			@Override
			public void run() {
				viga.setPivotX(viga.getMeasuredWidth() / 2);
				viga.setPivotY(0);
			}

		});

		garra_esq.post(new Runnable() {

			@Override
			public void run() {
				garra_esq.setPivotX((int) (garra_esq.getMeasuredWidth() * 0.357));
				garra_esq.setPivotY((int) (garra_esq.getMeasuredHeight() * 0.175));
			}

		});

		garra_dir.post(new Runnable() {

			@Override
			public void run() {
				garra_dir.setPivotX((int) (garra_dir.getMeasuredWidth() * 0.643));
				garra_dir.setPivotY((int) (garra_dir.getMeasuredHeight() * 0.175));
			}

		});

		Smg = (SensorManager) getSystemService(SENSOR_SERVICE);

		gravity = Smg.getDefaultSensor(Sensor.TYPE_GRAVITY);

	}

	@SuppressWarnings("unused")
	private void initAnimation() {
		// R.drawable.tile1 is PNG
		Bitmap b = BitmapFactory.decodeResource(getResources(),
				R.drawable.regua_unity_dir);
		AnimationDrawable shiftedAnimation = getAnimation(b);

		// R.id.img_3 is ImageView in my application
		View v = findViewById(R.id.imageView10);
		v.setBackground(shiftedAnimation);
		shiftedAnimation.start();
	}

	private Bitmap getShiftedBitmap(Bitmap bitmap, int shiftY) {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Canvas newBitmapCanvas = new Canvas(newBitmap);

		Rect srcRect1 = new Rect(0, shiftY, bitmap.getWidth(),
				bitmap.getHeight());
		Rect destRect1 = new Rect(srcRect1);
		destRect1.offset(0, -shiftY);
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

		for (int i = 0; i < fragments; ++i) {
			shiftedBitmaps.add(getShiftedBitmap(bitmap, shiftLength * i));
		}

		return shiftedBitmaps;
	}

	private AnimationDrawable getAnimation(Bitmap bitmap) {
		AnimationDrawable animation = new AnimationDrawable();
		animation.setOneShot(false);

		List<Bitmap> shiftedBitmaps = getShiftedBitmaps(bitmap);
		int duration = 50;

		for (Bitmap image : shiftedBitmaps) {
			BitmapDrawable navigationBackground = new BitmapDrawable(
					getResources(), image);
			navigationBackground.setTileModeX(TileMode.REPEAT);

			animation.addFrame(navigationBackground, duration);
		}
		return animation;
	}

	@Override
	protected void onPause() {
		super.onPause();
		//Intent mServiceIntent = new Intent(this, BGJloop.class);
		stopService(mServiceStartIntent);
		// Smg.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Smg.registerListener(this, gravity,SensorManager.SENSOR_DELAY_GAME);
		startService(mServiceStartIntent);
	}

	public void rot_garra_esq(View view) {
		theta += 5;
		garra_esq.setRotation(-theta);
		garra_dir.setRotation(theta);

	}

	public void rot_garra_dir(View view) {
		theta -= 5;
		garra_esq.setRotation(-theta);
		garra_dir.setRotation(theta);

		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),
				BitmapFactory.decodeResource(getResources(),
						R.drawable.regua_unity_dir));
		bitmapDrawable.setTileModeY(TileMode.REPEAT);

		Matrix roll = new Matrix();
		roll.postTranslate(0, theta);
		regua_dir.setScaleType(ScaleType.MATRIX);
		regua_dir.setScaleType(ScaleType.FIT_XY);
		regua_dir.setImageMatrix(roll);
		regua_dir.setImageDrawable(bitmapDrawable);

	}

	public void change_induc(View view) {

		if (stoplog_move.isRunning())
			stoplog_move.cancel();

		if (view.getTag().equals("grey")) {
			// anim_fade.getValues()[0]

			((ImageView) view).setImageResource(R.drawable.contato_verde);
			view.setTag("green");
			if (inductive_left.getTag().equals("green")
					|| inductive_right.getTag().equals("green")) {
				anim_fade.setTarget(stoplog_still);
				anim_appear.setTarget(stoplog_moving);

				anim_fade.setFloatValues(0);
				anim_appear.setFloatValues(1);
			}
		} else {

			view.setTag("grey");
			((ImageView) view).setImageResource(R.drawable.contato_cinza);
			if (inductive_left.getTag().equals("grey")
					&& inductive_right.getTag().equals("grey")) {
				anim_fade.setTarget(stoplog_moving);
				anim_appear.setTarget(stoplog_still);

				anim_fade.setFloatValues(0);
				anim_appear.setFloatValues(0.3f);
			}
		}
		stoplog_move.start();

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	public void submerge(View view) {

		if (submerge.isRunning())
			submerge.cancel();

		if (view.getTag().equals("full")) {
			view.setTag("empty");

			submerge1.setFloatValues(1);
			submerge2.setFloatValues(0);

		} else {

			view.setTag("full");
			submerge1.setFloatValues(display.getMeasuredHeight() / 2);
			submerge2.setFloatValues(-display.getMeasuredHeight() / 2);
		}

		submerge.start();

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {

		case Sensor.TYPE_GRAVITY:
			gdir = event.values.clone();
			break;

		default:
			return;
		}
		if (gdir[2] > lvlmin)
			return;

		float theta = (float) Math.toDegrees(Math.atan2(gdir[0], gdir[1]));

		float abs_theta = Math.abs(theta);

		txt_level.setText(String.valueOf((int) abs_theta) + "°");

		if (abs_theta > 10 && level.getTag().equals("green")) {
			// level.setImageResource(R.drawable.nivel_vermelho);
			level.setBackgroundColor(0xFFFF4444);
			txt_level.setTextColor(0xFFFF4444);
			level.setTag("red");
		}

		if (abs_theta < 10 && level.getTag().equals("red")) {
			// level.setImageResource(R.drawable.nivel_verde);
			level.setBackgroundColor(0xFF41E020);
			txt_level.setTextColor(0xFF41E020);
			level.setTag("green");
		}

		level.setRotation(theta);

		viga.setRotation(theta);

	}

	@Override
	public void inductive_right(boolean value) {
		if (inductive_rightvalue ^ value) {
			inductive_rightvalue = value;
			change_induc(inductive_right);
		}

	}

	@Override
	public void inclination_body(double value) {

		float inc = (float) Math.toDegrees(value);

		level.setRotation(inc);

		viga.setRotation(inc);

		inc = Math.abs(inc);

		txt_level.setText(String.valueOf((int) inc) + "°");

		if (inc > 10 && level.getTag().equals("green")) {
			// level.setImageResource(R.drawable.nivel_vermelho);
			level.setBackgroundColor(0xFFFF4444);
			txt_level.setTextColor(0xFFFF4444);
			level.setTag("red");
		}

		if (inc < 10 && level.getTag().equals("red")) {
			// level.setImageResource(R.drawable.nivel_verde);
			level.setBackgroundColor(0xFF41E020);
			txt_level.setTextColor(0xFF41E020);
			level.setTag("green");
		}

	}

	@Override
	public void inductive_left(boolean value) {
		if (inductive_leftvalue ^ value) {
			inductive_leftvalue = value;
			change_induc(inductive_left);
		}

	}

	@Override
	public void inductive_key(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void inclination_right(double value) {

		float inc = (float) Math.toDegrees(value);
		garra_esq.setRotation(-inc);
		garra_dir.setRotation(inc);

	}

	@Override
	public void inclination_key(double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressure(double value) {
		if (value == pressure) {
			if(still.getVisibility() == View.INVISIBLE){
				still.setVisibility(View.VISIBLE);
				move_up.setVisibility(View.INVISIBLE);
				move_down.setVisibility(View.INVISIBLE);
			}
		} else {
			if (value > pressure) {
				if(move_down.getVisibility() == View.INVISIBLE){
					still.setVisibility(View.INVISIBLE);
					move_up.setVisibility(View.INVISIBLE);
					move_down.setVisibility(View.VISIBLE);
				}

				if (pressure < 100200 && value >= 100200)
					submerge(water);
			} else {
				if(move_up.getVisibility() == View.INVISIBLE){
					still.setVisibility(View.INVISIBLE);
					move_up.setVisibility(View.VISIBLE);
					move_down.setVisibility(View.INVISIBLE);
				}

				if (pressure > 100100 && value <= 100100)
					submerge(water);
			}
		}

		pressure = value;

	}
}
