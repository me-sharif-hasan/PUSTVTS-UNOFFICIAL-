package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces;

public interface BusTrackerInterface {
    String getTrackingInformation(String busId) throws Exception;
    public void login(String username,String pass) throws Exception;
}
