package caged.coaa.com.cagedspace.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SaideepReddy on 12/11/2015.
 */
public class getOrchestraInfo extends AsyncTask<String,Void,JSONArray> {

    ProgressDialog pd;
    Context mContext;

    public getOrchestraInfo(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setTitle("Loading Information...");
        pd.show();
    }

    @Override
    protected void onPostExecute(JSONArray performers) {
        super.onPostExecute(performers);
        pd.dismiss();
    }

    @Override
    protected JSONArray doInBackground(String[] params) {
        HttpURLConnection connection;
        JSONArray performers = null;
        try {
            connection = (HttpURLConnection) new URL(params[0]).openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();
            while (line!=null){
                sb.append(line);
                line = reader.readLine();
            }
            performers = new JSONArray(sb.toString());
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
