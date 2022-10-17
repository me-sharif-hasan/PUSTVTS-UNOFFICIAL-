package bd.ac.pust.pustvtsunofficial.BusLocationProvider;

import android.app.Activity;

import bd.ac.pust.pustvtsunofficial.MainActivity;

public class Config {
    private Config(){}
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
}
