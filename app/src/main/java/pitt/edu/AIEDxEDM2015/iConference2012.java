package pitt.edu.AIEDxEDM2015;


import java.util.ArrayList;


import data.CheckDBUpdate;
import data.Conference;
import data.ConferenceInfoParser;
import data.DBAdapter;
import data.Keynote;
import data.LoadPaperFromDB;
import data.LoadSessionFromDB;
import data.Paper;
import data.PaperContent;
import data.PaperContentParse;
import data.Poster;
import data.Session;
import data.UserScheduleParse;
import data.Workshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class iConference2012 extends Activity {
    private Handler mHandler = new Handler();
    private TextView status;
    private ImageView imageview;
    private DBAdapter db;

    public int alpha = 255;
    private Handler handler = new Handler();


    /**
     * Called when the activity is first created.
     */



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(pitt.edu.AIEDxEDM2015.R.layout.main);

        imageview = (ImageView) this.findViewById(pitt.edu.AIEDxEDM2015.R.id.Logo);
        imageview.setAlpha(alpha);
        status = (TextView) this.findViewById(pitt.edu.AIEDxEDM2015.R.id.status);

        //updateApp();
        new Thread(new Runnable() {
            public void run() {
                try {
                    if(alpha>0){
                        alpha-=5;
                        mHandler.sendMessage(mHandler.obtainMessage());
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                loadData();

            }

        }).start();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                imageview.setAlpha(alpha);
                //status.invalidate();
            }
        };

    }


    public String getUserID()
    {
        String id = "";

        SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
        id = getUserID.getString("userID", "");

        if(id.compareTo("")!=0)
            Conference.userSignin = true;
        return id;
    }


    public void loadData(){
        Conference.userID=getUserID();
        db=new DBAdapter(this).open();
        db.getConferenceInfo();
        if(Conference.title.equals("")&&Conference.title!=null){//first launch
            finish();
            Intent intent = new Intent(iConference2012.this, FirstLaunchUpdate.class);
            startActivity(intent);
        }else{
            finish();
            Intent in = new Intent(iConference2012.this, MainInterface.class);

            startActivity(in);

        }



    }

    public static boolean isConnect(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null) {

                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public void showToast(final String s) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s,
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}