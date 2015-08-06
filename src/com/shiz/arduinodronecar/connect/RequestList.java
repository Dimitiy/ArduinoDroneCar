package com.shiz.arduinodronecar.connect;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.util.AppConstants;

public class RequestList {
	public static void sendCommandRequest(String command, Context ctx) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("command", command);
			jsonObject.put("type", AppConstants.TYPE_COMMAND_REQUEST);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			SocketService.sendToServerComand(RequestBuilder.buildComandRequest(
					jsonObject.toString(), ctx));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
