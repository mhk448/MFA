package ir.mhk448.android.myfriendavatar.model;

import ir.mhk448.android.myfriendavatar.Main;
import ir.mhk448.android.myfriendavatar.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;

public class Contact {

	private String name;
	private List<String> numbers;
	private String imageURI;
	private String localId;
	private String uid;
	// private String id;
	private String imageType = IMAGE_TYPE_EMPTY;

	public static final String IMAGE_TYPE_EMPTY = "EMPTY";
	public static final String IMAGE_TYPE_PRIVATE = "PRIVATE";
	public static final String IMAGE_TYPE_VIBER = "VIBER";
	public static final String IMAGE_TYPE_WHATSAPP = "WHATSAPP";
	public static final String IMAGE_TYPE_LINE = "LINE";

	public Contact() {
		numbers = new LinkedList<String>();
	}

	public Contact(String jsonObject) {
		this();
		try {
			JSONObject j = new JSONObject(jsonObject);
			this.setName(j.getString("name"));
			this.setLocalId(j.getString("localId"));

		} catch (JSONException e) {
		}
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	// public String getId() {
	// return id;
	// }
	//
	// public void setId(String id) {
	// this.id = id;
	// }

	public String getName() {
		if (name == null)
			return "";
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getNumber() {
		if (numbers.size() > 0)
			if (numbers.get(0) != null)
				return numbers.get(0);
		return "";
	}

	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}

	public void addToNumbers(String phoneNumber) {
		numbers.add(phoneNumber);
	}

	public JSONObject getJson() {
		JSONObject out = new JSONObject();
		try {
			out.put("name", getName());
			out.put("localId", getLocalId());
			// TODO
		} catch (JSONException e) {
		}
		return out;
	}

	public static List<Contact> fetchContacts() {

		List<Contact> data = new LinkedList<Contact>();
		String phoneNumber = null;

		ContentResolver contentResolver = Main.curContext.getContentResolver();

		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		if (cursor.getCount() > 0) {
			int i = 0;
			while (cursor.moveToNext()) {
				Contact c = new Contact();
				boolean mustAdd = false;
				i++;
				if (i > 10) {
					// TODO
					break;
				}

				String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				c.setLocalId(contact_id);
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

				if (hasPhoneNumber > 0) {

					c.setName(name);

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contact_id }, null);

					while (phoneCursor.moveToNext()) {
						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						String num = CorrectNumber(phoneNumber);
						if (isMobile(num)) {
							mustAdd = true;
							c.addToNumbers(num);
						}
					}

					phoneCursor.close();

					Uri uri = getPhotoUriById(cursor, contact_id);
					if (uri != null) {
						c.setImageType(IMAGE_TYPE_PRIVATE);
						c.setImageURI(uri.toString());
					}
				}
				if (mustAdd)
					data.add(c);
			}

		}
		return data;
	}

	// private static Uri getPhotoUri(Cursor cur, String id) {
	// new URI(cur
	// .getString(cur
	// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)));
	//
	// try {
	// // Cursor cur = cursor.getContentResolver().query(
	// // ContactsContract.Data.CONTENT_URI,
	// // null,
	// // ContactsContract.Data.CONTACT_ID + "=" + this.getId() + " AND "
	// // + ContactsContract.Data.MIMETYPE + "='"
	// // + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'",
	// // null,
	// // null);
	// if (cur != null) {
	// // if (!cur.moveToFirst()) {
	// // return null; // no photo
	// // }
	// } else {
	// return null; // error in cursor process
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// Uri person = ContentUris.withAppendedId(
	// ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
	// return Uri.withAppendedPath(person,
	// ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
	// }

	public static Uri getPhotoUriById(Cursor cursor, String id) {
		Uri photo = Uri.parse("android.resource://" + Main.curContext.getPackageName() + "/" + R.drawable.ic_launcher);

		if (id == null || cursor == null)
			return photo;

		try {
			String sUri;
			if ((sUri = cursor.getString(cursor.getColumnIndex("photo_uri"))) != null) {
				return Uri.parse(sUri);
			}
			return photo;
		} catch (Exception e) {
			photo = ContentUris.withAppendedId(Contacts.CONTENT_URI, Long.valueOf(id));
			photo = Uri.withAppendedPath(photo, Contacts.Photo.CONTENT_DIRECTORY);
			return photo;
		}
	}

	public static String CorrectNumber(String num) {// +989XXXX... 912 78 78 788
		// - 21 77 78 78 88
		num = num.replace("-", "");
		int l = num.length();
		if (l < 10)
			return null;
		if (l > 15)
			return null;

		if (num.startsWith("+98"))
			return num;

		if (num.startsWith("98"))
			return "+" + num;

		if (l == 11 && num.startsWith("0"))
			return "+98" + num.substring(1);

		return null;

	}

	public static boolean isMobile(String correctedNum) {
		if (correctedNum == null)
			return false;
		return correctedNum.startsWith("+989");
	}

	public static boolean SaveLocal(List<Contact> contacts) {

		String cols = "number ,name ,localId ,imageType ,imageURI";

		StringBuilder vals = new StringBuilder();

		for (Contact contact : contacts) {

			for (String num : contact.numbers) {
				vals.append("('");
				vals.append(num);
				vals.append("','");
				vals.append(contact.getName());
				vals.append("','");
				vals.append(contact.getLocalId());
				vals.append("','");
				vals.append(contact.getImageType());
				vals.append("','");
				vals.append(contact.getImageURI());
				vals.append("')");
				vals.append(",");
			}

		}

		DataBase.getInstance().save("contacts", cols, vals.substring(1, vals.length() - 2));

		return true;
	}

	public static void SaveServer(List<Contact> contacts) {

		JSONArray cs = new JSONArray();
		for (Contact contact : contacts) {
			cs.put(contact.getJson());
		}
		Server.getInstance().postData("", "contact", cs);
	}

	public static void updateLocal(List<Contact> serverContacts) {

	}

	public static List<Contact> loadLocal(int start, int len) {
		return DataBase.getInstance().loadContacts(start, len);
	}

	public static List<Contact> loadServer() {
		// TODO Auto-generated method stub
		return null;
	}

	public static List<Contact> ListOfHasImages() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] downloadImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addImageToContact(String imagePath) {
		File file = new File(imagePath);
		int size = (int) file.length();
		byte[] bytes = new byte[size];
		try {
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
			buf.read(bytes, 0, bytes.length);
			buf.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		addImageToContact(bytes);
	}

	public void addImageToContact(byte[] imageBytes) {

		ContentResolver contentResolver = Main.curContext.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(ContactsContract.Data.RAW_CONTACT_ID, getLocalId());
		values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
		values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, imageBytes);
		values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

		Uri uri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, Long.parseLong(getLocalId()));
		contentResolver.update(uri, values, ContactsContract.Contacts._ID + " = " + getLocalId(), null);
		// Uri dataUri =
		// ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI,
		// dataId);
		// contentResolver.update(dataUri , values, null, null);

		// TODO Auto-generated method stub
		// update db

	}

	public void addImageToContact2(String imagePath) {
		// int id = 1;
		// String firstname = "Contact's first name";
		// String lastname = "Last name";
		// String number = "000 000 000";
		// String photo_uri =
		// "android.resource://com.my.package/drawable/default_photo";

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		// // Name
		// Builder builder =
		// ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
		// builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" +
		// " AND " + ContactsContract.Data.MIMETYPE + "=?", new
		// String[]{String.valueOf(id),
		// ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE});
		// builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
		// lastname);
		// builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
		// firstname);
		// ops.add(builder.build());

		// // Number
		// builder =
		// ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
		// builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" +
		// " AND " + ContactsContract.Data.MIMETYPE + "=?"+ " AND " +
		// ContactsContract.CommonDataKinds.Organization.TYPE + "=?", new
		// String[]{String.valueOf(id),
		// ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
		// String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)});
		// builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
		// number);
		// ops.add(builder.build());

		// Picture
		try {
			File file = new File(imagePath);
			int size = (int) file.length();
			byte[] bytes = new byte[size];
			try {
				BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
				buf.read(bytes, 0, bytes.length);
				buf.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
//			Bitmap bitmap = MediaStore.Images.Media.getBitmap(Main.curContext.getContentResolver(), Uri.parse(imagePath));
//			ByteArrayOutputStream image = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, image);

			Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
			builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?", new String[] { getLocalId() });
			builder.withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes);
			ops.add(builder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Update
		try {
			Main.curContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return getJson().toString();
	}
}
