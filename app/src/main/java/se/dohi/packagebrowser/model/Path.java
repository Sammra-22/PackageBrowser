package se.dohi.packagebrowser.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sam22 on 10/1/15.
 */
public class Path implements Serializable {

    private String name;
    private String info;
    private long length;
    private long time;
    private String imageUri;
    private List<Line> polyline;

    Path(String name) {
        this.name = name;
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

    public void setPolyline(List<Line> polyline) {
        this.polyline = polyline;
    }

    public static class Builder {
        private Path path;

        public Builder(String pathName) {
            this.path = new Path(pathName);
        }

        public Builder setInfo(String info) {
            path.info = info;
            return this;
        }

        public Builder setTime(long time) {
            path.time = time;
            return this;
        }

        public Builder setLength(long length) {
            path.length = length;
            return this;
        }

        public Builder setImageUri(String imageUri) {
            path.imageUri = imageUri;
            return this;
        }

        public Path toPath() {
            return path;
        }

    }
}
