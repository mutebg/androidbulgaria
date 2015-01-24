package sd.androidbgapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class ViewPMActivity extends ListActivity implements OnClickListener {

	public String jsonData;
	public String URL = "andapp/pm.php?";
	public String URL_GET = "";
	public Map<String, String> URL_PARAMS; 
	public ProgressDialog Loading = null;
	public ListView lv = null;
	public ViewPMAdapter mSchedule = null;
	public ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	public static final String PREFS_NAME = "myprefs";
	public WebManager WM;

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	mNotificationManager.cancel(9999);
    	
    	WM = new WebManager();
    	
    	URL_GET = getResources().getText(R.string.BASE_URL) + URL;
    	
    	
    	lv = getListView();
    	
    	LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.view_pm_header, lv, false);
		lv.addHeaderView(header, null, false);
		
		mSchedule = new ViewPMAdapter(this, mylist);
		setListAdapter(mSchedule);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if ( mylist.size() > position ) {	
					int messageID = Integer.parseInt( mylist.get( position -1 ).get("id") );
					String subject = mylist.get( position -1 ).get("subject");
					
					
					////////////////////////////////////////////////////////////////////////
					final TextView message = new TextView( ViewPMActivity.this );
					final ScrollView sv = new ScrollView( ViewPMActivity.this );
					
					
					
					final SpannableString s = new SpannableString(  Html.fromHtml( WebManager.bbcode( mylist.get( position -1 ).get("body") ) ) );
					Linkify.addLinks(s, Linkify.WEB_URLS);
					message.setText(s);
					message.setPadding(10, 10, 10, 10);
					message.setTextColor( getResources().getColor(R.color.active_color) );
					message.setMovementMethod(LinkMovementMethod.getInstance());
					
					sv.addView( message );
					
					new AlertDialog.Builder( ViewPMActivity.this )
					   .setTitle( subject )
					   .setNegativeButton("Затвори", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							//Put your code in here for a neutral response
							}
					   	})
					   .setView(sv)
					   .create()
					   .show();
					
					ImageView img = (ImageView)view.findViewById(R.id.forum_icon);
					img.setImageResource(R.drawable.icon_unactive);
					markMessageAsRead(messageID);
				}
			}
		});
		
		//back button
		ImageButton backBtn = (ImageButton)findViewById(R.id.btn_back_to_index);
		backBtn.setOnClickListener( this );
		
		getData();
	}
    
    @Override
	protected void onResume() {
    	super.onResume();
        
        //getData();
     }
    
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch ( v.getId() ) {
			case R.id.btn_back_to_index:
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
    
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		
		URL_GET += "&section=pun_pm&pmpage=inbox&";
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					
					jsonData = WM.getDataFromWeb(URL_GET + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 1));
				} catch (Exception e) {
					new AlertDialog.Builder(ViewPMActivity.this)
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
    	
    	//System.out.println( jsonData );
    	
    	try {
    		JSONObject data = new JSONObject(jsonData);
    		
    		if ( data.getInt("error") == 0 ) {
    		
				JSONArray jsonArray = data.getJSONArray("list");
	
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject p = jsonArray.getJSONObject(i);
	
					HashMap<String, String> map = new HashMap<String, String>();
	
					map.put("id", 			p.getString("id"));
					map.put("status", 		p.getString("status"));
					map.put("user_id", 		p.getString("user_id"));
					map.put("subject", 		p.getString("subject"));
					map.put("body",			p.getString("body"));
					map.put("sent_at", 		p.getString("sent_at"));
					map.put("username",		p.getString("username"));
					
					mylist.add(map);
				}
			
    		}
			
			mSchedule.notifyDataSetChanged();

		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    }
    
    
    public void markMessageAsRead(int messageID) {
    	
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		
		URL_GET = getResources().getText(R.string.BASE_URL) + URL + "&section=read&message_id=" + messageID;
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WM.getDataFromWeb(URL_GET + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 2));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		t.start();
	}
    
	
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					Loading.dismiss();
					loadData();
					break;
					
				case 2:
					
					break;
			}
				
		}
	};
}