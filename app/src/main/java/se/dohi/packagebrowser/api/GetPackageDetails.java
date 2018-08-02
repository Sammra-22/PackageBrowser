package se.dohi.packagebrowser.api;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.dohi.packagebrowser.PackageManager;
import se.dohi.packagebrowser.R;
import se.dohi.packagebrowser.listener.BundleListener;
import se.dohi.packagebrowser.model.Bundle;
import se.dohi.packagebrowser.model.Environment;
import se.dohi.packagebrowser.model.Line;
import se.dohi.packagebrowser.model.Package;
import se.dohi.packagebrowser.model.Path;
import se.dohi.packagebrowser.network.HttpsAsyncTaskJson;

/**
 * Created by Sam22 on 9/28/15.
 * Fetch the detailed info of a specific package
 */
public class GetPackageDetails extends HttpsAsyncTaskJson {

    Context context;
    Package mPackage;
    BundleListener listener;
    boolean isLanguageSet;

    public GetPackageDetails(Context context, Package pckg, BundleListener listener){
        super(FORMAT.OBJECT);
        this.context = context;
        this.mPackage = pckg;
        this.listener = listener;
    }

    /**
     * execute query to fetch all
     */
    public void query(){
        isLanguageSet = false;
        execute(context.getString(R.string.url_base) + "read/" + this.mPackage.getName());
    }

    /**
     * execute filtered query
     * @param environment {Production/Development}
     * @param language language code
     */
    public void query(Environment environment, String language){
        isLanguageSet = true;
        execute(context.getString(R.string.url_base) + "read/" + this.mPackage.getName() +"/"+environment.toString()+"/"+language);
    }

    @Override
    protected void onResult(Object result) {
        Bundle bundle = null;
        if(result==null){
            //No internet connection/Server failure
            listener.onReceiveBundle(null);
            return;
        }
        try {
            // Set language to device's default if no language has been selected
                String defaultLang = context.getResources().getConfiguration().locale.getLanguage();
                String lang =Locale.getDefault().getDisplayLanguage();
                JSONObject pckgDetail = (JSONObject) result;
                String info,more;
                if(!isLanguageSet){
                    info= pckgDetail.getJSONObject("bundle_info").optString(defaultLang);
                    more= pckgDetail.getJSONObject("bundle_more_info").optString(defaultLang);
                    if(TextUtils.isEmpty(info)){
                        info= pckgDetail.getJSONObject("bundle_info").optString("en");
                        more= pckgDetail.getJSONObject("bundle_more_info").optString("en");
                    }
                }else{
                    info= pckgDetail.getString("bundle_info");
                    more= pckgDetail.getString("bundle_more_info");
                }
                bundle = new Bundle(info);
                bundle.setMoreInfo(more);
                JSONArray pathArray = pckgDetail.getJSONArray("paths");
                List<Path> pathList = new ArrayList<>();
                for (int i = 0; i < pathArray.length(); i++) {
                    try {
                        JSONObject pathJson = pathArray.getJSONObject(i);
                        String name,description;
                        if(!isLanguageSet){
                            name= pathJson.getJSONObject("path_name").optString(defaultLang);
                            description= pathJson.getJSONObject("path_info").optString(defaultLang);
                            if(TextUtils.isEmpty(name)){
                                name= pathJson.getJSONObject("path_name").optString("en");
                                description= pathJson.getJSONObject("path_info").optString("en");
                            }
                        }else{
                            name= pathJson.getString("path_name");
                            description= pathJson.getString("path_info");
                        }
                        Path path = new Path(name);
                        path.setInfo(description);
                        path.setLength(pathJson.optLong("path_length"));
                        path.setTime(pathJson.optLong("path_time"));
                        path.setImageUri(pathJson.optString("path_image"));
                        JSONArray polylineJson =pathJson.optJSONArray("path_polyline");
                        if(polylineJson!=null){
                            List<Line> polyline = new ArrayList<>();
                            for(int j=0;j<polylineJson.length();j++){
                                Line line = new Line();
                                JSONArray lineJson = polylineJson.getJSONArray(j);
                                for(int k=0;k<lineJson.length();k++){
                                    JSONObject locationJson = lineJson.getJSONObject(j);
                                    Location location = new Location(String.valueOf(j));
                                    location.setLatitude(locationJson.getDouble("lat"));
                                    location.setLongitude(locationJson.getDouble("lng"));
                                    line.addCoordinate(location);
                                }
                                polyline.add(line);
                            }
                            path.setPolyline(polyline);
                        }
                        pathList.add(path);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                bundle.setPaths(pathList);

        }catch (JSONException e){
            e.printStackTrace();
        }
        PackageManager.getInstance().setBundle(bundle);
        if(listener!=null)
            listener.onReceiveBundle(bundle);
    }
}
