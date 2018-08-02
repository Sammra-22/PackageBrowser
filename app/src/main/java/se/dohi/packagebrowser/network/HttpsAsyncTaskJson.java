package se.dohi.packagebrowser.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sam22 on 9/28/15.
 * Async task to execute HTTPS requests for JSON responses
 */
public abstract class HttpsAsyncTaskJson extends AsyncTask<String, Void, Object> {
    private final static String TAG = HttpsAsyncTaskJson.class.getName();
    protected enum FORMAT {OBJECT,ARRAY}

    FORMAT returnType;

    /**
     * Default Constructor
     * @param returnType expected data structure
     */
    protected HttpsAsyncTaskJson(FORMAT returnType){this.returnType = returnType;}

    abstract protected void onResult(Object result);

    @Override
    protected Object doInBackground(String... arg0) {
        Object response = null;
        try {
            Log.i(TAG, "+++ URL: " + arg0[0]);
            URL url = new URL(arg0[0]);
            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(5000);
            c.setReadTimeout(5000);
            c.connect();
            if(c.getResponseCode()==200){
                //            FileOutputStream fos = new FileOutputStream(updateVersion);
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                if(returnType==FORMAT.OBJECT)
                    response = new JSONObject(sb.toString());
                else if(returnType==FORMAT.ARRAY)
                    response = new JSONArray(sb.toString());
                Log.i(TAG, "+++ JSON: " + response);
                br.close();
            }

        } catch (MalformedURLException | ProtocolException e){
            Log.e(TAG, "Request failure");
            e.printStackTrace();
        } catch (IOException e){
            Log.e(TAG, "Connection failure");
            e.printStackTrace();
        } catch (JSONException e){
            Log.e(TAG, "Response failure");
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        onResult(result);
    }
}
