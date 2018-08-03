package se.dohi.packagebrowser.api;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import se.dohi.packagebrowser.PackageManager;
import se.dohi.packagebrowser.R;
import se.dohi.packagebrowser.model.Package;
import se.dohi.packagebrowser.network.HttpsAsyncTaskJson;
import se.dohi.packagebrowser.utils.Const;

/**
 * Created by Sam22 on 9/28/15.
 * Fetch the list of available packages
 */
public class GetPackages extends HttpsAsyncTaskJson {

    WeakReference<Context> context;

    public GetPackages(Context context) {
        super(FORMAT.ARRAY);
        this.context = new WeakReference<>(context);
    }

    /**
     * execute query
     */
    public void query() {
        execute(context.get().getString(R.string.url_base) + "/list");
    }

    @Override
    protected void onResult(Object result) {
        Intent broadIntent = new Intent(Const.BROADCAST_PACKAGES_RESULT);
        try {
            if (result == null) {
                broadIntent.putExtra(Const.SUCCESS, false);
            } else {
                //Parse received result
                broadIntent.putExtra(Const.SUCCESS, true);
                List<Package> packages = new ArrayList<>();
                JSONArray pckgList = (JSONArray) result;
                for (int i = 0; i < pckgList.length(); i++) {
                    JSONObject pckg = pckgList.getJSONObject(i);
                    Package unit = new Package(pckg.getString("name"));
                    String languages = pckg.optString("lang");
                    if (!TextUtils.isEmpty(languages)) {
                        String[] listLang = languages.split(",");
                        for (String lang : listLang) {
                            unit.addLanguage(lang);
                        }
                    }
                    packages.add(unit);
                }
                PackageManager.getInstance().setPackages(packages);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        context.get().sendBroadcast(broadIntent);
    }
}
