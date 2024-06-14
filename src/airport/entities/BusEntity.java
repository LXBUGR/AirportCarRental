package airport.entities;

import airport.util.BusSchedule;
import airport.util.IdManager;
import desmoj.core.simulator.Entity;
import desmoj.core.simulator.Model;

import java.util.ArrayList;
import java.util.List;

public class BusEntity extends Entity {
    private final int capacity;
    private int currentStationId;
    private final List<PassengerEntity> passengerList;

    //Used to retrieve information on next station and traveltime
    private final BusSchedule schedule;

    public BusEntity (Model owner, String name, boolean showInTrace, int capacity) {
        super(owner, name, showInTrace);
        this.capacity = capacity;
        passengerList = new ArrayList<>();
        schedule = new BusSchedule(owner);
    }

    public void drive() {
        currentStationId = IdManager.getStation(currentStationId).getId();
    }

    public void setCurrentStationId(int stationId) {
        this.currentStationId = schedule.getNextStationId(currentStationId);
    }

    public double getNextStationDriveTime() {
        return schedule.getNextStationTime(currentStationId);
    }

    public double getNextStationId() {
        return schedule.getNextStationId(currentStationId);
    }

    public void addPassenger(PassengerEntity passengerEntity) {
        if(passengerList.size() < capacity) {
            passengerList.add(passengerEntity);
        }
    }

    public void removePassengers(int destination) {
        passengerList.removeIf(passengerEntity ->
                passengerEntity.getDestinationId() == currentStationId
        );
    }

    public void addSchedule(int startStationId, int endStationId, int driveTime) {
        schedule.addScheduleEntry(startStationId, endStationId, driveTime);
    }
}
