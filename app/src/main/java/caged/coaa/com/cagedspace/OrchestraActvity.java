package caged.coaa.com.cagedspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import caged.coaa.com.cagedspace.Tasks.getImage;
import caged.coaa.com.cagedspace.Tasks.getOrchestraInfo;
import caged.coaa.com.cagedspace.Utils.Performer;
import caged.coaa.com.cagedspace.Utils.PerformersAdapter;

public class OrchestraActvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Orchestra");
        setContentView(R.layout.activity_orchestra);
        ListView lv = (ListView) findViewById(R.id.listView);
        try {
            JSONArray array = new getOrchestraInfo(OrchestraActvity.this).execute("http://52.26.164.148:8080/CagedSpaceWS/rest/grids/orchestra").get();
            ArrayList<Performer> performers = new ArrayList<>();
            for(int i=0;i<array.length();i++){
                JSONObject object = array.getJSONObject(i);
                Performer performer = new Performer();
                performer.setName(object.getString("playerName"));
                performer.setCaption(object.getString("playerCaption"));
                performer.setImage(new getImage(OrchestraActvity.this).execute(object.getString("playerPhoto")).get());
                performers.add(performer);
            }
            PerformersAdapter adapter = new PerformersAdapter(OrchestraActvity.this,R.layout.row_layout_orchestra,performers);
            lv.setAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
