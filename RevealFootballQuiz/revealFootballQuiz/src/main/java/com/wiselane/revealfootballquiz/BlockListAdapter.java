package com.wiselane.revealfootballquiz;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BlockListAdapter extends BaseAdapter {

	private static final String PREFS_NAME = "unlock_prefs";
	
	private Context mContext;
	private List<BlockInfo> blockInfoList;
	
	public BlockListAdapter(Context context, List<BlockInfo> blockInfoList){
		mContext = context;
		this.blockInfoList = blockInfoList;
	}

	public List<BlockInfo> getBlockInfoList() {
		return blockInfoList;
	}

	public void setBlockInfoList(List<BlockInfo> blockInfoList) {
		this.blockInfoList = blockInfoList;
	}

	public int getCount() {
		return blockInfoList.size();
	}

	public Object getItem(int arg0) {
		return blockInfoList.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		return blockInfoList.size();
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder = null;
		if(arg1 == null){
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.block_listview_adapter, arg2, false);
			holder.block = (RelativeLayout) arg1.findViewById(R.id.adapterBlock);
			holder.blockNameTextView = (TextView)arg1.findViewById(R.id.tvBlockTitle);
			holder.blockScoreTextView = (TextView)arg1.findViewById(R.id.tvBlockScore);
			holder.playersUnlockedTextView = (TextView)arg1.findViewById(R.id.tvBlockPlayersUnlocked);
			holder.playButton = (Button)arg1.findViewById(R.id.bBlockPlay);
			holder.blockProgressBar = (ProgressBar)arg1.findViewById(R.id.pbBlockProgress);
			holder.blockProgressTextView = (TextView)arg1.findViewById(R.id.tvBlockFinalProgress);
			holder.blockCounterTextView = (TextView)arg1.findViewById(R.id.tvBlockCounter);
			holder.startProgressTextView = (TextView)arg1.findViewById(R.id.tvBlockStartProgress);
			holder.playersTextView = (TextView)arg1.findViewById(R.id.tvBlockPlayerCount);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}
		
		setBlockDetails(holder, arg0);
		
		return arg1;
	}
	
	private void setBlockDetails(ViewHolder holder, final int position) {

		setBackgroundForBlock(holder, position);
		
		BlockInfo info = blockInfoList.get(position);
		
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		int unlockCount = sharedPreferences.getInt("unlockCount", 0);
		
		int blockCounter = info.getBlockCounter() - unlockCount;

		holder.blockNameTextView.setText(info.getBlockName());
		
		if(blockCounter <= 0){
			makeViewVisible(holder);
			
			holder.blockScoreTextView.setText("Score: " + info.getTotalScore());
			holder.playersUnlockedTextView.setText(info.getUnlockedPlayers() + " / 72");
			holder.blockProgressBar.setProgress(info.getBlockProgress());
			holder.blockProgressTextView.setText(info.getBlockProgress() + " %");
			holder.startProgressTextView.setText("0%");
			holder.playersTextView.setText("Players");
		
			holder.playButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View arg0) {
					Intent intent = new Intent(mContext, MainActivity.class);
					intent.putExtra("block", position + 1);
					intent.putExtra("blockInfo", blockInfoList.get(position));
					mContext.startActivity(intent);
				}
			});
		
		}else{
			holder.blockCounterTextView.setVisibility(View.VISIBLE);
			holder.blockCounterTextView.setText("Identify " + blockCounter + " players to unlock");
			holder.blockScoreTextView.setVisibility(View.INVISIBLE);
			holder.playersUnlockedTextView.setVisibility(View.INVISIBLE);
			holder.blockProgressBar.setVisibility(View.INVISIBLE);
			holder.blockProgressTextView.setVisibility(View.INVISIBLE);
			holder.playButton.setEnabled(false);
			holder.startProgressTextView.setVisibility(View.INVISIBLE);
			holder.playersTextView.setVisibility(View.INVISIBLE);
		}
		
	}

	private void makeViewVisible(ViewHolder holder) {
		holder.blockCounterTextView.setVisibility(View.INVISIBLE);
		holder.blockScoreTextView.setVisibility(View.VISIBLE);
		holder.playersUnlockedTextView.setVisibility(View.VISIBLE);
		holder.blockProgressBar.setVisibility(View.VISIBLE);
		holder.blockProgressTextView.setVisibility(View.VISIBLE);
		holder.playButton.setEnabled(true);
		holder.startProgressTextView.setVisibility(View.VISIBLE);
		holder.playersTextView.setVisibility(View.VISIBLE);
		
	}

	private void setBackgroundForBlock(ViewHolder holder, int position) {
		switch(position){
		case 0:
			holder.block.setBackgroundResource(R.drawable.block_one_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_one_drawable));
			break;
		case 1:
			holder.block.setBackgroundResource(R.drawable.block_two_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_two_drawable));
			break;
		case 2:
			holder.block.setBackgroundResource(R.drawable.block_three_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_three_drawable));
			break;
		case 3:
			holder.block.setBackgroundResource(R.drawable.block_four_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_four_drawable));
			break;
		case 4:
			holder.block.setBackgroundResource(R.drawable.block_five_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_five_drawable));
			break;
		case 5:
			holder.block.setBackgroundResource(R.drawable.block_six_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_six_drawable));
			break;
		case 6:
			holder.block.setBackgroundResource(R.drawable.block_seven_drawable);
			holder.blockProgressBar.setProgressDrawable(mContext.getResources().getDrawable(R.drawable.progress_bar_block_seven_drawable));
			break;
		default:
			
		}
	}

	static class ViewHolder {
		RelativeLayout block;
		TextView blockNameTextView;
		TextView blockScoreTextView;
		TextView playersUnlockedTextView;
		Button playButton;
		ProgressBar blockProgressBar;
		TextView blockProgressTextView;
		TextView blockCounterTextView;
		TextView playersTextView;
		TextView startProgressTextView;
	}

}
