package sd.androidbgapp;



import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		
		Preference themePref = findPreference("theme");
		themePref.setOnPreferenceChangeListener(
				new Preference.OnPreferenceChangeListener() {
	            	public boolean onPreferenceChange(Preference preference, Object newValue) {
	                	
	            		Toast.makeText(getApplicationContext(), "Промяната ще настръпи при следващото стартиране на програмата" , Toast.LENGTH_LONG ).show();
	                    
	            		return true;
	            	}
				}
			);
		
		
	}

	
	int getTopicPerPage( Context context ) {
		String pp = PreferenceManager.getDefaultSharedPreferences(context).getString("topic_per_page", "25");
		return Integer.parseInt(pp);
	}
	
	public int getAnswersPerPage( Context context ) {
		String pp = PreferenceManager.getDefaultSharedPreferences(context).getString("answer_per_page", "10");
		return Integer.parseInt(pp);
	}
	
	public int getTopicOpenType( Context context ) {
		String pp = PreferenceManager.getDefaultSharedPreferences(context).getString("open_topic_type", "0");
		return Integer.parseInt(pp);
	}
	
	public int getTheme( Context context ) {
		String pp = PreferenceManager.getDefaultSharedPreferences(context).getString("theme", "0");
		int themeID = Integer.parseInt(pp);
		return getThemeID(themeID);
	}
	
	private int getThemeID(int themeID ) {
		switch(themeID) {
			case 1: return R.style.black_theme;
		}
		return R.style.app_theme;
	}
	
	/*public boolean isLogin() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return settings.getBoolean("loged", false);
	}
	
	public int getUserID() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return settings.getInt("user_id", 0);
	}
	
	public int getUserGroup() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return settings.getInt("user_group", 0);
	}
	
	public String getUserName() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return settings.getString("user_name", "");
	}
	
	public String getUserPass() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return settings.getString("user_pass", "");
	}*/
	
	
}