package com.wiselane.revealfootballquiz;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends FragmentActivity {
	private ViewPager viewPager;
	private GridPagerAdapter gridPagerAdapter;

	private DataBaseHelper helper;

	private AdView adView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		adView = (AdView) this.findViewById(R.id.adViewGrid);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		Bundle extras = getIntent().getExtras();
		int position = extras.getInt("block");
		helper = new DataBaseHelper(this);

		BlockInfo info = extras.getParcelable("blockInfo");

		viewPager = (ViewPager) findViewById(R.id.gridPager);
		gridPagerAdapter = new GridPagerAdapter(getSupportFragmentManager(),
				position, info);
		viewPager.setAdapter(gridPagerAdapter);
		
	}

	public DataBaseHelper getHelper() {
		return helper;
	}

	public void setHelper(DataBaseHelper helper) {
		this.helper = helper;
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
		helper.close();
		helper = null;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

}
