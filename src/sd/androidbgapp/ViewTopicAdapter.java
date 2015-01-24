package sd.androidbgapp;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ViewTopicAdapter extends BaseAdapter {

	private Activity activity;
	//private String[] data;
	private ArrayList<HashMap<String, String>> mylist;
	private static LayoutInflater inflater = null;
	private WebManager bbcode;
	private MovementMethod linkText;

	// public ImageLoader imageLoader;

	public ViewTopicAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a; 
		mylist = d;
		inflater = (LayoutInflater) activity .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader=new ImageLoader(activity.getApplicationContext());
		bbcode = new WebManager();
		linkText = LinkMovementMethod.getInstance();
	}

	public int getCount() {
		return mylist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		public TextView poster_name;
		public TextView post_date;
		public TextView post_message;
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.view_topic_row, null);
			holder = new ViewHolder();
			holder.poster_name 	= (TextView) vi.findViewById(R.id.poster_name);
			holder.post_date 	= (TextView) vi.findViewById(R.id.post_date);
			holder.post_message = (TextView) vi.findViewById(R.id.post_messsage);
			holder.post_message.setMovementMethod( linkText );
			vi.setTag(holder);
			 
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		
		if ( position < this.getCount() ) {
			holder.poster_name.setText( 	mylist.get(position).get("username") );
			holder.post_date.setText( 		mylist.get(position).get("posted") );
			//holder.post_message.setText(  Html.fromHtml( bbcode.bbcode( mylist.get(position).get("message") ) ) );
			holder.post_message.setText( Html.fromHtml(WebManager.bbcode( mylist.get(position).get("message") ), imgGetter, null) );
		}
		return vi;
	}
	
	private ImageGetter imgGetter = new ImageGetter() {

        public Drawable getDrawable(String source) {
        		
        		//System.out.println( source );
        	
                HttpGet get = new HttpGet(source);
                DefaultHttpClient client = new DefaultHttpClient();
                Drawable drawable = null;
                
                drawable = activity.getResources().getDrawable(R.drawable.btn_back);
                /*
                try {
                        HttpResponse response = client.execute(get);
                        InputStream stream = response.getEntity().getContent();
                        FileOutputStream fileout = new FileOutputStream(new File(
                                        Environment.getExternalStorageDirectory()
                                                        .getAbsolutePath()
                                                        + "/test.jpg"));
                        int read = stream.read();
                        while (read != -1) {
                                fileout.write(read);
                                read = stream.read();
                        }
                        fileout.flush();
                        fileout.close();
                        drawable = Drawable.createFromPath(Environment
                                        .getExternalStorageDirectory().getAbsolutePath()
                                        + "/test.jpg");
                        
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

                } catch (ClientProtocolException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                */
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
        }
	};
}