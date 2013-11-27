package com.txh.intercept;

import com.txh.sqlite.Api;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class advance extends Activity{
	Api api;
	String dbFile;
	private txApplication tx;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advance);
		api = new Api();
		tx = (txApplication)getApplication();
		dbFile = tx.getDbFile();
		init();
	}
	
	private void init(){
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);        
        int width = metric.widthPixels;
        
        Button bt1 = (Button)findViewById(R.id.bt1);
        bt1.setWidth(2*width/5);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bt1.getLayoutParams();
        params.setMargins(0, 20 , 0, 0); 
        bt1.setLayoutParams(params);
        
        Button bt2 = (Button)findViewById(R.id.bt2);
        bt2.setWidth(2*width/5);
        params = (RelativeLayout.LayoutParams) bt2.getLayoutParams();
        params.setMargins(width/10, 20 , 0, 0); 
        bt2.setLayoutParams(params);
	}
	
	public void btClick(View v){
		switch(v.getId()){
		case R.id.bt1:
			addBlack();
			break;
		case R.id.bt2:
			addWhite();
			break;
		}
	}
	
	private String getPhone(){
		EditText e = (EditText)findViewById(R.id.phone);
		return e.getText().toString();
	}
	
	private void addBlack(){
		String number = getPhone();
		if(isAllow(number)){
			showToast(getResources().getString(R.string.error_allow));
			return ;
		}
		if(!isBlack(number)){
			String[]column = {"phone"};
			String[]value = {getPhone()};
			api.insertData(dbFile, "intercept", value, column);
			showToast(getResources().getString(R.string.success_add));
			return;
		}
		if(isBlack(number)){
			showToast(getResources().getString(R.string.error_exist));
		}
	}
	
	private void addWhite(){
		String number = getPhone();
		if(isBlack(number)){
			showToast(getResources().getString(R.string.error_black));
			return ;
		}
		if(!isAllow(number)){
			String[]column = {"phone"};
			String[]value = {getPhone()};
			api.insertData(dbFile, "allow", value, column);
			showToast(getResources().getString(R.string.success_add));
			return ;
		}
		if(isAllow(number)){
			showToast(getResources().getString(R.string.error_exist));
		}
	}
	
	private boolean isAllow(String num){
		int i ,j;
		String[]column={"phone"};
		String [][]value = api.getData(dbFile, "allow", 
				"select * from allow", null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				if(num.equals((value[i][j]))){
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isBlack(String num){
		int i ,j;
		String[]column={"phone"};
		String [][]value = api.getData(dbFile, "intercept", 
				"select * from intercept", null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				if(num.equals((value[i][j]))){
					return true;
				}
			}
		}
		return false;
	}
	
	private void showToast(String s){
		Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}
}
