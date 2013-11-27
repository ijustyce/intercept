package com.txh.intercept;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.txh.sqlite.Api;

public class MainActivity extends Activity {

	private txApplication tx;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tx = (txApplication)getApplication();
		if(tx.firstUse()){
			Log.i("---main---", "create table");
			createTable();
		}
		init();
	}	
	private void init(){
		
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        
        int width = metric.widthPixels;
        int height = metric.heightPixels-24;
        
        int h = 3*height/16;
        int w = 11*width/30;
        
		Button bt9 = (Button)findViewById(R.id.bt9);
		bt9.setBackgroundResource(R.drawable.exit);
		Button bt10 = (Button)findViewById(R.id.bt10);
		bt10.setBackgroundResource(R.drawable.setting);
		
		int j = R.id.bt1;
		
		for(int i = j;i<j+8;i++){
			Button bt = (Button)findViewById(i);
			bt.setHeight(h);
			bt.setWidth(w);
		}	
	}
	
	public void btClick(View v){
		switch(v.getId()){
		case R.id.bt9:
			exit();
			break;
		case R.id.bt10:
			startActivity(new Intent(this,setting.class));
			break;
		case R.id.bt1:
			startActivity(new Intent(this,black.class));
			this.finish();
			break;
		case R.id.bt2:
			startActivity(new Intent(this, white.class));
			this.finish();
			break;
		case R.id.bt3:
			startActivity(new Intent(this,help.class));
			break;
		case R.id.bt4:
			startActivity(new Intent(this,about.class));
			break;
		case R.id.bt5:
			more();
			break;
		case R.id.bt6:
			point();
			break;
		case R.id.bt7:
			startActivity(new Intent(this,history.class));
			this.finish();
			break;
		case R.id.bt8:
			startActivity(new Intent(this,advance.class));
			break;
		}
	}
	
	private void exit(){
		finish();
	}
	
	private void more(){
	}
	
	private void point(){
	}
	
	 protected void onDestroy() {  
		 super.onDestroy();
	 } 
	 
	 private void createTable(){
		 Api api;
		 api = new Api();
		 String dbFile = tx.getDbFile();
		 String sql = "create table if not exists 'intercept'('phone')";		
		 api.createTable(sql, dbFile);
		 sql = "create table if not exists 'allow'('phone')";
		 api.createTable(sql, dbFile);
		 sql = "create table if not exists 'history'(_id INTEGER PRIMARY KEY,'phone','date')";		
		 api.createTable(sql, dbFile);
	}
}