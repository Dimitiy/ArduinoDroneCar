package com.shiz.arduinodronecar.data;

import android.location.Location;

public interface DroneValueListener {

	public void onLocationChanged(Location location);

	public void onOrientationChanged(float[] aValues, float[] mValues);

}
