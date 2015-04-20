package ir.mhk448.android.myfriendavatar.ui;

import ir.mhk448.android.myfriendavatar.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.setting);
	}
}
