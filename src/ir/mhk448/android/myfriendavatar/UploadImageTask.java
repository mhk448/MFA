package ir.mhk448.android.myfriendavatar;

import ir.mhk448.android.myfriendavatar.model.Server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

/*
 * Asynchronous task to upload file to server
 */
class UploadImageTask extends AsyncTask<String, Integer, Boolean> {


	/**
	 * Prepare activity before upload
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * Clean app state after upload is completed
	 */
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	@Override
	protected Boolean doInBackground(String... imageAndUid) {
		File sourceFile = new File(imageAndUid[0]);
		if (!sourceFile.isFile()) {
			return false;
		}
		return doFileUpload(sourceFile, imageAndUid[1], Server.fileServerUrl);
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		// if (values[0] == 0) {
		// mDialog.setTitle(getString(R.string.progress_dialog_title_uploading));
		// }
		//
		// mDialog.setProgress(values[0]);
	}

	/**
	 * Upload given file to given url, using raw socket
	 * 
	 * @see http
	 *      ://stackoverflow.com/questions/4966910/androidhow-to-upload-mp3-file
	 *      -to-http-server
	 * 
	 * @param file
	 *            The file to upload
	 * @param uploadUrl
	 *            The uri the file is to be uploaded
	 * 
	 * @return boolean true is the upload succeeded
	 */
	private boolean doFileUpload(File file, String uid, String uploadUrl) {

		int serverResponseCode = 0;

		String fileName = "file";

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;

		if (!file.isFile()) {
			return false;
		} else {
			try {
				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(file);
				URL url = new URL(uploadUrl+"?uid="+uid);
				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);
				OutputStream os = conn.getOutputStream();
				dos = new DataOutputStream(os);
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename=" + fileName + "" + lineEnd);
				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();
				Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {
				}
				fileInputStream.close();
				dos.flush();
				dos.close();
				return true;
			} catch (MalformedURLException ex) {
				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
				return false;
			} catch (Exception e) {
				Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);
				return false;
			}

		} // End else block

	}

}
