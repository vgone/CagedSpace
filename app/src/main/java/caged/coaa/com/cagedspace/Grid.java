package caged.coaa.com.cagedspace;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vinodkumar on 11/30/2015.
 */
public class Grid {

    private int id;
    private String beaconId;
    private String streamURL;

    static Grid createGrid(JSONObject gridJSONObject) throws JSONException {
      Grid gridTemplate = new Grid();
        gridTemplate.setId(gridJSONObject.getInt("id"));
        gridTemplate.setBeaconId(gridJSONObject.getString("beaconId"));
        gridTemplate.setStreamURL(gridJSONObject.getString("streamURL"));
        return  gridTemplate;

    }

    @Override
    public String toString() {
        return "Grid{" +
                "id=" + id +
                ", beaconId='" + beaconId + '\'' +
                ", streamURL='" + streamURL + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public void setStreamURL(String streamURL) {
        this.streamURL = streamURL;
    }

}
