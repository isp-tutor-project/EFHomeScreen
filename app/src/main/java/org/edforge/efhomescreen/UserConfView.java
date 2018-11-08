package org.edforge.efhomescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import static org.edforge.efhomescreen.TCONST.CREATE_ACCT;
import static org.edforge.efhomescreen.TCONST.EAR_BUD_CHECK;
import static org.edforge.efhomescreen.TCONST.EXISTING_ACCT;
import static org.edforge.efhomescreen.TCONST.HOME;
import static org.edforge.efhomescreen.TCONST.USER_CHANGE;
import static org.edforge.efhomescreen.TCONST.USER_DATE;

/**
 * Created by kevin on 11/3/2018.
 */

public class UserConfView extends FrameLayout {

    private Context           mContext;
    private IEdForgeLauncher  mCallback;

    private Button      bHome;
    private ImageButton bSignin;
    private Button      bBack;
    private TextView    mOutput;
    private TextView    mPrompt1;
    private TextView    mPrompt2;
    private TextView    mPrompt3;

    private String      mData = "";
    private String      mMode;
    private String      mName;
    private int         mMonth;
    private int         mDay;

    private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    private LocalBroadcastManager   bManager;
    private confViewReceiver        bReceiver;

    static final String TAG = "StartView";


    public UserConfView(Context context) {
        super(context);
        init(context, null);
    }

    public UserConfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserConfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        mContext = context;


        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(mContext);

        IntentFilter filter = new IntentFilter(TCONST.NAME_CHANGE);
        filter.addAction(TCONST.MONTH_CHANGE);
        filter.addAction(TCONST.DAY_CHANGE);
        filter.addAction(TCONST.MODE_CHANGE);

        bReceiver = new confViewReceiver();
        bManager.registerReceiver(bReceiver, filter);
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        mOutput     = (TextView) findViewById(R.id.output);
        mPrompt1     = (TextView) findViewById(R.id.mPrompt1);
        mPrompt2     = (TextView) findViewById(R.id.mPrompt2);

        mPrompt3     = (TextView) findViewById(R.id.mPrompt3);
        mPrompt3.setVisibility(INVISIBLE);

        bHome       = (Button) findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);

        bSignin     = (ImageButton) findViewById(R.id.bSignin);
        bSignin.setOnClickListener(clickListener);

        bBack       = (Button) findViewById(R.id.bBack);
        bBack.setOnClickListener(clickListener);
    }


    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;

        bHome = (Button)findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vid = v.getId();

            switch(vid) {
                case  R.id.bHome:
                    mCallback.nextStep(HOME);
                    break;

                case  R.id.bSignin:

                    boolean exists = mCallback.queryUserExists();

                    switch(mMode) {
                        case CREATE_ACCT:

                            if(exists) {
                                mPrompt3.setText("Account Already Exists");
                                mPrompt3.setVisibility(VISIBLE);
                            }
                            else {
                                mCallback.nextStep(EAR_BUD_CHECK);
                            }
                            break;

                        case EXISTING_ACCT:

                            if(exists) {
                                mCallback.nextStep(EAR_BUD_CHECK);
                            }
                            else {
                                mPrompt3.setText("Account not found: Tap the Back button to make corrections");
                                mPrompt3.setVisibility(VISIBLE);
                            }
                            break;
                    }
                    break;

                case  R.id.bBack:
                    mCallback.nextStep(USER_DATE);
                    break;

                default:
                    break;
            }
        }
    };


    private void updateUserID() {

        mPrompt3.setVisibility(INVISIBLE);

        mData =  "";

        if(!mName.isEmpty())
            mData =  mName;
        if(mMonth > 0)
            mData += "-" + months[mMonth];
        if(mDay > 0)
            mData += "-"+ Integer.toString(mDay);

        mOutput.setText(mData);

        broadcast(USER_CHANGE, mData);
    }


    private void updateUserMode() {

        switch(mMode) {
            case CREATE_ACCT:
                mPrompt1.setText("Tap the Sign-in button to Create this new Account:");
                mPrompt2.setVisibility(VISIBLE);
                break;

            case EXISTING_ACCT:
                mPrompt1.setText("Tap Sign-in to Begin");
                mPrompt2.setVisibility(INVISIBLE);
                break;
        }
    }


    public void broadcast(String Action, String Msg) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.NAME_FIELD, Msg);

        bManager.sendBroadcast(msg);
    }


    class confViewReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent) {

            Log.d("nameView", "Broadcast recieved: ");

            switch(intent.getAction()) {

                case TCONST.MODE_CHANGE:
                    mMode = intent.getStringExtra(TCONST.NAME_FIELD);
                    updateUserMode();
                    break;

                case TCONST.NAME_CHANGE:
                    mName = intent.getStringExtra(TCONST.NAME_FIELD);
                    updateUserID();
                    break;

                case TCONST.MONTH_CHANGE:
                    mMonth = intent.getIntExtra(TCONST.INT_FIELD, -1);
                    updateUserID();
                    break;

                case TCONST.DAY_CHANGE:
                    mDay = intent.getIntExtra(TCONST.INT_FIELD, -1);
                    updateUserID();
                    break;
            }
        }
    }
}
