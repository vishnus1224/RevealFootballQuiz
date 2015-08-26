package com.wiselane.revealfootballquiz;

import android.os.Parcel;
import android.os.Parcelable;

public class BlockInfo implements Parcelable {

	private int id;
	private String blockName;
	private int blockScore;
	private int totalScore;
	private int totalPlayers;
	private int unlockedPlayers;
	private int blockProgress;
	private String blockTitle;
	private int blockCounter;

	public BlockInfo() {

	}

	public BlockInfo(Parcel source) {
		readFromParcel(source);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public int getBlockScore() {
		return blockScore;
	}

	public void setBlockScore(int blockScore) {
		this.blockScore = blockScore;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public void incrementTotalScore(int amount) {
		totalScore += amount;
	}

	public int getTotalPlayers() {
		return totalPlayers;
	}

	public void setTotalPlayers(int totalPlayers) {
		this.totalPlayers = totalPlayers;
	}

	public int getUnlockedPlayers() {
		return unlockedPlayers;
	}

	public void setUnlockedPlayers(int unlockedPlayers) {
		this.unlockedPlayers = unlockedPlayers;
	}

	public void incrementUnlockedPlayers(int amount) {
		unlockedPlayers += amount;
	}

	public int getBlockProgress() {
		return blockProgress;
	}

	public void setBlockProgress(int blockProgress) {
		this.blockProgress = blockProgress;
	}

	public String getBlockTitle() {
		return blockTitle;
	}

	public void setBlockTitle(String blockTitle) {
		this.blockTitle = blockTitle;
	}
	
	public void reduceBlockCounter(int value){
		blockCounter -= value;
	}
	
	public void setBlockCounter(int value){
		blockCounter = value;
	}
	
	public int getBlockCounter(){
		return blockCounter;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		arg0.writeString(blockName);
		arg0.writeInt(blockScore);
		arg0.writeInt(totalScore);
		arg0.writeInt(totalPlayers);
		arg0.writeInt(unlockedPlayers);
		arg0.writeInt(blockProgress);
		arg0.writeString(blockTitle);
		arg0.writeInt(blockCounter);
	}

	private void readFromParcel(Parcel parcel) {
		id = parcel.readInt();
		blockName = parcel.readString();
		blockScore = parcel.readInt();
		totalScore = parcel.readInt();
		totalPlayers = parcel.readInt();
		unlockedPlayers = parcel.readInt();
		blockProgress = parcel.readInt();
		blockTitle = parcel.readString();
		blockCounter = parcel.readInt();
	}

	public static final Parcelable.Creator<BlockInfo> CREATOR = new Parcelable.Creator<BlockInfo>() {

		public BlockInfo createFromParcel(Parcel source) {
			return new BlockInfo(source);
		}

		public BlockInfo[] newArray(int size) {
			return new BlockInfo[size];
		}
	};

}
