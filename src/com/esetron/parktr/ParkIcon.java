package com.esetron.parktr;

import pl.mg6.android.maps.extensions.Marker;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;


public class ParkIcon {

	private Marker marker;
	private Bitmap bitmap;
	private int parkingLotID;
	private int parkingLotSize;
	private int availableParkSize;
	private int prevAvailableParkSize = 0;
	private LatLng latLng;
	private boolean isShown = false;
	private String parkinglotName;
	private String parkinglotLocation;
	
	public ParkIcon() {
		
	}
	
	public ParkIcon(Marker marker) {
		this.marker = marker;
	}
	
	public ParkIcon(Marker marker, Bitmap bitmap) {
		this.marker = marker;
		this.bitmap = bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public Bitmap getBitmap() {
		return this.bitmap;
	}
	
	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
	public Marker getMarker() {
		return this.marker;
	}
	
	public void setParkingLotID(int parkingLotID) {
		this.parkingLotID = parkingLotID;
	}
	
	public void setParkingLotLocation(String parkingLotLocation) {
		this.parkinglotLocation = parkingLotLocation;
	}
	
	public String getParkingLotLocation() {
		return this.parkinglotLocation;
	}
	
	public int getGarkingLotID() {
		return this.parkingLotID;
	}
	
	public void setParkingLotSize(int parkingLotSize) {
		this.parkingLotSize = parkingLotSize;
	}
	
	public int getParkingLotSize() {
		return this.parkingLotSize;
	}
	
	public void setAvailableParkSize(int availableParkSize) {
		this.availableParkSize = availableParkSize;
	}
	
	public int getAvailableParkSize() {
		return this.availableParkSize;
	}
	
	public void setLatLng(LatLng latLng) {
		this.latLng = latLng;
	}
	
	public LatLng getLatLng() {
		return this.latLng;
	}
	
	public void setIsShown(boolean isShown) {
		this.isShown = isShown;
	}
	
	public boolean getIsShown() {
		return this.isShown;
	}
	
	public void setPrevAvailableParkSize(int prevAvailableParkSize) {
		this.prevAvailableParkSize = prevAvailableParkSize;
	}
	
	public int getPrevAvailableParkSize() {
		return this.prevAvailableParkSize;
	}
	
	public void setParkinglotName(String parkinglotName) {
		this.parkinglotName = parkinglotName;
	}
	
	public String getParkinglotName() {
		return this.parkinglotName;
	}
	
	
	public void setObject(ParkIcon parkIcon) {
		this.marker = parkIcon.getMarker();
		this.bitmap = parkIcon.getBitmap();
		this.parkingLotID = parkIcon.getGarkingLotID();
		this.parkinglotLocation = parkIcon.getParkingLotLocation();
		this.parkingLotSize = parkIcon.getParkingLotSize();
		this.availableParkSize = parkIcon.getAvailableParkSize();
		this.prevAvailableParkSize = parkIcon.getPrevAvailableParkSize();
		this.latLng = parkIcon.getLatLng();
		this.isShown = parkIcon.getIsShown();
		this.parkinglotName = parkIcon.getParkinglotName();
	}
}
