package sd.androidbgapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ForumAdapter extends BaseAdapter {

	private Activity activity;
	//private String[] data;
	private ArrayList<HashMap<String, String>> mylist;
	private static LayoutInflater inflater = null;

	// public ImageLoader imageLoader;

	public ForumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		mylist = d;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// imageLoader=new ImageLoader(activity.getApplicationContext());
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
		public TextView forum_name;
		public TextView forum_descr;
		public ImageView image;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		ViewHolder separator;
		
		if ( position < this.getCount() ) {
			int forumID = Integer.parseInt( mylist.get(position).get("forum_id") );
			
			if ( forumID == 0 ){
				vi = inflater.inflate(R.layout.list_forum_separator, null);
				separator = new ViewHolder();
				separator.forum_name = (TextView) vi.findViewById(R.id.forum_name);
				separator.forum_name.setText( mylist.get(position).get("forum_name") );
				vi.setTag(separator);
			} else {
				
				vi = inflater.inflate(R.layout.list_forum_row, null);
				holder = new ViewHolder();
				holder.forum_name = (TextView) vi.findViewById(R.id.forum_name);
				holder.forum_descr = (TextView) vi.findViewById(R.id.forum_descr);
				holder.image = (ImageView) vi.findViewById(R.id.forum_icon);
				vi.setTag(holder);
				
				holder.forum_name.setText( mylist.get(position).get("forum_name") );
				holder.forum_descr.setText( mylist.get(position).get("forum_descr") );
				
				int newTopic = Integer.parseInt( mylist.get(position).get("new") );
				
				if ( newTopic == 1 ) {
					holder.image.setImageResource(R.drawable.icon_active);
				}
				
			}
		}

		return vi;
	}
}