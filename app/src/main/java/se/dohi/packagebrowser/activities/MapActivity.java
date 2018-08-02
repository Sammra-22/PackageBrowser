package se.dohi.packagebrowser.activities;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import se.dohi.packagebrowser.R;
import se.dohi.packagebrowser.model.Line;

/**
 * Created by Sam22 on 9/30/15.
 * Display the path location using Google Maps APIs
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    List<Location> path;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        path= ((Line)getIntent().getExtras().getSerializable("path")).getCoordinates();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLng start = new LatLng(path.get(0).getLatitude(), path.get(0).getLongitude());
        final LatLng end = new LatLng(path.get(path.size()-1).getLatitude(), path.get(path.size()-1).getLongitude());

        mMap.addMarker(new MarkerOptions().position(start).title("Start"));
        mMap.addMarker(new MarkerOptions().position(end).title("Finish"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(start));

        //Render path route
        PolylineOptions route= new PolylineOptions().geodesic(true);
        for(Location point:path)
            route.add(new LatLng(point.getLatitude(), point.getLongitude()));

        mMap.addPolyline(route);

    }
}
