package com.shiz.arduinodronecar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class AboutActivity extends ActionBarActivity {
	private final String TAG = AboutActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_about);
		Intent intent = getIntent();
		
		int color = intent.getIntExtra("color", R.color.actionbar_background);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(color)));
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; goto parent activity.
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Only show items in the action bar relevant to this screen
	// // if the drawer is not showing. Otherwise, let the drawer
	// // decide what to show in the action bar.
	// Log.d(TAG, "onCreateOptionsMenu");
	// MenuInflater inflater = new MenuInflater(this);
	// inflater.inflate(R.menu.toolbar_menu, menu);
	//
	// return super.onCreateOptionsMenu(menu);
	// }
}