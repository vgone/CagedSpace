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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//Comment added
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {
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
    private Map<Integer, Integer> beaconCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        beaconManager = new BeaconManager(this);
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


        for (int i = 0; i < 24; i++) {
            if(i % 2 == 0) {
                streams.add("rtsp://wnyc-3gp.streamguys.com/wnycam/wnycam.sdp");
            } else {
                streams.add("http://142.4.217.133:8488/stream?icy=http");
            }
        }


        for (int i = 0; i < 3; i++) {
            MediaPlayerData data = new MediaPlayerData();
            MediaPlayer mp = new MediaPlayer();
            mp.setOnPreparedListener(this);
            mp.setOnErrorListener(this);
            data.setMediaPlayer(mp);
            data.setUrl(streams.get(i));
            mediaPlayers.put(i, data);
        }
        tvStreamNo = (TextView) findViewById(R.id.tvStreamNumber);
        Log.d("demo", "text is " + tvStreamNo.getText());
        tvStreamNo.setText("#");
        //beacons logic
        Map<String, Integer> placesByBeacons = new HashMap<>();
        placesByBeacons.put("20256:28960", 0);
        placesByBeacons.put("35131:24072", 1);
        placesByBeacons.put("34567:20852", 2);
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);

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
                    if (!beaconCounter.isEmpty()) {
                        //Set<Integer> regionSets = beaconCounter.keySet();
                        for (int i : beaconCounter.keySet()) {
                            Log.d("demo", "Key is " + i + " value is " + beaconCounter.get(i));
                        }
                        if (beaconCounter.containsKey(regionNo)) {
                            int count = beaconCounter.get(regionNo);
                            if (count == 10) {
                                // int id = Integer.parseInt((String) v.getTag());
                                currentStreamId = regionNo.intValue();
                                tvStreamNo.setText("" + (currentStreamId));
                                MediaPlayerData mediaPlayerData = mediaPlayers.get(currentStreamId);
                                MediaPlayer newPlayer = mediaPlayerData.getMediaPlayer();
                                for(int key: mediaPlayers.keySet()){
                                    MediaPlayerData mpd = mediaPlayers.get(key);
                                    MediaPlayer mp = mpd.getMediaPlayer();
                                    if(mp.isPlaying()){
                                        if(mp!=newPlayer){
                                            fadeOut(mp);
                                        }
                                    } else if(mp == newPlayer){
                                        try {
                                            newPlayer.setDataSource(mediaPlayerData.getUrl());
                                            newPlayer.prepareAsync();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
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
        });
        region = new Region("Global region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null);

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
                volume1 = 0;
                mp.setVolume(volume1,volume1);
                mp.start();
                timer1 = new Timer();
                timer1.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (volume1 < 1.1) {
                            mp.setVolume(volume1, volume1);
                            Log.d("demo","Volume1 is "+volume1);
                            volume1 += 0.1;
                        } else {
                            timer1.cancel();
                            timer1.purge();
                        }
                    }
                }, 0, 1100);
    }

    private void fadeOut(final MediaPlayer mp) {
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
        }, 0, 1000);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("Demo", "what is " + what + " extra is " + extra);
        return true;
    }

    int counter = 0;
    private Integer placesNearBeacon(Beacon beacon) {
        counter++;
        counter = counter % 24;
        return counter;
    }

}
