package com.esetron.parktr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;

public class FirstActivity extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		
		Commons.hostAct = this;
		
	}
	
	public class startActivityAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Intent intent = new Intent(Commons.hostAct, MainActivity.class);
			Commons.hostAct.startActivity(intent);
			Commons.hostAct.finish();
		}
	}
	
	@Override
	protected void onStart () {
		super.onStart();
		
		Boolean isNetworkAvailable = Commons.isNetworkAvailable(this);
		
		if (isNetworkAvailable == false) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
    		
        	builder.setMessage("Check your internet connection !");
        	
        	builder.setPositiveButton(getResources().getString(R.string.btt_ok), new DialogInterface.OnClickListener() {
    			
    			public void onClick(DialogInterface dialog, int which) {
    				switch (which) {
    				case DialogInterface.BUTTON_NEUTRAL:
    					FirstActivity.this.finish();
    					break;
    				}
    			}
    		}).show();
		} else {
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		    String IMEI; 
		    IMEI = tm.getDeviceId();
		    Commons.imei = IMEI;
			String arg[] = {Commons.imei, "sessionkey"};
			GetParkingLotsAsyncTask requestTask = new GetParkingLotsAsyncTask();
			requestTask.execute(arg);
		}
		
		try {
			startActivityAsyncTask task = new startActivityAsyncTask();
			task.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
}
