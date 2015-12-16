package caged.coaa.com.cagedspace.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import caged.coaa.com.cagedspace.Utils.Grid;
import caged.coaa.com.cagedspace.Utils.GridUtil;
import caged.coaa.com.cagedspace.Interface.GridCallBack;
import caged.coaa.com.cagedspace.R;

/**
 * Created by vinodkumar on 11/30/2015.
 */
public class GridTask extends AsyncTask<String, Void, ArrayList<Grid>> {


    private Context mContext;
    private GridCallBack gridCallBack;

    //private WeatherTaskCallback taskCallback;
    public GridTask(Context context, GridCallBack gridCallBack) {
        mContext = context;
        this.gridCallBack = gridCallBack;
    }


    ProgressDialog pd;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setMessage(mContext.getResources().getString(R.string.dpDownload));
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected void onPostExecute(ArrayList<Grid> grids) {
        super.onPostExecute(grids);
        pd.dismiss();
        gridCallBack.getGridList(grids);
    }

    @Override
    protected ArrayList<Grid> doInBackground(String... params) {
        HttpURLConnection con;
        URL url = null;
        try {
            url = new URL(params[0]);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            return GridUtil.parseGrids(con.getInputStream());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
