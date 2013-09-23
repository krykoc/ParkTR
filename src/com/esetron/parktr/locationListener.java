package com.esetron.parktr;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * 
 * @author ilkermoral
 *
 */
public class locationListener implements LocationListener{
	
	public locationListener(){}

	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		try {
			Commons.log("zz yy location latitude : " + location.getLatitude() + " longitude :" + location.getLongitude());
		}
		catch(Exception ex) {
			
		}
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	

}
