package com.esetron.parktr;


import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.esetron.parktr.Commons.MapLayers;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

import pl.mg6.android.maps.extensions.Circle;
import pl.mg6.android.maps.extensions.ClusteringSettings;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.BitmapFactory.Options;
import android.graphics.Interpolator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import pl.mg6.android.maps.extensions.GoogleMap;
import pl.mg6.android.maps.extensions.GoogleMap.InfoWindowAdapter;
import pl.mg6.android.maps.extensions.GoogleMap.OnCameraChangeListener;
import pl.mg6.android.maps.extensions.GoogleMap.OnInfoWindowClickListener;
import pl.mg6.android.maps.extensions.GoogleMap.OnMapClickListener;
import pl.mg6.android.maps.extensions.GoogleMap.OnMapLongClickListener;
import pl.mg6.android.maps.extensions.GoogleMap.OnMarkerClickListener;

import pl.mg6.android.maps.extensions.Marker;
import pl.mg6.android.maps.extensions.MarkerOptions;
import pl.mg6.android.maps.extensions.SupportMapFragment;


@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements OnDoubleTapListener, OnMarkerClickListener{

	private static GoogleMap mapView;

	public static LocationManager locMan_locationManager_MainActivity;
	public static Location locNetwork;
	public static Location locGPS;
	public static boolean bool_isNETWORK_PROVIDED_MainActivity;
	public static locationListener locListener_MyLocationListenerForGPS_MainActivity = new locationListener();
	public RelativeLayout footerList;
	public RelativeLayout profileList;
	public View myMapLayout;
	private TextView infoWindowUpperText;
	private TextView infoWindowBottomText;
	private Typeface tfNormal;
	private Typeface tfLight;
	private Typeface tfBold;
	private Marker selectedMarker=null;
	private View infoWindow;
    private TextView infoTitle;
    private TextView infoSnippet;
    private Button addButton;
    private Button naviButton;
    private OnInfoWindowElemTouchListener addButtonListener;
    private OnInfoWindowElemTouchListener naviButtonListener;
    private android.view.Display display;
    private RelativeLayout infoWindowLayout;
    private RelativeLayout parktr_bottomRelativeLayout;
    private Animation animUp;
    private Animation animDown;
    private Animation mapAnimUp;
    private Animation mapAnimDown;
    private Button parkTopIcon;
    private Button searchTopIcon;
    private int mapSize;
	private static final double[] CLUSTER_SIZES = new double[] { 180, 160, 144, 120, 96 };
	private MutableData[] dataArray = { new MutableData(6, new LatLng(-50, 0)), new MutableData(28, new LatLng(-52, 1)),
			new MutableData(496, new LatLng(-51, -2)), };
	private Handler handler = new Handler();
	private Runnable dataUpdater = new Runnable() {

		@Override
		public void run() {
			for (MutableData data : dataArray) {
				data.value = 7 + 3 * data.value;
			}
			onDataUpdate();
			handler.postDelayed(this, 1000);
		}
	};

	
	public static final String EXTRA_OPTIONS = "options";
	private static final String KEY_CAMERA_POSITION = "camera position";
	private static final long DELAY_CLUSTERING_SPINNER_MILLIS = 200l;
	
	private static Context context;
	
	private Intent mainServiceIntent;
	
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initialize();
		
		
		tfLight = Typeface.createFromAsset(getAssets(),"fonts/ss_light.ttf");
		tfNormal = Typeface.createFromAsset(getAssets(),"fonts/ss_normal.ttf");
		tfBold = Typeface.createFromAsset(getAssets(),"fonts/ss_bold.ttf");
    	 
		animUp = AnimationUtils.loadAnimation(this, R.anim.anim_up);
		animDown = AnimationUtils.loadAnimation(this, R.anim.anim_down);
		mapAnimUp = AnimationUtils.loadAnimation(this, R.anim.map_anim_up);
		mapAnimDown = AnimationUtils.loadAnimation(this, R.anim.map_anim_down);
		parkTopIcon = (Button) findViewById(R.id.parktr_icon);
		searchTopIcon = (Button) findViewById(R.id.search_button);
		
		display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
    	
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)findViewById(R.id.mainFrameID);
    	
		footerList = (RelativeLayout) findViewById(R.id.footerListLayout);
		profileList = (RelativeLayout) findViewById(R.id.profileListLayout);
		parktr_bottomRelativeLayout = (RelativeLayout) findViewById(R.id.parktr_bottomRelativeLayout);
		myMapLayout = (View) findViewById(R.id.mapViewID);
		footerList.setVisibility(View.GONE);
		profileList.setVisibility(View.GONE);
		context = this;
		Commons.hostAct = this;
		Commons.initialFunctions();
		
		mapWrapperLayout.init(mapView, getPixelsFromDp(this, 39 + 20));
		
//		ContextThemeWrapper cw = new ContextThemeWrapper(
//                getApplicationContext(), R.style.Transparent);
//    	LayoutInflater inflater = (LayoutInflater) cw
//                .getSystemService(LAYOUT_INFLATER_SERVICE);
		
		this.infoWindow =  (ViewGroup)getLayoutInflater().inflate(R.layout.custom_info_window, null);
		infoWindowLayout = (RelativeLayout) infoWindow.findViewById(R.id.imageview_comment);
        this.infoTitle = (TextView)infoWindow.findViewById(R.id.infoWindowUpperTV);
        this.infoSnippet = (TextView)infoWindow.findViewById(R.id.infoWindowBottomTV);
        this.addButton = (Button)infoWindow.findViewById(R.id.infoWindowAddBtn);
        this.naviButton = (Button)infoWindow.findViewById(R.id.infoWindowNavigateBtn);
        
        this.infoTitle.setTypeface(tfBold);
        this.infoSnippet.setTypeface(tfLight);
        
       
        parkTopIcon.setOnClickListener(new Button.OnClickListener() {  
        public void onClick(View v)
            {
              String tagList = footerList.getTag().toString();
              String tagProfile = footerList.getTag().toString();
              
        	//	if(tagList.equals("VISIBLE") && tagProfile.equals("VISIBLE")){
        			
        			if(footerList.getVisibility() == View.VISIBLE )
        				listParkButtonOnClicked(null);
        			else if(profileList.getVisibility() == View.VISIBLE )
        				profileClicked(null);
        			
        		
        			
        		
        	
            }
         });
		
        
        this.addButtonListener = new OnInfoWindowElemTouchListener(addButton,
                getResources().getDrawable(R.drawable.add_favorites),
                getResources().getDrawable(R.drawable.add)) 
        {

			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				
			Commons.toast("Add to Favorites");
			
			}
           

			
        }; 
        
        this.naviButtonListener = new OnInfoWindowElemTouchListener(naviButton,
                getResources().getDrawable(R.drawable.navigate),
                getResources().getDrawable(R.drawable.navi)) 
        {

			@Override
			protected void onClickConfirmed(View v, Marker marker) {
				
		     Commons.toast("Navigate");
			
			}
           

			
        }; 
        
        
        this.addButton.setOnTouchListener(addButtonListener);
        this.naviButton.setOnTouchListener(naviButtonListener);

        mapView.setInfoWindowAdapter(new InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
            	 
            	String[] tokens= parseSnippet(marker.getSnippet());

            	 infoTitle.setText(tokens[0]);        	
                 infoSnippet.setText(tokens[1]);
               
                 addButtonListener.setMarker(marker);
                 naviButtonListener.setMarker(marker);
                 mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                 return infoWindow;
                
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
            	
            	return null;
               
            }
        });

		
	

		mapView.setClustering(new ClusteringSettings().clusterOptionsProvider(new DemoClusterOptionsProvider(getResources())).addMarkersDynamically(true));

//		mapView.setInfoWindowAdapter(new InfoWindowAdapter() {
//
//			
//			private final View window = getLayoutInflater().inflate(
//                    R.layout.custom_info_window, null);
//			
//			
//			private TextView tv;
//			{
//				tv = new TextView(MainActivity.this);
//				tv.setTextColor(Color.WHITE);
//				tv.setBackgroundColor(Color.GRAY);
//			}
//			private ListView list;
//			{
//				
//				list = new ListView(MainActivity.this);
//				
//			}
//			
//			private Collator collator = Collator.getInstance();
//			private Comparator<Marker> comparator = new Comparator<Marker>() {
//				public int compare(Marker lhs, Marker rhs) {
//					String leftTitle = lhs.getTitle();
//					String rightTitle = rhs.getTitle();
//					if (leftTitle == null && rightTitle == null) {
//						return 0;
//					}
//					if (leftTitle == null) {
//						return 1;
//					}
//					if (rightTitle == null) {
//						return -1;
//					}
//					return collator.compare(leftTitle, rightTitle);
//				}
//			};
//
//			@Override
//			public View getInfoWindow(Marker marker) {
//				Log.i("184","getInfoWindow");
//				return window;
//			}
//
//			@Override
//			public View getInfoContents(Marker marker) {
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//		
//
//			@Override
//			public View getInfoContents(Marker marker) {
//				
//				
//				Log.i("191","getInfoContents");
//				
//				
//				View v = getLayoutInflater().inflate(R.layout.custom_info_window, null);
//
//	            // Getting the position from the marker
//	            LatLng latLng = marker.getPosition();
//
//	            // Getting reference to the TextView to set latitude
//	            Button cmiBtnLeft = (Button) v.findViewById(R.id.cmi_btn_left);
//
//	            // Getting reference to the TextView to set longitude
//	            Button cmiBtnRight = (Button) v.findViewById(R.id.cmi_btn_right);
//				
//	            return v;
//				
//			}
//				if (marker.isCluster()) {
//					List<Marker> markers = marker.getMarkers();
//					int i = 0;
//					String text = "";
//					String[] wordlist = new String[markers.size()];
//					//String[] wordlist = new String[] { "a", "b", "c","a", "b", "c","a", "b", "c"};
//					while (i < 4 && markers.size() > 0) {
//						Marker m = Collections.min(markers, comparator);
//						String title = m.getTitle();
//						if (title == null) {
//							break;
//						}
//						
//						wordlist[i]=title;
//						
//						
////						text += title + "\n";
//						markers.remove(m);
//						i++;
//					}
//					if (text.length() == 0) {
//						text = "Markers with mutable data";
//					} else if (markers.size() > 0) {
//						text += "and " + markers.size() + " more...";
//					} else {
//						text = text.substring(0, text.length() - 1);
//					}
//					//tv.setText(text);
//					
//					
//					 
//				        list.setAdapter(new MyAdapter(context, wordlist));
//			        //setContentView(list);
//					
//					return list;
//				} else {
//					Object data = marker.getData();
//					if (data instanceof MutableData) {
//						MutableData mutableData = (MutableData) data;
//						//tv.setText("Value: " + mutableData.value);
//						return list;
//					}
//				}
//
//			
//			
//		});
//
//		
//		mapView.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
//
//			@Override
//			public void onInfoWindowClick(Marker marker) {
//				if (marker.isCluster()) {
//					List<Marker> markers = marker.getMarkers();
//					Builder builder = LatLngBounds.builder();
//					for (Marker m : markers) {
//						builder.include(m.getPosition());
//					}
//					LatLngBounds bounds = builder.build();
//					mapView.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, getResources().getDimensionPixelSize(R.dimen.cluster_text_size_large)));
//				}
//			}
//		});

	   
		BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
		for (MutableData data : dataArray) {
			mapView.addMarker(new MarkerOptions().position(data.position).icon(icon).data(data));
		}

		setUpClusteringViews();
	
		
        
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		Commons.onPause();		
		
		
	}
	
	@Override
	protected void onDestroy() {
		
		
		super.onDestroy();
		Commons.isAppStarted = false;
		Commons.log("****SERVICE "+ mainServiceIntent);
		if(mainServiceIntent != null){
		     this.stopService(mainServiceIntent);
		     mainServiceIntent = null;
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
		handler.post(dataUpdater);
	}
	
	
	
	public void listParkButtonOnClicked(View V) {
		
		
		if(profileList.getVisibility()== View.VISIBLE)
			profileClicked(null);
			
		
		
		LayoutParams params = footerList.getLayoutParams();
		if(footerList.getVisibility()== View.VISIBLE){
			
			footerList.setVisibility(View.GONE);
			footerList.setTag("GONE");
			myMapLayout.setTranslationY(0);
			parktr_bottomRelativeLayout.setVisibility(View.VISIBLE);
			parkTopIcon.setBackgroundResource(R.drawable.parktr_top_icon);
	    	searchTopIcon.setBackgroundResource(R.drawable.search_top_icon);
		}
			else{
			
			params.height = (int) (display.getHeight()*0.7);
			Commons.log(""+params.height);
			mapSize = (params.height)*(-1);
	    	footerList.setVisibility(View.VISIBLE);
	    	footerList.setTag("VISIBLE");
	    	parktr_bottomRelativeLayout.setVisibility(View.GONE);
	    	myMapLayout.setTranslationY(mapSize);
	    	parkTopIcon.setBackgroundResource(R.drawable.back_button);
	    	searchTopIcon.setBackgroundResource(R.drawable.searchbox_bos);
	    	//footerList.setVisibility(View.VISIBLE);
			//footerList.startAnimation(animUp);
			}
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
	
	@SuppressWarnings("deprecation")
	public void profileClicked(View V) {	
		if(footerList.getVisibility()== View.VISIBLE)
			listParkButtonOnClicked(null);
	
		LayoutParams params = profileList.getLayoutParams();
		if(profileList.getVisibility()== View.VISIBLE){
			
			
		
			profileList.setVisibility(View.GONE);
			profileList.setTag("GONE");
			myMapLayout.setTranslationY(0);
			parktr_bottomRelativeLayout.setVisibility(View.VISIBLE);
			parkTopIcon.setBackgroundResource(R.drawable.parktr_top_icon);
	    	searchTopIcon.setBackgroundResource(R.drawable.search_top_icon);
		}
			else{
			
			
			params.height = (int) (display.getHeight()*0.7);
			Commons.log(""+params.height);
			mapSize = (params.height)*(-1);
	    	profileList.setVisibility(View.VISIBLE);
	    	profileList.setTag("VISIBLE");
	    	parktr_bottomRelativeLayout.setVisibility(View.GONE);
	    	myMapLayout.setTranslationY(mapSize);
	    	parkTopIcon.setBackgroundResource(R.drawable.back_button);
	    	searchTopIcon.setBackgroundResource(R.drawable.searchbox_bos);
			
			}
		
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
		
	    FragmentManager fm = getSupportFragmentManager();
		SupportMapFragment f = (SupportMapFragment) fm.findFragmentById(R.id.mapViewID);
		mapView = f.getExtendedMap();
		
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

	private SupportMapFragment findFragmentById(int mapviewid) {
		// TODO Auto-generated method stub
		return null;
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
		
		if(!marker.getId().equals("cm")){
			
			
			for (int i = 0; i < Commons.parkingPoints.size(); i++) {
				if (Commons.parkingPoints.get(i).getMarker().getId().equals(marker.getId()) && !Commons.parkingPoints.get(i).getIsShown()) {
					Commons.log("if");
					infoWindowLayout.setBackgroundResource(drawInfoWindow(Commons.parkingPoints.get(i).getAvailableParkSize()));
					marker.showInfoWindow();
					selectedMarker=marker;
					Commons.parkingPoints.get(i).setIsShown(true);
					com.esetron.parktr.MapWrapperLayout mapContainer = (com.esetron.parktr.MapWrapperLayout) findViewById(R.id.mainFrameID);
				    int container_height = mapContainer.getHeight();
				    Projection projection = mapView.getProjection();
				    LatLng markerLatLng = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
				    Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
				    Point pointHalfScreenAbove = new Point(markerScreenPosition.x, markerScreenPosition.y + (container_height/6));
				    LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
				    CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
				    mapView.animateCamera(center);
					
			
				}
				else if (Commons.parkingPoints.get(i).getMarker().getId().equals(marker.getId()) && Commons.parkingPoints.get(i).getIsShown()){
					
					Commons.log("else");
					selectedMarker=null;
					marker.hideInfoWindow();	
					Commons.parkingPoints.get(i).setIsShown(false);
			
					} 
				
				else 		
					{
					
					Commons.parkingPoints.get(i).setIsShown(false);
					
					}
			}
			
		
		}
		else{
			//setMarkersToDefault();
			Commons.log("cluster+");
			
//			
//			List<Marker> clusterList = new ArrayList<Marker>();
//			int singleClusterGroupCount=0;
//			marker.showInfoWindow();
//			clusterList = marker.getMarkers();
//			for(int k=0;k<clusterList.size();k++){
//							
//			singleClusterGroupCount+=Commons.parkingPoints.get(k).getAvailableParkSize();
//			Commons.log("else : " + singleClusterGroupCount);}
		
		}
	
		
		return true;	
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
		Log.i("571","showOnMapFunction");
    	locNetwork = locMan_locationManager_MainActivity.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	locGPS = locMan_locationManager_MainActivity.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if (locGPS != null && (locGPS.getTime() > locNetwork.getTime())) {
    		showOnMap(locGPS.getLatitude(), locGPS.getLongitude());
    	} else if (locNetwork != null) { 
    		showOnMap(locNetwork.getLatitude(), locNetwork.getLongitude());
    	}
	}
	
	public static void showOnMap(double latitude, double longitude) {
		Log.i("582","showOnMap");
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
			Log.i("582","animateCamera");
		}
	}
	
	public static void addMarkerFunction(int i) {
		Log.i("582","addMarkerFunction");
		
		Marker marker=null;
		
		if(!Commons.parkingPoints.get(i).getIsShown()){
		
			int choosenDrawing = drawMarkerIcon(Commons.parkingPoints.get(i).getAvailableParkSize());
			
			Bitmap bm = Commons.drawTextToBitmap(Commons.hostAct,choosenDrawing,Commons.parkingPoints.get(i).getAvailableParkSize());
			marker = mapView.addMarker(new MarkerOptions().position(Commons.parkingPoints.get(i).getLatLng()).icon(BitmapDescriptorFactory.fromBitmap(bm)));
			//marker.animatePosition(Commons.parkingPoints.get(i).getLatLng());
			
		
		}

			marker.setTitle(String.valueOf(Commons.parkingPoints.get(i).getAvailableParkSize()));
			marker.setSnippet(Commons.parkingPoints.get(i).getParkinglotName()+"--"+Commons.parkingPoints.get(i).getParkingLotLocation());
		
			Commons.parkingPoints.get(i).setMarker(marker);
			Commons.parkingPoints.get(i).setPrevAvailableParkSize(Commons.parkingPoints.get(i).getAvailableParkSize());		
		}
		
		
		
	
	
	private void onDataUpdate() {
		Marker m = mapView.getMarkerShowingInfoWindow();
		if (m != null && !m.isCluster() && m.getData() instanceof MutableData) {
			m.showInfoWindow();
		}
	}

	

	private void setUpClusteringViews() {
		CheckBox clusterCheckbox = (CheckBox) findViewById(R.id.checkbox_cluster);
		final SeekBar clusterSizeSeekbar = (SeekBar) findViewById(R.id.seekbar_cluster_size);
		clusterCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				clusterSizeSeekbar.setEnabled(isChecked);

				updateClustering(clusterSizeSeekbar.getProgress(), isChecked);
			}
		});
		clusterSizeSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				updateClustering(progress, true);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	void updateClustering(int clusterSizeIndex, boolean enabled) {
		ClusteringSettings clusteringSettings = new ClusteringSettings();
		clusteringSettings.addMarkersDynamically(true);

		if (enabled) {
			clusteringSettings.clusterOptionsProvider(new DemoClusterOptionsProvider(getResources()));

			double clusterSize = CLUSTER_SIZES[clusterSizeIndex];
			clusteringSettings.clusterSize(clusterSize);
		} else {
			clusteringSettings.enabled(false);
		}
		mapView.setClustering(clusteringSettings);
	}

	private static class MutableData {

		private int value;

		private LatLng position;

		public MutableData(int value, LatLng position) {
			this.value = value;
			this.position = position;
		}
	}
	
	 private class MyAdapter extends ArrayAdapter<String> {

	        public MyAdapter(Context context, String[] strings) {
	            super(context, -1, -1, strings);
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {

	        	
	            LinearLayout listLayout = new LinearLayout(MainActivity.this);
	            listLayout.setLayoutParams(new AbsListView.LayoutParams(
	                    50,
	                    AbsListView.LayoutParams.WRAP_CONTENT));
//	            listLayout.bringToFront();
//	            listLayout.setLayoutParams(new AbsListView.LayoutParams(
//	                    40,40));
	           // listLayout.setBackgroundColor(Color.GRAY);
	            listLayout.setId(5000);

	            TextView listText = new TextView(MainActivity.this);
	            listText.setId(5001);

	            listLayout.addView(listText);

	            listText.setText(super.getItem(position));

	            return listLayout;
	        }
	    }
	

	private void setMarkersToDefault(){
		 
		if(!(selectedMarker==null)){
			
			int choosenDrawingColor;
			
			int availableParkLot = Integer.parseInt(selectedMarker.getTitle());
			
			if (availableParkLot < 5){
				
				choosenDrawingColor = R.drawable.new_red_closed;
				
				}
			else if (availableParkLot > 4 && availableParkLot<29){
				choosenDrawingColor = R.drawable.new_yellow_closed;
				}
			else{
				choosenDrawingColor = R.drawable.new_green_closed;
				}
			
			Bitmap bm = Commons.drawTextToBitmap(context,choosenDrawingColor,availableParkLot);
			selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bm));
		}
		
		
	 }
	 
   public void NavigateBtnOnClicked(View V){
	   
	   Toast.makeText(context, "Navigate", Toast.LENGTH_SHORT).show();
   }
   
   public void AddBtnOnClicked(View V){
	  
	   Toast.makeText(context, "Add", Toast.LENGTH_SHORT).show();
	   
   }
   
   public static int getPixelsFromDp(Context context, float dp) {
       final float scale = context.getResources().getDisplayMetrics().density;
       return (int)(dp * scale + 0.5f);
   }
   
   public String[] parseSnippet(String snippet){
	   
	  
	   String[] tokens = snippet.split("--");

	  return tokens;
   }
   
   public int drawInfoWindow(int size){
	
	   int drawingColor;
	   
	   if (size < 5)
		   drawingColor = R.drawable.new_red_info;
			
		else if (size > 4 && size <29)
			drawingColor = R.drawable.new_yellow_info;
			
		else
			drawingColor = R.drawable.new_green_info;
			
		
	   return drawingColor;
	   
   }
   public static int drawMarkerIcon(int size){
		
	   int drawingColor;
	   
	   if (size < 5)
		
			drawingColor = R.drawable.new_red_open;
		else if (size > 4 && size <29)
			
			drawingColor = R.drawable.new_yellow_open;
		else
		
			drawingColor = R.drawable.new_green_open;
		
	   return drawingColor;
	   
   }
   
  
	
}
