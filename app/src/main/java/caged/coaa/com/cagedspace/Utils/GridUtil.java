package caged.coaa.com.cagedspace.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by vinodkumar on 11/30/2015.
 */
public class GridUtil {


    static public ArrayList<Grid> parseGrids(InputStream in) throws JSONException,IOException {
        ArrayList<Grid> gridsList = new ArrayList<Grid>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        Log.d("demo", sb.toString());
        JSONArray jsonGridArray = new JSONArray(sb.toString());
        for (int i = 0; i < jsonGridArray.length(); i++) {
            JSONObject gridJSONObject = jsonGridArray.getJSONObject(i);
            Grid gridTemplate = Grid.createGrid(gridJSONObject);
            gridsList.add(gridTemplate);
        }


        return gridsList;
    }


}
