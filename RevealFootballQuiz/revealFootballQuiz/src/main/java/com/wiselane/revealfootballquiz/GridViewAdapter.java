package com.wiselane.revealfootballquiz;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;

public class GridViewAdapter extends BaseAdapter{
	private Context mContext;
	private List<Player> players;
	private static final int PLAYERS_PER_PAGE = 9;
	
	public GridViewAdapter(Context context, List<Player> players){
		mContext = context;
		this.players = players;
	}

	public int getCount() {
		return PLAYERS_PER_PAGE;
	}

	public Object getItem(int arg0) {
		return players.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder holder;
		if(arg1 == null){
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.gridview_layout, arg2, false);
			holder.playerImage = (ImageView) arg1.findViewById(R.id.ivThumb);
			holder.playerScoreRating = (RatingBar)arg1.findViewById(R.id.playerScoreRating);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder) arg1.getTag();
		}
		
		setRating(arg0, holder.playerScoreRating);
		Player player = players.get(arg0);
		if(player.getUnlocked() == 0){
			holder.playerImage.setImageResource(R.drawable.mask_lock);
		}else{
			int resID = mContext.getResources().getIdentifier(player.getImageName(), "drawable", mContext.getPackageName());
			holder.playerImage.setImageResource(resID);
		}
		
		return arg1;
	}
	
	private void setRating(int position, RatingBar playerScoreRating) {
		Player player = (Player) getItem(position);
		if(player.getUnlocked() == 1){
			int score = player.getScore();
			if(score > 800){
				playerScoreRating.setRating(3);
			}else if(score > 500){
				playerScoreRating.setRating(2);
			}else{
				playerScoreRating.setRating(1);
			}
		}
	}

	static class ViewHolder{
		ImageView playerImage;
		RatingBar playerScoreRating;
	}

}
