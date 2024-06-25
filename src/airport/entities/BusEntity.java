package airport.entities;

import desmoj.core.simulator.*;
import airport.AirportCarRentalModel;
import airport.BusSchedule;
import java.util.ArrayList;
import java.util.List;

public class BusEntity extends Entity {
    private final int id;
    private boolean driving;
    private final int capacity;
    private int currentStationId;
    private final int startStationId;
    private final List<PassengerEntity> passengerList;
    private final BusSchedule schedule;
    private double lastRoundStartTime;
    private double lastArrivalTime;

    public BusEntity(AirportCarRentalModel owner, String name, boolean showInTrace, int id, int capacity, int startStationId) {
        super(owner, name, showInTrace);
        this.id = id;
        this.capacity = capacity;
        driving = false;
        passengerList = new ArrayList<>();
        schedule = new BusSchedule(owner);
        lastRoundStartTime = 0;
        lastArrivalTime = owner.presentTime().getTimeAsDouble();
        this.startStationId = startStationId;
    }

    public int getId() { return id; }

    public int getPassengerCount() {
        return passengerList.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public void addPassenger(PassengerEntity passengerEntity) {
        if (passengerList.size() < capacity) {
            passengerList.add(passengerEntity);
        }
    }

    public void removePassengers() {
        AirportCarRentalModel model = ((AirportCarRentalModel) getModel());
        double currentTime = presentTime().getTimeAsDouble();
        passengerList.removeIf(passenger -> {
            if (passenger.getDestinationId() == currentStationId) {
                double systemTime = currentTime - passenger.getArrivalTime().getTimeAsDouble();
                model.getPassengerSystemTimes(passenger.getArrivalId()).update(systemTime);
                model.getPassengerSystemTimeSeries().update(systemTime);
                return true;
            }
            return false;
        });

        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Passengers removed at station: " + currentStationId, getPassengerCount());
    }

    public void addSchedule(int startStationId, int endStationId, int driveTime) {
        schedule.addScheduleEntry(startStationId, endStationId, driveTime);
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Schedule added from " + startStationId + " to " + endStationId, getPassengerCount());
    }

    public double getNextStationDriveTime() {
        return schedule.getNextStationTime(currentStationId);
    }

    public int getNextStationId() {
        return schedule.getNextStationId(currentStationId);
    }

    public void startRound() {
        lastRoundStartTime = presentTime().getTimeAsDouble();
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Round started at station: " + currentStationId, getPassengerCount());
    }

    public void endRound() {
        double now = presentTime().getTimeAsDouble();
        double roundTime = now - lastRoundStartTime;
        ((AirportCarRentalModel) getModel()).getBusRoundTimes().update(roundTime);
        lastRoundStartTime = now;
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Round ended at station: " + currentStationId, getPassengerCount());
    }

    public int getCurrentStationId() {
        return currentStationId;
    }
    public void setCurrentStationId(int stationId) {
        this.currentStationId = stationId;
    }
    public boolean isDriving() {
        return driving;
    }
    public void setDriving(boolean driving) {
        this.driving = driving;
    }
    public double getLastArrivalTime() { return lastArrivalTime; }
    public void setLastArrivalTime(double lastArrivalTime) { this.lastArrivalTime = lastArrivalTime; }
    public int getStartStationId() { return startStationId; }
}
