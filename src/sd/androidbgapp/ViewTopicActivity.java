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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewTopicActivity extends ListActivity implements OnClickListener {

	public String jsonData;
	public String URL = "andapp/viewtopic.php?";
	public String URL_GET = "";
	public String URL_DELETE = "andapp/delete.php?";
	public String URL_GET_DELETE = "";
	public Map<String, String> URL_PARAMS; 
	public ProgressDialog Loading = null;
	public ListView lv = null;
	public ViewTopicAdapter mSchedule = null;
	public ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	public static final String PREFS_NAME = "myprefs";
	public int topicID;
	public int page = 1;
	public int perpage = 10;
	public int totalPages = 1;
	public int isAdMod = 0;
	public boolean newTopics = true;
	public ImageButton btnNextPage;
	public ImageButton btnPrevPage;
	public Button btnReply;
	public Button btnPages;
	public ImageButton btnTopPages;
	public int deleteID;
	public int reloadTry = 0;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	
    	URL = getResources().getText(R.string.BASE_URL) + URL;
    	URL_DELETE = getResources().getText(R.string.BASE_URL) + URL_DELETE;
    	
    	Bundle b = this.getIntent().getExtras();
		topicID = b.getInt("topic_id");
		
		//Toast.makeText(getApplicationContext(), topicID + "" , Toast.LENGTH_LONG ).show();
		
		newTopics = b.getBoolean("new", false);
		page	= b.getInt("page", 1);
		
		Loading = new ProgressDialog(this);
		
		
		perpage = settingsAct.getAnswersPerPage(this);
		
		lv = getListView();
		
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup header = (ViewGroup)inflater.inflate(R.layout.view_topic_header, lv, false);
		lv.addHeaderView(header, null, false);
		
		ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.view_topic_footer, lv, false);
		lv.addFooterView(footer, null, false);
		
		mSchedule = new ViewTopicAdapter(this, mylist);
		setListAdapter(mSchedule);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
		});
		
		//back button
		ImageButton backBtn = (ImageButton)findViewById(R.id.btn_back_to_index);
		backBtn.setOnClickListener( this );
		
		//prev next reply btns
		btnNextPage = (ImageButton) findViewById(R.id.btn_next_page);
		btnNextPage.setOnClickListener(this);
		
		btnPrevPage = (ImageButton) findViewById(R.id.btn_prev_page);
		btnPrevPage.setOnClickListener(this);
		
		btnReply = (Button) findViewById(R.id.btn_reply);
		btnReply.setOnClickListener(this);
		
		btnPages = (Button) findViewById(R.id.btn_select_page);
		btnPages.setOnClickListener(this);
		
		btnTopPages = (ImageButton) findViewById(R.id.btn_top_pages);
		btnTopPages.setOnClickListener(this);
		
		registerForContextMenu(lv);
		
		
		
		getData();
	}
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch ( v.getId() ) {
			case R.id.btn_back_to_index:
				finish();
			break;
			
			case R.id.btn_next_page:
				page = page+1;
				newTopics = false;
				mylist.clear();
				mSchedule.notifyDataSetChanged();
				getData();
			break;	
			
			case R.id.btn_prev_page:
				page = page - 1;
				newTopics = false;
				mylist.clear();
				mSchedule.notifyDataSetChanged();
				getData();
			break;	
			
			case R.id.btn_reply:
				Intent i = new Intent( this, ReplyActivity.class);
				i.putExtra("topic_id", topicID);
				startActivityForResult(i, 1);
			break;	
			
			case R.id.btn_select_page: case R.id.btn_top_pages :
				
				//Toast.makeText(getApplicationContext(), "pages:" + pages , Toast.LENGTH_LONG ).show();
				String[] items = new String[totalPages];
				for( int z = 1; z <= totalPages; z++ ) {
					items[ z - 1 ] = "Страница " + z; 
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setTitle("Избери страница");
    			builder.setItems(items, new DialogInterface.OnClickListener() {
    			    public void onClick(DialogInterface dialog, int item) {
    			    	page = item + 1;
    					newTopics = false;
    					mylist.clear();
    					mSchedule.notifyDataSetChanged();
    					getData();
    			    }
    			});
    			
    			builder.create();
    			builder.show();
				
			break;	
			
		}
		
		
	}
	
	public void getData() {
		
		lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_DISABLED);
		//lv.setStackFromBottom(false);
		
    	
		Loading.setTitle("Зареждане...");
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		
		if ( ! isFinishing() ) {
			Loading.show();
		}
    
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		URL_GET = URL;
		URL_GET += "id=" + topicID;
		URL_GET += "&page=" + page;
		URL_GET += "&perpage=" + perpage;
		//URL += "&user=" + settings.getString("user_name", "");
		//URL += "&pass=" + settings.getString("user_pass", "");
		if ( newTopics ) {
			URL_GET += "&action=new";
		}
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		//URL_PARAMS.put("id", topicID + "" );
		//URL_PARAMS.put("page", page + "" );
		
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL_GET + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 1));
				} catch (Exception e) {
					new AlertDialog.Builder(ViewTopicActivity.this)
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
    	//Toast.makeText(this, jsonData.length() + "", Toast.LENGTH_LONG ).show();
    	if ( jsonData.length() > 1 ) {
    		try {
    			
    			reloadTry = 0;
    			
	    		JSONObject data = new JSONObject(jsonData);
	    		
	    		totalPages = data.getInt("pages");
	    		page = data.getInt("page");
	    		isAdMod = data.getInt("is_mod");
	    		int firstNewPost = data.getInt("first_new");
	    		int moveTo = 0;
	    		//Toast.makeText(getApplicationContext(), "" + page , Toast.LENGTH_LONG ).show();
	    		
	    		TextView forumHeader = (TextView)findViewById(R.id.v_forum_name);
	    		forumHeader.setText( data.getString("topic_name") );
	    		
	    		TextView forumPages = (TextView)findViewById(R.id.v_forum_pages);
	    		forumPages.setText( "страница " + page + " от " + totalPages );
	    		
	
	    		if ( data.getInt("error") == 0 ) {
	    			
	    			JSONArray jsonArray = data.getJSONArray("posts");
		
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject p = jsonArray.getJSONObject(i);
		
						HashMap<String, String> map = new HashMap<String, String>();
						
						String cID = p.getString("id");
		
						map.put("id", 			cID);
						map.put("posted",		p.getString("posted"));
						map.put("username",		p.getString("username"));
						map.put("message",		p.getString("message"));
						map.put("poster_id",	p.getString("poster_id"));
						
						if ( Integer.parseInt(cID) == firstNewPost ) {
							moveTo = i + 1;
						}
						
						mylist.add(map);
					}
				}
				
				mSchedule.notifyDataSetChanged();
				
				//show / hide next page btn
				if ( totalPages > 1 && page < totalPages ) {
					btnNextPage.setVisibility(0);
				} else {
					btnNextPage.setVisibility(4);
				}
				
				//show / hide prev page btn
				if ( totalPages > 1 && page > 1 ) {
					btnPrevPage.setVisibility(0);
				} else {
					btnPrevPage.setVisibility(4);
				}
				
				//show / reply btn
				if ( data.getInt("topic_closed") == 0 ) {
					btnReply.setVisibility(0);
				}
				
				if ( totalPages > 1 ) {
					btnPages.setVisibility(0);
					btnTopPages.setVisibility(0);
				} else {
					btnPages.setVisibility(8);
					btnTopPages.setVisibility(8);
				}
				
				if ( firstNewPost > 0 ) {
					lv.setSelectionFromTop(moveTo, 0);
				}
				
				
				
	    	} catch (JSONException e) {
	    		Toast.makeText(this, "Грешка 111-48-22!", Toast.LENGTH_LONG ).show();
			}
    	
    	} else {
    		if ( reloadTry < 3 ) {
    			reloadTry++;
    			Loading.dismiss();
    			getData();
    			
    		} else {
    			Toast.makeText(this, "А дали имате интернет? Грешка 111-48-23!", Toast.LENGTH_LONG ).show();
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
					Loading.dismiss();
					deleteMessageResult();
				break;	
			}
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
			//add new message;
			case 1:
				mylist.clear();
				mSchedule.notifyDataSetChanged();
				getData();
				lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
			break;
			
			case 2:
				mylist.clear();
				mSchedule.notifyDataSetChanged();
				getData();
			break;
			
			case 3:
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
	    	menu.add(0, 0, 1, "Цитирай");
	    	menu.add(0, 1, 2, "Редактирай");
	    	menu.add(0, 2, 3, "Изтрий");
	         
	         
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {  
        
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		int posterID 	= Integer.parseInt( mylist.get( menuInfo.position -1 ).get("poster_id") );
		int userID		= settings.getInt("user_id", 0);
		
		switch( item.getItemId() ) {
			
			case 0:
				
				//int topicID = Integer.parseInt( mylist.get( menuInfo.position -1 ).get("id") );
				String topicAuthor = mylist.get( menuInfo.position -1 ).get("username");
				String topicMessage = mylist.get( menuInfo.position -1 ).get("message");
				
				
				Intent i = new Intent( this, ReplyActivity.class);
				i.putExtra("topic_id", topicID);
				i.putExtra("quote", "[quote=" + topicAuthor + "]" + topicMessage + "[/quote]");
				startActivityForResult(i, 1);
				
			break;
			
			default:
			
			case 1:	
				if ( ( isAdMod > 0 ) || ( posterID == userID ) ) {
					
					String editMessage = mylist.get( menuInfo.position -1 ).get("message");
					int editID = Integer.parseInt( mylist.get( menuInfo.position -1 ).get("id") );
					Intent e = new Intent( this, EditActivity.class);
					e.putExtra("message", editMessage);
					e.putExtra("id", editID);
					startActivityForResult(e, 2);
					
				} else {
					Toast.makeText(this, "Нямате право да редактирате това мнение", Toast.LENGTH_LONG ).show();
				}
				
			break;
			
			
			case 2:
				
				if ( ( isAdMod > 0 ) || ( posterID == userID ) ) {
					
					deleteID = Integer.parseInt( mylist.get( menuInfo.position -1 ).get("id") );
					
					new AlertDialog.Builder(this).setTitle("Изтриване")
    				.setMessage( "Сигурни ли сте, че искате да изтриете това мнение?" )
    				.setPositiveButton("Да", new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int whichButton) {
	                    	deleteMessage( deleteID );
	                    }
	                })
    				.setNegativeButton("Не", new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
	                    }
	                })
	                .show();
					
				} else {
					Toast.makeText(this, "Нямате право да изтривате това мнение", Toast.LENGTH_LONG ).show();
				}
			break;	
		}
		
		return true;  
    }
	
	void deleteMessage (int messageID ) {

		Loading.setTitle("Зареждане...");
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		
		if ( ! isFinishing() ) {
			Loading.show();
		}
		
		
    
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		URL_GET_DELETE = URL_DELETE;
		URL_GET_DELETE += "&id=" + messageID;
		URL_GET_DELETE += "&delete=1=";
		
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL_GET_DELETE + "&code=" + WM.encode( URL_PARAMS) );
					handler.sendMessage(Message.obtain(handler, 2));
				} catch (Exception e) {
					new AlertDialog.Builder(ViewTopicActivity.this)
						.setTitle( "Грешка" )
						.setMessage("Не може да осъществи връзка със сървара")
						.show();
					
					e.printStackTrace();
				}
			}
		};

		t.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		//menu.add(0, 0, 1, "Отговори");
		menu.add(0, 1, 2, "Отвори в браузър");
		menu.add(0, 2, 3, "Сподели");
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
			case 0:
				
				Intent i = new Intent( this, ReplyActivity.class);
				i.putExtra("topic_id", topicID);
				startActivityForResult(i, 1);
				
				return true;
		
			case 1:
				String browserURL = getResources().getText(R.string.BASE_URL) + "viewtopic.php?id=" + topicID;
				Intent browserIntent = new Intent(Intent.ACTION_VIEW);
				browserIntent.setData( Uri.parse( browserURL ) );
				startActivity( browserIntent );
				
				return true;	
			
			case 2:
				String shareURL = getResources().getText(R.string.BASE_URL) + "viewtopic.php?id=" + topicID;
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareURL);
				startActivity(Intent.createChooser(sharingIntent,"Сподели"));
				return true;	
	
			
		}
		
		return false;
	}
	
	void deleteMessageResult() {
		try {
    		JSONObject data = new JSONObject(jsonData);
    		
    		if ( data.getInt("error") == 0 ) {
    			
    			if ( data.getInt("delete_topic") == 0 ) {
    				mylist.clear();
    				mSchedule.notifyDataSetChanged();
    				getData();
    			} else {
    				finish();
    			}
    			
    			
    		} else {
    			new AlertDialog.Builder(ViewTopicActivity.this)
				.setTitle( "Грешка" )
				.setMessage( "Опитайте отново" )
				.show();
    		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}