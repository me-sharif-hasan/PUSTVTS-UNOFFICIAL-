package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces;

import java.util.Map;

public interface BusTrackerInterface {
    String getTrackingInformation(String busId) throws Exception;
    public void login(String username,String pass) throws Exception;

    public static Map<String, String> getBusList() throws Exception {
        return null;
    }
}
