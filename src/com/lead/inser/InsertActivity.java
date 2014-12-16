package com.lead.inser;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
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
import android.view.ViewGroup.MarginLayoutParams;
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

		switch (item.getItemId()) {

		case R.id.action_zero:
			AlertDialog.Builder alert_zero = new AlertDialog.Builder(this);

			alert_zero.setTitle("Configurar tara");
			alert_zero.setMessage("Usar os valores atuais como posicao zero?");

			alert_zero.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							key_offset += key.getRotation();
							claw_offset += claw_right.getRotation();
							liftbeam_offset += liftbeam.getRotation();
							pressure_offset = pressure;
							if (still.getVisibility() == View.INVISIBLE) {
								submerge(water);
								still.setVisibility(View.VISIBLE);
								pressure_value.setVisibility(View.INVISIBLE);
							}
						}
					});

			alert_zero.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert_zero.show();

			break;

		case R.id.action_settings:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Set new ip");
			alert.setMessage("IP address e.g. 192.168.1.75");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String ip = input.getText().toString();
							mServiceStartIntent.removeExtra("url");
							mServiceStartIntent.putExtra("url", "http://" + ip
									+ ":9292/api/tasks/localhost/");
							stopService(mServiceStartIntent);
							startService(mServiceStartIntent);
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static final double STD_PRESSURE = 100000;
	private float theta;
	private double pressure;
	private double pressure_offset;
	private SensorManager Smg;
	@SuppressWarnings("unused")
	private Sensor gravity;
	private float[] gdir;
	private View level;
	private TextView txt_level;
	private ImageView stoplog_still;
	private ObjectAnimator anim_fade;
	private ObjectAnimator anim_appear;
	private AnimatorSet stoplog_move;
	private ObjectAnimator submerge1;
	private ObjectAnimator submerge2;
	private AnimatorSet submerge;
	private ImageView stoplog_moving;
	private ImageView stoplog_moving_trava;
	private ImageView regua_dir;
	private ImageView regua_esq;

	private MarginLayoutParams margins;
	private ImageView claw_left;
	private ImageView claw_right;
	private float claw_offset;
	private RelativeLayout liftbeam;
	private float liftbeam_offset;
	private ImageView key;
	private float key_offset;
	private ImageView engate_btn;
	private ImageView engate_fig;
	private ImageView desengate_btn;
	private ImageView desengate_fig;
	private ImageView desengatado_btn;
	private ImageView desengatado_fig;
	private int inductive_keyvalue;
	private ImageView inductive_right;
	private boolean inductive_rightvalue;
	private ImageView inductive_left;
	private boolean inductive_leftvalue;
	private ImageView still;
	private ImageView move_up;
	private ImageView move_down;
	private TextView pressure_value;
	private FrameLayout display;
	private FrameLayout water;
	private final float lvlmin = 2 * SensorManager.STANDARD_GRAVITY / 3;

	private RockDataReceiver Monitorreceiver;
	private Intent mServiceStartIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inser);

		pressure = STD_PRESSURE;
		pressure_offset = STD_PRESSURE;
		level = findViewById(R.id.View3);

		display = (FrameLayout) findViewById(R.id.animationFrame);
		water = (FrameLayout) findViewById(R.id.water);

		stoplog_still = (ImageView) findViewById(R.id.imageView14);
		stoplog_moving = (ImageView) findViewById(R.id.stoplog_base);
		stoplog_moving_trava = (ImageView) findViewById(R.id.stoplog_travas);

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
		pressure_value = (TextView) findViewById(R.id.Profund_text);

		txt_level = (TextView) findViewById(R.id.textView2);

		regua_dir = (ImageView) findViewById(R.id.imageView10);
		regua_esq = (ImageView) findViewById(R.id.imageView9);
		margins = (MarginLayoutParams) regua_esq.getLayoutParams();

		liftbeam = (RelativeLayout) findViewById(R.id.relativelayoutgarra);
		liftbeam_offset = 0;
		claw_left = (ImageView) findViewById(R.id.claw_left);
		claw_right = (ImageView) findViewById(R.id.claw_right);
		claw_offset = 0;
		key = (ImageView) findViewById(R.id.key);
		key_offset = 0;

		theta = 0;


		engate_btn = (ImageView) findViewById(R.id.engate_btn);
		engate_fig = (ImageView) findViewById(R.id.engate_fig);
		desengate_btn = (ImageView) findViewById(R.id.desengate_btn);
		desengate_fig = (ImageView) findViewById(R.id.desengate_fig);
		desengatado_btn = (ImageView) findViewById(R.id.desengatado_btn);
		desengatado_fig = (ImageView) findViewById(R.id.desengatado_fig);
		inductive_right = (ImageView) findViewById(R.id.inductive_right);
		inductive_rightvalue = false;
		inductive_left = (ImageView) findViewById(R.id.inductive_left);
		inductive_leftvalue = false;
		inductive_keyvalue = 0;

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

		key.post(new Runnable() {

			@Override
			public void run() {
				key.setPivotX((float) (key.getMeasuredWidth() * 7.0 / 82));
				key.setPivotY((float) (key.getMeasuredHeight() * 26.0 / 34));
			}

		});

		liftbeam.post(new Runnable() {

			@Override
			public void run() {
				liftbeam.setPivotX((float) (liftbeam.getMeasuredWidth() / 2.0));
				liftbeam.setPivotY(0);
			}

		});

		claw_left.post(new Runnable() {

			@Override
			public void run() {
				claw_left.setPivotX((float) (claw_left.getMeasuredWidth() * 31.5 / 77));
				claw_left.setPivotY((float) (claw_left.getMeasuredHeight() * 19.5 / 213));
			}

		});

		claw_right.post(new Runnable() {

			@Override
			public void run() {
				claw_right.setPivotX((float) (claw_right.getMeasuredWidth() * 42.0 / 74));
				claw_right.setPivotY((float) (claw_right.getMeasuredHeight() * 21.0 / 213));
			}

		});

		Smg = (SensorManager) getSystemService(SENSOR_SERVICE);

		gravity = Smg.getDefaultSensor(Sensor.TYPE_GRAVITY);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// Intent mServiceIntent = new Intent(this, BGJloop.class);
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
		claw_left.setRotation(-theta);
		claw_right.setRotation(theta);

	}

	public void rot_garra_dir(View view) {
		theta -= 5;
		claw_left.setRotation(-theta);
		claw_right.setRotation(theta);

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
					&& inductive_right.getTag().equals("green")) {
				anim_fade.setTarget(stoplog_still);
				anim_appear.setTarget(stoplog_moving);

				anim_fade.setFloatValues(0);
				anim_appear.setFloatValues(1);
				stoplog_moving_trava.animate().alpha(1).setDuration(2000)
						.start();
				stoplog_move.start();
			}
		} else {

			view.setTag("grey");
			((ImageView) view).setImageResource(R.drawable.contato_cinza);
			if (inductive_left.getTag().equals("grey")
					|| inductive_right.getTag().equals("grey")) {
				anim_fade.setTarget(stoplog_moving);
				anim_appear.setTarget(stoplog_still);

				anim_fade.setFloatValues(0);
				anim_appear.setFloatValues(0);
				stoplog_moving_trava.animate().alpha(0).setDuration(2000)
						.start();
				stoplog_move.start();
			}
		}

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

		liftbeam.setRotation(theta);

	}

	@Override
	public void inductive_right(boolean value) {
		if (inductive_rightvalue ^ value) {
			inductive_rightvalue = value;

			if (stoplog_move.isRunning())
				stoplog_move.cancel();

			if (inductive_rightvalue) {

				inductive_right.setImageResource(R.drawable.contato_verde);
				if (inductive_leftvalue) {
					anim_fade.setTarget(stoplog_still);
					anim_appear.setTarget(stoplog_moving);

					anim_fade.setFloatValues(0);
					anim_appear.setFloatValues(1);
					stoplog_moving_trava.animate().alpha(1).setDuration(1000)
							.start();
					stoplog_move.start();
				} else
					inductive_left.setImageResource(R.drawable.contato_amarelo);

			} else {
				if (!inductive_leftvalue) {
					anim_fade.setTarget(stoplog_moving);
					anim_appear.setTarget(stoplog_still);

					anim_fade.setFloatValues(0);
					anim_appear.setFloatValues(0);
					stoplog_moving_trava.animate().alpha(0).setDuration(1000)
							.start();
					stoplog_move.start();
					inductive_right.setImageResource(R.drawable.contato_cinza);
					inductive_left.setImageResource(R.drawable.contato_cinza);
				} else
					inductive_right
							.setImageResource(R.drawable.contato_amarelo);
			}
		}

	}

	@Override
	public void inclination_body(double value) {

		float inc = (float) Math.toDegrees(value) - liftbeam_offset;

		level.animate().rotation(inc).setDuration(200).start();
		liftbeam.animate().rotation(inc).setDuration(200).start();

		inc = Math.abs(inc);

		txt_level.setText(String.format("%.1f¡", inc));

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

			if (stoplog_move.isRunning())
				stoplog_move.cancel();

			if (inductive_leftvalue) {

				inductive_left.setImageResource(R.drawable.contato_verde);
				if (inductive_rightvalue) {
					anim_fade.setTarget(stoplog_still);
					anim_appear.setTarget(stoplog_moving);

					anim_fade.setFloatValues(0);
					anim_appear.setFloatValues(1);
					stoplog_moving_trava.animate().alpha(1).setDuration(1000)
							.start();
					stoplog_move.start();
				} else
					inductive_right
							.setImageResource(R.drawable.contato_amarelo);

			} else {
				if (!inductive_rightvalue) {
					anim_fade.setTarget(stoplog_moving);
					anim_appear.setTarget(stoplog_still);

					anim_fade.setFloatValues(0);
					anim_appear.setFloatValues(0);
					stoplog_moving_trava.animate().alpha(0).setDuration(1000)
							.start();
					stoplog_move.start();
					inductive_right.setImageResource(R.drawable.contato_cinza);
					inductive_left.setImageResource(R.drawable.contato_cinza);
				} else
					inductive_left.setImageResource(R.drawable.contato_amarelo);
			}
		}

	}

	@Override
	public void inductive_key(boolean value) {

//		if (inductive_keyvalue ^ value) {
//			inductive_keyvalue = value;
//			if (inductive_keyvalue)
//				inductive_key.setImageResource(R.drawable.contato_azul);
//			else
//				inductive_key.setImageResource(R.drawable.contato_cinza);
//
//		}

	}

	@Override
	public void inclination_right(double value) {

		float inc = (float) Math.toDegrees(value) - claw_offset;
		claw_left.animate().rotation(-inc).setDuration(200).start();
		claw_right.animate().rotation(inc).setDuration(200).start();

	}

	@Override
	public void inclination_key(double value) {

		float inc = (float) Math.toDegrees(value) - key_offset;
		key.animate().rotation(inc).setDuration(200).start();
		int position=-1;
		
		inc = Math.abs(inc);
		if (inc < 90)
			position = 0;
		else if(inc < 118)
			position = 1;
		else if(inc < 180)
			position = 2;
		else 
			return;
		
		if (position == inductive_keyvalue)
			return;

		switch (inductive_keyvalue){
		case 0:
			engate_fig.animate().alpha(0.3f).setDuration(500).start();
			engate_btn.setImageResource(R.drawable.btn_desativado_engate);
			break;
		case 1:
			desengate_fig.animate().alpha(0.3f).setDuration(500).start();
			desengate_btn.setImageResource(R.drawable.btn_desativado_desengate);
			break;
		case 2:
			desengatado_fig.animate().alpha(0.3f).setDuration(500).start();
			desengatado_btn.setImageResource(R.drawable.btn_desativado_desengatado);
			break;
		}

		switch (position){
		case 0:
			engate_fig.animate().alpha(1f).setDuration(500).start();
			engate_btn.setImageResource(R.drawable.btn_engate);
			break;
		case 1:
			desengate_fig.animate().alpha(1f).setDuration(500).start();
			desengate_btn.setImageResource(R.drawable.btn_desengate);
			break;
		case 2:
			desengatado_fig.animate().alpha(1f).setDuration(500).start();
			desengatado_btn.setImageResource(R.drawable.btn_desengatada);
			break;
		}
		
		inductive_keyvalue = position;
		
	}
	

	@Override
	public void pressure(double value) {

		if (pressure < pressure_offset * 1.002
				&& value >= pressure_offset * 1.002) {
			submerge(water);
			still.setVisibility(View.INVISIBLE);
			pressure_value.setVisibility(View.VISIBLE);
		} else if (pressure > pressure_offset * 1.001
				&& value <= pressure_offset * 1.001) {
			submerge(water);
			still.setVisibility(View.VISIBLE);
			pressure_value.setVisibility(View.INVISIBLE);

		}

		pressure_value.setText(String.format("%.1f m",
				(value - pressure_offset) / 100000));

		pressure = value;

	}

	public void old_presure(double value) {
		if (value == pressure) {
			if (still.getVisibility() == View.INVISIBLE) {
				still.setVisibility(View.VISIBLE);
				move_up.setVisibility(View.INVISIBLE);
				move_down.setVisibility(View.INVISIBLE);
			}
		} else {
			if (value > pressure) {
				if (move_down.getVisibility() == View.INVISIBLE) {
					still.setVisibility(View.INVISIBLE);
					move_up.setVisibility(View.INVISIBLE);
					move_down.setVisibility(View.VISIBLE);
				}

				if (pressure < STD_PRESSURE * 1.002
						&& value >= STD_PRESSURE * 1.002)
					submerge(water);
			} else {
				if (move_up.getVisibility() == View.INVISIBLE) {
					still.setVisibility(View.INVISIBLE);
					move_up.setVisibility(View.VISIBLE);
					move_down.setVisibility(View.INVISIBLE);
				}

				if (pressure > STD_PRESSURE * 1.001
						&& value <= STD_PRESSURE * 1.001)
					submerge(water);
			}
		}

		pressure = value;

		margins.topMargin = (int) (STD_PRESSURE - pressure) / 1000;
		regua_esq.setLayoutParams(margins);
		regua_dir.setLayoutParams(margins);

	}
}
