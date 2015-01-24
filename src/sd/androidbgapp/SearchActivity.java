package sd.androidbgapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class SearchActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	SettingsActivity settingsAct = new SettingsActivity();
    	setTheme( settingsAct.getTheme(this) );
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.search);
    	
    	String html = "	<div id=\"cse\" style=\"width: 100%;\">Loading</div>" +
						"<script src=\"http://www.google.com/jsapi\" type=\"text/javascript\"></script>" +
						"<script type=\"text/javascript\">" + 
						"  google.load('search', '1', {language : 'bg', style : google.loader.themes.MINIMALIST});" +
						"  google.setOnLoadCallback(function() {" +
						"    var customSearchOptions = {}; " +
						"    var customSearchControl = new google.search.CustomSearchControl(" +
						"      '002207469081243015654:tcnv3izrp7k', customSearchOptions);" +
						"    customSearchControl.setResultSetSize(google.search.Search.FILTERED_CSE_RESULTSET);"+
						"    customSearchControl.draw('cse');" +
						"  }, true);" +
						"</script>";	
    	
    	String mime = "text/html";
    	String encoding = "utf-8";

    	
    	WebView myWebView = (WebView) findViewById(R.id.search_webview);
    	myWebView.getSettings().setJavaScriptEnabled(true);
    	myWebView.loadDataWithBaseURL(null, html, mime, encoding, null);
    	
    	
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
    
	
}