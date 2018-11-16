//*********************************************************************************
//
//    Copyright(c) 2016 Carnegie Mellon University. All Rights Reserved.
//    Copyright(c) Kevin Willows All Rights Reserved
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
//*********************************************************************************

package org.edforge.efhomescreen;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.edforge.efhomescreen.TCONST.USER_EXISTING;
import static org.edforge.efhomescreen.TCONST.USER_NEW;


public class StartView extends FrameLayout {

    private Context             mContext;
    private IEdForgeLauncher    mCallback;

    private ImageButton         SexistingUserButton;
    private ImageButton         SnewUserButton;
    private boolean             tutorEnable = false;

    private final Handler   mainHandler  = new Handler(Looper.getMainLooper());
    private HashMap         queueMap     = new HashMap();
    private boolean         _qDisabled   = false;
    private int[]           _screenCoord = new int[2];
    private float[]         _lastTouch   = new float[2];

    private int              X = 0;
    private int              Y = 1;

    private long            _lastTime   = 0;
    private String          mState      = UPPERLEFT;
    private int             mStateCount = 0;

    public static final String UPPERLEFT   = "left";
    public static final String LOWERRIGHT  = "right";


    private LocalBroadcastManager bManager;

    static final String TAG = "StartView";


    public StartView(Context context) {
        super(context);
        init(context, null);
    }

    public StartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        mContext = context;

        // Capture the local broadcast manager
        bManager = LocalBroadcastManager.getInstance(getContext());
    }


    public void setCallback(IEdForgeLauncher _callback) {

        mCallback = _callback;

        SexistingUserButton = (ImageButton)findViewById(R.id.SexistingUser);
        SexistingUserButton.setOnClickListener(clickListener);

        SnewUserButton = (ImageButton)findViewById(R.id.SnewUserButton);
        SnewUserButton.setOnClickListener(clickListener);

        // Allow hits anywhere on screen
        //
//        setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                callback.onStartTutor();
//            }
//        });
//        SexistingUserButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                callback.onStartTutor();
//            }
//        });

        setOnTouchListener(touchListener);
        setOnClickListener(clickListener);

    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                _lastTouch[0] = event.getX();
                _lastTouch[1] = event.getY();
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    };


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int vid = v.getId();

            switch(vid) {
                case  R.id.SexistingUser:
                    mCallback.nextStep(USER_EXISTING);
                    break;

                case  R.id.SnewUserButton:
                    mCallback.nextStep(USER_NEW);
                    break;

                default:
                    long time = System.currentTimeMillis();

                    // retrieve the stored coordinates
                    float x = _lastTouch[0];
                    float y = _lastTouch[1];

                    if (_lastTime <= 0)
                        _lastTime = time;

                    else {
                        if (time - _lastTime < 300) {
                            addTouch();
                        } else {
                            Log.d(TAG, "Timeout touch:");
                            mStateCount = 0;
                            mState = UPPERLEFT;
                        }

                        _lastTime = System.currentTimeMillis();
                    }

                    // use the coordinates for whatever
                    Log.i("TAG", "Click At: x = " + x + ", y = " + y);
                    break;
            }
        }
    };


    private void addTouch() {

        switch(mState) {
            case UPPERLEFT:
                if((_lastTouch[X] < 150) && (_lastTouch[Y] < 150)) {

                    mStateCount++;
                    if(mStateCount > 1) {
                        mStateCount = 0;
                        mState      = LOWERRIGHT;
                    }
                }
                break;

            case LOWERRIGHT:
                if((_lastTouch[X] > 1750) && (_lastTouch[Y] > 1050)) {

                    mStateCount++;
                    if(mStateCount > 2) {
                        Log.d(TAG, "Start Breakout:");
                        mCallback.startBreakOut();
                    }
                }

                break;
        }

        Log.d(TAG, "Add touch: " + mStateCount);

    }


    private void broadcastLocation(String Action, View target) {

        target.getLocationOnScreen(_screenCoord);

        PointF centerPt = new PointF(_screenCoord[0] + (target.getWidth() / 2), _screenCoord[1] + (target.getHeight() / 2));
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.SCREENPOINT, new float[]{centerPt.x, (float) centerPt.y});

        bManager.sendBroadcast(msg);
    }


    protected void broadcastLocation(String Action, PointF touchPt) {

        getLocationOnScreen(_screenCoord);

        // Let the persona know where to look
        Intent msg = new Intent(Action);
        msg.putExtra(TCONST.SCREENPOINT, new float[]{touchPt.x + _screenCoord[0], (float) touchPt.y + _screenCoord[1]});

        bManager.sendBroadcast(msg);
    }


    protected void cancelPointAt() {

        Intent msg = new Intent(TCONST.CANCEL_POINT);
        bManager.sendBroadcast(msg);
    }


    public void execCommand(String command, Object target ) {

        long    delay  = 0;

        switch(command) {

        }
    }



    //************************************************************************
    //************************************************************************
    // Component Message Queue  -- Start


    public class Queue implements Runnable {

        protected String _command;
        protected Object _target;

        public Queue(String command) {
            _command = command;
        }

        public Queue(String command, Object target) {
            _command = command;
            _target  = target;
        }


        @Override
        public void run() {

            try {
                queueMap.remove(this);

                execCommand(_command, _target);

            }
            catch(Exception e) {
                Log.e(TAG, "Run Error:" +  e);
            }
        }
    }


    /**
     *  Disable the input queues permenantly in prep for destruction
     *  walks the queue chain to diaable scene queue
     *
     */
    private void terminateQueue() {

        // disable the input queue permenantly in prep for destruction
        //
        _qDisabled = true;
        flushQueue();
    }


    /**
     * Remove any pending scenegraph commands.
     *
     */
    public void flushQueue() {

        Iterator<?> tObjects = queueMap.entrySet().iterator();

        while(tObjects.hasNext() ) {
            Map.Entry entry = (Map.Entry) tObjects.next();

            mainHandler.removeCallbacks((Queue)(entry.getValue()));
        }
    }


    /**
     * Keep a mapping of pending messages so we can flush the queue if we want to terminate
     * the tutor before it finishes naturally.
     *
     * @param qCommand
     */
    private void enQueue(Queue qCommand) {
        enQueue(qCommand, 0);
    }
    private void enQueue(Queue qCommand, long delay) {

        if(!_qDisabled) {
            queueMap.put(qCommand, qCommand);

            if(delay > 0) {
                mainHandler.postDelayed(qCommand, delay);
            }
            else {
                mainHandler.post(qCommand);
            }
        }
    }

    /**
     * Post a command to the tutorgraph queue
     *
     * @param command
     */
    public void post(String command) {
        post(command, 0);
    }
    public void post(String command, long delay) {

        enQueue(new Queue(command), delay);
    }


    /**
     * Post a command and target to this scenegraph queue
     *
     * @param command
     */
    public void post(String command, Object target) {
        post(command, target, 0);
    }
    public void post(String command, Object target, long delay) {

        enQueue(new Queue(command, target), delay);
    }


    // Component Message Queue  -- End
    //************************************************************************
    //************************************************************************




}
