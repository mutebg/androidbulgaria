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

public class ViewForumAdapter extends BaseAdapter {

	private Activity activity;
	//private String[] data;
	private ArrayList<HashMap<String, String>> mylist;
	private static LayoutInflater inflater = null;

	// public ImageLoader imageLoader;

	public ViewForumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
		public TextView subject;
		public TextView poster;
		public TextView lastpost;
		public ImageView image;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		
		
		
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.view_forum_row, null);
			holder = new ViewHolder();
			holder.subject 	= (TextView) vi.findViewById(R.id.subject);
			holder.lastpost = (TextView) vi.findViewById(R.id.lastpost);
		
			holder.image = (ImageView) vi.findViewById(R.id.forum_icon);
			vi.setTag(holder);
			 
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		
		if ( position < this.getCount() ) {
			holder.subject.setText( mylist.get(position).get("subject") );
			holder.lastpost.setText( "последно от: " + mylist.get(position).get("last_poster") + " в " + mylist.get(position).get("last_post") );
			
			int newTopic 	= Integer.parseInt( mylist.get(position).get("new") );
			int closed		= Integer.parseInt( mylist.get(position).get("closed") );
			int sticky		=Integer.parseInt( mylist.get(position).get("sticky") );
			
			if ( newTopic == 1 ) {
				if ( closed == 1 && sticky == 1 ) {
					holder.image.setImageResource(R.drawable.icon_active_s_c);
				} else if ( sticky == 1 ) {
					holder.image.setImageResource(R.drawable.icon_active_sticky);
				} else if ( closed == 1 ) {
					holder.image.setImageResource(R.drawable.icon_active_close);
				} else {
					holder.image.setImageResource(R.drawable.icon_active);
				}
			} else {
				if ( closed == 1 && sticky == 1 ) {
					holder.image.setImageResource(R.drawable.icon_unactive_s_c);
				} else if ( sticky == 1 ) {
					holder.image.setImageResource(R.drawable.icon_unactive_sticky);
				} else if ( closed == 1 ) {
					holder.image.setImageResource(R.drawable.icon_unactive_close);
				} else {
					holder.image.setImageResource(R.drawable.icon_unactive);
				}
			}
		}
			
		return vi;
	}
}