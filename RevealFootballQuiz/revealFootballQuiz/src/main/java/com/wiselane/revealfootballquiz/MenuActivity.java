package com.wiselane.revealfootballquiz;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;

public class MenuActivity extends Activity implements OnClickListener {

	private Button playButton;
	private Button settingsButton;
	private Button quitButton;
	private Button highscores;
	private AdView adView;
	
	private DataBaseHelper dbHelper;
	private GameHelper gameHelper;
	private List<BlockInfo> blockInfoList;
	
	private boolean canSignIn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		adView = (AdView) this.findViewById(R.id.adViewMenu);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		playButton = (Button) findViewById(R.id.bPlay);
		settingsButton = (Button) findViewById(R.id.bSettings);
		quitButton = (Button) findViewById(R.id.bQuit);
		highscores = (Button) findViewById(R.id.bHighscores);

		playButton.setOnClickListener(this);
		settingsButton.setOnClickListener(this);
		quitButton.setOnClickListener(this);
		highscores.setOnClickListener(this);

	}

	/*@Override
	protected void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}*/

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
		canSignIn = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(gameHelper != null){
			gameHelper.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
		
		if(dbHelper != null){
			dbHelper.close();
			dbHelper = null;
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bPlay:
			Intent intent = new Intent(MenuActivity.this,
					BlockListActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		case R.id.bSettings:
			Intent settingsIntent = new Intent(MenuActivity.this,
					SettingsActivity.class);
			startActivity(settingsIntent);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			break;
		case R.id.bQuit:
			showQuitDialog();
			break;
		case R.id.bHighscores:
			
			if(!canSignIn){
				createAndOpenDatabaseIfDoesNotExist();
				final int totalScore = generateTotalScore();
				gameHelper = new GameHelper(MenuActivity.this, GameHelper.CLIENT_GAMES);
				GameHelperListener listener = new GameHelperListener() {

					public void onSignInSucceeded() {
						if(gameHelper.getApiClient().isConnected()){
						Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_highscores), totalScore);
						
						startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
								gameHelper.getApiClient(), getString(R.string.leaderboard_highscores)),
								1);
						}
					}

					public void onSignInFailed() {
						Toast.makeText(getBaseContext(), "Failed to sign in",
								Toast.LENGTH_SHORT).show();
						canSignIn = false;
					}
				};

				gameHelper.setup(listener);
				gameHelper.onStart(MenuActivity.this);
			}
			break;
		}
	}
	
	private int generateTotalScore() {
		int totalScore = 0;
		blockInfoList = dbHelper.getAllBlocksInfo("BlockInfo");
		int length = blockInfoList.size();
		for(int i = 0 ; i < length ; i++){
			BlockInfo info = blockInfoList.get(i);
			totalScore += info.getTotalScore();
		}
		return totalScore;
	}

	private void createAndOpenDatabaseIfDoesNotExist() {
		dbHelper = new DataBaseHelper(this);
		
		try {
			dbHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}

		try {
			dbHelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;

		}
		
	}

	@Override
	protected void onActivityResult(int request, int response, Intent data) {
	    super.onActivityResult(request, response, data);
	    if(gameHelper != null){
	    	gameHelper.onActivityResult(request, response, data);
	    }
	}


	@Override
	public void onBackPressed() {
		showQuitDialog();
	}

	private void showQuitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
		builder.setTitle("Quit");
		builder.setMessage("Are you sure you want to quit?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				MenuActivity.this.finish();
			}

		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
