package com.wiselane.revealfootballquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DetailPagerAdapter extends FragmentStatePagerAdapter{

	private static final int NUMBER_OF_PAGES = 72;
	private int block;
	private BlockInfo info;
	
	public DetailPagerAdapter(FragmentManager fm, int block, BlockInfo info) {
		super(fm);
		this.block = block;
		this.info = info;
	}


	@Override
	public Fragment getItem(int arg0) {
		Bundle bundle = new Bundle();
		bundle.putInt("position", arg0);
		bundle.putInt("blockPosition", block);
		bundle.putParcelable("blockInfo", info);
		DetailViewFragment detailViewFragment =  new DetailViewFragment();
		detailViewFragment.setArguments(bundle);
		return detailViewFragment;
	}

	@Override
	public int getCount() {
		return NUMBER_OF_PAGES;
	}

	
}
