package com.txh.intercept;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class recevicer extends BroadcastReceiver {
	
	String TAG = "receiver" ,mIncomingNumber;
	private txApplication tx;
	public void onReceive(Context context, Intent intent) {
		tx = (txApplication)context.getApplicationContext();
		 if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
			 String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			 Log.i(TAG, "call OUT:" + phoneNumber);
		  } 
		 else{
		   TelephonyManager tManager =
		   (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
		   
		   switch (tManager.getCallState()){
		   case TelephonyManager.CALL_STATE_RINGING:
			   if(!tx.getCallState()){
				   mIncomingNumber = intent.getStringExtra("incoming_number");
				   Log.i(TAG, "RINGING :" + mIncomingNumber);
				   tx.setNumber(mIncomingNumber);
				   Intent noteList = new Intent(context, service.class);
				   noteList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				   context.startService(noteList);
			   }
			   break;
		   case TelephonyManager.CALL_STATE_OFFHOOK:
			   tx.setCallState(true);
			   Log.i(TAG, "incoming ACCEPT");
			   break;
		   case TelephonyManager.CALL_STATE_IDLE:
			   tx.setCallState(false);
			   Log.i(TAG, "incoming IDLE");
			   break;
		   }
		}
	}
}
