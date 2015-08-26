package com.wiselane.revealfootballquiz;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class ButtonClickListener implements OnClickListener{

	private Context mContext;
	private ButtonClickDelegate buttonClickDelegate;
			
	public ButtonClickListener(Context context, ButtonClickDelegate delegate){
		mContext = context;
		buttonClickDelegate = delegate;
	}
	
	public void onClick(View view) {
		buttonClickDelegate.buttonClicked(view);
	}
	
	public interface ButtonClickDelegate {
		public void buttonClicked(View view);
	}

}
