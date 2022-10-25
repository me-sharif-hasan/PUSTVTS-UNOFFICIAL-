package bd.ac.pust.pustvtsunofficial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusInfo;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Config;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;
import bd.ac.pust.pustvtsunofficial.Helper.VehiclesInfoBottomSheet;
import bd.ac.pust.pustvtsunofficial.Maps.MapController;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BusLocatorActivity extends AppCompatActivity {
    FrameLayout bus_finder;
    ImageView dashboard;
    DrawerLayout drawerLayout;
    LinearLayout vehicles,stoppages,add_alearm,help,logout;

    TextView bottomShow,stopedInfo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mpc.setContext(this);
        setContentView(R.layout.activity_bus_locator);
        bus_finder = findViewById(R.id.fl_fragmentHolder);
        dashboard = findViewById(R.id.iv_dashboard);
        drawerLayout = findViewById(R.id.dl_bus_locator);
        vehicles = findViewById(R.id.ll_nav_vehicles);
        stoppages = findViewById(R.id.ll_nav_stoppages);
        add_alearm = findViewById(R.id.ll_nav_add_alarm);
        help = findViewById(R.id.ll_nav_help);
        logout = findViewById(R.id.ll_nav_logout);

        bottomShow = findViewById(R.id.bus_name_show);
        stopedInfo = findViewById(R.id.stopage_name);

        bottomShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehiclesInfoBottomSheet bottomSheet = new VehiclesInfoBottomSheet("Bus1",
                        "Female Students",
                        "Tarminal -> Ananto -> Sahar","8:30 AM");
                bottomSheet.show(getSupportFragmentManager(),bottomSheet.getTag());
            }
        });

        stopedInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehiclesInfoBottomSheet bottomSheet = new VehiclesInfoBottomSheet("Bus1",
                        "Female Students",
                        "Tarminal -> Meril -> Gasspara","18 seconds ago.",
                        "Meril","5 minute ago");
                bottomSheet.show(getSupportFragmentManager(),bottomSheet.getTag());
            }
        });

        Switch isolator=findViewById(R.id.bus_solo_view);
        isolator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpc.isolateView(isolator);
            }
        });

        Spinner bus_selector=findViewById(R.id.bus_selector);
        ArrayAdapter<String> busSelectorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        busSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bus_selector.setAdapter(busSelectorAdapter);

        bus_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mpc.selectBusId(i,BusLocatorActivity.this);
                closeDrawer(drawerLayout);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrower();
            }
        });

        ImageButton recenter=findViewById(R.id.recenter);
        recenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpc.refocus(BusLocatorActivity.this);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        BusFactory.createBusList(new BusFactory.BusCreatedEvent() {
                            @Override
                            public void onBusCreated(String busName,String busId) {
                                BusLocatorActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        busSelectorAdapter.add(busName);
                                        busSelectorAdapter.sort(new Comparator<String>() {
                                            @Override
                                            public int compare(String s, String t1) {
                                                return s.compareTo(t1);
                                            }
                                        });
                                        busSelectorAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                        break;
                    }catch (Exception e){
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        }catch (Exception te){

                        }
                        e.printStackTrace();
                    }
                }
            }
        }).start();
//        vehicles.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(BusLocatorActivity.this,"You clicked on Vehicles.",
//                        Toast.LENGTH_LONG).show();
//                closeDrawer(drawerLayout);
//            }
//        });
//        stoppages.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(BusLocatorActivity.this,"You clicked on Stoppages.",
//                        Toast.LENGTH_LONG).show();
//                closeDrawer(drawerLayout);
//            }
//        });
//        add_alearm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(BusLocatorActivity.this,"You clicked on Add Alarm.",
//                        Toast.LENGTH_LONG).show();
//                closeDrawer(drawerLayout);
//            }
//        });
//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(BusLocatorActivity.this,"You clicked on Help.",
//                        Toast.LENGTH_LONG).show();
//                closeDrawer(drawerLayout);
//            }
//        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CookieManger.getInstance().clearCookies();
                    TrackerConfig.deleteUserAndPass();
                    Toast.makeText(BusLocatorActivity.this,"Logout successful",
                            Toast.LENGTH_LONG).show();
                    finishAffinity();
                    System.exit(0);
                } catch (Exception e) {
                    Toast.makeText(BusLocatorActivity.this,"Logout unsuccessful",
                            Toast.LENGTH_LONG).show();
                }
                closeDrawer(drawerLayout);
            }
        });

        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        manager.replace(bus_finder.getId(),new MapInflation()).commit();

//        try {
//            new Thread(new Runnable() {
//                int i=0;
//                @Override
//                public void run() {
//                    try {
//                        ArrayList <BusInfo> buses = Config.getInstance().getBus();
//                        for(i=0;i<buses.size();i++){
//                            busSelectorAdapter.add(buses.get(i).busName);
//                            busSelectorAdapter.notifyDataSetChanged();
//                            new Thread(new Runnable() {
//                                final int j=i;//copy
//                                @Override
//                                public void run() {
//                                    while (true) {
//                                        try {
//                                            BusFactory.createBus(j, buses.get(j).busId, buses.get(j).busName, buses.get(j).busRoute);
//                                            Log.d("II_WARN","ADDING BUS: "+buses.get(j).busName);
//                                            break;
//                                        } catch (Exception e) {
//                                            Log.e("II_ERROR","ADDING BUS "+buses.get(j).busName+" FAILURE, RETRYING IN 5 SECOND ");
//                                            e.printStackTrace();
//                                        }
//                                        try {
//                                            TimeUnit.MILLISECONDS.sleep(5000);
//                                        }catch (Exception e){
//
//                                        }
//                                    }
//                                }
//                            }).start();
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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

    static MapController mpc=new MapController();
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
            assert mapFragment != null;
            mapFragment.getMapAsync(mpc);
        }
    }
}