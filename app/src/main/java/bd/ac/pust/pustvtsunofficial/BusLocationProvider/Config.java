package bd.ac.pust.pustvtsunofficial.BusLocationProvider;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.Bus;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus.BusInfo;
import bd.ac.pust.pustvtsunofficial.MainActivity;

public class Config {
    private Config(){

    }
    private Activity mainContext;
    private static Config instance;

    public void setMainContext(Activity mainContext) {
        this.mainContext = mainContext;
    }

    public Activity getMainContext() {
        return mainContext;
    }

    public static Config getInstance() {
        if(instance==null) instance=new Config();
        return instance;
    }
    Map<String, ArrayList<BusInfo>> busMapper=new HashMap<>();
}
