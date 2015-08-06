package com.shiz.arduinodronecar.fragment;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.shiz.arduinodronecar.MainActivity;
import com.shiz.arduinodronecar.R;
import com.shiz.arduinodronecar.data.CompassView;




public class SensorFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static String TAG = SensorFragment.class.getSimpleName();

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	float[] aValues = new float[3];
	float[] mValues = new float[3];
	CompassView compassView;
	SensorManager sensorManager;
	ToggleButton changeCoordinateSystem;

	public static SensorFragment newInstance(int sectionNumber) {
		Log.d(TAG, "new instance");
		SensorFragment fragment = new SensorFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public SensorFragment() {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onAttach(getActivity());
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sensor_layout, container, false);

		compassView = (CompassView) view.findViewById(R.id.compassView);
		sensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);

		updateOrientation(new float[] { 0, 0, 0 });
		return view;
	}

	private void updateOrientation(float[] values) {
		if (compassView != null) {
			compassView.setBearing(values[0]);
			compassView.setPitch(values[1]);
			compassView.setRoll(-values[2]);
			compassView.invalidate();
		}
	}

	private float[] calculateOrientation() {
		float[] values = new float[3];
		float[] R = new float[9];
		float[] outR = new float[9];
		int axisX = 0, axisY = 0;
		int mScreenRotation = getActivity().getWindowManager()
				.getDefaultDisplay().getRotation();
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

	private final SensorEventListener sensorEventListener = new SensorEventListener() {
		
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
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
	};

	@Override
	public void onResume() {
		super.onResume();
		Log.d("SensorFragment", "onResume");
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor magField = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		sensorManager.registerListener(sensorEventListener, magField,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	public void onStop() {
		sensorManager.unregisterListener(sensorEventListener);
		super.onStop();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}
}
