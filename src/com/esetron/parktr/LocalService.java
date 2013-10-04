package com.esetron.parktr;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

public class LocalService extends IntentService{

	public static Handler m_handler;
	
	
	public LocalService() {
		super("background");
	}
	
	@Override
	public void onCreate() {
		try {
			m_handler = new Handler();
			m_handlerTask.run();
		} catch (Exception ex) {
			Commons.log("run time ex : " + ex);
		}
	}

	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Commons.log("Local Service : Received start id " + startId + " : " + intent);
        
        return START_STICKY;
    }
	
	public static Runnable m_handlerTask = new Runnable() {
		
		@Override
		public void run() {
			//Commons.log("task started");
			GetParkingLotsAsyncTask task = new GetParkingLotsAsyncTask();
			String[] sendArray = new String[2];
			sendArray[0] = Commons.imei;
			sendArray[1] = "sessionkey";
			task.execute(sendArray);
			m_handler.postDelayed(m_handlerTask, 15000);
		}
	};
	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Commons.log("onHandleIntent");
		
	}
	

}
