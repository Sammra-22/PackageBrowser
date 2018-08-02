package se.dohi.packagebrowser.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sam22 on 10/1/15.
 */
public class Path implements Serializable{

    String name;
    String info;
    long length;
    long time;
    String imageUri;
    List<Line> polyline;


    public Path(String name){this.name = name;}

    public void setInfo(String info) {
        this.info = info;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPolyline(List<Line> polyline) {
        this.polyline = polyline;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getInfo() {
        return info;
    }

    public long getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public long getLength() {
        return length;
    }

    public String getImageUri() {
        return imageUri;
    }

    public List<Line> getPolyline() {
        return polyline;
    }
}
