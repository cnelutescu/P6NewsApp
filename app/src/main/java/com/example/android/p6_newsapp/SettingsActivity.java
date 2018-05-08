package com.example.android.p6_newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

/**
 * Constant value for the book loader ID. We can choose any integer.
 * Maximum limit of articles to be displayed
*/
    private static final String MAX_PAGE_SIZE = "50";


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
            Preference pageSize = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(pageSize);
            // find ListPreference orderBy
            Preference section = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryToValue(section);
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

            // Limit at 50 the number of articles displayed (page_size) and write to preference file on device
            String stringKey = preference.getKey();
                        if (stringKey.equals("page_size")) {
                int intValue = Integer.parseInt(stringValue);
                int maxLim = Integer.parseInt(MAX_PAGE_SIZE);
                if (intValue > maxLim) {
                    stringValue = MAX_PAGE_SIZE;
                    // Write to preference file on device
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    SharedPreferences.Editor prefEditor = settings.edit();
                    // prefEditor.putInt(stringKey, intValue);
                    prefEditor.putString(stringKey, stringValue);
                    prefEditor.apply();
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