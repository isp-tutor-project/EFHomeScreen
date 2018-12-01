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
    private ArrayList<UserData> loginArray;

    public int        mUserNdx;

    // json loadable
    public UserData[] users;
    public UserData[] userLogins;

    final private String TAG = "UserDataPackage";

    public UserDataPackage() {

        userArray  = new ArrayList<UserData>();
        loginArray = new ArrayList<UserData>();
    }


    public UserData addUser(String userName) {

        UserData temp = new UserData(userName);

        return addUser(temp);
    }

    public UserData addUser(UserData userData) {

        userArray.add(userData);
        getUserIndexByName(userData.userName);

        return userData;
    }


    public void addLogin(UserData user) {

        user.timeStamp = System.currentTimeMillis();
        loginArray.add(user);
    }


    public UserData getUserByNdx(int Index) {

        mUserNdx = Index;

        return userArray.get(mUserNdx);
    }


    public UserData getUserByName(String userName) {

        UserData user;

        getUserIndexByName(userName);

        if(mUserNdx != -1)
            user = userArray.get(mUserNdx);
        else
            user = null;

        return user;
    }


    public int getUserIndexByName(String userName) {

        UserData user;
        mUserNdx = -1;

        for(int i1 = 0 ; i1 < userArray.size() ; i1++) {

            //#TEST
            // Log.d(TAG, "Launch User: " + userArray.get(i1).userName);

            if (userArray.get(i1).userName.equals(userName)) {
                mUserNdx = i1;
                break;
            }
        }

        return mUserNdx;
    }

    @Override
    public void saveJSON(JSON_Util writer) {

        UserData[] outputData;

        outputData = userArray.toArray(new UserData[userArray.size()]);
        writer.addObjectArray("users", outputData);

        outputData= loginArray.toArray(new UserData[loginArray.size()]);
        writer.addObjectArray("userLogins", outputData);
    }

    @Override
    public void loadJSON(JSONObject jsonObj, IScope scope) {

        JSON_Helper.parseSelf(jsonObj, this, CClassMap.classMap, scope);

        userArray  = new ArrayList<UserData>(Arrays.asList(users));
        loginArray = new ArrayList<UserData>(Arrays.asList(userLogins));
    }
}

