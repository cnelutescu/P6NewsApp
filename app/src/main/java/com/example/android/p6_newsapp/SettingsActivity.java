package com.example.android.p6_newsapp;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }





    public static class NewsAppPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            // find EditTextPreference minPageSize
            Preference minPageSize = findPreference(getString(R.string.settings_min_page_size_key));
            bindPreferenceSummaryToValue(minPageSize);
            // find ListPreference orderBy
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }


        // In order to update the preference summary when the settings activity is launched
        // we setup the bindPreferenceSummaryToValue() helper method
        private void bindPreferenceSummaryToValue(Preference preference) {
            // set the current EarthquakePreferenceFragment instance to listen for changes to the preference we pass in
            preference.setOnPreferenceChangeListener(this);
            // read the current value of the preference stored in the SharedPreferences on the device
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            // and display that in the preference summary
            onPreferenceChange(preference, preferenceString);
        }

        // This method will be called when the user has changed a Preference,
        // so inside of it we should add whatever action we want to happen after this change
        // The code in this method takes care of updating the displayed preference summary
        // after it has been changed
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            // The code in this method takes care of updating the displayed preference summary
            // after it has been changed
            String stringValue = value.toString();

            String stringKey = preference.getKey();
            if (stringKey.equals("page_size")) {
                int intValue = Integer.parseInt(stringValue);
                if (intValue > 50 ) {
                    intValue = 50;
                    stringValue = "50";

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    SharedPreferences.Editor prefEditor = settings.edit();
                    // prefEditor.putInt(stringKey, intValue);
                    prefEditor.putString(stringKey, stringValue);
                    prefEditor.commit();
                }

            }

            // properly update the summary of a ListPreference (using the label, instead of the key)
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);   // using the label
                }
            } else {
                preference.setSummary(stringValue);             // using the key
            }
            return true;
        }




    }






}