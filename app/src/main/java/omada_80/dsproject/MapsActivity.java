package omada_80.dsproject;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Map<LocationPOI, Long> mapResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapResult = (Map<LocationPOI, Long>) this.getIntent().
                getSerializableExtra("resultMap");

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
        // Add a marker in Sydney and move the camera
        for (Map.Entry<LocationPOI, Long> entry : mapResult.entrySet()) {
            LocationPOI locationPOI = entry.getKey();
            Long value = entry.getValue();
            LatLng loc = new LatLng(locationPOI.getLatitude(), locationPOI.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(loc).title(locationPOI.getPOIName()
                    + " <=> " + value));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }

    }
}
