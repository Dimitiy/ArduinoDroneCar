package com.shiz.arduinodronecar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends Activity implements OnFocusChangeListener {
	private final String TAG = "PasswordActivity";
	private String[] password;
	private EditText secondSimbolPassword, firstSimbolPassword,
			threeSimbolPassword, fourthtSimbolPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		password = new String[4];
		setContentView(R.layout.activity_password);
		firstSimbolPassword = (EditText) findViewById(R.id.editText1);
		firstSimbolPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				password[0] = firstSimbolPassword.getText().toString();
				Log.d(TAG, "User set EditText value to " + password[0]);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				checkPassword();

			}
		});

		secondSimbolPassword = (EditText) findViewById(R.id.editText2);
		secondSimbolPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				password[1] = secondSimbolPassword.getText().toString();
				Log.d(TAG, "User set EditText value to " + password[1]);
				}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				checkPassword();
				
			}
		});
		threeSimbolPassword = (EditText) findViewById(R.id.editText3);
		threeSimbolPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				password[2] = threeSimbolPassword.getText().toString();
				Log.d(TAG, "User set EditText value to " + password[1]);
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				checkPassword();

			}
		});
		fourthtSimbolPassword = (EditText) findViewById(R.id.editText4);
		fourthtSimbolPassword.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				password[3] = fourthtSimbolPassword.getText().toString();
				Log.d(TAG, "User set EditText value to " + password[0]);
			
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				checkPassword();

			}
		});
		firstSimbolPassword.setOnFocusChangeListener(this);
		secondSimbolPassword.setOnFocusChangeListener(this);
		threeSimbolPassword.setOnFocusChangeListener(this);
		fourthtSimbolPassword.setOnFocusChangeListener(this);

	}

	private void checkPassword() {
		String parol = "";
		for (int i = 0; i < 4; i++) {
			Log.d(TAG, "checkPassword " + i + password[i]);
			if (password[i] != null) {
				if (password[i].equals("")) {
					setFocus(i);
					return;
				} else
					parol += password[i];
			} else {
				setFocus(i);
				return;
			}

		}
		Log.d(TAG, "parol " + parol);
		if (parol.equals("1111")) {
			Intent intent = new Intent(getBaseContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			finish();
		} else {
			firstSimbolPassword.setText("");
			secondSimbolPassword.setText("");
			threeSimbolPassword.setText("");
			fourthtSimbolPassword.setText("");
			Toast.makeText(PasswordActivity.this, R.string.error_password,
					Toast.LENGTH_SHORT).show();
		}

	}

	private void setFocus(int i) {

		switch (i) {
		case 0:
			firstSimbolPassword.requestFocus();
			break;
		case 1:
			secondSimbolPassword.requestFocus();
			break;
		case 2:
			threeSimbolPassword.requestFocus();
			break;
		case 3:
			fourthtSimbolPassword.requestFocus();
			break;
		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		Log.d(TAG, "hasFocus" + hasFocus);

		if (hasFocus) {
			switch (v.getId()) {
			case R.id.editText1:
				Log.d(TAG, "editText1 onFocusChange");
				firstSimbolPassword.setText("");
				
				break;
			case R.id.editText2:
				Log.d(TAG, "editText2 onFocusChange");
				secondSimbolPassword.setText("");
				
				break;
			case R.id.editText3:
				Log.d(TAG, "editText3 onFocusChange");
				threeSimbolPassword.setText("");
				break;
			case R.id.editText4:
				Log.d(TAG, "editText4 onFocusChange");
				fourthtSimbolPassword.setText("");
				break;
			}
		}
	}
}
