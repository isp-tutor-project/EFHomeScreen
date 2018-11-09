package org.edforge.efhomescreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.edforge.util.JSON_Helper;
import org.edforge.util.JSON_Util;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.edforge.efhomescreen.TCONST.CREATE_ACCT;
import static org.edforge.efhomescreen.TCONST.EFHOME_FINISHER_INTENT;
import static org.edforge.efhomescreen.TCONST.EFHOST_LAUNCH_INTENT;
import static org.edforge.efhomescreen.TCONST.EXISTING_ACCT;
import static org.edforge.efhomescreen.TCONST.OWNER_BREAK_OUT;
import static org.edforge.efhomescreen.TCONST.REPLACE;
import static org.edforge.efhomescreen.TCONST.SET_DAY;
import static org.edforge.efhomescreen.TCONST.SET_MONTH;
import static org.edforge.efhomescreen.TCONST.SET_NAME;
import static org.edforge.efhomescreen.TCONST.USER_FIELD;


public class HomeActivity extends Activity implements IEdForgeLauncher{

    static public MasterContainer   masterContainer;
    static public Activity          activityLocal;

    private LocalBroadcastManager   bManager;
    private homeReceiver            bReceiver;

    private JSON_Util               mJsonWriter = null;
    private LayoutInflater          mInflater;
    private View                    mCurrView = null;

    private String                  mUser;
    private String                  mName;
    private int                     mMonth;
    private int                     mDay;

    private BreakOutView    breakOutView;
    private UserNameView    userNameView;
    private UserDateView    userDateView;
    private UserConfView    userConfView;
    private EarBudView      earBudView;
    private SoundCheckView  soundCheckView;

    private StartView       startView;
    private UserDataPackage mUserDataPackage;

    private String          mAccountMode       = TCONST.UNKNOWN_MODE;

    public final static String  ASSET_FOLDER   = Environment.getExternalStorageDirectory() + TCONST.EDFORGE_FOLDER;
    public final static String  UPDATE_FOLDER  = Environment.getExternalStorageDirectory() + TCONST.EDFORGE_UPDATE_FOLDER;
    public final static String  LOG_PATH       = Environment.getExternalStorageDirectory() + TCONST.EDFORGE_LOG_FOLDER;
    public final static String  DATA_PATH      = Environment.getExternalStorageDirectory() + TCONST.EDFORGE_DATA_FOLDER;
    public final static String  XFER_PATH      = Environment.getExternalStorageDirectory() + TCONST.EDFORGE_DATA_TRANSFER;

    public final static String[] efPaths = {ASSET_FOLDER,UPDATE_FOLDER,LOG_PATH,DATA_PATH,XFER_PATH};

    final private String TAG = "HomeActivity";

    // debug
    String JSONDATA = "{\"currUser\":{\"userName\":\"KEVINWI-DEC-27\",\"currTutor\":\"\",\"currScene\":\"\"},\"users\":[{\"userName\":\"KEVINWI-DEC-27\",\"currTutor\":\"test\",\"currScene\":\"scene1\"}, {\"userName\":\"JACKWE-Jan-2\",\"currTutor\":\"test2\",\"currScene\":\"scene12\"}]}";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FrameLayout.LayoutParams params;

        super.onCreate(savedInstanceState);

        activityLocal = this;
        validatePaths(efPaths);

        // Get the primary container for tutors
        setContentView(R.layout.activity_home);
        masterContainer = (MasterContainer)findViewById(R.id.master_container);

        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter(TCONST.NAME_CHANGE);
        filter.addAction(TCONST.MONTH_CHANGE);
        filter.addAction(TCONST.DAY_CHANGE);
        filter.addAction(TCONST.USER_CHANGE);
        filter.addAction(TCONST.START_TUTOR);
        filter.addAction(TCONST.OWNER_BREAK_OUT);
        filter.addAction(EFHOME_FINISHER_INTENT);

        bReceiver = new homeReceiver();
        bManager.registerReceiver(bReceiver, filter);

        setFullScreen();

        mJsonWriter = new JSON_Util();
        mInflater   = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        breakOutView = (BreakOutView) mInflater.inflate(R.layout.breakout_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        breakOutView.setLayoutParams(params);
        breakOutView.setMode(breakOutView.BREAK_OUT, this);
        breakOutView.setCallback(this);

        userNameView = (UserNameView) mInflater.inflate(R.layout.user_name_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        userNameView.setLayoutParams(params);
        userNameView.setCallback(this);

        userDateView = (UserDateView) mInflater.inflate(R.layout.user_date_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        userDateView.setLayoutParams(params);
        userDateView.setCallback(this);

        userConfView = (UserConfView) mInflater.inflate(R.layout.conf_acct_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        userConfView.setLayoutParams(params);
        userConfView.setCallback(this);

        earBudView = (EarBudView) mInflater.inflate(R.layout.earbud_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        earBudView.setLayoutParams(params);
        earBudView.setCallback(this);

        soundCheckView = (SoundCheckView) mInflater.inflate(R.layout.sound_chk_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        soundCheckView.setLayoutParams(params);
        soundCheckView.setCallback(this);

        // Create the start dialog
        //
        startView = (StartView) mInflater.inflate(R.layout.start_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        startView.setLayoutParams(params);
        startView.setCallback(this);

        // Load the user data file
        //
        mUserDataPackage = new UserDataPackage();
        String jsonData  = JSON_Helper.cacheDataByName(DATA_PATH + TCONST.USER_DATA);

        try {
            if(!jsonData.isEmpty())
                mUserDataPackage.loadJSON(new JSONObject(jsonData), null);

        } catch (Exception e) {

            // TODO: Manage Exceptions
            Log.e(TAG, "UserData Parse Error: " + e);
        }

        nextStep(TCONST.HOME);

        setFullScreen();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bManager.unregisterReceiver(bReceiver);
    }


    /**
     * get time stamp string for current time in milliseconds
     */
    protected String timestampMillis() {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.SSS", Locale.US).format(new Date(System.currentTimeMillis()));
    }



    private void setFullScreen() {

        ((View) masterContainer).setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    public void startTutor() {

        Log.d(TAG, "Start: Tutor Launcher");

        Intent launch = new Intent(EFHOST_LAUNCH_INTENT);
        launch.putExtra(USER_FIELD, mUser);

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(launch, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if(isIntentSafe) {
            startActivity(launch);
        }
    }

    public void finishActivitywithResult() {
        setResult(1);
    }

    @Override
    public void startBreakOut() {

        switchView(breakOutView);
    }


    @Override
    public boolean queryUserExists() {

        boolean result = false;

        if(mUserDataPackage.users != null) {
            for (UserData user : mUserDataPackage.users) {

                if (user.userName.equals(mUser)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }


    public void createNewUser() {

        mUserDataPackage.addUser(mUser);
    }


    @Override
    public void gotoHome() {

        nextStep(TCONST.HOME);
    }


    private void changemode(String mode) {

        mAccountMode = mode;
        broadcast(TCONST.MODE_CHANGE, mode);
    }


    @Override
    public void nextStep(String stepID) {

        switch(stepID) {
            case TCONST.HOME:
                switchView(startView);
                broadcast(SET_NAME,"");
                broadcast(SET_MONTH,-1);
                broadcast(SET_DAY,-1);
                break;

            case TCONST.USER_NAME:
                changemode(TCONST.EXISTING_ACCT);
                switchView(userNameView);
                break;

            case TCONST.USER_NEW:
                changemode(TCONST.CREATE_ACCT);
                switchView(userNameView);
                break;


            case TCONST.USER_DATE:
                switchView(userDateView);
                break;

            case TCONST.USER_CONF:
                switchView(userConfView);
                break;

            case TCONST.EAR_BUD_CHECK:
                switchView(earBudView);
                break;

            case TCONST.SOUND_CHECK:
                switchView(soundCheckView);
                break;

        }
    }


    public void switchView(View target) {

        if(mCurrView != null)
            masterContainer.removeView(mCurrView);

        masterContainer.addAndShow(target);
        mCurrView = target;
    }


    @Override
    protected void onStart() {
        super.onStart();

        setFullScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void broadcast(String Action, int Msg) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.INT_FIELD, Msg);

        bManager.sendBroadcast(msg);
    }
    public void broadcast(String Action, String Msg) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.NAME_FIELD, Msg);

        bManager.sendBroadcast(msg);
    }


    class homeReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent) {

            Log.d("homeReceiver", "Broadcast recieved: ");

            switch(intent.getAction()) {

                case TCONST.USER_CHANGE:
                    mUser = intent.getStringExtra(TCONST.NAME_FIELD);

                    mUserDataPackage.currUser.userName = mUser;
                    break;

                case TCONST.NAME_CHANGE:
                    mName = intent.getStringExtra(TCONST.NAME_FIELD);

                    break;

                case TCONST.MONTH_CHANGE:
                    mMonth = intent.getIntExtra(TCONST.INT_FIELD, -1);

                    break;

                case TCONST.DAY_CHANGE:
                    mDay = intent.getIntExtra(TCONST.INT_FIELD, -1);

                    break;

                case TCONST.START_TUTOR:

                    switch(mAccountMode) {
                        case CREATE_ACCT:
                            createNewUser();
                            // fall through and start tutor

                        case EXISTING_ACCT:
                            mJsonWriter.write(mUserDataPackage, DATA_PATH + TCONST.USER_DATA, REPLACE);

                            startTutor();
                            break;
                    }
                    break;

                case EFHOME_FINISHER_INTENT:
                case OWNER_BREAK_OUT:
                    finish();
//                    finishActivitywithResult();
                    break;
            }
        }
    }


    public String validatePath(String path) {

        String sdcard = "";
        String[] pathArr = path.split("/");

        for(String folder:pathArr) {

            if((folder != null) && (!folder.isEmpty())) {

                sdcard += File.separator + folder;

                File folderInstance = new File(sdcard);

                if(!folderInstance.exists()) {
                    Log.i(TAG, "Making: " + sdcard);
                    folderInstance.mkdir();
                }
            }
        }

        return path;
    }


    public void validatePaths(String[] paths) {

        for(String folder:paths) {

            validatePath(folder);
        }
    }


    /**
     * TODO: Manage the back button
     */
    @Override
    public void onBackPressed() {

        // Allow the screen to sleep when not in a session
        //
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setFullScreen();
    }


}
