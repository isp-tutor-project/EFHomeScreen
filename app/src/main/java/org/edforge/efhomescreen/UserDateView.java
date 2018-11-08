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
import static org.edforge.efhomescreen.TCONST.USER_NEW;
import static org.edforge.efhomescreen.TCONST.MONTH_CHANGE;
import static org.edforge.efhomescreen.TCONST.USER_CONF;

/**
 * Created by kevin on 11/3/2018.
 */

public class UserDateView extends FrameLayout {

    private Context           mContext;
    private IEdForgeLauncher  mCallback;

    private Button      bHome;
    private Button      bD30;
    private Button      bD31;

    private TextView    monthofyear;
    private TextView    dayofmonth;

    private int         mMthNdx;
    private int         mDayNdx;

    private Button      bNext;
    private Button      bBack;

    private String      mDataMth = "";
    private String      mDataDay = "";


    private Integer[] mthId = {
            R.id.bJan,R.id.bFeb,R.id.bMar,R.id.bApr,R.id.bMay,R.id.bJun,
            R.id.bJul,R.id.bAug,R.id.bSep,R.id.bOct,R.id.bNov,R.id.bDec};

    private String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    private Integer[] dayId = {
            R.id.bD1,R.id.bD2,R.id.bD3,R.id.bD4,R.id.bD5,R.id.bD6,R.id.bD7,R.id.bD8,R.id.bD9,R.id.bD10,
            R.id.bD11,R.id.bD12,R.id.bD13,R.id.bD14,R.id.bD15,R.id.bD16,R.id.bD17,R.id.bD18,R.id.bD19,
            R.id.bD20,R.id.bD21,R.id.bD22,R.id.bD23,R.id.bD24,R.id.bD25,R.id.bD26,R.id.bD27,R.id.bD28,
            R.id.bD29,R.id.bD30, R.id.bD31};

    private ArrayList<Integer>   monthIds = new ArrayList<Integer>(Arrays.asList(mthId));
    private ArrayList<String>    monthArr = new ArrayList<String>(Arrays.asList(months));

    private ArrayList<Integer>   dayIds   = new ArrayList<Integer>(Arrays.asList(dayId));

    private LocalBroadcastManager   bManager;
    private dateViewReceiver        bReceiver;

    static final String TAG = "UserDateView";


    public UserDateView(Context context) {
        super(context);
        init(context, null);
    }

    public UserDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UserDateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        mContext = context;

        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(mContext);

        IntentFilter filter = new IntentFilter(TCONST.SET_MONTH);
        filter.addAction(TCONST.SET_DAY);

        bReceiver = new dateViewReceiver();
        bManager.registerReceiver(bReceiver, filter);
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        // Wire up the Month keypad
        //
        for(int i1 = 0 ; i1 < monthIds.size() ; i1++) {

            try {
                int id = (int) monthIds.get(i1);
                Button kbdButton = (Button) findViewById(id);

                kbdButton.setOnClickListener(clickListener);
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        // Wire up the Day of the Month keypad
        //
        for(int i1 = 0 ; i1 < dayIds.size() ; i1++) {

            try {
                int id = (int) dayIds.get(i1);
                Button kbdButton = (Button) findViewById(id);

                kbdButton.setOnClickListener(clickListener);
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        monthofyear = (TextView) findViewById(R.id.monthofyear);
        dayofmonth  = (TextView) findViewById(R.id.dayofmonth);

        bHome       = (Button) findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);

        bNext       = (Button) findViewById(R.id.bNext);
        bNext.setOnClickListener(clickListener);

        bBack       = (Button) findViewById(R.id.bBack);
        bBack.setOnClickListener(clickListener);

        bD30       = (Button) findViewById(R.id.bD30);
        bD31       = (Button) findViewById(R.id.bD31);

        initState(-1,-1);
    }


    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;

        bHome = (Button)findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);
    }


    public void setNumDays(int numDays) {

        switch(numDays) {
            case 29:
                bD30.setVisibility(INVISIBLE);
                bD31.setVisibility(INVISIBLE);
                break;

            case 30:
                bD30.setVisibility(VISIBLE);
                bD31.setVisibility(INVISIBLE);
                break;

            case 31:
                bD30.setVisibility(VISIBLE);
                bD31.setVisibility(VISIBLE);
                break;
        }
    }


    public void initState(int mth, int day) {

        updateMonthByIndex(mth);
        updateDayByIndex(day);
    }


    private void updateMonthByIndex(int ndx ) {

        if (ndx >= 0) {
            mMthNdx = ndx;
            mDataMth = monthArr.get(ndx);
            monthofyear.setText(mDataMth);
        }
        else {
            mMthNdx = -1;
            mDataMth = "";
            monthofyear.setText(mDataMth);
        }
        broadcast(MONTH_CHANGE, mMthNdx);
    }


    private void updateMonthRscId(int rid ) {

        int ndx = monthIds.indexOf(rid);

        updateMonthByIndex(ndx );
    }


    private void updateDayByIndex(int ndx ) {

        if (ndx >= 0) {
            mDayNdx = ndx;
            mDataDay = Integer.toString(ndx);
            dayofmonth.setText(mDataDay);
        }
        else {
            mDayNdx = -1;
            mDataDay = "";
            dayofmonth.setText(mDataDay);
        }
        broadcast(DAY_CHANGE, mDayNdx);
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vid = v.getId();

            switch(vid) {
                case  R.id.bHome:
                    mCallback.nextStep(HOME);
                    break;

                case  R.id.bNext:
                    if(!mDataMth.isEmpty() && !mDataDay.isEmpty())
                                        mCallback.nextStep(USER_CONF);
                    break;
                case  R.id.bBack:
                    mCallback.nextStep(USER_NEW);
                    break;

                case R.id.bJan:
                case R.id.bMar:
                case R.id.bMay:
                case R.id.bJul:
                case R.id.bAug:
                case R.id.bOct:
                case R.id.bDec:
                    updateMonthRscId(vid);
                    updateDayByIndex(-1);
                    setNumDays(31);
                    break;

                case R.id.bSep:
                case R.id.bApr:
                case R.id.bJun:
                case R.id.bNov:
                    updateMonthRscId(vid);
                    updateDayByIndex(-1);
                    setNumDays(30);
                    break;

                case R.id.bFeb:
                    updateMonthRscId(vid);
                    updateDayByIndex(-1);
                    setNumDays(29);
                    break;

                default:
                    int ndx = dayIds.indexOf(vid);

                    updateDayByIndex(ndx+1);
                    break;
            }
        }
    };


    public void broadcast(String Action, int Msg) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.INT_FIELD, Msg);

        bManager.sendBroadcast(msg);
    }


    class dateViewReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent) {

            Log.d("nameView", "Broadcast recieved: ");

            switch(intent.getAction()) {

                case TCONST.SET_MONTH:
                    int newMonth = intent.getIntExtra(TCONST.INT_FIELD, -1);
                    updateMonthByIndex(newMonth);
                    break;

                case TCONST.SET_DAY:
                    int newDay = intent.getIntExtra(TCONST.INT_FIELD, -1);
                    updateDayByIndex(newDay);
                    break;
            }
        }
    }
}

