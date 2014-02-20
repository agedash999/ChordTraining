package jp.agedash999.sample.chordtraining;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		getFragmentManager().beginTransaction()
		//		.replace(android.R.id.content, new SettingsFragment()).commit();

		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment()).commit();
	}

	public void restartThis() {
		finish();
		overridePendingTransition(0, 0);
		startActivity(getIntent());
		overridePendingTransition(0, 0);
	}


	public static class SettingsFragment
	extends PreferenceFragment
	implements OnSharedPreferenceChangeListener {

		ListPreference lp_rhythm;

		ListPreference lp_major_chord;
		ListPreference lp_minor_chord;
		ListPreference lp_7th_chord;
		ListPreference lp_major7th_chord;
		ListPreference lp_minor7th_chord;
		ListPreference lp_minorm7th_chord;
		ListPreference lp_diminish_chord;

		String key_restore_default;

		@Override
		public void onResume() {
			super.onResume();
			SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
			sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
		}
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preference_settings);

			lp_rhythm = (ListPreference)findPreference(getString(R.string.key_rhythm));
			lp_rhythm.setSummary(lp_rhythm.getEntry());

			lp_major_chord = (ListPreference)findPreference(getString(R.string.key_major_chord));
			lp_major_chord.setSummary(lp_major_chord.getEntry());
			lp_minor_chord = (ListPreference)findPreference(getString(R.string.key_minor_chord));
			lp_minor_chord.setSummary(lp_minor_chord.getEntry());
			lp_7th_chord = (ListPreference)findPreference(getString(R.string.key_7th_chord));
			lp_7th_chord.setSummary(lp_7th_chord.getEntry());
			lp_major7th_chord = (ListPreference)findPreference(getString(R.string.key_major7th_chord));
			lp_major7th_chord.setSummary(lp_major7th_chord.getEntry());
			lp_minor7th_chord = (ListPreference)findPreference(getString(R.string.key_minor7th_chord));
			lp_minor7th_chord.setSummary(lp_minor7th_chord.getEntry());
			lp_minorm7th_chord = (ListPreference)findPreference(getString(R.string.key_minorm7th_chord));
			lp_minorm7th_chord.setSummary(lp_minorm7th_chord.getEntry());
			lp_diminish_chord = (ListPreference)findPreference(getString(R.string.key_diminish_chord));
			lp_diminish_chord.setSummary(lp_diminish_chord.getEntry());

			key_restore_default = getString(R.string.key_restore_default);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if(key.equals(lp_rhythm.getKey())){
				lp_rhythm.setSummary(lp_rhythm.getEntry());
			}else if(key.equals(lp_major_chord.getKey())){
				lp_major_chord.setSummary(lp_major_chord.getEntry());
			}else if(key.equals(lp_minor_chord.getKey())){
				lp_minor_chord.setSummary(lp_minor_chord.getEntry());
			}else if(key.equals(lp_7th_chord.getKey())){
				lp_7th_chord.setSummary(lp_7th_chord.getEntry());
			}else if(key.equals(lp_major7th_chord.getKey())){
				lp_major7th_chord.setSummary(lp_major7th_chord.getEntry());
			}else if(key.equals(lp_minor7th_chord.getKey())){
				lp_minor7th_chord.setSummary(lp_minor7th_chord.getEntry());
			}else if(key.equals(lp_minorm7th_chord.getKey())){
				lp_minorm7th_chord.setSummary(lp_minorm7th_chord.getEntry());
			}else if(key.equals(lp_diminish_chord.getKey())){
				lp_diminish_chord.setSummary(lp_diminish_chord.getEntry());
			}else if(key.equals(key_restore_default)){
				SharedPreferences pref =  PreferenceManager.getDefaultSharedPreferences(getActivity());
				if(pref.getBoolean(key_restore_default, true) ){
					pref.edit().putBoolean(key_restore_default, false);
					PreferenceManager.getDefaultSharedPreferences(getActivity())
					.edit().clear().apply();
					PreferenceManager.setDefaultValues(getActivity(), R.xml.preference_settings, true);
					((SettingsActivity)getActivity()).restartThis();
				}

			}

		}
	}
}
