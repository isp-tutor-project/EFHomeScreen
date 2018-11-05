//*********************************************************************************
//
//    Copyright(c) 2016-2017  Kevin Willows All Rights Reserved
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

package org.edforge.util;

// global tutor constants

public class TCONST {

    public static final String EDFORGE_FOLDER         = "/EdForge/";
    public static final String EDFORGE_UPDATE_FOLDER  = "/EdForge_UPDATE/";
    public static final String EDFORGE_LOG_FOLDER     = "/EdForge_LOG/";
    public static final String EDFORGE_DATA_FOLDER    = "/EdForge_DATA/";
    public static final String EDFORGE_DATA_TRANSFER  = "/EdForge_XFER/";


    // data sources
    public static final String ASSETS          = "ASSETS";
    public static final String RESOURCES       = "RESOURCE";
    public static final String EXTERN          = "EXTERN";
    public static final String DEFINED         = "DEFINED";

    // WIFI constants
    public static final String WEP         = "WEP";
    public static final String WPA         = "WPA";
    public static final String OPEN        = "OPEN";

    public static final String START_PROGRESSIVE_UPDATE   = "START_PROGRESSIVE_UPDATE";
    public static final String START_INDETERMINATE_UPDATE = "START_INDETERMINATE_UPDATE";
    public static final String UPDATE_PROGRESS            = "UPDATE_PROGRESS";
    public static final String PROGRESS_TITLE             = "PROGRESS_TITLE";
    public static final String PROGRESS_MSG1              = "PROGRESS_MSG1";
    public static final String PROGRESS_MSG2              = "PROGRESS_MSG2";
    public static final String ASSET_UPDATE_MSG           = "Installing Assets: ";

    public static final String INT_FIELD                  = "INT_FIELD";
    public static final String TEXT_FIELD                 = ".text";

    public static final String PLEASE_WAIT                = " - Please Wait.";


    // Server States
    public static final int START_STATE         = 0;
    public static final int COMMAND_WAIT        = 1;
    public static final int COMMAND_PACKET      = 2;
    public static final int PROCESS_COMMAND     = 3;

    public static final int COMMAND_SENDSTART   = 4;
    public static final int COMMAND_SENDDATA    = 5;
    public static final int COMMAND_SENDACK     = 6;

    public static final int COMMAND_RECVSTART   = 7;
    public static final int COMMAND_RECVDATA    = 8;
    public static final int COMMAND_RECVACK     = 9;


    public static final String PULL         = "PULL";
    public static final String PUSH         = "PUSH";
    public static final String INSTALL      = "INSTALL";

    public static final String EDFORGEZIPTEMP = "efztempfile.zip";

}
