package com.lead.inser;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.rosa.R;
import com.lead.rosa.SettingsActivity;

public class InsertActivity extends Activity implements MonitoringDisplay {

	private MenuItem connection_menu;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_inser, menu);
		connection_menu = menu.findItem(R.id.action_settings);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		final SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

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

							SharedPreferences.Editor editor = sharedPref.edit();
							editor.putFloat("liftbeam_offset", liftbeam_offset);
							editor.putFloat("pressure_offset", pressure_offset);
							editor.commit();

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

			alert.setTitle("Novo endereço IP");
			alert.setMessage("Endereço IP:");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			try {
				input.setText(new URL(sharedPref.getString("url",
						"http://192.168.1.75:9292/api/tasks/localhost/"))
						.getHost());
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			alert.setView(input);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							String ip = input.getText().toString();
							mServiceStartIntent.removeExtra("url");

							try {
								String url = "http://"
										+ URLEncoder.encode(
												ip.replace(" ", ""), "UTF-8")
										+ ":9292/api/tasks/localhost/";

								mServiceStartIntent.putExtra("url", url);

								SharedPreferences.Editor editor = sharedPref
										.edit();
								editor.putString("url", url);
								editor.commit();

							} catch (UnsupportedEncodingException e) {
								new AlertDialog.Builder(getBaseContext())
										.setTitle("IP errado")
										.setMessage(
												"O IP digitado possuí caracteres inválidos.")
										.setNeutralButton("Fechar", null)
										.setIcon(
												android.R.drawable.ic_dialog_alert)
										.show();

								e.printStackTrace();
							}
							stopService(mServiceStartIntent);
							startService(mServiceStartIntent);
							connection_menu.setIcon(R.drawable.ic_action_network_wifi_green);
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

		case R.id.action_setting_page:
			startActivity(new Intent(this, SettingsActivity.class));

		}
		return super.onOptionsItemSelected(item);
	}

	public static final float STD_PRESSURE = 100000;
	public static final float PRESSURE_TO_DEPTH = 1/100000;
	private static final int EPSLON_PRESSURE = 1000;
	private float pressure;
	private float pressure_offset;
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
	MediaPlayer mediaPlayer;
	private RockDataReceiver Monitorreceiver;
	private Intent mServiceStartIntent;
	private float align_max;
	private int depth_limit;
	private boolean too_deep;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inser);

		pressure = STD_PRESSURE;
		too_deep = false;
		level = findViewById(R.id.View3);

		pressure_value = (TextView) findViewById(R.id.Profund_text);
		still = (ImageView) findViewById(R.id.still_depth);

		txt_level = (TextView) findViewById(R.id.textView2);

		liftbeam = (FrameLayout) findViewById(R.id.relativelayoutgarra);
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

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String url = sharedPref.getString("url",
				"http://192.168.1.75:9292/api/tasks/localhost/");

		mServiceStartIntent.putExtra("url", url);

		mServiceStartIntent.putExtra(INDUCTIVE_LEFT,
				"bus1/ports/inductive_left/read.");
		mServiceStartIntent.putExtra(INDUCTIVE_RIGHT,
				"bus1/ports/inductive_right/read");
		mServiceStartIntent.putExtra(INDUCTIVE_KEY_ATTACHED,
				"bus1/ports/inductive_key_attached/read");
		mServiceStartIntent.putExtra(INDUCTIVE_KEY_DETACHED,
				"bus1/ports/inductive_key_detached/read");
		mServiceStartIntent.putExtra(INCLINATION_BODY,
				"inclination_body/ports/angle/read");
		mServiceStartIntent.putExtra(PRESSURE,
				"pressure/ports/pressure_samples/read");

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
		if (mediaPlayer != null) {

			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		align_max = sharedPref.getInt("align_limit", 8);
		depth_limit = sharedPref.getInt("depth_limit", 30);
		pressure_offset = sharedPref.getFloat("pressure_offset", STD_PRESSURE);
		liftbeam_offset = sharedPref.getFloat("liftbeam_offset", 0);
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

								SharedPreferences sharedPref = PreferenceManager
										.getDefaultSharedPreferences(getApplicationContext());

								if (sharedPref.getBoolean(
										"claw_unsuccess_sound", true)) {
									mediaPlayer = MediaPlayer.create(
											getApplicationContext(),
											R.raw.warning_sound);
									mediaPlayer.start();
								}

								inductive_left
										.setImageResource(R.drawable.contato_vermelho);
								claw_left
										.setImageResource(R.drawable.garra_esq_vermelha);
							}
						}
					}, 1000);
				} else {

					SharedPreferences sharedPref = PreferenceManager
							.getDefaultSharedPreferences(this);

					if (sharedPref.getBoolean("claw_success_sound", true)) {
						mediaPlayer = MediaPlayer.create(this,
								R.raw.grab_sound);
						mediaPlayer.start();
					}
				}

			} else {
				claw_right.setImageResource(R.drawable.garra_dir_amarela);

				if (!inductive_leftvalue) {
					claw_left.setImageResource(R.drawable.garra_esq_amarela);
					inductive_right.setImageResource(R.drawable.contato_cinza);
					inductive_left.setImageResource(R.drawable.contato_cinza);
				} else {
					inductive_right
							.setImageResource(R.drawable.contato_amarelo);

					main_handler.postDelayed(new Runnable() {
						public void run() {
							if (!inductive_rightvalue && inductive_leftvalue) {

								SharedPreferences sharedPref = PreferenceManager
										.getDefaultSharedPreferences(getApplicationContext());

								if (sharedPref.getBoolean(
										"claw_unsuccess_sound", true)) {
									mediaPlayer = MediaPlayer.create(
											getApplicationContext(),
											R.raw.warning_sound);
									mediaPlayer.start();
								}

								inductive_right
										.setImageResource(R.drawable.contato_vermelho);
								claw_right
										.setImageResource(R.drawable.garra_dir_vermelha);
							}
						}
					}, 1000);

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

		if (inc > align_max && level.getTag().equals("green")) {

			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(this);

			if (sharedPref.getBoolean("align_sound", true)) {
				mediaPlayer = MediaPlayer.create(this, R.raw.warning_sound);
				mediaPlayer.start();
			}

			level.setBackgroundColor(0xFFFF4444);
			txt_level.setTextColor(0xFFFF4444);
			level.setTag("red");
		}

		if (inc < align_max && level.getTag().equals("red")) {
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

					inductive_right
							.setImageResource(R.drawable.contato_amarelo);

					main_handler.postDelayed(new Runnable() {
						public void run() {
							if (!inductive_rightvalue && inductive_leftvalue) {

								SharedPreferences sharedPref = PreferenceManager
										.getDefaultSharedPreferences(getApplicationContext());

								if (sharedPref.getBoolean(
										"claw_unsuccess_sound", true)) {
									mediaPlayer = MediaPlayer.create(
											getApplicationContext(),
											R.raw.warning_sound);
									mediaPlayer.start();
								}

								inductive_right
										.setImageResource(R.drawable.contato_vermelho);
								claw_right
										.setImageResource(R.drawable.garra_dir_vermelha);
							}
						}
					}, 1000);
				} else {

					SharedPreferences sharedPref = PreferenceManager
							.getDefaultSharedPreferences(this);

					if (sharedPref.getBoolean("claw_success_sound", true)) {
						mediaPlayer = MediaPlayer.create(this,
								R.raw.grab_sound);
						mediaPlayer.start();
					}
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

								SharedPreferences sharedPref = PreferenceManager
										.getDefaultSharedPreferences(getApplicationContext());

								if (sharedPref.getBoolean(
										"claw_unsuccess_sound", true)) {
									mediaPlayer = MediaPlayer.create(
											getApplicationContext(),
											R.raw.warning_sound);
									mediaPlayer.start();
								}

								inductive_left
										.setImageResource(R.drawable.contato_vermelho);
								claw_left
										.setImageResource(R.drawable.garra_esq_vermelha);
							}
						}
					}, 1000);

				}
			}
		}

	}

	@Override
	public void pressure(float value) {

		if (pressure < pressure_offset * 1.002
				&& value >= pressure_offset * 1.002) {
			still.setVisibility(View.GONE);
			pressure_value.setVisibility(View.VISIBLE);
		} else if (pressure > pressure_offset * 1.001
				&& value <= pressure_offset * 1.001) {
			still.setVisibility(View.VISIBLE);
			pressure_value.setVisibility(View.GONE);

		}

		float depth = (value - pressure_offset) / 100000;

		pressure_value.setText(String.format("%.1f m", depth));

		pressure = value;

		if (depth > depth_limit) {
			if (!too_deep) {
				too_deep = true;
				pressure_value.setTextColor(getResources().getColor(
						R.color.warningRed));

				SharedPreferences sharedPref = PreferenceManager
						.getDefaultSharedPreferences(this);

				if (sharedPref.getBoolean("depth_sound", false)) {
					mediaPlayer = MediaPlayer.create(this, R.raw.warning_sound);
					mediaPlayer.start();
				}
			}
		} else if (depth < (depth_limit - EPSLON_PRESSURE*PRESSURE_TO_DEPTH) && too_deep) {
			too_deep = false;
			pressure_value.setTextColor(getResources().getColor(R.color.white));
		}

	}

	@Override
	public void inductive_key_attached(boolean value) {
		if (!(value ^ inductive_keyvalue == ATTACHED))
			return;

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (value) {
			engate_fig.setImageResource(R.drawable.chave_engate_centered);
			engate_btn.setImageResource(R.drawable.btn_engate_ativo);

			desengate_fig.setImageResource(R.drawable.chave_sombra);
			desengate_btn.setImageResource(R.drawable.btn_desengate_inativo);

			if (sharedPref.getBoolean("attach_sound", false)) {
				mediaPlayer = MediaPlayer.create(this,
						R.raw.key_sound);
				mediaPlayer.start();
			}

			inductive_keyvalue = ATTACHED;
		} else {
			engate_fig.setImageResource(R.drawable.chave_sombra);
			engate_btn.setImageResource(R.drawable.btn_engate_inativo);

			desengate_fig.setImageResource(R.drawable.chave_engate_centered);
			desengate_btn.setImageResource(R.drawable.btn_desengate_ativo);

			if (sharedPref.getBoolean("detach_sound", false)) {
				mediaPlayer = MediaPlayer.create(this,
						R.raw.key_sound);
				mediaPlayer.start();
			}

			inductive_keyvalue = DETACHING;
		}

	}

	@Override
	public void inductive_key_detached(boolean value) {
		if (!(value ^ inductive_keyvalue == DETACHED))
			return;

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (value) {
			desengatado_fig.setImageResource(R.drawable.chave_engate_centered);
			desengatado_btn.setImageResource(R.drawable.btn_desengatada_ativo);

			desengate_fig.setImageResource(R.drawable.chave_sombra);
			desengate_btn.setImageResource(R.drawable.btn_desengate_inativo);

			if (sharedPref.getBoolean("detached_sound", true)) {
				mediaPlayer = MediaPlayer.create(this,
						R.raw.key_sound);
				mediaPlayer.start();
			}

			inductive_keyvalue = DETACHED;
		} else {
			desengatado_fig.setImageResource(R.drawable.chave_sombra);
			desengatado_btn
					.setImageResource(R.drawable.btn_desengatada_inativo);

			desengate_fig.setImageResource(R.drawable.chave_engate_centered);
			desengate_btn.setImageResource(R.drawable.btn_desengate_ativo);

			if (sharedPref.getBoolean("detach_sound", false)) {
				mediaPlayer = MediaPlayer.create(this,
						R.raw.key_sound);
				mediaPlayer.start();
			}

			inductive_keyvalue = DETACHING;
		}

	}

	@Override
	public void bad_connection(int error_number) {
		//stopService(mServiceStartIntent);
		Toast.makeText(this, "Sistema Offline", Toast.LENGTH_SHORT).show();
		connection_menu.setIcon(R.drawable.ic_action_network_wifi_red);
	}
}
