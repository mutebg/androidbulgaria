package sd.androidbgapp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;

public class WebManager {

	/**
	 * Get data from web
	 * 
	 * @param url
	 */
	
	public String getDataFromWeb(String url) {

		//System.out.println( url );
		
		try {
			
			URL myURL = new URL(url);

			
			URLConnection ucon = myURL.openConnection();

			
			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);

		
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;

			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}

		
			return new String(baf.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}

		/*
		// For using Internet methods, AndroidManifest.xml must have the following permission:
		//<uses-permission android:name="android.permission.INTERNET"/>
		// This assumes that you have a URL from which to get the answer
		URI myURL = new URI("www.website.org");
		
		// The HTTP objects
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet getMethod = new HttpGet(myURL);
		HttpResponse httpResponse;
		
		// The query result
		String result = null;
		
		try {
			httpResponse = httpClient.execute(getMethod);
			// You might want to check response.getStatusLine().toString()
		
			HttpEntity entity = httpResponse.getEntity();
		
			if (entity != null) {
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader( new InputStreamReader(instream));
				StringBuilder sb = new StringBuilder();
				String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
			// Deal with it
			} finally {
				try {
					instream.close();
				} catch (IOException e) {
				// Deal with it
				}
			}
			
			// Handle the result (for instance, get a JSON object
			// using the "Retrieve JSON from a server response" snippet)
			handleResult(result);
			}
		} catch (ClientProtocolException e) {
			// Deal with it
		} catch (IOException e) {
			// Deal with it
		}
		*/
	}
	
	/*
	public String getDataFromWeb(String openurl) {
		// For using Internet methods, AndroidManifest.xml must have the following permission:
		//<uses-permission android:name="android.permission.INTERNET"/>
		// The data that is retrieved 
		String result = null;
		
		try {
		     // This assumes that you have a URL from which the response will come
		     URL url = new URL(openurl);
		     
		     // Open a connection to the URL and obtain a buffered input stream
		     URLConnection connection = url.openConnection();
		     InputStream inputStream = connection.getInputStream();
		     BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
		     
		     // Read the response into a byte array
		     ByteArrayBuffer byteArray = new ByteArrayBuffer(50);
		     int current = 0;
		     while((current = bufferedInput.read()) != -1){
		          byteArray.append((byte)current);
		     }
		
		     // Construct a String object from the byte array containing the response
		     result = new String(byteArray.toByteArray());
		     return result;
		} catch (Exception e) {
			
		}
		
		// Handle the result
		return result;
	}
	*/
	
	
	public String postData(String url, HashMap<String,String> map) {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost( url );
	    
	    BufferedReader in = null;
	    
	    
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        
	        for (String key : map.keySet()) {
	            //System.out.println("key/value: " + key + "/"+map.get(key));
	            nameValuePairs.add(new BasicNameValuePair(key, map.get(key) ) );
	        }
	        //nameValuePairs.add(new BasicNameValuePair("id", "12345"));
	        
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        
	        in = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			String result = sb.toString();
			return result;
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	    return "";
	} 
	// see http://androidsnippets.com/executing-a-http-post-request-with-httpclient
	
	
	public static String bbcode(String text) {
        String html = text;

        Map<String,String> bbMap = new HashMap<String , String>();
        
        //html = nl2br( html );
        
        bbMap.put("(\r\n|\r|\n|\n\r)", "<br />");
        bbMap.put("\\[b\\](.+?)\\[/b\\]", "<b>$1</b>");
        bbMap.put("\\[i\\](.+?)\\[/i\\]", "<i>$1</i>");
        bbMap.put("\\[u\\](.+?)\\[/u\\]", "<u>$1</u>");
        bbMap.put("\\[h\\](.+?)\\[/h\\]", "<h1>$1</h1>");
        bbMap.put("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>");
        bbMap.put("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>");
        bbMap.put("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>");
        bbMap.put("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>");
        bbMap.put("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>");
        bbMap.put("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>");
        //bbMap.put("\\[quote=(.+?)\\](.+?)\\[/quote\\]", "<blockquote>$1 написа:<br />$2</blockquote>");
        //bbMap.put("\\[quote\\](.+?)\\[/quote\\]/Us", "<blockquote>$1</blockquote>");
        bbMap.put("\\[quote=(.+?)\\]", "<blockquote style='border:1px solid #cccccc'><b>$1 написа:</b><br />");
        bbMap.put("\\[quote\\]", "<blockquote>");
        bbMap.put("\\[/quote\\]", "</blockquote>");
        bbMap.put("\\[p\\](.+?)\\[/p\\]", "<p>$1</p>");
        bbMap.put("\\[p=(.+?),(.+?)\\](.+?)\\[/p\\]", "<p style='text-indent:$1px;line-height:$2%;'>$3</p>");
        bbMap.put("\\[center\\](.+?)\\[/center\\]", "<div align='center'>$1");
        bbMap.put("\\[align=(.+?)\\](.+?)\\[/align\\]", "<div align='$1'>$2");
        //bbMap.put("\\[color=(.+?)\\](.+?)\\[/color\\]", "<font color='$1'>$2</font>");
        bbMap.put("\\[color=(.+?)\\]", "<font color='$1'>");
        bbMap.put("\\[/color\\]", "</font>");
        bbMap.put("\\[size=(.+?)\\](.+?)\\[/size\\]", "<font size='$1'>$2</span>");
        bbMap.put("\\[img\\](.+?)\\[/img\\]", "<a href='$1' target='_blank'>[снимка - отвори в браузъра]</a>");
        bbMap.put("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<a href='$1' target='_blank'>[снимка - отвори в браузъра]</a>");
        //bbMap.put("\\[img\\](.+?)\\[/img\\]", "<img src='http://androidbg.com/forum/andapp/imageresize.php?path=$1' />");
        bbMap.put("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>");
        bbMap.put("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>");
        bbMap.put("\\[url\\](.+?)\\[/url\\]", "<a href='$1' target='_blank'>$1</a>");
        bbMap.put("\\[url=(.+?)\\](.+?)\\[/url\\]", "<a href='$1' target='_blank'>$2</a>");
        bbMap.put("\\[youtube\\](.+?)\\[/youtube\\]", "<a href='$1' target='_blank'>[видео - отвори в браузъра]</a>");
        //bbMap.put("\\[video\\](.+?)\\[/video\\]", "<a href='$1'>[видео - отвори в браузъра]</a>");
        bbMap.put("\\[video\\]", "");
        bbMap.put("\\[/video\\]", "");
        bbMap.put("\\[box\\]", "");
        bbMap.put("\\[/box\\]", "");
        bbMap.put("\\[spoiler\\]", "");
        bbMap.put("\\[/spoiler\\]", "");
        //other
        //bbMap.put("\\[quote\\]", "<blockquote>");
        //bbMap.put("\\[/quote\\]", "</blockquote>");

        for (Map.Entry entry: bbMap.entrySet()) {
            html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
        }

        return html;
    }
	
	public static String emotions( String text ) {
		
		Map<String,Integer> emo = new HashMap<String, Integer>();
		
		emo.put(":angel:", 	R.drawable.emo_im_angel);
		emo.put(":cool:",	R.drawable.emo_im_cool);
		emo.put(":cry:", R.drawable.emo_im_crying);
		emo.put(":angry:", R.drawable.emo_im_embarrassed);
		emo.put("=(", R.drawable.emo_im_foot_in_mouth);
		emo.put(":D", R.drawable.emo_im_happy);
		emo.put(":kiss:", R.drawable.emo_im_kissing);
		emo.put(":rofl:", R.drawable.emo_im_laughing);
		emo.put(":|", R.drawable.emo_im_lips_are_sealed);
		emo.put(":(", R.drawable.emo_im_sad);
		emo.put(":o", R.drawable.emo_im_surprised);
		emo.put(":p", R.drawable.emo_im_tongue_sticking_out);
		emo.put(":/", R.drawable.emo_im_undecided);
		emo.put(";)", R.drawable.emo_im_winking);
		emo.put(":rolleyes", R.drawable.emo_im_wtf);
		emo.put(":mad:", R.drawable.emo_im_yelling);
		
		
		return text;
	}
	
	
	public String encode( Map<String,String> get) {
		
		String data = "";
		
		data += String.format("%02d", get.size() );
		
		
		
		
		 for (Map.Entry entry: get.entrySet()) {
			 //html = html.replaceAll(entry.getKey().toString(), entry.getValue().toString());
			 data += String.format("%03d", entry.getKey().toString().length() );
			 data += String.format("%03d", entry.getValue().toString().length() );
		 }
		 
		 for (Map.Entry entry: get.entrySet()) {
			 for( int i = 0; i < entry.getKey().toString().length(); i++ ) {
				 char s = entry.getKey().toString().charAt(i);
				 data += String.format("%03d", (int)s );
			 }
			 
			 for( int i = 0; i < entry.getValue().toString().length(); i++ ) {
				 char s = entry.getValue().toString().charAt(i);
				 data += String.format("%03d", (int)s );
			 }
		 }
		
		 
		 int i, len = data.length();
		 StringBuffer dest = new StringBuffer(len);

		 for (i = (len - 1); i >= 0; i--) {
			 dest.append(data.charAt(i));
		 }
		 
		 data = dest.toString();
		  
		 
		 //Log.v("data", data);
		 return data;
	}
}
