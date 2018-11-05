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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import static org.edforge.efhomescreen.TCONST.DAY_CHANGE;
import static org.edforge.efhomescreen.TCONST.HOME;
import static org.edforge.efhomescreen.TCONST.NAME_CHANGE;
import static org.edforge.efhomescreen.TCONST.USER_DATE;

/**
 * Created by kevin on 11/3/2018.
 */

public class UserNameView extends FrameLayout {

    private Context           mContext;
    private IEdForgeLauncher  mCallback;

    private Button      bHome;
    private Button      bDel;
    private TextView    mOutput;

    private Button      bNext;
    private Button      bBack;

    private String      mData = "";


    private Integer[] ids = {
            R.id.bA,R.id.bB,R.id.bC,R.id.bD,R.id.bE,R.id.bF,R.id.bG,R.id.bH,R.id.bI,R.id.bJ,
            R.id.bK,R.id.bL,R.id.bM,R.id.bN,R.id.bO,R.id.bP,R.id.bQ,R.id.bR,R.id.bS,
            R.id.bT,R.id.bU,R.id.bV,R.id.bW,R.id.bX,R.id.bY,R.id.bZ};

    private Character[] chars = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};

    private ArrayList<Integer>   kbdIds  = new ArrayList<Integer>(Arrays.asList(ids));
    private ArrayList<Character> charIds = new ArrayList<Character>(Arrays.asList(chars));

    private LocalBroadcastManager   bManager;
    private nameViewReceiver        bReceiver;

    static final String TAG = "UserNameView";


    public UserNameView(Context context) {
        super(context);
        init(context, null);
    }

    public UserNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserNameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {

        mContext = context;

        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(mContext);

        IntentFilter filter = new IntentFilter(TCONST.SET_NAME);

        bReceiver = new nameViewReceiver();
        bManager.registerReceiver(bReceiver, filter);
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        // Wire up the keypad
        //
        for(int i1 = 0 ; i1 < kbdIds.size() ; i1++) {

            try {
                int id = (int) kbdIds.get(i1);
                Button kbdButton = (Button) findViewById(id);

                kbdButton.setOnClickListener(clickListener);
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        mOutput     = (TextView) findViewById(R.id.output);

        bHome       = (Button) findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);

        bDel        = (Button) findViewById(R.id.bDel);
        bDel.setOnClickListener(clickListener);

        bNext       = (Button) findViewById(R.id.bNext);
        bNext.setOnClickListener(clickListener);
    }


    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;
    }


    private void updateName(String newName) {

        mData = newName;
        mOutput.setText(mData);

        broadcast(NAME_CHANGE, mData);
    }



    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        int vid = v.getId();

        switch(vid) {
            case  R.id.bHome:
                updateName("");
                mCallback.nextStep(HOME);
                break;

            case  R.id.bDel:
                if (!mData.isEmpty()) {
                    updateName(mData.substring(0, mData.length() - 1));
                }
                break;

            case  R.id.bNext:
                if(!mData.isEmpty()) {
                    mCallback.nextStep(USER_DATE);
                    broadcast(NAME_CHANGE, mData);
                }
                break;

            default:
                int ndx = kbdIds.indexOf(vid);

                if (mData.length() < 30) {
                    updateName(mData += charIds.get(ndx));
                }
                break;
        }
        }
    };


    public void broadcast(String Action, String Msg) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.NAME_FIELD, Msg);

        bManager.sendBroadcast(msg);
    }


    class nameViewReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent) {

            Log.d("nameView", "Broadcast recieved: ");

            switch(intent.getAction()) {

                case TCONST.SET_NAME:

                    String newName = intent.getStringExtra(TCONST.NAME_FIELD);
                    updateName(newName);
                    break;

            }
        }
    }
}
