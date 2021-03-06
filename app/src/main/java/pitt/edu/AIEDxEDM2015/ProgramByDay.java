package pitt.edu.AIEDxEDM2015;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import data.DBAdapter;
import data.Paper;
import data.Session;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class ProgramByDay extends Activity {

    private int date;
    private DBAdapter db;
    private ArrayList<Session> sessions1, sessions2, sessions3, sessions4, sessions5, sessions6, sessions7, sessions8;
    private ListView day1lv, day2lv, day3lv, day4lv, day5lv, day6lv, day7lv, day8lv;
    private String sessionDate;
    private HorizontalScrollView scroll;
    private TabHost host;
    private int screenWidth,screenHeight;


    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_STAR = Menu.FIRST + 2;
    private final int MENU_SCHEDULE = Menu.FIRST + 3;
    private final int MENU_RECOMMEND = Menu.FIRST + 4;

    private float mPosX,mPosY,mCurPosX,mCurPosY;
    private void setGestureListener(View view){
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                        final int screenHeight = displayMetrics.heightPixels;
                        final int screenWidth = displayMetrics.widthPixels;
                        if(Math.abs(mCurPosY-mPosY)>(screenHeight/8)){
                            //System.out.println("!!!!!!!!"+(screenHeight/4));
                            return false;
                        }else if (mCurPosX - mPosX > 0
                                && (Math.abs(mCurPosX - mPosX) > 135)) {
                            //向右滑動
                            final int index=host.getCurrentTab();
                            if(index!=8-1){
                                host.setCurrentTab(index + 1);
                                //set the display of current tab
                                scroll=(HorizontalScrollView) findViewById(R.id.scroll);
                                new Handler().postDelayed((new Runnable() {
                                    @Override
                                    public void run() {
                                        if (host.getCurrentTab() >3) {
                                            scroll.scrollTo(screenWidth, 0);
                                        }else{
                                            scroll.scrollTo(0, 0);
                                        }
                                    }
                                }), 5);
                            }

                            return true;

                        } else if (mCurPosX - mPosX < 0
                                && (Math.abs(mCurPosX - mPosX) > 135)) {
                            //向左滑动
                            final int index=host.getCurrentTab();
                            if(index!=0){
                                host.setCurrentTab(index-1);
                                //set the display of current tab
                                scroll=(HorizontalScrollView) findViewById(R.id.scroll);
                                scroll.setLeft(0);
                                new Handler().postDelayed((new Runnable() {
                                    @Override
                                    public void run() {
                                        if (host.getCurrentTab() <=3) {
                                            scroll.scrollTo(0, 0);
                                            //scroll.setLeft(0);
                                        }else{
                                            scroll.scrollTo(screenWidth, 0);
                                        }

                                    }
                                }), 5);
                            }
                            return true;
                        }

                        break;
                }
                return false;//will not affect other clicks
            }

        });
    }
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.programbyday);

        sessionDate = "";
        Bundle b = getIntent().getExtras();
        if (b != null) {
            sessionDate = b.getString("date");
        }

        day1lv = (ListView) findViewById(R.id.day1);
        System.out.println(R.id.day1);
        day2lv = (ListView) findViewById(R.id.day2);
        System.out.println(R.id.day2);
        day3lv = (ListView) findViewById(R.id.day3);
        System.out.println(R.id.day3);
        day4lv = (ListView) findViewById(R.id.day4);
        System.out.println(R.id.day4);
        day5lv = (ListView) findViewById(R.id.day5);
        System.out.println(R.id.day5);
        day6lv = (ListView) findViewById(R.id.day6);
        System.out.println(R.id.day6);
        day7lv = (ListView) findViewById(R.id.day7);
        System.out.println(R.id.day7);
        day8lv = (ListView) findViewById(R.id.day8);
        System.out.println(R.id.day8);


        // Set up the tabs
        host = (TabHost) findViewById(R.id.tabdates);
        host.setup();


        TabWidget w=host.getTabWidget();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screenWidth = displayMetrics.widthPixels;
       // int screenHeight = displayMetrics.heightPixels;


        //1st day tab
        TabSpec day1 = host.newTabSpec("day1");
        day1.setIndicator("Mon Jun.22  ");
        day1.setContent(R.id.day1);
        host.addTab(day1);

        //2nd day tab
        TabSpec day2 = host.newTabSpec("day2");
        day2.setIndicator("Tue Jun.23  ");
        day2.setContent(R.id.day2);
        host.addTab(day2);

        // 3rd day tab
        TabSpec day3 = host.newTabSpec("day3");
        day3.setIndicator("Wed Jun.24  ");
        day3.setContent(R.id.day3);
        host.addTab(day3);

        // 4th day tab
        TabSpec day4 = host.newTabSpec("day4");
        day4.setIndicator("Thu Jun.25  ");
        day4.setContent(R.id.day4);
        host.addTab(day4);

        // 5th day tab
        TabSpec day5 = host.newTabSpec("day5");
        day5.setIndicator("Fri Jun.26  ");
        day5.setContent(R.id.day5);
        host.addTab(day5);

        // 6th day tab
        TabSpec day6 = host.newTabSpec("day6");
        day6.setIndicator("Sat Jun.27  ");
        day6.setContent(R.id.day6);
        host.addTab(day6);

        // 7th day tab
        TabSpec day7 = host.newTabSpec("day7");
        day7.setIndicator("Sun Jun.28  ");
        day7.setContent(R.id.day7);
        host.addTab(day7);

        // 8th day tab
        TabSpec day8 = host.newTabSpec("day8");
        day8.setIndicator("Mon Jun.29  ");
        day8.setContent(R.id.day8);
        host.addTab(day8);


        for(int i=0;i<8;i++){
            w.getChildTabViewAt(i).setMinimumWidth(screenWidth / 5);

        }

        Calendar c = Calendar.getInstance();
        date = c.get(Calendar.DAY_OF_YEAR);

        if(date<=173)
            //set up default tab
            host.setCurrentTabByTag("day1");
        if(date == 174)
            host.setCurrentTabByTag("day2");
        if(date == 175)
            host.setCurrentTabByTag("day3");
        if(date >= 176)
            host.setCurrentTabByTag("day4");
        if(date >= 177)
            host.setCurrentTabByTag("day5");
        if(date == 178)
            host.setCurrentTabByTag("day6");
        if(date == 179)
            host.setCurrentTabByTag("day7");
        if(date >= 180)
            host.setCurrentTabByTag("day8");


        //set the display of current tab
        scroll=(HorizontalScrollView) findViewById(R.id.scroll);
        scroll.setLeft(screenWidth);
        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                scroll.scrollTo(screenWidth * (host.getCurrentTab() / 4), 0);

            }
        }), 5);


        //1st day
        sessions1 = new ArrayList<Session>();
        sessions1 = getSession("1");
        MyListAdapter adapter1 = new MyListAdapter(sessions1);
        day1lv.setAdapter(adapter1);
        day1lv.setOnItemClickListener(adapter1);


        //2nd day
        sessions2 = new ArrayList<Session>();
        sessions2 = getSession("2");
        MyListAdapter adapter2 = new MyListAdapter(sessions2);
        day2lv.setAdapter(adapter2);
        day2lv.setOnItemClickListener(adapter2);

        //3rd day
        sessions3 = new ArrayList<Session>();
        sessions3 = getSession("3");
        MyListAdapter adapter3 = new MyListAdapter(sessions3);
        day3lv.setAdapter(adapter3);
        day3lv.setOnItemClickListener(adapter3);

        //4th day
        sessions4 = new ArrayList<Session>();
        sessions4 = getSession("4");
        MyListAdapter adapter4 = new MyListAdapter(sessions4);
        day4lv.setAdapter(adapter4);
        day4lv.setOnItemClickListener(adapter4);

        //5th day
        sessions5 = new ArrayList<Session>();
        sessions5 = getSession("5");
        MyListAdapter adapter5 = new MyListAdapter(sessions5);
        day5lv.setAdapter(adapter5);
        day5lv.setOnItemClickListener(adapter5);


        //6th day
        sessions6 = new ArrayList<Session>();
        sessions6 = getSession("6");
        MyListAdapter adapter6 = new MyListAdapter(sessions6);
        day6lv.setAdapter(adapter6);
        day6lv.setOnItemClickListener(adapter6);

        //7th day
        sessions7 = new ArrayList<Session>();
        sessions7 = getSession("7");
        MyListAdapter adapter7 = new MyListAdapter(sessions7);
        day7lv.setAdapter(adapter7);
        day7lv.setOnItemClickListener(adapter7);

        //8th day
        sessions8 = new ArrayList<Session>();
        sessions8 = getSession("8");
        MyListAdapter adapter8 = new MyListAdapter(sessions8);
        day8lv.setAdapter(adapter8);
        day8lv.setOnItemClickListener(adapter8);


        //set the Gesture
        setGestureListener(day1lv);
        setGestureListener(day2lv);
        setGestureListener(day3lv);
        setGestureListener(day4lv);
        setGestureListener(day5lv);
        setGestureListener(day6lv);
        setGestureListener(day7lv);
        setGestureListener(day8lv);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
       // menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(ProgramByDay.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(ProgramByDay.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(ProgramByDay.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(ProgramByDay.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(ProgramByDay.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    private void insertSessiontoDB(ArrayList<Session> sList) {
        // TODO Auto-generated method stub
        db = new DBAdapter(this);
        db.open();
        for (int i = 0; i < sList.size(); i++) {
            long error = db.insertSession(sList.get(i));
            if (error == -1)
                System.out.println(error);
        }
        db.close();

    }

    public final class ViewHolder {
        public TextView firstCharHintTextView, title, location;
    }

    private class MyListAdapter extends BaseAdapter implements OnItemClickListener {
        private ArrayList<Session> s;

        public MyListAdapter(ArrayList<Session> sList) {
            this.s = sList;
        }

        @Override
        public int getCount() {
            return s.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
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

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(s.get(position).name);

            if (s.get(position).room.compareToIgnoreCase("NULL") == 0 || s.get(position).room.compareToIgnoreCase("\n") == 0)
                holder.location.setVisibility(View.GONE);
            else {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText("At " + s.get(position).room);
            }

            int idx = position - 1;

            String previewb = idx >= 0 ? s.get(idx).beginTime : "";
            String currentb = s.get(position).beginTime;
            String previewe = idx >= 0 ? s.get(idx).endTime : "";
            String currente = s.get(position).endTime;

            if (currentb.compareTo(previewb) == 0 && currente.compareTo(previewe) == 0) {
                holder.firstCharHintTextView.setVisibility(View.GONE);
            } else {

                holder.firstCharHintTextView.setVisibility(View.VISIBLE);
                try {
                    beginDate = sdfSource.parse(s.get(position).beginTime);
                    endDate = sdfSource.parse(s.get(position).endTime);
                    begTime = sdfDestination.format(beginDate);
                    endTime = sdfDestination.format(endDate);
                    holder.firstCharHintTextView.setText(begTime + " - " + endTime);
                } catch (Exception e) {
                    System.out.println("Date Exception");
                }
            }
            return convertView;
        }


//        @Override
//        public boolean isEnabled(int position) {
//            if (getdata(s.get(position).ID).size() == 0)
//                return false;
//            else
//                return true;
//        }

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                long arg3) {
            //System.out.println("*******************" + "");
            Intent in = new Intent(ProgramByDay.this, PaperInSession.class);
            in.putExtra("papersessionID", s.get(pos).ID);
            in.putExtra("sessionName", s.get(pos).name);
            in.putExtra("bTime", s.get(pos).beginTime);
            in.putExtra("eTime", s.get(pos).endTime);
            in.putExtra("date", s.get(pos).date);
            in.putExtra("room", s.get(pos).room);
            startActivity(in);
        }

    }

    private ArrayList<Session> getSession(String dayid) {
        // TODO Auto-generated method stub
        ArrayList<Session> s = new ArrayList<Session>();
        db = new DBAdapter(this);
        db.open();
        s = db.getSessionBydayID(dayid);
        db.close();
        return s;
    }

    private ArrayList<Paper> getdata(String sessionID) {
        ArrayList<Paper> pList = new ArrayList<Paper>();
        // get data at local
        db = new DBAdapter(this);
        db.open();
        pList = db.getPapersBysessionID(sessionID);
        db.close();

        return pList;
    }
}