package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import java.util.HashMap;
import java.util.Map;

public class BusFactory {
    private static Map<Integer, Bus> buses = new HashMap<>();

    public static void createBus(int key, String busId, String busName, String busRoute) throws Exception {
        Bus newBus = new Bus(busId, busName, busRoute);
        buses.put(key, newBus);
    }

    public static void createBus(int key, String busId, String busName) throws Exception {
        createBus(key, busId, busName, "Default");
    }

    public static void createBus(int key, String busId) throws Exception {
        createBus(key, busId, "N/A");
    }

    public static String whereAreThisBus(int n) throws Exception {
        if (buses.containsKey(n)) {
            return buses.get(n).whereAreYou();
        } else {
            throw new Exception("BusLocationProvider.Bus key not match to any record");
        }
    }
}
