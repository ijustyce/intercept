package com.txh.intercept;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.txh.sqlite.Api;

public class history extends Activity{
	String next = "",dbFile;
	int total;
	Api api;
	private txApplication tx;
	private boolean showMenu; 

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_intercept);
		api = new Api();
		tx = (txApplication)getApplication();
		dbFile = tx.getDbFile();
		TextView t = (TextView)findViewById(R.id.info);
		t.setText(getResources().getString(R.string.history_list).toString());
		settext();
	}
	
	
	private void settext(){	
		int i,j;
		String id="",phone="",date="";
		String[]column={"_id","phone","date"};
		String [][]value = api.getData(dbFile, "history", 
				"select * from history", null, column);
		if(value.length>0){
			showMenu = true;
		}
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				if(j==0){
					id = value[i][j];
				}
				else if(j==1){
					phone = value[i][j];
				}
				else{
					date = value[i][j];
				}
			}
			ShowIntercept(date+": "+tx.getName(phone)[0],id);
		}
	}

	@SuppressWarnings("deprecation")
	private void ShowIntercept(String phone,String id){
		WindowManager wm = getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();

		Button bt = new Button(this);
		bt.setTextSize(14);
		bt.setBackgroundDrawable(getResources().getDrawable(R.drawable.bkcolor));
		bt.setTextColor(android.graphics.Color.BLACK);
		bt.setText(phone);
		bt.setContentDescription(phone);
		bt.setLayoutParams(new LinearLayout.LayoutParams(7 * width / 8,
														 LinearLayout.LayoutParams.WRAP_CONTENT));
		final String temp = id;
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
		api.delete(dbFile, "history", "_id=?", args);

		Intent intent = new Intent(this, history.class);
		startActivity(intent);
		this.finish();
	}
	
	private void clean(){
		api.delete(dbFile, "history", null, null);

		Intent intent = new Intent(this, history.class);
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
	
	public boolean onPrepareOptionsMenu(Menu menu)
	  {
	    // TODO Auto-generated method stub
	    MenuItem clean = menu.getItem(0);
	    if (showMenu)
	    {
	      clean.setVisible(true);
	    }
	    return super.onPrepareOptionsMenu(menu);
	  }
	  public boolean onCreateOptionsMenu(Menu menu)
	  {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	  }
	  public boolean onOptionsItemSelected(MenuItem item)
	  {
	    // TODO Auto-generated method stub
	    switch (item.getItemId())
	    {
	    case R.id.action_clean:
	      clean();
	      break;
	      default:
	      break;
	    }
	    return super.onOptionsItemSelected(item);
	  }
}
