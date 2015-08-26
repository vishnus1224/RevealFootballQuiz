package com.wiselane.revealfootballquiz;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class GridFragment extends Fragment {
	private GridView mainGridView;
	private GridViewAdapter gridAdapter;
	private List<Player> players;
	private DataBaseHelper databaseHelper;
	private int pageNumber;
	private int blockPosition;
	private BlockInfo info;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		databaseHelper = ((MainActivity)activity).getHelper();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grid, null);

		mainGridView = (GridView) view.findViewById(R.id.gvMain);
		
		mainGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(), DetailViewActivity.class);
				int realPosition = getPositionFromPageNumber(pageNumber);
				intent.putExtra("pos", realPosition + arg2);
				intent.putExtra("whichBlock", blockPosition);
				intent.putExtra("blockInfo", info);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}

			private int getPositionFromPageNumber(int pageNumber) {
				switch (pageNumber) {
				case 0:
					return 0;
				case 1:
					return 9;
				case 2:
					return 18;
				case 3:
					return 27;
				case 4:
					return 36;
				case 5:
					return 45;
				case 6:
					return 54;
				case 7:
					return 63;
				default:
					break;
				}
				return 0;
			}
		});
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Bundle arguments = getArguments();
		int position = arguments.getInt("position");
		pageNumber = position;
		blockPosition = arguments.getInt("blockPosition");
		info = databaseHelper.getAllBlocksInfo("BlockInfo").get(blockPosition - 1);
		
		players = new ArrayList<Player>();
		generatePlayers(position);
		

		gridAdapter = new GridViewAdapter(getActivity(), players);
		mainGridView.setAdapter(gridAdapter);
	}
	
	private void generatePlayers(int position) {
		int offset = 0;
		
		switch (position) {
		case 0:
			offset = 0;
			break;
		case 1:
			offset = 9;
			break;
		case 2:
			offset = 18;
			break;
		case 3:
			offset = 27;
			break;
		case 4:
			offset = 36;
			break;
		case 5:
			offset = 45;
			break;
		case 6:
			offset = 54;
			break;
		case 7:
			offset = 63;
			break;
		default:
			offset = 0;
			break;
		}
		
		int testValue = offset + 9;
		
		
		for(;offset < testValue; offset++){
			Player player = databaseHelper.getPlayer(offset, getTableName(blockPosition));
			
			players.add(player);
		}
	}

	private String getTableName(int blockPosition) {
		String tableName = null;
		switch(blockPosition){
		case 1:
			tableName = "BlockOne";
			break;
		case 2:
			tableName = "BlockTwo";
			break;
		case 3:
			tableName = "BlockThree";
			break;
		case 4:
			tableName = "BlockFour";
			break;
		case 5:
			tableName = "BlockFive";
			break;
		case 6:
			tableName = "BlockSix";
			break;
		case 7:
			tableName = "BlockSeven";
			break;
		}
		
		return tableName;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		databaseHelper.close();
		databaseHelper = null;
	}
	
}
