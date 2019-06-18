package org.exarhteam.iitcm_cn.fragments;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.exarhteam.iitcm_cn.R;
import org.exarhteam.iitcm_cn.prefs.PluginPreference;
import org.exarhteam.iitcm_cn.prefs.PluginPreferenceActivity;

import java.util.ArrayList;

public class PluginsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // just a dummy to get the preferenceScreen
        addPreferencesFromResource(R.xml.pluginspreference);

        // alphabetical order
        getPreferenceScreen().setOrderingAsAdded(false);

        if (getArguments() != null) {
            // get plugins category for this fragments and plugins list
            String category = getArguments().getString("category");
            boolean userPlugin = getArguments().getBoolean("userPlugin");
            ArrayList<PluginPreference> prefs =
                    PluginPreferenceActivity.getPluginPreference(category, userPlugin);

            // add plugin checkbox preferences
            for (PluginPreference pref : prefs) {
                getPreferenceScreen().addPreference(pref);
            }

            // set action bar stuff
            ActionBar bar = getActivity().getActionBar();
            bar.setTitle("IITC Plugins: " + category);
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
