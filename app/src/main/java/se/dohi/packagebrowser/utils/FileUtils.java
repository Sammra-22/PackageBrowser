package se.dohi.packagebrowser.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Sam22 on 10/1/15.
 */
public class FileUtils {

    static File storageDir;

    public static File getStorageDir(Context context){
        if(storageDir==null)
            storageDir= context.getExternalFilesDir(null);
        return storageDir;
    }

    public static File getImageFile(Context context, String name){
        return new File(getStorageDir(context).getAbsolutePath(),name);
    }

    public static boolean imageExists(Context context, String name){
        return getImageFile(context, name).exists() && getImageFile(context, name).length()>0;
    }

}
