package org.edforge.efhomescreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.edforge.efhomescreen.TCONST.HOME;
import static org.edforge.efhomescreen.TCONST.START_TUTOR;

/**
 * Created by kevin on 11/7/2018.
 */

public class SoundCheckView extends FrameLayout {
    private Context mContext;
    private IEdForgeLauncher  mCallback;

    private MediaPlayer             mPlayer;
    private Boolean                 mIsPlaying = false;
    private Button                  bHome;

    private LocalBroadcastManager   bManager;
    private SndChkViewReceiver      bReceiver;

    private int                     mLatched = 0;

    private Integer[] ids = {
            R.id.Sshape0,R.id.Sshape1,R.id.Sshape2,R.id.Sshape3,R.id.Sshape4,R.id.Sshape5};

    private ArrayList<Integer> shpIds  = new ArrayList<Integer>(Arrays.asList(ids));


    static final String TAG = "StartView";


    public SoundCheckView(Context context) {
        super(context);
        init(context, null);
    }

    public SoundCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SoundCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        mContext = context;


        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(mContext);

        IntentFilter filter = new IntentFilter(TCONST.NAME_CHANGE);

        bReceiver = new SndChkViewReceiver();
        bManager.registerReceiver(bReceiver, filter);
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

        // Wire up the keypad
        //
        for(int i1 = 0 ; i1 < shpIds.size() ; i1++) {

            try {
                int id = (int) shpIds.get(i1);
                Button shpButton = (Button) findViewById(id);

                shpButton.setOnClickListener(clickListener);
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        bHome       = (Button) findViewById(R.id.bHome);
        bHome.setOnClickListener(clickListener);
    }

    private void resetAllShapes() {

        // Wire up the keypad
        //
        for(int i1 = 0 ; i1 < shpIds.size() ; i1++) {

            try {
                int id = (int) shpIds.get(i1);
                Button shpButton = (Button) findViewById(id);

                shpButton.setSelected(false);
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }

    }

    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;
    }

    // TODO: normalize the implementation of HOME i.e. via callback or broadcast
    //
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        int vid = v.getId();

        switch(vid) {
            case  R.id.bHome:
                setPlayState(false);
                mCallback.nextStep(HOME);
                break;

            case R.id.Sshape0:

                resetAllShapes();
                mLatched = 1;

                v.setSelected(true);
                break;

            case R.id.Sshape3:
                if(mLatched == 1) {
                    setPlayState(false);
                    broadcast(START_TUTOR);
                    break;
                }

            case R.id.Sshape1:
            case R.id.Sshape2:
            case R.id.Sshape4:
            case R.id.Sshape5:
                mLatched++;

                v.setSelected(!v.isSelected());
                break;

            default:
                //Set the button's appearance
                //
                v.setSelected(!v.isSelected());
                break;
        }
        }
    };



    protected @Override void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if(!mIsPlaying) {
            setPlayState(true);
        }
    }



    public void setPlayState(boolean startStop) {

        if(startStop) {

            try {
                AssetFileDescriptor afd = mContext.getAssets().openFd("SoundCheck.mp3");
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(afd.getFileDescriptor());
                mPlayer.prepare();
                mPlayer.setLooping(true);
                mPlayer.start();

                mIsPlaying = true;
            }
            catch(IOException e) {
                // TODO: Give user written instructions
            }
        }
        else {
            if(mIsPlaying) {

                mPlayer.stop();

                mPlayer    = null;
                mIsPlaying = false;
            }
        }
    }

    public void broadcast(String Action) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);

        bManager.sendBroadcast(msg);
    }
    public void broadcast(String Action, String Msg) {

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.NAME_FIELD, Msg);

        bManager.sendBroadcast(msg);
    }


    class SndChkViewReceiver extends BroadcastReceiver {

        public void onReceive (Context context, Intent intent) {

            Log.d("earBudView", "Broadcast recieved: ");

            switch(intent.getAction()) {

            }
        }
    }

}
