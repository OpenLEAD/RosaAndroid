package com.lead.inser;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lead.rosa.R;

public class InsertActivity extends Activity implements SensorEventListener {

	
	private SensorManager Smg;
	private Sensor gravity;
	private float[] gdir;
	private ImageView level;
	private ImageView viga;
	private final float lvlmin = 2*SensorManager.STANDARD_GRAVITY/3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inser);

		level = (ImageView) findViewById(R.id.imageView3);
		
		viga = (ImageView) findViewById(R.id.imageView4);
		viga.post(new Runnable(){

			@Override
			public void run() {
				viga.setPivotX(viga.getMeasuredWidth()/2);
				viga.setPivotY(0);
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
		
		level.setRotation(theta);
		viga.setRotation(theta);
		
	}
}
