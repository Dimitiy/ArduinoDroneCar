package com.shiz.arduinodronecar.connect;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.util.Logging;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class JsonBuilder {
	private Context mContext;
	private String TAG = JsonBuilder.class.getSimpleName();

	public JsonBuilder(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public String buildComandRequest(String request, String type)
			throws IOException {
		String imeistring = null;

		String str = "";
		final TelephonyManager manager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);

		if (manager.getDeviceId() != null) {
			imeistring = manager.getDeviceId(); // *** use for mobiles
		} else {
			imeistring = Secure.getString(mContext.getContentResolver(),
					Secure.ANDROID_ID); // *** use for
										// tablets
		}
		if (!request.equals(" ") && !request.equals("")) {
			JSONObject jsonObject = new JSONObject();

			try {
				jsonObject.put("device", imeistring);
				// jsonObject.put("key", System.currentTimeMillis());
				jsonObject.put("type", type);
				jsonObject.put("data", request);
				str = jsonObject.toString();

				Logging.doLog(TAG, "jsonArray: " + jsonObject.toString());

			} catch (JSONException e1) {
				Logging.doLog(TAG, "json сломался");
				e1.printStackTrace();
			}

			Logging.doLog(TAG, "do make.request: " + request);

			return str;

		} else {
			Logging.doLog(TAG, "request == null");
		}
		return str;
	}
}
