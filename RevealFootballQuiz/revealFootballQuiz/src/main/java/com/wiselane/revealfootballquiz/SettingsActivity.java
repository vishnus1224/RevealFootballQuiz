package com.wiselane.revealfootballquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SettingsActivity extends Activity implements OnClickListener {
	
	private static final String PREFS_NAME = "unlock_prefs";

	private AdView adView;
	
	private Button likeOnFacebook;
	private Button vibrateToggle;
	private Button soundToggle;
	private Button rate;
	private Button resetGame;
	
	private DataBaseHelper mHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		adView = (AdView) this.findViewById(R.id.adViewSettings);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		mHelper = new DataBaseHelper(this);
		
		likeOnFacebook = (Button)findViewById(R.id.bLikeOnFacebook);
		vibrateToggle = (Button)findViewById(R.id.bVibrateSetting);
		soundToggle = (Button)findViewById(R.id.bSoundSetting);
		rate = (Button)findViewById(R.id.bRate);
		resetGame = (Button)findViewById(R.id.bResetGame);
		
		displayVibrateToggle(vibrateToggle, SettingsManager.getVibrateSetting(this));
		displaySoundToggle(soundToggle, SettingsManager.getSoundSetting(this));
		
		likeOnFacebook.setOnClickListener(this);
		vibrateToggle.setOnClickListener(this);
		soundToggle.setOnClickListener(this);
		rate.setOnClickListener(this);
		resetGame.setOnClickListener(this);
	}
	
	private void displayVibrateToggle(Button vibrate, boolean on){
		if(on){
			vibrate.setBackgroundResource(R.drawable.vibrate_button_on);
		}else{
			vibrate.setBackgroundResource(R.drawable.vibrate_button);
		}
	}
	
	
	private void displaySoundToggle(Button sound, boolean on){
		if(on){
			sound.setBackgroundResource(R.drawable.sound_button_on);
		}else{
			sound.setBackgroundResource(R.drawable.sound_button);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}
	
	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
		
		mHelper.close();
		mHelper = null;
	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bLikeOnFacebook:
			String url = "http://www.facebook.com/revealfootballquiz";
			Intent viewIntent = new Intent(Intent.ACTION_VIEW);
			viewIntent.setData(Uri.parse(url));
			startActivity(viewIntent);
			break;
		case R.id.bVibrateSetting:
			boolean vibrate = SettingsManager.getVibrateSetting(this);
			vibrate = !vibrate;
			SettingsManager.setVibrateSetting(this, vibrate);
			
			displayVibrateToggle((Button) v, vibrate);
			break;
		case R.id.bSoundSetting:
			boolean sound = SettingsManager.getSoundSetting(this);
			sound = !sound;
			SettingsManager.setSoundSetting(this, sound);
			displaySoundToggle((Button) v, sound);
			break;
		case R.id.bRate:
			String link = "https://play.google.com/store/apps/details?id=com.wiselane.revealfootballquiz";
			Intent rateIntent = new Intent(Intent.ACTION_VIEW);
			rateIntent.setData(Uri.parse(link));
			startActivity(rateIntent);
			break;
		case R.id.bResetGame:
			showConfirmationAlert();
			break;
		default:
			
		}
	}
	
	private void showConfirmationAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reset").setMessage("Are you sure you want to reset?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				new ResetGameTask().execute();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private class ResetGameTask extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SettingsActivity.this);
			progressDialog.setMessage("Resetting Game...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			mHelper.resetDatabase();
			setPreferences();
			return null;
		}

		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			Toast.makeText(SettingsActivity.this, "Game Reset", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	protected void setPreferences() {
		SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putInt("unlockCount", 0);
		editor.commit();
	}
}
