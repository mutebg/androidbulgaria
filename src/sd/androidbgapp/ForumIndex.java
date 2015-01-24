package sd.androidbgapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ForumIndex extends ListActivity implements OnClickListener {
	
	public String jsonData;
	public String URL = "andapp/index.php?";
	public Map<String, String> URL_PARAMS; 
	public ProgressDialog Loading = null;
	public ListView lv = null;
	public ForumAdapter mSchedule = null;
	public ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	public static final String PREFS_NAME = "myprefs";
	
	//login
	public String jsonDataLogin;
	public String URL_LOGIN = "";
	public Map<String, String> URL_PARAMS_LOGIN; 
	public EditText userName;
	public EditText userPass;
	
	
	//notification
	public NotificationManager mNotificationManager;
    private int YOURAPP_NOTIFICATION_ID = 9999;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    	
    	
    	
    	
    	if ( settings.getBoolean("loged", false) ) {
    		myOnCreate();
    	} else {
    		finish();
    	}
    	
    }
    
    @Override
	protected void onResume() {
       super.onResume();
       
      /* SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
       if ( ! settings.getBoolean("loged", false) ) {
    	   startActivity(new Intent(this, LoginActivity.class));
       }*/
    }
    
    public void getData() {
    	
    
    	
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		//URL_PARAMS.put("hide", settings.getString("hidden_cats", ""));
		
		if ( settings.getBoolean("loged", false) ) {
		
			Loading = new ProgressDialog(this);
			Loading.setTitle("Зареждане...");
			Loading.setMessage("Моля изчакайте");
			Loading.setIndeterminate(true);
			Loading.setCancelable(true);
			
			if ( ! isFinishing() ) {
				Loading.show();
			}
			
			//URL += "&user=" + settings.getString("user_name", "");
			//URL += "&pass=" + settings.getString("user_pass", "");
			URL += "&hide=" + settings.getString("hidden_cats", "");
			
			//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
			
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						WebManager WM = new WebManager();
						jsonData = WM.getDataFromWeb(URL + "&code=" + WM.encode( URL_PARAMS) );
						handler.sendMessage(Message.obtain(handler, 1));
					} catch (Exception e) {
						new AlertDialog.Builder(ForumIndex.this)
							.setTitle( "Грешка" )
							.setMessage("Не може да осъществи връзка със сървара")
							.show();
						
						e.printStackTrace();
					}
				}
			};
	
			t.start();
		
		} else {
			finish();
		}
	}
    
    public void loadData() {
    	if ( jsonData.length() > 0 ) {
    		try {
	    		
	    		JSONObject data = new JSONObject(jsonData);
	    		JSONArray jsonArray = data.getJSONArray("list");
	    		
	    		if ( data.getInt("error") == 0 ) {
	    		
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject p = jsonArray.getJSONObject(i);
		
						HashMap<String, String> map = new HashMap<String, String>();
		
						map.put("forum_id", 	p.getString("forum_id"));
						map.put("forum_name", 	p.getString("forum_name"));
						map.put("new", 			p.getString("new"));
						map.put("forum_descr", 	p.getString("forum_descr"));
						
						mylist.add(map);
					}
					mSchedule.notifyDataSetChanged();
				} else { 
					Toast.makeText(getApplicationContext(), data.getString("error_message") , Toast.LENGTH_LONG ).show();
				}
	    		
	    		if ( data.getInt("pm") == 1 ) {
	    			
	    			mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    			
	    			int icon = R.drawable.icon_notification;
	    			CharSequence tickerText = "Ново лично съобщение";
	    			long when = System.currentTimeMillis();
	
	    			Notification notification = new Notification(icon, tickerText, when);
	    			
	    			Context context = getApplicationContext();
	    			CharSequence contentTitle = "Ново лично съобщение";
	    			CharSequence contentText = "www.androidbg.com";
	    			Intent notificationIntent = new Intent(this, ViewPMActivity.class);
	    			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
	
	    			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	    			mNotificationManager.notify(YOURAPP_NOTIFICATION_ID, notification);
	    		}
					
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	} else {
    		//getData();
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

	public void onClick(View arg0) {
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		if ( settings.getBoolean("loged", false) ) {
			menu.add(0, 0, 1, "Изход");
			menu.add(0, 1, 2, "Презареди");
			menu.add(0, 2, 3, "Настройки на категориите");
			menu.add(0, 4, 4, "Лични съобщения");
			menu.add(0, 6, 5, "Търсене");
			menu.add(0, 3, 6, "Общи настройки");
			menu.add(0, 5, 7, "За програмата");
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				logOut();
				finish();
				return true;
	
			case 1:
				mylist.clear();
				getData();
				return true;
			
			
			case 2:
				startActivityForResult( new Intent(this, CategorySettingsActivity.class ), 1 );
				return true;
				
			case 3:
				startActivity( new Intent(this, SettingsActivity.class) );
				return true;
			
			case 4:
				startActivity( new Intent(this, ViewPMActivity.class) ); 
				return true;
				
			case 5:
				
				new AlertDialog.Builder(this).setTitle(R.string.about_title)
				.setMessage( R.string.about_text )
				.setNeutralButton("Donate", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	 Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                             myWebLink.setData(Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=GTZ3VABBVUFFG&lc=BG&item_name=Stoyan%20Delev&item_number=Android%20Forum%20App&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted"));
                             startActivity(myWebLink); 
                        }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                                //Put your code in here for a neutral response
                        }
                })
                .show();
				
				return true;
				
			case 6:
				startActivity( new Intent(this, SearchActivity.class) ); 
				return true;
		}
		
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
			case 1:
				mylist.clear();
				mSchedule.notifyDataSetChanged();
				getData();
			break;
			
			case 2:
				//mylist.clear();
				//mSchedule.notifyDataSetChanged();
				//getData();
				myOnCreate(); 
			break;	
				
		}
	}
	
	public void myOnCreate() {
		URL = getResources().getText(R.string.BASE_URL) + URL;
    	
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		settings.getBoolean("loged", false);
    	
		lv = getListView();
		
    	mSchedule = new ForumAdapter(this, mylist);
		setListAdapter(mSchedule);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if ( mylist.size() > position ) {
					int forumID = Integer.parseInt( mylist.get(position).get("forum_id") );
					if ( forumID > 0 ) {
						//Toast.makeText(getApplicationContext(), "forumid:" + forumID , Toast.LENGTH_LONG ).show();
						Intent i = new Intent(ForumIndex.this, ViewForumActivity.class);
						i.putExtra("forum_id", forumID);
						startActivity(i);
					}
				}
			}
		});
		
		
		//if user is logged to forum
    	getData();
		
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			new AlertDialog.Builder(this).setTitle("Изход")
			.setMessage( "Искате ли да излезете от приложението" )
			.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	 logOut();
                    	 finish();
                    }
            })
            .setNegativeButton("Не", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                            //Put your code in here for a neutral response
                    }
            })
            .show();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	} 
	
	
	public void logOut() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("loged", false);
		editor.commit();
	}
	
	public void test() {
		/*WebManager WM = new WebManager();
		HashMap<String, String>test = new HashMap<String, String>();
		test.put("pass", "test" );
		test.put("user", "гери" );
		//System.out.println( WM.encode( test ) );
		
		String name = "гери";
			
		
		
		//System.out.println( name.charAt(1) );
		//Toast.makeText(getApplicationContext(), (int)'е' + "" , Toast.LENGTH_LONG ).show();
		
		String str = "гери";
		for ( int i = 0; i < str.length(); ++i ){
			char c = str.charAt(i);
			int j = (int) c;
			System.out.println("ASCII OF "+c +" = " + j + ".");
		}*/
		
		
	}
	
}