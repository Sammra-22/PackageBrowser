package se.dohi.packagebrowser.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import se.dohi.packagebrowser.listener.DialogDismissListener;
import se.dohi.packagebrowser.listener.DialogPickerListener;

/**
 * Created by Sam22 on 9/30/15.
 * Utility class for dialog rendering
 */
public class DialogUtils {

    static int selection;

    /**
     * Display a simple Alert
     * @param context
     * @param title title
     * @param body message
     * @param listener callback when dismissed
     */
    public static void showAlert(final Context context, String title, String body, final DialogDismissListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false)
                .setTitle(title)
                .setMessage(body)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(listener!=null)
                            listener.onDismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * Display a dialog selector
     * @param context
     * @param title title
     * @param items single choice items
     * @param selected default selection
     * @param listener callback when selected
     */
    public static void showPicker(final Context context, String title, String[] items, int selected, final DialogPickerListener listener){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        selection = selected;
        builder.setCancelable(false)
                .setTitle(title)
                .setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selection = i;
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null)
                            listener.onItemSelected(selection);
                    }
                });
        builder.create().show();
    }
}
