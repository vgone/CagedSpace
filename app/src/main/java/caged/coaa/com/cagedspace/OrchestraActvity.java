package caged.coaa.com.cagedspace;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

import caged.coaa.com.cagedspace.Interface.OrchestraInfoCallBack;
import caged.coaa.com.cagedspace.Tasks.getOrchestraInfo;
import caged.coaa.com.cagedspace.Utils.Performer;
import caged.coaa.com.cagedspace.Utils.PerformersAdapter;

public class OrchestraActvity extends AppCompatActivity {
    ListView lv ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Orchestra");
        setContentView(R.layout.activity_orchestra);
        lv = (ListView) findViewById(R.id.listView);

             new getOrchestraInfo(OrchestraActvity.this,callBack).execute("http://52.26.164.148:8080/CagedSpaceWS/rest/grids/orchestra");



    }

    OrchestraInfoCallBack callBack = new OrchestraInfoCallBack() {
        @Override
        public void getPerformersList(ArrayList<Performer> performers) {
            //Log.d("Orgc",performers.toString());
            PerformersAdapter adapter = new PerformersAdapter(OrchestraActvity.this,R.layout.row_layout_orchestra,performers);
            lv.setAdapter(adapter);

        }
    };
}
