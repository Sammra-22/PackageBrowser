package se.dohi.packagebrowser.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import se.dohi.packagebrowser.R;
import se.dohi.packagebrowser.api.GetPackages;
import se.dohi.packagebrowser.listener.DialogDismissListener;
import se.dohi.packagebrowser.utils.Const;
import se.dohi.packagebrowser.utils.DialogUtils;

/**
 * Created by Sam22 on 9/28/15.
 * Splash screen- Loading packages
 */
public class SplashActivity extends Activity implements DialogDismissListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        registerReceiver(receiver, new IntentFilter(Const.BROADCAST_PACKAGES_RESULT));
        new GetPackages(this).query();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * Broadcast receiver monitoring packages acquisition
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction()==Const.BROADCAST_PACKAGES_RESULT){
                final boolean success =intent.getBooleanExtra(Const.SUCCESS, false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(success) {
                            startActivity(new Intent(SplashActivity.this, DisplayActivity.class));
                            finish();
                        }else
                            DialogUtils.showAlert(SplashActivity.this,
                                    context.getString(R.string.connection_warning_title),
                                    context.getString(R.string.connection_warning_body),
                                    SplashActivity.this
                            );
                    }
                },500);

            }
        }
    };

    @Override
    public void onDismiss() {
        finish();
    }
}
