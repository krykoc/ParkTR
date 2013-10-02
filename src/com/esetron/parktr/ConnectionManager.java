package com.esetron.parktr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;

public class ConnectionManager {
	
	public static void connectServer(String imei) {
		
	}
	
	public static HttpGet authorisedHttpGet(String url, String imei, String sessionKey){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Authorization", Base64.encodeToString((imei + "1:1" + sessionKey).getBytes(),Base64.URL_SAFE|Base64.NO_WRAP));
        httpGet.setHeader("SESSION_KEY", sessionKey);
        httpGet.setHeader("DEVICE_ID", imei);
        return httpGet;
	}
	
	public static HttpPost authorisedHttpPost(String url, String imei, String sessionKey){
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", Base64.encodeToString((imei + "1:1" + sessionKey).getBytes(),Base64.DEFAULT));
        httpPost.setHeader("SESSION_KEY", sessionKey);
        httpPost.setHeader("DEVICE_ID", imei);
        return httpPost;
	}

	/**
	 * 
	 * @param location
	 * @param zoomLevel
	 * @param imei
	 * @param sessionKey
	 */
	public static void getParkingLots(LatLng location, int zoomLevel, String imei, String sessionKey) {
		
		HttpClient httpClient = new DefaultHttpClient();
		
		String url = Commons.hostAct.getResources().getString(R.string.main_url);
		url += Commons.hostAct.getResources().getString(R.string.get_parking_lots);
		HttpGet request = authorisedHttpGet(url, imei, sessionKey);
		Commons.log("imei: "+imei);
		try {
			
			
			HttpResponse response = httpClient.execute(request);
			Commons.log("response: "+String.valueOf(response));
			JSONObject object = parseResponse(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), response.getEntity().getContent());
			Commons.log("json object: "+String.valueOf(object));
			JSONArray resultJsonArray = object.getJSONArray("response");
			
			Commons.log("result"+String.valueOf(resultJsonArray));
			//TODO BURDASIN UNUTMA 
			
			for (int i = 0; i < resultJsonArray.length(); i++) {
				ParkIcon parkIconObject = new ParkIcon();
				parkIconObject.setAvailableParkSize(resultJsonArray.getJSONObject(i).getInt("available_park_size"));
				parkIconObject.setParkingLotID(resultJsonArray.getJSONObject(i).getInt("id"));
				parkIconObject.setParkinglotName(resultJsonArray.getJSONObject(i).getString("name"));
				parkIconObject.setParkingLotLocation("ÇANKAYA / ANKARA");
				LatLng swapLatlng = new LatLng(resultJsonArray.getJSONObject(i).getDouble("latitude"), resultJsonArray.getJSONObject(i).getDouble("longitude"));
				parkIconObject.setLatLng(swapLatlng);
				if (Commons.parkingPointsMap.get(parkIconObject.getGarkingLotID()) != null) {
					parkIconObject.setPrevAvailableParkSize(Commons.parkingPoints.get(Commons.parkingPointsMap.get(parkIconObject.getGarkingLotID())).getPrevAvailableParkSize());
					parkIconObject.setMarker(Commons.parkingPoints.get(Commons.parkingPointsMap.get(parkIconObject.getGarkingLotID())).getMarker());
					parkIconObject.setBitmap(Commons.parkingPoints.get(Commons.parkingPointsMap.get(parkIconObject.getGarkingLotID())).getBitmap());
					parkIconObject.setIsShown(Commons.parkingPoints.get(Commons.parkingPointsMap.get(parkIconObject.getGarkingLotID())).getIsShown());
					Commons.parkingPoints.get(Commons.parkingPointsMap.get(parkIconObject.getGarkingLotID())).setObject(parkIconObject);
				} else {
					Commons.parkingPoints.add(parkIconObject);
					Commons.parkingPointsMap.put(parkIconObject.getGarkingLotID(), Commons.parkingPoints.size() - 1);
				}
			}
		} catch (Exception ex) {
			Commons.log("request :"+String.valueOf(request));
			Commons.log("zz getParkingLots ex :" + ex);
		
		}
	}
	
	public static void setParkOn(LatLng location, String imei, int parkSpaceId) {
		
	}
	
	public static void setParkOff(LatLng location, String imei, int parkSpaceId) {
		
	}
	
	private static JSONObject parseResponse(int responseCode, String responseText, InputStream inStr){
		JSONObject result;
    	try {
			result = new JSONObject("{'bool_success':"+false+",'response':'"+responseText+"','code':"+responseCode +"}");
		} catch (JSONException e) {
			Commons.log("JSON Exception:" + e);
			result = null;
		}
		try {
	        if (responseCode == 200)
	        {
				BufferedReader in = null;
				StringBuffer sb = new StringBuffer("");
				String line = null;
				in = new BufferedReader(new InputStreamReader(inStr));
				String NL = System.getProperty("line.separator");
		        while ((line = in.readLine()) != null) {
		              sb.append(line + NL);
		        }
	            try {
	            	String responseContent = sb.toString();

	            	if (responseContent.length()>0 && responseContent.substring(0,1).equals("[")){
	            		JSONArray object;
	            		if (responseContent.length() > 3)
	            			object = new JSONArray(responseContent);
	            		else
	            			object = new JSONArray();
	            		result.put("response",object);
	            	}
	            	else if (responseContent.substring(0,1).equals("{")){
	            		JSONObject object = new JSONObject(responseContent);
	            		result.put("response",object);
	            	}
	            	else{
	            		JSONObject object = new JSONObject();
	            		result.put("response",object);
	            	}
	            	result.put("bool_success", true);
	            	return result;
	            }
	            catch (Exception jsonEx) {
	            	Commons.log("aa Could not parse server response JSON : " + jsonEx);
	            }	            
	        }
	        else
	        {
	        	Commons.log("aa Server Response : " + responseCode + ":" + responseText);
	        }
	    } catch (ClientProtocolException e) {
	    	Commons.log("aa ClientProtocolException : " + e.toString());
	    } catch (IOException e) {
	    	Commons.log("aa IOException : " + e.toString());
	    }
		return result;		
	}	
}
