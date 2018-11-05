package org.edforge.efhomescreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import org.edforge.util.JSON_Helper;
import org.edforge.util.JSON_Util;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.edforge.efhomescreen.TCONST.CREATE_ACCT;
import static org.edforge.efhomescreen.TCONST.EXISTING_ACCT;
import static org.edforge.efhomescreen.TCONST.REPLACE;
import static org.edforge.efhomescreen.TCONST.SET_DAY;
import static org.edforge.efhomescreen.TCONST.SET_MONTH;
import static org.edforge.efhomescreen.TCONST.SET_NAME;
import static org.edforge.efhomescreen.TCONST.START_TUTOR;


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
    private UserNameView    UserNameView;
    private UserDateView    UserDateView;
    private UserConfView    UserConfView;
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

        bReceiver = new homeReceiver();
        bManager.registerReceiver(bReceiver, filter);

        setFullScreen();

        mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        breakOutView = (BreakOutView) mInflater.inflate(R.layout.breakout_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        breakOutView.setLayoutParams(params);
        breakOutView.setMode(breakOutView.BREAK_OUT, this);
        breakOutView.setCallback(this);

        UserNameView = (UserNameView) mInflater.inflate(R.layout.user_name_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        UserNameView.setLayoutParams(params);
        UserNameView.setCallback(this);

        UserDateView = (UserDateView) mInflater.inflate(R.layout.user_date_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        UserDateView.setLayoutParams(params);
        UserDateView.setCallback(this);

        UserConfView = (UserConfView) mInflater.inflate(R.layout.conf_acct_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        UserConfView.setLayoutParams(params);
        UserConfView.setCallback(this);


        // Create the start dialog
        //
        startView = (StartView) mInflater.inflate(R.layout.start_view, null );
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        startView.setLayoutParams(params);
        startView.setCallback(this);

        nextStep(TCONST.HOME);
        setFullScreen();


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


//        try {
//            Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);
//        }
//        catch(Exception e) {
//
//        }

//        startLockTask();
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

        String intent = "org.edforge.efandroidhost.EFTutorLauncher";

        String dataSource = "";
        String intentData = "";
        String features = "";

        Intent extIntent = new Intent();
        String extPackage;

        Log.d(TAG, "Start: Tutor Launcher");

        extPackage = intent.substring(0, intent.lastIndexOf('.'));

        extIntent.setClassName(extPackage, intent);
        extIntent.putExtra("datasource", dataSource);
        extIntent.putExtra("intentdata", intentData);
        extIntent.putExtra("features", features);

        try {
            activityLocal.startActivity(extIntent);
        }
        catch(Exception e) {
            Log.e("Home", "Launch Error: " + e + " : " + intent);
        }
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

        try {
            mJsonWriter = new JSON_Util(mUserDataPackage, DATA_PATH + TCONST.USER_DATA, REPLACE);

        } catch (IOException e) {
            e.printStackTrace();
        }
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

            case TCONST.NEW_USER:
                changemode(TCONST.CREATE_ACCT);
                switchView(UserNameView);
                break;

            case TCONST.LOGIN_STEP:
                changemode(TCONST.EXISTING_ACCT);
                switchView(UserNameView);
                break;

            case TCONST.USER_DATE:
                switchView(UserDateView);
                break;

            case TCONST.USER_CONF:
                switchView(UserConfView);
                break;

            case TCONST.START_TUTOR:

                switch(mAccountMode) {
                    case CREATE_ACCT:
                        createNewUser();
                        // fall through and start tutor

                    case EXISTING_ACCT:
                        startTutor();
                        break;
                }
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


}