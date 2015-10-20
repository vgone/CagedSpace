package caged.coaa.com.cagedspace;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Comment added
public class MainActivity extends AppCompatActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,MediaController.MediaPlayerControl{
    private MediaPlayer mediaPlayer1,mediaPlayer;
    private MediaController mediaController;
    Button btn1,btn2,btnStream;
    private Handler handler;
    private BeaconManager beaconManager;
    private Region region;
    private Map<String, Integer> PLACES_BY_BEACONS;
    int tempRegion = 0;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //mediaPlayer = new MediaPlayer();
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(MainActivity.this);
        mediaPlayer.setOnErrorListener(MainActivity.this);
        btnStream = (Button)findViewById(R.id.btnStart);
        btnStream.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                if(mediaPlayer!=null)
//                {
//                    mediaPlayer.release();
//                    mediaPlayer =null;
//                }



//                mediaPlayer1.setOnPreparedListener(MainActivity.this);
//                mediaPlayer1.setOnErrorListener(MainActivity.this);





                mediaController = new MediaController(MainActivity.this);
                try {

                    mediaPlayer.setDataSource("http://streaming.radionomy.com/SkylyneRadioRock1");
                    mediaPlayer.prepareAsync();

                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });






        btn1= (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mediaPlayer!=null) {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource("http://indiespectrum.com:9000");
                        // mediaPlayer.setOnPreparedListener(MainActivity.this);
                        mediaPlayer.prepareAsync();
                    }
                    // mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btn2= (Button)findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(mediaPlayer!=null) {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource("http://usa8-vn.mixstream.net:8138/");
                        //  mediaPlayer.setOnPreparedListener(MainActivity.this);
                        mediaPlayer.prepareAsync();
                    }
                    // mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //beacons logic
        Map<String, Integer> placesByBeacons = new HashMap<>();
        placesByBeacons.put("36677:41637", 1);
        placesByBeacons.put("48320:58596", 2);
        placesByBeacons.put("15212:31506", 3);
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);

        beaconManager = new BeaconManager(this);
        // beaconManager.setForegroundScanPeriod(1000,5000);
        Log.d("demo","onBeacons started");

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {

            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                Log.d("demo","onBeacons Discovered");
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                    Integer regionNo = placesNearBeacon(nearestBeacon);
                    if (tempRegion == regionNo)
                        count++;
                    else
                        tempRegion = regionNo;

                    if (count == 10) {

                        Log.d("debug", "region ******* is " + regionNo);
                        try {
                            switch (regionNo.intValue()) {
                                case 1:
                                    if(mediaPlayer!=null) {
                                        mediaPlayer.reset();
                                        mediaPlayer.setDataSource("http://usa8-vn.mixstream.net:8138/");
                                        //  mediaPlayer.setOnPreparedListener(MainActivity.this);
                                        mediaPlayer.prepareAsync();
                                    }
                                    break;
                                case 2:
                                    if(mediaPlayer!=null) {
                                        mediaPlayer.reset();
                                        mediaPlayer.setDataSource("http://indiespectrum.com:9000");
                                        //  mediaPlayer.setOnPreparedListener(MainActivity.this);
                                        mediaPlayer.prepareAsync();
                                    }
                                    break;
                                case 3:
                                    if(mediaPlayer!=null) {
                                        mediaPlayer.reset();
                                        mediaPlayer.setDataSource("http://streaming.radionomy.com/SkylyneRadioRock1");
                                        //  mediaPlayer.setOnPreparedListener(MainActivity.this);
                                        mediaPlayer.prepareAsync();
                                    }
                                    break;


                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        count =0;
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
    @Override
    protected void onResume() {

        super.onResume();

       beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onPause() {
        try {
            beaconManager.stopRanging(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onPause();
    }

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
    public void onPrepared(MediaPlayer mp) {
        Log.d("demo", "onPrepared");



        mp.start();

        Log.d("demo", "onPrepared1");
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.audiolayout));

//        handler = new Handler();
//        handler.post(new Runnable() {
//            public void run() {
//
//            }
//        });
        mediaController.setEnabled(true);
        mediaController.show();
        Log.d("demo", "onPrepared2");



    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mediaController!=null)
            mediaController.show();

        return true;
    }
    @Override
    public void finish() {
        if (mediaPlayer != null){
            mediaPlayer.stop();
        }
        super.finish();
    }

    @Override
    public void start() {
        if(mediaPlayer!=null)
            mediaPlayer.start();
    }

    @Override
    public void pause() {
        Log.d("demo","trying to Pause");
        if(mediaPlayer != null&&mediaPlayer.isPlaying())
            mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("Demo","what is "+what+" extra is "+extra);
        return true;
    }
    private Integer placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        Log.d("debug", "key of beacon is" + beaconKey);
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            Log.d("debug", "key of beacon is" + beaconKey);
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return 0;
    }
}
