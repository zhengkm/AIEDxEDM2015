package pitt.edu.AIEDxEDM2015;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import data.Author;
import data.Conference;
import data.DBAdapter;
import data.Paper;
import data.UserScheduledToServer;


public class Authors extends Activity implements AdapterView.OnItemClickListener/*OnScrollListener*/, Runnable {


    /**
     * Called when the activity is first created.
     */
    private MyListAdapter adapter;
    private ArrayList<Author> authorList;
    private WindowManager windowManager;
    private TextView txtOverlay;   //put in windows manager to show char hint
    private Handler handler1;
    private DisapearThread disapearThread;
    private int scrollState;
    private ListView list, listview;
    private DBAdapter db;
    private ImageButton ib;
    private String paperID, paperStatus;
    private ProgressDialog pd;
    private UserScheduledToServer us2s;
    private int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.proceedingsby);

        txtOverlay = (TextView) LayoutInflater.from(this).inflate(R.layout.list_popup_char_hint, null);
        txtOverlay.setVisibility(View.INVISIBLE);
        //WindowManager settings
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                //set to no focus state
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                //translucent
                PixelFormat.TRANSLUCENT);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(txtOverlay, lp);

        us2s = new UserScheduledToServer();

        handler1 = new Handler();
        disapearThread = new DisapearThread();

        db = new DBAdapter(this);
        db.open();
        authorList = new ArrayList<Author>();
        authorList = db.getAllAuthors();
        //System.out.println("!!!!!!!!!!!!!"+authorList.size());
        //System.out.println(pList.size());
        db.close();

        findViewById(R.id.FrameLayout01).setVisibility(View.VISIBLE);

        list = (ListView) this.findViewById(R.id.list); // real ListView
        listview = (ListView) this.findViewById(R.id.listview); //side ListView

        adapter = new MyListAdapter(this);
        list.setAdapter(adapter);
        //list.setOnScrollListener(this);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.textview, py);
        listview.setAdapter(adapter1);

        listview.setDivider(null);
        listview.setOnItemClickListener(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            //this.finish(); this.onStop(); this.onDestroy();
            this.finish();
            Intent in = new Intent(Authors.this, MainInterface.class);
            startActivity(in);
        }

        return super.onKeyDown(keyCode, event);
    }

    private class DisapearThread implements Runnable {
        @Override
        public void run() {
            // to avoid invisible in 1.5second
            if (scrollState == ListView.OnScrollListener.SCROLL_STATE_IDLE) {
                txtOverlay.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String getUserID() {
        String id = "";
        try {
            SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
            id = getUserID.getString("userID", "");
        } catch (Exception e) {
        }

        if (id.compareTo("") != 0)
            Conference.userSignin = true;
        return id;
    }

    public void callThread() {

        pd = ProgressDialog.show(this, "Synchronization", "Please Wait...",
                true, false);
        Thread thread = new Thread(this);
        thread.start();

    }

    private void CallSignin() {
//        Intent in = new Intent(Authors.this, Signin.class);
//        in.putExtra("activity", "ProceedingsByAuthor");
//        in.putExtra("paperID", paperID);
//        startActivity(in);
    }

    public String getPaperStarred(String paperID) {
        String status;
        db = new DBAdapter(this);
        db.open();

        status = db.getPaperStarredStatus(paperID);

        db.close();

        return status;
    }

    public String getPaperScheduled(String paperID) {
        String status;
        db = new DBAdapter(this);
        db.open();

        status = db.getPaperScheduledStatus(paperID);

        db.close();

        return status;
    }

    public void updateUserPaperStatus(String paperID, String status,
                                      String which) {
        db = new DBAdapter(this);
        db.open();
        if (which.compareTo("schedule") == 0)
            db.updatePaperBySchedule(paperID, status);
        else
            db.updatePaperByStar(paperID, status);
        db.close();
    }

    public void deleteMyScheduledPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.deleteMyScheduledPaper(paperID);
        db.close();
    }

    public void insertMyStarredPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.insertMyStarredPaper(paperID);
        db.close();
    }

    public void deleteMyStarredPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.deleteMyStarredPaper(paperID);
        db.close();
    }

    public void insertMyScheduledPaper(String paperID) {
        db = new DBAdapter(this);
        db.open();
        db.insertMyScheduledPaper(paperID);
        db.close();
    }

    static class ViewHolder {
        public TextView firstCharHintTextView;
        public TextView nameTextView;
        ImageButton star, schedule;
        public TextView time, author, type, recommend;
    }

    private class MyListAdapter extends BaseAdapter implements
            View.OnClickListener {
        private LayoutInflater inflater;
        SimpleDateFormat sdfSource = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sdfDestination = new SimpleDateFormat("h:mm a");
        Date beginDate, endDate;
        String begTime, endTime;
        private ArrayList<Author> authorLists;

        public MyListAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            authorLists=authorList;
        }

        @Override
        public int getCount() {
            return authorLists.size();
        }

        @Override
        public Object getItem(int position) {
            return authorLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();
                holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
                holder.nameTextView = (TextView) convertView.findViewById(R.id.title);
                convertView.findViewById(R.id.time).setVisibility(View.GONE);
                convertView.findViewById(R.id.author).setVisibility(View.GONE);
                convertView.findViewById(R.id.type).setVisibility(View.GONE);
                convertView.findViewById(R.id.location).setVisibility(View.GONE);
//                //holder.recommend = (TextView) convertView.findViewById(R.id.recommend);
                holder.schedule = (ImageButton) convertView.findViewById(R.id.ImageButton01);
                holder.schedule.setVisibility(View.GONE);
//
                holder.star = (ImageButton) convertView.findViewById(R.id.ImageButton02);
                holder.star.setVisibility(View.GONE);
//
//
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nameTextView.setText(authorLists.get(position).name);
            holder.nameTextView.setOnClickListener(this);
            holder.nameTextView.setTag(position);


            int idx = position - 1;

            char previewChar = idx >= 0 ? authorLists.get(idx).name.charAt(0) : ' ';
            char currentChar = authorLists.get(position).name.charAt(0);

            char newPreviewChar = Character.toUpperCase(previewChar);
            char newCurrentChar = Character.toUpperCase(currentChar);
            if (newCurrentChar != newPreviewChar) {
                holder.firstCharHintTextView.setVisibility(View.VISIBLE);
                holder.firstCharHintTextView.setText(String.valueOf(newCurrentChar));
            } else {

                holder.firstCharHintTextView.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            TextView tv;
            int index;
            switch (v.getId()) {
                case R.id.title:
                    tv = (TextView) v;
                    index = Integer.parseInt(tv.getTag().toString());
//                    ProceedingsByAuthor.this.finish();
//                    windowManager.removeView(txtOverlay);
                    Intent in = new Intent(Authors.this, AuthorDetail.class);
                    in.putExtra("authorID", authorLists.get(index).ID);
                    in.putExtra("authorName",authorLists.get(index).name);
                    in.putExtra("activity", "Authors");
                    in.putExtra("key", "no");
                    startActivity(in);
                    break;
                default:
                    break;
            }

        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        String s = ((TextView) view).getText().toString();
        txtOverlay.setText(s);
        txtOverlay.setVisibility(View.VISIBLE);
        handler1.removeCallbacks(disapearThread);

        handler1.postDelayed(disapearThread, 1500);

        int localPosition = binSearch(authorList, s);

        if (localPosition != -1) {
            txtOverlay.setVisibility(View.INVISIBLE);
            list.setSelection(localPosition);
        }
    }

    public static int binSearch(ArrayList<Author> a, String s) {
        for (int i = 0; i < a.size(); i++) {
            if (s.equalsIgnoreCase("" + a.get(i).name.charAt(0))) {
                return i;
            }
        }
        return -1;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        txtOverlay.setVisibility(View.INVISIBLE);
        windowManager.removeView(txtOverlay);
    }

    private String py[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    @Override
    public void run() {
        // TODO Auto-generated method stub
//        if (getPaperScheduled(paperID).compareTo("yes") == 0)
//            paperStatus = us2s.DeleteScheduledPaper2Sever(paperID);
//        else if (getPaperScheduled(paperID).compareTo("no") == 0)
//            paperStatus = us2s.addScheduledPaper2Sever(paperID);
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pd.dismiss();
            // update interface here

        }
    };
}
