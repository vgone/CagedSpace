package caged.coaa.com.cagedspace.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import caged.coaa.com.cagedspace.Interface.OrchestraInfoCallBack;
import caged.coaa.com.cagedspace.Utils.Performer;

/**
 * Created by SaideepReddy on 12/11/2015.
 */
public class getOrchestraInfo extends AsyncTask<String,Void,ArrayList<Performer>> {

    ProgressDialog pd;
    Context mContext;
    private OrchestraInfoCallBack callBack;

    public getOrchestraInfo(Context context,OrchestraInfoCallBack callBack) {
        this.mContext = context;
        this.callBack = callBack;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setTitle("Loading Information...");
        pd.show();
    }

    @Override
    protected void onPostExecute(ArrayList<Performer> performers) {
        super.onPostExecute(performers);
        pd.dismiss();
        callBack.getPerformersList(performers);
    }

    @Override
    protected ArrayList<Performer> doInBackground(String[] params) {
        HttpURLConnection connection;

        ArrayList<Performer> performers = new ArrayList<>();
        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();
            while (line!=null){
                sb.append(line);
                line = reader.readLine();
            }
            //Log.d("etOrche",sb.toString());
            JSONArray performersJSONArray = new JSONArray(sb.toString());

            for(int i=0;i<performersJSONArray.length();i++){
                JSONObject object = performersJSONArray.getJSONObject(i);
                Performer performer = new Performer();
                performer.setName(object.getString("playerName"));
                performer.setCaption(object.getString("playerCaption"));
                performer.setImage(object.getString("playerPhoto"));
                performers.add(performer);
            }

           // Log.d("getorch",performers.toString());
            return performers;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return performers;
    }
}
