package com.shiz.arduinodronecar.data;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.location.Location;

import com.android.util.Logging;

/**
 * LocationValue class is designed to get/set value location
 * 
 * @author johny homicide
 * 
 */
public class DroneValue {
	private static double latitude;
	private static double longitude;
	private static float accuracy;
	private static float speed;
	private static float bearing;
	private static double altitude;
	private volatile static boolean canGetLocation;
	private volatile static boolean connectClient;
	private static ArrayList<DroneValueListener> listeners = new ArrayList<DroneValueListener>();
	private final static String TAG = DroneValue.class.getSimpleName();

	public void addListener(DroneValueListener listener) {
		// Запоминаем listener
		listeners.add(listener);
	}

	public void delListener(DroneValueListener listener) {
		listeners.remove(listener);
	}

	public void setLocation(Location location) {
		for (DroneValueListener dListener : listeners) {
			dListener.onLocationChanged(location);
			
		}
	}

	public void setOrientation(float[] aValues, float[] mValues) {
		for (DroneValueListener dListener : listeners) {
			dListener.onOrientationChanged(aValues, mValues);
			
		}
}

	/**
	 * @return double
	 * */
	public static double getLatitude() {
		// return latitude
		return latitude;
	}

	/**
	 * @return double
	 * */
	public static double getLongitude() {
		return longitude;
	}

	public static void setLatitude(double mLatitude) {
		if (latitude != BigDecimal.ZERO.doubleValue()) {
			latitude = mLatitude;
		}
	}

	public static void setLongitude(double mLongitude) {
		if (longitude != BigDecimal.ZERO.doubleValue()) {
			longitude = mLongitude;
		}
	}

	public static void setAccuracy(float mAccuracy) {
		if (accuracy != BigDecimal.ZERO.doubleValue()) {
			accuracy = mAccuracy;
		}
	}

	/**
	 * @return float
	 * */
	public static float getAccuracy() {
		return accuracy;
	}

	/**
	 * Function to set location enabled
	 * 
	 * @return boolean
	 * */
	public static boolean canGetLocation() {
		return canGetLocation;
	}

	public static void setSpeed(float mSpeed) {
		speed = mSpeed;
	}

	/**
	 * @return float
	 * */
	public static float getSpeed() {
		return speed;
	}

	public static void setLocationClient(boolean mConnectClient) {
		connectClient = mConnectClient;
	}

	/**
	 * @return boolean
	 * */
	public static boolean isLocationClient() {
		return connectClient;
	}

	public static void setAltitude(double mAltitude) {
		altitude = mAltitude;
	}

	/**
	 * @return double
	 * */
	public static double getAltitude() {
		return altitude;
	}

	public static void setBearing(float mBearing) {
		bearing = mBearing;
	}
//
//	/**
//	 * @return float
//	 * */
//	public static float getBearing() {
//		return bearing;
//	}
//	
//	private static void setBearing(float values) {
//		bearing = values;
//
//	}
//
//	public static float getBearing() {
//		return bearing;
//	}
//
//	private static void setPitch(float values) {
//		pitch = values;
//
//	}
//
//	public static float getPitch() {
//		return pitch;
//	}
//
//	private static void setRoll(float values) {
//		roll = values;
//
//	}
//
//	public static float getRoll() {
//		return roll;
//	}
}
