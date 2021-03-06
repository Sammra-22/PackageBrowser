package se.dohi.packagebrowser;

import java.util.ArrayList;
import java.util.List;

import se.dohi.packagebrowser.model.Bundle;
import se.dohi.packagebrowser.model.Package;

/**
 * Created by Sam22 on 9/30/15.
 */
public class PackageManager {

    private static PackageManager instance;
    private List<Package> mPackages;
    private Bundle mBundle;


    private PackageManager() {
        mPackages = new ArrayList<>();
    }

    public static PackageManager getInstance() {
        if (instance == null) {
            instance = new PackageManager();
        }
        return instance;
    }

    public List<Package> getPackages() {
        return mPackages;
    }

    public void setPackages(List<Package> packages) {
        mPackages = packages;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle bundle) {
        this.mBundle = bundle;
    }
}
