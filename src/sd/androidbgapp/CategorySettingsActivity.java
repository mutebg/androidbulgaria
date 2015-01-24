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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CategorySettingsActivity extends ListActivity implements OnClickListener {
	
	public String jsonData;
	public String URL = "andapp/index.php?";
	public Map<String, String> URL_PARAMS; 
	public ProgressDialog Loading = null;
	public ListView lv = null;
	public CategorySettingsAdapter mSchedule = null;
	public ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	public static final String PREFS_NAME = "myprefs";
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	
    	URL = getResources().getText(R.string.BASE_URL) + URL;
    	

	    lv = getListView();
	    
	    LayoutInflater inflater = getLayoutInflater();
		ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.category_settings_footer, lv, false);
		lv.addFooterView(footer, null, true);
	    
	   	mSchedule = new CategorySettingsAdapter(this, mylist);
		setListAdapter(mSchedule);
	
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if ( mylist.size() > position ) {
				
					HashMap<String, String> map = mylist.get(position);
					
					ImageView img = (ImageView)view.findViewById(R.id.forum_icon);
					
					if ( Integer.parseInt( map.get("checked") ) == 1 ) {
						img.setImageResource(R.drawable.icon_unactive);
						map.remove("checked");
						map.put("checked", "0");
					} else {
						img.setImageResource(R.drawable.icon_active);
						map.remove("checked");
						map.put("checked", "1");
					}
					
					mylist.set(position, map);
				}
	      	}
		});
			
		getData();
		
		Button btnSaveCategory = (Button) findViewById(R.id.btn_save_category);
		Button btnCancelCategory = (Button) findViewById(R.id.btn_cancel_category);
		
		btnSaveCategory.setOnClickListener(this);
		btnCancelCategory.setOnClickListener(this);
		
	}
    
    
    
    public void getData() {
    	Loading = new ProgressDialog(this);
		Loading.setTitle("Зареждане...");
		Loading.setMessage("Моля изчакайте");
		Loading.setIndeterminate(true);
		Loading.setCancelable(true);
		Loading.show();
    
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		//URL += "&user=" + settings.getString("user_name", "");
		//URL += "&pass=" + settings.getString("user_pass", "");
		
		//Toast.makeText(getApplicationContext(), URL , Toast.LENGTH_LONG ).show();
		URL_PARAMS = new HashMap<String, String>();
		URL_PARAMS.put("user", settings.getString("user_name", "") );
		URL_PARAMS.put("pass", settings.getString("user_pass", "") );
		
		
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					WebManager WM = new WebManager();
					jsonData = WM.getDataFromWeb(URL + "&code=" + WM.encode( URL_PARAMS ) );
					handler.sendMessage(Message.obtain(handler, 1));
				} catch (Exception e) {
					new AlertDialog.Builder(CategorySettingsActivity.this)
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
    	
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String hiddenCats = settings.getString("hidden_cats", "");
    	
		String[] hiddenCatsArray = null;
		
		if ( ! hiddenCats.equals("") ) {
			hiddenCatsArray = hiddenCats.split(",");
			//Toast.makeText(getApplicationContext(), "vliza", Toast.LENGTH_LONG ).show();
		} else {
			//Toast.makeText(getApplicationContext(), "ne vliza", Toast.LENGTH_LONG ).show();
		}
		
		//Toast.makeText(getApplicationContext(), "" + hiddenCatsArray.length, Toast.LENGTH_LONG ).show();
    	try {

    		JSONObject data = new JSONObject(jsonData);
    		JSONArray jsonArray = data.getJSONArray("list");
    		
    		if ( data.getInt("error") == 0 ) {
			
    			for (int i = 0; i < jsonArray.length(); i++) {
					
					JSONObject p = jsonArray.getJSONObject(i);
					
					if ( p.getInt("forum_id") == 0 ) {
						HashMap<String, String> map = new HashMap<String, String>();
		
						map.put("cat_id", 		p.getString("cat_id"));
						map.put("forum_name", 	p.getString("forum_name"));
						map.put("forum_descr", 	p.getString("forum_descr"));
						
						//Toast.makeText(getApplicationContext(), p.getString("cat_id") + " | " + Arrays.binarySearch(hiddenCatsArray, p.getString("cat_id")  ) , Toast.LENGTH_LONG ).show();
						
						String checked = "1";
						if ( hiddenCatsArray != null ) {
							for( int v = 0 ; v < hiddenCatsArray.length; v++ ) {
								if ( Integer.parseInt( hiddenCatsArray[v] ) == Integer.parseInt( p.getString("cat_id") ) ) {
									checked = "0";
								}
							}
						}
						
						map.put("checked",	checked);
						
						mylist.add(map);
					}
				}
				
				mSchedule.notifyDataSetChanged();
			
    		} else { 
				Toast.makeText(getApplicationContext(), data.getString("error_message") , Toast.LENGTH_LONG ).show();
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


	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch( v.getId() ) {
			case R.id.btn_cancel_category:
				setResult(RESULT_CANCELED);
				finish();
			break;
			
			case R.id.btn_save_category:
				
				String hiddenCats = "";
				boolean onlyOneCats = false;
				
				for( int i = 0 ; i < mylist.size(); i++ ) {
					
					if ( Integer.parseInt( mylist.get(i).get("checked") ) == 0 ) {
						String catID = mylist.get( i ).get("cat_id");
						hiddenCats += catID + ",";
					} else {
						onlyOneCats = true;
					} 
				
				}	
				
				
				if ( hiddenCats.length() > 0 ) {
					hiddenCats = hiddenCats.substring( 0, hiddenCats.length() - 1 );
				}
				
				if ( !onlyOneCats ) {
					Toast.makeText(getApplicationContext(), "Трябва да имате поне една селектирана категория", Toast.LENGTH_LONG ).show();
				} else {
					
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("hidden_cats", hiddenCats );
					editor.commit();
					
					//Toast.makeText(getApplicationContext(), hiddenCats , Toast.LENGTH_LONG ).show();
					setResult(RESULT_OK);
					finish();
				
				}
			break;	
		}
	}
}