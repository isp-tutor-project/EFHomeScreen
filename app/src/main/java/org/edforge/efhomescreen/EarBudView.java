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

import static org.edforge.efhomescreen.TCONST.HOME;
import static org.edforge.efhomescreen.TCONST.SOUND_CHECK;
import static org.edforge.efhomescreen.TCONST.START_TUTOR;

/**
 * Created by kevin on 11/7/2018.
 */

public class EarBudView extends FrameLayout {

    private Context mContext;
    private IEdForgeLauncher  mCallback;

    private Button      bHome;

    private LocalBroadcastManager   bManager;
    private earBudViewReceiver      bReceiver;

    static final String TAG = "StartView";


    public EarBudView(Context context) {
        super(context);
        init(context, null);
    }

    public EarBudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EarBudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        mContext = context;


        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(mContext);

        IntentFilter filter = new IntentFilter(TCONST.NAME_CHANGE);

        bReceiver = new EarBudView.earBudViewReceiver();
        bManager.registerReceiver(bReceiver, filter);
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        bHome       = (Button) findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);

        setOnClickListener(clickListener);
    }

    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vid = v.getId();

            switch(vid) {
                case  R.id.bHome:
                    mCallback.nextStep(HOME);
                    break;

                default:
                    mCallback.nextStep(SOUND_CHECK);
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


    class earBudViewReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent) {

            Log.d("earBudView", "Broadcast recieved: ");

            switch(intent.getAction()) {

            }
        }
    }

}
