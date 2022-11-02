package bd.ac.pust.pustvtsunofficial;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import bd.ac.pust.pustvtsunofficial.Adapters.BusInfoAdapter;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.Maps.MapController;

public class BusNavigationController extends AppCompatActivity implements View.OnClickListener, BusFactory.BusLoadListener {
    FrameLayout bus_finder;
    ImageView dashboard;
    DrawerLayout drawerLayout;
    LinearLayout vehicles,stoppages,add_alearm,help,logout;
    TextView bottomShow, stoppedInfo;
    MapController mapController;
    BusInfoAdapter bia;
    Spinner busChoose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_locator);
        mapController=new MapController(this);
        init();
        addEventListener();
        BusFactory.setBusLoadListener(this);
        createBuses();
        busChoose.setAdapter(bia);
    }


    private void createBuses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        BusFactory.createBusList(null);
                        break;
                    }
                }catch (Exception e){

                }
            }
        }).start();
    }

    private void init(){
        bia=new BusInfoAdapter(this);
        bus_finder = findViewById(R.id.fl_fragmentHolder);
        dashboard = findViewById(R.id.iv_dashboard);
        drawerLayout = findViewById(R.id.dl_bus_locator);
        vehicles = findViewById(R.id.ll_nav_vehicles);
        stoppages = findViewById(R.id.ll_nav_stoppages);
        add_alearm = findViewById(R.id.ll_nav_add_alarm);
        help = findViewById(R.id.ll_nav_help);
        logout = findViewById(R.id.ll_nav_logout);
        bottomShow = findViewById(R.id.bus_name_show);
        stoppedInfo = findViewById(R.id.stopage_name);
        //busChoose=findViewById(R.id.bus_selector);
    }

    private void addEventListener(){
        dashboard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.iv_dashboard) {
            if(drawerLayout!=null) drawerLayout.openDrawer(GravityCompat.START);
        }
    }


    @Override
    public void onBusLoaded(Bus bus, int busKey) {
        Log.d("II_009",bus.getBusId());
        BusInfoAdapter.CustomViews customViews= new BusInfoAdapter.CustomViews(bus);
        bia.add(customViews);
    }
}
