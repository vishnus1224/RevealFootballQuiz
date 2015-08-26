package com.wiselane.revealfootballquiz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class GridPagerAdapter extends FragmentStatePagerAdapter {
	private static final int NUMBER_OF_PAGES = 8;
	private int blockPosition;
	private BlockInfo info;

	public GridPagerAdapter(FragmentManager fm, int blockPosition, BlockInfo info) {
		super(fm);
		this.blockPosition = blockPosition;
		this.info = info;
	}

	@Override
	public Fragment getItem(int arg0) {
		Bundle bundle = new Bundle();
		bundle.putInt("position", arg0);
		bundle.putInt("blockPosition", blockPosition);
		bundle.putParcelable("blockInfo", info);
		GridFragment gridFragment =  new GridFragment();
		gridFragment.setArguments(bundle);
		return gridFragment;
	}

	@Override
	public int getCount() {
		return NUMBER_OF_PAGES;
	}

	public BlockInfo getInfo() {
		return info;
	}

	public void setInfo(BlockInfo info) {
		this.info = info;
	}

}
