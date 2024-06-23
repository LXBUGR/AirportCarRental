package airport;

import java.util.HashMap;
import java.util.Map;

public class BusSchedule {
    private final AirportCarRentalModel meinModel;
    private record BusScheduleEntry(int nextStationId, int averageDriveTime) {}
    private final Map<Integer, BusScheduleEntry> schedule;

    //Class used to keep track of the bus schedule, get Travel times and destinations
    public BusSchedule(AirportCarRentalModel owner) {
        this.meinModel = owner;
        schedule = new HashMap<>();
    }

    public void addScheduleEntry(int currentStationId, int nextStationId, int averageDriveTime) {
        schedule.put(currentStationId, new BusScheduleEntry(nextStationId, averageDriveTime));
    }

    public double getNextStationTime(int currentStationId) {
        //TODO make different stations have different travel times
        return meinModel.getTravelTime().sample();
    }

    public int getNextStationId(int currentStationId) {
        if (!schedule.containsKey(currentStationId)) {
            throw new IllegalStateException("No schedule entry for station ID: " + currentStationId);
        }
        return schedule.get(currentStationId).nextStationId();
    }
}
