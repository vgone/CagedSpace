package caged.coaa.com.cagedspace;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
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
import java.util.TimerTask;

import caged.coaa.com.cagedspace.Interface.GridCallBack;
import caged.coaa.com.cagedspace.Interface.MovementListener;
import caged.coaa.com.cagedspace.Utils.Grid;
import caged.coaa.com.cagedspace.Utils.MediaPlayerData;
import caged.coaa.com.cagedspace.Utils.UserPositionDetector;

//Comment added
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,MovementListener {
    private Map<Integer, MediaPlayerData> mediaPlayers;
    private List<String> streams;
    TextView tvStreamNo;
    private BeaconManager beaconManager;
    private Region region;
    private Map<String, Integer> PLACES_BY_BEACONS;
    float volume1,volume2;
    Timer timer1;
    //int tempRegion = 0;
    //int count = 0;
    int currentStreamId = 0;
    MediaPlayer fadeOutMp = null;
    private Map<Integer, Integer> beaconCounter;
    int movementCount=0,previousCount=0;
    UserPositionDetector userPositionDetector;

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
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        userPositionDetector = new UserPositionDetector(sensorManager,this);
        userPositionDetector.startDetector();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mediaPlayers = new HashMap<>();
        //new GridTask(MainActivity.this, gridCallBack).execute("http://10.38.24.96:8081/CagedSpaceWS/rest/grids");

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


        @Override
        protected void onDestroy() {
            userPositionDetector.stopDetector();
            /*try {
                beaconManager.stopRanging(region);
            } catch (RemoteException e) {
                e.printStackTrace();
            }*/
            super.onDestroy();
        }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map) {
            intent = new Intent(MainActivity.this,MapActivity.class);
        } else if(id == R.id.action_orchestra){
            intent = new Intent(MainActivity.this,OrchestraActvity.class);
        } else if (id== R.id.action_about){
            intent = new Intent(MainActivity.this,AboutActivity.class);
        }
        if(intent!=null) startActivity(intent);
        return true;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        volume1 = 0;
        mp.setVolume(volume1,volume1);
        mp.start();
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (volume1 < 1.1) {
                    mp.setVolume(volume1, volume1);
                    if (fadeOutMp != null)
                        fadeOutMp.setVolume((float) 1 - volume1, (float) 1 - volume1);
                    Log.d("demo", "Volume1 is " + volume1);
                    volume1 += 0.1;
                } else {
                    if (fadeOutMp != null)
                        fadeOutMp.reset();
                    timer1.cancel();
                    timer1.purge();
                }
            }
        }, 0, 400);
    }

   /* private void fadeOut(final MediaPlayer mp) {
        volume2 = (float) 1.0;
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                if (volume2 > 0) {
                    mp.setVolume(volume2, volume2);
                    Log.d("demo", "Volume2 is " + volume2);
                    volume2 -= 0.1;
                } else {
                    timer2.cancel();
                    timer2.purge();
                    mp.reset();
                }
            }
        }, 0, 1200);
    }
*/
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
                                if (count == 3) {
                                    if(movementCount>previousCount+7) {

                                        previousCount = movementCount;
                                        Log.d("demo", "playing the stream " + regionNo);
                                        currentStreamId = regionNo.intValue();
                                        tvStreamNo.setText("" + (currentStreamId));
                                        MediaPlayerData mediaPlayerData = mediaPlayers.get(currentStreamId);
                                        MediaPlayer newPlayer = mediaPlayerData.getMediaPlayer();
                                        for (int key : mediaPlayers.keySet()) {
                                            MediaPlayerData mpd = mediaPlayers.get(key);
                                            MediaPlayer mp = mpd.getMediaPlayer();
                                            if (mp.isPlaying()) {
                                                if (mp != newPlayer) {
                                                    //fadeOut(mp);
                                                    fadeOutMp = mp;
                                                }
                                            } else if (mp == newPlayer) {
                                                try {
                                                    newPlayer.setDataSource(mediaPlayerData.getUrl());
                                                    newPlayer.prepareAsync();
                                                } catch (IOException | IllegalStateException e) {
                                                    Log.d("TAG", "Error occurred");
                                                }
                                            } else
                                                Log.d("demo", "playing same track");
                                        }
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

    @Override
    public void onMovement() {
        this.movementCount++;
        Log.d("movement","current count ="+movementCount+" previous Count ="+previousCount);
    }
}
