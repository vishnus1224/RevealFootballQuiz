package com.wiselane.revealfootballquiz;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	private String DB_PATH;

	private static String DB_NAME = "football_new.sqlite";

	// Block Table Columns names
	private static final String KEY_ID = "_id";
	private static final String KEY_NAME = "Name";
	private static final String KEY_IMAGE_NAME = "ImageName";
	private static final String KEY_SCORE = "Score";
	private static final String KEY_UNLOCKED = "Unlocked";
	private static final String KEY_HINT = "Hint";
	private static final String KEY_OPTIONS = "options";
	private static final String KEY_VISIBLE_PARTS = "VisibleParts";

	private static final String KEY_TOTAL_SCORE = "TotalScore";
	private static final String KEY_UNLOCKED_PLAYERS = "UnlockedPlayers";
	private static final String KEY_PROGRESS = "Progress";

	private static final String[] COLUMNS = { KEY_ID, KEY_NAME, KEY_IMAGE_NAME,
			KEY_SCORE, KEY_UNLOCKED, KEY_HINT, KEY_OPTIONS, KEY_VISIBLE_PARTS };

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;

		if (android.os.Build.VERSION.SDK_INT >= 17) {
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		} else {
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		}
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getWritableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public List<BlockInfo> getAllBlocksInfo(String tableName) {
		List<BlockInfo> blockInfoList = new ArrayList<BlockInfo>();

		String selectQuery = "SELECT  * FROM " + tableName;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				BlockInfo blockInfo = new BlockInfo();
				blockInfo.setId(cursor.getInt(0));
				blockInfo.setBlockName(cursor.getString(1));
				blockInfo.setBlockScore(cursor.getInt(2));
				blockInfo.setTotalScore(cursor.getInt(3));
				blockInfo.setTotalPlayers(cursor.getInt(4));
				blockInfo.setUnlockedPlayers(cursor.getInt(5));
				blockInfo.setBlockProgress(cursor.getInt(6));
				blockInfo.setBlockTitle(cursor.getString(7));
				blockInfo.setBlockCounter(cursor.getInt(8));
				blockInfoList.add(blockInfo);
			} while (cursor.moveToNext());
		}

		return blockInfoList;
	}

	public int updateBlockInfo(BlockInfo info, String tableName) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_TOTAL_SCORE, info.getTotalScore());
		values.put(KEY_UNLOCKED_PLAYERS, info.getUnlockedPlayers());
		values.put(KEY_PROGRESS, info.getBlockProgress());

		int i = database.update(tableName, values, "id" + " = ?",
				new String[] { String.valueOf(info.getId()) });

		database.close();

		return i;
	}

	public Player getPlayer(int id, String tableName) {
		SQLiteDatabase database = this.getReadableDatabase();

		Cursor cursor = database.query(tableName, COLUMNS, KEY_ID + " = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}

		Player player = new Player();
		player.setId(cursor.getInt(0));
		player.setName(cursor.getString(1));
		player.setImageName(cursor.getString(2));
		player.setScore(cursor.getInt(3));
		player.setUnlocked(cursor.getInt(4));
		player.setHint(cursor.getString(5));
		player.setOptions(cursor.getString(6));
		player.setVisibleParts(cursor.getString(7));
		return player;

	}

	public int updatePlayer(Player player, String tableName) {
		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_SCORE, player.getScore());
		values.put(KEY_UNLOCKED, player.getUnlocked());
		values.put(KEY_VISIBLE_PARTS, player.getVisibleParts());

		int i = database.update(tableName, values, KEY_ID + " = ?",
				new String[] { String.valueOf(player.getId()) });

		database.close();

		return i;
	}

	public void insertPlayer(Player player, String tableName) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ID, player.getId());
		values.put(KEY_NAME, player.getName());
		values.put(KEY_IMAGE_NAME, player.getImageName());
		values.put(KEY_SCORE, player.getScore());
		values.put(KEY_UNLOCKED, player.getUnlocked());
		values.put(KEY_HINT, player.getHint());
		values.put(KEY_OPTIONS, player.getOptions());
		values.put(KEY_VISIBLE_PARTS, player.getVisibleParts());
		long result = database.insert(tableName, null, values);

		database.close();

	}

	public List<Player> getAllPlayers(String tableName) {
		List<Player> playerList = new ArrayList<Player>();

		String selectQuery = "SELECT  * FROM " + tableName;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Player player = new Player();
				player.setId(cursor.getInt(0));
				player.setName(cursor.getString(1));
				player.setImageName(cursor.getString(2));
				player.setScore(cursor.getInt(3));
				player.setUnlocked(cursor.getInt(4));
				player.setHint(cursor.getString(5));
				player.setOptions(cursor.getString(6));
				player.setVisibleParts(cursor.getString(7));
				playerList.add(player);
			} while (cursor.moveToNext());
		}

		return playerList;
	}

	public void resetDatabase() {
		
		if(checkDataBase()){
			File database = new File(DB_PATH + DB_NAME);
			if(database.exists()){
				database.delete();
			}
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		int number = random.nextInt(9);
		if (number == 3 || number == 4 || number == 5) {
			return generateRandomNumber();
		} else {
			return number;
		}
	}
}