package com.wiselane.revealfootballquiz;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.database.SQLException;
import android.os.Bundle;
import android.widget.ListView;

public class BlockListActivity extends Activity {

	private ListView blockList;
	private BlockListAdapter mAdapter;
	private List<BlockInfo> blockInfoList;

	private DataBaseHelper mHelper;
	
	private boolean reload;
	
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_block_listview);
		
		adView = (AdView) this.findViewById(R.id.adViewBlock);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		blockList = (ListView) findViewById(R.id.listViewBlocks);
		mHelper = new DataBaseHelper(this);

		try {
			mHelper.createDataBase();
		} catch (IOException e) {
			throw new Error("Unable to create database");
		}

		try {

			mHelper.openDataBase();

		} catch (SQLException sqle) {

			throw sqle;

		}

		blockInfoList = mHelper.getAllBlocksInfo("BlockInfo");
		mAdapter = new BlockListAdapter(this, blockInfoList);
		blockList.setAdapter(mAdapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (adView != null) {
			adView.resume();
		}
		
		if(reload){
			blockInfoList = mHelper.getAllBlocksInfo("BlockInfo");
			mAdapter.setBlockInfoList(blockInfoList);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
		reload = true;
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
}
