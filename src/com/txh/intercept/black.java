package com.txh.intercept;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txh.sqlite.Api;

public class black extends Activity{
	String next = "",dbFile;
	int total;
	Api api;
	private txApplication tx;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_intercept);
		api = new Api();
		tx = (txApplication)getApplication();
		dbFile = tx.getDbFile();
		settext();
	}
	
	
	private void settext(){	
		int i,j;
		String[]column={"phone"};
		String [][]value = api.getData(dbFile, "intercept", 
				"select * from intercept", null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				ShowIntercept(value[i][j]);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void ShowIntercept(String phone){
		WindowManager wm = getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();

		Button bt = new Button(this);
		bt.setTextSize(18);
		bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bkcolor));
		bt.setTextColor(android.graphics.Color.BLACK);
		bt.setText(phone);
		bt.setContentDescription(phone);
		bt.setLayoutParams(new LinearLayout.LayoutParams(7 * width / 8,
														 LinearLayout.LayoutParams.WRAP_CONTENT));
		final String temp = phone;
		bt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0){
					next = temp;
					ict();
				}

		});
		final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.intercept);
		linearLayout.addView(bt, 0);

		TextView textview = new TextView(this);
		textview.setHeight(8);
		linearLayout.addView(textview, 0);
	}

	private void ict(){
		new AlertDialog.Builder(this)
		.setTitle(getResources().getString(R.string.ask))
		.setMessage(getResources().getString(R.string.ask_content))
		.setNegativeButton(getResources().getString(R.string.no),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which){}
		})
		.setPositiveButton(getResources().getString(R.string.yes),
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int whichButton){
					del();
				}
		}).show();
	}

	private void del(){
		String [] args = {next};
		api.delete(dbFile, "intercept", "phone=?", args);

		Intent intent = new Intent(this, black.class);
		startActivity(intent);
		this.finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			startActivity(new Intent(this, MainActivity.class));
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
