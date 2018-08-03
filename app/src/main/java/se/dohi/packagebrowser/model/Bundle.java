package se.dohi.packagebrowser.model;

import java.util.List;

/**
 * Created by Sam22 on 10/1/15.
 */
public class Bundle {

    private String info;
    private String moreInfo;

    private List<Path> paths;

    public Bundle(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }
}
