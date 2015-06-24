package pitt.edu.AIEDxEDM2015;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import data.DBAdapter;
import data.Keynote;
import data.Paper;


public class Tutorial extends Activity {



    private ListView lv1;
    private  TextView lv2;
    private DBAdapter db;
    private ArrayList<Paper> paList=new ArrayList<Paper>();
    private ArrayList<Paper> pList=new ArrayList<Paper>();

    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.track);

        db = new DBAdapter(this);
        db.open();
        paList = db.getPapersByPresentationType("Tutorial");
        HashSet<String> map=new HashSet<String>();
        //HashMap<String,String> map=new HashMap<String, String>();
        for(int i=0; i<paList.size();i++){
            if(!map.contains(paList.get(i).title)){
                map.add(paList.get(i).title);
                pList.add(paList.get(i));
            }
        }
        System.out.println("@@@@@@@@@@@@@tut"+pList.get(0).track);
        db.close();


        lv1 = (ListView) findViewById(R.id.ListView01);
        ListViewAdapter adapter = new ListViewAdapter(pList);
        lv1.setAdapter(adapter);
        lv1.setOnItemClickListener(adapter);

        lv2 = (TextView) findViewById(R.id.TextView01);
        lv2.setText("Tutorial");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.finish();
            Intent in = new Intent(Tutorial.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(Tutorial.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(Tutorial.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(Tutorial.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(Tutorial.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(Tutorial.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(Tutorial.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    public final class ViewHolder {
        public TextView firstCharHintTextView, title, location, time;
    }

    private class ListViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private ArrayList<Paper> p;

        public ListViewAdapter(ArrayList<Paper> pList) {
            this.p = pList;
        }

        public int getCount() {
            return p.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
            SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
            Date beginDate, endDate;
            String begTime, endTime;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.sessionitem, null);
                holder = new ViewHolder();
                holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.location = (TextView) convertView.findViewById(R.id.location);
               // holder.time = (TextView) convertView.findViewById(R.id.time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                beginDate = sdfSource.parse(p.get(position).exactbeginTime);
                endDate = sdfSource.parse(p.get(position).exactendTime);
                begTime = sdfDestination.format(beginDate);
                endTime = sdfDestination.format(endDate);
               // holder.time.setVisibility(View.VISIBLE);
                //holder.time.setText(begTime + " - " + endTime);
            } catch (Exception e) {
                System.out.println("Date Exception");
            }
            holder.title.setText(p.get(position).title);
            if (p.get(position).room.compareToIgnoreCase("NULL") == 0)
                holder.location.setVisibility(View.GONE);
            else {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText("At " + p.get(position).room);
            }
            int idx = position - 1;

            String previewb = idx >= 0 ? p.get(idx).date : "";
            String currentb = p.get(position).date;

            if (currentb.compareTo(previewb) == 0) {
                holder.firstCharHintTextView.setVisibility(View.GONE);
            } else {

                holder.firstCharHintTextView.setVisibility(View.VISIBLE);
                holder.firstCharHintTextView.setText(p.get(position).date);
            }
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            // TODO Auto-generated method stub
            Intent in = new Intent(Tutorial.this, TutorialDetail.class);
            in.putExtra("id", p.get(pos).id);
            in.putExtra("title", p.get(pos).title);
            in.putExtra("date", p.get(pos).date);
            in.putExtra("bTime", p.get(pos).exactbeginTime);
            in.putExtra("eTime", p.get(pos).exactendTime);
            in.putExtra("room", p.get(pos).room);
            in.putExtra("authors", p.get(pos).authors);
            in.putExtra("track", p.get(pos).track);
            in.putExtra("abstract", p.get(pos).paperAbstract);
            in.putExtra("presentationID", p.get(pos).presentationID);
            startActivity(in);
        }

    }
}
