package com.jingle.gpstracking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button button;
	// private TextView finalResult;
	// private AsyncTaskRunner runner = null;
	private Intent mServiceIntent = null;
	private boolean started = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.btn_do_it);
		System.out.println("Starting");
	}

	public void startCalendarActivity(View view) {
		Intent intent = new Intent(getApplicationContext(),
				CalendarActivity.class);
		startActivity(intent);
	}

	public void startCollectingData(View view) {
		if (started == false) {
			button = (Button) findViewById(R.id.btn_do_it);
			button.setText("Stop Tracking");
			Global.run = true;
			started = true;
			mServiceIntent = new Intent(this, LocationService.class);
			this.startService(mServiceIntent);
		} else {
			button = (Button) findViewById(R.id.btn_do_it);
			button.setText("Track Location");
			started = false;
			Global.run = false;
			this.stopService(mServiceIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
