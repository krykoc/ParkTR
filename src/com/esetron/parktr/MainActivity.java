package com.esetron.parktr;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.esetron.parktr.Commons.MapLayers;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


@SuppressLint("NewApi")
public class MainActivity extends Activity implements OnDoubleTapListener, OnMarkerClickListener{

	private static  GoogleMap mapView;
	public static LocationManager locMan_locationManager_MainActivity;
	public static Location locNetwork;
	public static Location locGPS;
	public static boolean bool_isNETWORK_PROVIDED_MainActivity;
	public static locationListener locListener_MyLocationListenerForGPS_MainActivity = new locationListener();
	public RelativeLayout footerList;
	public View myMapLayout;
	private Context context;
	private Intent mainServiceIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize();
		
		
		footerList = (RelativeLayout) findViewById(R.id.footerListLayout);
		myMapLayout = (View) findViewById(R.id.mapViewID);
		footerList.setVisibility(View.GONE);
		context = this;
		Commons.hostAct = this;
		Commons.initialFunctions();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try{
		Commons.onPause();
		this.stopService(mainServiceIntent);
		Log.w("onPause","Service Stopped");
		Commons.isAppStarted = false;}
		catch (Exception e) {
			Log.w("onPause","Service Already Stopped");
		}
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		try{
		//context.stopService(mainServiceIntent);
		Commons.isAppStarted = false;}
		catch (Exception e) {
			Log.w("onDestroy","Service Already Stopped");
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (Commons.isAppStarted == false) {
			mainServiceIntent  = new Intent(this, LocalService.class);
			this.startService(mainServiceIntent);
			Commons.isAppStarted = true;
		}
	} 
	
	@Override
	protected void onResume() {
		super.onResume(); 
	}
	
	public void listParkButtonOnClicked(View V) {
		
	}
	
	public void mapLayersButtonOnClicked(View V) {
		switch (Commons.mapLayer) {
			case normal: 
				mapView.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				Commons.mapLayer = Commons.MapLayers.hybrid;
				break;
			case hybrid:
				mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				Commons.mapLayer = Commons.MapLayers.normal;
				break;
		}
	}
	
	public void myLocationsButtonOnClicked(View V) {
		Commons.centerMap = true;
		showOnMapFunction();
	}
	
	public void profileClicked(View V) {	
		
		if(footerList.getVisibility()== View.VISIBLE){
			footerList.setVisibility(View.INVISIBLE);
			myMapLayout.setTranslationY(0);}
			
			else{
			footerList.setVisibility(View.VISIBLE);
			myMapLayout.setTranslationY(-850);}
		
	}
	
	public void settingsButtonOnClicked(View V) {
		
	}
	
	
	/**
	 * 
	 */
	@SuppressLint("NewApi")
	private void initialize() {
		
	    try {locMan_locationManager_MainActivity = (LocationManager) getSystemService(Context.LOCATION_SERVICE);}
        catch (Exception e){ Commons.log("locationManagerGetService Exception : " + e); }
        
	    try { bool_isNETWORK_PROVIDED_MainActivity = locMan_locationManager_MainActivity.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}
        catch (Exception e) {Commons.log("isNETWORK_PROVIDED exception : " + e);}
	    
	    checkGPSStatus(false);
		
		mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapViewID)).getMap();
		
    	try {
        	mapView.getUiSettings().setZoomControlsEnabled(false);
        	mapView.getUiSettings().setScrollGesturesEnabled(true);
        	mapView.getUiSettings().setCompassEnabled(false);
        	mapView.getUiSettings().setZoomGesturesEnabled(true);
        	mapView.getUiSettings().setRotateGesturesEnabled(false);
        	mapView.getUiSettings().setTiltGesturesEnabled(false);
        	mapView.getUiSettings().setMyLocationButtonEnabled(false);
        	mapView.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        	Commons.mapLayer = MapLayers.normal;
        	
        	mapView.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
				}
			});
        	
        	
        	mapView.setOnCameraChangeListener(new OnCameraChangeListener() {
				
				@Override
				public void onCameraChange(CameraPosition position) {
					// TODO Auto-generated method stub
					if (Commons.mapViewZoomLevel != position.zoom)
						Commons.mapViewZoomLevel = position.zoom;
					
					//Commons.log("zoom level : " + position.zoom);
					
					if (Commons.centerMapLatLng == null) {
						Commons.centerMapLatLng = new LatLng(position.target.latitude, position.target.longitude);
						
					} else {
						if (Commons.centerMapLatLng.latitude != position.target.longitude || Commons.centerMapLatLng.longitude != position.target.longitude) {
							Commons.centerMapLatLng = new LatLng(position.target.latitude, position.target.longitude);
							Commons.centerMap = false;
						}
					}
					
					//Commons.log("target : " + position.target.longitude);
					//Commons.log("tilt : " + position.tilt);
					showOnMapFunction();
				}
			});
        	
        	mapView.setOnMapClickListener(new OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng arg0) {
					// TODO Auto-generated method stub
					Commons.log("map clicked");
				}
			});
        	
        	mapView.setOnMapLongClickListener(new OnMapLongClickListener() {
				
				@Override
				public void onMapLongClick(LatLng arg0) {
					Commons.log("map long clicked ");
				}
			});
        	
        	mapView.setOnMarkerClickListener(this);
        	
    	} catch (Exception e) {
			// TODO: handle exception
    		
		}  		
    	
    	showOnMapFunction();
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		Commons.log("zz onDoubleTab");
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Commons.log("zz onDoubleTabEvent");
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Commons.log("zz onSingleTabConfirmed");
		return false;
	}
	
	@Override
	 public boolean onMarkerClick(Marker marker) {
		boolean result = false;
		
		Commons.log("marker clicked : " + marker.getId());
		
		for (int i = 0; i < Commons.parkingPoints.size(); i++) {
			if (Commons.parkingPoints.get(i).getMarker().getId().equals(marker.getId()) && Commons.parkingPoints.get(i).getIsShown() == false) {
				Marker newMarker = marker;
				newMarker.remove();
				newMarker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(Commons.parkingPoints.get(i).getBitmap())));
				Commons.parkingPoints.get(i).setMarker(newMarker);
				Commons.parkingPoints.get(i).setIsShown(true);
				Commons.log("marker : true : " + i + " " + Commons.parkingPoints.get(i).getMarker().getId());
			} else if (Commons.parkingPoints.get(i).getIsShown() == true && !Commons.parkingPoints.get(i).getMarker().getId().equals(marker.getId())){
				Commons.log("non : " + Commons.parkingPoints.get(i).getMarker().getId());
					Marker newMarker = Commons.parkingPoints.get(i).getMarker();
					newMarker.remove();
					Commons.parkingPoints.get(i).setIsShown(false);
					addMarkerFunction(i);
					Commons.log("marker : false : " + i + " " + Commons.parkingPoints.get(i).getMarker().getId());
			} else {
				Commons.log("else : " + Commons.parkingPoints.get(i).getMarker().getId());
			}
		}
		
		return result;	
	}
	
	public static void checkGPSStatus(Boolean gpsEnabledJustNow) {
		//         
        try { Commons.bool_gps_enabled = locMan_locationManager_MainActivity.isProviderEnabled(LocationManager.GPS_PROVIDER);}
        catch (Exception e) {Commons.log("isGPS_PROVIDED exception : " + e);}
        
        if (Commons.bool_gps_enabled == false && !gpsEnabledJustNow)
        {
        	Commons.log("Location is Disabled!");
    		AlertDialog.Builder builder = new AlertDialog.Builder(Commons.hostAct);
    		
        	builder.setMessage(Commons.hostAct.getString(R.string.gps_enable_query));
        	
        	builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    			
    			public void onClick(DialogInterface dialog, int which) {
    				switch (which) {

    				case DialogInterface.BUTTON_POSITIVE:
    					try {
    							Commons.bool_gps_enabled = true;
    							Commons.hostAct.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	    	                    checkGPSStatus(true);//This will update the animation accordingly ;)
    	        			} catch (Exception e) {
    	        				Commons.log("aa intent gps exception " + e.getMessage());
    	        			}
    					break;
    				}
    			}
    		});
        	
        	builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
    			
    			public void onClick(DialogInterface dialog, int which) {
    				Commons.toast(Commons.hostAct.getString(R.string.gps_disabled));
    			}
    		}).show();

        	Commons.log("LocationManager will be prepared");
        }
        MainActivity.locMan_locationManager_MainActivity.requestLocationUpdates(LocationManager.GPS_PROVIDER, Commons.INT_MINIMUM_TIME_BETWEEN_UPDATES_MAINACTIVITY, Commons.INT_MINIMUM_DISTANCE_CHANGE_FOR_UPDATES_MAINACTIVITY, locListener_MyLocationListenerForGPS_MainActivity);
        
        try { 
        	Commons.bool_gps_enabled = MainActivity.locMan_locationManager_MainActivity.isProviderEnabled(LocationManager.GPS_PROVIDER);
        	
        	if (Commons.bool_gps_enabled){
        		
        		
        	}
        	else{
        		System.exit(0);
        	}
        }
        catch (Exception e){};
	}
	
	public static void showOnMapFunction() {
    	locNetwork = locMan_locationManager_MainActivity.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	locGPS = locMan_locationManager_MainActivity.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if (locGPS != null && (locGPS.getTime() > locNetwork.getTime())) {
    		showOnMap(locGPS.getLatitude(), locGPS.getLongitude());
    	} else if (locNetwork != null) { 
    		showOnMap(locNetwork.getLatitude(), locNetwork.getLongitude());
    	}
	}
	
	public static void showOnMap(double latitude, double longitude) {
		if (latitude != 0 && longitude != 0) {
			
			final LatLng latLngPointCenter = new LatLng(latitude, longitude);
			
			if (Commons.parkingPoints.size() != 0) {
				for (int i = 0; i < Commons.parkingPoints.size(); i++) {
					if (Commons.parkingPoints.get(i).getPrevAvailableParkSize() != Commons.parkingPoints.get(i).getAvailableParkSize()) {
						
						addMarkerFunction(i);
					}
				}
			}
			
			if (Commons.centerMap == true) {
				mapView.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngPointCenter, Commons.mapViewZoomLevel));
			}
			mapView.animateCamera(CameraUpdateFactory.zoomTo(Commons.mapViewZoomLevel), 4000, null);
		}
	}
	
	public static void addMarkerFunction(int i) {
		
		if (Commons.parkingPoints.get(i).getAvailableParkSize() > 29) {
			Bitmap textBitmap = Commons.drawTextToBitmap(Commons.hostAct, (R.drawable.parktr_iossapp_greeninfoparklotmarker), Commons.parkingPoints.get(i).getParkinglotName(), Commons.parkingPoints.get(i).getAvailableParkSize());
			Marker marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(textBitmap)));
			Commons.parkingPoints.get(i).setBitmap(textBitmap);
			if (Commons.parkingPoints.get(i).getIsShown() == false) {
				marker.remove();
				marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.parktr_iossapp_greenparklotmarker)));
			}
			Commons.parkingPoints.get(i).setMarker(marker);
		}
		else if (Commons.parkingPoints.get(i).getAvailableParkSize() <  29 && Commons.parkingPoints.get(i).getAvailableParkSize() > 4) {
			Bitmap textBitmap = Commons.drawTextToBitmap(Commons.hostAct, (R.drawable.parktr_iossapp_yellowinfoparklotmarker), Commons.parkingPoints.get(i).getParkinglotName(), Commons.parkingPoints.get(i).getAvailableParkSize());
			Marker marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(textBitmap)));
			Commons.parkingPoints.get(i).setBitmap(textBitmap);
			if (Commons.parkingPoints.get(i).getIsShown() == false) {
				marker.remove();
				marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.parktr_iossapp_yellowparklotmarker)));
			}
			Commons.parkingPoints.get(i).setMarker(marker);
		}
		else if (Commons.parkingPoints.get(i).getAvailableParkSize() < 5) {
			Bitmap textBitmap = Commons.drawTextToBitmap(Commons.hostAct, (R.drawable.parktr_iossapp_redinfoparklotmarker), Commons.parkingPoints.get(i).getParkinglotName(), Commons.parkingPoints.get(i).getAvailableParkSize());
			Marker marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(textBitmap)));
			Commons.parkingPoints.get(i).setBitmap(textBitmap);
			if (Commons.parkingPoints.get(i).getIsShown() == false) {
				marker.remove();
				marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.parktr_iossapp_redparklotmarker)));
			}
			Commons.parkingPoints.get(i).setMarker(marker);
		}
		Commons.parkingPoints.get(i).setPrevAvailableParkSize(Commons.parkingPoints.get(i).getAvailableParkSize());		
	}
	
	
	
		
		
		
	
}
