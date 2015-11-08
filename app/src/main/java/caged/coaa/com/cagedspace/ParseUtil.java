package caged.coaa.com.cagedspace;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinodkumar on 11/4/2015.
 */
public class ParseUtil {
   public static  List<String> alStreams;
    public static List<String> getStreams(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("StreamURLS");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    alStreams = new ArrayList<String>();
                    for(ParseObject po:scoreList){
                        alStreams.add(po.getString("url"));

                    }


                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

    return alStreams;
    }




}
