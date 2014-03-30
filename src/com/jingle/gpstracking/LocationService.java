package com.jingle.gpstracking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.provider.CalendarContract.Instances;

import com.jingle.gpstracking.LocationContract.LocationEntry;

public class LocationService extends IntentService {

	private String resp;
	private Boolean sendData = true;
	private GPSTracker mgps;
	private Context mcontext;

	public LocationService() {
		super("LocationService");
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {
		mcontext = getApplicationContext();
		mgps = new GPSTracker(mcontext);
		doInBackground();
		System.out.println("Finished");
	}

	@Override
	public int onStartCommand(android.content.Intent intent, int flags,
			int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	protected String doInBackground() {
		System.out.println("started doInBackgroung");
		// publishProgress("Here we go...");
		GPSTracker gps = mgps;
		long begin = 0;
		long end = 0;
		while (sendData) {
			try {
				sendData = Global.run;
				int delay = 1000 * 60 * 10;
				System.out.println(gps.canGetLocation());
				if (gps.canGetLocation()) {
					gps.getLocation();
					// publishProgress("got location");
					resp = String.valueOf(gps.latitude) + ","
							+ String.valueOf(gps.longitude);
					if (end == 0) {
						begin = System.currentTimeMillis();
					} else {
						begin = end;
					}
					end = System.currentTimeMillis() + delay;
					readCalendar(mcontext, gps.latitude, gps.longitude, begin,
							end);
				} else {
					resp = "";
				}
				getdata(resp);
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
				resp = e.getMessage();
			} catch (Exception e) {
				e.printStackTrace();
				resp = e.getMessage();
			}
		}
		return resp;
	}

	private void getdata(String locat) {
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			String params = "id=" + Global.id + "&location=" + locat;
			URL url = new URL(Global.jingleBase + "?" + params);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			readStream(con.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readCalendar(Context context, double latitude,
			double longitude, long begin, long end) {

		String[] proj = new String[] { Instances._ID, Instances.BEGIN,
				Instances.END, Instances.EVENT_ID, Instances.TITLE,
				Instances.EVENT_LOCATION, Instances.ALL_DAY };
		Cursor cursor = Instances.query(context.getContentResolver(), proj,
				begin, end);

		LocationDbHelper mDbHelper = new LocationDbHelper(mcontext);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		String[] allColumns = { LocationEntry.COLUMN_NAME_LATITUDE,
				LocationEntry.COLUMN_NAME_LONGITUDE,
				LocationEntry.COLUMN_NAME_REGEX };
		Cursor savedLocations = db.query(LocationEntry.TABLE_NAME, // The
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
		boolean notified = false;
		if (cursor.moveToFirst()) {
			do {
				String location = cursor.getString(5);
				int allDay = cursor.getInt(6);
				long eventBegin = cursor.getLong(1);
				String title = cursor.getString(4);
				if (allDay == 0 && eventBegin > begin) {
					// Search db for location match

					boolean matched = false;
					if (savedLocations.moveToFirst()) {
						do {
							String storedRegex = savedLocations.getString(2);
							Pattern p = Pattern.compile(storedRegex);
							Matcher m = p.matcher(location);
							if (m.matches()) {
								matched = true;
								double locationLat = savedLocations
										.getDouble(0);
								double locationLon = savedLocations
										.getDouble(1);
								if (locationMatch(latitude, longitude,
										locationLat, locationLon) == false) {
									sendText(title + " is starting soon");
									notified = true;
									sendText(title);
								}
							}

						} while (matched == false
								&& savedLocations.moveToNext());
					}
					if (notified == false && atHome(latitude, longitude)) {
						matched = true;
						sendText(title + " is starting soon");
						notified = true;
					}

				}

			} while (cursor.moveToNext());
		}

	}

	private boolean locationMatch(double latitude, double longitude,
			double locationLat, double locationLon) {
		double tol = 0.00001;
		if (Math.abs(latitude - locationLat) < tol
				&& Math.abs(longitude - locationLon) < tol) {
			return true;
		} else {
			return false;
		}
	}

	private boolean atHome(double latitude, double longitude) {
		double tol = 0.0001;
		final double[] latitudes = { 34.1063476 };
		final double[] longitudes = { -117.7069019 };
		for (int i = 0; i < latitudes.length; ++i) {
			if (Math.abs(latitude - latitudes[i]) < tol
					&& Math.abs(longitude - longitudes[0]) < tol) {
				return true;
			}
		}
		return false;
	}

	private void sendText(String message) {
		try {
			String params = "id=" + Global.id + "&action=text,"
					+ URLEncoder.encode(message, "ISO-8859-1");
			URL url = new URL(Global.jingleBase + "?" + params);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readStream(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
