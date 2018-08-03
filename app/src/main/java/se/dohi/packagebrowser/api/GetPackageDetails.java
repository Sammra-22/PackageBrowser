package se.dohi.packagebrowser.api;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

    private WeakReference<Context> context;
    private Package mPackage;
    private BundleListener listener;
    private boolean isLanguageSet;

    public GetPackageDetails(Context context, Package pckg, BundleListener listener) {
        super(FORMAT.OBJECT);
        this.context = new WeakReference<>(context);
        this.mPackage = pckg;
        this.listener = listener;
    }

    /**
     * execute query to fetch all
     */
    public void query() {
        isLanguageSet = false;
        execute(context.get().getString(R.string.url_base) + "read/" + this.mPackage.getName());
    }

    /**
     * execute filtered query
     *
     * @param environment {Production/Development}
     * @param language    language code
     */
    public void query(Environment environment, String language) {
        isLanguageSet = true;
        execute(context.get().getString(R.string.url_base) + "read/" + this.mPackage.getName() + "/" + environment.toString() + "/" + language);
    }

    @Override
    protected void onResult(Object result) {
        Bundle bundle = null;
        if (result == null) {
            //No internet connection/Server failure
            listener.onReceiveBundle(null);
            return;
        }
        try {
            // Set language to device's default if no language has been selected
            String defaultLang = context.get().getResources().getConfiguration().locale.getLanguage();
            JSONObject pckgDetail = (JSONObject) result;
            String info, more;
            if (!isLanguageSet) {
                info = pckgDetail.getJSONObject("bundle_info").optString(defaultLang);
                more = pckgDetail.getJSONObject("bundle_more_info").optString(defaultLang);
                if (TextUtils.isEmpty(info)) {
                    info = pckgDetail.getJSONObject("bundle_info").optString("en");
                    more = pckgDetail.getJSONObject("bundle_more_info").optString("en");
                }
            } else {
                info = pckgDetail.getString("bundle_info");
                more = pckgDetail.getString("bundle_more_info");
            }
            bundle = new Bundle(info);
            bundle.setMoreInfo(more);
            JSONArray pathArray = pckgDetail.getJSONArray("paths");
            List<Path> pathList = new ArrayList<>();
            for (int i = 0; i < pathArray.length(); i++) {
                try {
                    JSONObject pathJson = pathArray.getJSONObject(i);
                    String name, description;
                    if (!isLanguageSet) {
                        name = pathJson.getJSONObject("path_name").optString(defaultLang);
                        description = pathJson.getJSONObject("path_info").optString(defaultLang);
                        if (TextUtils.isEmpty(name)) {
                            name = pathJson.getJSONObject("path_name").optString("en");
                            description = pathJson.getJSONObject("path_info").optString("en");
                        }
                    } else {
                        name = pathJson.getString("path_name");
                        description = pathJson.getString("path_info");
                    }
                    Path path = new Path.Builder(name)
                            .setInfo(description)
                            .setLength(pathJson.optLong("path_length"))
                            .setTime(pathJson.optLong("path_time"))
                            .setImageUri(pathJson.optString("path_image"))
                            .toPath();
                    JSONArray polylineJson = pathJson.optJSONArray("path_polyline");
                    if (polylineJson != null) {
                        List<Line> polyline = new ArrayList<>();
                        for (int j = 0; j < polylineJson.length(); j++) {
                            JSONArray lineJson = polylineJson.getJSONArray(j);
                            Line line = Line.parseFromJson(lineJson);
                            polyline.add(line);
                        }
                        path.setPolyline(polyline);
                    }
                    pathList.add(path);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            bundle.setPaths(pathList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        PackageManager.getInstance().setBundle(bundle);
        if (listener != null) {
            listener.onReceiveBundle(bundle);
        }
    }
}
