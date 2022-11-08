package bd.ac.pust.pustvtsunofficial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import bd.ac.pust.pustvtsunofficial.Alarm.AlarmActivity;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.CookieAndSession.CookieManger;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.NavDrawer.VehicleAdapter;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.StoppageManager.StoppageManager;
import bd.ac.pust.pustvtsunofficial.Maps.MapController;
import bd.ac.pust.pustvtsunofficial.Updater.AppUpdater;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BusLocatorActivity extends AppCompatActivity {
    FrameLayout bus_finder;
    ImageView dashboard;
    static DrawerLayout drawerLayout;
    LinearLayout vehicles, stoppages, add_alearm, help, logout;

    TextView bottomShow, stopedInfo;
    RecyclerView vehicleRV;
    ArrayList<Bus> vehicleList;
    boolean vehiclesDataShow, stopageDataShow;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ensure bus information is loaded;
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
        stopedInfo = findViewById(R.id.additional_info);

        vehicleRV = findViewById(R.id.rv_vehicles);




        AppUpdater.getInstance().checkUpdate(new AppUpdater.UpdateCheckListener() {
            @Override
            public void onUpdateAvailable(String url, int availableV,String msg) {
                Log.d("II_UPDATE", url);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(BusLocatorActivity.this)
                                .setIcon(R.drawable.ic_logout)
                                .setTitle("New update available!")
                                .setMessage(msg)
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        stopedInfo.setText("Getting update ready.");
                                        AppUpdater.getInstance().downloadAndUpdate(url, BusLocatorActivity.this, new AppUpdater.UpdateProgress() {
                                            double cmls=0;
                                            @Override
                                            public void onDownloadData(long size) {
                                                cmls+=size;
                                                float mb= (float) (cmls/(1024*1024.0));
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        BusLocatorActivity.this.stopedInfo.setText(String.format("%.1f",mb)+" MB");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onComplete() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        BusLocatorActivity.this.stopedInfo.setText("Update downloaded!");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(String localizedMessage) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        BusLocatorActivity.this.stopedInfo.setText(localizedMessage);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();
                    }
                });
            }
        });





        ProgressBar progressBar = findViewById(R.id.bus_loding_indication);
        vehicleList = new ArrayList<>();


        Spinner stoppage_selector = findViewById(R.id.stoppage_selector);


        Switch isolator = findViewById(R.id.bus_solo_view);
        isolator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //vehicleRV.setVisibility(View.GONE);
                mpc.isolateView(isolator);
            }
        });

        ArrayAdapter<String> stoppageSelectorAdapter = new ArrayAdapter<String>(this, android.R.
                layout.simple_spinner_item, android.R.id.text1);
        //busSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stoppage_selector.setAdapter(stoppageSelectorAdapter);
        stoppageSelectorAdapter.notifyDataSetChanged();
        StoppageManager.init(new StoppageManager.StoppageLoadEvent() {
            @Override
            public void onStoppageCreated(String stoppageName, LatLng l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stoppageSelectorAdapter.add(stoppageName);
                        stoppageSelectorAdapter.notifyDataSetChanged();
                    }
                });
                mpc.addStoppage(stoppageName, l);
                mpc.notifyStoppageChange();
            }
        });

        stoppage_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (first) {
                    first = false;
                    return;
                }
                TextView t = (TextView) view;
                Log.d("II_420", (String) t.getText());
                try {
                    mpc.isolateStoppage((String) t.getText());
                    closeDrawer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        VehicleAdapter vadapter = new VehicleAdapter(this, vehicles);
        vehicleRV.setAdapter(vadapter);
        LinearLayoutManager llm = new LinearLayoutManager(BusLocatorActivity.this);
        vehicleRV.setLayoutManager(llm);

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vehicleRV.setVisibility(View.GONE);
                vehiclesDataShow = false;
                vadapter.notifyDataSetChanged();
                openDrower();

            }
        });

        ImageButton recenter = findViewById(R.id.recenter);
        recenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpc.refocus(BusLocatorActivity.this);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        BusFactory.createBusList(new BusFactory.BusCreatedEvent() {
                            @Override
                            public void onBusCreated(Bus bus) {
                                Log.d("II_890", bus.getBusName());
                                if (!bottomShow.getText().toString().equals("Showing All")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("II_909", "ENTERING");
                                            bottomShow.setText("Showing All");
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                                vadapter.addBus(bus);
                            }
                        });
                        break;
                    } catch (Exception e) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }).start();


        vehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehiclesDataShow) {
                    vehicleRV.setVisibility(View.GONE);
                    vehiclesDataShow = false;
                } else {
                    vehicleRV.setVisibility(View.VISIBLE);
                    vehiclesDataShow = true;
                }
//
//                Toast.makeText(BusLocatorActivity.this, "vehicle clicked" +
//                        "", Toast.LENGTH_SHORT).show();
            }
        });
        add_alearm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(BusLocatorActivity.this, AlarmActivity.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(BusLocatorActivity.this)
                        .setIcon(R.drawable.ic_logout)
                        .setTitle("Log Out")
                        .setMessage("Are you sure to log out.")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    CookieManger.getInstance().clearCookies();
                                    TrackerConfig.deleteUserAndPass();
                                    Toast.makeText(BusLocatorActivity.this, "Logout successful",
                                            Toast.LENGTH_LONG).show();
                                    System.exit(0);
                                } catch (Exception e) {
                                    Toast.makeText(BusLocatorActivity.this, "Logout unsuccessful",
                                            Toast.LENGTH_LONG).show();
                                }
                                closeDrawer();
                            }
                        })
                        .setNeutralButton("HELP", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(BusLocatorActivity.this, "Click Yes to " +
                                        "log out.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });

        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        manager.replace(bus_finder.getId(), new MapInflation()).commit();


        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void openDrower() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void BusIC_clickListener(View view) {
        closeDrawer();
    }

    public static void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer();
    }

    public static MapController mpc = new MapController();

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
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(mpc);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(BusLocatorActivity.this)
                .setIcon(R.drawable.ic_logout)
                .setTitle("Exit")
                .setMessage("Are you sure to Exit.")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNeutralButton("HELP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(BusLocatorActivity.this, "Click Yes to " +
                                "Exit.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }
}