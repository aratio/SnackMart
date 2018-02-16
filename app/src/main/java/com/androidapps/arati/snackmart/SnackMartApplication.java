package com.androidapps.arati.snackmart;

/**
 * Created by aogale on 2/15/18.
 */

import android.app.Application;

public class SnackMartApplication extends Application {

    public static SnackMartApplication theApp = null;
    public SnackList snackList = null;

    public SnackMartApplication()
    {
        theApp = this;

        //all activities should use this object
        snackList = new SnackList();
    }

    public void onCreate()
    {
        super.onCreate();

        //read the snackList from assets or database
        snackList.loadSnackList();

        /* Testing */
        //snackList.addSnackItem("example", "v");
        /* Testing */
    }


}
