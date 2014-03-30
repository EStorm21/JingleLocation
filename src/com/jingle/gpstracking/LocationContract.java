package com.jingle.gpstracking;

import android.provider.BaseColumns;

public final class LocationContract {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public LocationContract() {
	}

	/* Inner class that defines the table contents */
	public static abstract class LocationEntry implements BaseColumns {
		public static final String TABLE_NAME = "entry";
		public static final String COLUMN_NAME_REGEX = "regex";
		public static final String COLUMN_NAME_LATITUDE = "latitude";
		public static final String COLUMN_NAME_LONGITUDE = "longitude";
	}

}
