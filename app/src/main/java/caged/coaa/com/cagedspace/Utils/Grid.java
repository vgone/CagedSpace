package caged.coaa.com.cagedspace.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vinodkumar on 11/30/2015.
 */
public class Grid {

    private int id;
    private String beaconId;
    private String streamURL;
    private String gridImageURL;

    public String getGridImageURL() {
        return gridImageURL;
    }

    public void setGridImageURL(String gridImageURL) {
        this.gridImageURL = gridImageURL;
    }

    static Grid createGrid(JSONObject gridJSONObject) throws JSONException {
      Grid gridTemplate = new Grid();
        gridTemplate.setId(gridJSONObject.getInt("id"));
        gridTemplate.setBeaconId(gridJSONObject.getString("beaconId"));
        gridTemplate.setStreamURL(gridJSONObject.getString("streamURL"));
        gridTemplate.setGridImageURL(gridJSONObject.getString("gridImageURL"));
        return  gridTemplate;

    }

    @Override
    public String toString() {
        return "Grid{" +
                "id=" + id +
                ", beaconId='" + beaconId + '\'' +
                ", streamURL='" + streamURL + '\'' +
                ", gridImageURL='" + gridImageURL + '\'' +
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
