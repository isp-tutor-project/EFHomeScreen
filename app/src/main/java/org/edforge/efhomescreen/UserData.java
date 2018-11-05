package org.edforge.efhomescreen;

import org.edforge.util.CClassMap;
import org.edforge.util.ISerializableObject;
import org.edforge.util.IScope;
import org.edforge.util.JSON_Helper;
import org.edforge.util.JSON_Util;
import org.json.JSONObject;

/**
 * Created by kevin on 11/4/2018.
 */

public class UserData implements ISerializableObject {


    final private String TAG = "UserData";

    // json loadable
    public String userName;
    public String currTutor;
    public String currScene;


    public UserData() {

        userName  = "";
        currTutor = "";
        currScene = "";
    }

    public UserData(String _userName) {

        userName  = _userName;
        currTutor = "";
        currScene = "";
    }

    @Override
    public void saveJSON(JSON_Util writer) {

        writer.addElement("userName", userName);
        writer.addElement("currTutor", currTutor);
        writer.addElement("currScene", currScene);
    }

    @Override
    public void loadJSON(JSONObject jsonObj, IScope scope) {

        JSON_Helper.parseSelf(jsonObj, this, CClassMap.classMap, scope);
    }

}
