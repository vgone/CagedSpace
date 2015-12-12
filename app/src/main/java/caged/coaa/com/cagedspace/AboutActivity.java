package caged.coaa.com.cagedspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About");
        ((TextView)findViewById(R.id.textView10)).setText("Version 1.0\n\nDescription :\n");
    }
}
