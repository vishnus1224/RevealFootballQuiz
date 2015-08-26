package com.wiselane.revealfootballquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	private static int SPLASH_TIME_OUT = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Intent i = new Intent(SplashActivity.this, MenuActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
			}
		}, SPLASH_TIME_OUT);

	}
}
