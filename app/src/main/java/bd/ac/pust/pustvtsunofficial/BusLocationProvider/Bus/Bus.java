package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.*;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;

public class Bus implements Comparable{
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
    public static JSONObject busInfo(String busId,boolean busEngineStatus) throws Exception{
        BusInformationFactory bif=BusInformationFactory.initiate();
        JSONObject jsonObject=bif.getBusInfo(busId);
        JSONObject time=jsonObject.getJSONObject("time");

        Map<Date,JSONObject> bst=new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ROOT);
        ArrayList <Date> arrayList=new ArrayList();
        for (Iterator<String> it = time.keys(); it.hasNext(); ) {
            String t = it.next();
            Date d1 = sdf.parse(t);
            arrayList.add(d1);
            bst.put(d1,time.getJSONObject(t));
            Log.d("II_TIME",t);
        }
        Date currentTime=sdf.parse(sdf.format(new Date()));
        Log.d("TODAY", String.valueOf(currentTime));
        int l=0,r=1;
        if(currentTime.compareTo(arrayList.get(0))<=0||currentTime.compareTo(arrayList.get(arrayList.size()-1))>0){
            l=arrayList.size()-1; //if engine on
            r=0; //if engine off
            Log.d("II_BUS_TYPE","BOUNDARY "+(currentTime.compareTo(arrayList.get(0))<=0)+"; "+currentTime+"; "+arrayList.get(0));
        }
        else{
            while (r<arrayList.size()){
                if(arrayList.get(l).compareTo(currentTime)<=0&&arrayList.get(r).compareTo(currentTime)>=0) {
                    break;
                }else {
                    l++;r++;
                }
            }
        }
        Log.e("II_BUS_TYPE",String.valueOf(r)+" "+sdf.format(arrayList.get(r)));

        if(busEngineStatus){
            return time.getJSONObject(sdf.format(arrayList.get(l)).toUpperCase(Locale.ROOT));
        }else{
            return time.getJSONObject(sdf.format(arrayList.get(r)).toUpperCase(Locale.ROOT));
        }
    }

    public String getBusType() throws Exception {
        return busInfo(getBusId(),getEngineStatus()).getString("type");
    }
    public static String getBusType(String busId,boolean engineStatus) throws Exception {
        return busInfo(busId,engineStatus).getString("type");
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
    public void whereAreYou(UpdateActionListener u) throws Exception {
        whereAreYou();
        u.setLocationUpdateInterval(this);
    }
    public String getStartTime(boolean returnCurrent) throws Exception{
        BusInformationFactory bif=BusInformationFactory.initiate();
        JSONObject jsonObject=bif.getBusInfo(busId);
        JSONObject time=jsonObject.getJSONObject("time");

        Map<Date,JSONObject> bst=new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a",Locale.ROOT);
        ArrayList <Date> arrayList=new ArrayList();
        for (Iterator<String> it = time.keys(); it.hasNext(); ) {
            String t = it.next();
            Date d1 = sdf.parse(t);
            arrayList.add(d1);
            bst.put(d1,time.getJSONObject(t));
        }
        Date currentTime=sdf.parse(sdf.format(new Date()));
        String now=sdf.format(arrayList.get(0));
        for(Date d:arrayList){
            Log.d("II_0998",d.toString()+" "+currentTime.toString());
            if(!returnCurrent&&d.compareTo(currentTime)>=0){
                return sdf.format(d);
            }else if(returnCurrent&&currentTime.compareTo(d)>=0){
                now= sdf.format(d);
            }
        }
        return now;
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

    public String getBusRoute()throws Exception {
        return busInfo(getBusId(),getEngineStatus()).getString("route");
    }
    public static String getBusRoute(String busId, boolean busEngineStatus) throws Exception {
        return busInfo(busId,busEngineStatus).getString("route");
    }

    public boolean getEngineStatus(){
        if(getBusName().equals("ALL")) busEngineStatus=false;
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

    @Override
    public int compareTo(Object o) {
        Bus b=(Bus) o;
        return getBusName().compareTo(b.getBusName());
    }

    public interface UpdateActionListener{
        void setLocationUpdateInterval(Bus context);
    }
    public void aboardAllOperation(){
        aboard=true;
    }
}
