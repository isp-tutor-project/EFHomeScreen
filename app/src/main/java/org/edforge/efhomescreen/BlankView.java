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

import static org.edforge.efhomescreen.TCONST.HOME;
import static org.edforge.efhomescreen.TCONST.SOUND_CHECK;

/**
 * Created by kevin on 11/13/2018.
 */

public class BlankView extends FrameLayout {

    private Context mContext;
    private IEdForgeLauncher  mCallback;

    static final String TAG = "BlankView";


    public BlankView(Context context) {
        super(context);
        init(context, null);
    }

    public BlankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BlankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public void init(Context context, AttributeSet attrs) {
        mContext = context;

    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();

    }

}
