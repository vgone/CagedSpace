package caged.coaa.com.cagedspace;

import android.app.Application;

import com.estimote.sdk.BeaconManager;
import com.parse.Parse;


/**
 * Created by vinodkumar on 10/15/2015.
 */
public class App extends Application {
    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
       Parse.initialize(this, "T0zNIvsroRy6LRyCO3mT90txR5ADqz6nprz2rcs1", "EuNsGGr1rFccqfUeKOH7yX5lIsudzuSreL9PVmAb");
        Parse.enableLocalDatastore(this);
    }

}
