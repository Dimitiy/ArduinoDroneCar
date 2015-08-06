package com.shiz.arduinodronecar.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OrientationService extends Service implements SensorEventListener {
	static String TAG = OrientationService.class.getSimpleName().toString();
	SensorManager sensorManager;
	ToggleButton changeCoordinateSystem;
	public float[] aValues = new float[3];
	public float[] mValues = new float[3];
	private DroneValue dValue;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		sensorManager = (SensorManager) getApplicationContext()
				.getSystemService(SENSOR_SERVICE);
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		dValue = new DroneValue();
		
		sensorManager.registerListener(this, accelerometer,
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, magField,
				SensorManager.SENSOR_DELAY_NORMAL);
		return START_STICKY;
	}

	private float[] calculateOrientation() {
		float[] values = new float[3];
		float[] R = new float[9];
		float[] outR = new float[9];
		int axisX = 0, axisY = 0;
		WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = window.getDefaultDisplay();
		int mScreenRotation = display.getRotation();
		SensorManager.getRotationMatrix(R, null, aValues, mValues);
		switch (mScreenRotation) {
		case Surface.ROTATION_0:
			axisX = SensorManager.AXIS_X;
			axisY = SensorManager.AXIS_Z;
			break;

		case Surface.ROTATION_90:
			axisX = SensorManager.AXIS_Z;
			axisY = SensorManager.AXIS_MINUS_X;
			break;

		case Surface.ROTATION_180:
			axisX = SensorManager.AXIS_MINUS_X;
			axisY = SensorManager.AXIS_MINUS_Y;
			break;

		case Surface.ROTATION_270:
			axisX = SensorManager.AXIS_MINUS_Z;
			axisY = SensorManager.AXIS_X;
			break;

		default:
			break;
		}
		SensorManager.remapCoordinateSystem(R, axisX, axisY, outR);

		SensorManager.getOrientation(outR, values);

		// Convert from Radians to Degrees.
		values[0] = (float) Math.toDegrees(values[0]);
		values[1] = (float) Math.toDegrees(values[1]);
		values[2] = (float) Math.toDegrees(values[2]);

		return values;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
		super.onCreate();
	}

	private void updateOrientation(float[] values) {
//		dValue.setOrientation(values);
	}



	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			aValues = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mValues = event.values;
		updateOrientation(calculateOrientation());

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
