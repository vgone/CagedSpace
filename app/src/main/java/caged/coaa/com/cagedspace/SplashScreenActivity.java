package caged.coaa.com.cagedspace;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    AlertDialog alert;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ((ImageView)findViewById(R.id.imageView)).setImageResource(R.mipmap.temp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Bluetooth();
                    } else {
                        startMain();
                    }
                } else

                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
                    builder.setMessage("No Network Connection")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(SplashScreenActivity.this);
                                    builder1.setMessage("Checking For Internet...").setCancelable(false);
                                    final AlertDialog dialog1 = builder1.create();
                                    dialog1.show();
                                    if (!isNetworkAvailable()) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog1.dismiss();
                                                showAlert(builder);
                                            }
                                        }, 1000);
                                    } else {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog1.dismiss();
                                                Bluetooth();
                                            }
                                        }, 1000);
                                    }
                                }
                            }).setCancelable(false);
                    showAlert(builder);
                }
            }
        }, 1500);
    }

    private void Bluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
        builder.setMessage("Bluetooth Required")
                .setPositiveButton("Switch It On", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBluetoothAdapter.enable();
                        startMain();
                    }
                }).setCancelable(false);
        builder.create().show();
    }

    private void startMain() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
        builder.setCancelable(false)
                .setMessage("For best experience keep the handset in your pocket")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).create().show();
    }

    private void showAlert(AlertDialog.Builder builder) {
        alert = builder.create();
        alert.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
