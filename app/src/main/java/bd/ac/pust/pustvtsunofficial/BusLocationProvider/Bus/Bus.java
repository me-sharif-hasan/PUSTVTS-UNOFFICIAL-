package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import android.util.Log;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.*;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;

public class Bus {
    private final String busId;
    private final String busName;
    private final String busRoute;
    private double busLon=0;
    private double busLat=0;
    boolean busEngineStatus=false;
    BusTrackerInterface busTrackerInterface;
    private boolean aboard=false;

    public Bus(String busId, String busName, String busRoute) throws Exception {
        this.busId = busId;
        this.busName = busName;
        this.busRoute = busRoute;
        busTrackerInterface = TrackerFactory.getTracker(Utility.TRACKER_TYPE);
    }

    public String whereAreYou() throws Exception {
        if(busId.equals("")) throw new Exception("No bus id provided");
        String locationData=busTrackerInterface.getTrackingInformation(busId);
        Log.d("II_BUS_DEBUG",busId);
        JSONObject jsonObject=new JSONObject(locationData);
        jsonObject.getString("server_resp");
        JSONObject data=new JSONObject(jsonObject.getString("server_resp"));
        double lon=data.getDouble("gps_lon");
        double lat=data.getDouble("gps_lat");
        int hbAcc=data.getInt("hb_acc");
        int gpsAcc=data.getInt("gps_acc");
        busEngineStatus=(hbAcc==1&&gpsAcc==1);

        setBusLat(lat);
        setBusLon(lon);
        return locationData;
    }

    public void setBusLat(double busLat) {
        this.busLat = busLat;
    }

    public void setBusLon(double busLon) {
        this.busLon = busLon;
    }

    public double getBusLat() {
        return busLat;
    }

    public double getBusLon() {
        return busLon;
    }

    public String getBusId() {
        return busId;
    }

    public String getBusName() {
        return busName;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public boolean getEngineStatus(){
        return busEngineStatus;
    }
    public void setUpdateInterval(UpdateActionListener e,long interval){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true&&!aboard) {
                    e.setLocationUpdateInterval(Bus.this);
                    try {
                        TimeUnit.MILLISECONDS.sleep(interval);
                    }catch (Exception e){}
                }
            }
        }).start();
    }
    public interface UpdateActionListener{
        public void setLocationUpdateInterval(Bus context);
    }
    public void aboardAllOperation(){
        aboard=true;
    }
}
