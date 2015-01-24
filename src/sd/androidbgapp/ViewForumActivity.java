package sd.androidbgapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewForumActivity extends ListActivity implements OnClickListener {

	public String jsonData;
	public String URL = "andapp/viewforum.php?";
	public String URL_MARK_READ = "andapp/misc.php?";
	public Map<String, String> URL_PARAMS; 
	public ProgressDialog Loading = null;
	public ListView lv = null;
	public ViewForumAdapter mSchedule = null;
	public ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	public static final String PREFS_NAME = "myprefs";
	public int forumID;
	public int topicID;
	public int page;
	public int perpage = 25;
	public int perpageTopics = 10;
	public int openType = 0;
	public String markReadToken;
	public int reloadTry = 0;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	
    	URL = getResources().getText(R.string.BASE_URL) + URL;
    	URL_MARK_READ = getResources().getText(R.string.BASE_URL) + URL_MARK_READ;
    	
    	Bundle b = this.getIntent().getExtras();
		forumID = b.getInt("forum_id");
		page = 1;
		
		
		//setTheme( settingsAct.getTheme(this) );	
		
		perpage = settingsAct.getTopicPerPage(this);
		perpageTopics = settingsAct.getAnswersPerPage(this);
		openType = settingsAct.getTopicOpenType(this);
		
		lv = getListView();
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.view_forum_header, lv, false);
		lv.addHeaderView(header, null, false);
		
		
		ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.view_forum_footer, lv, false);
		lv.addFooterView(footer, null, false);
		
		Loading = new ProgressDialog(this);
		
		mSchedule = new ViewForumAdapter(this, mylist);
		setListAdapter(mSchedule);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				//Toast.makeText(getApplicationContext(), "postid:" + topicID , Toast.LENGTH_LONG ).show();
				if ( mylist.size() > position ) { 
					int topicID = Integer.parseInt( mylist.get( position -1 ).get("id") );
					
					mylist.get( position -1 ).put("new", "0");
					mSchedule.notifyDataSetChanged();
					
					if ( topicID > 0 ) {
						Intent i = new Intent(ViewForumActivity.this, ViewTopicActivity.class);
						i.putExtra("topic_id", topicID);
						
						switch( openType ) {
							case 1: 
								int numReplies  = Integer.parseInt( mylist.get( position -1 ).get("num_replies") );
								int pages =  (int) Math.ceil( (float)( numReplies + 1 ) / (float)perpageTopics );
								i.putExtra("page", pages);
							break;
							
							case 2: 
								i.putExtra("new", true); 
							break;
						}
						
						startActivity(i);
					}
				}
			}
		});
		
		//back button
		ImageButton backBtn = (ImageButton)findViewById(R.id.btn_back_to_index);
		backBtn.setOnClickListener( this );
		
		//load more topics
		
		Button btnPages = (Button) findViewById(R.id.btn_view_more_topic);
		btnPages.setOnClickListener(this);
		
		registerForContextMenu(lv);
		
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
			
			
			case R.id.btn_view_more_topic:
				page++;
				getData();
				mSchedule.notifyDataSetChanged();
			break;
		}
	}
	
	public void getData() {
		Loading.setTitle("Зареждане...");
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		
		if ( ! isFinishing() ) {
			Loading.show();
		}
		
    
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		
		URL += "&id=" + forumID;
		URL += "&page=" + page;
		URL += "&perpage=" + perpage;
		//URL += "&user=" + settings.getString("user_name", "");
		//URL += "&pass=" + settings.getString("user_pass", "");
		
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 1));
				} catch (Exception e) {
					new AlertDialog.Builder(ViewForumActivity.this)
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
    	
    	if ( jsonData.length() > 1 ) {
    	
	    	//System.out.println( jsonData );
	    	try {
	    		JSONObject data = new JSONObject(jsonData);
	    		
	    		reloadTry = 0;
	    		
	    		TextView forumHeader = (TextView)findViewById(R.id.v_forum_name);
	    		forumHeader.setText( data.getString("forum_name") );
	    		markReadToken = data.getString("markread");
	
	    		if ( data.getInt("error") == 0 ) {
	    		
					JSONArray jsonArray = data.getJSONArray("data");
		
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject p = jsonArray.getJSONObject(i);
		
						HashMap<String, String> map = new HashMap<String, String>();
		
						map.put("id", 			p.getString("id"));
						map.put("poster", 		p.getString("poster"));
						map.put("subject", 		p.getString("subject"));
						map.put("posted", 		p.getString("posted"));
						map.put("first_post_id",p.getString("first_post_id"));
						map.put("last_post", 	p.getString("last_post"));
						map.put("last_post_id",	p.getString("last_post_id"));
						map.put("last_poster",	p.getString("last_poster"));
						map.put("num_views",	p.getString("num_views"));
						map.put("num_replies",	p.getString("num_replies"));
						map.put("closed",		p.getString("closed"));
						map.put("sticky",		p.getString("sticky"));
						map.put("new",			p.getString("new"));
						
						mylist.add(map);
					}
				
	    		} else {
	    			Toast.makeText(getApplicationContext(), "Грешно! Презаредете!" , Toast.LENGTH_LONG ).show();
	    		}
				
				mSchedule.notifyDataSetChanged();
	
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(this, "Грешка 111-58-22!", Toast.LENGTH_LONG ).show();
			}
    	
    	} else {
    		if ( reloadTry < 3 ) {
    			reloadTry++;
    			getData();
    		} else {
    			Toast.makeText(this, "А дали имате интернет? Грешка 111-58-23!", Toast.LENGTH_LONG ).show();
    		}
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
				
				case 2:
					for( int i = 0; i < mylist.size(); i++ ) {
						mylist.get( i ).put("new", "0");
					}
					mSchedule.notifyDataSetChanged();
					Loading.dismiss();
				break;
			}
		}
	};

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 1, "Нова тема");
		menu.add(0, 1, 2, "Презареди");
		menu.add(0, 2, 3, "Отбележи форума като прочетен");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				Intent i = new Intent( this, NewtopicActivity.class);
				i.putExtra("forum_id", forumID);
				startActivityForResult(i, 1);
				return true;
	
			case 1:
				mylist.clear();
				getData();
				return true;
			case 2:
				markAsRead();
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
			//insert image
			case 1:
				mylist.clear();
				mSchedule.notifyDataSetChanged();
				getData();
			break;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {  
	    super.onCreateContextMenu(menu, v, menuInfo);  
	        //menu.setHeaderTitle("Context Menu");  
	        menu.add(0, 0, 1, "Нови мнения");  
	        menu.add(0, 1, 2, "Страници");
	        menu.add(0, 2, 3, "Отвори в браузър");
	        menu.add(0, 3, 4, "Сподели");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {  
		
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		
		if ( mylist.size() > menuInfo.position ) { 
			
			topicID = Integer.parseInt( mylist.get( menuInfo.position -1 ).get("id") );
			
			if ( item.getItemId() == 0 || item.getItemId() == 1 ) {
				mylist.get(  menuInfo.position -1 ).put("new", "0");
				mSchedule.notifyDataSetChanged();
			}
			
			switch( item.getItemId() ) {
			
				//new posts
				case 0:
					if ( topicID > 0 ) {
						Intent i = new Intent(ViewForumActivity.this, ViewTopicActivity.class);
						i.putExtra("topic_id", topicID);
						i.putExtra("new", true);
						startActivity(i);
					}
				break;
				
				//pagination
				case 1:
					
					int numReplies  = Integer.parseInt( mylist.get( menuInfo.position -1 ).get("num_replies") );
					
					int pages =  (int) Math.ceil( (float)( numReplies + 1 ) / (float)perpageTopics );
					
					//Toast.makeText(getApplicationContext(), "pages:" + pages , Toast.LENGTH_LONG ).show();
					String[] items = new String[pages];
					for( int i = 1; i <= pages; i++ ) {
						items[ i - 1 ] = "Страница " + i; 
					}
					
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    			builder.setTitle("Избери страница");
	    			builder.setItems(items, new DialogInterface.OnClickListener() {
	    			    public void onClick(DialogInterface dialog, int item) {
	    			        
	    			    	
	    					Intent i = new Intent(ViewForumActivity.this, ViewTopicActivity.class);
	    					i.putExtra("topic_id", topicID);
	    					i.putExtra("page", item + 1);
	    					startActivity(i);
	    					
	    			    }
	    			});
	    			
	    			builder.create();
	    			builder.show();
					
					
				break;	
				
				case 2:
					String browserURL = getResources().getText(R.string.BASE_URL) + "viewtopic.php?id=" + topicID;
					Intent browserIntent = new Intent(Intent.ACTION_VIEW);
					browserIntent.setData( Uri.parse( browserURL ) );
					startActivity( browserIntent );
					
				break;	
				
				case 3:
					String shareURL = getResources().getText(R.string.BASE_URL) + "viewtopic.php?id=" + topicID;
					Intent sharingIntent = new Intent(Intent.ACTION_SEND);
					sharingIntent.setType("text/plain");
					sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareURL);
					startActivity(Intent.createChooser(sharingIntent,"Сподели"));
				break;	
				
			}
			return true;  
		}
		return false;
    }
	
	public void markAsRead() {
    	
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		
		if ( ! isFinishing() ) {
			Loading.show();
		}
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		//misc.php?action=markforumread&fid=1&csrf_token=167041b59ea6819bdedbd4d5ca1ab72d3997f381
		URL_MARK_READ += "&action=markforumread";
		URL_MARK_READ += "&fid=" + forumID;
		URL_MARK_READ += "&csrf_token=" + markReadToken;
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL_MARK_READ + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 2));
				} catch (Exception e) {
					new AlertDialog.Builder(ViewForumActivity.this)
						.setTitle( "Грешка" )
						.setMessage("Не може да осъществи връзка със сървара")
						.show();
					
					e.printStackTrace();
				}
			}
		};

		t.start();
	}

	
}