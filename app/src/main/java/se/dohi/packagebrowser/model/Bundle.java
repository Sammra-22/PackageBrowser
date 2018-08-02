package se.dohi.packagebrowser.model;

import java.util.List;

/**
 * Created by Sam22 on 10/1/15.
 */
public class Bundle {

    String info;
    String moreInfo;

    List<Path> paths;

    public Bundle(String info){
        this.info = info;
    }


    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public String getInfo() {
        return info;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public List<Path> getPaths() {
        return paths;
    }
}
