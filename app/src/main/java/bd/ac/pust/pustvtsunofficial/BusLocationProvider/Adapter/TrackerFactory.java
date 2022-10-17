package bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter;

import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.Interfaces.BusTrackerInterface;
import bd.ac.pust.pustvtsunofficial.BusLocationProvider.Adapter.SimulatedTracker.SimulatedTracker;

public class TrackerFactory {
    public static BusTrackerInterface getTracker(int trackerType) throws Exception {
        if (trackerType == TrackerConfig.TRACK_BY_SIMULATION) {
            return new SimulatedTracker();
        } else if (trackerType == TrackerConfig.TRACK_BY_API) {
            return new SimulatedTracker();
        } else {
            throw new ClassNotFoundException("Error: Class for desired type not found.");
        }
    }
}
