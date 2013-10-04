package com.esetron.parktr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;



import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class Commons {

	public static Activity hostAct = null;
	public static Parktr appObject = new Parktr();
	public enum MapLayers {satellite, hybrid, normal, none, terrain}
	public static MapLayers mapLayer;
	public static boolean isAppStarted;
	
	public static final int INT_MINIMUM_TIME_BETWEEN_UPDATES_MAINACTIVITY = 1000; //millisecconds
	public static final int INT_MINIMUM_DISTANCE_CHANGE_FOR_UPDATES_MAINACTIVITY = 1; //meters
	
	public static boolean bool_gps_enabled;
	public static String imei;
	public static float mapViewZoomLevel = (float)14.0;
	public static ArrayList<ParkIcon> parkingPoints = new ArrayList<ParkIcon>();
	public static boolean centerMap = true;
	public static boolean bottomMap = false;
	public static LatLng centerMapLatLng;


	public static Map<Integer, Integer> parkingPointsMap = new HashMap<Integer, Integer>();
	
	public static void initialFunctions() {
		TelephonyManager tm = (TelephonyManager) hostAct.getSystemService(Context.TELEPHONY_SERVICE);
		
		appObject.setIMEI(tm.getDeviceId());
		imei = appObject.getIMEI();
		
		
		if(imei==null){
			
			
			WifiManager manager = (WifiManager) hostAct.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = manager.getConnectionInfo();
			String address = info.getMacAddress();
			imei = address;
		}
	}
	
	public static void onResume() {
		MainActivity.checkGPSStatus(false);
		MainActivity.locMan_locationManager_MainActivity.requestLocationUpdates(LocationManager.GPS_PROVIDER, INT_MINIMUM_TIME_BETWEEN_UPDATES_MAINACTIVITY, INT_MINIMUM_DISTANCE_CHANGE_FOR_UPDATES_MAINACTIVITY, MainActivity.locListener_MyLocationListenerForGPS_MainActivity);
	}
	
	public static void onPause() {
		MainActivity.locMan_locationManager_MainActivity.removeUpdates(MainActivity.locListener_MyLocationListenerForGPS_MainActivity);
		MainActivity.locMan_locationManager_MainActivity.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INT_MINIMUM_TIME_BETWEEN_UPDATES_MAINACTIVITY, INT_MINIMUM_DISTANCE_CHANGE_FOR_UPDATES_MAINACTIVITY, MainActivity.locListener_MyLocationListenerForGPS_MainActivity);
		Commons.isAppStarted = false;
	}
	
	public static void log(String input) {
		Log.i("ParkTR", input);
	}
	
	public static void toast(String title){
		if (hostAct != null)
			Toast.makeText(hostAct, title,Toast.LENGTH_SHORT).show();
	}
	public static void toast(String title, Context context){
		Toast.makeText(context, title,Toast.LENGTH_SHORT).show();
	}
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	

	/**
	 * 
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @return
	 */
	public static Bitmap drawTextToBitmap(Context gContext, int gResId, int count) {
		
	  Resources resources = gContext.getResources();
	  float scale = resources.getDisplayMetrics().density;
	  Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
	 
	  android.graphics.Bitmap.Config bitmapConfig =
	      bitmap.getConfig();
	  // set default bitmap config if none
	  if(bitmapConfig == null) {
	    bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
	  }
	  // resource bitmaps are imutable, 
	  // so we need to convert it to mutable one
	  bitmap = bitmap.copy(bitmapConfig, true);
	 
	  Canvas canvas = new Canvas(bitmap);
	  // new antialised Paint
//	  Paint paintWhere = new Paint(Paint.ANTI_ALIAS_FLAG);
//	  // text color - #3D3D3D
//	  paintWhere.setColor(Color.WHITE);
//	  // text size in pixels
//	  paintWhere.setTextSize((int) (20 * scale));
//	  paintWhere.setTextAlign(Align.CENTER);
//	  // text shadow
//	  //paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
//	 
//	  // draw text to the Canvas center
	  
	 // canvas.drawText(gText, x * scale, y * scale, paintWhere);
	  
	  String text = String.valueOf(count);
	  Paint paintCount = new Paint(Paint.ANTI_ALIAS_FLAG);
	  Rect bounds = new Rect();
	  paintCount.getTextBounds(text, 0, text.length(), bounds);
	  float x = bitmap.getWidth() / 2.0f;
	  float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;
	  paintCount.setColor(Color.WHITE);
	  paintCount.setTextSize((int)(25 * scale));
	  paintCount.setTextAlign(Align.CENTER);
	  
	  paintCount.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
	 
	  canvas.drawText(Integer.toString(count),x,y, paintCount);
	 
	  return bitmap;
	}


}
