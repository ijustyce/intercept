
package com.txh.intercept;

import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.txh.sqlite.Api;

public class service extends Service{

	ITelephony iTelephony;

	TelephonyManager manager;

	boolean isfriend = false , matteredPerson = false,
	blackName = false , cansend = false , samenum = false;

	String name = "" , dbFile , tag="intercept";
	Api api;
	private txApplication tx;

	public IBinder onBind(Intent arg0){

		return null;
	}

	@Override
	public void onCreate(){
		super.onCreate();
	}

	@Override
	public void onDestroy(){

		Log.i(tag, "service destroy , bye ...");
		super.onDestroy();
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.i(tag, "service start! ");
		api = new Api();
		tx = (txApplication)getApplication();
		dbFile = tx.getDbFile();
		phoner();
		intercept(tx.getNumber());
		stopSelfResult(startId);
		return START_NOT_STICKY;
	}

	private void intercept(String incomingNumber){
		
		Log.i(tag, "start intercept service ...");

		dealNumber(incomingNumber);

		SharedPreferences shared = getSharedPreferences(
				"com.txh.intercept_preferences", Context.MODE_PRIVATE);
		boolean isrun = shared.getBoolean("intercept", true);
		boolean mod = shared.getBoolean("mod", true);
		boolean mdr = shared.getBoolean("mdr", false);
		boolean issendto = shared.getBoolean("send", false);
		String sms_body = shared.getString("sms_body", "");
		
		if (isrun && !matteredPerson){
			Log.i(tag, "service run and is not matteredPerson ...");
			if(blackName){
				try{
					Log.i(tag, "is blackName ...");
					String[]column = {"phone","date"};
					String[]value = {incomingNumber,tx.getDate()};
					api.insertData(dbFile, "history", value, column);
					iTelephony.endCall();

				} catch (RemoteException e)
				{
					e.printStackTrace();
				}
			}
			else if (mdr){
				Log.i(tag, "no disturbing mode now ...");
				try{
					iTelephony.endCall();
					String[]column = {"phone","date"};
					String[]value = {incomingNumber,tx.getDate()};
					api.insertData(dbFile, "history", value, column);
					
					if (isfriend){
						String date1 = tx.getDate();

						SharedPreferences myshared = getSharedPreferences(
								"send_time", MODE_PRIVATE);
						
						String date2 = myshared.getString("date", "2013/06/16/22/22");
						
						String sendnum = myshared.getString("sendnum", "");

						if (sendnum.equals(incomingNumber)){
							samenum = true;
						}

						if (samenum){
							long time = tx.getQuot(date1, date2);
							if (time>600000){
								cansend = true;
							}
							else{
								cansend = false;
							}
						}

						if (cansend&&issendto){

							PendingIntent paIntent;
							SmsManager smsManager;
							if (sms_body.equals("")){
								sms_body = getResources().getString(
										R.string.sms_show);
							}
							paIntent = PendingIntent.getBroadcast(this, 0,
									new Intent(), 0);
							smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(incomingNumber, null,
									sms_body, paIntent, null);

							Editor edit = myshared.edit();
							edit.putString("date", date2);
							edit.putString("sendnum", incomingNumber);
							edit.commit();
						}
						
						notifi(incomingNumber);
					}

				} catch (RemoteException e){
					e.printStackTrace();
				}
			}

			else if (mod){
				Log.i(tag, "intercept stranger ...");
				if (!isfriend){
					try{
						iTelephony.endCall();
						String[]column = {"phone","date"};
						String[]value = {incomingNumber,tx.getDate()};
						api.insertData(dbFile, "history", value, column);
						stopSelf();
					} catch (RemoteException e){
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void dealNumber(String num){
		int i ,j;
		String[]column={"phone"};
		String [][]value = api.getData(dbFile, "allow", 
				"select * from allow", null, column);
		if(tx.getName(num)[1].equals("true")){
			isfriend = true;
		}
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				if(num.equals((value[i][j]))){
					matteredPerson = true;
					break;
				}
			}
		}
		
		value = api.getData(dbFile, "intercept", 
				"select * from intercept", null, column);
		for(i = 0;i<value.length;i++){
			for(j=0;j<value[i].length;j++){
				if(num.equals((value[i][j]))){
					Log.i(tag, "is blackName ...");
					blackName = true;
					break;
				}
			}
		}
	}
	
	private void notifi(String num) {
		
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType("vnd.android.cursor.dir/calls");
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, 0);
        String tickerText = getResources().getString(R.string.notifi).toString();
        String contentText = tx.getDate("yyyy-MM-dd  HH:mm:ss");
        String contentTitle = tx.getName(num)[0];
        
        tx.notifi(tickerText, contentTitle, contentText, contentIntent);
	}

	public void phoner(){
		manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Class<TelephonyManager> c = TelephonyManager.class;
		Method getITelephonyMethod = null;
		try{
			getITelephonyMethod = c.getDeclaredMethod("getITelephony",
					(Class[]) null);
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod.invoke(manager,
					(Object[]) null);
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
