package pitt.edu.AIEDxEDM2015;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import data.DBAdapter;
import data.Poster;
import java.util.Comparator;

public class Posters extends Activity {
	private ArrayList<Poster> pList;
	private DBAdapter db;
	private ListView lv;
	
	private final int MENU_HOME = Menu.FIRST;
	private final int MENU_TRACK = Menu.FIRST + 1;
	private final int MENU_SESSION = Menu.FIRST + 2;
	private final int MENU_STAR = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.track);
		
		ListViewAdapter adapter;
		db = new DBAdapter(this);
		db.open();

		pList = new ArrayList<Poster>();
		pList = db.getPoster();
		Collections.sort(pList, new dateCompare());
		db.close();
		
		adapter = new ListViewAdapter(pList);
		
		TextView tv = (TextView) findViewById(R.id.TextView01);
		tv.setText("Posters");
		
		lv = (ListView) findViewById(R.id.ListView01);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView av, View v, int pos, long arg) {
				
				Intent in = new Intent(Posters.this, PosterDetail.class);
				//in.putExtra("day_id", buttonNum);
				in.putExtra("id", pList.get(pos).ID);
				in.putExtra("title", pList.get(pos).name);
				in.putExtra("date", pList.get(pos).date);
				in.putExtra("bTime", pList.get(pos).beginTime);
				in.putExtra("eTime", pList.get(pos).endTime);
				in.putExtra("room", pList.get(pos).room);
				
				startActivity(in);
			}
		});
		
	}

	private class dateCompare implements Comparator<Poster>{


		@Override
		public int compare(Poster r1, Poster r2) {
			Hashtable<String, Integer> Dtrans = new Hashtable<String, Integer>();
			Dtrans.put("Monday, Jun.22", 1);
			Dtrans.put("Tuesday, Jun.23", 2);
			Dtrans.put("Wednesday, Jun.24", 3);
			Dtrans.put("Thursday, Jun.25", 4);
			Dtrans.put("Friday, Jun.26", 5);
			Dtrans.put("Saturday, Jun.27", 6);
			Dtrans.put("Sunday, Jun.28", 7);
			Dtrans.put("Monday, Jun.29", 8);
			if(Dtrans.get(r1.date)>Dtrans.get(r2.date)){
				return 1;

			}else if(Dtrans.get(r1.date)<Dtrans.get(r2.date)){
				return -1;
			}else{
				return 0;
			}


		}

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			this.finish();
			Intent in = new Intent(Posters.this, MainInterface.class);
			startActivity(in);
		}

		return super.onKeyDown(keyCode, event);
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
		menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
		menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
		menu.add(0, MENU_STAR, 0, "My Favourite").setIcon(R.drawable.star);
		menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent itemintent = new Intent();
		switch (item.getItemId()) {
		case MENU_HOME:
			this.finish();
			itemintent.setClass(Posters.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_SESSION:
			this.finish();
			itemintent.setClass(Posters.this, ProgramByDay.class);
			startActivity(itemintent);
			return true;
		case MENU_STAR:
			this.finish();
			itemintent.setClass(Posters.this, MyStaredPapers.class);
			startActivity(itemintent);
			return true;
		case MENU_TRACK:
			this.finish();
			itemintent.setClass(Posters.this, Proceedings.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(Posters.this, MyScheduledPapers.class);
			startActivity(itemintent);
			return true;
		}
		return false;
	}
	
	static class ViewHolder
	{
		TextView t1,t2,t3,firstCharHintTextView;
	}
	private class ListViewAdapter extends BaseAdapter {
		ArrayList<Poster> pList;
		public ListViewAdapter(ArrayList w) {
			this.pList = w;
		}

		public int getCount() {
			return pList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder v = null;
			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.sessionitem, null);
				v = new ViewHolder();
				v.t1= (TextView)convertView.findViewById(R.id.title);
				v.t3 = (TextView)convertView.findViewById(R.id.location);
				v.firstCharHintTextView =(TextView)convertView.findViewById(R.id.text_first_char_hint);
				convertView.setTag(v);
			}
			else {
				v = (ViewHolder) convertView.getTag();
			}
			v.t1.setText(pList.get(position).name);
			if(pList.get(position).room.compareToIgnoreCase("NULL")==0)
            	v.t3.setVisibility(View.GONE);
            else{
            	v.t3.setVisibility(View.VISIBLE);	
            	v.t3.setText("At "+pList.get(position).room);}
    			int idx = position - 1;   
   			 
                String preview = idx >= 0 ? pList.get(idx).date : "";
                String current = pList.get(position).date;
          
                if (current.compareTo(preview) == 0) {
                	v.firstCharHintTextView.setVisibility(View.GONE);   
                } else {   
                   
                	v.firstCharHintTextView.setVisibility(View.VISIBLE);
                	v.firstCharHintTextView.setText(pList.get(position).date);
                }
			return convertView;
		}
	}
}


