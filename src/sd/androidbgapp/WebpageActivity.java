package sd.androidbgapp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class WebpageActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Uri data = getIntent().getData();
		List<String> params = data.getPathSegments();
		String uri = params.get(1);
		
		
		if ( preg_match("topic([0-9]+)", uri) ) {
			String topicID = preg_match_result("topic([0-9]+)", uri);
			System.out.println( topicID );
			Intent i = new Intent(WebpageActivity.this, ViewTopicActivity.class);
			i.putExtra("topic_id", Integer.parseInt( topicID ) );
			
			
			
			startActivity(i);
			
		}
		
		if ( preg_match("forum([0-9]+)", uri) ) {
			String forumID = preg_match_result("forum([0-9]+)", uri);
			System.out.println( forumID );
			Intent i = new Intent(WebpageActivity.this, ViewForumActivity.class);
			i.putExtra("forum_id", Integer.parseInt( forumID) );
			
			Toast.makeText(getApplicationContext(), forumID + "" , Toast.LENGTH_LONG ).show();
			startActivity(i);     
			
		}
		
		finish();
	}
	
	public static boolean preg_match(String pttr, String input) {
		Pattern p = Pattern.compile(pttr);
		Matcher m = p.matcher(input);
		return m.find();
	}
	
	public static String preg_match_result(String pttr, String input) {
		Matcher matcher = Pattern.compile(pttr).matcher(input);
		if ( matcher.find()){
		   return matcher.group(1);
		}
		return "";
	}
	

}