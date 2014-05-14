package com.lead.inser;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lead.rosa.R;

public class InsertActivity extends Activity implements SensorEventListener {

	private float theta=0;
	private SensorManager Smg;
	private Sensor gravity;
	private float[] gdir;
	private View level;
	private TextView txt_level;
	private ImageView garra_dir;
	private ImageView garra_esq;
	private RelativeLayout viga;
	private final float lvlmin = 2*SensorManager.STANDARD_GRAVITY/3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inser);

		level = findViewById(R.id.View3);
		
		txt_level = (TextView) findViewById(R.id.textView2);
		
		viga = (RelativeLayout) findViewById(R.id.relativelayoutgarra);
		garra_esq = (ImageView) findViewById(R.id.imageView12);
		garra_dir = (ImageView) findViewById(R.id.imageView13);
		
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
	
	@Override
	protected void onPause() {
		super.onPause();
		Smg.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Smg.registerListener(this, gravity,SensorManager.SENSOR_DELAY_GAME);
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
		
	}
	public void change_induc(View view){
		if(view.getTag().equals("grey"))
		{
			((ImageView) view).setImageResource(R.drawable.contato_verde);
			view.setTag("green");
			}
		else
		{
			view.setTag("grey");
			((ImageView) view).setImageResource(R.drawable.contato_cinza);
			}
		
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
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
}
