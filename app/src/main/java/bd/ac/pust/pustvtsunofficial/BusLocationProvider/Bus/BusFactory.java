package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;

public class BusFactory {
    private static List buses = new ArrayList();

    public static void createBus(int key, String busId, String busName, String busRoute,boolean willsave) throws Exception {
        Bus newBus = new Bus(busId, busName, busRoute);
        if(willsave) buses.add(newBus);
    }
    public static void createBus(int key, String busId, String busName, String busRoute) throws Exception {
        createBus(key,busId,busName,busRoute,true);
    }

    public static void createBus(int key, String busId, String busName) throws Exception {
        createBus(key, busId, busName, "Default",true);
    }

    public static void createBus(int key, String busId) throws Exception {
        createBus(key, busId, "N/A");
    }

    public static void checkBusCanBeConnected() throws Exception{
        createBus(-1,"","","",false);
    }

    public static int getNumberOfBuses(){
        return buses.size();
    }
    public static List<Bus> getBuses(){
        return buses;
    }
    public static void checkUsernamePassword(String username,String pass) throws Exception {
        BusTrackerInterface busTrackerInterface= TrackerFactory.getTracker(Utility.TRACKER_TYPE);
        busTrackerInterface.login(username,pass);
    }
}
