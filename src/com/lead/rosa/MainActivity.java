package com.lead.rosa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.lead.inser.InsertActivity;
import com.lead.sonar.SonarActivity;

public class MainActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_setting_page:
			startActivity(new Intent(this, SettingsActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void inser(View view) {
		startActivity(new Intent(this, InsertActivity.class));
	}

	public void remo(View view) {
		startActivity(new Intent(this, InsertActivity.class));
	}

	public void sonar(View view) {
		startActivity(new Intent(this, SonarActivity.class));
	}
}