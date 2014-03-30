package com.jingle.gpstracking;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jingle.gpstracking.LocationContract.LocationEntry;

public class CalendarActivity extends Activity {

	// Projection array. Creating indices for this array instead of doing
	// dynamic lookups improves performance.
	public static final String[] EVENT_PROJECTION = new String[] {
			Calendars._ID, // 0
			Calendars.ACCOUNT_NAME, // 1
			Calendars.CALENDAR_DISPLAY_NAME, // 2
			Calendars.OWNER_ACCOUNT // 3
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		fillTable();

	}

	public void addLocation(View view) {
		EditText editText = (EditText) findViewById(R.id.LocationName);
		GPSTracker gps = new GPSTracker(this);
		AsyncLocationRunner runner = new AsyncLocationRunner(
				getApplicationContext(), gps, editText.getText().toString());
		runner.execute();
		editText.setText("");
	}

	private void fillTable() {
		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();
		TableLayout.LayoutParams tableParams = 
			      new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 
			                                   TableLayout.LayoutParams.MATCH_PARENT);
			    TableRow.LayoutParams rowParams = 
			      new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1f);
			    TableRow.LayoutParams itemParams = 
			      new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 
			                                TableRow.LayoutParams.MATCH_PARENT, 1f);
		LocationDbHelper dbHelper = new LocationDbHelper(
				getApplicationContext());
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		String[] allColumns = { LocationEntry.COLUMN_NAME_LATITUDE,
				LocationEntry.COLUMN_NAME_LONGITUDE,
				LocationEntry.COLUMN_NAME_REGEX };
		Cursor cursor = db.query(LocationEntry.TABLE_NAME, // The
															// table
															// to
															// query
				allColumns, // The columns to return
				LocationEntry.COLUMN_NAME_REGEX + " = "
						+ LocationEntry.COLUMN_NAME_REGEX, // The columns
															// for the WHERE
															// clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				null // The sort order
				);

		TableRow tableHeader = new TableRow(getApplicationContext());

		TextView locationHeader = new TextView(getApplicationContext());
		TextView latitudeHeader = new TextView(getApplicationContext());
		TextView longitudeHeader = new TextView(getApplicationContext());
		TextView actionHeader = new TextView(getApplicationContext());

		locationHeader.setText("Location");
		latitudeHeader.setText(String.valueOf("Latitude"));
		longitudeHeader.setText(String.valueOf("Longitude"));
		actionHeader.setText(String.valueOf("Action"));

		locationHeader.setBackground(getResources().getDrawable(
				R.drawable.cell_shape));
		latitudeHeader.setBackground(getResources().getDrawable(
				R.drawable.cell_shape));
		longitudeHeader.setBackground(getResources().getDrawable(
				R.drawable.cell_shape));
		actionHeader.setBackground(getResources().getDrawable(
				R.drawable.cell_shape));

		locationHeader.setTextColor(Color.BLACK);
		latitudeHeader.setTextColor(Color.BLACK);
		longitudeHeader.setTextColor(Color.BLACK);
		actionHeader.setTextColor(Color.BLACK);
		
		locationHeader.setLayoutParams(itemParams);
		latitudeHeader.setLayoutParams(itemParams);
		longitudeHeader.setLayoutParams(itemParams);
		//actionHeader.setLayoutParams(itemParams);

		tableHeader.addView(locationHeader);
		tableHeader.addView(latitudeHeader);
		tableHeader.addView(longitudeHeader);
		tableHeader.addView(actionHeader);

		table.addView(tableHeader);
		table.setLayoutParams(tableParams);

		if (cursor.moveToFirst()) {
			do {
				TableRow tableRow = new TableRow(getApplicationContext());
//				TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
//						TableLayout.LayoutParams.MATCH_PARENT,
//						TableLayout.LayoutParams.WRAP_CONTENT);
//
//				int leftMargin = 10;
//				int topMargin = 2;
//				int rightMargin = 10;
//				int bottomMargin = 2;
//
//				tableRowParams.setMargins(leftMargin, topMargin, rightMargin,
//						bottomMargin);
				tableRow.setLayoutParams(rowParams);
				tableRow.setLayoutParams(new TableLayout.LayoutParams(
	                    TableLayout.LayoutParams.MATCH_PARENT,
	                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f));

				final String location = cursor.getString(2);
				double latitude = cursor.getDouble(0);
				double longitude = cursor.getDouble(1);

				TextView columnsViewLocation = new TextView(
						getApplicationContext());
				TextView columnsViewLatitude = new TextView(
						getApplicationContext());
				TextView columnsViewLongitude = new TextView(
						getApplicationContext());
				Button deleteButton = new Button(getApplicationContext());

				// columnsView.setLayoutParams(new
				// TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
				// TableRow.LayoutParams.WRAP_CONTENT));

				columnsViewLocation.setText(location);
				columnsViewLatitude.setText(String.valueOf(latitude));
				columnsViewLongitude.setText(String.valueOf(longitude));
				deleteButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {
						db.delete(LocationEntry.TABLE_NAME,
								LocationEntry.COLUMN_NAME_REGEX + "='"
										+ location + "'", null);
						fillTable();
					}
				});

				columnsViewLocation.setLayoutParams(itemParams);
				columnsViewLatitude.setLayoutParams(itemParams);
				columnsViewLongitude.setLayoutParams(itemParams);
				//deleteButton.setLayoutParams(itemParams);
				
				columnsViewLocation.setBackground(getResources().getDrawable(
						R.drawable.cell_shape));
				columnsViewLatitude.setBackground(getResources().getDrawable(
						R.drawable.cell_shape));
				columnsViewLongitude.setBackground(getResources().getDrawable(
						R.drawable.cell_shape));
				deleteButton.setBackground(getResources().getDrawable(
						R.drawable.cell_shape));
				deleteButton.setText("Delete");

				columnsViewLocation.setTextColor(Color.BLACK);
				columnsViewLatitude.setTextColor(Color.BLACK);
				columnsViewLongitude.setTextColor(Color.BLACK);
				deleteButton.setTextColor(Color.BLACK);

				tableRow.addView(columnsViewLocation);
				tableRow.addView(columnsViewLatitude);
				tableRow.addView(columnsViewLongitude);
				tableRow.addView(deleteButton);

				table.addView(tableRow);
			} while (cursor.moveToNext());
		}

		// for (int i = 0; i < 3; ++i) {
		// TableRow tableRow = new TableRow(getApplicationContext());
		// //tableRow.setLayoutParams(new
		// TableRow.LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT));
		// for( int j = 0; j < 4; ++j) {
		// TextView columnsView = new TextView(getApplicationContext());
		// //columnsView.setLayoutParams(new
		// TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
		// TableRow.LayoutParams.WRAP_CONTENT));
		// columnsView.setText("test");
		// columnsView.setBackground(getResources().getDrawable(R.drawable.cell_shape));
		// tableRow.addView(columnsView);
		// }
		// table.addView(tableRow);
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class AsyncLocationRunner extends AsyncTask<String, String, String> {

		private String location;
		private GPSTracker mgps;
		private Context mcontext;

		public AsyncLocationRunner(Context context, GPSTracker gps,
				String locationName) {
			mgps = gps;
			location = locationName;
			mcontext = context;
		}

		@Override
		protected String doInBackground(String... params) {
			publishProgress("starting");
			GPSTracker gps = mgps;
			gps.getLocation();
			String latitude = String.valueOf(gps.latitude);
			String longitude = String.valueOf(gps.longitude);
			LocationDbHelper mDbHelper = new LocationDbHelper(mcontext);
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(LocationEntry.COLUMN_NAME_LATITUDE, latitude);
			values.put(LocationEntry.COLUMN_NAME_LONGITUDE, longitude);
			values.put(LocationEntry.COLUMN_NAME_REGEX, location);
			long newRowId = db.insert(LocationEntry.TABLE_NAME, null, values);
			publishProgress(String.valueOf(newRowId));
			return location;
		}
		
		protected void onPostExecute(String result)
	    {
	        fillTable();
	    }

	}
}
