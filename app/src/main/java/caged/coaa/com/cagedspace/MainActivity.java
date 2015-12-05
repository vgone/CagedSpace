package caged.coaa.com.cagedspace;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import caged.coaa.com.cagedspace.Interface.GridCallBack;
import caged.coaa.com.cagedspace.Tasks.GridTask;

//Comment added
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
    private Map<Integer, MediaPlayerData> mediaPlayers;
    private List<String> streams;
    TextView tvStreamNo;
    private BeaconManager beaconManager;
    private Region region;
    private Map<String, Integer> PLACES_BY_BEACONS;
    float volume1, volume2;
    Timer timer1;
    //int tempRegion = 0;
    //int count = 0;
    int currentStreamId = 0;
    private Map<Integer, Integer> beaconCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        beaconManager = new BeaconManager(this);
        region = new Region("Global region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.d("demo", "on resume beacon");
                try {
                    beaconManager.startRanging(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mediaPlayers = new HashMap<>();
        new GridTask(MainActivity.this, gridCallBack).execute("http://192.168.137.1:8081/CagedSpaceWS/rest/grids");

        tvStreamNo = (TextView) findViewById(R.id.tvStreamNumber);
        Log.d("demo", "text is " + tvStreamNo.getText());
        tvStreamNo.setText("#");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
        @Override
        protected void onDestroy() {
            try {
                beaconManager.stopRanging(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            super.onDestroy();
        }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        for (int key : mediaPlayers.keySet()) {
            if (key == currentStreamId) {
                MediaPlayerData mediaPlayerData = mediaPlayers.get(key);
                mediaPlayerData.getMediaPlayer().start();
            } else {
                MediaPlayerData mediaPlayerData = mediaPlayers.get(key);
                mediaPlayerData.getMediaPlayer().reset();
            }
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("Demo", "what is " + what + " extra is " + extra);
        return true;
    }

    private Integer placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        // Log.d("demo", "key of beacon is" + beaconKey);
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            Log.d("demo", "key of beacon is" + beaconKey);
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return 0;
    }

    GridCallBack gridCallBack = new GridCallBack() {
        @Override
        public void getGridList(ArrayList<Grid> alGrids) {
            Log.d("debug", alGrids.toString());
            Map<String, Integer> placesByBeacons = new HashMap<>();
            for (Grid tempGrid : alGrids) {
                MediaPlayerData data = new MediaPlayerData();
                MediaPlayer mp = new MediaPlayer();
                mp.setOnPreparedListener(MainActivity.this);
                mp.setOnErrorListener(MainActivity.this);
                data.setMediaPlayer(mp);
                data.setUrl(tempGrid.getStreamURL());
                mediaPlayers.put((int) tempGrid.getId(), data);

                placesByBeacons.put(tempGrid.getBeaconId(), tempGrid.getId());

            }
            for (int i : mediaPlayers.keySet()) {
                Log.d("demo", "Key of mediaplayer " + i + " value is " + mediaPlayers.get(i));
            }
            PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
            kickStartRanging();
        }
    };

    public void kickStartRanging() {
        beaconCounter = new HashMap<>();

        // beaconManager.setForegroundScanPeriod(1000,5000);
        Log.d("demo", "onBeacons started");

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            //
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                // Log.d("demo", "onBeacons Discovered");
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    Integer regionNo = placesNearBeacon(nearestBeacon);
                    if (regionNo != 0) {


                        if (!beaconCounter.isEmpty()) {
                            //Set<Integer> regionSets = beaconCounter.keySet();
                            for (int i : beaconCounter.keySet()) {
                                Log.d("demo", "Key is " + i + " value is " + beaconCounter.get(i));
                            }
                            if (beaconCounter.containsKey(regionNo)) {
                                int count = beaconCounter.get(regionNo);
                                if (count == 10) {
                                   Log.d("demo","playing the stream "+regionNo);
                                    currentStreamId = regionNo.intValue();
                                    tvStreamNo.setText("" + (currentStreamId));
                                    MediaPlayerData mediaPlayerData = mediaPlayers.get(currentStreamId);
                                    try {
                                       // if (!mediaPlayerData.getMediaPlayer().isPlaying()) {
                                            mediaPlayerData.getMediaPlayer().setDataSource(mediaPlayerData.getUrl());
                                            mediaPlayerData.getMediaPlayer().prepareAsync();
                                        //}
                                    } catch (IllegalStateException | IOException e) {
                                        Log.d("TAG", "Soemthing stupid happened");
                                    }
                                    beaconCounter.clear();
                                } else {
                                    beaconCounter.put(regionNo, count + 1);
                                }
                            } else {
                                beaconCounter.put(regionNo, 1);//registering first count of beacon as 1
                            }

                        } else {
                            beaconCounter.put(regionNo, 1);
                        }
                    }


                }
            }
        });

    }
}
