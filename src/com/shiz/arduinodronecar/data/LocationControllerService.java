package com.shiz.arduinodronecar.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.android.util.Logging;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationControllerService extends Service implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {
	private final static String TAG = LocationControllerService.class
			.getSimpleName();
	private LocationRequest mLocationRequest;
	private GoogleApiClient googleApiClient;
	private DroneValue dValue;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Logging.doLog(TAG, "OnStart");
		dValue = new DroneValue();
		if (isOnline())
			createApiGoogle();

		return START_STICKY;
	}

	

	private void createApiGoogle() {
		Logging.doLog(TAG, "createApiGoogle", "createApiGoogle");

		googleApiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();
		googleApiClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		Logging.doLog(TAG, "onLocationChanged" + location.toString());
		dValue.setLocation(location);

		// DroneValue.setLocation(location);

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Logging.doLog(TAG, "onConnectionFailed: " + connectionResult,
				"onConnectionFailed: " + connectionResult);
		DroneValue.setLocationClient(false);

		if (connectionResult.hasResolution()) {
			// Google Play services can fix the issue
			// e.g. the user needs to enable it, updates to latest version
			// or the user needs to grant permissions to it
			DroneValue.setLocationClient(false);
		} else {
			// Google Play services has no idea how to fix the issue
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		Logging.doLog(TAG, "Connected to Google Play Services: " + arg0,
				"Connected to Google Play Services: " + arg0);
		mLocationRequest = LocationRequest.create().setInterval(0)
				.setFastestInterval(5000L).setSmallestDisplacement(0);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		Logging.doLog(TAG, "connect locationClient " + googleApiClient,
				"connect locationClient " + googleApiClient);
		if (googleApiClient != null)
			if (googleApiClient.isConnected()) {
				LocationServices.FusedLocationApi.requestLocationUpdates(
						googleApiClient, mLocationRequest, this);
				DroneValue.setLocationClient(true);
			}

	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	private final IBinder myBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public LocationControllerService getService() {
			System.out.println("I am in Localbinder ");
			return LocationControllerService.this;

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	public void IsBoundable() {
		Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			googleApiClient.disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
