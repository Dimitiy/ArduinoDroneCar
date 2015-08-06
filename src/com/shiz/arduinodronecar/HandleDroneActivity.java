package com.shiz.arduinodronecar;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.util.Logging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shiz.arduinodronecar.connect.RequestList;
import com.shiz.arduinodronecar.connect.SocketService;
import com.shiz.arduinodronecar.data.CompassView;
import com.shiz.arduinodronecar.data.DroneValue;
import com.shiz.arduinodronecar.data.DroneValueListener;

public class HandleDroneActivity extends ActionBarActivity implements
		SensorEventListener, OnCheckedChangeListener, InfoWindowAdapter,
		DroneValueListener {
	private final String TAG = HandleDroneActivity.class.getSimpleName();
	private SensorManager mSensorManager;
	private Sensor mAccel;

	private GoogleMap map;
	// private Sensor mAccel;
	private android.support.v7.widget.SwitchCompat handleButton;

	private int xAxis = 0;
	private int yAxis = 0;

	private boolean show_Debug = true; // отображение отладочной информации
	private int xMax; // предел по оси X, максимальное значение для ШИМ (0-10),
						// чем больше, тем больше нужно наклонять
						// Android-устройство
	private int yMax; // предел по оси Y, максимальное значение для ШИМ (0-10)

	private int pwmMax; // максимальное значение ШИМ
	private int xR; // точка разворота
	private ServiceConnection mConnection;
	private SocketService mBoundService;
	private boolean mIsBound = false;

	private String FORWARD_COMMAND = "1";
	private String LEFT_COMMAND = "2";
	private String RIGHT_COMMAND = "3";
	private String BACKWARD_COMMAND = "4";
	private String STOP_COMMAND = "0";
	final private int DIALOG_SWITCH = 1;
	final private int DIALOG_HANDLE = 2;
	private DroneValue dValue;
	public float[] aValues = new float[3];
	public float[] mValues = new float[3];
	private static CompassView compassView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.handle_layout);
		initial();
		createDialog(DIALOG_SWITCH);

		// relativeLayout.addView(v);
		mConnection = new ServiceConnection() {
			// EDITED PART
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Logging.doLog(TAG, "connect service");
				mBoundService = ((SocketService.LocalBinder) service)
						.getService();

			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				Logging.doLog(TAG, "disconnect service");
				mBoundService = null;
			}

		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Only show items in the action bar relevant to this screen
		// if the drawer is not showing. Otherwise, let the drawer
		// decide what to show in the action bar.
		Log.d(TAG, "onCreateOptionsMenu");
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.handle_menu, menu);
		final MenuItem toggleservice = menu.findItem(R.id.myswitch);
		handleButton = (android.support.v7.widget.SwitchCompat) toggleservice
				.getActionView();

		if (handleButton != null) {
			handleButton.setOnCheckedChangeListener(this);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		case R.id.myswitch:
		case R.id.action_about:
			createDialog(DIALOG_HANDLE);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		// handleButton.setChecked(SocketService.isConnect());
		return super.onPrepareOptionsMenu(menu);
	}

	public void onSensorChanged(SensorEvent e) {

		String log = null;
		if ((getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
			xAxis = Math.round(e.values[0] * pwmMax / xR);
			yAxis = Math.round(e.values[1] * pwmMax / yMax);

		} else {
			yAxis = Math.round(e.values[0] * pwmMax / yMax);
			xAxis = Math.round(-e.values[1] * pwmMax / xR);

		}
		if (xAxis < -50 && yAxis >= -220) {
			// usbConnect.motionRight();
			Logging.doLog(TAG, "right");
			sendCommandToServer(RIGHT_COMMAND);
			log = "right";
		} else if (xAxis > 50 && yAxis >= -140) {
			// usbConnect.motionLeft();
			log = "left";
			Logging.doLog(TAG, "left");
			sendCommandToServer(LEFT_COMMAND);

		} else if (xAxis > -50 && yAxis > 140) {
			// usbConnect.motionForward();
			log = "forward";
			Logging.doLog(TAG, "forward");

			sendCommandToServer(FORWARD_COMMAND);

		} else if (xAxis > -50 && yAxis < -27) {
			// usbConnect.motionBackward();
			Logging.doLog(TAG, "back");

			log = "backward";
			sendCommandToServer(BACKWARD_COMMAND);

		} else {
			// usbConnect.stopMotion();
			Logging.doLog(TAG, "stop");

			log = "stop";
			sendCommandToServer(STOP_COMMAND);

		}

		TextView textX = (TextView) findViewById(R.id.textViewX);
		TextView textY = (TextView) findViewById(R.id.textViewY);
		TextView textCmdSend = (TextView) findViewById(R.id.textViewCmdSend);

		if (show_Debug) {
			textX.setText(String.valueOf("X:"
					+ String.format("%.1f", e.values[0]) + "; xPWM:" + xAxis));
			textY.setText(String.valueOf("Y:"
					+ String.format("%.1f", e.values[1]) + "; yPWM:" + yAxis));

			textCmdSend.setText(log);
		} else {
			textX.setText("");
			textY.setText("");
			textCmdSend.setText("");
		}

	}

	private void sendCommandToServer(String command) {
		if (SocketService.isConnect() == false)
			return;
		switch (command) {
		case "1":
			RequestList.sendCommandRequest(FORWARD_COMMAND, this);
			break;
		case "2":
			RequestList.sendCommandRequest(LEFT_COMMAND, this);

			break;
		case "3":
			RequestList.sendCommandRequest(RIGHT_COMMAND, this);
			break;
		case "4":
			RequestList.sendCommandRequest(BACKWARD_COMMAND, this);
			break;
		default:
			RequestList.sendCommandRequest(STOP_COMMAND, this);
			break;
		}

	}

	private void createDialog(int format) {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (format == 1)
			dialog.setContentView(R.layout.dialog);
		else if (format == 2)
			dialog.setContentView(R.layout.dialog_handle);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(
						R.color.black_transparent)));
		dialog.setCanceledOnTouchOutside(true);

		Window window = dialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();
		window.getDecorView().getRootView()
				.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						dialog.dismiss();
						return false;
					}
				});
		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
		window.setAttributes(wlp);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();
	}

	private void initial() {
		LayoutInflater ltInflater = getLayoutInflater();

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		if (map == null) {
			Toast.makeText(getApplicationContext(),
					"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
		}
		(findViewById(R.id.map))
				.getViewTreeObserver()
				.addOnGlobalLayoutListener(
						new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

							@Override
							public void onGlobalLayout() {
								if (android.os.Build.VERSION.SDK_INT >= 16) {
									(findViewById(R.id.map))
											.getViewTreeObserver()
											.removeOnGlobalLayoutListener(this);
								} else {
									(findViewById(R.id.map))
											.getViewTreeObserver()
											.removeGlobalOnLayoutListener(this);
								}

							}
						});
		map.setPadding(0, 250, 0, 0);
		map.setMyLocationEnabled(true);
		map.getUiSettings().setCompassEnabled(true);
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setTrafficEnabled(true);
		map.getUiSettings().setRotateGesturesEnabled(false);
		map.getUiSettings().setTiltGesturesEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.setIndoorEnabled(true);
		map.setBuildingsEnabled(false);

		Location location = map.getMyLocation();
		if (location != null) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 3));

		}
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		LinearLayout linLayout = (LinearLayout) findViewById(R.id.layout_compass);
		View view = ltInflater
				.inflate(R.layout.layout_compass, linLayout, true);

		compassView = (CompassView) view.findViewById(R.id.compassView);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle("");
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		xMax = Integer.parseInt((String) getResources().getText(
				R.string.default_xMax));
		xR = Integer.parseInt((String) getResources().getText(
				R.string.default_xR));
		yMax = Integer.parseInt((String) getResources().getText(
				R.string.default_yMax));
		pwmMax = Integer.parseInt((String) getResources().getText(
				R.string.default_pwmMax));

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
		if (dValue != null)
			dValue.delListener(this);
		if (mSensorManager != null)
			mSensorManager.unregisterListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		dValue = new DroneValue();
		dValue.addListener(this);
		doBindService();
		invalidateOptionsMenu(); // If you are using activity

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	private void doBindService() {
		bindService(new Intent(this, SocketService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
		if (mBoundService != null) {
			mBoundService.IsBoundable();
		}
	}

	private void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked == true) {
			Logging.doLog(TAG, "isChecked try", "isChecked try");
			mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			mSensorManager.registerListener(HandleDroneActivity.this, mAccel,
					SensorManager.SENSOR_DELAY_GAME);

		} else {
			Logging.doLog(TAG, "isChecked false", "isChecked false");
			mSensorManager.unregisterListener(HandleDroneActivity.this);
		}
	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshDisplay(final float[] values) {
		Logging.doLog(TAG, String.valueOf(values[0] + values[1] + values[2]),
				String.valueOf(values[0] + values[1] + values[2]));
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (compassView != null) {
					compassView.setBearing(values[0]);
					compassView.setPitch(values[1]);
					compassView.setRoll(-values[2]);
					compassView.invalidate();
				}
			}
		});

	}

	@Override
	public void onLocationChanged(final Location location) {
		// TODO Auto-generated method stub
		Logging.doLog(TAG, "onLocationChanged", "onLocationChanged");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location
								.getLongitude())).zoom(16).build();
				map.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
				MarkerOptions marker = new MarkerOptions().position(
						new LatLng(location.getLatitude(), location
								.getLongitude())).title("БТ ");

				// adding marker
				map.addMarker(marker);
			}
		});

	}

	@Override
	public void onOrientationChanged(float[] aValues, float[] mValues) {
		// TODO Auto-generated method stub
		Logging.doLog(TAG, "refreshDisplay", "refreshDisplay");
		this.aValues = aValues;
		this.mValues = mValues;
		refreshDisplay(calculateOrientation());
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
			axisX = SensorManager.AXIS_MINUS_X;
			axisY = SensorManager.AXIS_MINUS_Y;
			
			break;

		case Surface.ROTATION_90:
			axisX = SensorManager.AXIS_MINUS_Z;
			axisY = SensorManager.AXIS_X;
			
			break;

		case Surface.ROTATION_180:
			axisX = SensorManager.AXIS_X;
			axisY = SensorManager.AXIS_Z;
			break;

		case Surface.ROTATION_270:
		axisX = SensorManager.AXIS_Z;
			axisY = SensorManager.AXIS_MINUS_X;
		
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
}
