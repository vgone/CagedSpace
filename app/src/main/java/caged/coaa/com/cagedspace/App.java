package caged.coaa.com.cagedspace;

import android.app.Application;

import com.estimote.sdk.BeaconManager;


/**
 * Created by vinodkumar on 10/15/2015.
 */
public class App extends Application {
    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
//        Parse.initialize(this, "mBn5ZTL38GuillbOaSN8EXlTnBlqopMWEQYESnI9", "RQ7N7QCOLiKIrio82wknTMmIZjnKx7HXiTEjSd1e");
    }

}
