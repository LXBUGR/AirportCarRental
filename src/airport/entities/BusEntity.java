package airport.entities;

import desmoj.core.simulator.*;
import airport.AirportCarRentalModel;
import airport.BusSchedule;
import java.util.ArrayList;
import java.util.List;

public class BusEntity extends Entity {
    private boolean driving = false;
    private final int capacity;
    private int currentStationId;
    private final List<PassengerEntity> passengerList;
    private final BusSchedule schedule;
    private double lastRoundStartTime;

    public BusEntity(AirportCarRentalModel owner, String name, boolean showInTrace, int capacity) {
        super(owner, name, showInTrace);
        this.capacity = capacity;
        passengerList = new ArrayList<>();
        schedule = new BusSchedule(owner);
        lastRoundStartTime = 0;
    }

    public int getPassengerCount() {
        return passengerList.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public void addPassenger(PassengerEntity passengerEntity) {
        if (passengerList.size() < capacity) {
            passengerList.add(passengerEntity);
            ((AirportCarRentalModel) getModel()).getBusPassengerCount().update(passengerList.size()); // Histogramm aktualisieren
            ((AirportCarRentalModel) getModel()).getBusPassengerTimeSeries().update(presentTime().getTimeAsDouble()); // Zeitreihe aktualisieren
            ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Passenger added: " + passengerEntity.getName());
        }
    }

    public void removePassengers() {
        List<PassengerEntity> departingPassengers = new ArrayList<>();
        double currentTime = presentTime().getTimeAsDouble();
        passengerList.removeIf(passengerEntity -> {
            boolean shouldRemove = passengerEntity.getDestinationId() == currentStationId;
            if (shouldRemove) {
                departingPassengers.add(passengerEntity);
            }
            return shouldRemove;
        });
        ((AirportCarRentalModel) getModel()).getBusPassengerCount().update(passengerList.size()); // Histogramm aktualisieren
        ((AirportCarRentalModel) getModel()).getBusPassengerTimeSeries().update(presentTime().getTimeAsDouble()); // Zeitreihe aktualisieren
        ((AirportCarRentalModel) getModel()).getPassengerCountPerRide().update(getPassengerCount()); // Passagieranzahl pro Fahrt aktualisieren

        // Calculate and update the system time for departing passengers
        for (PassengerEntity passenger : departingPassengers) {
            double systemTime = currentTime - passenger.getArrivalTime().getTimeAsDouble();
            if (passenger.getArrivalId() == 1) {
                ((AirportCarRentalModel) getModel()).getTerminal1PassengerTimes().update(systemTime);
            } else if (passenger.getArrivalId() == 2) {
                ((AirportCarRentalModel) getModel()).getTerminal2PassengerTimes().update(systemTime);
            } else if (passenger.getArrivalId() == 3) {
                ((AirportCarRentalModel) getModel()).getCarRentalPassengerTimes().update(systemTime);
            }
        }
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Passengers removed at station: " + currentStationId);
    }

    public List<PassengerEntity> getPassengerList() {
        return new ArrayList<>(passengerList);
    }

    public void addSchedule(int startStationId, int endStationId, int driveTime) {
        schedule.addScheduleEntry(startStationId, endStationId, driveTime);
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Schedule added from " + startStationId + " to " + endStationId);
    }

    public double getNextStationDriveTime() {
        return schedule.getNextStationTime(currentStationId);
    }

    public int getNextStationId() {
        return schedule.getNextStationId(currentStationId);
    }

    public void startRound() {
        lastRoundStartTime = presentTime().getTimeAsDouble();
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Round started at station: " + currentStationId);
    }

    public void endRound() {
        double roundTime = presentTime().getTimeAsDouble() - lastRoundStartTime;
        ((AirportCarRentalModel) getModel()).getBusRoundTimes().update(roundTime);
        ((AirportCarRentalModel) getModel()).getPassengerCountPerRide().update(getPassengerCount());
        lastRoundStartTime = presentTime().getTimeAsDouble();
        ((AirportCarRentalModel) getModel()).sendTraceNoteWithPassengers("Round ended at station: " + currentStationId);
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

    public double getLastRoundStartTime() {
        return lastRoundStartTime;
    }
}
