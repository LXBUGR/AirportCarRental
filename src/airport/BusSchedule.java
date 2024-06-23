package airport;

import desmoj.core.dist.ContDist;
import desmoj.core.dist.ContDistNormal;
import desmoj.core.simulator.Model;

import java.util.HashMap;
import java.util.Map;

public class BusSchedule {
    private final Model owner;
    private record BusScheduleEntry(int nextStationId, int averageDriveTime) {}
    private final Map<Integer, BusScheduleEntry> schedule;

    //Class used to keep track of the bus schedule, get Travel times and destinations
    public BusSchedule(Model owner) {
        this.owner = owner;
        schedule = new HashMap<>();
    }

    public void addScheduleEntry(int currentStationId, int nextStationId, int averageDriveTime) {
        schedule.put(currentStationId, new BusScheduleEntry(nextStationId, averageDriveTime));
        System.out.println("Added schedule entry: " + currentStationId + " -> " + nextStationId + " (Drive time: " + averageDriveTime + ")");
    }

    public double getNextStationTime(int currentStationId) {
        ContDist distribution = new ContDistNormal(owner, "Travel Time", schedule.get(currentStationId).averageDriveTime(), 0.5, true, true);
        distribution.setNonNegative(true);
        return distribution.sample();
    }

    public int getNextStationId(int currentStationId) {
        if (!schedule.containsKey(currentStationId)) {
            throw new IllegalStateException("No schedule entry for station ID: " + currentStationId);
        }
        return schedule.get(currentStationId).nextStationId();
    }
}
