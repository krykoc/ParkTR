package com.esetron.parktr;

import android.os.AsyncTask;

public class GetParkingLotsAsyncTask extends AsyncTask<String, Void, Boolean>{

	@Override
	protected Boolean doInBackground(String... arg) {
		// TODO Auto-generated method stub
		ConnectionManager.getParkingLots(null, 0, arg[0], arg[1]);
		
		return true;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		try {
			MainActivity.showOnMapFunction();
		} catch(Exception ex) {
			Commons.log("getParkingLot ex : " + ex);
		}
	}

	
	
}
