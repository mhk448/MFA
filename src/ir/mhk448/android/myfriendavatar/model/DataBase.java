package ir.mhk448.android.myfriendavatar.model;

import ir.mhk448.android.myfriendavatar.Main;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBase {

	private static DataBase instance;

	public static DataBase getInstance() {
		if (instance == null)
			instance = new DataBase();
		return instance;
	}

	public void init() {
		runQuery("Drop TABLE IF EXISTS contacts;");
		runQuery("CREATE TABLE IF NOT EXISTS contacts (number VARCHAR,uid VARCHAR,name VARCHAR,localId VARCHAR,imageType VARCHAR,imageURI VARCHAR);");
	}

	public void runQuery(String query) {
		System.out.println("query is: " + query);
		SQLiteDatabase db = connect();
		db.execSQL(query);
		db.close();
	}

	public SQLiteDatabase connect() {
		SQLiteDatabase db = Main.curContext.openOrCreateDatabase("mhk",
				Main.MODE_PRIVATE, null);
		return db;
	}

	public Object load() {
		return null;
	}

	public void update(String table, String name, String value) {

	}

	public void save(String table, String cols, String vals) {
		runQuery("insert into " + table + " (" + cols + ") VALUES (" + vals
				+ ");");
	}

	public List<Contact> loadContacts(int start, int len) {
		SQLiteDatabase db = connect();
		Cursor cur = db.rawQuery("SELECT * FROM contacts", null);

		List<Contact> contacts = new ArrayList<Contact>(cur.getCount());

		if (cur.moveToFirst()) {
			int numberIndex = cur.getColumnIndex("number");
			int nameIndex = cur.getColumnIndex("name");
			int localIdIndex = cur.getColumnIndex("localId");
			int imageTypeIndex = cur.getColumnIndex("imageType");
			int imageURIIndex = cur.getColumnIndex("imageURI");
			// int Index=cur.getColumnIndex("");

			do {
				Contact c = new Contact();
				c.addToNumbers(cur.getString(numberIndex));
				c.setName(cur.getString(nameIndex));
				c.setLocalId(cur.getString(localIdIndex));
				c.setImageType(cur.getString(imageTypeIndex));
				c.setImageURI(cur.getString(imageURIIndex));

				contacts.add(c);
			} while (cur.moveToNext());
		}
		db.close();
		
		return contacts;
	}

}
