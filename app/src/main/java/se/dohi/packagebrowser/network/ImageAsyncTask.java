package se.dohi.packagebrowser.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import se.dohi.packagebrowser.listener.ImageListener;
import se.dohi.packagebrowser.model.Path;
import se.dohi.packagebrowser.utils.FileUtils;

/**
 * Created by Sam22 on 10/1/15.
 * Downloading a Thumbernail image
 */
public class ImageAsyncTask extends AsyncTask<Path, Void, Bitmap> {
    private final static String TAG = ImageAsyncTask.class.getName();
    private Context context;
    private ImageListener listener;

    public ImageAsyncTask(Context context, ImageListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Path... paths) {
        Bitmap result = null;
        try {
            String url = paths[0].getImageUri();
            String fileName = paths[0].getName();
            File imageFile = FileUtils.getImageFile(context, fileName);

            if (imageFile.exists() && imageFile.length() > 0) {
                Log.i(TAG, "File exists: " + imageFile.getAbsolutePath());
                FileInputStream streamIn = new FileInputStream(imageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
                streamIn.close();
                return bitmap;
            }

            Log.i(TAG, "Grabbing file: " + url);
            InputStream inStream = null;
            int response = -1;
            while (response == -1 || response == 301 || response == 302) {
                if (url.startsWith("https")) {
                    HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
                    connection.setInstanceFollowRedirects(false);
                    connection.connect();
                    response = connection.getResponseCode();
                    url = connection.getHeaderField("Location");
                    if (response == 200) {
                        inStream = connection.getInputStream();
                    }
                } else {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setInstanceFollowRedirects(false);
                    connection.connect();
                    response = connection.getResponseCode();
                    url = connection.getHeaderField("Location");
                    if (response == 200) {
                        inStream = connection.getInputStream();
                    }
                }
                Log.i(TAG, "Response code: " + response);
            }


            if (inStream != null) {
                result = BitmapFactory.decodeStream(inStream);
                OutputStream output = new FileOutputStream(imageFile);
                if (result != null) {
                    result.compress(Bitmap.CompressFormat.PNG, 100, output);
                }
                // flushing output
                output.flush();
                // closing streams
                output.close();
                inStream.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null && listener != null) {
            listener.onDownloadComplete();
        }
    }
}
