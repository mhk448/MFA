package ir.mhk448.android.myfriendavatar.ui;

import ir.mhk448.android.myfriendavatar.R;
import ir.mhk448.android.myfriendavatar.model.Contact;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactList extends ArrayAdapter<Contact> {

	public ContactList(Context context, List<Contact> contacts) {
		super(context, R.layout.contact_item, R.layout.main, contacts);
		this.contacts = contacts;
		this.context = context;
	}

	private Context context;
	private List<Contact> contacts;

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// return super.getView(position, convertView, parent);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.contact_item, parent, false);

		TextView nameView = (TextView) rowView.findViewById(R.id.nameV);
		TextView numberView = (TextView) rowView.findViewById(R.id.numberV);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.photo);

		nameView.setText(contacts.get(position).getName());
		numberView.setText(contacts.get(position).getNumber());

		String us = contacts.get(position).getImageURI();
		if (us != null && us.trim().length() > 0) {
			Uri u = Uri.parse(us);
			if (u != null) {
				imageView.setImageURI(u);
			}
		}

		return rowView;
	}

}
