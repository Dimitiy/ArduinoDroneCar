package com.shiz.arduinodronecar.connect;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.util.Logging;
import com.shiz.arduinodronecar.data.MapsViewController;

public class ResponseParser {
	String device, type, data;
	private String TAG = ResponseParser.class.getSimpleName();

	public void Parser(String response) {
		Logging.doLog(TAG, "response " + response, "response " + response);

		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
		} catch (JSONException e) {
			return;
		}

		String str = null;
		// ----------get id device------------

		try {
			str = jsonObject.getString("device");
		} catch (JSONException e) {
			str = null;
		}
		if (str != null) {
			setDevice(str);
		} else {
			setDevice(null);
		}
		// ----------type data------------
		try {
			str = jsonObject.getString("type");
		} catch (JSONException e) {
			str = null;
		}
		if (str != null) {
			setType(str);
		} else {
			setType(null);

		}
		// ----------data------------
		try {
			str = jsonObject.getString("data");
		} catch (JSONException e) {
			str = null;
		}
		if (str != null) {
			setData(str);

		} else {
			setData(null);
		}
		// ----------location------------
		try {
			str = jsonObject.getString("loc");
		} catch (JSONException e) {
			str = null;
		}
		if (str != null) {
			setLocation(str);

		} else {
//			setLocation(null);
		}
	}

	private void setLocation(String str) {
		// TODO Auto-generated method stub
		LocationToServerListener lToServer = new LocationToServerListener() {
			
			@Override
			public void changeLocation(Double x, Double y) {
				// TODO Auto-generated method stub
				
			}
		};
		
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDevice() {
		return this.device;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return this.data;
	}
}
