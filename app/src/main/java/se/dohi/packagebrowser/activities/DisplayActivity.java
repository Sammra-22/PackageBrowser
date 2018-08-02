package se.dohi.packagebrowser.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import se.dohi.packagebrowser.PackageManager;
import se.dohi.packagebrowser.R;
import se.dohi.packagebrowser.adapaters.PathListAdapter;
import se.dohi.packagebrowser.api.GetPackageDetails;
import se.dohi.packagebrowser.api.GetPackages;
import se.dohi.packagebrowser.listener.BundleListener;
import se.dohi.packagebrowser.listener.DialogPickerListener;
import se.dohi.packagebrowser.listener.ImageListener;
import se.dohi.packagebrowser.model.*;
import se.dohi.packagebrowser.model.Package;
import se.dohi.packagebrowser.network.ImageAsyncTask;
import se.dohi.packagebrowser.utils.Const;
import se.dohi.packagebrowser.utils.DialogUtils;

/**
 * Created by Sam22 on 9/28/15.
 * Main Activity
 */
public class DisplayActivity extends AppCompatActivity implements BundleListener,ImageListener,AdapterView.OnItemClickListener{

    TextView packageLabel, environmentLabel, languageLabel;
    View environmentRow, languageRow;
    PackageManager mPckgManager;
    Package selectedPackage;
    Environment selectedEnvironment;
    String selectedLanguage;
    PathListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.menubar));
        setContentView(R.layout.activity_display);

        packageLabel = (TextView)findViewById(R.id.packageName);
        environmentLabel = (TextView)findViewById(R.id.environment);
        languageLabel = (TextView)findViewById(R.id.language);

        environmentRow = findViewById(R.id.row_environment);
        languageRow = findViewById(R.id.row_language);

        mPckgManager = PackageManager.getInstance();

        if(mPckgManager.getPackages().size()==1) {
            // If only one package exists set it as default selection
            setPackageSelected(mPckgManager.getPackages().get(0));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(Const.BROADCAST_PACKAGES_RESULT));
        if(selectedPackage==null){
            new GetPackages(this).query();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);

    }

    /**
     * Set a package as selected
     * @param pckg package to select
     */
    private void setPackageSelected(Package pckg){
        selectedPackage = pckg;
        packageLabel.setText(pckg.getName());
        environmentRow.setVisibility(View.VISIBLE);
        if(!updateTrailInfo())
            new GetPackageDetails(this, selectedPackage, this).query();
    }

    /**
     * On press package selection button
     * @param v
     */
    public void onSelectPackage(View v){
        final List<Package> packages= mPckgManager.getPackages();
        final String[] items = new String[packages.size()];
        for (int i=0;i<packages.size();i++){
            items[i]="\n"+packages.get(i).getName()+"\n";
        }
        //Set default selection to the previous selection
        int defaultSelection = (selectedPackage!=null)?packages.indexOf(selectedPackage):0;
        DialogUtils.showPicker(this, "Select Package", items, defaultSelection, new DialogPickerListener() {
            @Override
            public void onItemSelected(int position) {
                setPackageSelected(packages.get(position));
            }
        });
    }

    /**
     * On press environment selection button
     * @param v
     */
    public void onSelectEnvironment(View v){
        final String[] items = Environment.toArray();
        //Set default selection to the previous selection
        int defaultSelection = (selectedEnvironment!=null)?selectedEnvironment.ordinal():-1;
        DialogUtils.showPicker(this, "Select Environment", items, defaultSelection, new DialogPickerListener() {
            @Override
            public void onItemSelected(int position) {
                if(position>=0) {
                    languageRow.setVisibility(View.VISIBLE);
                    selectedEnvironment = Environment.values()[position];
                    environmentLabel.setText(selectedEnvironment.name().toLowerCase());
                    updateTrailInfo();
                }
            }
        });
    }

    /**
     * On press language selection button
     * @param v
     */
    public void onSelectLanguage(View v){
        final String[] items = new String[selectedPackage.getLanguages().size()];
        Iterator it = selectedPackage.getLanguages().iterator();
        int defaultSelection = -1;
        int i=0;
        //Set default selection to the previous selection
        while (it.hasNext()){
            String lang = (String)it.next();
            items[i]="\n"+lang+"\n";
            if(selectedLanguage!=null && selectedLanguage.equals(lang))
                defaultSelection = i;
            i++;
        }

        DialogUtils.showPicker(this, "Select Language", items, defaultSelection, new DialogPickerListener() {
            @Override
            public void onItemSelected(int position) {
                if(position>=0) {
                    selectedLanguage = items[position].replaceAll("\n", "");
                    languageLabel.setText(selectedLanguage);
                    updateTrailInfo();
                }
            }
        });
    }

    @Override
    public void onReceiveBundle(final se.dohi.packagebrowser.model.Bundle bundle) {
        TextView info = (TextView) findViewById(R.id.info);
        Button moreInfoBtn = (Button) findViewById(R.id.moreInfo);
        ListView list = (ListView) findViewById(android.R.id.list);

        if(bundle!=null) {
            //Receive valid bundle
            info.setText(bundle.getInfo());
            moreInfoBtn.setVisibility(View.VISIBLE);
            if(TextUtils.isEmpty(bundle.getMoreInfo())) {
                moreInfoBtn.setEnabled(false);
                moreInfoBtn.setClickable(false);
            }else
                moreInfoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogUtils.showAlert(DisplayActivity.this, selectedPackage.getName(), bundle.getMoreInfo(), null);
                    }
            });

            adapter = new PathListAdapter(this, R.layout.path_item, bundle.getPaths());
            list.setOnItemClickListener(this);
            list.setAdapter(adapter);
            for(Path path:bundle.getPaths())
                new ImageAsyncTask(getBaseContext(), this).execute(path);

        }else{
            info.setText("No connection");
            moreInfoBtn.setVisibility(View.GONE);
            adapter = new PathListAdapter(this, R.layout.path_item, null);
            list.setAdapter(adapter);
        }
    }

    @Override
    public void onDownloadComplete() {
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }

    /**
     * Update package detailed trail info
     * @return updating occurred
     */
    private boolean updateTrailInfo(){
        if(!TextUtils.isEmpty(selectedLanguage) && selectedEnvironment!=null) {
            new GetPackageDetails(this, selectedPackage, this).query(selectedEnvironment, selectedLanguage);
            return true;
        }
        return false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("path", adapter.getItem(i).getPolyline().get(0));
        startActivity(intent);
    }

    /**
     * Broadcast receiver listening for packages updating
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction()!=null && intent.getAction()==Const.BROADCAST_PACKAGES_RESULT){
                if(mPckgManager.getPackages().size()==1) {
                    setPackageSelected(mPckgManager.getPackages().get(0));
                }
            }
        }
    };
}
