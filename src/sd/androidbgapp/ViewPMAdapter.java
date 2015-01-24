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

public class ViewPMAdapter extends BaseAdapter {

	private Activity activity;
	//private String[] data;
	private ArrayList<HashMap<String, String>> mylist;
	private static LayoutInflater inflater = null;

	// public ImageLoader imageLoader;

	public ViewPMAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
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
		public TextView info;
		public ImageView status;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		ViewHolder holder;
		
		
		
		
		if (convertView == null) {
			vi = inflater.inflate(R.layout.view_pm_row, null);
			holder = new ViewHolder();
			holder.subject 	= (TextView) vi.findViewById(R.id.subject);
			holder.info 	= (TextView) vi.findViewById(R.id.sendfrom);
			holder.status = (ImageView) vi.findViewById(R.id.forum_icon);
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}
		
		if ( position < this.getCount() ) {
			holder.subject.setText( mylist.get(position).get("subject") );
			holder.info.setText( "от " + mylist.get(position).get("username") + " в " + mylist.get(position).get("sent_at") );
			
			if ( mylist.get(position).get("status").equals("read") ) {
				holder.status.setImageResource(R.drawable.icon_unactive);
			} else {
				holder.status.setImageResource(R.drawable.icon_active);
			}
		}	

		return vi;
	}
}