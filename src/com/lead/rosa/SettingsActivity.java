package com.lead.rosa;

import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class SettingsActivity extends PreferenceActivity {
	public static class SettingsFragment extends PreferenceFragment {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			String settings = getArguments().getString("settings");
			switch(settings){
			case "sound":
				addPreferencesFromResource(R.xml.sound_preferences);
				break;
			case "limit":
				addPreferencesFromResource(R.xml.limit_preferences);
				break;
			}
		}

	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		// TODO Auto-generated method stub
		super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.preference_headers, target);

	}

}
