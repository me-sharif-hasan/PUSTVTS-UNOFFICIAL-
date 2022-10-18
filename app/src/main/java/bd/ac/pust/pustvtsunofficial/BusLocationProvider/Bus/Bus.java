package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Bus;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.*;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerConfig;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.TrackerFactory;

public class Bus {
    private final String busId;
    private final String busName;
    private final String busRoute;
    BusTrackerInterface busTrackerInterface;

    public Bus(String busId, String busName, String busRoute) throws Exception {
        this.busId = busId;
        this.busName = busName;
        this.busRoute = busRoute;
        busTrackerInterface = TrackerFactory.getTracker(Utility.TRACKER_TYPE);
    }

    public String whereAreYou() throws Exception {
        return busTrackerInterface.getTrackingInformation(busId);
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
}
