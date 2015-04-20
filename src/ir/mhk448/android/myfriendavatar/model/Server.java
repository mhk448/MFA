package ir.mhk448.android.myfriendavatar.model;

import ir.mhk448.android.myfriendavatar.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Server {

	public final static String serverUrl = "http://192.168.1.15/";
	public final static String fileServerUrl = "http://192.168.1.15/";

	private static Server instance;

	public static Server getInstance() {
		if (instance == null)
			instance = new Server();
		return instance;
	}

	public JSONObject GET(String url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			return convertToJson(httpResponse.getEntity().getContent());

		} catch (Exception e) {
		}
		return null;
	}

	private JSONObject convertToJson(InputStream inputStream) throws IOException, JSONException {
		String result = "";
		if (inputStream != null)
			result = convertInputStreamToString(inputStream);

		if (result == null || result.length() == 0) {
			return new JSONObject();
		} else {
			return new JSONObject(result);
		}
	}

	public JSONObject postData(String path, String method, JSONArray data) {
		JSONObject dataO = new JSONObject();
		try {
			dataO.put("items", data);
		} catch (JSONException e) {
		}
		return this.postData(path, method, dataO);
	}

	public JSONObject postData(String path, String method, JSONObject data) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(serverUrl + path);

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
			nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
			nameValuePairs.add(new BasicNameValuePair("time", String.valueOf(new Date().getTime())));
			nameValuePairs.add(new BasicNameValuePair("phone", Setting.getInstance().getNumber()));
			nameValuePairs.add(new BasicNameValuePair("uid", Setting.getInstance().getUserUid()));
			nameValuePairs.add(new BasicNameValuePair("method", method));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			return convertToJson(response.getEntity().getContent());
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
		inputStream.close();
		return result;
	}

	public static boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) Main.curContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	
	
	
	
	

}
