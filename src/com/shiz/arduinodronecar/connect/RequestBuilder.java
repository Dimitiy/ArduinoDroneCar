package com.shiz.arduinodronecar.connect;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.util.Logging;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class RequestBuilder {
	private static String TAG = RequestBuilder.class.getSimpleName();

	public static String buildComandRequest(String request,
			Context mContext) throws IOException {
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
			JSONArray jsonArray;
			str = "[" + request + "]";
			try {
				jsonObject.put("device", imeistring);
				jsonArray = new JSONArray(str);
				jsonObject.put("data", jsonArray);
				str = jsonObject.toString();

			} catch (JSONException e1) {
				Logging.doLog(TAG, "json сломался");
				e1.printStackTrace();
			}

			Logging.doLog(TAG, "do make.request: " + str);

			return str;

		} else {
			Logging.doLog(TAG, "request == null");
		}
		return str;
	}
}
