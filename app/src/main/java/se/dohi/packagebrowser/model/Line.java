package se.dohi.packagebrowser.model;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam22 on 10/1/15.
 */
public class Line implements Serializable {

    private List<Location> coordinates;

    public Line() {
        coordinates = new ArrayList<>();
    }

    public static Line parseFromJson(JSONArray lineJson) throws JSONException {
        Line line = new Line();
        for (int k = 0; k < lineJson.length(); k++) {
            JSONObject locationJson = lineJson.getJSONObject(k);
            Location location = new Location(String.valueOf(k));
            location.setLatitude(locationJson.getDouble("lat"));
            location.setLongitude(locationJson.getDouble("lng"));
            line.addCoordinate(location);
        }
        return line;
    }

    public List<Location> getCoordinates() {
        return coordinates;
    }

    private void addCoordinate(Location coordinate) {
        coordinates.add(coordinate);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(coordinates.size());
        for (Location location : coordinates) {
            stream.writeObject(location.getLatitude());
            stream.writeObject(location.getLongitude());
        }

    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int size = (int) stream.readObject();
        coordinates = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Location coord = new Location("" + i);
            coord.setLatitude((double) stream.readObject());
            coord.setLongitude((double) stream.readObject());
            coordinates.add(coord);
        }
    }
}
