package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import java.util.HashMap;
import java.util.Map;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Utility;

public class BusFactory {
    private static Map<Integer, Bus> buses = new HashMap<>();

    public static void createBus(int key, String busId, String busName, String busRoute,boolean willsave) throws Exception {
        Bus newBus = new Bus(busId, busName, busRoute);
        if(willsave) buses.put(key, newBus);
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

    public static void checkBusCanBeConnected(String busId) throws Exception{
        createBus(-1,busId,"","",false);
    }

    public static int getNumberOfBuses(){
        return buses.size();
    }
    public static String whereAreThisBus(int n) throws Exception {
        if (buses.containsKey(n)) {
            return buses.get(n).whereAreYou();
        } else {
            throw new Exception("BusLocationProvider.Bus key does not matching to any record");
        }
    }
    public static void checkUsernamePassword(String username,String pass) throws Exception {
        BusTrackerInterface busTrackerInterface= TrackerFactory.getTracker(Utility.TRACKER_TYPE);
        busTrackerInterface.login(username,pass);
    }
}
