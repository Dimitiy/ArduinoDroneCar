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
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.util.Logging;
import com.shiz.arduinodronecar.connect.SocketService;
import com.shiz.arduinodronecar.data.MapsViewController;

public class HandleDroneActivity extends ActionBarActivity implements
		SensorEventListener, OnCheckedChangeListener {
	private final String TAG = HandleDroneActivity.class.getSimpleName();
	private SensorManager mSensorManager;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

		setContentView(R.layout.handle_layout);
		MapsViewController mMap = new MapsViewController(this);
		mMap.init();

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
			SocketService.sendToServerComand(RIGHT_COMMAND);
			log = "right";
		} else if (xAxis > 50 && yAxis >= -140) {
			// usbConnect.motionLeft();
			log = "left";
			Logging.doLog(TAG, "left");

			SocketService.sendToServerComand(LEFT_COMMAND);

		} else if (xAxis > -50 && yAxis > 140) {
			// usbConnect.motionForward();
			log = "forward";
			Logging.doLog(TAG, "forward");

			SocketService.sendToServerComand(FORWARD_COMMAND);

		} else if (xAxis > -50 && yAxis < -27) {
			// usbConnect.motionBackward();
			Logging.doLog(TAG, "back");

			log = "backward";
			SocketService.sendToServerComand(BACKWARD_COMMAND);

		} else {
			// usbConnect.stopMotion();
			Logging.doLog(TAG, "stop");

			log = "stop";
			SocketService.sendToServerComand(STOP_COMMAND);

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
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.MATCH_PARENT);
		dialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// startService(new Intent(HandleDroneActivity.this,
		// SocketService.class));
		doBindService();
		invalidateOptionsMenu(); // If you are using activity
		// mAccel = mSensorManager
		// .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//
		// mSensorManager.registerListener(this, mAccel,
		// SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mSensorManager != null)
			mSensorManager.unregisterListener(this);
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

			// mSensorManager = (SensorManager)
			// getSystemService(Context.SENSOR_SERVICE);
			// mAccel =
			// mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			// mSensorManager.registerListener(HandleDroneActivity.this, mAccel,
			// SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Logging.doLog(TAG, "isChecked false", "isChecked false");
			mSensorManager.unregisterListener(HandleDroneActivity.this);
		}
	}

}
