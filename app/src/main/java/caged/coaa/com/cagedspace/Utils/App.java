package caged.coaa.com.cagedspace.Utils;

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
       Parse.initialize(this, "FkOrKacTGHOegnsonl7DeF6oBzRJzZWdjw0xafmv", "e6pzZ3UQFskVDyFkEw07sSCmd1Jgvdc427PDIpF6");
        //Parse.enableLocalDatastore(this);
    }

}
