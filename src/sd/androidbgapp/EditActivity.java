package sd.androidbgapp;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EditActivity extends Activity implements OnClickListener {

	public String jsonData;
	public String URL = "andapp/edit.php?";
	public Map<String, String> URL_PARAMS; 
	public static final String PREFS_NAME = "myprefs";
	public ProgressDialog Loading = null;
	public int postID;
	

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.reply);
    	
    	URL = getResources().getText(R.string.BASE_URL) + URL;
    	
    	Bundle b = this.getIntent().getExtras();
    	String postMessage = b.getString("message"); 
		postID = b.getInt("id");
    	
		Button btnSend 		= (Button)findViewById(R.id.btn_send_reply);
		Button btnCancel 	= (Button)findViewById(R.id.btn_cancel_reply);
		TextView message = (TextView) findViewById(R.id.reply_message);
		
		btnSend.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		message.setText(postMessage);
		
	}
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch ( v.getId() ) {
			case R.id.btn_send_reply:
				getData();
			break;
			
			case R.id.btn_cancel_reply:
				setResult(RESULT_CANCELED);
				finish();
			break;	
		}
	}
	
	public void getData() {
    	Loading = new ProgressDialog(this);
		Loading.setTitle("Зареждане...");
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		Loading.show();
    
		TextView message = (TextView) findViewById(R.id.reply_message);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		URL += "&form_sent=1";
		URL += "&id=" + postID;
		URL += "&req_message=" + URLEncoder.encode( message.getText().toString() );
		//URL += "&user=" + settings.getString("user_name", "");
		//URL += "&pass=" + settings.getString("user_pass", "");
		
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		//URL_PARAMS.put("form_sent", "1");
		//URL_PARAMS.put("tid", "" + topicID);
		//URL_PARAMS.put("req_message", message.getText().toString() );
		
		
		 System.out.println( URL );
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 1));
				} catch (Exception e) {
					new AlertDialog.Builder(EditActivity.this)
						.setTitle( "Грешка" )
						.setMessage("Не може да осъществи връзка със сървара")
						.show();
					
					e.printStackTrace();
				}
			}
		};

		t.start();
	}
    
    public void loadData() {
    	
    	try {
    		JSONObject data = new JSONObject(jsonData);
    		
    		if ( data.getInt("error") == 0 ) {
    			
    			setResult(RESULT_OK);
				finish();
    			
    		} else {
    			new AlertDialog.Builder(EditActivity.this)
				.setTitle( "Грешка" )
				.setMessage( data.getString("error_message") )
				.show();
    		}
		} catch (JSONException e) {
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
	
	
	
}