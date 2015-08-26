package com.wiselane.revealfootballquiz;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.wiselane.revealfootballquiz.ButtonClickListener.ButtonClickDelegate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class DetailViewFragment extends Fragment implements ButtonClickDelegate {

	private static final int INVALID_VIEW_ID = -1;
	private static final String PREFS_NAME = "unlock_prefs";

	private ImageView playerImage;
	private Button removeMask;
	private ImageView[] masks;
	private List<Integer> visibleParts;
	private DataBaseHelper databaseHelper;
	private Player player;
	private int blockPosition;
	private RelativeLayout rootLayout;
	private RelativeLayout checkLayout;
	private RelativeLayout optionsLayout;
	private TextView userScoreTextView;

	private LinearLayout userAnswerStart;
	private LinearLayout userAnswerEnd;
	private RatingBar ratingBarDetail;

	private ButtonClickListener mListener;

	private Button[] optionButtons;
	private Button[] answerButtons;

	private Map<Integer, Object> questionAnswerMap;
	private String correctAnswer = "";
	private char[] userAnswer;

	private BlockInfo info;
	private Vibrator vibrator;

	private int soundIdCorrectAnswer;
	private int soundIdWrongAnswer;

	private SoundPool soundPool;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail, null);
		rootLayout = (RelativeLayout) view.findViewById(R.id.root);
		checkLayout = (RelativeLayout) view.findViewById(R.id.answerLayout);
		optionsLayout = (RelativeLayout) view.findViewById(R.id.optionsLayout);
		playerImage = (ImageView) view.findViewById(R.id.ivPicture);
		ratingBarDetail = (RatingBar) view.findViewById(R.id.userRatingDetail);
		userAnswerStart = (LinearLayout) view
				.findViewById(R.id.userAnswerStart);
		userAnswerEnd = (LinearLayout) view.findViewById(R.id.userAnswerEnd);
		userScoreTextView = (TextView) view.findViewById(R.id.tvUserScore);

		vibrator = (Vibrator) getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);

		int resID = getActivity().getResources().getIdentifier(
				player.getImageName(), "drawable",
				getActivity().getPackageName());
		playerImage.setImageResource(resID);

		questionAnswerMap = new LinkedHashMap<Integer, Object>();

		masks = new ImageView[9];
		visibleParts = new ArrayList<Integer>();
		populateMasks(view);

		mListener = new ButtonClickListener(getActivity(), this);

		removeMask = (Button) view.findViewById(R.id.bShowPiece);

		removeMask.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				player.setScore(player.getScore() - 100);
				int randomNumber = generateRandomNumber(masks.length);
				masks[randomNumber].setVisibility(View.INVISIBLE);
			}
		});

		generateAnswerLayout();

		findOptionButtons(view);

		return view;
	}


	protected void setPreferences() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		int unlockCount = sharedPreferences.getInt("unlockCount", 0);
		Editor editor = sharedPreferences.edit();
		editor.putInt("unlockCount", ++unlockCount);
		editor.commit();
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		databaseHelper = ((DetailViewActivity) activity).getHelper();
		soundPool = ((DetailViewActivity) activity).soundPool;
		soundIdCorrectAnswer = ((DetailViewActivity) activity).soundIDCorrect;
		soundIdWrongAnswer = ((DetailViewActivity) activity).soundIDWrong;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle arguments = getArguments();
		int position = arguments.getInt("position");
		blockPosition = arguments.getInt("blockPosition");
		info = arguments.getParcelable("blockInfo");
		player = databaseHelper
				.getPlayer(position, getTableName(blockPosition));

	}

	@Override
	public void onResume() {
		super.onResume();
		
		String part = player.getVisibleParts();
		visibleParts = extractIntegers(part);
		for (int i = 0; i < visibleParts.size(); i++) {
			masks[visibleParts.get(i)].setVisibility(View.INVISIBLE);
		}

		if (visibleParts.size() == masks.length) {
			removeMask.setEnabled(false);
		}

		if (player.getUnlocked() == 1) {
			setRating(ratingBarDetail);

			userScoreTextView.setVisibility(View.VISIBLE);
			userScoreTextView.setText("Score : "
					+ String.valueOf(player.getScore()));

			optionsLayout.setVisibility(View.INVISIBLE);
			for (int j = 0; j < correctAnswer.length(); j++) {
				int freePosition = getFirstFreePosition();
				if (freePosition != -1) {
					answerButtons[freePosition].setText(String
							.valueOf(correctAnswer.charAt(j)));
					answerButtons[freePosition].setEnabled(false);
				}
			}
		}

	}

	private void setRating(RatingBar playerScoreRating) {
		if (player.getUnlocked() == 1) {
			int score = player.getScore();
			if (score > 800) {
				playerScoreRating.setRating(3);
				userScoreTextView.setTextColor(getResources().getColor(
						R.color.gold));
			} else if (score > 500) {
				playerScoreRating.setRating(2);
				userScoreTextView.setTextColor(getResources().getColor(
						R.color.silver));
			} else {
				playerScoreRating.setRating(1);
				userScoreTextView.setTextColor(getResources().getColor(
						R.color.bronze));
			}
		}
	}

	private void generateAnswerLayout() {
		String[] cha = player.getName().split(" ");
		int screenSize = getDeviceScreenType();
		answerButtons = new Button[calculateLength(cha)];

		int tagToBeAdded = 0;
		int len = cha.length;
		for (int i = 0; i < len; i++) {

			for (int j = 0; j < cha[i].length(); j++) {
				Button button = new Button(getActivity());
				LinearLayout.LayoutParams params = setParamsBasedOnScreenSize(
						screenSize, button);
				if (cha[i].charAt(j) == '&') {
					button.setBackgroundResource(R.drawable.answer_arrow_button);
					button.setEnabled(false);
					button.setText("   ");
				} else {
					button.setBackgroundResource(R.drawable.answer_button);
				}
				button.setTextColor(Color.WHITE);
				// button.setPadding(0, 0, 5, 0);
				params.rightMargin = 5;
				button.setLayoutParams(params);

				if (tagToBeAdded == 0) {
					button.setTag(j);
					answerButtons[j] = button;
					answerButtons[j].setOnClickListener(mListener);
				} else {
					button.setTag(tagToBeAdded + j);
					answerButtons[tagToBeAdded + j] = button;
					answerButtons[tagToBeAdded + j]
							.setOnClickListener(mListener);
				}

				if (i == 0) {
					userAnswerStart.addView(button);
				} else {
					userAnswerEnd.addView(button);
				}
			}
			if (cha[i].contains("&")) {
				String b = cha[i].substring(0, cha[i].indexOf('&'));
				cha[i] = b;
			}
			correctAnswer += cha[i];
			tagToBeAdded = cha[i].length();
		}

		userAnswer = new char[correctAnswer.length()];
		for (int i = 0; i < correctAnswer.length(); i++) {
			userAnswer[i] = ' ';
		}
	}

	private LayoutParams setParamsBasedOnScreenSize(int screenSize,
			Button button) {
		LinearLayout.LayoutParams params = null;
		switch (screenSize) {
		case 0:
			params = new LinearLayout.LayoutParams(60, 60);
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
			break;
		case 1:
			params = new LinearLayout.LayoutParams(55, 55);
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			break;
		case 2:
			params = new LinearLayout.LayoutParams(80, 80);
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			break;
		case 3:
			params = new LinearLayout.LayoutParams(120, 120);
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			break;
		default:
			params = new LinearLayout.LayoutParams(150, 150);
			button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			break;
		}
		return params;
	}

	private int getDeviceScreenType() {

		DisplayMetrics metrics = new DisplayMetrics();

		WindowManager windowManager = (WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE);

		windowManager.getDefaultDisplay().getMetrics(metrics);

		int density = metrics.densityDpi;

		int screenType = -1;

		if (density == DisplayMetrics.DENSITY_XXHIGH) {
			screenType = 3;
		}

		else if (density == DisplayMetrics.DENSITY_XHIGH) {
			screenType = 2;
		}

		else if (density == DisplayMetrics.DENSITY_HIGH) {
			screenType = 1;
		}

		else if (density == DisplayMetrics.DENSITY_MEDIUM) {
			screenType = 0;
		}

		else if (density == DisplayMetrics.DENSITY_LOW) {
			screenType = 0;
		}

		else {
			screenType = -1;
		}

		return screenType;
	}

	private int calculateLength(String[] cha) {
		int length = 0;
		for (int i = 0; i < cha.length; i++) {
			length += cha[i].length();
		}
		return length;
	}

	private void findOptionButtons(View view) {
		optionButtons = new Button[18];
		optionButtons[0] = (Button) view.findViewById(R.id.buttonOption1);
		optionButtons[1] = (Button) view.findViewById(R.id.buttonOption2);
		optionButtons[2] = (Button) view.findViewById(R.id.buttonOption3);
		optionButtons[3] = (Button) view.findViewById(R.id.buttonOption4);
		optionButtons[4] = (Button) view.findViewById(R.id.buttonOption5);
		optionButtons[5] = (Button) view.findViewById(R.id.buttonOption6);
		optionButtons[6] = (Button) view.findViewById(R.id.buttonOption7);
		optionButtons[7] = (Button) view.findViewById(R.id.buttonOption8);
		optionButtons[8] = (Button) view.findViewById(R.id.buttonOption9);
		optionButtons[9] = (Button) view.findViewById(R.id.buttonOption10);
		optionButtons[10] = (Button) view.findViewById(R.id.buttonOption11);
		optionButtons[11] = (Button) view.findViewById(R.id.buttonOption12);
		optionButtons[12] = (Button) view.findViewById(R.id.buttonOption13);
		optionButtons[13] = (Button) view.findViewById(R.id.buttonOption14);
		optionButtons[14] = (Button) view.findViewById(R.id.buttonOption15);
		optionButtons[15] = (Button) view.findViewById(R.id.buttonOption16);
		optionButtons[16] = (Button) view.findViewById(R.id.buttonOption17);
		optionButtons[17] = (Button) view.findViewById(R.id.buttonOption18);

		String options = player.getOptions();

		for (int i = 0; i < optionButtons.length; i++) {
			optionButtons[i].setText(String.valueOf(options.charAt(i)));
			optionButtons[i].setOnClickListener(mListener);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		player.setVisibleParts("");
		for (int i = 0; i < visibleParts.size(); i++) {
			player.addToVisibleParts(visibleParts.get(i));
		}
		databaseHelper.updatePlayer(player, getTableName(blockPosition));

		databaseHelper.updateBlockInfo(info, "BlockInfo");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		vibrator.cancel();
		databaseHelper.close();
		Drawable d = playerImage.getDrawable();
		d = null;
		playerImage = null;
		for (int i = 0; i < masks.length; i++) {
			Drawable b = masks[i].getDrawable();
			b = null;
		}
		masks = null;
	}

	private void populateMasks(View view) {
		masks[0] = (ImageView) view.findViewById(R.id.mask1);
		masks[1] = (ImageView) view.findViewById(R.id.mask2);
		masks[2] = (ImageView) view.findViewById(R.id.mask3);
		masks[3] = (ImageView) view.findViewById(R.id.mask4);
		masks[4] = (ImageView) view.findViewById(R.id.mask5);
		masks[5] = (ImageView) view.findViewById(R.id.mask6);
		masks[6] = (ImageView) view.findViewById(R.id.mask7);
		masks[7] = (ImageView) view.findViewById(R.id.mask8);
		masks[8] = (ImageView) view.findViewById(R.id.mask9);
	}

	private int generateRandomNumber(int range) {
		int generatedNumber = new Random().nextInt(range);
		if (!visibleParts.contains(generatedNumber)) {
			visibleParts.add(generatedNumber);
			if (visibleParts.size() == masks.length) {
				removeMask.setEnabled(false);
			}
			return generatedNumber;
		} else {
			return generateRandomNumber(range);
		}
	}

	private String getTableName(int blockPosition) {
		String tableName = null;
		switch (blockPosition) {
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

	public void buttonClicked(View view) {

		if (view.getId() == INVALID_VIEW_ID) {
			Object tag = view.getTag();
			answerButtons[(Integer) tag].setText("");
			View button = (View) questionAnswerMap.get(tag);
			if (button != null) {
				button.setVisibility(View.VISIBLE);
				int index = (Integer) tag;
				userAnswer[index] = ' ';
				questionAnswerMap.remove(tag);
			}
		} else {
			int freePosition = getFirstFreePosition();
			if (freePosition != INVALID_VIEW_ID) {
				String text = (String) ((Button) view).getText();
				answerButtons[freePosition].setText(text);
				questionAnswerMap.put(freePosition, view);
				userAnswer[freePosition] = text.charAt(0);
				view.setVisibility(View.INVISIBLE);
				checkAnswer();
			}
		}

	}

	private void checkAnswer() {

		for (int j = 0; j < userAnswer.length; j++) {
			char c = userAnswer[j];
			if (c == ' ') {
				return;
			}
		}

		int offMargin = 0;

		for (int i = 0; i < correctAnswer.length(); i++) {
			if (correctAnswer.charAt(i) != userAnswer[i]) {
				offMargin++;
			}
		}

		long pattern[] = { 0, 100, 100, 100 };

		if (offMargin == 0) {
			if (SettingsManager.getVibrateSetting(getActivity())) {
				vibrator.vibrate(500);
			}

			if (SettingsManager.getSoundSetting(getActivity())) {
				playSound(soundIdCorrectAnswer);
			}

			player.setUnlocked(1);

			for (int i = 0; i < 9; i++) {
				if (!visibleParts.contains(i)) {
					visibleParts.add(i);
				}
				masks[visibleParts.get(i)].setVisibility(View.INVISIBLE);
			}

			removeMask.setEnabled(false);
			setRating(ratingBarDetail);
			info.incrementUnlockedPlayers(1);
			info.incrementTotalScore(player.getScore());

			int progress = (int) (((float) info.getUnlockedPlayers() / info
					.getTotalPlayers()) * 100);
			info.setBlockProgress(progress);

			userScoreTextView.setVisibility(View.VISIBLE);
			userScoreTextView.setText("Score : "
					+ String.valueOf(player.getScore()));

			optionsLayout.setVisibility(View.INVISIBLE);

			for (int b = 0; b < answerButtons.length; b++) {
				if (answerButtons[b] != null) {
					answerButtons[b].setEnabled(false);
				}
			}
			
			setPreferences();

			Toast.makeText(getActivity(), "Correct Answer", Toast.LENGTH_SHORT)
					.show();
		} else if (offMargin == 1) {
			if (SettingsManager.getVibrateSetting(getActivity())) {
				vibrator.vibrate(pattern, -1);
			}

			if (SettingsManager.getSoundSetting(getActivity())) {
				playSound(soundIdWrongAnswer);
			}

			Toast.makeText(getActivity(), "Almost Correct", Toast.LENGTH_SHORT)
					.show();
		} else {
			if (SettingsManager.getVibrateSetting(getActivity())) {
				vibrator.vibrate(pattern, -1);
			}

			if (SettingsManager.getSoundSetting(getActivity())) {
				playSound(soundIdWrongAnswer);
			}

			Toast.makeText(getActivity(), "Wrong Answer", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void playSound(int soundId) {
		AudioManager audioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
		float actualVolume = (float) audioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;

		soundPool.play(soundId, volume, volume, 1, 0, 1f);
	}

	private int getFirstFreePosition() {
		for (int i = 0; i < answerButtons.length; i++) {
			if (answerButtons[i] != null) {
				if (answerButtons[i].getText().equals("")) {
					return i;
				}
			}
		}
		return -1;
	}

	public List<Integer> extractIntegers(String input) {
		List<Integer> result = new ArrayList<Integer>();
		int index = 0;
		int v = 0;
		while (index < input.length()) {
			char c = input.charAt(index);
			if (Character.isDigit(c)) {
				v *= 10;
				v += c - '0';
				result.add(v);
				v = 0;
			}
			index++;
		}
		return result;
	}

}
