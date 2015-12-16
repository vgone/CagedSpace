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
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import caged.coaa.com.cagedspace.Interface.GridCallBack;
import caged.coaa.com.cagedspace.Interface.MovementListener;
import caged.coaa.com.cagedspace.Tasks.GridTask;
import caged.coaa.com.cagedspace.Utils.Grid;
import caged.coaa.com.cagedspace.Utils.MediaPlayerData;
import caged.coaa.com.cagedspace.Utils.UserPositionDetector;

//Comment added
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MovementListener {
    private Map<Integer, MediaPlayerData> mediaPlayers;
    private List<String> streams;
    private BeaconManager beaconManager;
    private Region region;
    private Map<String, Integer> PLACES_BY_BEACONS;
    private Map<Integer, String> GRID_WITH_IMAGES;
    Timer timer1;
    String userId;
    ParseQuery<ParseObject> query;
    //int tempRegion = 0;
    //int count = 0;
    int currentStreamId = 0;
    MediaPlayer fadeOutMp = null;
    private Map<Integer, Integer> beaconCounter;
    int movementCount = 0, previousCount = 0;
    UserPositionDetector userPositionDetector;
    private ImageView ivGridImage;
    TextView gridId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ivGridImage = (ImageView) findViewById(R.id.ivGrid);
        gridId = (TextView) findViewById(R.id.tvGridId);
        //Parse Logic
        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, com.parse.ParseException e) {
                    if (e != null) {

                        Log.d("MyApp", "Anonymous login failed.");
                    } else {
                        userId = parseUser.getObjectId();
                        Log.d("MyApp", "Anonymous user logged in.");
                    }
                }
            });
        }


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
        userPositionDetector = new UserPositionDetector(sensorManager, this);
        userPositionDetector.startDetector();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mediaPlayers = new HashMap<>();
        new GridTask(MainActivity.this, gridCallBack).execute("http://52.26.164.148:8080/CagedSpaceWS/rest/grids");


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
            intent = new Intent(MainActivity.this, MapActivity.class);
        } else if (id == R.id.action_orchestra) {
            intent = new Intent(MainActivity.this, OrchestraActvity.class);
        } else if (id == R.id.action_about) {
            intent = new Intent(MainActivity.this, AboutActivity.class);
        }
        else if(id==R.id.action_exit){
            System.exit(0);
        }
        if (intent != null) startActivity(intent);
        return true;
    }

    @Override
    public void onPrepared(final MediaPlayer mp) {
        float offset = 0f;
        mp.setVolume(offset, offset);
        mp.start();

        while (offset < 1.1) {
            mp.setVolume(offset, offset);
            if (currentMPlayerPlaying != null)
                currentMPlayerPlaying.setVolume((float) 1 - offset, (float) 1 - offset);
            Log.d("demo", "Volume1 is " + offset);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            offset += 0.1;
        }
        if (currentMPlayerPlaying != null)
            currentMPlayerPlaying.reset();
        currentMPlayerPlaying = mp;
        jumpInProgress = false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("Demo", "what is " + what + " extra is " + extra);
        return true;
    }

    private Integer getRegionNumber(Beacon beacon) {
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
            Map<Integer, String> gridImageURL = new HashMap<>();
            for (Grid tempGrid : alGrids) {
                MediaPlayerData data = new MediaPlayerData();
                MediaPlayer mp = new MediaPlayer();
                mp.setOnPreparedListener(MainActivity.this);
                mp.setOnErrorListener(MainActivity.this);
                data.setMediaPlayer(mp);
                data.setUrl(tempGrid.getStreamURL());
                mediaPlayers.put((int) tempGrid.getId(), data);

                placesByBeacons.put(tempGrid.getBeaconId(), tempGrid.getId());
                gridImageURL.put(tempGrid.getId(), tempGrid.getGridImageURL());

            }
            for (int i : mediaPlayers.keySet()) {
                Log.d("demo", "Key of mediaplayer " + i + " value is " + mediaPlayers.get(i));
            }

            PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
            GRID_WITH_IMAGES = Collections.unmodifiableMap(gridImageURL);
            kickStartRanging();
        }
    };

    private boolean jumpInProgress = false;
    private MediaPlayer currentMPlayerPlaying = null;

    public void kickStartRanging() {
        query = ParseQuery.getQuery("User");
        beaconCounter = new HashMap<>();

        // beaconManager.setForegroundScanPeriod(1000,5000);
        Log.d("demo", "onBeacons started");

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            //
            @Override
            public void onBeaconsDiscovered(final Region region, List<Beacon> list) {
                // Log.d("demo", "onBeacons Discovered");
                if (!list.isEmpty() && !jumpInProgress) {
                    Beacon nearestBeacon = list.get(0);
                    final Integer regionNo = getRegionNumber(nearestBeacon);
                    gridId.setText(regionNo + " mcount " + movementCount + " pcount " + previousCount);
                    if (regionNo != 0) {
                        if (!beaconCounter.isEmpty()) {
                            for (int i : beaconCounter.keySet()) {
                                Log.d("MainActivity", "Key is " + i + " value is " + beaconCounter.get(i));
                            }
                            if (beaconCounter.containsKey(regionNo)) {
                                int count = beaconCounter.get(regionNo);
                                gridId.setText(gridId.getText() + " count " + count);
                                if ((count >= 3 && (movementCount > previousCount || movementCount == 0)) || count > 40 || movementCount == 0) {

                                    previousCount = movementCount;
                                    Log.d("MainActivity", "playing the stream " + regionNo);
                                    currentStreamId = regionNo.intValue();
                                    Picasso.with(MainActivity.this)
                                            .load(GRID_WITH_IMAGES.get(regionNo))
                                            .into(ivGridImage);

                                    MediaPlayerData mediaPlayerData = mediaPlayers.get(currentStreamId);
                                    MediaPlayer newPlayer = mediaPlayerData.getMediaPlayer();
                                    if (!newPlayer.isPlaying()) {
                                        try {
                                            newPlayer.setDataSource(mediaPlayerData.getUrl());
                                            newPlayer.prepareAsync();
                                            query.getInBackground(userId, new GetCallback<ParseObject>() {
                                        public void done(ParseObject userObject, com.parse.ParseException e) {
                                            if (e == null) {
                                                userObject.put("currentGrid", regionNo.intValue());
                                                userObject.saveInBackground();
                                            }
                                        }
                                    });
                                            //Parse Logic End
                                            jumpInProgress = true;
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
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
        Log.d("movement", "current count =" + movementCount + " previous Count =" + previousCount);
    }
}
