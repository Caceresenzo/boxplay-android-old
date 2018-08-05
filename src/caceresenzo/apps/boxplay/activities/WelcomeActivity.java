package caceresenzo.apps.boxplay.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import caceresenzo.apps.boxplay.R;
import caceresenzo.apps.boxplay.helper.LocaleHelper;

public class WelcomeActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LocaleHelper.onAttach(base));
	}
	
}