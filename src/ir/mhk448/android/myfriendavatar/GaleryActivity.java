package ir.mhk448.android.myfriendavatar;

import ir.mhk448.android.myfriendavatar.model.Contact;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GaleryActivity extends Activity {

	public static final int DIALOG_LOADING_PROGRESS = 0;
	private ProgressDialog mProgressDialog;

	private GridView gidView;
	private ImageAdapter imageAdapter;
	Contact curContact;
	// ArrayList<HashMap<String, Object>> MyArrList = new
	// ArrayList<HashMap<String, Object>>();
	ArrayList<File> MyArrList = new ArrayList<File>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		curContact =  new Contact(i.getExtras().getString("contact"));
		// ProgressBar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.galery);

		// gridView1 and imageAdapter
		gidView = (GridView) findViewById(R.id.gridView1);
		gidView.setClipToPadding(false);
		imageAdapter = new ImageAdapter(getApplicationContext());
		gidView.setAdapter(imageAdapter);

		new LoadContentFromServer().execute();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_LOADING_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Loading Gallery..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	private static int RESULT_LOAD_IMAGE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();
			// ImageView imageView = (ImageView) findViewById(R.id.imgView);
			// imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

		}

	}

	class LoadContentFromServer extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			showDialog(DIALOG_LOADING_PROGRESS);
		}

		@Override
		protected String doInBackground(String... params) {

			// String[] Thumb = { MediaStore.Images.Thumbnails._ID };
//			String dir = Environment.getExternalStorageDirectory()
//					.getAbsolutePath() + "/viber/media/User photos";
			String dir = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/DCIM/Camera";
			File f = new File(dir);
			// Uri uri0 = Uri.fromFile(f);

			File fs[] = f.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return true;//filename.endsWith("jpg");
				}
			});

			Arrays.sort(fs, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified()).compareTo(
							f2.lastModified());
				}
			});
			MyArrList.addAll(Arrays.asList(fs));

			return null;
		}

		@Override
		public void onProgressUpdate(String... progress) {
			imageAdapter.notifyDataSetChanged();
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			setProgressBarIndeterminateVisibility(false); // When Finish
			dismissDialog(DIALOG_LOADING_PROGRESS);
			removeDialog(DIALOG_LOADING_PROGRESS);
		}
	}

	class ImageAdapter extends BaseAdapter {

		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public int getCount() {
			return MyArrList.size();
		}

		public Object getItem(int position) {
			return MyArrList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(2, 2, 2, 2);
			imageView.setImageURI(Uri.fromFile(MyArrList.get(position)));
			// imageView.setImageBitmap((Bitmap)MyArrList.get(position).get("ImageBitmap"));
			final int p=position;
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String s = String.valueOf(MyArrList.size());
					Toast.makeText(Main.curContext, s+MyArrList.get(p).getPath(), Toast.LENGTH_LONG).show();
					new UploadImageTask().execute(MyArrList.get(p).getPath(), curContact.getNumber());
					curContact.addImageToContact2(MyArrList.get(p).getPath());
				}
			});
			return imageView;
		}

	}

}
