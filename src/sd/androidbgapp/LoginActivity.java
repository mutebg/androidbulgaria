package sd.androidbgapp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	public ProgressDialog Loading = null;
	public String jsonData;
	public String URL = "";
	public Map<String, String> URL_PARAMS; 
	
	public EditText userName;
	public EditText userPass;
	
	public static final String PREFS_NAME = "myprefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		
		userName = (EditText)findViewById(R.id.user_name);
		userPass = (EditText)findViewById(R.id.user_pass);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		userName.setText( settings.getString("user_name", "") );
		userPass.setText( settings.getString("user_pass", "") );
		
		Button btnLogin = (Button)findViewById(R.id.user_login_btn);
		btnLogin.setOnClickListener(this);
		
		
		MovementMethod mm = LinkMovementMethod.getInstance();
		
		TextView linkReg = (TextView)findViewById(R.id.btn_link_registration);
		TextView linkForget = (TextView)findViewById(R.id.btn_link_forgotpass);
		
		linkReg.setText( Html.fromHtml("<a href='http://www.androidbg.com/forum/register.html'>Регистрация</a>") );
		linkForget.setText( Html.fromHtml("<a href='http://www.androidbg.com/forum/request-password.html'>Забравена парола</a> ") );
		
		linkReg.setMovementMethod(mm);
		linkForget.setMovementMethod(mm);
		
	}

	
	
	@Override
	protected void onResume() {
	    super.onResume();
	        
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
	    
	    if ( settings.getBoolean("loged", false) ) {
	    	startActivity(new Intent(this, ForumIndex.class));
	    }
	}	
		
	// LOGIN
	public void getData() {
		
		

		Loading = new ProgressDialog(this);
		Loading.setTitle("Зареждане...");
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		Loading.show();

		URL = getResources().getText(R.string.BASE_URL) + "andapp/login.php?";//form_sent=1&user=" +  userName.getText().toString() + "&pass=" + userPass.getText().toString();

		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("form_sent", "1" );
		URL_PARAMS.put("user", userName.getText().toString() );
		URL_PARAMS.put("pass", userPass.getText().toString() );
		
		// Here is the heavy-duty thread
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL + "code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		t.start();
	}

	public void loadData() {

		//Toast.makeText(getApplicationContext(), jsonData , Toast.LENGTH_LONG ).show();
		
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			
			int error = jsonObject.getInt("error");
			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			
			if ( error == 0 ) {
				
				int userID = jsonObject.getInt("user_id");
				int userGroup = jsonObject.getInt("user_group");
				
				editor.putBoolean("loged", true);
				editor.putInt("user_id", userID);
				editor.putInt("user_group", userGroup);
				editor.putString("user_name", userName.getText().toString());
				editor.putString("user_pass", userPass.getText().toString());
				editor.commit();
					
				//setResult(RESULT_OK);
				startActivity(new Intent(this, ForumIndex.class ) );

			} else {
				editor.putBoolean("loged", false);
				Toast.makeText(getApplicationContext(), "Грешно потребителско име или парола" , Toast.LENGTH_LONG ).show();
			}
			
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), "Грешка" , Toast.LENGTH_LONG ).show();
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Loading.dismiss();
				loadData();
				break;
			}
		}
	};

	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		getData();
	}

}