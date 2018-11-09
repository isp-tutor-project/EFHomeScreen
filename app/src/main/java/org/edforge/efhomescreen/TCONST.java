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

// global tutor constants

import android.content.Intent;

import java.util.HashMap;

public class TCONST {

    public static final String EDFORGE_FOLDER         = "/EdForge/";
    public static final String EDFORGE_UPDATE_FOLDER  = "/EdForge_UPDATE/";
    public static final String EDFORGE_LOG_FOLDER     = "/EdForge_LOG/";
    public static final String EDFORGE_DATA_FOLDER    = "/EdForge_DATA/";
    public static final String EDFORGE_DATA_TRANSFER  = "/EdForge_XFER/";

    public static final String USER_DATA            = "isp_userdata.json";

    public static final String FONT_FOLDER          = "fonts/";
    public static final String POINT_AND_TAP        = "POINTANDTAP";
    public static final String CANCEL_POINT         = "CANCEL_POINT";
    public static final String POINT_AT_BUTTON      = "POINT_AT_BUTTON";
    public static final String SCREENPOINT          = "SCREENPOINT";


    public static final String HOME                 = "HOME";
    public static final String USER_NEW             = "USER_NEW";
    public static final String USER_NAME            = "USERNAME";
    public static final String USER_DATE            = "USER_DATE";
    public static final String USER_CONF            = "USERCONF";
    public static final String EAR_BUD_CHECK        = "EARBUDCHECK";
    public static final String SOUND_CHECK          = "SOUNDCHECK";

    public static final String START_TUTOR          = "START_TUTOR";
    public static final String OWNER_BREAK_OUT      = "OWNER_BREAK_OUT";


    public static final String UNKNOWN_MODE          = "UNKNOWNMODE";
    public static final String CREATE_ACCT           = "CREATE_ACCT";
    public static final String EXISTING_ACCT         = "EXISTING_ACCT";

    public static final String MODE_CHANGE          = "MODE_CHANGE";
    public static final String NAME_CHANGE          = "NAME_CHANGE";
    public static final String MONTH_CHANGE         = "MONTH_CHANGE";
    public static final String DAY_CHANGE           = "DAY_CHANGE";
    public static final String USER_CHANGE          = "USER_CHANGE";

    public static final String SET_NAME             = "SET_NAME";
    public static final String SET_MONTH            = "SET_MONTH";
    public static final String SET_DAY              = "SET_DAY";

    public static final String NAME_FIELD            = "NAME_FIELD";
    public static final String INT_FIELD             = "INT_FIELD";
    public static final String USER_FIELD            = "USER_FIELD";

    public static final boolean APPEND     = true;
    public static final boolean REPLACE    = false;

    public static final boolean READ     = true;
    public static final boolean WRITE    = false;

    public static final String PLUG_CONNECT     = "android.intent.action.ACTION_POWER_CONNECTED";
    public static final String PLUG_DISCONNECT  = "android.intent.action.ACTION_POWER_DISCONNECTED";

    public static final String EFHOST_LAUNCH_INTENT   = "org.edforge.androidhost.EF_ANDROID_HOST";
    public static final String LAUNCH_HOME            = Intent.ACTION_MAIN;

    public static final String EFHOST_FINISHER_INTENT   = "org.edforge.androidhost.EFHOST_FINISHER_INTENT";
    public static final String EFHOME_FINISHER_INTENT   = "org.edforge.efhomescreen.EFHOME_FINISHER_INTENT";
    public static final String EFHOME_STARTER_INTENT    = "org.edforge.efhomescreen.EFHOME_STARTER_INTENT";

}
