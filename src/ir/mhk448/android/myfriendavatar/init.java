package ir.mhk448.android.myfriendavatar;

import ir.mhk448.android.myfriendavatar.model.Contact;
import ir.mhk448.android.myfriendavatar.model.DataBase;
import ir.mhk448.android.myfriendavatar.model.Server;
import ir.mhk448.android.myfriendavatar.model.Setting;
import ir.mhk448.android.myfriendavatar.ui.ContactList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class init extends AsyncTask<String, Void, List<Contact>> {

	public ContactList contactList;

	@Override
	protected List<Contact> doInBackground(String... arg0) {

		boolean setup = Setting.getInstance().isInitSetup();
		setup = false;
		List<Contact> contacts = new ArrayList<Contact>();
		if (!setup) {
			DataBase.getInstance().init();
			contacts = Contact.fetchContacts();
//			JSONArray ja = Setting.readAccountsAndSetNumber();
//			Contact.SaveLocal(contacts);
//			Server.getInstance().postData("", "init", ja);
//			Contact.SaveServer(contacts);

			Setting.getInstance().setInitSetup(true);
		} else {
			contacts = Contact.loadLocal(0, 0);
		}
		// startDownloadThread

		// String baseDir = Environment.getExternalStorageDirectory()
		// .getAbsolutePath();
		// String s=Server.uploadFile(baseDir+"/viber/media/User photos/g");
		// String s = new Server().uploadUserPhoto("system/etc/hosts");

		return contacts;
	}

	@Override
	protected void onPostExecute(List<Contact> contacts) {
		contactList = new ContactList(Main.THIS, contacts);
		ListView lv = (ListView) Main.THIS.findViewById(R.id.list);
		lv.setAdapter(contactList);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int position, long id) {
				Contact contact = contactList.getItem(position);
				 Intent i=new Intent(Main.THIS,GaleryActivity.class);
				 i.putExtra("contact", contact.getJson().toString());
				 Main.THIS.startActivity(i);
//				contact.addImageToContact2("android.resource://ir.mhk448.android.myfriendavatar/drawable/ic_action_add.png");

			}
		});
	}
}
