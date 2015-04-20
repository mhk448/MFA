package ir.mhk448.android.myfriendavatar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class Main extends Activity {

	public static Context curContext;
	public static Main THIS;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		curContext = this;
		THIS=this;
		new init().execute(new String[]{});
		
		// startActivity(new Intent(Main.this,SettingActivity.class));
		// if (Server.isConnected()) {

		// }

		// call AsynTask to perform network operation on separate thread
		// new
		// HttpAsyncTask().execute("http://hmkcode.appspot.com/rest/controller/get.json");

	}

	public static void showToster(String text) {
		Toast t = Toast.makeText(curContext, text, Toast.LENGTH_LONG);
		t.show();
	}

	
}
