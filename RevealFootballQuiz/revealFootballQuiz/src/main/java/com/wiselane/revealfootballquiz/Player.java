package com.wiselane.revealfootballquiz;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
	private int id;
	private String name;
	private String imageName;
	private int score;
	private String hint;
	private int unlocked;
	private String options;
	private String visibleParts;
	
	public Player(){
		
	}
	
	public Player(Parcel parcel){
		readFromParcel(parcel);
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getUnlocked() {
		return unlocked;
	}

	public void setUnlocked(int unlocked) {
		this.unlocked = unlocked;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public String getOptions(){
		return options;
	}
	
	public void setOptions(String options){
		this.options = options;
	}
	
	public String getVisibleParts() {
		return visibleParts;
	}

	public void setVisibleParts(String visibleParts) {
		this.visibleParts = visibleParts;
	}
	
	public void addToVisibleParts(int part){
		visibleParts += part;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		arg0.writeString(name);
		arg0.writeString(imageName);
		arg0.writeInt(score);
		arg0.writeInt(unlocked);
		arg0.writeString(hint);
		arg0.writeString(options);
	}
	
	

	private void readFromParcel(Parcel parcel) {
		id = parcel.readInt();
		name = parcel.readString();
		imageName = parcel.readString();
		score = parcel.readInt();
		unlocked = parcel.readInt();
		//unlocked = (Boolean) parcel.readValue(Boolean.class.getClassLoader());
		hint = parcel.readString();
		options = parcel.readString();
	}
	
	public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {

		public Player createFromParcel(Parcel source) {
			return new Player(source);
		}

		public Player[] newArray(int size) {
			return new Player[size];
		}
	};

}
