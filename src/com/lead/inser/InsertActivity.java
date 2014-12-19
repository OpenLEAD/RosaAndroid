package com.lead.inser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lead.rosa.R;

public class InsertActivity extends Activity implements MonitoringDisplay {

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
					liftbeam_offset += liftbeam.getRotation();
					pressure_offset = pressure;
					if (still.getVisibility() == View.INVISIBLE) {
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
	private double pressure;
	private double pressure_offset;
	private View level;
	private TextView txt_level;
	private final Handler main_handler = new Handler();
	private ImageView claw_left;
	private ImageView claw_right;
	private FrameLayout liftbeam;
	private float liftbeam_offset;
	private ImageView engate_btn;
	private ImageView engate_fig;
	private ImageView desengate_btn;
	private ImageView desengate_fig;
	private ImageView desengatado_btn;
	private ImageView desengatado_fig;
	private int inductive_keyvalue;
	private final int ATTACHED = 2;
	private final int DETACHING = 1;
	private final int DETACHED = 0;
	private ImageView inductive_right;
	private boolean inductive_rightvalue;
	private ImageView inductive_left;
	private boolean inductive_leftvalue;
	private ImageView still;
	private TextView pressure_value;

	private RockDataReceiver Monitorreceiver;
	private Intent mServiceStartIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inser);

		pressure = STD_PRESSURE;
		pressure_offset = STD_PRESSURE;
		level = findViewById(R.id.View3);
		
		pressure_value = (TextView) findViewById(R.id.Profund_text);
		still = (ImageView) findViewById(R.id.still_depth);

		txt_level = (TextView) findViewById(R.id.textView2);


		liftbeam = (FrameLayout) findViewById(R.id.relativelayoutgarra);
		liftbeam_offset = 0;
		claw_left = (ImageView) findViewById(R.id.claw_left);
		claw_right = (ImageView) findViewById(R.id.claw_right);

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
		mServiceStartIntent.putExtra(INDUCTIVE_KEY_ATTACHED,
				"bus1/ports/inductive_key_attached/read.json");
		mServiceStartIntent.putExtra(INDUCTIVE_KEY_DETACHED,
				"bus1/ports/inductive_key_detached/read.json");
		mServiceStartIntent.putExtra(INCLINATION_BODY,
				"inclination_body/ports/angle/read.json");
		mServiceStartIntent.putExtra(PRESSURE,
				"pressure/ports/pressure_samples/read.json");

		liftbeam.post(new Runnable() {

			@Override
			public void run() {
				liftbeam.setPivotX((float) (liftbeam.getMeasuredWidth() / 2.0));
				liftbeam.setPivotY(0);
			}

		});

	}

	@Override
	protected void onPause() {
		super.onPause();
		// Intent mServiceIntent = new Intent(this, BGJloop.class);
		stopService(mServiceStartIntent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startService(mServiceStartIntent);
	}

	@Override
	public void inductive_right(boolean value) {
		if (inductive_rightvalue ^ value) {

			if (inductive_rightvalue = value) {

				inductive_right.setImageResource(R.drawable.contato_verde);
				claw_right.setImageResource(R.drawable.garra_direita_verde);
				
				if (!inductive_leftvalue) {

					inductive_left.setImageResource(R.drawable.contato_amarelo);

					main_handler.postDelayed(new Runnable() {
						public void run() {
							if (!inductive_leftvalue && inductive_rightvalue) {
								inductive_left.setImageResource(R.drawable.contato_vermelho);
								claw_left.setImageResource(R.drawable.garra_esq_vermelha);
							}
						}
					}, 5000);
				}

			} else {
				claw_right.setImageResource(R.drawable.garra_dir_amarela);
				
				if (!inductive_leftvalue) {
					claw_left.setImageResource(R.drawable.garra_esq_amarela);
					inductive_right.setImageResource(R.drawable.contato_cinza);
					inductive_left.setImageResource(R.drawable.contato_cinza);
				} else {
					inductive_right.setImageResource(R.drawable.contato_amarelo);

					main_handler.postDelayed(new Runnable() {
						public void run() {
							if (!inductive_rightvalue && inductive_leftvalue) {
								inductive_right.setImageResource(R.drawable.contato_vermelho);
								claw_right.setImageResource(R.drawable.garra_dir_vermelha);
							}
						}
					}, 5000);				
				
				}
			}
		}

	}

	@Override
	public void inclination_body(double value) {

		float inc = (float) Math.toDegrees(value) - liftbeam_offset;

		level.animate().rotation(inc).setDuration(200).start();
		liftbeam.animate().rotation(inc).setDuration(200).start();

		inc = Math.abs(inc);

		txt_level.setText(String.format("%.1f°", inc));

		if (inc > 10 && level.getTag().equals("green")) {
			level.setBackgroundColor(0xFFFF4444);
			txt_level.setTextColor(0xFFFF4444);
			level.setTag("red");
		}

		if (inc < 10 && level.getTag().equals("red")) {
			level.setBackgroundColor(0xFF41E020);
			txt_level.setTextColor(0xFF41E020);
			level.setTag("green");
		}

	}

	@Override
	public void inductive_left(boolean value) {
		if (inductive_leftvalue ^ value) {

			if (inductive_leftvalue = value) {

				inductive_left.setImageResource(R.drawable.contato_verde);
				claw_left.setImageResource(R.drawable.garra_esquerda_verde);
				
				if (!inductive_rightvalue) {

					inductive_right.setImageResource(R.drawable.contato_amarelo);

					main_handler.postDelayed(new Runnable() {
						public void run() {
							if (!inductive_rightvalue && inductive_leftvalue) {
								inductive_right.setImageResource(R.drawable.contato_vermelho);
								claw_right.setImageResource(R.drawable.garra_dir_vermelha);
							}
						}
					}, 5000);
				}

			} else {
				claw_left.setImageResource(R.drawable.garra_esq_amarela);
				
				if (!inductive_rightvalue) {
					claw_right.setImageResource(R.drawable.garra_dir_amarela);
					inductive_right.setImageResource(R.drawable.contato_cinza);
					inductive_left.setImageResource(R.drawable.contato_cinza);
				} else {
					inductive_left.setImageResource(R.drawable.contato_amarelo);

					main_handler.postDelayed(new Runnable() {
						public void run() {
							if (!inductive_leftvalue && inductive_rightvalue) {
								inductive_left.setImageResource(R.drawable.contato_vermelho);
								claw_left.setImageResource(R.drawable.garra_esq_vermelha);
							}
						}
					}, 5000);				
				
				}
			}
		}

	}

	@Override
	public void pressure(double value) {

		if (pressure < pressure_offset * 1.002
				&& value >= pressure_offset * 1.002) {
			still.setVisibility(View.GONE);
			pressure_value.setVisibility(View.VISIBLE);
		} else if (pressure > pressure_offset * 1.001
				&& value <= pressure_offset * 1.001) {
			still.setVisibility(View.VISIBLE);
			pressure_value.setVisibility(View.GONE);

		}

		pressure_value.setText(String.format("%.1f m",
				(value - pressure_offset) / 100000));

		pressure = value;

	}

	@Override
	public void inductive_key_attached(boolean value) {
		if (!(value ^ inductive_keyvalue == ATTACHED))
			return;

		if (value) {
			engate_fig.setImageResource(R.drawable.chave_engate_centered);
			engate_btn.setImageResource(R.drawable.btn_engate_ativo);

			desengate_fig.setImageResource(R.drawable.chave_sombra);
			desengate_btn.setImageResource(R.drawable.btn_desengate_inativo);

			inductive_keyvalue = ATTACHED;
		} else {
			engate_fig.setImageResource(R.drawable.chave_sombra);
			engate_btn.setImageResource(R.drawable.btn_engate_inativo);

			desengate_fig.setImageResource(R.drawable.chave_engate_centered);
			desengate_btn.setImageResource(R.drawable.btn_desengate_ativo);

			inductive_keyvalue = DETACHING;
		}

	}

	@Override
	public void inductive_key_detached(boolean value) {
		if (!(value ^ inductive_keyvalue == DETACHED))
			return;

		if (value) {
			desengatado_fig.setImageResource(R.drawable.chave_engate_centered);
			desengatado_btn.setImageResource(R.drawable.btn_desengatada_ativo);

			desengate_fig.setImageResource(R.drawable.chave_sombra);
			desengate_btn.setImageResource(R.drawable.btn_desengate_inativo);

			inductive_keyvalue = DETACHED;
		} else {
			desengatado_fig.setImageResource(R.drawable.chave_sombra);
			desengatado_btn
			.setImageResource(R.drawable.btn_desengatada_inativo);

			desengate_fig.setImageResource(R.drawable.chave_engate_centered);
			desengate_btn.setImageResource(R.drawable.btn_desengate_ativo);

			inductive_keyvalue = DETACHING;
		}

	}

	@Override
	public void inductive_key(boolean value) {
	}

	@Override
	public void inclination_right(double value) {
	}

	@Override
	public void inclination_key(double value) {
	}
}
