package bd.ac.pust.pustvtsunofficial;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.databinding.ActivityBusFinderBinding;

public class BusFinderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityBusFinderBinding binding;

    private Map <Integer,MarkerOptions> buseMarkers=new HashMap<>();
    private int focusedBus=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBusFinderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /* Create Buses*/

        /*Update Buses*/
        new Thread(new Runnable() {
            int i=0;
            @Override
            public void run() {
                //JSON to latlang
                while (true) {
                    for (i = 0; i < BusFactory.getNumberOfBuses(); i++) {
                        if(focusedBus!=-1&&focusedBus!=i) break;
                        int k=i;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d("IITRACK","GETTING "+k);
                                    //String busCurrentLocation = BusFactory.whereAreThisBus(k);
                                   // Log.d("IIPOS",busCurrentLocation);
                                } catch (Exception e) {
                                   // e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        MarkerOptions markerOptions=new MarkerOptions().position(sydney).title("Marker in Sydney");
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}