package org.edforge.efhomescreen;

import org.edforge.util.CClassMap;
import org.edforge.util.ISerializableObject;
import org.edforge.util.IScope;
import org.edforge.util.JSON_Helper;
import org.edforge.util.JSON_Util;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kevin on 11/4/2018.
 */

public class UserDataPackage implements ISerializableObject {

    private ArrayList<UserData> userArray;

    // json loadable
    public UserData   currUser;
    public UserData[] users;

    final private String TAG = "UserDataPackage";

    public UserDataPackage() {

        currUser  = new UserData();
        userArray = new ArrayList<UserData>();
    }

    public void addUser(String userName) {

        UserData temp = new UserData(userName);

        userArray.add(temp);
    }

    @Override
    public void saveJSON(JSON_Util writer) {

        writer.addElement("currUser", currUser);

        UserData[] outputData = userArray.toArray(new UserData[userArray.size()]);

        writer.addObjectArray("users", outputData);
    }

    @Override
    public void loadJSON(JSONObject jsonObj, IScope scope) {

        JSON_Helper.parseSelf(jsonObj, this, CClassMap.classMap, scope);

        userArray = new ArrayList<UserData>(Arrays.asList(users));
    }
}

