package ir.mhk448.android.myfriendavatar.model;

import java.util.UUID;

import ir.mhk448.android.myfriendavatar.Main;

import org.json.JSONArray;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class Setting {

	private static Setting instance;
	public static Setting getInstance() {
		if (instance == null)
			instance = new Setting();
		return instance;
	}
	
	
	private String number;
	private String userUid;
	private long lastUpdate;
	private String phoneInfo;
	private String lastPhotoName;
	private boolean initSetup;

	SharedPreferences pref;
	SharedPreferences.Editor prefEdit;

	public Setting() {
		pref = PreferenceManager.getDefaultSharedPreferences(Main.curContext);
		prefEdit = pref.edit();
	}

	public boolean getRemoveOldPhoto() {
		return pref.getBoolean("removeoldphoto", false);
	}

	public String getNumber() {
		if (number == null)
			number = pref.getString("number", "");
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
		prefEdit.putString("number", number);
		prefEdit.commit();
	}

	public String getUserUid() {
		if (userUid == null){
			userUid = pref.getString("useruid", "");
			if(userUid.length()==0){
				setUserUid(UUID.randomUUID().toString());
			}
		}
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
		prefEdit.putString("useruid", userUid);
		prefEdit.commit();
	}

	public long getLastUpdate() {
		if (lastUpdate == 0)
			lastUpdate = pref.getLong("lastupdate", 0);
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
		prefEdit.putLong("lastupdate", lastUpdate);
		prefEdit.commit();
	}

	public String getPhoneInfo() {
		if (phoneInfo == null)
			phoneInfo = pref.getString("phoneinfo", "");
		return phoneInfo;
	}

	public void setPhoneInfo(String phoneInfo) {
		this.phoneInfo = phoneInfo;
		prefEdit.putString("phoneinfo", phoneInfo);
		prefEdit.commit();
	}

	public String getLastPhotoName() {
		if (lastPhotoName == null)
			lastPhotoName = pref.getString("lastphotoname", "");
		return lastPhotoName;
	}

	public void setLastPhotoName(String lastPhotoName) {
		this.lastPhotoName = lastPhotoName;
		prefEdit.putString("lastphotoname", phoneInfo);
		prefEdit.commit();
	}

	public boolean isInitSetup() {
		initSetup = pref.getBoolean("initSetup", false);
		return initSetup;
	}

	public void setInitSetup(boolean initSetup) {
		this.initSetup = initSetup;
		prefEdit.putBoolean("initSetup", initSetup);
		prefEdit.commit();
	}

	public static JSONArray readAccountsAndSetNumber() {

		JSONArray ja = new JSONArray();

		Account[] accounts = AccountManager.get(Main.curContext).getAccounts();
		for (Account account : accounts) {
			try {
				JSONObject jo = new JSONObject();
				jo.put("server", account.type);
				jo.put("name", account.name);
				ja.put(jo);
				String n = Contact.CorrectNumber(account.name);

				if (Contact.isMobile(n)) {
					getInstance().setNumber(n);
				}
			} catch (Exception e) {
			}
		}
		try {
			JSONObject jo = new JSONObject();
			TelephonyManager tMgr = (TelephonyManager) Main.curContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneNumber = tMgr.getLine1Number();
			jo.put("server", "hard");
			jo.put("name", mPhoneNumber);
			ja.put(jo);
		} catch (Exception e) {
		}
		return ja;
	}

}
