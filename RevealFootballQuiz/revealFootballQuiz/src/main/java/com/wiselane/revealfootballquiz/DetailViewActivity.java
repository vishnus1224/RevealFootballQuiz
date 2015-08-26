package com.wiselane.revealfootballquiz;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DetailViewActivity extends FragmentActivity {

	private ViewPager viewPager;
	private DetailPagerAdapter detailPagerAdapter;

	private DataBaseHelper helper;
	
	public SoundPool soundPool;
	
	public int soundIDCorrect;
	public int soundIDWrong;

	private AdView adView;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_detail);

		adView = (AdView) this.findViewById(R.id.adViewDetail);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		soundIDCorrect = soundPool.load(this, R.raw.whistle_right, 1);
		soundIDWrong = soundPool.load(this, R.raw.whistle_wrong, 1);

		helper = new DataBaseHelper(this);
		viewPager = (ViewPager) findViewById(R.id.detailPager);
		Bundle extras = getIntent().getExtras();
		int block = extras.getInt("whichBlock");
		BlockInfo info = extras.getParcelable("blockInfo");
		detailPagerAdapter = new DetailPagerAdapter(
				getSupportFragmentManager(), block, info);
		viewPager.setAdapter(detailPagerAdapter);
		viewPager.setCurrentItem(getIntent().getExtras().getInt("pos"));
	}
	
	@Override
	protected void onStart() {
		super.onStart();
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
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
		helper.close();
		helper = null;
		
		soundPool.unload(soundIDCorrect);
		soundPool.unload(soundIDWrong);
		soundPool.release();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public DataBaseHelper getHelper() {
		return helper;
	}

	public void setHelper(DataBaseHelper helper) {
		this.helper = helper;
	}
}
