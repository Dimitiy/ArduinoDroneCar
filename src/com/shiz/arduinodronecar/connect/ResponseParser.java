package com.shiz.arduinodronecar.connect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;

import com.android.util.Logging;
import com.shiz.arduinodronecar.data.DroneValue;

public class ResponseParser {
	String device = "", data = "";
	int type;
	private String TAG = ResponseParser.class.getSimpleName();
	private String time;

	public void parser(String response) throws JSONException {
		JSONArray jsonArrayData;
		JSONObject jsonObject;
		DroneValue drValue = new DroneValue();
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
			setDevice("");
		}

		// ----------get time event------------

		try {
			str = jsonObject.getString("time");
		} catch (JSONException e) {
			str = null;
		}
		if (str != null) {
			setTime(str);
		} else {
			setTime("");
		}

		// ----------data------------
		jsonArrayData = jsonObject.getJSONArray("data");
		for (int i = 0; i < jsonArrayData.length(); ++i) {
			jsonObject = jsonArrayData.getJSONObject(i);
			try {
				type = jsonObject.getInt("type");
			} catch (JSONException e) {
				type = 0;
			}
			if (type != 0) {
				setType(type);
			} else {
				setType(0);
			}
			switch (type) {
			case 1:
				str = jsonArrayData.get(2).toString();
				break;
			case 2:
				str = jsonObject.getString("location");
				String[] loc = str.split(" ");

				Logging.doLog(TAG, loc[0] + loc[1] + loc[2] + loc[3] + loc[4],
						loc[0] + loc[1] + loc[2] + loc[3] + loc[4]);
				Location location = new Location("device");
				for (int a = 0; a < loc.length; ++a) {
					double number = Double.parseDouble(loc[a]);
					switch (a) {
					case 0:
						location.setLongitude(number);
						break;
					case 1:
						location.setLatitude(number);
						break;
					case 2:
						location.setAltitude(number);
						break;
					case 3:
						location.setAccuracy((float) number);
						break;
					case 4:
						location.setSpeed((float) number);
						break;
					default:
						break;
					}
				}
				drValue.setLocation(location);

				Logging.doLog(TAG,
						location.getLatitude() + "" + location.getLongitude());
				drValue.setLocation(location);
				break;
			case 3:
				str = jsonObject.getString("orientation").replace("[", "")
						.replace("]", "").trim();
				String[] parts = str.split(",");
				float[] aValues = new float[3],
				mValues = new float[3];
				for (int a = 0; a < parts.length; ++a) {
					float number = Float.parseFloat(parts[a]);
					if (a < 3)
						aValues[a] = number;
					else
						mValues[a - 3] = number;
				}
				drValue.setOrientation(aValues, mValues);

				break;
			default:
				break;
			}
		}

	}

	private void setTime(String time) {
		// TODO Auto-generated method stub
		this.time = time;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getDevice() {
		return this.device;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return this.data;
	}
}
