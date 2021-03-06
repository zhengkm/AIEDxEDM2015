package pitt.edu.AIEDxEDM2015;


import java.util.ArrayList;
import java.util.HashMap;


import data.Author;
import data.CheckDBUpdate;
import data.Conference;
import data.ConferenceInfoParser;
import data.DBAdapter;
import data.ConferenceDataLoad;
import data.KeynoteWorkshopPosterParse;
import data.LoadPaperFromDB;
import data.LoadSessionFromDB;
import data.Paper;
import data.PaperContent;
import data.PaperContentParse;
import data.Keynote;
import data.Poster;
import data.RecommendParse;
import data.Session;
import data.UserScheduleParse;
import data.Workshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateOption extends Activity {


    private ProgressDialog pd;
    private DBAdapter db;
    private TextView session, keynote, presentation, paper, success, sync;
    private AlertDialog ad;
    private ImageButton up;
    private final int MENU_HOME = Menu.FIRST;
    private final int MENU_TRACK = Menu.FIRST + 1;
    private final int MENU_SESSION = Menu.FIRST + 2;
    private final int MENU_STAR = Menu.FIRST + 3;
    private final int MENU_SCHEDULE = Menu.FIRST + 4;
    private final int MENU_RECOMMEND = Menu.FIRST + 5;
    private final int MENU_KEYNOTE = Menu.FIRST + 6;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.update);
        db = new DBAdapter(this);
        up = (ImageButton) findViewById(R.id.ImageButton01);
        up.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isConnect(UpdateOption.this)) {
                    ad = new AlertDialog.Builder(UpdateOption.this)
                            .setTitle("Update Options")
                            .setMessage("It takes about \"1 to 2 minutes\" to update local DB")
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            dialoginterface.cancel();
                                            new AsyncUpdate().execute();
                                            //update();
                                        }
                                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent in = new Intent(UpdateOption.this, MainInterface.class);
                                    startActivity(in);
                                }
                            })
                            .show();
                } else {
                    ad = new AlertDialog.Builder(UpdateOption.this)
                            .setMessage("This porcess requires internet connection, please check your internet connection.")
                            .setPositiveButton("Back to \"HOME\"",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialoginterface, int i) {
                                            Intent in = new Intent(UpdateOption.this, MainInterface.class);
                                            startActivity(in);
                                        }
                                    })
                            .show();
                }

            }
        });

    }

    public void showDialog(String s) {
        pd = ProgressDialog.show(this, "Synchronization", s, true, false);
    }

    public void dismissDialog() {
        pd.dismiss();
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

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_HOME, 0, "Home").setIcon(R.drawable.home);
        menu.add(0, MENU_TRACK, 0, "Proceedings").setIcon(R.drawable.proceedings);
        menu.add(0, MENU_SESSION, 0, "Schedule").setIcon(R.drawable.session);
        menu.add(0, MENU_STAR, 0, "My Favorite").setIcon(R.drawable.star);
        menu.add(0, MENU_SCHEDULE, 0, "My Schedule").setIcon(R.drawable.schedule);
        menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommends);
        menu.add(0, MENU_KEYNOTE, 0, "Keynote").setIcon(R.drawable.keynote);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent itemintent = new Intent();
        switch (item.getItemId()) {
            case MENU_HOME:
                this.finish();
                itemintent.setClass(UpdateOption.this, MainInterface.class);
                startActivity(itemintent);
                return true;
            case MENU_SESSION:
                this.finish();
                itemintent.setClass(UpdateOption.this, ProgramByDay.class);
                startActivity(itemintent);
                return true;
            case MENU_STAR:
                this.finish();
                itemintent.setClass(UpdateOption.this, MyStaredPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_TRACK:
                this.finish();
                itemintent.setClass(UpdateOption.this, Proceedings.class);
                startActivity(itemintent);
                return true;
            case MENU_SCHEDULE:
                this.finish();
                itemintent.setClass(UpdateOption.this, MyScheduledPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_RECOMMEND:
                this.finish();
                itemintent.setClass(UpdateOption.this, MyRecommendedPapers.class);
                startActivity(itemintent);
                return true;
            case MENU_KEYNOTE:
                this.finish();
                itemintent.setClass(UpdateOption.this, KeyNote.class);
                startActivity(itemintent);
                return true;
        }
        return false;
    }

    private class AsyncUpdate extends AsyncTask<Void, Integer, Integer> {
        protected void onPreExcute() {
        }

        @Override
        protected Integer doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            int state = 0;
            //check update
            boolean needUpdate = false;
            CheckDBUpdate cdbu = new CheckDBUpdate();
            needUpdate = cdbu.check();



            //execute update
            if (needUpdate) {
                ArrayList<Session> sList = new ArrayList<Session>();
                ArrayList<Paper> pList = new ArrayList<Paper>();
                ArrayList<PaperContent> pcList = new ArrayList<PaperContent>();
                ArrayList<Keynote> knList = new ArrayList<Keynote>();
                ArrayList<String> pidList = new ArrayList<String>();
                ArrayList<String> pidRList = new ArrayList<String>();
                ArrayList<String> pidLList = new ArrayList<String>();
                ArrayList<Workshop> wListDes = new ArrayList<Workshop>();
                ArrayList<Poster> poList = new ArrayList<Poster>();
                HashMap<String, Author> authorMap=new HashMap<String, Author>();

                ConferenceDataLoad cdl = new ConferenceDataLoad();

                cdl.loadConferenceInfo();

                //Update keynote and workshop and poster info
                publishProgress(14);
                KeynoteWorkshopPosterParse knp = new KeynoteWorkshopPosterParse();
                knp.getData();
                knList = knp.getKeynoteData();
                wListDes=knp.getwWorkshopData();
                poList=knp.getwPosterData();

                if (knList.size() != 0 && wListDes.size() != 0 && poList.size()!= 0) {
                    publishProgress(12);
                } else {
                    publishProgress(13);
                    state=2;
                    return state;
                }


                //Update session info
                publishProgress(6);
                LoadSessionFromDB sdbr = new LoadSessionFromDB();
                sList = sdbr.getSessionData();
                if (sList.size() != 0) {
                    publishProgress(0);
                } else {
                    publishProgress(1);
                    state=2;
                    return state;
                }

                //Update presentation info
                publishProgress(7);
                LoadPaperFromDB pdbr = new LoadPaperFromDB();
                pList = pdbr.getPaperData();
                if (pList.size() != 0) {
                    publishProgress(2);
                } else {
                    publishProgress(3);
                    state=2;
                    return state;
                }

                //Update paper content info
                publishProgress(8);
                PaperContentParse pcp = new PaperContentParse();
                pcp.getData();
                pcList=pcp.getPaperContent();
                authorMap=pcp.getAuthorMap();
                if (pcList.size() != 0&&authorMap.size() !=0) {
                    publishProgress(4);
                } else {
                    publishProgress(5);
                    state=2;
                    return state;
                }

                ConferenceInfoParser.getConferenceInfo("135");
                Conference.timstamp=cdbu.getTimstamp();
                db.open();
                db.deleteConference();
                long errorr = db.insertConference(Conference.id, Conference.title, Conference.startDate,
                        Conference.endDate, Conference.location, Conference.description, Conference.timstamp);
                if (errorr == -1)
                    System.out.println("Insertion ConferenceInfo Failed");
                db.close();


                if (authorMap.size() != 0 && poList.size()!= 0 && wListDes.size()!=0 && knList.size() != 0 && sList.size() != 0 && pList.size() != 0 && pcList.size() != 0) {
                    try {
                        db.open();
                        db.deleteKeynote();
                        db.deleteWorkshop();
                        db.deletePoster();
                        db.deleteSession();
                        db.deletePaper();
                        db.deleteAllAuthors();
                        db.deleteAuthorToPaper();
                        db.deletePaperContent();


                        for (int i = 0; i < knList.size(); i++) {
                            long error = db.insertKeynote(knList.get(i));
                            if (error == -1)
                                System.out.println("error occured");
                        }

                        for (int i = 0; i < wListDes.size(); i++) {
                            long error = db.insertWorkshopDes(wListDes.get(i));
                            if (error == -1)
                                System.out.println("error occured");
                        }

                        for (int i = 0; i < poList.size(); i++) {
                            long error = db.insertPoster(poList.get(i));
                            if (error == -1)
                                System.out.println("error occured");
                        }

                        for (int i = 0; i < sList.size(); i++) {
                            long error = db.insertSession(sList.get(i));
                            if (error == -1)
                                System.out.println("Insertion Failed session");
                        }
                        for (int i = 0; i < pList.size(); i++) {
                            long error = db.insertPaper(pList.get(i));
                            if (error == -1)
                                System.out.println("Insertion Failed session");
                        }
                        for (int i = 0; i < pcList.size(); i++) {
                            if (pcList.get(i).authors == null || pcList.get(i).authors == "") {
                                pcList.get(i).authors = "No author information available";
                            }
                            if (pcList.get(i).type == null || pcList.get(i).type == "") {
                                pcList.get(i).type = "No type information available";
                            }
                            if (pcList.get(i).title == null || pcList.get(i).title == "") {
                                pcList.get(i).title = "No title information available";
                            }
                            if (pcList.get(i).paperAbstract == null || pcList.get(i).paperAbstract == "") {
                                pcList.get(i).paperAbstract = "No abstract information available";
                            }
                            for(String authorID:pcList.get(i).authorIDList){
                                long error = db.insertAuthorToPaper(authorID,pcList.get(i).id);
                                if (error == -1)
                                    System.out.println("Insert author to paper error occured");
                            }
                            long error = db.insertPaperContent(pcList.get(i));
                            if (error == -1)
                                System.out.println("Insert paper content error occured");
                        }

                        for (Author author:authorMap.values()) {
                            long error = db.insertAuthor(author.ID,author.name);
                            if (error == -1)
                                System.out.println("Insertion Author Failed session");
                        }


                        db.close();
                    } catch (Exception e) {
                        System.out.print(e.getMessage());
                    }
                    //Update recommendation and schedule
                    SharedPreferences getUserID = getSharedPreferences("userinfo", 0);
                    String userID = getUserID.getString("userID", "");

                    if (userID.compareTo("") != 0) {
                        publishProgress(9);
                        UserScheduleParse usp = new UserScheduleParse();
                        RecommendParse rp = new RecommendParse();
                        pidList = usp.getData();
                        pidRList = rp.getRecom();
                        if (pidList.size() != 0) {
                            publishProgress(10);
                            try {
                                db.open();
                                db.updatePaperScheduleToDefault();
                                db.updatePaperRecommendToDefault();
                                db.deleteUserScheduled();
                                db.deleteUserRecommended();
                                pidLList = db.getMyStarredPaper();

                                for (int i = 0; i < pidList.size(); i++) {
                                    db.insertMyScheduledPaper(pidList.get(i));
                                    db.updatePaperBySchedule(pidList.get(i), "yes");
                                }
                                for (int i = 0; i < pidRList.size(); i++) {
                                    db.insertMyRecommendedPaper(pidRList.get(i));
                                    db.updatePaperByRecommend(pidRList.get(i), "yes");
                                }
                                for (int i = 0; i < pidLList.size(); i++) {
                                    db.updatePaperByStar(pidLList.get(i), "yes");
                                }
                                db.close();
                            } catch (Exception e) {
                                //log
                            }
                        } else {
                            publishProgress(11);
                            state = 4;//error: no return
                        }
                    } else {
                        publishProgress(11);
                        state = 3;//error
                    }
                    state += 1;//success
                } else {
                    state = 2;//error
                }
            } else {
                state = 0;
            }

            return state;
        }

        protected void onProgressUpdate(Integer... progress) {
            session = (TextView) findViewById(R.id.sessionupdate);
            presentation = (TextView) findViewById(R.id.presentationupdate);
            paper = (TextView) findViewById(R.id.paperupdate);
            sync = (TextView) findViewById(R.id.papersync);
            keynote = (TextView)findViewById(R.id.keynoteupdate);
            int command = progress[0];
            switch (command) {
                case 0:
                    session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    session.setText("Update introduction,keynote,workshop information: success!");
                    break;
                case 1:
                    session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    session.setText("Fail to update introduction,keynote,workshop information");
                    break;
                case 2:
                    presentation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    presentation.setText("Update presentation information: success!");
                    break;
                case 3:
                    presentation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    presentation.setText("Fail to update presentation information");
                    break;
                case 4:
                    paper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    paper.setText("Update paper information: success!");
                    break;
                case 5:
                    paper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    paper.setText("Fail to update paper information");
                    break;
                case 6:
                    session.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    session.setText("Updating introduction,keynote,workshop information ...");
                    break;
                case 7:
                    presentation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    presentation.setText("Updating presentation information ...");
                    break;
                case 8:
                    paper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    paper.setText("Updating paper information ...");
                    break;
                case 9:
                    sync.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    sync.setText("Updating schedule and recommendation ...");
                    break;
                case 10:
                    sync.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    sync.setText("Update schedule and recommendation: success!");
                    break;
                case 11:
                    sync.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    sync.setText("Fail to update schedule and recommendation");
                    break;
                case 12:
                    keynote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.accept, 0, 0, 0);
                    keynote.setText("Update keynote, poster and workshop information: success!");
                    break;
                case 13:
                    keynote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.error, 0, 0, 0);
                    keynote.setText("Fail to update keynote, poster and workshop information");
                    break;
                case 14:
                    keynote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.db_refresh, 0, 0, 0);
                    keynote.setText("Updating keynote, poster and workshop information ...");
                    break;
                default:
                    break;

            }

        }

        protected void onPostExecute(Integer state) {
            success = (TextView) findViewById(R.id.success);
            switch (state) {
                case 1:
                    Toast.makeText(getApplicationContext(),
                            "Updated!",
                            Toast.LENGTH_LONG)
                            .show();

                    success.setText("Update Success!");
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),
                            "Server error, please try again.",
                            Toast.LENGTH_LONG)
                            .show();

                    success.setText("Update Fail!");
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(),
                            "Is the latest data, server last update was on " + Conference.timstamp,
                            Toast.LENGTH_LONG)
                            .show();

                    success.setText("No need to update.");
                    break;
                case 5:
                    Toast.makeText(getApplicationContext(),
                            "Conference information is updated, but no personal schedule information",
                            Toast.LENGTH_LONG)
                            .show();

                    success.setText("Update Success!Please go to \"My schedule\" to schedule or sync with DB.");
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(),
                            "Conference information is updated, but not personal schedule information due to not sign in.",
                            Toast.LENGTH_LONG)
                            .show();

                    success.setText("Update Success!Please go to \"My schedule\" to schedule or sync with DB.");
                    break;
                default:
                    break;
            }
        }
    }
}
