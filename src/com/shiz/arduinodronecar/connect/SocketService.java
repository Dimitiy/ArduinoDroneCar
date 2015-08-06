package com.shiz.arduinodronecar.connect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.android.util.AppConstants;
import com.android.util.Logging;

public class SocketService extends Service {
	private String address = "0.0.0.0";
	static Socket socket = null;
	private static Context mContext;
	private final static String TAG = SocketService.class.getSimpleName();
	private SharedPreferences sPref = null;
	private static DataOutputStream out = null;
	private DataInputStream in = null;
	private static boolean isConnect = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Logging.doLog(TAG, "I am in on start", "I am in on start");
		// Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
		if (AppConstants.isApplication == false) {
			this.stopSelf();
			return 0;
		}
		mContext = getApplicationContext();
		ConnectSocket connect = new ConnectSocket();
		connect.execute();
		return START_STICKY;
	}

	private void establishingConnection() {
		sPref = mContext.getSharedPreferences("Settings", Context.MODE_PRIVATE);

		Logging.doLog(TAG, "connect", "connect");
		int serverPort = Integer.parseInt(sPref.getString("port", "10082")); // здесь
																				// обязательно
																				// нужно
																				// указать
																				// порт
																				// к
		// которому привязывается сервер.
		this.address = sPref.getString("ip", "192.168.1.10");
		Logging.doLog(TAG, "address" + address, "address" + address);
		// это IP-адрес компьютера, где исполняется наша
		// серверная программа.
		// Здесь указан адрес того самого компьютера где
		// будет исполняться и клиент.

		try {
			InetAddress ipAddress = InetAddress.getByName(address); // создаем
																	// объект
																	// который
																	// отображает
																	// вышеописанный
																	// IP-адрес.
			Logging.doLog(TAG, "Any of you heard of a socket with IP address "
					+ address + " and port " + serverPort + "?",
					"Any of you heard of a socket with IP address " + address
							+ " and port " + serverPort + "?");
			socket = new Socket(ipAddress, serverPort); // создаем сокет
														// используя
														// IP-адрес и
														// порт сервера.
			Logging.doLog(TAG, "Yes! I just got hold of the program.",
					"Yes! I just got hold of the program.");
			setConnect(true);
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(mContext, "Соединение с ПУ установлено!",
							Toast.LENGTH_LONG).show();
				}
			});
			// Берем входной и выходной потоки сокета, теперь можем получать и
			// отсылать данные клиентом.
			InputStream sin = socket.getInputStream();
			OutputStream sout = socket.getOutputStream();

			// Конвертируем потоки в другой тип, чтоб легче обрабатывать
			// текстовые сообщения.
			in = new DataInputStream(sin);
			out = new DataOutputStream(sout);

			ResponseParser respParse = new ResponseParser();

			Logging.doLog(
					TAG,
					"Type in something and press enter. Will send it to the server and tell ya what it thinks.",
					"Type in something and press enter. Will send it to the server and tell ya what it thinks.");

			while (true) {
				// ждем пока сервер отошлет строку текста.
				respParse.parser(String.valueOf(in.readUTF()));
//				if (respParse.getType() == 1)
//
//					Logging.doLog("ConnectServ",
//							"The server was very polite. It sent me this : "
//									+ line,
//							"The server was very polite. It sent me this : "
//									+ line);
//				out.writeUTF(line); // отсылаем введенную строку текста серверу.
//				out.flush(); // заставляем поток закончить передачу данных.
//
			}
		} catch (Exception x) {

			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(new Runnable() {
				public void run() {
					Toast.makeText(
							mContext,
							"Ошибка соединения с сервером! ",
							Toast.LENGTH_LONG).show();
				}
			});
			x.printStackTrace();
		}
	}

	static void sendToServerComand(String line) {
		try {
			if (out != null || socket != null) {
				out.writeUTF(line); // отсылаем клиенту конфигурацию
				out.flush(); // заставляем поток закончить передачу
								// данных.System.out.println("Waiting for the next line...");
								// status = in.readUTF(); // ожидаем пока клиент
								// пришлет строку
				if (String.valueOf(line).equals("END")) {
					if (socket != null) {
						socket.close();
						setConnect(false);

					}
				}
				

			}
		} catch (IOException e) {
			// TODO Автоматически созданный блок catch
			e.printStackTrace();
		}
	}

	public static void closeConnect() {
		sendToServerComand(AppConstants.END_CONNECTIONS);

	}

	private class ConnectSocket extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Автоматически созданная заглушка метода
			establishingConnection();
			return null;
		}

	}

	private final IBinder myBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		public SocketService getService() {
			Logging.doLog(TAG, "I am in Localbinder ", "I am in Localbinder ");
			return SocketService.this;

		}
	}

	private static void setConnect(boolean setConnect) {
		isConnect = setConnect;
	}

	public static boolean isConnect() {
		return isConnect;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return myBinder;
	}

	public void IsBoundable() {
		Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			socket.close();
			setConnect(false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket = null;
	}
}