package com.txh.intercept;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Process;
import android.provider.ContactsContract;
import android.util.Log;

public class txApplication extends Application{
	
	private static String tag = "---txApplication---";
	private String dbFile , incomingNumber; 
	private boolean chat = false;
	public void onCreate(){
		Log.i(tag, "Application onCreate , pid = " + Process.myPid());
	}
	
	/**
	 * check if is first time , return true if is first time
	 * @return
	 */
	public boolean firstUse(){
		File dbFile = new File(getDbFile());
		if(!dbFile.exists()){
			Log.i(tag, "this is first time to use");
			return true;
		}
		
		Log.i(tag, "this is not the first time to use");
		return false;
	}
	
	/**
	 * return sqlite file path ,if parent directory not exist
	 * it will create !
	 * @return
	 */
	public String getDbFile(){
		String file = this.getFilesDir().getPath() ;
		File f = new File(file);
		if(!f.exists()){
			f.mkdir();
		}
		dbFile = file + "/intercept.db";
		Log.i(tag, dbFile);
		return dbFile;
	}
	
	/**
	 * find name by phone number , return phone number 
	 * and false if not exist , or return name and true
	 * @return
	 */
	public String[] getName(String number){
		String name = "";
		String temp = number;
		String[] result = new String[2];
		result[1] = "true";
		int error = 0;
		while(name.equals("")&&error<3){
			if(error ==1&&number.length()>10){
				temp = number.substring(0,3)+" "+number.substring(3,7)+" "+number.substring(7,11);
			}
			if(error ==2&&number.length()>10){
				temp = number.substring(0,3)+" "+number.substring(3,7)+" "+number.substring(7,11);
			}
			
			Log.i(tag, temp);
			String[] projection = {
					ContactsContract.PhoneLookup.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.NUMBER };
			Cursor cur = this.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					projection, // Which columns to return.
					ContactsContract.CommonDataKinds.Phone.NUMBER
					+ " = '" + temp + "'", // WHERE clause.
					null, // WHERE clause value substitution
					null); // Sort order.
			if (cur == null){
				result[0] = number;
				result[1] = "false";
				return result;
			}
			for (int i = 0; i < cur.getCount(); i++){
				cur.moveToPosition(i);
				int nameFieldColumnIndex = cur
				.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
				name = cur.getString(nameFieldColumnIndex);
			}
			Log.i(tag, name);
			cur.close();
			error++;
		}
		if (name.equals("")){
			name = number;
			result[1] = "false";
		}
		result[0] = name;
		Log.i(tag, result[0]);
		return result;
	}
	
	/**
	 * return date , format like yyyy/MM/dd/HH/mm
	 * @return
	 */
	public String getDate(){
		 SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd/HH/mm",Locale.CHINA);
		 Date dd = new Date();
		 return ft.format(dd);
	}
	
	/**
	 * return date by your own formatter , like yyyy/MM/dd/HH/mm
	 * @return
	 */
	public String getDate(String formatter){
		 SimpleDateFormat ft = new SimpleDateFormat(formatter,Locale.CHINA);
		 Date dd = new Date();
		 return ft.format(dd);
	}
	
	/**
	 * return time1-time2 as a millisecond value
	 * @param time1
	 * @param time2
	 * @return
	 */	
	public long getQuot(String time1, String time2){
		 long quot = 0;
		 SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd/HH/mm",Locale.CHINA);
		 try {
			 Date date1 = ft.parse(time1);
			 Date date2 = ft.parse(time2);
			 quot = date1.getTime() - date2.getTime();
		  } catch (ParseException e) {
		   e.printStackTrace();
		  }
		  return quot;
	}
	
	/**
	 * return pkgName+" "+versionName , if fail return ""
	 * @return
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        String pkgName = info.applicationInfo.loadLabel(getPackageManager()).toString();
	        return pkgName+" "+version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	/**
	 * setting the number to intercept
	 * @param num
	 */
	
	public void setNumber(String num){
		incomingNumber = num;
	}
	
	
	/**
	 * return the number to intercept
	 * @return
	 */
	public String getNumber(){
		return incomingNumber;
	}
	
	/**
	 * return call state
	 * @return
	 */
	public boolean getCallState(){
		return chat;
	}
	
	/**
	 * set call state
	 * @return
	 */
	public void setCallState(boolean state){
		chat = state;
	}
	
	@SuppressWarnings("deprecation")
	public void notifi(String tickerText , String contentTitle ,
			String contentText , PendingIntent intent) {
		
		NotificationManager mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(R.drawable.intercept, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Context context = getApplicationContext();
        notification.setLatestEventInfo(context, contentTitle, contentText , intent); 
        mNotificationManager.notify(0, notification);
	}
}
