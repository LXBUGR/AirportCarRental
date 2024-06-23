package airport.entities;

import airport.AirportCarRentalModel;
import airport.BusSchedule;
import desmoj.core.simulator.Entity;

import java.util.ArrayList;
import java.util.List;

public class BusEntity extends Entity {
    private boolean driving = false;
    private final int capacity;
    private int currentStationId;
    private final List<PassengerEntity> passengerList;

    //Used to retrieve information on next station and traveltime
    private final BusSchedule schedule;

    public BusEntity (AirportCarRentalModel owner, String name, boolean showInTrace, int capacity) {
        super(owner, name, showInTrace);
        this.capacity = capacity;
        passengerList = new ArrayList<>();
        schedule = new BusSchedule(owner);
    }

    public int getCurrentStationId() { return currentStationId; }
    public void setCurrentStationId(int stationId) {
        this.currentStationId = stationId;
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

    public void removePassengers() {
        passengerList.removeIf(passengerEntity ->
                passengerEntity.getDestinationId() == currentStationId
        );
    }

    public void addSchedule(int startStationId, int endStationId, int driveTime) {
        schedule.addScheduleEntry(startStationId, endStationId, driveTime);
    }

     public int getCapacity() {
        return capacity;
    }

    public int getPassengerCount() {
        return passengerList.size();
    }

    public boolean isDriving() { return driving; }

    public void setDriving( boolean val ) { driving = val; }
}
