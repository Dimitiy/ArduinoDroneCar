package com.shiz.arduinodronecar;

import java.util.Stack;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shiz.arduinodronecar.fragment.LocationFragment;
import com.shiz.arduinodronecar.fragment.SearchDroneFragment;
import com.shiz.arduinodronecar.fragment.SensorFragment;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private String TAG = MainActivity.class.getSimpleName();
	private NavigationDrawerFragment mNavigationDrawerFragment;
	Stack<Fragment> fragmentStack;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private Toolbar toolbar;
	private int color = -1;
	SharedPreferences sPref;
	private static final String MY_SETTINGS = "Settings";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		sPref = getSharedPreferences(MY_SETTINGS, MODE_PRIVATE);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		Log.d(TAG, "mNavigationDrawerFragment.setUp");

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		switch (position) {
		case 1:
			Log.d(TAG, "case 1");
			fragmentManager
					.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.replace(R.id.container,
							SearchDroneFragment.newInstance(position)).commit();
			break;

		case 2:
			Log.d(TAG, "case 2");

			fragmentManager
					.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.replace(R.id.container,
							LocationFragment.newInstance(position)).commit();
			break;
		case 3:
			Log.d(TAG, "case 3");

			fragmentManager
					.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.replace(R.id.container,
							SensorFragment.newInstance(position)).commit();
			break;
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:

			mTitle = getString(R.string.title_section1);
			break;

		case 2:

			mTitle = getString(R.string.title_section2);
			break;
		case 3:

			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		Log.d(TAG, "restoreActionBar");
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(mTitle);
		if (mTitle.equals(getString(R.string.title_section1)))
			color = R.color.actionbar_background;
		else if (mTitle.equals(getString(R.string.title_section2)))
			color = R.color.actionbar_background_location;
		else if (mTitle.equals(getString(R.string.title_section3)))
			color = R.color.actionbar_background_sensors;

		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(color)));
	}

	public void onBackPressed() {
		Log.d(TAG, Boolean.toString(mNavigationDrawerFragment.isDrawerOpen()));

		if (mNavigationDrawerFragment.isOpen()) {
			Log.d(TAG, Boolean.toString(mNavigationDrawerFragment.isOpen()));
			mNavigationDrawerFragment.CloseDrawer();

		} else if (!mTitle.equals(getString(R.string.title_section1))) {
			mTitle = getString(R.string.title_section1);

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.container, SearchDroneFragment.newInstance(1))
					.commit();
			restoreActionBar();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			Log.d(TAG, "onCreateOptionsMenu");
			MenuInflater inflater = new MenuInflater(this);
			inflater.inflate(R.menu.toolbar_menu, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_settings:

			AlertDialog.Builder builder = new Builder(this);
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.settings, null);
			final EditText ipfield = (EditText) view.findViewById(R.id.txtIP);
			final EditText portfield = (EditText) view.findViewById(R.id.port);
			ipfield.setText(sPref.getString("ip", ""));
			portfield.setText(sPref.getString("port", ""));
			InputFilter[] filters = new InputFilter[1];
			filters[0] = new InputFilter() {

				@Override
				public CharSequence filter(CharSequence source, int start,
						int end, Spanned dest, int dstart, int dend) {
					// TODO Auto-generated method stub
					if (end > start) {
						String destTxt = dest.toString();
						String resultingTxt = destTxt.substring(0, dstart)
								+ source.subSequence(start, end)
								+ destTxt.substring(dend);
						if (!resultingTxt
								.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
							return "";
						} else {
							String[] splits = resultingTxt.split("\\.");
							for (int i = 0; i < splits.length; i++) {
								if (Integer.valueOf(splits[i]) > 255) {
									return "";
								}
							}
						}
					}
					return null;
				}
			};
			ipfield.setFilters(filters);
			builder.setView(view);
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							// do what you need
							if (ipfield.getText().toString().equals("")
									|| portfield.getText().toString()
											.equals("")) {
								Toast.makeText(getApplicationContext(),
										"Введите корректный адресс ПУ",
										Toast.LENGTH_LONG).show();
							} else {
								Editor ed = sPref.edit();
								ed.putString("ip", ipfield.getText().toString());
								ed.putString("port", portfield.getText()
										.toString());
								ed.commit();
								Toast.makeText(
										getApplicationContext(),
										"IP ПУ изменен"
												+ sPref.getString("ip",
														"192.168.1.10")
												+ ":"
												+ sPref.getString("port",
														"10082"),
										Toast.LENGTH_LONG).show();
							}
						}

					});

			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					});
			AlertDialog dialog = builder.create();
			dialog.show();
			break;

		case R.id.action_about:
			Intent intent = new Intent(this, AboutActivity.class);
			intent.putExtra("color", color);
			startActivity(intent);
			break;

		case R.id.action_search:
			Toast.makeText(this, "action_search", Toast.LENGTH_SHORT).show();
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return super.onOptionsItemSelected(item);
	}

}
