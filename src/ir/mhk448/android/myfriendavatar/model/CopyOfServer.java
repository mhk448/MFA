package ir.mhk448.android.myfriendavatar.model;

import ir.mhk448.android.myfriendavatar.Main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CopyOfServer {

	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}
	
	public void postData() {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("id", "12345"));
	        nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
//	        if(response.)

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	} 
	

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}

	public static boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) Main.curContext
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(Main.curContext, "Received!",
					Toast.LENGTH_LONG).show();
			try {
				JSONObject json = new JSONObject(result);
				String str = "";
				JSONArray articles = json.getJSONArray("articleList");
				str += "articles length = "
						+ json.getJSONArray("articleList").length();
				str += "\n--------\n";
				str += "names: " + articles.getJSONObject(0).names();
				str += "\n--------\n";
				str += "url: " + articles.getJSONObject(0).getString("url");
				// etResponse.setText(str);
				// etResponse.setText(json.toString(1));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// final String uploadFilePath = "/mnt/sdcard/";
	// final String uploadFileName = "service_lifecycle.png";
	static String upLoadServerUri = "http://192.168.1.13/index.php";

	public static String uploadFile(String sourceFileUri) {

		int serverResponseCode = 0;

		String line = "0";

		String fileName = sourceFileUri;

		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = new File(sourceFileUri);

		line += "1";
		if (!sourceFile.isFile()) {

			// dialog.dismiss();

			// Log.e("uploadFile", "Source File not exist :"
			// +uploadFilePath + "" + uploadFileName);

			// runOnUiThread(new Runnable() {
			// public void run() {
			// // messageText.setText("Source File not exist :"
			// // +uploadFilePath + "" + uploadFileName);
			// }
			// });
			line += "2";
			return "not file";

		} else {
			try {

				// open a URL connection to the Servlet
				FileInputStream fileInputStream = new FileInputStream(
						sourceFile);
				line += "3";
				URL url = new URL(upLoadServerUri);
				line += "4";
				// Open a HTTP connection to the URL
				conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true); // Allow Inputs
				conn.setDoOutput(true); // Allow Outputs
				conn.setUseCaches(false); // Don't use a Cached Copy
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("ENCTYPE", "multipart/form-data");
				conn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				conn.setRequestProperty("uploaded_file", fileName);
				line += "5";
				OutputStream os = conn.getOutputStream();
				line += "-5.5-";
				dos = new DataOutputStream(os);
				line += "6";
				dos.writeBytes(twoHyphens + boundary + lineEnd);
				dos.writeBytes("Content-Disposition: form-data; name=uploaded_file;filename="
						+ fileName + "" + lineEnd);
				line += "7";
				dos.writeBytes(lineEnd);

				// create a buffer of maximum size
				bytesAvailable = fileInputStream.available();

				line += "8";
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				buffer = new byte[bufferSize];

				line += "9";
				// read file and write it into form...
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);

				while (bytesRead > 0) {

					dos.write(buffer, 0, bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				}
				line += "-10";

				// send multipart form data necesssary after file data...
				dos.writeBytes(lineEnd);
				dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
				line += "-11";
				// Responses from the server (code and message)
				serverResponseCode = conn.getResponseCode();
				String serverResponseMessage = conn.getResponseMessage();
				line += "-12";
				Log.i("uploadFile", "HTTP Response is : "
						+ serverResponseMessage + ": " + serverResponseCode);

				if (serverResponseCode == 200) {

					// runOnUiThread(new Runnable() {
					// public void run() {
					//
					// String msg =
					// "File Upload Completed.\n\n See uploaded file here : \n\n"
					// +" http://www.androidexample.com/media/uploads/"
					// +uploadFileName;
					//
					// messageText.setText(msg);
					// Toast.makeText(UploadToServer.this,
					// "File Upload Complete.",
					// Toast.LENGTH_SHORT).show();
					// }
					// });
				}
				line += "-13";
				// close the streams //
				fileInputStream.close();
				dos.flush();
				dos.close();
				return "succss";
			} catch (MalformedURLException ex) {

				// dialog.dismiss();
				ex.printStackTrace();

				// runOnUiThread(new Runnable() {
				// public void run() {
				// messageText.setText("MalformedURLException Exception : check script url.");
				// Toast.makeText(UploadToServer.this, "MalformedURLException",
				// Toast.LENGTH_SHORT).show();
				// }
				// });

				Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
				return "ex" + ex.getMessage();
			} catch (Exception e) {

				// dialog.dismiss();
				e.printStackTrace();

				// runOnUiThread(new Runnable() {
				// public void run() {
				// messageText.setText("Got Exception : see logcat ");
				// Toast.makeText(UploadToServer.this,
				// "Got Exception : see logcat ",
				// Toast.LENGTH_SHORT).show();
				// }
				// });
				Log.e("Upload file to server Exception",
						"Exception : " + e.getMessage(), e);
				return "line " + line + " " + e.getMessage();
			}
			// dialog.dismiss();
			// return serverResponseCode;

		} // End else block
	}

	private DefaultHttpClient mHttpClient;

	public CopyOfServer() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(params);
	}

	public String uploadUserPhoto(String sourceFileUri) {

		try {
			File image = new File(sourceFileUri);
			HttpPost httppost = new HttpPost(upLoadServerUri);

			MultipartEntity multipartEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartEntity.addPart("Title", new StringBody("Title"));
			multipartEntity.addPart("Nick", new StringBody("Nick"));
			multipartEntity.addPart("Email", new StringBody("Email"));
			multipartEntity.addPart("image", new FileBody(image));
			httppost.setEntity(multipartEntity);

			mHttpClient.execute(httppost, new PhotoUploadResponseHandler());
			return "suc";
		} catch (Exception e) {
			Log.e(CopyOfServer.class.getName(), e.getLocalizedMessage(), e);
			return "fail";
		}
	}
}

class PhotoUploadResponseHandler implements ResponseHandler<Object> {

	@Override
	public Object handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {

		HttpEntity r_entity = response.getEntity();
		String responseString = EntityUtils.toString(r_entity);
		Log.d("UPLOAD", responseString);

		return null;
	}

}
