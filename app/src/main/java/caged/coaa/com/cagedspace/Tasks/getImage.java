package caged.coaa.com.cagedspace.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getImage extends AsyncTask<String,Void,Bitmap> {
    ProgressDialog pd;
    Context mContext;

    public getImage(Context context){
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(mContext);
        pd.setTitle("Loading Image...");
        pd.show();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        pd.dismiss();
    }

    @Override
    protected Bitmap doInBackground(String[] params) {
        HttpURLConnection connection;
        Bitmap bp = null;
        try {
            if(params[0]!=null) {
                connection = (HttpURLConnection) new URL(params[0]).openConnection();
                bp = BitmapFactory.decodeStream(connection.getInputStream());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bp;
    }
}
