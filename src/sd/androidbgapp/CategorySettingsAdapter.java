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

public class CategorySettingsAdapter extends BaseAdapter {

	private Activity activity;
	//private String[] data;
	private ArrayList<HashMap<String, String>> mylist;
	private static LayoutInflater inflater = null;
	

	// public ImageLoader imageLoader;

	public CategorySettingsAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
		activity = a;
		mylist = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		public ImageView image;
		public TextView forum_name;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.category_settings_row, null);
			holder = new ViewHolder();
			holder.image 		= (ImageView) vi.findViewById(R.id.forum_icon);
			holder.forum_name	= (TextView) vi.findViewById(R.id.forum_name);
	
			vi.setTag(holder);
			 
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		
		if ( position < this.getCount() ) {
			holder.forum_name.setText( mylist.get(position).get("forum_name") );
		
			if ( Integer.parseInt( mylist.get(position).get("checked") ) == 1 ) {
				holder.image.setImageResource(R.drawable.icon_active);
			}
		}
		
		return vi;
	}
	
	
}