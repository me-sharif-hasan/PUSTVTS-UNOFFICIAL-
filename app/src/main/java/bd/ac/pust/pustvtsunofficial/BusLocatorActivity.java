package bd.ac.pust.pustvtsunofficial;

import Maps.MapController;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

public class BusLocatorActivity extends AppCompatActivity {

    FrameLayout bus_finder;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_locator);
        bus_finder = findViewById(R.id.fl_fragmentHolder);
        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        manager.replace(bus_finder.getId(),new MapInflation()).commit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BusFactory.createBus(0,"0351510093645193","BUS 1 (BOYS)","Ananta Bazar - Shohor - Meril - Campus");
                    BusFactory.createBus(1,"0351510093643297","BUS 2 (BOYS)","Ananta Bazar - Shohor - Meril - Campus");
                    BusFactory.createBus(2,"0351510093647488","BUS 3 (BOYS)","Ananta Bazar - Shohor - Meril - Campus");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public static class MapInflation extends Fragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_bus_finder, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            SupportMapFragment mapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            MapController mpc=new MapController();
            assert mapFragment != null;
            mapFragment.getMapAsync(mpc);
        }
    }
}