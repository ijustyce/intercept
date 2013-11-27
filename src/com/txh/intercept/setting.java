package com.txh.intercept;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class setting extends PreferenceActivity {
    /** Called when the activity is first created. */
    @SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        
    }
}
