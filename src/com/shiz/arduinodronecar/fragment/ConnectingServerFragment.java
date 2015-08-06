package com.shiz.arduinodronecar.fragment;

import java.net.Socket;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.shiz.arduinodronecar.HandleDroneActivity;
import com.shiz.arduinodronecar.R;
import com.shiz.arduinodronecar.connect.SocketService;

public class ConnectingServerFragment extends Fragment {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	static Socket socket;
	private ToggleButton toggleConnectContropPoint;
	SocketService mBoundService;

	Button bHandle, bObserver;
	boolean mIsBound = false;
	private ServiceConnection mConnection;
	public static final String TAG = ConnectingServerFragment.class
			.getSimpleName();

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static ConnectingServerFragment newInstance(int sectionNumber) {
		ConnectingServerFragment fragment = new ConnectingServerFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mConnection = new ServiceConnection() {
			// EDITED PART
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				mBoundService = ((SocketService.LocalBinder) service)
						.getService();

			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				mBoundService = null;
			}

		};
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		onAttach(getActivity());
		// setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_connect, container, false);

		toggleConnectContropPoint = (ToggleButton) view
				.findViewById(R.id.toggleConnectControlPoint);
		toggleConnectContropPoint.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (toggleConnectContropPoint.isChecked()) {
					Log.d("isChecked", "try");
					getActivity().startService(
							new Intent(getActivity(), SocketService.class));
					doBindService();
				} else {
					Log.d("isChecked", "false");
					// if (getSocket != null)
					// getSocket.closeConnect();
					SocketService.closeConnect();
					doUnbindService();
					toggleConnectContropPoint.setChecked(false);
				}
			}
		});
		bObserver = (Button) view.findViewById(R.id.button_handle);
		bObserver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),
						HandleDroneActivity.class);
				startActivity(intent);
			}
		});
		return view;
	}

	private void doBindService() {
		getActivity().bindService(
				new Intent(getActivity(), SocketService.class), mConnection,
				Context.BIND_AUTO_CREATE);
		mIsBound = true;
		if (mBoundService != null) {
			mBoundService.IsBoundable();
		}
	}

	private void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			getActivity().unbindService(mConnection);
			mIsBound = false;
		}
	}

}
