package bd.ac.pust.pustvtsunofficial;

import Maps.MapController;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

public class BusLocatorActivity extends AppCompatActivity {

    FrameLayout bus_finder;
    ImageView dashboard;
    DrawerLayout drawerLayout;
    LinearLayout vehicles,stoppages,add_alearm,help,logout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_locator);
        bus_finder = findViewById(R.id.fl_fragmentHolder);
        dashboard = findViewById(R.id.iv_dashboard);
        drawerLayout = findViewById(R.id.dl_bus_locator);
        vehicles = findViewById(R.id.ll_nav_vehicles);
        stoppages = findViewById(R.id.ll_nav_stoppages);
        add_alearm = findViewById(R.id.ll_nav_add_alarm);
        help = findViewById(R.id.ll_nav_help);
        logout = findViewById(R.id.ll_nav_logout);

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrower();
            }
        });

        vehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BusLocatorActivity.this,"You clicked on Vehicles.",
                        Toast.LENGTH_LONG).show();
                closeDrawer(drawerLayout);
            }
        });
        stoppages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BusLocatorActivity.this,"You clicked on Stoppages.",
                        Toast.LENGTH_LONG).show();
                closeDrawer(drawerLayout);
            }
        });
        add_alearm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BusLocatorActivity.this,"You clicked on Add Alarm.",
                        Toast.LENGTH_LONG).show();
                closeDrawer(drawerLayout);
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BusLocatorActivity.this,"You clicked on Help.",
                        Toast.LENGTH_LONG).show();
                closeDrawer(drawerLayout);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BusLocatorActivity.this,"You clicked on Log Out.",
                        Toast.LENGTH_LONG).show();
                closeDrawer(drawerLayout);
            }
        });

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

    private void openDrower() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void BusIC_clickListener(View view){
        closeDrawer(drawerLayout);
    }

    private void closeDrawer(DrawerLayout layout){
        if(layout.isDrawerOpen(GravityCompat.START)){
            layout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
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