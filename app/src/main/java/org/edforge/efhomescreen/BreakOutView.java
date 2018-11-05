package org.edforge.efhomescreen;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 11/2/2018.
 */

public class BreakOutView extends FrameLayout {

    private Context           mContext;
    private IEdForgeLauncher  mCallback;

    private LocalBroadcastManager   bManager;
    private Intent                  mLaunch;

    private Integer[] ids = {
            R.id.b0,R.id.b1,R.id.b2,R.id.b3,R.id.b4,
            R.id.b5,R.id.b6,R.id.b7,R.id.b8,R.id.b9,
            R.id.bdel, R.id.bOK};

    private ArrayList<Integer> padIds = new ArrayList<Integer>(Arrays.asList(ids));

    private TextView mRequest;
    private TextView mOutput;
    private TextView mInfo;
    private Button bHome;

    private String   mMode;
    private String   mPrompt;
    private String   mData = "";
    private Boolean  mLatched = false;

    final private String      ACTION_DEVICE_OWNER = "org.edforge.EF_DEVICE_OWNER";

    final public String       REASON         = "REASON";
    final public String       BREAK_OUT      = "BREAK_OUT";
    final public String       FACTORYRESET   = "FACTORYRESET";

    final private String       TAG       = "BreakOutView";



    public BreakOutView(Context context) {
        super(context);
        init(context, null);
    }

    public BreakOutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BreakOutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // Function to find the index of an element in a primitive array in Java
    public static int find(int[] a, int target)
    {
        for (int i = 0; i < a.length; i++)
            if (a[i] == target)
                return i;

        return -1;
    }
    public void init(Context context, AttributeSet attrs) {

        mContext = context;

        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(getContext());
    }

    public void setMode(String mode, IEdForgeLauncher _callback) {

        mMode     = mode;
        mCallback = _callback;

        switch(mMode) {
            case FACTORYRESET:
                mRequest.setText("Factory Reset Request");
                mPrompt = "WARNING: Device will be Factory Reset:  Press 5 to Wipe Device";
                mRequest.setBackgroundColor(0xFFFF0000);
                break;

            case BREAK_OUT:
                mRequest.setText("System Breakout");
                mPrompt = "To Exit to System - Press 5 ";
                mRequest.setBackgroundColor(0xFF0000FF);
                break;
        }
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        // Wire up the keypad
        //
        for(int i1 = 0 ; i1 < padIds.size() ; i1++) {

            try {
                int id = (int) padIds.get(i1);
                Button padButton = (Button) findViewById(id);

                padButton.setOnClickListener(clickListener);
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }

        }
        mRequest    = (TextView) findViewById(R.id.request);
        mOutput     = (TextView) findViewById(R.id.output);
        mInfo       = (TextView) findViewById(R.id.info);

        mOutput.setText("");
        mInfo.setVisibility(INVISIBLE);
    }


    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;

        bHome = (Button)   findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vid = v.getId();

            if (vid == R.id.bHome) {

                mCallback.gotoHome();

            } else {
                int ndx = padIds.indexOf(vid);

                switch (ndx) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        if (mLatched) {

                            mLatched = false;
                            mData = "";
                            mOutput.setText(mData);

                            if (mMode.equals(FACTORYRESET)) {
                                Log.i(TAG, "reseting device");

                                // Open the main screen
                                //
                                mLaunch = new Intent();
                                mLaunch.setAction(ACTION_DEVICE_OWNER);
                                mLaunch.putExtra(REASON, FACTORYRESET);
                                mLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                mContext.startActivity(mLaunch);
                            }
                            else {
                                Log.i(TAG, "Breaking Out To System");

                                // Open the main screen
                                //
                                mLaunch = new Intent();
                                mLaunch.setAction(ACTION_DEVICE_OWNER);
                                mLaunch.putExtra(REASON, BREAK_OUT);
                                mLaunch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                mContext.startActivity(mLaunch);

                            }
                            break;
                        }
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (mData.length() < 25) {
                            mData += Integer.toString(ndx);
                            mOutput.setText(mData);
                        }
                        break;

                    case 10:
                        if (!mData.isEmpty()) {
                            mData = mData.substring(0, mData.length() - 1);
                            mOutput.setText(mData);
                        }
                        break;

                    case 11:

                        if (mData.equals("314159")) {
                            if (!mLatched) {
                                mLatched = true;
                                mInfo.setText(mPrompt);
                                mInfo.setVisibility(VISIBLE);
                            }
                        }
                        break;

                }

                if (!mData.equals("314159")) {
                    mInfo.setVisibility(INVISIBLE);
                    mLatched = false;
                }
            }
        }
    };


}
