package ir.mhk448.android.myfriendavatar;

import ir.mhk448.android.myfriendavatar.model.Contact;

import java.util.List;

import android.os.AsyncTask;

public class LoadImage extends AsyncTask<String, Void, Boolean> {

	
	@Override
	protected Boolean doInBackground(String... params) {

		List<Contact> serverContacts = Contact.loadServer();
		Contact.updateLocal(serverContacts);
		
		List<Contact> ic= Contact.ListOfHasImages();
		for (Contact c : ic) {
			c.addImageToContact(c.downloadImage());
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		new init().execute(new String[] {});
	}
}
